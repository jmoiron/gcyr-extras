package net.jmoiron.gcyrextras.common.item;

import net.jmoiron.gcyrextras.common.data.GcyrExtrasBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SpacestationCoreItem extends BlockItem {

    public SpacestationCoreItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            double x = player.getX() + player.getLookAngle().x * 4.5;
            double y = player.getEyeY() + player.getLookAngle().y * 4.5;
            double z = player.getZ() + player.getLookAngle().z * 4.5;
            BlockPos pos = BlockPos.containing(x, y, z);

            if (level.isInWorldBounds(pos) && level.getBlockState(pos).canBeReplaced()) {
                level.setBlock(pos, GcyrExtrasBlocks.SPACESTATION_CORE.get().defaultBlockState(), 3);
                if (!player.isCreative()) {
                    if (usedHand == InteractionHand.MAIN_HAND) {
                        player.getInventory().removeFromSelected(false);
                    } else {
                        player.getInventory().removeItem(Inventory.SLOT_OFFHAND, 1);
                    }
                }
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.gcyrextras.spacestation_core.angel_block")
                .withStyle(ChatFormatting.GRAY));
    }
}
