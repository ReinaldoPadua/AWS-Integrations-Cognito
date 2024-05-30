package org.rpadua.awsintegrations.DTOs;

public class UserCognitoDTO {

    private String username;
    private String email;
    private String name;
    private String status;

    public UserCognitoDTO(String username,String status, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
