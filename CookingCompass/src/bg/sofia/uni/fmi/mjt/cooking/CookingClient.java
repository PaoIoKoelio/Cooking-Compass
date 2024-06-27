package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.eatingtime.HealthLabels;
import bg.sofia.uni.fmi.mjt.cooking.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.cooking.exception.NotAuthorizedException;
import bg.sofia.uni.fmi.mjt.cooking.exception.NotFoundException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RequestTimeoutException;
import bg.sofia.uni.fmi.mjt.cooking.response.hits.Hit;
import bg.sofia.uni.fmi.mjt.cooking.eatingtime.MealType;
import bg.sofia.uni.fmi.mjt.cooking.response.hits.Recipe;

import bg.sofia.uni.fmi.mjt.cooking.response.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

public class CookingClient {

    private final HttpClient client;

    private static final int NOT_FOUND = 404;

    private static final int BAD_REQUEST = 400;

    private static final int NOT_AUTHORIZED = 401;

    private static final int REQUEST_TIMEOUT = 408;

    public CookingClient(HttpClient client) {
        this.client = client;
    }

    public List<Recipe> converter(String jsonResponse)
            throws IOException, InterruptedException {
        Gson gson = new Gson();
        Response response = gson.fromJson(jsonResponse,
                new TypeToken<Response>() {
                }.getType());
        Collection<Hit> hits = response.getHits();
        for (int i = 0; i < 2; i++) {
            if (response.getLinks().getNext() != null) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(response.getLinks().getNext().getHref())).build();
                jsonResponse = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                response = gson.fromJson(jsonResponse, new TypeToken<Response>() {
                }.getType());
                hits.addAll(response.getHits());
            }
        }
        return hits
                .stream()
                .map(Hit::getRecipe)
                .toList();
    }

    private List<Recipe> getRecipes(URI uri) throws NotFoundException,
            NotAuthorizedException, BadRequestException, RequestTimeoutException {
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .build();
            HttpResponse<String> jsonResponse = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (jsonResponse.statusCode() == NOT_FOUND) {
                throw new NotFoundException();
            }
            if (jsonResponse.statusCode() == NOT_AUTHORIZED) {
                throw new NotAuthorizedException();
            }
            if (jsonResponse.statusCode() == BAD_REQUEST) {
                throw new BadRequestException();
            }
            if (jsonResponse.statusCode() == REQUEST_TIMEOUT) {
                throw new RequestTimeoutException();
            }

            return converter(jsonResponse.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public List<Recipe> getRecipesByMealType(Collection<MealType> mealTypes)
            throws RequestTimeoutException, NotFoundException,
            BadRequestException, NotAuthorizedException {
        if (mealTypes == null) {
            throw new IllegalArgumentException();
        }
        URI uri = URI.create(URLMaker.makeMealTypeURL(mealTypes));
        return getRecipes(uri);
    }

    public List<Recipe> getRecipesByHealthLabels(Collection<HealthLabels> healthLabels)
            throws RequestTimeoutException, NotFoundException, BadRequestException, NotAuthorizedException {
        if (healthLabels == null) {
            throw new IllegalArgumentException();
        }
        URI uri = URI.create(URLMaker.makeHealthLabelURL(healthLabels));
        return getRecipes(uri);
    }

    public List<Recipe> getRecipesByMealTypesAndHealthLabels(Collection<MealType> mealTypes,
                                                             Collection<HealthLabels> healthLabels)
            throws RequestTimeoutException, NotFoundException, BadRequestException, NotAuthorizedException {
        if (mealTypes == null || healthLabels == null) {
            throw new IllegalArgumentException();
        }
        URI uri = URI.create(URLMaker.makeMealTypeAndHealthLabelURL(mealTypes, healthLabels));
        return getRecipes(uri);

    }

    public List<Recipe> getRecipesByKeywords(Collection<String> keywords)
            throws URISyntaxException, RequestTimeoutException,
            NotFoundException, BadRequestException, NotAuthorizedException {
        if (keywords == null) {
            throw new IllegalArgumentException();
        }
        URI uri = new URI("https", "api.edamam.com",
                "/api/recipes/v2", URLMaker.makeKeywordURL(keywords), null);
        return getRecipes(uri);
    }

    public List<Recipe> getRecipesByKeywordsAndMealType(
            Collection<String> keywords,
            Collection<MealType> mealTypes)
            throws URISyntaxException, RequestTimeoutException,
            NotFoundException, BadRequestException, NotAuthorizedException {
        if (keywords == null || mealTypes == null) {
            throw new IllegalArgumentException();
        }
        URI uri = new URI("https", "api.edamam.com", "/api/recipes/v2",
                URLMaker.makeKeywordAndMealTypeURL(keywords, mealTypes),
                null);
        return getRecipes(uri);
    }

    public List<Recipe> getRecipesByKeywordsAndHealthLabels(Collection<String> keywords,
                                                            Collection<HealthLabels> healthLabels)
            throws URISyntaxException, RequestTimeoutException,
            NotFoundException, BadRequestException, NotAuthorizedException {
        if (keywords == null || healthLabels == null) {
            throw new IllegalArgumentException();
        }
        URI uri = new URI("https",
                "api.edamam.com",
                "/api/recipes/v2",
                URLMaker.makeKeywordAndHealthLabelURL(keywords, healthLabels),
                null);
        return getRecipes(uri);
    }

    public List<Recipe> getRecipesByKeywordsAndHealthLabelsAndMealTypes(
            Collection<String> keywords,
            Collection<HealthLabels> healthLabels,
            Collection<MealType> mealTypes)
            throws URISyntaxException, RequestTimeoutException,
            NotFoundException, BadRequestException, NotAuthorizedException {
        if (keywords == null || mealTypes == null || healthLabels == null) {
            throw new IllegalArgumentException();
        }
        URI uri = new URI("https",
                "api.edamam.com",
                "/api/recipes/v2",
                URLMaker.makeKeywordAndMealTypeAndHealthLabelURL(keywords, mealTypes, healthLabels),
                null);
        return getRecipes(uri);
    }
}
