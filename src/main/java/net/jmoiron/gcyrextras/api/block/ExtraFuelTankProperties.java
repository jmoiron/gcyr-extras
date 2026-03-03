package net.jmoiron.gcyrextras.api.block;

import argent_matter.gcyr.api.block.IFuelTankProperties;
import net.minecraftforge.fluids.FluidType;

public enum ExtraFuelTankProperties implements IFuelTankProperties {
    STELLAR("stellar",    4, 20 * FluidType.BUCKET_VOLUME),
    GALACTIC("galactic",  5, 30 * FluidType.BUCKET_VOLUME),
    COSMIC("cosmic",      6, 45 * FluidType.BUCKET_VOLUME),
    UNIVERSAL("universal",7, 64 * FluidType.BUCKET_VOLUME);

    private final String serializedName;
    private final int tier;
    private final int fuelStorage;

    ExtraFuelTankProperties(String serializedName, int tier, int fuelStorage) {
        this.serializedName = serializedName;
        this.tier = tier;
        this.fuelStorage = fuelStorage;
    }

    @Override public String getSerializedName() { return serializedName; }
    @Override public int getTier()              { return tier; }
    @Override public int getFuelStorage()       { return fuelStorage; }
}
