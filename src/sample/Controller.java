package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import javax.swing.text.Position;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Controller {

    ArrayList<String> getTitles = new ArrayList<String>();
    ArrayList<String> getArtist = new ArrayList<String>();

    @FXML
    ListView lMusiclist, lSearchSongs;

    @FXML
    Button bContinue, bStart, bPause, bStop;

    @FXML
    TextField tSearch;

    MediaPlayer mediaPlayer;
    Duration stopTime;


    ObservableList<String> songList = FXCollections.observableArrayList();
    ListView<String> listView = new ListView<String>(songList);


    public void initialize() {

        DB.selectSQL("Select fldTitle from tblInformation");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                getTitles.add(data);
            }
        } while (true);

        DB.selectSQL("Select fldArtist from tblInformation");
        do {
            String dataArtist = DB.getData();
            if (dataArtist.equals(DB.NOMOREDATA)) {
                break;
            } else {
                getArtist.add(dataArtist);
            }
        } while (true);

        System.out.println(getTitles + " - " + getArtist);

        for (int i = 0; i <getTitles.size() ; i++) {
            songList.add(getTitles.get(i) + " - " + getArtist.get(i));
        }
        lMusiclist.setItems(songList);
    }


    public void music() {

        //Hvis man starter to sange, kører den to sange oveni hinanden.

        String song = (String) lMusiclist.getSelectionModel().getSelectedItem();

        DB.selectSQL("Select fldFilePath from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");

        String data = DB.getData();

        // System.out.println(data);

        Media MediaPlayer = new Media(Paths.get(data).toUri().toString());
        mediaPlayer = new MediaPlayer(MediaPlayer);
        mediaPlayer.play();
        bStart.setVisible(false);
        bPause.setVisible(true);
        bStop.setVisible(true);

    }

    public void pause() {
        mediaPlayer.pause();
        stopTime = mediaPlayer.getCurrentTime();
        System.out.println(stopTime);
        bPause.setVisible(false);
        bContinue.setVisible(true);
    }

    public void handleContinue(ActionEvent actionEvent) {
        mediaPlayer.setStartTime(stopTime);
        mediaPlayer.play();
        bContinue.setVisible(false);
        bPause.setVisible(true);
    }

    public void test(KeyEvent keyEvent) {
        ObservableList<String> songSearch = FXCollections.observableArrayList();

        String searchString = tSearch.getText();

        for (int i = 0; i < getTitles.size(); i++) {
            if(getTitles.get(i).toLowerCase().contains(searchString.toLowerCase())
                    || getArtist.get(i).toLowerCase().contains(searchString.toLowerCase())) {

                songSearch.add(getTitles.get(i) + " - " + getArtist.get(i));
            }
        }
        lMusiclist.setItems(songSearch);
    }

    public void stopMusic(ActionEvent actionEvent) {
        mediaPlayer.stop();
        bStop.setVisible(false);
        bStart.setVisible(true);
        bContinue.setVisible(false);
        bPause.setVisible(false);
    }
}
