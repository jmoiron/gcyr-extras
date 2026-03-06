package net.jmoiron.gcyrextras.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public final class GcyrExtrasLang {

    private GcyrExtrasLang() {}

    public static void init(RegistrateLangProvider provider) {
        provider.add("block.gcyrextras.stellar_rocket_motor", "Stellar Rocket Motor");
        provider.add("block.gcyrextras.galactic_rocket_motor", "Galactic Rocket Motor");
        provider.add("block.gcyrextras.cosmic_rocket_motor", "Cosmic Rocket Motor");
        provider.add("block.gcyrextras.universal_rocket_motor", "Universal Rocket Motor");

        provider.add("block.gcyrextras.stellar_fuel_tank", "Stellar Fuel Tank");
        provider.add("block.gcyrextras.galactic_fuel_tank", "Galactic Fuel Tank");
        provider.add("block.gcyrextras.cosmic_fuel_tank", "Cosmic Fuel Tank");
        provider.add("block.gcyrextras.universal_fuel_tank", "Universal Fuel Tank");
        provider.add("block.gcyrextras.beam_receiver", "Beam Receiver");
        provider.add("block.gcyrextras.beam_former", "Beam Former");

        provider.add("tooltip.gcyrextras.tier", "Tier: %s");
        provider.add("tooltip.gcyrextras.motor.carry_weight", "Max Carry Weight: %s");
        provider.add("tooltip.gcyrextras.motor.motor_count", "Thruster Power: %s");
        provider.add("tooltip.gcyrextras.tank.fluid_capacity", "Fluid Capacity: %s B");
    }
}
