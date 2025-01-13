package io.github.shadowdevsthings.pseudoscience;


import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;

public class PseudoscienceBlocks {
	public static final MachineBlock EXTRUDER = new MachineBlock(AbstractBlock.Settings.create());


	public static void register(ModContainer mod) {
		Registry.register(Registries.BLOCK, new Identifier(mod.metadata().id(), "extruder"), EXTRUDER);
		Registry.register(Registries.ITEM, new Identifier(mod.metadata().id(), "extruder"), new BlockItem(EXTRUDER, new Item.Settings()));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.addItem(EXTRUDER.asItem());
		});
	}
}
