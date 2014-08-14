package vswe.production.gui.container.slot;


import net.minecraft.item.ItemStack;
import vswe.production.item.Upgrade;
import vswe.production.page.Page;
import vswe.production.page.unit.Unit;
import vswe.production.tileentity.TileEntityTable;

public class SlotUnitFurnaceQueue extends SlotUnitFurnaceInput {
    private int queueId;
    public SlotUnitFurnaceQueue(TileEntityTable table, Page page, int id, int x, int y, Unit unit, int queueId) {
        super(table, page, id, x, y, unit);
        this.queueId = queueId;
    }

    @Override
    public boolean isVisible() {
        return isUsed() && super.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return isUsed() && super.isEnabled();
    }

    private boolean isUsed() {
        return queueId < table.getUpgradePage().getUpgradeCount(unit.getId(), Upgrade.QUEUE);
    }

    @Override
    public boolean canShiftClickInto(ItemStack item) {
        return true;
    }
}
