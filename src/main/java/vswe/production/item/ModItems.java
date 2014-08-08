package vswe.production.item;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class ModItems {
    public static ItemUpgrade upgrade;

    private static final String UNLOCALIZED_NAME = "production_table_upgrade";
    private static final String UNKNOWN_UPGRADE = "Unknown upgrade";

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
        for (Upgrade upgrade : Upgrade.values()) {
            LanguageRegistry.addName(upgrade.getItemStack(), upgrade.getName());
        }
        LanguageRegistry.addName(Upgrade.getInvalidItemStack(), UNKNOWN_UPGRADE);


        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.BLANK.getItemStack(), "SP", "PS", 'S', STONE, 'P', PLANKS));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.STORAGE.getItemStack(), "C", "U", 'C', Blocks.chest, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.AUTO_CRAFTER.getItemStack(), "PPP", "CTC", "CUC", 'P', PLANKS, 'C', COBBLE, 'T', Blocks.piston, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.CHARGED.getItemStack(), "IRI", "IUI", "IRI", 'I', IRON, 'R', REDSTONE, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.SPEED.getItemStack(), "IRI", "LUL", "IRI", 'I', IRON, 'R', REDSTONE, 'L', LAPIS, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.QUEUE.getItemStack(), "PPP", "IUI", "PPP", 'I', IRON, 'P', PLANKS, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.LAVA.getItemStack(), "NFN", "NUN", "NNN", 'N', Blocks.netherrack, 'F', Blocks.furnace, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.SOLAR.getItemStack(), "ICI", "IGI", "IUI", 'I', IRON, 'G', GLOW_STONE, 'C', GLASS, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.EFFICIENCY.getItemStack(), "III", "FPF", "RUR", 'I', IRON, 'R', REDSTONE, 'F', Blocks.furnace, 'P', Blocks.piston, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.AUTO_TRANSFER.getItemStack(), "GGG", "HUH", "GGG", 'G', GOLD, 'H', Blocks.hopper, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.FILTER.getItemStack(), "III", "GBG", "IUI", 'G', Blocks.light_weighted_pressure_plate, 'I', IRON, 'B', Blocks.iron_bars, 'U', Upgrade.BLANK.getItemStack()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.TRANSFER.getItemStack(), "III", "GRG", "GUG", 'G', GOLD, 'I', IRON, 'R', REDSTONE_BLOCK, 'U', Upgrade.BLANK.getItemStack()));

    }



    private ModItems() {}
}
