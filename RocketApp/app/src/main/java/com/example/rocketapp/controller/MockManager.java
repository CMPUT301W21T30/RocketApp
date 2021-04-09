package com.example.rocketapp.controller;

/**
 * Initializes mock for all controllers for unit testing to simulate database interactions
 */
public class MockManager {
    public static void initializeMock() {

        UserManager.inject(new MockUserManager());
        ExperimentManager.inject(new MockExperimentManager());
        TrialManager.inject(new MockTrialManager());
        ForumManager.inject(new MockForumManager());
    }
}
