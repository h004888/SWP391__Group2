package com.OLearning.service.termsAndCondition;

import com.OLearning.entity.TermsAndCondition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TermsAndConditionService {
    List<TermsAndCondition> getAll();
}
