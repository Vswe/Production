package vswe.production.nei;


import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import vswe.production.StevesProduction;
import vswe.production.gui.GuiTable;
import vswe.production.page.PageMain;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class NEIProductionConfig implements IConfigureNEI {
    @Override
    public void loadConfig() {
        OverlayWrapper overlay = new OverlayWrapper();
        API.registerGuiOverlay(GuiTable.class, "crafting", overlay);
        API.registerGuiOverlayHandler(GuiTable.class, overlay, "crafting");
    }

    @Override
    public String getName() {
        return StevesProduction.NAME;
    }

    @Override
    public String getVersion() {
        return StevesProduction.VERSION;
    }

    //TODO arrows with clickable recipes?
    //TODO furnaces?

    public static class OverlayWrapper implements IOverlayHandler, IStackPositioner {
        private GuiTable cachedInterface;

        private Overlay[] overlays;
        private OverlayWrapper() {
            overlays = new Overlay[4];
            for (int i = 0; i < overlays.length; i++) {
                int x = (i % 2) * PageMain.WIDTH / 2;
                int y = (i / 2) * PageMain.HEIGHT / 2;
                overlays[i] = new Overlay(x - 20, y - 1);
            }
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onOpen(GuiOpenEvent event) {
            GuiScreen gui = event.gui;
            if (gui instanceof GuiRecipe) {
                GuiContainer first = ((GuiRecipe) gui).getFirstScreen();
                if (first instanceof GuiTable) {
                    cachedInterface = (GuiTable)first;
                }
            }
        }


        @Override
        public ArrayList<PositionedStack> positionStacks(ArrayList<PositionedStack> items) {
            int id = getIndex(cachedInterface, items);
            if (id != -1) {
                int offsetX = overlays[id].getOffsetX();
                int offsetY = overlays[id].getOffsetY();

                for(PositionedStack stack : items) {
                    stack.relx += offsetX;
                    stack.rely += offsetY;
                }
            }


            return items;
        }

        private class Overlay extends DefaultOverlayHandler {
            private int offsetX;
            private int offsetY;
            private Overlay(int x, int y) {
                super(x, y);
                this.offsetX = x;
                this.offsetY = y;
            }

            public int getOffsetX() {
                return offsetX;
            }

            public int getOffsetY() {
                return offsetY;
            }
        }

        private int getIndex(GuiContainer gui, List<PositionedStack> items) {
            if (gui instanceof GuiTable) {
                TileEntityTable table = ((GuiTable)gui).getTable();
                List<UnitCrafting> craftingList = new ArrayList<UnitCrafting>();
                for (UnitCrafting crafting : table.getMainPage().getCraftingList()){
                    if (crafting.isEnabled() && !crafting.isWorking()) {
                        craftingList.add(crafting);
                    }
                }


                if (!items.isEmpty()) {
                    int offset = 18;
                    int minX = Integer.MAX_VALUE;
                    int minY = Integer.MAX_VALUE;
                    int maxX = Integer.MIN_VALUE;
                    int maxY = Integer.MIN_VALUE;
                    for (PositionedStack stack : items) {
                        minX = Math.min(minX, stack.relx);
                        minY = Math.min(minY, stack.rely);
                        maxX = Math.max(maxX, stack.relx);
                        maxY = Math.max(maxY, stack.rely);
                    }
                    int width = (maxX - minX) / offset + 1;
                    int height = (maxY - minY) / offset + 1;
                    PositionedStack[][] recipe = new PositionedStack[width][height];
                    for (PositionedStack stack : items) {
                        int x = (stack.relx - minX) / offset;
                        int y = (stack.rely - minY) / offset;
                        recipe[x][y] = stack;
                    }


                    for (UnitCrafting crafting : craftingList) {
                        for (int startX = 0; startX <= 3 - width; startX++) {
                            for (int startY = 0; startY <= 3 - height; startY++) {
                                boolean isMatch = true;
                                boolean isEmpty = true;
                                for (int offsetX = 0; offsetX < 3; offsetX++) {
                                    for (int offsetY = 0; offsetY < 3; offsetY++) {
                                        int slotId = offsetX + offsetY * 3;
                                        int recipeX = offsetX - startX;
                                        ItemStack itemStack = table.getStackInSlot(crafting.getGridId() + slotId);
                                        int recipeY = offsetY - startY;
                                        if (recipeX >= 0 && recipeX < width && recipeY >= 0 && recipeY < height) {
                                            if (itemStack != null) {
                                                PositionedStack positionedStack = recipe[recipeX][recipeY];
                                                if (positionedStack == null || !positionedStack.contains(itemStack)) {
                                                    isMatch = false;
                                                    break;
                                                }else{
                                                    isEmpty = false;
                                                }
                                            }
                                        }else if (itemStack != null) {
                                            isMatch = false;
                                            break;
                                        }


                                    }

                                    if (!isMatch) break;
                                }
                                if (isMatch && !isEmpty) {
                                    return crafting.getId();
                                }
                            }
                        }
                    }
                }


                for (UnitCrafting crafting : craftingList) {
                    boolean isEmpty = true;
                    for (int i = 0; i < UnitCrafting.GRID_SIZE; i++) {
                        if (table.getStackInSlot(crafting.getGridId() + i) != null) {
                            isEmpty = false;
                            break;
                        }
                    }

                    if (isEmpty) {
                        return crafting.getId();
                    }
                }

                if (!craftingList.isEmpty()){
                    return craftingList.get(0).getId();
                }
            }else{
                System.err.println("Interface is not a GuiTable");
            }

            return -1;
        }

        @Override
        public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {

            int id = getIndex(gui, recipe.getIngredientStacks(recipeIndex));
            if (id != -1) {
                overlays[id].overlayRecipe(gui, recipe, recipeIndex, shift);
            }
        }
    }
}
