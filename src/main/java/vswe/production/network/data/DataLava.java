package vswe.production.network.data;

import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.IBitCount;
import vswe.production.network.MaxCount;
import vswe.production.tileentity.TileEntityTable;


public class DataLava extends DataBase {
    private static IBitCount LAVA_BIT_COUNT = new MaxCount(TileEntityTable.MAX_LAVA);

    @Override
    public void save(TileEntityTable table, DataWriter dw, int id) {
        dw.writeData(table.getLava(), LAVA_BIT_COUNT);
    }

    @Override
    public void load(TileEntityTable table, DataReader dr, int id) {
        table.setLava(dr.readData(LAVA_BIT_COUNT));
    }
}
