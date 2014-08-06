package vswe.production.gui.menu;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.production.gui.GuiBase;
import vswe.production.gui.component.Button;
import vswe.production.tileentity.TileEntityTable;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiMenu {

    protected List<Button> buttons;
    protected TileEntityTable table;

    public GuiMenu(TileEntityTable table) {
        this.table = table;
        buttons = new ArrayList<Button>();
        buttons.add(new Button("Save", 150, 230) {
            @Override
            public void clicked() {
                save();
                close();
            }
        });

        buttons.add(new Button("Cancel", 200, 230) {
            @Override
            public void clicked() {
                close();
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY) {
        for (Button button : buttons) {
            button.draw(gui, mX, mY);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClick(GuiBase gui, int mX, int mY) {
        for (Button button : buttons) {
            button.onClick(gui, mX, mY);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onRelease(GuiBase gui, int mX, int mY) {

    }

    protected abstract void save();

    protected void close() {
        table.setMenu(null);
    }
}
