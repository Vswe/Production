package vswe.production.gui.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.production.gui.GuiBase;
import vswe.production.page.Page;
import vswe.production.page.setting.Transfer;
import vswe.production.tileentity.TileEntityTable;


public class SlotBase extends Slot {
    private int x;
    private int y;
    private Transfer[] input = new Transfer[6];
    private Transfer[] output = new Transfer[6];
    protected TileEntityTable table;

    public SlotBase(IInventory inventory, TileEntityTable table, int id, int x, int y) {
        super(inventory, id, x, y);

        this.x = x;
        this.y = y;
        this.table = table;
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
        return table.getMenu() == null;
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

    public int getTextureIndex(GuiBase gui) {
        return isEnabled() ? 0 : 1;
    }

    public boolean isBig() {
        return false;
    }

    public boolean isOutputValid(int id, ItemStack item) {
        return output[id] != null && output[id].isValid(table, item);
    }

    public boolean isInputValid(int id, ItemStack item) {
        return input[id] != null && input[id].isValid(table, item);
    }

    public void resetValidity(int id) {
        this.output[id] = null;
        this.input[id] = null;
    }

    public void setValidity(int id, Transfer input, Transfer output) {
        this.output[id] = output;
        this.input[id] = input;
    }

    public boolean canAcceptItems() {
        return true;
    }

    public boolean canSupplyItems() {
        return true;
    }

}
