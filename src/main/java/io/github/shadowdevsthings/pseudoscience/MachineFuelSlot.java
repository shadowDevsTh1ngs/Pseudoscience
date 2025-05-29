package io.github.shadowdevsthings.pseudoscience;

import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Holder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Util;

import java.util.Map;

public class MachineFuelSlot extends Slot {
	public MachineFuelSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public boolean canInsert(ItemStack stack) {
		return isFuel(stack);
	}

	public boolean isFuel(ItemStack stack) {
		return createFuelTimeMap().containsKey(stack.getItem());
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

	private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
		for(Holder<Item> holder : Registries.ITEM.getTagOrEmpty(tag)) {
			if (!holder.value().getBuiltInRegistryHolder().isIn(ItemTags.NON_FLAMMABLE_WOOD)) {
				fuelTimes.put(holder.value(), fuelTime);
			}
		}
	}
}
