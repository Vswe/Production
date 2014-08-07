package vswe.production.page;

import net.minecraft.item.ItemStack;
import vswe.production.gui.GuiBase;
import vswe.production.gui.container.slot.SlotUpgrade;
import vswe.production.item.ModItems;
import vswe.production.item.Upgrade;
import vswe.production.tileentity.TileEntityTable;

import java.util.HashMap;
import java.util.Map;


public class PageUpgrades extends Page {
    private static final int START_X = 10;
    private static final int START_Y = 20;
    private static final int OFFSET_X = 100;
    private static final int OFFSET_Y = 50;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = 4;
    private static final int SLOT_ROWS = 2;
    private static final int GLOBAL_SLOTS = 8;

    private static final int GLOBAL_X = 10;
    private static final int GLOBAL_Y = 130;

    public PageUpgrades(TileEntityTable table, String name) {
        super(table, name);
    }

    private int startId;

    @Override
    public int createSlots(int id) {
        startId = id;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                SlotUpgrade main = null;
                for (int r = 0; r < SLOT_ROWS; r++) {
                    for (int c = 0; c < SLOTS_PER_ROW; c++) {
                        SlotUpgrade slot = new SlotUpgrade(table, this, id++, START_X + OFFSET_X * x + SLOT_SIZE * c, START_Y + OFFSET_Y * y + SLOT_SIZE * r, main, x + y * 2);
                        addSlot(slot);
                        if (main == null) {
                            main = slot;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < GLOBAL_SLOTS; i++) {
            addSlot(new SlotUpgrade(table, this, id++, GLOBAL_X + SLOT_SIZE * i, GLOBAL_Y, null, 4));
        }

        return id;
    }

    @Override
    public void draw(GuiBase gui, int mX, int mY) {
        super.draw(gui, mX, mY);
        gui.drawString("Global Upgrades", GLOBAL_X, GLOBAL_Y - 10, 0x404040);
    }

    public ItemStack getUpgradeMainItem(int id) {
        return table.getSlots().get(startId + id * SLOT_ROWS * SLOTS_PER_ROW).getStack();
    }

    private Map[] upgrades;

    public void onUpgradeChange() {
        upgrades = new Map[5];
        for (int i = 0; i < 4; i++) {
            if (getUpgradeMainItem(i) != null) {
                upgrades[i] = loadUpgrades(startId + i * SLOT_ROWS * SLOTS_PER_ROW + 1, SLOT_ROWS * SLOTS_PER_ROW - 1);
            }
        }
        upgrades[GLOBAL_ID] = loadUpgrades(startId + 4 * SLOT_ROWS * SLOTS_PER_ROW, GLOBAL_SLOTS);
    }


    private static final int GLOBAL_ID = 4;
    public int getUpgradeCountRaw(int id, Upgrade upgrade) {
        //noinspection unchecked
        Map<Upgrade, Integer> map = upgrades[id];

        if (map != null) {
            Integer count = map.get(upgrade);
            if (count != null) {
                return count;
            }
        }

        return 0;
    }

    public int getUpgradeCount(int id, Upgrade upgrade) {
        return Math.min(getUpgradeCountRaw(id, upgrade), upgrade.getMaxCount());
    }

    public boolean hasUpgrade(int id, Upgrade upgrade) {
        return getUpgradeCountRaw(id, upgrade) > 0;
    }

    public int getGlobalUpgradeCount(Upgrade upgrade) {
        return getUpgradeCount(GLOBAL_ID, upgrade);
    }

    public boolean hasGlobalUpgrade(Upgrade upgrade) {
        return hasUpgrade(GLOBAL_ID, upgrade);
    }


    private Map<Upgrade, Integer> loadUpgrades(int startId, int length) {
        Map<Upgrade, Integer> map = new HashMap<Upgrade, Integer>();

        for (int i = startId; i < startId + length; i++) {
            ItemStack itemStack = table.getStackInSlot(i);
            Upgrade upgrade = ModItems.upgrade.getUpgrade(itemStack);
            if (upgrade != null) {
                Integer count = map.get(upgrade);
                if (count == null) {
                    count = 0;
                }
                count += itemStack.stackSize;
                map.put(upgrade, count);
            }
        }

        return map;
    }
}
