package net.jmoiron.gcyrextras.data;

import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;

public final class GcyrExtrasModels {

    private GcyrExtrasModels() {}

    public static void initBlockStates(GTBlockstateProvider prov) {
        prov.simpleBlock(GcyrExtrasBlocks.MINING_LASER_CASING.get(),
                prov.models().cubeAll("mining_laser_casing",
                        GcyrExtras.id("block/casings/solid/mining_laser_casing")));
    }

    public static void initItemModels(RegistrateItemModelProvider prov) {
        prov.withExistingParent("mining_laser_casing", prov.modLoc("block/mining_laser_casing"));
    }
}
