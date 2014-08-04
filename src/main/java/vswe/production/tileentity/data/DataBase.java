package vswe.production.tileentity.data;


import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.tileentity.TileEntityTable;

public abstract class DataBase {
    public abstract void save(TileEntityTable table, DataWriter dw, int id);
    public abstract void load(TileEntityTable table, DataReader dr, int id);
    public boolean shouldBounce(TileEntityTable table) {
        return true;
    }
    public boolean shouldBounceToAll(TileEntityTable table) {
        return false;
    }
}
