package org.rpadua.awsintegrations.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCognitoDTO {

    private String username;
    private String status;
    private String email;
    private String name;

}
