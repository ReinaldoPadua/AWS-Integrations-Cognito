package org.rpadua.awsintegrations.providers;

import org.rpadua.awsintegrations.DTOs.ConfirmDTO;
import org.rpadua.awsintegrations.DTOs.RegisterDTO;
import org.rpadua.awsintegrations.DTOs.UserCognitoDTO;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class CognitoProvider {

    private CognitoIdentityProviderClient cognitoClient;
    private String userPoolId = "us-east-1_UeTO78DbC";

    public CognitoProvider(){
        super();
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create("AKIASZ24GQTVJCAAKBFD", "F8PQlCCL3YAJaX1DijtbTLw1PEYlaXp2YtWAS9V6"))
                .build();
    }

    public List<UserCognitoDTO> listAllUsers() throws Exception {

        try {
            this.createUser();
            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(this.userPoolId)
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

    public void createUser() throws Exception {

        try {
            AdminCreateUserRequest adminCreateUserRequest = AdminCreateUserRequest.builder()
                    .userPoolId(this.userPoolId)
                    .username("makakito@gmail.com")
                    .temporaryPassword("Punk1986202093#")
                    .userAttributes(
                            new AttributeType[]{
                                    AttributeType.builder().name("name").value("cavalo").build(),
                            }).build();

            this.cognitoClient.adminCreateUser(adminCreateUserRequest);
        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e);
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
                    .clientId("1l1pbq6g2l68n056ltcm9p080h")
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
                    .clientId("1l1pbq6g2l68n056ltcm9p080h")
                    .confirmationCode(confirmDTO.getCode())
                    .username(confirmDTO.getEmail())
                    .build();

            this.cognitoClient.confirmSignUp(signUpRequest);

        } catch (CognitoIdentityProviderException e) {
            throw new Exception(e);
        }
    }
}
