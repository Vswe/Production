package vswe.production.page.unit;


import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import vswe.production.gui.GuiBase;
import vswe.production.gui.container.slot.SlotUnit;
import vswe.production.gui.container.slot.SlotUnitFurnaceResult;
import vswe.production.page.Page;
import vswe.production.tileentity.TileEntityTable;
import vswe.production.tileentity.data.DataSmelting;
import vswe.production.tileentity.data.DataType;

public class UnitSmelting extends Unit {


    public UnitSmelting(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, page, id, x, y);
    }

    private int inputId;
    private int outputId;

    private static final int START_X = 23;
    private static final int START_Y = 23;
    private static final int RESULT_X = 56;

    @Override
    public int createSlots(int id) {
        inputId = id;
        addSlot(new SlotUnit(table, page, id++, this.x + START_X, this.y + START_Y, this));
        outputId = id;
        addSlot(new SlotUnitFurnaceResult(table, page, id++, this.x + START_X + RESULT_X, this.y + START_Y, this));

        return id;
    }

    @Override
    public void onSlotChanged() {

    }

    private static final int SMELT_TIME = 200; //TODO use the same speed as vanilla or not?
    private int smeltingProgress;

    @Override
    public void onUpdate() {
        if (!table.getWorldObj().isRemote) {
            boolean updatedProgress = false;

            ItemStack input = table.getStackInSlot(inputId);
            if (input != null) {
                ItemStack output = table.getStackInSlot(outputId);
                ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
                if (table.getPower() > 0 && canSmelt(result, output)) {
                    table.setPower(table.getPower() - 1);
                    smeltingProgress++;
                    if (smeltingProgress >= SMELT_TIME) {
                        smeltingProgress = 0;
                        if (output == null) {
                            table.setInventorySlotContents(outputId, result.copy());
                        }else{
                            table.getStackInSlot(outputId).stackSize += result.stackSize;
                        }

                        table.decrStackSize(inputId, 1);
                    }
                    updatedProgress = true;
                }
            }else if (smeltingProgress != 0){
                smeltingProgress = 0;
                updatedProgress = true;
            }

            if (updatedProgress) {
                table.sendDataToAllPlayer(TYPES[this.id]);
            }
        }
    }

    private static DataType[] TYPES = {DataType.SMELT_1, DataType.SMELT_2, DataType.SMELT_3, DataType.SMELT_4};

    private boolean canSmelt(ItemStack result, ItemStack output) {
        if (result != null) {
            if (output == null) {
                return true;
            }else if(output.isItemEqual(result)){
                int resultSize = output.stackSize + result.stackSize;
                if (resultSize <= table.getInventoryStackLimit() && resultSize <= output.getMaxStackSize()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isEnabled() {
        ItemStack item = table.getUpgradePage().getUpgradeMainItem(id);

        return item != null && Item.getItemFromBlock(Blocks.furnace) == item.getItem();
    }

    public int getSmeltingProgress() {
        return smeltingProgress;
    }

    public void setSmeltingProgress(int smeltingProgress) {
        this.smeltingProgress = smeltingProgress;
    }

    private static final int ARROW_SRC_X = 0;
    private static final int ARROW_SRC_Y = 34;
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;
    private static final int ARROW_X = 25;
    private static final int ARROW_Y = 1;
    private static final int PROGRESS_OFFSET = -1;

    @Override
    public void draw(GuiBase gui, int mX, int mY) {
        super.draw(gui, mX, mY);

        gui.drawRect(this.x + START_X + ARROW_X, this.y + START_Y + ARROW_Y, ARROW_SRC_X, ARROW_SRC_Y, ARROW_WIDTH, ARROW_HEIGHT);
        gui.drawRect(this.x + START_X + ARROW_X, this.y + START_Y + ARROW_Y + PROGRESS_OFFSET, ARROW_SRC_X, ARROW_SRC_Y + ARROW_HEIGHT, smeltingProgress * ARROW_WIDTH / SMELT_TIME, ARROW_HEIGHT);
    }
}
