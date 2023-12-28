package school;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.Document;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Backup {

 
    PreparedStatement sqliteps;
    Connection SQLiteconn = DBconnector.getConnect();

    //Mongodb connection 
    String uri = "mongodb://Larrien:zxcvbnm123456@cluster0.u7xnpo6.mongodb.net/?retryWrites=true&w=majority";
    MongoClientURI clienturi = new MongoClientURI(uri);
    MongoClient mongoClient = new MongoClient(clienturi);

    MongoDatabase mongoDatabase = mongoClient.getDatabase("databasename");

    Mongo mongo = new Mongo();




    public Boolean BackupAll() throws SQLException{

        boolean done = true;
        // Get a list of all tables in the source database
        Statement sourceStmt = SQLiteconn.createStatement();
        ResultSet rs = sourceStmt.executeQuery("SHOW TABLES");

        while (rs.next()) {
            
            String tableName = rs.getString(1);

            String sql = "SELECT * FROM " + tableName +  " WHERE is_new = 1";
            sqliteps = SQLiteconn.prepareStatement(sql);
            ResultSet SQLiteRs = sqliteps.executeQuery();


            
            // Create the table in the destination database
            ResultSetMetaData SQLiteMetaData = SQLiteRs.getMetaData();

             //get name of first 2 columns in Table
            String column1 = SQLiteMetaData.getColumnName(1);
            String column2 = SQLiteMetaData.getColumnName(2);
            int columnCount = SQLiteMetaData.getColumnCount();

            //create Mongodb collection
            mongoDatabase.createCollection(tableName);
            //checking if table creation was done successfully
            if(!done){
                // return false for error message to pop up
                return false; 
            }
            else if(done){

                //get value of first 2 columns of a record
                String value1 = SQLiteRs.getString(column1);
                String value2 = SQLiteRs.getString(column2);

                //creating collection documents and passing data to it
                while(SQLiteRs.next()){
                
                    Document doc = new Document(column1, SQLiteRs.getString(column1));
                    for(int i=1; i<columnCount; i++){
                        doc.append(SQLiteMetaData.getColumnName(i), SQLiteRs.getString(SQLiteMetaData.getColumnName(i)));
                    }
                    
                    MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
                    collection.insertOne(doc);

                    // Check if the document exists
                    done = mongoDatabase.getCollection(tableName).find(new Document(column1, SQLiteRs.getString(column1))) != null;
                    if(!done){
                        //alert message for internet error or Database error
                        return false;
                    }else{
                        //use cloumn1 and column2 to change is_new to 0 after successful backup
                        sql = "UPDATE " + tableName + " SET is_new = 0 WHERE is_new = 1 AND " + column1 +" = "+ value1 + " AND " + column2 + " = " + value2;
                        sqliteps = SQLiteconn.prepareStatement(sql);
                        sqliteps.executeUpdate();
                    }
                }   
            }   

        }
            
        // Close the connections and statements
        sqliteps.close();
        SQLiteconn.close();
        return done;
        
    }



    public void DeleteTale(String tablename) throws SQLException{
        MongoCollection<Document> collection = mongoDatabase.getCollection(tablename);
        BasicDBObject document = new BasicDBObject();
        collection.deleteMany(document);
    }


    /*
     * this function is used to backup any individual table
     * Class result/ Marksheet table cant be backup using this function.
     */
    public boolean BackupTable(String tableName) throws SQLException{

        boolean done = true;
        String sql = "SELECT * FROM " + tableName +  " WHERE is_new = 1";
        sqliteps = SQLiteconn.prepareStatement(sql);
        ResultSet SQLiteRs = sqliteps.executeQuery();
        

        //gets total number of columnd in the table
        ResultSetMetaData SQLiteMetaData = SQLiteRs.getMetaData();
        int columnCount = SQLiteMetaData.getColumnCount();

        //get name of first 2 columns in Table
        String column1 = SQLiteMetaData.getColumnName(1);
        String column2 = SQLiteMetaData.getColumnName(2);

        //create Mongodb collection
        mongoDatabase.createCollection(tableName);
        //checking if table creation was done successfully
        if(!done){
            // return false for error message to pop up
        }
        else if(done){
          
            //get value of first 2 columns of a record
            String value1 = SQLiteRs.getString(column1);
            String value2 = SQLiteRs.getString(column2);
            
            //creating collection documents and passing data to it
            while(SQLiteRs.next()){
            
                Document doc = new Document(column1, SQLiteRs.getString(column1));
                for(int i=1; i<columnCount; i++){
                    doc.append(SQLiteMetaData.getColumnName(i), SQLiteRs.getString(SQLiteMetaData.getColumnName(i)));
                }
                
                MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
                collection.insertOne(doc);

                 // Check if the document exists
                 done = mongoDatabase.getCollection(tableName).find(new Document(column1, SQLiteRs.getString(column1))) != null;
                if(!done){ 
                    //end
                }else{
                    //use cloumn1 and column2 to change is_new to 0 after successful backup
                    sql = "UPDATE " + tableName + " SET is_new = 0 WHERE is_new = 1 AND " + column1 +" = "+ value1 + " AND " + column2 + " = " + value2;
                    sqliteps = SQLiteconn.prepareStatement(sql);
                    sqliteps.executeUpdate();
            
                }
            }
        }            
        
        // Close the connections and statements
        
        sqliteps.close();
        SQLiteRs.close(); 
        SQLiteconn.close();
        return done;           
    }


    /** function takes data from mongodb and saves it in sqlite
     * restoration of data is done only for data that does exist in the sqlite DB table
     * @return 
     * @throws SQLException
     * 
     */
    public boolean RestoreResults(String tableName) throws SQLException{
        //execution value
        boolean done = true;
        //get mongodb collection
        MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);

        String sql = "SELECT * FROM " + tableName +  " WHERE is_new = 1";
        sqliteps = SQLiteconn.prepareStatement(sql);
        ResultSet SQLiteRs = sqliteps.executeQuery();
        
        // get table metaData
        ResultSetMetaData SQLiteMetaData = SQLiteRs.getMetaData();

        //total column of the table
        int columnCount = SQLiteMetaData.getColumnCount();

        // Retrieve all documents from the collection
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {

            //iterate through documents
            Document document = cursor.next();

            String insertQuery = "INSERT INTO " + tableName +  "( ";
            for(int i=1; i<=columnCount; i++){
                //adding columns to the insert query
                insertQuery += (SQLiteMetaData.getColumnName(i) + ",");
            }
            insertQuery = insertQuery.substring(0, insertQuery.length() - 2);  // Remove the last comma and space
            insertQuery += ") VALUES(";

            // Get all keys of the document
            for (String key : document.keySet()) {
                // Get the value associated with the key
                Object value = document.get(key);
                //adding values to the insert query
                insertQuery += (value + " ,");
            }
            insertQuery = insertQuery.substring(0, insertQuery.length() - 2);  // Remove the last comma and space
            insertQuery += ")";

            //executing insertion query to sqlite
            sqliteps = SQLiteconn.prepareStatement(insertQuery);
            done = sqliteps.execute();
        }

        return done;
    }


    
    public void mongodbInsertMarksheet(String tableName) throws IOException, ParseException, SQLException{
        String API_URL = "https://us-east-1.aws.data.mongodb-api.com/app/scholaidapp-kjzfi/endpoint/";
        String qry = "SELECT * FROM " +tableName;
        sqliteps = SQLiteconn.prepareStatement(qry);
        ResultSet rs = sqliteps.executeQuery();
        while(rs.next()){
            String matricule = rs.getString("matricule");
            String name = rs.getString("name");
            String sequence = rs.getString("sequence");

            String message = "{\n" + 
            "\"tablename\": \"%s\", \r\n" +  
            "\"matricule\": \"%s\", \r\n" +
            "\"name\": \"%s\", \r\n" +  
            "\"sequence\": \"%s\"\r\n" +  
            "\n}";

            String formate = String.format(message, tableName,matricule,name,sequence);
            
            mongo.POSTReq(API_URL+"insertmarksheet", formate);
        }
        
        

    }

}

// //“No organization ever created an innovation. People innovate, not companies.” - Seth Godi


