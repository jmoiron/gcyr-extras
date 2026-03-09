package net.jmoiron.gcyrextras.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class GcyrExtrasRenderBufferHelper {

    private static final int TUBE_SIDES = 12;
    private static final int TORUS_SEGMENTS = 24;
    private static final int SPHERE_SEGMENTS = 12;

    private GcyrExtrasRenderBufferHelper() {}

    public static void renderFusionStyleBeam(PoseStack poseStack, VertexConsumer glowBuffer, VertexConsumer coreBuffer,
                                             Vec3 start, Vec3 end, BeamProfile profile, BeamColors colors) {
        renderBeamGlow(poseStack, glowBuffer, start, end, profile.glowRadius(), colors.glowColor());
        renderBeamShell(poseStack, coreBuffer, start, end, profile.shellRadius(), colors.shellColor());
        renderBeamCore(poseStack, coreBuffer, start, end, profile.haloRadius(), colors.haloColor());
        renderBeamCore(poseStack, coreBuffer, start, end, profile.haloRadius(), colors.haloColor());
        renderBeamCore(poseStack, coreBuffer, start, end, profile.coreRadius(), colors.coreColor());
        renderBeamCore(poseStack, coreBuffer, start, end, profile.coreRadius(), colors.coreColor());
    }

    public static void renderFusionStyleTorus(PoseStack poseStack, VertexConsumer glowBuffer, VertexConsumer coreBuffer,
                                              Vec3 center, Vec3 axis, float majorRadius, BeamProfile profile,
                                              BeamColors colors) {
        renderTorusLayer(poseStack, glowBuffer, center, axis, majorRadius, profile.glowRadius(), colors.glowColor());
        renderTorusLayer(poseStack, coreBuffer, center, axis, majorRadius, profile.shellRadius(), colors.shellColor());
        renderTorusLayer(poseStack, coreBuffer, center, axis, majorRadius, profile.haloRadius(), colors.haloColor());
        renderTorusLayer(poseStack, coreBuffer, center, axis, majorRadius, profile.haloRadius(), colors.haloColor());
        renderTorusLayer(poseStack, coreBuffer, center, axis, majorRadius, profile.coreRadius(), colors.coreColor());
        renderTorusLayer(poseStack, coreBuffer, center, axis, majorRadius, profile.coreRadius(), colors.coreColor());
    }

    public static void renderFusionStyleSphere(PoseStack poseStack, VertexConsumer glowBuffer, VertexConsumer coreBuffer,
                                               Vec3 center, BeamProfile profile, BeamColors colors) {
        renderSphereLayer(poseStack, glowBuffer, center, profile.glowRadius(), colors.glowColor());
        renderSphereLayer(poseStack, coreBuffer, center, profile.shellRadius(), colors.shellColor());
        renderSphereLayer(poseStack, coreBuffer, center, profile.haloRadius(), colors.haloColor());
        renderSphereLayer(poseStack, coreBuffer, center, profile.haloRadius(), colors.haloColor());
        renderSphereLayer(poseStack, coreBuffer, center, profile.coreRadius(), colors.coreColor());
        renderSphereLayer(poseStack, coreBuffer, center, profile.coreRadius(), colors.coreColor());
    }

    public static void renderBeamGlow(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                      float radius, int color) {
        renderTubeBeam(poseStack, buffer, start, end, radius, color);
    }

    public static void renderBeamShell(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                       float radius, int color) {
        renderTubeBeam(poseStack, buffer, start, end, radius, color);
    }

    public static void renderBeamCore(PoseStack poseStack, VertexConsumer buffer, Vec3 start, Vec3 end,
                                      float radius, int color) {
        renderTubeBeam(poseStack, buffer, start, end, radius, color);
    }

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

    private static void renderTorusLayer(PoseStack poseStack, VertexConsumer buffer, Vec3 center, Vec3 axis,
                                         float majorRadius, float minorRadius, int color) {
        if (majorRadius <= 0.0f || minorRadius <= 0.0f) {
            return;
        }

        Vec3 normal = axis.normalize();
        Vec3 reference = Math.abs(normal.y) > 0.98 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 basisA = normal.cross(reference).normalize();
        Vec3 basisB = normal.cross(basisA).normalize();

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        for (int seg = 0; seg < TORUS_SEGMENTS; seg++) {
            double u0 = (Math.PI * 2.0 * seg) / TORUS_SEGMENTS;
            double u1 = (Math.PI * 2.0 * (seg + 1)) / TORUS_SEGMENTS;

            Vec3 center0 = center.add(basisA.scale(Math.cos(u0) * majorRadius)).add(basisB.scale(Math.sin(u0) * majorRadius));
            Vec3 center1 = center.add(basisA.scale(Math.cos(u1) * majorRadius)).add(basisB.scale(Math.sin(u1) * majorRadius));
            Vec3 radial0 = basisA.scale(Math.cos(u0)).add(basisB.scale(Math.sin(u0)));
            Vec3 radial1 = basisA.scale(Math.cos(u1)).add(basisB.scale(Math.sin(u1)));

            for (int side = 0; side < TUBE_SIDES; side++) {
                double v0 = (Math.PI * 2.0 * side) / TUBE_SIDES;
                double v1 = (Math.PI * 2.0 * (side + 1)) / TUBE_SIDES;

                Vec3 offset00 = radial0.scale(Math.cos(v0) * minorRadius).add(normal.scale(Math.sin(v0) * minorRadius));
                Vec3 offset01 = radial0.scale(Math.cos(v1) * minorRadius).add(normal.scale(Math.sin(v1) * minorRadius));
                Vec3 offset10 = radial1.scale(Math.cos(v0) * minorRadius).add(normal.scale(Math.sin(v0) * minorRadius));
                Vec3 offset11 = radial1.scale(Math.cos(v1) * minorRadius).add(normal.scale(Math.sin(v1) * minorRadius));

                vertex(buffer, matrix, center0.add(offset00), color);
                vertex(buffer, matrix, center0.add(offset01), color);
                vertex(buffer, matrix, center1.add(offset11), color);
                vertex(buffer, matrix, center1.add(offset10), color);
            }
        }
    }

    private static void renderSphereLayer(PoseStack poseStack, VertexConsumer buffer, Vec3 center, float radius, int color) {
        if (radius <= 0.0f) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        for (int lat = 0; lat < SPHERE_SEGMENTS; lat++) {
            double theta0 = Math.PI * lat / SPHERE_SEGMENTS;
            double theta1 = Math.PI * (lat + 1) / SPHERE_SEGMENTS;

            for (int lon = 0; lon < SPHERE_SEGMENTS * 2; lon++) {
                double phi0 = Math.PI * 2.0 * lon / (SPHERE_SEGMENTS * 2);
                double phi1 = Math.PI * 2.0 * (lon + 1) / (SPHERE_SEGMENTS * 2);

                vertex(buffer, matrix, center.add(spherePoint(radius, theta0, phi0)), color);
                vertex(buffer, matrix, center.add(spherePoint(radius, theta1, phi0)), color);
                vertex(buffer, matrix, center.add(spherePoint(radius, theta1, phi1)), color);
                vertex(buffer, matrix, center.add(spherePoint(radius, theta0, phi1)), color);
            }
        }
    }

    private static Vec3 spherePoint(float radius, double theta, double phi) {
        double sinTheta = Math.sin(theta);
        return new Vec3(
                radius * sinTheta * Math.cos(phi),
                radius * Math.cos(theta),
                radius * sinTheta * Math.sin(phi));
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int color) {
        buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color),
                        FastColor.ARGB32.blue(color), FastColor.ARGB32.alpha(color))
                .endVertex();
    }

    public record BeamProfile(float glowRadius, float shellRadius, float haloRadius, float coreRadius) {}

    public record BeamColors(int glowColor, int shellColor, int haloColor, int coreColor) {}
}
