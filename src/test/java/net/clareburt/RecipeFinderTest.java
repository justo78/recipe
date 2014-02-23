package net.clareburt;

import net.clareburt.exception.ParsingException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class RecipeFinderTest {

	private RecipeFinder recipeFinder;

	@Before
	public void setup() {
		recipeFinder = RecipeFinder.getInstance();
	}

	@Test(expected = ParsingException.class)
	public void shouldThrowParsingExceptionForInvalidCsvFilename() throws ParsingException {
		try {
			recipeFinder.generateRecipesFromFilenames("invalid.csv", "dummy.json");
			fail("ParsingException expected due to invalid csv filename");
		} catch (ParsingException e) {
			assertEquals("File not found: invalid.csv", e.getMessage());
			throw e;
		}
	}

	@Test(expected = ParsingException.class)
	public void shouldThrowParsingExceptionForInvalidJsonFilename() throws ParsingException {
		try {
			recipeFinder.generateRecipesFromFilenames("src\\test\\resources\\fridge.csv", "dummy.json");
			fail("ParsingException expected due to invalid json filename");
		} catch (ParsingException e) {
			assertEquals("File not found: dummy.json", e.getMessage());
			throw e;
		}
	}

	@Test
	public void shouldReturnValidResponseForValidFilenames() throws ParsingException {
		final String suggestion = recipeFinder.generateRecipesFromFilenames("src\\test\\resources\\fridge.csv", "src\\test\\resources\\recipes.json");
		assertEquals("salad sandwich", suggestion);
	}

}
