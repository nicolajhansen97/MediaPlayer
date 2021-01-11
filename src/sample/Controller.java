package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class Controller {

    ArrayList<String> alTitles = new ArrayList<>(); //ArrayList to get all titles
    ArrayList<String> alArtist = new ArrayList<>(); //ArrayList to get all artist
    ArrayList<String> alPlaylist = new ArrayList<>(); //ArrayList to get all playlist
    ArrayList<String> alSongsForPersonalPlaylist = new ArrayList<>(); //ArrayList to get all songs to personal playlist
    ObservableList<String> olStoreAllPlaylist = FXCollections.observableArrayList(); //ObservableList to store all the playlist
    ObservableList<String> olStoreSongs = FXCollections.observableArrayList(); //ObservableList to store all the songs from DB
    ObservableList<String> olSongsOnPersonalPlaylist = FXCollections.observableArrayList(); //ObservableList to store all the songs on peoples personal playlist
    boolean addToPlaylistWorkaround = true; //Work around so the action handler will only give a output once.

    @FXML
    ListView lMusiclist, lPlaylist, lPlaylistSong;

    @FXML
    Button bContinue, bStart, bPause, bStop, bCreate, bOpenPlaylist, bClosePlaylist;

    @FXML
    TextField tSearch;

    @FXML
    Label lSong, lArtist, lCurrentTime, lTotalTime;

    @FXML
    ImageView iAlbum;

    @FXML
    ComboBox cAddToPlaylist;

    @FXML
    Slider sProgress;

    MediaPlayer mediaPlayer; //Is the MediaPlayer
    Duration stopTime; //Is used to pause and stop the song
    Duration tim1,tim2; //Timer for the MediaPlayer to save the time
    int min; //Store the timeframe of the song in minutes
    int count = 0; //Is counting the the timeframe and make it run smooth
    boolean done = false; //Is stopping the do while loop so its not starting multiple threads.

    public void initialize() {


        DB.selectSQL("Select fldTitle from tblInformation");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                alTitles.add(data);
            }
        } while (true);

        DB.selectSQL("Select fldArtist from tblInformation");
        do {
            String dataArtist = DB.getData();
            if (dataArtist.equals(DB.NOMOREDATA)) {
                break;
            } else {
                alArtist.add(dataArtist);
            }
        } while (true);

        System.out.println(alTitles + " - " + alArtist);

        for (int i = 0; i < alTitles.size(); i++) {
            olStoreSongs.add(alTitles.get(i) + " - " + alArtist.get(i));
        }

        lMusiclist.setItems(olStoreSongs);
        lMusiclist.setStyle("-fx-control-inner-background: rgb(120,120,120);");

        updatePlaylist();
        cAddToPlaylist.setItems(olStoreAllPlaylist);

    }

    public void handleMusic() {

        String song = "";

        if (!lMusiclist.getSelectionModel().isEmpty())
        {
            song = (String) lMusiclist.getSelectionModel().getSelectedItem();
        }
        else if(lMusiclist.getSelectionModel().isEmpty())
        {
            song = (String) lPlaylistSong.getSelectionModel().getSelectedItem();
        }

        DB.selectSQL("Select fldFilePath from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataSong = DB.getData();

        DB.selectSQL("Select fldArtist from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataArtist = DB.getData();

        DB.selectSQL("Select fldTitle from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataTitle = DB.getData();

        DB.selectSQL("Select fldAlbumPicture from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String dataAlbumPicture = DB.getData();

        DB.selectSQL("Select fldLength from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-")-1) + "'");
        String length = DB.getData();


        Media MediaPlayer = new Media(Paths.get(dataSong).toUri().toString());
        mediaPlayer = new MediaPlayer(MediaPlayer);
        mediaPlayer.play();
        bStart.setVisible(false);
        bPause.setVisible(true);
        bStop.setVisible(true);
        lArtist.setText("by: " + dataArtist);
        lSong.setText("Currently playing: " + dataTitle);
        lTotalTime.setText(length);
        lArtist.setVisible(true);
        lSong.setVisible(true);
        iAlbum.setImage(new Image(getClass().getResourceAsStream(""+dataAlbumPicture+"")));
        iAlbum.setVisible(true);

        done = false;
        count = 0;
        currentTimeLabel();

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

    public void handleSearchFunction(KeyEvent keyEvent) {
        ObservableList<String> songSearch = FXCollections.observableArrayList();

        String searchString = tSearch.getText();

        for (int i = 0; i < alTitles.size(); i++) {
            if(alTitles.get(i).toLowerCase().contains(searchString.toLowerCase())
                    || alArtist.get(i).toLowerCase().contains(searchString.toLowerCase())) {

                songSearch.add(alTitles.get(i) + " - " + alArtist.get(i));
            }
        }
        lMusiclist.setItems(songSearch);
    }

    public void handleStopMusic(ActionEvent actionEvent) {
        mediaPlayer.stop();
        mediaPlayer.pause();
        stopTime = mediaPlayer.getStartTime();
        count = 0;
        done = true;
        sProgress.setValue(count);
        lCurrentTime.setText("0:00");


        mediaPlayer.stop();
        bStop.setVisible(false);
        bStart.setVisible(true);
        bContinue.setVisible(false);
        bPause.setVisible(false);
        lArtist.setVisible(false);
        lSong.setVisible(false);
        iAlbum.setVisible(false);
        lTotalTime.setText("00:00");
    }

    public void handleCreatePlaylistMenu(ActionEvent actionEvent) throws IOException {

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
                alPlaylist.add(data);
            }
        } while (true);

        olStoreAllPlaylist.addAll(alPlaylist);
        lPlaylist.setItems(olStoreAllPlaylist);
        lPlaylist.setStyle("-fx-control-inner-background: rgb(120,120,120);");

    }

    public void updatePersonPlaylist(){

         alSongsForPersonalPlaylist.clear();
         lPlaylistSong.getItems().clear();
        System.out.println(playlistChosen);
        DB.selectSQL("Select fldSongNames from "+playlistChosen.replaceAll("\\s","")+"");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                alSongsForPersonalPlaylist.add(data);
            }
        } while (true);

        olSongsOnPersonalPlaylist.addAll(alSongsForPersonalPlaylist);
        lPlaylistSong.setItems(olSongsOnPersonalPlaylist);
        lPlaylistSong.setStyle("-fx-control-inner-background: rgb(120,120,120);");

    }

    public void handleUpdatePlaylistAfter(){
        alPlaylist.clear();
        lPlaylist.getItems().clear();
        updatePlaylist();

    }

    String playlistChosen = "";

    public void handleOpenPlaylist(ActionEvent actionEvent) {
        playlistChosen = (String) lPlaylist.getSelectionModel().getSelectedItem();
        DB.selectSQL("Select fldSongNames from "+playlistChosen.replaceAll("\\s","")+"");

        do {
            String dataSong = DB.getData();
            if (dataSong.equals(DB.NOMOREDATA)) {
                break;
            } else {
                alSongsForPersonalPlaylist.add(dataSong);
            }
        } while (true);

        olSongsOnPersonalPlaylist.addAll(alSongsForPersonalPlaylist);
        lPlaylistSong.setItems(olSongsOnPersonalPlaylist);
        lPlaylistSong.setStyle("-fx-control-inner-background: rgb(120,120,120);");
        lPlaylistSong.setVisible(true);
        lPlaylist.setVisible(false);
        bOpenPlaylist.setVisible(false);
        bClosePlaylist.setVisible(true);

    }

    public void handleClosePlaylist(ActionEvent actionEvent) {

        alSongsForPersonalPlaylist.clear();
        olSongsOnPersonalPlaylist.clear();

        lPlaylist.setVisible(true);
        lPlaylistSong.setVisible(false);
        bOpenPlaylist.setVisible(true);
        bClosePlaylist.setVisible(false);

    }

    public void handleClearMainPlayList(MouseEvent mouseEvent) {
        lMusiclist.getSelectionModel().clearSelection();
    }

    public void handleClearPersonPlaylist(MouseEvent mouseEvent) {
        lPlaylistSong.getSelectionModel().clearSelection();


    }

    public void handleAddtoPlaylist(ActionEvent actionEvent) {

        String getWhichPlaylistToAddTo = (String) cAddToPlaylist.getSelectionModel().getSelectedItem();


        if(addToPlaylistWorkaround == true)
        {

            String fixedSongName = lMusiclist.getSelectionModel().getSelectedItem().toString();
            fixedSongName.replaceAll("\\[","");
            fixedSongName.replaceAll("\\]","");

            DB.insertSQL("INSERT INTO "+getWhichPlaylistToAddTo.replaceAll("\\s","")+ " VALUES ('"+fixedSongName+"')");

            updatePersonPlaylist();

            addToPlaylistWorkaround = false;

        }
        else
        {
            addToPlaylistWorkaround = true;
        }


    }

    public void handleRemoveSong(ActionEvent actionEvent) {

        String fixedSongName = lPlaylistSong.getSelectionModel().getSelectedItem().toString();
        fixedSongName.replaceAll("\\[","");
        fixedSongName.replaceAll("\\]","");

        System.out.println(fixedSongName);
        DB.deleteSQL("Delete FROM "+playlistChosen.replaceAll("\\s","")+" Where fldSongNames = '"+fixedSongName+"'");

        updatePersonPlaylist();
    }

    public void handleDeletePlaylist(ActionEvent actionEvent) {

        String NotfixedPlaylist = lPlaylist.getSelectionModel().getSelectedItem().toString();
        String fixedPlaylist = NotfixedPlaylist.replaceAll("\\s","");

        System.out.println(fixedPlaylist);
        DB.deleteSQL("DROP TABLE "+fixedPlaylist+"");
        DB.deleteSQL("Delete FROM tblAllPlaylist WHERE fldPlaylist = '"+NotfixedPlaylist+"'");

        handleUpdatePlaylistAfter();

    }


    /***
     * den får længden af sangen og følger den i mens sangen spiller
     */
    public void slider(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        sProgress.setMax(tim2.toSeconds());
                        sProgress.setValue(count);
                    }
                });
            }
        }).start();
    }

    /***
     * her kan vi spole i sangen. og flytte slideren til et bestemt punkt
     * @param event mouseevent så vi kan se hvor du klikker på slideren
     */
    @FXML
    public void sliderChoice(MouseEvent event){
        double dx = event.getX();
        double dWidth = sProgress.getWidth();
        double progression = (dx/dWidth);
        double mili = (progression * mediaPlayer.getTotalDuration().toMillis());
        Duration duration = new Duration(mili);
        mediaPlayer.seek(duration);
        count =(int)mediaPlayer.getCurrentTime().toSeconds();
        sProgress.setValue(sProgress.getValue());
        mediaPlayer.pause();
        mediaPlayer.play();
        t();
        slider();
    }

    //sætter nogle værdier op


    /***
     * den her sætter timern til at være korrekt på en god formatteted måde.
     */
    public void t(){
        min = count/60;
        if (count==0||count-(min*60)==0){
            lCurrentTime.setText(String.format("%d:00", min));

        }
        else if (count<10||count-(min*60)<10){
            lCurrentTime.setText(String.format("%d:0%d", min,count-(min*60)));

        }
        else {
            lCurrentTime.setText(String.format("%d:%d", min,count-(min*60)));

        }
    }


    /***
     * den her får hvornår sangen starter og slutter og laver et loop så går igennem hvert milisecond af sangen og checker
     * hvornår et sekund af gået og så ændre den count og opdater slideren og timeren.
     */
    public void currentTimeLabel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                do{
                    tim1 = mediaPlayer.getCurrentTime();
                    tim2 = mediaPlayer.getMedia().getDuration();
                    if ((int)tim1.toSeconds()==count+1) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                t();
                                slider();
                            }
                        });
                        count++;
                    }
                }while(!done);
            }
        }).start();
    }
}

