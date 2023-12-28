package school;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainpageController {

    @FXML
    private AnchorPane centerpane;

    @FXML
    private VBox detailBox;

    @FXML
    private VBox detailBox2;

    @FXML
    private VBox detailBox3;

    @FXML
    private Label engStudents;

    @FXML
    private Label engTeachers;

    @FXML
    private Label freTeachers;

    @FXML
    private Label freStudents;


    PreparedStatement ps;
    Connection conn = DBconnector.getConnect();


    public void initialize() throws SQLException{
        countteachers();
    }


    public void countteachers() throws SQLException{
        int i = 0;
        int j = 0;
        String qry = "SELECT * FROM Teacher WHERE section = ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, "english");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            i++;
        }

        ps.setString(1, "french");
        ResultSet rs1 = ps.executeQuery();
        while(rs1.next()){
            j++;
        }

        freTeachers.setText(Integer.toString(j) + " Teachers");
        engTeachers.setText(Integer.toString(i) + " Teachers");
    }

}


