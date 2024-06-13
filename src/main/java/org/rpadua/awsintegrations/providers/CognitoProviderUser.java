package org.rpadua.awsintegrations.providers;

import org.rpadua.awsintegrations.DTOs.CognitoClientDTO;
import org.rpadua.awsintegrations.util.AwsUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Service
public class CognitoProviderUser extends CognitoProviderAbstract {

    public CognitoProviderUser(){
        super();
    }

    private void initCognitoClient(){
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .build();
    }

    public ListUsersResponse listAllUsers(String userPoll) throws Exception {

        try {
            this.initCognitoClient();

            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPoll,this.USER_POOL_IDS);

            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(cognitoClient.getUserPoolId()).build();

            return this.cognitoClient.listUsers(usersRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }

    public AdminGetUserResponse getUser(String userPool, String userName) throws Exception {
        try {
            this.initCognitoClient();
            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            AdminGetUserRequest userRequest = AdminGetUserRequest.builder()
                    .username(userName)
                    .userPoolId(cognitoClient.getUserPoolId())
                    .build();

            return this.cognitoClient.adminGetUser(userRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }
}
