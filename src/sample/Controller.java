package sample;


import javafx.scene.media.MediaPlayer;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;


/***
 * @author Nicolaj, Niels, Monir, Rasmus
 * @since 11-01-2021
 * @version 1.0
 */


public class Controller {

    ArrayList<String> alTitles = new ArrayList<>(); //ArrayList to get all titles
    ArrayList<String> alArtist = new ArrayList<>(); //ArrayList to get all artist
    ArrayList<String> alPlaylist = new ArrayList<>(); //ArrayList to get all playlist
    ArrayList<String> alSongsForPersonalPlaylist = new ArrayList<>(); //ArrayList to get all songs to personal playlist
    ObservableList<String> olStoreAllPlaylist = FXCollections.observableArrayList(); //ObservableList to store all the playlist
    ObservableList<String> olStoreSongs = FXCollections.observableArrayList(); //ObservableList to store all the songs from DB
    ObservableList<String> olSongsOnPersonalPlaylist = FXCollections.observableArrayList(); //ObservableList to store all the songs on peoples personal playlist
    boolean addToPlaylistWorkaround = true; //Work around so the action handler will only give a output once.
    String playlistChosen = ""; //Will store which playlist is chosen.
    boolean personPlaylistChosenOrNot = false; //Avoid it from
    MediaPlayer mediaPlayer; //Is the MediaPlayer
    Duration stopTime; //Is used to pause and stop the song
    Duration tim1, tim2; //Timer for the MediaPlayer to save the time
    int min; //Store the timeframe of the song in minutes
    int count = 0; //Is counting the the timeframe and make it run smooth
    boolean done = false; //Is stopping the do while loop so its not starting multiple threads.
    int myCount;
    boolean isPlaying = false;

    @FXML
    ListView lMusiclist, lPlaylist, lPlaylistSong;

    @FXML
    Button bContinue, bStart, bPause, bStop, bCreate, bOpenPlaylist, bClosePlaylist, bPlayThrough;

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

    /***
     * This function intialize will start whenever the program is opened. This gets all the information about playlistes etc, so its all loaded when the programs open.
     */
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

    /***
     * HandleMusic happens when the play buttom is pressed. This will load all information about what song is loaded and play it.
     */
    public void handleMusic() throws InterruptedException {
        try {

        if(isPlaying == true) {
            mediaPlayer.stop();
            isPlaying = false;
        }
            isPlaying = true;

        String song = "";

        if (!lMusiclist.getSelectionModel().isEmpty()) {
            song = (String) lMusiclist.getSelectionModel().getSelectedItem();
        } else if (lMusiclist.getSelectionModel().isEmpty()) {
            song = (String) lPlaylistSong.getSelectionModel().getSelectedItem();
        }

        DB.selectSQL("Select fldFilePath from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-") - 1) + "'");
        String dataSong = DB.getData();

        DB.selectSQL("Select fldArtist from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-") - 1) + "'");
        String dataArtist = DB.getData();

        DB.selectSQL("Select fldTitle from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-") - 1) + "'");
        String dataTitle = DB.getData();

        DB.selectSQL("Select fldAlbumPicture from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-") - 1) + "'");
        String dataAlbumPicture = DB.getData();

        DB.selectSQL("Select fldLength from tblInformation WHERE fldTitle = '" + song.substring(0, song.indexOf("-") - 1) + "'");
        String length = DB.getData();

        clearDatabaseConnection();

        Media MediaPlayer = new Media(Paths.get(dataSong).toUri().toString());
        mediaPlayer = new MediaPlayer(MediaPlayer);
        mediaPlayer.play();

       // bStart.setVisible(false);
        bPause.setVisible(true);
        bStop.setVisible(true);
        lArtist.setText("by: " + dataArtist);
        lSong.setText("Currently playing: " + dataTitle);
        lTotalTime.setText(length);
        lArtist.setVisible(true);
        lSong.setVisible(true);
        iAlbum.setImage(new Image(getClass().getResourceAsStream("" + dataAlbumPicture + "")));
        iAlbum.setVisible(true);


        done = false;
        count = 0;
        currentTimeLabel();
        }catch (Exception e) {
            System.out.println("You didnt chose a song, this made a error.");
        }

    }

    /***
     * Pause will pause the song
     */
    public void pause() {
        mediaPlayer.pause();
        stopTime = mediaPlayer.getCurrentTime();
        System.out.println(stopTime);
        bPause.setVisible(false);
        bContinue.setVisible(true);
    }

    /***
     * HandleContinue will resume the song after its paused
     * @param actionEvent - starts on press
     */
    public void handleContinue(ActionEvent actionEvent) {
        mediaPlayer.setStartTime(stopTime);
        mediaPlayer.play();
        bContinue.setVisible(false);
        bPause.setVisible(true);
    }

    /***
     * A search function which search by songs or artist it is automatic updated whenever a key is pressed.
     * @param keyEvent - will search whenever any key is pressed.
     */
    public void handleSearchFunction(KeyEvent keyEvent) {
        ObservableList<String> songSearch = FXCollections.observableArrayList();

        String searchString = tSearch.getText();

        for (int i = 0; i < alTitles.size(); i++) {
            if (alTitles.get(i).toLowerCase().contains(searchString.toLowerCase())
                    || alArtist.get(i).toLowerCase().contains(searchString.toLowerCase())) {

                songSearch.add(alTitles.get(i) + " - " + alArtist.get(i));
            }
        }
        lMusiclist.setItems(songSearch);
    }

    /***
     * handleStopMusic will stop the song and reset everything.
     * @param actionEvent - stop the music as soon the bottom is pressed
     */
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

    /***
     * handleCreatePlayListMenu will open a new window where you are able to create your own playlist with whatever name you want, unless its already taken.
     * the database will store the playlist which is created.
     * @param actionEvent - will open a new window as soon the bottom is pressed.
     * @throws IOException
     */
    public void handleCreatePlaylistMenu(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("createPlaylist.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.setTitle("Playlist creator");
        stage.setScene(scene);
        stage.show();

    }

    /***
     * This is a function which is called from other methods that will update the playlist
     * this is made so the playlist automatic will be updated as soon anything is changed, so you dont have to reset the program.
     */
    public void updatePlaylist() {

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

    /***
     * This is a function which is called from other methods that will update the personal playlist
     * this is made so the personal playlist automatic will be updated as soon anything is changed, so you dont have to reset the program.
     */
    public void updatePersonPlaylist() {

        alSongsForPersonalPlaylist.clear();
        lPlaylistSong.getItems().clear();

        DB.selectSQL("Select fldSongNames from " + playlistChosen.replaceAll("\\s", "") + "");
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

    /***
     * This is used so it will clear and update the playlist, so its not going to be made double.
     */
    public void handleUpdatePlaylistAfter() {
        alPlaylist.clear();
        lPlaylist.getItems().clear();
        updatePlaylist();

    }


    /***
     * This method will open the playlist, and show all the song that is on it.
     * @param actionEvent - will open the playlist as soon as the bottom is pressed.
     */
    public void handleOpenPlaylist(ActionEvent actionEvent) {
        try {
        personPlaylistChosenOrNot = true;
        playlistChosen = (String) lPlaylist.getSelectionModel().getSelectedItem();
        DB.selectSQL("Select fldSongNames from " + playlistChosen.replaceAll("\\s", "") + "");

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
        bPlayThrough.setVisible(true);

        }catch (Exception e) {
            System.out.println("You didnt chose a playlist to open, this made an error");
        }

    }

    /***
     * This method will close the playlist, and will it again show all the personal playlist.
     * @param actionEvent - will open as soon the bottom is pressed.
     */
    public void handleClosePlaylist(ActionEvent actionEvent) {

        personPlaylistChosenOrNot = false;
        alSongsForPersonalPlaylist.clear();
        olSongsOnPersonalPlaylist.clear();

        lPlaylist.setVisible(true);
        lPlaylistSong.setVisible(false);
        bOpenPlaylist.setVisible(true);
        bClosePlaylist.setVisible(false);
        bPlayThrough.setVisible(false);

    }

    /***
     * Clears the musiclist
     * @param mouseEvent
     */
    public void handleClearMainPlayList(MouseEvent mouseEvent) {
        lMusiclist.getSelectionModel().clearSelection();
    }

    /***
     * Clears the playlist playlist
     * @param mouseEvent
     */
    public void handleClearPersonPlaylist(MouseEvent mouseEvent) {
        lPlaylistSong.getSelectionModel().clearSelection();

    }

    /***
     * Adds a song to the playlist selected from musicplaylist to a specific playlist from a combobox
     * @param eventAdd
     */

    public void handleAddtoPlaylist(ActionEvent eventAdd) {

        String getWhichPlaylistToAddTo = (String) cAddToPlaylist.getSelectionModel().getSelectedItem();


        if (addToPlaylistWorkaround == true) {


            String fixedSongName = lMusiclist.getSelectionModel().getSelectedItem().toString();
            fixedSongName.replaceAll("\\[", "");
            fixedSongName.replaceAll("\\]", "");
            String test = "INSERT INTO " + getWhichPlaylistToAddTo.replaceAll("\\s", "") + " VALUES ('" + fixedSongName + "')";

            addToPlaylistWorkaround = false;
            DB.insertSQL(test);

            if(personPlaylistChosenOrNot == true)
            {
                updatePersonPlaylist();
            }

        } else {
            addToPlaylistWorkaround = true;
        }
    }

    /***
     * Removes a song from the user playlist that is selected in the listview
     * @param actionEvent Button to delete song
     */
    public void handleRemoveSong(ActionEvent actionEvent) {

        String fixedSongName = lPlaylistSong.getSelectionModel().getSelectedItem().toString();
        fixedSongName.replaceAll("\\[", "");
        fixedSongName.replaceAll("\\]", "");

        System.out.println(fixedSongName);
        DB.deleteSQL("Delete FROM " + playlistChosen.replaceAll("\\s", "") + " Where fldSongNames = '" + fixedSongName + "'");

        updatePersonPlaylist();
    }

    /***
     * Deletes a selected playlist from the listview showcasing playlists
     * @param actionEvent the delete button
     */
    public void handleDeletePlaylist(ActionEvent actionEvent) {

        String NotfixedPlaylist = lPlaylist.getSelectionModel().getSelectedItem().toString();
        String fixedPlaylist = NotfixedPlaylist.replaceAll("\\s", "");

        System.out.println(fixedPlaylist);
        DB.deleteSQL("DROP TABLE " + fixedPlaylist + "");
        DB.deleteSQL("Delete FROM tblAllPlaylist WHERE fldPlaylist = '" + NotfixedPlaylist + "'");

        handleUpdatePlaylistAfter();

    }


    /***
     * Gets the length of the song and makes it possible to skip in the song in a certain song period
     */
    public void slider() {
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
     * This function will skip the time so you get in that point of the song that you want.
     * @param event mouseevent checks where on the slider you press
     */
    @FXML
    public void sliderChoice(MouseEvent event) {
        double dx = event.getX();
        double dWidth = sProgress.getWidth();
        double progression = (dx / dWidth);
        double mili = (progression * mediaPlayer.getTotalDuration().toMillis());
        Duration duration = new Duration(mili);
        mediaPlayer.seek(duration);
        count = (int) mediaPlayer.getCurrentTime().toSeconds();
        sProgress.setValue(sProgress.getValue());
        mediaPlayer.pause();
        mediaPlayer.play();
        t();
        slider();
    }


    /***
     * Sets the time on the right place and will format the time in a nice way
     */
    public void t() {
        min = count / 60;
        if (count == 0 || count - (min * 60) == 0) {
            lCurrentTime.setText(String.format("%d:00", min));

        } else if (count < 10 || count - (min * 60) < 10) {
            lCurrentTime.setText(String.format("%d:0%d", min, count - (min * 60)));

        } else {
            lCurrentTime.setText(String.format("%d:%d", min, count - (min * 60)));

        }
    }


    /***
     * This makes the song start and stop every milisecond of the song and checks when a second has passed and updated the
     * slider and timer
     */
    public void currentTimeLabel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    tim1 = mediaPlayer.getCurrentTime();
                    tim2 = mediaPlayer.getMedia().getDuration();
                    if ((int) tim1.toSeconds() == count + 1) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                t();
                                slider();
                            }
                        });
                        count++;
                    }
                } while (!done);
            }
        }).start();
    }

    public void clearDatabaseConnection()
    {
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                System.out.println(data);
            }
        } while (true);
    }

    public String repeatSongInitialize(String song) {

        DB.selectSQL("Select fldArtist from tblInformation WHERE fldFilePath = '" + song + "'");
        String dataArtist = DB.getData();

        DB.selectSQL("Select fldTitle from tblInformation WHERE fldFilePath = '" + song + "'");
        String dataTitle = DB.getData();

        DB.selectSQL("Select fldAlbumPicture from tblInformation WHERE fldFilePath = '" + song + "'");
        String dataAlbumPicture = DB.getData();

        DB.selectSQL("Select fldLength from tblInformation WHERE fldFilePath = '" + song + "'");
        String length = DB.getData();

        clearDatabaseConnection();

        bPause.setVisible(true);
        bStop.setVisible(true);
        lArtist.setText("by: " + dataArtist);
        lSong.setText("Currently playing: " + dataTitle);
        lTotalTime.setText(length);
        lArtist.setVisible(true);
        lSong.setVisible(true);
        iAlbum.setImage(new Image(getClass().getResourceAsStream("" + dataAlbumPicture + "")));
        iAlbum.setVisible(true);
        done = false;
        count = 0;
        currentTimeLabel();
        return "Hej";
    }



    String currentSong;

    Iterator<String> itr;
    ArrayList<String> alAutoplay = new ArrayList<>();
    public void repeatPlaylist(ActionEvent actionEvent) {
        alAutoplay.clear();

        if(isPlaying == true) {
            mediaPlayer.stop();
            isPlaying = false;
        }
        isPlaying = true;
        for (int i = 0; i < alSongsForPersonalPlaylist.size(); i++) {
            String playlistSongs = alSongsForPersonalPlaylist.get(i);
            DB.selectSQL("Select fldFilePath from tblInformation WHERE fldTitle = '" + playlistSongs.substring(0, playlistSongs.indexOf("-") - 1) + "'");
            String dataSong = DB.getData();
            alAutoplay.add(dataSong);
        }
        clearDatabaseConnection();
        System.out.println(alAutoplay);

        itr = alAutoplay.iterator();
        play(itr.next());

    }

    int repeatThroughImages = 0;

    public void play(String mediaFile){

        Media MediaPlayer = new Media(Paths.get(mediaFile).toUri().toString());
        mediaPlayer = new MediaPlayer(MediaPlayer);
        mediaPlayer.play();
        repeatSongInitialize(alAutoplay.get(repeatThroughImages));
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
                repeatThroughImages++;
                if (itr.hasNext()) {
                    //Plays the subsequent files
                    play(itr.next());
                }
                else {
                    iAlbum.setVisible(false);
                    lArtist.setVisible(false);
                    lSong.setVisible(false);
                }
                return;
            }
        });
    }
}




