package org.rpadua.awsintegrations.providers;

import org.rpadua.awsintegrations.DTOs.ConfirmDTO;
import org.rpadua.awsintegrations.DTOs.RegisterDTO;
import org.rpadua.awsintegrations.DTOs.UserCognitoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class CognitoProvider {

    @Value("${aws.credentials.access-key-id}")
    private String AWS_ACCESS_KEY;

    @Value("${aws.credentials.secret-access-key-id}")
    private String AWS_SECRET_ACCESS_KEY;

    @Value("${aws.cognito.user-pool-id}")
    private String USER_POOL_ID;

    private CognitoIdentityProviderClient cognitoClient;


    public CognitoProvider(){
        super();
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY))
                .build();
    }

    public List<UserCognitoDTO> listAllUsers() throws Exception {

        try {

            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .build();

            List<UserCognitoDTO> listUsers = new ArrayList<>();

            ListUsersResponse response = this.cognitoClient.listUsers(usersRequest);

            response.users().forEach(user -> {

                UserCognitoDTO userCognitoDTO = new UserCognitoDTO();
                userCognitoDTO.setUsername(user.username());
                userCognitoDTO.setStatus(user.userStatusAsString());

                userCognitoDTO.setEmail(user.attributes().get(0).value());
                userCognitoDTO.setName(user.attributes().get(1).value());

                listUsers.add(userCognitoDTO);
            });
            this.cognitoClient.close();

            return listUsers;


        } catch (CognitoIdentityProviderException e) {
            throw new Exception();
        }

    }

    public void signUp(RegisterDTO registerDTO) throws Exception {
        AttributeType userAttrs = AttributeType.builder()
                .name("name")
                .value(registerDTO.getName())
                .build();

        List<AttributeType> userAttrsList = new ArrayList<>();
        userAttrsList.add(userAttrs);
        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .userAttributes(userAttrsList)
                    .username(registerDTO.getEmail())
                    .clientId(registerDTO.getClientId())
                    .password(registerDTO.getPassword())
                    .build();

            this.cognitoClient.signUp(signUpRequest);


        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e);
        }
    }

    public void confirmSignUp(ConfirmDTO confirmDTO) throws Exception {
        try {
            ConfirmSignUpRequest signUpRequest = ConfirmSignUpRequest.builder()
                    .clientId(confirmDTO.getClientId())
                    .confirmationCode(confirmDTO.getCode())
                    .username(confirmDTO.getEmail())
                    .build();

            this.cognitoClient.confirmSignUp(signUpRequest);

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e);
        }
    }
}
