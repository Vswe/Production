package vswe.production.gui.container.slot;

import vswe.production.gui.GuiBase;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class SlotTable extends SlotBase {
    private Page page;

    public SlotTable(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, table, id, x, y);

        this.page = page;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && (page == null || page.equals(table.getSelectedPage()));
    }

    @Override
    public int getTextureIndex(GuiBase gui) {
        return gui.mc.thePlayer.inventory.getItemStack() == null && !getHasStack() && gui.getSelectedSlot() != null &&  gui.getSelectedSlot() instanceof SlotPlayer && gui.getSelectedSlot().getHasStack() && isItemValid(gui.getSelectedSlot().getStack()) && getSlotStackLimit(gui.getSelectedSlot().getStack()) > (getHasStack() ? getStack().stackSize : 0) ? 3 : super.getTextureIndex(gui);
    }
}
