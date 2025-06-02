package io.github.shadowdevsthings.pseudoscience;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


public class ExtruderRecipe implements Recipe<Inventory> {

	private final Ingredient input;
	private final ItemStack result;
	private final Identifier id;

	public ExtruderRecipe(ItemStack result, Ingredient input) {
		this.id = new Identifier("pseudoscience:extruding");
		this.input = input;
		this.result = result;
	}

	public Ingredient getInput() {
		return this.input;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager manager) {
		return this.result;
	}

	public ItemStack getResult() {
		return this.result;
	}

	//Should override?
	public Identifier getId() {
		return this.id;
	}

	public ItemStack craft(Inventory inv, DynamicRegistryManager manager) {
		return this.result;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}


	@Override
	public boolean matches(Inventory inv, World world) {
		return input.test(inv.getStack(0));
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ExtruderRecipeSerializer.INSTANCE;
	}

	public static class Type implements RecipeType<ExtruderRecipe> {
		private Type() {}
		public static final Type INSTANCE = new Type();
		public static final String ID = "extruding";
	}

	@Override
	public RecipeType<?> getType() {
		return Type.INSTANCE;
	}

	class ExtruderRecipeJsonFormat {
		JsonObject input;
		String outputItem;
		int outputAmount;
		int processTime;
	}



}
