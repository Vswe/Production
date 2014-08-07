package vswe.production.item;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;

public final class ModItems {
    public static ItemUpgrade upgrade;

    private static final String UNLOCALIZED_NAME = "production_table_upgrade";
    private static final String UNKNOWN_UPGRADE = "Unknown upgrade";

    public static void init() {
        upgrade = new ItemUpgrade();
        upgrade.setUnlocalizedName(UNLOCALIZED_NAME);
        GameRegistry.registerItem(upgrade, UNLOCALIZED_NAME);
        for (Upgrade upgrade : Upgrade.values()) {
            LanguageRegistry.addName(upgrade.getItemStack(), upgrade.getName());
        }
        LanguageRegistry.addName(Upgrade.getInvalidItemStack(), UNKNOWN_UPGRADE);
    }

    private ModItems() {}
}
