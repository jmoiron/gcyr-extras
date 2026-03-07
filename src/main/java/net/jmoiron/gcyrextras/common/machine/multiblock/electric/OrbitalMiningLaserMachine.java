package net.jmoiron.gcyrextras.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

public class OrbitalMiningLaserMachine extends WorkableElectricMultiblockMachine {

    public static final int DEFAULT_BEAM_COLOR = 0xFF44FF66;

    public OrbitalMiningLaserMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }
}
