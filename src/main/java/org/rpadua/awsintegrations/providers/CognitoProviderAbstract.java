package org.rpadua.awsintegrations.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

public abstract class CognitoProviderAbstract {

    protected  Logger logger = LoggerFactory.getLogger(CognitoProviderAbstract.class);

    @Value("${aws.cognito.user-pool-ids}")
    protected String[] USER_POOL_IDS;

    protected CognitoIdentityProviderClient cognitoClient;
}
