package vswe.production.network.data;

import vswe.production.item.Upgrade;
import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.network.IBitCount;
import vswe.production.network.MaxCount;
import vswe.production.page.unit.Unit;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.tileentity.TileEntityTable;


public abstract class DataUnit extends DataBase {
    public static final int LENGTH = 8;

    protected Unit getUnit(TileEntityTable table, int id) {
        boolean isCrafting = id % 2 == 1;
        id /= 2;
        if (isCrafting) {
            return table.getMainPage().getCraftingList().get(id);
        }else{
            return table.getMainPage().getSmeltingList().get(id);
        }
    }

    public static int getId(Unit unit) {
        return unit.getId() * 2 + (unit instanceof UnitCrafting ? 1 : 0);
    }


    public static class Progress extends DataUnit {
        private static final IBitCount BIT_COUNT = new MaxCount(Unit.PRODUCTION_TIME);

        @Override
        public void save(TileEntityTable table, DataWriter dw, int id) {
            dw.writeData(getUnit(table, id).getProductionProgress(), BIT_COUNT);
        }

        @Override
        public void load(TileEntityTable table, DataReader dr, int id) {
            getUnit(table, id).setProductionProgress(dr.readData(BIT_COUNT));
        }
    }

    public static class Charged extends DataUnit {
        private static final IBitCount BIT_COUNT = new MaxCount(Unit.CHARGES_PER_LEVEL * Upgrade.CHARGED.getMaxCount());

        @Override
        public void save(TileEntityTable table, DataWriter dw, int id) {
            dw.writeData(getUnit(table, id).getChargeCount(), BIT_COUNT);
        }

        @Override
        public void load(TileEntityTable table, DataReader dr, int id) {
            getUnit(table, id).setChargeCount(dr.readData(BIT_COUNT));
        }
    }

}
