package vswe.production.page.setting;


import net.minecraft.item.ItemStack;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.page.unit.Unit;
import vswe.production.tileentity.TileEntityTable;

import java.util.List;

public class SettingNormal extends Setting{
    public SettingNormal(TileEntityTable table, int id, int x, int y) {
        super(table, id, x, y);
    }

    @Override
    public ItemStack getItem() {
        return table.getUpgradePage().getUpgradeMainItem(id);
    }

    @Override
    public List<SlotBase> getSlots() {
        Unit unit = table.getMainPage().getCraftingList().get(id);
        if (!unit.isEnabled()) {
            unit = table.getMainPage().getSmeltingList().get(id);
            if (!unit.isEnabled()) {
                return null;
            }
        }

        return unit.getSlots();
    }

    @Override
    public String getName() {
        return null;
    }
}
