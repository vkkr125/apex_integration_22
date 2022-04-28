package com;

import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Error;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class JavaClient {
    // create an class variable to store the connection information 
    public static EnterpriseConnection connection = null;

    // define all the mehods  -> CRED operations
    // define a method to log into SF
    public static void doLogin(){
        // create an instance of ConnectorConfig, set username and password
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername("vicky.kumar.vkkr125@mtxb2b.com");
        config.setPassword("vkkr125@");
        try {
            connection = Connector.newConnection(config);
            System.out.println("Connected Successfully!!");
        } catch (Exception e) {
           System.out.println("Could Not Connect ... " + e.getMessage());
        }
    }
    // define a method to fetch and display records from Account ->void
    public static void queryAccountRecords(){
        String query = "SELECT Id,Name,AnnualRevenue FROM Account";
        try {
            // qr = queryMore(qury); and qr.getQueryLocator() used to get the records in batches if there is large amount of data

            // connection.query() generate the data in the salesforce but data will be fetched to the local machine heap by calling getRecords method
            QueryResult qr = connection.query(query);
            for(SObject obj : qr.getRecords()){
                Account acc = (Account)obj;
                System.out.println(acc.getName() + " ---- " + acc.getAnnualRevenue());
            }

        
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
    // define a method to insert a record - Account -> returns Id
    public static String insertAccountRecord(){
        SObject records[] = new SObject[1];
        Account acc = new Account();
        String accId = null;

        acc.setName("Sales PT Partner");
        acc.setAnnualRevenue(5000000.0);
        acc.setBillingCity("Jaipur");
        acc.setBillingCountry("India");
        records[0] = acc;
        try {
            SaveResult []srArray = connection.create(records);
            for(SaveResult sr : srArray){
                if(sr.isSuccess()){
                    accId = sr.getId();
                    System.out.println("Account has been created successfully !!!" + sr.getId());
                }else{
                    for(Error err : sr.getErrors()){
                        System.out.println(err.getMessage());
                    }
                }
            }
            return accId;
        } catch (ConnectionException e) {
            e.printStackTrace();
            return accId;
        }
    }
    // define a method to update an account record based on Id ->void
    public static void updateAccountRecord(String accId,Double annualRevenue){
        SObject records[] = new SObject[1];
        Account acc = new Account();
        acc.setId(accId);
        acc.setAnnualRevenue(annualRevenue);
        records[0] = acc;
        try {
           SaveResult []srArray= connection.update(records);
           for(SaveResult sr : srArray){
               if(sr.isSuccess()){
                   System.out.println("Account has been update successfully!!!");
               }else{
                   for(Error err : sr.getErrors()){
                       System.out.println(err.getMessage());
                   }
               }
           }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        
    }
    // define a method to delete an account record based on Id -> void
    public static void deleteAccountRecord(String accId){

    }

    public static void main(String[] args) {
        doLogin();
        queryAccountRecords();

        System.out.println("before insertion ...........");
        String accId = insertAccountRecord();
        System.out.println("after insertion ..........." + accId);
       // queryAccountRecords();
        if(accId != null){
            updateAccountRecord(accId, 3000000.0);
            System.out.println("Records has been update successfully !!!!");
        }else{
            System.out.println("Records has not been updated");
        }
    }
}