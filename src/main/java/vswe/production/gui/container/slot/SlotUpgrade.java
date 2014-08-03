package vswe.production.gui.container.slot;

import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class SlotUpgrade extends SlotBase {
    private SlotUpgrade main;

    public SlotUpgrade(TileEntityTable table, Page page, int id, int x, int y, SlotUpgrade main) {
        super(table, page, id, x, y);
        this.main = main;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isEnabled() {
        return main == null || main.getHasStack();
    }
}
