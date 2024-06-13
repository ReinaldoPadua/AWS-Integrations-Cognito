package org.rpadua.awsintegrations.controllers;


import org.rpadua.awsintegrations.DTOs.*;
import org.rpadua.awsintegrations.services.CognitoServiceUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cognito-integration/user")
public class CognitoIntegrationUserController {

    Logger logger = LoggerFactory.getLogger(CognitoIntegrationUserController.class);

    private final CognitoServiceUser cognitoServiceUser;


    public CognitoIntegrationUserController(final CognitoServiceUser cognitoServiceUser){
        this.cognitoServiceUser = cognitoServiceUser;
    }

    @GetMapping("/")
    public ResponseEntity<?> listAllUsers(@RequestHeader String userPool)  {
        try {
            List<UserCognitoDTO> listUsers = cognitoServiceUser.listAllUsers(userPool);
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(listUsers);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @GetMapping("/{userName}")
    public ResponseEntity<?> getUser(@RequestHeader String userPool, @PathVariable String userName)  {
        try {
            UserCognitoDTO user = cognitoServiceUser.getUser(userPool,userName);
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(user);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

}

