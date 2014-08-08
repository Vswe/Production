package vswe.production.gui.container.slot;

import vswe.production.item.Upgrade;
import vswe.production.page.Page;
import vswe.production.page.unit.Unit;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.tileentity.TileEntityTable;


public class SlotUnitCraftingStorage extends SlotUnit {
    public SlotUnitCraftingStorage(TileEntityTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isVisible() {
        return isAvailable() && super.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return isAvailable() && super.isEnabled();
    }

    private boolean isAvailable() {
        return table.getUpgradePage().hasUpgrade(unit.getId(), Upgrade.STORAGE);
    }

    @Override
    public boolean canAcceptItems() {
        return true;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        ((UnitCrafting)unit).onGridChanged();
    }
}
