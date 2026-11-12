# DB_JSON_SCHEMA.md — Đặc Tả Cơ Sở Dữ Liệu & Ánh Xạ Mảng JSON (NgocRongBaKhi)

Tài liệu này là **bản đồ tra cứu chuẩn xác 100%** được trích xuất trực tiếp từ **`nrobakhi.sql`** (43 bảng), **`PlayerDAO.java`** (hàm `createNewPlayer` & `updatePlayer`), **`MrBlue.java`** (hàm `login` & `loadPlayer`) và **`ClanService.java` / `Manager.java`**.

Mục đích: Giúp Lập trình viên và AI Agent **không bao giờ phải đọc lại 2.500 dòng code của `PlayerDAO.java` và `MrBlue.java` hay quét lại file `nrobakhi.sql`** mỗi khi viết tính năng mới trên Server Java hoặc Web PHP (`WebTeamobi`).

---

## 1. Tổng Quan 43 Bảng Trong `nrobakhi.sql`

### 1.1. Nhóm Bảng Dữ Liệu Động (Runtime / Transactional Tables)
1. **`account`**: Tài khoản đăng nhập, số dư tiền tệ Web/Server (`vnd`, `tongnap`, `thoi_vang`, `vang`, `luotquay`, `event_point`, `vip`), trạng thái khóa (`ban`), kích hoạt (`active`), quyền quản trị (`is_admin`, `admin`), thời gian đăng nhập/đăng xuất (`last_time_login`, `last_time_logout`).
2. **`player`**: Dữ liệu toàn diện của nhân vật (62 cột), trong đó **39 cột lưu dưới dạng chuỗi JSON Array/Object** (chi tiết từng index ở Phần 2).
3. **`clan`**: Bang hội (`id`, `NAME`, `NAME_2`, `slogan`, `img_id`, `power_point`, `max_member`, `clan_point`, `LEVEL`, `members` [JSON], `tops` [JSON], `thanhTichBDKB` [JSON], `thongTinLeader` [JSON]).
4. **`shop_ky_gui`**: Chợ ký gửi vật phẩm (`id`, `player_id`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `itemOption` [JSON], `isUpTop`, `isBuy`).
5. **`super_rank`**: Đấu trường Siêu Hạng (`id`, `player_id`, `name`, `rank`, `last_pk_time`, `last_reward_time`, `ticket`, `win`, `lose`, `history` [JSON], `info` [JSON]).
6. **`history_transaction`**: Lịch sử giao dịch giữa 2 người chơi (`player_1`, `player_2`, `item_player_1`, `item_player_2`, `bag_1_before_tran`, `bag_2_before_tran`, `bag_1_after_tran`, `bag_2_after_tran`, `time_tran`).
7. **`giftcode`** & **`giftcode_reload_request`**: Mã quà tặng (`code`, `count_left`, `detail` [JSON], `datecreate`, `expired`) và bảng tín hiệu yêu cầu Server nạp lại Giftcode từ Web Admin.
8. **`shop_reload_request`**: Bảng tín hiệu yêu cầu Server nạp lại dữ liệu Cửa hàng (`item_shop`) từ Web Admin.
9. **Nhóm bảng Web & Nạp tiền (`WebTeamobi`)**: `posts`, `comments`, `phongchat`, `napthe`, `payments`, `bank_transfers`, `adminpanel`, `settings`.

### 1.2. Nhóm Bảng Cấu Hình Tĩnh (Static Template Tables — Nạp vào `Manager.java` lúc bật Server)
* **Vật phẩm & Cửa hàng**: `item_template`, `item_option_template`, `shop`, `tab_shop`, `item_shop`, `item_shop_option`, `flag_bag`, `head_avatar`, `part`, `data_badges`, `radar`.
* **Bản đồ, Quái & NPC**: `map_template`, `mob_template`, `npc_template`, `bg_item_template`, `array_head_2_frames`, `img_by_name`.
* **Kỹ năng, Nội tại & Nhiệm vụ**: `skill_template`, `intrinsic`, `task_main_template`, `task_sub_template`, `side_task_template`, `clan_task_template`, `achievement_template`, `task_badges_template`, `notify`.

---

## 2. Đặc Tả Chi Tiết Bảng `account`

| Tên cột | Kiểu dữ liệu | Mặc định | Ánh xạ trong `MySession` (`MrBlue.login`) | Ý nghĩa nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `int(11)` | `NOT NULL` | `session.userId` | Khóa chính tài khoản (liên kết với `player.account_id`). |
| `username` | `varchar(20)` | `NOT NULL` | `session.uu` | Tên đăng nhập. |
| `password` | `varchar(100)` | `NOT NULL` | `session.pp` | Mật khẩu (Hiện tại Java kiểm tra plaintext, Web hỗ trợ cả plaintext & bcrypt). |
| `ban` | `tinyint(1)` | `0` | `rs.getBoolean("ban")` | `1` = Đang bị khóa tài khoản. |
| `is_admin` | `tinyint(1)` | `0` | `session.isAdmin` | `1` = Tài khoản Admin (mở lệnh GM trong `Command.java` và trang `/admin/`). |
| `active` | `int(11)` | `1` | `session.actived` | `1` = Đã mở thành viên (được phép giao dịch `Trade` và đổi vàng). |
| `vnd` | `int(11)` | `0` | `session.vnd` | Số dư tiền mặt (VNĐ) dùng để mua thành viên hoặc đổi thỏi vàng trong game. |
| `tongnap` | `int(11)` | `0` | `session.tongnap` | Tổng tiền đã nạp tích lũy (dùng để xét thành tựu nạp lần đầu & mốc nạp). |
| `thoi_vang` | `int(11)` | `0` | `session.goldBar` | Số thỏi vàng lưu ở cấp tài khoản. |
| `vang` | `bigint(20)` | `0` | `session.gold` | Số vàng lưu ở cấp tài khoản. |
| `luotquay` | `int(11)` | `0` | `session.luotquay` | Lượt quay vòng quay may mắn trên tài khoản. |
| `event_point` | `int(11)` | `0` | `session.eventPoint` | Điểm sự kiện cấp tài khoản. |
| `bd_player` | `double` | `1` | `session.bdPlayer` | Hệ số nhân/biến đổi của tài khoản. |
| `vip` | `int(11)` | `0` | `session.vip` | Cấp VIP của tài khoản. |
| `last_time_login` | `timestamp` | `'2002-07-30...'` | `lastTimeLogin` | Thời điểm đăng nhập gần nhất (dùng trong `PlayerDAO.checkLogout` chống kẹt acc). |
| `last_time_logout` | `timestamp` | `'2002-07-30...'` | `session.lastTimeLogout` | Thời điểm đăng xuất gần nhất. |

---

## 3. Đặc Tả Bảng `player` & Chỉ Mục Mảng JSON (Positional JSON Arrays)

### 3.1. Các Cột Nguyên Thủy (Scalar Columns) trong bảng `player`
* `id` (`int`): ID nhân vật (`player.id`). Lưu ý: Đệ tử có `pet.id = -player.id`.
* `account_id` (`int`): Liên kết tới `account.id`.
* `name` (`varchar(20)`): Tên nhân vật (5–10 ký tự, chữ thường).
* `head` (`int`): ID ngoại hình đầu mặc định (`player.head`).
* `gender` (`int`): Hành tinh (`0` = Trái Đất, `1` = Namec, `2` = Xayda).
* `have_tennis_space_ship` (`tinyint(1)`): `1` = Có tàu vũ trụ Tennis.
* `clan_id` (`int`): ID Bang hội (`-1` nếu không có bang).
* `lasttimepkcommeson` (`bigint`): `player.lastPkCommesonTime` — Thời gian PK thuốc Commeson.
* `doanhtrai` (`bigint`): `player.lastTimeJoinDT` — Thời gian vào Doanh Trại Độc Nhãn gần nhất.
* `rongxuong` (`bigint`): `player.lastTimeShenronAppeared` — Thời gian gọi Rồng Xương gần nhất.
* `rank` (`int`): `player.superRank.rank` — Thứ hạng Siêu Hạng.
* `event_point` (`int`): `player.event.getEventPoint()` — Điểm sự kiện.
* `point_sukien`, `point_sukien1`, `point_sukien2` (`int`): Các cột điểm đua Top sự kiện.
* `thachdauwhis` (`int`): `player.thachdauwhis` — Cấp độ thách đấu Whis (Đua Top Whis).
* `point_maydam` (`int`), `total_damage_maydam` (`bigint`): Điểm và tổng sát thương sự kiện Máy Đấm.
* `firstTimeLogin` (`timestamp`): Thời điểm đăng nhập lần đầu.
* `notify` (`text`): Thông báo riêng của nhân vật (`player.notify`).

---

### 3.2. Cấu Trúc Chi Tiết Từng Cột JSON Trong Bảng `player`

#### (1) `data_inventory` — Tiền tệ trong hành trang
Định dạng: `[gold, gem, ruby, coupon, event]`
* **`[0]` (`long`)**: `player.inventory.gold` — Vàng hiện có (Giới hạn bởi `Inventory.LIMIT_GOLD`).
* **`[1]` (`int`)**: `player.inventory.gem` — Ngọc xanh.
* **`[2]` (`int`)**: `player.inventory.ruby` — Hồng ngọc.
* **`[3]` (`int`)**: `player.inventory.coupon` — Điểm Coupon / Điểm tích lũy cửa hàng.
* **`[4]` (`int`)**: `player.inventory.event` — Điểm sự kiện hành trang *(Lưu ý: trong `MrBlue.java:209` đang có điều kiện `if (dataArray.size() >= 5 && false)` nên khi load luôn gán về `0`)*.

#### (2) `data_location` — Vị trí bản đồ
Định dạng: `[mapId, x, y]`
* **`[0]` (`int`)**: `mapId` — ID bản đồ (Nếu thoát game khi đang chết hoặc đang ở map Phó bản/Doanh trại/Sao đen, tự động đưa về nhà `gender + 21`).
* **`[1]` (`int`)**: `player.location.x` — Tọa độ X.
* **`[2]` (`int`)**: `player.location.y` — Tọa độ Y.

#### (3) `data_point` — Chỉ số gốc & Sức mạnh nhân vật (14 phần tử)
Định dạng: `[limitPower, power, tiemNang, stamina, maxStamina, hpg, mpg, dameg, defg, critg, critdragon, nangDong, hp, mp]`
* **`[0]` (`byte`)**: `player.nPoint.limitPower` — Cấp mở giới hạn sức mạnh (`0..N`).
* **`[1]` (`long`)**: `player.nPoint.power` — Sức mạnh hiện tại *(Web Admin `player.php` và `bxh01.php` đọc/sửa trực tiếp ở index `[1]` này)*.
* **`[2]` (`long`)**: `player.nPoint.tiemNang` — Tiềm năng hiện có.
* **`[3]` (`short`)**: `player.nPoint.stamina` — Thể lực hiện tại.
* **`[4]` (`short`)**: `player.nPoint.maxStamina` — Thể lực tối đa (mặc định `1000`).
* **`[5]` (`int`)**: `player.nPoint.hpg` — HP gốc (chưa tính đồ/cải trang).
* **`[6]` (`int`)**: `player.nPoint.mpg` — KI (MP) gốc.
* **`[7]` (`int`)**: `player.nPoint.dameg` — Sức đánh gốc.
* **`[8]` (`int`)**: `player.nPoint.defg` — Giáp gốc.
* **`[9]` (`byte`)**: `player.nPoint.critg` — Chí mạng gốc.
* **`[10]` (`byte`)**: `player.nPoint.critdragon` — Chí mạng cộng thêm từ điều ước Rồng Thần.
* **`[11]` (`int`)**: Năng động (hiện tại luôn lưu `0`).
* **`[12]` (`int`/`long`)**: `player.nPoint.hp` — Máu (HP) hiện tại lúc thoát game.
* **`[13]` (`int`/`long`)**: `player.nPoint.mp` — Năng lượng (KI) hiện tại lúc thoát game.

#### (4) `data_magic_tree` — Cây Đậu Thần (5 phần tử)
Định dạng: `[level, currPeas, isUpgrade, lastTimeHarvest, lastTimeUpgrade]`
* **`[0]` (`byte`)**: `magicTree.level` — Cấp cây đậu (`1..10`).
* **`[1]` (`byte`)**: `magicTree.currPeas` — Số hạt đậu hiện có trên cây.
* **`[2]` (`byte`)**: `magicTree.isUpgrade` — Đang nâng cấp hay không (`1` = true, `0` = false).
* **`[3]` (`long`)**: `magicTree.lastTimeHarvest` — Timestamp lần thu hoạch cuối.
* **`[4]` (`long`)**: `magicTree.lastTimeUpgrade` — Timestamp bắt đầu nâng cấp cây.

#### (5) `items_body`, `items_bag`, `items_box`, `items_box_lucky_round`, `items_daban` — Cấu trúc Lưu Vật Phẩm
Tất cả 5 cột hành trang đều là mảng JSON chứa các chuỗi JSON con (Double-encoded JSON Array strings).
Mỗi phần tử đại diện cho **1 ô (slot) đồ** có cấu trúc 4 phần tử:
`"[tempId, quantity, \"[[optId, param], [optId, param]]\", createTime]"`
* **Slot `[0]` (`short`)**: `item.template.id` — ID mẫu vật phẩm (`-1` nếu ô trống).
* **Slot `[1]` (`int`)**: `item.quantity` — Số lượng (`0` nếu ô trống).
* **Slot `[2]` (`String` JSON Array)**: Danh sách `itemOptions`, mỗi option là mảng 2 phần tử `[optionTemplate.id, param]`.
* **Slot `[3]` (`long`)**: `item.createTime` — Timestamp tạo vật phẩm (ms) *(Lưu ý: `items_box_lucky_round` khi lưu có ghi `createTime`, khi đọc trong `MrBlue` chỉ đọc 3 phần tử đầu)*.
* **Thứ tự ô mặc định của `items_body` (11 ô trang bị nhân vật)**:
  * `0`: Áo | `1`: Quần | `2`: Găng | `3`: Giày | `4`: Rada | `5`: Cải trang | `6`: Giáp tập luyện | `7`: Vệ tinh / Linh thú | `8`: Vật phẩm đeo lưng (Flag/Cánh) | `9`: Chân mệnh / Phụ kiện | `10`: Chibi / Thú cưỡi phụ.

#### (6) `pet` — Toàn bộ dữ liệu Đệ Tử (Mảng 4 phần tử lớn)
Nếu chưa có đệ tử: `"[]"`. Nếu đã có đệ tử, cột `pet` là mảng gồm **4 chuỗi JSON con**: `[petInfo, petPoint, petBody, petSkill]`:
1. **`pet[0]` (`petInfo` - 6 phần tử)**: `[typePet, gender, name, typeFusion, timeLeftFusion, status]`
   * `[0]` (`byte`): `pet.typePet` (`0`: Đệ thường, `1`: Mabư, `2`: Uub, `3`: Kid Beer, `4`: Kid Jiren...).
   * `[1]` (`byte`): `pet.gender` (`0, 1, 2`).
   * `[2]` (`String`): `pet.name`.
   * `[3]` (`byte`): `player.fusion.typeFusion` (Trạng thái hợp thể: Không hợp thể, Lưỡng long nhất thể, Bông tai Porata...).
   * `[4]` (`int`): Thời gian hợp thể còn lại (ms).
   * `[5]` (`byte`): `pet.status` (Trạng thái đệ tử: Đi theo, Bảo vệ, Tấn công, Về nhà).
2. **`pet[1]` (`petPoint` - 12 phần tử)**: `[limitPower, power, tiemNang, stamina, maxStamina, hpg, mpg, dameg, defg, critg, hp, mp]`
   * Chú ý: Khác với `data_point` của Sư phụ (14 phần tử), `petPoint` của Đệ tử chỉ có **12 phần tử** (không có `critdragon` và `nangDong`, nên `hp` nằm ở `[10]` và `mp` nằm ở `[11]`).
3. **`pet[2]` (`petBody`)**: Mảng các ô trang bị của Đệ tử (7 ô đối với đệ thường/Mabư, 9 ô đối với `typePet == 2, 3, 4`), định dạng giống hệt `items_body`.
4. **`pet[3]` (`petSkill`)**: Mảng kỹ năng của Đệ tử (4 hoặc 5 kỹ năng), mỗi kỹ năng là `"[skillId, point, lastTimeUse, currLevel]"`.

#### (7) `skills` & `skills_shortcut` — Kỹ năng nhân vật
* **`skills`**: Mảng các chuỗi kỹ năng, mỗi phần tử có 4 giá trị: `"[templateId, point, lastTimeUseThisSkill, currLevel]"`.
* **`skills_shortcut`**: Mảng 10 số nguyên `[skillId_0, ..., skillId_9]` tương ứng 10 ô phím tắt kỹ năng trên màn hình.

#### (8) `friends` & `enemies` — Bạn bè & Kẻ thù
Mỗi phần tử trong mảng là chuỗi JSON 7 giá trị: `"[id, name, head, body, leg, bag, power]"`.

#### (9) `data_intrinsic` — Nội tại (8 phần tử)
Định dạng: `[intrinsicId, param1, param2, countOpen, isIntrinsic, skillID, cooldown, lastTimeUseSkill]`
* `[0]` (`byte`): ID Nội tại | `[1]` (`short`): Chỉ số 1 | `[2]` (`short`): Chỉ số 2 | `[3]` (`byte`): Số lần đã mở nội tại.
* `[4]` (`boolean`): `effectSkill.isIntrinsic` | `[5]` (`int`): `skillID` | `[6]` (`int`): `cooldown` | `[7]` (`long`): `lastTimeUseSkill`.

#### (10) `data_task`, `data_side_task`, `data_clan_task` — Nhiệm vụ
* **`data_task` (Nhiệm vụ chính tuyến - 4 phần tử)**: `[taskId, subTaskIndex, currentCount, lastTime]`
* **`data_side_task` (Nhiệm vụ hàng ngày Bò Mộng - 6 phần tử)**: `[templateId, receivedTime, count, maxCount, leftTask, level]`
* **`data_clan_task` (Nhiệm vụ Bang hội hàng ngày - 6 phần tử)**: `[templateId, receivedTime, count, maxCount, leftTask, level]`

#### (11) `data_charm` — Bùa Hạt Mít (10 phần tử Timestamp hết hạn ms)
Định dạng: `[tdTriTue, tdManhMe, tdDaTrau, tdOaiHung, tdBatTu, tdDeoDai, tdThuHut, tdDeTu, tdTriTue3, tdTriTue4]`

#### (12) `data_black_ball` — Phần thưởng Ngọc Rồng Sao Đen (7 phần tử cho 1–7 Sao)
Mỗi phần tử `i` (`0..6` tương ứng 1..7 sao) là chuỗi JSON 3 giá trị: `"[timeOutOfDateReward, lastTimeGetReward, quantilyBlackBall]"`.

#### (13) Các Cột JSON Tính Năng Phụ & Sự Kiện Khác
* **`checkNhanQua` (3 phần tử)**: `[luotNhanNgocMienPhi, luotNhanCapsuleBang, lastCheckInIsoString]` (Mặc định: `[1,1,"1970-01-01T00:00:00"]`).
* **`baovetaikhoan` (3 phần tử)**: `[mbv_code, is_enabled_boolean, mbv_time_long]`.
* **`data_mabu_egg` & `data_duahau_egg` (2 phần tử)**: `[lastTimeCreate, timeDone]` (Nếu không có trứng thì là `[]`).
* **`bandokhobau` (2 phần tử)**: `[timesPerDayBDKB, lastTimeJoinBDKB]`.
* **`conduongrandoc` (4 phần tử)**: `[joinCDRD, lastTimeJoinCDRD, talkToThuongDe, talkToThanMeo]`.
* **`nhanthoivang` (2 phần tử)**: `[danhanthoivang_bool, lastRewardGoldBarTime]`.
* **`ruonggo` (5 phần tử)**: `[levelWoodChest, goldChallenge, rubyChallenge, lastTimeRewardWoodChest, lastTimePKDHVT23]`.
* **`sieuthanthuy` (3 phần tử)**: `[winSTT_bool, lastTimeWinSTT, callBossPocolo_bool]`.
* **`vodaisinhtu` (4 phần tử)**: `[haveRewardVDST_bool, thoiVangVoDaiSinhTu, lastTimePKVoDaiSinhTu, timePKVDST]`.
* **`data_luyentap` (10 phần tử)**: `[levelLuyenTap, dangKyTapTuDong_bool, mapIdDangTapTuDong, tnsmLuyenTap, lastTimeOffline, top, time, lastTime, lastTop, lastRewardTime]`.
* **`data_vip` (8 phần tử)**: `[timesPerDayCuuSat, lastTimeCuuSat, nhanDeTuNangVIP, nhanVangNangVIP, nhanSKHVIP, vipLevel, timevip, vipPurchaseCount]`.
* **`LearnSkill` (3 phần tử)**: `[Time, ItemTemplateSkillId, Potential]`.
* **`BoughtSkill`**: Mảng các `int` ID kỹ năng đã mua `[skillId1, skillId2, ...]`.
* **`giftcode`**: Mảng các `String` mã Giftcode nhân vật đã nhập `["CODE1", "CODE2", ...]`.
* **`data_achievement`**: Mảng các chuỗi `"[completedCount_long, isReceived_boolean]"` theo thứ tự bảng `achievement_template`.
* **`data_event` (10 phần tử)**: `[eventPointType1..6, checkDailyReward, checkTopReward1..3]`.
* **`data_card` (Sách sưu tầm Radar)**: Mảng các JSON Object `{"id":short, "amount":byte, "max":byte, "level":byte, "option":[{"id":int, "param":int, "active":byte}], "used":byte}`.
* **`dataBadges`**: Mảng các JSON Object `{"idBadGes":int, "timeofUseBadges":long, "isUse":boolean}`.
* **`dataTaskBadges`**: Mảng các JSON Object `{"id":int, "count":int, "countMax":int, "idBadgesReward":int}`.
* **`dailyGift`**: Mảng các JSON Object `{"id":byte, "daNhan":boolean}`.
* **`nhiem_vu_kol` (Chuẩn Gson POJO `KOLProgressData`)**: JSON Object `{"kolQuestStage":int, "kolVIPQuestStage":int, "destronGas70CompletionCount":int, "martialArtsTournamentWins":int, "dailySuperHardQuestCompletionCount":int, "bossBabyDefeatParticipationCount":int, "monsterKillCountAutoTrain":long}`.

---

## 4. Phát Hiện 4 Lỗi Lệch Index (Data Corruption Bugs) Giữa `PlayerDAO.java` và `MrBlue.java`

Qua việc đối chiếu từng dòng giữa hàm lưu (`PlayerDAO.updatePlayer`) và hàm đọc (`MrBlue.loadPlayer`), có **4 lỗi lệch vị trí phần tử mảng JSON** đang tồn tại trong source mà bạn cần biết để sửa hoặc tránh đạp phải:

1. **Lệch toàn bộ Index từ phần tử thứ 12 trở đi trong `data_item_time`!**
   * Khi lưu (`PlayerDAO.java` dòng 604–628):
     * `[12]` = `isUseCoBonLa` (Cỏ bốn lá)
     * `[13]` = `isUseKhoBauX2` (Kho báu X2)
     * `[14]` = `isUseBuaSanta` (Bùa Santa)
     * `[15]` = `isEatMeal` (Thức ăn)
     * `[16]` = `iconMeal`
     * `[17]` = `isUseTDLT` (Tự động luyện tập)
     * ...
     * `[25..29]` = `NCD, NuocMia1, NuocMia2, NuocMia3, Kilis`.
   * Nhưng khi đọc (`MrBlue.java` dòng 592–644):
     * Ai đó đã thêm `isUseCoBonLa` và `isUseBuaSanta` vào giữa `PlayerDAO` nhưng **quên cập nhật thứ tự đọc trong `MrBlue.java`**! Trong `MrBlue.java`, `[12]` lại gán cho `timeKhoBauX2`, `[13]` bỏ trống, `[14]` gán cho `timeMeal`, `[15]` gán cho `iconMeal`, `[16]` gán cho `timeUseTDLT`... và ở dòng 643: `timeNuocMia3` lại đọc nhầm `dataArray.get(28)` thay vì `get(29)`.
   * **Hậu quả**: Khi người chơi dùng Tự Động Luyện Tập (TDLT), Thức ăn hoặc Cỏ 4 lá rồi thoát game vào lại, thời gian TDLT sẽ bị đọc nhầm sang `iconMeal`, làm mất hiệu ứng hoặc sai lệch hoàn toàn thời gian ItemTime!

2. **Lỗi cắt cụt mảng `data_item_event` khi lưu**:
   * Trong `MrBlue.java` (dòng 996–1013), hàm đọc `data_item_event` đọc tới **16 phần tử** (`[0..15]`, bao gồm cả Bánh Quy, Kẹo Người Tuyết, Cá Tuyết, Chuông Đồng, Kẹo Đường).
   * Nhưng trong `PlayerDAO.java` (dòng 829–836), hàm lưu chỉ ghi đúng **6 phần tử đầu (`[0..5]`)** (`TVGS`, `HH`, `BN`). Vì vậy 5 vật phẩm sự kiện Giáng Sinh (`[6..15]`) sẽ luôn rơi vào `IndexOutOfBoundsException` khi đăng nhập và bị reset toàn bộ về `0`!

3. **Lỗi đọc sai tên cột và sai index trong `conduongrandoc` & `masterDoesNotAttack`**:
   * Trong `MrBlue.java` (dòng 931): `player.talkToThanMeo = Boolean.parseBoolean(dataArray.get(2).toString());` — Đọc nhầm `get(2)` hai lần thay vì `get(3)`.
   * Trong `MrBlue.java` (dòng 945): Đọc `rs.getBoolean("masterDoesAttack")` nhưng tên cột thực tế trong `nrobakhi.sql` và `PlayerDAO.java` là **`masterDoesNotAttack`** (gây `SQLException` ngầm mỗi lần đăng nhập và luôn trả về `false`).

4. **Đoạn code thừa trong `MrBlue.java` (dòng 715–720)**:
   * Ngay sau khi đọc `data_side_task`, trong `MrBlue.java` có đoạn `dataArray.add(player.event.luotNhanNgocMienPhi)... String checkNhanQua = dataArray.toJSONString(); dataArray.clear();` bị copy-paste nhầm từ `PlayerDAO` sang mà không sử dụng.

---

## 5. Cấu Trúc JSON Trong Bảng `clan` và `shop_ky_gui`

* **Bảng `clan`**:
  * `members`: Mảng các chuỗi JSON Object: `'["{\"id\":1,\"name\":\"...\",\"head\":102,\"body\":...,\"leg\":...,\"role\":0,\"donate\":0,\"receive_donate\":0,\"member_point\":0,\"clan_point\":0,\"join_time\":...,\"ask_pea_time\":...,\"power\":...}"]'`.
  * `thanhTichBDKB`: `[levelDoneBanDoKhoBau, thoiGianHoanThanhBDKB]`.
* **Bảng `shop_ky_gui`**:
  * `itemOption`: Mảng các JSON Object hoặc mảng `[id, param]` đại diện cho `List<ItemOption>` của vật phẩm ký gửi.
* **Bảng `giftcode`**:
  * `detail`: Chuỗi JSON Array chứa danh sách phần thưởng của Giftcode: `[{"temp_id":457, "quantity":10, "options":[{"id":30, "param":1}]}]`.
