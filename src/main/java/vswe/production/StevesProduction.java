package vswe.production;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.DefaultOverlayHandler;

import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.RecipeInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import vswe.production.block.ModBlocks;
import vswe.production.config.ConfigLoader;
import vswe.production.creativetab.CreativeTabProduction;
import vswe.production.gui.GuiHandler;
import vswe.production.gui.GuiTable;
import vswe.production.item.ModItems;
import vswe.production.item.Upgrade;
import vswe.production.network.PacketHandler;
import vswe.production.page.PageMain;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.List;


@Mod(modid = "StevesWorkshop", name = StevesProduction.NAME, version = StevesProduction.VERSION)
public class StevesProduction {
    public static final String CHANNEL = "SWorkshop";
    public static final String NAME = "Steve's Workshop";
    public static final String VERSION = "0.3.0";

    public static FMLEventChannel packetHandler;

    @Mod.Instance("StevesWorkshop")
    public static StevesProduction instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigLoader.init(event.getSuggestedConfigurationFile());
        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);
        new CreativeTabProduction();
        ModItems.init();
        ModBlocks.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        packetHandler.register(new PacketHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

}
