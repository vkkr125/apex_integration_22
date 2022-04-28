package com;

// import java.io.IOException;
// import java.io.UnsupportedEncodingException;

// import org.apache.http.HttpResponse;
// import org.apache.http.client.ClientProtocolException;
// import org.apache.http.client.HttpClient;
// import org.apache.http.client.methods.HttpPost;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class JavaClient {
    public static String USERNAME = "vicky.kumar.vkkr125@mtxb2b.com";
    public static String PASSWORD = "vkkr125@";
    public static String CLIENT_ID = "3MVG9pRzvMkjMb6kD0UH8SXJCXzCHinUxg4lGrgFG4sDHTqjdezg6Yn7D4w6Y09Anvh4pYgY9ib0fux0O7zo4";
    public static String CLIENT_SECRET = "AD568F9AC233F6395CFFE26867F1C7994072A87F805C51A5CC1C2D82E7635DE0";
    public static String OAUTH_ENDPOINT = "/services/oauth2/token";
    public static String LOGIN_INSTANCE_DOMAIN = "login.salesforce.com";
    public static String LOGIN_HOST_URI = "https://"+LOGIN_INSTANCE_DOMAIN+OAUTH_ENDPOINT;


    public static void doLogin(){
        // define string buffer to store credential details
        StringBuffer sb = new StringBuffer("grant_type=password");
        sb.append("&username=");
        sb.append(USERNAME);
        sb.append("&passwrod=");
        sb.append(PASSWORD);
        sb.append("&client_id=");
        sb.append(CLIENT_ID);
        sb.append("&client_secret=");
        sb.append(CLIENT_SECRET);

        try {
            StringEntity strEntity = new StringEntity(sb.toString());
            strEntity.setContentType("application/x-www-form-urlencoded");

            //Create an instance of HttpPost, so that will hit the server and get the token
            HttpPost post = new HttpPost(LOGIN_HOST_URI);
            post.setEntity(strEntity);

            // create an instance of HttpDefaultClient and call execute method
            HttpClient client = new DefaultHttpClient();
            HttpResponse responce =  client.execute(post);
            if(responce.getStatusLine().getStatusCode() == 200){
                String responce_string = EntityUtils.toString(responce.getEntity());
                JSONObject json = new JSONObject(responce_string);
                System.out.println("Hello");
                System.out.println(json.toString(1));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(".....Login using OAuth .... ");

    }
    public static void main(String[] args) {
        doLogin();
    }
}




// import org.apache.http.entity.StringEntity;
// import org.apache.http.impl.client.DefaultHttpClient;
// import org.apache.http.util.EntityUtils;
// import org.json.JSONException;
// import org.json.JSONObject;
 
// public class JavaClient {
 
// //    public static String USERNAME = "suresh@dev1.com";
// //    public static String PASSWORD = "Training1";
// //    public static String CLIENT_ID = "3MVG9pRzvMkjMb6mGOQ0RUxkyh0iYcDN0k1YRRpBXr.Oyy59dLd0Oq.DoJhdBjhrZ_t7A2Pz1hkHYt6XR77Y4";
// //    public static String CLIENT_SECRET = "4B5633BC205FF993ABDCF100D464D8632070EF9F19DD21290401F29D1D71BD85";
// //    public static String OAUTH_ENDPOINT = "/services/oauth2/token";
// //    public static String LOGIN_INSTANCE_DOMAIN = "login.salesforce.com";
// //    public static String LOGIN_HOST_URI = "https://"+LOGIN_INSTANCE_DOMAIN+OAUTH_ENDPOINT;


//     public static String USERNAME = "vicky.kumar.vkkr125@mtxb2b.com";
//     public static String PASSWORD = "vkkr125@";
//     public static String CLIENT_ID = "3MVG9pRzvMkjMb6kD0UH8SXJCXzCHinUxg4lGrgFG4sDHTqjdezg6Yn7D4w6Y09Anvh4pYgY9ib0fux0O7zo4";
//     public static String CLIENT_SECRET = "AD568F9AC233F6395CFFE26867F1C7994072A87F805C51A5CC1C2D82E7635DE0";
//     public static String OAUTH_ENDPOINT = "/services/oauth2/token";
//     public static String LOGIN_INSTANCE_DOMAIN = "login.salesforce.com";
//     public static String LOGIN_HOST_URI = "https://"+LOGIN_INSTANCE_DOMAIN+OAUTH_ENDPOINT;

  
 
//    public static void doLogin(){
 
//        //Define a StringBuffer to store credential details
//        StringBuffer sb = new StringBuffer("grant_type=password");
//        sb.append("&username=");
//        sb.append(USERNAME);
 
//        sb.append("&password=");
//        sb.append(PASSWORD);
 
 
//        sb.append("&client_id=");
//        sb.append(CLIENT_ID);
 
 
//        sb.append("&client_secret=");
//        sb.append(CLIENT_SECRET);
      
//        try {
//            StringEntity strEntity = new StringEntity(sb.toString());
//            strEntity.setContentType("application/x-www-form-urlencoded");
 
//            //Ceate an instance of HttpPost
//            HttpPost post = new HttpPost(LOGIN_HOST_URI);
//            post.setEntity(strEntity);
 
//            //Create an instance of HttpDefaultClient and call execute() method
//            HttpClient client = new DefaultHttpClient();
//            HttpResponse response =  client.execute(post);
 
//            if(response.getStatusLine().getStatusCode() == 200){
//                String response_string = EntityUtils.toString(response.getEntity());
//                JSONObject json = new JSONObject(response_string);
//                System.out.println(json.toString(1));
              
 
//            }
 
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
 
//        System.out.println(".....Login using OAuth.....");
 
 
//    }
//    public static void main(String[] args) {
//        doLogin();
//    }
// }
