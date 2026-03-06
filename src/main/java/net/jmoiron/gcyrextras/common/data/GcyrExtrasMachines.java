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
import com.gregtechceu.gtceu.common.data.GTMaterials;
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

import static com.gregtechceu.gtceu.api.pattern.Predicates.air;
import static com.gregtechceu.gtceu.api.pattern.Predicates.any;
import static com.gregtechceu.gtceu.api.pattern.Predicates.autoAbilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.controller;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TUNGSTENSTEEL_ROBUST;
import static com.gregtechceu.gtceu.common.data.GTBlocks.FUSION_GLASS;
import static com.gregtechceu.gtceu.common.data.GTMaterialBlocks.MATERIAL_BLOCKS;

public final class GcyrExtrasMachines {

    public static final MultiblockMachineDefinition ORBITAL_MINING_LASER = GcyrExtrasRegistries.REGISTRATE
            .multiblock("orbital_mining_laser", OrbitalMiningLaserMachine::new)
            .langValue("Orbital Mining Laser")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowFlip(false)
            .allowExtendedFacing(false)
            .recipeType(com.gregtechceu.gtceu.common.data.GTRecipeTypes.DUMMY_RECIPES)
            .appearanceBlock(() -> CASING_TUNGSTENSTEEL_ROBUST.get())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .pattern(def -> FactoryBlockPattern.start()
                    .aisle("               ", "               ", "      XXX      ", "      XXX      ", "      XXX      ", "               ", "               ")
                    .aisle("               ", "               ", "    XXGGGXX    ", "    XXGGGXX    ", "    XXGGGXX    ", "               ", "               ")
                    .aisle("               ", "               ", "   XGG###GGX   ", "   XGG###GGX   ", "   XGG###GGX   ", "               ", "               ")
                    .aisle("               ", "               ", "  XG#######GX  ", "  XG#######GX  ", "  XG#######GX  ", "               ", "               ")
                    .aisle("               ", "               ", " XG###BGB###GX ", " XG###BGB###GX ", " XG###BGB###GX ", "               ", "               ")
                    .aisle("               ", "       G       ", " XG###BFB###GX ", " XG###BFB###GX ", " XG###BFB###GX ", "       G       ", "               ")
                    .aisle("      XXX      ", "      X X      ", " XG###GGG###GX ", " XG###GGG###GX ", " XG###GGG###GX ", "      X X      ", "      XXX      ")
                    .aisle("      XGX      ", "     G   G     ", " XG###BFB###GX ", " XG###BFB###GX ", " XG###BFB###GX ", "     G   G     ", "      XGX      ")
                    .aisle("      XXX      ", "      X X      ", " XG###BGB###GX ", " XG###BGB###GX ", " XG###BGB###GX ", "      X X      ", "      XXX      ")
                    .aisle("               ", "       G       ", "  XG#######GX  ", "  XG#######GX  ", "  XG#######GX  ", "       G       ", "               ")
                    .aisle("               ", "               ", "   XGG###GGX   ", "   XGG###GGX   ", "   XGG###GGX   ", "               ", "               ")
                    .aisle("               ", "               ", "    XXGGGXX    ", "    XXGGGXX    ", "    XXGGGXX    ", "               ", "               ")
                    .aisle("               ", "               ", "      XXX      ", "      XSX      ", "      XXX      ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .where('S', controller(blocks(def.getBlock())))
                    .where('X', blocks(CASING_TUNGSTENSTEEL_ROBUST.get()).setMinGlobalLimited(24)
                            .or(autoAbilities(def.getRecipeTypes()))
                            .or(autoAbilities(true, false, true)))
                    .where('G', blocks(FUSION_GLASS.get()).or(blocks(CASING_TUNGSTENSTEEL_ROBUST.get())))
                    .where('B', blocks(GcyrExtrasBlocks.BEAM_RECEIVER.get()))
                    .where('F', blocks(MATERIAL_BLOCKS.get(com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt,
                            GTMaterials.NaquadahAlloy).get()))
                    .where('#', air())
                    .where(' ', any())
                    .build())
            .model(createLocalWorkableCasingMachineModel(
                            GcyrExtras.id("block/casings/solid/beam_receiver"),
                            GTCEu.id("block/multiblock/assembly_line"))
                    .andThen(b -> b.addDynamicRenderer(GcyrExtrasDynamicRenderHelper::createOrbitalMiningLaserRender)))
            .hasBER(true)
            .register();

    private GcyrExtrasMachines() {}

    public static void init() {}

    private static MachineBuilder.ModelInitializer createLocalWorkableCasingMachineModel(ResourceLocation baseCasingTexture,
                                                                                         ResourceLocation overlayDir) {
        return (DataGenContext<Block, ? extends Block> ctx, GTBlockstateProvider prov, MachineModelBuilder<BlockModelBuilder> builder) -> {
            WorkableOverlays overlays = WorkableOverlays.get(overlayDir, prov.getExistingFileHelper());

            builder.forAllStates(state -> {
                RecipeLogic.Status status = state.getValue(GTMachineModelProperties.RECIPE_LOGIC_STATUS);
                var model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GcyrExtras.id("block/machine/template/cube_all/sided")))
                        .texture("all", baseCasingTexture);
                return GTMachineModels.addWorkableOverlays(overlays, status, model);
            });
            builder.addTextureOverride("all", baseCasingTexture);
        };
    }
}
