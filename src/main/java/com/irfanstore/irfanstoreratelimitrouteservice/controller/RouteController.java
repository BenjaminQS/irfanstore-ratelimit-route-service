package com.irfanstore.irfanstoreratelimitrouteservice.controller;

import com.irfanstore.irfanstoreratelimitrouteservice.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

@RestController
public class RouteController {
    static final String FORWARDED_URL = "X-CF-Forwarded-Url";
    static final String PROXY_METADATA = "X-CF-Proxy-Metadata";
    static final String PROXY_SIGNATURE = "X-CF-Proxy-Signature";


    @Autowired
    RestOperations restOperations;

    @Autowired
    RateLimitService rateLimitService;

    @RequestMapping(headers = {FORWARDED_URL, PROXY_METADATA, PROXY_SIGNATURE})
    ResponseEntity<?> service(RequestEntity<byte[]> incomingRequest) {


        String incomingUrl = incomingRequest.getHeaders().get("X-CF-Forwarded-Url").toString();
        String remoteIp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest()
                            .getRemoteAddr();


        if(rateLimitService.ExceedRateLimit(incomingUrl,remoteIp)) {
            String responseMessage = String.format("Request exceed the maximum limit of %s requests within %s minutes",
                    rateLimitService.getAllowedAttempts(), rateLimitService.getRateLimitExpiry());

            return ResponseEntity.ok().body(responseMessage);

        }

        RequestEntity<?> outgoingRequest = getOutgoingRequest(incomingRequest);
        return this.restOperations.exchange(outgoingRequest, byte[].class);
    }

    private static RequestEntity<?> getOutgoingRequest(RequestEntity<?> incomingRequest) {
       HttpHeaders headers = new HttpHeaders();
        headers.putAll(incomingRequest.getHeaders());

        URI uri = headers.remove(FORWARDED_URL).stream()
                .findFirst()
                .map(URI::create)
                .orElseThrow(() -> new IllegalStateException(String.format("No %s header present", FORWARDED_URL)));


        return new RequestEntity<>(incomingRequest.getBody(), headers, incomingRequest.getMethod(), uri);
    }
}
