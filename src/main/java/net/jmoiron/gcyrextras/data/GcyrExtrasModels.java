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
        prov.simpleBlock(GcyrExtrasBlocks.GAS_MINER_CASING.get(),
                prov.models().cubeAll("gas_miner_casing",
                        GcyrExtras.id("block/casings/solid/gas_miner_casing")));
        prov.simpleBlock(GcyrExtrasBlocks.VACUUM_COUPLING_CASING.get(),
                prov.models().cubeAll("vacuum_coupling_casing",
                        GcyrExtras.id("block/casings/gcym/vacuum_coupling_casing")));
        prov.simpleBlock(GcyrExtrasBlocks.SPACESTATION_CORE.get(),
                prov.models().cubeAll("spacestation_core",
                        prov.mcLoc("block/iron_block")));
    }

    public static void initItemModels(RegistrateItemModelProvider prov) {
        prov.withExistingParent("mining_laser_casing", prov.modLoc("block/mining_laser_casing"));
        prov.withExistingParent("gas_miner_casing", prov.modLoc("block/gas_miner_casing"));
        prov.withExistingParent("vacuum_coupling_casing", prov.modLoc("block/vacuum_coupling_casing"));
        prov.withExistingParent("spacestation_core", prov.modLoc("block/spacestation_core"));
    }
}
