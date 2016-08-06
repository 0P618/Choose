package com.goldenration.sdbi.xx.timechoose.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kiuber on 2016/8/3.
 */

public class Student extends BmobObject {
    private String Stu_ID;
    private String Stu_Name;
    private String Stu_Class;
    private String Stu_Sex;
    private String Stu_Pwd;
    private String Stu_DefaultPwdStatus;

    public String getStu_Class() {
        return Stu_Class;
    }

    public void setStu_Class(String stu_Class) {
        Stu_Class = stu_Class;
    }
    public String getStu_ID() {
        return Stu_ID;
    }

    public void setStu_ID(String stu_ID) {
        Stu_ID = stu_ID;
    }

    public String getStu_Name() {
        return Stu_Name;
    }

    public void setStu_Name(String stu_Name) {
        Stu_Name = stu_Name;
    }

    public String getStu_Sex() {
        return Stu_Sex;
    }

    public void setStu_Sex(String stu_Sex) {
        Stu_Sex = stu_Sex;
    }

    public String getStu_Pwd() {
        return Stu_Pwd;
    }

    public void setStu_Pwd(String stu_Pwd) {
        Stu_Pwd = stu_Pwd;
    }

    public String getStu_DefaultPwdStatus() {
        return Stu_DefaultPwdStatus;
    }

    public void setStu_DefaultPwdStatus(String stu_DefaultPwdStatus) {
        Stu_DefaultPwdStatus = stu_DefaultPwdStatus;
    }
}
