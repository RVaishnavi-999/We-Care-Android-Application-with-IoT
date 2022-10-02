package com.example.projectwecare;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Alldrview {

    private String encodedImage;
    private String fullname;
    private  String userid;
    private String speciality;
    private  String designation;
    private  String service_yeras;
    private String contactnum;
    private String email;


    public Alldrview() {
    }

    public Alldrview(String encodedImage, String fullname, String userid, String speciality, String designation,
                     String service_yeras, String contactnum, String email) {

        this.encodedImage = encodedImage;
        this.fullname = fullname;
        this.userid = userid;
        this.speciality = speciality;
        this.designation = designation;
        this.service_yeras = service_yeras;
        this.contactnum = contactnum;
        this.email = email;
    }

    public String getencodedImage()
    {
        return encodedImage;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUserid() {
        return userid;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getDesignation() {
        return designation;
    }

    public String getService_yeras() {
        return service_yeras;
    }

    public String getContactnum() {
        return contactnum;
    }

    public String getEmail() {
        return email;
    }

    public void setencodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setService_yeras(String service_yeras) {
        this.service_yeras = service_yeras;
    }

    public void setContactnum(String contactnum) {
        this.contactnum = contactnum;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
