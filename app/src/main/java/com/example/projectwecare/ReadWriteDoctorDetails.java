package com.example.projectwecare;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ReadWriteDoctorDetails {
    public String  encodedImage, fullname, speciality, service_yeras, designation, contactnum, userid, email, pwd;
    public ReadWriteDoctorDetails() { };

    public ReadWriteDoctorDetails(String encodedImage, String textFullname, String textSpeciality, String textServiceyears, String textDesignation,
                                  String textContactNumber, String textUserid, String textEmail, String textPwd)
    {
        this.encodedImage = encodedImage;
        this.fullname = textFullname;
        this.speciality = textSpeciality;
        this.service_yeras = textServiceyears;
        this.designation = textDesignation;
        this.contactnum = textContactNumber;
        this.userid = textUserid;
        this.email = textEmail;
        this.pwd = textPwd;
    }


    public String getencodedImage(){
        return encodedImage;
    }

    public void setencodedImage(String imageUrl) {
        this.encodedImage = encodedImage;
    }

    public String getFullname() {
        return fullname;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getService_yeras() {
        return service_yeras;
    }

    public String getDesignation() {
        return designation;
    }

    public String getContactnum() {
        return contactnum;
    }

    public String getUserid() {
        return userid;
    }

    public String getEmail() {
        return email;
    }


    //Edit dr profile only eithout profile field
/*
    public ReadWriteDoctorDetails(String textFullname, String textServiceyears, String textContactNumber)
    {
        this.fullname = textFullname;
        this.service_yeras = textServiceyears;
        this.contactnum = textContactNumber;

    } */
}
