package vswe.production.page.unit;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import vswe.production.gui.container.slot.SlotUnit;
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
        for (int i = 0; i < STORAGE_COUNT; i++) {
            addSlot(new SlotUnitCraftingStorage(table, page, id++, this.x + START_X + i * SLOT_SIZE, this.y + STORAGE_Y, this));
        }

        gridId = id;
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                addSlot(new SlotUnit(table, page, id++, this.x + START_X + x * SLOT_SIZE, this.y + START_Y + y * SLOT_SIZE, this));
            }
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
    public void onSlotChanged() {
        ItemStack result = inventoryCrafting.getResult();
        if (result != null) {
            result = result.copy();
        }
        table.setInventorySlotContents(resultId, result);
    }

    @Override
    protected boolean canCharge() {
        return super.canCharge() && table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER);
    }

    public void onCrafting() {
        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryCrafting.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null) {
                if (itemStack.getItem().hasContainerItem(itemStack)) {
                    //TODO where should the container go?
                    if (false && itemStack.getItem().doesContainerItemLeaveCraftingGrid(itemStack)) {
                        inventoryCrafting.decrStackSize(i, 1);
                        ItemStack containerItem = itemStack.getItem().getContainerItem(itemStack);
                    }else{
                       inventoryCrafting.setInventorySlotContents(i, itemStack.getItem().getContainerItem(itemStack));
                    }
                }else{
                    inventoryCrafting.decrStackSize(i, 1);
                }
            }
        }
    }

    private CraftingDummy inventoryCrafting = new CraftingDummy();
    public class CraftingDummy extends InventoryCrafting {

        private static final int INVENTORY_WIDTH = 3;
        private static final int INVENTORY_HEIGHT = 3;

        public CraftingDummy() {
            super(null, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        }

        @Override
        public int getSizeInventory() {
            return INVENTORY_WIDTH * INVENTORY_HEIGHT;
        }

        @Override
        public ItemStack getStackInSlot(int id) {
            return table.getStackInSlot(gridId + id);
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


        @Override
        public ItemStack getStackInSlotOnClosing(int id) {
            return table.getStackInSlotOnClosing(gridId + id);
        }

        @Override
        public ItemStack decrStackSize(int id, int count) {
            return table.decrStackSize(gridId + id, count);
        }


        @Override
        public void setInventorySlotContents(int id, ItemStack item) {
            table.setInventorySlotContents(gridId + id, item);
        }

        public ItemStack getResult() {
            IRecipe recipe = getRecipe();
            return recipe == null ? null : recipe.getCraftingResult(this);
        }

        public IRecipe getRecipe() {
            for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
                IRecipe irecipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

                if (irecipe.matches(this, table.getWorldObj())) {
                    return irecipe;
                }
            }

            return null;
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
            return table.getStackInSlot(resultId);
        }else{
            return null;
        }
    }

    @Override
    protected int getOutputId() {
        return outputId;
    }

    @Override
    protected void onProduction() {
        onCrafting();
        onSlotChanged();
    }
}
