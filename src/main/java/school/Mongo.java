package school;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Mongo {    

    public JSONArray API_getResults(String API_URI, String messageContent) throws IOException, ParseException{
        URL url = new URL(API_URI);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        try(OutputStream os = conn.getOutputStream()){
            byte[] input = messageContent.getBytes("utf-8");

            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8")
        )){
            StringBuilder response = new StringBuilder();
            String responseLine = null;

            while((responseLine = br.readLine()) != null){
                response.append(responseLine.trim());
            }
            int code = conn.getResponseCode();

            // JSONParser paser = new JSONParser();
            // JSONArray jObject = (JSONArray)paser.parse(response.toString());
            // JSONObject j = (JSONObject)paser.parse( jObject.get(0).toString());

            JSONParser paser = new JSONParser();
            JSONArray jObject = (JSONArray)paser.parse(response.toString());
           

            return jObject;
        }
    }



    public void POSTReq(String API_URI, String messageContent) throws IOException, ParseException{
        URL url = new URL(API_URI);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        try(OutputStream os = conn.getOutputStream()){
            byte[] input = messageContent.getBytes("utf-8");

            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8")
        )){
            StringBuilder response = new StringBuilder();
            String responseLine = null;

            while((responseLine = br.readLine()) != null){
                response.append(responseLine.trim());
            }
            int code = conn.getResponseCode();

            // JSONParser paser = new JSONParser();
            // JSONArray jObject = (JSONArray)paser.parse(response.toString());
            // JSONObject j = (JSONObject)paser.parse( jObject.get(0).toString());

            
        }
    }


    public static void main(String[] args) throws IOException, ParseException{
        String msg = "{\r\n" + //
                "    \"_id\": \"6543782dfb0737c1126815f4\",\r\n" + //
                "    \"matricule\": \"123456789\",\r\n" + //
                "    \"name\": \"mr.john\",\r\n" + //
                "    \"class\": \"form1AA\",\r\n" + //
                "    \"section\": \"english\",\r\n" + //
                "    \"acadyear\": \"2023_2024\"\r\n" + //
                "}";
        Mongo mongo = new Mongo();
        mongo.POSTReq(null, msg);
    }
}
  

    
