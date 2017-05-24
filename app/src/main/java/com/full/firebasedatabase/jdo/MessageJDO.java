package com.full.firebasedatabase.jdo;

import com.google.firebase.database.PropertyName;

/**
 * Created by Shangeeth Sivan on 23/05/17.
 */

public class MessageJDO {

    @PropertyName("username")
    String Username;
    @PropertyName("message")
    String Message;

    @PropertyName("time")
    String Time;

    public MessageJDO() {
    }

    public MessageJDO(String username, String message,String time) {
        Username = username;
        Message = message;
        Time = time;
    }

    public String getUsername() {
        return Username;
    }

    public String getMessage() {
        return Message;
    }

    public String getTime() {
        return Time;
    }
}
