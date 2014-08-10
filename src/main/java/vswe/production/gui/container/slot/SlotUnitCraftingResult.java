package vswe.production.gui.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vswe.production.item.Upgrade;
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
    public boolean canAcceptItems() {
        return false;
    }


    @Override
    public int getY() {
        int offset = 0;
        if (table.getUpgradePage().hasUpgrade(unit.getId(), Upgrade.AUTO_CRAFTER)) {
            offset = UnitCrafting.RESULT_AUTO_OFFSET;
        }
        return super.getY() + offset;
    }

    @Override
    public boolean canPickUpOnDoubleClick() {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int count) {
        ItemStack itemstack = getStack();
        if (itemstack != null) {
            putStack(null);
        }
        return itemstack;
    }
}
