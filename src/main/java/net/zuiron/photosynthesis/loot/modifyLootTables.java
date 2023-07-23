package net.zuiron.photosynthesis.loot;

import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.item.ModItems;

public class modifyLootTables {
    private static final Identifier GRASS_ID1 = new Identifier("minecraft", "blocks/grass");
    private static final Identifier GRASS_ID = Blocks.GRASS.getLootTableId();
    private static final Identifier TALL_GRASS_ID = Blocks.TALL_GRASS.getLootTableId();

    public static void registerModifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Let's only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
            // We also check that the loot table ID is equal to the ID we want.

            if (source.isBuiltin() && GRASS_ID.equals(id) || TALL_GRASS_ID.equals(id)) {
                //grass fibre piece drops if grass is hit with cutting knifes.
                final TagKey<Item> CUTTING_KNIFES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "cutting_board_cutting_knifes"));
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.GRASS_FIBRE_PIECE))
                        .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(CUTTING_KNIFES)).build())
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)).build());
                tableBuilder.pool(poolBuilder);
            }
            if (source.isBuiltin() && GRASS_ID.equals(id) || TALL_GRASS_ID.equals(id)) {
                //seeds drop from grass if hit with garden grubbers
                final TagKey<Item> GARDEN_GRUBBERS = TagKey.of(RegistryKeys.ITEM, new Identifier("photosynthesis", "garden_grubbers"));
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.TOMATO_SEEDS))
                        .with(ItemEntry.builder(ModItems.BASIL_SEEDS))
                        .with(ItemEntry.builder(ModItems.OREGANO_SEEDS))
                        .with(ItemEntry.builder(ModItems.STRAWBERRY_SEEDS))
                        .with(ItemEntry.builder(ModItems.OAT_SEEDS))
                        .with(ItemEntry.builder(ModItems.JALAPENO_SEEDS))
                        .with(ItemEntry.builder(ModItems.CHILI_SEEDS))
                        .with(ItemEntry.builder(ModItems.CUCUMBER_SEEDS))
                        .with(ItemEntry.builder(ModItems.ONION_SEEDS))
                        .with(ItemEntry.builder(ModItems.LEEK_SEEDS))
                        .with(ItemEntry.builder(ModItems.CELERY_SEEDS))
                        .with(ItemEntry.builder(ModItems.PEAS_SEEDS))
                        .with(ItemEntry.builder(ModItems.SWEET_POTATO_SEEDS))
                        .with(ItemEntry.builder(ModItems.ASPARAGUS_SEEDS))
                        .with(ItemEntry.builder(ModItems.SCALLION_SEEDS))

                        .with(ItemEntry.builder(ModItems.GARLIC_SEEDS))
                        .with(ItemEntry.builder(ModItems.CHIVE_SEEDS))
                        .with(ItemEntry.builder(ModItems.BROCCOLI_SEEDS))
                        .with(ItemEntry.builder(ModItems.CAULIFLOWER_SEEDS))
                        .with(ItemEntry.builder(ModItems.CORN_SEEDS))
                        .with(ItemEntry.builder(ModItems.CABBAGE_SEEDS))
                        .with(ItemEntry.builder(ModItems.BELLPEPPER_SEEDS))
                        .with(ItemEntry.builder(ModItems.TURNIP_SEEDS))
                        .with(ItemEntry.builder(ModItems.RUTABAGA_SEEDS))
                        .with(ItemEntry.builder(ModItems.CANOLA_SEEDS))
                        .with(ItemEntry.builder(ModItems.BARLEY_SEEDS))
                        .with(ItemEntry.builder(ModItems.COTTON_SEEDS))
                        .with(ItemEntry.builder(ModItems.SUGARBEET_SEEDS))
                        .with(ItemEntry.builder(ModItems.RICE_SEEDS))
                        .with(ItemEntry.builder(ModItems.SOYBEAN_SEEDS))
                        .with(ItemEntry.builder(ModItems.SPINACH_SEEDS))
                        .with(ItemEntry.builder(ModItems.ARROWROOT_SEEDS))
                        .with(ItemEntry.builder(ModItems.ARTICHOKE_SEEDS))
                        .with(ItemEntry.builder(ModItems.BRUSSELS_SPROUTS_SEEDS))
                        .with(ItemEntry.builder(ModItems.CASSAVA_SEEDS))

                        .with(ItemEntry.builder(ModItems.EGGPLANT_SEEDS))
                        .with(ItemEntry.builder(ModItems.SUNFLOWER_SEEDS))
                        .with(ItemEntry.builder(ModItems.JICAMA_SEEDS))
                        .with(ItemEntry.builder(ModItems.KALE_SEEDS))
                        .with(ItemEntry.builder(ModItems.KOHLRABI_SEEDS))
                        .with(ItemEntry.builder(ModItems.LETTUCE_SEEDS))
                        .with(ItemEntry.builder(ModItems.OKRA_SEEDS))
                        .with(ItemEntry.builder(ModItems.PARSNIP_SEEDS))
                        .with(ItemEntry.builder(ModItems.RADISH_SEEDS))
                        .with(ItemEntry.builder(ModItems.RHUBARB_SEEDS))
                        .with(ItemEntry.builder(ModItems.MILLET_SEEDS))
                        .with(ItemEntry.builder(ModItems.RYE_SEEDS))
                        .with(ItemEntry.builder(ModItems.SQUASH_SEEDS))
                        .with(ItemEntry.builder(ModItems.ZUCCHINI_SEEDS))
                        .with(ItemEntry.builder(ModItems.COFFEA_SEEDS))
                        .with(ItemEntry.builder(ModItems.PARSLEY_SEEDS))
                        .with(ItemEntry.builder(ModItems.MINT_SEEDS))
                        .with(ItemEntry.builder(ModItems.PINEAPPLE_SEEDS))
                        .with(ItemEntry.builder(ModItems.HOP_SEEDS))
                        .with(ItemEntry.builder(ModItems.FILIPENDULA_SEEDS))

                        .with(ItemEntry.builder(ModItems.CAMELLIA_SINENSIS_SEEDS))
                        .with(ItemEntry.builder(ModItems.NICOTIANA_RUSTICA_SEEDS))
                        .with(ItemEntry.builder(ModItems.PAPAVER_SOMNIFERUM_SEEDS))
                        .with(ItemEntry.builder(ModItems.ERYTHROXYLUM_COCA_SEEDS))
                        .with(ItemEntry.builder(ModItems.CACTUS_FRUIT_SEEDS))
                        .with(ItemEntry.builder(ModItems.CANTALOUPE_SEEDS))
                        .with(ItemEntry.builder(ModItems.FLAX_SEEDS))
                        .with(ItemEntry.builder(ModItems.JUTE_SEEDS))
                        .with(ItemEntry.builder(ModItems.KENAF_SEEDS))
                        .with(ItemEntry.builder(ModItems.SISAL_SEEDS))
                        .with(ItemEntry.builder(ModItems.AMARANTH_SEEDS))
                        .with(ItemEntry.builder(ModItems.BEAN_SEEDS))
                        .with(ItemEntry.builder(ModItems.CHICKPEA_SEEDS))
                        .with(ItemEntry.builder(ModItems.LENTIL_SEEDS))
                        .with(ItemEntry.builder(ModItems.QUINOA_SEEDS))
                        .with(ItemEntry.builder(ModItems.PEANUT_SEEDS))
                        .with(ItemEntry.builder(ModItems.TARO_SEEDS))
                        .with(ItemEntry.builder(ModItems.TOMATILLO_SEEDS))
                        .with(ItemEntry.builder(ModItems.AGAVE_SEEDS))
                        .with(ItemEntry.builder(ModItems.GINGER_SEEDS))

                        .with(ItemEntry.builder(ModItems.SESAME_SEEDS))
                        .with(ItemEntry.builder(ModItems.MUSTARD_SEEDS))
                        .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(GARDEN_GRUBBERS)).build())
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                tableBuilder.pool(poolBuilder);
            }
        });
    }
}