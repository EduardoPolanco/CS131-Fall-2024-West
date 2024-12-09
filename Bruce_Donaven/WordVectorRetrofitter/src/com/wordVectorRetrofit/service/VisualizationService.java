package com.wordVectorRetrofit.service;

import com.wordVectorRetrofit.util.LoggerUtil;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Set;

public class VisualizationService {

    public void visualize(Map<String, double[]> originalVectors, Map<String, double[]> retrofittedVectors,
                          Set<String> trackedWords, Stage ownerStage) {
        LoggerUtil.log("Starting visualization for original and retrofitted vectors.");

        ScatterChart<Number, Number> originalChart = createScatterChart("Before Retrofitting", originalVectors, trackedWords, false);
        ScatterChart<Number, Number> retrofittedChart = createScatterChart("After Retrofitting", retrofittedVectors, trackedWords, true);

        HBox chartsBox = new HBox(50, originalChart, retrofittedChart);
        chartsBox.setStyle("-fx-padding: 10; -fx-background-color: #FFFFFF; -fx-alignment: center;");

        StackPane visualizationPane = new StackPane(chartsBox);
        visualizationPane.setStyle("-fx-padding: 20; -fx-background-color: #F5F5F5;");
        StackPane.setAlignment(chartsBox, javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(visualizationPane, 1600, 800);

        Stage visualizationStage = new Stage();
        visualizationStage.setScene(scene);
        visualizationStage.setTitle("Word Vector Visualization: Before and After Retrofitting");

        visualizationStage.setX((javafx.stage.Screen.getPrimary().getVisualBounds().getWidth() - 1600) / 2);
        visualizationStage.setY((javafx.stage.Screen.getPrimary().getVisualBounds().getHeight() - 800) / 2);

        visualizationStage.initOwner(ownerStage);
        visualizationStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        visualizationStage.show();

        LoggerUtil.log("Visualization window displayed successfully.");
    }

    private ScatterChart<Number, Number> createScatterChart(String title, Map<String, double[]> vectors, Set<String> trackedWords, boolean isAfter) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Dimension 1");
        yAxis.setLabel("Dimension 2");

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle(title);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(title);

        vectors.forEach((word, vector) -> {
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(vector[0], vector[1]);
            series.getData().add(dataPoint);

            dataPoint.nodeProperty().addListener((observable, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip tooltip = new Tooltip(word);
                    Tooltip.install(newNode, tooltip);

                    if (trackedWords.contains(word)) {
                        newNode.setStyle("-fx-background-color: #FFD700; -fx-radius: 6;"); 
                    } else {
                        String color = isAfter ? "#FF4500" : "#4682B4"; 
                        newNode.setStyle("-fx-background-color: " + color + "; -fx-radius: 5;");
                    }
                }
            });
        });

        scatterChart.getData().add(series);
        return scatterChart;
    }

    
        
    
}
