package vswe.production.nei;


import codechicken.nei.recipe.GuiCraftingRecipe;
import vswe.production.page.unit.Unit;
import vswe.production.page.unit.UnitCrafting;
import vswe.production.page.unit.UnitSmelting;

public class NEICallback implements INEICallback {

    @Override
    public void onArrowClick(Unit unit) {
        if (unit instanceof UnitCrafting) {
            GuiCraftingRecipe.openRecipeGui("crafting");
        }else if(unit instanceof UnitSmelting) {
            GuiCraftingRecipe.openRecipeGui("smelting");
        }
    }
}
