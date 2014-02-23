package net.clareburt;

import com.google.gson.JsonSyntaxException;
import net.clareburt.model.Ingredient;
import net.clareburt.model.Recipe;
import net.clareburt.model.Unit;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class RecipeParserTest {

	private RecipeParser recipeParser;

	@Before
	public void setup() {
		recipeParser = new RecipeParser();
	}

	@Test
	public void nullInputShouldReturnEmptyCollection() {
		final Collection<Recipe> recipesFromJson = recipeParser.getRecipesFromJson(null);
		assertNotNull(recipesFromJson);
	}

	@Test(expected=JsonSyntaxException.class)
	public void invalidInputShouldThrowJsonSyntaxException() {
		recipeParser.getRecipesFromJson("invalid data");
		fail("JsonSyntaxException expected");
	}

	@Test
	public void testGetRecipesFromJson_singleItem() {
		final String testItems = "[" +
				"    {" +
				"        \"name\": \"toast\"," +
				"        \"ingredients\": [" +
				"            { \"item\":\"bread\", \"amount\":\"1\", \"unit\":\"slices\"}" +
				"        ]" +
				"    }" +
				"]";
		final Collection<Recipe> recipes = recipeParser.getRecipesFromJson(testItems);
		assertNotNull(recipes);
		assertEquals(1, recipes.size());
		for (Recipe recipe : recipes) {
			assertEquals("toast", recipe.getName());
			final Collection<Ingredient> ingredients = recipe.getIngredients();
			for (Ingredient ingredient : ingredients) {
				assertNotNull(ingredients);
				assertEquals("bread", ingredient.getItem());
				assertEquals(1, ingredient.getAmount());
				assertEquals(Unit.slices, ingredient.getUnit());
			}
		}
	}

	@Test
	public void testGetRecipesFromJson_multipleItems() {
		final String testItems = "[" +
				"    {" +
				"        \"name\": \"grilled cheese on toast\"," +
				"        \"ingredients\": [" +
				"            { \"item\":\"bread\", \"amount\":\"2\", \"unit\":\"slices\"}," +
				"            { \"item\":\"cheese\", \"amount\":\"2\", \"unit\":\"slices\"}" +
				"        ]" +
				"    }" +
				"    ," +
				"    {" +
				"        \"name\": \"salad sandwich\"," +
				"        \"ingredients\": [" +
				"            { \"item\":\"bread\", \"amount\":\"2\", \"unit\":\"slices\"}," +
				"            { \"item\":\"mixed salad\", \"amount\":\"100\", \"unit\":\"grams\"}" +
				"        ]" +
				"    }" +
				"]";
		final Collection<Recipe> recipes = recipeParser.getRecipesFromJson(testItems);
		assertNotNull(recipes);
		assertEquals(2, recipes.size());
		assertTrue(recipes.contains(generateRecipe("grilled cheese on toast", Arrays.asList(generateIngredient("bread", 2, Unit.slices), generateIngredient("cheese", 2, Unit.slices)))));
		assertTrue(recipes.contains(generateRecipe("salad sandwich", Arrays.asList(generateIngredient("bread", 2, Unit.slices), generateIngredient("mixed salad", 100, Unit.grams)))));
	}

	//------- Test helper methods --------

	private Recipe generateRecipe(String recipeName, List<Ingredient> ingredients) {
		final Recipe recipe2 = new Recipe();
		recipe2.setName(recipeName);
		recipe2.setIngredients(ingredients);
		return recipe2;
	}

	private Ingredient generateIngredient(String item, int amount, Unit units) {
		final Ingredient ingredient = new Ingredient();
		ingredient.setItem(item);
		ingredient.setAmount(amount);
		ingredient.setUnit(units);
		return ingredient;
	}

}
