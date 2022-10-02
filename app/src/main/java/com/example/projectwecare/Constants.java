package com.example.projectwecare;

import java.util.HashMap;

public class Constants {

    //SIGN IN AND SIGN UP CONSTANTS
    public static final String KEY_AUTHUSER_ID ="userid";

    public static final String KEY_ADMINNAME ="name";
    public static final String KEY_ADMIN_IS_SIGNED_IN ="isSignedIn";
    public static final String KEY_ADMINPHONE ="phonenum";
    public static final String KEY_ADMINADD ="address";
    public static final String KEY_ADMINTYPE ="type";
    public static final String KEY_ADMINIMG ="imgpro";
    public static final String KEY_ADMINID ="aid";

    public static final String KEY_COLLECTION_USERS ="users";
    public static final String KEY_NAME ="name";
    public static final String KEY_EMAIL ="email";
    public static final String KEY_PASSWORD ="password";
    public static final String KEY_PREFERENCE_NAME ="chatAppPreference";
    public static final String KEY_IS_SIGNED_IN ="isSignedIn";
    public static final String KEY_USER_ID ="userID";
    public static final String KEY_IMAGE ="image";
    public static final String KEY_DESIGNATION = "type";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERID = "userid";


    public static final String KEY_USERNAME1 = "username";
    public static final String KEY_USERID1 = "userid";
    public static final String KEY_USERLLASTNAMEP = "lastnamep";
    public static final String KEY_PPHONE = "p_phone";
    public static final String KEY_USEREMAIL = "useremail";

    public static final String KEY_COLLECTION_PID ="users";

    public static final String KEY_FCM_TOKEN = "fcmToken";

    public static final String KEY_USER = "username";
    public static final String KEY_EMAIL1 = "useremail";
    public static final String KEY_DESIGNATION1 = "designation";
    public static final String KEY_IS_SIGNED_IN1 = "usersignedin";
    public static final String KEY_USER_ID1 = "userid";
    public static final String KEY_NAME1 = "name1";
    public static final String KEY_IMAGE1 = "image1";
    public static final String KEY_PATIENTID = "pid";
    public static final String KEY_PATIENTDOA = "pdoa";
    public static final String KEY_PATIENTDOT = "pdot";
    public static final String KEY_PATIENTAGE = "page";
    public static final String KEY_PATIENTNUM = "pnum";
    public static final String KEY_PATIENTEMAIL = "pemail";
    public static final String KEY_PATIENTSEX = "psex";
    public static final String KEY_PATIENTDOC = "pdoc";
    public static final String KEY_PATIENTNAME = "pname";
    public static final String KEY_PATIENTDIAGNOSIS = "pdiagnosis";
    public static final String KEY_PATIENTPREEXIST = "preexist";
    public static final String KEY_PATIENTTREATMENT = "ptreatment";
    public static final String KEY_PATIENTCONDITION = "pcondition";
    public static final String KEY_PATIENTADVICE = "padvice";
    public static final String KEY_PATIENTFOLLOWP = "pfollowup";
    public static final String KEY_PATIENTTEST1 = "ptest1";
    public static final String KEY_PATIENTTEST2 = "ptest2";
    public static final String KEY_SHIFTEND = "shiftend";
    public static final String KEY_SHIFTID = "shiftid";
    public static final String KEY_SHIFTYPE = "shifttype";
    public static final String KEY_SHIFTCURRENT = "shiftcurrent";
    public static final String KEY_SHIFTHOLIDAY = "shiftholiday";

    public static final String KEY_PATIENTIDIOT = "pid";
    public static final String KEY_PATIENTNAMEIOT = "pname";
    public static final String KEY_PATIENTCAREUNITIOT = "pcare";

    public static final String KEY_COLLECTION_CHAT ="chat";
    public static final String KEY_SENDER_ID ="senderId";
    public static final String KEY_RECEIVED_ID ="receiverId";
    public static final String KEY_MESSAGE ="message";
    public static final String KEY_TIMESTAMP ="timestamp";

    public static final String KEY_COLLECTION_CONVERSATION = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";

    public static final String KEY_AVAILABILITY = "availability";

    public static final String KEY_MSG_AUTHORIZATION = "Authorization";
    public static final String KEY_MSG_CONTENT_TYPE = "Content-Type";
    public static final String KEY_MSG_DATA = "data";
    public static final String KEY_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String,String> remotemsgheader = null;

    public static HashMap<String ,String> getRemoteMsgHeader(){
        if(remotemsgheader == null){
            remotemsgheader = new HashMap<>();
            remotemsgheader.put(KEY_MSG_AUTHORIZATION,"AAAAnPDCt1s:APA91bEtO-HM57z5Ep0Ajj-xnHNCPkzST9F8IgP2hAE4L40ei1GQv2Ch7dbvNn-J4OudMKkgVrEXKGtDAPu_6kRpzxuqYa4RGa67SUo5JESq_DrZUHF1Uu8pIid2Rdp-Dhq-JNC9hwmF");
            remotemsgheader.put(KEY_MSG_CONTENT_TYPE,"application/json");
        }
       return remotemsgheader;
    }

}
