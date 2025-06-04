package io.github.shadowdevsthings.pseudoscience;

import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeHolder;
import net.minecraft.registry.Holder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;

public class MachineBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory {
	private final DefaultedList<ItemStack> items;
	private final Map<Item, Integer> fuelTimeMap;
	private final PropertyDelegate machineData; //index 0 is total burn time of fuel, index 1 is current burn time, index 2 is total process time, index 3 is current process time


	public MachineBlockEntity(BlockPos pos, BlockState state) {
		super(PseudoscienceBlocks.MACHINE_BLOCK_ENTITY, pos, state);
		items = DefaultedList.ofSize(9, ItemStack.EMPTY);
		fuelTimeMap = createFuelTimeMap();
		machineData = new ArrayPropertyDelegate(4);
	}

	public MachineBlockEntity(BlockPos pos, BlockState state, int inventorySize) {
		super(PseudoscienceBlocks.MACHINE_BLOCK_ENTITY, pos, state);
		items = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
		fuelTimeMap = createFuelTimeMap();
		machineData = new ArrayPropertyDelegate(4);
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
		return new MachineScreenHandler(syncId, playerInventory, this, machineData);
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


	public static void tick(World world, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
		if(!world.isClient()) {
			Optional<RecipeHolder<ExtruderRecipe>> match = world.getRecipeManager().getFirstMatch(ExtruderRecipe.Type.INSTANCE, blockEntity, world);
			if(match.isPresent()) {
				//Valid recipe

				ItemStack output = match.get().value().getResult().copy();
				output.increment(match.get().value().getOutputAmount()-1);
				if(blockEntity.getStack(2).isEmpty() || ItemStack.itemsMatch(blockEntity.getStack(2), output) && blockEntity.getStack(2).getCount() + output.getCount() <= 64) {
					//Conditions met to begin processing

					ItemStack fuelItem = blockEntity.getStack(1);
					if(blockEntity.machineData.get(1) == 0 && blockEntity.fuelTimeMap.containsKey(fuelItem.getItem())) {
						//Has fuel to consume and needs to

						blockEntity.machineData.set(0, blockEntity.fuelTimeMap.get(fuelItem.getItem()).intValue());
						blockEntity.machineData.set(1, blockEntity.fuelTimeMap.get(fuelItem.getItem()).intValue());
						fuelItem.decrement(1);
					}

					if(blockEntity.machineData.get(3) == 0 && blockEntity.machineData.get(1) > 0) {
						//Starting new recipe

						blockEntity.machineData.set(2, match.get().value().getProcessTime());
					}

					if(blockEntity.machineData.get(3) == blockEntity.machineData.get(2) && blockEntity.machineData.get(2) != 0) {
						//Finished processing

						//Reset progress
						blockEntity.machineData.set(2, 0);
						blockEntity.machineData.set(3, 0);

						if(blockEntity.getStack(2).isEmpty()) {
							blockEntity.setStack(2, output.copy());
							blockEntity.getStack(0).decrement(1);
						} else if (ItemStack.itemsMatch(blockEntity.getStack(2), output) && blockEntity.getStack(2).getCount() + output.getCount() <= 64) {
							blockEntity.getStack(2).increment(output.getCount());
							blockEntity.getStack(0).decrement(1);
						}
					} else if (blockEntity.machineData.get(1) > 0){
						//Continue processing current recipe if burning
						blockEntity.machineData.set(3, blockEntity.machineData.get(3)+1);
						blockEntity.machineData.set(1, blockEntity.machineData.get(1)-1);
					}






					//Pseudoscience.LOGGER.info("FuelTime: " + blockEntity.machineData.get(0) +" BurnTime: " + blockEntity.machineData.get(1) +" CraftTime: " + blockEntity.machineData.get(2) +" ProcessTime: " + blockEntity.machineData.get(3));
				}


			} else {
				//Deletes current progress because no recipe is found
				blockEntity.machineData.set(2, 0);
				blockEntity.machineData.set(3, 0);

			}
		}
	}

	private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
		for(Holder<Item> holder : Registries.ITEM.getTagOrEmpty(tag)) {
			if (!holder.value().getBuiltInRegistryHolder().isIn(ItemTags.NON_FLAMMABLE_WOOD)) {
				fuelTimes.put(holder.value(), fuelTime);
			}
		}
	}

	private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
		Item item2 = item.asItem();
		if (item2.getBuiltInRegistryHolder().isIn(ItemTags.NON_FLAMMABLE_WOOD)) {
			if (SharedConstants.isDevelopment) {
				throw (IllegalStateException) Util.throwOrPause(
					new IllegalStateException(
						"A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"
					)
				);
			}
		} else {
			fuelTimes.put(item2, fuelTime);
		}
	}

	public static Map<Item, Integer> createFuelTimeMap() {
		Map<Item, Integer> map = Maps.newLinkedHashMap();
		addFuel(map, Items.LAVA_BUCKET, 20000);
		addFuel(map, Blocks.COAL_BLOCK, 16000);
		addFuel(map, Items.BLAZE_ROD, 2400);
		addFuel(map, Items.COAL, 1600);
		addFuel(map, Items.CHARCOAL, 1600);
		addFuel(map, ItemTags.LOGS, 300);
		addFuel(map, ItemTags.BAMBOO_BLOCKS, 300);
		addFuel(map, ItemTags.PLANKS, 300);
		addFuel(map, Blocks.BAMBOO_MOSAIC, 300);
		addFuel(map, ItemTags.WOODEN_STAIRS, 300);
		addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
		addFuel(map, ItemTags.WOODEN_SLABS, 150);
		addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 150);
		addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
		addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
		addFuel(map, ItemTags.WOODEN_FENCES, 300);
		addFuel(map, ItemTags.FENCE_GATES, 300);
		addFuel(map, Blocks.NOTE_BLOCK, 300);
		addFuel(map, Blocks.BOOKSHELF, 300);
		addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
		addFuel(map, Blocks.LECTERN, 300);
		addFuel(map, Blocks.JUKEBOX, 300);
		addFuel(map, Blocks.CHEST, 300);
		addFuel(map, Blocks.TRAPPED_CHEST, 300);
		addFuel(map, Blocks.CRAFTING_TABLE, 300);
		addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
		addFuel(map, ItemTags.BANNERS, 300);
		addFuel(map, Items.BOW, 300);
		addFuel(map, Items.FISHING_ROD, 300);
		addFuel(map, Blocks.LADDER, 300);
		addFuel(map, ItemTags.SIGNS, 200);
		addFuel(map, ItemTags.HANGING_SIGNS, 800);
		addFuel(map, Items.WOODEN_SHOVEL, 200);
		addFuel(map, Items.WOODEN_SWORD, 200);
		addFuel(map, Items.WOODEN_HOE, 200);
		addFuel(map, Items.WOODEN_AXE, 200);
		addFuel(map, Items.WOODEN_PICKAXE, 200);
		addFuel(map, ItemTags.WOODEN_DOORS, 200);
		addFuel(map, ItemTags.BOATS, 1200);
		addFuel(map, ItemTags.WOOL, 100);
		addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
		addFuel(map, Items.STICK, 100);
		addFuel(map, ItemTags.SAPLINGS, 100);
		addFuel(map, Items.BOWL, 100);
		addFuel(map, ItemTags.WOOL_CARPETS, 67);
		addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
		addFuel(map, Items.CROSSBOW, 300);
		addFuel(map, Blocks.BAMBOO, 50);
		addFuel(map, Blocks.DEAD_BUSH, 100);
		addFuel(map, Blocks.SCAFFOLDING, 50);
		addFuel(map, Blocks.LOOM, 300);
		addFuel(map, Blocks.BARREL, 300);
		addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
		addFuel(map, Blocks.FLETCHING_TABLE, 300);
		addFuel(map, Blocks.SMITHING_TABLE, 300);
		addFuel(map, Blocks.COMPOSTER, 300);
		addFuel(map, Blocks.AZALEA, 100);
		addFuel(map, Blocks.FLOWERING_AZALEA, 100);
		addFuel(map, Blocks.MANGROVE_ROOTS, 300);
		return map;
	}

}
