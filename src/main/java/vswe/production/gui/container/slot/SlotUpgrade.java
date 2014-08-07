package vswe.production.gui.container.slot;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vswe.production.gui.GuiBase;
import vswe.production.item.ModItems;
import vswe.production.item.Upgrade;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class SlotUpgrade extends SlotTable {
    private SlotUpgrade main;
    private boolean isMain;
    private int upgradeSection;

    public SlotUpgrade(TileEntityTable table, Page page, int id, int x, int y, SlotUpgrade main, int upgradeSection) {
        super(table, page, id, x, y);
        this.main = main;
        isMain = main == null && upgradeSection < 4;
        this.upgradeSection = upgradeSection;
    }


    @Override
    public int getSlotStackLimit() {
        return isMain ? 1 : 8;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return super.isItemValid(itemstack) && (itemstack == null || (isMain ? isMainItem(itemstack) : isUpgradeItem(itemstack)));
    }

    private boolean isUpgradeItem(ItemStack itemstack) {
        Upgrade upgrade = ModItems.upgrade.getUpgrade(itemstack);
        return upgrade != null && upgrade.isValid(main != null ? main.getStack() : null);
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
        if (isMain) {
            if (getHasStack()) {
                return 2;
            }
        }else{
            Upgrade upgrade = ModItems.upgrade.getUpgrade(getStack());
            if (upgrade != null) {
                if (table.getUpgradePage().getUpgradeCount(upgradeSection, upgrade) > upgrade.getMaxCount()) {
                    return 4;
                }
            }
        }

        return super.getTextureIndex(gui);
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        table.onUpgradeChangeDistribute();
    }
}
