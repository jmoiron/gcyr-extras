package net.jmoiron.gcyrextras.client.renderer.machine.impl;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.BloomUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.jmoiron.gcyrextras.client.renderer.GcyrExtrasRenderTypes;
import net.jmoiron.gcyrextras.client.util.GcyrExtrasRenderBufferHelper;
import net.jmoiron.gcyrextras.common.machine.multiblock.electric.OrbitalMiningLaserMachine;

public class OrbitalMiningLaserRender extends DynamicRender<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> {

    public static final Codec<OrbitalMiningLaserRender> CODEC = Codec.unit(OrbitalMiningLaserRender::new);
    public static final DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> TYPE =
            new DynamicRenderType<>(CODEC);

    private static final int CONTROLLER_X = 7;
    private static final int CONTROLLER_Y = 3;
    private static final int CONTROLLER_Z = 0;

    @Override
    public DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(OrbitalMiningLaserMachine machine, Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public void render(OrbitalMiningLaserMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed()) {
            return;
        }

        if (GTCEu.Mods.isShimmerLoaded()) {
            BloomUtils.entityBloom(source -> renderBeams(machine, poseStack, source));
        } else {
            renderBeams(machine, poseStack, buffer);
        }
    }

    private void renderBeams(OrbitalMiningLaserMachine machine, PoseStack poseStack, MultiBufferSource buffer) {
        int beamColor = OrbitalMiningLaserMachine.DEFAULT_BEAM_COLOR;
        int glowColor = (beamColor & 0x00FFFFFF) | 0x44000000;
        int innerColor = (beamColor & 0x00FFFFFF) | 0xCC000000;
        int coreColor = 0xFFFFFFFF;

        VertexConsumer glow = buffer.getBuffer(GcyrExtrasRenderTypes.laserGlow());
        VertexConsumer core = buffer.getBuffer(GcyrExtrasRenderTypes.laserCore());

        renderHorizontalBeam(poseStack, glow, core,
                patternPoint(machine, 7, 3, 5), patternPoint(machine, 7, 3, 2), glowColor, innerColor, coreColor);
        renderHorizontalBeam(poseStack, glow, core,
                patternPoint(machine, 7, 3, 9), patternPoint(machine, 7, 3, 12), glowColor, innerColor, coreColor);
        renderHorizontalBeam(poseStack, glow, core,
                patternPoint(machine, 5, 3, 7), patternPoint(machine, 2, 3, 7), glowColor, innerColor, coreColor);
        renderHorizontalBeam(poseStack, glow, core,
                patternPoint(machine, 9, 3, 7), patternPoint(machine, 12, 3, 7), glowColor, innerColor, coreColor);

        Vec3 start = patternPoint(machine, 7, 0, 7);
        double endY = findVerticalBeamEndY(machine);
        Vec3 end = new Vec3(start.x, endY, start.z);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, glow, start, end, 0.52f, glowColor);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, core, start, end, 0.30f, innerColor);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, core, start, end, 0.15f, coreColor);
    }

    private void renderHorizontalBeam(PoseStack poseStack, VertexConsumer glow, VertexConsumer core,
                                      Vec3 start, Vec3 end, int glowColor, int innerColor, int coreColor) {
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, glow, start, end, 0.34f, glowColor);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, core, start, end, 0.22f, innerColor);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, core, start, end, 0.11f, coreColor);
    }

    private static double findVerticalBeamEndY(OrbitalMiningLaserMachine machine) {
        Level level = machine.getLevel();
        if (level == null) {
            return patternPoint(machine, 7, -12, 7).y;
        }

        BlockPos emitterPos = patternBlockPos(machine, 7, 0, 7);
        int minY = level.getMinBuildHeight();

        for (int y = emitterPos.getY() - 1; y >= minY; y--) {
            BlockPos checkPos = new BlockPos(emitterPos.getX(), y, emitterPos.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && (state.canOcclude() || state.blocksMotion())) {
                return y + 1.0 - machine.getPos().getY();
            }
        }

        return minY - machine.getPos().getY();
    }

    private static BlockPos patternBlockPos(OrbitalMiningLaserMachine machine, int patternX, int patternY, int patternZ) {
        int upOffset = patternY - CONTROLLER_Y;
        int leftOffset = patternX - CONTROLLER_X;
        int forwardOffset = patternZ - CONTROLLER_Z;
        return RelativeDirection.offsetPos(machine.getPos(), machine.getFrontFacing(), machine.getUpwardsFacing(),
                machine.isFlipped(), upOffset, leftOffset, forwardOffset);
    }

    private static Vec3 patternPoint(OrbitalMiningLaserMachine machine, double patternX, double patternY, double patternZ) {
        double right = CONTROLLER_X - patternX;
        double up = patternY - CONTROLLER_Y;
        double back = patternZ - CONTROLLER_Z;
        return relativePoint(machine, right, up, back);
    }

    private static Vec3 relativePoint(OrbitalMiningLaserMachine machine, double right, double up, double back) {
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var rightDir = RelativeDirection.RIGHT.getRelative(front, upwards, flipped);
        var upDir = RelativeDirection.UP.getRelative(front, upwards, flipped);
        var backDir = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        return new Vec3(
                0.5 + rightDir.getStepX() * right + upDir.getStepX() * up + backDir.getStepX() * back,
                0.5 + rightDir.getStepY() * right + upDir.getStepY() * up + backDir.getStepY() * back,
                0.5 + rightDir.getStepZ() * right + upDir.getStepZ() * up + backDir.getStepZ() * back);
    }

    @Override
    public boolean shouldRenderOffScreen(OrbitalMiningLaserMachine machine) {
        return machine.isFormed();
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(OrbitalMiningLaserMachine machine) {
        return new AABB(machine.getPos()).inflate(16.0, 64.0, 16.0);
    }
}
