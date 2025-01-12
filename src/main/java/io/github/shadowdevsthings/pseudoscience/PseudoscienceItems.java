package io.github.shadowdevsthings.pseudoscience;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;


public class PseudoscienceItems {

	//Item Declarations
	//public static final Block EXTRUDER = new Item(new Item.Settings().maxCount(1));

	public static void register(ModContainer mod) {
//		Registry.register(Registries.ITEM, new Identifier(mod.metadata().id(), "extruder"), EXTRUDER);
//		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> {
//			entries.addItem(EXTRUDER);
//		});
	}
}
