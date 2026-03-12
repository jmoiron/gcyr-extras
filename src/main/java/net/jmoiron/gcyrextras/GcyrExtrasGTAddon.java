package net.jmoiron.gcyrextras;

import argent_matter.gcyr.common.data.GCYRRecipeTypes;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasRecipeTypes;
import net.jmoiron.gcyrextras.api.registries.GcyrExtrasRegistries;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

@GTAddon
public class GcyrExtrasGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GcyrExtrasRegistries.REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return GcyrExtras.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true,
                ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "mining_laser_casing"),
                new ItemStack(GcyrExtrasBlocks.MINING_LASER_CASING.get(), 2),
                "PhP", "PFP", "PwP",
                'P', new MaterialEntry(TagPrefix.plate, GTMaterials.TitaniumCarbide),
                'F', new MaterialEntry(TagPrefix.frameGt, GTMaterials.TitaniumCarbide));

        GTRecipeTypes.ASSEMBLER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "mining_laser_casing"))
                .inputItems(TagPrefix.plate, GTMaterials.TitaniumCarbide, 6)
                .inputItems(TagPrefix.frameGt, GTMaterials.TitaniumCarbide)
                .circuitMeta(6)
                .outputItems(new ItemStack(GcyrExtrasBlocks.MINING_LASER_CASING.get(), 2))
                .duration(50)
                .EUt(16)
                .save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider, true,
                ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "gas_miner_casing"),
                new ItemStack(GcyrExtrasBlocks.GAS_MINER_CASING.get(), 2),
                "PhP", "PFP", "PwP",
                'P', new MaterialEntry(TagPrefix.plateDouble, GTMaterials.Invar),
                'F', new MaterialEntry(TagPrefix.frameGt, GTMaterials.TungstenCarbide));

        GTRecipeTypes.ASSEMBLER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "gas_miner_casing"))
                .inputItems(TagPrefix.plateDouble, GTMaterials.Invar, 6)
                .inputItems(TagPrefix.frameGt, GTMaterials.TungstenCarbide)
                .circuitMeta(6)
                .outputItems(new ItemStack(GcyrExtrasBlocks.GAS_MINER_CASING.get(), 2))
                .duration(50)
                .EUt(16)
                .save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider, true,
                ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "vacuum_coupling_casing"),
                new ItemStack(GcyrExtrasBlocks.VACUUM_COUPLING_CASING.get(), 2),
                "PhP", "PFP", "PwP",
                'P', new MaterialEntry(TagPrefix.plate, GTMaterials.Brass),
                'F', new MaterialEntry(TagPrefix.frameGt, GTMaterials.HSLASteel));

        GTRecipeTypes.ASSEMBLER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "vacuum_coupling_casing"))
                .inputItems(TagPrefix.plate, GTMaterials.Brass, 6)
                .inputItems(TagPrefix.frameGt, GTMaterials.HSLASteel)
                .circuitMeta(6)
                .outputItems(new ItemStack(GcyrExtrasBlocks.VACUUM_COUPLING_CASING.get(), 2))
                .duration(50)
                .EUt(16)
                .save(provider);

        GcyrExtrasRecipeTypes.ORBITAL_MINER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "cobblestone_probe"))
                .circuitMeta(1)
                .outputItems(new ItemStack(Blocks.COBBLESTONE))
                .duration(20 * 30)
                .EUt(28)
                .save(provider);

        GcyrExtrasRecipeTypes.ORBITAL_GAS_MINER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "overworld_air_scoop"))
                .circuitMeta(1)
                .outputFluids(GTMaterials.Air.getFluid(100_000))
                .dimension(Level.OVERWORLD)
                .duration(20 * 30)
                .EUt(GTValues.VA[GTValues.IV])
                .save(provider);

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
