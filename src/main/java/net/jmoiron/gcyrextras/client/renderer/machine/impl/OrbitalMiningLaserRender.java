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

import java.util.HashMap;
import java.util.Map;

public class OrbitalMiningLaserRender extends DynamicRender<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> {

    public static final Codec<OrbitalMiningLaserRender> CODEC = Codec.unit(OrbitalMiningLaserRender::new);
    public static final DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> TYPE =
            new DynamicRenderType<>(CODEC);

    private static final int CONTROLLER_X = 7;
    private static final int CONTROLLER_Y = 3;
    private static final int CONTROLLER_Z = 0;
    private static final long BEAM_SCAN_INTERVAL_TICKS = 5L;

    private final Map<Long, BeamEndCache> verticalBeamCache = new HashMap<>();

    @Override
    public DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(OrbitalMiningLaserMachine machine, Vec3 cameraPos) {
        return isLaserActive(machine);
    }

    @Override
    public void render(OrbitalMiningLaserMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!isLaserActive(machine)) {
            return;
        }

        if (GTCEu.Mods.isShimmerLoaded()) {
            BloomUtils.entityBloom(source -> renderEffects(machine, partialTick, poseStack, source));
        } else {
            renderEffects(machine, partialTick, poseStack, buffer);
        }
    }

    private void renderEffects(OrbitalMiningLaserMachine machine, float partialTick, PoseStack poseStack,
                               MultiBufferSource buffer) {
        BeamColors colors = beamColors(machine, partialTick);
        VertexConsumer glow = buffer.getBuffer(GcyrExtrasRenderTypes.laserGlow());
        VertexConsumer core = buffer.getBuffer(GcyrExtrasRenderTypes.laserCore());

        BeamProfile horizontal = new BeamProfile(0.33f, 0.24f, 0.18f, 0.12f);
        BeamProfile vertical = new BeamProfile(0.50f, 0.36f, 0.28f, 0.20f);
        BeamProfile torus = new BeamProfile(0.15f, 0.11f, 0.08f, 0.055f);
        BeamProfile sourceSphere = new BeamProfile(0.625f, 0.475f, 0.375f, 0.275f);

        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 7, 3, 5), patternPoint(machine, 7, 3, 2), horizontal, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 7, 3, 9), patternPoint(machine, 7, 3, 12), horizontal, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 5, 3, 7), patternPoint(machine, 2, 3, 7), horizontal, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core,
                patternPoint(machine, 9, 3, 7), patternPoint(machine, 12, 3, 7), horizontal, colors);

        Vec3 ringCenter = patternPoint(machine, 7, 3, 7);
        Vec3 ringAxis = directionVector(RelativeDirection.UP.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped()));
        GcyrExtrasRenderBufferHelper.renderFusionStyleTorus(poseStack, glow, core, ringCenter, ringAxis, 1.25f, torus, colors);

        Vec3 start = patternPoint(machine, 7, 3, 7);
        double endY = findVerticalBeamEndY(machine);
        Vec3 end = new Vec3(start.x, endY, start.z);
        GcyrExtrasRenderBufferHelper.renderFusionStyleBeam(poseStack, glow, core, start, end, vertical, colors);
        GcyrExtrasRenderBufferHelper.renderFusionStyleSphere(poseStack, glow, core, start, sourceSphere, colors);
    }

    private BeamColors beamColors(OrbitalMiningLaserMachine machine, float partialTick) {
        float fadeAlpha = 1.0f;
        int baseColor = OrbitalMiningLaserMachine.DEFAULT_BEAM_COLOR;
        int glowColor = color(Mth.floor(0x88 * fadeAlpha), red(baseColor), green(baseColor), blue(baseColor));
        int shellColor = color(Mth.floor(0xF0 * fadeAlpha), 255, 255, 255);
        int haloColor = color(Mth.floor(0xFF * fadeAlpha), 255, 255, 255);
        int coreColor = color(Mth.floor(255 * fadeAlpha), 255, 255, 255);
        return new BeamColors(glowColor, shellColor, haloColor, coreColor);
    }

    private double findVerticalBeamEndY(OrbitalMiningLaserMachine machine) {
        Level level = machine.getLevel();
        if (level == null) {
            return patternPoint(machine, 7, -12, 7).y;
        }

        long machineKey = machine.getPos().asLong();
        long gameTime = level.getGameTime();
        BeamEndCache cached = verticalBeamCache.get(machineKey);
        if (cached != null && gameTime < cached.nextRefreshTick()) {
            return cached.endY();
        }

        BlockPos bottomGlassPos = relativeBlockPos(machine, 0, -3, 7);
        int minY = level.getMinBuildHeight();

        for (int y = bottomGlassPos.getY() - 1; y >= minY; y--) {
            BlockPos checkPos = new BlockPos(bottomGlassPos.getX(), y, bottomGlassPos.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (blocksVerticalBeam(level, checkPos, state)) {
                double endY = y + 1.0 - machine.getPos().getY();
                verticalBeamCache.put(machineKey, new BeamEndCache(endY, gameTime + BEAM_SCAN_INTERVAL_TICKS));
                return endY;
            }
        }

        double endY = minY - machine.getPos().getY();
        verticalBeamCache.put(machineKey, new BeamEndCache(endY, gameTime + BEAM_SCAN_INTERVAL_TICKS));
        return endY;
    }

    private static boolean blocksVerticalBeam(Level level, BlockPos pos, BlockState state) {
        if (state.isAir()) {
            return false;
        }
        if (state.getFluidState().isSource()) {
            return false;
        }
        return state.isSolidRender(level, pos) && state.getLightBlock(level, pos) >= 15;
    }

    private static Vec3 directionVector(net.minecraft.core.Direction direction) {
        return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    private static BlockPos relativeBlockPos(OrbitalMiningLaserMachine machine, int right, int up, int back) {
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var rightDir = RelativeDirection.RIGHT.getRelative(front, upwards, flipped);
        var upDir = RelativeDirection.UP.getRelative(front, upwards, flipped);
        var backDir = RelativeDirection.BACK.getRelative(front, upwards, flipped);

        return machine.getPos()
                .relative(rightDir, right)
                .relative(upDir, up)
                .relative(backDir, back);
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
        return isLaserActive(machine);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(OrbitalMiningLaserMachine machine) {
        return new AABB(machine.getPos()).inflate(16.0, 64.0, 16.0);
    }

    private static boolean isLaserActive(OrbitalMiningLaserMachine machine) {
        return machine.isFormed() && machine.recipeLogic.isWorking() && machine.recipeLogic.getLastRecipe() != null;
    }

    private record BeamEndCache(double endY, long nextRefreshTick) {}
}
