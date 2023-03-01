package net.zuiron.photosynthesis.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.util.FluidStack;

public class KegRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;

    private final int cookingTime;

    private final DefaultedList counts;


    public KegRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int cookingTime, DefaultedList counts) {
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
        return recipeItems.get(1).test(inventory.getStack(1))
                && recipeItems.get(2).test(inventory.getStack(2))
                && recipeItems.get(3).test(inventory.getStack(3))
                && recipeItems.get(4).test(inventory.getStack(4));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
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

    public static class Type implements RecipeType<KegRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "keg";
    }

    public static class Serializer implements RecipeSerializer<KegRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "keg";
        // this is the name given in the json file

        @Override
        public KegRecipe read(Identifier id, JsonObject json) {


            JsonObject outputobj = JsonHelper.getObject(json, "output");
            int fluidamount = JsonHelper.getInt(outputobj, "amount", 81000);

            String fluidstring = JsonHelper.getString(outputobj, "fluid");
            Fluid fluid = (Fluid) Registries.FLUID.getOrEmpty(new Identifier(fluidstring)).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown fluid '" + fluidstring + "'");
            });
            FluidStack fluidoutput = new FluidStack(FluidVariant.of(fluid), fluidamount);
            Photosynthesis.LOGGER.info("output fluid: " + fluidoutput);






            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(4, Ingredient.EMPTY);
            DefaultedList<Integer> counts = DefaultedList.ofSize(4, 0);

            for (int i = 0; i < ingredients.size()-1; i++) {
                if (i >= inputs.size()) {
                    inputs.add(Ingredient.EMPTY);
                    counts.add(0);
                }
                inputs.set(i, Ingredient.fromJson(ingredients.get(i+1)));
                counts.set(i, JsonHelper.getInt(ingredients.get(i+1).getAsJsonObject(),"count"));
            }

            Photosynthesis.LOGGER.info("ingredients: "+inputs+", counts: "+counts);
            //TODO


            /*
            until its done.
             */
            DefaultedList<Ingredient> inputsS = DefaultedList.ofSize(5, Ingredient.EMPTY);
            ItemStack output = new ItemStack(Items.STICK);

            int CookTime = JsonHelper.getInt(json, "cookingtime");
            return new KegRecipe(id, output, inputsS, CookTime, counts);
        }

        @Override
        public KegRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new KegRecipe(id, output, inputs, 0, DefaultedList.ofSize(7, 0));
        }

        @Override
        public void write(PacketByteBuf buf, KegRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput());
        }
    }
}
