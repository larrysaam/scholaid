package school;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

public class MarksheetController {

    @FXML
    private Button addclassBtn;

    @FXML
    private Button importBtn;

    @FXML
    private VBox listContainer;

    @FXML
    private HBox classesContainer;

    @FXML
    private Button closebtn;

    @FXML
    private VBox popupbox;

    @FXML
    private Button savebtn;

    @FXML
    private TextField classfield;




    PreparedStatement ps;
    Connection conn = DBconnector.getConnect();

    String selected;
    String section; 
    String acadyear;
    String table;
    boolean connected;


    ArrayList<String> classList = new ArrayList<String>();
    ArrayList<Student> studentlist = new ArrayList<Student>();
    AlertBox alert = new AlertBox();
    Section sectionData = new Section();
    StudentData studentdata = new StudentData(studentlist);
    Backup backup = new Backup();


    public void initialize() throws SQLException{

        section = sectionData.getSection();
        acadyear = sectionData.SetAcademicYear();
        selected = sectionData.getFirstClass(section, acadyear);
        System.out.println("________________________"+section + "______" + acadyear + "____class "+ selected);
        table = selected + "_" + acadyear;

        if(selected == null){
            listContainer.getChildren().clear();
            Label message = new Label();
            message.setText("No Class Created Yet");
            message.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            message.setPadding(new Insets(100, 0, 0, 0));
            listContainer.getChildren().add(message);
        }else{
            createList(selected, section);
        }
        
        insertclasses();
    }


    @FXML
    void addClass(ActionEvent event) {
        popupbox.setVisible(true);
    }


    @FXML
    void importClassList(ActionEvent event) throws SQLException, IOException, ParseException {
        //import if only internet connection is available
        checkInternet();
        if(connected){
            importfile();
        }else{
            // alert "no internet connection"
            alert.alertInfo("no internet connection. connect and try again!");
        }
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




    
    



    /**
     * 
     * other functions below
     * 
    **/


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

            // scroll to selected class and change box color 

            // get list of students from this class
            selected = name;
            table = selected + "_" + acadyear;
            form.setStyle("-fx-background-color:  #D1F6FF; -fx-background-radius: 10 ; -fx-text-fill: #0033ff; ");
            try {
                createList(selected, section);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        form.setMinHeight(30);
        form.setMinWidth(100);

        form.setStyle("-fx-background-color:   #f8fafc; -fx-background-radius: 10 ; -fx-text-fill: black; ");
        form.setFont(new Font("Arial", 14));

        classesContainer.getChildren().add(form);
    }


    
    //create list of classes
    public void insertclasses() throws SQLException{
        //first remove all classes
        classesContainer.getChildren().removeAll();

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
        int len = classList.size();
        for(int i=1; i<=len; i++){
            Classbox(classList.get(i-1));
        }

    }


    //create studentbox which represent each student detail.
    public void studentBox(String num, String matricule, String name){
        HBox box = new HBox();
        Label numLabel = new Label(num);
        Label matriculelLabel = new Label(matricule);
        Label nameLabel = new Label(name);

        box.minWidth(781);
        box.minHeight(50);
        box.prefHeight(50);
        box.setPrefHeight(50);
        box.setSpacing(150);
        box.setPadding(new Insets(0,0,0,50));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-border-color:  #c9c5c5; -fx-background-color: white;");

        numLabel.setTextFill(Color.GREY);
        numLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 14));
        matriculelLabel.setTextFill(Color.GREY);
        matriculelLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.GREY);
        nameLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 14));

        box.getChildren().addAll(numLabel, matriculelLabel, nameLabel);
        listContainer.getChildren().add(box);

    }


    //create list of students
    public void createList(String classe, String section) throws SQLException{
        //first remove all list
        listContainer.getChildren().clear();
        studentlist.clear();

        //iterate through the list
        int i = 1;
        studentdata.getClassList(classe, section, acadyear);
        for(Student student: studentlist){
            String num = String.valueOf(i++);
            String mat = student.getMatricule();
            String name = student.getName();

            //student row
            studentBox(num, mat, name);
        }
    }


    


    //function imports file from .xlss and copy all its content into mongodb and sqlite db
    public void importfile() throws SQLException, IOException, ParseException {
        //first remove all list
        listContainer.getChildren().removeAll();

        // Read the Excel file
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showOpenDialog(null);

        //if approved button is clicked
        if(response == JFileChooser.APPROVE_OPTION){
            //get files absolute path from PC
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            FileInputStream inputStream = new FileInputStream(file);

                try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    //clear whole sqlite table
                    String qry = "DELETE FROM "+ table;
                    ps = conn.prepareStatement(qry);
                    ps.executeUpdate();

                    // Get the column names from the first row of the sheet
                    Row headerRow = sheet.getRow(0);
                    int numColumns = headerRow.getLastCellNum();
                    System.out.println("____________"+sheet.getLastRowNum());

                    String[] columnNames = new String[numColumns];
                    
                    //also verify if column names are 'matricule' and 'studentname'
                    

                        for (int i = 0; i < numColumns; i++) {
                            Cell cell = headerRow.getCell(i);
                            columnNames[i] = cell.getStringCellValue();
                        }        

                        //create marksheet table if it doesn't exist
                        sectionData.createtable(table);
   
                        // Insert the data into the database
                        int num = 1;
                        while(num <= 6){
                            int numRows = sheet.getLastRowNum();
                            for (int i = 1; i <= numRows; i++) {

                                Row row = sheet.getRow(i);
                                Cell cell1 = row.getCell(0);
                                Cell cell2 = row.getCell(1);
                                
                                String sql = "INSERT INTO " +table+ "(matricule, name, sequence) VALUES(?,?,?)";
                                ps = conn.prepareStatement(sql);
                                ps.setString(1, cell1.getStringCellValue());
                                ps.setString(2, cell2.getStringCellValue());
                                ps.setString(3, Integer.toString(num));
                                ps.execute();
                            }
                            num++;
                        }

                }
                createList(selected, section);
                alert.alertInfo("Import Completed successfully");
            }
            backup.mongodbInsertMarksheet(table);
       }

    }



