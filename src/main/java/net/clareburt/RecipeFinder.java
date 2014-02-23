package net.clareburt;

import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.JsonParseException;
import net.clareburt.exception.ParsingException;
import net.clareburt.model.Ingredient;
import net.clareburt.model.Recipe;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

/**
 * @author Justin Clareburt
 * @since 22/02/14
 */
public class RecipeFinder {

	private static final Logger logger = LoggerFactory.getLogger(RecipeFinder.class);

	private static final RecipeFinder instance = new RecipeFinder();

	private IngredientsParser ingredientsParser;
	private RecipeParser recipeParser;
	private RecipeGenerator recipeGenerator;

	private RecipeFinder() {
		ingredientsParser = new IngredientsParser();
		recipeParser = new RecipeParser();
		recipeGenerator = new RecipeGenerator();
	}

	public static RecipeFinder getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Invalid input parameters.");
			System.out.println("RecipeFinder fridgeCSV recipesJson ");
		}
		final String fridgeCsvFilename = args[0];
		final String recipesJsonFilename = args[1];

		try {
			final String recipeSuggestion = RecipeFinder.getInstance().generateRecipesFromFilenames(fridgeCsvFilename, recipesJsonFilename);
			System.out.println(recipeSuggestion);
		} catch (ParsingException e) {
			System.out.println(e.getMessage());
		}
	}

	public String generateRecipesFromFilenames(String fridgeCsvFilename, String recipesJsonFilename) throws ParsingException {
		// Read and parse fridge items
		final Collection<Ingredient> fridgeItems = getIngredients(fridgeCsvFilename);

		// Read and parse recipes
		final Collection<Recipe> recipes = getRecipes(recipesJsonFilename);

		// Determine best recipe
		return recipeGenerator.generateRecipe(fridgeItems, recipes);
	}

	private Collection<Ingredient> getIngredients(String fridgeCsvFilename) throws ParsingException {
		final Collection<Ingredient> fridgeItems;
		try {
			fridgeItems = ingredientsParser.getIngredients(new CSVReader(new FileReader(new File(fridgeCsvFilename))).readAll());
			logger.debug("fridgeItems = " + fridgeItems);
		} catch (FileNotFoundException e) {
			throw new ParsingException("File not found: " + fridgeCsvFilename);
		} catch (IOException e) {
			throw new ParsingException("Error reading file: " + fridgeCsvFilename);
		} catch (ParseException e) {
			throw new ParsingException("Error parsing date: " + e.getMessage());
		}
		return fridgeItems;
	}

	private Collection<Recipe> getRecipes(String recipesJsonFilename) throws ParsingException {
		final Collection<Recipe> recipes;
		try {
			recipes = recipeParser.getRecipesFromJson(FileUtils.readFileToString(new File(recipesJsonFilename)));
			logger.debug("recipes = " + recipes);
		} catch (IOException e) {
			throw new ParsingException("File not found: " + recipesJsonFilename);
		} catch (JsonParseException e) {
			throw new ParsingException("Error reading recipe Json: " + recipesJsonFilename);
		}
		return recipes;
	}

}
