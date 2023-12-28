package school;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class dashboardController {

    
     @FXML
    private Button addsectionBtn;

    @FXML
    private AnchorPane centerpane;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button englishBtn;

    @FXML
    private Button frenchBtn;

    @FXML
    private Label heading;

    @FXML
    private Button marksheetBtn;

    @FXML
    private Button registerBtn;

    @FXML
    private Button resultsBtn;

    @FXML
    private Button subjectBtn;

    @FXML
    private Button seq1;

    @FXML
    private Button seq2;

    @FXML
    private Button seq3;

    @FXML
    private Button seq4;

    @FXML
    private Button seq5;

    @FXML
    private Button seq6;

    @FXML
    private VBox seqSelector;

    @FXML
    private HBox sidebarcontainer;


    Section section1 = new Section();



    String pagename;


    public void initialize() throws IOException, SQLException{
  
        toHome(null);
        removesideBar();
        
    }


    @FXML
    void addSection(ActionEvent event) {

    }

    @FXML
    void setEnglish(ActionEvent event) throws SQLException, IOException {
            // move to english section
            section1.setSection("english");  
             // loading receipt controller
             String pagename = section1.getPage();
             System.out.println(pagename);
        

             FXMLLoader loader = new FXMLLoader(getClass().getResource(pagename + ".fxml"));
             Parent root = loader.load();
             Node node = root;
     
             if(pagename == "mainpage"){
                 MainpageController  page = loader.getController();
                 page.initialize();
             }
             else if(pagename == "result"){
                 ResultsController  page = loader.getController();
                 page.initialize();
             }
             else if(pagename == "teacherregistration"){
                 TeacherregistrationController  page = loader.getController();
                 page.initialize();
             }
             else if(pagename == "marksheet"){
                 MarksheetController  page = loader.getController();
                 page.initialize();
             }
             else if(pagename == "subjects"){
                System.out.println(pagename);
                SubjectsController  page3 = loader.getController();
                page3.initialize();
            }
    

        //hight button
        englishBtn.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF;  -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");
        frenchBtn.setStyle("-fx-text-fill: black; -fx-background-color:  #f8fafc;   -fx-background-radius: 20;");

        // change language to english

    }


    @FXML
    void setFrench(ActionEvent event) throws SQLException, IOException {
        // move to english section
        section1.setSection("french");
        // loading receipt controller
        pagename = section1.getPage();
        System.out.println(pagename);
        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(pagename + ".fxml"));
        Parent root = loader.load();
        Node node = root;

        if(pagename == "mainpage"){
            MainpageController  page = loader.getController();
            page.initialize();
        }
        else if(pagename == "result"){
            ResultsController  page1 = loader.getController();
            page1.initialize();
        }
        else if(pagename == "teacherregistration"){
            TeacherregistrationController  page2 = loader.getController();
            page2.initialize();
        }
        else if(pagename == "marksheet"){
            System.out.println(pagename);
            MarksheetController  page3 = loader.getController();
            page3.initialize();
        }
        else if(pagename == "subjects"){
            System.out.println(pagename);
            SubjectsController  page3 = loader.getController();
            page3.initialize();
        }

        

        //hight button
        frenchBtn.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF;  -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");
        englishBtn.setStyle("-fx-text-fill: black; -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");

        // change language to french

        
        
    }


    public void reloadResultPage() throws IOException, SQLException{
        pagename = section1.getPage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pagename + ".fxml"));
        Parent root = loader.load();
        Node node = root;
        ResultsController  page1 = loader.getController();
        page1.initialize();
    }


    public void removesideBar(){
        seqSelector.setDisable(true);
    }

    public void insertsideBar(){
        seqSelector.setDisable(false);
        
    }

    //not avaiable in home page

    public void reset_SequenceBtn_Style(){
        seq1.setStyle("-fx-text-fill: black;  -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");
        seq2.setStyle("-fx-text-fill: black;  -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");
        seq3.setStyle("-fx-text-fill: black;  -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");
        seq4.setStyle("-fx-text-fill: black;  -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");
        seq5.setStyle("-fx-text-fill: black;  -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");
        seq6.setStyle("-fx-text-fill: black;  -fx-background-color:  #f8fafc;  -fx-background-radius: 20;");
    }

    @FXML
    void setSeq1(ActionEvent event) throws SQLException, IOException {
        reset_SequenceBtn_Style();
        seq1.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

        //change sequence
        section1.setSequence("1");
        //hight button
        toPage("results");
       
    }

    @FXML
    void setSeq2(ActionEvent event) throws SQLException, IOException {
        reset_SequenceBtn_Style();
        seq2.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

        //change sequence
        section1.setSequence("2");
        //hight button
        toPage("results");
        
    }

    @FXML
    void setSeq3(ActionEvent event) throws SQLException, IOException {
        reset_SequenceBtn_Style();
        seq3.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

        //change sequence
        section1.setSequence("3");
        //hight button
        toPage("results");
    }

    @FXML
    void setSeq4(ActionEvent event) throws SQLException, IOException {
        reset_SequenceBtn_Style();
        seq4.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

        //change sequence
        section1.setSequence("4");
        //hight button
        toPage("results");
    }

    @FXML
    void setSeq5(ActionEvent event) throws SQLException, IOException {
        reset_SequenceBtn_Style();
        seq5.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

        //change sequence
        section1.setSequence("5");
        //hight button
        toPage("results");
        
    }

    @FXML
    void setSeq6(ActionEvent event) throws SQLException, IOException {
        reset_SequenceBtn_Style();
        seq6.setStyle("-fx-text-fill: #0033ff; -fx-background-color:  #D1F6FF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: linear-gradient(to bottom, #61d8de, #e839f6)");

        //change sequence
        section1.setSequence("6");
        //hight button
        toPage("results");
    }

    /** end  
     * @throws SQLException
     */ 


    @FXML
    void toHome(ActionEvent event) throws IOException, SQLException {
        heading.setText("Dashboard");
        setEnglish(event);
        removesideBar();
        dashboardBtn.setStyle("-fx-background-color:  #89CFF0; -fx-background-radius: 10;");
        resultsBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        registerBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        marksheetBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        subjectBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        toPage("mainpage");
    }

    @FXML
    void toRegister(ActionEvent event) throws IOException, SQLException {
        heading.setText("Registration");
        removesideBar();
        dashboardBtn.setStyle("-fx-background-color:  none; -fx-background-radius: 10;");
        resultsBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        registerBtn.setStyle("-fx-background-color: #89CFF0; -fx-background-radius: 10;");
        marksheetBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        subjectBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        toPage("teacherregistration");
    }

    @FXML
    void toResults(ActionEvent event) throws IOException, SQLException {
        heading.setText("Results");
        insertsideBar();
        setSeq1(event);
        dashboardBtn.setStyle("-fx-background-color:  none; -fx-background-radius: 10;");
        resultsBtn.setStyle("-fx-background-color: #89CFF0; -fx-background-radius: 10;");
        registerBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        marksheetBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        subjectBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        toPage("results");
    }


    @FXML
    void tomarksheet(ActionEvent event) throws IOException, SQLException {
        heading.setText("Marksheet");
        removesideBar();
        dashboardBtn.setStyle("-fx-background-color:  none; -fx-background-radius: 10;");
        resultsBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        registerBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        marksheetBtn.setStyle("-fx-background-color: #89CFF0; -fx-background-radius: 10;");
        subjectBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        toPage("marksheet");
    }

    @FXML
    void toSubject(ActionEvent event) throws IOException, SQLException {
        heading.setText("subjects");
        removesideBar();
        dashboardBtn.setStyle("-fx-background-color:  none; -fx-background-radius: 10;");
        resultsBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        registerBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        marksheetBtn.setStyle("-fx-background-color: none; -fx-background-radius: 10;");
        subjectBtn.setStyle("-fx-background-color: #89CFF0; -fx-background-radius: 10;");
        toPage("subjects");
    }
    


    public void toPage(String pagename) throws IOException, SQLException{

        section1.setPage(pagename);
        centerpane.getChildren().removeAll();
        Parent fxml = FXMLLoader.load(getClass().getResource(pagename+".fxml"));
        AnchorPane.setBottomAnchor(fxml, 0.0);
        AnchorPane.setLeftAnchor(fxml, 0.0);
        AnchorPane.setRightAnchor(fxml, 0.0);
        AnchorPane.setTopAnchor(fxml, 0.0);
        centerpane.getChildren().add(fxml);
    }
    


}
