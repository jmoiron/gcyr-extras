package net.jmoiron.gcyrextras.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class GcyrExtrasRenderBufferHelper {

    private GcyrExtrasRenderBufferHelper() {}

    public static void renderCrossBeam(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                       float width, int color) {
        renderBeamSegment(poseStack, buffer, start, end, new Vec3(width, 0, 0), color);
        renderBeamSegment(poseStack, buffer, start, end, new Vec3(0, width, 0), color);
    }

    public static void renderVerticalBeam(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                          float coreWidth, float glowWidth, int coreColor, int glowColor) {
        renderBeamSegment(poseStack, buffer, start, end, new Vec3(coreWidth, 0, 0), coreColor);
        renderBeamSegment(poseStack, buffer, start, end, new Vec3(0, 0, coreWidth), coreColor);
        renderBeamSegment(poseStack, buffer, start, end, new Vec3(glowWidth, 0, 0), glowColor);
        renderBeamSegment(poseStack, buffer, start, end, new Vec3(0, 0, glowWidth), glowColor);
    }

    public static void renderBeamSegment(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                         Vec3 perpendicular, int color) {
        Vec3 offset = perpendicular.scale(0.5);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        vertex(buffer, matrix, start.subtract(offset), color);
        vertex(buffer, matrix, start.add(offset), color);
        vertex(buffer, matrix, end.add(offset), color);
        vertex(buffer, matrix, end.subtract(offset), color);
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int color) {
        buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color),
                        FastColor.ARGB32.blue(color), FastColor.ARGB32.alpha(color))
                .endVertex();
    }
}
