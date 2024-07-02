package org.rpadua.awsintegrations.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDTO {

    private String email;
    private String name;
    private String password;
    private String code;

}
