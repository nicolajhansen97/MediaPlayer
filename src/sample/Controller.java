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

    String One = "Hey There Delilah";
    String Two = "Waka Waka - This Time For Africa";
    String Tree = "Paint it, Black";
    String Four = "Someday";
    String Five = "Thunderstruck";

    ObservableList<String> names = FXCollections.observableArrayList(
            One, Two, Tree, Four, Five);
    ListView<String> listView = new ListView<String>(names);











    public void initialize(){

        musicList.setItems(names);

    }

    public void music(){

        String a = (String) musicList.getSelectionModel().getSelectedItem();
        DB.selectSQL("Select fldFilePath from tblInformation WHERE fldTitle = '" + a + "'");

        String data = DB.getData();

       // System.out.println(data);

        Media MediaPlayer = new Media(Paths.get(data).toUri().toString());
        mediaPlayer = new MediaPlayer(MediaPlayer);
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
