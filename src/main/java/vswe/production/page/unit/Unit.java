package vswe.production.page.unit;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import vswe.production.gui.GuiBase;
import vswe.production.gui.container.slot.SlotBase;
import vswe.production.item.Upgrade;
import vswe.production.network.data.DataType;
import vswe.production.network.data.DataUnit;
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

    private static final int ARROW_SRC_X = 0;
    private static final int ARROW_SRC_Y = 34;
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;
    private static final int PROGRESS_OFFSET = -1;

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY) {
        gui.prepare();
        int x = getArrowX();
        int y = getArrowY();
        gui.drawRect(this.x + x, this.y + y, ARROW_SRC_X, ARROW_SRC_Y, ARROW_WIDTH, ARROW_HEIGHT);
        int max = getMaxCharges();
        boolean charging = false;
        if (max > 0 && chargeCount > 0) {
            charging = true;
            GL11.glColor4f(0.11F, 0.35F, 0.17F, 1);
            int count = Math.min(chargeCount, max);
            gui.drawRect(this.x + x, this.y + y, ARROW_SRC_X, ARROW_SRC_Y + ARROW_HEIGHT, count * ARROW_WIDTH / max, ARROW_HEIGHT);
        }

        if (isCharging()) {
            charging = true;
            GL11.glColor4f(0.25F, 0.8F, 0.38F, 0.5F);
            GL11.glEnable(GL11.GL_BLEND);
        }else{
            GL11.glColor4f(1, 1, 1, 1);
        }
        gui.drawRect(this.x + x, this.y + y + PROGRESS_OFFSET, ARROW_SRC_X, ARROW_SRC_Y + ARROW_HEIGHT, productionProgress * ARROW_WIDTH / PRODUCTION_TIME, ARROW_HEIGHT);
        GL11.glDisable(GL11.GL_BLEND);

        if (charging && gui.inBounds(this.x + x, this.y + y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY)) {
            List<String> str = new ArrayList<String>();
            str.add(EnumChatFormatting.GREEN + (chargeCount < max ? "Charging" : "Fully Charged"));
            str.add("Charges: " + chargeCount + "/" + max);
            str.add(EnumChatFormatting.GRAY + "Charges can be consumed to instantly produce an item");

            gui.drawMouseOver(str, mX, mY);
        }
    }

    protected abstract int getArrowX();
    protected abstract int getArrowY();

    protected void addSlot(SlotBase slot) {
        table.addSlot(slot);
        slots.add(slot);
    }

    public int getId() {
        return id;
    }

    public abstract int createSlots(int id);

    public static final int PRODUCTION_TIME = 400;
    private int productionProgress;
    private int chargeCount;
    public static final int CHARGES_PER_LEVEL = 4;

    protected boolean canCharge() {
        return chargeCount < getMaxCharges();
    }

    private int getMaxCharges() {
        return table.getUpgradePage().getUpgradeCount(id, Upgrade.CHARGED) * CHARGES_PER_LEVEL;
    }

    private boolean isCharging() {
        if (canCharge()) {
            ItemStack result = getProductionResult();
            if (result == null) {
                return true;
            }else{
                ItemStack output = table.getStackInSlot(getOutputId());
                return !canMove(result, output);
            }
        }else{
            return false;
        }
    }

    private int getProductionSpeed(boolean charging) {
        int base = 1 + table.getUpgradePage().getUpgradeCount(id, Upgrade.SPEED);

        return charging ? base : base * 4;
    }

    private int getPowerConsumption(boolean charging) {
        int base = 1 + table.getUpgradePage().getUpgradeCount(id, Upgrade.SPEED) * 2;

        return charging ? base * 2 : base;
    }

    private void produce(ItemStack result, ItemStack output) {
        if (output == null) {
            table.setInventorySlotContents(getOutputId(), result.copy());
        }else{
            table.getStackInSlot(getOutputId()).stackSize += result.stackSize;
        }

        onProduction();
    }

    public void onUpdate() {
        if (!table.getWorldObj().isRemote) {
            boolean canCharge = false;
            boolean updatedProgress = false;
            boolean canReset = false;
            ItemStack result = getProductionResult();
            if (result != null) {
                boolean updatedCharge = false;
                boolean done;
                do {
                    done = true;
                    ItemStack output = table.getStackInSlot(getOutputId());
                    if (canMove(result, output)) {
                        if (chargeCount > 0) {
                            chargeCount--;
                            done = false;
                            updatedCharge = true;
                            produce(result, output);
                            result = getProductionResult();
                        }else {
                            int powerConsumption = getPowerConsumption(false);

                            if (table.getPower() >= powerConsumption) {
                                table.setPower(table.getPower() - powerConsumption);
                                productionProgress += getProductionSpeed(false);
                                if (productionProgress >= PRODUCTION_TIME) {
                                    productionProgress -= PRODUCTION_TIME;
                                    produce(result, output);
                                }
                                updatedProgress = true;
                            }
                        }
                    }else{
                        canCharge = true;
                    }
                }while (!done);

                if (updatedCharge) {
                    table.sendDataToAllPlayer(DataType.CHARGED, DataUnit.getId(this));
                }
            }else{
                canCharge = true;
                canReset = true;
            }

            if (canCharge && canCharge()) {
                int powerConsumption = getPowerConsumption(true);
                if (table.getPower() >= powerConsumption) {
                    table.setPower(table.getPower() - powerConsumption);
                    productionProgress += getProductionSpeed(true);
                    if (productionProgress >= PRODUCTION_TIME) {
                        productionProgress -= PRODUCTION_TIME;

                        chargeCount++;
                        table.sendDataToAllPlayer(DataType.CHARGED, DataUnit.getId(this));
                    }
                    updatedProgress = true;
                }
            }else if (canReset && productionProgress != 0){
                productionProgress = 0;
                updatedProgress = true;
            }

            if (updatedProgress) {
                table.sendDataToAllPlayer(DataType.PROGRESS, DataUnit.getId(this));
            }
        }
    }

    public int getChargeCount() {
        return chargeCount;
    }

    public void setChargeCount(int chargeCount) {
        this.chargeCount = chargeCount;
    }

    protected abstract ItemStack getProductionResult();
    protected abstract int getOutputId();
    protected abstract void onProduction();

    protected boolean canMove(ItemStack source, ItemStack target) {
        if (source != null) {
            if (target == null) {
                return true;
            }else if(target.isItemEqual(source) && ItemStack.areItemStackTagsEqual(target, source)){
                int resultSize = target.stackSize + source.stackSize;
                if (resultSize <= table.getInventoryStackLimit() && resultSize <= target.getMaxStackSize()) {
                    return true;
                }
            }
        }

        return false;
    }


    public int getProductionProgress() {
        return productionProgress;
    }

    public void setProductionProgress(int productionProgress) {
        this.productionProgress = productionProgress;
    }




    public abstract boolean isEnabled();

    //TODO make sure this is triggered when items enter through, for instance, hoppers. If it don't, trigger this from the tile entity rather than from the slot. (it probably don't work to be honest)
    //TODO figure out a way to make this trigger only once. For instance, using drag click can make this happen 9 times. When clicking normally it fires twice as well.
    public void onSlotChanged() {}

    private List<SlotBase> slots = new ArrayList<SlotBase>();
    public List<SlotBase> getSlots() {
        return slots;
    }
}
