package com.OLearning.service.termsAndCondition.impl;

import com.OLearning.entity.TermsAndCondition;
import com.OLearning.repository.TermsAndConditionRepository;
import com.OLearning.service.termsAndCondition.TermsAndConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TermsAndConditionServiceImpl implements TermsAndConditionService {

    @Autowired
    private TermsAndConditionRepository termsAndConditionRepository;

    @Override
    public List<TermsAndCondition> getAll() {
        return termsAndConditionRepository.findAll(Sort.by("displayOrder").ascending());
    }
}