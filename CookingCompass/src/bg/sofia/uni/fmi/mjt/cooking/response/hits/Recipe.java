package bg.sofia.uni.fmi.mjt.cooking.response.hits;

import java.util.Collection;

public class Recipe {
    private String label;
    private Collection<String> dietLabels;
    private Collection<String> healthLabels;
    private double totalWeight;
    private Collection<String> mealType;
    private Collection<String> cuisineType;
    private Collection<String> dishType;
    private Collection<String> ingredientLines;

    public Recipe(String label, Collection<String> dietLabels,
                  Collection<String> healthLabels, double totalWeight,
                  Collection<String> mealType, Collection<String> cuisineType,
                  Collection<String> dishType, Collection<String> ingredientLines) {
        this.label = label;
        this.dietLabels = dietLabels;
        this.healthLabels = healthLabels;
        this.totalWeight = totalWeight;
        this.mealType = mealType;
        this.cuisineType = cuisineType;
        this.dishType = dishType;
        this.ingredientLines = ingredientLines;
    }

    public String getLabel() {
        return label;
    }

    public Collection<String> getMealType() {
        return mealType;
    }

    public Collection<String> getDishType() {
        return dishType;
    }

    public Collection<String> getCuisineType() {
        return cuisineType;
    }

    public Collection<String> getHealthLabels() {
        return healthLabels;
    }

    public Collection<String> getDietLabels() {
        return dietLabels;
    }

    public Collection<String> getIngredientLines() {
        return ingredientLines;
    }

    public double getTotalWeight() {
        return totalWeight;
    }
}

