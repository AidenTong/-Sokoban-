package hk.ust.comp3021.gui.component.board;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.game.RenderingEngine;
import hk.ust.comp3021.gui.utils.Message;
import hk.ust.comp3021.gui.utils.Resource;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Control logic for a {@link GameBoard}.
 * <p>
 * GameBoardController serves the {@link RenderingEngine} which draws the current game map.
 */
public class GameBoardController implements RenderingEngine, Initializable {
    @FXML
    private GridPane map;

    @FXML
    private Label undoQuota;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Draw the game map in the {@link #map} GridPane.
     *
     * @param state The current game state.
     */
    @Override
    public void render(@NotNull GameState state) {
        //todo
        Platform.runLater(() -> {
            map.getChildren().removeAll();
            for (int y = 0; y < state.getMapMaxHeight(); y++) {
                for (int x = 0; x < state.getMapMaxWidth(); x++) {
                    Cell cell;
                    var entity = state.getEntity(Position.of(x, y));
                    try {
                        cell = new Cell();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(entity instanceof Box){

                        Box box= (Box) entity;

                        if(ifDestinations(x,y,state.getDestinations())){
                            cell.getController().markAtDestination();
                        }
                        cell.getController().setImage(Resource.getBoxImageURL(box.getPlayerId()));
                    } else if (entity instanceof  Player) {
                        Player player= (Player) entity;
                        cell.getController().setImage(Resource.getPlayerImageURL(player.getId()));

                    }else if (entity instanceof Wall) {
                        cell.getController().setImage(Resource.getWallImageURL());
                    }else if (entity instanceof Empty) {

                        cell.getController().setImage(Resource.getEmptyImageURL());

                        if(ifDestinations(x,y,state.getDestinations())){
                            cell.getController().setImage(Resource.getDestinationImageURL());
                        }

                    }
                    map.add(cell,x,y);
                }
            }

            if (state.getUndoQuota().get() >= 0) {
                undoQuota.setText("Undo Quota:" + state.getUndoQuota().get());
            } else {
                undoQuota.setText("Undo Quota:unlimited");
            }

        });
    }

    public boolean ifDestinations(int x,int y,Set<Position> destinations ){
        for(Position pos:destinations){
            if(x==pos.x()&&y==pos.y()){
                return true;
            }
        }
        return false;
    }
    /**
     * Display a message via a dialog.
     *
     * @param content The message
     */
    @Override
    public void message(@NotNull String content) {
        Platform.runLater(() -> Message.info("Sokoban", content));
    }
}
