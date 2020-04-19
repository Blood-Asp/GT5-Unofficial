package gregtech.api.util;

import cpw.mods.fml.common.ProgressManager;
import gregtech.GT_Mod;
import gregtech.api.enums.Materials;
import gregtech.common.GT_Proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("rawtypes, unchecked, deprecation")
public class GT_CLS_Compat {

    private static Class alexiilMinecraftDisplayer;
    private static Class alexiilProgressDisplayer;

    private static Method getLastPercent;
    private static Method displayProgress;

    private static Field isReplacingVanillaMaterials;
    private static Field isNice;
    private static Field isRegisteringGTmaterials;

    static {
        //CLS
        try {
            alexiilMinecraftDisplayer = Class.forName("alexiil.mods.load.MinecraftDisplayer");
            alexiilProgressDisplayer = Class.forName("alexiil.mods.load.ProgressDisplayer");
        } catch (ClassNotFoundException ex) {
            GT_Mod.GT_FML_LOGGER.catching(ex);
        }

        Optional.ofNullable(alexiilMinecraftDisplayer).ifPresent(e -> {
            try {
                getLastPercent = e.getMethod("getLastPercent");
                isReplacingVanillaMaterials = e.getField("isReplacingVanillaMaterials");
                isRegisteringGTmaterials = e.getField("isRegisteringGTmaterials");
                isNice = e.getField("isNice");
            } catch (NoSuchMethodException | NoSuchFieldException ex) {
                GT_Mod.GT_FML_LOGGER.catching(ex);
            }

        });

        Optional.ofNullable(alexiilProgressDisplayer).ifPresent(e -> {
            try {
                displayProgress = e.getMethod("displayProgress", String.class, float.class);
            } catch (NoSuchMethodException ex) {
                GT_Mod.GT_FML_LOGGER.catching(ex);
            }
        });
    }

    public static void stepMaterialsCLS(Collection<GT_Proxy.OreDictEventContainer> mEvents, ProgressManager.ProgressBar progressBar) throws IllegalAccessException, InvocationTargetException {
        int sizeStep = GT_CLS_Compat.setStepSize(mEvents);
        int size = 0;
        GT_Proxy.OreDictEventContainer tEvent;
        boolean hasSetNice = false;

        for (Iterator<GT_Proxy.OreDictEventContainer> i$ = mEvents.iterator(); i$.hasNext(); GT_Proxy.registerRecipes(tEvent)) {
            tEvent = i$.next();
            sizeStep--;

            String materialName = tEvent.mMaterial == null ? "" : tEvent.mMaterial.toString();

            displayProgress.invoke(null, materialName, ((float) size) / 100);

            if (size == 70 && !hasSetNice) {
                hasSetNice = true;
                isNice.set(null, true);
            } else if (size != 70) {
                isNice.set(null, false);
            }

            if (sizeStep == 0) {
                GT_Mod.GT_FML_LOGGER.info("Baking : " + size + "%", new Object[0]);
                sizeStep = mEvents.size() / 100 - 1;
                size++;
            }

            progressBar.step(materialName);
        }
        ProgressManager.pop(progressBar);
        isRegisteringGTmaterials.set(null, false);
    }


    public static int setStepSize(Collection mEvents) {
        try {
            isRegisteringGTmaterials.set(null, true);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            GT_Mod.GT_FML_LOGGER.catching(e);
        }

        return mEvents.size() / 100 - 1;
    }

    private GT_CLS_Compat() {
    }

    private static int[] setSizeSteps(Set<Materials> replaceVanillaItemsSet){
        int sizeStep;
        int sizeStep2;
        if (replaceVanillaItemsSet.size() >= 100) {
            sizeStep = replaceVanillaItemsSet.size() / 100 - 1;
            sizeStep2 = 1;
        } else {
            sizeStep = 100 / replaceVanillaItemsSet.size();
            sizeStep2 = sizeStep;
        }
        return new int[]{sizeStep, sizeStep2};
    }

    private static void displayMethodAdapter(int counter, String mDefaultLocalName, int size) throws InvocationTargetException, IllegalAccessException {
        if (counter == 1) {
            displayProgress.invoke(null, mDefaultLocalName, ((float) 95) / 100);
        } else if (counter == 0) {
            displayProgress.invoke(null, mDefaultLocalName, (float) 1);
        } else {
            displayProgress.invoke(null, mDefaultLocalName, ((float) size) / 100);
        }
    }

    public static void doActualRegistrationCLS(ProgressManager.ProgressBar progressBar, Set<Materials> replaceVanillaItemsSet) throws InvocationTargetException, IllegalAccessException {
        int size = 0;
        int counter = replaceVanillaItemsSet.size();
        try {
            isReplacingVanillaMaterials.set(null, true);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            GT_Mod.GT_FML_LOGGER.catching(e);
        }

        int[] sizeSteps = setSizeSteps(replaceVanillaItemsSet);

        for (Materials m : replaceVanillaItemsSet) {
            counter--;
            sizeSteps[0]--;

            displayMethodAdapter(counter,m.mDefaultLocalName,size);
            GT_Mod.doActualRegistration(m);

            size += sizeSteps[1];
            progressBar.step(m.mDefaultLocalName);
        }
    }

    public static void pushToDisplayProgress() throws InvocationTargetException, IllegalAccessException {
        isReplacingVanillaMaterials.set(null, false);
        displayProgress.invoke(null, "Post Initialization: loading GregTech", getLastPercent.invoke(null));
    }

}
