package vswe.production.gui.container.slot;

import net.minecraft.inventory.IInventory;
import vswe.production.gui.GuiBase;
import vswe.production.tileentity.TileEntityTable;


public class SlotPlayer extends SlotBase {
    public SlotPlayer(IInventory inventory, TileEntityTable table, int id, int x, int y) {
        super(inventory, table, id, x, y);
    }

    @Override
    public int getTextureIndex(GuiBase gui) {
        return gui.mc.thePlayer.inventory.getItemStack() == null && getHasStack() && gui.getSelectedSlot() != null && !gui.getSelectedSlot().getHasStack() &&  gui.getSelectedSlot() instanceof SlotTable && gui.getSelectedSlot().getSlotStackLimit(getStack()) > 0 && gui.getSelectedSlot().isItemValid(getStack()) ? 3 : super.getTextureIndex(gui);
    }
}
