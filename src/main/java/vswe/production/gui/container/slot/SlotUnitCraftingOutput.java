package vswe.production.gui.container.slot;

import net.minecraft.item.ItemStack;
import vswe.production.item.Upgrade;
import vswe.production.page.Page;
import vswe.production.page.unit.Unit;
import vswe.production.tileentity.TileEntityTable;


public class SlotUnitCraftingOutput extends SlotUnit {
    public SlotUnitCraftingOutput(TileEntityTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isVisible() {
        return isAutoCrafting() && super.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return isAutoCrafting() && super.isEnabled();
    }

    private boolean isAutoCrafting() {
        return table.getUpgradePage().hasUpgrade(unit.getId(), Upgrade.AUTO_CRAFTER);
    }

    @Override
    public boolean canSupplyItems() {
        return true;
    }

    @Override
    public boolean canAcceptItems() {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }


}
