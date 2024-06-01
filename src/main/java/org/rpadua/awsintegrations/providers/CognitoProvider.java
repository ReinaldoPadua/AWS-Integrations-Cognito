package org.rpadua.awsintegrations.providers;

import org.rpadua.awsintegrations.DTOs.*;
import org.rpadua.awsintegrations.util.AwsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CognitoProvider {

    @Value("${aws.credentials.access-key-id}")
    private String AWS_ACCESS_KEY;

    @Value("${aws.credentials.secret-access-key-id}")
    private String AWS_SECRET_ACCESS_KEY;

    @Value("${aws.cognito.user-pool-ids}")
    private String[] USER_POOL_IDS;

    private CognitoIdentityProviderClient cognitoClient;


    public CognitoProvider(){
        super();
    }

    private void initCognitoClient(){
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY))
                .build();
    }

    public List<UserCognitoDTO> listAllUsers(String userPoll) throws Exception {

        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPoll,this.USER_POOL_IDS);

            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(cognitoClient.getUserPoolId()).build();

            ListUsersResponse response = this.cognitoClient.listUsers(usersRequest);

            return response.users().stream().map(userType -> new UserCognitoDTO(
                    userType.username(),userType.userStatusAsString(),
                    userType.attributes().stream().filter(
                            u-> u.name().equals("email")
                    ).map(u -> u.value()).findFirst().get(),
                    userType.attributes().stream().filter(
                            u-> u.name().equals("name")
                    ).map(u -> u.value()).findFirst().get()
            )).collect(Collectors.toList());

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }

    public void signUp(String userPoll,RegisterDTO registerDTO) throws Exception {

        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPoll,this.USER_POOL_IDS);

            AttributeType userAttrs = AttributeType.builder()
                    .name("name")
                    .value(registerDTO.getName())
                    .build();

            List<AttributeType> userAttrsList = new ArrayList<>();
            userAttrsList.add(userAttrs);

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .secretHash(AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                            cognitoClient.getSecretClient(),
                            registerDTO.getEmail()))
                    .userAttributes(userAttrsList)
                    .username(registerDTO.getEmail())
                    .password(registerDTO.getPassword())
                    .build();

            this.cognitoClient.signUp(signUpRequest);

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public void confirmSignUp(String userPool,ConfirmDTO confirmDTO) throws Exception {
        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            ConfirmSignUpRequest signUpRequest = ConfirmSignUpRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .secretHash(AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                            cognitoClient.getSecretClient(),
                            confirmDTO.getEmail()))
                    .confirmationCode(confirmDTO.getCode())
                    .username(confirmDTO.getEmail())
                    .build();

            this.cognitoClient.confirmSignUp(signUpRequest);

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }
    }

    public AdminInitiateAuthResponse signIn(String userPool, SignInDTO signInDTO) throws Exception {

        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            Map<String, String> authParameters = new ConcurrentHashMap<>();
            authParameters.put("USERNAME", signInDTO.getEmail());
            authParameters.put("PASSWORD", signInDTO.getPassword());
            authParameters.put("SECRET_HASH", AwsUtils.calculateSecretHash(cognitoClient.getClientId(),
                    cognitoClient.getSecretClient(),
                    signInDTO.getEmail()));


            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .clientId(cognitoClient.getClientId())
                    .userPoolId(cognitoClient.getUserPoolId())
                    .authParameters(authParameters)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .build();

            AdminInitiateAuthResponse response = this.cognitoClient.adminInitiateAuth(authRequest);
            return response;

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }
}
