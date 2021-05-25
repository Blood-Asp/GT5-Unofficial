package gregtech.api.util;

public class GT_ClientPreference {
    private final boolean mSingleBlockInitialFilter;
    private final boolean mInputBusInitialFilter;

    public GT_ClientPreference(boolean mSingleBlockInitialFilter, boolean mInputBusInitialFilter) {
        this.mSingleBlockInitialFilter = mSingleBlockInitialFilter;
        this.mInputBusInitialFilter = mInputBusInitialFilter;
    }

    public GT_ClientPreference(GT_Config aClientDataFile) {
        this.mSingleBlockInitialFilter = aClientDataFile.get("preference", "mSingleBlockInitialFilter", false);
        this.mInputBusInitialFilter = aClientDataFile.get("preference", "mInputBusInitialFilter", true);
    }

    public boolean isSingleBlockInitialFilterEnabled() {
        return mSingleBlockInitialFilter;
    }

    public boolean isInputBusInitialFilterEnabled() {
        return mInputBusInitialFilter;
    }
}
