package com.wordVectorRetrofit.ui;

import com.wordVectorRetrofit.util.AlertUtil;
import com.wordVectorRetrofit.util.LoggerUtil;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarController {

    private MenuBar menuBar;

    public MenuBarController() {
        menuBar = new MenuBar();
        createMenus();
    }

    private void createMenus() {
        Menu fileMenu = new Menu("File");

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            LoggerUtil.info("Exiting application.");
            System.exit(0);
        });

        fileMenu.getItems().addAll(exitItem);

        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> {
            AlertUtil.showInfo("About", "Word Vector Visualizer\nVersion 1.0\nDeveloped by OpenAI's ChatGPT.");
        });

        helpMenu.getItems().addAll(aboutItem);


        menuBar.getMenus().addAll(fileMenu, helpMenu);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
