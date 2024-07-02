//package org.rpadua.awsintegrations.providers;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.rpadua.awsintegrations.DTOs.SignUpRequestDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
//import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@ExtendWith(MockitoExtension.class)
//public class CognitoAuthProviderTest {
//
//    @MockBean
//    private CognitoIdentityProviderClient cognitoClient;
//
//    @MockBean
//    private CognitoAuthProvider cognitoAuthProvider;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testSignUpSuccess() throws Exception {
//        // Mock the behavior of cognitoClient
//        doNothing().when(cognitoClient).signUp(any(SignUpRequest.class));
//
//        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
//        signUpRequestDTO.setName("Test User");
//        signUpRequestDTO.setEmail("test@example.com");
//        signUpRequestDTO.setPassword("password");
//
//        assertDoesNotThrow(() -> cognitoAuthProvider.signUp("new-clients-test", signUpRequestDTO));
//    }
//
//    @Test
//    public void testSignUpFailure() throws Exception {
//        // Mock the behavior of cognitoClient to throw an exception
//        doThrow(CognitoIdentityProviderException.builder().message("Error").build())
//                .when(cognitoClient).signUp(any(SignUpRequest.class));
//
//        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
//        signUpRequestDTO.setName("Test User");
//        signUpRequestDTO.setEmail("test@example.com");
//        signUpRequestDTO.setPassword("Password123$");
//
//        Exception exception = assertThrows(Exception.class, () -> cognitoAuthProvider.signUp("new-clients-test", signUpRequestDTO));
//        assertEquals("Error", exception.getMessage());
//    }
//
//    @Test
//    public void testSignOutSuccess() throws Exception {
//        // Mock the behavior of cognitoClient
//        doNothing().when(cognitoClient).globalSignOut(any(GlobalSignOutRequest.class));
//
//        assertDoesNotThrow(() -> cognitoAuthProvider.signOut("valid_token"));
//    }
//
//    @Test
//    public void testSignOutFailure() throws Exception {
//        // Mock the behavior of cognitoClient to throw an exception
//        doThrow(CognitoIdentityProviderException.builder().message("Error").build())
//                .when(cognitoClient).globalSignOut(any(GlobalSignOutRequest.class));
//
//        Exception exception = assertThrows(Exception.class, () -> cognitoAuthProvider.signOut("valid_token"));
//        assertEquals("Error", exception.getMessage());
//    }
//
//    @Test
//    public void testResendConfirmationCodeSuccess() throws Exception {
//        // Mock the behavior of cognitoClient
//        doNothing().when(cognitoClient).resendConfirmationCode(any(ResendConfirmationCodeRequest.class));
//
//        assertDoesNotThrow(() -> cognitoAuthProvider.resendConfirmationCode("userPool", "username"));
//    }
//
//    @Test
//    public void testResendConfirmationCodeFailure() throws Exception {
//        // Mock the behavior of cognitoClient to throw an exception
//        doThrow(CognitoIdentityProviderException.builder().message("Error").build())
//                .when(cognitoClient).resendConfirmationCode(any(ResendConfirmationCodeRequest.class));
//
//        Exception exception = assertThrows(Exception.class, () -> cognitoAuthProvider.resendConfirmationCode("new-clients-test", "test@example.com"));
//        assertEquals("Error", exception.getMessage());
//    }
//}
//
