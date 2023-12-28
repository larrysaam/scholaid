package school;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentData {

    PreparedStatement ps;
    Connection conn = DBconnector.getConnect();
    ArrayList<Student> studentlist = new ArrayList<Student>();


    public StudentData(ArrayList<Student> studentlist){
        this.studentlist = studentlist;
    }


    public ArrayList<Student> getClassList(String classe, String section, String acadyear) throws SQLException{
        String tablename = classe+"_"+acadyear;

        String sql = "SELECT * FROM "+ tablename + " WHERE sequence=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, "1");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            studentlist.add(new Student(rs.getString("matricule"), rs.getString("name")));
        }

        return studentlist;
        
        
    }
    
}


// String message = "{\n" + 
// "\"tablename\": \"Form1AA_2023_2024\", \r\n" +  
// "\"matricule\": \"ictuieo\", \r\n" +
// "\"name\": \"paul\", \r\n" +  
// "\"sequence\": \"1\"\r\n" +  
// "\n}";