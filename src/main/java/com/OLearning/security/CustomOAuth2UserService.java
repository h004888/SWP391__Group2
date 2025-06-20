package com.OLearning.security;

import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.repository.RoleRepository;
import com.OLearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (picture != null && !picture.equals(user.getProfilePicture())) {
                user.setProfilePicture(picture);
                userRepository.save(user);
            }

        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setUsername(email.split("@")[0]);
            user.setStatus(true);
            user.setProfilePicture(picture);

            // Mặc định role là ROLE_USER
            Role roleUser = roleRepository.findRoleByName("User")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            user.setRole(roleUser);

            //Set default when login Oauth2
            user.setPassword(new BCryptPasswordEncoder().encode("123"));

            userRepository.save(user);
        }

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase());

        return new CustomUserDetails(user,
                Collections.singleton(authority),
                oauth2User.getAttributes());
    }
}

