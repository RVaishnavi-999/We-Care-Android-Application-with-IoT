package com.example.projectwecare;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Allnurseview {
    private String encodedImage;
    private String fname;
    private String lname;
    private  String nurseID;
    private String dutyshift;
    private String contactnum;
    private String email;


    public Allnurseview() {
    }

    public Allnurseview(String encodedImage, String fname, String lname, String nurseID, String dutyshift,
                        String contactnum, String email) {
        this.encodedImage = encodedImage;
        this.fname = fname;
        this.lname = lname;
        this.nurseID = nurseID;
        this.dutyshift = dutyshift;
        this.contactnum = contactnum;
        this.email = email;
    }

    public String getencodedImage() {
        return encodedImage;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getNurseID() {
        return nurseID;
    }

    public String getDutyshift() {
        return dutyshift;
    }

    public String getContactnum() {
        return contactnum;
    }

    public String getEmail() {
        return email;
    }
}
