package com.OLearning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.OLearning.repository.UserRepository;

@Component
public class TestRunCommand implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Total Instructor: " + userRepository.countInstructor());
        System.out.println("Total Student: " + userRepository.countStudent());
    }
}
