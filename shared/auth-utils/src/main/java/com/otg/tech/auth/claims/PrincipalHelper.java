package com.otg.tech.auth.claims;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class PrincipalHelper {
    public Identity getIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isNull(authentication) ? null : (Identity) (authentication.getPrincipal() instanceof Identity ?
                authentication.getPrincipal() : null);
    }
}