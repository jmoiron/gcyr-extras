package net.jmoiron.gcyrextras;

import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GcyrExtras.MOD_ID)
public class GcyrExtras {

    public static final String MOD_ID = "gcyrextras";

    public GcyrExtras() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        GcyrExtrasBlocks.BLOCKS.register(bus);
        GcyrExtrasBlocks.ITEMS.register(bus);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GcyrExtrasBlocks::registerWithGcyr);
    }
}
