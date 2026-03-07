package net.jmoiron.gcyrextras.client.renderer.machine.impl;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.MultiBufferSource;
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

        int color = OrbitalMiningLaserMachine.DEFAULT_BEAM_COLOR;
        int glowColor = (color & 0x00FFFFFF) | 0x66000000;
        VertexConsumer consumer = buffer.getBuffer(GcyrExtrasRenderTypes.laserBeam());

        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, consumer,
                patternPoint(machine, 7, 3, 5), patternPoint(machine, 7, 3, 2), 0.16f, color);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, consumer,
                patternPoint(machine, 7, 3, 9), patternPoint(machine, 7, 3, 12), 0.16f, color);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, consumer,
                patternPoint(machine, 5, 3, 7), patternPoint(machine, 2, 3, 7), 0.16f, color);
        GcyrExtrasRenderBufferHelper.renderTubeBeam(poseStack, consumer,
                patternPoint(machine, 9, 3, 7), patternPoint(machine, 12, 3, 7), 0.16f, color);

        GcyrExtrasRenderBufferHelper.renderVerticalBeam(poseStack, consumer,
                patternPoint(machine, 7, 0, 7), patternPoint(machine, 7, -12, 7),
                0.22f, 0.45f, color, glowColor);
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
        return new AABB(machine.getPos()).inflate(16.0, 16.0, 16.0);
    }
}
