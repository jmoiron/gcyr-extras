package net.jmoiron.gcyrextras.forge;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.client.renderer.machine.impl.OrbitalMiningLaserRender;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = GcyrExtras.MOD_ID, value = Dist.CLIENT)
public final class GcyrExtrasForgeClientEvents {

    private static boolean dynamicRendersRegistered;

    private GcyrExtrasForgeClientEvents() {}

    public static void registerDynamicRenders() {
        if (dynamicRendersRegistered) {
            return;
        }
        dynamicRendersRegistered = true;
        DynamicRenderManager.register(GcyrExtras.id("orbital_mining_laser"), OrbitalMiningLaserRender.TYPE);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(GcyrExtrasForgeClientEvents::registerDynamicRenders);
    }
}
