package net.jmoiron.gcyrextras.client.renderer.machine;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import net.jmoiron.gcyrextras.client.renderer.machine.impl.OrbitalMiningLaserRender;

public final class GcyrExtrasDynamicRenderHelper {

    private GcyrExtrasDynamicRenderHelper() {}

    public static DynamicRender<?, ?> createOrbitalMiningLaserRender() {
        return new OrbitalMiningLaserRender();
    }
}
