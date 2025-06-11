package io.github.shadowdevsthings.pseudoscience;


import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
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
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

public class PseudoscienceBlocks {

	//Basically configs
	public static final int MachineInventorySize = 3;




	//Blocks
	public static final MachineBlock EXTRUDER = new MachineBlock(AbstractBlock.Settings.create().strength(4.0F).requiresTool(), MachineInventorySize);
	public static final ItemTubeBlock ITEM_TUBE = new ItemTubeBlock(QuiltBlockSettings.create().strength(2.0F).solid(false).nonOpaque());



	//Supporting stuff
	public static final BlockEntityType<MachineBlockEntity> MACHINE_BLOCK_ENTITY = QuiltBlockEntityTypeBuilder
		.<MachineBlockEntity>create(MachineBlockEntity::new, EXTRUDER).build();

	public static final ScreenHandlerType<MachineScreenHandler> MACHINE_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER_TYPE, new Identifier("pseudoscience:extruder"), new ScreenHandlerType<>(MachineScreenHandler::new, FeatureFlagBitSet.empty()));




	public static void register(ModContainer mod) {
		//Registering blocks and corresponding items
		Registry.register(Registries.BLOCK, new Identifier(mod.metadata().id(), "extruder"), EXTRUDER);
		Registry.register(Registries.ITEM, new Identifier(mod.metadata().id(), "extruder"), new BlockItem(EXTRUDER, new Item.Settings()));
		Registry.register(Registries.BLOCK, new Identifier(mod.metadata().id(), "item_tube"), ITEM_TUBE);
		Registry.register(Registries.ITEM, new Identifier(mod.metadata().id(), "item_tube"), new BlockItem(ITEM_TUBE, new Item.Settings()));


		//Creative tab stuff
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.addItem(EXTRUDER.asItem());
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.addItem(ITEM_TUBE.asItem());
		});

		//Registering supporting stuff
		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(mod.metadata().id(), "extruder"), MACHINE_BLOCK_ENTITY);
		HandledScreens.register(MACHINE_SCREEN_HANDLER, MachineScreen::new);
		BlockRenderLayerMap.put(RenderLayer.getCutout(), ITEM_TUBE);


		//Recipes
		Registry.register(Registries.RECIPE_SERIALIZER, ExtruderRecipeSerializer.ID, ExtruderRecipeSerializer.INSTANCE);
		Registry.register(Registries.RECIPE_TYPE, new Identifier("pseudoscience", ExtruderRecipe.Type.ID), ExtruderRecipe.Type.INSTANCE);
		Pseudoscience.LOGGER.info(Registries.RECIPE_TYPE.getIds().toString());

	}
}
