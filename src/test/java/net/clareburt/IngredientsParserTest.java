package net.clareburt;

import net.clareburt.model.Ingredient;
import net.clareburt.model.Unit;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class IngredientsParserTest {

	private IngredientsParser ingredientsParser;

	@Before
	public void setup() {
		ingredientsParser = new IngredientsParser();
	}

	@Test
	public void nullInputShouldReturnEmptyCollection() throws ParseException {
		final Collection<Ingredient> ingredients = ingredientsParser.getIngredients(null);
		assertNotNull(ingredients);
	}

	@Test(expected = ParseException.class)
	public void shouldThrowParseExceptionForInvalidDate() throws ParseException {
		final Collection<String[]> csvData = new ArrayList<String[]>();
		csvData.add(new String[]{"cheese", "2", "slices", "invalidDate"});
		ingredientsParser.getIngredients(csvData);
		fail("Expected ParseException due to invalid date");
	}

	@Test
	public void testGetIngredients_singleItem() throws ParseException {
		final Collection<String[]> csvData = new ArrayList<String[]>();
		csvData.add(new String[]{"cheese", "2", "slices", "25/12/2014"});

		final Collection<Ingredient> ingredients = ingredientsParser.getIngredients(csvData);

		assertTrue(ingredients.contains(createIngredient("cheese", 2, Unit.slices, "25/12/2014")));
	}

	@Test
	public void testGetIngredients_multipleItems() throws ParseException {
		final Collection<String[]> csvData = new ArrayList<String[]>();
		csvData.add(new String[]{"cheese", "2", "slices", "25/12/2014"});
		csvData.add(new String[]{"crackers", "10", "of", "14/04/2014"});

		final Collection<Ingredient> ingredients = ingredientsParser.getIngredients(csvData);

		assertTrue(ingredients.contains(createIngredient("cheese", 2, Unit.slices, "25/12/2014")));
		assertTrue(ingredients.contains(createIngredient("crackers", 10, Unit.of, "14/04/2014")));
	}

	//-------- Helper methods --------

	private Ingredient createIngredient(String item, int amount, Unit unit, String useByStr) throws ParseException {
		final Ingredient expectedIngredient = new Ingredient();
		expectedIngredient.setItem(item);
		expectedIngredient.setAmount(amount);
		expectedIngredient.setUnit(unit);
		expectedIngredient.setUseBy(useByStr);
		return expectedIngredient;
	}

}
