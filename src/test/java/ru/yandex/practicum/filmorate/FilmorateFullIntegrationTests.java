package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FilmorateFullIntegrationTests {

    private final TestRestTemplate restTemplate;

    @Value("${filmorate-server.url}")
    String serverUrl;

    private static HttpHeaders headers;

    @BeforeAll
    public static void beforeAll() {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
    }

    @Test
    public void createUser() {
        NewUserRequest newUserRequest = getNewUserRequest();

        HttpEntity<NewUserRequest> httpEntity = new HttpEntity<>(newUserRequest, headers);

        ResponseEntity<UserDto> responseEntity = restTemplate.exchange(serverUrl + "/users", HttpMethod.POST, httpEntity, UserDto.class);

        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode().value(), 200);
        assertNotNull(responseEntity.getBody());
        UserDto user = responseEntity.getBody();
        assertNotNull(user.getId());

        assertThat(user).usingRecursiveComparison().ignoringFields("id").isEqualTo(newUserRequest);
    }

    @Test
    public void updateUser() {
        UserDto user = exchangeCreateUser();

        UpdateUserRequest updateUserRequest = getUpdateUserRequest();
        updateUserRequest.setId(user.getId());

        HttpEntity<UpdateUserRequest> updateHttpEntity = new HttpEntity<>(updateUserRequest, headers);
        ResponseEntity<UserDto> updateResponseEntity = restTemplate.exchange(serverUrl + "/users", HttpMethod.PUT, updateHttpEntity, UserDto.class);

        assertNotNull(updateResponseEntity);
        assertEquals(updateResponseEntity.getStatusCode().value(), 200);
        assertNotNull(updateResponseEntity.getBody());
        UserDto updatedUser = updateResponseEntity.getBody();

        assertThat(updatedUser).usingRecursiveComparison().isEqualTo(updateUserRequest);
    }

    @Test
    public void deleteUser() {
        UserDto userDto = exchangeCreateUser();
        long userId = userDto.getId();

        ResponseEntity<Map<String, String>> responseByDeletion = restTemplate.exchange(serverUrl + "/users/" + userId,
                 HttpMethod.DELETE, HttpEntity.EMPTY, new ParameterizedTypeReference<>() {});

        assertThat(responseByDeletion).isNotNull();
        assertThat(responseByDeletion.getBody()).hasFieldOrProperty("result");

        ResponseEntity<UserDto> responseByGetting = restTemplate.exchange(serverUrl + "/users/" + userId,
                HttpMethod.GET, HttpEntity.EMPTY, UserDto.class);

        assertThat(responseByGetting).isNotNull();
        assertThat(responseByGetting.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    public void findUserById() {
        UserDto userDto = exchangeCreateUser();

        ResponseEntity<UserDto> response = restTemplate.exchange(serverUrl + "/users/" + userDto.getId(),
                HttpMethod.GET, HttpEntity.EMPTY, UserDto.class);

        assertNotNull(response);
        assertEquals(response.getStatusCode().value(), 200);
        assertNotNull(response.getBody());
        UserDto returnedDto = response.getBody();

        assertThat(returnedDto).usingRecursiveComparison().isEqualTo(userDto);
    }

    @Test
    public void addFriend() {
        UserDto firstUser = exchangeCreateUser();
        UserDto secondUser = exchangeCreateUser();

        ResponseEntity<Map<String, String>> responceByAddFriend = restTemplate.exchange(
                serverUrl + "/users/" + firstUser.getId() + "/friends/" + secondUser.getId(),
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});

        assertThat(responceByAddFriend).isNotNull();
        assertEquals(responceByAddFriend.getStatusCode().value(), 200);
        assertThat(responceByAddFriend.getBody()).isNotNull();
        assertThat(responceByAddFriend.getBody()).hasFieldOrProperty("result");
    }

    @Test
    public void getFriends() {
        UserDto firstUser = exchangeCreateUser();
        UserDto secondUser = exchangeCreateUser();
        UserDto thirdUser = exchangeCreateUser();
        exchangeAddFriend( firstUser.getId(), secondUser.getId());
        exchangeAddFriend(firstUser.getId(),  thirdUser.getId());

        ResponseEntity<Collection<UserDto>> responseGetFriends = restTemplate.exchange(
                serverUrl + "/users/" + firstUser.getId() + "/friends",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});

        assertThat(responseGetFriends).isNotNull();
        assertThat(responseGetFriends.getStatusCode().value()).isEqualTo(200);
        assertThat(responseGetFriends.getBody()).isNotNull();
        Collection<UserDto> friends = responseGetFriends.getBody();
        assertThat(friends.size()).isEqualTo(2);
        assertThat(friends).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(secondUser, thirdUser);
    }

    @Test
    public void deleteFriend() {
        UserDto firstUser = exchangeCreateUser();
        UserDto secondUser = exchangeCreateUser();
        exchangeAddFriend(firstUser.getId(), secondUser.getId());

        ResponseEntity<Map<String, String>> responseDeletion = restTemplate.exchange(
                serverUrl + "/users/" + firstUser.getId() + "/friends/" + secondUser.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});

        assertThat(responseDeletion).isNotNull();
        assertEquals(responseDeletion.getStatusCode().value(), 200);
        assertThat(responseDeletion.getBody()).isNotNull();
        assertThat(responseDeletion.getBody()).hasFieldOrProperty("result");

        ResponseEntity<Collection<UserDto>> responseGetFriends = restTemplate.exchange(
                serverUrl + "/users/" + firstUser.getId() + "/friends",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});

        assertThat(responseGetFriends).isNotNull();
        assertThat(responseGetFriends.getStatusCode().value()).isEqualTo(200);
        assertThat(responseGetFriends.getBody()).isNotNull();
        Collection<UserDto> friends = responseGetFriends.getBody();
        assertThat(friends).isEmpty();
    }

    @Test
    public void getCommonFriends() {
        UserDto firstUser = exchangeCreateUser();
        UserDto secondUser = exchangeCreateUser();
        UserDto commonFriend = exchangeCreateUser();
        UserDto notCommonFriend = exchangeCreateUser();

        exchangeAddFriend(firstUser.getId(), commonFriend.getId());
        exchangeAddFriend(secondUser.getId(), commonFriend.getId());
        exchangeAddFriend(firstUser.getId(), notCommonFriend.getId());

        ResponseEntity<Collection<UserDto>> responseEntity = restTemplate.exchange(
                serverUrl + "/users/" + firstUser.getId() + "/friends/common/" + secondUser.getId(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isNotNull();
        Collection<UserDto> commonFriends = responseEntity.getBody();
        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends).contains(commonFriend);
    }

    private UserDto exchangeCreateUser() {
        NewUserRequest newUserRequest = getNewUserRequest();
        HttpEntity<NewUserRequest> httpEntity = new HttpEntity<>(newUserRequest, headers);
        ResponseEntity<UserDto> responseEntity = restTemplate.exchange(serverUrl + "/users", HttpMethod.POST, httpEntity, UserDto.class);

        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode().value(), 200);
        assertNotNull(responseEntity.getBody());
        UserDto userDto = responseEntity.getBody();
        assertNotNull(userDto.getId());

        assertThat(userDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(newUserRequest);
        return userDto;
    }

    private void exchangeAddFriend(long userId, long friendId) {
        ResponseEntity<Map<String, String>> responseByAddFriend1 = restTemplate.exchange(
                serverUrl + "/users/" + userId + "/friends/" + friendId,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {});

        assertThat(responseByAddFriend1).isNotNull();
        assertEquals(responseByAddFriend1.getStatusCode().value(), 200);
        assertThat(responseByAddFriend1.getBody()).isNotNull();
        assertThat(responseByAddFriend1.getBody()).hasFieldOrProperty("result");
    }

    private NewUserRequest getNewUserRequest() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setEmail(RandomUtils.getRandomEmail());
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        return  newUserRequest;

    }

    private UpdateUserRequest getUpdateUserRequest() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("NewName");
        updateUserRequest.setLogin("NewLogin");
        updateUserRequest.setEmail(RandomUtils.getRandomEmail());
        updateUserRequest.setBirthday(LocalDate.of(2002, 2, 2));
        return  updateUserRequest;

    }
}
