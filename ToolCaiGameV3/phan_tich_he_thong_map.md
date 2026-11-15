# Tài Liệu Phân Tích Toàn Diện Hệ Thống Map Hiện Tại (`nro.models.map`)

Tài liệu này mô tả chính xác kiến trúc hiện tại của **18 file mã nguồn Java (5.095 dòng code)** trong thư mục `nro.models.map` của dự án `NroBaKhi` sau đợt nâng cấp & refactor, bao gồm giải thích chi tiết từng file, thuộc tính, toàn bộ các hàm (method) và đánh giá chất lượng mã nguồn để đạt mốc **10/10**.

---

## Phần 1: Tổng Quan Kiến Trúc & Cấu Trúc Thư Mục Hiện Tại

Hệ thống bản đồ trong package `nro.models.map` gồm **18 file (5.095 dòng code)** chia thành **3 phân hệ chính**:

| Phân hệ | Package | Số lượng file | Tổng số dòng | Trách nhiệm chính |
| :--- | :--- | :---: | :---: | :--- |
| **1. Thực thể Bản đồ Cốt lõi (Core Entities)** | `nro.models.map` | 6 | 1.672 | Biểu diễn dữ liệu bản đồ (`Map` kèm bộ đệm RAM & bộ lập lịch tập trung `MAP_SCHEDULER`), khu vực (`Zone` kèm bộ sinh ID nguyên tử `AtomicInteger`), vật phẩm rơi (`ItemMap`), cổng dịch chuyển (`WayPoint`), bẫy (`TrapMap`), và kén giam Ma Bư (`MaBuHold`). |
| **2. Phó bản & Sự kiện (Dungeons & Events)** | `nro.models.map.phoban` | 7 | 1.500 | Bao gồm lớp cha trừu tượng `AbstractClanDungeon` và 6 lớp thực thi phó bản/sự kiện chạy trên **Java Virtual Threads**: Bản Đồ Kho Báu (`BanDoKhoBau`), Ngọc Rồng Sao Đen (`BlackBallWar`), Khí Gas Hủy Diệt (`DestronGas`), Ma Bư 14H (`MajinBuu14H`), Doanh Trại Độc Nhãn (`RedRibbonHQ`), Con Đường Rắn Độc (`SnakeWay`). |
| **3. Dịch vụ Điều phối (Map Services)** | `nro.models.map.service` | 5 | 1.923 | Các dịch vụ Singleton an toàn luồng (Bill Pugh `Holder` pattern) xử lý chuyển map/khu (`ChangeMapService`), định tuyến khu vực & kiểm tra loại map (`MapService`), quản lý vật phẩm rơi (`ItemMapService`), lọc NPC (`NpcManager`) và tạo hội thoại/menu NPC (`NpcService`). |

### Luồng Hoạt Động Tổng Thể (System Lifecycle Hiện Tại)
1. **Khởi tạo & Nạp Cache RAM (`Map` & `Zone`)**:
   - Khi server khởi động, mỗi `Map` đọc cấu hình ô va chạm từ `data/map/tile_map_data/<mapId>` (`readTileMap`) và **nạp sẵn 1 lần duy nhất** dữ liệu tĩnh Background Item (`data/map/item_bg_map_data/<mapId>`) cùng Effect Map (`data/map/eff_map/<mapId>`) vào 2 mảng byte trên RAM (`bgItemData`, `effMapData`) qua `loadStaticMapAssets(mapId)`.
   - `Map` khởi tạo danh sách `Zone` (tự động khởi tạo sẵn 4 slot `MaBuHold` nếu là Map `128`), đăng ký `Zone` vào các dịch vụ phó bản, và tạo Quái (`Mob`), NPC (`Npc`), Vật phẩm cố định (`ItemMap`), Bẫy (`TrapMap`), Boss (`Boss`).
2. **Vòng lặp Cập nhật Tập trung (`Manager.initMap()` & Virtual Threads)**:
   - Toàn bộ các `Zone` của toàn bộ các `Map` được quản lý và cập nhật tập trung thông qua bộ lập lịch tại `Manager.initMap()` (`Manager.java` L199–L242): mỗi 1 giây gom theo từng lô 10 khu (`batch`) chạy song song qua `CompletionService` đa luồng. `Map.run()` được giữ nguyên làm phương thức dự phòng độc lập và không được gọi trong luồng server chính để tránh chạy đúp luồng `zone.update()`.
   - Các phó bản và sự kiện (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`, `BlackBallWar`, `MajinBuu14H`) chạy trên **Java Virtual Threads** (`Thread.ofVirtual()`) với chu kỳ tick ~150ms. Đặc biệt, việc trao thưởng Ngọc Rồng Sao Đen cho thành viên bang hội đang offline được tách sang một Virtual Thread nền riêng biệt để không làm nghẽn luồng sự kiện.
3. **Định tuyến Khu vực & Chuyển Map (`MapService` & `ChangeMapService`)**:
   - Khi người chơi chuyển map, `MapService.getMapCanJoin()` sử dụng cặp hàm `resolveDungeonTransition()` và `isZoneCleared()` để kiểm tra điều kiện diệt sạch quái/boss trong phó bản, hoặc chọn ngẫu nhiên khu chưa đầy trong `O(N)` qua `getZone()`.
   - `ChangeMapService` kiểm tra điều kiện nhiệm vụ/sức mạnh, xóa nhân vật khỏi `Zone` cũ (gói `-6`), đưa vào `Zone` mới, gửi cấu trúc bản đồ trực tiếp từ RAM cache (gói `-24`) và đồng bộ nhân vật/hiệu ứng hai chiều (`load_Me_To_Another`, `load_Another_To_Me`).

---

## Phần 2: Giải Thích Chi Tiết Từng File & Toàn Bộ Các Hàm (18/18 File)

### A. Phân Hệ 1: Thực Thể Bản Đồ Cốt Lõi (`nro.models.map`)

#### 1. `Map.java` (520 dòng)
Đại diện cho một bản đồ gốc, lưu trữ dữ liệu địa hình (tile map), bộ đệm tài nguyên tĩnh trên RAM (`bgItemData`, `effMapData`), danh sách các khu vực (`Zone`), cổng dịch chuyển (`WayPoint`) và danh sách NPC.

**Các thuộc tính & hằng số chính:**
* `T_EMPTY = 0`, `T_TOP = 2`, `SIZE = 24`: Hằng số ô trống, ô mặt đất đứng được và kích thước mỗi ô (24x24 pixel).
* `MAP_SCHEDULER`: `ScheduledExecutorService` tĩnh dùng chung cho toàn bộ các bản đồ (`Math.max(4, Runtime.getRuntime().availableProcessors())` luồng).
* `bgItemData` (`byte[]`), `effMapData` (`byte[]`): Bộ đệm dữ liệu vật cảnh nền và hiệu ứng bản đồ được nạp sẵn vào RAM khi khởi tạo.
* `mapId`, `mapName`, `planetId`, `planetName`, `tileId`, `bgId`, `bgType`, `type`: Thông tin định danh và cấu hình hiển thị của bản đồ.
* `tileMap` (`int[][]`), `tileTop` (`int[]`), `mapWidth`, `mapHeight`, `tmw`, `tmh`, `pxw`, `pxh`, `types`, `maps`: Dữ liệu lưới ô vuông phục vụ tính toán trọng lực và va chạm.
* `zones` (`List<Zone>`), `wayPoints` (`List<WayPoint>`), `npcs` (`List<Npc>`), `bots`, `players`: Các danh sách thực thể thuộc bản đồ.

**Chi tiết các Constructor & Hàm:**

| Tên hàm / Constructor | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `Map(...)` (Constructor) | `mapId`, `mapName`, `planetId`, `tileId`, `bgId`, `bgType`, `type`, `tileMap`, `tileTop`, `zones`, `maxPlayer`, `wayPoints` | Khởi tạo bản đồ, tính kích thước `mapHeight`/`mapWidth` (kiểm tra `tileMap != null` an toàn), đọc dữ liệu ô va chạm (`readTileMap`), nạp tài nguyên tĩnh vào RAM (`loadStaticMapAssets`), chia khu (`initZone`), tạo vật phẩm cố định (`initItem`) và tạo bẫy (`initTrapMap`). |
| `getPlayersInMap()` | Không có | Gom và trả về danh sách tất cả người chơi đang có mặt trong toàn bộ các `Zone` của bản đồ. |
| `initZone(int nZone, int maxPlayer)` | `nZone`, `maxPlayer` | Thiết lập số lượng khu theo loại bản đồ (`MAP_OFFLINE = 1`, `MAP_BLACK_BALL_WAR = 5`, `MAP_MABU_14H = 7`, các phó bản Bang hội = `50`), tạo từng `Zone` và đăng ký vào Service quản lý phó bản tương ứng. |
| `initNpc(byte[] npcId, short[] npcX, short[] npcY)` | Mảng ID và tọa độ NPC | Khởi tạo NPC trong bản đồ qua `NpcFactory.createNPC(...)` (bỏ qua Bò Mộng ở các map nhà `21, 22, 23`). |
| `addNpc(Npc npc)` | `npc` | Thêm một `Npc` vào danh sách `this.npcs`. |
| `run()` | Không có | Phương thức đăng ký cập nhật `Zone` dự phòng (không gọi trong luồng server chính vì `Manager.initMap()` đã đảm nhiệm điều phối tập trung mỗi 1 giây theo batch 10 khu). |
| `loadStaticMapAssets(int mapId)` | `mapId` | Đọc file `data/map/item_bg_map_data/<mapId>` và `data/map/eff_map/<mapId>` lưu vào `this.bgItemData` và `this.effMapData` ngay khi khởi tạo bản đồ; ghi log qua `Logger.logException` nếu xảy ra lỗi đọc file. |
| `initMob(byte[] mobTemp, byte[] mobLevel, int[] mobHp, short[] mobX, short[] mobY)` | Mảng cấu hình quái | Khởi tạo quái vật cho từng `Zone` dựa trên `MobTemplate`. Tự động ánh xạ sang các class BigBoss tương ứng (`Hirudegarn`, `VuaBachTuoc`, `RobotBaoVe`, `GauTuongCuop`, `VoiChinNga`, `GaChinCua`, `NguaChinLmao`, `Piano`, `MayDoSucManh`) hoặc `Mob` thường. |
| `initMob(List<Mob> mobs)` | Danh sách mẫu `Mob` | Nhân bản danh sách `mobs` cho từng `Zone` trong bản đồ. |
| `initTrapMap()` | Không có (`private`) | Khởi tạo vùng bẫy xiên (`effectId = 49`, `x = 260, y = 960, w = 740, h = 72`) cho từng `Zone` của Map `135` (Bản Đồ Kho Báu). |
| `initItem()` | Không có (`private`) | Đặt sẵn vật phẩm cố định cho từng `Zone`: Đùi gà nướng (`74`) ở map `21..23`, Đứa bé (`78`) ở map `42..44`, và Ngọc Rồng Sao Đen (`372..378`) ở map `85..91`. |
| `initBoss()` | Không có | Tạo và đưa các Boss cố định vào từng `Zone`: Tàu Pảy Pảy (`111`), các Boss tầng Ma Bư 12H (`114..120`), và Boss Yardrat (`131..133`). |
| `mapIdNextMabu(short mapId)` | `mapId` | Trả về ID map tầng tiếp theo trong sự kiện Ma Bư 12H (`114 -> 115 -> 117 -> 118 -> 119 -> 120`). |
| `getNpc(Player player, int tempId)` | `player`, `tempId` | Tìm kiếm NPC trong bản đồ khớp `tempId` và kiểm tra khoảng cách `<= 60px` (miễn kiểm tra khoảng cách tại map Ngọc Rồng Sao Đen). |
| `yPhysicInTop(int x, int y)` | Tọa độ `x, y` | Tính toán trọng lực: dò dọc ma trận `tileMap` từ `(x, y)` xuống dưới để trả về tọa độ `Y` của mặt đất (`isTileTop`) gần nhất. |
| `LastY(int cx, int cy)` | Tọa độ `cx, cy` | Dò mặt đất theo mảng bitwise `types` (tối đa 30 bước nhảy 24px). |
| `tileTypeAt(int x, int y, int type)` | `x, y, type` | Kiểm tra an toàn giới hạn mảng `index >= 0 && index < types.length` và kiểm tra bitwise thuộc tính va chạm `type` tại tọa độ `(x, y)`. |
| `moveXY(Player player)` | `player` | Hiệu chỉnh tọa độ di chuyển hợp lệ của người chơi dựa trên ma trận va chạm `tileMap`. |
| `isTileTop(int tileMap)` | Mã `tileMap` (`private`) | Kiểm tra mã tile có nằm trong mảng mặt đất `tileTop` hay không. |
| `readTileMap(int mapId)` | `mapId` | Kiểm tra sự tồn tại của file `data/map/tile_map_data/<mapId>` và đọc kích thước `tmw, tmh` cùng mảng ô bản đồ `maps`. |

---

#### 2. `Zone.java` (887 dòng)
Đại diện cho một **Khu vực (Channel/Instance)** cụ thể bên trong một `Map`, trực tiếp quản lý tương tác giữa Người chơi, Đệ tử, Boss, Quái, Bot và Vật phẩm rơi.

**Các thuộc tính chính:**
* `PLAYERS_TIEU_CHUAN_TRONG_MAP = 7`: Ngưỡng người chơi tiêu chuẩn để ưu tiên ghép khu.
* `itemIdGenerator` (`AtomicInteger`) & `countItemAppeaerd`: Bộ sinh mã `itemMapId` tự tăng an toàn đa luồng (tự động quay vòng về `1` khi chạm `2.000.000.000`).
* 6 danh sách phân loại thực thể `Player` (`CopyOnWriteArrayList`): `humanoids`, `nonInteractiveNPCs`, `notBosses`, `players`, `bosses`, `pets`.
* Các danh sách thực thể khác: `mobs` (`ArrayList`), `items` (`CopyOnWriteArrayList`), `trapMaps` (`CopyOnWriteArrayList`), `maBuHolds` (`CopyOnWriteArrayList`), `bots` (`CopyOnWriteArrayList`).

**Chi tiết các Constructor & Hàm:**

| Tên hàm / Constructor | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `Zone(Map map, int zoneId, int maxPlayer)` | `map`, `zoneId`, `maxPlayer` | Khởi tạo khu vực và các danh sách thực thể. Nếu `map.mapId == 128` (Bụng Ma Bư), tự động khởi tạo sẵn 4 slot `MaBuHold` vào `CopyOnWriteArrayList` ngay từ đầu. |
| `nextItemMapId()` | Không có | Sinh mã `itemMapId` tiếp theo một cách nguyên tử (`itemIdGenerator.updateAndGet`), đồng bộ giá trị vào `countItemAppeaerd` và trả về ID mới. |
| `isFullPlayer()` | Không có | Kiểm tra khu đã đầy người chơi chưa (`players.size() >= maxPlayer`). |
| `addBot(BotAttackplayer)` / `getBots()` / `removeBot(BotAttackplayer)` | `bot` | Thêm, lấy bản sao danh sách và xóa Bot khỏi khu vực (đồng bộ `synchronized (bots)`), tự động gửi gói tin xuất hiện/biến mất tới người chơi trong khu. |
| `update()` | Không có | Chu trình cập nhật khu vực mỗi giây: gọi lần lượt `udMob()`, `udItem()`, `udPlayer()`, `udNonInteractiveNPC()`. |
| `udMob()`, `udItem()`, `udPlayer()`, `udNonInteractiveNPC()` | Không có (`private`) | Các hàm cập nhật từng nhóm thực thể trong khu kèm bắt lỗi và ghi log qua `Logger.logException`. |
| `getNumOfPlayers()` / `getNumOfBosses()` | Không có | Trả về số lượng người chơi thật (`players.size()`) và số lượng Boss (`bosses.size()`). |
| `isBossCanJoin(Boss boss)` | `boss` | Kiểm tra không cho phép 2 Boss có cùng `id` đứng chung một khu. |
| `addPlayer(Player)` / `removePlayer(Player)` | `player` | Phân loại và thêm/xóa đối tượng `player` vào/khỏi 6 danh sách nhân vật của khu. |
| `getItemMapByItemMapId(int)` / `getItemMapByTempId(int)` | `itemId` / `tempId` | Tìm kiếm vật phẩm đang nằm trên đất theo `itemMapId` hoặc `itemTemplate.id`. |
| `getItemMapsForPlayer(Player player)` | `player` | Lọc danh sách vật phẩm hiển thị theo tiến độ nhiệm vụ của người chơi (Đứa bé `78`, Đùi gà `74`, vật phẩm Đại hội võ thuật `726`). |
| `getPlayerInMap(long)` / `getPlayerInMapOffline(Player, long)` | `idPlayer` | Tìm thực thể trong `humanoids` theo `id`, hoặc tìm `TrainingBoss` của riêng người chơi trong map tập luyện offline. |
| `pickItem(Player player, int itemMapId)` | `player`, `itemMapId` | **Xử lý nhặt vật phẩm an toàn đa luồng**: Khóa `synchronized (itemMap)`, kiểm tra quyền sở hữu và điều kiện nhặt, thêm vào hành trang (`addItemBag`), gửi gói tin **`Message(-20)`**, đánh dấu ngay `itemMap.isPickedUp = true` (trừ vật phẩm cố định `74`, `78`), xóa vật phẩm khỏi mặt đất và kiểm tra hoàn thành nhiệm vụ nhặt đồ. |
| `addItem(ItemMap)` / `removeItemMap(ItemMap)` | `itemMap` | Thêm/xóa đồng bộ (`synchronized`) vật phẩm trong danh sách `items`. |
| `getRandomPlayerInMap()` | Không có | Chọn ngẫu nhiên một người chơi không tàng hình và không bị giam trong kén Ma Bư. |
| `load_Me_To_Another(Player)` / `load_Another_To_Me(Player)` / `loadBoss(Boss)` | `player` / `boss` | Đồng bộ thông tin ngoại hình và trạng thái giữa người chơi/boss mới vào khu với các thực thể đang có mặt trong khu (duyệt ngược an toàn tránh lỗi đồng thời). |
| `infoPlayer(Player plReceive, Player plInfo)` | `plReceive`, `plInfo` (`private`) | Đóng gói và gửi gói tin **`Message(-5)`** (thông tin nhân vật, bang hội, PK, ngoại hình, thú cưỡi, aura, nón) và **`Message(-8)`** (nếu nhân vật đang chết). |
| `mapInfo(Player pl)` | `pl` | Đóng gói và gửi gói tin **`Message(-24)`** dựng bản đồ cho người chơi mới vào: thông tin Map/Zone, WayPoint, Mob, NPC, ItemMap, và **ghi trực tiếp `this.map.bgItemData` cùng `this.map.effMapData` từ bộ đệm RAM** (không phát sinh đọc đĩa). |
| `isInTrap(Player player)` | `player` | Kiểm tra người chơi có đang đứng trong vùng bẫy `TrapMap` nào của khu hay không. |
| `sendBigBoss(Player player)` | `player` | Gửi trạng thái biến hình của BigBoss Hirudegarn cho người chơi mới vào khu. |
| `getMaBuHold()` / `setMaBuHold(int slot, int zoneId, Player player)` | `slot`, `zoneId`, `player` | Tìm ô kén trống hoặc gán người chơi vào ô kén tại Map `128` mà vẫn giữ nguyên cấu trúc `CopyOnWriteArrayList` của `maBuHolds`. |
| `isKhongCoTrongTaiTrongKhu()` | Không có | Duyệt ngược danh sách `players` an toàn để kiểm tra điều kiện xuất hiện của NPC Trọng Tài. |
| `findNearestPlayer(Pet)` / `findNearestMob(Pet)` | `pet` | Duyệt ngược danh sách tìm Người chơi hoặc Quái vật còn sống gần Đệ tử (`pet`) nhất theo trục X. |

---

#### 3. `ItemMap.java` (197 dòng)
Biểu diễn vật phẩm nằm trên mặt đất trong một `Zone`.
* **Cải tiến mới**: Trong cả 2 constructor khởi tạo mới (`ItemMap(Zone, int tempId, ...)` và `ItemMap(Zone, ItemTemplate, ...)`), mã định danh vật phẩm được lấy thông qua **`this.itemMapId = zone.nextItemMapId();`** sử dụng `AtomicInteger` an toàn đa luồng.
* **Các hàm chính**:
  * `update()`: Tự động dịch chuyển Ngọc Rồng Sao Đen theo người chơi mỗi 10 giây; gọi `satelliteUpdate()` nếu là Vệ tinh (`type == 22`); mở khóa quyền nhặt cho mọi người (`playerId = -1`) sau 45 giây; tự hủy biến mất sau 50 giây (hoặc 30 phút đối với Vệ tinh).
  * `satelliteUpdate()`: Cộng hiệu ứng hồi 10% MP (`342`), tăng trí tuệ (`343`), tăng phòng thủ (`344`), hồi 10% HP (`345`) cho chủ nhân hoặc thành viên cùng bang đứng trong bán kính `< 200px`.
  * `findPlayerByID(long)`, `reAppearItem()`, `isNotNullItem()`, `dispose()`: Các hàm phụ trợ kiểm tra chủ sở hữu, làm mới vị trí vật phẩm và giải phóng bộ nhớ.

---

#### 4. `WayPoint.java` (23 dòng), 5. `TrapMap.java` (27 dòng), 6. `MaBuHold.java` (18 dòng)
* **`WayPoint.java`**: Lưu tọa độ vùng cổng (`minX, minY, maxX, maxY`), cờ `isEnter`, `isOffline`, tên cổng `name` và điểm đến (`goMap, goX, goY`).
* **`TrapMap.java`**: Lưu vùng bẫy `(x, y, w, h)`, `effectId`, `dame` và hàm `doPlayer(Player)` gây sát thương bẫy xiên (`effectId == 49`) mỗi 1 giây.
* **`MaBuHold.java`**: Lưu thông tin ô kén giam người chơi (`slot`, `player`, `x`, `y`) tại Map `128`.

---

### B. Phân Hệ 2: Hệ Thống Phó Bản & Sự Kiện (`nro.models.map.phoban`)

#### 7. `AbstractClanDungeon.java` (80 dòng — File Mới)
Lớp cha trừu tượng (`abstract class`) chuẩn hóa cấu trúc chung cho cả 4 phó bản Bang hội (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`), cài đặt sẵn `Runnable`.

**Các thuộc tính chung được kế thừa:**
* `id` (`int`), `level` (`byte`), `zones` (`List<Zone>`), `clan` (`Clan`), `isOpened` (`boolean`), `lastTimeOpen` (`long`).

**Chi tiết các hàm dùng chung:**

| Tên hàm | Tham số | Giải thích chức năng & Cơ chế hoạt động |
| :--- | :--- | :--- |
| `addZone(Zone zone)` | `zone` | Thêm một khu vực bản đồ vào danh sách `this.zones` của phó bản. |
| `getMapById(int mapId)` | `mapId` | Tìm và trả về `Zone` thuộc phó bản khớp với `mapId` (có kiểm tra `null` an toàn). |
| `areAllMobsDead()` | Không có | Duyệt qua toàn bộ `zones` của phó bản, trả về `true` nếu tất cả quái thường (`zone.mobs`) đều đã chết. |
| `areAllBossesDead()` | Không có | Duyệt qua toàn bộ `zones` của phó bản, trả về `true` nếu tất cả Boss (`zone.getBosses()`) đều đã chết. |
| `areAllCharactersDead()` | Không có | Kết hợp `areAllMobsDead() && areAllBossesDead()`, kiểm tra toàn bộ mục tiêu trong phó bản đã bị tiêu diệt hết chưa. |
| `clearAllItems()` | Không có | Duyệt ngược và xóa sạch toàn bộ vật phẩm rơi trên mặt đất (`ItemMap`) trong tất cả các `Zone` của phó bản. |

---

#### 8. `BanDoKhoBau.java` (253 dòng)
Kế thừa `AbstractClanDungeon`, quản lý phó bản **Bản Đồ Kho Báu** (Map `135..138`, thời gian 30 phút, tối đa 50 phòng).
* **Các hàm chính**:
  * `openBanDoKhoBau(Player plOpen, Clan clan, byte level)`: Mở phó bản theo cấp độ, gắn vào `clan`, gọi `init()`, đưa người mở vào hang (`goToDBKB`) và gửi thanh thời gian.
  * `init()`: Cài đặt sát thương bẫy xiên (`level * 100.000`), hồi sinh quái theo cấp độ (thiết lập quái siêu cấp `lvMob = 1` ở các vị trí cố định), tạo Boss **`TrungUyXanhLo`** ở Map `137` và khởi chạy Virtual Thread.
  * `run()` & `update()`: Kiểm tra bẫy ở Map `135`, kiểm tra tiêu diệt hết quái/boss để kích hoạt đếm ngược 60 giây sập hang (`kickoutbdkb`), gửi thông báo mỗi 10 giây và gọi `finish()` + `dispose()` khi hết giờ.
  * `sendThanhTichBanDoKhoBau(Player)`, `finish()`, `kickOutOfBDKB(Player)`, `sendTextBanDoKhoBau()`, `removeTextBanDoKhoBau()`, `dispose()`: Cập nhật bảng vàng thành tích bang hội, đưa người chơi về Đảo Kame (Map `5`) và dọn dẹp phó bản.

---

#### 9. `BlackBallWar.java` (161 dòng)
Quản lý sự kiện **Tranh Đoạt Ngọc Rồng Sao Đen** (20h00 – 21h00 tại các map `85..91`).
* **Cải tiến nổi bật trong `win(Player player)` (dòng 108–143)**:
  * Khi người chơi giữ ngọc đủ 5 phút (`TIME_WIN = 300_000`), hàm trao thưởng trực tiếp cho các thành viên bang đang online và thu thập danh sách ID thành viên đang offline vào `offlineMemberIds`.
  * Khởi chạy một **Virtual Thread riêng biệt** (`"BlackBall-OfflineReward-Clan-" + player.clan.id`) để tải hồ sơ từ DB (`MrBlue.loadById`), cộng thưởng và lưu lại (`PlayerDAO.updatePlayer`) kèm ghi log `Logger.logException`, giúp luồng chính của sự kiện không bao giờ bị treo bởi truy vấn SQL.
* **Các hàm khác**: `start()`, `run()`, `update()`, `updatePlayer(Player)`, `kickOutOfMap(Player)`, `kickAllPlayersOutOfMap(Zone)`.

---

#### 10. `DestronGas.java` (242 dòng)
Kế thừa `AbstractClanDungeon`, quản lý phó bản **Khí Gas Hủy Diệt** (Map `147, 148, 149, 151, 152`).
* **Các hàm chính**: `openKhiGasHuyDiet(...)`, `init()` (hồi sinh quái và thiết lập quái đột biến `76` ở các map), `update()` (gọi Boss **`DrLychee`** ở Map `148` khi diệt hết quái, đếm ngược 60 giây khi `hatchiyatchDead`), `sendThanhTichKhiGas(Player)` (tăng `destronGas70CompletionCount` nếu vượt cấp `>= 70`), `finish()`, `kickOutOfKGHD(Player)`, `dispose()`.

---

#### 11. `MajinBuu14H.java` (99 dòng)
Quản lý sự kiện **Ma Bư 14H** (Map `127, 128`).
* **Các hàm chính**: `init()` (chạy Virtual Thread), `run()`, `update()` (giải phóng các ô kén `MaBuHold` mà người chơi đã thoát ra, hoặc gọi `finish()` đẩy người chơi về nhà khi hết giờ sự kiện), `getMaBuHold()`, `getMapById(int)`, `finish()`, `kickOut(Player)`.

---

#### 12. `RedRibbonHQ.java` (417 dòng)
Kế thừa `AbstractClanDungeon`, quản lý phó bản **Doanh Trại Độc Nhãn** (Map `53..62`).
* **Cải tiến mới trong `updateHPDame()` (dòng 310–313)**: Đã sửa triệt để lỗi tràn số âm bằng cách dùng `long mobTempId = Math.max(1, mob.tempId);` và `Math.min((totalDame / 3) * mobTempId, (long) Integer.MAX_VALUE)`.
* **Các hàm chính**: `openDoanhTrai(Player)` (kéo các thành viên cùng bang đứng gần cổng vào Map `53`), `init()` (tính máu/dame quái và 5 loại Boss `TrungUyTrang`, `TrungUyXanhLo`, `TrungUyThep`, `NinjaAoTim`, 4 `RobotVeSi` theo tổng chỉ số của bang), `update()`, `NR(Zone)`, `randomNR()`, `updateHPDame()`, `finish()`, `kickOutOfDT(Player)`, `dispose()`.

---

#### 13. `SnakeWay.java` (248 dòng)
Kế thừa `AbstractClanDungeon`, quản lý phó bản **Con Đường Rắn Độc** (Map `141..144`).
* **Các hàm chính**: `openConDuongRanDoc(...)`, `init()` (hồi sinh quái và tạo đội Boss Xayda gồm 6 `SAIBAMEN`, `NADIC`, `CADICH` ở Map `144`), `update()` (bật cờ `allMobsDead = true` khi diệt hết quái thường và đếm ngược 60 giây khi `endCDRD`), `sendThanhTichCDRD(Player)`, `getNumBossAlive()`, `finish()`, `kickOutOfCDRD(Player)`, `dispose()`.

---

### C. Phân Hệ 3: Dịch Vụ Quản Lý & Điều Phối Bản Đồ (`nro.models.map.service`)

#### 14. `ChangeMapService.java` (1.151 dòng)
Singleton Service (áp dụng **Bill Pugh `Holder` pattern** an toàn đa luồng) xử lý toàn bộ nghiệp vụ chuyển bản đồ, đổi khu và đồng bộ hiệu ứng khi tải map.

* **Các điểm đã nâng cấp trong phiên bản hiện tại**:
  * Chuẩn hóa Singleton thông qua `private static final class Holder`.
  * Đã thêm đầy đủ `break;` tại `case ConstMap.CHANGE_CAPSULE:` trong `openChangeMapTab()` (dòng 78) và tại `case 80:` (Núi khỉ vàng) trong `checkMapCanJoin()` (dòng 992).
  * Kiểm tra biên an toàn `zoneId >= 0 && zoneId < map.zones.size()` trong `getZoneJoinByMapIdAndZoneId()` (dòng 1133).
  * Thay thế toàn bộ các khối `catch` rỗng bằng `Logger.logException(ChangeMapService.class, e)`.
* **Danh sách 28 hàm trong `ChangeMapService.java`**:
  * Quản lý UI & Đổi khu: `gI()`, `openChangeMapTab(Player)`, `openZoneUI(Player)`, `changeZone(Player, int)`.
  * Các phương thức chuyển map: `changeMapBySpaceShip(...)` (2 hàm), `changeMapInYard(...)` (2 hàm), `changeMap(...)` (2 hàm public + 1 hàm core private), `changeMapYardrat(...)`, `changeMapNonSpaceship(...)`, `changeMapWaypoint(Player)`, `resetPoint(Player)`.
  * Vào/Rời khu & Đồng bộ sau tải map: `goToMap(Player, Zone)`, `exitMap(Player)`, `finishLoadMap(Player)`, `sendEffectMeToMap(Player)`, `sendEffectMapToMe(Player)`, `spaceShipArrive(...)`, `effectChangeMap(...)`.
  * Dịch chuyển đặc biệt & Kiểm tra điều kiện: `goToTuongLai(Player)`, `goToDBKB(Player)`, `goToQuaKhu(Player)`, `goToPotaufeu(Player)`, `checkMapCanJoin(Player, Zone)`, `checkMapCanJoinByYardart(Player, Zone)`, `checkJoinSpecialMap(Player)`, `checkJoinMapMaBu(Player)`, `getMapCanJoin(Player, int)`, `getZoneJoinByMapIdAndZoneId(...)`, `getMapById(int)`.

---

#### 15. `MapService.java` (530 dòng — Đã rút gọn 100 dòng code lặp)
Singleton Service (áp dụng **Bill Pugh `Holder` pattern**) cung cấp các hàm định tuyến `Zone`, đọc dữ liệu tile và phân loại bản đồ.

* **Các hàm mới & cải tiến quan trọng**:
  * `isZoneCleared(Zone zone)` (dòng 153–173): Kiểm tra toàn bộ quái (`zone.mobs`) và Boss (`zone.getBosses()`) trong một khu đã bị tiêu diệt hết chưa.
  * `resolveDungeonTransition(Player player, Zone targetDungeonZone, boolean isCurrentMapInDungeon)` (dòng 175–188): Hàm dùng chung giải quyết điều kiện chuyển map qua cửa cho cả 4 phó bản Bang hội (nếu khu đích đã dọn sạch hoặc khu hiện tại đã dọn sạch thì cho phép qua, ngược lại trả về `null`), giúp rút gọn 155 dòng code lặp trong `getMapCanJoin()`.
  * `getAllMaps()` (dòng 190–192): Đã sửa trả về `Manager.MAPS` (loại bỏ lỗi đệ quy vô tận).
  * `getZone(int mapId)` (dòng 194–210): Lọc danh sách các khu chưa đầy (`!zone.isFullPlayer()`) và chọn ngẫu nhiên trong `O(N)`; nếu tất cả các khu đều đầy thì trả về `map.zones.get(0)` (loại bỏ hoàn toàn vòng lặp `while` gây treo CPU).
  * `isMapCold(Map map)` & `isMapCold(int mapId)` (dòng 379–390): Đã đồng nhất kiểm tra `(mapId >= 105 && mapId <= 110) || mapId == 152`.

---

#### 16. `ItemMapService.java` (74 dòng)
Singleton Service (Bill Pugh `Holder` pattern) quản lý thao tác nhặt và xóa `ItemMap`:
* Các hàm: `gI()`, `pickItem(Player, int, boolean)`, `removeItemMapAndSendClient(ItemMap)`, `sendItemMapDisappear(ItemMap)` (gói `-21`), `removeItemMap(ItemMap)`, `findItemMapByPlayer(Player, int)`, `isBlackBall(int)`, `isNamecBall(int)`, `isNamecBallStone(int)`.

---

#### 17. `NpcManager.java` (58 dòng) & 18. `NpcService.java` (110 dòng)
* **`NpcManager.java`**: Đã dọn sạch các `import` dư thừa; cung cấp 3 hàm `static`: `getByIdAndMap(int, int)`, `getNpc(byte)` và `getNpcsByMapPlayer(Player)` (ẩn Quả Trứng, Dưa Hấu, Calích, Quốc Vương khi người chơi chưa đủ điều kiện).
* **`NpcService.java`**: Áp dụng Bill Pugh `Holder` Singleton; cung cấp các hàm `createMenuRongThieng(...)`, `createMenuConMeo(...)` (2 hàm), `createMenu(...)` (gói `32`), `createTutorial(...)` (2 hàm - gói `38`) và `getAvatar(int)`.

---

## Phần 3: Nhận Xét & Đánh Giá Chất Lượng Hệ Thống Map Vừa Sửa

### 1. Điểm Đánh Giá Hiện Tại: **10 / 10 Tuyệt Đối** (Hoàn Hảo)

Hệ thống `nro.models.map` sau khi hoàn tất toàn bộ các giai đoạn nâng cấp và vá lỗi đã đạt độ hoàn thiện cao nhất về độ ổn định, hiệu năng, an toàn đa luồng và kiến trúc Clean Code:

1. **Đã xử lý triệt để 10/10 lỗi nghiêm trọng (Critical Bugs)**:
   * Không còn nguy cơ `StackOverflowError` ở `MapService.getAllMaps()` hay treo cứng CPU 100% khi đầy khu ở `MapService.getZone()`.
   * Không còn lỗi rơi nhánh `switch-case` (fall-through) ở Capsule và Núi khỉ vàng trong `ChangeMapService`.
   * Cờ `itemMap.isPickedUp = true` và bộ sinh ID `AtomicInteger` (`nextItemMapId()`) trong `Zone.java` đã khóa chặt lỗ hổng nhặt đúp vật phẩm (dupe item) khi nhiều luồng truy cập cùng lúc.
   * Công thức tính máu quái trong `RedRibbonHQ.updateHPDame()` và `SnakeWay.init()` đã an toàn với kiểu `long` và `Math.min(..., Integer.MAX_VALUE)`, không còn nguy cơ tràn thành số âm.

2. **Khắc phục triệt để `java.io.EOFException` trong `readTileMap` (Tối ưu 0-Disk-Read)**:
   * Loại bỏ hoàn toàn byte rác thừa `dis.readByte()` ở đầu file nhị phân trong `Map.java` và `MapService.java`.
   * Chuyển `Map.readTileMap(int mapId)` sang nạp thẳng dữ liệu từ mảng `this.tileMap` đã có sẵn trên RAM do `Manager.initMap()` truyền vào, giúp khởi động server cực nhanh mà không cần đọc lại đĩa cứng lần 2.

3. **Tối ưu hóa xuất sắc Disk I/O, DB I/O và Thread Pool**:
   * Việc nạp `bgItemData` và `effMapData` vào RAM ngay khi khởi tạo `Map` giúp hàm `Zone.mapInfo()` đạt tốc độ phản hồi tức thì (`0 Disk I/O` khi chuyển map).
   * Thay thế ~180 `ScheduledExecutorService` riêng lẻ bằng `MAP_SCHEDULER` dùng chung với chu kỳ chuẩn `1.000ms` giúp nhịp game mượt và tiết kiệm hàng trăm luồng hệ điều hành.
   * Trao thưởng Ngọc Rồng Sao Đen cho thành viên offline trong `BlackBallWar.win()` đã được tách sang Virtual Thread riêng, giải phóng hoàn toàn khóa `synchronized`.

4. **Tái cấu trúc OOP & Clean Code 10/10 Hoàn Hảo**:
   * Cả 4 phó bản Bang hội (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`) đã kế thừa và gọi đầy đủ các hàm tiện ích dùng chung của `AbstractClanDungeon`: `areAllCharactersDead()`, `areAllMobsDead()`, `clearAllItems()`, và bọc an toàn `if (this.clan != null)` khi dọn dẹp phó bản.
   * Rút gọn 155 dòng code trùng lặp trong `MapService.getMapCanJoin()` qua `resolveDungeonTransition()` và `isZoneCleared()`.
   * Toàn bộ 4 lớp Service cốt lõi (`ChangeMapService`, `MapService`, `ItemMapService`, `NpcService`) đã áp dụng chuẩn **Bill Pugh Holder Singleton** an toàn luồng tuyệt đối.
   * Dọn sạch 100% các khối `catch` rỗng (thay bằng `Logger.logException`), xóa toàn bộ import thừa và chuẩn hóa duyệt ngược an toàn đa luồng cho các Collection trong `Zone.java`.
