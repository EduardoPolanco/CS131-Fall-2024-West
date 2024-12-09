package com.wordVectorRetrofit;

import com.wordVectorRetrofit.service.FileService;
import com.wordVectorRetrofit.service.VectorService;
import com.wordVectorRetrofit.ui.ControlPanelController;
import com.wordVectorRetrofit.ui.MenuBarController;
import com.wordVectorRetrofit.ui.TerminalConsole;
import com.wordVectorRetrofit.util.LoggerUtil;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WordVectorVisualizer extends Application {

    @Override
    public void start(Stage stage) {
        LoggerUtil.info("Starting Word Vector Visualizer application.");

        BorderPane rootLayout = new BorderPane();

        TerminalConsole terminalConsole = new TerminalConsole();
        LoggerUtil.initialize(terminalConsole);
        LoggerUtil.info("TerminalConsole initialized.");

        VectorService vectorService = new VectorService();
        FileService fileService = new FileService();

        MenuBarController menuBarController = new MenuBarController();
        ControlPanelController controlPanelController = new ControlPanelController(stage, fileService, vectorService);

        rootLayout.setTop(menuBarController.getMenuBar());

        StackPane centralPanel = new StackPane(controlPanelController.getControlPanelWidget());
        centralPanel.setAlignment(Pos.CENTER);
        centralPanel.setPadding(new javafx.geometry.Insets(10));

        rootLayout.setCenter(centralPanel);

        rootLayout.setBottom(terminalConsole.getConsole());

        Scene scene = new Scene(rootLayout, 1300, 600);

        stage.setScene(scene);
        stage.setTitle("Word Vector Visualizer");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
