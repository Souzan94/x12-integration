/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.as2.client;

/**
 *
 * @author souzan
 */
public class Utils {

    private static Utils instance;

    public String results;
    public String patientInfo;
    public String insuranceInfo;
    public String patientinsured;
    public String addNewInsurance;
    public String  MessageSent;
    public String mrnInfo;
    public String  saveMessage;
    public String updateRecord;
    public String currentDateTime;

    public Utils() {

        //---------------------------------------
        results = "select top 20 ACCUMED_PATIENT_ID ,accumed_patient_insurance_id from \n" +
"                  ACCUMED_PATIENT_INSURANCE \n" +
"                 where  X12_PROCEED =0";
        currentDateTime="SELECT  CONVERT(varchar,GETDATE() ,112)  AS CurrentDate ,SUBSTRING(CONVERT(varchar,GETDATE() ,108),1,5) as currentTime";
         updateRecord="update ACCUMED_PATIENT_INSURANCE set X12_PROCEED =2 where accumed_patient_insurance_id= ?";
        
        patientInfo = "select  patient_name,patient_surname,CONVERT(varchar,Date_of_birth,112) as [Date_of_birth],EMIRATES_ID,gender_id from accumed_patient \n" +
"          			\n" +
"              			where  accumed_patient_id= ?";
      
        insuranceInfo = "select CONVERT(varchar,start_date,112) as [start_date] ,CONVERT(varchar,end_date,112) as [end_date] ,policy_name,insurance_name,insurance_lisence from accumed_patient_insurance where  accumed_patient_insurance_id= ?";
        
        mrnInfo="select top 1 mrn ,facility_license from ACCUMED_PATIENT_FACILITY_MRN where accumed_patient_id= ?";

      //  patientinsured = "select  top 1 member_id, accumed_patient_id   from accumed_haad_claim_line where EMIRATES_ID= ?";
      
        MessageSent="update ACCUMED_PATIENT_INSURANCE set X12_PROCEED=1 where accumed_patient_insurance_id= ?";
        saveMessage="insert into ACCUMED_X12_SENT_MESSAGES (msg_content,MSG_DATE,IS_PROCESSED) values (?,?,?)";
        

    }

    public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                instance = new Utils();
            }
        }
        return instance;
    }

}
