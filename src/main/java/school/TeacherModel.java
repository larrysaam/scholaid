package school;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.UUID;

import org.json.simple.parser.ParseException;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TeacherModel {

    boolean successful;

    //Mongodb connection 
    String API_URL = "https://us-east-1.aws.data.mongodb-api.com/app/scholaidapp-kjzfi/endpoint/";


    //SQLite connection
    Connection conn = DBconnector.getConnect();
    PreparedStatement ps;

    //Objects
    ArrayList<Teacher> TeacherList = new ArrayList<Teacher>();
    ArrayList<String> ids = new ArrayList<String>();
    Mongo mongo = new Mongo();



    // contructor
    public TeacherModel(ArrayList<Teacher> TeacherList){
        this.TeacherList = TeacherList;
    }


    //add teachers details to arraylist
    public ArrayList<Teacher> getTeacherList(String classe, String section, String acadyear) throws SQLException{

        String qry = "SELECT * FROM Teacher WHERE acadyear = ?  AND class= ? AND Section = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, acadyear);
        ps.setString(2, classe);
        ps.setString(3, section);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            TeacherList.add(new Teacher(rs.getString("matricule"), rs.getString("name")));
        }

        return TeacherList;
    }




    // UNIUE IDENTIFICATION NUMBER
    public String UIN(){
        UUID uuid = UUID.randomUUID();
        String UIN = uuid.toString();

        return UIN;
    }
    

    public void registerTeacher(String teachername, String classe, String section, String acadyear) throws SQLException{
        String matricule = UIN();
        int i =0;

        //verify if teacher with name already exist
        String qry2 = "SELECT * FROM Teacher WHERE name=?";
        ps =conn.prepareStatement(qry2);
        ps.setString(1, teachername);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            i++;
        }
        if(i>0){

            //if teacher with name already exist, maintain name and add a number at the end of name
            String qry = "INSERT INTO Teacher(matricule, name, class, Section, acadyear) VALUES(?,?,?,?,?)";
            ps =conn.prepareStatement(qry);
            ps.setString(1, matricule);
            ps.setString(2, teachername + Integer.toString(i));
            ps.setString(3, classe);
            ps.setString(4, section);
            ps.setString(5, acadyear);
            ps.execute();
        }else{
            //if name is new, simply save name into table
            String qry = "INSERT INTO Teacher(matricule, name, class, Section, acadyear) VALUES(?,?,?,?,?)";
            ps =conn.prepareStatement(qry);
            ps.setString(1, matricule);
            ps.setString(2, teachername);
            ps.setString(3, classe);
            ps.setString(4, section);
            ps.setString(5, acadyear);
            ps.execute();
        }

    }


    public String getTCS_id(String teachername, String classe, String section, String acadyear) throws SQLException{
        String mat = "";
        String id ="";
        String qry2 = "SELECT matricule FROM Teacher WHERE name=?";
        ps =conn.prepareStatement(qry2);
        ps.setString(1, teachername);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            mat = rs.getString("matricule");
        }

        String qry1 = "SELECT TCS_id FROM TCS_link WHERE matricule=? AND class=?  AND acadyear=?";
        ps =conn.prepareStatement(qry1);
        ps.setString(1, mat);
        ps.setString(2, classe);
        ps.setString(3, section);
        ps.setString(4, acadyear);
        ResultSet rs1 = ps.executeQuery();
        while(rs1.next()){
            id = rs1.getString("TCS_id");
        }

        return id;
        
    }


    public String getmatricule(String name) throws SQLException{
        String mat = "";
        String qry2 = "SELECT matricule FROM Teacher WHERE name=?";
        ps =conn.prepareStatement(qry2);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            mat = rs.getString("matricule");
        }
        return mat;
    }



    public boolean MongodbregisterTeacher(String name, String classe, String subject, String section, String acadyear) throws SQLException, ParseException{
        
        /*
         * 
         * HTTP request to register Teacher.
         * 
         * 
         */
        //get matricule
        String matricule = getmatricule(name);

        String message = "{\n" + 
        "\"matricule\": \"%s\", \r\n" +  
        " \"name\": \"%s\"\n}";

        String body = String.format(message, matricule, name);
        
        try {
            mongo.POSTReq(API_URL+"signup", body);
            successful =  true;
        } catch (IOException e) {
            successful = false;
        }

        return successful;
    

        // Document doc = new Document();
        // doc.append("matricule", matricule);
        // doc.append("name", name);
        // mongoDatabase.getCollection("Teacher").insertOne(doc);


        /*
         * 
         * HTTP request to register TCSLink
         * subject schould stay blank
         * 
         * 
         */


        // message = "{\n" + 
        // "\"TCS_id\": "+ id +", \r\n" +
        // "\"matricule\": "+ matricule +", \r\n" +
        // "\"classe\": "+ classe +", \r\n" +
        // "\"subject\": \"\", \r\n" +
        // "\"section\": "+ section +", \r\n" +
        // " \"acadyear\": "+ acadyear + "\n}";

        // mongo.POSTReq(API_URL+"registerTCSLink", message);



        // Document doc1 = new Document();
        // doc1.append("TCS_id", id);
        // doc1.append("matricule", matricule);
        // doc1.append("classe", classe);
        // doc1.append("subject", "");
        // doc1.append("section", section);
        // doc1.append("acadyear", acadyear);
        // mongoDatabase.getCollection("TCS_link").insertOne(doc1);
    }




    public boolean verifysubject(String matricule, String classe, String subject, String acadyear) throws SQLException{
        int i = 0;
        String query = "SELECT * FROM TCS_link WHERE subject = ? AND matricule = ? AND acadyear =? AND class=?";
        ps =conn.prepareStatement(query);
        ps.setString(1, subject);
        ps.setString(2, matricule);
        ps.setString(3, acadyear);
        ps.setString(4, classe);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            i++;
            
        }
        if(i>0){
            return true;
        }else{
            return false;
        }
    }


    public void assignSubject(String matricule, String classe, String subject, String acadyear) throws SQLException{
        boolean exist = verifysubject(matricule, classe, subject, acadyear);
        //insert only new subject to corresponding teacher matricule
        if(!exist){
            String qry = "INSERT INTO TCS_link(matricule, class, subject, acadyear) VALUES(?,?,?,?)";
            ps = conn.prepareStatement(qry);
            ps.setString(1, matricule);
            ps.setString(2, classe);
            ps.setString(3, subject);
            ps.setString(4, acadyear);
            ps.execute();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "subject already assigned to teacher", ButtonType.OK);
            alert.show();
        }
        
    }


    public boolean monodbAssignSubject(String name, String subject, String classe, String section, String acadyear) throws SQLException, ParseException{
        /*
         * 
         * HTTP request to update TCS_link to add subject.
         * 
         * 
         */

        String id = getmatricule(name);
        String message = "{\n" + 
        "\"matricule\": \"%s\", \r\n" +  
        "\"class\": \"%s\", \r\n" + 
        "\"subject\": \"%s\", \r\n" + 
        " \"acadyear\": \"%s\"\n}";

        String body = String.format(message, id, classe, subject, acadyear);

        try {
            mongo.POSTReq(API_URL+"AssignSubject", body);
            successful =  true;
        } catch (IOException e) {
            successful = false;
        }

        return successful;

    }


    public void deleteAssinedSubject(String matricule, String subject, String section, String classe, String acadyear) throws SQLException{
        String qry = "DELETE FROM TCS_link WHERE matricule = ? AND subject =? AND class= ? AND acadyear = ?";
            ps = conn.prepareStatement(qry);
            ps.setString(1, matricule);
            ps.setString(2, subject);
            ps.setString(3, classe);
            ps.setString(4, acadyear);
            ps.execute();
    }

    public ArrayList<String> getTCS_id(String mat, String classe, String acadyear) throws SQLException{
        
        String id = "";
        String qry1 = "SELECT TCS_id FROM TCS_link WHERE matricule=? AND class=? AND acadyear=?";
        ps =conn.prepareStatement(qry1);
        ps.setString(1, mat);
        ps.setString(2, classe);
        ps.setString(3, acadyear);
        ResultSet rs1 = ps.executeQuery();
        while(rs1.next()){
            id = rs1.getString("TCS_id");
            ids.add(id);
        }

        return ids;
    }


    public boolean mongodbDeletassignedSubject(String name, String subject, String classe, String section, String acadyear) throws SQLException, ParseException{

        /*
         * 
         * HTTP request to delete doc from TCLink.
         * 
         * 
         */

        String id = getTCS_id(name, classe, section, acadyear);
        String message = "{\n" + 
        "\"matricule\": \"%s\", \r\n" +  
        "\"class\": \"%s\", \r\n" + 
        "\"subject\": \"%s\", \r\n" + 
        " \"acadyear\": \"%s\"\n}";

        String body = String.format(message, id, classe, subject, acadyear);


        try {
            mongo.POSTReq(API_URL+"DeletassignedSubject", body);
            successful =  true;
        } catch (IOException e) {
            successful = false;
        }

        return successful;


    }


    public void deleteTeacherAssign(String matricule, String classe, String acadyear, String section) throws SQLException{
        String qry = "DELETE FROM TCS_link WHERE matricule = ? AND class= ? AND acadyear = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, matricule);
        ps.setString(2, classe);
        ps.setString(3, acadyear);
        ps.execute();


        String qry1 = "DELETE FROM Teacher WHERE matricule = ? AND class= ? AND acadyear = ?";
        ps = conn.prepareStatement(qry1);
        ps.setString(1, matricule);
        ps.setString(2, classe);
        ps.setString(3, acadyear);
        ps.execute();

    }

    public boolean mongoDeleteTeacherAssign(String matricule, String classe, String acadyear, String section) throws SQLException, ParseException{
        return false;

        // //delete from Teacher mongodb
        // mongoDatabase.getCollection("Teacher").deleteOne(((Document) Filters.eq("matricule", matricule)).append("class", classe).append("acadyear", acadyear));   

        // //delete from TCS_link mongodb
        // getTCS_id(matricule, classe, acadyear);
        // for(String id : ids){
        //     mongoDatabase.getCollection("TCS_link").deleteOne(Filters.eq("TCS_id", id));   
        // }
    }


    public boolean mongoeditTeacherName(String name, String matricule) throws SQLException, ParseException{
        /*
         * 
         * HTTP request to update Teacher name .
         * 
         * 
         */
        String message = "{\n" + 
        "\"matricule\": \"%s\", \r\n" +  
        " \"name\": \"%s\"\n}";
 
        String body = String.format(message, matricule, name);

        try {
            mongo.POSTReq(API_URL+"editTeacherName", body);
            successful =  true;
        } catch (IOException e) {
            successful = false;
        }

        return successful;
        
        
    }

    public void editTeacherName(String name, String matricule) throws SQLException{
        String qry2 = "UPDATE Teacher SET name = ? WHERE matricule =?";
        ps =conn.prepareStatement(qry2);
        ps.setString(1, name);
        ps.setString(2, matricule);
        ps.executeUpdate();
    }
}
