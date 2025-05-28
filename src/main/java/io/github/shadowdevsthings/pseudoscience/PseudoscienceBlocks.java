package io.github.shadowdevsthings.pseudoscience;


import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.feature_flags.FeatureFlagBitSet;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

public class PseudoscienceBlocks {
	//Blocks
	public static final MachineBlock EXTRUDER = new MachineBlock(AbstractBlock.Settings.create().strength(4.0f).requiresTool());

	//Supporting stuff
	public static final BlockEntityType<MachineBlockEntity> MACHINE_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder
		.<MachineBlockEntity>create(MachineBlockEntity::new, EXTRUDER).build();
	public static final ScreenHandlerType<MachineScreenHandler> MACHINE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER_TYPE, new Identifier("pseudoscience:extruder"), new ScreenHandlerType<>(MachineScreenHandler::new, FeatureFlagBitSet.empty()));

	public static void register(ModContainer mod) {
		//Registering blocks and corresponding items
		Registry.register(Registries.BLOCK, new Identifier(mod.metadata().id(), "extruder"), EXTRUDER);
		Registry.register(Registries.ITEM, new Identifier(mod.metadata().id(), "extruder"), new BlockItem(EXTRUDER, new Item.Settings()));

		//Creative tab stuff
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.addItem(EXTRUDER.asItem());
		});

		//Registering supporting stuff
		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(mod.metadata().id(), "extruder"), MACHINE_BLOCK_ENTITY);
		HandledScreens.register(MACHINE_SCREEN_HANDLER, MachineScreen::new);
	}
}
