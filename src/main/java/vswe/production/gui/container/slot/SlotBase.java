package vswe.production.gui.container.slot;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class SlotBase extends Slot {
    private Page page;
    protected TileEntityTable table;
    private int x;
    private int y;
    private SlotValidity[] valid = new SlotValidity[6];

    public SlotBase(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, id, x, y);
        this.table = table;
        this.page = page;

        this.x = x;
        this.y = y;

        update(isVisible());
    }

    public void update(boolean visible) {
        if (visible && isEnabled()) {
            xDisplayPosition = x;
            yDisplayPosition = y;
        }else{
            xDisplayPosition = -9000;
            yDisplayPosition = -9000;
        }
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return isEnabled();
    }

    public boolean isVisible() {
        return page == null || page.equals(table.getSelectedPage());
    }

    public boolean isEnabled() {
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTextureIndex() {
        return isEnabled() ? 0 : 1;
    }

    public boolean isBig() {
        return false;
    }

    public SlotValidity getValid(int id) {
        return valid[id];
    }

    public void setValid(SlotValidity valid, int id) {
        this.valid[id] = valid;
    }

    public boolean canAcceptItems() {
        return true;
    }

    public boolean canSupplyItems() {
        return true;
    }

}
