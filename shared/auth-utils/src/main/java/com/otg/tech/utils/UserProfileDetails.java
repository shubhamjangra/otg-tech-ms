package com.otg.tech.utils;

import com.otg.tech.auth.claims.PrincipalHelper;
import com.otg.tech.auth.exception.AuthenticationException;
import com.otg.tech.redis.domain.Session;
import com.otg.tech.redis.domain.UserProfile;
import com.otg.tech.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserProfileDetails {

    private final PrincipalHelper principalHelper;
    private final RedisService redisService;

    @SuppressWarnings("unused")
    public UserProfile getUserProfileFromSessionDetails() {
        var auIdentity = principalHelper.getIdentity();
        Session session = redisService.get(auIdentity.getSessionStateId(), Session.class)
                .orElseThrow(() -> new AuthenticationException("Invalid Session"));

        return session.getUserProfile();
    }

    public Session getSession() {
        var auIdentity = principalHelper.getIdentity();
        return Optional.ofNullable(auIdentity)
                .flatMap(identity -> redisService.get(identity.getSessionStateId(), Session.class))
                .orElse(null);
    }

    public String getToken() {
        return Optional.ofNullable(getSession())
                .map(session -> "Bearer " + session.getAccessToken())
                .orElse(null);
    }
}