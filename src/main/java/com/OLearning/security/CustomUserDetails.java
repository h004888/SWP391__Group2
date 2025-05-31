package com.OLearning.security;

import com.OLearning.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public String getFullName() {
        return user.getFullName();
    }

    public String getphone() {
        return user.getPhone();
    }

    public LocalDate getBirthday() {
        return user.getBirthday();
    }

    public String getAdress() {
        return user.getAddress();
    }

    public String getPersonalSkill() {
        return user.getPersonalSkill();
    }

    public String getEmail() {
        return user.getEmail();
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