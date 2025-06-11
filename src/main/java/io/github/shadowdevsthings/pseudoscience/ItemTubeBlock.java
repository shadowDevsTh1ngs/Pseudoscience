package io.github.shadowdevsthings.pseudoscience;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemTubeBlock extends Block {


	public static final MapCodec<MachineBlock> CODEC = Block.method_54094(MachineBlock::new);
	public static final BooleanProperty UP = Properties.UP;
	public static final BooleanProperty DOWN = Properties.DOWN;
	public static final BooleanProperty NORTH = Properties.NORTH;
	public static final BooleanProperty SOUTH = Properties.SOUTH;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty WEST = Properties.WEST;
	public static final BooleanProperty STRAIGHT = BooleanProperty.of("straight");

	public ItemTubeBlock(Settings settings) {
		super(settings);

		setDefaultState(getDefaultState().with(UP, false).with(DOWN, false).with(NORTH, false).with(SOUTH, false).with(EAST, false).with(WEST, false).with(STRAIGHT, false));
	}

	@Override
	protected MapCodec<? extends MachineBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP);
		builder.add(DOWN);
		builder.add(NORTH);
		builder.add(SOUTH);
		builder.add(EAST);
		builder.add(WEST);
		builder.add(STRAIGHT);
	}

	public void update(World world, BlockPos pos, BlockState state) {
		if(!world.isClient) {
			BlockState newState = state;

			newState = newState.with(NORTH, isValidConnection(world, pos.north()));
			newState = newState.with(SOUTH, isValidConnection(world, pos.south()));
			newState = newState.with(EAST, isValidConnection(world, pos.east()));
			newState = newState.with(WEST, isValidConnection(world, pos.west()));
			newState = newState.with(UP, isValidConnection(world, pos.up()));
			newState = newState.with(DOWN, isValidConnection(world, pos.down()));

			boolean cNorth = newState.get(NORTH);
			boolean cSouth = newState.get(SOUTH);
			boolean cEast = newState.get(EAST);
			boolean cWest = newState.get(WEST);
			boolean cUp = newState.get(UP);
			boolean cDown = newState.get(DOWN);

			if ((cNorth && cSouth && !cEast && !cWest && !cUp && !cDown) ^ (cEast && cWest && !cNorth && !cSouth && !cUp && !cDown) ^ (cUp && cDown && !cEast && !cWest && !cNorth && !cSouth)) {
				newState = newState.with(STRAIGHT, true);
			} else {
				newState = newState.with(STRAIGHT, false);
			}

			world.setBlockState(pos, newState);
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		update(world, pos, state);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		update(world, pos, state);
	}


	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(!world.isClient) {
			//Pseudoscience.LOGGER.info("state replace");
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (ItemStack.itemsMatch(player.getStackInHand(hand), Items.STICK.getDefaultStack())) {
//			Pseudoscience.LOGGER.info("North " + world.getBlockState(pos).get(NORTH));
//			Pseudoscience.LOGGER.info("South " + world.getBlockState(pos).get(SOUTH));
//			Pseudoscience.LOGGER.info("East " + world.getBlockState(pos).get(EAST));
//			Pseudoscience.LOGGER.info("West " + world.getBlockState(pos).get(WEST));
//			Pseudoscience.LOGGER.info("Up " + world.getBlockState(pos).get(UP));
//			Pseudoscience.LOGGER.info("Down " + world.getBlockState(pos).get(DOWN));
//			Pseudoscience.LOGGER.info("Straight " + world.getBlockState(pos).get(STRAIGHT));
			player.sendMessage(Text.literal(world.getTime() + " ==========================="), false);
			player.sendMessage(Text.literal("North " + world.getBlockState(pos).get(NORTH)), false);
			player.sendMessage(Text.literal("South " + world.getBlockState(pos).get(SOUTH)), false);
			player.sendMessage(Text.literal("East " + world.getBlockState(pos).get(EAST)), false);
			player.sendMessage(Text.literal("West " + world.getBlockState(pos).get(WEST)), false);
			player.sendMessage(Text.literal("Up " + world.getBlockState(pos).get(UP)), false);
			player.sendMessage(Text.literal("Down " + world.getBlockState(pos).get(DOWN)), false);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	public boolean isValidConnection(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof ItemTubeBlock || world.getBlockEntity(pos) instanceof Inventory;
	}

}
