package net.jmoiron.gcyrextras.common.block;

import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SpacestationCoreBlock extends Block {

    public SpacestationCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
                                       FluidState fluid) {
        if (!player.isCreative()) {
            player.getInventory().placeItemBackInInventory(GcyrExtrasBlocks.SPACESTATION_CORE_ITEM.get().getDefaultInstance(), true);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
