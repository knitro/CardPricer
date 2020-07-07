package knitro.betterSearch_legacy.display.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import knitro.betterSearch_legacy.database.Database;
import knitro.betterSearch_legacy.database.DatabaseImpl;
import knitro.betterSearch_legacy.database.card.DbItem;
import knitro.betterSearch_legacy.database.filter.CardColour;
import knitro.betterSearch_legacy.database.filter.CardType;
import knitro.betterSearch_legacy.database.filter.Filter;
import knitro.betterSearch_legacy.priceGetter.PriceGetter;
import knitro.betterSearch_legacy.priceGetter.info.CardImage;
import knitro.betterSearch_legacy.priceGetter.info.CardInfo;
import knitro.betterSearch_legacy.priceGetter.scg.StarCityGames;
import knitro.betterSearch_legacy.search.Search;
import knitro.betterSearch_legacy.search.Style;
import knitro.betterSearch_legacy.search.impl.SearchImpl;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import knitro.support.InvalidModeException;
import knitro.support.Preconditions;

public class JavaFxApplication extends Application {

	///////////////////////////////////
	/*Enums*/
	///////////////////////////////////
	
	private enum SearchMode {
		STANDARD, LEGACY, TEST
	}
	
	///////////////////////////////////
	/*Fields*/
	///////////////////////////////////
	
	/*Worker Objects*/
	private PriceGetter priceGetter;
	private Database database;
	
	/*JavaFx Fields*/
	private Stage primaryStage;
	
	/*Active Searching Fields*/
	private TilePane activeSearchingPane_content;
	private ScrollPane activeSearchingContentPane_display;
	
	/*Display Values*/
	private static final double WIDTH = 1280;
	private static final double HEIGHT = 720;
	
	private static final double DEFAULT_IMAGE_HEIGHT = 659;
	private static final double DEFAULT_IMAGE_WIDTH = 473;
	
	private double displayImageGap = WIDTH/64;
	private double displayImageHeight = DEFAULT_IMAGE_HEIGHT/2.0;
	private double displayImageWidth = DEFAULT_IMAGE_WIDTH/2.0;
	private double displayEnlargeFactor = 1.1;
	
	private static final double CARDNAME_WIDTH = WIDTH/3;
	private static final double CARDTYPE_WIDTH = WIDTH/3;
	private static final double CARDTEXT_WIDTH = WIDTH/3;
	
	/*Scene Recovery*/
	private int maxPreviousScenes = 10;
	private List<Scene> previousScenes;
	
	/*Adjustable Settings*/
	//Make Drop Down Menu for these Settings:
	private int marginOfError;
	private Style currentStyle; //Determines foil/non-foil/any
	private SearchMode currentSearchMode;
	private Color highlightColour;
	private Color defaultColour;
	private boolean isBuy;
	
	/*Adjustable Filter*/
	private Filter filter;
	
	///////////////////////////////////
	/*Constructors*/
	///////////////////////////////////
	
	public JavaFxApplication() {
		super();
		
		/*Create the Appropriate Field Instances*/
		this.priceGetter = new StarCityGames();
		this.previousScenes = new ArrayList<>();
		this.database = new DatabaseImpl();
		
		/*Set Default Settings*/
		this.currentStyle = Style.ANY;
		this.currentSearchMode = SearchMode.STANDARD;
		this.marginOfError = 1;
		this.highlightColour = Color.ORANGE;
		this.isBuy = false;
		
		/*Set Default Filter*/
		//filter = new Filter();
		filter = new Filter(
				"UMA", 
//				Filter.NO_FILTER_SETCODE,
				new HashSet<CardColour>(Arrays.asList(CardColour.BLUE)),
				new HashSet<CardType>(Arrays.asList(CardType.CREATURE)), 
				3, 
				Style.NON_FOIL);
		
		/*Database Initialisation*/
		if (!database.isLoaded()) {
			database.loadDatabase();
		}
	}
	
	
	///////////////////////////////////
	/*Overridden Methods*/
	///////////////////////////////////
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;	
		displaySearch(primaryStage);
	}
	
	///////////////////////////////////
	/*Button Addition Methods*/
	///////////////////////////////////
	
	private void addTopButtons(BorderPane currentPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Add Buttons*/
		HBox topPane = new HBox();
		addHomeButton(topPane);
		addBackButton(topPane);
		addSettings(topPane, currentPane);
		addFilters(topPane, currentPane);
		currentPane.setTop(topPane);
	}
	
	private void addHomeButton(Pane currentPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Create Button*/
		String filePath = "images/topButtons/home.png";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image imageFX = SwingFXUtils.toFXImage(img, null);
		ImageView button = new ImageView(imageFX);
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				displaySearch(primaryStage);
			}
		});
		
		/*Add Button*/
		currentPane.getChildren().add(button);
	}
	
	private void addBackButton(Pane currentPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Create Button*/
		String filePath = "images/topButtons/back.png";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image imageFX = SwingFXUtils.toFXImage(img, null);
		ImageView button = new ImageView(imageFX);
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				loadPreviousScene(primaryStage);
			}
		});
		
		/*Add Button*/
		currentPane.getChildren().add(button);
	}
	
	private void addSettings(Pane currentPane, BorderPane borderPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Create Button*/
		String filePath = "images/topButtons/settings.png";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image imageFX = SwingFXUtils.toFXImage(img, null);
		ImageView button = new ImageView(imageFX);
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addSettings_all(borderPane);
			}
		});
		
		/*Add Button*/
		currentPane.getChildren().add(button);
	}
	
	private void addFilters(Pane currentPane, BorderPane borderPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Create Button*/
		String filePath = "images/topButtons/filter.png";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image imageFX = SwingFXUtils.toFXImage(img, null);
		ImageView button = new ImageView(imageFX);
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addFilters_all(borderPane);
			}
		});
		
		/*Add Button*/
		currentPane.getChildren().add(button);
	}


	///////////////////////////////////
	/*Settings: RadioButton Addition Methods*/
	///////////////////////////////////
	
	private void addSettings_all(BorderPane currentPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Setting 1: Active Searching Enable/Disable*/
		
		/*Setting 1: Active Searching Enable/Disable*/
		
	}
	
	///////////////////////////////////
	/*Settings: RadioButton Addition Methods*/
	///////////////////////////////////
	
	private void addFilters_all(BorderPane currentPane) {
	
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Box Initialisation*/
		HBox bottomPane = new HBox();
		
		/*Filter 1: Set*/
		addFilters_set(bottomPane);
		
		/*Filter 2: Colour*/
		addFilters_colour(bottomPane);
		
		/*Filter 3: Type*/
		addFilters_type(bottomPane);
		
		/*Filter 4: Converted Mana Cost*/
		addFilters_cmc(bottomPane);
		
		/*Filter 5: Style (Foil/Non-Foil)*/
		addFilters_style(bottomPane);
		
		/*Add Box to Pane*/
		currentPane.setBottom(bottomPane);
	}
	
	private void addFilters_set(Pane currentPane) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentPane != null, "currentPane is null");
		
		/*Variable Initialisation*/
		final boolean existsPreviousInfo = filter.checkSetCode() ? true : false;
		final String info = existsPreviousInfo ? filter.getSetCode() : null;
		
		/*Create Button on BorderPane*/
		Button filterButton = new Button("Set: ???");
		if (existsPreviousInfo) {
			filterButton.setText("Set: " + info);
		}
		
		/*Get List of Sets*/
		Map<String, String> setListMap = database.getSetMap();
		List<String> setList = new ArrayList<>();
		for (String setCode : setListMap.keySet()) {
			String setName = setListMap.get(setCode);
			String addString = setCode + " - " +setName;
			setList.add(addString);
		}
		setList.sort(Comparator.naturalOrder());
		
		filterButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				ChoiceDialog<String> dialog = new ChoiceDialog<>(info, setList);
	
				dialog.setTitle("Filter: Choose the Set");
				dialog.setHeaderText("Choose the Set you want to Filter to:");
				dialog.setContentText("Set:");
				
				Optional<String> result = dialog.showAndWait();
				result.ifPresent(setCode -> filter.extractAndApplySetCode(setCode));
			}
		});
		filterButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				filterButton.setTextFill(highlightColour);
			}
		});
		filterButton.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				filterButton.setTextFill(defaultColour);
			}
		});
		
		currentPane.getChildren().add(filterButton);
	}
	
	private void addFilters_colour(Pane currentPane) {
			
	}
	
	private void addFilters_type(Pane currentPane) {
		
	}
	
	private void addFilters_cmc(Pane currentPane) {
			
	}
	
	private void addFilters_style(Pane currentPane) {
		
	}
	
	///////////////////////////////////
	/*Scene Loading/Back Button Methods*/
	///////////////////////////////////
	
	private void loadScene(Stage primaryStage, Scene currentScene) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
		Preconditions.preconditionCheck(currentScene != null, "currentScene is null");
	
		primaryStage.setTitle("BetterSearch: Loaded Previous");
		primaryStage.setScene(currentScene);
		primaryStage.show();
		addPreviousScene(currentScene);
	}
	
	private void loadPreviousScene(Stage primaryStage) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
	
		/*Get Previous Scene and loadScene*/
		int last2Index = previousScenes.size() - 2;
		if (last2Index >= 0) {
			int last1Index = previousScenes.size() - 1;
			previousScenes.remove(last1Index);
			Scene previousScene = previousScenes.remove(last2Index);
			loadScene(primaryStage, previousScene);
		} else {
			//Nothing
			System.out.println("No Valid Scene to revert to!");
		}
	}
	
	private void addPreviousScene(Scene currentScene) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(currentScene != null, "currentScene is null");
		
		/*Add Previous Scene to Storage*/
		//Check if Storage is Full or Not, remove very last scene if so
		if (previousScenes.size() >= maxPreviousScenes) {
			previousScenes.remove(0);
		}
		
		//Add Previous Scene
		previousScenes.add(currentScene);
	}
	
	///////////////////////////////////
	/*Searching Methods*/
	///////////////////////////////////
	
	private void beginSearch_buy(String textField_text) {
		beginSearch(textField_text, true);
	}  
	
	private void beginSearch_sell(String textField_text) {
		beginSearch(textField_text, false);
	}  
	
	private void beginSearch(String textField_text, boolean isBuy) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(textField_text != null, "textField_text is null");
		
		/*Create SearchTerm instance*/
		Search currentSearch  = new SearchImpl(textField_text, isBuy, marginOfError, filter);
		
		/*Process Search*/
		if (currentSearchMode == SearchMode.STANDARD) {
			processSearch_standard(currentSearch);
		} else if (currentSearchMode == SearchMode.LEGACY) {
			processSearch_legacy(currentSearch);
		} else if (currentSearchMode == SearchMode.TEST) {
			processSearch_test(currentSearch);
		} else {
			throw new InvalidModeException("ERROR, currentSearchMode unknown");
		}
		
	}
	
	private void processSearch_standard(Search searchTerm) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(searchTerm != null, "searchTerm is null");
		
		/*Get possible cards*/
		Set<DbItem> possibleCards = database.getCards(searchTerm);
		
		/*Check for specific conditions*/
		//Check if no output
		int numOfResults = possibleCards.size();
		if (numOfResults == 0) {
			String message = "Oops, no cards of the name have been found.";
			displayMessage(primaryStage, message); 
		}
		//Check if 1 output
		else if (numOfResults == 1) {
			
			for (DbItem query : possibleCards) { //Note: This loop should only once
				Set<String> printings = query.getPrintings();
				
				//Check if there is only 1 printing :: Shortcut does not work. Collectors number is required
				/*
				if (printings.size() == 1) {
					
				} else {
					
				}
				*/
				String cardName = query.getName();
				Search updatedSearch = new SearchImpl(cardName, searchTerm);
				List<CardInfo> results = priceGetter.getCardInfo_sell(updatedSearch);
				displayResults(primaryStage, results);
			} 
		}
		else {
			displayRefineSearch(primaryStage, possibleCards, searchTerm);
		}
	}
	
	private void processSearch_legacy(Search searchTerm) {
	
		/*Preconditions*/
		Preconditions.preconditionCheck(searchTerm != null, "searchTerm is null");
		
		if (searchTerm.getIsBuy()) {
			
			List<CardInfo> results = priceGetter.getCardInfo_buy(searchTerm);
			displayResults(primaryStage, results);
					
		} else if (searchTerm.getIsSell()) {
			
			List<CardInfo> results = priceGetter.getCardInfo_sell(searchTerm);
			displayResults(primaryStage, results);
			
		} else {
			throw new InvalidModeException("Invalid Search Mode");
		}
		
	}
	
	private void processSearch_test(Search searchTerm) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(searchTerm != null, "searchTerm is null");
		
		System.out.println("Not implemented yet");
		throw new RuntimeException("processSearch_test not implemented");
	}

	///////////////////////////////////
	/*Display Methods*/
	///////////////////////////////////
	
	private void displaySearch(Stage primaryStage) {
		
		/*Stage Initialisation*/
		primaryStage.setTitle("BetterSearch: Search Screen");
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setSpacing(HEIGHT/64);
		BorderPane root = new BorderPane(box);
		Scene mainScene = new Scene(root, WIDTH, HEIGHT);
		primaryStage.setScene(mainScene);
		primaryStage.show();
		addPreviousScene(mainScene);
		
		addTopButtons(root);
		
		/*Text*/
		double text_fontSize = WIDTH/64;
		Text searchTitleText = new Text();
		searchTitleText.setFont(new Font(text_fontSize));
		searchTitleText.setText("Type in your card name below to get Searching");
//		double textWidth = searchTitleText.getLayoutBounds().getWidth();
		
//		searchTitleText.setX((WIDTH/2) - (textWidth/2));
//		searchTitleText.setY((HEIGHT/2) - text_fontSize);
		box.getChildren().add(searchTitleText);
		
		/*Line*/
//		Line line = new Line(); 
		     
		//Setting the properties to a line 
//		line.setStartX(0.0); 
//		line.setStartY(HEIGHT/2.0); 
//		line.setEndX(WIDTH); 
//		line.setEndY(HEIGHT/2.0);
//		root.getChildren().add(line);
		
		/*Text Field*/
		TextField textField = new TextField();
		double textField_fontSize = HEIGHT/15;
		double textField_height = HEIGHT/10;
		
		//Setting the properties of the text field
		textField.setLayoutX(WIDTH/4.0); 
		textField.setLayoutY(HEIGHT/2.0); 
		textField.setPrefWidth(WIDTH/2.0);
		textField.setMaxWidth(WIDTH/2.0);
		textField.setPrefHeight(textField_height);
		textField.setMaxHeight(textField_height);
		textField.setFont(new Font(textField_fontSize));
		   
		//Handling the key typed event 
		EventHandler<KeyEvent> eventHandlerTextField = new EventHandler<KeyEvent>() { 
			@Override 
			public void handle(KeyEvent event) { 
				
				String characterPressed = event.getCode().toString();
				String textField_text = textField.getText();
				
				System.out.println("Key Pressed: \"" + characterPressed + "\"");
				System.out.println("Text Field Text: " + textField_text + "\""); 
				
				//Check if Enter is Pressed, then begin search
				if (event.getCode() == KeyCode.ENTER) {
					System.out.println("Beginnning Search for: " + textField_text);
					beginSearch_sell(textField_text);
				} else {
					/*
					 * TODO:: Add active search functionality
					 * This functionality can be implemented if a downloaded scryfall database is available.
					 */
//					final String searchString = textField_text + characterPressed;
//					Search currentSearch = new SearchImpl(searchString, isBuy, marginOfError, currentStyle);
//					Set<DbQuery> possibleCards = database.getCards(currentSearch);
//					displayActiveSearching(root, possibleCards, currentSearch);
				}
			}
		};              
		//Adding an event handler to the text field and a textProperty listener
		textField.addEventHandler(KeyEvent.KEY_PRESSED, eventHandlerTextField);
		//Use of textPropertyListener is since eventHandler for KeyEvent is 1 char delayed
		textField.textProperty().addListener((obj, oldVal, newVal)  -> {
			String textField_text = textField.getText();
			Search currentSearch = new SearchImpl(textField_text, isBuy, marginOfError, filter);
			Set<DbItem> possibleCards = database.getCards(currentSearch);
			displayActiveSearching(box, possibleCards, currentSearch);
		});
//		root.setCenter(textField);
		box.getChildren().add(textField);
	}
	
	private void displayLoading(Stage primaryStage) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
		
		/*Stage Initialisation*/
		//TODO::
	}
	
	private void displayResults(Stage primaryStage, List<CardInfo> results) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
		Preconditions.preconditionCheck(results != null, "results is null");
		
		/*Stage Initialisation*/
		primaryStage.setTitle("BetterSearch: Displaying " + results.size() + " Results");

//		TilePane box = new TilePane();
		AnchorPane box = new AnchorPane();
//		box.setHgap(displayImageGap);
//		box.setVgap(displayImageGap);
//		box.setPrefColumns(5);
		
	    ScrollPane scrollPane = new ScrollPane(box);
	    scrollPane.setFitToWidth(true);
	    scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
	    
	    BorderPane root = new BorderPane(scrollPane);
	    root.setPadding(new Insets(0));
		
	    Scene mainScene = new Scene(root, WIDTH, HEIGHT);
		primaryStage.setScene(mainScene);
		primaryStage.show();
		addPreviousScene(mainScene);
		addTopButtons(root);
		
		/*Displaying Images*/
		
		//Loop Initialisation
		double xOffset = ((WIDTH - (2 * displayImageGap)) % (displayImageWidth + displayImageGap)) / 2;
		double xPos = xOffset;
		double yPos = displayImageGap;
		
		//Loop
		for (CardInfo card : results) {
			
			//Variable Initialisation
			String webURL = card.getCardURL();
			
			//Get Image
			CardImage cardImage = card.getCardImage();
			java.awt.Image imageBuffered = cardImage.getImage();
			Image imageFX = SwingFXUtils.toFXImage((BufferedImage) imageBuffered, null);
			ImageView imageView = new ImageView();
			imageView.setImage(imageFX);
			
			//Adjust ImageView
			imageView.setFitHeight(displayImageHeight);
			imageView.setFitWidth(displayImageWidth);
//			imageView.setPreserveRatio(true);
			
			//Display Image
			imageView.setX(xPos);
			imageView.setY(yPos);
			
			box.getChildren().add(imageView);
			
			//Add to Image
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					displayWebpage(primaryStage, webURL);
				}
			});
			imageView.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					
					//Set Height/Width
					double newHeight = displayImageHeight * displayEnlargeFactor;
					double newWidth = displayImageWidth * displayEnlargeFactor;
					imageView.setFitHeight(newHeight);
					imageView.setFitWidth(newWidth);
					
					//Set X,Y
					double additionX = (newWidth - displayImageWidth) / 2;
					double additionY = (newHeight - displayImageHeight) / 2;
					imageView.setX(imageView.getX() - additionX);
					imageView.setY(imageView.getY() - additionY);
				}
			});
			imageView.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					
					//Set X,Y
					double additionX = (imageView.getFitWidth() - displayImageWidth) / 2;
					double additionY = (imageView.getFitHeight() - displayImageHeight) / 2;
					imageView.setX(imageView.getX() + additionX);
					imageView.setY(imageView.getY() + additionY);
					
					//Set Height/Width
					imageView.setFitHeight(displayImageHeight);
					imageView.setFitWidth(displayImageWidth);
				}
			});
			
			//Iteration
			xPos += (displayImageWidth + displayImageGap);
			if (xPos >= WIDTH - displayImageWidth) {
				xPos = xOffset;
				yPos += (displayImageHeight + displayImageGap);
			}
		}
		
		
	}
	
	private void displayWebpage(Stage primaryStage, String url) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
		Preconditions.preconditionCheck(url != null, "url is null");
		
		/*Stage Initialisation*/
		primaryStage.setTitle("Loaded Webpage: " + url);

        WebView webView = new WebView();

        webView.getEngine().load(url);
        webView.setPrefHeight(HEIGHT);
        webView.setPrefWidth(WIDTH);
        
        AnchorPane box = new AnchorPane(webView);
        BorderPane root = new BorderPane(box);
        Scene mainScene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setScene(mainScene);
        primaryStage.show();
        
        addTopButtons(root);
        addPreviousScene(mainScene);
	}
	
	private void displayMessage(Stage primaryStage, String message) {
		//TODO::
		System.out.println(message);
	}
	
	private void displayRefineSearch(Stage primaryStage, Set<DbItem> results, Search searchTerm) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
		Preconditions.preconditionCheck(results != null, "results is null");
		
		/*Stage Initialisation*/
		primaryStage.setTitle("BetterSearch: Displaying " + results.size() + " similar cards");

		TilePane box = new TilePane();
		box.setHgap(displayImageGap);
		box.setVgap(displayImageGap);
		box.setPrefColumns(1);
		
	    ScrollPane scrollPane = new ScrollPane(box);
//	    scrollPane.setFitToWidth(true);
	    scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
	    
	    BorderPane root = new BorderPane(scrollPane);
	    root.setPadding(new Insets(0));
		
	    Scene mainScene = new Scene(root, WIDTH, HEIGHT);
		primaryStage.setScene(mainScene);
		primaryStage.show();
		addPreviousScene(mainScene);
		addTopButtons(root);
		
		/*Displaying Card Names*/
		
		//Loop
		for (DbItem query : results) {
			
			//Variable Initialisation
			String cardName = query.getName();
			
			//Get Text
			/*Text*/
			double text_fontSize = WIDTH/64;
			Text cardText = new Text();
			cardText.setFont(new Font(text_fontSize));
			cardText.setText(cardName);
//			double textWidth = cardText.getLayoutBounds().getWidth();
			
//			cardText.setX((WIDTH/2) - (textWidth/2));
//			cardText.setY((HEIGHT/2) - text_fontSize);
			box.getChildren().add(cardText);
			
			//Add to Image
			cardText.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Search updatedSearch = new SearchImpl(cardName, searchTerm);
					List<CardInfo> results = priceGetter.getCardInfo_sell(updatedSearch);
					displayResults(primaryStage, results);
				}
			});
			
			cardText.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					cardText.setStroke(highlightColour);
				}
			});
			cardText.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					cardText.setStroke(defaultColour);
				}
			});
		}

	}
	
	private void displayActiveSearching(VBox box, Set<DbItem> results, Search searchTerm) {
		
		/*Preconditions*/
		Preconditions.preconditionCheck(primaryStage != null, "primaryStage is null");
		Preconditions.preconditionCheck(results != null, "results is null");
		
		/*Check if searchTerm is nothing*/
		if ("".equals(searchTerm.getSearchTerm())) {
			activeSearchingPane_content = null;
			activeSearchingContentPane_display = null;
			displaySearch(primaryStage);
			return; //Stops bug where nothing appears when user types after clearing search string
		}

		/*Stage Initialisation*/
		primaryStage.setTitle("BetterSearch: Displaying " + results.size() + " similar cards");
		box.setAlignment(Pos.TOP_CENTER);
		
		//Check if activeSearchingPane is initialised or not
		if (activeSearchingPane_content == null) {
			activeSearchingPane_content = new TilePane();
			activeSearchingPane_content.setHgap(displayImageGap);
			activeSearchingPane_content.setVgap(displayImageGap);
			activeSearchingPane_content.setPrefColumns(1);
//			activeSearchingPane_content.setPrefColumns(3);
			activeSearchingPane_content.setTileAlignment(Pos.CENTER_LEFT);
			
		    activeSearchingContentPane_display = new ScrollPane(activeSearchingPane_content);
		    activeSearchingContentPane_display.setFitToWidth(true);
		    activeSearchingContentPane_display.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		    activeSearchingContentPane_display.setPrefViewportHeight(HEIGHT);
		    box.getChildren().add(activeSearchingContentPane_display);
		} else {
			//Empty Pre-existing search list
			activeSearchingPane_content.getChildren().clear();
		}
		
		
		/*Displaying Card Names*/
		
		//Loop
		for (DbItem query : results) {
			
			//Variable Initialisation
			String cardName = query.getName();
			String cardFullType = query.getFullType();
			String cardText = query.getText();
			
			//Remove New Line Characters to avoid Formatting Errors
			cardText = cardText.replaceAll("\\.\n", ". "); //Replace all "new line sentences" with a space instead
			cardText = cardText.replaceAll("\n", "; "); //Replace leftover \n with ;
					
			//Text Initialisation
			double text_fontSize = WIDTH/64;
			Text currentText = new Text();
			currentText.setFont(new Font(text_fontSize));
			currentText.setTextAlignment(TextAlignment.LEFT);
			currentText.setX(0);
			
			//Text "content text" formatting
			
			currentText.setText(cardName);
			
			double textWidth = currentText.getLayoutBounds().getWidth();
			
			while (textWidth < CARDNAME_WIDTH) {
				currentText.setText(currentText.getText() + "\t");
				textWidth = currentText.getLayoutBounds().getWidth();
			}
			currentText.setText(currentText.getText() + cardFullType);
			
			while (textWidth < CARDNAME_WIDTH + CARDTYPE_WIDTH) {
				currentText.setText(currentText.getText() + "\t");
				textWidth = currentText.getLayoutBounds().getWidth();
			}
			currentText.setText(currentText.getText() + cardText);
			
			
			activeSearchingPane_content.getChildren().add(currentText);
			
			currentText.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Search updatedSearch = new SearchImpl(cardName, searchTerm);
					List<CardInfo> results = priceGetter.getCardInfo_sell(updatedSearch);
					displayResults(primaryStage, results);
				}
			});
			currentText.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					currentText.setStroke(highlightColour);
				}
			});
			currentText.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					currentText.setStroke(defaultColour);
				}
			});
		}
	}
	
	///////////////////////////////////
	/*Main*/
	///////////////////////////////////
	public static void main(String args[]) {           
		launch(args);      
	}
}
