package org.rpadua.awsintegrations.controllers;


import org.rpadua.awsintegrations.services.CognitoUserService;
import org.rpadua.awsintegrations.DTOs.UserCognitoDTO;
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

    private final CognitoUserService cognitoUserService;


    public CognitoIntegrationUserController(final CognitoUserService cognitoUserService){
        this.cognitoUserService = cognitoUserService;
    }

    @GetMapping("/")
    public ResponseEntity<?> listAllUsers(@RequestHeader String userPool)  {
        try {
            List<UserCognitoDTO> listUsers = cognitoUserService.listAllUsers(userPool);
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(listUsers);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @GetMapping("/{userName}")
    public ResponseEntity<?> getUser(@RequestHeader String userPool, @PathVariable String userName)  {
        try {
            UserCognitoDTO user = cognitoUserService.getUser(userPool,userName);
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(user);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PatchMapping("/")
    public ResponseEntity<?> updateUser(@RequestHeader String userPool, @RequestBody UserCognitoDTO userCognito)  {
        try {
            cognitoUserService.updateUser(userPool,userCognito);
            return ResponseEntity.status(HttpStatus.valueOf(204)).body("");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @DeleteMapping("/{userName}")
    public ResponseEntity<?> deleteUser(@RequestHeader String userPool, @PathVariable String userName)  {
        try {
            cognitoUserService.deleteUser(userPool,userName);
            return ResponseEntity.status(HttpStatus.valueOf(204)).body("");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

}

