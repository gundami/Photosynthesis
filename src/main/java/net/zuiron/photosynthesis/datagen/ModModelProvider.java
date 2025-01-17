package net.zuiron.photosynthesis.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.zuiron.photosynthesis.fluid.ModFluids;
import net.zuiron.photosynthesis.item.ModItems;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        //Spawn Eggs
        itemModelGenerator.register(ModItems.BOAR_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.ALLIGATOR_SPAWN_EGG,
                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));

        //itemModelGenerator.register(ModItems.CACTUS_FRUIT, );
        Item[] simple_items = {
                ModItems.WRENCH,
                ModItems.SLOT_LOCK_LOCKED,
                ModItems.SLOT_LOCK_UNLOCKED,
                ModItems.FORESTY_BUNDLE,
                ModItems.PLASTIC_WRAP,
                ModItems.TMR,
                ModItems.BOAR_TUSK,
                ModItems.GRASS_BUNDLE,
                ModItems.HAY_BUNDLE,
                ModItems.STRAW_BUNDLE,
                ModItems.GRASS_FIBRE_PIECE,
                ModItems.STRAW_PIECE,
                ModItems.GRASS_TWINE,
                ModItems.GRASS_TWINE_DRY,
                ModItems.SHARP_FLINT_FRAGMENT,
                ModItems.SHEEP_BONES,
                ModItems.STONE_LARGE,
                ModItems.STONE_LONG,
                ModItems.STONE_SMALL,
                ModItems.STURDY_STICK,
                ModItems.TINY_CHARCOAL,
                ModItems.TREE_BRANCH,
                ModItems.TREE_CONE,
                ModItems.TREE_LEAF,
                /*ModItems.WOOL_RAW_BLACK,
                ModItems.WOOL_RAW_BLUE,
                ModItems.WOOL_RAW_BROWN,
                ModItems.WOOL_RAW_CYAN,
                ModItems.WOOL_RAW_DARK_GRAY,
                ModItems.WOOL_RAW_GRAY,
                ModItems.WOOL_RAW_GREEN,
                ModItems.WOOL_RAW_LIGHT_BLUE,
                ModItems.WOOL_RAW_LIME,
                ModItems.WOOL_RAW_MAGENTA,
                ModItems.WOOL_RAW_ORANGE,
                ModItems.WOOL_RAW_PINK,
                ModItems.WOOL_RAW_PURPLE,
                ModItems.WOOL_RAW_RED,
                ModItems.WOOL_RAW_WHITE,
                ModItems.WOOL_RAW_YELLOW,*/
                ModItems.SEASHELL,
                ModItems.GRILLED_SALMON,
                ModItems.GRILLED_COD,
                ModItems.MANURE,
                ModItems.RAW_SULFUR,
                ModItems.SULFUR_DUST,

                ModItems.GARDEN_GRUBBER_FLINT,
                ModItems.GARDEN_GRUBBER_IRON,
                ModItems.GARDEN_GRUBBER_DIAMOND,

                ModItems.CHEESE_SLICER,

                ModItems.CUTTING_KNIFE_FLINT,
                ModItems.CUTTING_KNIFE_IRON,
                ModItems.CUTTING_KNIFE_DIAMOND,

                ModItems.CUTTING_KNIFE_BLADE_FLINT,
                ModItems.CUTTING_KNIFE_BLADE_IRON,
                ModItems.CUTTING_KNIFE_BLADE_DIAMOND,

                ModItems.CUTTING_KNIFE_HANDLE,

                ModItems.CACTUS_FRUIT,
                ModItems.CACTUS_FRUIT_SEEDS,
                ModItems.CANTALOUPE,
                ModItems.CANTALOUPE_SEEDS,
                ModItems.FLAX,
                ModItems.FLAX_SEEDS,
                ModItems.JUTE,
                ModItems.JUTE_SEEDS,
                ModItems.KENAF,
                ModItems.KENAF_SEEDS,
                ModItems.SISAL,
                ModItems.SISAL_SEEDS,
                ModItems.AMARANTH,
                ModItems.AMARANTH_SEEDS,
                ModItems.BEAN,
                ModItems.BEAN_SEEDS,
                ModItems.CHICKPEA,
                ModItems.CHICKPEA_SEEDS,
                ModItems.LENTIL,
                ModItems.LENTIL_SEEDS,
                ModItems.QUINOA,
                ModItems.QUINOA_SEEDS,
                ModItems.PEANUT,
                ModItems.PEANUT_SEEDS,
                ModItems.TARO,
                ModItems.TARO_SEEDS,
                ModItems.TOMATILLO,
                ModItems.TOMATILLO_SEEDS,
                ModItems.AGAVE,
                ModItems.AGAVE_SEEDS,
                ModItems.GINGER,
                ModItems.GINGER_SEEDS,
                ModItems.SESAME,
                ModItems.SESAME_SEEDS,
                ModItems.MUSTARD,
                ModItems.MUSTARD_SEEDS,
                ModItems.GRASS_SEEDS,
                ModItems.WHITE_BUTTON_MUSHROOM_SPORES,
                ModItems.PORCINI_MUSHROOM_SPORES,
                ModItems.CHANTERELLE_MUSHROOM_SPORES,
                ModItems.MOREL_MUSHROOM_SPORES,

                ModItems.LINGONBERRIES,
                ModItems.CRANBERRIES,
                ModItems.HUCKLEBERRIES,
                ModItems.JUNIPERBERRIES,
                ModItems.MULBERRIES,

                ModItems.COW_BRISKET,
                ModItems.COOKED_COW_BRISKET,
                ModItems.COW_RIBS,
                ModItems.COOKED_COW_RIBS,
                ModItems.COOKED_COW_ROAST_BEEF,
                ModItems.COW_HEARTH,
                ModItems.COW_KIDNEY,
                ModItems.COW_LIVER,
                ModItems.COW_TONGUE,
                ModItems.COW_TRIMMINGS,
                ModItems.COW_STEAK,
                ModItems.COOKED_COW_STEAK,

                ModItems.SHEEP_TRIMMINGS,
                ModItems.SHEEP_RIBS,
                ModItems.SHEEP_LEGS,

                ModItems.CHICKEN_BONES,
                ModItems.CHICKEN_FEET,
                ModItems.CHICKEN_LIVER,
                ModItems.CHICKEN_WINGS,
                ModItems.CHICKEN_TRIMMINGS,

                ModItems.RAW_BACON,
                ModItems.PIG_BELLY,
                ModItems.RAW_HAM,
                ModItems.PIG_SKIN,
                ModItems.PIG_BONES,
                ModItems.PIG_TRIMMINGS,

                ModItems.HORSE_MEAT,
                ModItems.HORSE_BONES,
                ModItems.HORSE_HAIR,
                ModItems.HORSE_LEATHER,
                ModItems.WOLF_PELT,

                ModItems.RAW_LEATHER,
                ModItems.SALTED_RAW_LEATHER,
                ModItems.SALTED_PIG_SKIN,
                ModItems.SALTED_WOLF_PELT,
                ModItems.SALTED_HORSE_LEATHER,
                ModItems.SALTED_RABBIT_HIDE,

                //ModItems.FIRESTARTER,

                ModItems.BOILED_EGG,
                ModItems.WHEAT_DOUGH,
                ModItems.RICE_PANICLE,

                ModItems.BBQ_SKEWERS,
                ModItems.FRIED_RICE,
                ModItems.COOKED_RICE,
                ModItems.MINCED_BEEF,
                ModItems.MINCED_CHICKEN,
                ModItems.MINCED_SHEEP,

                ModItems.FRIED_MINCED_BEEF,
                ModItems.FRIED_MINCED_SHEEP,
                ModItems.FRIED_MINCED_CHICKEN,
                ModItems.FRIED_MINCED_PORK,

                //ModItems.YELLOW_CHEESE,
                ModItems.ENGLISH_BREAKFAST,
                ModItems.MEATBALLS,
                ModItems.MINCED_PORK,
                ModItems.PUMPKIN_SOUP,
                //ModItems.WAFFLE,

                ModItems.PEPPERONI_SAUSAGE,
                ModItems.PEPPERONI_SLICE,

                ModItems.PIZZA_CHEESE,
                ModItems.PIZZA_HAM,
                ModItems.PIZZA_PEPPERONI,

                ModItems.CUT_KIWI,
                ModItems.CUT_WHITE_BUTTON_MUSHROOM,
                ModItems.CUT_PORCINI_MUSHROOM,
                ModItems.CUT_CHANTERELLE_MUSHROOM,
                ModItems.CUT_MOREL_MUSHROOM,

                /*ModItems.MEASURING_CUP,
                ModItems.MEASURING_CUP_WATER,
                ModItems.MEASURING_CUP_MILK,
                ModItems.MEASURING_CUP_MILK_CREAM,
                ModItems.MEASURING_CUP_CANOLAOIL,
                ModItems.MEASURING_CUP_SUNFLOWEROIL,
                ModItems.MEASURING_CUP_MEAD,
                */
                ModItems.DEBUG_ITEM,

                ModItems.LEATHER_WATER_BLADDER,
                ModItems.LEATHER_WATER_BLADDER_DIRTY,
                ModItems.LEATHER_WATER_BLADDER_CLEAN,

                ModItems.MARSHMALLOW,
                ModItems.BUTTER,
                ModItems.WHIPPED_CREAM,
                ModItems.COOKIE_DOUGH,
                ModItems.CHOCOLATE_COOKIE_DOUGH,
                ModItems.WHEAT_FLOUR,
                ModItems.BREAD_SLICE,
                ModItems.POWDERED_SUGAR,
                ModItems.GELATIN_SHEET,
                ModItems.VANILLA_POWDER,
                ModItems.MESOPHILIC_CULTURE,
                ModItems.RENNET,

                ModItems.GOAT_CHEESE,
                ModItems.SOFT_GOAT_CHEESE,
                ModItems.GOAT_CHEESE_SLICE,

                ModItems.CHEDDAR_CHEESE,
                ModItems.SOFT_CHEDDAR_CHEESE,
                ModItems.CHEDDAR_CHEESE_SLICE,

                ModItems.MOZZARELLA_CHEESE,
                ModItems.SOFT_MOZZARELLA_CHEESE,
                ModItems.MOZZARELLA_CHEESE_DICES,

                //ModItems.PIZZA,
                //ModItems.PANCAKES,
                ModItems.PIE_RASPBERRY,
                ModItems.CHICKEN_THIGH,
                ModItems.BAKED_ZUCCHINI,
                ModItems.BAGUETTE,
                ModItems.OMELETTE,
                ModItems.GARLIC_BREAD_SLICE,
                ModItems.DUMPLING,
                ModItems.PANCAKE,
                ModItems.BREAD_FORM,
                ModItems.BAGUETTE_FORM,
                ModItems.OVEN_TRAY,
                ModItems.BACON,
                ModItems.CHOCOLATE_CUPCAKE,
                ModItems.CHICKEN_NUGGETS,
                ModItems.CAULIFLOWER_SOUP,
                ModItems.HAM,
                ModItems.HAM_STRIPS,
                ModItems.HAM_SLICE,
                ModItems.EGG_SANDWICH,
                ModItems.TACO,
                //ModItems.SUSHI_ROLL2,
                //ModItems.SUSHI_ROLL,
                ModItems.SALMON_SUSHI,
                //ModItems.PIZZA_SAUCE,
                ModItems.OAT_BREAD,
                ModItems.RYE_BREAD,
                ModItems.TACO_SHELL,
                ModItems.RAW_CHICKEN_NUGGETS,
                ModItems.CORN_FLOUR,
                ModItems.FISH_SOUP,
                ModItems.GARLIC_BREAD,
                ModItems.CHEESE_BREAD_SLICE,
                ModItems.HAM_BAGUETTE,
                ModItems.SEMOLINA_FLOUR,
                ModItems.OAT_FLOUR,
                ModItems.RYE_FLOUR,
                ModItems.BARLEY_FLOUR,

                ModItems.CHICKPEA_SOUP,
                ModItems.LENTIL_SOUP,
                ModItems.FRUIT_SALAD,
                ModItems.BERRY_SALAD,
                ModItems.NUT_MIXTURE,
                ModItems.QUINOA_PORRIDGE,
                ModItems.RUTABAGA_KOHLRABI_MASH,
                ModItems.SWEET_POTATO_AND_CHICKEN_POT,
                ModItems.BURGER_BUN,
                ModItems.CHEESE_BURGER,

                ModItems.OAT_DOUGH,
                ModItems.RYE_DOUGH,
                ModItems.BARLEY_DOUGH,
                ModItems.CORN_DOUGH,
                ModItems.SEMOLINA_DOUGH,
                ModItems.DANDELION_SEEDS,
                ModItems.POPPY_SEEDS,
                ModItems.BLUE_ORCHID_SEEDS,
                ModItems.ALLIUM_SEEDS,
                ModItems.AZURE_BLUET_SEEDS,
                ModItems.RED_TULIP_SEEDS,
                ModItems.ORANGE_TULIP_SEEDS,
                ModItems.WHITE_TULIP_SEEDS,
                ModItems.PINK_TULIP_SEEDS,
                ModItems.OXEYE_DAISY_SEEDS,
                ModItems.CORNFLOWER_SEEDS,
                ModItems.LILY_OF_THE_VALLEY_SEEDS,
                ModItems.FLORAMELISSIA_SEEDS,
                ModItems.WITHER_ROSE_SEEDS,
                ModItems.FISH_ROE,
                ModItems.SMOKED_FISH_ROE,
                ModItems.KAVIAR,
                ModItems.WHEAT_BREAD_SLICE_WITH_SCRAMBLED_EGG_AND_KAVIAR,
                ModFluids.PIZZASAUCE_BUCKET,
                ModFluids.TOMATOSAUCE_BUCKET,

                //alligator

                ModItems.ALLIGATOR_GUMBO,
                ModItems.ALLIGATOR_LEATHER,
                ModItems.SALTED_ALLIGATOR_LEATHER,
                ModItems.ALLIGATOR_MEAT,
                ModItems.COOKED_ALLIGATOR_MEAT,
                ModItems.FRIED_ALLIGATOR,
                ModItems.SMOKED_ALLIGATOR_JERKY,
                ModItems.ALLIGATOR_TOOTH,



                ModFluids.DRINKINGWATER_BUCKET








        };
        for (Item item : simple_items) {
            itemModelGenerator.register(item, Models.GENERATED);
        }
    }
}
