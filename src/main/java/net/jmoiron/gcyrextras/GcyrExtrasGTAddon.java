package net.jmoiron.gcyrextras;

import argent_matter.gcyr.common.data.GCYRRecipeTypes;
import argent_matter.gcyr.common.data.GCYRMaterials;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasRecipeTypes;
import net.jmoiron.gcyrextras.api.registries.GcyrExtrasRegistries;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;
import net.jmoiron.gcyrextras.common.data.GcyrExtrasMachines;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceKey;
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
    private static final int ORBITAL_MINER_DURATION = 20 * 15;
    private static final int ORBITAL_MINER_CHANCE = 6000;
    private static final int ORBITAL_MINER_EUT = GTValues.VA[GTValues.EV] / 2;


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

        VanillaRecipeHelper.addShapedRecipe(provider, true,
                ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "beam_receiver"),
                new ItemStack(GcyrExtrasBlocks.BEAM_RECEIVER.get(), 2),
                "WhW", "WFW", "WwW",
                'W', new MaterialEntry(TagPrefix.wireGtDouble, GTMaterials.UraniumTriplatinum),
                'F', new MaterialEntry(TagPrefix.frameGt, GCYRMaterials.Bisalloy400));

        GTRecipeTypes.ASSEMBLER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "beam_receiver"))
                .inputItems(TagPrefix.wireGtDouble, GTMaterials.UraniumTriplatinum, 6)
                .inputItems(TagPrefix.frameGt, GCYRMaterials.Bisalloy400)
                .circuitMeta(6)
                .outputItems(new ItemStack(GcyrExtrasBlocks.BEAM_RECEIVER.get(), 2))
                .duration(50)
                .EUt(16)
                .save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider, true,
                ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "beam_former"),
                new ItemStack(GcyrExtrasBlocks.BEAM_FORMER.get(), 2),
                "WhW", "WFW", "WwW",
                'W', new MaterialEntry(TagPrefix.wireGtDouble, GTMaterials.MercuryBariumCalciumCuprate),
                'F', new MaterialEntry(TagPrefix.frameGt, GTMaterials.Electrum));

        GTRecipeTypes.ASSEMBLER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "beam_former"))
                .inputItems(TagPrefix.wireGtDouble, GTMaterials.MercuryBariumCalciumCuprate, 6)
                .inputItems(TagPrefix.frameGt, GTMaterials.Electrum)
                .circuitMeta(6)
                .outputItems(new ItemStack(GcyrExtrasBlocks.BEAM_FORMER.get(), 2))
                .duration(50)
                .EUt(16)
                .save(provider);

        GTRecipeTypes.ASSEMBLY_LINE_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "orbital_mining_laser"))
                .inputItems(TagPrefix.frameGt, GTMaterials.NaquadahAlloy)
                .inputItems(GTItems.EMITTER_LuV.asStack())
                .inputItems(CustomTags.LuV_CIRCUITS, 2)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Naquadah, 2)
                .inputItems(TagPrefix.plate, GTMaterials.TitaniumCarbide, 64)
                .inputItems(TagPrefix.rod, GTMaterials.TitaniumCarbide, 16)
                .inputItems(GcyrExtrasBlocks.BEAM_FORMER.get().asItem(), 4)
                .inputItems(GcyrExtrasBlocks.BEAM_RECEIVER.get().asItem(), 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(GTValues.L * 4))
                .outputItems(GcyrExtrasMachines.ORBITAL_MINING_LASER.asStack())
                .scannerResearch(b -> b
                        .researchStack(GTMultiMachines.LARGE_MINER[GTValues.LuV].asStack())
                        .duration(2400)
                        .EUt(GTValues.VA[GTValues.IV]))
                .duration(600)
                .EUt(GTValues.VA[GTValues.LuV])
                .save(provider);

        GTRecipeTypes.ASSEMBLY_LINE_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "orbital_gas_miner"))
                .inputItems(TagPrefix.frameGt, GTMaterials.NaquadahAlloy)
                .inputItems(GTItems.FIELD_GENERATOR_LuV.asStack())
                .inputItems(CustomTags.LuV_CIRCUITS, 2)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Naquadah, 2)
                .inputItems(GcyrExtrasBlocks.GAS_MINER_CASING.get().asItem(), 8)
                .inputItems(GcyrExtrasBlocks.VACUUM_COUPLING_CASING.get().asItem(), 4)
                .inputItems(TagPrefix.cableGtSingle, GTMaterials.NiobiumTitanium, 8)
                .inputItems(TagPrefix.pipeLargeFluid, GTMaterials.Naquadah, 2)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(GTValues.L * 4))
                .outputItems(GcyrExtrasMachines.ORBITAL_GAS_MINER.asStack())
                .scannerResearch(b -> b
                        .researchStack(GTMultiMachines.FLUID_DRILLING_RIG[GTValues.EV].asStack())
                        .duration(2400)
                        .EUt(GTValues.VA[GTValues.IV]))
                .duration(600)
                .EUt(GTValues.VA[GTValues.LuV])
                .save(provider);

        // TODO: test-only recipe, keep disabled unless a simple orbital miner smoke test is needed again.
        // GcyrExtrasRecipeTypes.ORBITAL_MINER_RECIPES
        //         .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "cobblestone_probe"))
        //         .circuitMeta(1)
        //         .outputItems(new ItemStack(Blocks.COBBLESTONE))
        //         .duration(20 * 30)
        //         .EUt(28)
        //         .save(provider);

        addOrbitalMinerRecipe(provider, "luna_orbit_ores",
                orbitDimension("luna_orbit"),
                1,
                TagPrefix.getPrefix("moon"),
                GTMaterials.Bauxite,
                GTMaterials.Ilmenite,
                GTMaterials.Aluminium);

        addOrbitalMinerRecipe(provider, "mars_orbit_ores",
                orbitDimension("mars_orbit"),
                2,
                TagPrefix.getPrefix("mars"),
                GTMaterials.Tungstate,
                GTMaterials.Hematite,
                GTMaterials.Nickel,
                GTMaterials.Monazite,
                GTMaterials.Chalcopyrite,
                GTMaterials.Stibnite);

        addOrbitalMinerRecipe(provider, "mercury_orbit_ores",
                orbitDimension("mercury_orbit"),
                3,
                TagPrefix.getPrefix("mercury"),
                GTMaterials.Redstone,
                GTMaterials.Cinnabar,
                GTMaterials.Ruby);

        addOrbitalMinerRecipe(provider, "venus_orbit_ores",
                orbitDimension("venus_orbit"),
                4,
                TagPrefix.getPrefix("venus"),
                GTMaterials.Scheelite,
                GTMaterials.Bauxite,
                GTMaterials.Bastnasite,
                GTMaterials.Gold,
                GTMaterials.Sphalerite,
                GTMaterials.Cobaltite);

        GcyrExtrasRecipeTypes.ORBITAL_GAS_MINER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, "overworld_air_scoop"))
                .circuitMeta(1)
                .outputFluids(GTMaterials.Air.getFluid(100_000))
                .dimension(Level.OVERWORLD)
                .duration(20 * 5)
                .EUt(GTValues.VA[GTValues.LV] / 2)
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

    private static void addOrbitalMinerRecipe(Consumer<FinishedRecipe> provider, String name,
                                              ResourceKey<Level> dimension, int circuitMeta, TagPrefix orePrefix,
                                              Material... outputs) {
        var builder = GcyrExtrasRecipeTypes.ORBITAL_MINER_RECIPES
                .recipeBuilder(ResourceLocation.fromNamespaceAndPath(GcyrExtras.MOD_ID, name))
                .circuitMeta(circuitMeta)
                .inputFluids(GTMaterials.SodiumPotassium.getFluid(5))
                .dimension(dimension)
                .duration(ORBITAL_MINER_DURATION)
                .EUt(ORBITAL_MINER_EUT);

        for (Material output : outputs) {
            builder.chancedOutput(orePrefix, output, ORBITAL_MINER_CHANCE, 0);
        }

        builder.save(provider);
    }

    private static ResourceKey<Level> orbitDimension(String path) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("gcyr", path));
    }
}
