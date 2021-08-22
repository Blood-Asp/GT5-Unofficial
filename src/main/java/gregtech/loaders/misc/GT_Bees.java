package gregtech.loaders.misc;

import cpw.mods.fml.common.Loader;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.core.IClimateProvider;
import forestry.api.genetics.*;
import forestry.core.genetics.alleles.Allele;
import forestry.core.utils.StringUtil;
import gregtech.GT_Mod;
import gregtech.common.bees.GT_AlleleHelper;
import gregtech.common.items.ItemComb;
import gregtech.common.items.ItemDrop;
import gregtech.common.items.ItemPollen;
import gregtech.common.items.ItemPropolis;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import static gregtech.api.enums.GT_Values.MOD_ID_FR;

public class GT_Bees {

    public static IAlleleInteger noFertility;
    public static IAlleleInteger superFertility;

    public static IAlleleInteger noFlowering;
    public static IAlleleInteger superFlowering;

    public static IAlleleArea noTerritory;
    public static IAlleleArea superTerritory;

    public static IAlleleFloat noWork;
    public static IAlleleFloat speedBlinding;
    public static IAlleleFloat superSpeed;

    public static IAlleleInteger blinkLife;
    public static IAlleleInteger superLife;

    public static ItemPropolis propolis;
    public static ItemPollen pollen;
    public static ItemDrop drop;
    public static ItemComb combs;

    public GT_Bees() {
        if (Loader.isModLoaded(MOD_ID_FR) && GT_Mod.gregtechproxy.mGTBees) {
            GT_AlleleHelper.initialisation();
            setupGTAlleles();
            propolis = new ItemPropolis();
            propolis.initPropolisRecipes();
            pollen = new ItemPollen();
            drop = new ItemDrop();
            drop.initDropsRecipes();
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
        superSpeed = new AlleleFloat("speedAccelerated", 4F, false);
        speedBlinding = (IAlleleFloat) AlleleManager.alleleRegistry.getAllele("magicbees.speedBlinding") == null ? new AlleleFloat("speedBlinding", 2f, false) : (IAlleleFloat) AlleleManager.alleleRegistry.getAllele("magicbees.speedBlinding") ;

        blinkLife = new AlleleInteger("lifeBlink", 2, false, EnumBeeChromosome.LIFESPAN);
        superLife = new AlleleInteger("lifeEon", 600, false, EnumBeeChromosome.LIFESPAN);

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

    public static class DimensionMutationCondition implements IMutationCondition {

        int dimID;
        String dimName;

        public DimensionMutationCondition(int id, String name) {
            dimID = id;
            dimName = name;
        }

        @Override
        public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
            if(world.provider.dimensionId == dimID)return 1;
            return 0;
        }

        @Override
        public String getDescription() {
            return StringUtil.localizeAndFormat("mutation.condition.dim") + " " + dimName;
        }

    }

    public static class BiomeIDMutationCondition implements IMutationCondition {

        int biomeID;
        String biomeName;

        public BiomeIDMutationCondition(int id, String name) {
            biomeID = id;
            biomeName = name;
        }

        @Override
        public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
            if(climate.getBiome().biomeID == biomeID) return 1;
            return 0;
        }

        @Override
        public String getDescription() {
            if (BiomeGenBase.getBiome(biomeID)!=null) {
                return StringUtil.localizeAndFormat("mutation.condition.biomeid") + " " + biomeName;
            }
            return "";
        }

    }
}
