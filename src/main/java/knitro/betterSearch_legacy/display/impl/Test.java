package knitro.betterSearch_legacy.display.impl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Test extends Application{
	
	private int xPos = 0;
	private int yPos = 0;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		TilePane tile = new TilePane();
	    tile.setTileAlignment(Pos.CENTER_LEFT);
	    tile.setPrefColumns(4);

	    Button b = new Button("add");
	    b.setOnAction(ev -> tile.getChildren().add(newText()));

	    ScrollPane scrollPane = new ScrollPane(tile);
//	    scrollPane.setFitToHeight(true);
	    scrollPane.setFitToWidth(true);

	    BorderPane root = new BorderPane(scrollPane);
	    root.setPadding(new Insets(15));
	    root.setTop(b);

	    Scene scene = new Scene(root, 400, 400);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	    
	    for (int i = 0; i < 20; i++) {
	    	tile.getChildren().add(newText());
	    }
	}
	
	private Text newText() {
		Text currentText = new Text("Test");
		currentText.setFont(new Font(20));
		currentText.setX(xPos);
		currentText.setY(yPos);
		yPos += 40;
		return currentText;
	}

	///////////////////////////////////
	/*Main*/
	///////////////////////////////////
	public static void main(String args[]) {           
		launch(args);      
	}

}
