package com.example.projectwecare;

public class ReadWriteNurseDetails {
    public String  encodedImage, fname, lname, dob, gender, contactnum, address, dutyshift, nurseID, email, pwd;

    public ReadWriteNurseDetails() { };


    public ReadWriteNurseDetails(String encodedImage, String textFname, String textLname, String textDob, String textGender,
                                  String textCon_num, String textAddress, String textDutyshift, String textNurseid, String textEmail, String textPwd)
    {
        this.encodedImage = encodedImage;
        this.fname = textFname;
        this.lname = textLname;
        this.dob = textDob;
        this.gender = textGender;
        this.contactnum = textCon_num;
        this.address = textAddress;
        this.dutyshift = textDutyshift;
        this.nurseID = textNurseid;
        this.email= textEmail;
        this.pwd = textPwd;
    }

    public String getencodedImage() {
        return encodedImage;
    }

    public void setencodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getContactnum() {
        return contactnum;
    }

    public String getAddress() {
        return address;
    }

    public String getDutyshift() {
        return dutyshift;
    }

    public String getNurseID() {
        return nurseID;
    }

    public String getEmail() {
        return email;
    }
}

