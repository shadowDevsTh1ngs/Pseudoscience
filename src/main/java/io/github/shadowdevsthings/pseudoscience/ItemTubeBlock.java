package io.github.shadowdevsthings.pseudoscience;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemTubeBlock extends Block {


	public static final MapCodec<MachineBlock> CODEC = Block.method_54094(MachineBlock::new);
	public static final EnumProperty<ConduitConnection> UP = EnumProperty.of("up", ConduitConnection.class);
	public static final EnumProperty<ConduitConnection> DOWN = EnumProperty.of("down", ConduitConnection.class);
	public static final EnumProperty<ConduitConnection> NORTH = EnumProperty.of("north", ConduitConnection.class);
	public static final EnumProperty<ConduitConnection> SOUTH = EnumProperty.of("south", ConduitConnection.class);
	public static final EnumProperty<ConduitConnection> EAST = EnumProperty.of("east", ConduitConnection.class);
	public static final EnumProperty<ConduitConnection> WEST = EnumProperty.of("west", ConduitConnection.class);
	public static final BooleanProperty STRAIGHT = BooleanProperty.of("straight");

	public ItemTubeBlock(Settings settings) {
		super(settings);

		setDefaultState(getDefaultState().with(UP, ConduitConnection.NONE).with(DOWN, ConduitConnection.NONE).with(NORTH, ConduitConnection.NONE).with(SOUTH, ConduitConnection.NONE).with(EAST, ConduitConnection.NONE).with(WEST, ConduitConnection.NONE).with(STRAIGHT, false));
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

			newState = getConnectionState(NORTH, newState, world, pos.north());
			newState = getConnectionState(SOUTH, newState, world, pos.south());
			newState = getConnectionState(EAST, newState, world, pos.east());
			newState = getConnectionState(WEST, newState, world, pos.west());
			newState = getConnectionState(UP, newState, world, pos.up());
			newState = getConnectionState(DOWN, newState, world, pos.down());

			boolean cNorth = newState.get(NORTH).isConnected();
			boolean cSouth = newState.get(SOUTH).isConnected();
			boolean cEast = newState.get(EAST).isConnected();
			boolean cWest = newState.get(WEST).isConnected();
			boolean cUp = newState.get(UP).isConnected();
			boolean cDown = newState.get(DOWN).isConnected();

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

		BlockState newState = world.getBlockState(pos);
		ItemTubeBlockEntity entity;

		if(newState.get(NORTH) == ConduitConnection.CONDUIT) {
			entity = ((ItemTubeBlock) world.getBlockState(pos.north()).getBlock()).getEntity(world, pos);
		}
		else if(newState.get(SOUTH) == ConduitConnection.CONDUIT) {
			entity = ((ItemTubeBlock) world.getBlockState(pos.south()).getBlock()).getEntity(world, pos);
		}
		else if(newState.get(EAST) == ConduitConnection.CONDUIT) {
			entity = ((ItemTubeBlock) world.getBlockState(pos.east()).getBlock()).getEntity(world, pos);
		}
		else if(newState.get(WEST) == ConduitConnection.CONDUIT) {
			entity = ((ItemTubeBlock) world.getBlockState(pos.west()).getBlock()).getEntity(world, pos);
		}
		else if(newState.get(UP) == ConduitConnection.CONDUIT) {
			entity = ((ItemTubeBlock) world.getBlockState(pos.up()).getBlock()).getEntity(world, pos);
		}
		else if(newState.get(DOWN) == ConduitConnection.CONDUIT) {
			entity = ((ItemTubeBlock) world.getBlockState(pos.down()).getBlock()).getEntity(world, pos);
		}
		else {
			entity = new ItemTubeBlockEntity(pos, newState);
		}
		world.addBlockEntity(entity);
		getEntity(world, pos);
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
			player.sendMessage(Text.literal(world.getTime() + " ==========================="), false);
			player.sendMessage(Text.literal("North " + world.getBlockState(pos).get(NORTH)), false);
			player.sendMessage(Text.literal("South " + world.getBlockState(pos).get(SOUTH)), false);
			player.sendMessage(Text.literal("East " + world.getBlockState(pos).get(EAST)), false);
			player.sendMessage(Text.literal("West " + world.getBlockState(pos).get(WEST)), false);
			player.sendMessage(Text.literal("Up " + world.getBlockState(pos).get(UP)), false);
			player.sendMessage(Text.literal("Down " + world.getBlockState(pos).get(DOWN)), false);
			player.sendMessage(Text.literal("Entity " + getEntity(world, pos)), false);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	public BlockState getConnectionState(EnumProperty<ConduitConnection> side, BlockState state, World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if(state.get(side).isDisconnected()) {
			return state.with(side, ConduitConnection.DISCONNECTED);
		} else if(block instanceof ItemTubeBlock) {
			return state.with(side, ConduitConnection.CONDUIT);
		} else if(world.getBlockEntity(pos) instanceof Inventory) {
			return state.with(side, ConduitConnection.CONNECTION);
		} else {
			return state.with(side, ConduitConnection.NONE);
		}
	}

	public ItemTubeBlockEntity getEntity(World world, BlockPos pos) {
		if(!(world.getBlockEntity(pos) instanceof ItemTubeBlockEntity)) {
			Pseudoscience.LOGGER.info("fuck");
		}
		return (ItemTubeBlockEntity) world.getBlockEntity(pos);
	}





}
