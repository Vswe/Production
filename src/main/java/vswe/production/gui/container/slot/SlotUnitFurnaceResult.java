package vswe.production.gui.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vswe.production.page.Page;
import vswe.production.page.unit.Unit;
import vswe.production.tileentity.TileEntityTable;


public class SlotUnitFurnaceResult extends SlotUnit {
    public SlotUnitFurnaceResult(TileEntityTable table, Page page, int id, int x, int y, Unit unit) {
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
}
