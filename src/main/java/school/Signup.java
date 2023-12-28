
package school;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.time.ZoneId;

import org.json.simple.parser.ParseException;



public class Signup {

    //variables
    private String school_name;
    String school_contact;
    String proprietor_name;
    String proprietor_contact;
    String location;
    int i = 0 ;
    String message;
    boolean successful;

    //sqlite connections
    public Boolean connec_check = false;
    PreparedStatement preparedStatement;
    Connection conn = DBconnector.getConnect();

    //Mongodb connection 
    String API_URL = "https://us-east-1.aws.data.mongodb-api.com/app/scholaidapp-kjzfi/endpoint/";

    //objects
    Year current_year = Year.now(ZoneId.systemDefault());
    Mongo mongo = new Mongo();


    public String getSchool_name(){
        return school_name;
    }

    public void setSchool_name(String school_name){
        this.school_name = school_name;
    }


    // ******************checking internet connection**************
    public Boolean check_connection(){
        URL url;
        try {
            url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            connec_check = true;

        } catch (Exception e) {
            connec_check = false;
            
        }
        return connec_check;
    }


    // create database of the whole system
    public void create_db() throws SQLException{
        String query = "CREATE DATABASE IF NOT EXISTS scholai";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.execute();
    }

    // create signup table
    public void create_signup_table() throws SQLException{

        
        String query = " CREATE TABLE IF NOT EXISTS School("+
                        "school_key             VARCHAR (40) PRIMARY KEY ,"+
                        "school_name            VARCHAR (40) NOT NULL,"+
                        "school_contact         VARCHAR (40),"+
                        "proprietor_name        VARCHAR (40) NOT NULL,"+
                        "proprietor_contact     VARCHAR (40),"+
                        "location               VARCHAR (40) NOT NULL,"+
                        "is_new                  INT  )";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.execute();

        //create Mongodb collection if internet is available
        connec_check = check_connection();
        if(connec_check == true){
            //create Mongodb collection
        }else{
            //
        }
        
     
    }


    // create all the table of the system
    public void create_all_empty_tables() throws SQLException{

        // create current year enrollment table.
        String sql1 = "CREATE TABLE IF NOT EXISTS Class("+
                        "class_id               VARCHAR (40) PRIMARY KEY,"+
                        "classname              VARCHAR (40),"+
                        "Section                VARCHAR (40),"+
                        "acadyear               VARCHAR (40))";
                        

        String sql2 = "CREATE TABLE IF NOT EXISTS Section("+
                        "id                VARCHAR (40) PRIMARY KEY,"+
                        "name              VARCHAR (40),"+
                        "type              VARCHAR (40))";

        String sql3 = "CREATE TABLE IF NOT EXISTS Sequence("+
                        "id                VARCHAR (40) PRIMARY KEY,"+
                        "name              VARCHAR (40),"+
                        "type              VARCHAR (40))";

        // teacher, class and subject link
         String sql4 = "CREATE TABLE IF NOT EXISTS TCS_link("+
                        "TCS_id               VARCHAR (40) PRIMARY KEY,"+
                        "matricule            VARCHAR (40),"+
                        "class                VARCHAR (40),"+
                        "subject              VARCHAR (40),"+
                        "acadyear             VARCHAR (40))";

         String sql5 = "CREATE TABLE IF NOT EXISTS Class_Subject("+
                        "id                  VARCHAR (40) PRIMARY KEY,"+
                        "class               VARCHAR (40),"+
                        "subject             VARCHAR (40),"+
                        "Section             VARCHAR (40),"+
                        "acadyear            VARCHAR (40))";

         String sql6 = "CREATE TABLE IF NOT EXISTS Subject("+
                        "id                     VARCHAR (40) PRIMARY KEY,"+
                        "name                   VARCHAR (40),"+
                        "Section                VARCHAR (40),"+
                        "acadyear               VARCHAR (40))";
                
        String sql7 = "CREATE TABLE IF NOT EXISTS Teacher("+
                        "matricule              VARCHAR (40) PRIMARY KEY,"+
                        "name                   VARCHAR (40),"+
                        "class                  VARCHAR (40),"+
                        "Section                VARCHAR (40),"+
                        "acadyear               VARCHAR (40))";
        String sql8 = "CREATE TABLE IF NOT EXISTS PageSelected("+
                        "name                   VARCHAR (40))";


        preparedStatement = conn.prepareStatement(sql1);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql2);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql3);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql4);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql5);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql6);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql7);
        preparedStatement.execute();
        preparedStatement = conn.prepareStatement(sql8);
        preparedStatement.execute();
    }


    // insert variables to database
    public boolean insert(String key, String school_name, String school_contact, String proprietor_name, String propietor_contact, String location) throws SQLException{
        String query = "INSERT INTO School(school_key,school_name,school_contact,proprietor_name,proprietor_contact,location, is_new)"+
                        "VALUES(?,?,?,?,?,?,?)";
        preparedStatement = conn.prepareStatement(query);
         preparedStatement.setString(1, key);
        preparedStatement.setString(2, school_name);
        preparedStatement.setString(3, school_contact);
        preparedStatement.setString(4, proprietor_name);
        preparedStatement.setString(5, propietor_contact);
        preparedStatement.setString(6, location);
        preparedStatement.setInt(7, 1);

        return preparedStatement.execute();

    }

    

    public boolean Cloudinsert(String key, String school_name, String school_contact, String proprietor_name, String propietor_contact, String location) throws ParseException{

        check_connection();
        if(connec_check ){
            //cloud insertion
        //    Document doc = new Document("school_key", key);
        //    doc.append("school_name", school_name);
        //    doc.append("school_contact", school_contact);
        //    doc.append("proprietor_name", proprietor_name);
        //    doc.append("proprietor_contact", propietor_contact);
        //    doc.append("location", location);
        //    doc.append("is_new", 1);

        //    MongoCollection<Document> collection = mongoDatabase.getCollection("School");
        //    collection.insertOne(doc);

        //    valid = 1;

        /*
         * 
         * HTTP request to signup school.
         * 
         * 
         */
 
            String message = "{\n" + 
            "\"school_key\": \"%s\", \r\n" +  
            "\"school_name\": \"%s\", \r\n" + 
            "\"school_contact\": \"%s\", \r\n" + 
            "\"proprietor_name\": \"%s\", \r\n" + 
            "\"proprietor_contact\": \"%s\", \r\n" + 
            "\"location\": \"%s\", \r\n" + 
            "\"is_new\": \"1\" \n}";

            String body = String.format(message, key,school_name, school_contact, proprietor_name, propietor_contact,location);

    
            try {
                mongo.POSTReq(API_URL+"signupSchool", body);
                successful = true;
            } catch (IOException e) {
                successful = false;
            }

        }else{
            successful = false;
        }

        return successful;
       
    }




    public Boolean verify_account(String school_name, String school_contact) throws SQLException{
        String query = "SELECT * FROM School WHERE school_name = ? OR school_contact = ?";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, school_name);
        preparedStatement.setString(2, school_contact);
        ResultSet verify_result = preparedStatement.executeQuery();

        int i = 0;
        while(verify_result.next()){
            i = i+1;
        }
        if(i > 0){
            return false;
        }else{
            return true;
        }

    }



    public boolean Cloud_verify_account(String school_name, String school_contact) throws SQLException, IOException, ParseException{

        check_connection();
        if(connec_check ){
            /*
             * 
             * HTTP request to verify if school already exists
             * if status == SUCCESS then grant access
             * 
             * 
            */
            String message = "{\n" + 
            "\"school_name\":  \"%s\", \r\n" +  
            "\"school_contact\": \"%s\"\n}";


            String body = String.format(message, school_name, school_contact);
           
            try {
                int size = mongo.API_getResults(API_URL+"loginSchool", body).size();
                if(size>0){
                    successful = true;
                }else{
                    successful = false;
                }
            } catch (IOException e) {
                successful = false;
            }

            
            

            // MongoCollection<Document> collection = mongoDatabase.getCollection("school");
            // // Create the query
            // Document query = new Document();
            // query.append("SchoolName", school_name);
            // query.append("SchoolContact", school_contact);
            
            // // Execute the query
            // Document result = collection.find(query).first();
            // // Check if the result is not null  
       }else{
        successful = false;
       }

       return successful;
        
    }




    public boolean validate_signin(String school_name,  String school_key) throws SQLException{
        String query = "SELECT * FROM School WHERE school_name = ? AND school_key = ?";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, school_name);
        preparedStatement.setString(2, school_key);
        ResultSet verify_result = preparedStatement.executeQuery();

        int i = 0;
        while(verify_result.next()){
            i = i+1;
        }
        if(i > 0){
            return true;
        }else{
            return false;
        }
    }


    // error message is only for the signup controller
    public String error_message(){
        if (check_connection() == false){
            message = "The is no internet connection, check your connection and try again";
        }else{
        }
        return message;
    }

}
