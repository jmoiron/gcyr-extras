package net.jmoiron.gcyrextras.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;

public class OrbitalGasMinerMachine extends WorkableElectricMultiblockMachine {

    private static final int PARTICLE_UP_OFFSET = -4;
    private static final int PARTICLE_LEFT_OFFSET = 0;
    private static final int PARTICLE_FORWARD_OFFSET = -2;
    private static final int[][] PILLAR_SPORE_OFFSETS = {
            {0, 3, -2},
            {0, 2, -2},
            {0, 1, -2},
            {0, 0, -2},
            {0, -1, -2},
            {0, -2, -2},
            {0, -3, -2},
            {0, -4, -2},
            {0, -5, -2},
            {0, -6, -2}
    };

    public OrbitalGasMinerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (getLevel() == null || !isFormed() || !recipeLogic.isWorking()) {
            return;
        }
        if ((getOffsetTimer() & 1) != 0) {
            return;
        }

        BlockPos particlePos = RelativeDirection.offsetPos(
                getPos(),
                getFrontFacing(),
                getUpwardsFacing(),
                isFlipped(),
                PARTICLE_UP_OFFSET,
                PARTICLE_LEFT_OFFSET,
                PARTICLE_FORWARD_OFFSET);

        var random = getLevel().random;
        for (int[] offset : PILLAR_SPORE_OFFSETS) {
            BlockPos interiorPos = RelativeDirection.offsetPos(
                    getPos(),
                    getFrontFacing(),
                    getUpwardsFacing(),
                    isFlipped(),
                    offset[1],
                    offset[0],
                    offset[2]);
            double x = interiorPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = interiorPos.getY() + 0.2 + random.nextDouble() * 0.4;
            double z = interiorPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double vx = (random.nextDouble() - 0.5) * 0.01;
            double vy = -0.005 - random.nextDouble() * 0.01;
            double vz = (random.nextDouble() - 0.5) * 0.01;
            getLevel().addParticle(ParticleTypes.CRIMSON_SPORE, x, y, z, vx, vy, vz);
        }
    }
}
