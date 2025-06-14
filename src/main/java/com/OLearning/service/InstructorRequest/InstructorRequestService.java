package com.OLearning.service.InstructorRequest;

import com.OLearning.entity.InstructorRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface InstructorRequestService {
    Page<InstructorRequest> getAllInstructorRequests(Pageable pageable);
    
    InstructorRequest getRequestById(Long requestId);
    
    InstructorRequest acceptRequest(Long requestId, Long adminId);
    
    InstructorRequest rejectRequest(Long requestId, Long adminId, String rejectionReason);
    
    Page<InstructorRequest> filterRequests(String keyword, String status, Pageable pageable);
}
