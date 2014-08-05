package vswe.production.page.setting;


public class Transfer {
    private boolean enabled;
    private boolean isInput;
    private boolean auto; //TODO doesn't currently do anything nor is synced between client and server

    public Transfer(boolean isInput) {
        this.isInput = isInput;
    }

    public boolean isInput() {
        return isInput;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}
