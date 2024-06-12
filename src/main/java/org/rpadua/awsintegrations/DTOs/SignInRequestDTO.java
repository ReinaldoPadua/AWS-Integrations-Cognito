package org.rpadua.awsintegrations.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDTO {

    private String email;
    private String password;
    private String totpCode;
    private String session;

}
