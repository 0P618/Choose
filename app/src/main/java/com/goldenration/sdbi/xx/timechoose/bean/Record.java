package com.goldenration.sdbi.xx.timechoose.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Kiuber on 2016/8/4.
 */

public class Record extends BmobObject {
    private String Stu_ID;
    private String Stu_Name;
    private String Stu_Class;
    private String Study_Where;
    private String Where_Remarks;
    private String Where_Prove;
    private String Check_Status;
    private String Check_By_Who;

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

    public String getStudy_Where() {
        return Study_Where;
    }

    public void setStudy_Where(String study_Where) {
        Study_Where = study_Where;
    }

    public String getWhere_Remarks() {
        return Where_Remarks;
    }

    public void setWhere_Remarks(String where_Remarks) {
        Where_Remarks = where_Remarks;
    }

    public String getWhere_Prove() {
        return Where_Prove;
    }

    public void setWhere_Prove(String where_Prove) {
        Where_Prove = where_Prove;
    }

    public String getCheck_Status() {
        return Check_Status;
    }

    public void setCheck_Status(String check_Status) {
        Check_Status = check_Status;
    }

    public String getCheck_By_Who() {
        return Check_By_Who;
    }

    public void setCheck_By_Who(String check_By_Who) {
        Check_By_Who = check_By_Who;
    }
}
