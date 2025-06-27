package com.OLearning.security;

import com.OLearning.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes; // chứa info từ Google

    // Constructor cho form login
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public String getFullName() {
        return user.getFullName() != null ? user.getFullName() : "";
    }

    public String getPhone() {
        return user.getPhone() != null ? user.getPhone() : "";
    }

    public LocalDate getBirthday() {
        return user.getBirthDay();
    }

    public String getAddress() {
        return user.getAddress() != null ? user.getAddress() : "";
    }

    public String getPersonalSkill() {
        if (user.getProfilePicture() != null && !user.getProfilePicture().trim().isEmpty()) {
            return user.getProfilePicture();
        }
        // Trả về ảnh mặc định nếu không có
        return "/assets/images/avatar/01.jpg";
    }

    public String getEmail() {
        return user.getEmail() != null ? user.getEmail() : "";
    }

    public String getProfilePicture() {
        return user.getProfilePicture();
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public User getUserEntity() {
        return this.user;
    }

    // LOGIN OAUTH2
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    // DeFault function
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // LOGIN WITH EMAIL
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}