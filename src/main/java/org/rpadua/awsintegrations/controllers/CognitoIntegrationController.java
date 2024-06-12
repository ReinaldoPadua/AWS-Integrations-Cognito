package org.rpadua.awsintegrations.controllers;


import org.json.JSONObject;
import org.rpadua.awsintegrations.DTOs.*;
import org.rpadua.awsintegrations.providers.CognitoProvider;
import org.rpadua.awsintegrations.services.CognitoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cognito-integration")
public class CognitoIntegrationController {

    Logger logger = LoggerFactory.getLogger(CognitoIntegrationController.class);

    private final CognitoProvider cognitoProvider;
    private final CognitoService cognitoService;

    public CognitoIntegrationController(final CognitoProvider cognitoProvider,final CognitoService cognitoService){
        this.cognitoProvider = cognitoProvider;
        this.cognitoService = cognitoService;
    }

    @GetMapping("/")
    public ResponseEntity<?> listAllUsers(@RequestHeader String userPool)  {
        try {
            List<UserCognitoDTO> listUsers = cognitoProvider.listAllUsers(userPool);
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(listUsers);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity<?> signUp(@RequestHeader String userPool,@RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            cognitoProvider.signUp(userPool, signUpRequestDTO);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/auth/confirm-sign-up")
    public ResponseEntity<?> confirmSignUp(@RequestHeader String userPool,
                                           @RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            cognitoProvider.confirmSignUp(userPool,signUpRequestDTO);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<?> signIn(@RequestHeader String userPool,@RequestBody SignInRequestDTO signInRequestDTO) {
        try {
            SignInResponseDTO signInResponse = this.cognitoService.signIn(userPool, signInRequestDTO);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(signInResponse);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/auth/associate-software-token",produces="application/json")
    public ResponseEntity<?>  associateSoftwareToken(@RequestHeader String authorization) {
        try {
            String secretCode = this.cognitoProvider.associateSoftwareToken(authorization);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(
                    new JSONObject().put("secretCode",secretCode).toString());
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/auth/verify-software-token",produces="application/json")
    public ResponseEntity<?> verifySoftwareToken(@RequestHeader String authorization,
                                                 @RequestBody SignInRequestDTO signInRequestDTO) {
        try {

            String verifyStatus = this.cognitoProvider.verifySoftwareToken(authorization,
                    signInRequestDTO.getTotpCode());

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(
                    new JSONObject().put("verifyStatus",verifyStatus).toString()
            );
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/auth/respond-to-auth-challenge",produces="application/json")
    public ResponseEntity<?> respondToAuthChallenge(@RequestHeader String userPool,
                                                    @RequestBody SignInRequestDTO signInRequestDTO) {
        try {

            SignInResponseDTO signInResponse = this.cognitoService.respondToAuthChallenge(
                    userPool, signInRequestDTO);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(signInResponse);

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/auth/sign-out",produces="application/json")
    public ResponseEntity<?> signOut(@RequestHeader String authorization) {
        try {

            this.cognitoProvider.signOut(authorization.replace("Bearer ",""));

            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

}

