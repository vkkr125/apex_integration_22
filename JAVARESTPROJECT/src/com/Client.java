package com;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This program demonstrates the following basic use cases for the REST API:
 * - authentication with OAuth 2.0 (This is for development purposes only. Not a
 * real implementation.)
 * - querying (using account records)
 * - inserting (using a contact record related to one of the retrieved account
 * records)
 * - updating (updates contact record added in previous step)
 *
 * @author salesforce training
 */
public class Client extends Object {

    // ---------REST and OAuth-------
    // Portions of the URI for REST access that are re-used throughout the code
    private static String OAUTH_ENDPOINT = "/services/oauth2/token";
    private static String REST_ENDPOINT = "/services/data";

    // Basic header information added to each HTTP object that is used
    // to invoke the REST API.
    private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");

    // ================Code starts here===================
    public static void main(String[] args) {
        new Client();
    }

    /**
     * Constructor drives console interaction and calls appropriate methods.
     */
    public Client() {
        RestConnectionHelper restConn = this.oauth2Login();
        if (restConn.baseUri != null) {
            // Retrieved accountId that is used when contact is added.
            String accountId = this.queryAccount(restConn);
            if (accountId != null) {
                // Id of inserted contact. Used to update contact.
                String contactId = this.insertContact(restConn, accountId);
                if (contactId != null) {
                    this.updateContact(restConn, contactId);
                } else {
                    System.out.println("Contact not found.");
                }
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    /**
     * This method connects the program to the Salesforce organization using OAuth.
     * It stores returned values for further access to organization.
     * 
     * @param userCredentials Contains all credentials necessary for login
     * @return
     */
    public RestConnectionHelper oauth2Login() {
        System.out.println("_______________ Login _______________");
        OAuth2Response oauth2Response = null;
        HttpResponse response = null;
        UserCredentials userCredentials = new UserCredentials();
        RestConnectionHelper restConn = new RestConnectionHelper();
        String loginHostUri = "https://" +
                userCredentials.loginInstanceDomain + OAUTH_ENDPOINT;

        try {
            // Construct the objects for making the request
            HttpClient httpClient = new DefaultHttpClient();
            // If you are behind a proxy server, uncomment
            // the next line of code after supplying values for your proxy server
            // httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new
            // HttpHost("myProxy.domain.com", 8088 /*this may need to change as well*/,
            // "http"));
            HttpPost httpPost = new HttpPost(loginHostUri);
            StringBuffer requestBodyText = new StringBuffer("grant_type=password");
            requestBodyText.append("&username=");
            requestBodyText.append(userCredentials.userName);
            requestBodyText.append("&password=");
            requestBodyText.append(userCredentials.password);
            requestBodyText.append("&client_id=");
            requestBodyText.append(userCredentials.consumerKey);
            requestBodyText.append("&client_secret=");
            requestBodyText.append(userCredentials.consumerSecret);
            StringEntity requestBody = new StringEntity(requestBodyText.toString());
            requestBody.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(requestBody);
            httpPost.addHeader(prettyPrintHeader);
            System.out.println("body is " + requestBodyText);
            // Make the request and store the result
            response = httpClient.execute(httpPost);

            // Parse the result if we were able to connect.
            if (response.getStatusLine().getStatusCode() == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject json = new JSONObject(response_string);
                    oauth2Response = new OAuth2Response(json);
                    System.out.println("JSON returned by response: +\n" + json.toString(1));
                } catch (JSONException je) {
                    je.printStackTrace();
                }
                restConn.baseUri = oauth2Response.instance_url + REST_ENDPOINT
                        + "/v" + userCredentials.apiVersion + ".0";
                restConn.oauthHeader = new BasicHeader("Authorization", "OAuth " +
                        oauth2Response.access_token);
                System.out.println("\nSuccessfully logged in to instance: "
                        + restConn.baseUri);
            } else {
                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println(getBody(response.getEntity().getContent()));
                System.exit(-1);
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return restConn;
    }

    /**
     * This method demonstrates
     * - How to use HTTPGet and a constructed URI to retrieve data from Salesforce.
     * - Simple parsing of a JSON object.
     */
    public String queryAccount(RestConnectionHelper restConn) {
        System.out.println("\n_______________ Account QUERY _______________");
        String accountId = null;
        try {
            // Set up the HTTP objects needed to make the request.
            HttpClient httpClient = new DefaultHttpClient();
            /********************************************************
             *   
             * todo:
             * Set the value of uri on the line below to the baseURI concatenated
             * with the query parameter and the query string.
             ********************************************************/
            String uri = restConn.baseUri + "/query?q=SELECT+id+,+name+FROM+Account+limit+2";
            System.out.println("Query URL: " + uri);
            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader(restConn.oauthHeader);
            httpGet.addHeader(prettyPrintHeader);

            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);

            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String response_string = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject json = new JSONObject(response_string);
                    System.out.println("JSON result of Query:\n" + json.toString(1));

                    /********************************************************
                     * todo:
                     * Add an assignment statement the line below
                     * to store the account id of the first object returned to
                     * use later when creating the Contact record.
                     ********************************************************/
                    accountId = json.getJSONArray("records").getJSONObject(0).getString("Id");
                    System.out.println("accountId value is " + accountId);
                } catch (JSONException je) {
                    je.printStackTrace();

                }
            } else {
                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return accountId;
    }

    /**
     * This method demonstrates
     * - How to use HTTPPost and a constructed URI to insert data into Salesforce.
     * - Simple creation of a JSON object.
     */
    public String insertContact(RestConnectionHelper restConn, String accountId) {
        System.out.println("\n_______________ Contact INSERT _______________");
        String contactId = null;
        /********************************************************
         * todo:
         * On the line below add code to the URI
         * to indicate the type of object that will be inserted into the database
         ********************************************************/
        String uri = restConn.baseUri + "/sobjects/Contact/";
        try {
            // create the JSON object containing the new contact details.
            JSONObject contact = new JSONObject();
            contact.put("LastName", "Chin");
            contact.put("FirstName", "Jasmine");
            contact.put("MobilePhone", "(415)222-3333");
            contact.put("Phone", "(650)123-3211");

            /********************************************************
             * todo:
             * On the line below add the key value pair for the accountId
             * to the JSON contact data.
             ********************************************************/
            contact.put("AccountId", accountId);
            System.out.println("JSON for contact record to be inserted:\n" + contact.toString(1));

            // Construct the objects needed for the request
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader(restConn.oauthHeader);
            httpPost.addHeader(prettyPrintHeader);
            // The message we are going to post
            StringEntity body = new StringEntity(contact.toString(1));
            body.setContentType("application/json");
            httpPost.setEntity(body);

            // Make the request
            HttpResponse response = httpClient.execute(httpPost);

            // Process the results
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 201) {
                String response_string = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(response_string);
                // Store the retrieved contact id to use when we update the contact.
                contactId = json.getString("id");
                System.out.println("New contact id from response: " + contactId);
            } else {
                System.out.println("Insertion unsuccessful. Status code returned is " + statusCode);
            }
        } catch (JSONException e) {
            System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return contactId;
    }

    /**
     * This method demonstrates
     * - How to use HTTPPatch and a constructed URI to update data in Salesforce.
     * NOTE: You have to create the HTTPPatch, as it does not exist in the standard
     * library.
     * - Simple creation of a JSON object.
     */
    public void updateContact(RestConnectionHelper restConn, String contactid) {
        System.out.println("\n_______________ Contact UPDATE _______________");

        // Notice, the id for the record to update is part of the URI, not part of the
        // JSON
        String uri = restConn.baseUri + "/sobjects/Contact/" + contactid;
        try {
            // Create the JSON object containing the updated contact phone number
            // and the id of the contact we are updating.
            JSONObject contact = new JSONObject();
            contact.put("Phone", "(415)555-1234");
            System.out.println("JSON for update of contact record:\n" + contact.toString(1));

            // Set up the objects necessary to make the request.
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPatch httpPatch = new HttpPatch(uri);
            httpPatch.addHeader(restConn.oauthHeader);
            httpPatch.addHeader(prettyPrintHeader);
            StringEntity body = new StringEntity(contact.toString(1));
            body.setContentType("application/json");
            httpPatch.setEntity(body);

            // Make the request
            HttpResponse response = httpClient.execute(httpPatch);
            // Process the response
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) {
                System.out.println("Updated the contact successfully.");
            } else {
                System.out.println("Contact update NOT successfully. Status code is " + statusCode);
            }
        } catch (JSONException e) {
            System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * Extend the Apache HttpPost method to implement an HttpPost
     * method.
     */
    private static class HttpPatch extends HttpPost {
        public HttpPatch(String uri) {
            super(uri);
        }

        public String getMethod() {
            return "PATCH";
        }
    }

    /**
     * This class is used to hold values returned by the OAuth request.
     */
    static class OAuth2Response {
        String id;
        String issued_at;
        String instance_url;
        String signature;
        String access_token;

        public OAuth2Response() {
        }

        public OAuth2Response(JSONObject json) {
            try {
                id = json.getString("id");
                issued_at = json.getString("issued_at");
                instance_url = json.getString("instance_url");
                signature = json.getString("signature");
                access_token = json.getString("access_token");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This class holds all the values related to the credentials needed to
     * make the OAuth2 request for authentication. Normally they would not be set in
     * this manner.
     */
    class UserCredentials {
        String loginInstanceDomain = "na1.salesforce.com";
        String apiVersion = "22";
        // ---------Credentials----------
        // Credentials providing access to a specific Salesforce organization.
        /********************************************************
         * todo:
         * 1. Enter Salesforce login userName
         * 2. Enter Salesforce login password
         * 3. Enter consumerKey
         * 4. Enter consumerSecret
         ********************************************************/
        String userName = "suresh.hn@lightning.com";
        String password = "Training2";
        String consumerKey = "3MVG9i1HRpGLXp.omzBOaVA3ORgTcoXo_jY6QIigq0x.rZRa5adpiyAXbwqSUkHZSGeBc6e4Xzz_yr3pRO2Lc";
        String consumerSecret = "5817891868633559522";
        String grantType = "password";
    }

    /**
     * This class holds information gained from login that are used in subsequent
     * calls.
     */
    class RestConnectionHelper {
        // Holds URI returned from OAuth call, which is then used throughout the code.
        String baseUri;

        // The oauthHeader set in the oauth2Login method, and then added to
        // each HTTP object that is used to invoke the REST API.
        Header oauthHeader;

    }

    // ==========utility methods=============
    /**
     * Utility method for changing a stream into a String.
     * 
     * @param inputStream
     * @return
     */
    private String getBody(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
                result += "\n";
            }
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
}
