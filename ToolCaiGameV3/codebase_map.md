# CODEBASE_MAP.md — Bản Đồ Mã Nguồn & Chỉ Mục Dòng (NgocRongBaKhi)

Tài liệu này là **Chỉ mục định vị nhanh (Zero-Search Codebase Index)** cho toàn bộ **543 tệp Java** (`ToolCaiGameV3/Source/NroBaKhi/src/nro/models`) và **33 tệp PHP** (`ToolCaiGameV3/Source/WebTeamobi`).

**Quy tắc dành cho AI Agent:**
* Khi người dùng mô tả bằng ngôn ngữ trong game (Ví dụ: "sửa rơi đồ", "fix đệ tử"), hãy tra cứu ngay **Mục 0 (Ma Trận Tính Năng Game)** bên dưới để biết đích danh tệp.
* Trước khi đọc một tệp lớn, hãy tra cứu **Mục 2 (Line-Range Index)** để chỉ đọc đúng khoảng dòng (`StartLine` .. `EndLine`) cần sửa, tuyệt đối không đọc cả file hàng ngàn dòng gây tốn token.

---

## 0. Ma Trận Tra Cứu Nhanh Theo Tính Năng Game (Feature-to-File Matrix)

| Ngôn Ngữ Game Thường Dùng | Tệp Chịu Trách Nhiệm Chính | Hàm / Vị Trí Cụ Thể |
| :--- | :--- | :--- |
| **Nhặt đồ, rơi đồ, rớt vật phẩm trên Map** | `nro.models.map.ItemMap.java`<br>`nro.models.services.InventoryService.java`<br>`nro.models.services.Service.java` | `Service.dropItemMap()`<br>`InventoryService.addItemBag()`<br>`ItemMap.isPicked()` |
| **Đập đồ, nâng cấp sao pha lê, chuyển hóa, ép đồ** | `nro.models.services.CombineService.java`<br>`nro.models.item.Combine.java` | `CombineService.combine()` (`L34–L200`)<br>`CombineService.showInfoCombine()` |
| **Đệ tử (Pet), gọi đệ, đệ ăn đậu, mở kỹ năng đệ** | `nro.models.player.Pet.java`<br>`nro.models.services.PetService.java`<br>`nro.models.services_func.useitem.SkillAndPetHandler.java` | `PetService.changeStatus()`<br>`SkillAndPetHandler.openPetSkill()`<br>`Pet.followMaster()` |
| **Sử dụng vật phẩm, mở hộp quà, bùa, capsule** | `nro.models.services_func.UseItem.java`<br>`nro.models.services_func.useitem.*` | `GiftBoxHandler.java` (hộp quà, rương)<br>`ConsumableHandler.java` (đậu, bùa)<br>`MiniPetHandler.java` (pet theo sau) |
| **Boss xuất hiện, chiêu thức boss, tỉ lệ rơi đồ boss** | `nro.models.boss.Boss_Manager.BossManager.java`<br>`nro.models.boss.BossFactory.java`<br>`nro.models.boss.Boss.java`<br>`nro.models.services.RewardService.java` | `BossManager.update()`<br>`Boss.afk()` / `Boss.attack()`<br>`RewardService.initChiSoItem()` |
| **Giao dịch người chơi (Trade), Ký gửi (Siêu thị)** | `nro.models.player.Trade.java`<br>`nro.models.services.ShopService.java`<br>`nro.models.item.ConsignmentShop.java` | `Trade.openTabTrade()`<br>`ShopService.buyItemShop()`<br>`ConsignmentShop.consignItem()` |
| **Nhiệm vụ chính tuyến, Bò Mộng, Bang hội** | `nro.models.services.TaskService.java`<br>`nro.models.task.TaskOrder.java` | `TaskService.doneTask()` (`L618`)<br>`TaskService.checkDoneTaskKillBoss()` (`L394`) |
| **Kỹ năng (Skill), hồi chiêu, đánh quái, đánh người** | `nro.models.services.SkillService.java`<br>`nro.models.skill.Skill.java` | `SkillService.useSkill()` (`L48`)<br>`SkillService.playerAttackPlayer()` (`L961`) |
| **Lệnh Chat Admin / GM** | `nro.models.server.Command.java` | `initAdminCommands()` (`L56`)<br>`initParameterizedCommands()` (`L71`) |
| **Nạp tiền Web, Duyệt đơn, Tạo Giftcode, Hot-Reload** | `WebTeamobi/admin/` (`giftcode.php`, `vang.php`)<br>`nro.models.database.ShopDAO.java` | `ShopDAO.pollReloadRequests()`<br>`ajax_create_giftcode.php` |

---

## 1. Bản Đồ 57 Packages Trong `nro.models` (543 Tệp Java)

### 1.1. Tầng Lõi Hệ Thống, Mạng & Cơ Sở Dữ Liệu (`server`, `network`, `data`, `database`, `interfaces`)
* **`nro.models.server` (11 files, 3.432 dòng)** — Khởi động, điều phối luồng, gói tin và bảo trì:
  * `ServerManager.java` (324 dòng): Hàm `main()` (`L86`), khởi chạy các luồng dịch vụ/Boss (`run()` `L106`), cập nhật BXH (`startTopUpdater()` `L157`), mở Socket (`activeServerSocket()` `L203`), giới hạn IP (`canConnectWithIp()` `L234`), đóng server bảo trì (`close()` `L268`), lệnh Console (`activeCommandLine()` `L289`).
  * `Manager.java` (1.173 dòng): Kho lưu trữ dữ liệu tĩnh toàn cục trên RAM (`MAPS`, `ITEM_TEMPLATES`, `MOB_TEMPLATES`, `NPCS`, `CLANS`, `SHOPS`, `ACHIEVEMENT_TEMPLATE`). Khởi tạo bản đồ & luồng cập nhật Zone (`initMap()` `L183`), nạp toàn bộ dữ liệu từ MySQL (`loadDatabase()` `L288`), truy vấn BXH (`realTop()` `L983`), đọc `Config.properties` (`loadProperties()` `L1032`).
  * `Controller.java` (952 dòng): Bộ điều phối 58 mã lệnh mạng chính (`onMessage()` `L76`, `messageNotLogin()` `L721`, `messageNotMap()` `L742`, `messageSubCommand()` `L827`, `createChar()` `L867`, `sendInfo()` `L916`). *(Xem chi tiết tại `PACKET_PROTOCOL.md`)*.
  * `Client.java` (209 dòng): Quản lý danh sách người chơi đang online (`players_id`, `players_userId`, `players_name`, `players`), xử lý thoát game và lưu dữ liệu (`remove(Player)` `L79`, `kickSession()` `L131`, `close()` `L150`).
  * `Command.java` (317 dòng): Xử lý các câu lệnh Chat Admin/GM (`initAdminCommands()` `L56`, `initParameterizedCommands()` `L71`, `chat()` `L246`).
  * `MenuController.java` (70 dòng): Điều phối mở Menu NPC (`openMenuNPC()`) và chọn tùy chọn (`doSelectMenu()`).
  * `Maintenance.java` (73 dòng) & `AutoMaintenance.java` (95 dòng): Đếm ngược bảo trì và bảo trì định kỳ theo giờ.
  * `ServerNotify.java` (96 dòng), `ServerLog.java` (71 dòng), `AutoUpdate_NhanQuaFree.java` (52 dòng).
* **`nro.models.network` (12 files, 1.262 dòng)** — Tầng TCP Socket với Java Virtual Threads:
  * `Network.java` (190 dòng), `Session.java` (193 dòng), `MySession.java` (158 dòng - lưu `userId`, `uu`, `pp`, `actived`, `vnd`, `tongnap`, `version`, `player`).
  * `Collector.java` (83 dòng - luồng đọc gói tin), `Sender.java` (99 dòng - luồng gửi gói tin qua `LinkedBlockingDeque`), `MessageSendCollect.java` (101 dòng - mã hóa/giải mã XOR theo `KEYS`), `Message.java` (247 dòng).
  * `SessionManager.java` (70 dòng), `QueueHandler.java` (62 dòng), `KeyHandler.java` (24 dòng), `MyKeyHandler.java` (15 dòng), `SessionFactory.java` (20 dòng).
* **`nro.models.data` (5 files, 1.341 dòng)** — Kết nối HikariCP & Gửi gói tin dữ liệu nền:
  * `LocalManager.java` (199 dòng): Quản lý `HikariDataSource`, thực thi `executeQuery()` và `executeUpdate()`.
  * `DataGame.java` (507 dòng) & `ItemData.java` (115 dòng): Đóng gói và gửi dữ liệu bản đồ, kỹ năng, vật phẩm, hình ảnh, icon, tileSet xuống Client.
  * `ResultSetImpl.java` (456 dòng) & `LocalResultSet.java` (64 dòng): Lớp bọc kết quả truy vấn SQL trong bộ nhớ.
* **`nro.models.database` (7 files, 3.222 dòng)** — Truy cập dữ liệu (DAO):
  * `MrBlue.java` (1.355 dòng): Xác thực đăng nhập (`login()` `L54`), tải nhân vật offline (`loadById()` `L155`), giải mã toàn bộ 39 cột JSON nhân vật (`loadPlayer()` `L178`).
  * `PlayerDAO.java` (1.137 dòng): Khởi tạo nhân vật mới (`createNewPlayer()` `L31`), đóng gói JSON và lưu nhân vật xuống MySQL (`updatePlayer()` `L343`), kiểm tra đăng xuất (`checkLogout()` `L997`), trừ tiền VNĐ (`subvnd()` `L1021`, `MuaThanhVien()` `L1048`).
  * `SuperRankDAO.java` (320 dòng), `ShopDAO.java` (201 dòng - có `pollReloadRequests()`), `HistoryTransactionDAO.java` (98 dòng), `EventDAO.java` (73 dòng), `TraningDAO.java` (38 dòng).

---

### 1.2. Tầng Thực Thể Người Chơi, Kỹ Năng & Vật Phẩm (`player`, `skill`, `item`, `combine`, `shop`, `shop_ky_gui`)
* **`nro.models.player` (27 files, 8.412 dòng)** — Thực thể Nhân vật, Chỉ số, Đệ tử & Hiệu ứng:
  * `NPoint.java` (2.162 dòng): Trung tâm tính toán mọi chỉ số nhân vật, cải trang, set kích hoạt, nội tại, bùa, thức ăn.
  * `Player.java` (1.908 dòng): Lớp thực thể người chơi, ngoại hình (`getHead/Body/Leg/Mount/Aura`), nhận sát thương (`injured()`), chết (`setDie()`), vòng lặp `update()`.
  * `Pet.java` (1.177 dòng): Trí tuệ nhân tạo (AI) của Đệ tử, hợp thể (`fusion`, `fusionGogeta`, `fusion2`, `fusion3`), tự đánh quái/bảo vệ sư phụ.
  * `DropItem.java` (660 dòng), `EffectSkin.java` (513 dòng - hiệu ứng cải trang/skin), `EffectSkill.java` (263 dòng - choáng, trói, thôi miên, khiên năng lượng, khỉ), `SetClothes.java` (239 dòng - kiểm tra đủ bộ Set Kích Hoạt 5 món).
  * `NewSkill.java` (182 dòng), `Inventory.java` (151 dòng), `IDMark.java` (150 dòng - lưu các cờ trạng thái tạm thời của người chơi), `ItemEvent.java` (150 dòng), `Charms.java` (147 dòng), `Fusion.java`, `Friend.java`, `Enemy.java`, `Location.java`, `KOLProgressData.java`, `Traning.java`, `RewardBlackBall.java`, `FightMabu.java`, `Satellite.java`, `LearnSkill.java`.
* **`nro.models.combine` (32 files, 4.947 dòng)** — Hệ thống Đập đồ / Pha lê hóa / Chế tạo tại Bà Hạt Mít & Whis:
  * Điều phối chính: `CombineService.java` (737 dòng), `CombineSystem.java` (354 dòng).
  * 30 lớp xử lý từng loại đập đồ riêng biệt (đã được tách module rất tốt): `NangCapVatPham.java` (284 dòng), `PhaLeHoaTrangBi.java` (215 dòng), `ChuyenHoaTrangBi_Ngoc.java` (203 dòng), `CuongHoaLoSaoPhaLe.java` (201 dòng), `ChuyenHoaTrangBi_Vang.java` (196 dòng), `CheTaoTrangBiThienSu.java` (173 dòng), `EpSaoTrangBi.java` (169 dòng), `DoiSachTuyetKy.java` (159 dòng), `NangChiSoBongTai3.java` (154 dòng), `NangCapBongTai3.java` (152 dòng), `NangCapChanMenh.java`, `NangCapDeTu.java`, `NangCapKichHoat.java`, `NangCapKichHoatVip.java`, `NangCapSaoPhaLe.java`, `NangCapSachTuyetKy.java`, `NangCapVatPham_1.java`, `NhapNgocRong.java`, `PhaLeHoaTrangBi_1.java`, `PhanRaSach.java`, `PhanRaTrangBiKichHoat.java`, `TaySach.java`...
* **`nro.models.shop` (12 files, 1.940 dòng)** & **`nro.models.shop_ky_gui` (3 files, 644 dòng)**:
  * `ShopService.java` (1.299 dòng): Mở cửa hàng, mua vật phẩm bằng vàng/ngọc/hồng ngọc/vật phẩm sự kiện, bán vật phẩm, mua lại đồ đã bán (`items_daban`), rương phụ Vòng quay thượng đế.
  * `ConsignShopService.java` (528 dòng), `ConsignShopManager.java` (75 dòng), `ConsignItem.java` (41 dòng): Chợ ký gửi vật phẩm giữa người chơi.

---

### 1.3. Tầng Dịch Vụ Nghiệp Vụ (`services`, `services_func`, `services_dungeon`)
* **`nro.models.services` (22 files, 14.582 dòng)** & **`nro.models.services.shenron` (5 files, 1.310 dòng)**:
  * `Service.java` (2.972 dòng): Gửi gói tin đồng bộ trạng thái nhân vật, thông báo, chat, BXH, cờ PK, hiệu ứng.
  * `RewardService.java` (1.881 dòng): Khởi tạo chỉ số gốc cho trang bị rơi ra (`initBaseOptionClothes`), vòng quay thượng đế (`getListItemLuckyRound`), kích hoạt SKH.
  * `TaskService.java` (1.654 dòng): Kiểm tra tiến độ và trao thưởng Nhiệm vụ chính tuyến (`TaskMain`), Nhiệm vụ hàng ngày Bò Mộng (`SideTask`) và Nhiệm vụ Bang hội (`ClanTask`).
  * `SkillService.java` (1.330 dòng): Xử lý toàn bộ kỹ năng tấn công, trị thương, tự phát nổ, dịch chuyển, đẻ trứng, khiên năng lượng, kỹ năng mới (`useSkill`, `useSkillAttack`, `useSkillAlone`, `useSkillBuffToPlayer`).
  * `InventoryService.java` (1.212 dòng): Tìm kiếm, thêm, xóa, giảm số lượng, sắp xếp, mặc/tháo trang bị giữa Túi (`itemsBag`), Rương (`itemsBox`), Thân (`itemsBody`) và Đệ tử (`pet.inventory.itemsBody`).
  * `ItemService.java` (1.175 dòng): Tạo mới vật phẩm, tạo đồ Thần Linh (`randDoTL`), đồ Thiên Sứ (`DoThienSu`), mở hộp SKH (`OpenSKH`).
  * `ClanService.java` (1.064 dòng): Tạo bang, xin vào bang, cho đậu, phong chức, đuổi thành viên, lưu bang hội.
  * Các dịch vụ khác: `EffectSkillService.java` (611 dòng), `PetService.java` (352 dòng), `PlayerService.java` (350 dòng), `FriendAndEnemyService.java` (316 dòng), `ItemTimeService.java` (297 dòng), `AchievementService.java`, `ChatGlobalService.java`, `FlagBagService.java`, `IntrinsicService.java`, `OpenPowerService.java`, `RadarService.java`, `SubMenuService.java`.
  * Gọi Rồng Thần (`services.shenron`): `SummonDragon.java` (556 dòng - Rồng Thần Trái Đất & Rồng Xương), `SummonDragonNamek.java` (321 dòng - Rồng Namec Porunga), `Shenron_Event.java` (275 dòng).
* **`nro.models.services_func` (8 files, 2.303 dòng)** & **`nro.models.services_func.useitem` (5 files, 2.418 dòng)**:
  * `Input.java` (759 dòng): Xử lý tất cả các biểu mẫu nhập chữ/số từ người chơi và Admin (`doInput` `L97`, `createForm*` `L583..743`).
  * `UseItem.java` (500 dòng): Điều phối sử dụng vật phẩm và ủy quyền xuống 5 lớp Handler con trong `useitem/`:
    1. `GiftBoxHandler.java` (1.072 dòng): Mở Rương gỗ, Capsule kỳ bí, Hộp thần linh, Hộp quà Tết/Noel/Goku Day/VIP.
    2. `ConsumableHandler.java` (664 dòng): Đậu thần, nho, thuốc tăng lực, item sự kiện, thức ăn, siêu thần thủy.
    3. `SkillAndPetHandler.java` (310 dòng): Sách võ công, đổi đệ tử, trứng Mabư, trứng Linh thú, bông tai Porata.
    4. `TeleportAndRadarHandler.java` (266 dòng): Viên Capsule bay, máy dò tìm boss/ngọc rồng.
    5. `MiniPetHandler.java` (106 dòng): Gọi thú cưng bay theo.
  * `Trade.java` (466 dòng) & `TransactionService.java` (233 dòng): Giao dịch trực tiếp giữa 2 người chơi.
  * `LuckyRound.java` (188 dòng), `EffectMapService.java` (89 dòng), `BuyBackService.java` (37 dòng), `MiniGame.java` (31 dòng).

---

### 1.4. Tầng Bản Đồ, Phó Bản, Quái Vật, NPC & Boss (`map`, `mob`, `npc`, `boss`, `matches`, `Bot`)
* **`nro.models.map` (6 files, 1.631 dòng)**, **`map.phoban` (6 files, 1.484 dòng)**, **`map.service` (5 files, 2.011 dòng)** & **`services_dungeon` (10 files, 1.507 dòng)**:
  * `Zone.java` (868 dòng) & `Map.java` (492 dòng): Quản lý thực thể trong từng khu vực, nhặt vật phẩm (`Zone.pickItem` `L322`), đồng bộ thông tin khu vực (`Zone.mapInfo` `L612`).
  * `ChangeMapService.java` (1.140 dòng) & `MapService.java` (629 dòng): Chuyển bản đồ, kiểm tra điều kiện vào map đặc biệt (`checkMapCanJoin` `L875`).
  * 6 Phó bản trong `map.phoban`: `RedRibbonHQ.java` (Doanh trại Độc Nhãn - 436 dòng), `BanDoKhoBau.java` (272 dòng), `SnakeWay.java` (Con đường rắn độc - 269 dòng), `DestronGas.java` (Khí gas hủy diệt - 259 dòng), `BlackBallWar.java` (Ngọc rồng sao đen - 148 dòng), `MajinBuu14H.java` (100 dòng).
* **`nro.models.npc` (8 files, 1.736 dòng)** & **`nro.models.npc_list` (59 files, 6.676 dòng)**:
  * `NpcFactory.java` (750 dòng): Khởi tạo 59 NPC trong `npc_list/` (lớn nhất: `QuyLaoKame.java` 585 dòng, `BaHatMit.java` 457 dòng, `LyTieuNuong.java` 391 dòng, `ToriBot.java` 371 dòng, `Osin.java` 292 dòng, `Whis.java` 262 dòng) và xử lý menu `ConMeo` (`L318`), `RongThieng` (`L257`).
* **`nro.models.boss` (31 sub-packages, ~140 files, ~17.500 dòng)**:
  * Lõi Boss: `Boss.java` (1.107 dòng - vòng đời `REST -> RESPPAWN -> JOIN_MAP -> CHAT_S -> ACTIVE -> DIE -> REWARD -> CHAT_E -> LEAVE_MAP`), `BossesData.java` (2.763 dòng - dữ liệu cấu hình toàn bộ Boss), `BossID.java` (199 dòng), `BossManager.java` (440 dòng - khởi tạo Boss tại `loadBoss()`).
* **`nro.models.Bot` (7 files, 1.109 dòng)**: Hệ thống Bot giả lập người chơi (`Bot.java`, `BotAttackplayer.java`, `BotPemQuai.java`, `BotSanBoss.java`, `BotGiaoDich.java`, `NewBot.java`).

---

## 2. Chỉ Mục Dòng (Line-Range Index) Cho 10 Tệp Lớn Nhất

Khi cần đọc hoặc sửa các tệp dưới đây, hãy dùng công cụ `view_file` với đúng `StartLine` và `EndLine` tương ứng:

### 2.1. `nro/models/services/Service.java` (2.972 dòng)
* **`L72–L225`**: Hiển thị con số may mắn, gửi hiệu ứng Chibi, Pet đi theo, Huy hiệu (`sendPetFollow`, `sendBadgesPlayer`, `sendChibi`).
* **`L228–L489`**: Hiển thị các Bảng Xếp Hạng (BXH Sức mạnh, Trái Đất, Namek, Xayda, BDKB, Khí Gas, CDRD).
* **`L491–L680`**: Gửi gói tin廣播 (Broadcast) toàn server hoặc trong phạm vi `Zone` (`sendMessAllPlayer`, `sendMessAllPlayerInMap`, `sendMessAnotherNotMeInMap`, `Send_Info_NV`, `clearMap`).
* **`L686–L925`**: Chat khu vực (`chat`), dịch chuyển tàu (`Transport`), gửi chỉ số (`point` `L766`), gửi toàn bộ thông tin nhân vật (`player` `L821`).
* **`L946–L1185`**: Cộng sức mạnh & tiềm năng (`addSMTN` `L946`), tính cấp độ (`getCurrLevel`), hồi sinh/chết (`hsChar`, `charDie`), đánh quái (`attackMob` `L1166`).
* **`L1187–L1355`**: Gửi ngoại hình Cải trang (`Send_Caitrang`), Cờ đeo lưng (`sendFlagBag`), các loại Thông báo màn hình (`sendThongBaoOK`, `sendThongBaoAllPlayer`, `sendBigMessage`, `sendThongBao` `L1293`), gửi tiền tệ (`sendMoney` `L1324`).
* **`L1356–L1588`**: Cờ PK (`openFlagUI`, `changeFlag`, `chooseFlag`), đánh người chơi (`attackPlayer` `L1456`), hồi chiêu kỹ năng (`releaseCooldownSkill`, `sendTimeSkill`), rơi vật phẩm ra bản đồ (`dropItemMap` `L1539`).
* **`L1589–L1845`**: Thông tin Đệ tử (`showInfoPet` `L1589`), tốc độ & tọa độ (`sendSpeedPlayer`, `setPos`), menu tương tác người chơi (`getPlayerMenu`), chat riêng (`chatPrivate`), đổi mật khẩu (`changePassword` `L1813`).
* **`L1848–L2255`**: Thiết lập Client (`setClientType` `L1912`), Vệ tinh (`dropSatellite`), Mã bảo vệ (`mabaove` `L1959`), Hiệu ứng nhân vật & Cải trang (`sendEffPlayer`, `sendEffCaiTrang`), PvP (`sendPVP`).
* **`L2258–L2972`**: Gọi phân thân (`callNhanBan`), BigBoss (`sendBigBoss`), Ma Bư (`sendMabuHold`, `sendMabuEat`), cập nhật điểm Máy Đấm (`updatePlayerTotalDamage` `L2762`, `updatePlayerPointMayDam` `L2776`), đồng bộ Bot (`sendInfoCharMoiToMe` `L2810`).

### 2.2. `nro/models/player/NPoint.java` (2.162 dòng)
* **`L213–L580`**: Hàm trung tâm `calPoint()` (`L213`) và `setPointWhenWearClothes()` (`L228`) — Duyệt qua toàn bộ trang bị nhân vật & đệ tử để kích hoạt Set trang bị, Bông tai, Cải trang, Chân mệnh.
* **`L583–L765`**: Hàm `addOption(ItemOption io)` (`L583`) — `switch (io.optionTemplate.id)` cộng từng loại chỉ số Option vào nhân vật.
* **`L766–L1355`**: Tính tốc độ (`setSpeed` `L766`), chỉ số cơ bản (`setBasePoint` `L819`), hồi HP/MP (`setHpHoi` `L850`, `setMpHoi` `L871`), máu tối đa (`setHpMax` `L892`), KI tối đa (`setMpMax` `L1041`), sức đánh (`setDame` `L1171`), giáp (`setDef` `L1330`), chí mạng (`setCrit` `L1339`).
* **`L1356–L1810`**: Reset chỉ số trước khi tính lại (`resetPoint` `L1356`), cộng/trừ HP/MP, tính sát thương gây ra thực tế (`getDameAttack` `L1464`), tính tiềm năng nhận được khi đánh quái (`calSucManhTiemNang` `L1690`), trừ sát thương qua giáp (`subDameInjureWithDeff` `L1801`).
* **`L1811–L2162`**: Giới hạn sức mạnh theo cấp mở giới hạn (`getPowerLimit` `L1815`, `getHpMpLimit` `L1869`, `getDameLimit` `L1896`), cộng điểm tiềm năng (`increasePoint` `L1986`), vòng lặp hồi phục tự nhiên (`update` `L2102`).

### 2.3. `nro/models/player/Player.java` (1.908 dòng)
* **`L1–L390`**: Khai báo toàn bộ thuộc tính của `Player` và hàm khởi tạo `Player()` (`L356`).
* **`L394–L620`**: Kiểm tra chết (`isDie` `L394`), vòng lặp `run()` (`L420`), hàm cập nhật mỗi giây `update()` (`L433`).
* **`L729–L1045`**: Tính toán ID ngoại hình hiển thị (`getHat` `L729`, `getAura` `L733`, `getEffFront` `L748`, `getHead` `L825`, `getBody` `L879`, `getLeg` `L939`, `getFlagBag` `L999`, `getMount` `L1024`).
* **`L1048–L1330`**: Nhận sát thương khi bị đánh (`injured` `L1048`), tự phát nổ (`setBom` `L1236`), xử lý khi chết (`setDie` `L1244`).
* **`L1335–L1908`**: Triệu hồi MiniPet (`sendNewPet` `L1335`), giải phóng bộ nhớ khi thoát (`dispose` `L1664`).

### 2.4. `nro/models/services/RewardService.java` (1.881 dòng)
* **`L37–L214`**: Danh sách vật phẩm Vòng quay thượng đế (`getListItemLuckyRound` `L37`), thưởng Lân con (`rewardLancon` `L184`).
* **`L215–L1013`**: Khởi tạo chỉ số ngẫu nhiên cho vật phẩm (`initChiSoItem` `L219`).
* **`L1014–L1669`**: Khởi tạo chỉ số gốc cho quần áo, găng, giày, рада theo cấp (`initBaseOptionClothes` `L1014`).
* **`L1670–L1881`**: Khởi tạo Option Set Kích Hoạt (`initActivationOption` `L1670`), thưởng Capsule Tết (`rewardCapsuleTet` `L1679`), random đồ cấp (`randItemDoCap` `L1857`).

### 2.5. `nro/models/services/TaskService.java` (1.654 dòng)
* **`L55–L156`**: Lấy nhiệm vụ và gửi gói tin cập nhật nhiệm vụ xuống Client (`sendTaskMain` `L72`, `sendNextTaskMain` `L105`, `sendUpdateCountSubTask` `L125`).
* **`L157–L617`**: Các hàm kiểm tra hoàn thành nhiệm vụ theo hành động: nói chuyện NPC (`checkDoneTaskTalkNpc` `L157`), vào bang (`L255`), đạt mốc sức mạnh (`L267`), đi tới bản đồ (`L312`), nhặt đồ (`L344`), giết Boss (`checkDoneTaskKillBoss` `L394`), giết Quái (`checkDoneTaskKillMob` `L478`).
* **`L618–L1135`**: Hàm trung tâm `doneTask(Player, int idTaskCustom)` (`L618`) và trao thưởng nhiệm vụ (`rewardDoneTask` `L1089`).
* **`L1250–L1654`**: Nhiệm vụ hàng ngày Bò Mộng (`changeSideTask` `L1257`, `paySideTask` `L1279`) và Nhiệm vụ Bang hội (`changeClanTask` `L1483`, `payClanTask` `L1504`).

### 2.6. `nro/models/services/SkillService.java` (1.330 dòng)
* **`L48–L359`**: Điều phối dùng kỹ năng (`useSkill` `L48`), kỹ năng mới (`useNewSkillNotFocus` `L122`, `updateSkillSpecial` `L149`, `learSkillSpecial` `L340`).
* **`L360–L656`**: Kỹ năng tấn công mục tiêu (`useSkillAttack` `L360`). *(Lưu ý: `Skill.DA_DON_THO` tạo `new Thread` ở `L562`)*.
* **`L657–L902`**: Kỹ năng tự thân (`useSkillAlone` `L657` - Thái dương hạ san, Khiên năng lượng, Hóa khỉ, Tự sát, Đẻ trứng, Huýt sáo) và kỹ năng hỗ trợ (`useSkillBuffToPlayer` `L847` - Trị thương).
* **`L903–L1330`**: Phản sát thương (`L903`), hút HP/MP (`L941`), đánh người (`playerAttackPlayer` `L961`), đánh quái (`playerAttackMob` `L1020`), kiểm tra KI/Cooldown (`L1099..L1250`).

---

## 3. Bản Đồ Mã Nguồn Web & Admin (`ToolCaiGameV3/Source/WebTeamobi`)

### 3.1. Các Tệp Trang Chủ & Người Dùng (`WebTeamobi/`)
* `config.php` (18 dòng) & `connect.php` (5 dòng): Cấu hình MySQLi (`localhost`, `root`, `nrobakhi`) và đường dẫn Zalo/TestFlight.
* `dang-nhap.php` (120 dòng), `dang-ky.php` (140 dòng), `dangky.php` (77 dòng), `doimatkhau.php` (68 dòng): Xác thực tài khoản người chơi.
* `tai-khoan.php` (1.291 dòng): Trang quản lý tài khoản người chơi, nạp thẻ, lịch sử giao dịch, đổi mật khẩu.
* `bxh01.php` (332 dòng) & `bxh02.php` (327 dòng): Bảng xếp hạng Sức mạnh, Nhiệm vụ, Tổng nạp, Thỏi vàng (parse trực tiếp các cột JSON `data_point`, `data_task`, `items_bag`, `items_box` từ bảng `player`).
* `forum.php` (390 dòng), `forum_data.php` (257 dòng), `bai-viet.php` (164 dòng), `post_detail_logic.php` (137 dòng): Diễn đàn cộng đồng, đăng bài và bình luận.

### 3.2. Các Tệp Quản Trị Viên (`WebTeamobi/admin/`)
* `panel.php` (423 dòng): Bảng điều khiển trung tâm của Admin.
* `player.php` (842 dòng): Tìm kiếm, xem và chỉnh sửa trực tiếp chỉ số nhân vật (`data_point`), đệ tử (`pet`), hành trang (`items_body`, `items_bag`, `items_box`) trong bảng `player`.
* `account.php` (826 dòng): Quản lý danh sách tài khoản `account` (khóa/mở khóa, cộng VNĐ/Tổng nạp, thêm tài khoản).
* `vang.php` (573 dòng), `ngoc.php` (553 dòng), `itemshop.php` (158 dòng), `ajax_update_shop.php` (52 dòng): Quản lý vật phẩm bán trong Cửa hàng (`item_shop`, `item_shop_option`) và gửi tín hiệu `shop_reload_request` cho Java Server nạp lại.
* `giftcode.php` (473 dòng), `taocode.php` (460 dòng), `ajax_create_giftcode.php` (56 dòng): Tạo và quản lý Giftcode kèm tín hiệu `giftcode_reload_request`.
* `item.php` (518 dòng), `map.php` (533 dòng), `giaodich.php` (228 dòng): Tra cứu danh sách `item_template`, `map_template` và lịch sử `history_transaction`.
