package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;

public class User extends DataManager.FirestoreDocument {

    private String name;
    private String email;
    private String phone_number;
    // TODO add contact info

    public User(){}

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(User user) {
        return user.getId().equals(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + getId() + '\'' +
                '}';
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhone_number(String phone_number){
        this.phone_number = phone_number;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPhone_number(){
        return this.phone_number;
    }



}
