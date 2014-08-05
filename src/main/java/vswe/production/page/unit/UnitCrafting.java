package vswe.production.page.unit;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import vswe.production.gui.GuiBase;
import vswe.production.gui.container.slot.SlotUnit;
import vswe.production.gui.container.slot.SlotUnitCraftingResult;
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

    private static final int ARROW_SRC_X = 0;
    private static final int ARROW_SRC_Y = 34;
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;
    private static final int ARROW_X = 61;
    private static final int ARROW_Y = 19;

    private int startId;
    private int resultId;

    @Override
    public int createSlots(int id) {
        startId = id;

        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                addSlot(new SlotUnit(table, page, id++, this.x + START_X + x * SLOT_SIZE, this.y + START_Y + y * SLOT_SIZE, this));
            }
        }

        resultId = id;
        addSlot(new SlotUnitCraftingResult(table, page, id++, this.x + START_X + RESULT_OFFSET_X, this.y + START_Y + RESULT_OFFSET_Y, this));

        return id;
    }

    @Override
    public void draw(GuiBase gui, int mX, int mY) {
        super.draw(gui, mX, mY);

        gui.drawRect(this.x + START_X + ARROW_X, this.y + START_Y + ARROW_Y, ARROW_SRC_X, ARROW_SRC_Y, ARROW_WIDTH, ARROW_HEIGHT);
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


    //TODO make sure this is called properly when hoppers are extracting the items (to be honest, they probably won't work as they are now)
    //TODO handle container items properly
    public void onCrafting() {
        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            inventoryCrafting.decrStackSize(i, 1);
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
            return table.getStackInSlot(startId + id);
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
            return table.getStackInSlotOnClosing(startId + id);
        }

        @Override
        public ItemStack decrStackSize(int id, int count) {
            return table.decrStackSize(startId + id, count);
        }


        @Override
        public void setInventorySlotContents(int id, ItemStack item) {
            table.setInventorySlotContents(startId + id, item);
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
}
