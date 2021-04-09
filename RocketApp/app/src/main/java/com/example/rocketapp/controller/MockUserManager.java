package com.example.rocketapp.controller;
import android.app.Activity;
import com.example.rocketapp.controller.callbacks.Callback;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.users.User;

import java.util.ArrayList;
import java.util.Random;

public class MockUserManager extends UserManager {
    private static final String TAG = "MockUserManager";
    private Random rand = new Random();

    public MockUserManager() {
        super();
        initializeUsers();
    }

    private User createMockUser(String name, String id) {
        User user = new User(name);
        ((FirestoreDocument) user).setId(new FirestoreDocument.Id(id));
        return user;
    }

    @Override
    protected void initializeUsers() {
        userArrayList = new ArrayList<>();
        userArrayList.add(createMockUser("Mock User", "555"));
        userArrayList.add(createMockUser("Jerry Mock", "123"));
        userArrayList.add(createMockUser("Kendra Mock", "456"));
        userArrayList.add(createMockUser("Lucy Mock", "789"));
        subscriptions = new ArrayList<>();
    }

    @Override
    protected void listenImp(User user) {
    }

    @Override
    protected void setUpdateCallbackImp(Callback callback) {
        super.setUpdateCallbackImp(callback);
    }

    @Override
    protected void createUserImp(String userName, Activity activity, ObjectCallback<User> onSuccess, ObjectCallback<Exception> onFailure) {
        user = userArrayList.get(0);
        user.setName(userName);
        onSuccess.callBack(user);
    }

    @Override
    protected void loginImp(Activity activity, ObjectCallback<User> onSuccess, ObjectCallback<Exception> onFailure) {
//        user = userArrayList.get(0);
//        onSuccess.callBack(user);
        onFailure.callBack(new Exception("No account"));
    }

    @Override
    protected void updateUserImp(ObjectCallback<User> onSuccess, ObjectCallback<Exception> onFailure) {
        onSuccess.callBack(user);
    }

    @Override
    protected ArrayList<FirestoreDocument.Id> getSubscriptionsIdListImp() {
        return super.getSubscriptionsIdListImp();
    }

    @Override
    protected void subscribeImp(Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        subscriptions.add(experiment.getId());
        onSuccess.callBack();
    }
}
