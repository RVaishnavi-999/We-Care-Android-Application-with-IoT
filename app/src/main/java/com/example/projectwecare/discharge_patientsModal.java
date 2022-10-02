package com.example.projectwecare;

public class discharge_patientsModal {
    String Pid,Pfname,treatment_given,advice_discharge,condition_discharge,confirmed_diagnosis,followup,preexisting_condition,Report1,Report2;

    public discharge_patientsModal(){

    }

    public discharge_patientsModal(String pid, String pfname, String treatment_given, String advice_discharge, String condition_discharge, String confirmed_diagnosis, String followup, String preexisting_condition, String report1, String report2) {
        Pid = pid;
        Pfname = pfname;
        this.treatment_given = treatment_given;
        this.advice_discharge = advice_discharge;
        this.condition_discharge = condition_discharge;
        this.confirmed_diagnosis = confirmed_diagnosis;
        this.followup = followup;
        this.preexisting_condition = preexisting_condition;
        Report1 = report1;
        Report2 = report2;
    }

    public String getReport1() {
        return Report1;
    }

    public String getReport2() {
        return Report2;
    }

    public String getAdvice_discharge() {
        return advice_discharge;
    }

    public String getCondition_discharge() {
        return condition_discharge;
    }

    public String getConfirmed_diagnosis() {
        return confirmed_diagnosis;
    }

    public String getFollowup() {
        return followup;
    }

    public String getPreexisting_condition() {
        return preexisting_condition;
    }

    public String getPid() {
        return Pid;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public String getPfname() {
        return Pfname;
    }

    public void setPfname(String pfname) {
        Pfname = pfname;
    }

    public String getTreatment_given() {
        return treatment_given;
    }

    public void setTreatment_given(String treatment_given) {
        this.treatment_given = treatment_given;
    }
}
