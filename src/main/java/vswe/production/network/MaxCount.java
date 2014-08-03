package vswe.production.network;


public class MaxCount implements IBitCount {
    private int bits;

    public MaxCount(int max) {
        bits = (int)(Math.log10(max + 1) / Math.log10(2)) + 1;
    }

    @Override
    public int getBitCount() {
        return bits;
    }
}
