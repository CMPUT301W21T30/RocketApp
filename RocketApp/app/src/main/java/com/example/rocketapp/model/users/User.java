package com.example.rocketapp.model.users;
import com.example.rocketapp.controller.FirestoreDocument;

/**
 * User are users of this application
 * They can behave as Owners by creating new experiment
 * They can behave as Experimenters by subscribing or participating by submitting trials to an experiment
 * Users are added to firestore database upon creation
 */
public class User extends FirestoreDocument {
    private String name;
    private String email;
    private String phone_number;

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public User(){}

    /**
     * Constructor with a username passed
     * @param name
     *         username of User
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * getter for username of User
     * @return
     *      username of User
     */
    public String getName() {
        return name;
    }

    /**
     * setter for username
     * @param name
     *          username to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Display function of User
     * @return String describing a display for User object with username, name and userID
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + getId() + '\'' +
                '}';
    }

    /**
     * setter for User email
     * @param email
     *          new email the user wants to set
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * setter for User phone number
     * @param phone_number
     *          new phone number the User wants to set
     */
    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }

    /**
     * getter for User email
     * @return user email
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * getter for User phone number
     * @return User phone number
     */
    public String getPhoneNumber(){
        return this.phone_number;
    }



}
