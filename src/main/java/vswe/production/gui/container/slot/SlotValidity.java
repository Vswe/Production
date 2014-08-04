package vswe.production.gui.container.slot;


public enum  SlotValidity {
    BOTH(true, true),
    INPUT(true, false),
    OUTPUT(false, true),
    NONE(false, false);

    private boolean isInput;
    private boolean isOutput;

    SlotValidity(boolean isInput, boolean isOutput) {
        this.isInput = isInput;
        this.isOutput = isOutput;
    }

    public boolean isInput() {
        return isInput;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public static SlotValidity getValidity(boolean isSlotInput, boolean isSlotOutput) {
        if (isSlotInput && isSlotOutput) {
            return BOTH;
        }else if(isSlotInput) {
            return INPUT;
        }else if(isSlotOutput) {
            return OUTPUT;
        }else{
            return NONE;
        }
    }
}
