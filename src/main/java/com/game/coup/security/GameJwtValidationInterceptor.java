package com.game.coup.security;

import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class GameJwtValidationInterceptor implements ChannelInterceptor {

    // Inject your existing JWT engine here
    // private final JwtService jwtService; 


}