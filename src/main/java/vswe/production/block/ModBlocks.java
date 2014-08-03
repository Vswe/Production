package vswe.production.block;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import vswe.production.creativetab.CreativeTabProduction;
import vswe.production.tileentity.TileEntityTable;

public final class ModBlocks {
    public static Block table;

    private static final String UNLOCALIZED_NAME = "production_table";
    private static final String LOCALIZED_NAME = "Production Table";

    public static void init() {
        CreativeTabProduction tab = new CreativeTabProduction();

        table = new BlockTable();
        GameRegistry.registerBlock(table, UNLOCALIZED_NAME);
        GameRegistry.registerTileEntity(TileEntityTable.class, UNLOCALIZED_NAME);
        LanguageRegistry.addName(table, LOCALIZED_NAME); //TODO

        tab.init(table);
    }

    private ModBlocks(){}
}
