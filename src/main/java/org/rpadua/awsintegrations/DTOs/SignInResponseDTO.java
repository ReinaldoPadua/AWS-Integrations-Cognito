package org.rpadua.awsintegrations.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInResponseDTO {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private String idToken;
    private Integer expiresIn;
    private String session;
}
