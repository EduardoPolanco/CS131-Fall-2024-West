package com.wordVectorRetrofit.ui;

import com.wordVectorRetrofit.util.LoggerUtil;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class TerminalConsole {

    private TextArea console;

    public TerminalConsole() {
        console = new TextArea();
        console.setEditable(false);
        console.setWrapText(true);
        console.setPrefHeight(150);
        console.setStyle("-fx-control-inner-background: black; -fx-text-fill: white; -fx-font-family: 'Consolas';");
    }

    public void appendText(String text) {
        console.appendText(text + "\n");
    }

    public TextArea getConsole() {
        return console;
    }
}
