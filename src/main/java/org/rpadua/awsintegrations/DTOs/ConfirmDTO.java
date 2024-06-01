package org.rpadua.awsintegrations.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmDTO {

    private String email;
    private String code;

}
