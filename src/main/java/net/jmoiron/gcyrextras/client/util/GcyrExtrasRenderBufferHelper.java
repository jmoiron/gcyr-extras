package net.jmoiron.gcyrextras.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class GcyrExtrasRenderBufferHelper {

    private static final int TUBE_SIDES = 8;

    private GcyrExtrasRenderBufferHelper() {}

    public static void renderTubeBeam(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                      float radius, int color) {
        Vec3 axis = end.subtract(start);
        double length = axis.length();
        if (length < 1.0e-4) {
            return;
        }

        Vec3 forward = axis.scale(1.0 / length);
        Vec3 reference = Math.abs(forward.y) > 0.98 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 basisA = forward.cross(reference).normalize();
        Vec3 basisB = forward.cross(basisA).normalize();

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        for (int i = 0; i < TUBE_SIDES; i++) {
            double angle0 = (Math.PI * 2.0 * i) / TUBE_SIDES;
            double angle1 = (Math.PI * 2.0 * (i + 1)) / TUBE_SIDES;

            Vec3 ring0 = basisA.scale(Math.cos(angle0) * radius).add(basisB.scale(Math.sin(angle0) * radius));
            Vec3 ring1 = basisA.scale(Math.cos(angle1) * radius).add(basisB.scale(Math.sin(angle1) * radius));

            vertex(buffer, matrix, start.add(ring0), color);
            vertex(buffer, matrix, start.add(ring1), color);
            vertex(buffer, matrix, end.add(ring1), color);
            vertex(buffer, matrix, end.add(ring0), color);
        }
    }

    public static void renderVerticalBeam(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                          float coreRadius, float glowRadius, int coreColor, int glowColor) {
        renderTubeBeam(poseStack, buffer, start, end, glowRadius, glowColor);
        renderTubeBeam(poseStack, buffer, start, end, coreRadius, coreColor);
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int color) {
        buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color),
                        FastColor.ARGB32.blue(color), FastColor.ARGB32.alpha(color))
                .endVertex();
    }
}
