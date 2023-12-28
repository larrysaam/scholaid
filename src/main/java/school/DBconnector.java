package school;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DBconnector {

    private static Connection connection ;
    
    public static Connection getConnect (){
    try {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection("jdbc:sqlite:scholai.db");
    } catch (SQLException ex) {
        Logger.getLogger(DBconnector.class.getName()).log(Level.SEVERE, null, ex);
    }
        
        return connection;
    }


}

