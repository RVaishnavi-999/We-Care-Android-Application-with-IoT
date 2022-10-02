package com.example.projectwecare;

public class UserPatients {
    String fname, lname, contactnumbet, email, gender, age, address,
            emergencyname, emergencyphone, relationship,
            patient_id, adm_date, adm_time, illness, careunit, ward, treating_dr_name, status;

    public UserPatients() {
    }

    public UserPatients(String fname, String lname, String contactnumbet, String email, String gender, String age, String address,
                        String emergencyname, String emergencyphone, String relationship, String patient_id, String adm_date, String adm_time, String illness, String careunit, String ward, String treating_dr_name, String status) {
        this.fname = fname;
        this.lname = lname;
        this.contactnumbet = contactnumbet;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.emergencyname = emergencyname;
        this.emergencyphone = emergencyphone;
        this.relationship = relationship;
        this.patient_id = patient_id;
        this.adm_date = adm_date;
        this.adm_time = adm_time;
        this.illness = illness;
        this.careunit = careunit;
        this.ward = ward;
        this.treating_dr_name = treating_dr_name;
        this.status = status;
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

    public String getContactnumbet() {
        return contactnumbet;
    }

    public void setContactnumbet(String contactnumbet) {
        this.contactnumbet = contactnumbet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyname() {
        return emergencyname;
    }

    public void setEmergencyname(String emergencyname) {
        this.emergencyname = emergencyname;
    }

    public String getEmergencyphone() {
        return emergencyphone;
    }

    public void setEmergencyphone(String emergencyphone) {
        this.emergencyphone = emergencyphone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getAdm_date() {
        return adm_date;
    }

    public void setAdm_date(String adm_date) {
        this.adm_date = adm_date;
    }

    public String getAdm_time() {
        return adm_time;
    }

    public void setAdm_time(String adm_time) {
        this.adm_time = adm_time;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}