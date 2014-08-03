package vswe.production.tileentity.data;

import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.IBitCount;
import vswe.production.network.MaxCount;
import vswe.production.tileentity.TileEntityTable;


public class DataFuel extends DataBase {
    private static IBitCount FUEL_BIT_COUNT = new MaxCount(TileEntityTable.MAX_POWER);

    @Override
    public void save(TileEntityTable table, DataWriter dw) {
        dw.writeData(table.getPower(), FUEL_BIT_COUNT);
    }

    @Override
    public void load(TileEntityTable table, DataReader dr) {
        table.setPower(dr.readData(FUEL_BIT_COUNT));
    }
}
