package uk.ac.soton.seg15.view.scenes;

import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.ac.soton.seg15.view.View;

public abstract class BaseScene extends Scene {

    protected View view;

    protected StackPane root;
    protected Stage stage;


    public BaseScene (StackPane root, Color color, View view,Stage stage) {
        super(root,color);
        root.setPrefWidth(view.getStageWidth());
        root.setPrefHeight(view.getStageHeight());

        this.view = view;
        this.root = root;
        this.stage = stage;

        this.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
//        view.getStage().widthProperty().addListener((observableValue, number, t1) -> {
//            this.build();
//        });
//        view.getStage().heightProperty().addListener((observableValue, number, t1) -> {
//            this.build();
//        });
    }

    public abstract void build();

}
