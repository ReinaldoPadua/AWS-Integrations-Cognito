package org.rpadua.awsintegrations.util;

import org.rpadua.awsintegrations.DTOs.CognitoClientDTO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class AwsUtils {

    public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
   public static CognitoClientDTO getCognitoClient(String userPool, String[] UserPoolIds){

       String[] userPoolStringArray = Arrays.stream(UserPoolIds).filter(
                       poll-> Objects.nonNull(userPool) && poll.contains(userPool)).findFirst().orElse("")
               .split(":");
        return  new CognitoClientDTO(userPoolStringArray[1],userPoolStringArray[2],userPoolStringArray[3]);
   }
}
