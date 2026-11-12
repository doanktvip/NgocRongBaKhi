# PACKET_PROTOCOL.md — Đặc Tả Giao Thức Gói Tin Mạng (NgocRongBaKhi)

Tài liệu này được trích xuất trực tiếp từ **`nro/models/server/Controller.java`** (952 dòng) và các lớp Service được ủy quyền trong bộ mã nguồn **`NgocRongBaKhi`**. 
Đây là tài liệu tham chiếu chuẩn để:
1. Tách `Controller.java` theo **Command Pattern** (`IPacketHandler`).
2. Cấu hình **Token Bucket Rate Limiter** cho từng nhóm mã lệnh (`cmd`).
3. Kiểm tra bảo mật (Anti-Cheat / Packet Spoofing / Validation) trên từng luồng dữ liệu.

---

## 1. Tổng Quan Luồng Gói Tin (Packet Pipeline)

1. **Tầng Đọc (`nro.models.network.Collector`)**:
   * Đọc `Message` từ `DataInputStream` qua `MessageSendCollect.readMessage(ISession, DataInputStream)`.
   * Nếu `msg.command == -27`: Gọi trực tiếp `session.sendKey()`.
   * Các `msg.command` khác: Chuyển vào `Controller.gI().onMessage(ISession, Message)`.
2. **Tầng Điều Phối (`nro.models.server.Controller`)**:
   * Phân nhánh chính tại `onMessage()` với **58 mã lệnh (`cmd` kiểu `byte` có dấu từ `-127` đến `127`)**.
   * Ba mã lệnh điều phối con (Sub-Controllers):
     * `cmd = -29` -> `messageNotLogin(MySession, Message)` (Xác thực & Thiết lập Client).
     * `cmd = -28` -> `messageNotMap(MySession, Message)` (Tạo nhân vật, Đồng bộ dữ liệu nền & Hoàn tất vào Map).
     * `cmd = -30` -> `messageSubCommand(MySession, Message)` (Cộng điểm chỉ số & Menu phụ).

---

## 2. Nhóm 1: Kết Nối, Xác Thực & Tải Tài Nguyên (Handshake, Auth & Assets)

Các gói tin này có thể được gửi khi `player == null` (trước khi vào map) hoặc trong quá trình tải dữ liệu.

| Mã lệnh (`cmd`) | Dòng trong `Controller` | Cấu trúc Payload đọc từ Client (`_msg.reader()`) | Dịch vụ xử lý & Luồng phản hồi (Server -> Client) | Điều kiện & Ghi chú Bảo mật / Rate Limit |
| :--- | :--- | :--- | :--- | :--- |
| **`-27`** | `618–621` *(và `Collector:36`)* | *(Không có payload)* | ` _session.sendKey()` -> `DataGame.sendVersionRes(_session)`: Gửi khóa giải mã XOR (`KEYS`) và phiên bản tài nguyên. | Tối đa 2 lần/kết nối. Lưu ý: `Collector` đã bắt `-27` trước khi vào `Controller`. |
| **`-29`** | `634–636` -> `721–740` | `byte subCmd`:<br>• **`subCmd = 0`**: `UTF username`, `UTF password`<br>• **`subCmd = 2`**: `byte clientType`, `byte zoomLevel`, `boolean isGprs`, `int width`, `int height`, `boolean isQwerty`, `boolean isTouch`, `UTF platform` (`"os\|version"`) | `messageNotLogin()`:<br>• `0`: `session.login(user, pass)` -> `MrBlue.login()`<br>• `2`: `Service.gI().setClientType()` -> `DataGame.sendLinkIP(session)` | Lỗi `IOException` sẽ ngắt kết nối ngay (`session.disconnect()`). Cần áp dụng Rate Limit 1 lần/2 giây chống Brute-force. |
| **`-28`** | `631–633` -> `742–825` | `byte subCmd`:<br>• **`subCmd = 2`**: `UTF name`, `byte gender`, `byte hair`<br>• **`subCmd = 6`**: *(Trống)*<br>• **`subCmd = 7`**: *(Trống)*<br>• **`subCmd = 8`**: *(Trống)*<br>• **`subCmd = 10`**: `ubyte mapTempId` (`readUnsignedByte()`)<br>• **`subCmd = 13`**: *(Trống - Client báo đã sẵn sàng)* | `messageNotMap()`:<br>• `2`: `createChar()` -> `PlayerDAO.createNewPlayer()` -> tự gọi `session.login()`<br>• `6`: `DataGame.updateMap(_session)`<br>• `7`: `DataGame.updateSkill(_session)`<br>• `8`: `ItemData.updateItem(_session)`<br>• `10`: `DataGame.sendMapTemp(_session, id)`<br>• `13`: Khởi tạo dữ liệu nhân vật vào Map (`Service.player()`, `Send_Caitrang()`, `sendFlagBag()`, `sendSkillShortCut()`, `sendAllItemTime()`, `sendThongBaoServer()`, `zone.mapInfo()`, `sendTimeSkill()`, `sendNewPet()`). | Ở `subCmd = 2` (`createChar`): Kiểm tra `!Maintenance.isRunning`, độ dài tên `5..10` ký tự, không chứa ký tự đặc biệt, không nằm trong `ConstIgnoreName.IGNORE_NAME`. |
| **`-101`** | `350–352` -> `912–914` | *(Không đọc payload)* | `login2(_session, _msg)` -> `Service.gI().sendThongBaoOK()` báo truy cập Domain để đăng ký. | Chặn đăng ký/chơi thử tự động, hướng dẫn người chơi lên Web. |
| **`42`** | `197–200` | *(Đã comment vô hiệu hóa)* | *(Trống - Rơi xuống `case -127:` nếu gửi `cmd = 42`!)* | **CẢNH BÁO BUG**: `case 42:` bị comment mất `break;`, nên nếu Client gửi `cmd = 42`, code sẽ chạy thẳng vào `case -127` (`LuckyRound.readOpenBall`). Cần thêm `break;` ngay cho `case 42:`. |
| **`-111`** | `622–624` | *(Không có payload)* | `DataGame.sendDataImageVersion(_session)` | Gửi phiên bản hình ảnh. |
| **`-87`** | `417–419` | *(Không có payload)* | `DataGame.updateData(_session)` | Gửi dữ liệu cấu hình game (`dart`, `arrow`, `effect`, `image`, `part`, `skill`). |
| **`-74`** | `394–403` | `byte type`:<br>• `type == 1`: Yêu cầu kích thước gói tài nguyên<br>• `type == 2`: Tải toàn bộ file tài nguyên | • `type == 1`: `DataGame.sendSizeRes(_session)`<br>• `type == 2`: `DataGame.sendRes(_session)` | Có ghi log cảnh báo IP đang tải dữ liệu. Cần giới hạn tần suất để tránh tấn công làm nghẽn băng thông đọc ổ cứng. |
| **`-67`** | `420–423` | `int id` (ID của Icon) | `DataGame.sendIcon(_session, id)` | Tải ảnh Icon theo ID. |
| **`66`** | `424–426` | `UTF imageName` | `DataGame.sendImageByName(_session, imageName)` | Cần kiểm tra chống Path Traversal trong tên ảnh (`..` hoặc `/`). |
| **`-66`** | `427–440` | `short effId` | `DataGame.sendEffectTemplate(_session, effId, idT)` (Tự động đổi `idT = 59` hoặc `60` nếu `effId == 25` và map đang gọi Rồng Thần `shenronType != -1`). | Yêu cầu `player != null` và `player.zone != null`. |
| **`-62`** | `441–445` | `byte flagId` | `FlagBagService.gI().sendIconFlagChoose(player, flagId)` | Lấy icon cờ trên спина/túi đeo. |
| **`-63`** | `446–452` | `byte fbid` (Chuyển sang không dấu `fbid & 0xFF`) | `FlagBagService.gI().sendIconEffectFlag(player, fbidz)` | Lấy hiệu ứng cờ đeo lưng. |
| **`-32`** | `453–456` | `short bgId` | `DataGame.sendItemBGTemplate(_session, bgId)` | Tải mẫu vật phẩm nền bản đồ (Background Item). |
| **`11`** | `567–570` | `byte mobId` | `DataGame.requestMobTemplate(_session, mobId)` | Tải hình ảnh và khung hình (frame) của Quái vật. |
| **`-41`** | `534–536` | `byte gender` | `Service.gI().sendCaption(_session, gender)` | Gửi danh hiệu cấp bậc theo hành tinh. |
| **`-38`** | `677–681` -> `947–951` | *(Không có payload)* | `finishUpdate(player)` -> Gán `_session.finishUpdate = true`. | Đánh dấu Client đã hoàn tất cập nhật tài nguyên. |

---

## 3. Nhóm 2: Di Chuyển, Bản Đồ & Khu Vực (Movement, Map & Zone)

| Mã lệnh (`cmd`) | Dòng trong `Controller` | Cấu trúc Payload đọc từ Client (`_msg.reader()`) | Dịch vụ xử lý & Hành vi Hệ thống | Điều kiện & Ghi chú Bảo mật / Rate Limit |
| :--- | :--- | :--- | :--- | :--- |
| **`-7`** | `364–393` | `byte b` (Loại di chuyển: `1` = Bay), `short toX`, `[optional] short toY` | • Kiểm tra nhiệm vụ thành tựu bay nếu `b == 1` (`AchievementService.checkDoneTaskFly`).<br>• Gọi `PlayerService.gI().playerMove(player, toX, toY)`. | • Bỏ qua nếu `player.isDie()` (gửi lại `charDie`) hoặc đang bị khống chế (`isHaveEffectSkill()`).<br>• **Lỗ hổng Anti-Cheat**: Chỉ kiểm tra khoảng cách `> 500px` tại map Ngọc Rồng Sao Đen (`isMapBlackBallWar`). Cần bổ sung kiểm tra tốc độ/khoảng cách cho mọi map. |
| **`-33`** & **`-23`** | `463–469` | *(Không có payload)* | `ChangeMapService.gI().changeMapWaypoint(player)` + `Service.gI().hideWaitDialog(player)` | Chuyển bản đồ khi nhân vật bước vào điểm truyền tống (Waypoint) ở rìa map. |
| **`-39`** | `562–566` | *(Không có payload)* | `ChangeMapService.gI().finishLoadMap(player)` | Client báo đã tải xong bản đồ mới; Server bắt đầu đồng bộ các thực thể trong `Zone` cho người chơi. |
| **`29`** | `313–317` | *(Không có payload)* | `ChangeMapService.gI().openZoneUI(player)` | Mở bảng danh sách Khu vực (Zone) của bản đồ hiện tại. |
| **`21`** | `318–323` | `byte zoneId` | `ChangeMapService.gI().changeZone(player, zoneId)` | Đổi sang khu vực `zoneId` được chọn. |
| **`-105`** | `168–196` | *(Không có payload - dựa vào trạng thái `player.type` và `player.maxTime` trên Server)* | Xử lý kết thúc thời gian chờ tàu vũ trụ/chuyển map đặc biệt:<br>• `type=0, maxTime=30`: Đi Tương Lai (Map 102)<br>• `type=1, maxTime=5`: Đi Bản Đồ Kho Báu (Map 135)<br>• `type=2, maxTime=5`: Đi Hành Tinh Thực Vật (Map 80 / 160)<br>• `type=3, maxTime=5`: Chuyển khu Khí Gas Hủy Diệt<br>• `type=4, maxTime=5`: Vào Khí Gas Hủy Diệt (Map 149)<br>• `type=5, maxTime=5`: Vào Map 156 | Hoàn toàn dựa trên cờ `player.idMark` (`isGoToBDKB`, `isGoToKGHD`). Đảm bảo tốt tính Server-Authoritative. |
| **`-91`** | `550–561` | `byte index` (Chỉ số mục chọn trên bảng Capsule / Sao Đen) | Phụ thuộc `player.idMark.getTypeChangeMap()`:<br>• `CHANGE_CAPSULE`: `UseItem.gI().choseMapCapsule(player, index)`<br>• `CHANGE_BLACK_BALL`: `BlackBallWarService.gI().changeMap(player, index)` | Bay bằng Capsule hoặc dịch chuyển trong sự kiện Ngọc Rồng Sao Đen. |
| **`-15`** | `640–645` | *(Không có payload)* | `ChangeMapService.gI().changeMapBySpaceShip(player, mapId, 0, -1)` | Về nhà khi nhân vật bị hạ gục (nếu đang ở map Ma Bư thì về map 114, ngược lại về nhà theo hành tinh `gender + 21`). |
| **`-16`** | `646–650` | *(Không có payload)* | `PlayerService.gI().hoiSinh(player)` | Hồi sinh tại chỗ bằng Ngọc (chỉ cho phép khi `!player.isPKDHVT`). |
| **`18`** | `233–238` | `int playerId` (Đọc trong `FriendAndEnemyService.goToPlayerWithYardrat`) | Đặt `player.changeMapVIP = true` và gọi `FriendAndEnemyService.gI().goToPlayerWithYardrat(player, _msg)`. | Dịch chuyển tức thời (Yardrat) tới người chơi khác. Trong hàm đích đã kiểm tra `player.isAdmin() \|\| player.nPoint.teleport`. |

---

## 4. Nhóm 3: Chiến Đấu, Kỹ Năng, Chỉ Số & Đệ Tử (Combat, Skills, Stats & Pet)

| Mã lệnh (`cmd`) | Dòng trong `Controller` | Cấu trúc Payload đọc từ Client (`_msg.reader()`) | Dịch vụ xử lý & Hành vi Hệ thống | Điều kiện & Ghi chú Bảo mật / Rate Limit |
| :--- | :--- | :--- | :--- | :--- |
| **`34`** | `593–598` | `short selectSkill` (Template ID của kỹ năng) | `SkillService.gI().selectSkill(player, selectSkill)` | Đổi kỹ năng đang chọn để chuẩn bị tung chiêu. |
| **`-45`** | `470–479` | `byte status`<br>*(Nếu `status == 20` thì đọc thêm trong `SkillService.useSkill`: `byte skillId`, `short dx`, `short dy`, `byte dir`, `short x`, `short y`)* | `SkillService.gI().useSkill(player, null, null, status, _msg)` | Sử dụng kỹ năng đặc biệt / kỹ năng không cần mục tiêu hoặc kỹ năng mới (`type == 4`). Chặn nếu đang trong giao dịch (`TransactionService.check(player)`). |
| **`54`** | `599–610` | `byte mobId`<br>*(Nếu `mobId == -1` tức là đánh Đệ tử Khỉ/MobMe của người chơi khác -> đọc thêm `int masterId`)* | `Service.gI().attackMob(player, mobId, isMobMe, masterId)` | Tấn công quái vật trên bản đồ hoặc MobMe. Rate Limit khuyến nghị: tối đa 8–10 gói/giây. |
| **`-60`** | `611–617` | `int playerId` (ID của người chơi/Boss mục tiêu) | `Service.gI().attackPlayer(player, playerId)` | Tấn công người chơi khác hoặc Boss. |
| **`-113`** | `338–349` | Vòng lặp 10 lần: `byte skillShortcut[i]` (Nếu thiếu byte thì gán `-1`) | Cập nhật mảng `player.playerSkill.skillShortCut[0..9]` và gọi `sendSkillShortCut()`. | Lưu cấu hình 10 ô phím tắt kỹ năng trên màn hình Client. |
| **`-103`** | `353–363` | `byte act`:<br>• `act == 0`: *(Không đọc thêm)*<br>• `act == 1`: `byte flagType` | • `0`: `Service.gI().openFlagUI(player)` (Mở bảng Cờ PK)<br>• `1`: `Service.gI().chooseFlag(player, flagType)` (Đổi cờ PK) | Bật/tắt hoặc đổi màu cờ PK. |
| **`-30`** | `637–639` -> `827–857` | `byte subCommand`:<br>• **`subCommand = 16`**: `byte type`, `short point`<br>• **`subCommand = 64`**: `int playerId`, `short menuId` | `messageSubCommand()`:<br>• `16`: `player.nPoint.increasePoint(type, point)` (Cộng tiềm năng vào HP, KI, Sức đánh, Giáp, Chí mạng)<br>• `64`: `SubMenuService.gI().controller(player, playerId, menuId)` | Cần đảm bảo trong `increasePoint()` kiểm tra chặt `point > 0` để tránh lỗi truyền số âm. |
| **`112`** | `211–215` | *(Không có payload)* | `IntrinsicService.gI().showMenu(player)` | Mở bảng Nội tại nhân vật. |
| **`-107`** | `263–267` | *(Không có payload)* | `Service.gI().showInfoPet(player)` | Xem thông tin chỉ số và trang bị của Đệ tử (Pet). |
| **`-108`** | `268–272` | `byte status` | `player.pet.changeStatus(status)` | Đổi trạng thái Đệ tử (Đi theo, Bảo vệ, Tấn công, Về nhà, Hợp thể). Yêu cầu `player.pet != null`. |

---

## 5. Nhóm 4: Vật Phẩm, Cửa Hàng, Giao Dịch & Chợ Ký Gửi (Economy, Items & Trade)

Đây là nhóm **nhạy cảm nhất về bảo mật**, yêu cầu khóa đồng bộ (`synchronized`), kiểm tra trạng thái bảo trì (`!Maintenance.isRunning`), không trong giao dịch khác (`!TransactionService.check(player)`) và đã tắt khóa bảo vệ (`!player.baovetaikhoan`).

| Mã lệnh (`cmd`) | Dòng trong `Controller` | Cấu trúc Payload đọc từ Client (`_msg.reader()`) | Dịch vụ xử lý & Hành vi Hệ thống | Điều kiện & Ghi chú Bảo mật / Rủi ro Dup đồ |
| :--- | :--- | :--- | :--- | :--- |
| **`-20`** | `625–630` | `short itemMapId` | `ItemMapService.gI().pickItem(player, itemMapId, false)` | Nhặt vật phẩm rơi dưới đất. Yêu cầu `!player.isDie()`. |
| **`-40`** | `525–533` | `byte type`, `byte index` *(Đọc trong `UseItem.getItem`)*:<br>• `0`: Rương -> Thân/Túi<br>• `1`: Túi -> Rương<br>• `3`: Thân -> Rương<br>• `4`: Túi -> Thân (Mặc đồ)<br>• `5`: Thân -> Túi (Tháo đồ)<br>• `6`: Túi -> Thân Đệ tử<br>• `7`: Thân Đệ tử -> Túi | `UseItem.gI().getItem(_session, _msg)` -> Gọi `InventoryService` tương ứng, sau đó cập nhật `setClothes.setup()`, `sendFlagBag()`, `point()`, `sendSpeedPlayer()`. | Tự động hủy giao dịch đang mở (`TransactionService.cancelTrade`) ngay đầu hàm `getItem` để chống vừa giao dịch vừa cất đồ vào rương. |
| **`-43`** | `537–549` | `byte type`, `byte where`, `byte index`, `[nếu index == -1] short iditem` *(Đọc trong `UseItem.doItem`)*:<br>• `type = 0`: Dùng vật phẩm (`DO_USE_ITEM`)<br>• `type = 1`: Vứt vật phẩm (`DO_THROW_ITEM`)<br>• `type = 2`: Xác nhận vứt (`ACCEPT_THROW_ITEM`)<br>• `type = 3`: Xác nhận dùng (`ACCEPT_USE_ITEM`) | `UseItem.gI().doItem(player, _msg)` | Chặn nếu đang giao dịch hoặc đang bật `baovetaikhoan`. Đã kiểm tra biên `index >= 0 && index < itemsBag.size()`. |
| **`6`** | `273–292` | `byte typeBuy`, `short tempId` | `ShopService.gI().takeItem(player, typeBuy, tempId)` | Mua vật phẩm từ NPC Shop. Chặn khi `Maintenance.isRunning`, đang giao dịch hoặc bật `baovetaikhoan`. |
| **`7`** | `293–312` | `byte action`, `byte where`, `short index`:<br>• `action == 0`: Hiển thị hộp thoại xác nhận bán<br>• `action != 0`: Thực hiện bán vật phẩm | • `0`: `ShopService.gI().showConfirmSellItem(player, where, index)`<br>• `!= 0`: `ShopService.gI().sellItem(player, where, index)` | Bán vật phẩm vào Shop. Có đầy đủ kiểm tra bảo trì, giao dịch và bảo vệ tài khoản. |
| **`-86`** | `258–262` | `byte action` *(Đọc trong `TransactionService.controller`)*:<br>• `0` (`SEND_INVITE_TRADE`): `int playerId`<br>• `1` (`ACCEPT_TRADE`): `int playerId`<br>• `2` (`ADD_ITEM_TRADE`): `byte index`, `int quantity`<br>• `3` (`CANCEL_TRADE`): *(Trống)*<br>• `5` (`LOCK_TRADE`): *(Trống)*<br>• `7` (`ACCEPT`): *(Trống)* | `TransactionService.gI().controller(player, _msg)` -> Điều phối lớp `Trade`. | Có kiểm tra `actived`, `baovetaikhoan`, kiểm tra `PlayerDAO.checkLogout()` trên DB trước khi mở giao dịch và khóa `synchronized (trade)` khi `ACCEPT`. |
| **`-100`** | `84–136` | `byte action`:<br>• **`0` (Ký gửi đồ)**: `short idItem`, `byte moneyType`, `int money`, `int/byte quantity` (`int` nếu `version >= 220`, `byte` nếu `< 220`)<br>• **`1` (Hủy bán)** & **`2` (Nhận tiền)**: `short idItem`<br>• **`3` (Mua đồ)**: `short idItem`, `byte unused1`, `int unused2`<br>• **`4` (Mở tab/trang)**: `byte moneyType`, `byte page`<br>• **`5` (Đẩy lên Top)**: `short idItem` | `ConsignShopService.gI()`:<br>• `0`: `KiGui(player, idItem, money, moneyType, quantity)`<br>• `1, 2`: `claimOrDel(player, action, idItem)`<br>• `3`: `buyItem(player, idItem)`<br>• `4`: `openShopKyGui(player, moneyType, money)`<br>• `5`: `upItemToTop(player, idItem)` | **ĐIỂM NÓNG CẦN SỬA NGAY**: Trong `ConsignShopService`, các hàm `buyItem`, `claimOrDel` chưa được `synchronized` trên `ConsignItem`, dễ dẫn đến Race Condition dup đồ khi 2 luồng cùng bấm mua/nhận. |
| **`-81`** | `404–416` | `byte actionUnused`, `byte length`, vòng lặp `length` lần: `byte indexItem[i]` | `CombineService.gI().showInfoCombine(player, indexItem)` | Bỏ vật phẩm vào ô Pha lê hóa / Nâng cấp trang bị để xem thông tin tỷ lệ đập đồ. |
| **`-127`** | `201–205` | `byte typeUnused`, `byte count` *(Đọc trong `LuckyRound.readOpenBall`)* | `LuckyRound.gI().readOpenBall(player, _msg)` | Quay Vòng Quay Thượng Đế (`count` lượt bằng Vàng hoặc Ngọc). |

---

## 6. Nhóm 5: Tương Tác NPC, Nhiệm Vụ, Bang Hội & Xã Hội (NPC, Clan, Social & PvP)

| Mã lệnh (`cmd`) | Dòng trong `Controller` | Cấu trúc Payload đọc từ Client (`_msg.reader()`) | Dịch vụ xử lý & Hành vi Hệ thống | Điều kiện & Ghi chú Bảo mật / Rate Limit |
| :--- | :--- | :--- | :--- | :--- |
| **`33`** | `587–592` | `short npcId` | `MenuController.gI().openMenuNPC(_session, npcId, player)` | Nhấn vào NPC trên bản đồ để mở lời thoại/menu chính. |
| **`32`** | `580–586` | `short npcId`, `byte select` | `MenuController.gI().doSelectMenu(player, npcId, select)` | Chọn 1 dòng tùy chọn (`select`) trong Menu của NPC. |
| **`22`** | `457–462` | `byte unused`, `byte select` | `NpcManager.getNpc(ConstNpc.DAU_THAN).confirmMenu(player, select)` | Xác nhận menu Cây Đậu Thần ở nhà. |
| **`-34`** | `216–227` | `byte action`:<br>• `1`: Mở menu cây đậu<br>• `2`: Tải thông tin cây đậu | • `1`: `player.magicTree.openMenuTree()`<br>• `2`: `player.magicTree.loadMagicTree()` | Tương tác trực tiếp với Cây Đậu Thần (Thu hoạch, Nâng cấp, Kết hạt). |
| **`-125`** | `206–210` | `byte length`, vòng lặp `length` lần: `UTF text[i]` *(Đọc trong `Input.doInput`)* | `Input.gI().doInput(player, _msg)` | Xử lý dữ liệu người chơi nhập vào hộp thoại Input (Nhập Giftcode, Đổi tên, Nạp/Đổi vàng, Gọi Bot Admin...). |
| **`44`** | `571–579` | `UTF text` | `Command.gI().chat(player, text)` | Chat thường trong khu vực (`Zone`) kiêm **Xử lý Lệnh Admin (GM Commands)** nếu người chơi là Admin. Chặn khi đang giao dịch. |
| **`-71`** | `324–332` | `UTF text` | `ChatGlobalService.gI().chat(player, text)` | Chat Thế Giới (Kênh Thế Giới tốn ngọc). Chặn khi đang giao dịch. |
| **`-72`** | `239–243` | `int playerId`, `UTF text` *(Đọc trong `FriendAndEnemyService.chatPrivate`)* | `FriendAndEnemyService.gI().chatPrivate(player, _msg)` | Nhắn tin riêng giữa 2 người chơi (Có sẵn cooldown 5.000ms). |
| **`-80`** | `244–248` | `byte action` *(Đọc trong `controllerFriend`)*:<br>• `0`: Mở danh sách bạn bè<br>• `1`: `int playerId` (Kết bạn)<br>• `2`: `int playerId` (Xóa bạn) | `FriendAndEnemyService.gI().controllerFriend(player, _msg)` | Quản lý danh sách Bạn bè. |
| **`-99`** | `228–232` | `byte action` *(Đọc trong `controllerEnemy`)*:<br>• `0`: Mở danh sách kẻ thù<br>• `1`: `int enemyId` (Báo thù/Dịch chuyển tới kẻ thù)<br>• `2`: `int enemyId` (Xóa kẻ thù) | `FriendAndEnemyService.gI().controllerEnemy(player, _msg)` | Quản lý danh sách Kẻ thù. |
| **`-79`** | `333–337` | `int playerId` | `Service.gI().getPlayerMenu(player, playerId)` | Xem thông tin menu tương tác khi bấm vào người chơi khác. |
| **`-59`** | `249–257` | `byte action`, `byte type`, `int playerId` *(Đọc trong `PVPService.controllerThachDau`)* | `PVPService.gI().controllerThachDau(player, _msg)` | Mời thách đấu hoặc chấp nhận thách đấu (PvP cược vàng). Chặn nếu bật `baovetaikhoan`. |
| **`-46`** | `480–484` | `byte action` (`1..4`), `[nếu 2 hoặc 4] byte imgId, UTF nameOrSlogan` | `ClanService.gI().getClan(player, _msg)` | Tạo Bang hội mới hoặc sửa biểu tượng/khẩu hiệu Bang hội. |
| **`-47`** | `510–514` | `UTF clanNameSearch` | `ClanService.gI().sendListClan(player, clanNameSearch)` | Tìm kiếm Bang hội theo tên. |
| **`-49`** | `495–499` | `int clanMessageId`, `byte action` (`0`: Đồng ý, `1`: Từ chối) | `ClanService.gI().joinClan(player, _msg)` | Duyệt hoặc từ chối đơn xin vào Bang hội. |
| **`-50`** | `500–504` | `int clanId` | `ClanService.gI().sendListMemberClan(player, clanId)` | Xem danh sách thành viên trong Bang hội. |
| **`-51`** | `485–489` | `byte type`:<br>• `0` (Chat bang): `UTF text`<br>• `1` (Xin đậu): *(Trống)*<br>• `2` (Xin vào bang): `int clanId` | `ClanService.gI().clanMessage(player, _msg)` | Chat kênh Bang hội, Xin đậu thần hoặc Gửi đơn xin vào Bang. |
| **`-54`** | `490–494` | `int clanMessageId` | `ClanService.gI().clanDonate(player, _msg)` | Cho đậu thần thành viên khác trong Bang. |
| **`-55`** | `515–519` | *(Không có payload)* | `ClanService.gI().showMenuLeaveClan(player)` | Mở hộp thoại xác nhận rời Bang hội. |
| **`-56`** | `505–509` | `int playerId`, `byte role` (`-1`: Kick, `0`: Phong PC, `1`: Phong Phó bang, `2`: Cắt chức) | `ClanService.gI().clanRemote(player, _msg)` | Quản trị nhân sự trong Bang hội (chỉ Bang chủ/Phó bang). |
| **`-57`** | `520–524` | `byte action` (`0`: Gửi lời mời, `1`: Đồng ý vào bang), `int targetOrClanId` | `ClanService.gI().clanInvite(player, _msg)` | Mời người chơi khác vào Bang hội. |

---

## 7. Nhóm 6: Tính Năng Bổ Trợ, Sưu Tầm, Giải Đấu & Các Lệnh Bỏ Trống (Misc & Stubs)

| Mã lệnh (`cmd`) | Dòng trong `Controller` | Cấu trúc Payload đọc từ Client (`_msg.reader()`) | Dịch vụ xử lý & Hành vi Hệ thống | Điều kiện & Ghi chú Kỹ thuật |
| :--- | :--- | :--- | :--- | :--- |
| **`127`** | `138–167` | `byte actionRadar`:<br>• `0`: Mở danh sách Sách Sưu Tầm (Radar)<br>• `1`: `short idCard` (Bật/tắt sử dụng thẻ sưu tầm) | • `0`: `RadarService.gI().sendRadar(player, player.Cards)`<br>• `1`: Đảo trạng thái `card.Used` (`0 <-> 1`), gọi `RadarService.Radar1()` và cập nhật lại chỉ số `Service.point(player)`. | Kiểm tra chỉ cho phép tối đa 1 thẻ có `Used == 1` tại một thời điểm và `card.Level > 0`. |
| **`-76`** | `692–696` | `byte indexAchievement` | `AchievementService.gI().confirmAchievement(player, indexAchievement)` | Nhận thưởng mốc Thành tựu. |
| **`-104`** | `651–655` | `int maBaoVe` | `Service.gI().mabaove(player, maBaoVe)` | Nhập hoặc kích hoạt Mã Bảo Vệ tài khoản. |
| **`-118`** | `656–676` | `int targetId` | Phụ thuộc `player.idMark.getMenuType()`:<br>• `0, 1, 2`: Thách đấu Giải Siêu Hạng (`SuperRankService.gI().competing(player, targetId)`)<br>• Khác: Nếu `player.isAdmin()` -> Dịch chuyển tới Boss có ID `targetId` (`BossManager.gI().getBoss(_id)`). | Kết hợp cả tính năng đấu Siêu Hạng và công cụ Admin dịch chuyển tìm Boss. |
| **`-78`** | `684–686` | `int second` | *(Chỉ đọc `readInt()` rồi bỏ qua)* | Gói tin kiểm tra đứng yên/di chuyển từ Client (`checkMMove`). |
| **`126`**, **`-114`**, **`27`** | `682–691` | *(Không đọc payload)* | *(Bỏ trống - No-op)* | Các gói tin phụ từ Client Android (`androidPack2`, `RequestPean`, `menuid`) hiện không xử lý trên Server. |

---

## 8. Đề Xuất Cấu Hình Token Bucket & Kiến Trúc Tách Handler

Khi bạn áp dụng **Command Pattern** và **Token Bucket Rate Limiter** cho `Controller.java`, hãy chia 58 mã lệnh trên thành **4 nhóm cấu hình giới hạn tốc độ (Rate Limit Tiers)** như sau:

1. **Tier 1 — Real-time Movement (`cmd = -7`)**:
   * **Dung lượng rổ (Burst Capacity)**: `25 tokens` | **Tốc độ hồi (Refill Rate)**: `15 tokens/giây`.
   * *Lý do*: Gói tin di chuyển gửi liên tục khi người chơi bay/chạy, cần độ trễ thấp nhất nhưng phải chặn spam làm lag `Zone`.
2. **Tier 2 — Combat & Action (`cmd = 54, -60, -45, 34, -20`)**:
   * **Dung lượng rổ (Burst Capacity)**: `12 tokens` | **Tốc độ hồi (Refill Rate)**: `8 tokens/giây`.
   * *Lý do*: Ngăn chặn các bản Mod Auto-Attack hoặc Auto-Pick gửi hàng trăm gói tin đánh quái/nhặt đồ mỗi giây.
3. **Tier 3 — Economy, Shop, Trade & Inventory (`cmd = -100, -86, 6, 7, -40, -43, -81, -127, -125`)**:
   * **Dung lượng rổ (Burst Capacity)**: `4 tokens` | **Tốc độ hồi (Refill Rate)**: `2 tokens/giây`.
   * *Lý do*: Tuyệt đối không có người chơi thật nào mua đồ, ký gửi hoặc giao dịch nhanh hơn 2 lần/giây. Giới hạn chặt nhóm này giúp triệt tiêu 99% các cuộc tấn công spam gói tin tìm lỗ hổng dup đồ.
4. **Tier 4 — Auth, Database & Heavy Asset I/O (`cmd = -29, -28, -74, -47, -50`)**:
   * **Dung lượng rổ (Burst Capacity)**: `3 tokens` | **Tốc độ hồi (Refill Rate)**: `1 token/giây`.
   * *Lý do*: Bảo vệ MySQL (`HikariCP`) và ổ cứng khỏi bị cạn kiệt tài nguyên do truy vấn tìm kiếm Bang hội hoặc tải Resource liên tục.
