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
        return shouldSlotHighlightSelf() && shouldHighlight(this, gui.getSelectedSlot()) && gui.getSelectedSlot() instanceof SlotPlayer ? 3 : super.getTextureIndex(gui);
    }
}
