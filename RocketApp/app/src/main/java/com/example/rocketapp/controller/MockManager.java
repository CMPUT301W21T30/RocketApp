package com.example.rocketapp.controller;

public class MockManager {
    public static void initializeMock() {

        UserManager.inject(new MockUserManager());
        ExperimentManager.inject(new MockExperimentManager());
        TrialManager.inject(new MockTrialManager());
        ForumManager.inject(new MockForumManager());
    }
}
