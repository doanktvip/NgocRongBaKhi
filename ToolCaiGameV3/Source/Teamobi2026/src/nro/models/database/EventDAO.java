package nro.models.database;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nro.models.data.LocalManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import lombok.Getter;
import lombok.Setter;
import nro.models.utils.Logger;

public class EventDAO {

    @Setter
    @Getter
    private static long remainingTimeToIncreasePotentialAndPower = 0;
    @Setter
    @Getter
    private static long remainingTimeToIncreaseHP = 0;
    @Setter
    @Getter
    private static long remainingTimeToIncreaseMP = 0;
    @Setter
    @Getter
    private static long remainingTimeToIncreaseDame = 0;

    public static void loadInternationalWomensDayEvent() {
        try (Connection con = LocalManager.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `data` FROM `event` WHERE `name` = 'international_womens_day'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(rs.getString("data"), JsonObject.class);
                if (jsonObject != null) {
                    if (jsonObject.has("damePrecent")) {
                        remainingTimeToIncreaseDame = jsonObject.getAsJsonPrimitive("damePrecent").getAsLong();
                    }
                    if (jsonObject.has("hpPrecent")) {
                        remainingTimeToIncreaseHP = jsonObject.getAsJsonPrimitive("hpPrecent").getAsLong();
                    }
                    if (jsonObject.has("mpPrecent")) {
                        remainingTimeToIncreaseMP = jsonObject.getAsJsonPrimitive("mpPrecent").getAsLong();
                    }
                    if (jsonObject.has("papPrecent")) {
                        remainingTimeToIncreasePotentialAndPower = jsonObject.getAsJsonPrimitive("papPrecent").getAsLong();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.logException(EventDAO.class, ex);
        }
    }

    public static void save() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("damePrecent", remainingTimeToIncreaseDame);
            jsonObject.addProperty("hpPrecent", remainingTimeToIncreaseHP);
            jsonObject.addProperty("mpPrecent", remainingTimeToIncreaseMP);
            jsonObject.addProperty("papPrecent", remainingTimeToIncreasePotentialAndPower);

            String jsonData = jsonObject.toString();

            LocalManager.executeUpdate("UPDATE `event` SET `data` = ? WHERE `name` = 'international_womens_day'", jsonData);
        } catch (Exception e) {
            Logger.logException(EventDAO.class, e, "Lỗi save Event Data\n");
        }

    }

}
