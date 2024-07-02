package org.rpadua.awsintegrations.services;


import org.rpadua.awsintegrations.DTOs.UserCognitoDTO;
import org.rpadua.awsintegrations.providers.CognitoUserProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CognitoUserService {

    private final CognitoUserProvider cognitoProviderUser;


    public CognitoUserService(final CognitoUserProvider cognitoProviderUser){
        this.cognitoProviderUser = cognitoProviderUser;
    }


    public List<UserCognitoDTO> listAllUsers(String userPool) throws Exception {

        ListUsersResponse listUsers = this.cognitoProviderUser.listAllUsers(userPool);

        return listUsers.users().stream().map(userType -> new UserCognitoDTO(
                userType.username(),userType.userStatusAsString(),
                userType.attributes().stream().filter(
                        u-> u.name().equals("email")
                ).map(AttributeType::value).findFirst().orElse(null),
                userType.attributes().stream().filter(
                        u-> u.name().equals("name")
                ).map(AttributeType::value).findFirst().orElse(null)
        )).collect(Collectors.toList());
    }

    public UserCognitoDTO getUser(String userPool, String userName) throws Exception {

        AdminGetUserResponse user = this.cognitoProviderUser.getUser(userPool,userName);

        return new UserCognitoDTO(
                user.username(),
                user.userStatusAsString(),
                user.userAttributes().stream().filter(
                        u-> u.name().equals("email")
                ).map(AttributeType::value).findFirst().orElse(null),
                user.userAttributes().stream().filter(
                        u-> u.name().equals("name")
                ).map(AttributeType::value).findFirst().orElse(null)
        );
    }

    public void updateUser(String userPool, UserCognitoDTO userCognito) throws Exception {
        if(Objects.nonNull(userCognito.getName())){
            this.cognitoProviderUser.updateUserAttributeName(userPool,userCognito.getEmail(), userCognito.getName());
        }

        if(userCognito.getStatus().equals("Disable")){
            this.cognitoProviderUser.disableUser(userPool,userCognito.getEmail());
        }
    }

    public void deleteUser(String userPool, String userName) throws Exception {
        this.cognitoProviderUser.deleteUser(userPool,userName);
    }
}
