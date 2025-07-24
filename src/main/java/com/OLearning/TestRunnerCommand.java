package com.OLearning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.OLearning.service.enrollment.EnrollmentService;

@Component
public class TestRunnerCommand implements CommandLineRunner {

    @Autowired
    private EnrollmentService enrollmentService;

    @Override
    public void run(String... args) throws Exception {
        // enrollmentService.updateStatusToCompleted(16L, 15L);
        System.out.println("Update status completed");
    }
}
