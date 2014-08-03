package vswe.production.gui.container.slot;

import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class SlotUpgrade extends SlotBase {
    public SlotUpgrade(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, page, id, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
