package school;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ResultsController {

    @FXML
    private HBox classContainer;

    @FXML
    private Button downloadBtn;

    @FXML
    private VBox listContainer;

    @FXML
    private HBox subjectHeading;


    String classe;
    String section;
    String sequence;
    String selected;
    String acadyear;
    String table;
    boolean connected;

    
    Section sectionModel = new Section();

    AlertBox alert = new AlertBox();
    ArrayList<String> classList = new ArrayList<String>();
    ArrayList<String> subjectsList = new ArrayList<String>();

    PreparedStatement ps;
    Connection conn = DBconnector.getConnect();



    public void initialize() throws SQLException{

        //set variables and constants
        sequence = sectionModel.getSequence();
        section = sectionModel.getSection();
        acadyear = sectionModel.SetAcademicYear();
        classe = sectionModel.getFirstClass(section, acadyear);
        table = classe + "_" + acadyear;
        
        subjectsList.clear();
        insertclasses();
        buildtableHeader();
        buildResultTable();
    }


    @FXML
    void DownloadFile(ActionEvent event) throws IOException, SQLException, ParseException {
        checkInternet();
        if(connected){
            sectionModel.RestoreResults(table);
            Download(sequence);
        }else{
            alert.alertInfo("No internet connection. please connect and try again!");
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



    public void buildResultTable() throws SQLException{
        listContainer.getChildren().clear();
        sequence = sectionModel.getSequence();
        int i = 1;
        String qry = "SELECT * FROM " + table +  " WHERE sequence=?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, sequence);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            String num = String.valueOf(i++);
            String matricule = rs.getString("matricule");
            String name = rs.getString("name");
            //build each row of the result table UI
            resultRow(num, matricule, name);
        }
        rs.close();
    }


    public void buildtableHeader() throws SQLException{
        subjectHeading.getChildren().clear();

        String sql = "SELECT * FROM " + table;
        ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        // get table metaData
        ResultSetMetaData SQLiteMetaData = rs.getMetaData();

        //total column of the table
        int columnCount = SQLiteMetaData.getColumnCount();

        for(int i=4; i<=columnCount; i++){
            String subjectname = SQLiteMetaData.getColumnName(i);
            Label subjectLabel = new Label(subjectname.substring(0,3));
            subjectsList.add(subjectname);
            subjectLabel.setTextFill(Color.GRAY);
            subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            //add scores to score container UI
            subjectHeading.getChildren().add(subjectLabel);
        }
        rs.close();

    } 



    //create result sheet which represent each student score.
    public void resultRow(String num, String matricule, String name) throws SQLException{
        HBox box = new HBox();
        HBox boxnum = new HBox();
        HBox boxname = new HBox();
        HBox box1 = new HBox();
        Label numLabel = new Label(num);
        Label nameLabel = new Label(name);


        boxnum.setPrefWidth(100);
        boxnum.setMinWidth(100);
        boxnum.setMaxWidth(100);
        boxnum.setPrefHeight(50);
        boxnum.setAlignment(Pos.CENTER_LEFT);
        boxnum.getChildren().add(numLabel);

        boxname.setPrefWidth(175);
        boxname.setMaxWidth(175);
        boxname.setMinWidth(175);
        boxname.setPrefHeight(50);
        boxname.setAlignment(Pos.CENTER_LEFT);
        boxname.getChildren().add(nameLabel);

        box1.minWidth(694);
        box1.setPrefHeight(50);
        box1.setSpacing(50);
        box1.setAlignment(Pos.CENTER_LEFT);
        box1.setId(matricule);
        HBox.setHgrow(box1, Priority.ALWAYS);

        box.minWidth(1027);
        box.setPrefHeight(50);
        box.setPadding(new Insets(0,0,0,50));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-border-color:  #c9c5c5; -fx-background-color: white;");


        numLabel.setTextFill(Color.GREY);
        numLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.GREY);
        nameLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 14));


        /**
         * Build score container UI
        */ 

        int columnCount = subjectsList.size();
        sequence = sectionModel.getSequence();

        //get scores of a specific student
        String qry = "SELECT * FROM " + table +  " WHERE matricule=? AND sequence=?";
        ps = conn.prepareStatement(qry);
        ps.setString(1, matricule);
        ps.setString(2, sequence);
        ResultSet rs2 = ps.executeQuery();

        while(rs2.next()){

            for(int i=0; i<columnCount; i++){
                String score = rs2.getString(subjectsList.get(i));
                Label scoreLabel = new Label(score);
                scoreLabel.setTextFill(Color.GRAY);
                scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                //add scores to score container UI
                scoreLabel.setId(score);
                box1.getChildren().add(scoreLabel);
            }

        } 

        box.getChildren().addAll(boxnum, boxname, box1);
        listContainer.getChildren().add(box);
 
    }



    //create classbox which represent each class.
    public void Classbox(String name){
        Button form = new Button(name);
        form.setId(name);

        form.setOnMouseClicked(e->{
            try {
                selected = form.getId();

                //clear list
                subjectsList.clear();
       
                // rebuild result table
                buildtableHeader();
                buildResultTable();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            //restyle selected btn
            form.setStyle("-fx-background-color:  #D1F6FF; -fx-background-radius: 10 ; -fx-text-fill: #0033ff;  -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

            // scroll to selected class and change box color 
            
            // get marksheet results

        });

        form.setMinHeight(30);
        form.setMinWidth(100);

        form.setStyle("-fx-background-color:   #f8fafc; -fx-background-radius: 10 ; -fx-text-fill: black; ");
        form.setFont(new Font("Arial", 14));

        classContainer.getChildren().add(form);
    }

    
    
    //create list of classes
    public void insertclasses() throws SQLException{
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



    /**Select directory to create excell file
     * @return
     * @throws IOException
     */
    public String create_Excelfile() throws IOException{
        String path = null;
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showSaveDialog(null);

        if(response == JFileChooser.APPROVE_OPTION){
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath()+".xlsx");
            path = fileChooser.getSelectedFile().getAbsolutePath()+".xlsx";

            // if file is created
            boolean value = file.createNewFile();
            if(value){
                
            }else{
                //error message 
                alert.alertInfo("This File already exists in your computer. Try a different name");
            }
        }else{

        }
        return path;
    }



    //get scores from where sequenceid = ? AND class = ?
    /**
     * This function takes a database table and converts it to an Excel file.
     *
     * @param tableName (String): The name of the database table to be converted
     * @param fileName (String): The name of the Excel file to be created
     * @param connection (Connection): The database connection object
     */
    public void Download(String seqid) throws IOException{
    
        try {
            
            //select all from class marksheet
            ResultSet resultSet = sectionModel.getMarks(table, seqid);

            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(table);

            // Create a header row
            Row headerRow = sheet.createRow(0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i < columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Cell cell = headerRow.createCell(i - 1);
                cell.setCellValue(columnName);
            }

            // Create data rows
            int rowNum = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 1; i < columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(i - 1);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Float) {
                            cell.setCellValue((Float) value);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else if (value instanceof Date) {
                            cell.setCellValue((Date) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }

            // Write the workbook to a file
            FileOutputStream outputStream = new FileOutputStream(create_Excelfile());
            workbook.write(outputStream);
            ((FileOutputStream) workbook).close();
            outputStream.close();

            // Log success message
            System.out.println("Table " + table + " successfully converted to Excel file ");
            alert.alertInfo("Download was successful");
        } catch (SQLException | IOException e) {
            // Log error message
            alert.alertInfo("an error occured. please try again!");

        }

    }


}
