package vswe.production.page.unit;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.production.gui.GuiBase;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {
    protected TileEntityTable table;
    protected Page page;
    protected int id;
    protected int x;
    protected int y;


    public Unit(TileEntityTable table, Page page, int id, int x, int y) {
        this.table = table;
        this.page = page;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY) {

    }

    protected void addSlot(SlotBase slot) {
        table.addSlot(slot);
        slots.add(slot);
    }

    public abstract int createSlots(int id);

    public void onUpdate() {}

    public abstract boolean isEnabled();

    //TODO make sure this is triggered when items enter through, for instance, hoppers. If it don't, trigger this from the tile entity rather than from the slot. (it probably don't work to be honest)
    //TODO figure out a way to make this trigger only once. For instance, using drag click can make this happen 9 times. When clicking normally it fires twice as well.
    public void onSlotChanged() {}

    private List<SlotBase> slots = new ArrayList<SlotBase>();
    public List<SlotBase> getSlots() {
        return slots;
    }
}
