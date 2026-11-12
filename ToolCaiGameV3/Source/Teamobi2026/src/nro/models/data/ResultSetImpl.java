package nro.models.data;

import java.sql.Timestamp;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.sql.ResultSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import nro.models.data.LocalResultSet;

public class ResultSetImpl implements LocalResultSet {

    private Map<String, Object>[] data;
    private Object[][] values;
    private int indexData;

    @SuppressWarnings("unchecked")
    public ResultSetImpl(final ResultSet rs) throws Exception {
        this.indexData = -1;
        try {
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int nColumn = rsmd.getColumnCount();
            List<Map<String, Object>> dataList = new ArrayList<>();
            List<Object[]> valuesList = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> rowMap = new HashMap<>(nColumn * 2);
                Object[] rowValues = new Object[nColumn];
                for (int j = 1; j <= nColumn; ++j) {
                    final String tableName = rsmd.getTableName(j);
                    final String columnName = rsmd.getColumnName(j);
                    final Object columnValue = rs.getObject(j);
                    if (columnName != null) {
                        rowMap.put(columnName.toLowerCase(), columnValue);
                        if (tableName != null && !tableName.isEmpty()) {
                            rowMap.put(tableName.toLowerCase() + "." + columnName.toLowerCase(), columnValue);
                        }
                    }
                    rowValues[j - 1] = columnValue;
                }
                dataList.add(rowMap);
                valuesList.add(rowValues);
            }

            this.data = dataList.toArray(new Map[0]);
            this.values = valuesList.toArray(new Object[0][]);
        } catch (final Exception e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    if (rs.getStatement() != null) {
                        rs.getStatement().close();
                    }
                    rs.close();
                } catch (final Exception ex) {
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (Map map : this.data) {
            map.clear();
            map = null;
        }
        this.data = null;
        for (final Object[] array : this.values) {
            Object[] obj = array;
            for (Object o : array) {
                o = null;
            }
            obj = null;
        }
        this.values = null;
    }

    @Override
    public boolean next() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        ++this.indexData;
        return this.indexData < this.data.length;
    }

    @Override
    public boolean first() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.data.length > 0) {
            this.indexData = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean gotoResult(final int index) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData < 0 || this.indexData >= this.data.length) {
            throw new Exception("Index out of bound");
        }
        this.indexData = index;
        return true;
    }

    @Override
    public boolean gotoFirst() throws Exception {
        if (this.data == null || this.data.length == 0) {
            throw new Exception("No data available");
        }
        this.indexData = 0;
        return true;
    }

    @Override
    public void gotoBeforeFirst() {
        this.indexData = -1;
    }

    @Override
    public boolean gotoLast() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        this.indexData = this.data.length - 1;
        return true;
    }

    @Override
    public int getRows() throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        return this.data.length;
    }

    private byte toByte(Object val) {
        if (val == null) {
            return 0;
        }
        if (val instanceof Number num) {
            return num.byteValue();
        }
        return Byte.parseByte(String.valueOf(val).trim());
    }

    private short toShort(Object val) {
        if (val == null) {
            return 0;
        }
        if (val instanceof Number num) {
            return num.shortValue();
        }
        return Short.parseShort(String.valueOf(val).trim());
    }

    private int toInt(Object val) {
        if (val == null) {
            return 0;
        }
        if (val instanceof Number num) {
            return num.intValue();
        }
        return Integer.parseInt(String.valueOf(val).trim());
    }

    private long toLong(Object val) {
        if (val == null) {
            return 0L;
        }
        if (val instanceof Number num) {
            return num.longValue();
        }
        return Long.parseLong(String.valueOf(val).trim());
    }

    private float toFloat(Object val) {
        if (val == null) {
            return 0.0f;
        }
        if (val instanceof Number num) {
            return num.floatValue();
        }
        return Float.parseFloat(String.valueOf(val).trim());
    }

    private double toDouble(Object val) {
        if (val == null) {
            return 0.0;
        }
        if (val instanceof Number num) {
            return num.doubleValue();
        }
        return Double.parseDouble(String.valueOf(val).trim());
    }

    private boolean toBoolean(Object val) {
        if (val == null) {
            return false;
        }
        if (val instanceof Boolean b) {
            return b;
        }
        if (val instanceof Number num) {
            return num.intValue() == 1;
        }
        String s = String.valueOf(val).trim();
        return "1".equals(s) || "true".equalsIgnoreCase(s);
    }

    private Timestamp toTimestamp(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Timestamp ts) {
            return ts;
        }
        if (val instanceof java.util.Date d) {
            return new Timestamp(d.getTime());
        }
        return Timestamp.valueOf(String.valueOf(val).trim());
    }

    @Override
    public byte getByte(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toByte(this.values[this.indexData][column - 1]);
    }

    @Override
    public byte getByte(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toByte(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public int getInt(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toInt(this.values[this.indexData][column - 1]);
    }

    @Override
    public int getInt(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toInt(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public float getFloat(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toFloat(this.values[this.indexData][column - 1]);
    }

    @Override
    public float getFloat(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toFloat(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public double getDouble(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toDouble(this.values[this.indexData][column - 1]);
    }

    @Override
    public double getDouble(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toDouble(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public long getLong(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toLong(this.values[this.indexData][column - 1]);
    }

    @Override
    public long getLong(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toLong(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public String getString(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return String.valueOf(this.values[this.indexData][column - 1]);
    }

    @Override
    public String getString(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return String.valueOf(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public Object getObject(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return this.values[this.indexData][column - 1];
    }

    @Override
    public Object getObject(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return this.data[this.indexData].get(column.toLowerCase());
    }

    @Override
    public boolean getBoolean(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toBoolean(this.values[this.indexData][column - 1]);
    }

    @Override
    public boolean getBoolean(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toBoolean(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public Timestamp getTimestamp(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toTimestamp(this.values[this.indexData][column - 1]);
    }

    @Override
    public Timestamp getTimestamp(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toTimestamp(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public short getShort(final int column) throws Exception {
        if (this.values == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toShort(this.values[this.indexData][column - 1]);
    }

    @Override
    public short getShort(final String column) throws Exception {
        if (this.data == null) {
            throw new Exception("No data available");
        }
        if (this.indexData == -1) {
            throw new Exception("Results need to be prepared in advance");
        }
        return toShort(this.data[this.indexData].get(column.toLowerCase()));
    }

    @Override
    public void close() {
        dispose();
    }

}
