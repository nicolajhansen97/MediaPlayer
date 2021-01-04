package sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import javax.swing.text.Position;
import java.nio.file.Paths;

public class Controller {

    @FXML
            ListView musicList;

    MediaPlayer mediaPlayer;
    Duration stopTime;

    String One = "Hey There Delilah - Scarpetti";
    String Two = "Test";
    String Tree = "Test2";
    String Four = "Test3";
    String Five = "Test4";

    ObservableList<String> names = FXCollections.observableArrayList(
            One, Two, Tree, Four, Five);
    ListView<String> listView = new ListView<String>(names);

    public void initialize(){

        musicList.setItems(names);
    }

    public void music(){


       // String g = (String) musicList.getSelectionModel().getSelectedItem();
        String g = "HeyThereDelilah.mp3";
        Media h = new Media(Paths.get(g).toUri().toString());
        mediaPlayer = new MediaPlayer(h);
        mediaPlayer.play();
    }

    public void pause(ActionEvent actionEvent) {
        mediaPlayer.pause();
        stopTime = mediaPlayer.getCurrentTime();
        System.out.println(stopTime);

    }

    public void handleContinue(ActionEvent actionEvent) {
      mediaPlayer.setStartTime(stopTime);
      mediaPlayer.play();
    }
}
