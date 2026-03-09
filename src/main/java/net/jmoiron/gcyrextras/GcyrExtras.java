package net.jmoiron.gcyrextras;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import net.jmoiron.gcyrextras.api.registries.GcyrExtrasRegistries;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasMachines;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GcyrExtras.MOD_ID)
public class GcyrExtras {

    public static final String MOD_ID = "gcyrextras";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public GcyrExtras() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        GcyrExtrasBlocks.BLOCKS.register(bus);
        GcyrExtrasBlocks.ITEMS.register(bus);
        GcyrExtrasRegistries.REGISTRATE.registerRegistrate();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> net.jmoiron.gcyrextras.forge.GcyrExtrasForgeClientEvents.registerDynamicRenders());
        bus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        bus.addGenericListener(MachineDefinition.class, this::registerMachines);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GcyrExtrasBlocks::registerWithGcyr);
    }

    public void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GcyrExtrasMachines.init();
    }

    public void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        GcyrExtrasRecipeTypes.init();
    }
}
