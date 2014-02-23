package net.clareburt.model;

import java.util.Collection;

/**
 * @author Justin Clareburt
 * @since 22/02/14
 */
public class Recipe {

	String name;
	Collection<Ingredient> ingredients;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(Collection<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Recipe recipe = (Recipe) o;

		if (ingredients != null ? !ingredients.equals(recipe.ingredients) : recipe.ingredients != null) return false;
		if (name != null ? !name.equals(recipe.name) : recipe.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (ingredients != null ? ingredients.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Recipe{" +
				"name='" + name + '\'' +
				", ingredients=" + ingredients +
				'}';
	}
}
