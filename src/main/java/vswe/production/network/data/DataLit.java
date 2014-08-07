package vswe.production.network.data;

import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.tileentity.TileEntityTable;


public class DataLit extends DataBase {
    @Override
    public void save(TileEntityTable table, DataWriter dw, int id) {
        dw.writeBoolean(table.isLit());
    }

    @Override
    public void load(TileEntityTable table, DataReader dr, int id) {
        table.setLit(dr.readBoolean());
    }
}
