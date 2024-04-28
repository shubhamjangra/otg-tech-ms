package com.otg.tech.auth.security;

import com.otg.tech.auth.claims.ClaimBuilder;
import com.otg.tech.auth.claims.Identity;
import com.otg.tech.auth.claims.UserGroup;
import com.otg.tech.auth.claims.UserType;
import com.otg.tech.auth.exception.AuthenticationException;
import com.otg.tech.redis.domain.Session;
import com.otg.tech.redis.domain.UserProfile;
import com.otg.tech.redis.service.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    private final RedisService redisService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws org.springframework.security.core.AuthenticationException {
        log.info("AuthenticationProvider::authenticate called ");
        String authToken = (String) authentication.getCredentials();

        Identity identity = ClaimBuilder.Identity(authToken);
        Session session = redisService.get(identity.getSessionStateId(), Session.class)
                .orElseThrow(() -> new AuthenticationException("Invalid Session"));

        if (!authToken.equals(session.getAccessToken()))
            throw new AuthenticationException("invalid session");

        UserProfile userProfile = session.getUserProfile();
        MDC.put("userId", userProfile.getUserId());
        identity.setUserId(userProfile.getUserId());
        identity.setUserType(UserType.fromString(userProfile.getUserType().name()));
        identity.setSelfAuth(userProfile.isSelfAuth());
        if (userProfile.getUserGroups() != null && !userProfile.getUserGroups().isEmpty())
            identity.setUserGroups(
                    userProfile.getUserGroups().stream().map(ug -> new UserGroup(ug.getId(), ug.getName())).toList());

        List<SimpleGrantedAuthority> authorities = null;
        if (session.getUserProfile().getPermissions() != null) {
            authorities = session.getUserProfile().getPermissions().stream()
                    .map(SimpleGrantedAuthority::new).toList();
        }
        log.info("AuthenticationProvider::Session validation successful ");
        return new UsernamePasswordAuthenticationToken(identity, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
