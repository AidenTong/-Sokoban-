package hk.ust.comp3021.gui.scene.start;

import hk.ust.comp3021.gui.App;
import hk.ust.comp3021.gui.component.maplist.MapEvent;
import hk.ust.comp3021.gui.component.maplist.MapList;
import hk.ust.comp3021.gui.component.maplist.MapModel;
import hk.ust.comp3021.gui.utils.Message;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Control logic for {@link  StartScene}.
 */
public class StartController implements Initializable {
    @FXML
    private MapList mapList;

    @FXML
    private Button deleteButton;

    @FXML
    private Button openButton;

    App app;
    /**
     * Initialize the controller.
     * Load the built-in maps to {@link this#mapList}.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            mapList.getItems().add(MapModel.load(getClass().getClassLoader().getResource("map00.map")));
            mapList.getItems().add(MapModel.load(getClass().getClassLoader().getResource("map01.map")));
            deleteButton.setDisable(true);
            openButton.setDisable(true);

            mapList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(mapList.getSelectionModel().getSelectedItem()!=null){
                        deleteButton.setDisable(false);
                        openButton.setDisable(false);
                    }else {
                        deleteButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setApp(App app){
        this.app=app;
    }
    /**
     * Event handler for the open button.
     * Display a file chooser, load the selected map and add to {@link this#mapList}.
     * If the map is invalid or cannot be loaded, display an error message.
     *
     * @param event Event data related to clicking the button.
     */
    @FXML
    private void onLoadMapBtnClicked(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.map"));
        File selectedFile = fileChooser.showOpenDialog(null);
        loadMap(selectedFile);
    }

    public void loadMap(File mapfile) throws MalformedURLException {
        if (mapfile != null) {
            boolean hassame=false;
            MapModel mapModel=null;
            try {
                mapModel=MapModel.load(new URL("file:///"+mapfile.getAbsolutePath()));
            }catch (Exception e){
                Message.error("invalid map",e.getMessage());
                return;
            }
            for (int i = 0; i < mapList.getItems().size(); i++) {
                if(mapList.getItems().get(i).file().toAbsolutePath().toString().equals(mapfile.getAbsolutePath())){
                    mapList.getItems().set(i,mapModel);
                    hassame=true;
                    break;
                }
            }
            if(!hassame){
                mapList.getItems().add(0,mapModel);
            }
        }
    }
    /**
     * Handle the event when the delete button is clicked.
     * Delete the selected map from the {@link this#mapList}.
     */
    @FXML
    public void onDeleteMapBtnClicked() {
        if(mapList.getSelectionModel().getSelectedItem()!=null){
            mapList.getItems().remove(mapList.getSelectionModel().getSelectedIndex());
        }
    }

    /**
     * Handle the event when the map open button is clicked.
     * Retrieve the selected map from the {@link this#mapList}.
     * Fire an {@link MapEvent} so that the {@link hk.ust.comp3021.gui.App} can handle it and switch to the game scene.
     */
    @FXML
    public void onOpenMapBtnClicked() throws IOException {

        if(mapList.getSelectionModel().getSelectedItem()!=null){
            MapModel mapModel=mapList.getSelectionModel().getSelectedItem();
            MapEvent mapEvent=new MapEvent(MapEvent.OPEN_MAP_EVENT_TYPE,mapModel);
            app.onOpenMap(mapEvent);
        }
    }

    /**
     * Handle the event when a file is dragged over.
     *
     * @param event The drag event.
     * @see <a href="https://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm">JavaFX Drag and Drop</a>
     */
    @FXML
    public void onDragOver(DragEvent event) {
        if (event.getGestureSource() != mapList &&
                event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }

        event.consume();
    }

    /**
     * Handle the event when the map file is dragged to this app.
     * <p>
     * Multiple files should be supported.
     * Display error message if some dragged files are invalid.
     * All valid maps should be loaded.
     *
     * @param dragEvent The drag event.
     * @see <a href="https://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm">JavaFX Drag and Drop</a>
     */
    @FXML
    public void onDragDropped(DragEvent dragEvent) throws IOException {
        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            for (int i = 0; i < db.getFiles().size(); i++) {
                loadMap(db.getFiles().get(i));
            }
            success = true;
        }
        dragEvent.setDropCompleted(success);

        dragEvent.consume();
    }

}
