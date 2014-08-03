package vswe.production.page;

import vswe.production.tileentity.TileEntityTable;


public class PageTransfer extends Page {
    public PageTransfer(TileEntityTable table, String name) {
        super(table, name);
    }

    @Override
    public int createSlots(int id) {
        return id;
    }
}
