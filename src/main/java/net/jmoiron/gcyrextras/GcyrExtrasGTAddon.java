package net.jmoiron.gcyrextras;

import argent_matter.gcyr.common.data.GCYRRecipeTypes;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

@GTAddon
public class GcyrExtrasGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return null;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return GcyrExtras.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        if (!ModList.get().isLoaded("gtnn")) return;

        addGtnnFuel(provider, "rp_1_mixed_fuel",                    100);
        addGtnnFuel(provider, "dense_hydrazine_mixed_fuel",          150);
        addGtnnFuel(provider, "methylhydrazine_nitrate_rocket_fuel", 200);
        addGtnnFuel(provider, "udmh_rocket_fuel",                    250);
    }

    private static void addGtnnFuel(Consumer<FinishedRecipe> provider, String fluidName, int duration) {
        var loc = ResourceLocation.fromNamespaceAndPath("gtceu", fluidName);
        var fluid = ForgeRegistries.FLUIDS.getValue(loc);
        if (fluid == null || fluid == Fluids.EMPTY) return;

        GCYRRecipeTypes.ROCKET_FUEL_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, fluidName))
                .inputFluids(new FluidStack(fluid, 1))
                .duration(duration)
                .EUt(0)
                .save(provider);
    }
}
