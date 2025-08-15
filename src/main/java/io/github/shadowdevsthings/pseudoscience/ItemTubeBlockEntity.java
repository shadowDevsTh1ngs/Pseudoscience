package io.github.shadowdevsthings.pseudoscience;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ItemTubeBlockEntity extends BlockEntity {

	public ItemTubeBlockEntity(BlockPos pos, BlockState state) {
		super(PseudoscienceBlocks.ITEM_TUBE_BLOCK_ENTITY, pos, state);
	}
}
