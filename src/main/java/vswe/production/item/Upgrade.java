package vswe.production.item;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import vswe.production.reference.Localization;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public enum Upgrade {
    BLANK(new MaxCount(0), (ParentType)null),
    AUTO_CRAFTER(new MaxCount(1), ParentType.CRAFTING),
    STORAGE(new MaxCount(1), ParentType.CRAFTING),
    CHARGED(new ConfigurableMax(8)),
    SPEED(new ConfigurableMax(8)),
    QUEUE(new MaxCount(3), ParentType.SMELTING),
    EFFICIENCY(new ConfigurableMax(4), ParentType.GLOBAL),
    LAVA(new MaxCount(1), ParentType.GLOBAL),
    SOLAR(new ConfigurableMax(1),  ParentType.GLOBAL),
    AUTO_TRANSFER(new MaxCount(1), ParentType.GLOBAL),
    FILTER(new MaxCount(1),  ParentType.GLOBAL),
    TRANSFER(new ConfigurableMax(6, 20), ParentType.GLOBAL);

    //PATTERN("Pattern Crafting", "Remembers old recipes", 4, ParentType.CRAFTING), //TODO
    //RESTOCK("Restock Control", "Only produce more items when there isn't enough of them", 1), //TODO

    private final String unlocalizedName;
    private final String description;
    private final MaxCount maxCount;
    private final EnumSet<ParentType> validParents;


    Upgrade(MaxCount maxCount, EnumSet<ParentType> validParents) {
        this.validParents = validParents;
        this.unlocalizedName = toString().toLowerCase(Locale.ROOT);
        this.description = Localization.UPGRADE_BASE + "." + unlocalizedName + ".desc";
        this.maxCount = maxCount;
        maxCount.init(this);
    }

    Upgrade(MaxCount maxCount, ParentType type) {
        this(description, maxCount, type == null ? EnumSet.noneOf(ParentType.class) : EnumSet.of(type));
    }

    Upgrade(MaxCount maxCount) {
        this(description, maxCount, EnumSet.of(ParentType.CRAFTING, ParentType.SMELTING));
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }


    public boolean isEnabled() {
        return maxCount.getConfigurableMax() == 0 || maxCount.getMax() > 0;
    }

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcon(IIconRegister register) {
        icon = register.registerIcon("production:" + unlocalizedName);
    }


    public ItemStack getItemStack() {
        return new ItemStack(ModItems.upgrade, 1, ordinal());
    }

    public static ItemStack getInvalidItemStack() {
        return new ItemStack(ModItems.upgrade, 1, values().length);
    }

    public void addInfo(List<String> info) {
        info.add(EnumChatFormatting.GRAY + Localization.translate(description));
        if (GuiScreen.isShiftKeyDown()) {
            if (getMaxCount() == 1) {
                info.add(EnumChatFormatting.YELLOW + Localization.translate(Localization.UPGRADE_NO_STACK));
            }else if (getMaxCount() > 1) {
                info.add(EnumChatFormatting.YELLOW + String.format(Localization.translate(Localization.UPGRADE_STACK_WELL, getMaxCount())));
            }else if(!isEnabled()) {
                info.add(EnumChatFormatting.DARK_RED + Localization.translate(Localization.UPGRADE_DISABLED));
            }

            for (ParentType validParent : validParents) {
                info.add(EnumChatFormatting.GOLD + Localization.translate(validParent.description));
            }
        }
    }

    public boolean isValid(ItemStack parent) {
        for (ParentType validParent : validParents) {
            if (validParent.isValidParent(parent)) {
                return true;
            }
        }

        return false;
    }

    public int getMaxCount() {
        return maxCount.getMax();
    }

    public MaxCount getMaxCountObject() {
        return maxCount;
    }

    public enum ParentType {
        CRAFTING() {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item != null && Item.getItemFromBlock(Blocks.crafting_table).equals(item.getItem());
            }
        },
        SMELTING() {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item != null && Item.getItemFromBlock(Blocks.furnace).equals(item.getItem());
            }
        },
        GLOBAL() {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item == null;
            }
        };

        private final String description;

        ParentType() {
            this.description = Localization.UPGRADE_BASE + ".parent." + toString().toLowerCase(Locale.ROOT);
        }

        protected abstract boolean isValidParent(ItemStack item);
    }

    public static class MaxCount {
        private int max;
        private int defaultMax;

        public MaxCount(int max) {
            this.max = max;
            this.defaultMax = max;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int value) {
            this.max = value;
        }

        public int getConfigurableMax() {
            return defaultMax;
        }

        public void init(Upgrade upgrade) {

        }
    }

    private static class ConfigurableMax extends MaxCount {
        private boolean isGlobal;
        private int configurableMax;
        private ConfigurableMax(int max, int configurableMax) {
            super(max);
            this.configurableMax = configurableMax;
        }
        private ConfigurableMax(int max) {
            this(max, -1);
        }

        private static final int GLOBAL_MAX_COUNT = 8 * 64;
        private static final int MAX_COUNT = 7 * 64;
        @Override
        public int getConfigurableMax() {
            return configurableMax != -1 ? configurableMax : isGlobal ? GLOBAL_MAX_COUNT : MAX_COUNT;
        }

        @Override
        public void init(Upgrade upgrade) {
            isGlobal = upgrade.validParents.contains(ParentType.GLOBAL);
        }
    }
 }
