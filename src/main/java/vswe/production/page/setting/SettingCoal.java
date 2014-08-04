package vswe.production.page.setting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.List;


public class SettingCoal extends Setting {
    private ItemStack itemStack;

    public SettingCoal(TileEntityTable table, int id, int x, int y) {
        super(table, id, x, y);
        itemStack = new ItemStack(Items.coal);
    }

    @Override
    public ItemStack getItem() {
        return itemStack;
    }

    @Override
    public List<SlotBase> getSlots() {
        List<SlotBase> slots = new ArrayList<SlotBase>();
        slots.add(table.getSlots().get(0));
        return slots;
    }
}
