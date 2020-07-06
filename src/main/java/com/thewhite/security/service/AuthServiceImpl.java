package com.thewhite.security.service;

import com.thewhite.security.errorInfo.DiaryErrorInfo;
import com.whitesoft.util.exceptions.WSForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Konstantin Khudin
 */
@Service
public class AuthServiceImpl implements AuthService {
    public String getAuthorizedOwnerName() {
        return getPrincipal()
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new WSForbiddenException(DiaryErrorInfo.UNAUTHORIZE));
    }

    private Optional<UserDetails> getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof User)) return Optional.empty();

        User details = (User) authentication.getPrincipal();

        return Optional.of(details);
    }
}