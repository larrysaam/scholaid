package school;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;


public class SubjectModel {

    boolean successful;
    String API_URL = "https://us-east-1.aws.data.mongodb-api.com/app/scholaidapp-kjzfi/endpoint/";

    //Mongodb connection 
    String uri = "mongodb://Larrien:zxcvbnm123456@cluster0.u7xnpo6.mongodb.net/?retryWrites=true&w=majority";
    MongoClientURI clienturi = new MongoClientURI(uri);
    MongoClient mongoClient = new MongoClient(clienturi);

    MongoDatabase mdb = mongoClient.getDatabase("databasename");

    Connection conn = DBconnector.getConnect();
    PreparedStatement ps;

    Mongo mongo = new Mongo();


    public void registerSubject( String name, String section, String acadyear) throws SQLException{
        String sql = "SELECT * FROM Subject WHERE  name = ? AND acadyear =? AND Section =?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, acadyear);
        ps.setString(3, section);
        ResultSet rs = ps.executeQuery();
        int i = 0;
        while(rs.next()){
            i++;
        }

        if(i>0){
            //  message subject aleady exist
        }else{
            String qry = "INSERT INTO Subject(name, Section, acadyear) VALUES(?,?,?)";
                ps = conn.prepareStatement(qry);
                ps.setString(1, name);
                ps.setString(2, section);
                ps.setString(3, acadyear);
                ps.execute();
        }
    }
    
    public void registerClassSubject(String classe, String name, String section, String acadyear) throws SQLException{
        String sql = "SELECT * FROM Class_Subject WHERE class = ? AND subject = ? AND acadyear =?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, classe);
        ps.setString(2, name);
        ps.setString(3, acadyear);
        ResultSet rs = ps.executeQuery();
        int i = 0;
        while(rs.next()){
            i++;
        }

        if(i>0){
            //  message subject aleady exist
        }else{
            String qry = "INSERT INTO Class_Subject(class, subject, section, acadyear) VALUES(?,?,?,?)";
            ps = conn.prepareStatement(qry);
            ps.setString(1, classe);
            ps.setString(2, name);
            ps.setString(3, section);
            ps.setString(4, acadyear);
            ps.execute();
        }
        
    }

    public void removeClassSubject(String classe, String name, String acadyear) throws SQLException{
        String qry = "DELETE FROM Class_Subject WHERE class = ? AND subject =? AND acadyear =? ";
        ps = conn.prepareStatement(qry);
        ps.setString(1, classe);
        ps.setString(2, name);
        ps.setString(3, acadyear);
        ps.execute();
    }



    public boolean Mongodb_register_ClassSubject(String classe, String name, String section, String acadyear) throws SQLException, ParseException{


        /*
         * 
         * HTTP request to register ClassSubject.
         * 
         * 
         */


        String message = "{\n" + 
        "\"class\": \"%s\", \r\n" + 
        "\"subject\": \"%s\", \r\n" + 
        "\"section\": \"%s\", \r\n" +  
        " \"acadyear\": \"%s\"\n}";

        String body = String.format(message, classe, name, section, acadyear);

        try {
            mongo.POSTReq(API_URL+"registerclassSubject", body);
            successful = true;
        } catch (IOException e) {
            successful = false;
        }
        return successful;

        // Document doc = new Document();
        // doc.append("class", classe);
        // doc.append("subject", name);
        // doc.append("section", section);
        // doc.append("acadyear", acadyear);
        // mongoDatabase.getCollection("Class_Subject").insertOne(doc);
    }


    public void delete_subject(String name, String section, String acadyear) throws SQLException{
        String qry = "DELETE FROM Subject WHERE name = ? AND Section =? AND acadyear = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, name);
        ps.setString(2, section);
        ps.setString(3, acadyear);
        ps.execute();

        System.out.println(name +"______deleted____");
    
    }

 
    public String get_subject_id(String name, String section, String acadyear) throws SQLException{
        String id="";
        String qry2 = "SELECT id FROM Subject WHERE name = ? AND Section = ? AND acadyear = ?";
        ps = conn.prepareStatement(qry2);
        ps.setString(1, name);
        ps.setString(2, section);
        ps.setString(3, acadyear);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            id = rs.getString("id");
        }

        return id;
    }

 
    public boolean mongo_delete_subject(String name, String section, String acadyear) throws SQLException, ParseException{
        /*
         * 
         * HTTP request to delete subject.
         * 
         * 
         */
        String id = get_subject_id(name, section, acadyear);


        String message = "{\n" + 
        "\"id\": \"%s\"\n}";

        String body = String.format(message, id);


        try {
            mongo.POSTReq(API_URL+"deletesubject", body);
            successful = true;
        } catch (IOException e) {
            successful = false;
        }
        return successful;

    }


    public boolean addcolumnMarksheet(String tablename, String columnname) throws SQLException, IOException, ParseException{

        //add a column to every monogodb file in the collection
        // Document queryAll = new Document();
        // Document newvalue = new Document(columnname,"");
        // Document update = new Document("$set", newvalue);
        // mongoDatabase.getCollection(tablename).updateMany(queryAll, update);
        /*
         * 
         * HTTP request to add column to marksheet.
         * 
         * 
         */

         
         String message = "{\n" + 
         "\"tablename\": \"%s\", \r\n" +  
         "\"columnname\": \"%s\"\n}";
 
         String body = String.format(message, tablename, columnname);
        
        try {
            mongo.POSTReq(API_URL+"addcolumnMarksheet", body);
            successful = true;
        } catch (IOException e) {
            successful = false;
        }
       

        if(successful){
            //add column to SQlite marksheet
            String qry = "ALTER TABLE "+ tablename +" ADD " + columnname + " VARCHAR(4)";
            ps = conn.prepareStatement(qry);
            ps.execute();   
        }else{
            
        }

        return successful;

    }


    public boolean removecolumnMarksheet(String tablename, String columnname) throws SQLException, ParseException{

        //remove a column to every monogodb file in the collection
        // Document queryAll = new Document();
        // Document newvalue = new Document(columnname,"");
        // Document update = new Document("$unset", newvalue);
        // mongoDatabase.getCollection(tablename).updateMany(queryAll, update);


        /*
         * 
         * HTTP request to add column to marksheet.
         * 
         * 
         */


        String message = "{\n" + 
         "\"tablename\": \"%s\", \r\n" +  
         "\"columnname\": \"%s\"\n}";

        String body = String.format(message, tablename, columnname);

        try {
            mongo.POSTReq(API_URL+"removecolumnMarksheet", body);
            successful = true;
        } catch (IOException e) {
            successful = false;
        }

        if(successful){
            //remove column to SQlite marksheet
            String qry = "ALTER TABLE "+ tablename +" DROP COLUMN "+ columnname;
            ps = conn.prepareStatement(qry);
            ps.execute();   
        }else{
            //alert message
        }
        
        return successful;

   }
}
