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

import java.util.EnumSet;
import java.util.List;

public enum Upgrade {
    BLANK("Blank Upgrade", "Crafting component", new MaxCount(0), (ParentType)null),
    AUTO_CRAFTER("Auto Crafter", "Convert a crafting table into an auto crafting table", new MaxCount(1), ParentType.CRAFTING),
    STORAGE("Extra Storage", "Adds extra storage", new MaxCount(1), ParentType.CRAFTING),
    CHARGED("Charger", "Let idle components charge up for later", new ConfigurableMax(8)),
    SPEED("Production Speed", "Increase the production speed", new ConfigurableMax(8)),
    QUEUE("Input Queue", "Adds an input queue", new MaxCount(3), ParentType.SMELTING),
    EFFICIENCY("Fuel Efficiency", "Improves the fuel efficiency of solid fuel types", new ConfigurableMax(4), ParentType.GLOBAL),
    LAVA("Lava Generator", "Allows lava to be used as fuel", new MaxCount(1), ParentType.GLOBAL),
    SOLAR("Solar Generator", "Allows the table to be charged by solar power", new ConfigurableMax(1),  ParentType.GLOBAL),
    AUTO_TRANSFER("Auto Transfer", "Enables auto transfer to and from the table", new MaxCount(1), ParentType.GLOBAL),
    FILTER("Filter", "Enables transfer filters", new MaxCount(1),  ParentType.GLOBAL),
    TRANSFER("Transfer Capacity", "Increases the automatic transfer capacity", new ConfigurableMax(6, 20), ParentType.GLOBAL);

    //PATTERN("Pattern Crafting", "Remembers old recipes", 4, ParentType.CRAFTING), //TODO
    //RESTOCK("Restock Control", "Only produce more items when there isn't enough of them", 1), //TODO

    private String unlocalizedName;
    private String name;
    private String description;
    private MaxCount maxCount;
    private EnumSet<ParentType> validParents;


    Upgrade(String name, String description, MaxCount maxCount, EnumSet<ParentType> validParents) {
        this.name = name;
        this.validParents = validParents;
        this.unlocalizedName = toString().toLowerCase();
        this.description = description;
        this.maxCount = maxCount;
        maxCount.init(this);
    }

    Upgrade(String name, String description, MaxCount maxCount, ParentType type) {
        this(name, description, maxCount, type == null ? EnumSet.noneOf(ParentType.class) : EnumSet.of(type));
    }

    Upgrade(String name, String description, MaxCount maxCount) {
        this(name, description, maxCount, EnumSet.of(ParentType.CRAFTING, ParentType.SMELTING));
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }


    public boolean isEnabled() {
        return maxCount.getConfigurableMax() == 0 || maxCount.getMax() > 0;
    }

    public String getName() {
        return name;
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
        info.add(EnumChatFormatting.GRAY + description);
        if (GuiScreen.isShiftKeyDown()) {
            if (getMaxCount() == 1) {
                info.add(EnumChatFormatting.YELLOW + "Doesn't stack well");
            }else if (getMaxCount() > 1) {
                info.add(EnumChatFormatting.YELLOW + "Stacks well up to " + getMaxCount() + " items");
            }else if(!isEnabled()) {
                info.add(EnumChatFormatting.DARK_RED + "This item is disabled");
            }

            for (ParentType validParent : validParents) {
                info.add(EnumChatFormatting.GOLD + validParent.description);
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
        CRAFTING("Works with Crafting Tables") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item != null && Item.getItemFromBlock(Blocks.crafting_table).equals(item.getItem());
            }
        },
        SMELTING("Works with Furnaces") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item != null && Item.getItemFromBlock(Blocks.furnace).equals(item.getItem());
            }
        },
        GLOBAL("Upgrades the entire Production Table") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item == null;
            }
        };

        private String description;

        ParentType(String description) {
            this.description = description;
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
