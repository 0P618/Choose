package com.goldenration.sdbi.xx.timechoose.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/4.
 */

public class Admin extends BmobObject {
    private String Admin_Name;
    private String Admin_Tel;
    private String Admin_Class;
    private String Admin_Dept;
    private String Admin_ID;
    private String Admin_Pwd;

    public String getAdmin_Name() {
        return Admin_Name;
    }

    public void setAdmin_Name(String admin_Name) {
        Admin_Name = admin_Name;
    }

    public String getAdmin_Tel() {
        return Admin_Tel;
    }

    public void setAdmin_Tel(String admin_Tel) {
        Admin_Tel = admin_Tel;
    }

    public String getAdmin_Class() {
        return Admin_Class;
    }

    public void setAdmin_Class(String admin_Class) {
        Admin_Class = admin_Class;
    }

    public String getAdmin_Dept() {
        return Admin_Dept;
    }

    public void setAdmin_Dept(String admin_Dept) {
        Admin_Dept = admin_Dept;
    }

    public String getAdmin_ID() {
        return Admin_ID;
    }

    public void setAdmin_ID(String admin_ID) {
        Admin_ID = admin_ID;
    }

    public String getAdmin_Pwd() {
        return Admin_Pwd;
    }

    public void setAdmin_Pwd(String admin_Pwd) {
        Admin_Pwd = admin_Pwd;
    }
}
