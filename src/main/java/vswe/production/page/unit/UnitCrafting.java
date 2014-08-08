package vswe.production.page.unit;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import vswe.production.gui.container.slot.SlotUnit;
import vswe.production.gui.container.slot.SlotUnitCraftingGrid;
import vswe.production.gui.container.slot.SlotUnitCraftingOutput;
import vswe.production.gui.container.slot.SlotUnitCraftingResult;
import vswe.production.gui.container.slot.SlotUnitCraftingStorage;
import vswe.production.item.Upgrade;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;


public class UnitCrafting extends Unit {
    public UnitCrafting(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, page, id, x, y);
    }

    private static final int START_X = 5;
    private static final int START_Y = 5;
    private static final int SLOT_SIZE = 18;
    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;
    private static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;
    private static final int RESULT_OFFSET_X = 94;
    private static final int RESULT_OFFSET_Y = 18;
    public static final int RESULT_AUTO_OFFSET = -5;
    private static final int STORAGE_COUNT = 6;
    private static final int STORAGE_Y = 65;

    private static final int ARROW_X = 61;
    private static final int ARROW_Y = 19;

    private int gridId;
    private int resultId;
    private int outputId;

    @Override
    public int createSlots(int id) {
        gridId = id;
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                addSlot(new SlotUnitCraftingGrid(table, page, id++, this.x + START_X + x * SLOT_SIZE, this.y + START_Y + y * SLOT_SIZE, this));
            }
        }

        for (int i = 0; i < STORAGE_COUNT; i++) {
            addSlot(new SlotUnitCraftingStorage(table, page, id++, this.x + START_X + i * SLOT_SIZE, this.y + STORAGE_Y, this));
        }

        resultId = id;
        addSlot(new SlotUnitCraftingResult(table, page, id++, this.x + START_X + RESULT_OFFSET_X, this.y + START_Y + RESULT_OFFSET_Y, this));

        outputId = id;
        addSlot(new SlotUnitCraftingOutput(table, page, id++, this.x + START_X + RESULT_OFFSET_X, this.y + START_Y + 2 * SLOT_SIZE, this));

        return id;
    }

    @Override
    public boolean isEnabled() {
        ItemStack item = table.getUpgradePage().getUpgradeMainItem(id);

        return item != null && Item.getItemFromBlock(Blocks.crafting_table) == item.getItem();
    }


    @Override
    protected boolean canCharge() {
        return super.canCharge() && table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER);
    }

    public void onCrafting() {
        lockedRecipeGeneration = true;
        onCrafting(inventoryCrafting, false);
        lockedRecipeGeneration = false;
        onGridChanged();
    }

    private void onCrafting(CraftingBase crafting, boolean fake) {
        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack itemStack = crafting.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null) {
                int id = i;
                for (int j = GRID_SIZE; j < crafting.getSizeInventory(); j++) {
                    ItemStack other = crafting.getStackInSlot(j);
                    //TODO support ore dictionary and fuzzy etc?. Problem is that it needs to figure out if hte recipe supports it
                    if (other != null && itemStack.isItemEqual(other) && ItemStack.areItemStackTagsEqual(itemStack, other)) {
                        id = j;
                        itemStack = other;
                        break;
                    }
                }

                if (itemStack.getItem().hasContainerItem(itemStack)) {
                    //TODO where should the container go?
                    if (false && itemStack.getItem().doesContainerItemLeaveCraftingGrid(itemStack)) {
                        crafting.decrStackSize(id, 1);
                        ItemStack containerItem = itemStack.getItem().getContainerItem(itemStack);
                    }else{
                        crafting.setInventorySlotContents(id, itemStack.getItem().getContainerItem(itemStack));
                    }
                }else{
                    crafting.decrStackSize(id, 1);
                }
            }
        }
    }


    private CraftingBase inventoryCrafting = new CraftingWrapper();

    public int getGridId() {
        return gridId;
    }

    private boolean canAutoCraft;
    private boolean lockedRecipeGeneration;
    public void onGridChanged() {
        if (!lockedRecipeGeneration) {
            IRecipe recipe = inventoryCrafting.getRecipe();
            ItemStack result = inventoryCrafting.getResult(recipe);
            if (result != null) {
                result = result.copy();
            }
            table.setInventorySlotContents(resultId, result);

            if (table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER)) {
                if (recipe == null) {
                    canAutoCraft = false;
                }else{
                    CraftingBase dummy = new CraftingDummy(inventoryCrafting);
                    onCrafting(dummy, true);
                    canAutoCraft = dummy.isMatch(recipe);
                }
            }
        }
    }

    private int canCraftTick = 0;
    private static final int CAN_CRAFT_DELAY = 10;
    private CraftingBase oldGrid;
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (++canCraftTick == CAN_CRAFT_DELAY) {
            canCraftTick = 0;
            if (oldGrid == null || !oldGrid.equals(inventoryCrafting)) {
                oldGrid = new CraftingDummy(inventoryCrafting);
                onGridChanged();
            }
        }
    }

    private boolean hadAutoCraft;
    private boolean firstAutoCraftCheck = true;
    public void onUpgradeChange() {
        boolean autoCraft = table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER);
        boolean update = firstAutoCraftCheck || (autoCraft && !hadAutoCraft);


        hadAutoCraft = autoCraft;
        firstAutoCraftCheck = false;

        if (update) {
            onGridChanged();
        }
    }

    private class CraftingWrapper extends CraftingBase {
        @Override
        public ItemStack getStackInSlot(int id) {
            return table.getStackInSlot(gridId + id);
        }

        @Override
        public void setInventorySlotContents(int id, ItemStack item) {
            table.setInventorySlotContents(gridId + id, item);
        }
    }

    private class CraftingDummy extends CraftingBase {
        private ItemStack[] items;

        private CraftingDummy(CraftingBase base) {
            items = new ItemStack[base.getSizeInventory()];
            for (int i = 0; i < items.length; i++) {
                ItemStack itemStack = base.getStackInSlot(i);
                if (itemStack != null) {
                    items[i] = itemStack.copy();
                }
            }
        }

        @Override
        public int getSizeInventory() {
            return items.length;
        }

        @Override
        public ItemStack getStackInSlot(int id) {
            return items[id];
        }

        @Override
        public void setInventorySlotContents(int id, ItemStack item) {
            items[id] = item;
        }
    }

    private class CraftingBase extends InventoryCrafting {

        private static final int INVENTORY_WIDTH = 3;
        private static final int INVENTORY_HEIGHT = 3;

        public CraftingBase() {
            super(null, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        }

        @Override
        public int getSizeInventory() {
            return INVENTORY_WIDTH * INVENTORY_HEIGHT + (table.getUpgradePage().hasUpgrade(id, Upgrade.STORAGE) ? STORAGE_COUNT : 0);
        }


        @Override
        public ItemStack decrStackSize(int id, int count) {
            ItemStack item = getStackInSlot(id);
            if (item != null) {
                if (item.stackSize <= count) {
                    setInventorySlotContents(id, null);
                    return item;
                }

                ItemStack result = item.splitStack(count);

                if (item.stackSize == 0) {
                    setInventorySlotContents(id, null);
                }

                return result;
            }else {
                return null;
            }
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int id) {
            ItemStack item = getStackInSlot(id);
            setInventorySlotContents(id, null);
            return item;
        }

        @Override
        public ItemStack getStackInRowAndColumn(int x, int y) {
            if (x >= 0 && x < INVENTORY_WIDTH){
                int id = x + y * INVENTORY_WIDTH;
                return this.getStackInSlot(id);
            }else{
                return null;
            }
        }


        public ItemStack getResult() {
            return getResult(getRecipe());
        }

        public ItemStack getResult(IRecipe recipe) {
            return recipe == null ? null : recipe.getCraftingResult(this);
        }

        public boolean isMatch(IRecipe recipe) {
            return recipe.matches(this, table.getWorldObj());
        }

        public IRecipe getRecipe() {
            for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
                IRecipe recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

                if (isMatch(recipe)) {
                    return recipe;
                }
            }

            return null;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CraftingBase)) return false;

            CraftingBase crafting = (CraftingBase)obj;

            if (getSizeInventory() != crafting.getSizeInventory()) return false;

            for (int i = 0; i < getSizeInventory(); i++) {
                if (!ItemStack.areItemStacksEqual(getStackInSlot(i), crafting.getStackInSlot(i))) {
                    return false;
                }
            }

            return true;
        }
    }



    @Override
    protected int getArrowX() {
        return START_X + ARROW_X;
    }

    @Override
    protected int getArrowY() {
        int offset = 0;
        if (table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER)) {
            offset = RESULT_AUTO_OFFSET;
        }
        return START_Y + ARROW_Y + offset;
    }

    @Override
    protected ItemStack getProductionResult() {
        if (table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER)) {
            ItemStack result = table.getStackInSlot(resultId);
            if (result != null && canAutoCraft) {
                return result;
            }
        }

        return null;
    }

    @Override
    protected int getOutputId() {
        return outputId;
    }

    @Override
    protected void onProduction() {
        onCrafting();
    }
}
