package vswe.production.page;

import vswe.production.block.ModBlocks;
import vswe.production.gui.GuiBase;
import vswe.production.page.setting.Direction;
import vswe.production.page.setting.Setting;
import vswe.production.page.setting.SettingCoal;
import vswe.production.page.setting.SettingNormal;
import vswe.production.page.setting.Side;
import vswe.production.tileentity.TileEntityTable;
import vswe.production.tileentity.data.DataSide;
import vswe.production.tileentity.data.DataType;

import java.util.ArrayList;
import java.util.List;


public class PageTransfer extends Page {
    private List<Setting> settings;
    private Setting selected;

    public PageTransfer(TileEntityTable table, String name) {
        super(table, name);

        settings = new ArrayList<Setting>();
        for (int i = 0; i < 4; i++) {
            int x = SETTING_X + (i % 2) * SETTING_OFFSET;
            int y = SETTING_Y + (i / 2) * SETTING_OFFSET;
            settings.add(new SettingNormal(table, i, x, y));
        }
        settings.add(new SettingCoal(table, 4, SETTING_X + 2 * SETTING_OFFSET, SETTING_Y + SETTING_OFFSET / 2));


        for (Setting setting : settings) {
            for (Direction direction : Direction.values()) {
                setting.getSides().add(new Side(direction, SIDE_X + direction.getInterfaceX() * SIDE_OFFSET, SIDE_Y + direction.getInterfaceY() * SIDE_OFFSET));
            }
        }
    }


    private static final int SIDE_X = 75;
    private static final int SIDE_Y = 5;
    private static final int SIDE_OFFSET = 20;
    private static final int SIDE_SIZE = 18;
    private static final int SIDE_SRC_X = 0;
    private static final int SIDE_SRC_Y = 166;
    private static final int SIDE_ITEM_OFFSET = 1;

    private static final int SETTING_X = 5;
    private static final int SETTING_Y = 15;
    private static final int SETTING_OFFSET = 20;
    private static final int SETTING_SIZE = 18;
    private static final int SETTING_SRC_X = 0;
    private static final int SETTING_SRC_Y = 112;
    private static final int SETTING_ITEM_OFFSET = 1;

    @Override
    public int createSlots(int id) {
        return id;
    }

    @Override
    public void draw(GuiBase gui, int mX, int mY) {
        for (Setting setting : settings) {
            gui.prepare();
            boolean isValid = setting.isValid();
            boolean isSelected = setting.equals(selected);

            if (isSelected && !isValid) {
                selected = null;
            }

            int textureIndexX = isValid && gui.inBounds(setting.getX(), setting.getY(), SETTING_SIZE, SETTING_SIZE, mX, mY) ? 1 : 0;
            int textureIndexY = isValid ? isSelected ? 1 : 0 : 2;


            gui.drawRect(setting.getX(), setting.getY(), SETTING_SRC_X + textureIndexX * SETTING_SIZE, SETTING_SRC_Y + textureIndexY * SETTING_SIZE, SETTING_SIZE, SETTING_SIZE);
            gui.drawItem(setting.getItem(), setting.getX() + SETTING_ITEM_OFFSET, setting.getY() + SETTING_ITEM_OFFSET);
        }

        if (selected != null) {
            for (Side side : selected.getSides()) {
                gui.prepare();
                int textureIndexX = gui.inBounds(side.getX(), side.getY(), SIDE_SIZE, SIDE_SIZE, mX, mY) ? 1 : 0;
                boolean output = side.isOutputEnabled();
                boolean input = side.isInputEnabled();
                int textureIndexY = output && input ? 3 : output ? 2 : input ? 1 : 0;


                gui.drawRect(side.getX(), side.getY(), SIDE_SRC_X + textureIndexX * SIDE_SIZE, SIDE_SRC_Y + textureIndexY * SIDE_SIZE, SIDE_SIZE, SIDE_SIZE);
                gui.drawBlockIcon(ModBlocks.table.getIcon(side.getDirection().ordinal(), 0), side.getX() + SIDE_ITEM_OFFSET, side.getY() + SIDE_ITEM_OFFSET);
            }
        }
    }

    @Override
    public void onClick(GuiBase gui, int mX, int mY, int button) {
        for (Setting setting : settings) {
            if (gui.inBounds(setting.getX(), setting.getY(), SETTING_SIZE, SETTING_SIZE, mX, mY)) {
                if (setting.isValid()) {
                    if (setting.equals(selected)) {
                        selected = null;
                    }else{
                        selected = setting;
                    }
                }

                break;
            }
        }

        if (selected != null) {
            for (Side side : selected.getSides()) {
                if (gui.inBounds(side.getX(), side.getY(), SIDE_SIZE, SIDE_SIZE, mX, mY)) {
                    boolean input = side.isInputEnabled();
                    boolean output = side.isOutputEnabled();

                    int id = (output ? 2 : 0) + (input ? 1 : 0);
                    id += button == 0 ? 1 : -1;
                    if (id < 0) {
                        id += 4;
                    }else{
                        id %= 4;
                    }

                    side.setInputEnabled((id & 1) != 0);
                    side.setOutputEnabled((id & 2) != 0);

                    table.updateServer(DataType.SIDE, DataSide.getId(selected, side));
                    table.onSideChange();

                    break;
                }
            }
        }
    }

    public List<Setting> getSettings() {
        return settings;
    }
}
