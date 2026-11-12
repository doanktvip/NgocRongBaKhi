package nro.models.boss;

import nro.models.consts.AppearType;
import nro.models.consts.ConstPlayer;
import nro.models.skill.Skill;
import nro.models.utils.Util;

public class BossesData {

    private static final int REST_1_S = 1;
    private static final int REST_2_S = 2;
    private static final int REST_5_S = 5;
    private static final int REST_10_S = 10;
    private static final int REST_20_S = 20;
    private static final int REST_30_S = 30;
    private static final int REST_1_M = 60;
    private static final int REST_2_M = 120;
    private static final int REST_5_M = 300;
    private static final int REST_10_M = 600;
    private static final int REST_15_M = 900;
    private static final int REST_30_M = 1800;
    private static final int REST_24_H = 86400000;

    //========================Boss nappa========================
    public static final BossData KUKU = BossData.builder()
            .name("Kuku")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{159, 160, 161, -1, -1, -1})
            .dame(9000)
            .hp(new int[]{500000})
            .mapJoin(new int[]{68, 69, 70, 71, 72})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7}
            })
            .textS(new String[]{
                "|-1|Kuku ta đã xuất hiện, đứa nào ngon bước ra đây!",
                "|-1|Hôm nay ta sẽ cho cả khu này biết thế nào là lễ độ!"
            })
            .textM(new String[]{
                "|-1|Sức chiến đấu của bọn mày chỉ có thế thôi à?",
                "|-1|Haha, đứng lại, né đòn giỏi đấy nhưng vô ích thôi!",
                "|-1|Mày yếu quá, về tập luyện thêm đi con trai!",
                "|-1|Chạy đi đâu, hôm nay không có đường lui đâu!",
                "|-1|Một đòn là nằm, đỡ thử chiêu này xem nào!"
            })
            .textE(new String[]{
                "|-1|Khá lắm... đợi đấy, ta sẽ trở lại!",
                "|-1|Hôm nay coi như bọn mày may mắn...!"
            })
            .secondsRest(REST_10_M)
            .build();

    public static final BossData MAP_DAU_DINH = BossData.builder()
            .name("Mập Đầu Đinh")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{165, 166, 167, -1, -1, -1})
            .dame(10000)
            .hp(new int[]{1000000})
            .mapJoin(new int[]{63, 64, 65, 66, 67})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7}
            })
            .textS(new String[]{
                "|-1|HAHAHA! Mập Đầu Đinh ta đến rồi đây!",
                "|-1|Đứa nào dám cản đường tao đập bẹp dí!"
            })
            .textM(new String[]{
                "|-1|HAHAHA, tao chỉ cần 10 giây để dọn sạch bọn mày!",
                "|-1|Đỡ lấy quả đấm ngàn cân của tao đây!",
                "|-1|Đứng lại! Đừng có chạy lung tung!",
                "|-1|Gãi ngứa cho tao à, đánh mạnh lên xem nào!",
                "|-1|Hôm nay không đứa nào thoát được đâu!"
            })
            .textE(new String[]{
                "|-1|Đau quá... đông đánh một không chột cũng què...",
                "|-1|Nhớ mặt tao đấy, lần sau tao đập nát!"
            })
            .secondsRest(REST_10_M)
            .build();

    public static final BossData RAMBO = BossData.builder()
            .name("Rambo")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{162, 163, 164, -1, -1, -1})
            .dame(12400)
            .hp(new int[]{1500000})
            .mapJoin(new int[]{73, 74, 75, 76, 77})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7}
            })
            .textS(new String[]{
                "|-1|Rambo vĩ đại đã xuất hiện!",
                "|-1|Thấy thân hình vạm vỡ và cơ bắp cuồn cuộn của ta chưa?"
            })
            .textM(new String[]{
                "|-1|HAHAHA, thấy ta đẹp trai và phong độ không?",
                "|-1|Mày sợ cơ bắp của tao chưa hả nhóc?",
                "|-1|Đỡ một chiêu sấm sét của ta đây!",
                "|-1|Cơ bắp của ta là mình đồng da sắt đấy!",
                "|-1|Đánh chả xi-nhê gì cả, hết sức rồi à?"
            })
            .textE(new String[]{
                "|-1|Ôi bạn ơi... đấm gì mà thốn thế...",
                "|-1|Gãy hết cơ bắp rồi... ta rút lui trước!"
            })
            .secondsRest(REST_10_M)
            .build();

    //========================TDST========================
    public static final BossData SO_4 = BossData.builder()
            .name("Số 4")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{168, 169, 170, -1, -1, -1})
            .dame(10000)
            .hp(new int[]{25_000_000})
            .mapJoin(new int[]{79, 81, 82, 83})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 4 ta đây! Kẻ nào dám bước vào lãnh địa này?"
            })
            .textM(new String[]{
                "|-1|Thời gian... hãy ngưng đọng lại cho ta!",
                "|-1|Đứng im như tượng gỗ đi nào lũ nhóc!",
                "|-1|Chiêu thức của các ngươi quá chậm chạp!"
            })
            .textE(new String[]{
                "|-1|Không thể nào... ma thuật ngưng đọng thời gian của ta..."
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData SO_3 = BossData.builder()
            .name("Số 3")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{174, 175, 176, -1, -1, -1})
            .dame(11000)
            .hp(new int[]{30_000_000})
            .mapJoin(new int[]{79, 81, 82, 83})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 3 đã xuất trận! Chuẩn bị nếm mùi cơ bắp thép của ta đi!"
            })
            .textM(new String[]{
                "|-1|Một đấm ngàn cân! Đỡ nổi không nhóc con?",
                "|-1|Cơ bắp của ta là mình đồng da sắt, đấm nữa đi!",
                "|-1|Chỉ như gãi ngứa thôi, hết sức rồi à?"
            })
            .textE(new String[]{
                "|-1|Hự... đòn nặng đô đấy... ta rút lui trước..."
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData SO_2 = BossData.builder()
            .name("Số 2")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{171, 172, 173, -1, -1, -1})
            .dame(12000)
            .hp(new int[]{35_000_000})
            .mapJoin(new int[]{79, 81, 82, 83})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 2 đã tới! Ngọn lửa hủy diệt của ta sẽ thiêu rụi tất cả!"
            })
            .textM(new String[]{
                "|-1|Cầu lửa hủy diệt nung nấu vạn vật đây!",
                "|-1|Phối hợp tác chiến cùng Số 1 là bất khả chiến bại!",
                "|-1|Chạy đằng trời nhé lũ chuột nhắt!"
            })
            .textE(new String[]{
                "|-1|Chết tiệt... ngọn lửa của ta bị dập tắt rồi..."
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData SO_1 = BossData.builder()
            .name("Số 1")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{177, 178, 179, -1, -1, -1})
            .dame(13000)
            .hp(new int[]{40_000_000})
            .mapJoin(new int[]{79, 81, 82, 83})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 1 xuất trận! Tốc độ tối thượng sẽ kết liễu các ngươi!"
            })
            .textM(new String[]{
                "|-1|Quá chậm! Ngươi chỉ đang đánh vào tàn ảnh của ta thôi!",
                "|-1|Tốc độ ánh sáng! Ngươi còn không nhìn thấy đòn đánh của ta!",
                "|-1|Đuổi kịp ta đi nếu có bản lĩnh!"
            })
            .textE(new String[]{
                "|-1|Không thể tin được... tốc độ của ta lại bị vượt qua..."
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData TIEU_DOI_TRUONG = BossData.builder()
            .name("Tiểu đội trưởng")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{180, 181, 182, -1, -1, -1})
            .dame(15000)
            .hp(new int[]{50_000_000})
            .mapJoin(new int[]{79, 81, 82, 83})
            .skillTemp(new int[][]{
                {Skill.GALICK, 7},
                {Skill.ANTOMIC, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Đích thân Tiểu Đội Trưởng ta sẽ cho các ngươi biết thế nào là sức mạnh tối thượng!"
            })
            .textM(new String[]{
                "|-1|Chiêm ngưỡng đẳng cấp của người dẫn đầu Tiểu Đội Sát Thủ!",
                "|-1|Úm ba la xì bùa! Xem biến hóa đây!",
                "|-1|Bản lĩnh của các ngươi chỉ đến đây thôi sao?"
            })
            .textE(new String[]{
                "|-1|Không thể tin được... ta là Tiểu Đội Trưởng mà lại bại trận..."
            })
            .secondsRest(REST_10_M)
            .bossesAppearTogether(new int[]{BossID.SO_4, BossID.SO_3, BossID.SO_2, BossID.SO_1})
            .build();

    //========================TDST NAMEK========================
    public static final BossData SO_4_NM = BossData.builder()
            .name("Số 4 Namek")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{168, 169, 170, -1, -1, -1})
            .dame(10000)
            .hp(new int[]{2_500_000})
            .mapJoin(new int[]{7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43})
            .skillTemp(new int[][]{
                {Skill.DEMON, 7},
                {Skill.ANTOMIC, 7},
                {Skill.LIEN_HOAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 4 Namek đã đến! Đừng hòng sống sót rời khỏi hành tinh Namek!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Các ngươi không nhúc nhích được sao?",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            })
            .textE(new String[]{
                "|-1|Cay quá! Các ngươi hãy đợi đấy, anh em ta sẽ trả thù!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData SO_3_NM = BossData.builder()
            .name("Số 3 Namek")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{174, 175, 176, -1, -1, -1})
            .dame(10000)
            .hp(new int[]{3_000_000})
            .mapJoin(new int[]{7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43})
            .skillTemp(new int[][]{
                {Skill.DEMON, 7},
                {Skill.ANTOMIC, 7},
                {Skill.LIEN_HOAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 3 Namek xuất trận! Chuẩn bị nếm mùi cơ bắp bọc thép của ta!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            })
            .textE(new String[]{
                "|-1|Đau quá... Ta mà lại thua lũ sâu bọ này sao?!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData SO_2_NM = BossData.builder()
            .name("Số 2 Namek")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{171, 172, 173, -1, -1, -1})
            .dame(12200)
            .hp(new int[]{3_500_000})
            .mapJoin(new int[]{7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43})
            .skillTemp(new int[][]{
                {Skill.DEMON, 7},
                {Skill.ANTOMIC, 7},
                {Skill.LIEN_HOAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 2 Namek tới đây! Ngọn lửa hủy diệt sẽ thiêu rụi bọn mày!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            })
            .textE(new String[]{
                "|-1|Khốn kiếp... Đại ca hãy trả thù cho em!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData SO_1_NM = BossData.builder()
            .name("Số 1 Namek")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{177, 178, 179, -1, -1, -1})
            .dame(13200)
            .hp(new int[]{4_000_000})
            .mapJoin(new int[]{7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43})
            .skillTemp(new int[][]{
                {Skill.DEMON, 7},
                {Skill.ANTOMIC, 7},
                {Skill.LIEN_HOAN, 7}
            })
            .textS(new String[]{
                "|-1|Số 1 Namek xuất trận! Tốc độ tối thượng sẽ kết liễu các ngươi!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            })
            .textE(new String[]{
                "|-1|Không thể nào... Tốc độ của ta sao lại bị đánh bại?!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData TIEU_DOI_TRUONG_NM = BossData.builder()
            .name("Tiểu đội trưởng Namek")
            .gender(ConstPlayer.XAYDA)
            .outfit(new short[]{180, 181, 182, -1, -1, -1})
            .dame(15000)
            .hp(new int[]{5_000_000})
            .mapJoin(new int[]{7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43})
            .skillTemp(new int[][]{
                {Skill.DEMON, 7},
                {Skill.ANTOMIC, 7},
                {Skill.LIEN_HOAN, 7}
            })
            .textS(new String[]{
                "|-1|Đích thân Tiểu Đội Trưởng Namek sẽ dẹp sạch lũ tép riu các ngươi!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán", "|-1|Đại ca Fide có nhầm không nhỉ",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            })
            .textE(new String[]{
                "|-1|Đại ca Fide vĩ đại... Ta không hoàn thành nhiệm vụ rồi..."
            })
            .secondsRest(REST_10_M)
            .bossesAppearTogether(new int[]{BossID.SO_4_NM, BossID.SO_3_NM, BossID.SO_2_NM, BossID.SO_1_NM})
            .build();

    //========================FIDE========================
    public static final BossData FIDE_DAI_CA_1 = BossData.builder()
            .name("Fide đại ca 1")
            .gender(ConstPlayer.XAYDA)
            .dame(22000)
            .hp(new int[]{10000000})
            .outfit(new short[]{183, 184, 185, -1, -1, -1})
            .mapJoin(new int[]{80})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Khẩu khí ngang tàng lắm, để xem bản lĩnh của ngươi thế nào!"
            })
            .textM(new String[]{
                "|-1|Các ngươi tới số rồi mới gặp phải ta!",
                "|-1|Trận địa pháo mini đây!"
            })
            .textE(new String[]{
                "|-1|Ác quỷ biến hình, hây aaaa..."
            })
            .secondsRest(REST_10_M)
            .build();

    public static final BossData FIDE_DAI_CA_2 = BossData.builder()
            .name("Fide đại ca 2")
            .gender(ConstPlayer.XAYDA)
            .dame(25000)
            .hp(new int[]{20000000})
            .outfit(new short[]{186, 187, 188, -1, -1, -1})
            .mapJoin(new int[]{80})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Nếu đã biến thành thế này thì ta sẽ không nương tay như trước đâu!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả? Cố lên nhóc!",
                "|-1|Hahaha! Tốc độ kinh hoàng này ai mà né nổi chứ!",
                "|-1|Mấy đòn vừa rồi cũng đau đấy, nhưng đối thủ của mày là Fide này!"
            })
            .textE(new String[]{
                "|-1|Ác quỷ biến hình, Graaaaa...."
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData FIDE_DAI_CA_3 = BossData.builder()
            .name("Fide đại ca 3")
            .gender(ConstPlayer.XAYDA)
            .dame(30000)
            .hp(new int[]{30000000})
            .outfit(new short[]{189, 190, 191, -1, -1, -1})
            .mapJoin(new int[]{80})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Ta sẽ cho các ngươi thấy đâu mới là sức mạnh thật sự của Fide này!!"
            })
            .textM(new String[]{
                "|-1|Sức mạnh này của ta còn đáng sợ hơn địa ngục!!",
                "|-1|Hô hô hô hô, để ta tiễn ngươi về địa ngục!"
            })
            .textE(new String[]{
                "|-1|Lũ khốn... Một ngày nào đó ta sẽ quay lại trả thù các ngươi!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    //========================Boss Android========================
    public static final BossData DR_KORE = BossData.builder()
            .name("Dr.Kôrê")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{255, 256, 257, -1, -1, -1})
            .dame(12000)
            .hp(new int[]{2000000})
            .mapJoin(new int[]{96, 94, 93})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Mau đền mạng cho thằng em trai ta!"
            })
            .textM(new String[]{
                "|-1|Cho dù ngươi có mạnh đến đâu.. thì cũng không đánh bại được rôbốt bọn ta!",
                "|-1|Lôi Công Trảo! Hút hết năng lượng của các ngươi!",
                "|-1|Số 19! Xuất chiêu đi nào!"
            })
            .textE(new String[]{
                "|-1|Chết tiệt... không thể tin được ta lại thua lũ sâu bọ này..."
            })
            .secondsRest(REST_10_M)
            .bossesAppearTogether(new int[]{BossID.ANDROID_19})
            .build();

    public static final BossData ANDROID_19 = BossData.builder()
            .name("Android 19")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{249, 250, 251, -1, -1, -1})
            .dame(12200)
            .hp(new int[]{1000000})
            .mapJoin(new int[]{96, 94, 93})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Tuân lệnh đại ca Dr. Kôrê, em sẽ xử lý bọn này!"
            })
            .textM(new String[]{
                "|-1|Oải rồi hả? Cố lên nhóc!",
                "|-1|Ngươi sẽ không bao giờ thắng được người máy sát thủ đâu!"
            })
            .textE(new String[]{
                "|-1|Đại ca Kôrê... cứu em với..."
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData ANDROID_13 = BossData.builder()
            .name("Android 13")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{252, 253, 254, -1, -1, -1})
            .dame(12500)
            .hp(new int[]{6000000})
            .mapJoin(new int[]{104})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Mục tiêu của bọn ta là Songoku, kẻ nào cản đường đều phải chết!"
            })
            .textM(new String[]{
                "|-1|Sao thế hả? Ta mới chỉ khởi động thôi mà!",
                "|-1|Đừng có tưởng bở lũ sâu bọ, ta sẽ tiêu diệt tất cả!"
            })
            .textE(new String[]{
                "|-1|Sô.. Songoku....."
            })
            .secondsRest(REST_10_M)
            .bossesAppearTogether(new int[]{BossID.ANDROID_15, BossID.ANDROID_14})
            .build();

    public static final BossData ANDROID_14 = BossData.builder()
            .name("Android 14")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{246, 247, 248, -1, -1, -1})
            .dame(12000)
            .hp(new int[]{5000000})
            .mapJoin(new int[]{104})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Bọn ta là rôbốt sát thủ, sinh ra từ lòng căm thù để giết Songoku!"
            })
            .textM(new String[]{
                "|-1|Cản đường bọn ta chỉ có một con đường chết!",
                "|-1|Các ngươi không thể cảm nhận được khí của bọn ta đâu!"
            })
            .textE(new String[]{
                "|-1|Số 14 và số 15 tiêu tùng cả rồi à?"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData ANDROID_15 = BossData.builder()
            .name("Android 15")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{261, 262, 263, -1, -1, -1})
            .dame(12200)
            .hp(new int[]{4000000})
            .mapJoin(new int[]{104})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Android 15 xuất trận! Đừng hòng sống sót rời khỏi đây!"
            })
            .textM(new String[]{
                "|-1|Lũ con người yếu ớt!",
                "|-1|Ngươi chỉ đang tốn sức vô ích thôi!"
            })
            .textE(new String[]{
                "|-1|Thì ra vẫn chỉ là một đống sắt vụn!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData PIC = BossData.builder()
            .name("Pic")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{237, 238, 239, -1, -1, -1})
            .dame(17022)
            .hp(new int[]{10000000})
            .mapJoin(new int[]{97, 98, 99})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Chào! Có Goku ở đây không? Mau nói cho bọn ta biết!"
            })
            .textM(new String[]{
                "|-1|Lần này ta không nương tay đâu!",
                "|-1|Ngươi thực sự rất nhanh, nhưng chưa đủ thực lực đâu!"
            })
            .textE(new String[]{
                "|-1|Pic tiêu rồi, King Kong lên báo thù cho bọn ta!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData POC = BossData.builder()
            .name("Poc")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{240, 241, 242, -1, -1, -1})
            .dame(18000)
            .hp(new int[]{15000000})
            .mapJoin(new int[]{97, 98, 99})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Đừng tưởng ta là con gái mà dễ bắt nạt nhé!"
            })
            .textM(new String[]{
                "|-1|Đừng có mà trọng nam khinh nữ!",
                "|-1|Khôn hồn thì chỉ chỗ Goku cho bọn ta nhanh đi!"
            })
            .textE(new String[]{
                "|-1|Poc tiêu rồi, Pic lên tiếp chiêu đi!"
            })
            .typeAppear(AppearType.APPEAR_WITH_ANOTHER)
            .build();

    public static final BossData KING_KONG = BossData.builder()
            .name("King Kong")
            .gender(ConstPlayer.TRAI_DAT)
            .outfit(new short[]{243, 244, 245, -1, -1, -1})
            .dame(12000)
            .hp(new int[]{20000000})
            .mapJoin(new int[]{97, 98, 99})
            .skillTemp(new int[][]{
                {Skill.MASENKO, 7},
                {Skill.GALICK, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|King Kong đã tới! Mau đền mạng cho đồng đội của ta!"
            })
            .textM(new String[]{
                "|-1|Sức mạnh của ta chênh nhau với các ngươi một trời một vực đấy!",
                "|-1|Đừng ép ta phải dùng đến toàn bộ sức mạnh!"
            })
            .textE(new String[]{
                "|-1|Bảo vệ tự nhiên... và những loài chim nhỏ bé..."
            })
            .secondsRest(REST_10_M)
            .bossesAppearTogether(new int[]{BossID.PIC, BossID.POC})
            .build();
     
    //========================Boss cell========================
    public static final BossData XEN_BO_HUNG_1 = BossData.builder()
            .name("Xên bọ hung")
            .gender(ConstPlayer.XAYDA)
            .dame(22000)
            .hp(new int[]{50000000})
            .outfit(new short[]{228, 229, 230, -1, -1, -1})
            .mapJoin(new int[]{100})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.DICH_CHUYEN_TUC_THOI, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Hôm nay sẽ là ngày đáng nhớ đây!",
                "|-1|Ta sẽ hấp thụ số 17 và 18 để đạt được dạng hoàn hảo!",
                "|-1|Sao vậy? Nếu muốn ngăn cản ta thì xông lên hết đi!"
            })
            .textM(new String[]{
                "|-1|Đến đây nào! Khi kết hợp với ta, các ngươi sẽ trở thành một phần của thể sống hoàn mỹ!",
                "|-1|Làm đứt đuôi ta ư? Đừng quên ta có tế bào của Picôlô, ta có thể tái tạo mọi bộ phận cơ thể!",
                "|-1|Có vẻ như ta đã trở nên quá mạnh, ta đã giết nhiều người hơn dự tính!!",
                "|-1|Các ngươi không thể thắng nổi ta đâu! Mau từ bỏ đi!!",
                "|-1|Hê hê hê, rồi ngươi sẽ trở thành một phần của con quái vật này thôi!"
            })
            .textE(new String[]{
                "|-1|Á... Hahaha! Cảm ơn số 17! Chuẩn bị chiêm ngưỡng hình thái mới của ta đây!!"
            })
            .secondsRest(REST_10_M)
            .build();

    public static final BossData XEN_BO_HUNG_2 = BossData.builder()
            .name("Xên bọ hung 2")
            .gender(ConstPlayer.XAYDA)
            .dame(26000)
            .hp(new int[]{100000000})
            .outfit(new short[]{231, 232, 233, -1, -1, -1})
            .mapJoin(new int[]{100})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.DICH_CHUYEN_TUC_THOI, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Các ngươi hãy run sợ trước hình thái cấp 2 của Xên bọ hung ta đi!"
            })
            .textM(new String[]{
                "|-1|Các ngươi nghĩ có thể chạy được sao!?",
                "|-1|Muốn chạy khỏi ta thì đừng hòng!!",
                "|-1|Ta cũng ngạc nhiên với tốc độ của mình! Đó tất nhiên là do ta hấp thụ được số 17!",
                "|-1|Sức mạnh này đã tăng lên gấp bội!",
                "|-1|Đã đến lúc ta tìm số 18 để đạt đến trạng thái hoàn hảo!"
            })
            .textE(new String[]{
                "|-1|Số 18... Ngươi trốn đi đâu được chứ? Đến lúc rồi... Graaaaa!!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData XEN_BO_HUNG_3 = BossData.builder()
            .name("Xên hoàn thiện")
            .gender(ConstPlayer.XAYDA)
            .dame(30000)
            .hp(new int[]{150000000})
            .outfit(new short[]{234, 235, 236, -1, -1, -1})
            .mapJoin(new int[]{100})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.DICH_CHUYEN_TUC_THOI, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Cuối cùng ta cũng đã đạt được trạng thái hoàn hảo tuyệt đối!!"
            })
            .textM(new String[]{
                "|-1|Xin lỗi.. Ngươi có thể giúp ta làm nóng cơ thể lên không!?",
                "|-1|Thì ta đã bảo đây chỉ là khởi động làm nóng người thôi mà!!",
                "|-1|Giờ các ngươi chỉ là rác rưởi trước mặt ta mà thôi!",
                "|-1|Không một ai có thể đánh bại được thể sống hoàn hảo này!!"
            })
            .textE(new String[]{
                "|-1|Oái.. không...",
                "|-1|Cơ thể hoàn hảo của ta!!",
                "|-1|Ta không tin chuyện này sẽ xảy ra!!",
                "|-1|Đồ khốn kiếp!! Rồi các ngươi sẽ phải trả giá...!!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData SIEU_BO_HUNG_1 = BossData.builder()
            .name("Xên Hoàn Thiện")
            .gender(ConstPlayer.XAYDA)
            .dame(35000)
            .hp(new int[]{150000000})
            .outfit(new short[]{234, 235, 236, -1, -1, -1})
            .mapJoin(new int[]{103})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.DICH_CHUYEN_TUC_THOI, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Chào mừng các ngươi đến với võ đài Xên bọ hung!",
                "|-1|Kẻ nào dám bước lên đây thách đấu với ta!?"
            })
            .textM(new String[]{
                "|-1|Chỉ có bấy nhiêu thực lực thôi sao?",
                "|-1|Võ đài này sẽ là mồ chôn của tất cả các ngươi!",
                "|-1|Hahaha! Tuyệt vọng chưa lũ sâu bọ?"
            })
            .textE(new String[]{
                "|-1|Không thể nào... Ta lại thua lũ sâu bọ này sao?!",
                "|-1|Các ngươi đừng vội mừng... Hãy nếm thử sức mạnh tự hủy diệt của ta!!"
            })
            .secondsRest(REST_30_M)
            .bossesAppearTogether(new int[]{
                BossID.XEN_CON_1, BossID.XEN_CON_2, BossID.XEN_CON_3,
                BossID.XEN_CON_4, BossID.XEN_CON_5, BossID.XEN_CON_6, BossID.XEN_CON_7
            })
            .build();

    public static final BossData SIEU_BO_HUNG_2 = BossData.builder()
            .name("Siêu Bọ Hung")
            .gender(ConstPlayer.XAYDA)
            .dame(45000)
            .hp(new int[]{200000000})
            .outfit(new short[]{234, 235, 236, -1, -1, -1})
            .mapJoin(new int[]{103})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.DICH_CHUYEN_TUC_THOI, 7},
                {Skill.KHIEN_NANG_LUONG, 7},
                {Skill.THAI_DUONG_HA_SAN, 7}
            })
            .textS(new String[]{
                "|-1|Hahaha! Nhờ có tế bào Saiyan, ta đã hồi sinh từ cõi chết!",
                "|-1|Và bây giờ, ta đã đạt đến sức mạnh tối thượng - Siêu Bọ Hung!!"
            })
            .textM(new String[]{
                "|-1|Sức mạnh này vượt xa những gì các ngươi có thể tưởng tượng!",
                "|-1|Biến mất cùng với hành tinh này đi! Kamejoko toàn lực!!",
                "|-1|Không ai có thể cứu được Trái Đất này nữa đâu!"
            })
            .textE(new String[]{
                "|-1|Guh... Không thể nào... Ta là tạo vật hoàn mỹ nhất của tiến sĩ Gero mà...!!",
                "|-1|Aaaaaaaarrrrghhhhhhh!!!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData XEN_CON = BossData.builder()
            .name("Xên con")
            .gender(ConstPlayer.XAYDA)
            .dame(15000)
            .hp(new int[]{5000000})
            .outfit(new short[]{264, 265, 266, -1, -1, -1})
            .mapJoin(new int[]{103})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.LIEN_HOAN, 7},
                {Skill.GALICK, 7}
            })
            .textS(new String[]{
                "|-1|Tụi tao là Xên con đây!",
                "|-1|Xông lên anh em ơi, đánh hội đồng chúng nó!!"
            })
            .textM(new String[]{
                "|-1|Lêu lêu! Đánh không trúng kìa!",
                "|-1|Yếu thế này mà đòi đấu với bố ta sao!?",
                "|-1|Bắn chưởng nè! Haha!"
            })
            .textE(new String[]{
                "|-1|Oan quá... bố ơi trả thù cho con..."
            })
            .typeAppear(AppearType.CALL_BY_ANOTHER)
            .build();

    //========================BLACK GOKU========================
    public static final BossData BLACK_GOKU = BossData.builder()
            .name("Black Goku")
            .gender(ConstPlayer.TRAI_DAT)
            .dame(50000)
            .hp(new int[]{500000000})
            .outfit(new short[]{550, 551, 552, -1, -1, -1})
            .mapJoin(new int[]{102, 92, 93, 94, 96, 97, 98, 99, 100})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.TAI_TAO_NANG_LUONG, 7},
                {Skill.KHIEN_NANG_LUONG, 7},
                {Skill.GALICK, 7}
            })
            .textS(new String[]{
                "|-1|Ta là Sôn Gô Ku",
                "|-1|Cơ thể này, sức mạnh này",
                "|-1|Ta khá thích việc loại bỏ các ngươi",
                "|-1|Mau chấp nhận số phận đi lũ sâu bọ"
            })
            .textM(new String[]{
                "|-1|Các ngươi chỉ có vậy thôi sao?",
                "|-1|Đúng là loài người thấp kém",
                "|-2|Ngươi nói như thể ngươi không phải con người vậy?",
                "|-2|Chiếc nhẫn kia lẽ nào ngươi là một Kaioshin?!",
                "|-1|Các ngươi không nên biết quá nhiều",
                "|-2|Xem đòn đánh của ta đây!",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!"
            })
            .textE(new String[]{
                "|-1|Biến hình! Super Saiyan Rose"
            })
            .secondsRest(REST_30_M)
            .build();

    public static final BossData SUPER_BLACK_GOKU = BossData.builder()
            .name("Super Black Goku")
            .gender(ConstPlayer.TRAI_DAT)
            .dame(100000)
            .hp(new int[]{2_000_000_000})
            .outfit(new short[]{553, 551, 552, -1, -1, -1})
            .mapJoin(new int[]{102, 92, 93, 94, 96, 97, 98, 99, 100})
            .skillTemp(new int[][]{
                {Skill.THAI_DUONG_HA_SAN, 7},
                {Skill.TAI_TAO_NANG_LUONG, 7},
                {Skill.KHIEN_NANG_LUONG, 7},
                {Skill.KAMEJOKO, 7},
                {Skill.GALICK, 7}
            })
            .textM(new String[]{
                "|-1|Ta chính là người mang thân thể của Songoku",
                "|-1|Sức mạnh của ta là không có giới hạn",
                "|-1|Ta sẽ thống trị vũ trụ",
                "|-1|Để ta nói cho nghe, người Saiyan sau khi hồi phục sức mạnh sẽ tăng lên rất nhiều",
                "|-2|Tại sao ngươi lại lấy thân thể của Songoku chứ?"
            })
            .textE(new String[]{
                "|-1|Chúng ta sẽ gặp lại nhau sớm thôi",
                "|-2|Ngươi nói gì chứ?"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    //========================CUMBER========================
    public static final BossData CUMBER = BossData.builder()
            .name("Cumber")
            .gender(ConstPlayer.TRAI_DAT)
            .dame(50000)
            .hp(new int[]{500000000})
            .outfit(new short[]{1254, 1255, 1256, -1, -1, -1})
            .mapJoin(new int[]{155})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.TAI_TAO_NANG_LUONG, 7},
                {Skill.KHIEN_NANG_LUONG, 7},
                {Skill.GALICK, 7}
            })
            .textS(new String[]{
                "|-1|Ta là Cumber ác quỷ Saiyan!",
                "|-1|Cơ thể này, sức mạnh này sẽ nghiền nát tất cả!",
                "|-1|Mau chấp nhận số phận đi lũ sâu bọ!"
            })
            .textM(new String[]{
                "|-1|Các ngươi chỉ có vậy thôi sao?",
                "|-1|Đúng là lũ sâu bọ yếu ớt!",
                "|-1|Xem đòn tà khí hủy diệt của ta đây!!"
            })
            .textE(new String[]{
                "|-1|Biến hình! Super Saiyan Tà Ác!!"
            })
            .secondsRest(REST_30_M)
            .build();

    public static final BossData SUPER_CUMBER = BossData.builder()
            .name("Super Cumber")
            .gender(ConstPlayer.TRAI_DAT)
            .dame(100000)
            .hp(new int[]{2_000_000_000})
            .outfit(new short[]{1257, 1255, 1256, -1, -1, -1})
            .mapJoin(new int[]{155})
            .skillTemp(new int[][]{
                {Skill.THAI_DUONG_HA_SAN, 7},
                {Skill.TAI_TAO_NANG_LUONG, 7},
                {Skill.KHIEN_NANG_LUONG, 7},
                {Skill.KAMEJOKO, 7},
                {Skill.GALICK, 7}
            })
            .textM(new String[]{
                "|-1|Sức mạnh tà ác của ta là vô hạn!",
                "|-1|Hãy tan biến vào hư vô đi!",
                "|-1|Gaaaaahhhhhh!!!!"
            })
            .textE(new String[]{
                "|-1|Không thể nào... ta lại bị đánh bại sao?!",
                "|-1|Các ngươi hãy đợi đấy..."
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    //========================Majin Buu========================
    public static final BossData MABU = BossData.builder()
            .name("Mabư mập")
            .gender(ConstPlayer.XAYDA)
            .dame(500000)
            .hp(new int[]{50000000})
            .outfit(new short[]{297, 298, 299, -1, -1, -1})
            .mapJoin(new int[]{127})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.DRAGON, 7},
                {Skill.SOCOLA, 7},
            })
            .textS(new String[]{
                "|-1|Bư! Bư! Bư!",
                "|-1|Ai cho Bư kẹo đi!",
                "|-1|Mau cho Bư kẹo không Bư biến thành kẹo bây giờ!"
            })
            .textM(new String[]{
                "|-1|Xem đòn Độn Thổ Đá Ngầm của ta đây!",
                "|-1|Úm ba la xì bùa, biến thành kẹo hết đi!",
                "|-1|Măm măm... Ngon quá!"
            })
            .textE(new String[]{
                "|-1|Biến hình!"
            })
            .secondsRest(REST_10_M)
            .build();

    public static final BossData SUPER_BU = BossData.builder()
            .name("Super Bư")
            .gender(ConstPlayer.XAYDA)
            .dame(500000)
            .hp(new int[]{60000000})
            .outfit(new short[]{421, 422, 423, -1, -1, -1})
            .mapJoin(new int[]{127})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.DRAGON, 7},
                {Skill.NUOT_CHUNG, 7, 60000},
                {Skill.DA_DON_THO, 7, 6000}
            })
            .textS(new String[]{
                "|-1|Ta là Super Bư!",
                "|-1|Các ngươi chỉ là lũ sâu bọ!"
            })
            .textM(new String[]{
                "|-1|Xem đòn Độn Thổ Đá Ngầm của ta đây!",
                "|-1|Các ngươi không thoát khỏi tay ta đâu!"
            })
            .textE(new String[]{
                "|-1|Biến hình!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData BU_TENK = BossData.builder()
            .name("Bư Tênk")
            .gender(ConstPlayer.XAYDA)
            .dame(500000)
            .hp(new int[]{80000000})
            .outfit(new short[]{424, 425, 426, -1, -1, -1})
            .mapJoin(new int[]{127})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.DRAGON, 7},
                {Skill.TAI_TAO_NANG_LUONG, 7},
                {Skill.NUOT_CHUNG, 7, 60000},
                {Skill.DA_DON_THO, 7, 6000}
            })
            .textS(new String[]{
                "|-1|Sức mạnh của Gotenks giờ là của ta!",
                "|-1|Xem tuyệt kỹ của ta đây!"
            })
            .textM(new String[]{
                "|-1|Xem đòn Độn Thổ Đá Ngầm của ta đây!",
                "|-1|Ui da đau bụng quá!"
            })
            .textE(new String[]{
                "|-1|Biến hình!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData BU_HAN = BossData.builder()
            .name("Bư Han")
            .gender(ConstPlayer.XAYDA)
            .dame(500000)
            .hp(new int[]{100000000})
            .outfit(new short[]{427, 428, 429, -1, -1, -1})
            .mapJoin(new int[]{127})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.DRAGON, 7},
                {Skill.SOCOLA, 7},
                {Skill.TAI_TAO_NANG_LUONG, 5},
                {Skill.NUOT_CHUNG, 7, 60000},
                {Skill.DA_DON_THO, 7, 6000}
            })
            .textS(new String[]{
                "|-1|Ta là Bư Han bất khả chiến bại!",
                "|-1|Không ai có thể vượt qua ta!"
            })
            .textM(new String[]{
                "|-1|Xem đòn Độn Thổ Đá Ngầm của ta đây!",
                "|-1|Chịu chết đi!"
            })
            .textE(new String[]{
                "|-1|Biến hình!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData KID_BU = BossData.builder()
            .name("Kid Bư")
            .gender(ConstPlayer.XAYDA)
            .dame(500000)
            .hp(new int[]{150000000})
            .outfit(new short[]{439, 440, 441, -1, -1, -1})
            .mapJoin(new int[]{127})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.DRAGON, 7},
                {Skill.SOCOLA, 7},
                {Skill.TAI_TAO_NANG_LUONG, 7},
                {Skill.NUOT_CHUNG, 7, 60000},
                {Skill.DA_DON_THO, 7, 6000}
            })
            .textS(new String[]{
                "|-1|Ke... ke... ke...!",
                "|-1|Gaaaaahhhhhh!"
            })
            .textM(new String[]{
                "|-1|Xem đòn Độn Thổ Đá Ngầm của ta đây!",
                "|-1|Kyaaaahahaha!"
            })
            .textE(new String[]{
                "|-1|Biến hình!"
            })
            .typeAppear(AppearType.ANOTHER_LEVEL)
            .build();

    public static final BossData SUPER_BU_BUNG = BossData.builder()
            .name("Super Bư")
            .gender(ConstPlayer.XAYDA)
            .dame(500000)
            .hp(new int[]{50000000})
            .outfit(new short[]{421, 422, 423, -1, -1, -1})
            .mapJoin(new int[]{127, 128})
            .skillTemp(new int[][]{
                {Skill.KAMEJOKO, 7},
                {Skill.DRAGON, 7}
            })
            .textS(new String[]{
                "|-1|Các ngươi dám vào tận bụng ta sao?!",
                "|-1|Đừng hòng thoát khỏi đây!"
            })
            .textM(new String[]{
                "|-1|Khí công pháo!",
                "|-1|Các ngươi sẽ bị tiêu hóa tại đây!"
            })
            .secondsRest(REST_10_S)
            .build();

    //========================BOSS 12H========================
    public static final BossData MABU_12H = new BossData(
            "Mabư",
            ConstPlayer.XAYDA,
            new short[]{297, 298, 299, -1, -1, -1},
            10000,
            new int[]{100000000},
            new int[]{120},
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 3, 1200000},
                {Skill.GALICK, 7, 1000}},
            new String[]{"|-1|Bư! Bư! Bư!",
                "|-1|Bư! Bư!",}, //text chat 1
            new String[]{"|-1|Oe Oe"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M,
            new int[]{BossID.DRABURA_3}
    );

    public static final BossData GOKU = new BossData(
            "Gôku", //name
            ConstPlayer.XAYDA, //gender
            new short[]{101, 65, 66, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{60000000}, //hp
            new int[]{119}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 1000000},
                {Skill.THAI_DUONG_HA_SAN, 1, 60000}},//skill
            new String[]{"|-1|Tỉnh lại đi Cađíc!",
                "|-1| ",
                "|-1|Đừng lùa gà nữa",
                "|-1|Một Server là quá đủ rồi!",
                "|-1|Các em chỉ có 10k ăn sáng thôi",
                "|-1| ",
                "|-1| ",
                "|-1| "}, //text chat 1
            new String[]{"|-1|Cađíc tỉnh lại đi!",
                "|-1|Trời ơi...Hắn định giết mọi người ở đây chắc?",
                "|-1|Cađíc! dừng tay lại! Cậu điên mất rồi!",
                "|-1|Cađíc tỉnh lại đi!",}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.CALL_BY_ANOTHER
    );

    public static final BossData CADIC = new BossData(
            "Ca Đít", //name
            ConstPlayer.XAYDA, //gender
            new short[]{103, 16, 17, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{60000000}, //hp
            new int[]{119}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 1000000},},
            new String[]{"|-1| ",
                "|-1|Không, còn điện thoại mới, xe mới, nhà mới thì sao",
                "|-1| ",
                "|-1| ",
                "|-1| ",
                "|-1|Khônggggggggggggggggggg!",
                "|-1|Tối nay ta chuẩn bị ăn gà ngập mồm rồi",
                "|-1| ",
                "|-1|Từ giờ hãy gọi ta là CađícMẹc"}, //text chat 1
            new String[]{"|-1|Chúng ta sẽ 1 mất 1 còn!",
                "|-1|Hãy xem đây",
                "|-1|Ngươi có một tội rất lớn là...",
                "|-1|Dám qua mặt ta",
                "|-1|Kakalốt! Ta chờ đợi giây phút này đã từ lâu!"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.CALL_BY_ANOTHER
    );

    public static final BossData DRABURA = new BossData(
            "Drabura",
            ConstPlayer.XAYDA,
            new short[]{418, 419, 420, -1, -1, -1},
            10000,
            new int[]{20000000},
            new int[]{114},
            new int[][]{
                {Skill.GALICK, 7, 1000}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi"}, //text chat 3
            REST_1_M
    );
    public static final BossData DRABURA_2 = new BossData(
            "Drabura",
            ConstPlayer.XAYDA,
            new short[]{418, 419, 420, -1, -1, -1},
            200000,
            new int[]{20000000},
            new int[]{119},
            new int[][]{
                {Skill.GALICK, 7, 1000}},
            new String[]{"|-1|Ta đã trở lại, lợi hại gấp hai, hahaha"}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Hêhê..ta chẳng cần tốn sức đánh với các ngươi nữa",
                "|-1|Mà ta sẽ để cho các ngươi tự thanh toán lẫn nhau, xin chào",
                "|-2|Hắn nói sẽ để chúng ta tự tiêu diệt lẫn nhau",
                "|-2|Nghĩa là sao nhỉ?"}, //text chat 3
            REST_5_M,
            new int[]{BossID.GOKU, BossID.CADIC}
    );

    public static final BossData DRABURA_3 = new BossData(
            "Drabura",
            ConstPlayer.XAYDA,
            new short[]{418, 419, 420, -1, -1, -1},
            100000,
            new int[]{20000000},
            new int[]{114},
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 7, 10000000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{"|-1|Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi"}, //text chat 3
            AppearType.CALL_BY_ANOTHER
    );

    public static final BossData BUI_BUI = new BossData(
            "Bui Bui",
            ConstPlayer.XAYDA,
            new short[]{451, 452, 453, -1, -1, -1},
            200000,
            new int[]{40000000},
            new int[]{115},
            new int[][]{
                {Skill.GALICK, 7, 10000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Hãy xem đây nhóc"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M
    );

    public static final BossData BUI_BUI_2 = new BossData(
            "Bui Bui",
            ConstPlayer.XAYDA,
            new short[]{451, 452, 453, -1, -1, -1},
            200000,
            new int[]{40000000},
            new int[]{117},
            new int[][]{
                {Skill.GALICK, 7, 10000}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Trọng lực bây giờ đã tăng gấp 10 lần",
                "|-1|Đó là điều kiện không gian lý tưởng của ta",
                "|-1|Nhưng lại rất bất lợi cho bọn mi"}, //text chat 2
            new String[]{"|-1|Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi"}, //text chat 3
            REST_1_M
    );

    public static final BossData YACON = new BossData(
            "Ya côn",
            ConstPlayer.XAYDA,
            new short[]{415, 416, 417, -1, -1, -1},
            200000,
            new int[]{50000000},
            new int[]{118},
            new int[][]{
                {Skill.GALICK, 7, 100}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M
    );

    //========================DHVT========================
    public static final BossData SOI_HEC_QUYN = new BossData(
            "Sói hẹc quyn", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{394, 395, 396, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{10000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110},
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M //second rest
    );

    public static final BossData O_DO = new BossData(
            "Ở dơ", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{400, 401, 402, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            3000, //dame
            new int[]{25000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData XINBATO = new BossData(
            "Xinbatô", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{359, 360, 361, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            6000, //dame
            new int[]{50000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData CHA_PA = new BossData(
            "Cha pa", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{362, 363, 364, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            9000, //dame
            new int[]{100000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );
    public static final BossData PON_PUT = new BossData(
            "Pon put", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{365, 366, 367, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{250000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData CHAN_XU = new BossData(
            "Chan xư", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{371, 372, 373, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData TAU_PAY_PAY = new BossData(
            "Tàu Pảy Pảy", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{92, 93, 94, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            15000, //dame
            new int[]{2000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData YAMCHA = new BossData(
            "Yamcha", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{374, 375, 376, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            15000, //dame
            new int[]{5000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData POCOLO = new BossData(
            "PôCôLô", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{9, 12, 13, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new int[]{100000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData JACKY_CHUN = new BossData(
            "Jacky Chun", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{356, 357, 358, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            20000, //dame
            new int[]{25000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData THIEN_XIN_HANG = new BossData(
            "Thiên xin hăng", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{368, 369, 370, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            22000, //dame
            new int[]{75000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );
    public static final BossData THIEN_XIN_HANG_CLONE = new BossData(
            "Thiên xin hăng", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{368, 369, 370, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            5000, //dame
            new int[]{20000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData LIU_LIU = new BossData(
            "Liu Liu", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{397, 398, 399, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            30000, //dame
            new int[]{150000000}, //hp
            new int[]{168}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_S //second rest
    );

    public static final BossData TAU_PAY_PAY_DONG_NAM_KARIN = new BossData(
            "Tàu Pảy Pảy", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{338, 339, 340, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new int[]{10000}, //hp
            new int[]{111}, //map join
            new int[][]{
                {Skill.KAMEJOKO, 7, 5000},
                {Skill.GALICK, 7, 1000},},
            //skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //second rest
    );

    //========================Bojack========================
    public static final BossData BUJIN = new BossData(
            "Bujin", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{341, 342, 343, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            170000, //dame
            new int[]{20000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.DEMON, 7, 1000},
                {Skill.MASENKO, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán",
                "|-1|Các ngươi không nhúc nhích được sao?",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!",
                "|-1|Ta mà lại thua được sao?",
                "|-1|Hãy trả thù cho ta!"
            }, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER
    );

    public static final BossData KOGU = new BossData(
            "Kogu", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{329, 330, 331, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            180000, //dame
            new int[]{40000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 100000},
                {Skill.DRAGON, 7, 1000},
                {Skill.TROI, 4, 50000},
                {Skill.ANTOMIC, 4, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Trói"
            }, //text chat 2
            new String[]{"|-1|Cứu"
            }, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData ZANGYA = new BossData(
            "Zangya", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{332, 333, 334, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            207200, //dame
            new int[]{60000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 100000},
                {Skill.GALICK, 7, 1000},
                {Skill.TROI, 5, 50000},
                {Skill.ANTOMIC, 3, 3000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Trói"
            }, //text chat 2
            new String[]{"|-1|Cứu"
            }, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData BIDO = new BossData(
            "Bido", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{335, 336, 337, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            250200, //dame
            new int[]{80000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 100000},
                {Skill.DRAGON, 7, 1000},
                {Skill.KAMEJOKO, 4, 10000},},//skill//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Oải rồi hả?", "|-1|Ê cố lên nhóc",
                "|-1|Chán",
                "|-1|Một mình tao chấp hết tụi bây",
                "|-1|HAHAHAHA", "|-1|Chỉ là bọn con nít"
            }, //text chat 2
            new String[]{"|-1|Cay quá!"
            }, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData BOJACK = new BossData(
            "Bojack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{323, 324, 325, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            300000, //dame
            new int[]{100000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 100000},
                {Skill.TROI, 7, 120000},
                {Skill.MASENKO, 7, 1000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Hahaha"
            }, //text chat 2
            new String[]{"|-1|Hahaha"
            }, //text chat 3
            REST_15_M,
            new int[]{BossID.BUJIN, BossID.KOGU, BossID.BIDO, BossID.ZANGYA,} //type appear
    );

    public static final BossData SUPER_BOJACK = new BossData(
            "Siêu Bojack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{326, 327, 328, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            300000, //dame
            new int[]{150000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 100000},
                {Skill.THOI_MIEN, 7, 100000},
                {Skill.KHIEN_NANG_LUONG, 7, 100000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Hahaha"
            }, //text chat 2
            new String[]{"|-1|Hahaha"
            }, //text chat 3
            REST_30_M,
            AppearType.ANOTHER_LEVEL //type appear
    );

    public static final BossData SUPER_BOJACK_2 = new BossData(
            "Siêu Bojack", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{326, 327, 328, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            300000, //dame
            new int[]{500000000}, //hp
            new int[]{3, 4, 5, 6, 27, 28, 29, 30}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, 100000},
                {Skill.TROI, 3, 60000},
                {Skill.KAMEJOKO, 7, 1000},
                {Skill.GALICK, 7, 1000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Hahaha"
            }, //text chat 2
            new String[]{"|-1|Hahaha"
            }, //text chat 3
            REST_30_M
    );

    //========================Yardrat========================
    public static final BossData TAP_SU_0 = new BossData(
            "Tập sự-0", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 500},
                {Skill.ANTOMIC, 1, 500},
                {Skill.KAMEJOKO, 1, 500},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_1 = new BossData(
            "Tập sự-1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_2 = new BossData(
            "Tập sự-2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_3 = new BossData(
            "Tập sự-3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAP_SU_4 = new BossData(
            "Tập sự-4", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{526, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{350000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_5 = new BossData(
            "Tân binh-5", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{450000}, //hp
            new int[]{131}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S,
            new int[]{BossID.TAP_SU_0, BossID.TAP_SU_1, BossID.TAP_SU_2, BossID.TAP_SU_3, BossID.TAP_SU_4} //type appear
    );

    public static final BossData TAN_BINH_0 = new BossData(
            "Tân binh-0", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_1 = new BossData(
            "Tân binh-1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_2 = new BossData(
            "Tân binh-2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_3 = new BossData(
            "Tân binh-3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData TAN_BINH_4 = new BossData(
            "Tân binh-4", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{527, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{450000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_5 = new BossData(
            "Chiến binh-5", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{132}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S,
            new int[]{BossID.TAN_BINH_0, BossID.TAN_BINH_1, BossID.TAN_BINH_2, BossID.TAN_BINH_3, BossID.TAN_BINH_4} //type appear
    );

    public static final BossData CHIEN_BINH_0 = new BossData(
            "Chiến binh-0", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_1 = new BossData(
            "Chiến binh-1", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_2 = new BossData(
            "Chiến binh-2", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_3 = new BossData(
            "Chiến binh-3", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData CHIEN_BINH_4 = new BossData(
            "Chiến binh-4", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{528, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{500000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );

    public static final BossData DOI_TRUONG_5 = new BossData(
            "Đội trưởng-5", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{529, 525, 524, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{1000000}, //hp
            new int[]{133}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 1000},
                {Skill.ANTOMIC, 1, 1000},
                {Skill.KAMEJOKO, 1, 1000},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S,
            new int[]{BossID.CHIEN_BINH_0, BossID.CHIEN_BINH_1, BossID.CHIEN_BINH_2, BossID.CHIEN_BINH_3, BossID.CHIEN_BINH_4} //type appear
    );

    //========================Boss Võ đài bà hạt mít========================
    public static final BossData DRACULA = new BossData(
            "Đracula", //name
            ConstPlayer.XAYDA, //gender
            new short[]{353, 354, 355, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{5000}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData NGUOI_VO_HINH = new BossData(
            "Người vô hình", //name
            ConstPlayer.XAYDA, //gender
            new short[]{377, 378, 379, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{10000}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData BONG_BANG = new BossData(
            "Bông băng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{350, 351, 352, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            2000, //dame
            new int[]{15000}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData VUA_QUY_SA_TANG = new BossData(
            "Vua Quỷ Sa tăng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{344, 345, 346, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            2000, //dame
            new int[]{20000}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData THO_DAU_BAC = new BossData(
            "Thỏ Đầu Bạc", //name
            ConstPlayer.XAYDA, //gender
            new short[]{347, 348, 349, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            3000, //dame
            new int[]{30000}, //hp
            new int[]{112}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );
    //========================Boss event========================
        //======================Trung thu=======================
    public static final BossData KHIDOT = new BossData(
            "Khỉ đột", //name
            ConstPlayer.XAYDA, //gender
            new short[]{198, 193, 194, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new int[]{100000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.ANTOMIC, 7, 3000},
                {Skill.BIEN_KHI, 7, 60000},},//skill
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_15_M //second rest
    );

    public static final BossData NGUYETTHAN = new BossData(
            "Nguyệt thần", //name
            ConstPlayer.XAYDA, //gender
            new short[]{2058, 2059, 2060, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{50000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_15_M,//second rest
            new int[]{BossID.NHATTHAN}
    );

    public static final BossData NHATTTHAN = new BossData(
            "Nhật thần", //name
            ConstPlayer.XAYDA, //gender
            new short[]{2065, 2066, 2067, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{50000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER
    );
        //======================Halloween=======================
    public static final BossData MA_TROI = new BossData(
            "Ma trơi", //name
            ConstPlayer.XAYDA, //gender
            new short[]{651, 652, 653, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new int[]{500000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khà khà"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M
    );

    public static final BossData DOI = new BossData(
            "Dơi", //name
            ConstPlayer.XAYDA, //gender
            new short[]{654, 655, 656, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new int[]{500000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khà khà"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M
    );

    public static final BossData BI_MA = new BossData(
            "Bí ma", //name
            ConstPlayer.XAYDA, //gender
            new short[]{760, 761, 762, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100, //dame
            new int[]{500000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.GALICK, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Khà khà"}, //text chat 2
            new String[]{}, //text chat 3
            REST_10_M
    );
        //======================Noel=======================
    public static final BossData ONG_GIA_NOEL = new BossData(
            "Ông già Noel", //name
            ConstPlayer.XAYDA, //gender
            new short[]{657, 658, 659, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            5000000, //dame
            new int[]{500}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Mé ri chịch mệt",
                "|-1|Hô hô hô",
                "|-1|Giáng sinh vui vẻ!"}, //text chat 2
            new String[]{"|-1|Giáng sinh vui vẻ!"},
            REST_1_M //type appear
    );
        //======================Hùng vương=======================
    public static final BossData THUY_TINH = new BossData(
            "Thủy Tinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1686, 1687, 1688, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{50000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Trả Mị Nương lại cho ta",
                "|-1|Ta cho nước dâng chìm cả lũ bây giờ"}, //text chat 2
            new String[]{}, //text chat 3
            REST_15_M,//second rest
            new int[]{BossID.SON_TINH}
    );

    public static final BossData SON_TINH = new BossData(
            "Sơn Tinh", //name
            ConstPlayer.XAYDA, //gender
            new short[]{1683, 1684, 1685, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{50000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{}, //text chat 1
            new String[]{"|-1|Còn lâu á, chậm chân ráng chịu đi cưng",
                "|-1|Ta thách, chiêu này quá quen rồi"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER
    );
        //======================Tết=======================
    public static final BossData LAN_CON = new BossData(
            "Lân con", //name
            ConstPlayer.XAYDA, //gender
            new short[]{763, 764, 765, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            5000, //dame
            new int[]{5000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 7, Util.nextInt(5000, 10000)}},
            new String[]{}, //text chat 1
            new String[]{"|-1|Tùng tùng xèng xèng"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );
    //======================Cooler=======================
    public static final BossData COOLER = new BossData(
            "Cooler", //name
            ConstPlayer.XAYDA, //gender
            new short[]{317, 318, 319, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            32000, //dame
            new int[]{200000000}, //hp
            new int[]{110}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{"|-1|Ta sẽ cho chúng bây biết sức mạnh thực sự của dân tộc Frost Demons"
            }, //text chat 1
            new String[]{"|-1|Tụi mày có giỏi thì xông vào cứu hắn đi",
                "|-1|Trận địa pháo mini",
                "|-1|Nên giải quyết con sâu nào trước đây",
                "|-1|HA HA HA",
                "|-1|Ta chính là Vũ Trụ Đệ Nhất Cao Thủ",
                "|-1|Xí hụt ha ha",
                "|-1|Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha",
                "|-1|Chúng mày nghĩ kiến lại thắng nổi khủng long sao",
                "|-1|Hô hô hô",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!"
            }, //text chat 2
            new String[]{"|-1|Nãy giờ ta chưa thèm tung hết sức đâu",
                "|-1|Biến hình, hây aaaa..."}, //text chat 3
            REST_30_M //second rest
    );

    public static final BossData COOLER_2 = new BossData(
            "Cooler 2", //name
            ConstPlayer.XAYDA, //gender
            new short[]{320, 321, 322, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            50000, //dame
            new int[]{500000000}, //hp
            new int[]{110}, //map join
            new int[][]{
                {Skill.GALICK, 1, 2000},
                {Skill.ANTOMIC, 1, 6000},},
            new String[]{"|-1|Ta sẽ cho chúng bây biết sức mạnh thực sự của dân tộc Frost Demons"
            }, //text chat 1
            new String[]{"|-1|Tụi mày có giỏi thì xông vào cứu hắn đi",
                "|-1|Trận địa pháo mini",
                "|-1|Nên giải quyết con sâu nào trước đây",
                "|-1|HA HA HA",
                "|-1|Ta chính là Vũ Trụ Đệ Nhất Cao Thủ",
                "|-1|Xí hụt ha ha",
                "|-1|Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha",
                "|-1|Chúng mày nghĩ kiến lại thắng nổi khủng long sao",
                "|-1|Hô hô hô",
                "|-1|Được thôi, nếu muốn chết đến vậy, ta rất vui lòng!!",}, //text chat 2
            new String[]{"|-1|Mọi chuyện chưa kết thúc đâu",
                "|-1|Mọi chuyện chưa kết thúc đâu"},
            AppearType.ANOTHER_LEVEL //type appear
    );
    //======================Tháp karin=======================
    public static final BossData TAUPAYPAY = new BossData(
            "Tàu Pảy Pảy", //name
            ConstPlayer.XAYDA, //gender
            new short[]{92, 93, 94, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new int[]{1000}, //hp
            new int[]{46}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{"|-1|Ta sẽ dạy ngươi vài chiêu",
                "|-1|Ta sẽ đánh hết sức, ngươi cẩn thận nhé"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|OK ta chịu thua"}, //text chat 3
            REST_1_S
    );

    public static final BossData KARIN = new BossData(
            "Karin", //name
            ConstPlayer.XAYDA, //gender
            new short[]{89, 90, 91, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            500, //dame
            new int[]{500}, //hp
            new int[]{46}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{"|-1|Ta sẽ dạy ngươi vài chiêu",
                "|-1|Ta sẽ đánh hết sức, ngươi cẩn thận nhé"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|OK ta chịu thua"}, //text chat 3
            REST_1_S
    );

    public static final BossData YAJIRO = new BossData(
            "Yajirô", //name
            ConstPlayer.XAYDA, //gender
            new short[]{77, 78, 79, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1100, //dame
            new int[]{1100}, //hp
            new int[]{46}, //map join
            new int[][]{
                {Skill.GALICK, 1, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 60000},},
            new String[]{"|-1|Ngon nhào vô",
                "|-1|Cho mi biết sự lợi hại của ta"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|Ngươi thật lợi hại"}, //text chat 3
            REST_1_S
    );

    public static final BossData MRPOPO = new BossData(
            "Mr.PôPô", //name
            ConstPlayer.XAYDA, //gender
            new short[]{77, 78, 79, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1100, //dame
            new int[]{5100}, //hp
            new int[]{46}, //map join
            new int[][]{{Skill.GALICK, 1, 30000},
            {Skill.KAMEJOKO, 1, 30000},
            {Skill.THAI_DUONG_HA_SAN, 3, 30000},},
            new String[]{"|-1|Đánh trúng ta 1 cái coi như ngươi thắng",
                "|-1|Đánh trúng ta 3 cái coi như ngươi thắng"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây",
                "|-1|Phù.."}, //text chat 2
            new String[]{"|-1|Thua thì thua"}, //text chat 3
            REST_1_S
    );

    public static final BossData THUONG_DE = new BossData(
            "Thượng đế", //name
            ConstPlayer.XAYDA, //gender
            new short[]{86, 87, 88, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{1000}, //hp
            new int[]{49}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},},
            new String[]{"|-1|Ta sẽ dạy võ cho con trong phòng tập thời gian này",
                "|-1|Con hãy đánh hết sức nhé, ta sẽ không nương tay đâu"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|Ta rất tự hào về con"}, //text chat 3
            REST_1_S
    );

    public static final BossData KHI_BUBBLES = new BossData(
            "Khỉ Bubbles", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{95, 96, 97, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            30000, //dame
            new int[]{30000}, //hp
            new int[]{48}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.BIEN_KHI, 7, 1800000}}, //skill
            new String[]{"|-1|Ù ù khẹt khẹt",
                "|-1|Ù ù khẹt khẹt"}, //text chat 1
            new String[]{"|-1|Ù ù khẹt khẹt",
                "|-1|khẹt khẹt",
                "|-1|ù ù khẹc khẹc",
                "|-1|khẹc khẹc",
                "|-1|éc éc",}, //text chat 2
            new String[]{"|-1|Éc Éc Éc Éc!"}, //text chat 3
            REST_1_S //second rest
    );

    public static final BossData THAN_VU_TRU = new BossData(
            "Thần Vũ Trụ", //name
            ConstPlayer.XAYDA, //gender
            new short[]{98, 99, 100, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            45000, //dame
            new int[]{45000}, //hp
            new int[]{48}, //map join
            new int[][]{
                {Skill.GALICK, 7, 1000},
                {Skill.THAI_DUONG_HA_SAN, 7, 30000},},
            new String[]{"|-1|Ta sẽ dạy ngươi chiêu kaio-ken",
                "|-1|Ngươi cũng to gan lắm"}, //text chat 1
            new String[]{"|-1|Haizzzzz",
                "|-1|Hahaha",
                "|-1|Xem đây"}, //text chat 2
            new String[]{"|-1|Tại hôm nay ta...ta hơi bị đau bụng"}, //text chat 3
            REST_1_S
    );

    public static final BossData TO_SU_KAIO = new BossData(
            "Tổ sư Kaio", //name
            ConstPlayer.XAYDA, //gender
            new short[]{448, 449, 450, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            45000, //dame
            new int[]{45000}, //hp
            new int[]{50}, //map join
            new int[][]{
                {Skill.GALICK, 1, 60000},},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_S
    );

    public static final BossData WHIS = new BossData(
            "Whis", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{838, 839, 840, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{550000}, //hp
            new int[]{154}, //map join
            new int[][]{
                {Skill.DRAGON, 7, 500}},//skill
            new String[]{"|-1|Ta sẽ dạy ngươi vài chiêu"}, //text chat 1
            new String[]{"|-1|Xem đây",
                "|-1|Haizzzzz",
                "|-1|Hahaha", "|-1|AAAAAAAAAA"}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M
    );
     //======================Boss Baby=======================
    public static final BossData BABY = new BossData(
            "Baby",
            ConstPlayer.XAYDA,
            new short[]{1715, 1716, 1717, -1, -1, -1},
            200000,
            new int[]{2000000000},
            new int[]{14},
            new int[][]{
                {Skill.GALICK, 7, 100},
                {Skill.DRAGON, 7, 100},
                {Skill.DEMON, 7, 100},
                {Skill.MASENKO, 7, 100},
                {Skill.ANTOMIC, 7, 100},
                {Skill.KAMEJOKO, 7, 100},
                {Skill.LIEN_HOAN, 7, 10000},
                {Skill.SUPER_KAME, 7, 10000}},//skill
            new String[]{"|-2|Cái gì kia vậy!? Đó là loài gì thế!!?",
                "|-1|Hôm nay sẽ là ngày đáng nhớ đây!",
                "|-1|Ta sẽ kí sinh vào người của vegeta để đạt được dạng hoàn hảo!",
                "|-1|Sao vậy, goku? Nếu ngươi muốn ngăn ta lại thì xong lên đi chứ!?"
            }, //text chat 1
            new String[]{"|-2|Hắn làm ta bất ngờ đấy! Khốn kiếp!",
                "|-2|Tên đó muốn kí sinh vào vegeta sao?",
                "|-1|Đến đây nào! Khi kết hợp với ta, ngươi sẽ trở nên bất bại, trở thành một thể sống hoàn mỹ!",
                "|-2|Mơ đi, sao ta phải để ngươi kí sinh hả!?",
                "|-2|Dù muốn hay không, ngươi cũng sẽ bị ta kí sinh thôi..",
                "|-2|Chúng ta không thể để hắn đạt đến dạng hoàn hảo được!",
                "|-2|Mục tiêu của hắn không phải là Sôngôku.., mà là biến cả vũ trụ này thành nô lệ của hắn!",
                "|-2|Vậy thì để ngăn cản ngươi đạt đến dạng hoàn hảo, ta phải tiêu diệt ngươi!",
                "|-2|Hắn quá mạnh! Mình có thể làm được gì đây!?",
                "|-1|Có vẻ như ta đã trở nên quá mạnh, chắc là ta đã giết nhiều người hơn dự tính!!",
                "|-1|Ngươi không thể thắng nổi ta! Từ bỏ đi!!",
                "|-1|Đến lúc ta hấp thu ngươi rồi",
                "|-2|Đồ quái vật chết tiệt...",
                "|-1|Hê hê hê, rồi ngươi sẽ trở thành một phần của con quái vật này thôi!",
                "|-1|Lại thêm một tên ngốc nữa chán sống!"
            }, //text chat 2
            new String[]{"|-2|Khốn kiếp, vegeta.. hắn bị baby kí sinh rồi!!"}, //text chat 3
            REST_15_M
    );

    public static final BossData BABY_2 = new BossData(
            "Baby",
            ConstPlayer.XAYDA,
            new short[]{1718, 1719, 1720, -1, -1, -1},
            250000,
            new int[]{2000000000},
            new int[]{14},
            new int[][]{
                {Skill.GALICK, 7, 100},
                {Skill.DRAGON, 7, 100},
                {Skill.DEMON, 7, 100},
                {Skill.MASENKO, 7, 100},
                {Skill.ANTOMIC, 7, 100},
                {Skill.KAMEJOKO, 7, 100},
                {Skill.LIEN_HOAN, 7, 10000},
                {Skill.SUPER_KAME, 7, 20000}},//skill
            new String[]{}, //text chat 1
            new String[]{"|-2|Nguy rồi... thực sự nguy to rồi!",
                "|-1|Các ngươi nghĩ có thể chạy được sao!?",
                "|-1|Muốn chạy khỏi ta thì đừng hòng!!",
                "|-1|Ta cũng ngạc nhiên với tốc độ của mình! Đó tất nhiên là do ta kí sinh được vegeta!",
                "|-2|Hắn nhanh quá!!",
                "|-1|Ta muốn thử xem sức mạnh này đến đâu...",
                "|-1|Hmm.. có vẻ như sức mạnh này đã tăng lên gấp bội!",
                "|-1|Đã đến lúc ta đạt đến trạng thái hoàn hảo.!",
                "|-1|Có vẻ như ngươi muốn bị ép hơn là tự nguyện!!",
                "|-2|Bây giờ ta chưa thể thắng được ngươi!! Nhưng ngươi đừng hòng huyênh hoang!!!",
                "|-1|Muốn chạy à!!? Ta sẽ không để ngươi thoát đâu!!",}, //text chat 2
            new String[]{"|-1|Đến lúc rồi!"}, //text chat 3
            AppearType.ANOTHER_LEVEL
    );

    public static final BossData BABY_3 = new BossData(
            "Baby",
            ConstPlayer.XAYDA,
            new short[]{1721, 1722, 1723, -1, -1, -1},
            30000,
            new int[]{2000000000},
            new int[]{14},
            new int[][]{
                {Skill.GALICK, 7, 100},
                {Skill.DRAGON, 7, 100},
                {Skill.DEMON, 7, 100},
                {Skill.MASENKO, 7, 100},
                {Skill.ANTOMIC, 7, 100},
                {Skill.KAMEJOKO, 7, 100},
                {Skill.LIEN_HOAN, 7, 10000},
                {Skill.SUPER_KAME, 7, 20000}},//skill
            new String[]{"|-2|Cuối cùng hắn cũng đã biến đổi",
                "|-2|Khốn kiếp! Phải kết liễu hắn ngay lúc này!"
            }, //text chat 1
            new String[]{"|-2|Baby đã đạt đến dạng hoàn hảo rồi!",
                "|-2|Đồ khốn, sao ngươi dám làm vậy với vegeta!!",
                "|-2|Không ấn tượng lắm với dạng hoàn hảo của ngươi..",
                "|-2|Sao hắn không hề hấn gì?",
                "|-1|Xin lỗi.. Ngươi có thể giúp ta làm nóng cơ thể lên không !?",
                "|-2|Tình hình nguy cấp rồi!",
                "|-2|Khốn kiếp! Ngươi không chú tâm vào trận đấu!",
                "|-1|Thì ta đã bảo đây chỉ là làm nóng cơ thể mà!!",
                "|-1|Giờ ngươi chỉ là rác rưởi mà thôi!",
                "|-2|Không thể nào! Ngươi dù sao cũng chỉ là đồ sâu bọ!",}, //text chat 2
            new String[]{"|-1|Oái.. không...",
                "|-1|Cơ thể hoàn hảo của ta!!",
                "|-1|Ta không tin chuyện này sẽ xảy ra!!",
                "|-1|Đồ khốn kiếp!! Rồi ngươi sẽ phải trả giá"
            }, //text chat 3
            AppearType.ANOTHER_LEVEL
    );
    //======================Boss mini=======================
    public static final BossData GOLDEN_FRIEZA = new BossData(
            "Fide Vàng", //name
            ConstPlayer.XAYDA, //gender
            new short[]{502, 503, 504, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            100000, //dame
            new int[]{1000000000}, //hp
            new int[]{6}, //map join
            new int[][]{
                {Skill.TAI_TAO_NANG_LUONG, 1, 120000}, {Skill.GALICK, 7, 1000},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1000}, {Skill.KAMEJOKO, 3, 1000}, {Skill.KAMEJOKO, 4, 1000}, {Skill.KAMEJOKO, 5, 1000}, {Skill.KAMEJOKO, 6, 1000}, {Skill.KAMEJOKO, 7, 1000},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1000}, {Skill.MASENKO, 3, 1000}, {Skill.MASENKO, 4, 1000}, {Skill.MASENKO, 5, 1000}, {Skill.MASENKO, 6, 1000}, {Skill.MASENKO, 7, 1000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1000}, {Skill.ANTOMIC, 3, 1000}, {Skill.ANTOMIC, 4, 1000}, {Skill.ANTOMIC, 5, 1000}, {Skill.ANTOMIC, 6, 1000}, {Skill.ANTOMIC, 7, 1000},}, //skill
            new String[]{}, //text chat 1
            new String[]{"|-1|He he he",
                "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                "|-1|Xem các ngươi mạnh đến đâu"}, //text chat 2
            new String[]{}, //text chat 3
            REST_5_M,
            new int[]{BossID.DEATH_BEAM_1, BossID.DEATH_BEAM_2, BossID.DEATH_BEAM_3, BossID.DEATH_BEAM_4, BossID.DEATH_BEAM_5}
    );

    public static final BossData DEATH_BEAM = new BossData(
            "$", //name
            ConstPlayer.XAYDA, //gender
            new short[]{609, 610, 611, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            1000, //dame
            new int[]{500}, //hp
            new int[]{6}, //map join
            new int[][]{},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.CALL_BY_ANOTHER
    );
    
    public static final BossData AN_TROM = new BossData(
            "Ăn trộm",
            ConstPlayer.TRAI_DAT,
            new short[]{201, 202, 203, -1, -1, -1},
            1,
            new int[]{100}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 92, 93, 94, 96, 97, 98, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110}, //map join
            new int[][]{
                {Skill.THAI_DUONG_HA_SAN, 5, 10000}},
            new String[]{}, //text chat 1
            new String[]{}, //text chat 2
            new String[]{}, //text chat 3
            REST_1_M //type appear
    );

    public static final BossData RONG_NHI = new BossData(
            "Rồng Nhí",
            ConstPlayer.TRAI_DAT,
            new short[]{1662, 1663, 1664, -1, -1, -1},
            1,
            new int[]{50000000}, //hp
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37}, //map join
            new int[][]{
                {Skill.GALICK, 5, 1000000000}},
            new String[]{"|-1|Tới giờ làm việc"}, //text chat 1
            new String[]{"|-1|Ái chà chà"}, //text chat 2
            new String[]{"|-1|Ái chà chà"},
            REST_1_M //type appear
    );

    public static final BossData CADIC_M = new BossData(
            "Cadic M", //name
            ConstPlayer.TRAI_DAT, //gender
            new short[]{1639, 1632, 1633, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
            10000, //dame
            new int[]{6000000}, //hp
            new int[]{165}, //map join
            new int[][]{
                {Skill.DRAGON, 1, 1000},
                {Skill.DEMON, 1, 1000},
                {Skill.GALICK, 1, 1000},
                {Skill.MASENKO, 1, 500},
                {Skill.ANTOMIC, 1, 500},
                {Skill.KAMEJOKO, 1, 500},
                {Skill.LIEN_HOAN, 1, 1000},
                {Skill.DICH_CHUYEN_TUC_THOI, 7, 30000},},//skill
            new String[]{}, //text chat 1
            new String[]{"|-1|Khí công pháo"}, //text chat 2
            new String[]{}, //text chat 3
            AppearType.APPEAR_WITH_ANOTHER //type appear
    );
}