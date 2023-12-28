package school;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SubjectsController {
    @FXML
    private Button addBtn;

    @FXML
    private HBox classContainer;

    @FXML
    private TextField classfield;

    @FXML
    private Button closebtn;

    @FXML
    private HBox container1;

    @FXML
    private HBox container2;

    @FXML
    private HBox container3;

    @FXML
    private HBox container4;

    @FXML
    private HBox container5;

    @FXML
    private HBox container6;

    @FXML
    private HBox container7;

    @FXML
    private HBox container8;

    @FXML
    private Label header1;

    @FXML
    private Label hearder2;

    @FXML
    private VBox popupbox;

    @FXML
    private Button saveBtn;


    //variables
    String acadyear;
    String section;
    String classe;
    String table;
    boolean connected;
    boolean successful;

    //SQLite connection
    PreparedStatement ps;
    Connection conn = DBconnector.getConnect();

    //Arrays and Objects
    ArrayList<String> classList = new ArrayList<String>();
    ArrayList<String> subjectList = new ArrayList<String>();
    Section sectionData = new Section();
    AlertBox alert = new AlertBox();
    SubjectModel subjectModel = new SubjectModel();



    
    public void initialize() throws SQLException{
        section = sectionData.getSection();
        acadyear = sectionData.SetAcademicYear();
        classe = sectionData.getFirstClass(section, acadyear);
        table = classe + "_" + acadyear;
        System.out.println("______"+table);

        insertclasses();
        showallSubjects(section, true, false);
        addsubjectBox();
        
    }



    @FXML
    void addClass(ActionEvent event) {
        popupbox.setVisible(true);
    }



    @FXML
    void saveClass(ActionEvent event) throws SQLException {
        String newclass = classfield.getText();
        if(newclass.isEmpty()){
            alert.alertInfo("field is empty");
        }else{
            sectionData.setClass(newclass, section);
            insertclasses();
            popupbox.setVisible(false);
        }
    }


    @FXML
    void closepopup(ActionEvent event) {
        popupbox.setVisible(false);

    }



    

    // function checks if device is connected to the internet
    public boolean checkInternet(){
        URL url;
        try {
            url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            connected = true;

        } catch (Exception e) {
            connected = false; 
        }
        return connected;
    }



    //create classbox which represent each class.
    public void Classbox(String name){
        Button form = new Button(name);
        form.setId(name);

        form.setOnMouseClicked(e->{

            if(name == "All"){
                try {
                    
                    showallSubjects(section, true, false);
                    addsubjectBox();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }else{
                try {
                    classe = name;
                    showallSubjects(section, false, true);
                    showSubject();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                
            }
            // scroll to selected class and change box color 

            // get list of subjects from this class
            form.setStyle("-fx-background-color:  #D1F6FF; -fx-background-radius: 10 ; -fx-text-fill: #0033ff; ");
           
            
        });

        form.setMinHeight(30);
        form.setMinWidth(100);

        form.setStyle("-fx-background-color:   #f8fafc; -fx-background-radius: 10 ; -fx-text-fill: black; ");
        form.setFont(new Font("Arial", 14));

        classContainer.getChildren().add(form);
    }


    //create subjectbox which represent each class.
    public HBox Subjectbox(String name, String section, boolean x_visibility, boolean selected){
        
        HBox box1 = new HBox();
        HBox box2 = new HBox();
        HBox box3 = new HBox();
        Label subject = new Label(name);
        Label delete = new Label("X");


        box1.setId(name);
        delete.setId(name);

        if(x_visibility){
            delete.setVisible(true);
        }else{
            delete.setVisible(false);
        }

        box1.setOnMouseClicked(e->{
            // get list of subjects from this class
            if(x_visibility & !selected){
                // do nothing
            }
            else if(!x_visibility & selected){
                checkInternet();
                if(!connected){

                    //add subject to database
                    try {
                        //add subject to mongodb and sqlite classMarksheet
                        successful = subjectModel.addcolumnMarksheet(table, name);
                        if(successful){
                            successful =  subjectModel.Mongodb_register_ClassSubject(classe, name, section, acadyear);
                            if(successful){
                                subjectModel.registerClassSubject(classe, name, section, acadyear);
                                showSubject();
                            }else{
                                alert.alertInfo("An error occured. Verify connection and try again!");
                            }
                        }else{
                            alert.alertInfo("An error occured. Verify connection and try again!");
                        }
                    } catch (SQLException | IOException | ParseException e1) {

                        e1.printStackTrace();
                    }
                    
                }else{
                    //alert no internet connection
                    alert.alertInfo("no internet connection. connect and try again!");
                }

            }
            
        });
        delete.setOnMouseClicked(e->{
            if(!connected){
                if(selected){
                    //remove colum from sqlite table and mongodb collection
                    try {
                        successful = subjectModel.removecolumnMarksheet(table, name);
                        if(successful){
                            subjectModel.removeClassSubject(classe, name, acadyear);
                            //reload subject list
                            showallSubjects(section, false, true);
                            showSubject();
                        }else{
                            alert.alertInfo("An error occured. Verify connection and try again!");
                        }
                    } catch (SQLException | ParseException e1) {
                        e1.printStackTrace();
                    }
                    
                }
                else if(!selected){
                
                    //delete subject from database
                    try {
                        subjectModel.delete_subject(name, section, section);
                        
                        // subjectModel.mongo_delete_subject(name, section, section);
    
                        //reload subject list
                        showallSubjects(section, true, false);
                        addsubjectBox();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                   
                    
                }
            }else{
                //alert no internet connection
                alert.alertInfo("no internet connection. connect and try again!");
            }
           
            
        });

        box1.setMinHeight(40);
        box1.setMinWidth(180);
        box1.setPrefWidth(180);
        box2.setMinHeight(40);
        box2.setMinWidth(20);
        box2.setPrefWidth(20);
        box3.setMinHeight(40);
        box3.setMinWidth(140);
        box3.setPrefWidth(140);
        delete.setVisible(x_visibility);

        box1.setAlignment(Pos.CENTER_RIGHT);
        box2.setAlignment(Pos.CENTER);
        box3.setAlignment(Pos.CENTER);

        HBox.setMargin(delete, new Insets(0, 20, 0, 0));

        box1.setStyle("-fx-background-color: gray; -fx-background-radius: 20; ");
        delete.setFont(Font.font("Arial",FontWeight.BOLD, 16));
        delete.setTextFill(Color.WHITE);
        subject.setFont(Font.font("Arial", 16));
        subject.setTextFill(Color.WHITE);

        box3.getChildren().add(subject);
        box2.getChildren().add(delete);
        box1.getChildren().addAll(box3, box2);

        return box1;

    }


    //create list of classes
    public void insertclasses() throws SQLException{
        //first remove all classes
        classContainer.getChildren().clear();

        //select all classes from SQlite db
        String sql = "SELECT * FROM Class WHERE Section =? AND acadyear=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, section);
        ps.setString(2, acadyear);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            classList.add(rs.getString("classname"));
        }

        //add classes to classContainer in UI
        Classbox("All");
        
        int len = classList.size();
        for(int i=1; i<=len; i++){
            System.out.println("_______________________________________");
            Classbox(classList.get(i-1));
        }

    }




    public void showallSubjects(String section, boolean x_visibility, boolean selected ) throws SQLException{

        header1.setText("All Subjects");
        hearder2.setText("New Subject");

        container1.getChildren().clear();
        container2.getChildren().clear();
        container3.getChildren().clear();
        container4.getChildren().clear();
        container5.getChildren().clear();
        container6.getChildren().clear();
        container7.getChildren().clear();
        container8.getChildren().clear();

        subjectList.clear();
       

        //select all subjects from SQlite db
        String sql = "SELECT * FROM Subject WHERE Section =? AND acadyear = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, section);
        ps.setString(2, acadyear);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            subjectList.add(rs.getString("name"));
        }


        //display all subjects
        int len = subjectList.size();
        for(int i=1; i<=len; i++){
            String name = subjectList.get(i-1);
            HBox subjectbox = Subjectbox(name, section, x_visibility, selected);
            if(i<=5){
                container1.getChildren().add(subjectbox);
            }
            else if(i>5 && i<=10){
                container2.getChildren().add(subjectbox);
            }
            else if(i>10 && i<=15){
                container3.getChildren().add(subjectbox);
            }
            else if(i>15 && i<=20){
                container4.getChildren().add(subjectbox);
            }
        }
    }


    public void addsubjectBox() throws SQLException{
        TextField name = new TextField();
        Button addBtn = new Button("Add");
        
        //add new subject in the subject table
        addBtn.setOnMouseClicked(e->{            
            try {
                String subjectname = name.getText();
                if(subjectname.isEmpty()){
                    alert.alertInfo("Field is empty, enter a subject name");
                }else{
                    subjectModel.registerSubject(subjectname, section, acadyear);
                }

                showallSubjects(section, true, false);
                addsubjectBox();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            
        });

        name.minHeight(40);
        name.setPrefHeight(50);
        name.minWidth(200);
        name.setPrefWidth(200);
        name.setStyle("-fx-background-radius: 20;");
        name.setFont(Font.font("Arial", 16));

        addBtn.minHeight(50);
        addBtn.setPrefHeight(50);
        addBtn.minWidth(150);
        addBtn.setPrefWidth(150);
        addBtn.setStyle("-fx-background-radius: 20;  -fx-background-color:linear-gradient(to bottom, #61d8de, #e839f6); -fx-text-fill: White;");
        addBtn.setFont(Font.font("Arial",FontWeight.BOLD, 16));

        container5.getChildren().addAll(name, addBtn);
    }


    public void showSubject() throws SQLException{
        header1.setText("All Subjects");
        hearder2.setText(classe + " Subjects");
       

        container5.getChildren().clear();
        container6.getChildren().clear();
        container7.getChildren().clear();
        container8.getChildren().clear();

        subjectList.clear();

        //select all subjects from SQlite db
        String sql = "SELECT * FROM Class_Subject WHERE class = ? AND Section =? AND acadyear = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, classe);
        ps.setString(2, section);
        ps.setString(3, acadyear);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            subjectList.add(rs.getString("subject"));
        }


        //display all subjects
        int len = subjectList.size();
        for(int i=1; i<=len; i++){
            String name = subjectList.get(i-1);
            HBox subjectbox = Subjectbox(name, section, true, true);
            if(i<=5){
                container5.getChildren().add(subjectbox);
            }
            else if(i>5 && i<=10){
                container6.getChildren().add(subjectbox);
            }
            else if(i>10 && i<=15){
                container7.getChildren().add(subjectbox);
            }
            else if(i>15 && i<=20){
                container8.getChildren().add(subjectbox);
            }
        }
    }    

}
