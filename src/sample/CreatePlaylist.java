package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.*;

import javax.swing.text.Position;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CreatePlaylist {

    String lastPlaylist;


    @FXML
    Button bCreate;

    @FXML
    TextField tName;

    ArrayList<String> getPlaylist = new ArrayList<>();
    boolean isMade = false;

    public void initialize() {


        DB.selectSQL("Select fldPlaylist from tblAllPlaylist");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                getPlaylist.add(data);
            }
        } while (true);
        System.out.println(getPlaylist);
    }

    public void handleCreatePlaylist(ActionEvent actionEvent) {


        for (int i = 0; i <getPlaylist.size(); i++) {

            String check = getPlaylist.get(i);

            if (check.equals(tName.getText())) {
                tName.setText("This playlist name is already in use!");
                isMade = true;
                break;
            }
        }

        if(isMade == false)
        {
              DB.insertSQL("INSERT INTO tblAllPlaylist VALUES ('" + tName.getText() + "')");
              DB.insertSQL("CREATE TABLE "+tName.getText().replaceAll("\\s","")+"(fldSongNames nchar(70))");




        }
    }
}

