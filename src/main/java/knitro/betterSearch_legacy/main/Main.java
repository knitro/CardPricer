package knitro.betterSearch_legacy.main;

import knitro.betterSearch_legacy.display.impl.CardListCompiler;
import knitro.betterSearch_legacy.display.impl.JavaFxApplication;
import javafx.application.Application;

public class Main {
	
	public static void main(String[] args) {
//		Application.launch(CardListCompiler.class, args);
//		CardListCompiler.main(args);
		Application.launch(JavaFxApplication.class, args);
		JavaFxApplication.main(args);
    }
}
