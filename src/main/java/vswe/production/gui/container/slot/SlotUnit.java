package vswe.production.gui.container.slot;

import vswe.production.page.Page;
import vswe.production.page.unit.Unit;
import vswe.production.tileentity.TileEntityTable;


public class SlotUnit extends SlotBase {
    protected Unit unit;
    public SlotUnit(TileEntityTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y);

        this.unit = unit;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && unit.isEnabled();
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        unit.onSlotChanged();
    }
}
