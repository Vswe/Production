package vswe.production.network;

public enum StandardCounts implements IBitCount {
    BOOLEAN(1),
    BYTE(8),
    SHORT(16),
    INTEGER(32),
    NBT_LENGTH(15),
    DEFAULT_STRING(31);

    private int bitCount;

    StandardCounts(int bitCount) {
        this.bitCount = bitCount;
    }

    @Override
    public int getBitCount() {
        return bitCount;
    }
}