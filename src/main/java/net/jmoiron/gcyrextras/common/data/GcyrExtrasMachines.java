package net.jmoiron.gcyrextras.common.data;

import argent_matter.gcyr.common.data.GCYRBlocks;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.gregtechceu.gtceu.data.model.builder.MachineModelBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.api.registries.GcyrExtrasRegistries;
import net.jmoiron.gcyrextras.client.renderer.machine.GcyrExtrasDynamicRenderHelper;
import net.jmoiron.gcyrextras.common.machine.multiblock.electric.OrbitalGasMinerMachine;
import net.jmoiron.gcyrextras.common.machine.multiblock.electric.OrbitalMiningLaserMachine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.any;
import static com.gregtechceu.gtceu.api.pattern.Predicates.autoAbilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.controller;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.BATCH_MODE;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.OC_NON_PERFECT_SUBTICK;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_STAINLESS_CLEAN;
import static com.gregtechceu.gtceu.common.data.GTBlocks.COMPUTER_HEAT_VENT;
import static com.gregtechceu.gtceu.common.data.GTBlocks.FIREBOX_TUNGSTENSTEEL;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_LAMINATED_GLASS;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_LASER_SAFE_ENGRAVING;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_VIBRATION_SAFE;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.HEAT_VENT;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.PARALLEL_HATCH;

public final class GcyrExtrasMachines {

    public static final MultiblockMachineDefinition ORBITAL_MINING_LASER = GcyrExtrasRegistries.REGISTRATE
            .multiblock("orbital_mining_laser", OrbitalMiningLaserMachine::new)
            .langValue("Orbital Mining Laser")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowFlip(false)
            .allowExtendedFacing(false)
            .recipeType(GcyrExtrasRecipeTypes.ORBITAL_MINER_RECIPES)
            .recipeModifiers(PARALLEL_HATCH, OC_NON_PERFECT_SUBTICK, BATCH_MODE)
            .appearanceBlock(() -> GcyrExtrasBlocks.MINING_LASER_CASING.get())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .pattern(def -> FactoryBlockPattern.start()
                    .aisle("               ", "               ", "               ", "      bbb      ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "      bbb      ", "    bb   bb    ", "      bbb      ", "               ", "               ")
                    .aisle("               ", "               ", "    bb d bb    ", "   b  ded  b   ", "    bb d bb    ", "               ", "               ")
                    .aisle("               ", "               ", "   b       b   ", "  b bb   bb b  ", "   b       b   ", "               ", "               ")
                    .aisle("               ", "               ", "  b         b  ", " b b       b b ", "  b         b  ", "               ", "               ")
                    .aisle("               ", "               ", "  b         b  ", " b b   f   b b ", "  b         b  ", "       g       ", "       g       ")
                    .aisle("               ", "       g       ", " b     g     b ", "b d   hhh   d b", " b    ggg    b ", "      iii      ", "      hjh      ")
                    .aisle("       h       ", "      g g      ", " bd   g g   db ", "b e  fh hf  e b", " bd   g g   db ", "     gi ig     ", "     gjhjg     ")
                    .aisle("               ", "       g       ", " b     g     b ", "b d   hhh   d b", " b    ggg    b ", "      iii      ", "      hjh      ")
                    .aisle("               ", "               ", "  b         b  ", " b b   f   b b ", "  b         b  ", "       g       ", "       g       ")
                    .aisle("               ", "               ", "  b         b  ", " b b       b b ", "  b         b  ", "               ", "               ")
                    .aisle("               ", "               ", "   b       b   ", "  b bb   bb b  ", "   b       b   ", "               ", "               ")
                    .aisle("               ", "               ", "    bb d bb    ", "   b  ded  b   ", "    bb d bb    ", "               ", "               ")
                    .aisle("               ", "               ", "      bbb      ", "    bb   bb    ", "      bbb      ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "      bkb      ", "               ", "               ", "               ")
                    .where('k', controller(blocks(def.getBlock())))
                    .where('b', blocks(GcyrExtrasBlocks.MINING_LASER_CASING.get()).setMinGlobalLimited(24)
                            .or(autoAbilities(def.getRecipeTypes(), true, false, true, true, true, false))
                            .or(autoAbilities(true, false, true)))
                    .where('d', blocks(CASING_LASER_SAFE_ENGRAVING.get()))
                    .where('e', blocks(GcyrExtrasBlocks.BEAM_FORMER.get()))
                    .where('f', blocks(GcyrExtrasBlocks.BEAM_RECEIVER.get()))
                    .where('g', blocks(CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where('h', blocks(CASING_LAMINATED_GLASS.get()))
                    .where('i', blocks(HEAT_VENT.get()))
                    .where('j', blocks(CASING_VIBRATION_SAFE.get()))
                    .where(' ', any())
                    .build())
            .model(createLocalWorkableCasingMachineModel(
                            GcyrExtras.id("block/casings/solid/mining_laser_casing"),
                            GcyrExtras.id("block/machines/laser_engraver"))
                    .andThen(b -> b.addDynamicRenderer(GcyrExtrasDynamicRenderHelper::createOrbitalMiningLaserRender)))
            .hasBER(true)
            .register();

    public static final MultiblockMachineDefinition ORBITAL_GAS_MINER = GcyrExtrasRegistries.REGISTRATE
            .multiblock("orbital_gas_miner", OrbitalGasMinerMachine::new)
            .langValue("Orbital Gas Miner")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowFlip(false)
            .allowExtendedFacing(false)
            .recipeType(GcyrExtrasRecipeTypes.ORBITAL_GAS_MINER_RECIPES)
            .appearanceBlock(() -> GcyrExtrasBlocks.GAS_MINER_CASING.get())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .pattern(def -> FactoryBlockPattern.start()
                    .aisle("                 ", "                 ", "                 ", "        d        ", "        d        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("        e        ", "        d        ", "      bbbbb      ", "                 ", "                 ", "        d        ", "        d        ", "        d        ", "        d        ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("        e        ", "        d        ", "     bbbbbbb     ", "                 ", "                 ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "        d        ", "    bbb d bbb    ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        d        ", "        l        ")
                    .aisle("                 ", "                 ", "   bbb     bbb   ", "                 ", "                 ", "                 ", "                 ", "                 ", "        f        ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "                 ", "  bbb       bbb  ", "       ggg       ", "       hhh       ", "       iji       ", "       iji       ", "      fijif      ", "      lllll      ", "       iji       ", "        j        ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "                 ", "  bb         bb  ", "      g   g      ", "      hfffh      ", "      i   i      ", "      i   i      ", "      i   i      ", "      l   l      ", "      i   i      ", "       i i       ", "       mmm       ", "       ggg       ", "                 ")
                    .aisle("  ee         ee  ", " dddd       dddd ", " dbbd       dbbd ", "dd  d g   g d  dd", "dd  d hf fh d  dd", " dddd j   j dddd ", " dddd j   j dddd ", "  ddd j   j ddd  ", "  dddfl   lfddd  ", "   dd j   j dd   ", "   dd j   j dd   ", "    d  mom  d    ", "    d  g g  d    ", "    l       l    ")
                    .aisle("                 ", "                 ", "  bb         bb  ", "      g   g      ", "      hfffh      ", "      i   i      ", "      i   i      ", "      i   i      ", "      l   l      ", "      i   i      ", "       i i       ", "       mmm       ", "       ggg       ", "                 ")
                    .aisle("                 ", "                 ", "  bbb       bbb  ", "       ggg       ", "       hhh       ", "       iji       ", "       iji       ", "      fikif      ", "      lllll      ", "       iji       ", "        j        ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "                 ", "   bbb     bbb   ", "                 ", "                 ", "        f        ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "        d        ", "    bbb d bbb    ", "        d        ", "       ddd       ", "       ddd       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("        e        ", "        d        ", "     bbbbbbb     ", "       ddd       ", "       ddd       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("        e        ", "        d        ", "      bbbbb      ", "       ddd       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "        d        ", "      bbbbb      ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    .aisle("                 ", "                 ", "       bbb       ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ", "                 ")
                    //.where('a', air())
                    .where('b', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, TungstenCarbide)))
                    .where('d', blocks(CASING_STAINLESS_CLEAN.get()))
                    .where('e', blocks(GCYRBlocks.BASIC_ROCKET_MOTOR.get()))
                    .where('f', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, StainlessSteel)))
                    .where('g', blocks(COMPUTER_HEAT_VENT.get()))
                    .where('h', blocks(FIREBOX_TUNGSTENSTEEL.get()))
                    //.where("i", Predicates.blocks("gtceu:heatproof_machine_casing"))
                    .where('j', blocks(CASING_HIGH_TEMPERATURE_SMELTING.get()))
                    .where('k', controller(blocks(def.getBlock())))
                    .where('l', blocks(GcyrExtrasBlocks.VACUUM_COUPLING_CASING.get()))
                    .where('m', blocks(HEAT_VENT.get()))
                    .where('o', abilities(PartAbility.MUFFLER).setMinGlobalLimited(1).setMaxGlobalLimited(1))
                    // Placeholder pattern. Replace these aisles with the real orbital gas miner structure.
                    .where('i', blocks(GcyrExtrasBlocks.GAS_MINER_CASING.get()).setMinGlobalLimited(10)
                            .or(autoAbilities(def.getRecipeTypes(), true, false, true, false, true, true))
                            .or(autoAbilities(true, false, true)))
                    .where(' ', any())
                    .build())
            .model(createLocalWorkableCasingMachineModel(
                    GcyrExtras.id("block/casings/solid/gas_miner_casing"),
                    GcyrExtras.id("block/machines/centrifuge")))
            .register();

    private GcyrExtrasMachines() {}

    public static void init() {}

    private static MachineBuilder.ModelInitializer createLocalWorkableCasingMachineModel(ResourceLocation baseCasingTexture,
                                                                                         ResourceLocation overlayDir) {
        return (DataGenContext<Block, ? extends Block> ctx, GTBlockstateProvider prov, MachineModelBuilder<BlockModelBuilder> builder) -> {
            builder.forAllStates(state -> {
                RecipeLogic.Status status = state.getValue(GTMachineModelProperties.RECIPE_LOGIC_STATUS);
                var model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GcyrExtras.id("block/machine/template/cube_all/sided")))
                        .texture("all", baseCasingTexture);
                addFrontOverlay(model, overlayDir, status);
                return new net.minecraftforge.client.model.generators.ConfiguredModel[] {
                        new net.minecraftforge.client.model.generators.ConfiguredModel(model)
                };
            });
            builder.addTextureOverride("all", baseCasingTexture);
        };
    }

    private static void addFrontOverlay(BlockModelBuilder model, ResourceLocation overlayDir,
                                        RecipeLogic.Status status) {
        ResourceLocation front = overlayDir.withSuffix("/overlay_front");
        ResourceLocation frontEmissive = overlayDir.withSuffix("/overlay_front_emissive");
        ResourceLocation activeFront = overlayDir.withSuffix("/overlay_front_active");
        ResourceLocation activeFrontEmissive = overlayDir.withSuffix("/overlay_front_active_emissive");
        if (status == RecipeLogic.Status.WORKING || status == RecipeLogic.Status.WAITING) {
            model.texture("overlay_front", activeFront);
            model.texture("overlay_front_emissive", activeFrontEmissive);
            return;
        }
        model.texture("overlay_front", front);
        model.texture("overlay_front_emissive", frontEmissive);
    }
}
