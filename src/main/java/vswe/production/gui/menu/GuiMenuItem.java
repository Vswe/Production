package vswe.production.gui.menu;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vswe.production.page.setting.ItemSetting;
import vswe.production.tileentity.TileEntityTable;


public class GuiMenuItem extends GuiMenu {
    private ItemSetting setting;
    private ItemStack item;
    public GuiMenuItem(TileEntityTable table, ItemSetting setting) {
        super(table);

        this.setting = setting;
        this.item = setting.getItem();
        this.item = new ItemStack(Blocks.dirt);
    }

    @Override
    protected void save() {
        setting.setItem(item);
    }
}
