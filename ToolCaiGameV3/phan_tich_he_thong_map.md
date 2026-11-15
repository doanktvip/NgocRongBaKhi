# Tài Liệu Phân Tích Toàn Diện Hệ Thống Map (`nro.models.map`)

Tài liệu này giải thích chi tiết toàn bộ **17 file mã nguồn Java (5.125 dòng code)** trong thư mục `map`, bao gồm kiến trúc tổng thể, vai trò của từng file, giải thích từng thuộc tính và hàm (method), cùng đánh giá chuyên sâu về chất lượng mã nguồn (Code Quality Review) và các lỗi tiềm ẩn.

---

## Phần 1: Tổng Quan Kiến Trúc & Cấu Trúc Thư Mục

Hệ thống bản đồ được đóng gói trong package `nro.models.map` và chia thành **3 phân hệ chính**:

| Phân hệ | Package | Số lượng file | Tổng số dòng | Trách nhiệm chính |
| :--- | :--- | :---: | :---: | :--- |
| **1. Thực thể Bản đồ Cốt lõi (Core Entities)** | `nro.models.map` | 6 | 1.631 | Biểu diễn dữ liệu bản đồ (`Map`), khu vực (`Zone`), vật phẩm rơi (`ItemMap`), cổng dịch chuyển (`WayPoint`), bẫy (`TrapMap`), và kén giam Ma Bư (`MaBuHold`). |
| **2. Phó bản & Sự kiện (Dungeons & Events)** | `nro.models.map.phoban` | 6 | 1.483 | Quản lý vòng đời, luồng cập nhật (`Runnable`), quái/boss và điều kiện thắng/thua của 6 phó bản/sự kiện: Bản Đồ Kho Báu, Ngọc Rồng Sao Đen, Khí Gas Hủy Diệt, Ma Bư 14H, Doanh Trại Độc Nhãn, Con Đường Rắn Độc. |
| **3. Dịch vụ Điều phối (Map Services)** | `nro.models.map.service` | 5 | 2.011 | Xử lý nghiệp vụ chuyển map/chuyển khu (`ChangeMapService`), tra cứu & kiểm tra loại map (`MapService`), nhặt/xóa vật phẩm (`ItemMapService`), lọc NPC (`NpcManager`) và tạo hội thoại/menu NPC (`NpcService`). |

### Luồng Hoạt Động Tổng Thể (System Lifecycle)
1. **Khởi tạo (`Map` & `Zone`)**: Khi server khởi động, từng đối tượng `Map` được tạo từ dữ liệu cấu hình và đọc ma trận va chạm từ file nhị phân `data/map/tile_map_data/<mapId>`. Mỗi `Map` tự động khởi tạo danh sách các khu vực (`Zone`), gắn `Zone` vào các dịch vụ phó bản tương ứng, và khởi tạo Quái (`Mob`), NPC (`Npc`), Vật phẩm cố định (`ItemMap`), Bẫy (`TrapMap`) cùng Boss.
2. **Vòng lặp Cập nhật (`Game Loop`)**:
   - Mỗi `Map` sở hữu một `ScheduledExecutorService` chạy định kỳ gọi `zone.update()` cho toàn bộ các khu trong map.
   - Mỗi `Zone.update()` lần lượt cập nhật: Quái (`udMob`) -> Vật phẩm rơi (`udItem`) -> Người chơi (`udPlayer`) -> NPC đứng im (`udNonInteractiveNPC`).
   - Các phó bản (`BanDoKhoBau`, `RedRibbonHQ`, `SnakeWay`, `DestronGas`, `BlackBallWar`, `MajinBuu14H`) chạy trên **Java Virtual Thread** (`Thread.ofVirtual()`) riêng biệt với chu kỳ tick ~150ms để kiểm tra điều kiện thắng, gọi boss, đếm ngược sập hang và đẩy người chơi ra ngoài.
3. **Đồng bộ Trạng thái & Chuyển Map (`ChangeMapService`)**: Khi người chơi di chuyển qua `WayPoint`, dùng Capsule, Tàu vũ trụ hoặc Dịch chuyển tức thời, `ChangeMapService` kiểm tra điều kiện nhiệm vụ/sức mạnh/phó bản, xóa người chơi khỏi `Zone` cũ (gửi gói tin `-6`), thêm vào `Zone` mới, gửi cấu trúc bản đồ (gói tin `-24`) và đồng bộ thông tin nhân vật/hiệu ứng hai chiều (`load_Me_To_Another` & `load_Another_To_Me`).

---

## Phần 2: Giải Thích Chi Tiết Từng File & Các Hàm

### A. Phân Hệ 1: Thực Thể Bản Đồ Cốt Lõi (`nro.models.map`)

#### 1. `Map.java` (492 dòng)
Đại diện cho một bản đồ gốc (Map Template & Instance Container), chứa dữ liệu địa hình (tile map), danh sách các khu vực (`Zone`), các cổng chuyển map (`WayPoint`) và danh sách NPC chung. Implement `Runnable` để chạy lịch cập nhật cho toàn bộ các `Zone` bên trong.

**Các thuộc tính chính:**
* `T_EMPTY = 0`, `T_TOP = 2`, `SIZE = 24`: Hằng số loại ô địa hình (0: ô trống, 2: mặt đất đứng được) và kích thước mỗi ô tile (24x24 pixel).
* `scheduler`: Bộ lập lịch `ScheduledExecutorService` (1 luồng) riêng cho từng bản đồ.
* `mapId`, `mapName`, `planetId`, `planetName`, `tileId`, `bgId`, `bgType`, `type`: Định danh bản đồ, hành tinh và cấu hình hiển thị nền/loại map.
* `tileMap` (`int[][]`), `tileTop` (`int[]`), `mapWidth`, `mapHeight`, `tmw`, `tmh`, `pxw`, `pxh`, `types`, `maps`: Dữ liệu lưới ô vuông (tile grid) phục vụ tính toán vật lý mặt đất và chặn di chuyển.
* `zones` (`List<Zone>`), `wayPoints` (`List<WayPoint>`), `npcs` (`List<Npc>`), `bots`, `players`: Các danh sách thực thể thuộc bản đồ.

**Chi tiết các Constructor & Hàm:**

| Tên hàm / Constructor | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `Map(...)` (Constructor) | `mapId`, `mapName`, `planetId`, `tileId`, `bgId`, `bgType`, `type`, `tileMap`, `tileTop`, `zones`, `maxPlayer`, `wayPoints` | Khởi tạo thông tin cơ bản của bản đồ, tính `mapHeight` và `mapWidth` theo bội số của `SIZE` (24px), đọc file dữ liệu tile (`readTileMap`), chia khu (`initZone`), tạo vật phẩm cố định (`initItem`) và tạo bẫy (`initTrapMap`). |
| `getPlayersInMap()` | Không có | Duyệt qua toàn bộ các `Zone` của bản đồ, gom tất cả người chơi đang có mặt vào một `List<Player>` mới và trả về. |
| `initZone(int nZone, int maxPlayer)` | `nZone`: số khu mặc định, `maxPlayer`: số người tối đa/khu | Ghi đè số lượng khu (`nZone`) nếu map thuộc loại đặc biệt (`MAP_OFFLINE = 1`, `MAP_BLACK_BALL_WAR = 5`, `MAP_MABU_14H = 7`, các phó bản Bang hội = `50`). Sau đó khởi tạo từng đối tượng `Zone` và đăng ký vào Service quản lý phó bản tương ứng (`BlackBallWarService`, `RedRibbonHQService`, `TreasureUnderSeaService`, `SnakeWayService`, `DestronGasService`, `SuperDivineWaterService`, `MajinBuu14HService`). |
| `initNpc(byte[] npcId, short[] npcX, short[] npcY)` | Mảng ID và tọa độ X, Y của NPC | Khởi tạo danh sách NPC trong map thông qua `NpcFactory.createNPC(...)`. Bỏ qua NPC Bò Mộng nếu đang ở các map nhà (mapId `21`, `22`, `23`). |
| `addNpc(Npc npc)` | `npc` | Thêm trực tiếp một đối tượng `Npc` vào danh sách `this.npcs` của bản đồ. |
| `run()` | Không có | Kích hoạt `scheduler.scheduleAtFixedRate` chạy chu kỳ **5000ms (5 giây)** để duyệt qua toàn bộ `this.zones` và gọi `zone.update()`. Mọi ngoại lệ đều được bắt và ghi log qua `Logger.logException`. |
| `initMob(byte[] mobTemp, byte[] mobLevel, int[] mobHp, short[] mobX, short[] mobY)` | Mảng cấu hình quái | Khởi tạo quái cho từng `Zone`. Lấy thông tin mẫu từ `Manager.getMobTemplateByTemp`, thiết lập HP, tọa độ, tiềm năng. Nếu `tempId` trùng với các BigBoss (`HIRUDEGARN`, `VUA_BACH_TUOC`, `ROBOT_BAO_VE`, `GAU_TUONG_CUOP`, `VOI_CHIN_NGA`, `GA_CHIN_CUA`, `NGUA_CHIN_LMAO`, `PIANO`, `MAY_DO_SUC_MANH`) thì khởi tạo class con tương ứng, ngược lại tạo `Mob` thường và gắn vào `zone.mobs`. |
| `initMob(List<Mob> mobs)` | Danh sách mẫu `Mob` | Nhân bản (copy constructor) danh sách `mobs` truyền vào cho từng `Zone` trong bản đồ. |
| `initTrapMap()` | Không có | Khởi tạo bẫy địa hình cho từng `Zone`. Hiện tại chỉ cấu hình cho **Map 135** (Động Hải Tặc - Bản Đồ Kho Báu) với bẫy xiên (`effectId = 49`, tọa độ `x = 260, y = 960, w = 740, h = 72`). |
| `initItem()` | Không có | Đặt sẵn các vật phẩm cố định vào từng `Zone` khi mở server: Đùi gà nướng (`id = 74`) ở map nhà `21, 22, 23`; Đứa bé (`id = 78`) ở vách núi `42, 43, 44`; và 7 viên Ngọc Rồng Sao Đen (`id = 372..378`) ở các map `85..91`. |
| `initBoss()` | Không có | Tạo và đưa các Boss cố định vào từng `Zone` của map đặc thù: Tàu Pảy Pảy (map `111`), các Boss tầng Ma Bư 12H (`Drabura`, `Bui Bui`, `Ya Con`, `Mabu 12H` ở map `114..120`), và Boss Yardrat (`Tân Binh 5`, `Chiến Binh 5`, `Đội Trưởng 5` ở map `131..133`). |
| `mapIdNextMabu(short mapId)` | `mapId` hiện tại | Trả về ID map tầng tiếp theo trong sự kiện Ma Bư 12H (`114 -> 115 -> 117 -> 118 -> 119 -> 120`, mặc định `-1`). |
| `getNpc(Player player, int tempId)` | `player`, `tempId` | Tìm kiếm NPC có `tempId` trong map. Kiểm tra khoảng cách chống gian lận: người chơi phải đứng cách NPC `<= 60` pixel (trừ map Ngọc Rồng Sao Đen được bỏ qua khoảng cách). |
| `yPhysicInTop(int x, int y)` | Tọa độ pixel `x, y` | **Hàm vật lý trọng lực**: Từ tọa độ `(x, y)`, dò dọc từ trên xuống dưới trên ma trận `tileMap` để tìm ô mặt đất (`isTileTop`) gần nhất bên dưới và trả về tọa độ `Y` mặt đất (tính bằng pixel). Dùng để đặt vật phẩm rơi, quái hoặc người chơi đứng chuẩn trên mặt đất. |
| `LastY(int cx, int cy)` | Tọa độ `cx, cy` | Dò tìm mặt đất theo mảng bitwise `types` (tối đa 30 bước nhảy, mỗi bước 24px) và làm tròn tọa độ `Y` theo bội số của 24. |
| `tileTypeAt(int x, int y, int type)` | Tọa độ `x, y`, cờ `type` | Kiểm tra bitwise xem ô tile tại tọa độ `(x, y)` trong mảng `types` có mang thuộc tính `type` (ví dụ `T_TOP = 2`) hay không. |
| `moveXY(Player player)` | `player` | Tính toán và hiệu chỉnh tọa độ di chuyển hợp lệ của người chơi dựa trên va chạm tile map (chống đi xuyên tường/rơi khỏi đáy map). |
| `isTileTop(int tileMap)` | Mã `tileMap` | Kiểm tra xem mã ô tile truyền vào có nằm trong danh sách các ô mặt đất (`this.tileTop`) của bản đồ hay không. |
| `readTileMap(int mapId)` | `mapId` | Đọc file nhị phân `data/map/tile_map_data/<mapId>` để nạp chiều rộng (`tmw`), chiều cao (`tmh`), tính kích thước pixel (`pxw`, `pxh`) và nạp mảng ô bản đồ `maps`. |

---

#### 2. `Zone.java` (868 dòng)
Đại diện cho một **Khu vực (Channel/Instance)** cụ thể bên trong một `Map`. Đây là nơi trực tiếp chứa và quản lý sự tương tác giữa Người chơi (`Player`), Đệ tử (`Pet`), Boss (`Boss`), Quái (`Mob`), Bot (`BotAttackplayer`) và Vật phẩm rơi (`ItemMap`).

**Các thuộc tính chính:**
* `PLAYERS_TIEU_CHUAN_TRONG_MAP = 7`: Ngưỡng số người chơi tiêu chuẩn để ưu tiên xếp khu tự động.
* `countItemAppeaerd`: Bộ đếm tự tăng để cấp phát `itemMapId` duy nhất cho vật phẩm rơi trong khu (reset về 0 khi chạm 2 tỷ).
* Các danh sách phân loại thực thể `Player` (sử dụng `CopyOnWriteArrayList`):
  * `humanoids`: Chứa tất cả thực thể hình người (Player thật, Boss, Pet, NonInteractiveNPC).
  * `notBosses`: Chứa Player thật và Pet (không chứa Boss và NonInteractiveNPC).
  * `players`: Chỉ chứa **Người chơi thật** (không chứa Boss, Pet, NewPet, NonInteractiveNPC).
  * `bosses`: Chỉ chứa Boss.
  * `pets`: Chỉ chứa Pet và NewPet.
  * `nonInteractiveNPCs`: Chứa các NPC giả lập dạng Player (như Trọng tài, Khỉ Bông...).
* `mobs` (`List<Mob>`), `items` (`List<ItemMap>`), `trapMaps` (`List<TrapMap>`), `maBuHolds` (`List<MaBuHold>`), `bots` (`List<BotAttackplayer>`).
* Các cờ trạng thái sự kiện/phó bản: `lastTimeDropBlackBall`, `finishBlackBallWar`, `finishMapMaBu`, `isbulon1Alive`, `isbulon2Alive`, `isTUTAlive`, `isGoldenFriezaAlive`, `isCompeting`, `rank1`, `rank2`, `rankName1`, `rankName2`.

**Chi tiết các Constructor & Hàm:**

| Tên hàm / Constructor | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `Zone(Map map, int zoneId, int maxPlayer)` | `map`, `zoneId`, `maxPlayer` | Khởi tạo khu vực và cấp phát bộ nhớ cho các danh sách thực thể (`CopyOnWriteArrayList` cho các danh sách nhân vật/vật phẩm/bẫy, `ArrayList` cho `mobs`). |
| `isFullPlayer()` | Không có | Kiểm tra khu đã đầy người chơi chưa (`players.size() >= maxPlayer`). |
| `addBot(BotAttackplayer bot)` | `bot` | Thêm Bot vào danh sách `bots` (có `synchronized`), gán `bot.zone = this` và gửi gói tin xuất hiện (`Service.gI().sendAppear`) tới toàn bộ người chơi đang sống trong khu. |
| `getBots()` | Không có | Trả về bản sao (`new ArrayList<>(bots)`) danh sách Bot trong khu một cách an toàn luồng. |
| `removeBot(BotAttackplayer bot)` | `bot` | Xóa Bot khỏi danh sách `bots` và gửi gói tin biến mất (`Service.gI().sendDisappear`) tới tất cả người chơi trong khu. |
| `udMob()` | Không có (`private`) | Duyệt ngược danh sách `mobs` và gọi `mob.update()` (xử lý quái tấn công, tự hồi sinh). |
| `udNonInteractiveNPC()` | Không có (`private`) | Tạo bản sao danh sách `nonInteractiveNPCs`, duyệt ngược và gọi `pl.update()`. |
| `udItem()` | Không có (`private`) | Duyệt qua danh sách `items` rơi trên đất và gọi `item.update()`. Nếu phát hiện item bị lỗi `itemTemplate == null` thì xóa khỏi danh sách. |
| `udPlayer()` | Không có (`private`) | Duyệt ngược danh sách `notBosses`, lọc những thực thể không phải Pet/Bot/NewPet và gọi `update()`. |
| `update()` | Không có | Hàm điều phối cập nhật chính của khu vực, gọi lần lượt: `udMob()`, `udItem()`, `udPlayer()`, `udNonInteractiveNPC()`. |
| `getNumOfPlayers()` / `getNumOfBosses()` | Không có | Trả về số lượng người chơi thật (`players.size()`) và số lượng Boss (`bosses.size()`) hiện có trong khu. |
| `isBossCanJoin(Boss boss)` | `boss` | Kiểm tra xem trong khu đã có Boss nào trùng `id` với `boss` chuẩn bị vào hay chưa (chống 2 Boss cùng loại vào chung 1 khu). |
| `addPlayer(Player player)` | `player` | Phân loại đối tượng `player` dựa trên các cờ (`isBoss`, `isPet`, `isNewPet`, `instanceof NonInteractiveNPC`) để đưa vào đúng các danh sách `humanoids`, `nonInteractiveNPCs`, `notBosses`, `players`, `bosses`, `pets`. |
| `removePlayer(Player player)` | `player` | Xóa triệt để đối tượng `player` khỏi cả 6 danh sách quản lý nhân vật của khu. |
| `getItemMapByItemMapId(int itemId)` | `itemId` | Tìm kiếm vật phẩm trên mặt đất theo ID tự tăng `itemMapId`. |
| `getItemMapByTempId(int tempId)` | `tempId` | Tìm kiếm vật phẩm trên mặt đất theo ID mẫu (`itemTemplate.id`). |
| `getItemMapsForPlayer(Player player)` | `player` | Lọc danh sách vật phẩm trên mặt đất được phép hiển thị cho `player`: ẩn Đứa bé (`id = 78`) nếu người chơi không làm đúng nhiệm vụ `TASK_3_1`; ẩn Đùi gà (`id = 74`) nếu chưa tới `TASK_3_0`; ẩn vật phẩm `726` nếu không phải chủ sở hữu. |
| `getPlayerInMap(long idPlayer)` | `idPlayer` | Tìm kiếm một nhân vật bất kỳ (trong `humanoids`) đang ở trong khu theo `id`. |
| `getPlayerInMapOffline(Player player, long idPlayer)` | `player`, `idPlayer` | Tìm kiếm `TrainingBoss` (Boss tập luyện trong map offline) đang giao chiến với chính `player` đó. |
| `pickItem(Player player, int itemMapId)` | `player`, `itemMapId` | **Xử lý nhặt vật phẩm trên map**: Khóa đồng bộ `synchronized (itemMap)`, kiểm tra quyền sở hữu (`playerId == player.id` hoặc `-1`), kiểm tra điều kiện đặc biệt (item `648` yêu cầu có Tất/vớ giáng sinh `649`), thêm vào hành trang (`InventoryService.gI().addItemBag`), gửi gói tin **`Message(-20)`** thông báo nhặt thành công cho người nhặt, gửi hiệu ứng người khác thấy nhặt (`sendToAntherMePickItem`), xóa vật phẩm khỏi đất (trừ đùi gà `74` ở map nhà và đứa bé `78` ở vách núi), và cập nhật tiến độ Nhiệm vụ chính/phụ/bang hội. |
| `addItem(ItemMap itemMap)` | `itemMap` | Hàm `synchronized` thêm vật phẩm mới rơi vào đầu danh sách `items`. |
| `removeItemMap(ItemMap itemMap)` | `itemMap` | Hàm `synchronized` xóa vật phẩm khỏi danh sách `items`. |
| `getRandomPlayerInMap()` | Không có | Chọn ngẫu nhiên một thực thể trong `notBosses` không tàng hình (`!isVoHinh`) và không bị giam trong kén Ma Bư (`maBuHold == null`) để quái/boss chọn làm mục tiêu. |
| `load_Me_To_Another(Player player)` | `player` | Gửi thông tin của `player` vừa mới vào khu cho tất cả người chơi khác đang có mặt trong khu (bỏ qua nếu ở map riêng tư `164`, và xử lý riêng cho `TrainingBoss` ở map offline). |
| `load_Another_To_Me(Player player)` | `player` | Gửi thông tin của tất cả các thực thể (`humanoids`) đang có sẵn trong khu cho `player` vừa mới bước vào. |
| `loadBoss(Boss boss)` | `boss` | Đồng bộ thông tin hai chiều giữa `boss` mới xuất hiện và các `bosses` khác trong khu. |
| `infoPlayer(Player plReceive, Player plInfo)` | Người nhận `plReceive`, nguồn `plInfo` | Đóng gói và gửi gói tin **`Message(-5)`** chứa toàn bộ ngoại hình, chỉ số HP, Bang hội, PK, Cờ, Thú cưỡi, Aura, Nón, Hiệu ứng khỉ/chibi và trạng thái chết (**`Message(-8)`**) của `plInfo` tới client của `plReceive`. |
| `mapInfo(Player pl)` | `pl` | Đóng gói và gửi gói tin **`Message(-24)`** dựng toàn bộ khung cảnh bản đồ cho client của `pl`: thông số Map/Zone, tọa độ nhân vật, danh sách `WayPoint`, danh sách `Mob` (kèm trạng thái băng/lửa/gió/BigBoss), danh sách `Npc` (đã lọc theo nhiệm vụ), danh sách `ItemMap`, dữ liệu byte Background Item (`data/map/item_bg_map_data/`) và Effect Map (`data/map/eff_map/`). |
| `isInTrap(Player player)` | `player` | Kiểm tra tọa độ `(x, y)` của người chơi có nằm trong vùng hình chữ nhật `(x, y, w, h)` của bất kỳ bẫy `TrapMap` nào trong khu hay không. |
| `sendBigBoss(Player player)` | `player` | Nếu trong khu có Hirudegarn còn sống ở giai đoạn biến hình (`lvMob >= 1` hoặc `>= 2`), gửi gói tin cập nhật hình dạng BigBoss cho người chơi mới vào. |
| `getMaBuHold()` | Không có | Tìm một ô kén giam trống (`hold.player == null`) tại khu tương ứng của Map `128` (Bụng Ma Bư). Khởi tạo 4 slot nếu danh sách chưa tồn tại. |
| `setMaBuHold(int slot, int zoneId, Player player)` | `slot`, `zoneId`, `player` | Gán người chơi bị Ma Bư biến kẹo/nuốt vào ô kén `slot` tại Map `128`. |
| `isKhongCoTrongTaiTrongKhu()` | Không có | Kiểm tra xem khu vực hiện tại có đủ điều kiện để NPC Trọng Tài xuất hiện hay không (loại trừ map nhà, map sự kiện, võ đài và toàn bộ các map phó bản). |
| `findNearestPlayer(Pet pet)` | `pet` | Tìm người chơi còn sống gần đệ tử (`pet`) nhất theo khoảng cách trục ngang `|x1 - x2|` (trừ sư phụ của đệ tử). |
| `findNearestMob(Pet pet)` | `pet` | Tìm quái vật còn sống gần đệ tử (`pet`) nhất theo khoảng cách trục ngang `|x1 - x2|`. |

---

#### 3. `ItemMap.java` (203 dòng)
Biểu diễn một vật phẩm đang nằm trên mặt đất trong một `Zone` (vật phẩm đánh quái rơi ra, Vệ tinh, Ngọc Rồng Sao Đen, Ngọc Rồng Namếc, hoặc vật phẩm nhiệm vụ).

**Các thuộc tính chính:**
* `zone`, `itemMapId`, `itemTemplate`, `quantity`, `x`, `y`: Khu vực chứa, ID định danh trên map, mẫu vật phẩm, số lượng và tọa độ.
* `playerId`, `clanId`: ID chủ sở hữu được quyền nhặt (`-1` là ai cũng nhặt được) và ID bang hội (dùng cho Vệ tinh).
* `options` (`List<ItemOption>`): Các dòng chỉ số của trang bị/vật phẩm.
* `createTime`, `lastTimeMoveToPlayer`, `timeMoveToPlayer = 10000`: Mốc thời gian rơi ra và bộ đếm di chuyển (dành cho Ngọc Rồng Sao Đen).
* `isBlackBall`, `isNamecBall`, `isPickedUp`: Cờ đánh dấu loại ngọc rồng và trạng thái đã được nhặt.

**Chi tiết các Constructor & Hàm:**

| Tên hàm / Constructor | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `ItemMap(Zone, int tempId, int quantity, int x, int y, long playerId)` | Thông tin vật phẩm theo `tempId` | Khởi tạo vật phẩm trên đất, tự động lấy `itemMapId` từ `zone.countItemAppeaerd++`, tra cứu `ItemTemplate`, kiểm tra cờ Ngọc Rồng Đen/Namếc và tự động gọi `this.zone.addItem(this)`. |
| `ItemMap(Zone, ItemTemplate temp, ...)` | Thông tin vật phẩm theo `ItemTemplate` | Tương tự constructor trên nhưng nhận trực tiếp đối tượng `ItemTemplate` có sẵn. |
| `ItemMap(ItemMap itemMap)` | `itemMap` gốc | Copy constructor nhân bản một `ItemMap` có sẵn và thêm vào `zone`. |
| `update()` | Không có | Quản lý vòng đời của vật phẩm trên mặt đất:<br>1. **Ngọc Rồng Sao Đen (`isBlackBall`)**: Mỗi 10 giây tự động nhảy tới vị trí của người chơi đầu tiên trong khu.<br>2. **Vệ tinh (`type == 22`)**: Gọi `satelliteUpdate()` để cộng buff cho người chơi đứng gần.<br>3. **Mở khóa quyền nhặt (sau 45 giây)**: Đổi `playerId = -1` để người khác nhặt được (trừ vệ tinh, item `726`, `992`).<br>4. **Tự hủy biến mất (sau 50 giây cho item thường hoặc 30 phút cho vệ tinh)**: Xóa khỏi map và gửi gói tin biến mất về client (trừ Ngọc Rồng Namếc, đùi gà ở map nhà, đứa bé `78`, item `726` và ngọc rồng trong Doanh Trại).<br>5. **Xử lý riêng**: Xóa item `726` ở map `52` (Đại Hội Võ Thuật) nếu chủ nhân rời khu; xóa item `460` (cục xương) của `playerId == 123456789` sau 5 giây. |
| `findPlayerByID(long id)` | `id` | Kiểm tra xem người chơi có `id` còn đang đứng trong `this.zone` hay không. |
| `satelliteUpdate()` | Không có (`private`) | Kiểm tra các người chơi đứng trong bán kính `< 200px` cùng chủ sở hữu hoặc cùng Bang hội để kích hoạt hiệu ứng Vệ tinh: **Vệ tinh Trí lực (`342`)** hồi 10% MP; **Vệ tinh Trí tuệ (`343`)** tăng trí tuệ; **Vệ tinh Phòng thủ (`344`)** tăng thủ; **Vệ tinh Sinh lực (`345`)** hồi 10% HP. |
| `reAppearItem()` | Không có (`private`) | Gửi gói tin xóa vị trí cũ và thả lại vật phẩm ở tọa độ mới (dùng khi Ngọc Rồng Sao Đen tự dịch chuyển theo người chơi). |
| `isNotNullItem()` | Không có | Trả về `true` nếu `itemTemplate != null`. |
| `dispose()` | Không có | Giải phóng các tham chiếu (`zone`, `itemTemplate`, `options` gán về `null`) để hỗ trợ Garbage Collector thu hồi bộ nhớ. |

---

#### 4. `WayPoint.java` (23 dòng)
Class dữ liệu (POJO) biểu diễn một vùng cổng chuyển tiếp giữa hai bản đồ.
* **Các thuộc tính**:
  * `minX`, `minY`, `maxX`, `maxY` (`short`): Vùng hình chữ nhật giới hạn của cổng trên bản đồ hiện tại.
  * `isEnter`, `isOffline` (`boolean`): Cờ xác định loại cổng vào trong hay cổng map offline.
  * `name` (`String`): Tên hiển thị của cổng trên màn hình client.
  * `goMap` (`int`), `goX`, `goY` (`short`): ID bản đồ đích và tọa độ `(X, Y)` mà người chơi sẽ xuất hiện khi bước qua cổng.

---

#### 5. `TrapMap.java` (27 dòng)
Biểu diễn vùng bẫy gây sát thương trên bản đồ (như bẫy chông/xiên ở map Động Hải Tặc 135 trong Bản Đồ Kho Báu).
* **Thuộc tính**: `x`, `y`, `w`, `h` (tọa độ và kích thước vùng bẫy), `effectId` (mã hiệu ứng, ví dụ `49` là chông xiên), `dame` (sát thương cơ bản).
* **Hàm `doPlayer(Player player)`**: Nếu `effectId == 49`, người chơi còn sống và đã qua 1000ms (1 giây) kể từ lần bị xiên trước (`lastTimeAnXienTrapBDKB`), hàm sẽ trừ máu người chơi một lượng `dame ± 10%`, gửi cập nhật HP, phát hiệu ứng hình ảnh bẫy xiên cho toàn bộ khu vực (`EffectMapService`) và lưu lại mốc thời gian bị xiên.

---

#### 6. `MaBuHold.java` (18 dòng)
Biểu diễn một ô kén giam người chơi trong Bụng Ma Bư (Map 128 - Sự kiện Ma Bư 14H).
* **Thuộc tính**: `slot` (vị trí kén từ 0 đến 3), `player` (người chơi đang bị giam), `x`, `y` (tọa độ hiển thị kén).
* **Constructor `MaBuHold(int slot, Player player)`**: Gán `slot`, `player` và tự động ánh xạ tọa độ `(x, y)` cố định tương ứng với 4 vị trí kén:
  * Slot 0: `(196, 257)`
  * Slot 1: `(340, 256)`
  * Slot 2: `(412, 232)`
  * Slot 3+: `(532, 257)`

---

### B. Phân Hệ 2: Hệ Thống Phó Bản & Sự Kiện (`nro.models.map.phoban`)

#### 7. `BanDoKhoBau.java` (272 dòng)
Quản lý một phiên (instance) phó bản **Bản Đồ Kho Báu (Treasure Under Sea)** của một Bang hội (các map từ `135` đến `138`).

* **Hằng số**: `POWER_CAN_GO_TO_DBKB = 2_000_000_000` (2 tỷ sức mạnh), `AVAILABLE = 50` (50 phòng tối đa), `TIME_BAN_DO_KHO_BAU = 1_800_000` (30 phút).
* **Chi tiết các hàm**:
  * `BanDoKhoBau(int id)` & `addZone(Zone zone)`: Khởi tạo phòng phó bản và thêm các khu vực thuộc phòng đó.
  * `run()`: Vòng lặp chạy trên Virtual Thread mỗi ~150ms khi `isOpened == true` và server không bảo trì, gọi `update()`.
  * `update()`:
    * Kiểm tra hết giờ (30 phút) hoặc hết 60 giây đếm ngược sau khi thắng (`kickoutbdkb`) -> gọi `finish()` và `dispose()`.
    * Kiểm tra bẫy `TrapMap` tại map `135` đối với mọi người chơi trong khu.
    * Kiểm tra xem toàn bộ quái (`zone.mobs`) và Boss (`zone.getBosses()`) trong tất cả các map đã bị tiêu diệt hết chưa (`allCharactersDead`).
    * Nếu đã diệt sạch hoặc chỉ còn 60 giây cuối -> bật cờ `kickoutbdkb = true` và mỗi 10 giây gửi thông báo đếm ngược *"Cái hang này sắp sập rồi..."*.
  * `openBanDoKhoBau(Player plOpen, Clan clan, byte level)`: Thiết lập cấp độ (`level`), gắn phó bản vào `clan`, gọi `init()` khởi tạo quái/boss, đưa người mở vào map (`goToDBKB`) và gửi đồng hồ đếm ngược trên UI cho toàn bang.
  * `sendThanhTichBanDoKhoBau(Player pl)`: Tính thời gian hoàn thành, cập nhật kỷ lục cấp độ cao nhất hoặc thời gian nhanh nhất cho Bang hội và lưu vào DB.
  * `init()`: Thiết lập sát thương bẫy (`level * 100.000`), hồi sinh toàn bộ quái ở các map `135, 136, 137, 138` với chỉ số Dame/HP nhân theo `level` (cài đặt một số quái ở vị trí cố định thành quái Siêu cấp `lvMob = 1`), khởi tạo Boss **`TrungUyXanhLo`** ở map `137`, và khởi chạy Virtual Thread.
  * `finish()` & `kickOutOfBDKB(Player player)`: Ghi nhận thành tích cho các thành viên còn trong phó bản và đưa toàn bộ người chơi về Đảo Kame (Map `5`, tọa độ `x = 1038`) bằng tàu vũ trụ.
  * `getMapById(int mapId)`: Lấy `Zone` tương ứng với `mapId` trong phiên phó bản này.
  * `sendTextBanDoKhoBau()` / `removeTextBanDoKhoBau()`: Gửi hoặc xóa thanh thời gian phó bản (`ItemTimeService`) cho tất cả thành viên bang đang online.
  * `dispose()`: Cho Boss rời map, xóa toàn bộ vật phẩm rơi trên đất, xóa thanh thời gian và giải phóng tham chiếu `clan`, `boss`.

---

#### 8. `BlackBallWar.java` (147 dòng)
Quản lý sự kiện **Tranh Đoạt Ngọc Rồng Sao Đen** (diễn ra từ 20h00 đến 21h00 hằng ngày tại các map `85..91`). Mỗi đối tượng `BlackBallWar` gắn với 1 `Zone` cụ thể.

* **Hằng số**: Giờ mở `20:00:00`, giờ cho phép nhặt ngọc `20:30:00`, giờ đóng `21:00:00`, thời gian giữ ngọc để thắng `TIME_WIN = 300_000` (5 phút), các hệ số phù hộ `X3, X5, X7` và giá vàng tương ứng (`10tr, 30tr, 50tr`).
* **Chi tiết các hàm**:
  * `BlackBallWar(Zone zone)` & `start()`: Gắn `zone` và khởi chạy một Virtual Thread chạy xuyên suốt vòng đời server.
  * `run()` & `update()`: Cứ mỗi 150ms kiểm tra nếu ngoài giờ sự kiện thì đặt `zone.finishBlackBallWar = false`, sau đó duyệt toàn bộ người chơi trong khu và gọi `updatePlayer(player)`.
  * `updatePlayer(Player player)`:
    * Nếu sự kiện đã đóng (`!TimeUtil.isBlackBallWarOpen()`) -> đẩy người chơi về trạm tàu vũ trụ (`kickOutOfMap`).
    * Nếu người chơi đang ôm Ngọc Rồng Sao Đen (`isHoldBlackBall()`): Kiểm tra nếu đã giữ đủ 5 phút (`TIME_WIN`) thì gọi `win(player)`; nếu chưa đủ thì mỗi 10 giây gửi thông báo nhắc số giây còn lại cần giữ.
  * `win(Player player)`: Đánh dấu `finishBlackBallWar = true`, tính số sao của viên ngọc (`tempId - 371`), trao thưởng sao đen cho người thắng và **toàn bộ thành viên trong Bang hội** (nếu thành viên đang online thì cộng trực tiếp, nếu offline thì tải từ DB qua `MrBlue.loadById`, cộng thưởng và lưu lại qua `PlayerDAO.updatePlayer`), sau đó đẩy tất cả người chơi ra khỏi khu.
  * `kickOutOfMap(Player player)` & `kickAllPlayersOutOfMap(Zone zone)`: Đổi cờ PK nếu đang đeo cờ đen (`cFlag == 8`), gửi thông báo kết thúc sự kiện và đưa người chơi về Trạm tàu vũ trụ theo hành tinh (`player.gender + 24`).

---

#### 9. `DestronGas.java` (259 dòng)
Quản lý một phiên phó bản **Khí Gas Hủy Diệt** của Bang hội (các map `147, 148, 149, 151, 152`).

* **Hằng số**: `POWER_CAN_GO_TO_KHI_GAS_HUY_DIET = 2_000_000_000` (2 tỷ sức mạnh), `AVAILABLE = 50`, `TIME_KHI_GAS_HUY_DIET = 1_800_000` (30 phút).
* **Chi tiết các hàm**:
  * `DestronGas(int id)` / `run()`: Khởi tạo và chạy vòng lặp Virtual Thread 150ms tương tự Bản Đồ Kho Báu.
  * `update()`:
    * Kiểm tra hết 30 phút hoặc hết 60 giây sau khi Boss cuối chết -> gọi `finish()` và `dispose()`.
    * Kiểm tra nếu toàn bộ quái thường (`zone.mobs`) trong các map đã chết hết và chưa gọi Boss (`!callBoss`) -> khởi tạo Boss **`DrLychee`** tại Map `148` (sau khi Dr. Lychee chết sẽ gọi tiếp Hatchiyack từ logic của class Boss).
    * Khi cờ `hatchiyatchDead == true` (hoặc còn 60 giây cuối) -> kích hoạt đếm ngược 60 giây nổ tung và thông báo mỗi 10 giây.
  * `openKhiGasHuyDiet(Player plOpen, Clan clan, byte level)`: Mở phó bản cho bang hội, gọi `init()` và gửi thanh thời gian.
  * `sendThanhTichKhiGas(Player pl)`: Cập nhật thành tích cấp độ/thời gian hoàn thành Khí Gas cho bang hội; nếu hoàn thành từ cấp 70 trở lên thì tăng biến đếm `pl.destronGas70CompletionCount++`.
  * `init()`: Hồi sinh quái trong các map với chỉ số nhân theo `level`, thiết lập các quái đặc biệt (`lvMob = 1`) tại các vị trí cố định ở map `147, 149, 151, 152` và khởi chạy Virtual Thread.
  * `finish()`, `kickOutOfKGHD(Player player)`, `getMapById(int mapId)`, `sendTextKhiGasHuyDiet()`, `removeTextKhiGasHuyDiet()`, `dispose()`: Ghi nhận thành tích, đẩy người chơi về Làng Aru (Map `0`) và dọn dẹp tài nguyên (xóa item, xóa boss, xóa thanh thời gian).

---

#### 10. `MajinBuu14H.java` (100 dòng)
Quản lý sự kiện **Ma Bư 14H** (gồm Map `127` và Map `128` - Bụng Ma Bư), với `AVAILABLE = 7` phòng.

* **Chi tiết các hàm**:
  * `MajinBuu14H(int id)` & `init()`: Khởi tạo phòng sự kiện và chạy Virtual Thread ngay khi khởi tạo.
  * `run()` & `update()`: Chạy mỗi 150ms. Nếu hết giờ mở sự kiện (`!TimeUtil.isMabu14HOpen()`) thì gọi `finish()` đẩy toàn bộ người chơi (trừ Admin) về nhà (`player.gender + 21`). Nếu đang trong giờ sự kiện, duyệt qua các ô kén `MaBuHold` để giải phóng các ô mà người chơi đã thoát khỏi trạng thái bị trói (`hold.player.maBuHold == null`).
  * `getMaBuHold()`: Tìm và trả về một ô kén `MaBuHold` còn trống tại Map `128`.
  * `getMapById(int mapId)`, `finish()`, `kickOut(Player player)`: Hỗ trợ tìm khu theo `mapId` và đưa người chơi về nhà khi kết thúc sự kiện.

---

#### 11. `RedRibbonHQ.java` (436 dòng)
Quản lý phó bản **Doanh Trại Độc Nhãn** của Bang hội (các map từ `53` đến `62`). Điểm đặc biệt của phó bản này là **máu và sức đánh của Quái/Boss được tính động dựa trên tổng Sức đánh (`totalDamage`) và tổng HP (`totalHp`) của các thành viên bang hội đang online**.

* **Hằng số**: `N_PLAYER_CLAN = 5` (tối thiểu 5 thành viên trong bang), `AVAILABLE = 50`, `TIME_DOANH_TRAI = 1_800_000` (30 phút đánh), `TIME_PICK_DOANH_TRAI = 300_000` (5 phút nhặt ngọc rồng sau khi tìm thấy Độc Nhãn).
* **Chi tiết các hàm**:
  * `RedRibbonHQ(int id)`, `addZone(Zone)`, `getMapById(int)`: Khởi tạo và quản lý danh sách `Zone` của Doanh Trại.
  * `openDoanhTrai(Player player)`: Mở Doanh Trại, gọi `init()` tạo quái/boss, sau đó tự động kéo cả những thành viên cùng bang đang đứng gần cửa Doanh Trại (`x` từ `1285` đến `1645`, đã vào bang `>= 1` ngày và còn sống) vào Map `53` (Tường thành 1) cùng người mở.
  * `init()`: Cộng tổng Dame và tổng HP của các thành viên bang đang online (`clan.membersInGame`), thiết lập chỉ số cho toàn bộ quái (`dame = totalHp / mobTempId`, `maxHp = totalDamage * mobTempId`) và khởi tạo 5 loại Boss ở các map tương ứng:
    * Map `59`: **`TrungUyTrang`** (1.0x chỉ số cơ sở)
    * Map `62`: **`TrungUyXanhLo`** (1.1x chỉ số cơ sở)
    * Map `55`: **`TrungUyThep`** (1.15x chỉ số cơ sở)
    * Map `54`: **`NinjaAoTim`** (1.2x chỉ số cơ sở)
    * Map `57`: **4 con `RobotVeSi`** (1.3x chỉ số cơ sở)
  * `update()`: Kiểm tra hết giờ (30 phút đánh hoặc 5 phút nhặt ngọc khi `isTimePicking == true`). Kiểm tra nếu toàn bộ quái và Boss trong tất cả các map đã chết hết thì bật cờ `winDT = true` và gửi thông báo *"Mau đi tìm Độc Nhãn"*.
  * `NR(Zone zone)` & `randomNR()`: Rải ngẫu nhiên từ 3 đến 7 viên Ngọc Rồng (từ 4 sao đến 7 sao thường, tỷ lệ 1/500 ra ngọc 1–4 sao) vào mỗi map của Doanh Trại khi hoàn thành nhiệm vụ giải cứu.
  * `updateHPDame()`: Tính toán lại chỉ số HP/Dame của toàn bộ quái và Boss còn sống trong Doanh Trại (dùng khi có thành viên mới vào hoặc thay đổi chỉ số).
  * `finish()`, `kickOutOfDT(Player)`, `sendTextDoanhTrai()`, `sendTextTimePickDoanhTrai()`, `removeTextDoanhTrai()`, `dispose()`: Xử lý kết thúc phó bản, đưa người chơi về nhà và dọn dẹp tài nguyên.

---

#### 12. `SnakeWay.java` (269 dòng)
Quản lý phó bản **Con Đường Rắn Độc** của Bang hội (các map từ `141` đến `144`).

* **Hằng số**: `POWER_CAN_GO_TO_CDRD = 2_000_000_000` (2 tỷ sức mạnh), `AVAILABLE = 50`, `TIME_CON_DUONG_RAN_DOC = 1_800_000` (30 phút).
* **Chi tiết các hàm**:
  * `openConDuongRanDoc(Player plOpen, Clan clan, byte level)`: Mở phó bản Con Đường Rắn Độc theo `level`, gọi `init()` và gửi thanh thời gian.
  * `init()`: Hồi sinh quái ở các map `141..144` theo `level` (quái thứ 5 ở mỗi map là quái siêu cấp `lvMob = 1` mạnh gấp 120 lần quái thường). Tại Map `144` (Hoang mạc), khởi tạo đội hình Boss Xayda gồm: **6 con `SAIBAMEN`**, **1 `NADIC` (Nappa)** và **1 `CADICH` (Vegeta)**, sau đó khởi chạy Virtual Thread.
  * `update()`: Kiểm tra hết thời gian 30 phút hoặc đếm ngược 60 giây sau khi kết thúc (`endCDRD == true`). Kiểm tra nếu toàn bộ quái thường đã chết hết thì bật cờ `allMobsDead = true` (điều kiện để người chơi nói chuyện với Thần Mèo Karin và dịch chuyển sang Map `144` đánh đội Boss Xayda).
  * `sendThanhTichCDRD(Player pl)`: Cập nhật kỷ lục cấp độ và thời gian hoàn thành Con Đường Rắn Độc cho Bang hội.
  * `getNumBossAlive()`: Đếm số lượng Boss trong phó bản đang ở trạng thái khác `BossStatus.REST`.
  * `finish()`, `kickOutOfCDRD(Player)`, `getMapById(int)`, `sendTextConDuongRanDoc()`, `removeTextConDuongRanDoc()`, `dispose()`: Đưa người chơi về Đảo Kame (Map `5`) và dọn dẹp phó bản khi kết thúc.

---

### C. Phân Hệ 3: Dịch Vụ Quản Lý & Điều Phối Bản Đồ (`nro.models.map.service`)

#### 13. `ChangeMapService.java` (1.140 dòng)
Singleton Service trung tâm xử lý **mọi hình thức di chuyển giữa các bản đồ và khu vực** (chạy qua WayPoint, đi Tàu vũ trụ, dùng Capsule, Dịch chuyển tức thời Yardrat, vào Phó bản, vào Tương lai) và đồng bộ trạng thái nhân vật/hiệu ứng khi tải map.

**Chi tiết toàn bộ các hàm:**

| Tên hàm | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `gI()` | Không có | Trả về thể hiện Singleton duy nhất của `ChangeMapService`. |
| `openChangeMapTab(Player pl)` | `pl` | Gửi gói tin **`Message(-91)`** mở bảng danh sách bản đồ để bay (khi dùng Capsule, chọn map Ngọc Rồng Đen, hoặc chọn map Ma Bư dựa theo `pl.idMark.getTypeChangeMap()`). |
| `openZoneUI(Player pl)` | `pl` | Gửi gói tin **`Message(29)`** mở bảng chọn Khu vực (Zone UI). Chặn mở ở map offline và map phó bản (trừ Admin). Gửi kèm màu trạng thái khu (`< 5` người: Xanh `0`, `< 8` người: Vàng `1`, `>= 8` người: Đỏ `2`) và thông tin cặp đấu Võ đài nếu khu đang thi đấu (`isCompeting`). |
| `changeZone(Player pl, int zoneId)` | `pl`, `zoneId` | Xử lý đổi khu vực trong cùng 1 bản đồ. Kiểm tra chặn ở map offline, phó bản, map Ma Bư, bụng Bư (map `128`), kiểm tra thời gian hồi đổi khu **5 giây** (trừ Admin/Boss) và kiểm tra khu đầy trước khi gọi `changeMap`. |
| `changeMapBySpaceShip(...)` (2 hàm overload) | `(Player, int mapId, int zone, int x)` hoặc `(Player, Zone zoneJoin, int x)` | Chuyển bản đồ bằng Tàu vũ trụ (`AUTO_SPACE_SHIP`). Nếu người chơi đang chết hoặc bị thương: nếu có Tàu vũ trụ Tennis (`haveTennisSpaceShip`) sẽ được hồi đầy 100% HP/MP; nếu dùng tàu thường khi đang chết sẽ được hồi sinh với 1 HP / 1 MP. |
| `changeMapInYard(...)` (2 hàm overload) | `(Player, int mapId, int zoneId, int x)` hoặc `(Player, Zone, int x)` | Chuyển bản đồ và đặt nhân vật đứng ngay trên mặt đất (`yPhysicInTop(x, 100)`) mà không có hiệu ứng tàu vũ trụ (`NON_SPACE_SHIP`). |
| `changeMap(...)` (2 hàm overload public) | `(Player, int mapId, int zone, int x, int y)` hoặc `(Player, Zone, int x, int y)` | Chuyển bản đồ đến đúng tọa độ `(x, y)` chỉ định với kiểu `NON_SPACE_SHIP`. |
| `changeMapYardrat(Player pl, Zone zoneJoin, int x, int y)` | `pl`, `zoneJoin`, `x`, `y` | Chuyển bản đồ kèm hiệu ứng Dịch chuyển tức thời (`TELEPORT_YARDRAT`). |
| `changeMap(Player pl, Zone zoneJoin, int mapId, int zoneId, int x, int y, byte typeSpace)` | Đầy đủ tham số (`private`) | **Hàm lõi (Core Method) xử lý chuyển map**:<br>1. Kiểm tra nếu đang đeo Ngọc Rồng Namếc (`idNRNM != -1`): chặn chuyển map nếu chưa đủ 30 giây; cập nhật vị trí viên ngọc hoặc làm rơi ngọc nếu vào map không hợp lệ.<br>2. Hủy giao dịch đang mở (`TransactionService.gI().cancelTrade(pl)`).<br>3. Tìm khu hợp lệ và kiểm tra quyền vào map qua `checkMapCanJoin` & `checkMapCanJoinByYardart` (và chặn vào map `111` nếu sức mạnh `>= 1.500.000`).<br>4. Ngắt hiệu ứng gồng KI (`stopCharge`) và trói (`removeUseTroi`).<br>5. Gọi `goToMap(pl, zoneJoin)`, kéo đệ tử đi theo (`pl.pet.joinMapMaster()`), xóa màn hình cũ (`clearMap`) và gửi gói tin cấu trúc map mới (`zoneJoin.mapInfo(pl)` - gói `-24`).<br>6. Xử lý thưởng bình Nước phép khi xuống tầng sâu trong sự kiện Ma Bư (`mapId 115..120`).<br>7. Kiểm tra thay đổi môi trường: **Hành tinh Cold** (giảm 50% HP và Sức đánh) và **Tương lai** (cảnh báo chết bị trừ tiềm năng), sau đó tính lại chỉ số (`Service.gI().point(pl)`). |
| `changeMapWaypoint(Player player)` | `player` | Xử lý khi người chơi bước vào cổng `WayPoint` ở rìa map:<br>- Xử lý riêng việc rơi từ Tháp Karin (map `45`, `46`) xuống tầng dưới.<br>- Tìm `WayPoint` người chơi đang đứng và khu vực đích.<br>- Nếu đang chuyển giữa các map trong Khí Gas Hủy Diệt (trừ map `148`): bật hiệu ứng đếm ngược 5 giây (`effectChangeMap`).<br>- Nếu ở map `47` (Rừng Karin), đã diệt hết quái Con Đường Rắn Độc (`allMobsDead`) và đã nói chuyện với Thần Mèo: dịch chuyển thẳng vào Map `144` để đánh Vegeta/Nappa. |
| `resetPoint(Player player)` | `player` | Đẩy lùi người chơi cách rìa bản đồ 60px và gửi gói tin đặt lại vị trí khi người chơi không đủ điều kiện qua cổng `WayPoint`. |
| `finishLoadMap(Player player)` | `player` | Được gọi khi client gửi gói tin báo đã tải xong bản đồ: đồng bộ người chơi hai chiều (`load_Me_To_Another`, `load_Another_To_Me`), đồng bộ toàn bộ hiệu ứng kỹ năng/khiên/thôi miên/socola/trói/đẻ trứng (`sendEffectMapToMe`, `sendEffectMeToMap`), kiểm tra hoàn thành nhiệm vụ tới map, kiểm tra tự động xuống tầng dưới Ma Bư, hiển thị menu Tập tự động, đồng bộ ngoại hình Ginyu Hoán đổi thân xác, gửi trạng thái BigBoss Hirudegarn (map `126`) và hiển thị Quả trứng Ma Bư / Dưa hấu ở map nhà. |
| `sendEffectMeToMap(Player player)` | `player` (`private`) | Gửi hiệu ứng đang bật của `player` (Khiên năng lượng `-124`, Đẻ trứng `mobMe` của bản thân và đệ tử `-95`) cho những người chơi khác trong khu. |
| `sendEffectMapToMe(Player player)` | `player` (`private`) | Gửi các hiệu ứng đang tồn tại sẵn trong khu (Quái hoặc Người chơi khác đang bị Thôi miên, hóa Socola `-112`, Choáng/Thái Dương Hạ San, Trói, Khiên năng lượng, Đẻ trứng) cho `player` vừa mới vào khu. |
| `spaceShipArrive(Player player, byte typeSendMSG, byte typeSpace)` | `player`, phạm vi gửi, loại tàu | Gửi gói tin **`Message(-65)`** hiển thị hoạt ảnh tàu vũ trụ đáp xuống cho tất cả (`0`), cho bản thân (`1`) hoặc cho người khác trong khu (`2`). |
| `goToMap(Player player, Zone zoneJoin)` | `player`, `zoneJoin` | Rời khu cũ (`exitMap`), di chuyển trứng `mobMe` sang khu mới, gán `player.zone = zoneJoin` và thêm người chơi vào danh sách của khu mới (`zoneJoin.addPlayer`). |
| `exitMap(Player player)` | `player` | Xử lý toàn bộ thủ tục khi người chơi rời khỏi một khu vực: xử thua nếu đang PvP (`RUNS_AWAY`), tắt cờ PK Đại Hội Võ Thuật, làm rơi Ngọc Rồng Sao Đen nếu đang giữ, gỡ hiệu ứng Trói, xóa buff phù hộ HP/Dame nếu rời map Ngọc Rồng Đen, xóa khỏi danh sách `zone` và gửi gói tin **`Message(-6)`** báo cho người chơi khác xóa nhân vật này khỏi màn hình. |
| `goToTuongLai(Player player)` | `player` | Bật trạng thái bay sang Tương lai bằng Cỗ máy thời gian (hiệu ứng chờ 60 giây). |
| `goToDBKB(Player player)` | `player` | Kiểm tra giới hạn tối đa **3 lần/ngày** vào Bản Đồ Kho Báu của người chơi, sau đó bật hiệu ứng chuyển map 5 giây vào hang kho báu. |
| `goToQuaKhu(Player player)` | `player` | Đưa người chơi từ Tương lai quay về Quá khứ (Map `24` - Trạm tàu vũ trụ Trái Đất). |
| `goToPotaufeu(Player player)` | `player` | Đưa người chơi bằng tàu vũ trụ đến Hành tinh Potaufeu (Map `139`). |
| `effectChangeMap(Player player, int seconds, byte type)` | `player`, `seconds`, `type` | Gửi gói tin **`Message(-105)`** hiển thị màn hình chờ chuyển cảnh trong `seconds` giây (đi Tương lai hoặc vào Phó bản). |
| `checkMapCanJoin(Player player, Zone zoneJoin)` | `player`, `zoneJoin` | **Bộ lọc điều kiện nhiệm vụ & hành tinh**: Kiểm tra tiến độ nhiệm vụ chính tuyến (`TaskService.gI().getIdTask(player)`) từ `TASK_1_0` đến `TASK_27_0` ứng với từng nhóm bản đồ (Trái Đất, Namếc, Xayda, Nappa, Tương lai, Cold, Võ đài Xên...), chặn sức mạnh `> 1.500.000` ở map `111`, và chặn người chơi khác hành tinh vào nhà của hành tinh khác (map `21, 22, 23`). |
| `checkMapCanJoinByYardart(Player, Zone)` | `player`, `zoneJoin` | Chặn người chơi dùng Dịch chuyển tức thời vào thẳng Ngũ Hành Sơn (map `122, 123, 124`). |
| `checkJoinSpecialMap(Player)` / `checkJoinMapMaBu(Player)` | `player` (`private`) | Kích hoạt logic tham gia sự kiện khi người chơi bước vào map Ngọc Rồng Sao Đen (`85..91`) hoặc map Ma Bư (`114..120`). |
| `changeMapNonSpaceship(Player, int mapid, int x, int y)` | `player`, `mapid`, `x`, `y` | Tìm khu vực còn trống của `mapid` và chuyển người chơi tới tọa độ `(x, y)` không dùng tàu vũ trụ. |
| `getMapCanJoin(Player player, int mapId)` | `player`, `mapId` | Tìm một `Zone` của `mapId` có số người chơi `< 7` (`PLAYERS_TIEU_CHUAN_TRONG_MAP`). |
| `getZoneJoinByMapIdAndZoneId(...)` & `getMapById(int mapId)` | `player`, `mapId`, `zoneId` | Tra cứu `Map` từ `Manager.MAPS` và lấy `Zone` theo chỉ số `zoneId`. |

---

#### 14. `MapService.java` (629 dòng)
Singleton Service cung cấp các hàm tiện ích đọc dữ liệu tile map, tìm kiếm `Zone` khả dụng (đặc biệt là điều hướng `Zone` riêng của từng Bang hội trong Phó bản), gửi gói tin di chuyển và hơn 30 hàm kiểm tra phân loại bản đồ (`isMap...`).

**Chi tiết các hàm:**

| Nhóm hàm | Tên hàm | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| **Đọc dữ liệu & Va chạm** | `getWaypointPlayerIn(Player player)` | Kiểm tra tọa độ `(x, y)` hiện tại của người chơi có nằm trong vùng giới hạn `(minX..maxX, minY..maxY)` của cổng `WayPoint` nào trong map hay không. |
| | `readTileIndexTileType(int tileTypeFocus)` | Đọc file nhị phân `data/map/tile_set_info` để trích xuất danh sách các mã tile mang thuộc tính va chạm `tileTypeFocus` (như mặt đất `T_TOP = 2`) cho từng bộ tileset. |
| | `readTileMap(int mapId)` | Đọc file `data/map/tile_map_data/<mapId>` trả về mảng 2 chiều `int[h][w]` biểu diễn ma trận ô tile của bản đồ. |
| **Điều phối & Tìm kiếm Zone** | `getMapCanJoin(Player player, int mapId, int zoneId)` | **Hàm định tuyến Zone thông minh (198 dòng)**:<br>1. Nếu là map offline -> luôn trả về `zone 0`.<br>2. Nếu `mapId` là map Phó bản (Doanh Trại, Bản Đồ Kho Báu, Khí Gas, Con Đường Rắn Độc) nhưng người chơi **không có bang hoặc bang chưa mở phó bản** -> tự động cứu hộ bằng cách trả về khu vực an toàn (map nhà, Đảo Kame `5` hoặc Hành tinh Kaio `48`).<br>3. Nếu bang hội đang mở phó bản -> kiểm tra người chơi đã diệt hết quái và Boss ở map hiện tại chưa; nếu chưa diệt hết thì trả về `null` (chặn qua cửa), nếu đã diệt hết thì trả về đúng `Zone` thuộc phiên phó bản của bang hội đó.<br>4. Nếu là map thường -> gọi `getZone(mapId)` (khi `zoneId == -1`) hoặc `getZoneByMapIDAndZoneID`. |
| | `getAllMaps()` | Hàm lấy danh sách bản đồ (hiện đang bị lỗi đệ quy vô tận, xem Phần 3). |
| | `getZone(int mapId)` | Chọn ngẫu nhiên một `Zone` chưa đầy người (`getNumOfPlayers() < maxPlayer`) của `mapId`. |
| | `getZoneByMapIDAndZoneID(int mapId, int zoneId)` | Lấy chính xác `Zone` theo `mapId` và `zoneId`. |
| | `getMapById(int mapId)` | Tìm đối tượng `Map` trong `Manager.MAPS` theo `mapId`. |
| | `getMapForCalich()` | Chọn ngẫu nhiên một bản đồ từ `27` đến `29` để đặt NPC Calích. |
| | `getMapWithRandZone(int mapId)` | Chọn ngẫu nhiên một `Zone` bất kỳ của `mapId` (dùng cho Boss xuất hiện ngẫu nhiên khu). |
| | `getPlanetName(byte planetId)` | Đổi mã hành tinh (`0, 1, 2`) sang tên chuỗi (`"Trái đất"`, `"Namếc"`, `"Xayda"`). |
| **Danh sách Map Bay** | `getMapCapsule(Player pl)` | Tạo danh sách các `Zone` hiển thị khi dùng Capsule bay (bao gồm: Chỗ cũ `mapBeforeCapsule`, Nhà, Rừng Karin, Thần Điện, các Làng, Đảo Kame, Trạm tàu vũ trụ, Siêu thị, Võ đài...). |
| | `getMapBlackBall()` / `getMapMaBu()` | Trả về danh sách 7 map Ngọc Rồng Sao Đen (`85..91`) và 7 map Ma Bư (`114..120`). |
| | `addListMapCapsule(Player, List<Zone>, Zone)` | Thêm `Zone` vào danh sách Capsule nếu không bị trùng lặp và khác map hiện tại của người chơi. |
| **Mạng & Đồng bộ** | `sendPlayerMove(Player player)` | Gửi gói tin **`Message(-7)`** phát sóng tọa độ `(x, y)` mới của `player` cho toàn bộ người chơi khác trong cùng `Zone`. |
| | `goToMap(Player player, Zone zoneJoin)` | Rời khu cũ qua `ChangeMapService.gI().exitMap` và thêm người chơi vào `zoneJoin`. |
| | `getPlayersInMap(int mapId)` | Gom tất cả người chơi trong mọi `Zone` của `mapId`. |
| **Phân loại Bản đồ (`isMap...`)** | 30 hàm boolean (`isMapOffline`, `isMapBlackBallWar`, `isMapMaBu`, `isHome`, `isMapDoanhTrai`, `isMapBanDoKhoBau`, `isMapConDuongRanDoc`, `isMapKhiGasHuyDiet`, `isMapPhoBan`, `isMapCold`, `isMapTuongLai`, `isMapUpPorata`, `isMap3Planets`, v.v.) | Kiểm tra nhanh `mapId` có thuộc nhóm bản đồ đặc thù hay không dựa trên khoảng giá trị ID bản đồ. |

---

#### 15. `ItemMapService.java` (72 dòng)
Singleton Service hỗ trợ xử lý nghiệp vụ nhặt, kiểm tra và xóa vật phẩm trên mặt đất (`ItemMap`).

* **Chi tiết các hàm**:
  * `gI()`: Trả về Singleton instance của `ItemMapService`.
  * `pickItem(Player player, int itemMapId, boolean isThuHut)`: Kiểm tra giãn cách thời gian nhặt đồ tối thiểu **1000ms (1 giây)** đối với người chơi thường (bỏ qua cooldown nếu là nội tại/bùa Thu hút `isThuHut == true` hoặc là Bot `player.isBot`), sau đó ủy quyền cho `player.zone.pickItem(player, itemMapId)`.
  * `removeItemMapAndSendClient(ItemMap itemMap)`: Gửi gói tin xóa vật phẩm về client rồi xóa khỏi bộ nhớ server.
  * `sendItemMapDisappear(ItemMap itemMap)`: Gửi gói tin **`Message(-21)`** kèm `itemMapId` cho toàn bộ người chơi trong khu để xóa hình ảnh vật phẩm trên màn hình.
  * `removeItemMap(ItemMap itemMap)`: Xóa `itemMap` khỏi `zone.items` và gọi `itemMap.dispose()`.
  * `findItemMapByPlayer(Player player, int tempId)`: Kiểm tra xem trong khu hiện tại có vật phẩm `tempId` nào thuộc quyền sở hữu của `player` đang nằm trên đất hay không.
  * `isBlackBall(int tempId)`: Kiểm tra ID vật phẩm có phải Ngọc Rồng Sao Đen (`372..378`) không.
  * `isNamecBall(int tempId)`: Kiểm tra ID vật phẩm có phải Ngọc Rồng Namếc (`353..360`) không.
  * `isNamecBallStone(int tempId)`: Kiểm tra ID vật phẩm có phải Hóa thạch Ngọc Rồng Namếc (`362`) không.

---

#### 16. `NpcManager.java` (62 dòng)
Class tiện ích (`static` methods) tra cứu và lọc danh sách NPC hiển thị cho từng người chơi.

* **Chi tiết các hàm**:
  * `getByIdAndMap(int id, int mapId)`: Tìm `Npc` trong danh sách toàn cục `Manager.NPCS` khớp cả `tempId` và `mapId`.
  * `getNpc(byte tempId)`: Tìm `Npc` đầu tiên trong `Manager.NPCS` khớp `tempId`.
  * `getNpcsByMapPlayer(Player player)`: Lọc danh sách NPC trong map trước khi gửi về client (trong gói `-24`):
    * Ẩn **NPC Quả Trứng (`ConstNpc.QUA_TRUNG`)** ở map nhà nếu người chơi chưa có trứng Ma Bư (`player.mabuEgg == null`).
    * Ẩn **NPC Dưa Hấu (`ConstNpc.DUA_HAU`)** ở map nhà nếu người chơi không có dưa hấu (`player.DuaHauEgg == null`).
    * Ẩn **NPC Calích (`ConstNpc.CALICK`)** nếu người chơi chưa đạt nhiệm vụ `TASK_21_0`.
    * Ẩn **NPC Quốc Vương (`ConstNpc.QUOC_VUONG`)** nếu sức mạnh của người chơi `< 17.000.000.000` (17 tỷ).

---

#### 17. `NpcService.java` (108 dòng)
Singleton Service phụ trách tạo và gửi các bảng chọn (Menu) cũng như hội thoại hướng dẫn (Tutorial) của NPC về client.

* **Chi tiết các hàm**:
  * `gI()`: Trả về Singleton instance của `NpcService`.
  * `createMenuRongThieng(Player player, int indexMenu, String npcSay, String... menuSelect)`: Mở menu điều ước của Rồng Thiêng (`ConstNpc.RONG_THIENG`).
  * `createMenuConMeo(...)` (2 hàm overload): Mở menu ẩn thông qua NPC Con Mèo (`ConstNpc.CON_MEO`), hỗ trợ lưu kèm một đối tượng ngữ cảnh (`Object`) vào `NpcFactory.PLAYERID_OBJECT` theo `player.id` để xử lý ở bước xác nhận tiếp theo.
  * `createMenu(Player player, int indexMenu, byte npcTempId, int avatar, String npcSay, String... menuSelect)`: Lưu `indexMenu` vào `player.idMark` và gửi gói tin **`Message(32)`** về client để hiển thị hộp thoại NPC cùng các nút lựa chọn.
  * `createTutorial(...)` (2 hàm overload): Gửi gói tin **`Message(38)`** hiển thị khung hội thoại hướng dẫn (dùng avatar mặc định của Con Mèo hoặc `tempId` chỉ định).
  * `getAvatar(int npcId)`: Tra cứu mã hình đại diện (`avartar`) của NPC từ `Manager.NPCS` (mặc định trả về `1139` nếu không tìm thấy).

---

## Phần 3: Đánh Giá Chất Lượng Code (Code Quality Review)

### 1. Điểm Tổng Quan
* **Điểm đánh giá**: **5.5 / 10** (Đạt yêu cầu về mặt tính năng gameplay NRO truyền thống và đã bước đầu áp dụng Java 21+ Virtual Threads cho hệ thống phó bản, nhưng còn tồn tại **nhiều lỗi logic tiềm ẩn nguy cơ treo server**, vi phạm nguyên tắc SOLID, trùng lặp mã nguồn lớn và nghẽn I/O).

### 2. Các Điểm Mạnh (Strengths)
1. **Phân tách gói cơ bản hợp lý**: Đã tách biệt các thực thể (`Map`, `Zone`, `ItemMap`), các mô hình phó bản (`phoban/*`) và tầng dịch vụ (`service/*`), giúp dễ định vị chức năng hơn so với các bản source NRO cũ gộp chung một thư mục.
2. **Ứng dụng Java Virtual Threads (`Thread.ofVirtual()`)**: Các phó bản (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`, `BlackBallWar`, `MajinBuu14H`) sử dụng Virtual Threads có đặt tên rõ ràng (`"Bản Đồ Kho Báu: " + this.clan.name`) thay vì tạo Platform Thread truyền thống, giúp giảm đáng kể chi phí bộ nhớ khi nhiều bang hội mở phó bản cùng lúc.
3. **Cơ chế dọn dẹp gói tin (`msg.cleanup()`)**: Hầu hết các hàm tạo `Message` trong `ChangeMapService` đã đặt `msg.cleanup()` trong khối `finally`, hạn chế rò rỉ luồng `ByteArrayOutputStream`.

---

### 3. Các Lỗi Nghiêm Trọng Hiện Có Trong Code (Critical Bugs Cần Sửa Ngay)

Qua quá trình đọc từng dòng code, dưới đây là **10 lỗi thực tế** đang tồn tại trong thư mục `map` của bạn:

| STT | Mức độ | Vị trí File & Dòng | Mô tả Lỗi Chi tiết & Hậu quả | Cách Khắc phục |
| :---: | :---: | :--- | :--- | :--- |
| **1** | **Nguy hiểm (Crash)** | `service/MapService.java` (Dòng 291–293) | **Đệ quy vô tận (`StackOverflowError`)**: Hàm `public List<Map> getAllMaps() { return this.getAllMaps(); }` tự gọi lại chính nó mà không trả về `Manager.MAPS`. Bất kỳ chỗ nào gọi hàm này sẽ làm sập luồng ngay lập tức. | Sửa thành `return Manager.MAPS;`. |
| **2** | **Nguy hiểm (Treo CPU 100%)** | `service/MapService.java` (Dòng 301–306) | **Vòng lặp vô tận (`Infinite Loop`)**: Trong `getZone(int mapId)`, vòng lặp `while (map.zones.get(z).getNumOfPlayers() >= map.zones.get(z).maxPlayer)` bốc ngẫu nhiên `z` cho đến khi tìm được khu trống. Nếu **tất cả các khu của map đó đều đã đầy**, vòng lặp này chạy vĩnh viễn không bao giờ thoát, làm treo cứng luồng xử lý! | Duyệt tuần tự `for (Zone z : map.zones)` hoặc giới hạn số lần thử tối đa; nếu tất cả đều đầy thì trả về khu cuối hoặc `null`. |
| **3** | **Lỗi Logic (Dead Code)** | `Zone.java` (Dòng 334, 380–384, 403) | **Biến `picked` kiểm tra trước khi gán**: Trong `pickItem()`, biến `boolean picked = false;` được khai báo ở dòng 334, nhưng ở dòng 380 lại kiểm tra `if (picked) { itemMap.isPickedUp = true; }` (lúc này `picked` luôn là `false`), rồi tới dòng 403 mới gán `picked = true;`. Hậu quả là cờ `itemMap.isPickedUp` **không bao giờ được bật lên `true`**! | Đặt `itemMap.isPickedUp = true;` ngay bên trong khối `if (InventoryService.gI().addItemBag(player, item))` trước khi gửi thông báo. |
| **4** | **Lỗi Logic (Fall-through)** | `service/ChangeMapService.java` (Dòng 66–89) | **Thiếu `break;` trong `switch-case`**: Tại hàm `openChangeMapTab()`, nhánh `case ConstMap.CHANGE_CAPSULE:` **quên lệnh `break;`** ở dòng 80. Khi người chơi mở Capsule bay, code sẽ chạy tràn (fall-through) xuống `case ConstMap.CHANGE_BLACK_BALL:` bên dưới và ghi nối thêm dữ liệu map Ngọc Rồng Đen vào gói tin `-91`! | Thêm `break;` ngay sau vòng lặp `for` của `case ConstMap.CHANGE_CAPSULE:` (dòng 80). |
| **5** | **Lỗi Logic (Fall-through)** | `service/ChangeMapService.java` (Dòng 978–991) | **Thiếu `break;` ở `case 80:`**: Trong `checkMapCanJoin()`, nhánh `case 80:` (Núi khỉ vàng - yêu cầu `TASK_20_0`) thiếu `break;`, khiến code rơi thẳng xuống `case 102, 92..96` (yêu cầu `TASK_21_0`). Người chơi đang làm nhiệm vụ 20 sẽ bị chặn không vào được Núi khỉ vàng! | Thêm `break;` ở dòng 982 sau khối `if` của `case 80:`. |
| **6** | **Lỗi Tràn số (`Integer Overflow`)** | `phoban/RedRibbonHQ.java` (Dòng 331) | **Sai lệch biểu thức điều kiện và biểu thức gán**: Dòng 331 viết `(int) (totalDame/3 * mob.tempId < 2_147_483_647 ? totalDame * mob.tempId : 2_147_483_647)`. Điều kiện kiểm tra có chia 3 (`totalDame/3`), nhưng khi lấy giá trị lại **không chia 3** (`totalDame * mob.tempId`). Khi ép kiểu sang `(int)`, nếu tích vượt quá 2,14 tỷ nhưng bản chia 3 vẫn nhỏ hơn 2,14 tỷ, máu quái sẽ bị tràn số thành **số âm**! | Dùng `Math.min(totalDame * mob.tempId, 2_147_483_647L)` đồng nhất như ở hàm `init()`. |
| **7** | **Sai lệch Nhịp Game (Tick Rate)** | `Map.java` (Dòng 185–195) | **Comment 1 giây nhưng code chạy 5 giây**: `scheduler.scheduleAtFixedRate(..., 0, 5000, TimeUnit.MILLISECONDS); // Cập nhật mỗi 1 giây`. Chu kỳ 5.000ms (5 giây) làm cho mọi hoạt động trong `zone.update()` (quái tấn công, hồi sinh, vệ tinh buff, xóa vật phẩm rơi) bị trễ tới 5 giây một nhịp. | Giảm chu kỳ xuống `1000` (1 giây) hoặc `500` ms tùy theo thiết kế tick rate của server. |
| **8** | **Nghẽn Disk I/O Nghiêm trọng** | `Zone.java` (Dòng 709 & 717) | **Đọc ổ cứng mỗi lần người chơi chuyển map**: Trong `Zone.mapInfo(Player pl)`, mỗi khi có bất kỳ ai bước vào khu, server lại gọi `FileIO.readFile("data/map/item_bg_map_data/" + mapId)` và `FileIO.readFile("data/map/eff_map/" + mapId)`. Nếu 100 người chơi chuyển map cùng lúc sẽ phát sinh 200 lần đọc đĩa cứng đồng thời. | Đọc và lưu cache 2 mảng `byte[] bgItemData` và `byte[] effMapData` ngay trong class `Map` lúc khởi động server. |
| **9** | **Nghẽn DB trong hàm `synchronized`** | `phoban/BlackBallWar.java` (Dòng 106–129) | **Truy vấn SQL đồng bộ khi trao thưởng bang**: Hàm `private synchronized void win(Player player)` duyệt qua toàn bộ thành viên bang, gọi `MrBlue.loadById(m.id)` và `PlayerDAO.updatePlayer(pFromDb)` cho từng người offline ngay bên trong khối `synchronized`, gây khóa luồng sự kiện và tốn tài nguyên DB. | Đẩy việc cập nhật thưởng cho thành viên offline sang hàng đợi bất đồng bộ (Async Queue / Batch Update). |
| **10** | **Bất nhất Logic kiểm tra Map** | `service/MapService.java` (Dòng 477 vs 487, 583 vs 587) | - `isMapCold(Map map)` có kiểm tra `mapId == 152`, nhưng `isMapCold(int mapId)` lại thiếu `mapId == 152`.<br>- Hai hàm `isMapNappa(int mapId)` và `isMapStar(int mapId)` có nội dung giống hệt nhau (`mapId >= 63 && mapId <= 83`). | Gộp `isMapCold(Map map)` gọi thẳng `isMapCold(map.mapId)` (bổ sung map `152`), và chuẩn hóa lại các hằng số nhóm map. |

---

### 4. Phân Tích Kiến Trúc, Đa Luồng (Concurrency) & Clean Code

#### a) Vấn đề Quản lý Luồng & Đồng bộ hóa (Thread Safety)
* **Quá nhiều `ScheduledThreadPool(1)` ở `Map.java`**: Mỗi đối tượng `Map` tạo riêng một `Executors.newScheduledThreadPool(1)` (dòng 58). Với ~180 bản đồ trong game, server phải duy trì **~180 Platform Threads** chỉ để chạy lịch `Map.run()`, dù phần lớn các map không có người chơi. Nên gom chung vào một Thread Pool toàn cục hoặc dùng Virtual Thread / Ticker tập trung.
* **Lạm dụng `CopyOnWriteArrayList` không nhất quán trong `Zone.java`**:
  * `Zone` khởi tạo 9 danh sách bằng `CopyOnWriteArrayList`. Cấu trúc này tạo bản sao mảng mới mỗi khi `add`/`remove`, gây áp lực bộ nhớ (GC) nếu người chơi/vật phẩm ra vào liên tục.
  * Đặc biệt, tại `Zone.getMaBuHold()` (dòng 767) và `Zone.setMaBuHold()` (dòng 792), code lại ghi đè `z128.maBuHolds = new ArrayList<>();`, làm mất hoàn toàn tính chất `CopyOnWriteArrayList` đã khởi tạo ở constructor và gây nguy cơ `ConcurrentModificationException` khi `MajinBuu14H.update()` duyệt qua `zone.maBuHolds` trên luồng khác!
* **Singleton không an toàn luồng (Non-Thread-Safe Lazy Initialization)**: Cả 4 class `ChangeMapService`, `MapService`, `ItemMapService`, `NpcService` đều dùng mẫu `if (instance == null) instance = new ...` không có `synchronized` hoặc `volatile` (hoặc `static final` holder), dễ tạo ra 2 instance nếu được gọi đồng thời lần đầu từ nhiều luồng.

#### b) Vi phạm Nguyên lý Thiết kế (DRY & SOLID)
* **Trùng lặp mã nguồn cực lớn (Vi phạm DRY)**:
  * 4 class phó bản bang hội (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`) lặp lại 80% cấu trúc: vòng lặp `run()`, cờ `isOpened`, `kickout`, đếm ngược 60 giây, gửi/xóa `ItemTime`, `getMapById()`, `finish()`, `dispose()`.
  * Trong `MapService.getMapCanJoin()` (dòng 127–281), 4 khối `if` kiểm tra quái/boss chết cho 4 phó bản (`DoanhTrai`, `BanDoKhoBau`, `KhiGasHuyDiet`, `ConDuongRanDoc`) được copy-paste y hệt nhau (155 dòng code lặp lại).
* **God Method & Hardcode (Magic Numbers)**:
  * Hàm `ChangeMapService.changeMap()` và `checkMapCanJoin()` quá dài, ôm đồm cả logic nhiệm vụ, logic sự kiện Ma Bư, logic Ngọc Rồng Namếc, logic hành tinh Cold/Tương lai.
  * Hàng trăm con số định danh (`mapId`, `itemId`, `effectId`, mã gói tin `-24`, `-5`, `-124`...) bị viết cứng (hardcode) trực tiếp trong các câu lệnh `if/switch` thay vì khai báo trong `ConstMap`, `ConstItem` hay `Cmd`.
* **Thói quen nuốt ngoại lệ (`Empty Catch Blocks`)**:
  * Có hơn 25 khối `catch (Exception e) {}` để trống hoàn toàn trong `Map.java`, `Zone.java`, `ChangeMapService.java`, `RedRibbonHQ.java`, `SnakeWay.java`, `DestronGas.java`, `BanDoKhoBau.java`. Khi có lỗi `NullPointerException` hoặc `IndexOutOfBoundsException` xảy ra trong quá trình khởi tạo Boss hoặc gửi hiệu ứng, lỗi bị nuốt im lặng khiến việc tìm nguyên nhân rất vất vả.

---

### 5. Đề Xuất Lộ Trình Refactor & Tối Ưu Hóa

1. **Ưu tiên 1 (Sửa ngay các Bug nguy hiểm)**:
   * Sửa hàm `MapService.getAllMaps()`, thoát vòng lặp `while` trong `MapService.getZone()`, thêm `break;` ở 2 vị trí fall-through trong `ChangeMapService`, sửa thứ tự biến `picked` trong `Zone.pickItem()` và sửa lỗi tràn số ở `RedRibbonHQ.updateHPDame()`.
2. **Ưu tiên 2 (Tối ưu Hiệu năng & I/O)**:
   * Chuyển việc đọc file `data/map/item_bg_map_data/` và `data/map/eff_map/` từ `Zone.mapInfo()` sang hàm khởi tạo của `Map.java` (chỉ đọc 1 lần duy nhất khi bật server và lưu vào biến `byte[]` của `Map`).
   * Thay thế 180 `ScheduledExecutorService` riêng lẻ ở từng `Map` bằng một `ScheduledExecutorService` dùng chung hoặc cơ chế chỉ kích hoạt `update()` cho những `Zone` đang có người chơi (`!zone.getPlayers().isEmpty()`) hoặc đang là phó bản mở.
3. **Ưu tiên 3 (Tái cấu trúc Hướng đối tượng - OOP)**:
   * Tạo một lớp trừu tượng `AbstractClanDungeon` cho 4 phó bản Bang hội (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`) để gom chung vòng lặp `run()`, quản lý danh sách `zones`, `bosses`, đếm ngược đóng cửa, dọn dẹp `dispose()` và kiểm tra điều kiện qua cửa trong `MapService.getMapCanJoin()`.
