package com.kawai.config;

import com.kawai.models.Account;
import com.kawai.repositories.AccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalSessionAdvice {

    @Autowired
    private AccountRepository accountRepository;

    @ModelAttribute
    public void syncSessionUser(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            Account sessionUser = (Account) session.getAttribute("user");
            if (sessionUser == null) {
                String username = auth.getName();
                if (auth instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
                    org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken =
                        (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) auth;
                    if (oauthToken.getPrincipal() != null) {
                        String emailAttr = oauthToken.getPrincipal().getAttribute("email");
                        if (emailAttr != null) {
                            username = emailAttr;
                        }
                    }
                }
                Account account = accountRepository.findByUsername(username).orElse(null);
                if (account != null) {
                    session.setAttribute("user", account);
                }
            }
        }
    }
}
