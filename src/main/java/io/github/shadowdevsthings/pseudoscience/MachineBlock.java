package io.github.shadowdevsthings.pseudoscience;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MachineBlock extends BlockWithEntity {

	public static final MapCodec<MachineBlock> CODEC = Block.method_54094(MachineBlock::new);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public MachineBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends MachineBlock> getCodec() {
		return CODEC;
	}


	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING);
	}


	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
	}

	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MachineBlockEntity(pos, state);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}



	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.SUCCESS;

		if (!(world.getBlockEntity(pos) instanceof MachineBlockEntity blockEntity)) {
			return ActionResult.FAIL;
		}

		if (!player.getStackInHand(hand).isEmpty()) {
			// Check what is the first open slot and put an item from the player's hand there
			if (blockEntity.getStack(0).isEmpty()) {
				// Put the stack the player is holding into the inventory
				blockEntity.setStack(0, player.getStackInHand(hand).copy());
				// Remove the stack from the player's hand
				player.getStackInHand(hand).setCount(0);
			} else if (blockEntity.getStack(1).isEmpty()) {
				blockEntity.setStack(1, player.getStackInHand(hand).copy());
				player.getStackInHand(hand).setCount(0);
			} else {
				// If the inventory is full we'll notify the player
				player.sendMessage(Text.literal("The inventory is full! The first slot holds ")
					.append(blockEntity.getStack(0).getName())
					.append(" and the second slot holds ")
					.append(blockEntity.getStack(1).getName()), true);
			}
		} else {
			// If the player is not holding anything we'll get give him the items in the block entity one by one

			// Find the first slot that has an item and give it to the player
			if (!blockEntity.getStack(1).isEmpty()) {
				// Give the player the stack in the inventory
				player.getInventory().offerOrDrop(blockEntity.getStack(1));
				// Remove the stack from the inventory
				blockEntity.removeStack(1);
			} else if (!blockEntity.getStack(0).isEmpty()) {
				player.getInventory().offerOrDrop(blockEntity.getStack(0));
				blockEntity.removeStack(0);
			} else {
				return ActionResult.FAIL;
			}
		}
		return ActionResult.SUCCESS;
	}

}
