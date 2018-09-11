package gregtech.loaders.misc;

import cpw.mods.fml.common.Loader;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.core.genetics.alleles.Allele;
import gregtech.GT_Mod;
import gregtech.common.items.ItemComb;

public class GT_Bees {

    static IAlleleInteger noFertility;
    static IAlleleInteger superFertility;

    static IAlleleInteger noFlowering;
    static IAlleleInteger superFlowering;

    static IAlleleArea noTerritory;
    static IAlleleArea superTerritory;

    static IAlleleFloat noWork;
    static IAlleleFloat speedBlinding;
    static IAlleleFloat superSpeed;

    public static ItemComb combs;

    public GT_Bees() {
        if (Loader.isModLoaded("Forestry") && GT_Mod.gregtechproxy.mGTBees) {

            combs = new ItemComb();
            combs.initCombsRecipes();
            GT_BeeDefinition.initBees();
        }
    }

    private static void setupGTAlleles(){

        noFertility =  new AlleleInteger("fertilitySterile", 0, false, EnumBeeChromosome.FERTILITY);
        superFertility = new AlleleInteger("fertilityMultiply", 8, false, EnumBeeChromosome.FERTILITY);

        noFlowering = new AlleleInteger("floweringNonpollinating", 0, false, EnumBeeChromosome.FLOWERING);
        superFlowering = new AlleleInteger("floweringNaturalistic", 240, false, EnumBeeChromosome.FLOWERING);

        noTerritory = new AlleleArea("areaLethargic", 1, 1, false);
        superTerritory = new AlleleArea("areaExploratory", 32, 16, false);

        noWork = new AlleleFloat("speedUnproductive", 0, false);
        speedBlinding = (IAlleleFloat) AlleleManager.alleleRegistry.getAllele("magicbees.speedBlinding");
        superSpeed = new AlleleFloat("speedAccelerated", 4F, false);


    }

    private static class AlleleFloat extends Allele implements IAlleleFloat {
        private float value;

        public AlleleFloat(String id, float val, boolean isDominant) {
            super("gregtech."+id, "gregtech."+id, isDominant);
            this.value = val;
            AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.SPEED);

        }

        @Override
        public float getValue(){
            return this.value;
        }
    }

    private static class AlleleInteger extends Allele implements IAlleleInteger {

        private int value;

        public AlleleInteger(String id, int val, boolean isDominant, EnumBeeChromosome c) {
            super("gregtech."+id, "gregtech."+id, isDominant);
            this.value = val;
            AlleleManager.alleleRegistry.registerAllele(this, c);
        }

        @Override
        public int getValue(){
            return this.value;
        }
    }

    private static class AlleleArea extends Allele implements IAlleleArea {

        private int[] value;

        public AlleleArea(String id, int rangeXZ,int rangeY, boolean isDominant) {
            super("gregtech."+id, "gregtech."+id, isDominant);
            this.value = new int[] {rangeXZ,rangeY,rangeXZ};
            AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.TERRITORY);
        }

        @Override
        public int[] getValue(){
            return this.value;
        }
    }
}
