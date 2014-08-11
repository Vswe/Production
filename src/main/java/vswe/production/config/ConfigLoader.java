package vswe.production.config;


import net.minecraftforge.common.config.Configuration;
import vswe.production.item.Upgrade;

import java.io.File;

public final class ConfigLoader {

    private static final String UPGRADES = "Upgrades";
    private static final String MAX_COUNT_SUFFIX = ".max_count";


    public static void init(File file) {
        Configuration config = new Configuration(file);
        config.load();


        for (Upgrade upgrade : Upgrade.values()) {
            Upgrade.MaxCount max = upgrade.getMaxCountObject();
            if (max.getConfigurableMax() > 0) {
                upgrade.getMaxCountObject().setMax(config.getInt(upgrade.getUnlocalizedName() + MAX_COUNT_SUFFIX, UPGRADES, max.getMax(), 0, max.getConfigurableMax(), "Max count of the " + upgrade.getName() + " upgrade"));
            }
        }


        config.save();
    }

    private ConfigLoader() {}
}
