package vswe.production.page;

import vswe.production.gui.container.slot.SlotBase;
import vswe.production.gui.container.slot.SlotUpgrade;
import vswe.production.tileentity.TileEntityTable;


public class PageUpgrades extends Page {
    private static final int START_X = 10;
    private static final int START_Y = 20;
    private static final int OFFSET_X = 100;
    private static final int OFFSET_Y = 50;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 4;
    private static final int SLOT_ROWS = 2;

    public PageUpgrades(TileEntityTable table, String name) {
        super(table, name);
    }

    @Override
    public int createSlots(int id) {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                for (int r = 0; r < SLOT_ROWS; r++) {
                    for (int c = 0; c < SLOTS_PER_ROW; c++) {
                        addSlot(new SlotUpgrade(table, this, id++, START_X + OFFSET_X * x + SLOT_SIZE * c, START_Y + OFFSET_Y * y + SLOT_SIZE * r));
                    }
                }
            }
        }

        return super.createSlots(id);
    }
}
