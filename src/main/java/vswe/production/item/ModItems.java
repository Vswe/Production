package vswe.production.item;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class ModItems {
    public static ItemUpgrade upgrade;

    public static final String UNLOCALIZED_NAME = "production_table_upgrade";

    private static final String STONE = "stone";
    private static final String PLANKS = "plankWood";
    private static final String COBBLE = "cobblestone";
    private static final String LAPIS = "gemLapis";
    private static final String IRON = "ingotIron";
    private static final String REDSTONE = "dustRedstone";
    private static final String GLASS = "blockGlass";
    private static final String GLOW_STONE = "dustGlowstone";
    private static final String GOLD = "ingotGold";
    private static final String REDSTONE_BLOCK = "blockRedstone";

    public static void init() {
        upgrade = new ItemUpgrade();
        upgrade.setUnlocalizedName(UNLOCALIZED_NAME);
        GameRegistry.registerItem(upgrade, UNLOCALIZED_NAME);


        addRecipe(Upgrade.BLANK, "SP", "PS", 'S', STONE, 'P', PLANKS);
        addRecipe(Upgrade.STORAGE, "C", "U", 'C', Blocks.chest, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.AUTO_CRAFTER, "PPP", "CTC", "CUC", 'P', PLANKS, 'C', COBBLE, 'T', Blocks.piston, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.CHARGED, "IRI", "IUI", "IRI", 'I', IRON, 'R', REDSTONE, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.SPEED, "IRI", "LUL", "IRI", 'I', IRON, 'R', REDSTONE, 'L', LAPIS, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.QUEUE, "PPP", "IUI", "PPP", 'I', IRON, 'P', PLANKS, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.LAVA, "NFN", "NUN", "NNN", 'N', Blocks.netherrack, 'F', Blocks.furnace, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.SOLAR, "ICI", "IGI", "IUI", 'I', IRON, 'G', GLOW_STONE, 'C', GLASS, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.EFFICIENCY, "III", "FPF", "RUR", 'I', IRON, 'R', REDSTONE, 'F', Blocks.furnace, 'P', Blocks.piston, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.AUTO_TRANSFER, "GGG", "HUH", "GGG", 'G', GOLD, 'H', Blocks.hopper, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.FILTER, "III", "GBG", "IUI", 'G', Blocks.light_weighted_pressure_plate, 'I', IRON, 'B', Blocks.iron_bars, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.TRANSFER, "III", "GRG", "GUG", 'G', GOLD, 'I', IRON, 'R', REDSTONE_BLOCK, 'U', Upgrade.BLANK.getItemStack());

    }

    private static void addRecipe(Upgrade upgrade, Object ... recipe) {
        if (upgrade.isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(upgrade.getItemStack(), recipe));
        }
    }

    private ModItems() {}
}
