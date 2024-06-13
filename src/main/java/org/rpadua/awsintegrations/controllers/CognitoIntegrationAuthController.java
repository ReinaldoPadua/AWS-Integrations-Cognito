package org.rpadua.awsintegrations.controllers;

import org.json.JSONObject;
import org.rpadua.awsintegrations.DTOs.SignInRequestDTO;
import org.rpadua.awsintegrations.DTOs.SignInResponseDTO;
import org.rpadua.awsintegrations.DTOs.SignUpRequestDTO;
import org.rpadua.awsintegrations.providers.CognitoProviderAuth;
import org.rpadua.awsintegrations.services.CognitoServiceAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cognito-integration/auth")
public class CognitoIntegrationAuthController {

    Logger logger = LoggerFactory.getLogger(CognitoIntegrationAuthController.class);

    private final CognitoProviderAuth cognitoProviderAuth;
    private final CognitoServiceAuth cognitoServiceAuth;

    public CognitoIntegrationAuthController(final CognitoProviderAuth cognitoProviderAuth,
                                            final CognitoServiceAuth cognitoServiceAuth){
        this.cognitoProviderAuth = cognitoProviderAuth;
        this.cognitoServiceAuth = cognitoServiceAuth;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestHeader String userPool, @RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            cognitoProviderAuth.signUp(userPool, signUpRequestDTO);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/confirm-sign-up")
    public ResponseEntity<?> confirmSignUp(@RequestHeader String userPool,
                                           @RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            cognitoProviderAuth.confirmSignUp(userPool,signUpRequestDTO);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestHeader String userPool,@RequestBody SignInRequestDTO signInRequestDTO) {
        try {
            SignInResponseDTO signInResponse = this.cognitoServiceAuth.signIn(userPool, signInRequestDTO);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(signInResponse);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/associate-software-token",produces="application/json")
    public ResponseEntity<?>  associateSoftwareToken(@RequestHeader String authorization) {
        try {
            String secretCode = this.cognitoProviderAuth.associateSoftwareToken(authorization);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(
                    new JSONObject().put("secretCode",secretCode).toString());
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/verify-software-token",produces="application/json")
    public ResponseEntity<?> verifySoftwareToken(@RequestHeader String authorization,
                                                 @RequestBody SignInRequestDTO signInRequestDTO) {
        try {

            String verifyStatus = this.cognitoProviderAuth.verifySoftwareToken(authorization,
                    signInRequestDTO.getTotpCode());

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(
                    new JSONObject().put("verifyStatus",verifyStatus).toString()
            );
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/respond-to-auth-challenge",produces="application/json")
    public ResponseEntity<?> respondToAuthChallenge(@RequestHeader String userPool,
                                                    @RequestBody SignInRequestDTO signInRequestDTO) {
        try {

            SignInResponseDTO signInResponse = this.cognitoServiceAuth.respondToAuthChallenge(
                    userPool, signInRequestDTO);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(signInResponse);

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/sign-out",produces="application/json")
    public ResponseEntity<?> signOut(@RequestHeader String authorization) {
        try {

            this.cognitoProviderAuth.signOut(authorization.replace("Bearer ",""));
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

}
