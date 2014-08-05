package vswe.production.tileentity.data;


import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.page.setting.Setting;
import vswe.production.page.setting.Side;
import vswe.production.page.setting.Transfer;
import vswe.production.tileentity.TileEntityTable;


public abstract class DataSide extends DataBase {

    private static final int SETTINGS = 5;
    private static final int SIDES = 6;
    private static final int MODES = 2;
    public static final int LENGTH = SETTINGS * SIDES * MODES;


    protected Transfer getTransfer(TileEntityTable table, int id) {
        int settingId = id % SETTINGS;
        id /= SETTINGS;
        int sideId = id % SIDES;
        id /= SIDES;
        int modeId = id;
        Side side = table.getTransferPage().getSettings().get(settingId).getSides().get(sideId);
        if (modeId == 0) {
            return side.getInput();
        }else{
            return side.getOutput();
        }
    }

    public static int getId(Setting setting, Side side, Transfer transfer) {
        return setting.getId() + SETTINGS * side.getDirection().ordinal() + (transfer.isInput() ? 0 : SETTINGS * SIDES);
    }

    public static class Enabled extends DataSide {
        @Override
        public void save(TileEntityTable table, DataWriter dw, int id) {
            dw.writeBoolean(getTransfer(table, id).isEnabled());
        }

        @Override
        public void load(TileEntityTable table, DataReader dr, int id) {
            getTransfer(table, id).setEnabled(dr.readBoolean());
        }
    }

    public static class Auto extends DataSide {
        @Override
        public void save(TileEntityTable table, DataWriter dw, int id) {
            dw.writeBoolean(getTransfer(table, id).isAuto());
        }

        @Override
        public void load(TileEntityTable table, DataReader dr, int id) {
            getTransfer(table, id).setAuto(dr.readBoolean());
        }
    }
}
