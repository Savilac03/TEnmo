package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    public static final String API_BASE_URL = "http://localhost:8080/tenmo/";

    RestTemplate restTemplate = new RestTemplate();
    AccountService accountService = new AccountService();
    UserService userService = new UserService();
    User user = new AuthenticatedUser().getUser();
    private String authToken = null;

    public String getAuthToken() {
        return authToken;
    }
    public void createTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity(transfer, headers);

        String url = API_BASE_URL + "/transfers/" + transfer.getTransferId();
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Transfer.class);
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        }
    }
    public void updateTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity(transfer, headers);

        String url = API_BASE_URL + "/transfers/" + transfer.getTransferId();

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Transfer.class);
        } catch (RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        }
    }
    public Transfer[] getAllTransfers() {
        Transfer[] transfers = new Transfer[0];
        try {
            transfers = restTemplate.exchange(API_BASE_URL + "/transfers",
                    HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        }
        return transfers;
    }
    public Transfer[] getTransfersFromUserId(AuthenticatedUser authenticatedUser, int userId) {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(API_BASE_URL + "/transfers/user/" + userId,
                    HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        }
        return transfers;
    }
    public Transfer getTransferFromTransferId(int id) {
        Transfer transfer = null;
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "/transfers/" + id,
                    HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
        } catch(RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        }
        return transfer;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }


}
