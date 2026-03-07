package net.jmoiron.gcyrextras.client.renderer.machine.impl;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.BloomUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.jmoiron.gcyrextras.client.renderer.GcyrExtrasRenderTypes;
import net.jmoiron.gcyrextras.client.util.GcyrExtrasRenderBufferHelper;
import net.jmoiron.gcyrextras.client.util.GcyrExtrasRenderBufferHelper.BeamColors;
import net.jmoiron.gcyrextras.client.util.GcyrExtrasRenderBufferHelper.BeamProfile;
import net.jmoiron.gcyrextras.common.machine.multiblock.electric.OrbitalMiningLaserMachine;

import static net.minecraft.util.FastColor.ARGB32.alpha;
import static net.minecraft.util.FastColor.ARGB32.blue;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.util.FastColor.ARGB32.green;
import static net.minecraft.util.FastColor.ARGB32.red;

public class OrbitalMiningLaserRender extends DynamicRender<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> {

    public static final Codec<OrbitalMiningLaserRender> CODEC = Codec.unit(OrbitalMiningLaserRender::new);
    public static final DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> TYPE =
            new DynamicRenderType<>(CODEC);

    private static final int CONTROLLER_X = 7;
    private static final int CONTROLLER_Y = 3;
    private static final int CONTROLLER_Z = 0;
    private static final float FADEOUT = 30.0f;

    private float delta;
    private int lastColor = OrbitalMiningLaserMachine.DEFAULT_BEAM_COLOR;

    @Override
    public DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(OrbitalMiningLaserMachine machine, Vec3 cameraPos) {
        return machine.isFormed() || delta > 0;
    }

    @Override
    public void render(OrbitalMiningLaserMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed() && delta <= 0) {
            return;
        }

        if (GTCEu.Mods.isShimmerLoaded()) {
            BloomUtils.entityBloom(source -> renderBeams(machine, partialTick, poseStack, source));
        } else {
            renderBeams(machine, partialTick, poseStack, buffer);
        }
    }

    private void renderBeams(OrbitalMiningLaserMachine machine, float partialTick, PoseStack poseStack,
                             MultiBufferSource buffer) {
        BeamColors colors = beamColors(machine, partialTick);
        VertexConsumer glow = buffer.getBuffer(GcyrExtrasRenderTypes.laserGlow());
        VertexConsumer core = buffer.getBuffer(GcyrExtrasRenderTypes.laserCore());

        BeamProfile horizontal = new BeamProfile(0.44f, 0.32f, 0.24f, 0.16f);
        BeamProfile vertical = new BeamProfile(0.54f, 0.38f, 0.30f, 0.22f);

        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 7, 3, 5), patternPoint(machine, 7, 3, 2), horizontal, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 7, 3, 9), patternPoint(machine, 7, 3, 12), horizontal, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 5, 3, 7), patternPoint(machine, 2, 3, 7), horizontal, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 9, 3, 7), patternPoint(machine, 12, 3, 7), horizontal, colors);

        Vec3 start = patternPoint(machine, 7, 0, 7);
        double endY = findVerticalBeamEndY(machine);
        Vec3 end = new Vec3(start.x, endY, start.z);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core, start, end, vertical, colors);
    }

    private BeamColors beamColors(OrbitalMiningLaserMachine machine, float partialTick) {
        int baseColor = machine.isFormed() ? OrbitalMiningLaserMachine.DEFAULT_BEAM_COLOR : lastColor;
        float fadeAlpha = 1.0f;
        if (machine.isFormed()) {
            lastColor = OrbitalMiningLaserMachine.DEFAULT_BEAM_COLOR;
            delta = FADEOUT;
        } else {
            fadeAlpha = delta / FADEOUT;
            delta = Math.max(0.0f, delta - Minecraft.getInstance().getDeltaFrameTime());
        }

        float lerpFactor = Math.abs((Math.abs(machine.getOffsetTimer() % 50) + partialTick) - 25.0f) / 25.0f;
        int pulseR = Mth.floor(Mth.lerp(lerpFactor, red(baseColor), 255));
        int pulseG = Mth.floor(Mth.lerp(lerpFactor, green(baseColor), 255));
        int pulseB = Mth.floor(Mth.lerp(lerpFactor, blue(baseColor), 255));
        int pulseA = Mth.floor(255 * fadeAlpha);

        int glowColor = color(Mth.floor(0x88 * fadeAlpha), red(baseColor), green(baseColor), blue(baseColor));
        int shellColor = color(Mth.floor(0xF0 * fadeAlpha), pulseR, pulseG, pulseB);
        int haloColor = color(Mth.floor(0xFF * fadeAlpha), 255, 255, 255);
        int coreColor = color(pulseA, 255, 255, 255);
        lastColor = color(alpha(shellColor), pulseR, pulseG, pulseB);
        return new BeamColors(glowColor, shellColor, haloColor, coreColor);
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
        return machine.isFormed() || delta > 0;
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
