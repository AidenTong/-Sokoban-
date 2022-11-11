package hk.ust.comp3021.gui.component.control;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.actions.Move;
import hk.ust.comp3021.actions.Undo;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.game.InputEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Control logic for a {@link ControlPanel}.
 * ControlPanelController serves as {@link InputEngine} for the game.
 * It caches users input (move actions) and provides them to the {@link hk.ust.comp3021.gui.scene.game.GUISokobanGame}.
 */
public class ControlPanelController implements Initializable, InputEngine {
    @FXML
    private FlowPane playerControls;

    List<MovementButtonGroup> movementButtonGroupList=new ArrayList<>();
    List<Undo> undoList=new ArrayList<>();
    int playId;
     volatile boolean undo=false;
    /**
     * Fetch the next action made by users.
     * All the actions performed by users should be cached in this class and returned by this method.
     *
     * @return The next action made by users.
     */
    @Override
    public @NotNull Action fetchAction() {
        while (!undo){
            for (int i = 0; i < movementButtonGroupList.size(); i++) {
                if(movementButtonGroupList.get(i).getController().action!=null){
                    playId=movementButtonGroupList.get(i).getController().action.getInitiator();
                    undoList.add(new Undo(playId));
                    if(movementButtonGroupList.get(i).getController().action instanceof Move.Down){
                        movementButtonGroupList.get(i).getController().action=null;
                        return new Move.Down(playId);
                    }else if(movementButtonGroupList.get(i).getController().action instanceof Move.Up){
                        movementButtonGroupList.get(i).getController().action=null;
                        return new Move.Up(playId);
                    }else if(movementButtonGroupList.get(i).getController().action instanceof Move.Left){
                        movementButtonGroupList.get(i).getController().action=null;
                        return new Move.Left(playId);
                    }else if(movementButtonGroupList.get(i).getController().action instanceof Move.Right){
                        movementButtonGroupList.get(i).getController().action=null;
                        return new Move.Right(playId);
                    }
                }
            }
        }
        undo=false;
        if(undoList.size()>1){
            return undoList.remove(undoList.size()-1);
        }else{
            return undoList.get(0);
        }
    }

    /**
     * Initialize the controller as you need.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO
    }

    /**
     * Event handler for the undo button.
     * Cache the undo action and return it when {@link #fetchAction()} is called.
     *
     * @param event Event data related to clicking the button.
     */
    public void onUndo(ActionEvent event) {
        undo=true;
    }


    /**
     * Adds a player to the control player.
     * Should add a new movement button group for the player.
     *
     * @param player         The player.
     * @param playerImageUrl The URL to the profile image of the player
     */
    public void addPlayer(Player player, URL playerImageUrl) {
        try {
            MovementButtonGroup movementButtonGroup =new MovementButtonGroup();
            movementButtonGroup.getController().setPlayer(player);
            movementButtonGroup.getController().setPlayerImage(playerImageUrl);
            playerControls.getChildren().add(movementButtonGroup);
            movementButtonGroupList.add(movementButtonGroup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
