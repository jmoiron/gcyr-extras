package net.jmoiron.gcyrextras.api.block;

import argent_matter.gcyr.api.block.IRocketMotorType;

public enum ExtraRocketMotorType implements IRocketMotorType {
    STELLAR("stellar",    4, 100, 4),
    GALACTIC("galactic",  5, 125, 5),
    COSMIC("cosmic",      6, 150, 6),
    UNIVERSAL("universal",7, 175, 7);

    private final String serializedName;
    private final int tier;
    private final int maxCarryWeight;
    private final int motorCount;

    ExtraRocketMotorType(String serializedName, int tier, int maxCarryWeight, int motorCount) {
        this.serializedName = serializedName;
        this.tier = tier;
        this.maxCarryWeight = maxCarryWeight;
        this.motorCount = motorCount;
    }

    @Override public String getSerializedName() { return serializedName; }
    @Override public int getTier()              { return tier; }
    @Override public int getMaxCarryWeight()    { return maxCarryWeight; }
    @Override public int getMotorCount()        { return motorCount; }
}
