package net.zuiron.photosynthesis.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MixingBowlRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;

    private final int cookingTime;

    private final DefaultedList counts;


    public MixingBowlRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int cookingTime, DefaultedList counts) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.cookingTime = cookingTime;
        this.counts = counts;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        //is this not used???
        return recipeItems.get(0).test(inventory.getStack(0))
                && recipeItems.get(1).test(inventory.getStack(1))
                && recipeItems.get(2).test(inventory.getStack(2))
                && recipeItems.get(3).test(inventory.getStack(3))
                && recipeItems.get(4).test(inventory.getStack(4))
                && recipeItems.get(5).test(inventory.getStack(5))
                && recipeItems.get(6).test(inventory.getStack(6))
                && recipeItems.get(7).test(inventory.getStack(7))
                && recipeItems.get(8).test(inventory.getStack(8))
                ;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }

    public ItemStack getOutputStack() {
        return output.copy();
    }

    public int getCookTime() {
        return cookingTime;
    }

    public DefaultedList getCounts() { return counts; }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return recipeItems;
    } //IMPORTANT FOR REI

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MixingBowlRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "mixingbowl";
    }

    public static class Serializer implements RecipeSerializer<MixingBowlRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "mixingbowl";
        // this is the name given in the json file

        @Override
        public MixingBowlRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            int CookTime = JsonHelper.getInt(json, "cookingtime");
            //Photosynthesis.LOGGER.info("read cookingtime of: " + CookTime + ", for: " + output.getItem().getName().getString());

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(9, Ingredient.EMPTY); //size: max number of possible input ingredients.
            DefaultedList<Integer> counts = DefaultedList.ofSize(9, 0);

            for (int i = 0; i < ingredients.size(); i++) {
                if (i >= inputs.size()) {
                    inputs.add(Ingredient.EMPTY);
                    counts.add(0);
                }
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                counts.set(i, JsonHelper.getInt(ingredients.get(i).getAsJsonObject(),"count"));
            }

            return new MixingBowlRecipe(id, output, inputs, CookTime, counts);
        }

        @Override
        public MixingBowlRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new MixingBowlRecipe(id, output, inputs, 0, DefaultedList.ofSize(9, 0));
        }

        @Override
        public void write(PacketByteBuf buf, MixingBowlRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutputStack());
        }
    }
}
