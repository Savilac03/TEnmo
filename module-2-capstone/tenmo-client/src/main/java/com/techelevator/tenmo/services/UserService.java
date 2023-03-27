package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserService {
    public static final String API_BASE_URL = "http://localhost:8080/";
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    RestTemplate restTemplate = new RestTemplate();

    public User getUserById(AuthenticatedUser authenticatedUser, int id) {
        User user = new User();
        try {
            user = restTemplate.exchange(API_BASE_URL + "/users/" + id, HttpMethod.GET, makeAuthEntity(), User.class).getBody();

        } catch (RestClientResponseException e) {
            System.out.println("could not complete request" + e.getRawStatusCode());
        }
        return user;
    }
    public User[] getListOfUsers() {
        User[] users = null;
        try {
            users = restTemplate.exchange(API_BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch(ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }
        return users;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}
