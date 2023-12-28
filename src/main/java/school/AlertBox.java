package school;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;



public class AlertBox {


    public void alertInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }
    

}
