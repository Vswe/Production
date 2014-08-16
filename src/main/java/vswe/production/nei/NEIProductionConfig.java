package vswe.production.nei;


import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import vswe.production.StevesProduction;
import vswe.production.gui.GuiTable;

@SuppressWarnings("UnusedDeclaration")
public class NEIProductionConfig implements IConfigureNEI {
    @Override
    public void loadConfig() {
        OverlayWrapper overlay = new OverlayWrapper();
        API.registerGuiOverlay(GuiTable.class, "crafting", overlay);
        API.registerGuiOverlayHandler(GuiTable.class, overlay, "crafting");
        StevesProduction.nei = new NEICallback();
    }

    @Override
    public String getName() {
        return StevesProduction.NAME;
    }

    @Override
    public String getVersion() {
        return StevesProduction.VERSION;
    }
}
