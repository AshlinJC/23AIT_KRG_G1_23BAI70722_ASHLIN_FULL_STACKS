package com.livepoll.config;

import com.livepoll.entity.Role;
import com.livepoll.entity.User;
import com.livepoll.repository.UserRepository;
import com.livepoll.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email   = oauthUser.getAttribute("email");
        String name    = oauthUser.getAttribute("name");

        // Find existing user or auto-register on first Google login
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider("GOOGLE");
            newUser.setRoles(Set.of(Role.USER));
            return userRepository.save(newUser);
        });

        // Build Spring Security UserDetails from our DB user
        String roleString = "ROLE_" + user.getRoles().iterator().next().name();
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "",
                List.of(new SimpleGrantedAuthority(roleString))
        );

        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        String jwt = tokenProvider.generateToken(auth);

        // Redirect to React callback page with token in query param
        // React reads it once, stores in sessionStorage, then removes from URL
        String redirectUrl = "http://localhost:5173/oauth2/callback?token=" + jwt;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
