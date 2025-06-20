package com.OLearning.service.termsAndCondition;

import com.OLearning.entity.TermsAndCondition;
import com.OLearning.repository.TermsAndConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TermsAndConditionService {
    @Autowired
    private TermsAndConditionRepository termsAndConditionRepository;

    public List<TermsAndCondition> getAll() {
        return termsAndConditionRepository.findAll(Sort.by("displayOrder").ascending());
    }
}
