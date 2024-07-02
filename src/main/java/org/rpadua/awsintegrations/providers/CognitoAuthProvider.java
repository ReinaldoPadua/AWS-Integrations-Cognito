package org.rpadua.awsintegrations.providers;

import org.rpadua.awsintegrations.util.AwsUtils;
import org.rpadua.awsintegrations.DTOs.SignUpRequestDTO;
import org.rpadua.awsintegrations.DTOs.CognitoClientDTO;
import org.rpadua.awsintegrations.DTOs.SignInRequestDTO;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CognitoAuthProvider extends CognitoAbstractProvider {

    public CognitoAuthProvider(){
        super();
    }

    private void initCognitoClient(){
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .build();
    }

    public void signUp(String userPoll, SignUpRequestDTO signUpRequestDTO) throws Exception {

        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPoll,this.USER_POOL_IDS);

            AttributeType userAttrs = AttributeType.builder()
                    .name("name")
                    .value(signUpRequestDTO.getName())
                    .build();

            List<AttributeType> userAttrsList = new ArrayList<>();
            userAttrsList.add(userAttrs);

            String secretHash = AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                    cognitoClient.getSecretClient(),
                    signUpRequestDTO.getEmail());

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .secretHash(secretHash)
                    .userAttributes(userAttrsList)
                    .username(signUpRequestDTO.getEmail())
                    .password(signUpRequestDTO.getPassword())
                    .build();

            this.cognitoClient.signUp(signUpRequest);

        } catch (CognitoIdentityProviderException e) {
           // logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
             //       e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.getMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public void confirmSignUp(String userPool,SignUpRequestDTO signUpRequestDTO) throws Exception {
        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            String secretHash = AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                    cognitoClient.getSecretClient(),
                    signUpRequestDTO.getEmail());

            ConfirmSignUpRequest signUpRequest = ConfirmSignUpRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .secretHash(secretHash)
                    .confirmationCode(signUpRequestDTO.getCode())
                    .username(signUpRequestDTO.getEmail())
                    .build();

            this.cognitoClient.confirmSignUp(signUpRequest);
            this.enableUserMFA(cognitoClient.getUserPoolId(),signUpRequestDTO.getEmail());
        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public AdminInitiateAuthResponse signIn(String userPool, SignInRequestDTO signInRequestDTO) throws Exception {

        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            Map<String, String> authParameters = new ConcurrentHashMap<>();
            authParameters.put("USERNAME", signInRequestDTO.getEmail());
            authParameters.put("PASSWORD", signInRequestDTO.getPassword());
            authParameters.put("SECRET_HASH", AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                    cognitoClient.getSecretClient(),
                    signInRequestDTO.getEmail()));


            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .userPoolId(cognitoClient.getUserPoolId())
                    .authParameters(authParameters)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .build();

            return this.cognitoClient.adminInitiateAuth(authRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.error(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }

    private void enableUserMFA(String userPool, String username){
        AdminSetUserMfaPreferenceRequest mfaRequest = AdminSetUserMfaPreferenceRequest.builder()
                .username(username)
                .userPoolId(userPool)
                .softwareTokenMfaSettings(SoftwareTokenMfaSettingsType.builder()
                        .enabled(true)
                        .preferredMfa(true).build()).build();

        cognitoClient.adminSetUserMFAPreference(mfaRequest);
    }

    public String associateSoftwareToken(String authorization) throws Exception {

        try {
            this.initCognitoClient();
            AssociateSoftwareTokenRequest associateRequest = AssociateSoftwareTokenRequest.builder()
                    .accessToken(authorization.replace("Bearer ","")).build();

            AssociateSoftwareTokenResponse associateResult = this.cognitoClient.associateSoftwareToken(associateRequest);

            return associateResult.secretCode();
        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public String verifySoftwareToken(String authorization, String totpCode) throws Exception {
        try {
            this.initCognitoClient();

            VerifySoftwareTokenRequest verifyRequest = VerifySoftwareTokenRequest.builder()
                    .accessToken(authorization.replace("Bearer ",""))
                    .userCode(totpCode).build();

            VerifySoftwareTokenResponse verifyResult = this.cognitoClient.verifySoftwareToken(verifyRequest);


            return verifyResult.status().toString();
        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public AdminRespondToAuthChallengeResponse respondToAuthChallenge(String userPool, SignInRequestDTO signInRequestDTO) throws Exception {
        try {
            this.initCognitoClient();
            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            Map<String, String> challengeResponses = new HashMap<>();

            challengeResponses.put("USERNAME", signInRequestDTO.getEmail());
            challengeResponses.put("SOFTWARE_TOKEN_MFA_CODE", signInRequestDTO.getTotpCode());
            challengeResponses.put("SECRET_HASH", AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                    cognitoClient.getSecretClient(),
                    signInRequestDTO.getEmail()));

            AdminRespondToAuthChallengeRequest respondToAuthChallengeRequest = AdminRespondToAuthChallengeRequest.builder()
                    .challengeName(ChallengeNameType.SOFTWARE_TOKEN_MFA)
                    .clientId(cognitoClient.getClientId())
                    .userPoolId(cognitoClient.getUserPoolId())
                    .challengeResponses(challengeResponses)
                    .session(signInRequestDTO.getSession())
                    .build();

            return this.cognitoClient
                    .adminRespondToAuthChallenge(respondToAuthChallengeRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public void signOut(String token) throws Exception {
        try {
            this.initCognitoClient();

            GlobalSignOutRequest  globalSignOutRequest =
                    GlobalSignOutRequest.builder()
                            .accessToken(token)
                            .build();

            this.cognitoClient.globalSignOut(globalSignOutRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public void resendConfirmationCode(String userPool, String username) throws Exception {

        try {
            this.initCognitoClient();
            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool, this.USER_POOL_IDS);
            ResendConfirmationCodeRequest codeRequest = ResendConfirmationCodeRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .username(username)
                    .build();

            this.cognitoClient.resendConfirmationCode(codeRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s", e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(), e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }
}
