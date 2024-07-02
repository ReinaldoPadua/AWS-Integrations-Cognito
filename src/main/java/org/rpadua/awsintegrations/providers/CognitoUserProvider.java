package org.rpadua.awsintegrations.providers;

import org.rpadua.awsintegrations.DTOs.CognitoClientDTO;
import org.rpadua.awsintegrations.util.AwsUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class CognitoUserProvider extends CognitoAbstractProvider {

    public CognitoUserProvider(){
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

    public void updateUserAttributeName(String userPool, String userName,String newName) throws Exception {
        try {
            this.initCognitoClient();
            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            AttributeType userAttrs = AttributeType.builder()
                    .name("name")
                    .value(newName)
                    .build();

            List<AttributeType> userAttrsList = new ArrayList<>();
            userAttrsList.add(userAttrs);

            AdminUpdateUserAttributesRequest userRequest = AdminUpdateUserAttributesRequest.builder()
                    .userPoolId(cognitoClient.getUserPoolId())
                    .userAttributes(userAttrsList)
                    .username(userName)
                    .build();

            this.cognitoClient.adminUpdateUserAttributes(userRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }

    public void disableUser(String userPool, String userName) throws Exception {
        try {
            this.initCognitoClient();
            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            AdminDisableUserRequest userRequest = AdminDisableUserRequest.builder()
                    .userPoolId(cognitoClient.getUserPoolId())
                    .username(userName)
                    .build();

            this.cognitoClient.adminDisableUser(userRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }

    public void deleteUser(String userPool, String userName) throws Exception {
        try {
            this.initCognitoClient();
            CognitoClientDTO cognitoClient = AwsUtils.getCognitoClient(userPool,this.USER_POOL_IDS);

            AdminDeleteUserRequest userRequest = AdminDeleteUserRequest.builder()
                    .userPoolId(cognitoClient.getUserPoolId())
                    .username(userName)
                    .build();

            this.cognitoClient.adminDeleteUser(userRequest);

        } catch (CognitoIdentityProviderException e) {
            logger.debug(String.format("Error code: %s - Service: %s - Message: %s",e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().serviceName(),e.awsErrorDetails().errorMessage()));
            throw new Exception(e.awsErrorDetails().errorMessage());
        } finally {
            this.cognitoClient.close();
        }

    }
}
