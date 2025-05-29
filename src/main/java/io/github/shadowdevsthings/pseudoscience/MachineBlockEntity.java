package io.github.shadowdevsthings.pseudoscience;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class MachineBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory {
	private final DefaultedList<ItemStack> items;


	public MachineBlockEntity(BlockPos pos, BlockState state) {
		super(PseudoscienceBlocks.MACHINE_BLOCK_ENTITY, pos, state);
		items = DefaultedList.ofSize(9, ItemStack.EMPTY);
	}

	public MachineBlockEntity(BlockPos pos, BlockState state, int inventorySize) {
		super(PseudoscienceBlocks.MACHINE_BLOCK_ENTITY, pos, state);
		items = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return items;
	}

	// These Methods are from the NamedScreenHandlerFactory Interface
	// createMenu creates the ScreenHandler itself
	// `getDisplayName` will Provide its name which is normally shown at the top

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		// We provide *this* to the screenHandler as our class Implements Inventory
		// Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
		return new MachineScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable(getCachedState().getBlock().getTranslationKey());
	}


	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		Inventories.readNbt(nbt, items);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		Inventories.writeNbt(nbt, items);
	}
}
