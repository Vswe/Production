package vswe.production.tileentity.data;


import vswe.production.network.BasicCount;
import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.IBitCount;
import vswe.production.tileentity.TileEntityTable;

public class DataPage extends DataBase {
    private static final IBitCount BITS = new BasicCount(2);

    @Override
    public void save(TileEntityTable table, DataWriter dw) {
        dw.writeData(table.getSelectedPage().getId(), BITS);
    }

    @Override
    public void load(TileEntityTable table, DataReader dr) {
        table.setSelectedPage(table.getPages().get(dr.readData(BITS)));
    }
}
