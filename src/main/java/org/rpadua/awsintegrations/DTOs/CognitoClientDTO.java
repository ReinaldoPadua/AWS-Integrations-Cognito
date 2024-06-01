package org.rpadua.awsintegrations.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CognitoClientDTO {

    private String userPoolId;
    private String clientId;
    private String secretClient;

}
