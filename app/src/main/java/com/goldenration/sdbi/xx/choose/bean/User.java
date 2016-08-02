package com.goldenration.sdbi.xx.choose.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kiuber on 2016/6/29.
 */

public class User extends BmobObject{
    private String User_StuID;
    private String User_Name;
    private String User_Pwd;
    private String User_Tel;
    private String User_DefaultPwd;

    public String getUser_StuID() {
        return User_StuID;
    }

    public void setUser_StuID(String user_StuID) {
        User_StuID = user_StuID;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Pwd() {
        return User_Pwd;
    }

    public void setUser_Pwd(String user_Pwd) {
        User_Pwd = user_Pwd;
    }

    public String getUser_Tel() {
        return User_Tel;
    }

    public void setUser_Tel(String user_Tel) {
        User_Tel = user_Tel;
    }

    public String getUser_DefaultPwd() {
        return User_DefaultPwd;
    }

    public void setUser_DefaultPwd(String user_DefaultPwd) {
        User_DefaultPwd = user_DefaultPwd;
    }
}
