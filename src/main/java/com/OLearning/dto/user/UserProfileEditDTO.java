package com.OLearning.dto.user;

public class UserProfileEditDTO {
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String username;
    private String address;
    private String personalSkill;
    private java.time.LocalDate birthDay;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPersonalSkill() { return personalSkill; }
    public void setPersonalSkill(String personalSkill) { this.personalSkill = personalSkill; }

    public java.time.LocalDate getBirthDay() { return birthDay; }
    public void setBirthDay(java.time.LocalDate birthDay) { this.birthDay = birthDay; }
} 