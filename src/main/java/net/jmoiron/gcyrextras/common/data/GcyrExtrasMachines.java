package net.jmoiron.gcyrextras.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.gregtechceu.gtceu.client.model.machine.overlays.WorkableOverlays;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.data.model.builder.MachineModelBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.api.registries.GcyrExtrasRegistries;
import net.jmoiron.gcyrextras.client.renderer.machine.GcyrExtrasDynamicRenderHelper;
import net.jmoiron.gcyrextras.common.machine.multiblock.electric.OrbitalMiningLaserMachine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

import static com.gregtechceu.gtceu.api.pattern.Predicates.any;
import static com.gregtechceu.gtceu.api.pattern.Predicates.autoAbilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.controller;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_LAMINATED_GLASS;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TUNGSTENSTEEL_ROBUST;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_LASER_SAFE_ENGRAVING;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_VIBRATION_SAFE;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.HEAT_VENT;

public final class GcyrExtrasMachines {

    public static final MultiblockMachineDefinition ORBITAL_MINING_LASER = GcyrExtrasRegistries.REGISTRATE
            .multiblock("orbital_mining_laser", OrbitalMiningLaserMachine::new)
            .langValue("Orbital Mining Laser")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowFlip(false)
            .allowExtendedFacing(false)
            .recipeType(GcyrExtrasRecipeTypes.ORBITAL_MINER_RECIPES)
            .appearanceBlock(() -> CASING_TUNGSTENSTEEL_ROBUST.get())
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
                    .where('b', blocks(CASING_TUNGSTENSTEEL_ROBUST.get()).setMinGlobalLimited(24)
                            .or(autoAbilities(def.getRecipeTypes()))
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
                            GcyrExtras.id("block/casings/solid/beam_receiver"),
                            GcyrExtras.id("block/casings/solid/orbital_mining_laser_front"),
                            GTCEu.id("block/multiblock/assembly_line"))
                    .andThen(b -> b.addDynamicRenderer(GcyrExtrasDynamicRenderHelper::createOrbitalMiningLaserRender)))
            .hasBER(true)
            .register();

    private GcyrExtrasMachines() {}

    public static void init() {}

    private static MachineBuilder.ModelInitializer createLocalWorkableCasingMachineModel(ResourceLocation baseCasingTexture,
                                                                                         ResourceLocation frontCasingTexture,
                                                                                         ResourceLocation overlayDir) {
        return (DataGenContext<Block, ? extends Block> ctx, GTBlockstateProvider prov, MachineModelBuilder<BlockModelBuilder> builder) -> {
            WorkableOverlays overlays = WorkableOverlays.get(overlayDir, prov.getExistingFileHelper());

            builder.forAllStates(state -> {
                RecipeLogic.Status status = state.getValue(GTMachineModelProperties.RECIPE_LOGIC_STATUS);
                var model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GcyrExtras.id("block/machine/template/cube_all/sided_front")))
                        .texture("all", baseCasingTexture)
                        .texture("front", frontCasingTexture);
                return GTMachineModels.addWorkableOverlays(overlays, status, model);
            });
            builder.addTextureOverride("all", baseCasingTexture);
        };
    }
}
