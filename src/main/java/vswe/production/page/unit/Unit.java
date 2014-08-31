package vswe.production.page.unit;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import vswe.production.StevesProduction;
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
        int progress = Math.min(productionProgress, PRODUCTION_TIME);
        gui.drawRect(this.x + x, this.y + y + PROGRESS_OFFSET, ARROW_SRC_X, ARROW_SRC_Y + ARROW_HEIGHT, progress * ARROW_WIDTH / PRODUCTION_TIME, ARROW_HEIGHT);
        GL11.glDisable(GL11.GL_BLEND);

        if ((StevesProduction.nei != null || charging) && gui.inBounds(this.x + x, this.y + y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY)) {
            if (!charging) {
                gui.drawMouseOver("Recipes");
            }else{
                List<String> str = new ArrayList<String>();
                str.add(EnumChatFormatting.GREEN + (chargeCount < max ? "Charging" : "Fully Charged"));
                str.add("Charges: " + chargeCount + "/" + max);
                str.add(EnumChatFormatting.GRAY + "Charges can be consumed to instantly produce an item");

                if (StevesProduction.nei != null) {
                    str.add("");
                    str.add("Click for Recipes");
                }

                gui.drawMouseOver(str);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClick(GuiBase gui, int mX, int mY) {
        if (StevesProduction.nei != null &&gui.inBounds(this.x + getArrowX(), this.y + getArrowY(), ARROW_WIDTH, ARROW_HEIGHT, mX, mY)) {
            StevesProduction.nei.onArrowClick(this);
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

        onProduction(result);
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
                        if (chargeCount > 0 && getMaxCharges() > 0) {
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
                                while (productionProgress >= PRODUCTION_TIME) {
                                    productionProgress -= PRODUCTION_TIME;
                                    produce(result, output);
                                    result = getProductionResult();
                                    output = table.getStackInSlot(getOutputId());
                                    if (!canMove(result, output)) {
                                        break;
                                    }
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
                boolean done = false;
                while (canCharge() && !done) {
                    done = true;
                    int powerConsumption = getPowerConsumption(true);
                    if (table.getPower() >= powerConsumption) {
                        table.setPower(table.getPower() - powerConsumption);
                        productionProgress += getProductionSpeed(true);
                        if (productionProgress >= PRODUCTION_TIME) {
                            productionProgress -= PRODUCTION_TIME;

                            chargeCount++;
                            table.sendDataToAllPlayer(DataType.CHARGED, DataUnit.getId(this));
                            done = false;
                        }
                        updatedProgress = true;
                    }
                }
            }else if (canReset && productionProgress != 0){
                productionProgress = 0;
                updatedProgress = true;
            }

            if (updatedProgress) {
                workingTicks = WORKING_COOLDOWN;
                table.sendDataToAllPlayer(DataType.PROGRESS, DataUnit.getId(this));
            }else if(workingTicks > 0) {
                workingTicks--;
            }
        }else if(workingTicks > 0) {
            workingTicks--;
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
    protected abstract void onProduction(ItemStack result);

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
        workingTicks = WORKING_COOLDOWN;
    }




    public abstract boolean isEnabled();


    private List<SlotBase> slots = new ArrayList<SlotBase>();
    public List<SlotBase> getSlots() {
        return slots;
    }

    private static final String NBT_CHARGED = "Charged";
    private static final String NBT_PROGRESS = "Progress";
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(NBT_CHARGED, (byte)chargeCount);
        compound.setShort(NBT_PROGRESS, (short)productionProgress);
    }

    public void readFromNBT(NBTTagCompound compound) {
        chargeCount = compound.getByte(NBT_CHARGED);
        productionProgress = compound.getShort(NBT_PROGRESS);
    }

    private static final int WORKING_COOLDOWN = 20;
    private int workingTicks;
    public boolean isWorking() {
        return workingTicks > 0;
    }


}
