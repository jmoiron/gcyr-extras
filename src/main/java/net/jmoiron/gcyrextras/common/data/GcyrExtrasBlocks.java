package net.jmoiron.gcyrextras.common.data;

import argent_matter.gcyr.common.block.FuelTankBlock;
import argent_matter.gcyr.common.block.RocketMotorBlock;
import argent_matter.gcyr.common.data.GCYRBlocks;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.api.block.ExtraFuelTankProperties;
import net.jmoiron.gcyrextras.api.block.ExtraRocketMotorType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GcyrExtrasBlocks {

    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GcyrExtras.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GcyrExtras.MOD_ID);

    public static final Map<ExtraRocketMotorType, RegistryObject<RocketMotorBlock>> MOTORS =
            new EnumMap<>(ExtraRocketMotorType.class);
    public static final Map<ExtraFuelTankProperties, RegistryObject<FuelTankBlock>> TANKS =
            new EnumMap<>(ExtraFuelTankProperties.class);
    public static final RegistryObject<Block> BEAM_RECEIVER = registerSimpleBlock("beam_receiver");
    public static final RegistryObject<Block> BEAM_FORMER = registerSimpleBlock("beam_former");

    static {
        for (ExtraRocketMotorType type : ExtraRocketMotorType.values()) {
            RegistryObject<RocketMotorBlock> block = BLOCKS.register(
                    type.getSerializedName() + "_rocket_motor",
                    () -> new RocketMotorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), type));
            MOTORS.put(type, block);
            ITEMS.register(type.getSerializedName() + "_rocket_motor",
                    () -> new MotorBlockItem(block.get(), type));
        }

        for (ExtraFuelTankProperties props : ExtraFuelTankProperties.values()) {
            RegistryObject<FuelTankBlock> block = BLOCKS.register(
                    props.getSerializedName() + "_fuel_tank",
                    () -> new FuelTankBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), props));
            TANKS.put(props, block);
            ITEMS.register(props.getSerializedName() + "_fuel_tank",
                    () -> new TankBlockItem(block.get(), props));
        }
    }

    private static class MotorBlockItem extends BlockItem {
        private final ExtraRocketMotorType type;

        MotorBlockItem(Block block, ExtraRocketMotorType type) {
            super(block, new Item.Properties());
            this.type = type;
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(Component.translatable("tooltip.gcyrextras.tier", type.getTier())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.gcyrextras.motor.carry_weight", type.getMaxCarryWeight())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.gcyrextras.motor.motor_count", type.getMotorCount())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static class TankBlockItem extends BlockItem {
        private final ExtraFuelTankProperties props;

        TankBlockItem(Block block, ExtraFuelTankProperties props) {
            super(block, new Item.Properties());
            this.props = props;
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(Component.translatable("tooltip.gcyrextras.tier", props.getTier())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.gcyrextras.tank.fluid_capacity",
                            props.getFuelStorage() / 1000)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    public static void registerWithGcyr() {
        MOTORS.forEach((type, ro) -> GCYRBlocks.ALL_ROCKET_MOTORS.put(type, ro::get));
        TANKS.forEach((props, ro) -> GCYRBlocks.ALL_FUEL_TANKS.put(props, ro::get));
    }

    private static RegistryObject<Block> registerSimpleBlock(String name) {
        RegistryObject<Block> block = BLOCKS.register(name,
                () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
