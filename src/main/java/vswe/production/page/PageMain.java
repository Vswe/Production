package vswe.production.page;

import vswe.production.gui.GuiBase;
import vswe.production.page.unit.Unit;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.List;


public class PageMain extends Page {
    private List<Unit> units;

    public PageMain(TileEntityTable table, String name) {
        super(table, name);

        units = new ArrayList<Unit>();
        for (int i = 0; i < 4; i++){
            addUnit(i);
        }
    }

    private void addUnit(int id) {
        int x = id % 2;
        int y = id / 2;
        units.add(new UnitCrafting(table, this, id, x * WIDTH / 2, y * HEIGHT / 2));
    }

    @Override
    public int createSlots(int id) {
        for (Unit unit : units) {
            id = unit.createSlots(id);
        }

        return id;
    }

    private static final int WIDTH = 256;
    private static final int HEIGHT = 174;
    private static final int TEXTURE_SHEET_SIZE = 256;
    private static final int BAR_THICKNESS = 4;
    private static final int BAR_WIDTH = 240;
    private static final int BAR_HEIGHT = 162;

    private static final int BAR_HORIZONTAL_X = (WIDTH - BAR_WIDTH) / 2;
    private static final int BAR_HORIZONTAL_Y = (HEIGHT - BAR_THICKNESS) / 2;
    private static final int BAR_VERTICAL_X = (WIDTH - BAR_THICKNESS) / 2;
    private static final int BAR_VERTICAL_Y = (HEIGHT - BAR_HEIGHT) / 2;

    @Override
    public void draw(GuiBase gui, int mX, int mY) {
        gui.prepare();
        gui.drawRect(BAR_HORIZONTAL_X, BAR_HORIZONTAL_Y, 0, TEXTURE_SHEET_SIZE - BAR_THICKNESS, BAR_WIDTH, BAR_THICKNESS);
        gui.drawRect(BAR_VERTICAL_X, BAR_VERTICAL_Y, TEXTURE_SHEET_SIZE - BAR_THICKNESS, 0, BAR_THICKNESS, BAR_HEIGHT);

        for (Unit unit : units) {
            if (unit.isEnabled()) {
                unit.draw(gui, mX, mY);
            }
        }
    }
}
