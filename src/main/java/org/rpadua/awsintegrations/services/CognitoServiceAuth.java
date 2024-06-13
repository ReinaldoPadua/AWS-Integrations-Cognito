package org.rpadua.awsintegrations.services;


import org.rpadua.awsintegrations.DTOs.SignInRequestDTO;
import org.rpadua.awsintegrations.DTOs.SignInResponseDTO;
import org.rpadua.awsintegrations.providers.CognitoProviderAuth;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.util.Objects;

@Service
public class CognitoServiceAuth {

    private final CognitoProviderAuth cognitoProviderAuth;


    public CognitoServiceAuth(final CognitoProviderAuth cognitoProviderAuth){
        this.cognitoProviderAuth = cognitoProviderAuth;
    }

    public SignInResponseDTO signIn(String userPool, SignInRequestDTO signInRequestDTO) throws Exception {
        AdminInitiateAuthResponse response = cognitoProviderAuth.signIn(userPool, signInRequestDTO);

        SignInResponseDTO signInResponse = this.createSignInResponseDTO(response.authenticationResult());

        if(Objects.nonNull(response.session())){
            signInResponse.setSession(response.session());
        }

        return signInResponse;

    }

    public SignInResponseDTO respondToAuthChallenge(String userPool, SignInRequestDTO signInRequestDTO) throws Exception {
        AdminRespondToAuthChallengeResponse response =
                this.cognitoProviderAuth.respondToAuthChallenge(userPool, signInRequestDTO);

        return  this.createSignInResponseDTO(response.authenticationResult());
    }

    private SignInResponseDTO createSignInResponseDTO(AuthenticationResultType authenticationResult) {
        SignInResponseDTO signInResponse = new SignInResponseDTO();
        if(Objects.nonNull(authenticationResult)){
            signInResponse.setAccessToken(authenticationResult.accessToken());
            signInResponse.setExpiresIn(authenticationResult.expiresIn());
            signInResponse.setIdToken(authenticationResult.idToken());
            signInResponse.setTokenType(authenticationResult.tokenType());
            signInResponse.setRefreshToken(authenticationResult.refreshToken());
        }

        return  signInResponse;
    }
}
