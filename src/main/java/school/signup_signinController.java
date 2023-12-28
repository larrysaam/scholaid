package school;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.json.simple.parser.ParseException;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class signup_signinController {

    @FXML
    private Label a1;

    @FXML
    private Label a2;

    @FXML
    private Label a3;

    @FXML
    private Label b1;

    @FXML
    private Label b2;

    @FXML
    private Label b3;

    @FXML
    private Label message_label;

    @FXML
    private AnchorPane central_pane;

    @FXML
    private Button forgotten_key_btn;

    @FXML
    private TextField location_fld;

    @FXML
    private TextField proprietor_contact_fld;

    @FXML
    private TextField proprietor_name_fld;

    @FXML
    private TextField school_contact_fld;

    @FXML
    private TextField school_key_fld;

    @FXML
    private TextField school_name_fld;

    @FXML
    private AnchorPane side_pane;

    @FXML
    private Button side_signin_btn;

    @FXML
    private Button side_signup_btn;

    @FXML
    private Button signin_btn;

    @FXML
    private TextField signin_school_name_fld;

    @FXML
    private Button signup_btn;



    Signup signup = new Signup();
    boolean verify_account;
    boolean cloud_verify_account;



    public void initialize() throws SQLException{
        // creating all tables
        signup.create_signup_table();
        signup.create_all_empty_tables();


        b1.setVisible(false);
        b2.setVisible(false);
        b3.setVisible(false);
        signin_btn.setVisible(false);
        side_signup_btn.setVisible(false);
        forgotten_key_btn.setVisible(false);
        signin_school_name_fld.setVisible(false);
        school_key_fld.setVisible(false);
    }



    @FXML
    void get_new_key(ActionEvent event) {

    }



    @FXML
    void goto_signin(ActionEvent event) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.7));
        slide.setNode(side_pane);

        slide.setToX(493);
        slide.play();

        central_pane.setTranslateX(-309);
        message_label.setText("");
        b1.setVisible(true);
        b2.setVisible(true);
        b3.setVisible(true);
        signin_btn.setVisible(true);
        side_signup_btn.setVisible(true);
        forgotten_key_btn.setVisible(true);
        signin_school_name_fld.setVisible(true);
        school_key_fld.setVisible(true);
        a1.setVisible(false);
        a2.setVisible(false);
        a3.setVisible(false);
        side_signin_btn.setVisible(false);
        signup_btn.setVisible(false);
        school_name_fld.setVisible(false);
        school_contact_fld.setVisible(false);
        proprietor_contact_fld.setVisible(false);
        proprietor_name_fld.setVisible(false);
        location_fld.setVisible(false);
 

    }



    @FXML
    void goto_signup(ActionEvent event) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.7));
        slide.setNode(side_pane);

        slide.setToX(0);
        slide.play();

        central_pane.setTranslateX(0);
        message_label.setText("");
        b1.setVisible(false);
        b2.setVisible(false);
        b3.setVisible(false);
        signin_btn.setVisible(false);
        side_signup_btn.setVisible(false);
        forgotten_key_btn.setVisible(false);
        signin_school_name_fld.setVisible(false);
        school_key_fld.setVisible(false);
        a1.setVisible(true);
        a2.setVisible(true);
        a3.setVisible(true);
        side_signin_btn.setVisible(true);
        signup_btn.setVisible(true);
        school_name_fld.setVisible(true);
        school_contact_fld.setVisible(true);
        proprietor_contact_fld.setVisible(true);
        proprietor_name_fld.setVisible(true);
        location_fld.setVisible(true);
 
       
    }



    @FXML
    void signin(ActionEvent event) throws SQLException, IOException {

        String signin_school_name = signin_school_name_fld.getText();
        String key = school_key_fld.getText();

        boolean validate_signin = signup.validate_signin(signin_school_name, key);

        if(signin_school_name.isEmpty() || key.isEmpty()){
            message_label.setText("some fields are empty!");
        }
        else{
            if(validate_signin == false){
                message_label.setText("wrong school name or key!");
            }
            else if(validate_signin == true){
                // successfully signin. go to home page
                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                Stage window = (Stage) signin_btn.getScene().getWindow();
                window.setScene(new Scene(root));
                window.show();
            }
            else{
                message_label.setText("server error!");
            }
            
        }
    }



    /**
     * This function generates a unique 5-figure code using UUID.
     *
     * @return String: A unique 5-figure code
    */
    public static String generateUniqueCode() {
        try {
            // Generate a random UUID
            UUID uuid = UUID.randomUUID();

            // Get the first 5 characters of the UUID
            String code = uuid.toString().substring(0, 5);

            return code;
        } catch (Exception e) {
            // Log the error
            System.out.println("Error: " + e.getMessage());
            return "";
        }
    }





    @FXML
    void signup(ActionEvent event) throws SQLException, IOException, ParseException {

        message_label.setText("");
        String key = generateUniqueCode();
        String school_name = school_name_fld.getText();
        String school_contact = school_contact_fld.getText();
        String propietor_contact = proprietor_contact_fld.getText();
        String proprietor_name = proprietor_name_fld.getText();
        String location = location_fld.getText();

        signup.create_signup_table();
        verify_account = signup.verify_account(school_name, school_contact);
        cloud_verify_account = signup.Cloud_verify_account(school_name, school_contact);

        if(school_name.isEmpty() || school_contact.isEmpty() || propietor_contact.isEmpty() || proprietor_name.isEmpty() || location.isEmpty()){
            message_label.setText("some fields are empty!");
        }
        else if(school_contact.length()<9 || school_contact.length()>9){
            message_label.setText("school contact is not valid!");
            school_contact_fld.setText("");
        }
        else if(propietor_contact.length()<9 || propietor_contact.length()>9){
            message_label.setText("propietor contact is not valid!");
            proprietor_contact_fld.setText("");
        }
        else if(signup.check_connection() == false){
            message_label.setText("connect to the internet and try again.");
        }
        else if(cloud_verify_account){
            // error message saying "account already exists"
            message_label.setText("account already exists, Signin now!");
            clear_fields();
        }
        else{

            if(!signup.Cloudinsert(key, school_name, school_contact, proprietor_name, propietor_contact, location)){

                message_label.setText("internet or server problem");

            }else{
                
                signup.insert(key,school_name, school_contact, proprietor_name, propietor_contact, location);
                // successfully signin. go to home page
                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                Stage window = (Stage) signin_btn.getScene().getWindow();
                window.setScene(new Scene(root));
                window.show();
            }
            
            
        }
    }


    public void clear_fields(){
        school_name_fld.setText("");
        school_contact_fld.setText("");
        proprietor_contact_fld.setText("");
        proprietor_name_fld.setText("");
        location_fld.setText("");
    }


}


// server error!  contact customer service
// 
// 
// 
// 



