package com.vinatour.backend.security;

import com.vinatour.backend.entity.User;
import com.vinatour.backend.repository.UserRepository;
import com.vinatour.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture"); 

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);

            newUser.setUsername(email); 
            newUser.setPassword("");
            newUser.setAvatarUrl(picture);
            newUser.setRole("ROLE_USER");
            newUser.setActive(true);

            userRepository.save(newUser);
        } else {
            User existingUser = userOptional.get();
            if (picture != null && (existingUser.getAvatarUrl() == null || existingUser.getAvatarUrl().isEmpty())) {
                existingUser.setAvatarUrl(picture);
                userRepository.save(existingUser);
            }
        }

        String token = jwtUtil.generateToken(email);
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}