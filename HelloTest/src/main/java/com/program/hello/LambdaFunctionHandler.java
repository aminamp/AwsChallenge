package com.program.hello;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;


/**
 * Entry point of Lambda function 
 * @author Amin
 *
 */
public class LambdaFunctionHandler implements RequestStreamHandler {
    
	/**
	 * create Json Parser objects to handle output and input
	 */
	JSONParser parser = new JSONParser();
	JSONObject resultJson = new JSONObject();
	
	
	 /**
     * 
     * @param input, input received from API Gateway HTTP Call
     * @param output, output stream to write result into user screen
     * @param context, retrieve runtime information of Lambda function, while it is running 
     */
	
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

  
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String responseCode = "200";
        
        //URL to access Uploaded data in ElasticSearch service
        String myUrl = "https://search-mydata-r6vvgk5ki2x5zpj6hndk6vfthy.us-east-2.es.amazonaws.com/plans/_search?q=";

        try {
            JSONObject event = (JSONObject)parser.parse(reader);
            
            //parse String Parameters received from API Gateway call
            if (event.get("queryStringParameters") != null) {
                JSONObject myParameter = (JSONObject)event.get("queryStringParameters");
                if (myParameter.get("planName") != null) {
                    String planName = (String)myParameter.get("planName");
                    myUrl += "PLAN_NAME:\"" + URLEncoder.encode(planName, "UTF-8")+"\"";
                }
                else if (myParameter.get("sponsorName") != null) {
                    String sponsorName = (String)myParameter.get("sponsorName");
                    myUrl += "SPONSOR_DFE_NAME:\"" + URLEncoder.encode(sponsorName, "UTF-8")+"\"";
                }
                else if (myParameter.get("sponsorState") != null) {
                    String sponsorLocState = (String)myParameter.get("sponsorState");
                    myUrl += "SPONS_DFE_LOC_US_STATE:\"" + URLEncoder.encode(sponsorLocState, "UTF-8")+"\"";
                }
            }

            StringBuffer result = new StringBuffer();
            URL temp = new URL(myUrl);
            
            //Make a request to ElasticSearch service to search inside dataset
            HttpURLConnection connect = (HttpURLConnection)temp.openConnection();
            connect.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String inputtemp;
            while((inputtemp = in.readLine()) != null){
                result.append(inputtemp);
            }


            //parse the result returned by ElasticSearch
            JSONParser myParser = new JSONParser();
            JSONObject resultTemp = (JSONObject)myParser.parse(result.toString());

            
            resultJson.put("statusCode", responseCode);
            resultJson.put("body", resultTemp.toString());

        } catch(ParseException pex) {
        		resultJson.put("statusCode", "400");
        		resultJson.put("exception", pex);
        }

        //write the result to output
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(resultJson.toJSONString());
        writer.close();
    }
}