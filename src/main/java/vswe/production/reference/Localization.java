package vswe.production.reference;

import net.minecraft.util.StatCollector;
import vswe.production.item.ModItems;

public final class Localization {
    public static final String PREFIX = "steves_production:";
    
    public static final String UPGRADE_BASE = PREFIX + "item." + ModItems.UNLOCALIZED_NAME;
    public static final String UPGRADE_NO_STACK = UPGRADE_BASE + ".noStack";
    public static final String UPGRADE_STACK_WELL = UPGRADE_BASE + ".stackWell";
    public static final String UPGRADE_DISABLED = UPGRADE_BASE + ".disabled";
    public static final String ITEM_INVALID = PREFIX + "item.invalid.desc";
    
    public static final String TABLE_NAME = "production_table";
    
    public static String translate(String key) {
        return StatCollector.translateToLocal(key);
    }
}