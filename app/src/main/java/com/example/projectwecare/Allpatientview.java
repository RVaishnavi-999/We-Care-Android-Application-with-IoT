package com.example.projectwecare;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Allpatientview {
    private String fname;
    private String lname;
    private String email;
    private  String patient_id;
    private String illness;
    private  String careunit;
    private  String ward;
    private String treating_dr_name;
    private String emergencyphone;
    private String emergencyname;
    private String contactnumbet;

    public Allpatientview() {
    }

    public Allpatientview(String fname, String lname, String email, String patient_id, String illness,
                          String careunit, String ward, String treating_dr_name,
                          String emergencyphone, String emergencyname, String contactnumbet) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.patient_id = patient_id;
        this.illness = illness;
        this.careunit = careunit;
        this.ward = ward;
        this.treating_dr_name = treating_dr_name;
        this.emergencyphone = emergencyphone;
        this.emergencyname = emergencyname;
        this.contactnumbet = contactnumbet;
    }

    public String getContactnumbet() {
        return contactnumbet;
    }

    public void setContactnumbet(String contactnumbet) {
        this.contactnumbet = contactnumbet;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getCareunit() {
        return careunit;
    }

    public void setCareunit(String careunit) {
        this.careunit = careunit;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getTreating_dr_name() {
        return treating_dr_name;
    }

    public void setTreating_dr_name(String treating_dr_name) {
        this.treating_dr_name = treating_dr_name;
    }

    public String getEmergencyphone() {
        return emergencyphone;
    }

    public void setEmergencyphone(String emergencyphone) {
        this.emergencyphone = emergencyphone;
    }

    public String getEmergencyname() {
        return emergencyname;
    }

    public void setEmergencyname(String emergencyname) {
        this.emergencyname = emergencyname;
    }
}






