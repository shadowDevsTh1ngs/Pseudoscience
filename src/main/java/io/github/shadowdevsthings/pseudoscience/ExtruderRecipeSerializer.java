package io.github.shadowdevsthings.pseudoscience;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CookingCategory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class ExtruderRecipeSerializer implements RecipeSerializer<ExtruderRecipe> {

	private ExtruderRecipeSerializer() {}

	public static final ExtruderRecipeSerializer INSTANCE = new ExtruderRecipeSerializer();

	public static final Identifier ID = new Identifier("pseudoscience:extruding");


	@Override
	public void write(PacketByteBuf packetData, ExtruderRecipe recipe) {
		recipe.getInput().write(packetData);
		packetData.writeItemStack(recipe.getResult());
		packetData.writeVarInt(recipe.getProcessTime());
		packetData.writeVarInt(recipe.getOutputAmount());
	}

	@Override
	public ExtruderRecipe read(PacketByteBuf packetData) {
		Ingredient input = Ingredient.fromPacket(packetData);
		ItemStack output = packetData.readItemStack();
		int processTime = packetData.readVarInt();
		int outputAmount = packetData.readVarInt();
		return new ExtruderRecipe(input, output, processTime, outputAmount);
	}


	@Override
	public Codec<ExtruderRecipe> getCodec() {
		return RecordCodecBuilder.create(instance -> instance.group(
			//Codecs.method_53049(Codec.STRING, "group", "").forGetter(ExtruderRecipe -> ExtruderRecipe.group),
			//CookingCategory.CODEC.fieldOf("category").orElse(CookingCategory.MISC).forGetter(abstractCookingRecipe -> abstractCookingRecipe.category),
			Ingredient.field_46096.fieldOf("input").forGetter(ExtruderRecipe::getInput),
			Registries.ITEM.getCodec().xmap(ItemStack::new, ItemStack::getItem).fieldOf("result").forGetter(ExtruderRecipe::getTrueResult),
			Codec.INT.fieldOf("processingTime").forGetter(ExtruderRecipe::getProcessTime),
			Codec.INT.fieldOf("outputAmount").forGetter(ExtruderRecipe::getOutputAmount)
		).apply(instance, ExtruderRecipe::new));
	}
}
