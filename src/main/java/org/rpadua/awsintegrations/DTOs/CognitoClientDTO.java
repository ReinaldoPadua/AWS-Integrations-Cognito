package org.rpadua.awsintegrations.DTOs;

public class CognitoClientDTO {
    private String userPoolId;
    private String clientId;
    private String secretClient;

    public CognitoClientDTO(String userPoolId, String clientId, String secretClient) {
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.secretClient = secretClient;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecretClient() {
        return secretClient;
    }

    public void setSecretClient(String secretClient) {
        this.secretClient = secretClient;
    }
}
