package vswe.production.gui.container.slot;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vswe.production.gui.GuiBase;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class SlotUpgrade extends SlotTable {
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
    public boolean isItemValid(ItemStack itemstack) {
        return super.isItemValid(itemstack) && (itemstack == null || (main == null ? isMainItem(itemstack) : isUpgradeItem(itemstack)));
    }

    private boolean isUpgradeItem(ItemStack itemstack) {
        return false; //TODO
    }

    private boolean isMainItem(ItemStack itemstack) {
        return itemstack.getItem().equals(Item.getItemFromBlock(Blocks.crafting_table)) || itemstack.getItem().equals(Item.getItemFromBlock(Blocks.furnace));
    }

    @Override
    public boolean isEnabled() {
        return main == null || main.getHasStack();
    }

    @Override
    public int getTextureIndex(GuiBase gui) {
        return main == null && getHasStack() ? 2 : super.getTextureIndex(gui);
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        table.onUpgradeChange();
    }
}
