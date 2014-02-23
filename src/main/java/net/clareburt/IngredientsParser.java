package net.clareburt;

import net.clareburt.model.Ingredient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Justin Clareburt
 * @since 23/02/14
 */
public class IngredientsParser {

	public Collection<Ingredient> getIngredients(Collection<String[]> csvData) throws ParseException {
		final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		if (csvData != null) {
			for (String[] row : csvData) {
				ingredients.add(new Ingredient(row[0], row[1], row[2], row[3]));
			}
		}
		return ingredients;
	}

}
