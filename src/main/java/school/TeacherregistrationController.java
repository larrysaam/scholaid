package school;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.parser.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TeacherregistrationController {

    @FXML
    private Button addBtn;

    @FXML
    private HBox classContainer;

    @FXML
    private Button closebtn;

    @FXML
    private Button importBtn;

    @FXML
    private VBox listContainer;

    @FXML
    private VBox popupbox;

    @FXML
    private Label popupname;

    @FXML
    private Button savebtn;

    @FXML
    private TextField subjectField;

    //Arrays
    ArrayList<String> classList = new ArrayList<String>();
    ArrayList<Teacher> TeacherList = new ArrayList<Teacher>();
    
    //Object
    Section sectionData = new Section();
    Backup backup = new Backup();
    AlertBox alert = new AlertBox();
    TeacherModel teacherModel = new TeacherModel(TeacherList);

    //SQLite connection
    Connection conn = DBconnector.getConnect();
    PreparedStatement ps;

    //variables
    String selected;
    String classe;
    String section;
    String acadyear;
    String table;
    boolean connected;
    String option;
    boolean successful;



    public void initialize() throws SQLException, FileNotFoundException{
        section = sectionData.getSection();
        classe = sectionData.getFirstClass(section, acadyear);
        acadyear = sectionData.SetAcademicYear();
        table = selected + "_" + acadyear;

        insertclasses(section);
        buildList();
    }


    @FXML
    void addTeacher(ActionEvent event) throws SQLException {
        popupbox.setVisible(true);
        checkInternet();
        if(!connected){
            popupname.setText("Teacher's Name");
           option = "addTeacher";
        }else{
            //alert no internet
            alert.alertInfo("no internet connection. connect and try again!");
        }
        
    }


    @FXML
    void addsubject(ActionEvent event) throws SQLException, ParseException, FileNotFoundException {

        //check internet connection before
        checkInternet();
        if(!connected){
            if(option == "addTeacher"){
                //check if field is empty
                subjectField.setText("");
                String teachername = subjectField.getText();
                if(!teachername.isEmpty()){
                    
                    //register Teacher infomation into "Teacher", "TCS_link" in mongodb and SQLite
                    successful = teacherModel.MongodbregisterTeacher(selected, classe, null, section, acadyear);
                    if(successful){
                        teacherModel.registerTeacher(teachername, classe, section, acadyear);
                    }else{
                        alert.alertInfo("An error occured, Check your connection and try again!");
                    }

                }else{
                    alert.alertInfo("please enter a valid name");
                }
                
            }else if(option == "addsubject"){
                
                // insert into mongodb TCS Clink
                String subject = subjectField.getText();
                successful = teacherModel.monodbAssignSubject(selected, subject, classe, section, acadyear);
                if(successful)
                    teacherModel.assignSubject(selected, classe, subject, acadyear);
                else
                    alert.alertInfo("An error occured, Check your connection and try again!");
    
            }else if(option == "edit"){

                // edit teacher's name
                String newname = subjectField.getText();
                successful = teacherModel.mongoeditTeacherName(newname, selected);
                if (successful) 
                    teacherModel.editTeacherName(newname, selected);
                else 
                    alert.alertInfo("");
                
            }
        }else{
            // alert no internet
            alert.alertInfo("no internet connection. connect and try again!");
        }

        popupbox.setVisible(false);
        initialize();
    }

    
    
    @FXML
    void closepopup(ActionEvent event) {
        popupbox.setVisible(false);
    }


    @FXML
    void importFile(ActionEvent event) throws SQLException, IOException {

        checkInternet();
        if(!connected){

            // Read the Excel file
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);

            //if approved button is clicked
            if(response == JFileChooser.APPROVE_OPTION){
                //get files absolute path from PC
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                FileInputStream inputStream = new FileInputStream(file);

                try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                    Sheet sheet = workbook.getSheetAt(0);

                    //clear whole sqlite table
                    String qry = "DELETE * FROM "+ table;
                    ps = conn.prepareStatement(qry);
                    ps.execute();

                    // Get the column names from the first row of the sheet
                    Row headerRow = sheet.getRow(0);
                    int numColumns = headerRow.getLastCellNum();
                    String[] columnNames = new String[numColumns];
                    
                    //also verify if column names are 'matricule' and 'studentname'
                    if(numColumns!=2){
                        //error message popup
                        alert.alertInfo("Invalide Table format. Matricule and Teacher,s name required only");
                    }else{

                        for (int i = 0; i < numColumns; i++) {
                            Cell cell = headerRow.getCell(i);
                            columnNames[i] = cell.getStringCellValue();
                        }        
        
                        // Insert the data into the database
                        int numRows = sheet.getLastRowNum();
                        for (int i = 1; i <= numRows; i++) {
        
                            Row row = sheet.getRow(i);
                            Cell cell1 = row.getCell(0);
                            Cell cell2 = row.getCell(1);
                        
                            // insert if matricule does not exist yet
                            String sql = "INSERT INTO Teacher(matricule, name, class, Section, acadyear) VALUES(?,?,?,?,?)";
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, cell1.getStringCellValue());
                            ps.setString(2, cell2.getStringCellValue());
                            ps.setString(3, classe);
                            ps.setString(4, section);
                            ps.setString(5, acadyear);
                            ps.execute();
                        }

                    }
                    
                }
        }
        //copy file data to nongodb collection
        backup.BackupTable(table);

        }else{
            //alert no internet
            alert.alertInfo("no internet connection. connect and try again!");
        }

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




    public HBox teacherBox(String matricule, String name) throws FileNotFoundException{
        HBox box = new HBox();
        Button edit = new Button();
        Button delete = new Button();
        Button add = new Button("+");
        Label mat = new Label(matricule);
        Label nameLabel = new Label(name);
        ImageView editImage = new ImageView();
        ImageView deleteImage = new ImageView();

        box.setId(matricule);


        //edit teacher's name
        edit.setOnMouseClicked(e->{
            checkInternet();
            if(!connected){
                selected = matricule;
                subjectField.setText(name);
                popupname.setText("Edit Name");
                option = "edit";
                popupbox.setVisible(true);
            }else{
                // alert no internet
                alert.alertInfo("no internet connection. connect and try again!"); 
            }
        
        });


        //delete teacher's record
        delete.setOnMouseClicked(e->{
            checkInternet();
            if(!connected){
                selected = matricule;
                try {
                    //try to delete teacher record on all docs and tables
                    successful = teacherModel.mongoDeleteTeacherAssign(matricule, classe, acadyear, section);
                    if(successful){
                        teacherModel.deleteTeacherAssign(matricule, classe, acadyear, section);
                        buildList();
                    } 
                    else{
                        alert.alertInfo("An error occured. check your connection and try again!");
                    }    
                } catch (SQLException | ParseException | FileNotFoundException e1) {
                    alert.alertInfo("An error occured. check your connection and try again!");
                }            
            }else{
                // alert no internet
                alert.alertInfo("no internet connection. connect and try again!"); 
            }
        
        });




        //add new subjects to teacher
        add.setOnMouseClicked(e->{
            checkInternet();
            if(!connected){
                selected = matricule;
                popupname.setText("Subject Name");
                option = "addsubject";
                subjectField.setText("");
                popupbox.setVisible(true);
            }else{
                // alert no internet
                alert.alertInfo("no internet connection. connect and try again!"); 
            }
           
        });

        box.minHeight(48);
        box.minWidth(1044);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 30, 0, 30));
        box.setSpacing(30);

        box.setStyle("-fx-background-color: White; -fx-border-color: gray;");

        mat.setPrefHeight(30);
        mat.setPrefWidth(150);
        mat.setStyle("-fx-background-color: gray; -fx-background-radius: 20;");
        mat.setAlignment(Pos.CENTER);
        mat.setTextFill(Color.WHITE);
        mat.setFont(Font.font("Ariel", FontWeight.BOLD, 14));

        nameLabel.setPrefHeight(30);
        nameLabel.setPrefWidth(150);
        nameLabel.setStyle("-fx-background-color:  #D1F6FF; -fx-border-radius: 20;  -fx-background-radius: 20;");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setTextFill(Color.valueOf("#0033ff"));
        nameLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 14));

        
        delete.setStyle("-fx-background-color:  none;");
        edit.setStyle("-fx-background-color:  none;");
        add.setStyle("-fx-background-color:  none;");

        add.setFont(Font.font("Ariel", FontWeight.BOLD, 20));

        //set image
        Image image;
       
            image = new Image(new FileInputStream("C:\\Users\\LARRIEN\\Desktop\\Portfolio\\Platform\\Project 01\\scholaid\\src\\main\\java\\school\\pen.png"));
            editImage.setImage(image);
            editImage.setFitHeight(30);
            editImage.setFitWidth(30);
    
            Image image1=new Image(new FileInputStream("C:\\Users\\LARRIEN\\Desktop\\Portfolio\\Platform\\Project 01\\scholaid\\src\\main\\java\\school\\delete-512.png"));
            
            deleteImage.setImage(image1);
            deleteImage.setFitWidth(25);
            deleteImage.setFitHeight(25);
        

        edit.setGraphic(editImage);
        delete.setGraphic(deleteImage);

        HBox.setMargin(delete, new Insets(0, 0, 0, -30));

        box.getChildren().addAll(mat, nameLabel, edit, delete, add);

        return box;
        
    }



    //create subjectbox which represent each class.
    public HBox subjectBox(String matricule, String name, String section){
        
        HBox box1 = new HBox();
        HBox box2 = new HBox();
        Label subject = new Label(name);
        Label delete = new Label("X");


        box1.setId(matricule);
        delete.setId(matricule);


        delete.setOnMouseClicked(e->{
            checkInternet();
            if(!connected){
                try {
                    teacherModel.deleteAssinedSubject(matricule, name, section, classe, acadyear);
                    //delete doc from mongodb TCS_link collection too
                    // teacherModel.mongodbDeletassignedSubject(name, name, classe, section, acadyear);
    
                    initialize();
                } catch (SQLException | FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                
            }else{
                //alert no internet
                alert.alertInfo("no internet connection. connect and try again!");
            }
           
        });

        box1.setMaxHeight(35);
        box1.setMinWidth(170);
        box2.setMaxHeight(35);
        box2.setMinWidth(60);

        box1.setAlignment(Pos.CENTER);
        box2.setAlignment(Pos.CENTER_RIGHT);

        box1.setStyle("-fx-background-color: white; -fx-background-radius: 20;  -fx-border-radius: 20; -fx-border-color: #0033ff");
        delete.setFont(Font.font("Ariel", FontWeight.BOLD, 15));
        delete.setTextFill(Color.RED);
        subject.setFont(Font.font("Ariel", 15));
        subject.setTextFill(Paint.valueOf("#0033ff"));

        box2.getChildren().add(delete);
        box1.getChildren().addAll(subject, box2);

        return box1;

    }


    public HBox subjectboxContainer(String matricule) throws SQLException{

        HBox box = new HBox();

        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(30);
        
        String qry = "SELECT * FROM TCS_link WHERE matricule = ? AND acadyear = ?  AND class= ?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, matricule);
        ps.setString(2, acadyear);
        ps.setString(3, classe);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            String subject = rs.getString("subject");
            if(subject.isEmpty()){
                //do nothing
            }else{
                box.getChildren().add(subjectBox(matricule, subject, section));
            }
        }

        return box;

    }


    public void buildList() throws SQLException, FileNotFoundException{

        TeacherList.clear();
        listContainer.getChildren().clear();

        teacherModel.getTeacherList(classe, section, acadyear);
        for(Teacher teacher : TeacherList ){
            String teachername = teacher.getName();
            String matricule = teacher.getMatricule();

            //create subject container
            HBox teacherbox = teacherBox(matricule.substring(0,12), teachername);
            HBox box = subjectboxContainer(matricule);

            // append teachers subject container to row container
            teacherbox.getChildren().add(box);

            //append conatainers to main conatiner(Table)
            listContainer.getChildren().add(teacherbox);
        }
    }


    
    //create classbox which represent each class.
    public void Classbox(String name){
        Button form = new Button(name);
        form.setId(name);

        form.setOnMouseClicked(e->{
            classe = name;
            // scroll to selected class and change box color 

            // get list of subjects from this class
            form.setStyle("-fx-background-color:  #D1F6FF; -fx-background-radius: 10 ; -fx-text-fill: #0033ff; ");
           
            try {
                buildList();
            } catch (SQLException | FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        form.setMinHeight(30);
        form.setMinWidth(100);

        form.setStyle("-fx-background-color:   #f8fafc; -fx-background-radius: 10 ; -fx-text-fill: black; ");
        form.setFont(new Font("Arial", 14));

        classContainer.getChildren().add(form);
    }



    //create list of classes
    public void insertclasses(String section) throws SQLException{
        //first remove all classes
        classContainer.getChildren().clear();

        classList.clear();
        //select all classes from SQlite db
        String sql = "SELECT * FROM Class WHERE section =? AND acadyear=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, section);
        ps.setString(2, acadyear);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            classList.add(rs.getString("classname"));
        }

        //add classes to classContainer in UI
        int len = classList.size();
        for(int i=1; i<=len; i++){
            Classbox(classList.get(i-1));
        }
    }


}
