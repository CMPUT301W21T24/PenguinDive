package com.cmput301.penguindive;

public class Profile {

    // Variables
    private String email;
    private String userName;

    // Constructor
    public void Profile(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }


    // Getters
    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    // Setters
    public void setPhone(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Functions
    public void ShowProfile(String userName){
        // To be developed
    }

    public void EditProfile(String userName){
        // Prompt dialog to enter new username and phone
//        setUserName(newUserName);
//        setEmail(newEmail);
    }
}
