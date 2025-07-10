package com.OLearning.mapper.instructorRequest;

import com.OLearning.entity.InstructorRequest;
import com.OLearning.entity.User;
import com.OLearning.dto.instructorRequest.InstructorRequestDTO;
import java.time.LocalDateTime;

public class InstructorRequestMapper {
    public static InstructorRequest mapToEntity(InstructorRequestDTO dto, User user) {
        InstructorRequest entity = new InstructorRequest();
        entity.setRequestId(dto.getRequestId());
        entity.setRequestDate(dto.getRequestDate() != null ? dto.getRequestDate() : LocalDateTime.now());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        entity.setNote(dto.getNote());
        entity.setPersonalSkill(dto.getPersonalSkill());
        entity.setDecisionDate(dto.getDecisionDate());
        entity.setUser(user);
        return entity;
    }

    public static InstructorRequestDTO mapToDTO(InstructorRequest entity) {
        InstructorRequestDTO dto = new InstructorRequestDTO();
        dto.setRequestId(entity.getRequestId());
        dto.setRequestDate(entity.getRequestDate());
        dto.setStatus(entity.getStatus());
        dto.setPersonalSkill(entity.getPersonalSkill());
        dto.setNote(entity.getNote());
        dto.setDecisionDate(entity.getDecisionDate());
        if (entity.getUser() != null) {
            dto.setName(entity.getUser().getFullName());
            dto.setEmail(entity.getUser().getEmail());
            dto.setPhone(entity.getUser().getPhone());
            dto.setPersonalSkill(entity.getPersonalSkill());
        }
        return dto;
    }
} 