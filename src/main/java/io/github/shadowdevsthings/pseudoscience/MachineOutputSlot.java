package io.github.shadowdevsthings.pseudoscience;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MachineOutputSlot extends Slot {

	public MachineOutputSlot(PlayerEntity player, Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}
}
