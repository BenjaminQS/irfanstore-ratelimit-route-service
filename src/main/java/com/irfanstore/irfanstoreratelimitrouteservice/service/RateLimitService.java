package com.irfanstore.irfanstoreratelimitrouteservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RateLimitService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisService redisService;

    public boolean ExceedRateLimit(String incomingUrl,String remoteIp) {

        String key = incomingUrl.concat(":").concat(remoteIp);
        int attempts = getAttempts(key);

        this.logger.info(System.lineSeparator() +  System.lineSeparator() + "ATTEMPTS {} MADE BY IP {} " + System.lineSeparator(), attempts,remoteIp);

        int allowedAttempts = getAllowedAttempts();

        if (attempts > allowedAttempts) {
            this.logger.info(System.lineSeparator() +  System.lineSeparator() + "Request exceed the maximum limit of {} requests within {} minutes" + System.lineSeparator(), allowedAttempts,getRateLimitExpiry());
            return true;
        }
        else {
            return false;
        }
    }

    private int getAttempts(String key)
    {
        int rateLimitExpiry = getRateLimitExpiry();
        redisService.setValue(key,1,rateLimitExpiry);
        String attemptsStr = (String)redisService.getValue(key);

        int attempts  = Integer.parseInt(attemptsStr);
        if(attempts==1) { redisService.setExpiry(key,rateLimitExpiry); }

        return attempts;

    }

    public int getRateLimitExpiry() {
        String rateLimitExpiryEnvVar =  System.getenv("RATE_LIMIT_EXPIRY");
        int rateLimitExpiry = (rateLimitExpiryEnvVar != null && !rateLimitExpiryEnvVar.isEmpty()) ?  Integer.parseInt(rateLimitExpiryEnvVar) : 5;
        return rateLimitExpiry;
    }

    public int getAllowedAttempts() {
        String rateLimitAttemptAllowedEnvVar =  System.getenv("RATE_LIMIT_ATTEMPT_ALLOWED");
        int rateLimitAttemptAllowed = (rateLimitAttemptAllowedEnvVar != null && !rateLimitAttemptAllowedEnvVar.isEmpty()) ?
                Integer.parseInt(rateLimitAttemptAllowedEnvVar) : 5;

        return rateLimitAttemptAllowed;

    }

}


