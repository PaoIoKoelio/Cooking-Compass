package bg.sofia.uni.fmi.mjt.cookingtest;

import bg.sofia.uni.fmi.mjt.cooking.CookingClient;
import bg.sofia.uni.fmi.mjt.cooking.URLMaker;
import bg.sofia.uni.fmi.mjt.cooking.eatingtime.HealthLabels;
import bg.sofia.uni.fmi.mjt.cooking.eatingtime.MealType;
import bg.sofia.uni.fmi.mjt.cooking.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.cooking.exception.NotAuthorizedException;
import bg.sofia.uni.fmi.mjt.cooking.exception.NotFoundException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RequestTimeoutException;
import bg.sofia.uni.fmi.mjt.cooking.response.hits.Recipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.Collection;
import java.util.List;

public class URLMakerTest {

    @Test
    void MealTypeUrlTest() {
        Collection<MealType> mealTypes = List.of(MealType.LUNCH, MealType.DINNER);
        Assertions.assertEquals(URLMaker.makeMealTypeURL(mealTypes), "https://api.edamam.com/api/recipes/v2?type=public&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499&mealType=Lunch&mealType=Dinner");
    }

    @Test
    void HealthLabelUrlTest() {
        Collection<HealthLabels> healthLabels = List.of(HealthLabels.DASH, HealthLabels.EGG_FREE);
        Assertions.assertEquals(URLMaker.makeHealthLabelURL(healthLabels), "https://api.edamam.com/api/recipes/v2?type=public&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499&health=DASH&health=egg-free");
    }

    @Test
    void MealTypeAndHealthLabelTest() {
        Collection<MealType> mealTypes = List.of(MealType.LUNCH, MealType.DINNER);
        Collection<HealthLabels> healthLabels = List.of(HealthLabels.DASH, HealthLabels.EGG_FREE);
        Assertions.assertEquals("https://api.edamam.com/api/recipes/v2?type=public&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499&mealType=Lunch&mealType=Dinner&health=DASH&health=egg-free", URLMaker.makeMealTypeAndHealthLabelURL(mealTypes, healthLabels));
    }

    @Test
    void KeywordURLTest() {
        Collection<String> keywords = List.of("rice", "broccoli", "chicken");
        Assertions.assertEquals("type=public&q=rice broccoli chicken&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499", URLMaker.makeKeywordURL(keywords));
    }

    @Test
    void KeywordAndMealTypeUrlTest() {
        Collection<String> keywords = List.of("rice", "broccoli", "chicken");
        Collection<MealType> mealTypes = List.of(MealType.BREAKFAST, MealType.DINNER, MealType.LUNCH, MealType.SNACK, MealType.TEA_TIME);
        Assertions.assertEquals("type=public&q=rice broccoli chicken&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499&mealType=Breakfast&mealType=Dinner&mealType=Lunch&mealType=Snack&mealType=Teatime", URLMaker.makeKeywordAndMealTypeURL(keywords, mealTypes));
    }

    @Test
    void KeywordAndHealthLabelUrlTest() {
        Collection<String> keywords = List.of("rice", "broccoli", "chicken");
        Collection<HealthLabels> healthLabels = List.of(HealthLabels.ALCOHOL_COCKTAIL, HealthLabels.ALCOHOL_FREE, HealthLabels.CELERY_FREE, HealthLabels.MEDITERRANEAN, HealthLabels.MOLLUSK_FREE, HealthLabels.MUSTARD_FREE);
        Assertions.assertEquals("type=public&q=rice broccoli chicken&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499&health=alcohol-cocktail&health=alcohol-free&health=celery-free&health=Mediterranean&health=mollusk-free&health=mustard-free", URLMaker.makeKeywordAndHealthLabelURL(keywords, healthLabels));
    }

    @Test
    void KeywordAndMealTypeAndHealthLabelUrlTest() {
        Collection<String> keywords = List.of("rice", "broccoli", "chicken");
        Collection<MealType> mealTypes = List.of(MealType.LUNCH, MealType.DINNER);
        Collection<HealthLabels> healthLabels = List.of(HealthLabels.LUPINE_FREE, HealthLabels.EGG_FREE);
        Assertions.assertEquals("type=public&q=rice broccoli chicken&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499&mealType=Lunch&mealType=Dinner&health=lupine-free&health=egg-free", URLMaker.makeKeywordAndMealTypeAndHealthLabelURL(keywords, mealTypes, healthLabels));
    }
}
