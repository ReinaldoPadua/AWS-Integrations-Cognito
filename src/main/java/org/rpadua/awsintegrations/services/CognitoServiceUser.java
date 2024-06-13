package org.rpadua.awsintegrations.services;


import org.rpadua.awsintegrations.DTOs.UserCognitoDTO;
import org.rpadua.awsintegrations.providers.CognitoProviderUser;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CognitoServiceUser {

    private final CognitoProviderUser cognitoProviderUser;


    public CognitoServiceUser(final CognitoProviderUser cognitoProviderUser){
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
}
