package vswe.production.page;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.production.gui.GuiBase;
import vswe.production.gui.GuiTable;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.tileentity.TileEntityTable;


public abstract class Page {
    private String name;
    protected TileEntityTable table;
    private int id;


    public Page(TileEntityTable table, String name) {
        this.id = table.getPages().size();
        this.table = table;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int createSlots(int id);

    protected void addSlot(SlotBase slot) {
        table.addSlot(slot);
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY) {
        gui.drawString(name, 8, 6, 0x404040);
    }
    @SideOnly(Side.CLIENT)
    public void onClick(GuiBase gui, int mX, int mY, int button) {}

    public int getId() {
        return id;
    }

    public void onUpdate() {}

    @SideOnly(Side.CLIENT)
    public void onRelease(GuiTable gui, int mX, int mY, int button) {}
}
