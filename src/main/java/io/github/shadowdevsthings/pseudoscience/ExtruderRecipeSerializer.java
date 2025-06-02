package io.github.shadowdevsthings.pseudoscience;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class ExtruderRecipeSerializer implements RecipeSerializer<ExtruderRecipe> {

	private ExtruderRecipeSerializer() {}

	public static final ExtruderRecipeSerializer INSTANCE = new ExtruderRecipeSerializer();

	public static final Identifier ID = new Identifier("pseudoscience:extruding");


	@Override
	public void write(PacketByteBuf packetData, ExtruderRecipe recipe) {
		recipe.getInput().write(packetData);
		packetData.writeItemStack(recipe.getResult());
	}

	@Override
	public ExtruderRecipe read(PacketByteBuf packetData) {
		Ingredient input = Ingredient.fromPacket(packetData);
		ItemStack output = packetData.readItemStack();
		return new ExtruderRecipe(output, input);
	}


	@Override
	public Codec<ExtruderRecipe> getCodec() {
		return new Codec<ExtruderRecipe>() {
			@Override
			public <T> DataResult<Pair<ExtruderRecipe, T>> decode(DynamicOps<T> ops, T input) {
				return null;
			}

			@Override
			public <T> DataResult<T> encode(ExtruderRecipe input, DynamicOps<T> ops, T prefix) {
				return null;
			}
		};
	}
}
