package vswe.production.gui.container.slot;


import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;

public class SlotFuel extends SlotBase {
    public SlotFuel(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, page, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return super.isItemValid(itemstack) && TileEntityFurnace.isItemFuel(itemstack);
    }
}
