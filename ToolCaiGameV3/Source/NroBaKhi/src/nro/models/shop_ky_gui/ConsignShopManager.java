package nro.models.shop_ky_gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import nro.models.data.LocalManager;
import nro.models.utils.Logger;
import org.json.simple.JSONValue;

/**
 *
 * @author By Mr Blue
 * 
 */

public class ConsignShopManager {

    private static ConsignShopManager instance;

    public static ConsignShopManager gI() {
        if (instance == null) {
            instance = new ConsignShopManager();
        }
        return instance;
    }

    public long lastTimeUpdate;

    public String[] tabName = {"Áo Quần", "Găng Tay", "Phụ Kiện", "Linh tinh", ""};

    public List<ConsignItem> listItem = new CopyOnWriteArrayList<>();

    public void save() {
        try (Connection con = LocalManager.getConnection()) {
            boolean autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try (Statement s = con.createStatement();
                 PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO `shop_ky_gui`(`id`, `player_id`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `itemOption`, `isUpTop`, `isBuy`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                s.executeUpdate("DELETE FROM shop_ky_gui");
                for (ConsignItem it : this.listItem) {
                    if (it != null) {
                        ps.setInt(1, it.id);
                        ps.setInt(2, it.player_sell);
                        ps.setByte(3, it.tab);
                        ps.setShort(4, it.itemId);
                        ps.setInt(5, it.goldSell);
                        ps.setInt(6, it.gemSell);
                        ps.setInt(7, it.quantity);
                        String opts = (it.options == null) ? "[]" : JSONValue.toJSONString(it.options);
                        if (opts == null || opts.equals("null")) {
                            opts = "[]";
                        }
                        ps.setString(8, opts);
                        ps.setInt(9, it.isUpTop);
                        ps.setByte(10, (byte) (it.isBuy ? 1 : 0));
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                con.commit();
            } catch (Exception ex) {
                con.rollback();
                Logger.logException(ConsignShopManager.class, ex, "Lỗi lưu chợ ký gửi, đã rollback");
            } finally {
                con.setAutoCommit(autoCommit);
            }
        } catch (Exception e) {
            Logger.logException(ConsignShopManager.class, e);
        }
    }
}

