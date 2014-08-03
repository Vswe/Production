package vswe.production.tileentity.data;

import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.tileentity.TileEntityTable;


public class DataSmelting extends DataBase {

    private int id;

    private DataSmelting(int id) {
        this.id = id;
    }

    @Override
    public void save(TileEntityTable table, DataWriter dw) {
        dw.writeByte(table.getMainPage().getSmeltingList().get(id).getSmeltingProgress());
    }

    @Override
    public void load(TileEntityTable table, DataReader dr) {
        table.getMainPage().getSmeltingList().get(id).setSmeltingProgress(dr.readByte());
    }

    public static class DataSmelting1 extends DataSmelting {
        public DataSmelting1() {
            super(0);
        }
    }
    public static class DataSmelting2 extends DataSmelting {
        public DataSmelting2() {
            super(1);
        }
    }
    public static class DataSmelting3 extends DataSmelting {
        public DataSmelting3() {
            super(2);
        }
    }
    public static class DataSmelting4 extends DataSmelting {
        public DataSmelting4() {
            super(3);
        }
    }


}
