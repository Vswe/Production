package vswe.production.nei;


import vswe.production.page.unit.Unit;

/**
 * This interface exists so the rest of the mod can refer to a few nei call back actions without knowing anything
 * at all whether nei exists nor what it does.
 */
public interface INEICallback {
    void onArrowClick(Unit unit);
}
