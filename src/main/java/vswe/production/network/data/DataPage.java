package vswe.production.network.data;


import vswe.production.network.BasicCount;
import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.IBitCount;
import vswe.production.tileentity.TileEntityTable;

public class DataPage extends DataBase {
    private static final IBitCount BITS = new BasicCount(2);

    @Override
    public void save(TileEntityTable table, DataWriter dw, int id) {
        dw.writeData(table.getSelectedPage().getId(), BITS);
    }

    @Override
    public void load(TileEntityTable table, DataReader dr, int id) {
        table.setSelectedPage(table.getPages().get(dr.readData(BITS)));
    }
}
