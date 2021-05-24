package gregtech.api.util;

public class GT_ClientPreference {
    private final boolean mSingleBlockInitialFilter;

    public GT_ClientPreference(boolean mSingleBlockInitialFilter) {
        this.mSingleBlockInitialFilter = mSingleBlockInitialFilter;
    }

    public GT_ClientPreference(GT_Config aClientDataFile) {
        this.mSingleBlockInitialFilter = aClientDataFile.get("preference", "mSingleBlockInitialFilter", false);
    }

    public boolean isSingleBlockInitialFilterEnabled() {
        return mSingleBlockInitialFilter;
    }
}
