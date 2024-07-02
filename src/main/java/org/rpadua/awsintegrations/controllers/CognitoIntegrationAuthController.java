package org.rpadua.awsintegrations.controllers;

import org.json.JSONObject;
import org.rpadua.awsintegrations.DTOs.SignInRequestDTO;
import org.rpadua.awsintegrations.providers.CognitoAuthProvider;
import org.rpadua.awsintegrations.services.CognitoAuthService;
import org.rpadua.awsintegrations.DTOs.SignInResponseDTO;
import org.rpadua.awsintegrations.DTOs.SignUpRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cognito-integration/auth")
public class CognitoIntegrationAuthController {

    Logger logger = LoggerFactory.getLogger(CognitoIntegrationAuthController.class);

    private final CognitoAuthProvider cognitoAuthProvider;
    private final CognitoAuthService cognitoAuthService;

    public CognitoIntegrationAuthController(final CognitoAuthProvider cognitoAuthProvider,
                                            final CognitoAuthService cognitoAuthService){
        this.cognitoAuthProvider = cognitoAuthProvider;
        this.cognitoAuthService = cognitoAuthService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestHeader String userPool, @RequestBody SignUpRequestDTO signUpRequestDTO) {
        try {
            cognitoAuthProvider.signUp(userPool, signUpRequestDTO);
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
            cognitoAuthProvider.confirmSignUp(userPool,signUpRequestDTO);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestHeader String userPool,@RequestBody SignInRequestDTO signInRequestDTO) {
        try {
            SignInResponseDTO signInResponse = this.cognitoAuthService.signIn(userPool, signInRequestDTO);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(signInResponse);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @PostMapping(value="/associate-software-token",produces="application/json")
    public ResponseEntity<?>  associateSoftwareToken(@RequestHeader String authorization) {
        try {
            String secretCode = this.cognitoAuthProvider.associateSoftwareToken(authorization);

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

            String verifyStatus = this.cognitoAuthProvider.verifySoftwareToken(authorization,
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

            SignInResponseDTO signInResponse = this.cognitoAuthService.respondToAuthChallenge(
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

            this.cognitoAuthProvider.signOut(authorization.replace("Bearer ",""));
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }
    @PostMapping(value="/{userName}/resend-confirmation-code",produces="application/json")
    public ResponseEntity<?> resendConfirmationCode(@RequestHeader String userPool,
                                                    @PathVariable String userName) {
        try {

            this.cognitoAuthService.resendConfirmationCode(userPool,userName);
            return ResponseEntity.status(HttpStatus.valueOf(201)).body("");

        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

}
