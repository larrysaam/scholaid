package school;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class Section {

    //Mongodb connection 
    String API_URL = "https://us-east-1.aws.data.mongodb-api.com/app/scholaidapp-kjzfi/endpoint/";
    Mongo mongo = new Mongo();

    //sqlite connection
    Connection conn = DBconnector.getConnect();
    PreparedStatement ps;

    Calendar date = Calendar.getInstance();

    //variables
    int month = date.get(Calendar.MONTH)+1;
    int year = date.get(Calendar.YEAR);
    String acadyear;
    String Section_name;
    String sequence;
    String pagename;
    String className;


    public void createtable(String table) throws SQLException{
        String qry = "CREATE TABLE IF NOT EXISTS " + table +"("+
        "matricule              VARCHAR (40) PRIMARY KEY,"+
        "name                   VARCHAR (100),"+
        "sequence              VARCHAR (1))";
        ps = conn.prepareStatement(qry);
        ps.execute();
    }



    public void setSection(String name) throws SQLException{
        String qry = "INSERT INTO Section(name,type) VALUES(?,?)";
        ps = conn.prepareStatement(qry);
        ps.setString(1, name);
        ps.setString(2, "selected");
        ps.execute();
    }


    public String getSection() throws SQLException{
        String qry = "SELECT * FROM Section WHERE type = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, "selected");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Section_name = rs.getString("name");
        }

        return Section_name;
    }


    public void setClass(String classe, String section) throws SQLException{
        SetAcademicYear();
        String qry = "INSERT INTO Class(classname, Section, acadyear) VALUES(?,?,?)";
        ps = conn.prepareStatement(qry);
        ps.setString(1, classe);
        ps.setString(2, section);
        ps.setString(3, acadyear);
        ps.execute();


        // Document doc = new Document();
        // doc.append("classname", classe);
        // doc.append("section", section);
        // doc.append("acadyear", acadyear);
        // mongoDatabase.getCollection("Class").insertOne(doc);

        String table = classe+"_"+acadyear;

        createtable(table);
    }



    public void setSequence(String num) throws SQLException{
        String qry = "INSERT INTO Sequence(name, type) VALUES(?,?)";
        ps = conn.prepareStatement(qry);
        ps.setString(1, num);
        ps.setString(2, "selected");
        ps.execute();
    }


    public String getSequence() throws SQLException{
        String qry = "SELECT * FROM Sequence WHERE type = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, "selected");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){

                sequence = rs.getString("name");
        }

        return sequence;
    }


    
    public void setPage(String pagename) throws SQLException{
        String qry = "INSERT INTO PageSelected(name) VALUES(?)";
        ps = conn.prepareStatement(qry);
        ps.setString(1, pagename);
        ps.execute();
    }


    public String getPage() throws SQLException{
        String qry = "SELECT * FROM PageSelected";
        ps = conn.prepareStatement(qry);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            pagename = rs.getString("name");
            
        }

        return pagename;
    }


    public void setSelectedClass(String classname) throws SQLException{
        String qry = "INSERT INTO SelectedClass(name) VALUES(?)";
        ps = conn.prepareStatement(qry);
        ps.setString(1, classname);
        ps.execute();
    }


    public String getSelectedClass() throws SQLException{
        String qry = "SELECT * FROM SelectedClass ";
        ps = conn.prepareStatement(qry);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            className = rs.getString("name");
        }

        return className;
    }


    public String getFirstClass(String section, String acadyear) throws SQLException{
        String qry = "SELECT * FROM Class WHERE Section = ? and acadyear = ? ";
        ps = conn.prepareStatement(qry);
        ps.setString(1, section);
        ps.setString(2, acadyear);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            className = rs.getString("classname");
        }

        return className;
    }


    //evaluating value for the accademic year
    public String SetAcademicYear(){ 
        if(month>=9 & month<=12){
            acadyear = String.valueOf(year) + "_" + String.valueOf(year + 1);
        }else if(month>=1 & month<9){
            acadyear = String.valueOf(year - 1) + "_" + String.valueOf(year);
        }
        

        return acadyear;
    }




    public ResultSet getMarks(String tableName, String seqid) throws SQLException{
        String qry = "SELECT * FROM "+ tableName +" WHERE SequenceId = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, seqid);
        ResultSet rs = ps.executeQuery();

        return rs;
    }


    
     /** function takes data from mongodb and saves it in sqlite
     * restoration of data is done only for data that does exist in the sqlite DB table
     * @return 
     * @throws SQLException
     * @throws ParseException
     * @throws IOException
     * 
     */
    public boolean RestoreResults(String tableName) throws SQLException, ParseException, IOException{
        //execution value
        boolean done = true;
        //get mongodb collection
        // MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);

        String sql = "SELECT * FROM " + tableName;
        ps = conn.prepareStatement(sql);
        ResultSet SQLiteRs = ps.executeQuery();
        
        // get table metaData
        ResultSetMetaData SQLiteMetaData = SQLiteRs.getMetaData();

        //total column of the table
        int columnCount = SQLiteMetaData.getColumnCount();

        // Retrieve all documents from the collection
        // MongoCursor<Document> cursor = collection.find().iterator();
        /*
        * 
        * HTTP request to get all documents from a collection
        * open document and copy content to a SQLite database
        * 
        */

        String message = "{\n" + 
        "\"tablename\": \"%s\" \r\n}";

        String body = String.format(message, tableName);
        
        JSONParser paser = new JSONParser();
        JSONArray jObject = mongo.API_getResults(API_URL+"getallDoc", body);
        int size = jObject.size();
        int c = 0;

        while (size>=c) {

            //iterate through documents
            JSONObject j = (JSONObject)paser.parse( jObject.get(c).toString());
        

            String insertQuery = "INSERT INTO " + tableName +  "( ";
            for(int i=4; i<=columnCount; i++){
                //adding columns to the insert query
                insertQuery += (SQLiteMetaData.getColumnName(i) + ",");
            }
            insertQuery = insertQuery.substring(0, insertQuery.length() - 2);  // Remove the last comma and space
            insertQuery += ") VALUES(";

            // Get all keys of the document
            for (int i=4; i<=columnCount; i++) {
                // Get the value associated with the key
                Object value = j.get(SQLiteMetaData.getColumnName(i));
                //adding values to the insert query
                insertQuery += (String.valueOf(value) + " ,");
            }
            insertQuery = insertQuery.substring(0, insertQuery.length() - 2);  // Remove the last comma and space
            insertQuery += ")";

            //executing insertion query to sqlite
            ps = conn.prepareStatement(insertQuery);
            done = ps.execute();
        }

        return done;
    }


    public void getColumns(String tableName) throws SQLException{
        String sql = "SELECT * FROM " + tableName ;
        ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        // get table metaData
        ResultSetMetaData SQLiteMetaData = rs.getMetaData();

        //total column of the table
        int columnCount = SQLiteMetaData.getColumnCount();


        String qry = "SELECT * FROM " + tableName +  " WHERE sequence=?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, sequence);
        rs = ps.executeQuery();

        while(rs.next()){

            for(int i=1; i<=columnCount; i++){
                rs.getString(SQLiteMetaData.getColumnName(i));
            }

        } 

    }



}
