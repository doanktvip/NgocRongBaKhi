package nro.models.player;

import java.util.ArrayList;
import java.util.List;
import nro.models.item.Item;
import nro.models.item.Item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.services.InventoryService;
import nro.models.map.service.ItemMapService;
import nro.models.services.ItemService;
import nro.models.services.RewardService;
import nro.models.services.Service;
import nro.models.utils.Util;

/**
 * Hệ thống rơi vật phẩm chuẩn (Next-Gen Fluent Drop System).
 * Cung cấp mô hình Fluent Builder tự do, mạnh mẽ và an toàn tuyệt đối.
 * 
 * @author By Mr Blue & Antigravity
 */
public class DropItem {

    // =========================================================================
    // 1. DÀNH CHO PLAYER (TƯƠNG THÍCH MAP 52 DƯA HẤU)
    // =========================================================================
    private Player player;

    public DropItem(Player player) {
        this.player = player;
    }

    public void update() {
        Zone zone = player.zone;
        if (player.isPl() && zone != null && zone.map.mapId == 52
                && InventoryService.gI().getCountEmptyBag(player) > 0
                && !InventoryService.gI().isExistItemBag(player, 726)
                && !ItemMapService.gI().findItemMapByPlayer(player, 726)) {
            int x = Util.nextInt(100, zone.map.mapWidth - 100);
            int y = zone.map.yPhysicInTop(x, 100);
            ItemMap it = new ItemMap(zone, 726, 1, x, y, player.id);
            it.options.add(new Item.ItemOption(30, 0));
            it.options.add(new Item.ItemOption(93, 1));
            Service.gI().dropItemMapForMe(player, it);
        }
    }

    public void dispose() {
        player = null;
    }

    // =========================================================================
    // 2. KHỞI TẠO FLUENT DROP BUILDER (CỔNG VÀO CHÍNH)
    // =========================================================================

    /**
     * Khởi tạo rơi đồ cho một người chơi cụ thể (được ưu tiên nhặt trong 30s đầu).
     */
    public static DropBuilder to(Player player, Zone zone, int x, int y) {
        return new DropBuilder(player, zone, x, y);
    }

    public static DropBuilder to(Player player, Zone zone, Location location) {
        return new DropBuilder(player, zone, location != null ? location.x : 0, location != null ? location.y : 0);
    }

    /**
     * Khởi tạo rơi đồ tự do cho toàn bộ map tranh nhau nhặt (Free-for-all, playerId = -1).
     */
    public static DropBuilder free(Zone zone, int x, int y) {
        return new DropBuilder(null, zone, x, y);
    }

    public static DropBuilder free(Zone zone, Location location) {
        return new DropBuilder(null, zone, location != null ? location.x : 0, location != null ? location.y : 0);
    }

    // =========================================================================
    // 3. TOÁN HỌC VẬT LÝ VÀ CHỐNG GLITCH BẢN ĐỒ
    // =========================================================================

    public static Player getBeneficiary(Player player) {
        if (player == null) {
            return null;
        }
        if (player.isPet && player instanceof Pet) {
            return ((Pet) player).master;
        }
        return player;
    }

    public static boolean hasCharmThuHut(Player player) {
        return player != null && player.charms != null && player.charms.tdThuHut > System.currentTimeMillis();
    }

    public static int clampX(Zone zone, int x) {
        if (zone == null || zone.map == null) {
            return x;
        }
        int mapWidth = zone.map.mapWidth;
        if (x < 30) {
            return 30 + Util.nextInt(5, 15);
        } else if (x > mapWidth - 30) {
            return mapWidth - 30 - Util.nextInt(5, 15);
        }
        return x;
    }

    public static int getGroundY(Zone zone, int x, int originY) {
        if (zone == null || zone.map == null) {
            return originY;
        }
        int rayStartY = Math.max(24, originY - 60);
        return zone.map.yPhysicInTop(x, rayStartY);
    }

    // =========================================================================
    // 4. FLUENT DROP BUILDER (LÕI KIẾN TẠO LINH HOẠT TỰ DO)
    // =========================================================================
    public static class DropBuilder {
        private Player player;
        private Zone zone;
        private int x;
        private int y;

        private int rateNumerator = 1;
        private int rateDenominator = 1; // Mặc định 1/1 = 100%

        private boolean allowAutoPick = true;
        private int spreadDistance = 25;
        private int fixedStar = 0;
        private int randomStarRate = 0;
        private final List<ItemOption> extraOptions = new ArrayList<>();
        private final List<Item> itemsToDrop = new ArrayList<>();

        public DropBuilder(Player player, Zone zone, int x, int y) {
            this.player = player;
            this.zone = zone;
            this.x = x;
            this.y = y;
        }

        // --- CẤU HÌNH VẬT PHẨM ---

        /**
         * Rơi 1 đối tượng Item hoàn chỉnh (có sẵn option/bonus).
         */
        public DropBuilder item(Item item) {
            if (item != null && item.template != null) {
                this.itemsToDrop.add(item);
            }
            return this;
        }

        /**
         * Rơi 1 vật phẩm theo ID (tự động nạp option Shop nếu là trang bị).
         */
        public DropBuilder item(int tempId) {
            return item(tempId, 1);
        }

        public DropBuilder item(int tempId, int quantity) {
            Item it = ItemService.gI().createNewItem((short) tempId, quantity);
            if (it != null && it.template != null) {
                // Tự nạp option Shop nếu là trang bị chưa có chỉ số
                List<ItemOption> shopOps = ItemService.gI().getListOptionItemShop((short) tempId);
                if (shopOps != null && !shopOps.isEmpty()) {
                    for (ItemOption op : shopOps) {
                        if (op != null && op.optionTemplate != null) {
                            it.itemOptions.add(new ItemOption(op.optionTemplate.id, op.param));
                        }
                    }
                }
                this.itemsToDrop.add(it);
            }
            return this;
        }

        /**
         * Rơi trang bị theo Cấp độ chính xác (1 - 13) từ RewardService.
         */
        public DropBuilder equipment(int level) {
            Item it = RewardService.gI().randItemDoCap(level);
            if (it != null) {
                this.itemsToDrop.add(it);
            }
            return this;
        }

        /**
         * Rơi trang bị trong dải Cấp độ (ví dụ minLevel=6, maxLevel=8).
         */
        public DropBuilder equipment(int minLevel, int maxLevel) {
            Item it = RewardService.gI().randItemDoCap(minLevel, maxLevel);
            if (it != null) {
                this.itemsToDrop.add(it);
            }
            return this;
        }

        /**
         * Bốc ngẫu nhiên duy nhất 1 món từ danh sách các ID đưa vào.
         */
        public DropBuilder random(int... itemIds) {
            if (itemIds != null && itemIds.length > 0) {
                int pickedId = itemIds[Util.nextInt(0, itemIds.length - 1)];
                return item(pickedId, 1);
            }
            return this;
        }

        /**
         * Bốc ngẫu nhiên N món từ danh sách các ID.
         */
        public DropBuilder randomCount(int count, int... itemIds) {
            if (itemIds != null && itemIds.length > 0 && count > 0) {
                for (int i = 0; i < count; i++) {
                    int pickedId = itemIds[Util.nextInt(0, itemIds.length - 1)];
                    item(pickedId, 1);
                }
            }
            return this;
        }

        /**
         * Rơi nhiều món cùng lúc (tự động bung tỏa hình cánh sen).
         */
        public DropBuilder burst(List<Item> items) {
            if (items != null) {
                for (Item it : items) {
                    if (it != null && it.template != null) {
                        this.itemsToDrop.add(it);
                    }
                }
            }
            return this;
        }

        /**
         * Bốc 1 món theo bảng trọng số {{id, weight}, ...} hoặc {{id, weight, qty}, ...}.
         */
        public DropBuilder weighted(int[][] itemsWithRate) {
            if (itemsWithRate != null && itemsWithRate.length > 0) {
                int totalWeight = 0;
                for (int[] entry : itemsWithRate) {
                    if (entry != null && entry.length > 1) {
                        totalWeight += entry[1];
                    }
                }
                if (totalWeight > 0) {
                    int roll = Util.nextInt(1, totalWeight);
                    int cur = 0;
                    for (int[] entry : itemsWithRate) {
                        if (entry != null && entry.length > 1) {
                            cur += entry[1];
                            if (roll <= cur) {
                                int qty = entry.length > 2 ? entry[2] : 1;
                                return item(entry[0], qty);
                            }
                        }
                    }
                }
            }
            return this;
        }

        /**
         * Bốc từ DropPool hiện đại.
         */
        public DropBuilder pool(DropPool pool) {
            if (pool != null) {
                Item it = pool.rollItem();
                if (it != null) {
                    this.itemsToDrop.add(it);
                }
            }
            return this;
        }

        // --- CẤU HÌNH TỈ LỆ RƠI ---

        /**
         * Tỉ lệ phần trăm (1 - 100%).
         */
        public DropBuilder rate(int percent) {
            this.rateNumerator = percent;
            this.rateDenominator = 100;
            return this;
        }

        /**
         * Tỉ lệ phân số cho đồ siêu hiếm (ví dụ 1, 10000 = 1/10.000).
         */
        public DropBuilder rate(int numerator, int denominator) {
            this.rateNumerator = numerator;
            this.rateDenominator = denominator > 0 ? denominator : 1;
            return this;
        }

        // --- CẤU HÌNH CHỈ SỐ & TÍNH NĂNG ---

        /**
         * Gắn thêm 1 Option bất kỳ cho vật phẩm rơi ra.
         */
        public DropBuilder option(int optionId, int param) {
            this.extraOptions.add(new ItemOption(optionId, param));
            return this;
        }

        /**
         * Gắn cố định Sao Pha Lê (Option 107).
         */
        public DropBuilder star(int star) {
            this.fixedStar = star;
            return this;
        }

        /**
         * Gieo tỉ lệ % xuất hiện Sao Pha Lê ngẫu nhiên (1 - 6 sao).
         */
        public DropBuilder randomStar(int percent) {
            this.randomStarRate = percent;
            return this;
        }

        /**
         * Bật/tắt Bùa Thu Hút nhặt thẳng vào túi.
         * Mặc định với Boss: nên set false để đồ bung ra đất.
         */
        public DropBuilder autoPick(boolean allow) {
            this.allowAutoPick = allow;
            return this;
        }

        /**
         * Đổi khoảng cách tỏa khi rơi nhiều món (mặc định 25px).
         */
        public DropBuilder spread(int distance) {
            this.spreadDistance = distance;
            return this;
        }

        /**
         * Chuyển sang chế độ chọn duy nhất 1 tầng phần thưởng loại trừ lẫn nhau (Mutual Exclusive).
         * Hỗ trợ .rate(percent), .rate(num, den) và .otherwise() tự động gom tỉ lệ còn lại.
         */
        public PickOneBuilder pickOne() {
            return new PickOneBuilder(this.player, this.zone, this.x, this.y, this.allowAutoPick);
        }

        // --- THỰC THI (EXECUTE) ---

        /**
         * Thực thi và trả về ItemMap đầu tiên (hoặc null nếu trượt tỉ lệ hoặc lỗi).
         */
        public ItemMap execute() {
            List<ItemMap> list = executeAll();
            return list.isEmpty() ? null : list.get(0);
        }

        /**
         * Alias gọi nhanh của execute().
         */
        public ItemMap drop() {
            return execute();
        }

        /**
         * Thực thi toàn bộ danh sách và trả về toàn bộ ItemMap đã xuất hiện.
         */
        public List<ItemMap> executeAll() {
            List<ItemMap> result = new ArrayList<>();
            if (zone == null || zone.map == null || itemsToDrop.isEmpty()) {
                return result;
            }
            // 1. Kiểm tra tỉ lệ xác suất
            if (!Util.isTrue(rateNumerator, rateDenominator)) {
                return result;
            }

            // 2. Gắn các Option bổ sung (Sao, Option tùy chỉnh)
            for (Item it : itemsToDrop) {
                if (it == null || it.template == null) {
                    continue;
                }
                if (fixedStar > 0) {
                    it.itemOptions.removeIf(op -> op != null && op.optionTemplate != null && op.optionTemplate.id == 107);
                    it.itemOptions.add(new ItemOption(107, fixedStar));
                } else if (randomStarRate > 0 && Util.isTrue(randomStarRate, 100)) {
                    int rand = Util.nextInt(1, 100);
                    int s = (rand <= 80) ? Util.nextInt(1, 3) : (rand <= 97 ? Util.nextInt(4, 5) : 6);
                    it.itemOptions.removeIf(op -> op != null && op.optionTemplate != null && op.optionTemplate.id == 107);
                    it.itemOptions.add(new ItemOption(107, s));
                }
                if (!extraOptions.isEmpty()) {
                    for (ItemOption opt : extraOptions) {
                        if (opt != null && opt.optionTemplate != null) {
                            it.itemOptions.add(new ItemOption(opt.optionTemplate.id, opt.param));
                        }
                    }
                }
            }

            // 3. Tiến hành rơi vật phẩm theo thuật toán Fan-Out
            Player beneficiary = DropItem.getBeneficiary(player);
            long ownerId = (beneficiary != null) ? beneficiary.id : -1;

            for (int i = 0; i < itemsToDrop.size(); i++) {
                Item item = itemsToDrop.get(i);
                if (item == null || item.template == null) {
                    continue;
                }
                int step = (i + 1) / 2;
                int dir = (i % 2 == 1) ? -1 : 1;
                int offset = (i == 0) ? 0 : step * spreadDistance * dir;
                int targetX = DropItem.clampX(zone, x + offset + Util.nextInt(-3, 3));
                int targetY = DropItem.getGroundY(zone, targetX, y);

                ItemMap itemMap = new ItemMap(zone, item.template, item.quantity, targetX, targetY, ownerId);
                if (item.itemOptions != null && !item.itemOptions.isEmpty()) {
                    for (ItemOption opt : item.itemOptions) {
                        if (opt != null && opt.optionTemplate != null) {
                            itemMap.options.add(new ItemOption(opt.optionTemplate.id, opt.param));
                        }
                    }
                }

                // 4. Xử lý Bùa Thu Hút (nếu được phép)
                if (allowAutoPick && beneficiary != null && DropItem.hasCharmThuHut(beneficiary)) {
                    ItemMapService.gI().pickItem(beneficiary, itemMap.itemMapId, true);
                    if (itemMap.isPickedUp) {
                        result.add(itemMap);
                        continue;
                    }
                }

                // 5. Gửi Message 68 cho map
                Service.gI().dropItemMap(zone, itemMap);
                result.add(itemMap);
            }

            return result;
        }
    }

    // =========================================================================
    // 5. PICK-ONE BUILDER (PHÂN TẦNG TỈ LỆ LOẠI TRỪ LẪN NHAU - MUTUAL EXCLUSIVE)
    // =========================================================================

    public static class PickOneBuilder {
        private final Player player;
        private final Zone zone;
        private final int x;
        private final int y;
        private final boolean allowAutoPick;

        private static class Tier {
            int numerator;
            int denominator;
            DropBuilder builder;

            Tier(int num, int den, Player player, Zone zone, int x, int y, boolean autoPick) {
                this.numerator = num;
                this.denominator = den;
                this.builder = new DropBuilder(player, zone, x, y).autoPick(autoPick);
            }
        }

        private final List<Tier> tiers = new ArrayList<>();
        private Tier currentTier = null;
        private Tier otherwiseTier = null;

        public PickOneBuilder(Player player, Zone zone, int x, int y, boolean allowAutoPick) {
            this.player = player;
            this.zone = zone;
            this.x = x;
            this.y = y;
            this.allowAutoPick = allowAutoPick;
        }

        public PickOneBuilder rate(int percent) {
            return rate(percent, 100);
        }

        public PickOneBuilder rate(int numerator, int denominator) {
            currentTier = new Tier(numerator, denominator, player, zone, x, y, allowAutoPick);
            tiers.add(currentTier);
            return this;
        }

        public PickOneBuilder otherwise() {
            currentTier = new Tier(-1, -1, player, zone, x, y, allowAutoPick);
            otherwiseTier = currentTier;
            return this;
        }

        public PickOneBuilder item(int tempId) {
            if (currentTier != null) currentTier.builder.item(tempId);
            return this;
        }

        public PickOneBuilder item(int tempId, int quantity) {
            if (currentTier != null) currentTier.builder.item(tempId, quantity);
            return this;
        }

        public PickOneBuilder item(Item item) {
            if (currentTier != null) currentTier.builder.item(item);
            return this;
        }

        public PickOneBuilder equipment(int level) {
            if (currentTier != null) currentTier.builder.equipment(level);
            return this;
        }

        public PickOneBuilder equipment(int minLevel, int maxLevel) {
            if (currentTier != null) currentTier.builder.equipment(minLevel, maxLevel);
            return this;
        }

        public PickOneBuilder random(int... itemIds) {
            if (currentTier != null) currentTier.builder.random(itemIds);
            return this;
        }

        public PickOneBuilder randomCount(int count, int... itemIds) {
            if (currentTier != null) currentTier.builder.randomCount(count, itemIds);
            return this;
        }

        public PickOneBuilder randomStar(int percent) {
            if (currentTier != null) currentTier.builder.randomStar(percent);
            return this;
        }

        public PickOneBuilder star(int star) {
            if (currentTier != null) currentTier.builder.star(star);
            return this;
        }

        public PickOneBuilder option(int optionId, int param) {
            if (currentTier != null) currentTier.builder.option(optionId, param);
            return this;
        }

        public ItemMap execute() {
            if (tiers.isEmpty() && otherwiseTier == null) {
                return null;
            }
            // Quy đổi sang thang chuẩn 10.000 điểm (hỗ trợ độ chính xác tới 0.01%)
            final long BASE = 10000L;
            long currentPoint = 0;
            List<Long> thresholds = new ArrayList<>();

            for (Tier t : tiers) {
                long points = (t.numerator * BASE) / (t.denominator > 0 ? t.denominator : 100);
                currentPoint += points;
                thresholds.add(currentPoint);
            }

            long roll = Util.nextInt(1, (int) BASE);
            for (int i = 0; i < tiers.size(); i++) {
                if (roll <= thresholds.get(i)) {
                    return tiers.get(i).builder.execute();
                }
            }

            if (otherwiseTier != null) {
                return otherwiseTier.builder.execute();
            }

            return null;
        }
    }

    // =========================================================================
    // 6. DROP POOL ENGINE (DATA-DRIVEN)
    // =========================================================================

    public static class DropEntry {
        public int tempId;
        public int weight;
        public int minQty;
        public int maxQty;
        public List<ItemOption> options;

        public DropEntry(int tempId, int weight) {
            this(tempId, weight, 1, 1);
        }

        public DropEntry(int tempId, int weight, int minQty, int maxQty) {
            this.tempId = tempId;
            this.weight = weight;
            this.minQty = minQty;
            this.maxQty = maxQty;
            this.options = new ArrayList<>();
        }

        public DropEntry addOption(int optionId, int param) {
            this.options.add(new ItemOption(optionId, param));
            return this;
        }

        public Item toItem() {
            int qty = (minQty == maxQty) ? minQty : Util.nextInt(minQty, maxQty);
            Item item = ItemService.gI().createNewItem((short) tempId, qty);
            if (options != null && !options.isEmpty()) {
                for (ItemOption opt : options) {
                    if (opt != null && opt.optionTemplate != null) {
                        item.itemOptions.add(new ItemOption(opt.optionTemplate.id, opt.param));
                    }
                }
            }
            return item;
        }
    }

    public static class DropPool {
        private final List<DropEntry> entries = new ArrayList<>();
        private int totalWeight = 0;

        public DropPool add(int tempId, int weight) {
            return add(tempId, weight, 1, 1);
        }

        public DropPool add(int tempId, int weight, int minQty, int maxQty) {
            if (weight <= 0) {
                return this;
            }
            DropEntry entry = new DropEntry(tempId, weight, minQty, maxQty);
            entries.add(entry);
            totalWeight += weight;
            return this;
        }

        public DropPool add(DropEntry entry) {
            if (entry != null && entry.weight > 0) {
                entries.add(entry);
                totalWeight += entry.weight;
            }
            return this;
        }

        public Item rollItem() {
            if (entries.isEmpty() || totalWeight <= 0) {
                return null;
            }
            int roll = Util.nextInt(1, totalWeight);
            int current = 0;
            for (DropEntry entry : entries) {
                current += entry.weight;
                if (roll <= current) {
                    return entry.toItem();
                }
            }
            return entries.get(entries.size() - 1).toItem();
        }
    }
}
