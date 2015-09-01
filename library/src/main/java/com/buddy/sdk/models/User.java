package com.buddy.sdk.models;

import java.util.Date;

public class User  extends ModelBase {

    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    public String userName;
    public String firstName;
    public String lastName;
    public String email;
    public Date dateOfBirth;
    public Date lastLogin;
    public String profilePictureID;
    public String profilePictureUrl;

}
