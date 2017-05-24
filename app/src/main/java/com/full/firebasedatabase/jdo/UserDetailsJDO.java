package com.full.firebasedatabase.jdo;

import com.google.firebase.database.PropertyName;

/**
 * Created by Shangeeth Sivan on 24/05/17.
 */

public class UserDetailsJDO {
    @PropertyName("name")
    public String name;

    @PropertyName("userEmail")
    public String userEmail;

    @PropertyName("isActive")
    public boolean isActive;

    public UserDetailsJDO() {
    }

    public UserDetailsJDO(String pName, String pUserEmail, boolean pIsActive) {
        name = pName;
        userEmail = pUserEmail;
        isActive = pIsActive;
    }

    public String getName() {
        return name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
