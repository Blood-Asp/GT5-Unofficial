package gregtech.common.bees;

import forestry.api.apiculture.*;
import forestry.api.core.IClimateProvider;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.apiculture.genetics.BeeMutation;
import forestry.core.genetics.mutations.Mutation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;


public class GT_Bee_Mutation extends BeeMutation {

    private final float split;

    public GT_Bee_Mutation(IAlleleBeeSpecies bee0, IAlleleBeeSpecies bee1, IAllele[] result, int chance, float split) {
        super(bee0, bee1, result, chance);
        this.split = split;
        BeeManager.beeRoot.registerMutation(this);
    }

    @Override
    public float getBaseChance() {
        return super.getBaseChance() / split;
    }

    @Override
    public float getChance(IBeeHousing housing, IAlleleBeeSpecies allele0, IAlleleBeeSpecies allele1, IBeeGenome genome0, IBeeGenome genome1) {
        World world = housing != null ? housing.getWorld() : null;
        ChunkCoordinates housingCoordinates = housing != null ? housing.getCoordinates() : null;
        int x = housingCoordinates != null ? housingCoordinates.posX : 0;
        int y = housingCoordinates != null ? housingCoordinates.posY : 0;
        int z = housingCoordinates != null ? housingCoordinates.posZ : 0;

        float processedChance = getBasicChance(world, x, y, z, allele0, allele1, genome0, genome1, housing);

        if (processedChance <= 0f) {
            return 0f;
        }

        IBeeModifier beeHousingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
        IBeeModifier beeModeModifier = BeeManager.beeRoot.getBeekeepingMode(world).getBeeModifier();

        processedChance *= beeHousingModifier.getMutationModifier(genome0, genome1, processedChance);
        processedChance *= beeModeModifier.getMutationModifier(genome0, genome1, processedChance);

        return processedChance;
    }

    @SuppressWarnings("unchecked")
    private float getBasicChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
        float mutationChance = this.getBaseChance();
        List<IMutationCondition> mutationConditions = null;
        Field f = FieldUtils.getDeclaredField(Mutation.class, "mutationConditions", true);
        if (f == null)
            f = FieldUtils.getField(Mutation.class, "mutationConditions", true);
        if (f == null)
            return mutationChance;
        try {
            mutationConditions = f.get(this) instanceof List ? (List<IMutationCondition>) f.get(this) : null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (mutationConditions != null)
            for (IMutationCondition mutationCondition : mutationConditions) {
                mutationChance *= mutationCondition.getChance(world, x, y, z, allele0, allele1, genome0, genome1, climate);
                if (mutationChance == 0) {
                    return 0;
                }
            }
        return mutationChance;
    }
}
