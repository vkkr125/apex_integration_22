package com;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class JavaClient {
    static PartnerConnection connection = null;
    public static void doLogin(){
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername("vicky.kumar.vkkr125@mtxb2b.com");
        config.setPassword("vkkr125@");
        try {
            connection = Connector.newConnection(config);
            System.out.println("Connection successfull !!!");
            System.out.println("End Point : " + config.getAuthEndpoint());
            System.out.println("Service End Point : " + config.getServiceEndpoint());
            System.out.println("SessionId : " + config.getSessionId());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Not able to connect .... ");
        }
    }
    public static String queryContacts(){
        QueryResult qr = null;
        boolean moreResults = true;
        String contactId = "";
        String queryString = "SELECT Id,FirstName,LastName,Email FROM Contact";

        // se the batch size , ont the connection objet, specify a value
        // for QueryOptions when using WSC
        connection.setQueryOptions(250);
        try {
            qr = connection.query(queryString);
            while(moreResults){
                for(SObject obj : qr.getRecords()){
                    contactId = obj.getId();
                    String res = obj.getField("FirstName") + " " + obj.getField("LastName") + " " + obj.getField("Email") + " " + contactId;
                    System.out.println(res);
                }
                if(qr.isDone()){
                    moreResults = false;
                }else{
                    qr = connection.queryMore(qr.getQueryLocator());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactId;
    }
    public static void updateContact(String conId){
        SObject records [] = new SObject[1];
        SObject con = new SObject();
        con.setType("Contact");
        con.setId(conId);
        con.setField("LastName", "Smith");
        con.setField("Email", "smith@abc.com");
        
        records[0] = con;
        try {
            connection.update(records);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        doLogin();
        String conId = queryContacts();
        if(conId != null){
            System.out.println("Contact Id : " + conId);
        }
        updateContact(conId);

    }
}
