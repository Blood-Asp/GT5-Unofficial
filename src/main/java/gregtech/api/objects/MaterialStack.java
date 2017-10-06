package gregtech.api.objects;

import gregtech.api.enums.Materials;

public class MaterialStack implements Cloneable {
    public long mAmount;
    public Materials mMaterial;

    public MaterialStack(Materials aMaterial, long aAmount) {
        mMaterial = aMaterial == null ? Materials._NULL : aMaterial;
        mAmount = aAmount;
    }

    public MaterialStack copy(long aAmount) {
        return new MaterialStack(mMaterial, aAmount);
    }

    @Override
    public MaterialStack clone() {
        try { return (MaterialStack) super.clone(); } catch (Exception e) { return new MaterialStack(mMaterial, mAmount); }
    }

    @Override
    public boolean equals(Object aObject) {
        if (aObject == this) return true;
        if (aObject == null) return false;
        if (aObject instanceof Materials) return aObject == mMaterial;
        if (aObject instanceof MaterialStack)
            return ((MaterialStack) aObject).mMaterial == mMaterial && (mAmount < 0 || ((MaterialStack) aObject).mAmount < 0 || ((MaterialStack) aObject).mAmount == mAmount);
        return false;
    }

    @Override
	//Done sub numbering up to 50. couldn't find a better way to do this
    public String toString() {
         String temp1 = "", temp2 = mMaterial.getToolTip(true), temp3 = "", temp4 = "";
         if (mAmount == 2) {
             temp4 = "\u2082";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
         if (mAmount == 3) {
             temp4 = "\u2083";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
         if (mAmount == 4) {
             temp4 = "\u2084";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
         if (mAmount == 5) {
             temp4 = "\u2085";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
         if (mAmount == 6) {
             temp4 = "\u2086";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
         if (mAmount == 7) {
             temp4 = "\u2087";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
         if (mAmount == 8) {
             temp4 = "\u2088";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }	
		 if (mAmount == 9) {
             temp4 = "\u2089";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }	
		 if (mAmount == 10) {
             temp4 = "\u2081"+"\u2080";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 11) {
             temp4 = "\u2081"+"\u2081";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 12) {
             temp4 = "\u2081"+"\u2082";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 13) {
             temp4 = "\u2081"+"\u2083";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 14) {
             temp4 = "\u2081"+"\u2084";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 15) {
             temp4 = "\u2081"+"\u2085";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 16) {
             temp4 = "\u2081"+"\u2086";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 17) {
             temp4 = "\u2081"+"\u2087";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 18) {
             temp4 = "\u2081"+"\u2088";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 19) {
             temp4 = "\u2081"+"\u2089";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 20) {
             temp4 = "\u2082"+"\u2080";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 21) {
             temp4 = "\u2082"+"\u2081";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 22) {
             temp4 = "\u2082"+"\u2082";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 23) {
             temp4 = "\u2082"+"\u2083";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 24) {
             temp4 = "\u2082"+"\u2084";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 25) {
             temp4 = "\u2082"+"\u2085";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 26) {
             temp4 = "\u2082"+"\u2086";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 27) {
             temp4 = "\u2082"+"\u2087";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 28) {
             temp4 = "\u2082"+"\u2088";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 29) {
             temp4 = "\u2082"+"\u2089";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 30) {
             temp4 = "\u2083"+"\u2080";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 31) {
             temp4 = "\u2083"+"\u2081";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 32) {
             temp4 = "\u2083"+"\u2082";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 33) {
             temp4 = "\u2083"+"\u2083";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 34) {
             temp4 = "\u2083"+"\u2084";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 35) {
             temp4 = "\u2083"+"\u2085";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 36) {
             temp4 = "\u2083"+"\u2086";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 37) {
             temp4 = "\u2083"+"\u2087";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 38) {
             temp4 = "\u2083"+"\u2088";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 39) {
             temp4 = "\u2083"+"\u2089";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 40) {
             temp4 = "\u2084"+"\u2080";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 41) {
             temp4 = "\u2084"+"\u2081";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 42) {
             temp4 = "\u2084"+"\u2082";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 43) {
             temp4 = "\u2084"+"\u2083";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 44) {
             temp4 = "\u2084"+"\u2084";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 45) {
             temp4 = "\u2084"+"\u2085";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 46) {
             temp4 = "\u2084"+"\u2086";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 47) {
             temp4 = "\u2084"+"\u2087";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 48) {
             temp4 = "\u2084"+"\u2088";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 49) {
             temp4 = "\u2084"+"\u2089";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }
		 if (mAmount == 50) {
             temp4 = "\u2085"+"\u2080";
             
             if (mMaterial.mMaterialList.size() > 1 || isMaterialListComplex(this)) {
                temp1 = "(";
                temp3 = ")";
             }
         }

        return String.valueOf(new StringBuilder().append(temp1).append(temp2).append(temp3).append(temp4));
    }

    private boolean isMaterialListComplex(MaterialStack materialStack){
    	if (materialStack.mMaterial.mMaterialList.size() > 1) {
    		return true;
    	}
    	if (materialStack.mMaterial.mMaterialList.size() == 0) {
    		return false;
    	}
    	return isMaterialListComplex(materialStack.mMaterial.mMaterialList.get(0));
    }
    
    @Override
    public int hashCode() {
        return mMaterial.hashCode();
    }
}
