package com.OLearning.service.InstructorRequest.impl;

import com.OLearning.entity.InstructorRequest;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.repository.InstructorRequestRepository;
import com.OLearning.repository.RoleRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.InstructorRequest.InstructorRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;



@Service
public class InstructorRequestServiceImpl implements InstructorRequestService {
    @Autowired
    private InstructorRequestRepository instructorRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Page<InstructorRequest> getAllInstructorRequests(Pageable pageable) {
        return instructorRequestRepository.findAll(pageable);
    }
    
    @Override
    public InstructorRequest getRequestById(Long requestId) {
        return instructorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Instructor request not found with id: " + requestId));
    }
    
    @Override
    @Transactional
    public InstructorRequest acceptRequest(Long requestId, Long adminId) {
        InstructorRequest request = getRequestById(requestId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
        
        // Update request status
        request.setStatus("ACCEPTED");
        request.setDecisionDate(LocalDateTime.now());
        request.setAdmin(admin);
        
        // Update user role to instructor
        User user = request.getUser();
        Role instructorRole = roleRepository.findById(3L) // Assuming 3L is the instructor role ID
                .orElseThrow(() -> new RuntimeException("Instructor role not found"));
        user.setRole(instructorRole);
        userRepository.save(user);
        
        return instructorRequestRepository.save(request);
    }
    
    @Override
    @Transactional
    public InstructorRequest rejectRequest(Long requestId, Long adminId, String rejectionReason) {
        InstructorRequest request = getRequestById(requestId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
        
        // Update request status and note
        request.setStatus("REJECTED");
        request.setDecisionDate(LocalDateTime.now());
        request.setAdmin(admin);
        request.setNote(rejectionReason);
        
        return instructorRequestRepository.save(request);
    }

    @Override
    public Page<InstructorRequest> filterRequests(String keyword, String status, Pageable pageable) {
        if ((keyword == null || keyword.trim().isEmpty()) && (status == null || status.trim().isEmpty())) {
            return instructorRequestRepository.findAll(pageable);
        }

        return instructorRequestRepository.findByKeywordAndStatus(
            keyword != null ? keyword.trim() : "",
            status != null ? status.trim() : "",
            pageable
        );
    }
}
