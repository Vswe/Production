package vswe.production.network;


public enum PacketId {
    ALL(true),
    TYPE(true),
    CLOSE(false),
    UPGRADE_CHANGE(true);

    private boolean inInterface;

    PacketId(boolean inInterface) {
        this.inInterface = inInterface;
    }

    public boolean isInInterface() {
        return inInterface;
    }
}
