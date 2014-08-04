package vswe.production.gui.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vswe.production.page.Page;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.tileentity.TileEntityTable;


public class SlotUnitCraftingResult extends SlotUnit {
    public SlotUnitCraftingResult(TileEntityTable table, Page page, int id, int x, int y, UnitCrafting unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isBig() {
         return true;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack item) {
        super.onPickupFromSlot(player, item);
        ((UnitCrafting)unit).onCrafting();
    }

    @Override
    public boolean canSupplyItems() {
        return true;
    }

    @Override
    public boolean canAcceptItems() {
        return false;
    }


}
