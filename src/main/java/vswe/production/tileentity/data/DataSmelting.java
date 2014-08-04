package vswe.production.tileentity.data;

import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.tileentity.TileEntityTable;


public class DataSmelting extends DataBase {

    @Override
    public void save(TileEntityTable table, DataWriter dw, int id) {
        dw.writeByte(table.getMainPage().getSmeltingList().get(id).getSmeltingProgress());
    }

    @Override
    public void load(TileEntityTable table, DataReader dr, int id) {
        table.getMainPage().getSmeltingList().get(id).setSmeltingProgress(dr.readByte());
    }

}
