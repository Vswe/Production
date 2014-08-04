package vswe.production.tileentity.data;

import vswe.production.network.DataReader;
import vswe.production.network.DataWriter;
import vswe.production.page.setting.Setting;
import vswe.production.page.setting.Side;
import vswe.production.tileentity.TileEntityTable;

//TODO update this when sides are implemented better
public class DataSide extends DataBase {

    private static final int SETTINGS = 5;
    private static final int SIDES = 6;
    public static final int LENGTH = SETTINGS * SIDES;

    @Override
    public void save(TileEntityTable table, DataWriter dw, int id) {
        int settingId = id % SETTINGS;
        id /= SETTINGS;
        int sideId = id % SIDES;


        Side side = table.getTransferPage().getSettings().get(settingId).getSides().get(sideId);

        dw.writeBoolean(side.isInputEnabled());
        dw.writeBoolean(side.isOutputEnabled());

    }

    @Override
    public void load(TileEntityTable table, DataReader dr, int id) {
        int settingId = id % SETTINGS;
        id /= SETTINGS;
        int sideId = id % SIDES;


        Side side = table.getTransferPage().getSettings().get(settingId).getSides().get(sideId);
        side.setInputEnabled(dr.readBoolean());
        side.setOutputEnabled(dr.readBoolean());
    }

    public static int getId(Setting setting, Side side) {
        return setting.getId() + SETTINGS * side.getDirection().ordinal();
    }
}
