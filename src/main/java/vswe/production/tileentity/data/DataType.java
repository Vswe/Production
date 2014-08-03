package vswe.production.tileentity.data;


import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.tileentity.TileEntityTable;

public enum DataType {
    PAGE(DataPage.class);

    private DataBase data;

    DataType(Class<? extends DataBase> clazz) {
        try {
            data = clazz.newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(TileEntityTable table, DataWriter dw) {
        if (data != null) {
            data.save(table, dw);
        }
    }

    public void load(TileEntityTable table, DataReader dr) {
        if (data != null) {
            data.load(table, dr);
        }
    }

    public boolean shouldBounce(TileEntityTable table) {
        return data != null && data.shouldBounce(table);
    }

    public boolean shouldBounceToAll(TileEntityTable table) {
        return data != null && data.shouldBounceToAll(table);
    }
}
