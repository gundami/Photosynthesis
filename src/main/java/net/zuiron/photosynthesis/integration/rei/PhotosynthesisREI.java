package net.zuiron.photosynthesis.integration.rei;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.util.math.MatrixStack;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.block.ModBlocks;
import net.zuiron.photosynthesis.integration.rei.cookingpot.CookingPotRecipeCategory;
import net.zuiron.photosynthesis.integration.rei.cookingpot.CookingPotRecipeDisplay;
import net.zuiron.photosynthesis.integration.rei.cutting_board.CuttingBoardRecipeCategory;
import net.zuiron.photosynthesis.integration.rei.cutting_board.CuttingBoardRecipeDisplay;
import net.zuiron.photosynthesis.integration.rei.keg.KegRecipeCategory;
import net.zuiron.photosynthesis.integration.rei.keg.KegRecipeDisplay;
import net.zuiron.photosynthesis.integration.rei.skillet.SkilletRecipeCategory;
import net.zuiron.photosynthesis.integration.rei.skillet.SkilletRecipeDisplay;
import net.zuiron.photosynthesis.recipe.CookingPotRecipe;
import net.zuiron.photosynthesis.recipe.CuttingBoardRecipe;
import net.zuiron.photosynthesis.recipe.KegRecipe;
import net.zuiron.photosynthesis.recipe.SkilletRecipe;
import net.zuiron.photosynthesis.screen.CookingPotScreen;
import net.zuiron.photosynthesis.screen.CuttingBoardScreen;
import net.zuiron.photosynthesis.screen.KegScreen;
import net.zuiron.photosynthesis.screen.SkilletScreen;
import net.zuiron.photosynthesis.screen.renderer.FluidStackRenderer;
import net.zuiron.photosynthesis.util.FluidStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PhotosynthesisREI implements REIClientPlugin {

    public static final CategoryIdentifier<SkilletRecipeDisplay> SKILLET = CategoryIdentifier.of(Photosynthesis.MOD_ID, "skillet");
    public static final CategoryIdentifier<CuttingBoardRecipeDisplay> CUTTINGBOARD = CategoryIdentifier.of(Photosynthesis.MOD_ID, "cuttingboard");
    public static final CategoryIdentifier<CookingPotRecipeDisplay> COOKINGPOT = CategoryIdentifier.of(Photosynthesis.MOD_ID, "cookingpot");
    public static final CategoryIdentifier<KegRecipeDisplay> KEG = CategoryIdentifier.of(Photosynthesis.MOD_ID, "keg");

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(
                new SkilletRecipeCategory());
        registry.addWorkstations(SKILLET, EntryStacks.of(ModBlocks.SKILLET));

        registry.add(
                new CuttingBoardRecipeCategory());
        registry.addWorkstations(CUTTINGBOARD, EntryStacks.of(ModBlocks.CUTTINGBOARD));

        registry.add(
                new CookingPotRecipeCategory());
        registry.addWorkstations(COOKINGPOT, EntryStacks.of(ModBlocks.COOKINGPOT));

        registry.add(
                new KegRecipeCategory());
        registry.addWorkstations(KEG, EntryStacks.of(ModBlocks.KEG));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(SkilletRecipe.class, SkilletRecipe.Type.INSTANCE, SkilletRecipeDisplay::new);

        registry.registerRecipeFiller(CuttingBoardRecipe.class, CuttingBoardRecipe.Type.INSTANCE, CuttingBoardRecipeDisplay::new);

        registry.registerRecipeFiller(CookingPotRecipe.class, CookingPotRecipe.Type.INSTANCE, CookingPotRecipeDisplay::new);

        registry.registerRecipeFiller(KegRecipe.class, KegRecipe.Type.INSTANCE, KegRecipeDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(89, 25, 24, 17), SkilletScreen.class, SKILLET);

        registry.registerContainerClickArea(new Rectangle(99, 25, 24, 17), CuttingBoardScreen.class, CUTTINGBOARD);

        registry.registerContainerClickArea(new Rectangle(88, 44, 26, 18), CookingPotScreen.class, COOKINGPOT);

        registry.registerContainerClickArea(new Rectangle(88, 44, 26, 18), KegScreen.class, KEG);
    }
}
