package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Controller {

    ArrayList<String> getTitles = new ArrayList<String>();
    ArrayList<String> getArtist = new ArrayList<String>();
    ArrayList<String> getPlaylist = new ArrayList<>();
    ObservableList<String> playlist = FXCollections.observableArrayList();
    ObservableList<String> playlistEmpty = FXCollections.observableArrayList();
    CreatePlaylist test = new CreatePlaylist();

    @FXML
    ListView lMusiclist, lPlaylist, lPlaylistSong;

    @FXML
    Button bContinue, bStart, bPause, bStop, bCreate;

    @FXML
    TextField tSearch;

    @FXML
    Label lSong, lArtist;

    @FXML
    ImageView iAlbum;

    MediaPlayer mediaPlayer;
    Duration stopTime;

    ObservableList<String> songList = FXCollections.observableArrayList();





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

        for (int i = 0; i < getTitles.size(); i++) {
            songList.add(getTitles.get(i) + " - " + getArtist.get(i));
        }

        lMusiclist.setItems(songList);
        lMusiclist.setStyle("-fx-control-inner-background: rgb(120,120,120);");

        updatePlaylist();


    }

    public void music() {

        String song = (String) lMusiclist.getSelectionModel().getSelectedItem();

        DB.selectSQL("Select fldFilePath from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataSong = DB.getData();

        DB.selectSQL("Select fldArtist from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataArtist = DB.getData();

        DB.selectSQL("Select fldTitle from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataTitle = DB.getData();

         DB.selectSQL("Select fldAlbumPicture from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
         String dataAlbumPicture = DB.getData();

        // System.out.println(data);

        Media MediaPlayer = new Media(Paths.get(dataSong).toUri().toString());
        mediaPlayer = new MediaPlayer(MediaPlayer);
        mediaPlayer.play();
        bStart.setVisible(false);
        bPause.setVisible(true);
        bStop.setVisible(true);
        lArtist.setText("by: " + dataArtist);
        lSong.setText("Currently playing: " + dataTitle);
        lArtist.setVisible(true);
        lSong.setVisible(true);
        iAlbum.setImage(new Image(getClass().getResourceAsStream(""+dataAlbumPicture+"")));
        iAlbum.setVisible(true);




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
        lArtist.setVisible(false);
        lSong.setVisible(false);
        iAlbum.setVisible(false);
    }


    public void createPlaylistMenu(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("createPlaylist.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.setTitle("Playlist creator");
        stage.setScene(scene);
        stage.show();

    }

    public void updatePlaylist(){


        DB.selectSQL("Select fldPlaylist from tblAllPlaylist");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                getPlaylist.add(data);
            }
        } while (true);

        playlist.addAll(getPlaylist);
        lPlaylist.setItems(playlist);
        lPlaylist.setStyle("-fx-control-inner-background: rgb(120,120,120);");

    }


    public void updatePlaylistAfter(){
        getPlaylist.clear();
        lPlaylist.getItems().clear();
        updatePlaylist();

    }
    }

