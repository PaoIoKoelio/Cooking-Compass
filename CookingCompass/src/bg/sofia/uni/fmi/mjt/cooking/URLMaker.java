package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.eatingtime.HealthLabels;
import bg.sofia.uni.fmi.mjt.cooking.eatingtime.MealType;

import java.util.Collection;

public class URLMaker {
    private static final String API_ENDPOINT = "https://api.edamam.com/api/recipes/v2";
    private static final String APP_ID = "e81c75e1";
    private static final String APP_KEY = "4d5f15ebb11bace49edf6660840b5499";

    public static String makeKeywordURL(Collection<String> keywords) {
        StringBuilder url = new StringBuilder();
        initiateKeywordURL(url, keywords);
        return url.toString();
    }

    public static String makeMealTypeURL(Collection<MealType> mealTypes) {
        StringBuilder url = new StringBuilder();
        initiateRegURL(url);
        addMealTypeURL(url, mealTypes);
        return url.toString();
    }

    public static String makeHealthLabelURL(Collection<HealthLabels> healthLabels) {
        StringBuilder url = new StringBuilder();
        initiateRegURL(url);
        addHealthLabelURL(url, healthLabels);
        return url.toString();
    }

    public static String makeMealTypeAndHealthLabelURL(
            Collection<MealType> mealTypes,
            Collection<HealthLabels> healthLabels) {
        StringBuilder url = new StringBuilder();
        initiateRegURL(url);
        addMealTypeURL(url, mealTypes);
        addHealthLabelURL(url, healthLabels);
        return url.toString();
    }

    public static String makeKeywordAndMealTypeURL(
            Collection<String> keywords,
            Collection<MealType> mealTypes) {
        StringBuilder url = new StringBuilder();
        initiateKeywordURL(url, keywords);
        addMealTypeURL(url, mealTypes);
        return url.toString();
    }

    public static String makeKeywordAndHealthLabelURL(
            Collection<String> keywords,
            Collection<HealthLabels> healthLabels) {
        StringBuilder url = new StringBuilder();
        initiateKeywordURL(url, keywords);
        addHealthLabelURL(url, healthLabels);
        return url.toString();
    }

    public static String makeKeywordAndMealTypeAndHealthLabelURL(
            Collection<String> keywords, Collection<MealType> mealTypes,
            Collection<HealthLabels> healthLabels) {
        StringBuilder url = new StringBuilder();
        initiateKeywordURL(url, keywords);
        addMealTypeURL(url, mealTypes);
        addHealthLabelURL(url, healthLabels);
        return url.toString();
    }

    public static void initiateKeywordURL(StringBuilder url,
                                          Collection<String> keywords) {
        url.append("type=public&q=");
        for (String keyword : keywords) {
            url.append(keyword).append(" ");
        }
        url.deleteCharAt(url.length() - 1);
        url.append("&app_id=").append(APP_ID);
        url.append("&app_key=").append(APP_KEY);
    }

    public static void initiateRegURL(StringBuilder url) {
        url.append(API_ENDPOINT);
        url.append("?type=public&app_id=").append(APP_ID);
        url.append("&app_key=").append(APP_KEY);
    }

    public static void addMealTypeURL(StringBuilder url,
                                      Collection<MealType> mealTypes) {
        for (MealType mealType : mealTypes) {
            url.append("&mealType=");
            url.append(mealType.toString());
        }
    }

    public static void addHealthLabelURL(StringBuilder url,
                                         Collection<HealthLabels> healthLabels) {
        for (HealthLabels healthLabel : healthLabels) {
            url.append("&health=");
            url.append(healthLabel.toString());
        }
    }

}

