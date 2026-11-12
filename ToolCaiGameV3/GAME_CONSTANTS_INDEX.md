# GAME_CONSTANTS_INDEX.md — Toàn Bộ Từ Điển Hằng Số ID Vật Phẩm, Option, Map, NPC & Boss (NgocRongBaKhi)

> **Kho Dữ Liệu Tra Cứu Toàn Diện (Zero-Context Constant Dictionary)**
> Tài liệu này lưu trữ **100% đầy đủ toàn bộ hằng số định danh** trong source code và database của server **NgocRongBaKhi**.
> Khi AI Agent hoặc Lập trình viên cần kiểm tra bất kỳ ID nào, **chỉ cần tra cứu tại tệp này**, tuyệt đối **không cần mở lại** các file Java hàng ngàn dòng (`ConstTask.java`, `ConstItem.java`, `BossesData.java`, `ConstMap.java`, `ConstNpc.java`, `NPoint.java`, `nrobakhi.sql`).

### 📊 Tổng Kê Số Lượng Hằng Số Được Chỉ Mục:
- **ItemOption Templates**: `251/251` options (Đầy đủ từ ID `0` đến `250`).
- **Boss IDs**: `150/150` Boss (Đầy đủ từ ID `-4` đến `-926`).
- **Bản đồ (Maps & Type Maps)**: `158` hằng số (Đầy đủ Map `0` đến `163`).
- **NPC IDs & Menu Constants**: `78` NPC IDs + `164` Menu IDs.
- **Item Templates (`ConstItem.java`)**: `1225` hằng số vật phẩm.

---

## 1. Toàn Bộ 251 Chỉ Số Trang Bị (`item_option_template` ID 0 .. 250)

| Option ID | Tên Hiển Thị Trong Game | Phân Loại / Cơ Chế Tính Toán (`NPoint.java`) |
| :---: | :--- | :--- |
| **`0`** | Tấn công+# | Tấn công gốc cộng thẳng (`dame += param`) |
| **`1`** | Thời gian sử dụng # phút | Thời gian sử dụng tính bằng phút |
| **`2`** | HP, KI+#000 | HP, KI cộng hàng nghìn (`hp += param*1000, mp += param*1000`) |
| **`3`** | Vô hiệu và biến #% sát thương chưởng thành KI | Vô hiệu hóa sát thương chưởng và chuyển thành KI |
| **`4`** | Hồi phục #% KI khi bị đánh | Hồi phục % KI khi bị mục tiêu tấn công |
| **`5`** | +#% sức đánh chí mạng | +% Sức đánh chí mạng (`critDame += param`) |
| **`6`** | HP+# | HP gốc cộng thẳng (`hp += param`) |
| **`7`** | KI+# | KI gốc cộng thẳng (`mp += param`) |
| **`8`** | Hút #% HP, KI xung quanh mỗi 5 giây | Hút % HP, KI xung quanh mỗi 5 giây (Hào quang diện rộng) |
| **`9`** | Hiệu lực trong # phút | Hiệu lực vật phẩm thời gian |
| **`10`** | Sát thương chuẩn #% | Chỉ số option trong game |
| **`11`** | Công đức +# | Chỉ số option trong game |
| **`12`** | Số lần sử dụng còn lại +# | Chỉ số option trong game |
| **`13`** | Số yêu quái đã hạ +# | Chỉ số option trong game |
| **`14`** | Chí mạng+#% | +% Chí mạng (`crit += param`) |
| **`15`** | Phản đòn cận chiến+# | Chỉ số option trong game |
| **`16`** | Tốc độ di chuyển+#% | Tốc độ di chuyển nhân vật |
| **`17`** | Né đòn: +# | Chỉ số option trong game |
| **`18`** | Chính xác: +#% | Độ chuẩn xác khi ra đòn |
| **`19`** | Tấn công+#% khi đánh quái | +% Sát thương khi tấn công quái |
| **`20`** | PIN # | Chỉ số option trong game |
| **`21`** | Yêu cầu sức mạnh # tỉ | Chỉ số option trong game |
| **`22`** | HP+#K | HP cộng hàng nghìn |
| **`23`** | KI+#K | MP cộng hàng nghìn |
| **`24`** | Làm tăng trọng lực, gây chậm mọi người xung quanh | Chỉ số option trong game |
| **`25`** | Tàng hình mỗi 5 giây | Chỉ số option trong game |
| **`26`** | Hóa đá mọi người xung quanh mỗi 30 giây | Chỉ số option trong game |
| **`27`** | +# HP/30s | +HP hồi phục thụ động mỗi 30 giây |
| **`28`** | +# KI/30s | +KI hồi phục thụ động mỗi 30 giây |
| **`29`** | Biến Sôcôla mọi người xung quanh mỗi 30 giây | Chỉ số option trong game |
| **`30`** | Không thể giao dịch | **KHÔNG THỂ GIAO DỊCH** (Chặn tại Trade và Ký gửi) |
| **`31`** | Số lượng # | Chỉ số option trong game |
| **`32`** | Không bị hóa Xương | Chỉ số option trong game |
| **`33`** | Dịch chuyển tức thời | Dịch chuyển tức thời tới mục tiêu |
| **`34`** | Tinh ấn | Chỉ số option trong game |
| **`35`** | Nguyệt ấn | Chỉ số option trong game |
| **`36`** | Nhật ấn | Chỉ số option trong game |
| **`37`** | Số lần đã ký gửi # | Chỉ số option trong game |
| **`38`** | Chỉ có tác dụng khi hợp thể | Chỉ số option trong game |
| **`39`** | <Cập nhật bản mới để xem> | Chỉ số option trong game |
| **`40`** | Siêu cải trang # đá ngũ sắc | Chỉ số option trong game |
| **`41`** | Chỉ số thưởng +#: | Chỉ số option trong game |
| **`42`** | Tấn công+#% lên quái bay | Chỉ số option trong game |
| **`43`** | Tấn công+#% lên quái khỉ | Chỉ số option trong game |
| **`44`** | Tấn công+#% lên quái mặt đất | Chỉ số option trong game |
| **`45`** | Tấn công+#% lên tộc Namếc | Chỉ số option trong game |
| **`46`** | Tấn công+#% lên tộc Trái đất | Chỉ số option trong game |
| **`47`** | Giáp+# | Giáp gốc cộng thẳng (`def += param`) |
| **`48`** | HP, KI+# | Cộng đồng thời cả HP và KI |
| **`49`** | Tấn công+#% | +% Sát thương tấn công |
| **`50`** | Sức đánh+#% | +% Sức đánh (`tlDame.add(param)`) |
| **`51`** | Sôn Gô Ku ss# | Chỉ số option trong game |
| **`52`** | Ca Đic siêu ss# | Chỉ số option trong game |
| **`53`** | Broly | Chỉ số option trong game |
| **`54`** | Mr. Santa | Chỉ số option trong game |
| **`55`** | Broly ss# | Chỉ số option trong game |
| **`56`** | Ca Lích | Chỉ số option trong game |
| **`57`** | Thên Xin Hăng | Chỉ số option trong game |
| **`58`** | Pic | Chỉ số option trong game |
| **`59`** | Siêu na mếc # | Chỉ số option trong game |
| **`60`** | Sôn Gô Tên | Chỉ số option trong game |
| **`61`** | Sôn Gô Ku | Chỉ số option trong game |
| **`62`** | Phục hồi thể lực #% | Chỉ số option trong game |
| **`63`** | Còn lại # ngày | Chỉ số option trong game |
| **`64`** | Còn lại # giờ | Chỉ số option trong game |
| **`65`** | Còn lại # phút | Chỉ số option trong game |
| **`66`** | Chưa có | Chỉ số option trong game |
| **`67`** | Dùng nâng cấp găng tay | Chỉ số option trong game |
| **`68`** | Dùng để nâng cấp áo | Chỉ số option trong game |
| **`69`** | Dùng để nâng cấp quần | Chỉ số option trong game |
| **`70`** | Dùng để nâng cấp giày | Chỉ số option trong game |
| **`71`** | Dùng để nâng cấp rađa | Chỉ số option trong game |
| **`72`** | Cấp # | Cấp độ (Cấp rương gỗ ĐHVT, cấp Bông tai Porata) |
| **`73`** |  | Chỉ số option trong game |
| **`74`** | Dùng để ép thành đá | Chỉ số option trong game |
| **`75`** | Dùng để làm phép | Chỉ số option trong game |
| **`76`** | Vip | Chỉ số option trong game |
| **`77`** | HP+#% | +% HP gốc (`tlHp.add(param)`) |
| **`78`** | Sức hủy diệt+#% | Chỉ số option trong game |
| **`79`** | Đệ tử #% sức đánh | Chỉ số option trong game |
| **`80`** | HP+#%/30s | +% HP hồi phục mỗi 30 giây |
| **`81`** | KI+#%/30s | +% KI hồi phục mỗi 30 giây |
| **`82`** | Không bị quái chủ động đánh và giảm 20% sát thương khi bị đánh | Chỉ số option trong game |
| **`83`** | Tăng 20% sức mạnh và tiềm năng nhận được khi đánh quái | Chỉ số option trong game |
| **`84`** | Dùng để bay không tốn KI | Chỉ số option trong game |
| **`85`** | Dùng để bay và phục hồi KI | Chỉ số option trong game |
| **`86`** | Ký gửi vàng | Cho phép ký gửi bán bằng Vàng trên Chợ |
| **`87`** | Ký gửi ngọc | Cho phép ký gửi bán bằng Ngọc trên Chợ |
| **`88`** | Cộng #% Tiềm năng và sức mạnh nhận được khi đánh quái | +% Kinh nghiệm (Exp) khi đánh quái |
| **`89`** | Dùng để bay và phục hồi HP, KI | Chỉ số option trong game |
| **`90`** | Tặng cho người khác (bỏ ra đất) sẽ được nhận may mắn | Chỉ số option trong game |
| **`91`** | Mở ra để nhận may mắn | Chỉ số option trong game |
| **`92`** | Chúc tết bang hội và mọi người kèm theo pháo hoa | Chỉ số option trong game |
| **`93`** | Hạn sử dụng # ngày | **HẠN SỬ DỤNG** tính theo ngày (`param` ngày) |
| **`94`** | Giảm #% sát thương | +% Giáp phòng thủ (`tlDef.add(param)`) |
| **`95`** | Biến #% tấn công thành HP | Biến % tấn công thành HP (Hút máu) |
| **`96`** | Biến #% tấn công thành KI | Biến % tấn công thành MP (Hút KI) |
| **`97`** | Phản #% sát thương | Phản % sát thương khi nhận đòn (`tlDamePhan += param`) |
| **`98`** | Xuyên giáp #% chưởng | Xuyên giáp chưởng đối thủ |
| **`99`** | Xuyên giáp #% cận chiến | Xuyên giáp đấm cận chiến |
| **`100`** | +#% vàng từ quái | +% Vàng rơi ra khi tiêu diệt quái |
| **`101`** | +#% tiềm năng, sức mạnh | +% Tiềm năng, Sức mạnh nhận được |
| **`102`** | # Sao Pha Lê | Số sao pha lê đã ép / Bậc nâng cấp |
| **`103`** | KI +#% | +% KI gốc (`tlMp.add(param)`) |
| **`104`** | Biến #% tấn công quái thành HP | Biến % tấn công quái thành HP |
| **`105`** | Vô hình khi không đánh quái và boss | Vô hình khi không đánh quái và boss |
| **`106`** | Không ảnh hưởng bởi cái lạnh | Tự động hồi phục % HP, KI mỗi 30 giây |
| **`107`** | # Sao Pha Lê | Chỉ số option trong game |
| **`108`** | #% Né đòn | +% Né đòn (`tlNeDon += param`) |
| **`109`** | Hôi, giảm #% HP | Hôi hám / Quái không chủ động tấn công |
| **`110`** | Dò pha lê | Chỉ số option trong game |
| **`111`** | Phân tâm | Chỉ số option trong game |
| **`112`** | Giảm #% khi mua Avatar hoặc Cải trang | Chỉ số option trong game |
| **`113`** | Ném cho Sói Hẹc Quyn | Chỉ số option trong game |
| **`114`** | +#% TĐ chạy | Tăng tốc độ di chuyển |
| **`115`** | Biến cà rốt | Chỉ số option trong game |
| **`116`** | Kháng TDHS | Kháng Thái Dương Hạ San & Kháng lạnh trên map băng |
| **`117`** | Đẹp +#% SĐ cho mình và người xung quanh | Quyến rũ / Đẹp trai (Tăng sức đánh bản thân & xung quanh) |
| **`118`** | Tới ngay mục tiêu và gây choáng trong # mili giây | Chỉ số option trong game |
| **`119`** | Gây mù xung quanh trong # giây | Chỉ số option trong game |
| **`120`** | Ra đòn sau # giây | Chỉ số option trong game |
| **`121`** | Ru ngủ trong # giây | Chỉ số option trong game |
| **`122`** | Bảo vệ trong # giây | Chỉ số option trong game |
| **`123`** | Trói gô mục tiêu trong # giây | Chỉ số option trong game |
| **`124`** | Tỉnh giấc bị yếu đi -#% sức đánh trong 10 giây | Chỉ số option trong game |
| **`125`** | Tăng và hồi phục #% HP tạm thời cho mình và xung quanh trong 30 giây | Chỉ số option trong game |
| **`126`** | Biến sôcôla làm yếu đi -#% sức đánh trong 30 giây | Chỉ số option trong game |
| **`127`** | Set Thên Xin Hăng | Chỉ số option trong game |
| **`128`** | Set Kirin | Chỉ số option trong game |
| **`129`** | Set Sôngôku | Chỉ số option trong game |
| **`130`** | Set Picolo | Chỉ số option trong game |
| **`131`** | Set Ốc tiêu | Chỉ số option trong game |
| **`132`** | Set Pikkoro Daimao | Chỉ số option trong game |
| **`133`** | Set Kakarot | Chỉ số option trong game |
| **`134`** | Set Ca Đíc | Chỉ số option trong game |
| **`135`** | Set Nappa | Chỉ số option trong game |
| **`136`** | $(5 món +100% sát thương đấm Galick) | Chỉ số option trong game |
| **`137`** | $(5 món x5 thời gian hóa khỉ) | Chỉ số option trong game |
| **`138`** | $(5 món +80% HP) | Chỉ số option trong game |
| **`139`** | $(5 món x2 thời gian chói mắt) | Chỉ số option trong game |
| **`140`** | $(5 món +100% sát thương Quả Cầu Kênh Khi) | Chỉ số option trong game |
| **`141`** | $(5 món +100% sát thương Kamejoko) | Chỉ số option trong game |
| **`142`** | $(5 món +100% sát thương Masenkosappo) | Chỉ số option trong game |
| **`143`** | $(5 món +100% sát thương Liên Hoàn) | Chỉ số option trong game |
| **`144`** | $(5 món +100% sát thương và bất tử đệ từ Đẻ Trứng) | Chỉ số option trong game |
| **`145`** | $(Ở gần 1 CT Dr Slum khác loại +20% sức đánh +66% tốc độ chạy) | Chỉ số option trong game |
| **`146`** | $(Ở gần 2 CT Dr Slum khác loại +30% sức đánh +100% tốc độ chạy) | Chỉ số option trong game |
| **`147`** | +#% sức đánh | +% Sức đánh đặc biệt / Miễn nhiễm hiệu ứng độc |
| **`148`** | +#% tốc độ chạy | Tăng tốc độ chạy |
| **`149`** | (Chỉ số tăng khi ở gần CT Hải Tặc khác loại) | Chỉ số option trong game |
| **`150`** | (Chỉ số tăng khi ở gần Android Sát Thủ khác loại) | Chỉ số option trong game |
| **`151`** | $(Ở gần 1 CT nhóm Piláp khác loại +20% sức đánh +66% tốc độ chạy) | Chỉ số option trong game |
| **`152`** | $(Ở gần 2 CT nhóm Piláp khác loại +30% sức đánh +100% tốc độ chạy) | Chỉ số option trong game |
| **`153`** | #% tỉ lệ phát nổ sau khi chết | +% Tự sát phát nổ (Bom) sau khi chết |
| **`154`** | Không thể bán lại | Chỉ số option trong game |
| **`155`** | Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái | Chỉ số option trong game |
| **`156`** | Cộng dồn 1% sức đánh khi chỉ dùng chiêu đấm, tối đa #% | Giảm 50% chỉ số, tăng % SM/TN và Vàng từ quái |
| **`157`** | Giảm #% mọi sát thương khi KI dưới 20% | Chỉ số option trong game |
| **`158`** | Khi ở trần và không đeo cải trang sẽ có cơ hội tìm thấy vật phẩm sự kiện | Chỉ số option trong game |
| **`159`** | x# sức đánh đòn chưởng cơ bản mỗi phút | Chỉ số option trong game |
| **`160`** | +#% TN, SM cho đệ tử khi sư phụ mặc | Chỉ số option trong game |
| **`161`** | Thêm # ngọc mỗi ngày khi đánh quái. | Chỉ số option trong game |
| **`162`** | Cute hồi #% KI/s bản thân và xung quanh | Cute hồi % KI/s cho bản thân và xung quanh |
| **`163`** | Biến người xung quanh thành Bí Ngô | Chỉ số option trong game |
| **`164`** | Đổi bằng # điểm sự kiện | Chỉ số option trong game |
| **`165`** | Cơ hội ra đòn +#% sát thương lửa khi cận chiến quái | Chỉ số option trong game |
| **`166`** | Ngầu +#% sức đánh lên quái khi bay với Cải Trang Tàu Pảy Pảy | Chỉ số option trong game |
| **`167`** | Tạo không khí lạnh | Chỉ số option trong game |
| **`168`** | Hấp thụ sức mạnh rồi bộc phá | Chỉ số option trong game |
| **`169`** | Cơ hội hạ độc đối thủ | Chỉ số option trong game |
| **`170`** | Ngầu +#% sức đánh lên quái khi bay với Cải Trang nhà Fide | Chỉ số option trong game |
| **`171`** | Số lượng #K | Chỉ số option trong game |
| **`172`** | # | Chỉ số option trong game |
| **`173`** | Phục hồi #% HP và KI cho đồng đội | Phục hồi % HP và KI cho đồng đội bang hội |
| **`174`** | Sự kiện năm # | Chỉ số option trong game |
| **`175`** | Giảm #% thời gian bị mù | Chỉ số option trong game |
| **`176`** | $(Ở gần đủ 5 loại +20% sức đánh +50% tốc độ chạy) | Chỉ số option trong game |
| **`177`** | $(Tối đa +2% tất cả khi ở gần Mabư mập) | Chỉ số option trong game |
| **`178`** | KI+#%/10s | Chỉ số option trong game |
| **`179`** | +2% sức đánh, tối đa 10% khi ở gần Cải Trang tộc Demons Frost | Chỉ số option trong game |
| **`180`** | +#% sức đánh khi ở gần Cải Trang Black Gohan Rose | Chỉ số option trong game |
| **`181`** | Dịch chuyển tức thời +#% sát thương | Chỉ số option trong game |
| **`182`** | +#% sát thương đệ từ trứng | Chỉ số option trong game |
| **`183`** | Giảm #% thời gian hồi Khiên | Chỉ số option trong game |
| **`184`** | +#% sức đánh, tối đa 10% khi ở gần Gohan xanh, Poc đỏ, Arale búp bê | Chỉ số option trong game |
| **`185`** | +# giờ sử dụng | Chỉ số option trong game |
| **`186`** | Biến kẹo 30 giây | Chỉ số option trong game |
| **`187`** | Giảm # giây thời gian bị mù | Chỉ số option trong game |
| **`188`** | Đệ tử chưởng kamejoko +#% sát thương | Chỉ số option trong game |
| **`189`** | Đệ tử chưởng antomic +#% sát thương | Chỉ số option trong game |
| **`190`** | Đệ tử chưởng masenko +#% sát thương | Chỉ số option trong game |
| **`191`** | Né chí mạng+#% | Chỉ số option trong game |
| **`192`** | +#% Chí mạng | Chỉ số option trong game |
| **`193`** | +#% sức đánh, tối đa 11% khi ở gần Cải trang cầu thủ khác | Chỉ số option trong game |
| **`194`** | +#% sức đánh, tối đa 10% khi ở gần Cải trang hè khác | Chỉ số option trong game |
| **`195`** | +#% sức đánh, tối đa 10% khi ở gần Cải trang siêu nhân khác | Chỉ số option trong game |
| **`196`** | Xinh +#% sức đánh, tối đa 18% khi ở gần Cải trang Thỏ khác | Chỉ số option trong game |
| **`197`** | Tấn công+#% lên tộc Xayda | Chỉ số option trong game |
| **`198`** | Giảm #% sát thương từ tộc Trái Đất | Chỉ số option trong game |
| **`199`** | Giảm #% sát thương từ tộc Namếc | Chỉ số option trong game |
| **`200`** | Set Hủy Diệt Beerus | Chỉ số option trong game |
| **`201`** | $[2] Tăng nhẹ SĐ và STCM | Chỉ số option trong game |
| **`202`** | $[4] Tăng mạnh SĐ và STCM | Chỉ số option trong game |
| **`203`** | $[5] Tăng cực mạnh SĐ và STCM | Chỉ số option trong game |
| **`204`** | Tấn công+#% lên Boss | Chỉ số option trong game |
| **`205`** | Đi cùng CT Diệt Quỷ +#% SĐ, tối đa 18% | Chỉ số option trong game |
| **`206`** | Vật phẩm hiếm rơi từ quái (+#%) | Chỉ số option trong game |
| **`207`** | Vật phẩm hiếm rơi từ boss (+#%) | Chỉ số option trong game |
| **`208`** | Trang bị đã chuyển hóa | Chỉ số option trong game |
| **`209`** | Bị rớt cấp # lần | Chỉ số option trong game |
| **`210`** | # Dòng chỉ số ẩn | Chỉ số option trong game |
| **`211`** | Giám định #/5 | Tăng số lượng ltdb |
| **`212`** | Độ bền #/1000 | Chỉ số option trong game |
| **`213`** | Giá bán: # triệu vàng | Chỉ số option trong game |
| **`214`** | Phạm vi tuyệt kỹ +#% | Chỉ số option trong game |
| **`215`** | Số lượng mục tiêu +# | Chỉ số option trong game |
| **`216`** | Tuyệt kỹ +#% sát thương | Chỉ số option trong game |
| **`217`** | - Chưa giám định | Chỉ số option trong game |
| **`218`** |  --------- | Số lỗ sao pha lê đục trên trang bị |
| **`219`** | Số lần tẩy # | Chỉ số option trong game |
| **`220`** | Hoàn thành #% | Chỉ số option trong game |
| **`221`** | Phục hồi #% HP và  KI mỗi 30 giây cho bản thân và đồng bang | Chỉ số option trong game |
| **`222`** | 120 giây kích nộ sức đánh #% trong 30 giây | Chỉ số option trong game |
| **`223`** | 120 giây kích hồi #% HP trong vòng 30 giây | Chỉ số option trong game |
| **`224`** | Giảm thời gian cooldown của tất cả kỹ năng về 0,1 giây | Chỉ số option trong game |
| **`225`** | Giảm # giây bị phong ấn của Ma Phong Ba | Chỉ số option trong game |
| **`226`** | Cute +#% SĐ cho mình và người xung quanh | Tăng sức đánh đồng đội lân cận |
| **`227`** | Giảm #% tác dụng của các đòn khống chế khi dùng khiên năng lượng | Chỉ số option trong game |
| **`228`** | Cường hóa tới ô sao pha lê # | Bậc cường hóa sao pha lê |
| **`229`** | Lạnh gây giảm #% HP , KI mỗi 30s cho những người xung quanh | Chỉ số option trong game |
| **`230`** | +# triệu tiềm năng, sức mạnh | Chỉ số option trong game |
| **`231`** | Hạn sử dụng hoặc vĩnh viễn | Chỉ số option trong game |
| **`232`** | Số lần giao dịch còn lại: # | Chỉ số option trong game |
| **`233`** | Set Gohan | Chỉ số option trong game |
| **`234`** | $(5 món +150% may mắn +30% vàng rơi từ quái) | Chỉ số option trong game |
| **`235`** | Số lần ký gửi còn lại: # | Chỉ số option trong game |
| **`236`** | +#% May mắn | Chỉ số option trong game |
| **`237`** | Set Nail chiến binh Namếc | Chỉ số option trong game |
| **`238`** | $[2] Tăng nhẹ sát thương chí mạng | Chỉ số option trong game |
| **`239`** | $[4] Giảm nhẹ hồi chiêu Masenko | Chỉ số option trong game |
| **`240`** | $[5] Tăng sát thương Masenko, giảm mạnh hồi chiêu Masenko | Chỉ số option trong game |
| **`241`** | Set Cađic M | Set Kích Hoạt Cađic M |
| **`242`** | $[2] Tăng nhẹ HP và phạm vi ảnh hưởng chiêu phát nổ | Cađic M [2 món]: Tăng nhẹ HP và phạm vi chiêu phát nổ |
| **`243`** | $[4] Tăng nhẹ sát thương chiêu phát nổ | Cađic M [4 món]: Tăng nhẹ sát thương chiêu phát nổ |
| **`244`** | $[5] Tăng mạnh sát thương chiêu phát nổ | Cađic M [5 món]: Tăng mạnh sát thương chiêu phát nổ |
| **`245`** | Set Thần Vũ Trụ Kaio | Set Kích Hoạt Thần Vũ Trụ Kaio |
| **`246`** | $[2] Tăng mạnh chí mạng | Kaio [2 món]: Tăng mạnh chí mạng |
| **`247`** | $[4] Giảm nhẹ hao HP,KI chiêu Kaioken | Kaio [4 món]: Giảm nhẹ tiêu hao HP/KI chiêu Kaioken |
| **`248`** | $[5] Tăng mạnh sát thương và giảm mạnh hao HP,KI chiêu Kaioken | Kaio [5 món]: Tăng mạnh sát thương & giảm mạnh hao HP/KI Kaioken |
| **`249`** | Hắc hóa: +#% HP cho người chơi xung quanh | Hắc hóa: +% HP cho người chơi xung quanh |
| **`250`** | # Kilis | Chỉ số Kilis (Năng lượng chiến đấu) |

---

## 2. Toàn Bộ 150 Boss ID (`BossID.java`)

| ID Boss | Tên Hằng Số trong `BossID.java` | Nhóm / Phân Khu Cốt Truyện & Sự Kiện |
| :---: | :--- | :--- |
| **`-4`** | `TRUNG_UY_TRANG` | DOANH TRAI |
| **`-5`** | `TRUNG_UY_THEP` | DOANH TRAI |
| **`-6`** | `TRUNG_UY_XANH_LO` | DOANH TRAI |
| **`-7`** | `NINJA_AO_TIM` | DOANH TRAI |
| **`-8`** | `ROBOT_VE_SI` | DOANH TRAI |
| **`-9`** | `NINJA_AO_TIM1` | DOANH TRAI |
| **`-10`** | `NINJA_AO_TIM2` | DOANH TRAI |
| **`-11`** | `NINJA_AO_TIM3` | DOANH TRAI |
| **`-12`** | `NINJA_AO_TIM4` | DOANH TRAI |
| **`-13`** | `NINJA_AO_TIM5` | DOANH TRAI |
| **`-14`** | `NINJA_AO_TIM6` | DOANH TRAI |
| **`-15`** | `CADICH` | DOANH TRAI |
| **`-16`** | `NADIC` | DOANH TRAI |
| **`-17`** | `SAIBAMEN` | DOANH TRAI |
| **`-20`** | `KUKU` | DOANH TRAI |
| **`-21`** | `MAP_DAU_DINH` | DOANH TRAI |
| **`-22`** | `RAMBO` | DOANH TRAI |
| **`-23`** | `SO_4` | DOANH TRAI |
| **`-24`** | `SO_3` | DOANH TRAI |
| **`-25`** | `SO_2` | DOANH TRAI |
| **`-26`** | `SO_1` | DOANH TRAI |
| **`-27`** | `TIEU_DOI_TRUONG` | DOANH TRAI |
| **`-223`** | `TDST` | DOANH TRAI |
| **`-28`** | `FIDE` | DOANH TRAI |
| **`-29`** | `COOLER` | DOANH TRAI |
| **`-30`** | `ANDROID_19` | DOANH TRAI |
| **`-31`** | `DR_KORE` | DOANH TRAI |
| **`-32`** | `ANDROID_13` | DOANH TRAI |
| **`-33`** | `ANDROID_14` | DOANH TRAI |
| **`-34`** | `ANDROID_15` | DOANH TRAI |
| **`-35`** | `PIC` | DOANH TRAI |
| **`-36`** | `POC` | DOANH TRAI |
| **`-37`** | `KING_KONG` | DOANH TRAI |
| **`-100`** | `XEN_BO_HUNG` | DOANH TRAI |
| **`-101`** | `SIEU_BO_HUNG` | DOANH TRAI |
| **`-102`** | `XEN_CON_1` | DOANH TRAI |
| **`-103`** | `XEN_CON_2` | DOANH TRAI |
| **`-104`** | `XEN_CON_3` | DOANH TRAI |
| **`-105`** | `XEN_CON_4` | DOANH TRAI |
| **`-106`** | `XEN_CON_5` | DOANH TRAI |
| **`-107`** | `XEN_CON_6` | DOANH TRAI |
| **`-108`** | `XEN_CON_7` | DOANH TRAI |
| **`-204`** | `HIT` | DOANH TRAI |
| **`-205`** | `CHILL_1` | DOANH TRAI |
| **`-206`** | `CHILL_2` | DOANH TRAI |
| **`-207`** | `HATCHIYACK` | DOANH TRAI |
| **`-208`** | `DR_LYCHEE` | DOANH TRAI |
| **`-1822`** | `BROLY` | DOANH TRAI |
| **`-82282`** | `SUPER_BROLY` | DOANH TRAI |
| **`-77`** | `SOI_HEC_QUYN1` | MINIBOSSS |
| **`-78`** | `O_DO1` | MINIBOSSS |
| **`-79`** | `Virut` | MINIBOSSS |
| **`-799`** | `MAT_TROI` | MINIBOSSS |
| **`-233`** | `DRABURA` | BOSS 12H |
| **`-234`** | `BUI_BUI` | BOSS 12H |
| **`-235`** | `YA_CON` | BOSS 12H |
| **`-236`** | `MABU_12H` | BOSS 12H |
| **`-237`** | `DRABURA_2` | BOSS 12H |
| **`-238`** | `BUI_BUI_2` | BOSS 12H |
| **`-341`** | `GOKU` | BOSS 12H |
| **`-342`** | `CADIC` | BOSS 12H |
| **`-343`** | `DRABURA_3` | BOSS 12H |
| **`-203`** | `BLACK_GOKU` | BOSS 12H |
| **`-203999`** | `CUMBER` | BOSS 12H |
| **`-308`** | `TAU_PAY_PAY_DONG_NAM_KARIN` | BOSS 12H |
| **`-309`** | `TAUPAYPAY` | BOSS 12H |
| **`-311`** | `SO_4_NM` | BOSS 12H |
| **`-312`** | `SO_3_NM` | BOSS 12H |
| **`-313`** | `SO_2_NM` | BOSS 12H |
| **`-314`** | `SO_1_NM` | BOSS 12H |
| **`-315`** | `TIEU_DOI_TRUONG_NM` | BOSS 12H |
| **`-316`** | `BUJIN` | BOSS 12H |
| **`-317`** | `KOGU` | BOSS 12H |
| **`-318`** | `ZANGYA` | BOSS 12H |
| **`-319`** | `BIDO` | BOSS 12H |
| **`-320`** | `BOJACK` | BOSS 12H |
| **`-321`** | `SUPER_BOJACK` | BOSS 12H |
| **`-322`** | `TAP_SU_0` | YARDART |
| **`-323`** | `TAP_SU_1` | YARDART |
| **`-324`** | `TAP_SU_2` | YARDART |
| **`-325`** | `TAP_SU_3` | YARDART |
| **`-326`** | `TAP_SU_4` | YARDART |
| **`-327`** | `TAN_BINH_5` | YARDART |
| **`-328`** | `TAN_BINH_0` | YARDART |
| **`-329`** | `TAN_BINH_1` | YARDART |
| **`-330`** | `TAN_BINH_2` | YARDART |
| **`-331`** | `TAN_BINH_3` | YARDART |
| **`-332`** | `TAN_BINH_4` | YARDART |
| **`-333`** | `CHIEN_BINH_5` | YARDART |
| **`-334`** | `CHIEN_BINH_0` | YARDART |
| **`-335`** | `CHIEN_BINH_1` | YARDART |
| **`-336`** | `CHIEN_BINH_2` | YARDART |
| **`-337`** | `CHIEN_BINH_3` | YARDART |
| **`-338`** | `CHIEN_BINH_4` | YARDART |
| **`-339`** | `DOI_TRUONG_5` | YARDART |
| **`-340`** | `POCOLO` | SIEU THANH THUY |
| **`-77`** | `SOI_HEC_QUYN` | DAI HOI VO THUAT 23 |
| **`-78`** | `O_DO` | DAI HOI VO THUAT 23 |
| **`-79`** | `XINBATO` | DAI HOI VO THUAT 23 |
| **`-80`** | `CHA_PA` | DAI HOI VO THUAT 23 |
| **`-81`** | `PON_PUT` | DAI HOI VO THUAT 23 |
| **`-82`** | `CHAN_XU` | DAI HOI VO THUAT 23 |
| **`-83`** | `TAU_PAY_PAY` | DAI HOI VO THUAT 23 |
| **`-84`** | `YAMCHA` | DAI HOI VO THUAT 23 |
| **`-85`** | `JACKY_CHUN` | DAI HOI VO THUAT 23 |
| **`-86`** | `THIEN_XIN_HANG` | DAI HOI VO THUAT 23 |
| **`-87`** | `LIU_LIU` | DAI HOI VO THUAT 23 |
| **`-88`** | `THIEN_XIN_HANG_CLONE` | DAI HOI VO THUAT 23 |
| **`-89`** | `THIEN_XIN_HANG_CLONE1` | DAI HOI VO THUAT 23 |
| **`-90`** | `THIEN_XIN_HANG_CLONE2` | DAI HOI VO THUAT 23 |
| **`-91`** | `THIEN_XIN_HANG_CLONE3` | DAI HOI VO THUAT 23 |
| **`-92`** | `PO_CO_LO` | DAI HOI VO THUAT 23 |
| **`-93`** | `DRACULA` | VO DAI BA HAT MIT |
| **`-94`** | `NGUOI_VO_HINH` | VO DAI BA HAT MIT |
| **`-95`** | `BONG_BANG` | VO DAI BA HAT MIT |
| **`-96`** | `VUA_QUY_SA_TANG` | VO DAI BA HAT MIT |
| **`-97`** | `THO_DAU_BAC` | VO DAI BA HAT MIT |
| **`-344`** | `KHIDOT` | TRUNGTHU EVENT |
| **`-345`** | `NGUYETTHAN` | TRUNGTHU EVENT |
| **`-346`** | `NHATTHAN` | TRUNGTHU EVENT |
| **`-502`** | `GOLDEN_FRIEZA` | BOSS 21H |
| **`-609`** | `DEATH_BEAM_1` | BOSS 21H |
| **`-610`** | `DEATH_BEAM_2` | BOSS 21H |
| **`-611`** | `DEATH_BEAM_3` | BOSS 21H |
| **`-612`** | `DEATH_BEAM_4` | BOSS 21H |
| **`-613`** | `DEATH_BEAM_5` | BOSS 21H |
| **`-613`** | `HAKAI` | BOSS 21H |
| **`-214`** | `MABU` | MABU 2H |
| **`-348`** | `SUPERBU` | MABU 2H |
| **`-349`** | `MATROI` | HALLOWEEN EVENT |
| **`-350`** | `DOI` | HALLOWEEN EVENT |
| **`-351`** | `BIMA` | HALLOWEEN EVENT |
| **`-353`** | `ONG_GIA_NOEL` | CHRISTMAS EVENT |
| **`-354`** | `SON_TINH` | HUNGVUONG EVENT |
| **`-355`** | `THUY_TINH` | HUNGVUONG EVENT |
| **`-371`** | `LAN_CON` | TET EVENT |
| **`-357`** | `KARIN` | TET EVENT |
| **`-358`** | `YAJIRO` | TET EVENT |
| **`-359`** | `MRPOPO` | TET EVENT |
| **`-360`** | `THUONG_DE` | TET EVENT |
| **`-361`** | `KHI_BUBBLES` | TET EVENT |
| **`-362`** | `THAN_VU_TRU` | TET EVENT |
| **`-363`** | `TO_SU_KAIO` | TET EVENT |
| **`-364`** | `WHIS` | TET EVENT |
| **`-365`** | `AN_TROM` | LINH TINH |
| **`-385`** | `TAU_PAIPAI` | LINH TINH |
| **`-386998`** | `RONG_NHI` | LINH TINH |
| **`-924`** | `CADIC_M` | Cadic M |
| **`-925`** | `BABY` | Cadic M |
| **`-926`** | `GAU_TUONG_CUOP` | Cadic M |

---

## 3. Toàn Bộ Bản Đồ & Loại Map (`ConstMap.java`)

### 3.1. Các Loại Bản Đồ (`Type Map`)
| Giá trị | Hằng Số `ConstMap` | Mô tả loại Map |
| :---: | :--- | :--- |
| **`0`** | `MAP_NORMAL` | Loại bản đồ `MAP_NORMAL` |
| **`1`** | `MAP_OFFLINE` | Loại bản đồ `MAP_OFFLINE` |
| **`2`** | `MAP_DOANH_TRAI` | Loại bản đồ `MAP_DOANH_TRAI` |
| **`3`** | `MAP_BLACK_BALL_WAR` | Loại bản đồ `MAP_BLACK_BALL_WAR` |
| **`4`** | `MAP_BAN_DO_KHO_BAU` | Loại bản đồ `MAP_BAN_DO_KHO_BAU` |
| **`5`** | `MAP_MA_BU` | Loại bản đồ `MAP_MA_BU` |
| **`6`** | `MAP_CON_DUONG_RAN_DOC` | Loại bản đồ `MAP_CON_DUONG_RAN_DOC` |
| **`7`** | `MAP_KHI_GAS_HUY_DIET` | Loại bản đồ `MAP_KHI_GAS_HUY_DIET` |
| **`8`** | `MAP_TAY_KARIN` | Loại bản đồ `MAP_TAY_KARIN` |
| **`9`** | `MAP_MABU_14H` | Loại bản đồ `MAP_MABU_14H` |
| **`500`** | `CHANGE_CAPSULE` | Kiểu chuyển map `CHANGE_CAPSULE` |
| **`501`** | `CHANGE_BLACK_BALL` | Kiểu chuyển map `CHANGE_BLACK_BALL` |
| **`502`** | `CHANGE_MAP_MA_BU` | Kiểu chuyển map `CHANGE_MAP_MA_BU` |

### 3.2. Danh Sách Toàn Bộ Map Trong Game (`Data Map` ID 0 .. 163)
| ID Map | Hằng Số `ConstMap` | Phân Khu Bản Đồ |
| :---: | :--- | :--- |
| **`2`** | `TILE_TOP` | Bản đồ dã ngoại / quái sơ cấp |
| **`0`** | `LANG_ARU` | Làng xuất phát 3 hành tinh |
| **`1`** | `DOI_HOA_CUC` | Bản đồ dã ngoại / quái sơ cấp |
| **`2`** | `THUNG_LUNG_TRE` | Bản đồ dã ngoại / quái sơ cấp |
| **`3`** | `RUNG_NAM` | Bản đồ dã ngoại / quái sơ cấp |
| **`4`** | `RUNG_XUONG` | Bản đồ dã ngoại / quái sơ cấp |
| **`5`** | `DAO_KAME` | Đảo Kame / Đảo Guru / Thành phố Vegeta |
| **`6`** | `DONG_KARIN` | Khu vực game |
| **`7`** | `LANG_MORI` | Làng xuất phát 3 hành tinh |
| **`8`** | `DOI_NAM_TIM` | Bản đồ dã ngoại / quái sơ cấp |
| **`9`** | `THI_TRAN_MOORI` | Bản đồ dã ngoại / quái sơ cấp |
| **`10`** | `THUNG_LUNG_NAMEC` | Bản đồ dã ngoại / quái sơ cấp |
| **`11`** | `THUNG_LUNG_MAIMA` | Bản đồ dã ngoại / quái sơ cấp |
| **`12`** | `VUC_MAIMA` | Bản đồ dã ngoại / quái sơ cấp |
| **`13`** | `DAO_GURU` | Đảo Kame / Đảo Guru / Thành phố Vegeta |
| **`14`** | `LANG_KAKAROT` | Làng xuất phát 3 hành tinh |
| **`15`** | `DOI_HOANG` | Bản đồ dã ngoại / quái sơ cấp |
| **`16`** | `LANG_PLANT` | Bản đồ dã ngoại / quái sơ cấp |
| **`17`** | `RUNG_NGUYEN_SINH` | Bản đồ dã ngoại / quái sơ cấp |
| **`18`** | `RUNG_THONG_XAYDA` | Bản đồ dã ngoại / quái sơ cấp |
| **`19`** | `THANH_PHO_VEGETA` | Đảo Kame / Đảo Guru / Thành phố Vegeta |
| **`20`** | `VACH_NUI_DEN` | Bản đồ dã ngoại / quái sơ cấp |
| **`21`** | `NHA_GOHAN` | Nhà của 3 hành tinh (ấp trứng, hồi phục) |
| **`22`** | `NHA_MOORI` | Nhà của 3 hành tinh (ấp trứng, hồi phục) |
| **`23`** | `NHA_BROLY` | Nhà của 3 hành tinh (ấp trứng, hồi phục) |
| **`24`** | `TRAM_TAU_VU_TRU` | Khu vực game |
| **`25`** | `TRAM_TAU_VU_TRU_25` | Khu vực game |
| **`26`** | `TRAM_TAU_VU_TRU_26` | Khu vực game |
| **`27`** | `RUNG_BAMBOO` | Khu vực game |
| **`28`** | `RUNG_DUONG_XI` | Khu vực game |
| **`29`** | `NAM_KAME` | Khu vực game |
| **`30`** | `DAO_BULONG` | Khu vực game |
| **`31`** | `NUI_HOA_VANG` | Khu vực game |
| **`32`** | `NUI_HOA_TIM` | Khu vực game |
| **`33`** | `NAM_GURU` | Khu vực game |
| **`34`** | `DONG_NAM_GURU` | Khu vực game |
| **`35`** | `RUNG_CO` | Khu vực game |
| **`36`** | `RUNG_DA` | Khu vực game |
| **`37`** | `THUNG_LUNG_DEN` | Khu vực game |
| **`38`** | `BO_VUC_DEN` | Khu vực game |
| **`39`** | `VACH_NUI_ARU` | Khu vực game |
| **`40`** | `VACH_NUI_MOORI` | Khu vực game |
| **`41`** | `VUC_PLANT` | Khu vực game |
| **`42`** | `VACH_NUI_ARU_42` | Khu vực game |
| **`43`** | `VACH_NUI_MOORI_43` | Khu vực game |
| **`44`** | `VACH_NUI_KAKAROT` | Khu vực game |
| **`45`** | `THAN_DIEN` | Khu vực Thần điện, Tháp Karin, Kaio & Tập luyện |
| **`46`** | `THAP_KARIN` | Khu vực Thần điện, Tháp Karin, Kaio & Tập luyện |
| **`47`** | `RUNG_KARIN` | Khu vực Thần điện, Tháp Karin, Kaio & Tập luyện |
| **`48`** | `HANH_TINH_KAIO` | Khu vực Thần điện, Tháp Karin, Kaio & Tập luyện |
| **`49`** | `PHONG_TAP_THOI_GIAN` | Khu vực Thần điện, Tháp Karin, Kaio & Tập luyện |
| **`50`** | `THANH_DIA_KAIO` | Khu vực Thần điện, Tháp Karin, Kaio & Tập luyện |
| **`51`** | `DAU_TRUONG` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`52`** | `DAI_HOI_VO_THUAT` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`53`** | `TUONG_THANH_1` | Phó bản Doanh Trại Độc Nhãn |
| **`54`** | `TANG_3` | Phó bản Doanh Trại Độc Nhãn |
| **`55`** | `TANG_1` | Phó bản Doanh Trại Độc Nhãn |
| **`56`** | `TANG_2` | Phó bản Doanh Trại Độc Nhãn |
| **`57`** | `TANG_4` | Phó bản Doanh Trại Độc Nhãn |
| **`58`** | `TUONG_THANH_2` | Phó bản Doanh Trại Độc Nhãn |
| **`59`** | `TUONG_THANH_3` | Phó bản Doanh Trại Độc Nhãn |
| **`60`** | `TRAI_DOC_NHAN_1` | Phó bản Doanh Trại Độc Nhãn |
| **`61`** | `TRAI_DOC_NHAN_2` | Phó bản Doanh Trại Độc Nhãn |
| **`62`** | `TRAI_DOC_NHAN_3` | Phó bản Doanh Trại Độc Nhãn |
| **`63`** | `TRAI_LINH_FIDE` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`64`** | `NUI_DAY_LEO` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`65`** | `NUI_CAY_QUY` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`66`** | `TRAI_QUY_GIA` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`67`** | `VUC_CHET` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`68`** | `THUNG_LUNG_NAPPA` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`69`** | `VUC_CAM` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`70`** | `NUI_APPULE` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`71`** | `CAN_CU_RASPBERRY` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`72`** | `THUNG_LUNG_RASPBERRY` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`73`** | `THUNG_LUNG_CHET` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`74`** | `DOI_CAY_FIDE` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`75`** | `KHE_NUI_TU_THAN` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`76`** | `NUI_DA` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`77`** | `RUNG_DA_77` | Khu vực Nappa, Trại lính Fide, Đồi cây Fide |
| **`79`** | `NUI_KHI_DO` | Khu vực game |
| **`80`** | `NUI_KHI_VANG` | Khu vực game |
| **`81`** | `HANG_QUY_CHIM` | Khu vực game |
| **`82`** | `NUI_KHI_DEN` | Khu vực game |
| **`83`** | `HANG_KHI_DEN` | Khu vực game |
| **`84`** | `SIEU_THI` | Khu vực game |
| **`85`** | `HANH_TINH_M_2` | Khu vực game |
| **`86`** | `HANH_TINH_POLARIS` | Khu vực game |
| **`87`** | `HANH_TINH_CRETACEOUS` | Khu vực game |
| **`88`** | `HANH_TINH_MONMAASU` | Khu vực game |
| **`89`** | `HANH_TINH_RUDEEZE` | Khu vực game |
| **`90`** | `HANH_TINH_GELBO` | Khu vực game |
| **`91`** | `HANH_TINH_TIGERE` | Khu vực game |
| **`92`** | `THANH_PHO_PHIA_DONG` | Khu vực Tương Lai (Android sát thủ) |
| **`93`** | `THANH_PHO_PHIA_NAM` | Khu vực Tương Lai (Android sát thủ) |
| **`94`** | `DAO_BALE` | Khu vực Tương Lai (Android sát thủ) |
| **`96`** | `CAO_NGUYEN` | Khu vực Tương Lai (Android sát thủ) |
| **`97`** | `THANH_PHO_PHIA_BAC` | Khu vực Tương Lai (Android sát thủ) |
| **`98`** | `NGON_NUI_PHIA_BAC` | Khu vực Tương Lai (Android sát thủ) |
| **`99`** | `THUNG_LUNG_PHIA_BAC` | Khu vực Tương Lai (Android sát thủ) |
| **`100`** | `THI_TRAN_GINDER` | Khu vực Tương Lai (Android sát thủ) |
| **`102`** | `NHA_BUNMA` | Khu vực Tương Lai (Android sát thủ) |
| **`103`** | `VO_DAI_XEN_BO_HUNG` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`104`** | `SAN_SAU_SIEU_THI` | Khu vực Tương Lai (Android sát thủ) |
| **`105`** | `CANH_DONG_TUYET` | Hành tinh Băng Cold (Cooler, Fide Vàng) |
| **`106`** | `RUNG_TUYET` | Hành tinh Băng Cold (Cooler, Fide Vàng) |
| **`107`** | `NUI_TUYET` | Hành tinh Băng Cold (Cooler, Fide Vàng) |
| **`108`** | `DONG_SONG_BANG` | Hành tinh Băng Cold (Cooler, Fide Vàng) |
| **`109`** | `RUNG_BANG` | Hành tinh Băng Cold (Cooler, Fide Vàng) |
| **`110`** | `HANG_BANG` | Hành tinh Băng Cold (Cooler, Fide Vàng) |
| **`111`** | `DONG_NAM_KARIN` | Khu vực game |
| **`112`** | `VO_DAI_HAT_MIT` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`113`** | `DAI_HOI_VO_THUAT_113` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`114`** | `CONG_PHI_THUYEN` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`115`** | `PHONG_CHO` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`117`** | `CUA_AI_1` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`118`** | `CUA_AI_2` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`119`** | `CUA_AI_3` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`120`** | `PHONG_CHI_HUY` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`122`** | `NGU_HANH_SON` | Khu vực game |
| **`123`** | `NGU_HANH_SON_123` | Khu vực game |
| **`124`** | `NGU_HANH_SON_124` | Khu vực game |
| **`126`** | `THANH_PHO_SANTA` | Phó bản Khí Gas Hủy Diệt (Destron Gas) |
| **`127`** | `CONG_PHI_THUYEN_127` | Phó bản Phi Thuyền Ma Bử (14h) |
| **`129`** | `DAI_HOI_VO_THUAT_129` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`131`** | `HANH_TINH_YARDART` | Khu vực game |
| **`132`** | `HANH_TINH_YARDART_2` | Khu vực game |
| **`133`** | `HANH_TINH_YARDART_3` | Khu vực game |
| **`135`** | `DONG_HAI_TAC` | Phó bản Bản Đồ Kho Báu (Hải Tặc) |
| **`136`** | `HANG_BACH_TUOC` | Phó bản Bản Đồ Kho Báu (Hải Tặc) |
| **`137`** | `DONG_KHO_BAU` | Phó bản Bản Đồ Kho Báu (Hải Tặc) |
| **`138`** | `CANG_HAI_TAC` | Phó bản Bản Đồ Kho Báu (Hải Tặc) |
| **`139`** | `HANH_TINH_POTAUFEU` | Khu vực game |
| **`140`** | `HANG_DONG_POTAUFEU` | Khu vực game |
| **`141`** | `CON_DUONG_RAN_DOC` | Phó bản Con Đường Rắn Độc |
| **`142`** | `CON_DUONG_RAN_DOC_142` | Phó bản Con Đường Rắn Độc |
| **`143`** | `CON_DUONG_RAN_DOC_143` | Phó bản Con Đường Rắn Độc |
| **`144`** | `HOANG_MAC` | Phó bản Con Đường Rắn Độc |
| **`145`** | `VO_DAI_SIEU_CAP` | Đấu trường, Đại hội võ thuật, Võ đài Xên/Bà Hạt Mít |
| **`146`** | `TAY_KARIN` | Khu vực game |
| **`154`** | `HANH_TINH_BILL` | Hành tinh Bill / Hành tinh Ngục Tù (Cumber) |
| **`155`** | `HANH_TINH_NGUC_TU` | Hành tinh Bill / Hành tinh Ngục Tù (Cumber) |
| **`160`** | `KHU_HANG_DONG` | Rừng nguyên thủy / Làng Plant nguyên thủy |
| **`161`** | `BIA_RUNG_NGUYEN_THUY` | Rừng nguyên thủy / Làng Plant nguyên thủy |
| **`162`** | `RUNG_NGUYEN_THUY` | Rừng nguyên thủy / Làng Plant nguyên thủy |
| **`163`** | `LANG_PLANT_NGUYEN_THUY` | Rừng nguyên thủy / Làng Plant nguyên thủy |

---

## 4. Toàn Bộ Hằng Số NPC & Menu (`ConstNpc.java`)

### 4.1. Toàn Bộ 78 Định Danh NPC (`NPC ID`)
| ID NPC | Tên Hằng Số trong `ConstNpc.java` | Vai Trò / Bản Đồ Mặc Định |
| :---: | :--- | :--- |
| **`0`** | `ONG_GOHAN` | NPC `ONG_GOHAN` |
| **`1`** | `ONG_PARAGUS` | NPC `ONG_PARAGUS` |
| **`2`** | `ONG_MOORI` | NPC `ONG_MOORI` |
| **`3`** | `RUONG_DO` | NPC `RUONG_DO` |
| **`4`** | `DAU_THAN` | NPC `DAU_THAN` |
| **`5`** | `CON_MEO` | NPC `CON_MEO` |
| **`6`** | `KHU_VUC` | NPC `KHU_VUC` |
| **`7`** | `BUNMA` | NPC `BUNMA` |
| **`8`** | `DENDE` | NPC `DENDE` |
| **`9`** | `APPULE` | NPC `APPULE` |
| **`10`** | `DR_DRIEF` | NPC `DR_DRIEF` |
| **`11`** | `CARGO` | NPC `CARGO` |
| **`12`** | `CUI` | NPC `CUI` |
| **`13`** | `QUY_LAO_KAME` | NPC `QUY_LAO_KAME` |
| **`14`** | `TRUONG_LAO_GURU` | NPC `TRUONG_LAO_GURU` |
| **`15`** | `VUA_VEGETA` | NPC `VUA_VEGETA` |
| **`16`** | `URON` | NPC `URON` |
| **`17`** | `BO_MONG` | NPC `BO_MONG` |
| **`18`** | `THAN_MEO_KARIN` | NPC `THAN_MEO_KARIN` |
| **`19`** | `THUONG_DE` | NPC `THUONG_DE` |
| **`20`** | `THAN_VU_TRU` | NPC `THAN_VU_TRU` |
| **`21`** | `BA_HAT_MIT` | NPC `BA_HAT_MIT` |
| **`22`** | `TRONG_TAI` | NPC `TRONG_TAI` |
| **`23`** | `GHI_DANH` | NPC `GHI_DANH` |
| **`24`** | `RONG_THIENG` | NPC `RONG_THIENG` |
| **`25`** | `LINH_CANH` | NPC `LINH_CANH` |
| **`26`** | `DOC_NHAN` | NPC `DOC_NHAN` |
| **`27`** | `RONG_THIENG_NAMEC` | NPC `RONG_THIENG_NAMEC` |
| **`28`** | `CUA_HANG_KY_GUI` | NPC `CUA_HANG_KY_GUI` |
| **`29`** | `RONG_OMEGA` | NPC `RONG_OMEGA` |
| **`30`** | `RONG_2S` | NPC `RONG_2S` |
| **`31`** | `RONG_3S` | NPC `RONG_3S` |
| **`32`** | `RONG_4S` | NPC `RONG_4S` |
| **`33`** | `RONG_5S` | NPC `RONG_5S` |
| **`34`** | `RONG_6S` | NPC `RONG_6S` |
| **`35`** | `RONG_7S` | NPC `RONG_7S` |
| **`36`** | `RONG_1S` | NPC `RONG_1S` |
| **`37`** | `BUNMA_TL` | NPC `BUNMA_TL` |
| **`38`** | `CALICK` | NPC `CALICK` |
| **`39`** | `SANTA` | NPC `SANTA` |
| **`40`** | `MABU_MAP` | NPC `MABU_MAP` |
| **`41`** | `TRUNG_THU` | NPC `TRUNG_THU` |
| **`42`** | `QUOC_VUONG` | NPC `QUOC_VUONG` |
| **`43`** | `TO_SU_KAIO` | NPC `TO_SU_KAIO` |
| **`44`** | `OSIN` | NPC `OSIN` |
| **`45`** | `KIBIT` | NPC `KIBIT` |
| **`46`** | `BABIDAY` | NPC `BABIDAY` |
| **`47`** | `GIUMA_DAU_BO` | NPC `GIUMA_DAU_BO` |
| **`48`** | `NGO_KHONG` | NPC `NGO_KHONG` |
| **`49`** | `DUONG_TANG` | NPC `DUONG_TANG` |
| **`50`** | `QUA_TRUNG` | NPC `QUA_TRUNG` |
| **`51`** | `DUA_HAU` | NPC `DUA_HAU` |
| **`52`** | `HUNG_VUONG` | NPC `HUNG_VUONG` |
| **`53`** | `TAPION` | NPC `TAPION` |
| **`54`** | `LY_TIEU_NUONG` | NPC `LY_TIEU_NUONG` |
| **`55`** | `BILL` | NPC `BILL` |
| **`56`** | `WHIS` | NPC `WHIS` |
| **`57`** | `CHAMPA` | NPC `CHAMPA` |
| **`58`** | `VADOS` | NPC `VADOS` |
| **`59`** | `TRONG_TAI_2` | NPC `TRONG_TAI_2` |
| **`60`** | `GOKU_SSJ` | NPC `GOKU_SSJ` |
| **`61`** | `GOKU_SSJ_2` | NPC `GOKU_SSJ_2` |
| **`62`** | `POTAGE` | NPC `POTAGE` |
| **`63`** | `JACO` | NPC `JACO` |
| **`64`** | `DAI_THIEN_SU` | NPC `DAI_THIEN_SU` |
| **`65`** | `YARIROBE` | NPC `YARIROBE` |
| **`66`** | `NOI_BANH` | NPC `NOI_BANH` |
| **`67`** | `MR_POPO` | NPC `MR_POPO` |
| **`68`** | `PANCHY` | NPC `PANCHY` |
| **`69`** | `THO_DAI_CA` | NPC `THO_DAI_CA` |
| **`70`** | `BARDOCK` | NPC `BARDOCK` |
| **`74`** | `TORIBOT` | NPC `TORIBOT` |
| **`72`** | `CAY_NEU` | NPC `CAY_NEU` |
| **`75`** | `EVENT` | NPC `EVENT` |
| **`81`** | `CHI_CHI` | NPC `CHI_CHI` |
| **`82`** | `RUONG_SUU_TAM` | NPC `RUONG_SUU_TAM` |
| **`83`** | `DR_MYUU` | NPC `DR_MYUU` |
| **`84`** | `XE_NUOC_MIA` | NPC `XE_NUOC_MIA` |

### 4.2. Toàn Bộ 164 Định Danh Menu (`Menu ID`)
| ID Menu | Hằng Số `ConstNpc` | Ngữ Cảnh Sử Dụng |
| :---: | :--- | :--- |
| **`10000`** | `GO_UPSTAIRS_MENU` | Điều phối menu `GO_UPSTAIRS_MENU` |
| **`72002`** | `IGNORE_MENU` | Điều phối menu `IGNORE_MENU` |
| **`31072002`** | `BASE_MENU` | Điều phối menu `BASE_MENU` |
| **`50394`** | `ORTHER_MENU` | Điều phối menu `ORTHER_MENU` |
| **`23505`** | `ORTHER_MENU1` | Điều phối menu `ORTHER_MENU1` |
| **`53906`** | `ORTHER_MENU2` | Điều phối menu `ORTHER_MENU2` |
| **`500`** | `MENU_OPEN_DBKB` | Điều phối menu `MENU_OPEN_DBKB` |
| **`501`** | `MENU_OPENED_DBKB` | Điều phối menu `MENU_OPENED_DBKB` |
| **`502`** | `MENU_ACCEPT_GO_TO_BDKB` | Điều phối menu `MENU_ACCEPT_GO_TO_BDKB` |
| **`601`** | `SANTA_PGG` | Điều phối menu `SANTA_PGG` |
| **`500`** | `MENU_DI_CHUYEN` | Điều phối menu `MENU_DI_CHUYEN` |
| **`500`** | `MENU_PHA_LE_HOA_TRANG_BI` | Điều phối menu `MENU_PHA_LE_HOA_TRANG_BI` |
| **`501`** | `MENU_CHUYEN_HOA_TRANG_BI` | Điều phối menu `MENU_CHUYEN_HOA_TRANG_BI` |
| **`502`** | `MENU_OPTION_SHOP_BUA` | Điều phối menu `MENU_OPTION_SHOP_BUA` |
| **`503`** | `MENU_START_COMBINE` | Điều phối menu `MENU_START_COMBINE` |
| **`504`** | `MENU_PHAN_RA_DO_THAN_LINH` | Điều phối menu `MENU_PHAN_RA_DO_THAN_LINH` |
| **`505`** | `MENU_NANG_CAP_DO_TS` | Điều phối menu `MENU_NANG_CAP_DO_TS` |
| **`507`** | `MENU_CHE_TAO_CHAN_MENH` | Điều phối menu `MENU_CHE_TAO_CHAN_MENH` |
| **`508`** | `MENU_NANG_CAP_CHAN_MENH` | Điều phối menu `MENU_NANG_CAP_CHAN_MENH` |
| **`509`** | `MENU_NANG_CAP_DAC_CAU` | Điều phối menu `MENU_NANG_CAP_DAC_CAU` |
| **`510`** | `MENU_SACH_TUYET_KY` | Điều phối menu `MENU_SACH_TUYET_KY` |
| **`508`** | `MENU_DAP_DO` | Điều phối menu `MENU_DAP_DO` |
| **`502`** | `MENU_JOIN_DOANH_TRAI` | Điều phối menu `MENU_JOIN_DOANH_TRAI` |
| **`502`** | `MAKE_MATCH_PVP` | Điều phối menu `MAKE_MATCH_PVP` |
| **`503`** | `MAKE_FRIEND` | Điều phối menu `MAKE_FRIEND` |
| **`504`** | `REVENGE` | Điều phối menu `REVENGE` |
| **`505`** | `TUTORIAL_SUMMON_DRAGON` | Điều phối menu `TUTORIAL_SUMMON_DRAGON` |
| **`506`** | `SUMMON_SHENRON` | Điều phối menu `SUMMON_SHENRON` |
| **`507`** | `INTRINSIC` | Điều phối menu `INTRINSIC` |
| **`508`** | `CONFIRM_OPEN_INTRINSIC` | Điều phối menu `CONFIRM_OPEN_INTRINSIC` |
| **`509`** | `CONFIRM_OPEN_INTRINSIC_VIP` | Điều phối menu `CONFIRM_OPEN_INTRINSIC_VIP` |
| **`510`** | `CONFIRM_LEAVE_CLAN` | Điều phối menu `CONFIRM_LEAVE_CLAN` |
| **`511`** | `CONFIRM_NHUONG_PC` | Điều phối menu `CONFIRM_NHUONG_PC` |
| **`512`** | `MENU_ADMIN` | Điều phối menu `MENU_ADMIN` |
| **`513`** | `BAN_PLAYER` | Điều phối menu `BAN_PLAYER` |
| **`514`** | `BUFF_PET` | Điều phối menu `BUFF_PET` |
| **`515`** | `CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND` | Điều phối menu `CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND` |
| **`516`** | `MENU_FIND_PLAYER` | Điều phối menu `MENU_FIND_PLAYER` |
| **`517`** | `CONFIRM_DISSOLUTION_CLAN` | Điều phối menu `CONFIRM_DISSOLUTION_CLAN` |
| **`522`** | `CONFIRM_TELE_NAMEC` | Điều phối menu `CONFIRM_TELE_NAMEC` |
| **`527`** | `UP_TOP_ITEM` | Điều phối menu `UP_TOP_ITEM` |
| **`528`** | `MA_BAO_VE` | Điều phối menu `MA_BAO_VE` |
| **`529`** | `OTT` | Điều phối menu `OTT` |
| **`530`** | `OTT_ACCEPT` | Điều phối menu `OTT_ACCEPT` |
| **`531`** | `RUONG_GO` | Điều phối menu `RUONG_GO` |
| **`532`** | `DAT_CUOC_HAT_MIT` | Điều phối menu `DAT_CUOC_HAT_MIT` |
| **`533`** | `BUY_BACK` | Điều phối menu `BUY_BACK` |
| **`726`** | `MENU_OPTION_USE_ITEM726` | Điều phối menu `MENU_OPTION_USE_ITEM726` |
| **`2006`** | `MENU_SIEU_THAN_THUY` | Điều phối menu `MENU_SIEU_THAN_THUY` |
| **`2007`** | `MENU_XUONG_TANG_DUOI` | Điều phối menu `MENU_XUONG_TANG_DUOI` |
| **`928`** | `SUMMON_SHENRON_SUPER` | Điều phối menu `SUMMON_SHENRON_SUPER` |
| **`929`** | `SUMMON_SHENRON_NOEL` | Điều phối menu `SUMMON_SHENRON_NOEL` |
| **`930`** | `SUMMON_SHENRON_HALLOWEEN` | Điều phối menu `SUMMON_SHENRON_HALLOWEEN` |
| **`501`** | `SHENRON_CONFIRM` | Điều phối menu `SHENRON_CONFIRM` |
| **`502`** | `SHENRON_1_1` | Điều phối menu `SHENRON_1_1` |
| **`503`** | `SHENRON_1_2` | Điều phối menu `SHENRON_1_2` |
| **`506`** | `SHENRON_1_3` | Điều phối menu `SHENRON_1_3` |
| **`504`** | `SHENRON_2` | Điều phối menu `SHENRON_2` |
| **`505`** | `SHENRON_3` | Điều phối menu `SHENRON_3` |
| **`31720020`** | `SHOW_SHENRON_NAMEK_CONFIRM` | Điều phối menu `SHOW_SHENRON_NAMEK_CONFIRM` |
| **`31720021`** | `SHENRON_NAMEK_CONFIRM` | Điều phối menu `SHENRON_NAMEK_CONFIRM` |
| **`31720022`** | `SUMMON_SHENRON_EVENT` | Điều phối menu `SUMMON_SHENRON_EVENT` |
| **`31720023`** | `SHOW_SHENRON_EVENT_CONFIRM` | Điều phối menu `SHOW_SHENRON_EVENT_CONFIRM` |
| **`31720024`** | `SHENRON_EVENT_CONFIRM` | Điều phối menu `SHENRON_EVENT_CONFIRM` |
| **`31720225`** | `SUMMON_SHENRON_EVENT_NOEL` | Điều phối menu `SUMMON_SHENRON_EVENT_NOEL` |
| **`3170026`** | `SHOW_SHENRON_EVENT_CONFIRM_NOEL` | Điều phối menu `SHOW_SHENRON_EVENT_CONFIRM_NOEL` |
| **`3172039`** | `SHENRON_EVENT_CONFIRM_NOEL` | Điều phối menu `SHENRON_EVENT_CONFIRM_NOEL` |
| **`31720025`** | `SUB_MENU` | Điều phối menu `SUB_MENU` |
| **`31720026`** | `TAP_TU_DONG_CONFIRM` | Điều phối menu `TAP_TU_DONG_CONFIRM` |
| **`4`** | `MENU_CLAN_UP` | Điều phối menu `MENU_CLAN_UP` |
| **`5`** | `MENU_CLAN_TASK` | Điều phối menu `MENU_CLAN_TASK` |
| **`6`** | `MENU_CLAN_TASK_REMOVE` | Điều phối menu `MENU_CLAN_TASK_REMOVE` |
| **`0`** | `DANGKYDHVT_CONFIRM` | Điều phối menu `DANGKYDHVT_CONFIRM` |
| **`501`** | `MAGIC_TREE_NON_UPGRADE_LEFT_PEA` | Điều phối menu `MAGIC_TREE_NON_UPGRADE_LEFT_PEA` |
| **`502`** | `MAGIC_TREE_NON_UPGRADE_FULL_PEA` | Điều phối menu `MAGIC_TREE_NON_UPGRADE_FULL_PEA` |
| **`503`** | `MAGIC_TREE_CONFIRM_UPGRADE` | Điều phối menu `MAGIC_TREE_CONFIRM_UPGRADE` |
| **`504`** | `MAGIC_TREE_UPGRADE` | Điều phối menu `MAGIC_TREE_UPGRADE` |
| **`505`** | `MAGIC_TREE_CONFIRM_UNUPGRADE` | Điều phối menu `MAGIC_TREE_CONFIRM_UNUPGRADE` |
| **`500`** | `CAN_NOT_OPEN_EGG` | Điều phối menu `CAN_NOT_OPEN_EGG` |
| **`501`** | `CAN_OPEN_EGG` | Điều phối menu `CAN_OPEN_EGG` |
| **`502`** | `CONFIRM_OPEN_EGG` | Điều phối menu `CONFIRM_OPEN_EGG` |
| **`503`** | `CONFIRM_DESTROY_EGG` | Điều phối menu `CONFIRM_DESTROY_EGG` |
| **`500`** | `CHUA_THE_THU_HOACH` | Điều phối menu `CHUA_THE_THU_HOACH` |
| **`501`** | `CO_THE_THU_HOACH` | Điều phối menu `CO_THE_THU_HOACH` |
| **`501`** | `CO_THE_THU` | Điều phối menu `CO_THE_THU` |
| **`500`** | `CAN_NOT_OPEN_BILL` | Điều phối menu `CAN_NOT_OPEN_BILL` |
| **`501`** | `CAN_OPEN_BILL` | Điều phối menu `CAN_OPEN_BILL` |
| **`502`** | `CONFIRM_OPEN_BILL` | Điều phối menu `CONFIRM_OPEN_BILL` |
| **`503`** | `CONFIRM_DESTROY_BILL` | Điều phối menu `CONFIRM_DESTROY_BILL` |
| **`500`** | `QUA_TAN_THU` | Điều phối menu `QUA_TAN_THU` |
| **`501`** | `MENU_PHAN_THUONG` | Điều phối menu `MENU_PHAN_THUONG` |
| **`502`** | `NAP_THE` | Điều phối menu `NAP_THE` |
| **`500`** | `OPEN_POWER_MYSEFT` | Điều phối menu `OPEN_POWER_MYSEFT` |
| **`501`** | `OPEN_POWER_PET` | Điều phối menu `OPEN_POWER_PET` |
| **`500`** | `MENU_CHOOSE_LUCKY_ROUND` | Điều phối menu `MENU_CHOOSE_LUCKY_ROUND` |
| **`501`** | `MENU_FIND_KUKU` | Điều phối menu `MENU_FIND_KUKU` |
| **`502`** | `MENU_FIND_MAP_DAU_DINH` | Điều phối menu `MENU_FIND_MAP_DAU_DINH` |
| **`503`** | `MENU_FIND_RAMBO` | Điều phối menu `MENU_FIND_RAMBO` |
| **`500`** | `MENU_NOT_OPEN_BDW` | Điều phối menu `MENU_NOT_OPEN_BDW` |
| **`501`** | `MENU_OPEN_BDW` | Điều phối menu `MENU_OPEN_BDW` |
| **`502`** | `MENU_REWARD_BDW` | Điều phối menu `MENU_REWARD_BDW` |
| **`500`** | `MENU_NOT_OPEN_MMB` | Điều phối menu `MENU_NOT_OPEN_MMB` |
| **`501`** | `MENU_OPEN_MMB` | Điều phối menu `MENU_OPEN_MMB` |
| **`502`** | `MENU_REWARD_MMB` | Điều phối menu `MENU_REWARD_MMB` |
| **`500`** | `MENU_PHU_HP` | Điều phối menu `MENU_PHU_HP` |
| **`501`** | `MENU_OPTION_PHU_HP` | Điều phối menu `MENU_OPTION_PHU_HP` |
| **`502`** | `MENU_OPTION_GO_HOME` | Điều phối menu `MENU_OPTION_GO_HOME` |
| **`500`** | `MENU_OPTION_LEVEL_SIDE_TASK` | Điều phối menu `MENU_OPTION_LEVEL_SIDE_TASK` |
| **`501`** | `MENU_OPTION_PAY_SIDE_TASK` | Điều phối menu `MENU_OPTION_PAY_SIDE_TASK` |
| **`3333`** | `event3` | Điều phối menu `event3` |
| **`3334`** | `event3_1` | Điều phối menu `event3_1` |
| **`500`** | `SHOP_BUNMA_QK_0` | Điều phối menu `SHOP_BUNMA_QK_0` |
| **`501`** | `SHOP_DENDE_0` | Điều phối menu `SHOP_DENDE_0` |
| **`502`** | `SHOP_APPULE_0` | Điều phối menu `SHOP_APPULE_0` |
| **`503`** | `SHOP_SANTA_0` | Điều phối menu `SHOP_SANTA_0` |
| **`504`** | `SHOP_SANTA_1` | Điều phối menu `SHOP_SANTA_1` |
| **`505`** | `SHOP_URON_0` | Điều phối menu `SHOP_URON_0` |
| **`506`** | `SHOP_BA_HAT_MIT_0` | Điều phối menu `SHOP_BA_HAT_MIT_0` |
| **`507`** | `SHOP_BA_HAT_MIT_1` | Điều phối menu `SHOP_BA_HAT_MIT_1` |
| **`508`** | `SHOP_BA_HAT_MIT_2` | Điều phối menu `SHOP_BA_HAT_MIT_2` |
| **`509`** | `SIDE_BOX_LUCKY_ROUND` | Điều phối menu `SIDE_BOX_LUCKY_ROUND` |
| **`510`** | `SHOP_BUNMA_TL_0` | Điều phối menu `SHOP_BUNMA_TL_0` |
| **`511`** | `SIDE_BOX_ITEM_REWARD` | Điều phối menu `SIDE_BOX_ITEM_REWARD` |
| **`9990`** | `HOC_TUYET_KY` | Điều phối menu `HOC_TUYET_KY` |
| **`99980`** | `SACH_TUYET_KY` | Điều phối menu `SACH_TUYET_KY` |
| **`19787`** | `DONG_THANH_SACH_CU` | Điều phối menu `DONG_THANH_SACH_CU` |
| **`28777`** | `DOI_SACH_TUYET_KY` | Điều phối menu `DOI_SACH_TUYET_KY` |
| **`188877`** | `CHE_TAO_TRANG_BI_THIEN_SU` | Điều phối menu `CHE_TAO_TRANG_BI_THIEN_SU` |
| **`1655`** | `CAPSULE_KICH_HOAT` | Điều phối menu `CAPSULE_KICH_HOAT` |
| **`1998`** | `HOP_TRANG_BI_CAP_12` | Điều phối menu `HOP_TRANG_BI_CAP_12` |
| **`718`** | `VE_TANG_NGOC` | Điều phối menu `VE_TANG_NGOC` |
| **`197882`** | `DANG_SINH_LE` | Điều phối menu `DANG_SINH_LE` |
| **`197827`** | `DANG_SINH_LE_VIP` | Điều phối menu `DANG_SINH_LE_VIP` |
| **`500`** | `CON_SO_MAY_MAN_VANG` | Điều phối menu `CON_SO_MAY_MAN_VANG` |
| **`501`** | `CON_SO_MAY_MAN_NGOC_XANH` | Điều phối menu `CON_SO_MAY_MAN_NGOC_XANH` |
| **`502`** | `KEO_BUA_BAO` | Điều phối menu `KEO_BUA_BAO` |
| **`505`** | `CHON_AI_DAY` | Điều phối menu `CHON_AI_DAY` |
| **`506`** | `CHON_AI_DAY_VANG` | Điều phối menu `CHON_AI_DAY_VANG` |
| **`507`** | `CHON_AI_DAY_NGOC` | Điều phối menu `CHON_AI_DAY_NGOC` |
| **`508`** | `CHON_AI_DAY_HONG_NGOC` | Điều phối menu `CHON_AI_DAY_HONG_NGOC` |
| **`2828509`** | `KEO_BUA_BAO_CUSTOM_BET` | Điều phối menu `KEO_BUA_BAO_CUSTOM_BET` |
| **`509`** | `UPDATE_CHON_AI_DAY_NGOC` | Điều phối menu `UPDATE_CHON_AI_DAY_NGOC` |
| **`1001`** | `MENU_NANG_GIOI_HAN` | Điều phối menu `MENU_NANG_GIOI_HAN` |
| **`838282`** | `QUEST_CHA_GIO` | Điều phối menu `QUEST_CHA_GIO` |
| **`938282`** | `QUEST_CHA_GIO1` | Điều phối menu `QUEST_CHA_GIO1` |
| **`977723`** | `TALK_MENU` | Điều phối menu `TALK_MENU` |
| **`977883`** | `MAY_DAM` | Điều phối menu `MAY_DAM` |
| **`9778839`** | `PHAO_BONG` | Điều phối menu `PHAO_BONG` |
| **`9778683`** | `PHAO_BONG_VIP` | Điều phối menu `PHAO_BONG_VIP` |
| **`977183`** | `GOKU_DAY` | Điều phối menu `GOKU_DAY` |
| **`1001`** | `MENU_OPEN_MMB_WITH_JAR` | Điều phối menu `MENU_OPEN_MMB_WITH_JAR` |
| **`1002`** | `MENU_OPEN_MMB_NO_JAR` | Điều phối menu `MENU_OPEN_MMB_NO_JAR` |
| **`1003`** | `MENU_NOT_OPEN_MMB_WITH_JAR` | Điều phối menu `MENU_NOT_OPEN_MMB_WITH_JAR` |
| **`1004`** | `MENU_NOT_OPEN_MMB_NO_JAR` | Điều phối menu `MENU_NOT_OPEN_MMB_NO_JAR` |
| **`1005`** | `BUA_HO_TRO` | Điều phối menu `BUA_HO_TRO` |
| **`1006`** | `BINH_HUT_NANG_LUONG` | Điều phối menu `BINH_HUT_NANG_LUONG` |
| **`1007`** | `KOL_QUEST_MENU` | Điều phối menu `KOL_QUEST_MENU` |
| **`1008`** | `KOL_VIP_REWARD_MENU` | Điều phối menu `KOL_VIP_REWARD_MENU` |
| **`1009`** | `KOL_QUEST_TYPE_ITEM_COLLECTION` | Điều phối menu `KOL_QUEST_TYPE_ITEM_COLLECTION` |
| **`1010`** | `KOL_QUEST_TYPE_DUNGEON_COMPLETION` | Điều phối menu `KOL_QUEST_TYPE_DUNGEON_COMPLETION` |
| **`1011`** | `KOL_QUEST_TYPE_PVP_WINS` | Điều phối menu `KOL_QUEST_TYPE_PVP_WINS` |
| **`1012`** | `KOL_QUEST_TYPE_DAILY_QUEST_COMPLETION` | Điều phối menu `KOL_QUEST_TYPE_DAILY_QUEST_COMPLETION` |
| **`1013`** | `KOL_QUEST_TYPE_BOSS_DEFEAT_PARTICIPATION` | Điều phối menu `KOL_QUEST_TYPE_BOSS_DEFEAT_PARTICIPATION` |
| **`1014`** | `KOL_QUEST_TYPE_MONSTER_KILL_COUNT` | Điều phối menu `KOL_QUEST_TYPE_MONSTER_KILL_COUNT` |

---

## 5. Toàn Bộ 1.225 Hằng Số Vật Phẩm (`ConstItem.java`)

> Danh mục toàn bộ các hằng số vật phẩm đã được định nghĩa trong `ConstItem.java` theo thứ tự ID tăng dần.

| ID Vật Phẩm | Tên Hằng Số trong `ConstItem.java` |
| :---: | :--- |
| **`0`** | `AO_VAI_3_LO` |
| **`1`** | `AO_SOI_LEN` |
| **`2`** | `AO_VAI_THO` |
| **`3`** | `AO_VAI_DAY` |
| **`4`** | `AO_LEN_PICO` |
| **`5`** | `AO_GIAP_SAT` |
| **`6`** | `QUAN_VAI_DEN` |
| **`7`** | `QUAN_SOI_LEN` |
| **`8`** | `QUAN_VAI_THO` |
| **`9`** | `QUAN_VAI_DAY` |
| **`10`** | `QUAN_VAI_THO_PICO` |
| **`11`** | `QUAN_GIAP_SAT` |
| **`12`** | `RADA_CAP_1` |
| **`13`** | `DAU_THAN_CAP_1` |
| **`14`** | `NGOC_RONG_1_SAO` |
| **`15`** | `NGOC_RONG_2_SAO` |
| **`16`** | `NGOC_RONG_3_SAO` |
| **`17`** | `NGOC_RONG_4_SAO` |
| **`18`** | `NGOC_RONG_5_SAO` |
| **`19`** | `NGOC_RONG_6_SAO` |
| **`20`** | `NGOC_RONG_7_SAO` |
| **`21`** | `GANG_VAI_DEN` |
| **`22`** | `GANG_SOI_LEN` |
| **`23`** | `GANG_VAI_THO` |
| **`24`** | `GANG_THUN_DEN` |
| **`25`** | `GANG_LEN_PICO` |
| **`26`** | `GANG_SAT` |
| **`27`** | `GIAY_NHUA` |
| **`28`** | `GIAY_SOI_LEN` |
| **`29`** | `GIAY_VAI_THO` |
| **`30`** | `GIAY_CAO_SU` |
| **`31`** | `GIAY_NHUA_PICO` |
| **`32`** | `GIAY_SAT` |
| **`33`** | `AO_THUN_3_LO` |
| **`34`** | `AO_THUN_DAY` |
| **`35`** | `QUAN_THUN_DEN` |
| **`36`** | `QUAN_THUN_DAY` |
| **`37`** | `GANG_VAI_DAY` |
| **`38`** | `GANG_THUN_DAY` |
| **`39`** | `GIAY_NHUA_DE_DAY` |
| **`40`** | `GIAY_CAO_SU_DE_DAY` |
| **`41`** | `AO_SOI_GAI` |
| **`42`** | `AO_THUN_PICO` |
| **`43`** | `QUAN_SOI_GAI` |
| **`44`** | `QUAN_THUN_PICO` |
| **`45`** | `GANG_THUN_PICO` |
| **`46`** | `GANG_SOI_GAI` |
| **`47`** | `GIAY_SOI_GAI` |
| **`48`** | `GIAY_CAO_SU_PICO` |
| **`49`** | `AO_THUN_THO` |
| **`50`** | `AO_GIAP_DONG` |
| **`51`** | `QUAN_THUN_THO` |
| **`52`** | `QUAN_GIAP_DONG` |
| **`53`** | `GANG_THUN_THO` |
| **`54`** | `GANG_DONG` |
| **`55`** | `GIAY_CAO_SU_THO` |
| **`56`** | `GIAY_DONG` |
| **`57`** | `RADA_CAP_2` |
| **`58`** | `RADA_CAP_3` |
| **`59`** | `RADA_CAP_4` |
| **`60`** | `DAU_THAN_CAP_2` |
| **`61`** | `DAU_THAN_CAP_3` |
| **`62`** | `DAU_THAN_CAP_4` |
| **`63`** | `DAU_THAN_CAP_5` |
| **`64`** | `DAU_THAN_CAP_6` |
| **`65`** | `DAU_THAN_CAP_7` |
| **`66`** | `SACH_DAM_DRAGON_LV1` |
| **`67`** | `SACH_DAM_DRAGON_LV2` |
| **`68`** | `SACH_DAM_DRAGON_LV3` |
| **`69`** | `SACH_DAM_DRAGON_LV4` |
| **`70`** | `SACH_DAM_DRAGON_LV5` |
| **`71`** | `SACH_DAM_DRAGON_LV6` |
| **`72`** | `SACH_DAM_DRAGON_LV7` |
| **`73`** | `DUI_GA` |
| **`74`** | `DUI_GA_NUONG` |
| **`75`** | `DUI_HEO_XAYDA` |
| **`76`** | `VANG` |
| **`77`** | `NGOC` |
| **`78`** | `DUA_BE` |
| **`79`** | `SACH_DAM_DEMON_LV1` |
| **`80`** | `SACH_DAM_DEMON_LV2` |
| **`81`** | `SACH_DAM_DEMON_LV3` |
| **`82`** | `SACH_DAM_DEMON_LV4` |
| **`83`** | `SACH_DAM_DEMON_LV5` |
| **`84`** | `SACH_DAM_DEMON_LV6` |
| **`85`** | `TRUYEN_TRANH` |
| **`86`** | `SACH_DAM_DEMON_LV7` |
| **`87`** | `SACH_DAM_GALICK_LV1` |
| **`88`** | `SACH_DAM_GALICK_LV2` |
| **`89`** | `SACH_DAM_GALICK_LV3` |
| **`90`** | `SACH_DAM_GALICK_LV4` |
| **`91`** | `SACH_DAM_GALICK_LV5` |
| **`92`** | `SACH_DAM_GALICK_LV6` |
| **`93`** | `SACH_DAM_GALICK_LV7` |
| **`94`** | `SACH_KAMEJOKO_LV1` |
| **`95`** | `SACH_KAMEJOKO_LV2` |
| **`96`** | `SACH_KAMEJOKO_LV3` |
| **`97`** | `SACH_KAMEJOKO_LV4` |
| **`98`** | `SACH_KAMEJOKO_LV5` |
| **`99`** | `SACH_KAMEJOKO_LV6` |
| **`100`** | `SACH_KAMEJOKO_LV7` |
| **`101`** | `SACH_MASENKO_LV1` |
| **`102`** | `SACH_MASENKO_LV2` |
| **`103`** | `SACH_MASENKO_LV3` |
| **`104`** | `SACH_MASENKO_LV4` |
| **`105`** | `SACH_MASENKO_LV5` |
| **`106`** | `SACH_MASENKO_LV6` |
| **`107`** | `SACH_MASENKO_LV7` |
| **`108`** | `SACH_ANTOMIC_LV1` |
| **`109`** | `SACH_ANTOMIC_LV2` |
| **`110`** | `SACH_ANTOMIC_LV3` |
| **`111`** | `SACH_ANTOMIC_LV4` |
| **`112`** | `SACH_ANTOMIC_LV5` |
| **`113`** | `SACH_ANTOMIC_LV6` |
| **`114`** | `SACH_ANTOMIC_LV7` |
| **`115`** | `THAI_DUONG_HA_SAN_LV1` |
| **`116`** | `THAI_DUONG_HA_SAN_LV2` |
| **`117`** | `THAI_DUONG_HA_SAN_LV3` |
| **`118`** | `THAI_DUONG_HA_SAN_LV4` |
| **`119`** | `THAI_DUONG_HA_SAN_LV5` |
| **`120`** | `THAI_DUONG_HA_SAN_LV6` |
| **`121`** | `THAI_DUONG_HA_SAN_LV7` |
| **`122`** | `SACH_HOC_TRI_THUONG_LV1` |
| **`123`** | `SACH_HOC_TRI_THUONG_LV2` |
| **`124`** | `SACH_HOC_TRI_THUONG_LV3` |
| **`125`** | `SACH_HOC_TRI_THUONG_LV4` |
| **`126`** | `SACH_HOC_TRI_THUONG_LV5` |
| **`127`** | `SACH_HOC_TRI_THUONG_LV6` |
| **`128`** | `SACH_HOC_TRI_THUONG_LV7` |
| **`129`** | `TAI_TAO_NANG_LUONG_LV1` |
| **`130`** | `TAI_TAO_NANG_LUONG_LV2` |
| **`131`** | `TAI_TAO_NANG_LUONG_LV3` |
| **`132`** | `TAI_TAO_NANG_LUONG_LV4` |
| **`133`** | `TAI_TAO_NANG_LUONG_LV5` |
| **`134`** | `TAI_TAO_NANG_LUONG_LV6` |
| **`135`** | `TAI_TAO_NANG_LUONG_LV7` |
| **`136`** | `AO_VAI_KAME` |
| **`137`** | `AO_THUN_KAME` |
| **`138`** | `AO_VO_KAME` |
| **`139`** | `AO_VO_GOKU` |
| **`140`** | `QUAN_VAI_KAME` |
| **`141`** | `QUAN_THUN_KAME` |
| **`142`** | `QUAN_VO_KAME` |
| **`143`** | `QUAN_VO_GOKU` |
| **`144`** | `GANG_VAI_KAME` |
| **`145`** | `GANG_THUN_KAME` |
| **`146`** | `GANG_VO_KAME` |
| **`147`** | `GANG_VO_GOKU` |
| **`148`** | `GIAY_NHUA_KAME` |
| **`149`** | `GIAY_CAO_SU_KAME` |
| **`150`** | `GIAY_VO_KAME` |
| **`151`** | `GIAY_VO_GOKU` |
| **`152`** | `AO_CHOANG_LEN` |
| **`153`** | `AO_CHOANG_THUN` |
| **`154`** | `AO_VAI_PICO` |
| **`155`** | `AO_DA_PICO` |
| **`156`** | `QUAN_LEN_CUNG` |
| **`157`** | `QUAN_THUN_CUNG` |
| **`158`** | `QUAN_VAI_CUNG_PICO` |
| **`159`** | `QUAN_VAI_MEM_PICO` |
| **`160`** | `GANG_LEN_CUNG` |
| **`161`** | `GANG_THUN_CUNG` |
| **`162`** | `GANG_VAI_PICO` |
| **`163`** | `GANG_DA_PICO` |
| **`164`** | `GIAY_NHUA_CUNG` |
| **`165`** | `GIAY_CAO_SU_CUNG` |
| **`166`** | `GIAY_DA_PICO` |
| **`167`** | `GIAY_SAT_PICO` |
| **`168`** | `AO_GIAP_BAC` |
| **`169`** | `AO_GIAP_VANG` |
| **`170`** | `AO_LONG_XAYDA` |
| **`171`** | `AO_KHOAC_XAYDA` |
| **`172`** | `QUAN_GIAP_BAC` |
| **`173`** | `QUAN_GIAP_VANG` |
| **`174`** | `QUAN_LONG_XAYDA` |
| **`175`** | `QUAN_DA_XAYDA` |
| **`176`** | `GANG_BAC` |
| **`177`** | `GANG_VANG` |
| **`178`** | `GANG_LONG_XAYDA` |
| **`179`** | `GANG_DA_XAYDA` |
| **`180`** | `GIAY_BAC` |
| **`181`** | `GIAY_VANG` |
| **`182`** | `GIAY_LONG_XAYDA` |
| **`183`** | `GIAY_DA_XAYDA` |
| **`184`** | `RADA_CAP_5` |
| **`185`** | `RADA_CAP_6` |
| **`186`** | `RADA_CAP_7` |
| **`187`** | `RADA_CAP_8` |
| **`188`** | `VANG_188` |
| **`189`** | `VANG_189` |
| **`190`** | `VANG_190` |
| **`191`** | `CA_CHUA` |
| **`192`** | `CA_ROT` |
| **`193`** | `GOI_10_VIEN_CAPSULE` |
| **`194`** | `VIEN_CAPSULE_DAC_BIET` |
| **`195`** | `SIEU_THANH_THUY` |
| **`196`** | `AVATAR` |
| **`197`** | `AVATAR_197` |
| **`198`** | `AVATAR_198` |
| **`199`** | `AVATAR_199` |
| **`200`** | `AVATAR_200` |
| **`201`** | `AVATAR_201` |
| **`202`** | `AVATAR_202` |
| **`203`** | `AVATAR_203` |
| **`204`** | `AVATAR_204` |
| **`205`** | `AVATAR_205` |
| **`206`** | `AVATAR_206` |
| **`207`** | `AVATAR_207` |
| **`208`** | `AVATAR_208` |
| **`209`** | `AVATAR_209` |
| **`210`** | `AVATAR_210` |
| **`211`** | `NHO_TIM` |
| **`212`** | `NHO_XANH` |
| **`213`** | `BUA_TRI_TUE` |
| **`214`** | `BUA_MANH_ME` |
| **`215`** | `BUA_DA_TRAU` |
| **`216`** | `BUA_OAI_HUNG` |
| **`217`** | `BUA_BAT_TU` |
| **`218`** | `BUA_DEO_DAI` |
| **`219`** | `BUA_THU_HUT` |
| **`220`** | `DA_LUC_BAO` |
| **`221`** | `DA_SAPHIA` |
| **`222`** | `DA_RUBY` |
| **`223`** | `DA_TITAN` |
| **`224`** | `DA_THACH_ANH_TIM` |
| **`225`** | `MANH_DA_VUN` |
| **`226`** | `BINH_NUOC_PHEP` |
| **`227`** | `AVATAR_VIP` |
| **`228`** | `AVATAR_VIP_228` |
| **`229`** | `AVATAR_VIP_229` |
| **`230`** | `AO_BAC_GOKU` |
| **`231`** | `AO_VANG_GOKU` |
| **`232`** | `AO_DA_CALIC` |
| **`233`** | `AO_JEAN_CALIC` |
| **`234`** | `AO_SAT_TRON` |
| **`235`** | `AO_DONG_TRON` |
| **`236`** | `AO_BAC_ZEALOT` |
| **`237`** | `AO_VANG_ZEALOT` |
| **`238`** | `AO_LONG_DO` |
| **`239`** | `AO_SIEU_XAYDA` |
| **`240`** | `AO_KAIO` |
| **`241`** | `AO_LUONG_LONG` |
| **`242`** | `QUAN_BAC_GOKU` |
| **`243`** | `QUAN_VANG_GOKU` |
| **`244`** | `QUAN_DA_CALIC` |
| **`245`** | `QUAN_JEAN_CALIC` |
| **`246`** | `QUAN_SAT_TRON` |
| **`247`** | `QUAN_DONG_TRON` |
| **`248`** | `QUAN_BAC_ZEALOT` |
| **`249`** | `QUAN_VANG_ZEALOT` |
| **`250`** | `QUAN_LONG_DO` |
| **`251`** | `QUAN_SIEU_XAYDA` |
| **`252`** | `QUAN_KAIO` |
| **`253`** | `QUAN_LUONG_LONG` |
| **`254`** | `GANG_BAC_GOKU` |
| **`255`** | `GANG_VANG_GOKU` |
| **`256`** | `GANG_DA_CALIC` |
| **`257`** | `GANG_JEAN_CALIC` |
| **`258`** | `GANG_SAT_TRON` |
| **`259`** | `GANG_DONG_TRON` |
| **`260`** | `GANG_BAC_ZEALOT` |
| **`261`** | `GANG_VANG_ZEALOT` |
| **`262`** | `GANG_LONG_DO` |
| **`263`** | `GANG_SIEU_XAYDA` |
| **`264`** | `GANG_KAIO` |
| **`265`** | `GANG_LUONG_LONG` |
| **`266`** | `GIAY_BAC_GOKU` |
| **`267`** | `GIAY_VANG_GOKU` |
| **`268`** | `GIAY_DA_CALIC` |
| **`269`** | `GIAY_JEAN_CALIC` |
| **`270`** | `GIAY_SAT_TRON` |
| **`271`** | `GIAY_DONG_TRON` |
| **`272`** | `GIAY_BAC_ZEALOT` |
| **`273`** | `GIAY_VANG_ZEALOT` |
| **`274`** | `GIAY_LONG_DO` |
| **`275`** | `GIAY_SIEU_XAYDA` |
| **`276`** | `GIAY_KAIO` |
| **`277`** | `GIAY_LUONG_LONG` |
| **`278`** | `RADA_CAP_9` |
| **`279`** | `RADA_CAP_10` |
| **`280`** | `RADA_CAP_11` |
| **`281`** | `RADA_CAP_12` |
| **`282`** | `CAI_TRANG_THAN_VU_TRU` |
| **`283`** | `CAI_TRANG_YARIROBE` |
| **`284`** | `CAI_TRANG_TAU_PAY_PAY` |
| **`285`** | `CAI_TRANG_THAN_MEO_KARIN` |
| **`286`** | `CAI_TRANG_THUONG_DE` |
| **`287`** | `CAI_TRANG_POPO` |
| **`288`** | `CAI_TRANG_KUKU` |
| **`289`** | `CAI_TRANG_RAMBO` |
| **`290`** | `CAI_TRANG_SO_2` |
| **`291`** | `CAI_TRANG_NINJA_AO_TIM` |
| **`292`** | `CAI_TRANG_SO_3` |
| **`293`** | `GOI_30_DAU_THAN_CAP_1` |
| **`294`** | `GOI_30_DAU_THAN_CAP_2` |
| **`295`** | `GOI_30_DAU_THAN_CAP_3` |
| **`296`** | `GOI_30_DAU_THAN_CAP_4` |
| **`297`** | `GOI_30_DAU_THAN_CAP_5` |
| **`298`** | `GOI_30_DAU_THAN_CAP_6` |
| **`299`** | `GOI_30_DAU_THAN_CAP_7` |
| **`300`** | `KAIOKEN_LV1` |
| **`301`** | `KAIOKEN_LV2` |
| **`302`** | `KAIOKEN_LV3` |
| **`303`** | `KAIOKEN_LV4` |
| **`304`** | `KAIOKEN_LV5` |
| **`305`** | `KAIOKEN_LV6` |
| **`306`** | `KAIOKEN_LV7` |
| **`307`** | `QUA_CAU_KENH_KHI_LV1` |
| **`308`** | `QUA_CAU_KENH_KHI_LV2` |
| **`309`** | `QUA_CAU_KENH_KHI_LV3` |
| **`310`** | `QUA_CAU_KENH_KHI_LV4` |
| **`311`** | `QUA_CAU_KENH_KHI_LV5` |
| **`312`** | `QUA_CAU_KENH_KHI_LV6` |
| **`313`** | `QUA_CAU_KENH_KHI_LV7` |
| **`314`** | `HOA_KHI_KHONG_LO_LV1` |
| **`315`** | `HOA_KHI_KHONG_LO_LV2` |
| **`316`** | `HOA_KHI_KHONG_LO_LV3` |
| **`317`** | `HOA_KHI_KHONG_LO_LV4` |
| **`318`** | `HOA_KHI_KHONG_LO_LV5` |
| **`319`** | `HOA_KHI_KHONG_LO_LV6` |
| **`320`** | `HOA_KHI_KHONG_LO_LV7` |
| **`321`** | `BOM_HI_SINH_LV1` |
| **`322`** | `BOM_HI_SINH_LV2` |
| **`323`** | `BOM_HI_SINH_LV3` |
| **`324`** | `BOM_HI_SINH_LV4` |
| **`325`** | `BOM_HI_SINH_LV5` |
| **`326`** | `BOM_HI_SINH_LV6` |
| **`327`** | `BOM_HI_SINH_LV7` |
| **`328`** | `MAKANKOSAPPO_LV1` |
| **`329`** | `MAKANKOSAPPO_LV2` |
| **`330`** | `MAKANKOSAPPO_LV3` |
| **`331`** | `MAKANKOSAPPO_LV4` |
| **`332`** | `MAKANKOSAPPO_LV5` |
| **`333`** | `MAKANKOSAPPO_LV6` |
| **`334`** | `MAKANKOSAPPO_LV7` |
| **`335`** | `DE_TRUNG_LV1` |
| **`336`** | `DE_TRUNG_LV2` |
| **`337`** | `DE_TRUNG_LV3` |
| **`338`** | `DE_TRUNG_LV4` |
| **`339`** | `DE_TRUNG_LV5` |
| **`340`** | `DE_TRUNG_LV6` |
| **`341`** | `DE_TRUNG_LV7` |
| **`342`** | `VE_TINH_TRI_LUC` |
| **`343`** | `VE_TINH_TRI_TUE` |
| **`344`** | `VE_TINH_PHONG_THU` |
| **`345`** | `VE_TINH_SINH_LUC` |
| **`346`** | `CAN_DAU_VAN` |
| **`347`** | `PHI_LONG` |
| **`348`** | `VAN_BAY` |
| **`349`** | `CAN_DAU_VAN_VIP` |
| **`350`** | `PHI_LONG_VIP` |
| **`351`** | `VAN_BAY_VIP` |
| **`352`** | `DAU_THAN_CAP_8` |
| **`353`** | `NGOC_RONG_NAMEK_1_SAO` |
| **`354`** | `NGOC_RONG_NAMEK_2_SAO` |
| **`355`** | `NGOC_RONG_NAMEK_3_SAO` |
| **`356`** | `NGOC_RONG_NAMEK_4_SAO` |
| **`357`** | `NGOC_RONG_NAMEK_5_SAO` |
| **`358`** | `NGOC_RONG_NAMEK_6_SAO` |
| **`359`** | `NGOC_RONG_NAMEK_7_SAO` |
| **`360`** | `NGOC_RONG_NAMEK` |
| **`361`** | `GOI_10_RADA_DO_NGOC` |
| **`362`** | `HOA_THACH_NGOC_RONG` |
| **`363`** | `THAO_CO` |
| **`364`** | `CO_XANH` |
| **`365`** | `CO_DO` |
| **`366`** | `CO_TIM` |
| **`367`** | `CO_VANG` |
| **`368`** | `CO_LUC` |
| **`369`** | `CO_HONG` |
| **`370`** | `CO_CAM` |
| **`371`** | `CO_XAM` |
| **`372`** | `NGOC_RONG_1_SAO_DEN` |
| **`373`** | `NGOC_RONG_2_SAO_DEN` |
| **`374`** | `NGOC_RONG_3_SAO_DEN` |
| **`375`** | `NGOC_RONG_4_SAO_DEN` |
| **`376`** | `NGOC_RONG_5_SAO_DEN` |
| **`377`** | `NGOC_RONG_6_SAO_DEN` |
| **`378`** | `NGOC_RONG_7_SAO_DEN` |
| **`379`** | `MAY_DO_CAPSULE_KI_BI` |
| **`380`** | `VIEN_CAPSULE_KI_BI` |
| **`381`** | `CUONG_NO` |
| **`382`** | `BO_HUYET` |
| **`383`** | `BO_KHI` |
| **`384`** | `GIAP_XEN_BO_HUNG` |
| **`385`** | `AN_DANH` |
| **`386`** | `NON_NOEL_XAM` |
| **`387`** | `NON_NOEL_DO` |
| **`388`** | `NON_NOEL_XANH` |
| **`389`** | `NON_NOEL_XAM_389` |
| **`390`** | `NON_NOEL_DO_390` |
| **`391`** | `NON_NOEL_XANH_391` |
| **`392`** | `NON_NOEL_XAM_392` |
| **`393`** | `NON_NOEL_DO_393` |
| **`394`** | `NON_NOEL_XANH_394` |
| **`395`** | `NHAN_VAT_BI_AN` |
| **`396`** | `THU_CUOI_CUC_VIP` |
| **`397`** | `HOP_QUA_TET` |
| **`398`** | `HOP_QUA_TET_398` |
| **`399`** | `THIEP_CHUC_TET` |
| **`400`** | `DAT_TEN_DE_TU` |
| **`401`** | `DOI_DE_TU` |
| **`402`** | `NANG_CAP_KY_NANG_1_DE_TU` |
| **`403`** | `NANG_CAP_KY_NANG_2_DE_TU` |
| **`404`** | `NANG_CAP_KY_NANG_3_DE_TU` |
| **`405`** | `CAI_TRANG_FIDE_CAP_1` |
| **`406`** | `CAI_TRANG_FIDE_CAP_2` |
| **`407`** | `CAI_TRANG_FIDE_CAP_3` |
| **`408`** | `CAI_TRANG_CHI_CHI` |
| **`409`** | `CAI_TRANG_BUM_MA` |
| **`410`** | `CAI_TRANG_LY_TIEU_NUONG` |
| **`411`** | `CAI_TRANG_POC` |
| **`412`** | `AVATAR_412` |
| **`413`** | `AVATAR_413` |
| **`414`** | `AVATAR_414` |
| **`415`** | `AVATAR_415` |
| **`416`** | `AVATAR_416` |
| **`417`** | `AVATAR_417` |
| **`418`** | `AVATAR_418` |
| **`419`** | `AVATAR_419` |
| **`420`** | `AVATAR_420` |
| **`421`** | `CAI_TRANG_SON_TINH` |
| **`422`** | `CAI_TRANG_THUY_TINH` |
| **`423`** | `CAI_TRANG_BUJIN` |
| **`424`** | `CAI_TRANG_KOGU` |
| **`425`** | `CAI_TRANG_ZANGYA` |
| **`426`** | `CAI_TRANG_BIDO` |
| **`427`** | `CAI_TRANG_BOJACK` |
| **`428`** | `CAI_TRANG_SUPER_BOJACK` |
| **`429`** | `CAI_TRANG_SO_4` |
| **`430`** | `CAI_TRANG_SO_3_430` |
| **`431`** | `CAI_TRANG_SO_2_431` |
| **`432`** | `CAI_TRANG_SO_1` |
| **`433`** | `CAI_TRANG_TIEU_DOI_TRUONG_GINYU` |
| **`434`** | `KHIEN_NANG_LUONG_LV1` |
| **`435`** | `KHIEN_NANG_LUONG_LV2` |
| **`436`** | `KHIEN_NANG_LUONG_LV3` |
| **`437`** | `KHIEN_NANG_LUONG_LV4` |
| **`438`** | `KHIEN_NANG_LUONG_LV5` |
| **`439`** | `KHIEN_NANG_LUONG_LV6` |
| **`440`** | `KHIEN_NANG_LUONG_LV7` |
| **`441`** | `SAO_PHA_LE` |
| **`442`** | `SAO_PHA_LE_442` |
| **`443`** | `SAO_PHA_LE_443` |
| **`444`** | `SAO_PHA_LE_444` |
| **`445`** | `SAO_PHA_LE_445` |
| **`446`** | `SAO_PHA_LE_446` |
| **`447`** | `SAO_PHA_LE_447` |
| **`448`** | `CAI_TRANG_DRACULA` |
| **`449`** | `CAI_TRANG_NGUOI_VO_HINH` |
| **`450`** | `CAI_TRANG_BONG_BANG` |
| **`451`** | `CAI_TRANG_VUA_QUY_SATAN` |
| **`452`** | `CAI_TRANG_THO_DAU_BAC` |
| **`453`** | `CHIEN_THUYEN_TENNIS` |
| **`454`** | `BONG_TAI_PORATA` |
| **`455`** | `CAI_TRANG_O_DO` |
| **`456`** | `BINH_NUOC` |
| **`457`** | `THOI_VANG` |
| **`458`** | `CAI_TRANG_XINBATO` |
| **`459`** | `PHIEU_GIAM_GIA` |
| **`460`** | `CUC_XUONG` |
| **`461`** | `CAI_TRANG_SOI_HEC_QUYN` |
| **`462`** | `CU_CA_ROT` |
| **`463`** | `CAI_TRANG_THO_DAI_CA` |
| **`464`** | `CAI_TRANG_THO_BUNMA` |
| **`465`** | `BANH_TRUNG_THU_1_TRUNG` |
| **`466`** | `BANH_TRUNG_THU_2_TRUNG` |
| **`467`** | `LONG_DEN_ONG_SAO` |
| **`468`** | `LONG_DEN_CA_CHEP` |
| **`469`** | `LONG_DEN_KEO_QUAN` |
| **`470`** | `LONG_DEN_ONG_TRANG` |
| **`471`** | `LONG_DEN_HOI_AN` |
| **`472`** | `BANH_TRUNG_THU_DAC_BIET` |
| **`473`** | `HOP_BANH_TRUNG_THU` |
| **`474`** | `SACH_BIEN_SOCOLA_LV1` |
| **`475`** | `SACH_BIEN_SOCOLA_LV2` |
| **`476`** | `SACH_BIEN_SOCOLA_LV3` |
| **`477`** | `SACH_BIEN_SOCOLA_LV4` |
| **`478`** | `SACH_BIEN_SOCOLA_LV5` |
| **`479`** | `SACH_BIEN_SOCOLA_LV6` |
| **`480`** | `SACH_BIEN_SOCOLA_LV7` |
| **`481`** | `SACH_LIEN_HOAN_LV1` |
| **`482`** | `SACH_LIEN_HOAN_LV2` |
| **`483`** | `SACH_LIEN_HOAN_LV3` |
| **`484`** | `SACH_LIEN_HOAN_LV4` |
| **`485`** | `SACH_LIEN_HOAN_LV5` |
| **`486`** | `SACH_LIEN_HOAN_LV6` |
| **`487`** | `SACH_LIEN_HOAN_LV7` |
| **`488`** | `SACH_DICH_CHUYEN_LV1` |
| **`489`** | `SACH_DICH_CHUYEN_LV2` |
| **`490`** | `SACH_DICH_CHUYEN_LV3` |
| **`491`** | `SACH_DICH_CHUYEN_LV4` |
| **`492`** | `SACH_DICH_CHUYEN_LV5` |
| **`493`** | `SACH_DICH_CHUYEN_LV6` |
| **`494`** | `SACH_DICH_CHUYEN_LV7` |
| **`495`** | `SACH_THOI_MIEN_LV1` |
| **`496`** | `SACH_THOI_MIEN_LV2` |
| **`497`** | `SACH_THOI_MIEN_LV3` |
| **`498`** | `SACH_THOI_MIEN_LV4` |
| **`499`** | `SACH_THOI_MIEN_LV5` |
| **`500`** | `SACH_THOI_MIEN_LV6` |
| **`501`** | `SACH_THOI_MIEN_LV7` |
| **`502`** | `SACH_TROI_LV1` |
| **`503`** | `SACH_TROI_LV2` |
| **`504`** | `SACH_TROI_LV3` |
| **`505`** | `SACH_TROI_LV4` |
| **`506`** | `SACH_TROI_LV5` |
| **`507`** | `SACH_TROI_LV6` |
| **`508`** | `SACH_TROI_LV7` |
| **`509`** | `SACH_HUYT_SAO_LV1` |
| **`510`** | `SACH_HUYT_SAO_LV2` |
| **`511`** | `SACH_HUYT_SAO_LV3` |
| **`512`** | `SACH_HUYT_SAO_LV4` |
| **`513`** | `SACH_HUYT_SAO_LV5` |
| **`514`** | `SACH_HUYT_SAO_LV6` |
| **`515`** | `SACH_HUYT_SAO_LV7` |
| **`516`** | `SOCOLA` |
| **`517`** | `MO_RONG_HANH_TRANG` |
| **`518`** | `MO_RONG_RUONG_DO` |
| **`519`** | `CO_KAIO` |
| **`520`** | `CO_MABU` |
| **`521`** | `TU_DONG_LUYEN_TAP` |
| **`522`** | `BUA_DE_TU` |
| **`523`** | `DAU_THAN_CAP_9` |
| **`524`** | `CAI_TRANG_ANDROID_19` |
| **`525`** | `CAI_TRANG_ANDROID_20` |
| **`526`** | `CAI_TRANG_XEN_BO_HUNG_1` |
| **`527`** | `CAI_TRANG_XEN_BO_HUNG_2` |
| **`528`** | `CAI_TRANG_XEN_BO_HUNG_3` |
| **`529`** | `GIAP_TAP_LUYEN_CAP_1` |
| **`530`** | `GIAP_TAP_LUYEN_CAP_2` |
| **`531`** | `GIAP_TAP_LUYEN_CAP_3` |
| **`532`** | `QUY_CHIM` |
| **`533`** | `KEO_GIANG_SINH` |
| **`534`** | `GIAP_TAP_LUYEN_CAP_1_534` |
| **`535`** | `GIAP_TAP_LUYEN_CAP_2_535` |
| **`536`** | `GIAP_TAP_LUYEN_CAP_3_536` |
| **`537`** | `CHU_GIAI` |
| **`538`** | `CHU_KHAI` |
| **`539`** | `CHU_PHONG` |
| **`540`** | `CHU_AN` |
| **`541`** | `QUA_HONG_DAO` |
| **`542`** | `QUA_HONG_DAO_CHIN` |
| **`543`** | `VONG_KIM_CO` |
| **`544`** | `CAI_TRANG_TON_NGO_KHONG` |
| **`545`** | `CAI_TRANG_TON_NGO_KHONG_545` |
| **`546`** | `CAI_TRANG_TON_NGO_KHONG_546` |
| **`547`** | `CAI_TRANG_TON_NGO_KHONG_DE_TU` |
| **`548`** | `CAI_TRANG_BAT_GIOI_DE_TU` |
| **`549`** | `CAI_TRANG_XEN_BO_HUNG_CON` |
| **`550`** | `CAI_TRANG_ANDROID_13` |
| **`551`** | `CAI_TRANG_ANDROID_14` |
| **`552`** | `CAI_TRANG_ANDROID_15` |
| **`553`** | `PHAO_BONG` |
| **`554`** | `SIEU_PHAO_BONG_X100` |
| **`555`** | `AO_THAN_LINH` |
| **`556`** | `QUAN_THAN_LINH` |
| **`557`** | `AO_THAN_NAMEC` |
| **`558`** | `QUAN_THAN_NAMEC` |
| **`559`** | `AO_THAN_XAYDA` |
| **`560`** | `QUAN_THAN_XAYDA` |
| **`561`** | `NHAN_THAN_LINH` |
| **`562`** | `GANG_THAN_LINH` |
| **`563`** | `GIAY_THAN_LINH` |
| **`564`** | `GANG_THAN_NAMEC` |
| **`565`** | `GIAY_THAN_NAMEC` |
| **`566`** | `GANG_THAN_XAYDA` |
| **`567`** | `GIAY_THAN_XAYDA` |
| **`568`** | `QUA_TRUNG` |
| **`569`** | `DUA_HAU` |
| **`570`** | `RUONG_GO` |
| **`571`** | `RUONG_BAC` |
| **`572`** | `RUONG_VANG` |
| **`573`** | `CAPSULE_BAC` |
| **`574`** | `CAPSULE_VANG` |
| **`575`** | `CAI_TRANG_BUI_BUI` |
| **`576`** | `CAI_TRANG_YACON` |
| **`577`** | `CAI_TRANG_DRABURA` |
| **`578`** | `CAI_TRANG_MABU_MAP` |
| **`579`** | `DUOI_KHI` |
| **`580`** | `CAI_TRANG_CHI_CHI_2` |
| **`581`** | `CAI_TRANG_BUM_MA_2` |
| **`582`** | `CAI_TRANG_LY_TIEU_NUONG_2` |
| **`583`** | `CAI_TRANG_POC_2` |
| **`584`** | `CAI_TRANG_THO_BUNMA_2` |
| **`585`** | `BI_NGO` |
| **`586`** | `CAI_TRANG_BONG_BANG_HALLOWEEN` |
| **`587`** | `CAI_TRANG_VUA_QUY_SATAN_HALLOWEEN` |
| **`588`** | `CAI_TRANG_DRACULA_HALLOWEEN` |
| **`589`** | `BONG_HOA` |
| **`590`** | `BI_KIEP` |
| **`591`** | `CAI_TRANG_SIEU_THAN` |
| **`592`** | `CAI_TRANG_YARDRAT` |
| **`593`** | `CAI_TRANG_YARDRAT_593` |
| **`594`** | `CAI_TRANG_YARDRAT_594` |
| **`595`** | `DAU_THAN_CAP_10` |
| **`596`** | `GOI_30_DAU_THAN_CAP_8` |
| **`597`** | `GOI_30_DAU_THAN_CAP_9` |
| **`598`** | `AVATAR_598` |
| **`599`** | `AVATAR_599` |
| **`600`** | `AVATAR_600` |
| **`601`** | `CAI_TRANG_HOP_THE` |
| **`602`** | `CAI_TRANG_HOP_THE_602` |
| **`603`** | `CAI_TRANG_HOP_THE_603` |
| **`604`** | `CAI_TRANG_VIP` |
| **`605`** | `CAI_TRANG_VIP_605` |
| **`606`** | `CAI_TRANG_VIP_606` |
| **`607`** | `CAI_TRANG_CHAN_XU` |
| **`608`** | `CAI_TRANG_LAO_CAN` |
| **`609`** | `CAI_TRANG_XAYDA` |
| **`610`** | `BONG_HOA_610` |
| **`611`** | `BAN_DO_KHO_BAU` |
| **`612`** | `CAI_TRANG_ARALE` |
| **`613`** | `CAI_TRANG_GATCHAN` |
| **`614`** | `CAI_TRANG_OBOTCHAMAN` |
| **`615`** | `CAI_TRANG_SO_1_615` |
| **`616`** | `CAI_TRANG_SO_4_616` |
| **`617`** | `CAI_TRANG_TIEU_DOI_TRUONG_GINYU_617` |
| **`618`** | `CAI_TRANG_LUFFY` |
| **`619`** | `CAI_TRANG_ZORO` |
| **`620`** | `CAI_TRANG_SANJI` |
| **`621`** | `CAI_TRANG_BROOK` |
| **`622`** | `CAI_TRANG_CHOPPER` |
| **`623`** | `CAI_TRANG_NAMI` |
| **`624`** | `CAI_TRANG_FRANKY` |
| **`625`** | `CAI_TRANG_USOPP` |
| **`626`** | `CAI_TRANG_ROBIN` |
| **`627`** | `CAPSULE_QUA_TANG` |
| **`628`** | `CAI_TRANG_HAI_TAC` |
| **`629`** | `CAI_TRANG_FIDE_VANG` |
| **`630`** | `CAI_TRANG_FROST_1` |
| **`631`** | `CAI_TRANG_FROST_2` |
| **`632`** | `CAI_TRANG_FROST_3` |
| **`633`** | `CAI_TRANG_PIC` |
| **`634`** | `CAI_TRANG_KING_KONG` |
| **`635`** | `CAI_TRANG_PI_LAP` |
| **`636`** | `CAI_TRANG_MAI` |
| **`637`** | `CAI_TRANG_SU` |
| **`638`** | `BINH_CHUA_COMMESON` |
| **`639`** | `CAI_TRANG_HOP_THE_639` |
| **`640`** | `CAI_TRANG_HOP_THE_640` |
| **`641`** | `CAI_TRANG_HOP_THE_641` |
| **`642`** | `CAI_TRANG_MA_TROI` |
| **`643`** | `CAI_TRANG_DOI_NHI` |
| **`644`** | `CAI_TRANG_BO_XUONG` |
| **`645`** | `CAI_TRANG_BO_XUONG_645` |
| **`646`** | `CAI_TRANG_BO_XUONG_646` |
| **`647`** | `CAI_TRANG_SAIBAMEN` |
| **`648`** | `HOP_QUA_GIANG_SINH` |
| **`649`** | `TAT_VO_GIANG_SINH` |
| **`650`** | `AO_HUY_DIET` |
| **`651`** | `QUAN_HUY_DIET` |
| **`652`** | `AO_HUY_DIET_652` |
| **`653`** | `QUAN_HUY_DIET_653` |
| **`654`** | `AO_HUY_DIET_654` |
| **`655`** | `QUAN_HUY_DIET_655` |
| **`656`** | `NHAN_HUY_DIET` |
| **`657`** | `GANG_HUY_DIET` |
| **`658`** | `GIAY_HUY_DIET` |
| **`659`** | `GANG_HUY_DIET_659` |
| **`660`** | `GIAY_HUY_DIET_660` |
| **`661`** | `GANG_HUY_DIET_661` |
| **`662`** | `GIAY_HUY_DIET_662` |
| **`663`** | `BANH_PUDDING` |
| **`664`** | `XUC_XICH` |
| **`665`** | `KEM_DAU` |
| **`666`** | `MI_LY` |
| **`667`** | `SUSHI` |
| **`668`** | `HOP_QUA_TET_2019` |
| **`669`** | `DUA_HAU_669` |
| **`670`** | `CA_ROT_670` |
| **`671`** | `BUA_TRI_TUE_X3` |
| **`672`** | `BUA_TRI_TUE_X4` |
| **`673`** | `ABCD` |
| **`674`** | `DA_NGU_SAC` |
| **`675`** | `CAI_TRANG_SARAHS_MOTHER` |
| **`676`** | `CAI_TRANG_SARAH` |
| **`677`** | `CAI_TRANG_PAN` |
| **`678`** | `CAI_TRANG_ANDROID_21` |
| **`679`** | `CAI_TRANG_MUSCULAR_LADY` |
| **`680`** | `CAI_TRANG_CAULIFLA` |
| **`681`** | `CAI_TRANG_NANG_TIEN_CA` |
| **`682`** | `AVATAR_682` |
| **`683`** | `AVATAR_683` |
| **`684`** | `AVATAR_684` |
| **`685`** | `AVATAR_685` |
| **`686`** | `AVATAR_686` |
| **`687`** | `AVATAR_687` |
| **`688`** | `AVATAR_688` |
| **`689`** | `AVATAR_689` |
| **`690`** | `AVATAR_690` |
| **`691`** | `QUAN_DI_BIEN` |
| **`692`** | `QUAN_DI_BIEN_692` |
| **`693`** | `QUAN_DI_BIEN_693` |
| **`694`** | `TRAI_DUA` |
| **`695`** | `VO_OC` |
| **`696`** | `VO_SO` |
| **`697`** | `CON_CUA` |
| **`698`** | `SAO_BIEN` |
| **`699`** | `HP_400K` |
| **`700`** | `SUC_DANH_20K` |
| **`701`** | `KI_400K` |
| **`702`** | `BI_NGO_1_SAO` |
| **`703`** | `BI_NGO_2_SAO` |
| **`704`** | `BI_NGO_3_SAO` |
| **`705`** | `BI_NGO_4_SAO` |
| **`706`** | `BI_NGO_5_SAO` |
| **`707`** | `BI_NGO_6_SAO` |
| **`708`** | `BI_NGO_7_SAO` |
| **`709`** | `BONG_HOA_709` |
| **`710`** | `CAI_TRANG_QUY_LAO_KAME` |
| **`711`** | `CAI_TRANG_JACKY_CHUN` |
| **`712`** | `TRUY` |
| **`713`** | `BAT` |
| **`714`** | `DAI` |
| **`715`** | `VUONG` |
| **`716`** | `URON` |
| **`717`** | `BAO_LI_XI` |
| **`718`** | `VE_TANG_NGOC` |
| **`719`** | `CAI_TRANG_URON` |
| **`720`** | `MANH_CAI_TRANG` |
| **`721`** | `PHIEU_GIAM_GIA_VIP` |
| **`722`** | `CAPSULE_HONG` |
| **`723`** | `BONG_HONG` |
| **`724`** | `CAI_TRANG_CHI_CHI_724` |
| **`725`** | `SIEU_THAN_THUY` |
| **`726`** | `MANH_GIAY_CO_CHU_MA` |
| **`727`** | `SIEU_THAN_THUY_727` |
| **`728`** | `SIEU_THAN_THUY_728` |
| **`729`** | `CAI_TRANG_HATCHIYACK` |
| **`730`** | `CAI_TRANG_SOI_BASIL` |
| **`731`** | `CAI_TRANG_SOI_LAVENDER` |
| **`732`** | `CAI_TRANG_SOI_BERGAMO` |
| **`733`** | `CAN_DAU_VAN_NGU_SAC` |
| **`734`** | `NGOC_THO` |
| **`735`** | `LONG_DEN_CA_CHEP_2` |
| **`736`** | `HOP_QUA_5_SAO` |
| **`737`** | `CAPSULE_TRUNG_THU` |
| **`738`** | `CAI_TRANG_DR_LYCHEE` |
| **`739`** | `CAI_TRANG_BILL_BI_NGO` |
| **`740`** | `LUOI_HAI_THAN_CHET` |
| **`741`** | `CANH_DOI_DRACULA` |
| **`742`** | `CAI_TRANG_CAUFILA_DOI` |
| **`743`** | `CHOI_BAY_PHU_THUY` |
| **`744`** | `COT_NHA` |
| **`745`** | `BONG_TUYET` |
| **`746`** | `XE_TUAN_LOC` |
| **`747`** | `CO_XANH_DUONG` |
| **`748`** | `THIT_HEO` |
| **`749`** | `THUNG_NEP` |
| **`750`** | `THUNG_DAU_XANH` |
| **`751`** | `LA_DONG` |
| **`752`** | `BANH_TET` |
| **`753`** | `BANH_CHUNG` |
| **`754`** | `NON_CHUOT_MAY_MAN` |
| **`755`** | `NON_CHUOT_MAY_MAN_755` |
| **`756`** | `NON_CHUOT_MAY_MAN_756` |
| **`757`** | `TUI_CANH_TY_2020` |
| **`758`** | `CAPSULE_TET_2022` |
| **`759`** | `NANG_CAP_KY_NANG_4_DE_TU` |
| **`760`** | `CAI_TRANG_VIP_760` |
| **`761`** | `AVATAR_DEO_KHAU_TRANG` |
| **`762`** | `AVATAR_DEO_KHAU_TRANG_762` |
| **`763`** | `AVATAR_DEO_KHAU_TRANG_763` |
| **`764`** | `KHAU_TRANG` |
| **`765`** | `CAI_TRANG_GOHAN_BU` |
| **`766`** | `CO_XAM_766` |
| **`767`** | `CO_DEN` |
| **`768`** | `CO_XANH_LA` |
| **`769`** | `CO_XANH_BIEN` |
| **`770`** | `CO_HONG_770` |
| **`771`** | `CO_CAM_771` |
| **`772`** | `CO_VANG_772` |
| **`773`** | `CO_TIM_773` |
| **`774`** | `CO_XANH_DA` |
| **`775`** | `CO_DO_775` |
| **`776`** | `KHAN_XANH_LA` |
| **`777`** | `KHAN_XANH_DUONG` |
| **`778`** | `KHAN_VANG` |
| **`779`** | `KHAN_TIM` |
| **`780`** | `KHAN_NAU` |
| **`781`** | `KHAN_XAM` |
| **`782`** | `KHAN_DO` |
| **`783`** | `KHAN_HONG` |
| **`784`** | `KHAN_XANH_DA` |
| **`785`** | `BA_LO` |
| **`786`** | `DAO` |
| **`787`** | `GAY` |
| **`788`** | `MAI_RUA` |
| **`789`** | `GIO_BO` |
| **`790`** | `GIO_DUA_HAU` |
| **`791`** | `GIO_CU_CAI_TRANG` |
| **`792`** | `GIO_CA_ROT` |
| **`793`** | `GIO_CHUOI` |
| **`794`** | `GAY_PHEP` |
| **`795`** | `GHE_BAY` |
| **`796`** | `HOP_CAPSULE` |
| **`797`** | `BUA_TRI_TUE_797` |
| **`798`** | `BUA_MANH_ME_798` |
| **`799`** | `BUA_DA_TRAU_799` |
| **`800`** | `LONG_DEN_CO_VY` |
| **`801`** | `LONG_DEN_CON_TAU` |
| **`802`** | `LONG_DEN_CON_GA` |
| **`803`** | `LONG_DEN_CON_BUOM` |
| **`804`** | `LONG_DEN_DOREMON` |
| **`805`** | `NON_THIEN_THAN` |
| **`806`** | `CAI_TRANG_DOREMON` |
| **`807`** | `NGOC_DEN_1_SAO` |
| **`808`** | `NGOC_DEN_2_SAO` |
| **`809`** | `NGOC_DEN_3_SAO` |
| **`810`** | `NGOC_DEN_4_SAO` |
| **`811`** | `NGOC_DEN_5_SAO` |
| **`812`** | `NGOC_DEN_6_SAO` |
| **`813`** | `NGOC_DEN_7_SAO` |
| **`814`** | `MA_TROI` |
| **`815`** | `HON_MA_GOKU` |
| **`816`** | `HON_MA_CA_DIC` |
| **`817`** | `HON_MA_POCOLO` |
| **`818`** | `CAPSULE_HALLOWEEN` |
| **`819`** | `CAI_TRANG_XUKA` |
| **`820`** | `VE_QUAY_NGOC_DEN` |
| **`821`** | `VE_QUAY_NGOC_VANG` |
| **`822`** | `CAY_THONG` |
| **`823`** | `TUI_QUA` |
| **`824`** | `CAI_TRANG_NOEL` |
| **`825`** | `CAI_TRANG_NOEL_825` |
| **`826`** | `CAI_TRANG_NOEL_826` |
| **`827`** | `CAI_TRANG_NOEL_827` |
| **`828`** | `MANH_KHUNG_LONG` |
| **`829`** | `MANH_LON_LOI` |
| **`830`** | `MANH_QUY_DAT` |
| **`831`** | `MANH_KHUNG_LONG_ME` |
| **`832`** | `MANH_LON_LOI_ME` |
| **`833`** | `MANH_QUY_DAT_ME` |
| **`834`** | `MANH_THAN_LAN_BAY` |
| **`835`** | `MANH_PHI_LONG` |
| **`836`** | `MANH_QUY_BAY` |
| **`837`** | `MANH_LINH_DOC_NHAN` |
| **`838`** | `MANH_LINH_DOC_NHAN_838` |
| **`839`** | `MANH_SOI_XAM` |
| **`840`** | `MANH_TRUNG_UY_TRANG` |
| **`841`** | `MANH_NINJA_AO_TIM` |
| **`842`** | `MANH_TRUNG_UY_XANH_LO` |
| **`843`** | `CAI_TRANG_TRAU_VANG` |
| **`844`** | `CAI_TRANG_TRAU_DEN` |
| **`845`** | `CAI_TRANG_TRAU_TRANG` |
| **`846`** | `NON_TRAU_MAY_MAN` |
| **`847`** | `NON_TRAU_MAY_MAN_847` |
| **`848`** | `NON_TRAU_MAY_MAN_848` |
| **`849`** | `PHAO_THANG_THIEN` |
| **`850`** | `CO_TUOI` |
| **`851`** | `CO_TUOI_851` |
| **`852`** | `CAY_TRUC` |
| **`853`** | `CAI_TRANG_TRAU_NAU` |
| **`854`** | `CAI_TRANG_TRAU_NAU_854` |
| **`855`** | `CAI_TRANG_TRAU_NAU_855` |
| **`856`** | `CAI_TRANG_TRAU_DOM` |
| **`857`** | `CAI_TRANG_TRAU_DOM_857` |
| **`858`** | `CAI_TRANG_TRAU_DOM_858` |
| **`859`** | `MANH_DOC_NHAN` |
| **`860`** | `CAI_TRANG_MI_NUONG` |
| **`861`** | `HONG_NGOC` |
| **`862`** | `CAI_TRANG_NOBITA` |
| **`863`** | `CAI_TRANG_XEKO` |
| **`864`** | `CAI_TRANG_CHAIEN` |
| **`865`** | `KIEM_Z` |
| **`866`** | `AVATAR_866` |
| **`867`** | `AVATAR_867` |
| **`868`** | `AVATAR_868` |
| **`869`** | `CAPSULE_1_SAO` |
| **`870`** | `CAPSULE_2_SAO` |
| **`871`** | `CAPSULE_3_SAO` |
| **`872`** | `AVATAR_GOHAN_1` |
| **`873`** | `AVATAR_GOHAN_2` |
| **`874`** | `RUA_CON` |
| **`875`** | `CAI_TRANG_CADIC` |
| **`876`** | `CAI_TRANG_GOHAN` |
| **`877`** | `CAI_TRANG_POCOLO` |
| **`878`** | `CAI_TRANG_COOLER_VANG` |
| **`879`** | `CAI_TRANG_THONG_CHE_KILO` |
| **`880`** | `CUA_RANG_ME` |
| **`881`** | `BACH_TUOC_NUONG` |
| **`882`** | `TOM_TAM_BOT_CHIEN_XU` |
| **`883`** | `CAI_TRANG_BLACK_GOHAN_ROSE` |
| **`884`** | `CAI_TRANG_HIT` |
| **`885`** | `CAI_TRANG_LICH_TEN` |
| **`886`** | `TRUNG_VIT_MUOI` |
| **`887`** | `GA_QUAY_NGUYEN_CON` |
| **`888`** | `BOT_MI` |
| **`889`** | `DAU_XANH` |
| **`890`** | `BANH_TRUNG_THU_GA_QUAY` |
| **`891`** | `BANH_TRUNG_THU_THAP_CAM` |
| **`892`** | `THO_XAM` |
| **`893`** | `THO_TRANG` |
| **`894`** | `AVATAR_894` |
| **`895`** | `AVATAR_895` |
| **`896`** | `AVATAR_896` |
| **`897`** | `RUA_BAY` |
| **`898`** | `CAI_TRANG_ZAMASU` |
| **`899`** | `KEO_MOT_MAT` |
| **`900`** | `SUP_BI_HAC_AM` |
| **`901`** | `KEO_BAN_TAY` |
| **`902`** | `BANH_GATO_NHEN` |
| **`903`** | `HAMBURGER_SAU` |
| **`904`** | `CAI_TRANG_BLACK_GOKU_SSJ_WHITE` |
| **`905`** | `CAI_TRANG_SIEU_THAN_TRAI_DAT` |
| **`906`** | `CAI_TRANG_MIGHTY_MASK` |
| **`907`** | `CAI_TRANG_SIEU_THAN_NAMEC` |
| **`908`** | `MA_PHONG_BA` |
| **`909`** | `THAN_CHET_CUTE` |
| **`910`** | `BI_NGO_NHI_NHANH` |
| **`911`** | `CAI_TRANG_SIEU_THAN_XAYDA` |
| **`912`** | `CAI_TRANG_GOHAN_AO_XANH` |
| **`913`** | `CAI_TRANG_POC_AO_DO` |
| **`914`** | `CAI_TRANG_BUP_BE_ARALE` |
| **`915`** | `CAPSULE_SQUID_GAME` |
| **`916`** | `LINH_BAO_VE_TAM_GIAC` |
| **`917`** | `LINH_BAO_VE_VUONG` |
| **`918`** | `LINH_BAO_VE_TRON` |
| **`919`** | `BUP_BE` |
| **`920`** | `GAY_NHU_Y` |
| **`921`** | `BONG_TAI_PORATA_CAP_2` |
| **`922`** | `CAI_TRANG_GOKU_NOEL` |
| **`923`** | `CAI_TRANG_PICO_NOEL` |
| **`924`** | `CAI_TRANG_RADIC_NOEL` |
| **`925`** | `NGOC_RONG_BANG_1_SAO` |
| **`926`** | `NGOC_RONG_BANG_2_SAO` |
| **`927`** | `NGOC_RONG_BANG_3_SAO` |
| **`928`** | `NGOC_RONG_BANG_4_SAO` |
| **`929`** | `NGOC_RONG_BANG_5_SAO` |
| **`930`** | `NGOC_RONG_BANG_6_SAO` |
| **`931`** | `NGOC_RONG_BANG_7_SAO` |
| **`932`** | `CAI_TRANG_UUB` |
| **`933`** | `MANH_VO_BONG_TAI` |
| **`934`** | `MANH_HON_BONG_TAI` |
| **`935`** | `DA_XANH_LAM` |
| **`936`** | `TUAN_LOC_NHI` |
| **`937`** | `CAI_TRANG_MABU_NOEL` |
| **`938`** | `CAI_TRANG_GOKU_THOI_TRANG` |
| **`939`** | `CAI_TRANG_POCOLO_THOI_TRANG` |
| **`940`** | `CAI_TRANG_CA_DIC_THOI_TRANG` |
| **`941`** | `NON_HO_VANG` |
| **`942`** | `HO_MAP_VANG` |
| **`943`** | `HO_MAP_TRANG` |
| **`944`** | `HO_MAP_XANH` |
| **`945`** | `CAI_TRANG_GOKU_TARZAN` |
| **`946`** | `NON_HO_TRANG` |
| **`947`** | `NON_HO_XANH` |
| **`948`** | `CAI_TRANG_HO_VANG` |
| **`949`** | `THIT_TUOI` |
| **`950`** | `THIT_TUOI_950` |
| **`951`** | `CAI_TRANG_SIEU_GOKU_TARZAN` |
| **`952`** | `CAI_TRANG_HO_TRANG` |
| **`953`** | `CAI_TRANG_HO_XANH` |
| **`954`** | `BO_HOA_HONG` |
| **`955`** | `BO_HOA_VANG` |
| **`956`** | `MANH_DOI_TRUONG_VANG` |
| **`957`** | `CAI_TRANG_GOKU_VO_SI` |
| **`958`** | `CAI_TRANG_POCOLO_VO_SI` |
| **`959`** | `CAI_TRANG_CADIC_VO_SI` |
| **`960`** | `CO_TRANG` |
| **`961`** | `CO_DEN_961` |
| **`962`** | `CAPSULE_THOI_TRANG_30_NGAY` |
| **`963`** | `CAPSULE_THOI_TRANG_7_NGAY` |
| **`964`** | `SAO_PHA_LE_964` |
| **`965`** | `SAO_PHA_LE_965` |
| **`966`** | `TRAI_BONG` |
| **`967`** | `SAO_LA` |
| **`968`** | `SO_7_GOKU` |
| **`969`** | `SO_11_GOHAN` |
| **`970`** | `SO_6_CADIC` |
| **`971`** | `SO_2_KRILIN` |
| **`972`** | `SO_4_THEN_XIN_HANG` |
| **`973`** | `SO_10_POCOLO` |
| **`974`** | `SO_1_MABU` |
| **`975`** | `SO_8_BIDEN` |
| **`976`** | `SO_9_QUY_LAO_KAME` |
| **`977`** | `SO_3_POC` |
| **`978`** | `SO_5_PIC` |
| **`979`** | `HUY_CHUONG_DONG` |
| **`980`** | `HUY_CHUONG_BAC` |
| **`981`** | `HUY_CHUONG_VANG` |
| **`982`** | `CUP_VANG` |
| **`983`** | `CO_CO_DONG` |
| **`984`** | `CAPSULE_SEA_GAMES` |
| **`985`** | `CAI_TRANG_CHILL` |
| **`986`** | `CAI_TRANG_CHILL_CAP_2` |
| **`987`** | `DA_BAO_VE` |
| **`988`** | `MO_RONG_TUI_VANG` |
| **`989`** | `CAI_TRANG_GOHAN_SIEU_NHAN` |
| **`990`** | `CAI_TRANG_BIDEN_SIEU_NHAN` |
| **`991`** | `CAI_TRANG_BAN_CO_NUONG_SIEU_NHAN` |
| **`992`** | `NHAN_THOI_KHONG_SAI_LECH` |
| **`993`** | `GIO_THUC_AN` |
| **`994`** | `VO_OC_994` |
| **`995`** | `CAY_KEM` |
| **`996`** | `CA_HEO` |
| **`997`** | `CON_DIEU` |
| **`998`** | `DIEU_RONG` |
| **`999`** | `MEO_MUN` |
| **`1000`** | `XIEN_CA` |
| **`1001`** | `PHONG_LON` |
| **`1002`** | `CA_NOC` |
| **`1003`** | `CA_BAY_MAU` |
| **`1004`** | `CA_DIEU_HONG` |
| **`1005`** | `XO_CA_XANH` |
| **`1006`** | `XO_CA_VANG` |
| **`1007`** | `VAN_LUOT_SONG` |
| **`1008`** | `CUA_DO` |
| **`1009`** | `RUONG_TRANH_NGOC_NAMEC` |
| **`1010`** | `CAI_TRANG_AO_VIT_CAM` |
| **`1011`** | `CAI_TRANG_AO_TRANG_HOA` |
| **`1012`** | `CAI_TRANG_NON_ROM_MUA_HE` |
| **`1013`** | `KIEM_ANH_SANG` |
| **`1014`** | `RADA_DO_NGOC_NAMEC` |
| **`1015`** | `NGOC_RONG_SIEU_CAP` |
| **`1016`** | `THUOC_MO_IPANA` |
| **`1017`** | `THUOC_MO_IPANA_DAC_BIET` |
| **`1018`** | `CAI_TRANG_BROLY` |
| **`1019`** | `CAI_TRANG_BROLY_1019` |
| **`1020`** | `CAI_TRANG_BROLY_1020` |
| **`1021`** | `BUA_MJOLNIR` |
| **`1022`** | `BUA_STORMBREAKER` |
| **`1023`** | `QUAT_BA_TIEU` |
| **`1024`** | `GAY_NHU_Y_1024` |
| **`1025`** | `BO_CAO` |
| **`1026`** | `NGUYET_NHA_SAN` |
| **`1027`** | `QUYEN_TRUONG` |
| **`1028`** | `DAO_RANG_CUA` |
| **`1029`** | `LO_NUOC_PHEP` |
| **`1030`** | `CO_HOA_DANG` |
| **`1031`** | `CO_HOA_SEN` |
| **`1032`** | `GIAY_MAU` |
| **`1033`** | `LOI_CHUC` |
| **`1034`** | `NEN` |
| **`1035`** | `KHUNG_TRE` |
| **`1036`** | `PHAO_HOA` |
| **`1037`** | `HOA_DANG` |
| **`1038`** | `HOA_DANG_CO_LOI_CHUC` |
| **`1039`** | `PET_THO_OM` |
| **`1040`** | `PET_THO_MAP` |
| **`1041`** | `THO_HONG_BUN_MA` |
| **`1042`** | `THO_DO_CHI_CHI` |
| **`1043`** | `THO_DEN_ANDROID_18` |
| **`1044`** | `EMPTY` |
| **`1045`** | `DUOI_KHI_1045` |
| **`1046`** | `PET_KHI_BONG_BONG` |
| **`1047`** | `LONG_DEN_LON` |
| **`1048`** | `AO_THIEN_SU` |
| **`1049`** | `AO_THIEN_SU_1049` |
| **`1050`** | `AO_THIEN_SU_1050` |
| **`1051`** | `QUAN_THIEN_SU` |
| **`1052`** | `QUAN_THIEN_SU_1052` |
| **`1053`** | `QUAN_THIEN_SU_1053` |
| **`1054`** | `GANG_THIEN_SU` |
| **`1055`** | `GANG_THIEN_SU_1055` |
| **`1056`** | `GANG_THIEN_SU_1056` |
| **`1057`** | `GIAY_THIEN_SU` |
| **`1058`** | `GIAY_THIEN_SU_1058` |
| **`1059`** | `GIAY_THIEN_SU_1059` |
| **`1060`** | `NHAN_THIEN_SU` |
| **`1061`** | `NHAN_THIEN_SU_1061` |
| **`1062`** | `NHAN_THIEN_SU_1062` |
| **`1063`** | `AVATAR_THIEN_SU` |
| **`1064`** | `AVATAR_THIEN_SU_1064` |
| **`1065`** | `AVATAR_THIEN_SU_1065` |
| **`1066`** | `MANH_AO` |
| **`1067`** | `MANH_QUAN` |
| **`1068`** | `MANH_GIAY` |
| **`1069`** | `MANH_NHAN` |
| **`1070`** | `MANH_GANG_TAY` |
| **`1071`** | `CONG_THUC` |
| **`1072`** | `CONG_THUC_1072` |
| **`1073`** | `CONG_THUC_1073` |
| **`1074`** | `DA_NANG_CAP_CAP_1` |
| **`1075`** | `DA_NANG_CAP_CAP_2` |
| **`1076`** | `DA_NANG_CAP_CAP_3` |
| **`1077`** | `DA_NANG_CAP_CAP_4` |
| **`1078`** | `DA_NANG_CAP_CAP_5` |
| **`1079`** | `DA_MAY_MAN_CAP_1` |
| **`1080`** | `DA_MAY_MAN_CAP_2` |
| **`1081`** | `DA_MAY_MAN_CAP_3` |
| **`1082`** | `DA_MAY_MAN_CAP_4` |
| **`1083`** | `DA_MAY_MAN_CAP_5` |
| **`1084`** | `CONG_THUC_VIP` |
| **`1085`** | `CONG_THUC_VIP_1085` |
| **`1086`** | `CONG_THUC_VIP_1086` |
| **`1087`** | `TANJIRO` |
| **`1088`** | `INOSUKE_HASHIBIRA` |
| **`1089`** | `INOSUKE` |
| **`1090`** | `ZENITSU` |
| **`1091`** | `NEZUKO` |
| **`1092`** | `GAY_QUY_LAO` |
| **`1093`** | `DAT_TRONG_CAY` |
| **`1094`** | `PHAN_BON` |
| **`1095`** | `HAT_MAM` |
| **`1096`** | `THUOC_TANG_TRUONG` |
| **`1097`** | `CHAU_SU` |
| **`1098`** | `HOA_HONG_XANH` |
| **`1099`** | `CHAU_HOA_HONG_XANH` |
| **`1100`** | `CHAU_HOA_AN_THIT` |
| **`1101`** | `LOI_CHUC_1101` |
| **`1102`** | `LOI_CHUC_VIP` |
| **`1103`** | `MARRON` |
| **`1104`** | `POC_SATAN` |
| **`1105`** | `BUN_MA_PHU_THUY` |
| **`1106`** | `BIDEN_DRACULA` |
| **`1107`** | `PET_BI_MA_VUONG` |
| **`1108`** | `DINH_BA_SATAN` |
| **`1109`** | `CHOI_PHU_THUY` |
| **`1110`** | `CANH_THIEN_THAN` |
| **`1111`** | `CANH_THIEN_THAN_2` |
| **`1112`** | `CAY_NAP_AM` |
| **`1113`** | `CON_CA` |
| **`1114`** | `PET_MA_VANG_PHU_THUY` |
| **`1115`** | `MAY_DO_NGOC_RONG_SU_KIEN` |
| **`1116`** | `HOM_HALLOWEEN` |
| **`1117`** | `THIEP_HALLOWEEN` |
| **`1148`** | `PHUONG_HOANG_LUA` |
| **`1149`** | `CAI_TRANG_SSJ_3_WHITE` |
| **`1159`** | `NOEL_2022_GOKU` |
| **`1160`** | `NOEL_2022_CADIC` |
| **`1161`** | `NOEL_2022_POCOLO` |
| **`1154`** | `CUONG_NO_2` |
| **`1155`** | `BO_KHI_2` |
| **`1156`** | `BO_HUYET_2` |
| **`1157`** | `GIAP_XEN_BO_HUNG_2` |
| **`1158`** | `AN_DANH_2` |
| **`1162`** | `CHU_LUN_1` |
| **`1163`** | `CHU_LUN_2` |
| **`1164`** | `CHU_LUN_3` |
| **`1165`** | `CHU_LUN_4` |
| **`1166`** | `CHU_LUN_5` |
| **`1167`** | `CHU_LUN_6` |
| **`1168`** | `CHU_LUN_7` |
| **`1169`** | `CHUONG_DONG` |
| **`1170`** | `CA_TUYET` |
| **`1171`** | `BANH_QUY` |
| **`1172`** | `KEO_DUONG` |
| **`1173`** | `KEO_NGUOI_TUYET` |
| **`1174`** | `GOI_QUA` |
| **`1175`** | `TUI_7_CHU_LUN` |
| **`1176`** | `XE_HEO_TUAN_LOC` |
| **`1177`** | `RUONG_MANH_THIEN_SU` |
| **`1178`** | `KIMONO` |
| **`1179`** | `KIMONO_1179` |
| **`1180`** | `KIMONO_1180` |
| **`1181`** | `MANG_CAU` |
| **`1182`** | `QUA_DUA` |
| **`1183`** | `DU_DU` |
| **`1184`** | `QUA_XOAI` |
| **`1185`** | `TRAI_SUNG` |
| **`1186`** | `MAM_NGU_QUA` |
| **`1187`** | `BAO_LI_XI_MEO` |
| **`1188`** | `GOI_QUA_DAC_BIET` |
| **`1189`** | `CANH_MAI` |
| **`1190`** | `CANH_DAO` |
| **`1191`** | `HOP_QUA_TET_20231` |
| **`1192`** | `PET_MEO_DEN_DUOI_VANG` |
| **`1193`** | `XUC_XICH_XONG_KHOI` |
| **`1194`** | `DIA_THUC_AN_CHO_MEO` |
| **`1195`** | `THIEP_CHUC_TET_1195` |
| **`1196`** | `THIEP_CHUC_TET_1196` |
| **`1197`** | `THIEP_CHUC_TET_1197` |
| **`1198`** | `PHIEU_BE_NGOAN` |
| **`1199`** | `XI_MUOI_HOA_DAO` |
| **`1200`** | `XI_MUOI_HOA_MAI` |
| **`1201`** | `BONG_VIT_VANG` |
| **`1202`** | `NON_MEO` |
| **`1203`** | `NON_MEO_1203` |
| **`1204`** | `NON_MEO_1204` |
| **`1205`** | `KARIN_KID_LAN` |
| **`1206`** | `PET_MEO_TRANG_DUOI_VANG` |
| **`1207`** | `PET_MEO_TRANG_DUOI_VANG_1207` |
| **`1208`** | `MANH_RONG_THAN_NAMEC` |
| **`1209`** | `DRABURA_FROST` |
| **`1210`** | `TRAI_TIM_VALENTINE` |
| **`1210`** | `BUNMA_TOC_TIM_THAT_BIM` |
| **`1211`** | `QUY_HOA_BAO_DIEN` |
| **`1212`** | `TICH_TA_KIEM_PHO` |
| **`1213`** | `PET_MEO_HOANG_THUONG` |
| **`1214`** | `COM_NEP` |
| **`1215`** | `VAN_COM_NEP` |
| **`1216`** | `TEP_COM_NEP` |
| **`1217`** | `LA_DONG_1217` |
| **`1218`** | `SOI_COI` |
| **`1219`** | `TEP_BANH_CHUNG` |
| **`1220`** | `NGA_VOI` |
| **`1221`** | `CUA_GA` |
| **`1222`** | `HONG_MAO` |
| **`1223`** | `PET_KHI_GAS` |
| **`1224`** | `PET_VOI_CHIN_NGA` |
| **`1225`** | `PET_GA_CHIN_CUA` |
| **`1226`** | `PET_NGUA_CHIN_HONG_MAO` |
| **`1227`** | `HOP_QUA_THUONG` |
| **`1228`** | `HOP_QUA_CAO_CAP` |
| **`1229`** | `BI_KIP_TUYET_KY` |
| **`1230`** | `HAO_QUANG_RUC_RO` |
| **`1231`** | `GAY_THUONG_DE` |
| **`1232`** | `BINH_CHUA_NHO` |
| **`1233`** | `NOI_COM_DIEN` |
| **`1234`** | `CAI_TRANG_PIC_THO_LAN_2023` |
| **`1235`** | `CAI_TRANG_POC_BIKINI_2023` |
| **`1236`** | `CAI_TRANG_KING_KONG_SANH_DIEU_2023` |
| **`1237`** | `CANH_KHO` |
| **`1238`** | `NUOC_SUOI_TINH_KHIET` |
| **`1239`** | `GO_LON` |
| **`1240`** | `QUE_DOT` |
| **`1241`** | `BON_TAM_GO` |
| **`1242`** | `BON_TAM_VANG` |
| **`1243`** | `PET_BO_CANH_CUNG` |
| **`1244`** | `PET_NGAI_DEM` |
| **`1245`** | `BO_KIEN_VUONG_HAI_SUNG` |
| **`1246`** | `BO_HUNG_TE_GIAC` |
| **`1247`** | `BO_KEP_KIM` |
| **`1248`** | `BO_CANH_CUNG` |
| **`1249`** | `NGAI_DEM` |
| **`1250`** | `HU_MAT_ONG` |
| **`1251`** | `VOT_BAT_BO` |
| **`1252`** | `VE_SAU_XEN` |
| **`1253`** | `VE_SAU_XEN_TIEN_HOA` |
| **`1352`** | `BO_KEO_KINH_DI` |
| **`1401`** | `HAC_VO_THUONG` |
| **`1402`** | `BACH_VO_THUONG` |
| **`1716`** | `GIAP_TAP_LUYEN_CAP_4` |