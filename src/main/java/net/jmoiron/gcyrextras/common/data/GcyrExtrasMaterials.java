package net.jmoiron.gcyrextras.common.data;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_FRAME;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_ROD;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Electrum;
import static com.gregtechceu.gtceu.common.data.GTMaterials.TitaniumCarbide;

public final class GcyrExtrasMaterials {

    private GcyrExtrasMaterials() {}

    public static void modifyMaterials() {
        TitaniumCarbide.addFlags(GENERATE_ROD, GENERATE_FRAME);
        Electrum.addFlags(GENERATE_FRAME);
    }
}
