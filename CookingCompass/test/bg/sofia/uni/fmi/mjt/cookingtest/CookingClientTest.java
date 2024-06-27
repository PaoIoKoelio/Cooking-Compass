package bg.sofia.uni.fmi.mjt.cookingtest;

import bg.sofia.uni.fmi.mjt.cooking.CookingClient;
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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CookingClientTest {
    @Mock
    private HttpClient httpClientMock;

    private CookingClient client;

    @Mock
    private HttpResponse<String> httpResponseMock;


    @Test
    void TestNullValuePass() {
        HttpClient client = HttpClient.newBuilder().build();
        CookingClient cookingClient = new CookingClient(client);
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByKeywords(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByMealType(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByHealthLabels(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByKeywordsAndHealthLabelsAndMealTypes(null, null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByKeywordsAndMealType(null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByMealTypesAndHealthLabels(null, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cookingClient.getRecipesByKeywordsAndHealthLabels(null, null));
    }

    @Test
    void testMockedJSONConversion() throws Exception {
        client = new CookingClient(httpClientMock);
        Mockito.when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        String json = """
                  {
                  "from": 1,
                  "to": 1,
                  "count": 1,
                  "_links": {},
                  "hits": [
                    {
                      "recipe": {
                        "uri": "http://www.edamam.com/ontologies/edamam.owl#recipe_0562e1196ded5fa589928b95db27e2c3",
                        "label": "Breakfast Kedgeree",
                        "image": "https://edamam-product-images.s3.amazonaws.com/web-img/df4/df454e5ab117aaa45bf88357baad9ad0.jpg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEF0aCXVzLWVhc3QtMSJGMEQCIHWRGKwUNPawxpl%2BvnL3cb35yGHK0d2WFxROl44oolTEAiBWORlQGvT%2FokfyGrWjY61solEGatJlSeSnm5%2FmoSbOHSq5BQgVEAAaDDE4NzAxNzE1MDk4NiIM7yxEQzQN4OML%2FOR%2BKpYFl%2FrdSysAVcRaORaXEoNdYYsdqODkMbOcA%2BeGZcTAL%2FZdIMafrtSQObQb%2FcetF0vD5SS1IUG54U1XJtbQdziWgDDkLOenvGyCoqA3QCiTdDvHkvsYacFJ74%2FHIZkB04JXwEJ97yxcagC%2B6i5pXQujLY3uNuAHtaESQE49F4WWDn3MK0lJEQs6nbSOW2E1TmDle%2BqgVMRazE8n7Pl5CSvHOGD6gPmcCR2gvaNr56T6Kz7RHDMcdt7PknkWXgfRM80NW73i6nvNhoygYGUNiQ%2FPtxaUBwW%2BFy2lSaI0JbKBHtRHVBBKh%2BrD7y%2FPFiB3bY8vXK6acWs5rKv8mhGP6HntPhY1ppz8oWrG%2F%2BnoleTWfV%2BBm1ZnzzQWyPtTSPBQkZDhNKEk554h1WQq13k%2BSZNcDZF0H0uCX0L2%2FvhUtzd%2BjbPpcP6aklopN4%2B8EgLbdzvtw7gVB1lRvfvzVWJH15Dxo%2BwKNxCglhVhDNOEU%2FMi4%2FPWGbH9Pv9LO8hpK5WWpbKEX5AsB3ePk%2B4MHl501CkSCPwfGtiHVB%2FjOVH8ViOCfkOXoZU84489efMCYKk0%2Fjx38QuH2OREeSIlLWN6IROiOBQOurDuZfnMvHkUgN0b7qlQ0%2Fbdk%2FkQq1ivD3cqSNSEib4DEYFuMSQkXORyHPGE7dbpIA84bIwmL4MVWLOxohj%2BdulSvqSlTSIuossGvvObUXA7idkiZQmgAKsdLFGwpTEXp5aar0vmZ72bEL4J1Wmr%2BYB3kppbs8CfIc%2BzDPuWYX%2BdKoumDWOkjnJdbCxzupvGSdSyFSEsymNvsfAcNeIv%2BbJ%2FgkKkFdR83%2BDJaCOEJ%2FuWfyX094iHatHRf6j6q0O%2B4VkZSRdwFfiEpBTFaGs3htZKJxAw9vOurQY6sgFlkqaX0WzGeY9xZ4XQGnCZJZPJNfjn8tzKwfRYUSH6cGWE2zIyYDtAuTJqDBeX2wKJQNAP6F5aMD08sEPF0jacKtS7QNPqTPhbSSJYE8bdu2kD87gh1PWbGthAt1O92j5MqUk6e%2FTg0f48pAzC9bTQN%2BSP%2BgP6bHSHH6A%2B53PRKH%2FGUwJy7teTeKX3ehKv2guXOeyyquIpIv9bJT0%2FjPAuVoR58mhEg3Nyxi0ZhBFqvLwh&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240120T131605Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=ASIASXCYXIIFFSD5OW72%2F20240120%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=8d3c5335e6741927e8a790fd8dbadd23088ff4268f7f4dd0a0dd7ad34c0bd32b",
                        "images": {
                          "THUMBNAIL": {
                            "url": "https://edamam-product-images.s3.amazonaws.com/web-img/df4/df454e5ab117aaa45bf88357baad9ad0-s.jpg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEF0aCXVzLWVhc3QtMSJGMEQCIHWRGKwUNPawxpl%2BvnL3cb35yGHK0d2WFxROl44oolTEAiBWORlQGvT%2FokfyGrWjY61solEGatJlSeSnm5%2FmoSbOHSq5BQgVEAAaDDE4NzAxNzE1MDk4NiIM7yxEQzQN4OML%2FOR%2BKpYFl%2FrdSysAVcRaORaXEoNdYYsdqODkMbOcA%2BeGZcTAL%2FZdIMafrtSQObQb%2FcetF0vD5SS1IUG54U1XJtbQdziWgDDkLOenvGyCoqA3QCiTdDvHkvsYacFJ74%2FHIZkB04JXwEJ97yxcagC%2B6i5pXQujLY3uNuAHtaESQE49F4WWDn3MK0lJEQs6nbSOW2E1TmDle%2BqgVMRazE8n7Pl5CSvHOGD6gPmcCR2gvaNr56T6Kz7RHDMcdt7PknkWXgfRM80NW73i6nvNhoygYGUNiQ%2FPtxaUBwW%2BFy2lSaI0JbKBHtRHVBBKh%2BrD7y%2FPFiB3bY8vXK6acWs5rKv8mhGP6HntPhY1ppz8oWrG%2F%2BnoleTWfV%2BBm1ZnzzQWyPtTSPBQkZDhNKEk554h1WQq13k%2BSZNcDZF0H0uCX0L2%2FvhUtzd%2BjbPpcP6aklopN4%2B8EgLbdzvtw7gVB1lRvfvzVWJH15Dxo%2BwKNxCglhVhDNOEU%2FMi4%2FPWGbH9Pv9LO8hpK5WWpbKEX5AsB3ePk%2B4MHl501CkSCPwfGtiHVB%2FjOVH8ViOCfkOXoZU84489efMCYKk0%2Fjx38QuH2OREeSIlLWN6IROiOBQOurDuZfnMvHkUgN0b7qlQ0%2Fbdk%2FkQq1ivD3cqSNSEib4DEYFuMSQkXORyHPGE7dbpIA84bIwmL4MVWLOxohj%2BdulSvqSlTSIuossGvvObUXA7idkiZQmgAKsdLFGwpTEXp5aar0vmZ72bEL4J1Wmr%2BYB3kppbs8CfIc%2BzDPuWYX%2BdKoumDWOkjnJdbCxzupvGSdSyFSEsymNvsfAcNeIv%2BbJ%2FgkKkFdR83%2BDJaCOEJ%2FuWfyX094iHatHRf6j6q0O%2B4VkZSRdwFfiEpBTFaGs3htZKJxAw9vOurQY6sgFlkqaX0WzGeY9xZ4XQGnCZJZPJNfjn8tzKwfRYUSH6cGWE2zIyYDtAuTJqDBeX2wKJQNAP6F5aMD08sEPF0jacKtS7QNPqTPhbSSJYE8bdu2kD87gh1PWbGthAt1O92j5MqUk6e%2FTg0f48pAzC9bTQN%2BSP%2BgP6bHSHH6A%2B53PRKH%2FGUwJy7teTeKX3ehKv2guXOeyyquIpIv9bJT0%2FjPAuVoR58mhEg3Nyxi0ZhBFqvLwh&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240120T131605Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=ASIASXCYXIIFFSD5OW72%2F20240120%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=a5ce5fe74eeefae904ac31bd78e0728bc2261816e930811c619cf5567f4db100",
                            "width": 100,
                            "height": 100
                          },
                          "SMALL": {
                            "url": "https://edamam-product-images.s3.amazonaws.com/web-img/df4/df454e5ab117aaa45bf88357baad9ad0-m.jpg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEF0aCXVzLWVhc3QtMSJGMEQCIHWRGKwUNPawxpl%2BvnL3cb35yGHK0d2WFxROl44oolTEAiBWORlQGvT%2FokfyGrWjY61solEGatJlSeSnm5%2FmoSbOHSq5BQgVEAAaDDE4NzAxNzE1MDk4NiIM7yxEQzQN4OML%2FOR%2BKpYFl%2FrdSysAVcRaORaXEoNdYYsdqODkMbOcA%2BeGZcTAL%2FZdIMafrtSQObQb%2FcetF0vD5SS1IUG54U1XJtbQdziWgDDkLOenvGyCoqA3QCiTdDvHkvsYacFJ74%2FHIZkB04JXwEJ97yxcagC%2B6i5pXQujLY3uNuAHtaESQE49F4WWDn3MK0lJEQs6nbSOW2E1TmDle%2BqgVMRazE8n7Pl5CSvHOGD6gPmcCR2gvaNr56T6Kz7RHDMcdt7PknkWXgfRM80NW73i6nvNhoygYGUNiQ%2FPtxaUBwW%2BFy2lSaI0JbKBHtRHVBBKh%2BrD7y%2FPFiB3bY8vXK6acWs5rKv8mhGP6HntPhY1ppz8oWrG%2F%2BnoleTWfV%2BBm1ZnzzQWyPtTSPBQkZDhNKEk554h1WQq13k%2BSZNcDZF0H0uCX0L2%2FvhUtzd%2BjbPpcP6aklopN4%2B8EgLbdzvtw7gVB1lRvfvzVWJH15Dxo%2BwKNxCglhVhDNOEU%2FMi4%2FPWGbH9Pv9LO8hpK5WWpbKEX5AsB3ePk%2B4MHl501CkSCPwfGtiHVB%2FjOVH8ViOCfkOXoZU84489efMCYKk0%2Fjx38QuH2OREeSIlLWN6IROiOBQOurDuZfnMvHkUgN0b7qlQ0%2Fbdk%2FkQq1ivD3cqSNSEib4DEYFuMSQkXORyHPGE7dbpIA84bIwmL4MVWLOxohj%2BdulSvqSlTSIuossGvvObUXA7idkiZQmgAKsdLFGwpTEXp5aar0vmZ72bEL4J1Wmr%2BYB3kppbs8CfIc%2BzDPuWYX%2BdKoumDWOkjnJdbCxzupvGSdSyFSEsymNvsfAcNeIv%2BbJ%2FgkKkFdR83%2BDJaCOEJ%2FuWfyX094iHatHRf6j6q0O%2B4VkZSRdwFfiEpBTFaGs3htZKJxAw9vOurQY6sgFlkqaX0WzGeY9xZ4XQGnCZJZPJNfjn8tzKwfRYUSH6cGWE2zIyYDtAuTJqDBeX2wKJQNAP6F5aMD08sEPF0jacKtS7QNPqTPhbSSJYE8bdu2kD87gh1PWbGthAt1O92j5MqUk6e%2FTg0f48pAzC9bTQN%2BSP%2BgP6bHSHH6A%2B53PRKH%2FGUwJy7teTeKX3ehKv2guXOeyyquIpIv9bJT0%2FjPAuVoR58mhEg3Nyxi0ZhBFqvLwh&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240120T131605Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=ASIASXCYXIIFFSD5OW72%2F20240120%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=72accdd6e0733e4561a64ddf0c8faac5a67bd69817b532a2df5d6b91635582ce",
                            "width": 200,
                            "height": 200
                          },
                          "REGULAR": {
                            "url": "https://edamam-product-images.s3.amazonaws.com/web-img/df4/df454e5ab117aaa45bf88357baad9ad0.jpg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEF0aCXVzLWVhc3QtMSJGMEQCIHWRGKwUNPawxpl%2BvnL3cb35yGHK0d2WFxROl44oolTEAiBWORlQGvT%2FokfyGrWjY61solEGatJlSeSnm5%2FmoSbOHSq5BQgVEAAaDDE4NzAxNzE1MDk4NiIM7yxEQzQN4OML%2FOR%2BKpYFl%2FrdSysAVcRaORaXEoNdYYsdqODkMbOcA%2BeGZcTAL%2FZdIMafrtSQObQb%2FcetF0vD5SS1IUG54U1XJtbQdziWgDDkLOenvGyCoqA3QCiTdDvHkvsYacFJ74%2FHIZkB04JXwEJ97yxcagC%2B6i5pXQujLY3uNuAHtaESQE49F4WWDn3MK0lJEQs6nbSOW2E1TmDle%2BqgVMRazE8n7Pl5CSvHOGD6gPmcCR2gvaNr56T6Kz7RHDMcdt7PknkWXgfRM80NW73i6nvNhoygYGUNiQ%2FPtxaUBwW%2BFy2lSaI0JbKBHtRHVBBKh%2BrD7y%2FPFiB3bY8vXK6acWs5rKv8mhGP6HntPhY1ppz8oWrG%2F%2BnoleTWfV%2BBm1ZnzzQWyPtTSPBQkZDhNKEk554h1WQq13k%2BSZNcDZF0H0uCX0L2%2FvhUtzd%2BjbPpcP6aklopN4%2B8EgLbdzvtw7gVB1lRvfvzVWJH15Dxo%2BwKNxCglhVhDNOEU%2FMi4%2FPWGbH9Pv9LO8hpK5WWpbKEX5AsB3ePk%2B4MHl501CkSCPwfGtiHVB%2FjOVH8ViOCfkOXoZU84489efMCYKk0%2Fjx38QuH2OREeSIlLWN6IROiOBQOurDuZfnMvHkUgN0b7qlQ0%2Fbdk%2FkQq1ivD3cqSNSEib4DEYFuMSQkXORyHPGE7dbpIA84bIwmL4MVWLOxohj%2BdulSvqSlTSIuossGvvObUXA7idkiZQmgAKsdLFGwpTEXp5aar0vmZ72bEL4J1Wmr%2BYB3kppbs8CfIc%2BzDPuWYX%2BdKoumDWOkjnJdbCxzupvGSdSyFSEsymNvsfAcNeIv%2BbJ%2FgkKkFdR83%2BDJaCOEJ%2FuWfyX094iHatHRf6j6q0O%2B4VkZSRdwFfiEpBTFaGs3htZKJxAw9vOurQY6sgFlkqaX0WzGeY9xZ4XQGnCZJZPJNfjn8tzKwfRYUSH6cGWE2zIyYDtAuTJqDBeX2wKJQNAP6F5aMD08sEPF0jacKtS7QNPqTPhbSSJYE8bdu2kD87gh1PWbGthAt1O92j5MqUk6e%2FTg0f48pAzC9bTQN%2BSP%2BgP6bHSHH6A%2B53PRKH%2FGUwJy7teTeKX3ehKv2guXOeyyquIpIv9bJT0%2FjPAuVoR58mhEg3Nyxi0ZhBFqvLwh&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240120T131605Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=ASIASXCYXIIFFSD5OW72%2F20240120%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=8d3c5335e6741927e8a790fd8dbadd23088ff4268f7f4dd0a0dd7ad34c0bd32b",
                            "width": 300,
                            "height": 300
                          },
                          "LARGE": {
                            "url": "https://edamam-product-images.s3.amazonaws.com/web-img/df4/df454e5ab117aaa45bf88357baad9ad0-l.jpg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEF0aCXVzLWVhc3QtMSJGMEQCIHWRGKwUNPawxpl%2BvnL3cb35yGHK0d2WFxROl44oolTEAiBWORlQGvT%2FokfyGrWjY61solEGatJlSeSnm5%2FmoSbOHSq5BQgVEAAaDDE4NzAxNzE1MDk4NiIM7yxEQzQN4OML%2FOR%2BKpYFl%2FrdSysAVcRaORaXEoNdYYsdqODkMbOcA%2BeGZcTAL%2FZdIMafrtSQObQb%2FcetF0vD5SS1IUG54U1XJtbQdziWgDDkLOenvGyCoqA3QCiTdDvHkvsYacFJ74%2FHIZkB04JXwEJ97yxcagC%2B6i5pXQujLY3uNuAHtaESQE49F4WWDn3MK0lJEQs6nbSOW2E1TmDle%2BqgVMRazE8n7Pl5CSvHOGD6gPmcCR2gvaNr56T6Kz7RHDMcdt7PknkWXgfRM80NW73i6nvNhoygYGUNiQ%2FPtxaUBwW%2BFy2lSaI0JbKBHtRHVBBKh%2BrD7y%2FPFiB3bY8vXK6acWs5rKv8mhGP6HntPhY1ppz8oWrG%2F%2BnoleTWfV%2BBm1ZnzzQWyPtTSPBQkZDhNKEk554h1WQq13k%2BSZNcDZF0H0uCX0L2%2FvhUtzd%2BjbPpcP6aklopN4%2B8EgLbdzvtw7gVB1lRvfvzVWJH15Dxo%2BwKNxCglhVhDNOEU%2FMi4%2FPWGbH9Pv9LO8hpK5WWpbKEX5AsB3ePk%2B4MHl501CkSCPwfGtiHVB%2FjOVH8ViOCfkOXoZU84489efMCYKk0%2Fjx38QuH2OREeSIlLWN6IROiOBQOurDuZfnMvHkUgN0b7qlQ0%2Fbdk%2FkQq1ivD3cqSNSEib4DEYFuMSQkXORyHPGE7dbpIA84bIwmL4MVWLOxohj%2BdulSvqSlTSIuossGvvObUXA7idkiZQmgAKsdLFGwpTEXp5aar0vmZ72bEL4J1Wmr%2BYB3kppbs8CfIc%2BzDPuWYX%2BdKoumDWOkjnJdbCxzupvGSdSyFSEsymNvsfAcNeIv%2BbJ%2FgkKkFdR83%2BDJaCOEJ%2FuWfyX094iHatHRf6j6q0O%2B4VkZSRdwFfiEpBTFaGs3htZKJxAw9vOurQY6sgFlkqaX0WzGeY9xZ4XQGnCZJZPJNfjn8tzKwfRYUSH6cGWE2zIyYDtAuTJqDBeX2wKJQNAP6F5aMD08sEPF0jacKtS7QNPqTPhbSSJYE8bdu2kD87gh1PWbGthAt1O92j5MqUk6e%2FTg0f48pAzC9bTQN%2BSP%2BgP6bHSHH6A%2B53PRKH%2FGUwJy7teTeKX3ehKv2guXOeyyquIpIv9bJT0%2FjPAuVoR58mhEg3Nyxi0ZhBFqvLwh&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240120T131605Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=ASIASXCYXIIFFSD5OW72%2F20240120%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=2ca5cf3dc0994188422313d3e59b92e827e4d1cd320cdeeea754908a83527c53",
                            "width": 600,
                            "height": 600
                          }
                        },
                        "source": "naturalkitchenadventures.com",
                        "url": "http://naturalkitchenadventures.com/live-below-the-line/day-5-eats/",
                        "shareAs": "http://www.edamam.com/recipe/breakfast-kedgeree-0562e1196ded5fa589928b95db27e2c3/rice+chicken+broccoli/low-potassium",
                        "yield": 4,
                        "dietLabels": [
                          "Balanced",
                          "Low-Sodium"
                        ],
                        "healthLabels": [
                          "Sugar-Conscious",
                          "Low Potassium",
                          "Kidney-Friendly",
                          "Gluten-Free",
                          "Wheat-Free",
                          "Peanut-Free",
                          "Tree-Nut-Free",
                          "Soy-Free",
                          "Fish-Free",
                          "Shellfish-Free",
                          "Pork-Free",
                          "Red-Meat-Free",
                          "Crustacean-Free",
                          "Celery-Free",
                          "Mustard-Free",
                          "Sesame-Free",
                          "Lupine-Free",
                          "Mollusk-Free",
                          "Alcohol-Free",
                          "Sulfite-Free"
                        ],
                        "cautions": [
                          "Tree-Nuts",
                          "Sulfites"
                        ],
                        "ingredientLines": [
                          "* 60g white rice",
                          "* chicken stock",
                          "* 2 egg",
                          "* 1/2 tsp coconut oil",
                          "* 1 garlic clove, minced",
                          "* 50g frozen mixed vegetable (carrots, pea, broccoli, cauliflower)",
                          "* 1/4 tsp turmeric",
                          "* 1/4 tsp cumin",
                          "* pinch salt",
                          "* leftover yoghurt, about 1/2 tb"
                        ],
                        "ingredients": [
                          {
                            "text": "* 60g white rice",
                            "quantity": 60,
                            "measure": "gram",
                            "food": "white rice",
                            "weight": 60,
                            "foodCategory": "grains",
                            "foodId": "food_bpumdjzb5rtqaeabb0kbgbcgr4t9",
                            "image": "https://www.edamam.com/food-img/0fc/0fc9fa8a3e0276198d75b2e259068f8a.jpg"
                          },
                          {
                            "text": "* chicken stock",
                            "quantity": 0,
                            "measure": null,
                            "food": "chicken stock",
                            "weight": 42.11229166673721,
                            "foodCategory": "canned soup",
                            "foodId": "food_bptblvzambd16nbhewqmhaw1rnh5",
                            "image": "https://www.edamam.com/food-img/26a/26a10c4cb4e07bab54d8a687ef5ac7d8.jpg"
                          },
                          {
                            "text": "* 2 egg",
                            "quantity": 2,
                            "measure": "<unit>",
                            "food": "egg",
                            "weight": 86,
                            "foodCategory": "Eggs",
                            "foodId": "food_bhpradua77pk16aipcvzeayg732r",
                            "image": "https://www.edamam.com/food-img/a7e/a7ec7c337cb47c6550b3b118e357f077.jpg"
                          },
                          {
                            "text": "* 1/2 tsp coconut oil",
                            "quantity": 0.5,
                            "measure": "teaspoon",
                            "food": "coconut oil",
                            "weight": 2.25,
                            "foodCategory": "Oils",
                            "foodId": "food_b40ubq8a0enoidbcr1tmfbwgs6aw",
                            "image": "https://www.edamam.com/food-img/3c9/3c97284c57e76e16093d51572b558be8.jpg"
                          },
                          {
                            "text": "* 1 garlic clove, minced",
                            "quantity": 1,
                            "measure": "clove",
                            "food": "garlic",
                            "weight": 3,
                            "foodCategory": "vegetables",
                            "foodId": "food_avtcmx6bgjv1jvay6s6stan8dnyp",
                            "image": "https://www.edamam.com/food-img/6ee/6ee142951f48aaf94f4312409f8d133d.jpg"
                          },
                          {
                            "text": "* 50g frozen mixed vegetable (carrots, pea, broccoli, cauliflower)",
                            "quantity": 50,
                            "measure": "gram",
                            "food": "frozen mixed vegetable",
                            "weight": 50,
                            "foodCategory": "vegetables",
                            "foodId": "food_bitqzx8b319psvbib2dufarphbxy",
                            "image": "https://www.edamam.com/food-img/f3f/f3fa6996eba331be219778406f67a5a3.jpg"
                          },
                          {
                            "text": "* 1/4 tsp turmeric",
                            "quantity": 0.25,
                            "measure": "teaspoon",
                            "food": "turmeric",
                            "weight": 0.75,
                            "foodCategory": "Condiments and sauces",
                            "foodId": "food_bc3ig84amucgmwba3vixyatnyd9b",
                            "image": "https://www.edamam.com/food-img/03e/03eb469286b3caf1ae9c13e4eba13587.jpg"
                          },
                          {
                            "text": "* 1/4 tsp cumin",
                            "quantity": 0.25,
                            "measure": "teaspoon",
                            "food": "cumin",
                            "weight": 0.525,
                            "foodCategory": "Condiments and sauces",
                            "foodId": "food_a8jjbx4biqndasapojdb5by3e92e",
                            "image": "https://www.edamam.com/food-img/07e/07e2a4eb77ce46591033846504817d35.jpg"
                          },
                          {
                            "text": "* pinch salt",
                            "quantity": 1,
                            "measure": "pinch",
                            "food": "salt",
                            "weight": 0.380208333815447,
                            "foodCategory": "Condiments and sauces",
                            "foodId": "food_btxz81db72hwbra2pncvebzzzum9",
                            "image": "https://www.edamam.com/food-img/694/6943ea510918c6025795e8dc6e6eaaeb.jpg"
                          },
                          {
                            "text": "* leftover yoghurt, about 1/2 tb",
                            "quantity": 0.5,
                            "measure": "tablespoon",
                            "food": "yoghurt",
                            "weight": 7.65624999987055,
                            "foodCategory": "yogurt",
                            "foodId": "food_a79ojfkbgdeekgblqmky9bunr8f6",
                            "image": "https://www.edamam.com/food-img/933/933eb3791b3a2175e007f1607d56b7e2.jpg"
                          }
                        ],
                        "calories": 423.6594874999464,
                        "totalCO2Emissions": 1131.1925440535272,
                        "co2EmissionsClass": "C",
                        "totalWeight": 252.6737500004232,
                        "totalTime": 0,
                        "cuisineType": [
                          "american"
                        ],
                        "mealType": [
                          "breakfast"
                        ],
                        "dishType": [
                          "starter"
                        ],
                        "totalNutrients": {
                          "ENERC_KCAL": {
                            "label": "Energy",
                            "quantity": 423.6594874999464,
                            "unit": "kcal"
                          },
                          "FAT": {
                            "label": "Fat",
                            "quantity": 11.926975624996638,
                            "unit": "g"
                          },
                          "FASAT": {
                            "label": "Saturated",
                            "quantity": 5.012366706247509,
                            "unit": "g"
                          },
                          "FATRN": {
                            "label": "Trans",
                            "quantity": 0.033729999999999996,
                            "unit": "g"
                          },
                          "FAMS": {
                            "label": "Monounsaturated",
                            "quantity": 3.8047863499992545,
                            "unit": "g"
                          },
                          "FAPU": {
                            "label": "Polyunsaturated",
                            "quantity": 2.018452931250031,
                            "unit": "g"
                          },
                          "CHOCDF": {
                            "label": "Carbs",
                            "quantity": 58.52084514582979,
                            "unit": "g"
                          },
                          "CHOCDF.net": {
                            "label": "Carbohydrates (net)",
                            "quantity": 56.23247014582979,
                            "unit": "g"
                          },
                          "FIBTG": {
                            "label": "Fiber",
                            "quantity": 2.288375,
                            "unit": "g"
                          },
                          "SUGAR": {
                            "label": "Sugars",
                            "quantity": 1.4062429583284155,
                            "unit": "g"
                          },
                          "PROCNT": {
                            "label": "Protein",
                            "quantity": 18.150751624997287,
                            "unit": "g"
                          },
                          "CHOLE": {
                            "label": "Cholesterol",
                            "quantity": 322.17868124998535,
                            "unit": "mg"
                          },
                          "NA": {
                            "label": "Sodium",
                            "quantity": 359.0777856037681,
                            "unit": "mg"
                          },
                          "CA": {
                            "label": "Calcium",
                            "quantity": 88.27893124996118,
                            "unit": "mg"
                          },
                          "MG": {
                            "label": "Magnesium",
                            "quantity": 50.15854374999211,
                            "unit": "mg"
                          },
                          "K": {
                            "label": "Potassium",
                            "quantity": 369.4230104165786,
                            "unit": "mg"
                          },
                          "FE": {
                            "label": "Iron",
                            "quantity": 3.366743625001675,
                            "unit": "mg"
                          },
                          "ZN": {
                            "label": "Zinc",
                            "quantity": 2.229109291666483,
                            "unit": "mg"
                          },
                          "P": {
                            "label": "Phosphorus",
                            "quantity": 292.67600624989615,
                            "unit": "mg"
                          },
                          "VITA_RAE": {
                            "label": "Vitamin A",
                            "quantity": 267.4243104166324,
                            "unit": "µg"
                          },
                          "VITC": {
                            "label": "Vitamin C",
                            "quantity": 6.304180833332827,
                            "unit": "mg"
                          },
                          "THIA": {
                            "label": "Thiamin (B1)",
                            "quantity": 0.16409161458332047,
                            "unit": "mg"
                          },
                          "RIBF": {
                            "label": "Riboflavin (B2)",
                            "quantity": 0.517129072916543,
                            "unit": "mg"
                          },
                          "NIA": {
                            "label": "Niacin (B3)",
                            "quantity": 2.375786395834351,
                            "unit": "mg"
                          },
                          "VITB6A": {
                            "label": "Vitamin B6",
                            "quantity": 0.34962474791666825,
                            "unit": "mg"
                          },
                          "FOLDFE": {
                            "label": "Folate equivalent (total)",
                            "quantity": 63.2540520833278,
                            "unit": "µg"
                          },
                          "FOLFD": {
                            "label": "Folate (food)",
                            "quantity": 63.2540520833278,
                            "unit": "µg"
                          },
                          "FOLAC": {
                            "label": "Folic acid",
                            "quantity": 0,
                            "unit": "µg"
                          },
                          "VITB12": {
                            "label": "Vitamin B12",
                            "quantity": 0.7937281249995211,
                            "unit": "µg"
                          },
                          "VITD": {
                            "label": "Vitamin D",
                            "quantity": 1.7276562499998704,
                            "unit": "µg"
                          },
                          "TOCPHA": {
                            "label": "Vitamin E",
                            "quantity": 0.9758099374999434,
                            "unit": "mg"
                          },
                          "VITK1": {
                            "label": "Vitamin K",
                            "quantity": 0.5508870833332155,
                            "unit": "µg"
                          },
                          "WATER": {
                            "label": "Water",
                            "quantity": 161.77712708328554,
                            "unit": "g"
                          }
                        },
                        "totalDaily": {
                          "ENERC_KCAL": {
                            "label": "Energy",
                            "quantity": 21.182974374997322,
                            "unit": "%"
                          },
                          "FAT": {
                            "label": "Fat",
                            "quantity": 18.349193269225594,
                            "unit": "%"
                          },
                          "FASAT": {
                            "label": "Saturated",
                            "quantity": 25.061833531237543,
                            "unit": "%"
                          },
                          "CHOCDF": {
                            "label": "Carbs",
                            "quantity": 19.506948381943264,
                            "unit": "%"
                          },
                          "FIBTG": {
                            "label": "Fiber",
                            "quantity": 9.1535,
                            "unit": "%"
                          },
                          "PROCNT": {
                            "label": "Protein",
                            "quantity": 36.301503249994575,
                            "unit": "%"
                          },
                          "CHOLE": {
                            "label": "Cholesterol",
                            "quantity": 107.39289374999511,
                            "unit": "%"
                          },
                          "NA": {
                            "label": "Sodium",
                            "quantity": 14.961574400157005,
                            "unit": "%"
                          },
                          "CA": {
                            "label": "Calcium",
                            "quantity": 8.827893124996116,
                            "unit": "%"
                          },
                          "MG": {
                            "label": "Magnesium",
                            "quantity": 11.942510416664788,
                            "unit": "%"
                          },
                          "K": {
                            "label": "Potassium",
                            "quantity": 7.8600640514165665,
                            "unit": "%"
                          },
                          "FE": {
                            "label": "Iron",
                            "quantity": 18.704131250009304,
                            "unit": "%"
                          },
                          "ZN": {
                            "label": "Zinc",
                            "quantity": 20.264629924240754,
                            "unit": "%"
                          },
                          "P": {
                            "label": "Phosphorus",
                            "quantity": 41.810858035699454,
                            "unit": "%"
                          },
                          "VITA_RAE": {
                            "label": "Vitamin A",
                            "quantity": 29.71381226851471,
                            "unit": "%"
                          },
                          "VITC": {
                            "label": "Vitamin C",
                            "quantity": 7.004645370369809,
                            "unit": "%"
                          },
                          "THIA": {
                            "label": "Thiamin (B1)",
                            "quantity": 13.674301215276707,
                            "unit": "%"
                          },
                          "RIBF": {
                            "label": "Riboflavin (B2)",
                            "quantity": 39.77915945511869,
                            "unit": "%"
                          },
                          "NIA": {
                            "label": "Niacin (B3)",
                            "quantity": 14.848664973964693,
                            "unit": "%"
                          },
                          "VITB6A": {
                            "label": "Vitamin B6",
                            "quantity": 26.894211378205252,
                            "unit": "%"
                          },
                          "FOLDFE": {
                            "label": "Folate equivalent (total)",
                            "quantity": 15.81351302083195,
                            "unit": "%"
                          },
                          "VITB12": {
                            "label": "Vitamin B12",
                            "quantity": 33.07200520831338,
                            "unit": "%"
                          },
                          "VITD": {
                            "label": "Vitamin D",
                            "quantity": 11.51770833333247,
                            "unit": "%"
                          },
                          "TOCPHA": {
                            "label": "Vitamin E",
                            "quantity": 6.505399583332956,
                            "unit": "%"
                          },
                          "VITK1": {
                            "label": "Vitamin K",
                            "quantity": 0.4590725694443462,
                            "unit": "%"
                          }
                        },
                        "digest": [
                          {
                            "label": "Fat",
                            "tag": "FAT",
                            "schemaOrgTag": "fatContent",
                            "total": 11.926975624996638,
                            "hasRDI": true,
                            "daily": 18.349193269225594,
                            "unit": "g",
                            "sub": [
                              {
                                "label": "Saturated",
                                "tag": "FASAT",
                                "schemaOrgTag": "saturatedFatContent",
                                "total": 5.012366706247509,
                                "hasRDI": true,
                                "daily": 25.061833531237543,
                                "unit": "g"
                              },
                              {
                                "label": "Trans",
                                "tag": "FATRN",
                                "schemaOrgTag": "transFatContent",
                                "total": 0.033729999999999996,
                                "hasRDI": false,
                                "daily": 0,
                                "unit": "g"
                              },
                              {
                                "label": "Monounsaturated",
                                "tag": "FAMS",
                                "schemaOrgTag": null,
                                "total": 3.8047863499992545,
                                "hasRDI": false,
                                "daily": 0,
                                "unit": "g"
                              },
                              {
                                "label": "Polyunsaturated",
                                "tag": "FAPU",
                                "schemaOrgTag": null,
                                "total": 2.018452931250031,
                                "hasRDI": false,
                                "daily": 0,
                                "unit": "g"
                              }
                            ]
                          },
                          {
                            "label": "Carbs",
                            "tag": "CHOCDF",
                            "schemaOrgTag": "carbohydrateContent",
                            "total": 58.52084514582979,
                            "hasRDI": true,
                            "daily": 19.506948381943264,
                            "unit": "g",
                            "sub": [
                              {
                                "label": "Carbs (net)",
                                "tag": "CHOCDF.net",
                                "schemaOrgTag": null,
                                "total": 56.23247014582979,
                                "hasRDI": false,
                                "daily": 0,
                                "unit": "g"
                              },
                              {
                                "label": "Fiber",
                                "tag": "FIBTG",
                                "schemaOrgTag": "fiberContent",
                                "total": 2.288375,
                                "hasRDI": true,
                                "daily": 9.1535,
                                "unit": "g"
                              },
                              {
                                "label": "Sugars",
                                "tag": "SUGAR",
                                "schemaOrgTag": "sugarContent",
                                "total": 1.4062429583284155,
                                "hasRDI": false,
                                "daily": 0,
                                "unit": "g"
                              },
                              {
                                "label": "Sugars, added",
                                "tag": "SUGAR.added",
                                "schemaOrgTag": null,
                                "total": 0,
                                "hasRDI": false,
                                "daily": 0,
                                "unit": "g"
                              }
                            ]
                          },
                          {
                            "label": "Protein",
                            "tag": "PROCNT",
                            "schemaOrgTag": "proteinContent",
                            "total": 18.150751624997287,
                            "hasRDI": true,
                            "daily": 36.301503249994575,
                            "unit": "g"
                          },
                          {
                            "label": "Cholesterol",
                            "tag": "CHOLE",
                            "schemaOrgTag": "cholesterolContent",
                            "total": 322.17868124998535,
                            "hasRDI": true,
                            "daily": 107.39289374999511,
                            "unit": "mg"
                          },
                          {
                            "label": "Sodium",
                            "tag": "NA",
                            "schemaOrgTag": "sodiumContent",
                            "total": 359.0777856037681,
                            "hasRDI": true,
                            "daily": 14.961574400157005,
                            "unit": "mg"
                          },
                          {
                            "label": "Calcium",
                            "tag": "CA",
                            "schemaOrgTag": null,
                            "total": 88.27893124996118,
                            "hasRDI": true,
                            "daily": 8.827893124996116,
                            "unit": "mg"
                          },
                          {
                            "label": "Magnesium",
                            "tag": "MG",
                            "schemaOrgTag": null,
                            "total": 50.15854374999211,
                            "hasRDI": true,
                            "daily": 11.942510416664788,
                            "unit": "mg"
                          },
                          {
                            "label": "Potassium",
                            "tag": "K",
                            "schemaOrgTag": null,
                            "total": 369.4230104165786,
                            "hasRDI": true,
                            "daily": 7.8600640514165665,
                            "unit": "mg"
                          },
                          {
                            "label": "Iron",
                            "tag": "FE",
                            "schemaOrgTag": null,
                            "total": 3.366743625001675,
                            "hasRDI": true,
                            "daily": 18.704131250009304,
                            "unit": "mg"
                          },
                          {
                            "label": "Zinc",
                            "tag": "ZN",
                            "schemaOrgTag": null,
                            "total": 2.229109291666483,
                            "hasRDI": true,
                            "daily": 20.264629924240754,
                            "unit": "mg"
                          },
                          {
                            "label": "Phosphorus",
                            "tag": "P",
                            "schemaOrgTag": null,
                            "total": 292.67600624989615,
                            "hasRDI": true,
                            "daily": 41.810858035699454,
                            "unit": "mg"
                          },
                          {
                            "label": "Vitamin A",
                            "tag": "VITA_RAE",
                            "schemaOrgTag": null,
                            "total": 267.4243104166324,
                            "hasRDI": true,
                            "daily": 29.71381226851471,
                            "unit": "µg"
                          },
                          {
                            "label": "Vitamin C",
                            "tag": "VITC",
                            "schemaOrgTag": null,
                            "total": 6.304180833332827,
                            "hasRDI": true,
                            "daily": 7.004645370369809,
                            "unit": "mg"
                          },
                          {
                            "label": "Thiamin (B1)",
                            "tag": "THIA",
                            "schemaOrgTag": null,
                            "total": 0.16409161458332047,
                            "hasRDI": true,
                            "daily": 13.674301215276707,
                            "unit": "mg"
                          },
                          {
                            "label": "Riboflavin (B2)",
                            "tag": "RIBF",
                            "schemaOrgTag": null,
                            "total": 0.517129072916543,
                            "hasRDI": true,
                            "daily": 39.77915945511869,
                            "unit": "mg"
                          },
                          {
                            "label": "Niacin (B3)",
                            "tag": "NIA",
                            "schemaOrgTag": null,
                            "total": 2.375786395834351,
                            "hasRDI": true,
                            "daily": 14.848664973964693,
                            "unit": "mg"
                          },
                          {
                            "label": "Vitamin B6",
                            "tag": "VITB6A",
                            "schemaOrgTag": null,
                            "total": 0.34962474791666825,
                            "hasRDI": true,
                            "daily": 26.894211378205252,
                            "unit": "mg"
                          },
                          {
                            "label": "Folate equivalent (total)",
                            "tag": "FOLDFE",
                            "schemaOrgTag": null,
                            "total": 63.2540520833278,
                            "hasRDI": true,
                            "daily": 15.81351302083195,
                            "unit": "µg"
                          },
                          {
                            "label": "Folate (food)",
                            "tag": "FOLFD",
                            "schemaOrgTag": null,
                            "total": 63.2540520833278,
                            "hasRDI": false,
                            "daily": 0,
                            "unit": "µg"
                          },
                          {
                            "label": "Folic acid",
                            "tag": "FOLAC",
                            "schemaOrgTag": null,
                            "total": 0,
                            "hasRDI": false,
                            "daily": 0,
                            "unit": "µg"
                          },
                          {
                            "label": "Vitamin B12",
                            "tag": "VITB12",
                            "schemaOrgTag": null,
                            "total": 0.7937281249995211,
                            "hasRDI": true,
                            "daily": 33.07200520831338,
                            "unit": "µg"
                          },
                          {
                            "label": "Vitamin D",
                            "tag": "VITD",
                            "schemaOrgTag": null,
                            "total": 1.7276562499998704,
                            "hasRDI": true,
                            "daily": 11.51770833333247,
                            "unit": "µg"
                          },
                          {
                            "label": "Vitamin E",
                            "tag": "TOCPHA",
                            "schemaOrgTag": null,
                            "total": 0.9758099374999434,
                            "hasRDI": true,
                            "daily": 6.505399583332956,
                            "unit": "mg"
                          },
                          {
                            "label": "Vitamin K",
                            "tag": "VITK1",
                            "schemaOrgTag": null,
                            "total": 0.5508870833332155,
                            "hasRDI": true,
                            "daily": 0.4590725694443462,
                            "unit": "µg"
                          },
                          {
                            "label": "Sugar alcohols",
                            "tag": "Sugar.alcohol",
                            "schemaOrgTag": null,
                            "total": 0,
                            "hasRDI": false,
                            "daily": 0,
                            "unit": "g"
                          },
                          {
                            "label": "Water",
                            "tag": "WATER",
                            "schemaOrgTag": null,
                            "total": 161.77712708328554,
                            "hasRDI": false,
                            "daily": 0,
                            "unit": "g"
                          }
                        ]
                      },
                      "_links": {
                        "self": {
                          "title": "Self",
                          "href": "https://api.edamam.com/api/recipes/v2/0562e1196ded5fa589928b95db27e2c3?type=public&app_id=e81c75e1&app_key=4d5f15ebb11bace49edf6660840b5499"
                        }
                      }
                    }
                  ]
                }
                """;
        Mockito.when(httpResponseMock.body()).thenReturn(json);

        Collection<String> healthLabels = List.of("Sugar-Conscious", "Low Potassium",
                "Kidney-Friendly", "Gluten-Free", "Wheat-Free", "Peanut-Free",
                "Tree-Nut-Free", "Soy-Free", "Fish-Free", "Shellfish-Free",
                "Pork-Free", "Red-Meat-Free", "Crustacean-Free", "Celery-Free",
                "Mustard-Free", "Sesame-Free", "Lupine-Free", "Mollusk-Free",
                "Alcohol-Free", "Sulfite-Free"
        );
        Recipe recipe = new Recipe("Breakfast Kedgeree", List.of("Balanced", "Low-Sodium"),
                healthLabels, 252.6737500004232, List.of("breakfast"), List.of("american"),
                List.of("starter"), List.of("* 60g white rice",
                "* chicken stock",
                "* 2 egg",
                "* 1/2 tsp coconut oil",
                "* 1 garlic clove, minced",
                "* 50g frozen mixed vegetable (carrots, pea, broccoli, cauliflower)",
                "* 1/4 tsp turmeric",
                "* 1/4 tsp cumin",
                "* pinch salt",
                "* leftover yoghurt, about 1/2 tb"));
        Collection<Recipe> expected = List.of(recipe);
        Collection<Recipe> actual = client.getRecipesByKeywordsAndHealthLabelsAndMealTypes(List.of("rice", "chicken", "broccoli"), List.of(HealthLabels.LOW_POTASSIUM), List.of(MealType.BREAKFAST));
        Assertions.assertEquals(actual.iterator().next().getCuisineType(), expected.iterator().next().getCuisineType());
        Assertions.assertEquals(actual.iterator().next().getTotalWeight(), expected.iterator().next().getTotalWeight());
        Assertions.assertEquals(actual.iterator().next().getMealType(), expected.iterator().next().getMealType());
        Assertions.assertEquals(actual.iterator().next().getLabel(), expected.iterator().next().getLabel());
        Assertions.assertEquals(actual.iterator().next().getDishType(), expected.iterator().next().getDishType());
        Assertions.assertEquals(actual.iterator().next().getIngredientLines(), expected.iterator().next().getIngredientLines());
        Assertions.assertEquals(actual.iterator().next().getHealthLabels(), expected.iterator().next().getHealthLabels());
        Assertions.assertEquals(actual.iterator().next().getDietLabels(), expected.iterator().next().getDietLabels());
    }

    @Test
    void testHTTPStatusCodeExceptions() throws Exception {
        client = new CookingClient(httpClientMock);
        Mockito.when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        Mockito.when(httpResponseMock.statusCode()).thenReturn(404);
        Assertions.assertThrows(NotFoundException.class, () -> client.getRecipesByKeywords(List.of("bread")));
        Mockito.when(httpResponseMock.statusCode()).thenReturn(400);
        Assertions.assertThrows(BadRequestException.class, () -> client.getRecipesByMealType(List.of(MealType.LUNCH)));
        Mockito.when(httpResponseMock.statusCode()).thenReturn(401);
        Assertions.assertThrows(NotAuthorizedException.class, () -> client.getRecipesByHealthLabels(List.of(HealthLabels.LUPINE_FREE)));
        Mockito.when(httpResponseMock.statusCode()).thenReturn(408);
        Assertions.assertThrows(RequestTimeoutException.class, () -> client.getRecipesByKeywords(List.of("bread")));

    }
}