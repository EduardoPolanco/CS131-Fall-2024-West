package com.wordVectorRetrofit.ui;

import com.wordVectorRetrofit.service.FileService;
import com.wordVectorRetrofit.service.VectorService;
import com.wordVectorRetrofit.service.VisualizationService;
import com.wordVectorRetrofit.util.AlertUtil;
import com.wordVectorRetrofit.util.LoggerUtil;
import com.wordVectorRetrofit.util.FileOperations;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class ControlPanelController {

    private VBox controlPanelWidget;
    private GridPane controlPanel;
    private TextField vectorFileField = new TextField();
    private TextField lexiconFileField = new TextField();
    private Button vectorizeButton = new Button("Vectorize");
    private Slider similaritySlider = new Slider(0.0, 1.0, 0.8);
    private Label similarityLabel = new Label("Similarity: 0.80");
    private Button filterButton = new Button("Filter");
    private Button visualizeButton = new Button("Visualize");
    private Button exportButton = new Button("Export");
    private Button visualizeSmallDatasetButton = new Button("Visualize Small Dataset"); 

    private FileService fileService;
    private VectorService vectorService;
    private VisualizationService visualizationService;


    private Set<String> trackedWords;

    public ControlPanelController(Stage stage, FileService fileService, VectorService vectorService) {
        this.fileService = fileService;
        this.vectorService = vectorService;
        this.visualizationService = new VisualizationService();
        this.trackedWords = initializeTrackedWords();
        createControlPanel(stage);
        LoggerUtil.log("ControlPanelController initialized.");
    }

    private Set<String> initializeTrackedWords() {
        Set<String> words = new HashSet<>();
        String[][] wordPairs = {
        		{"dog", "puppy"},
        	    {"cat", "kitten"},
        	    {"bird", "cat"},
        	    {"dog", "kitten"},
        	    {"puppy", "bird"}
        };
        for (String[] pair : wordPairs) {
            words.add(pair[0]);
            words.add(pair[1]);
        }
        return words;
    }

    private void createControlPanel(Stage stage) {
        controlPanel = new GridPane();
        controlPanel.setPadding(new Insets(10));
        controlPanel.setHgap(10);
        controlPanel.setVgap(10);
        controlPanel.setAlignment(Pos.CENTER);

        configureTextField(vectorFileField);
        configureTextField(lexiconFileField);

        HBox vectorFileRow = createFileRow("Load Vectors", vectorFileField, stage, fileService::loadWordVectors);
        HBox lexiconFileRow = createFileRow("Load Lexicon", lexiconFileField, stage, fileService::loadLexicon);

        HBox similarityRow = createSimilaritySlider();

        configureButtons(stage);

        HBox buttonRow1 = new HBox(10, vectorizeButton, filterButton);
        buttonRow1.setAlignment(Pos.CENTER);

        HBox buttonRow2 = new HBox(10, visualizeButton, exportButton);
        buttonRow2.setAlignment(Pos.CENTER);

        HBox buttonRow3 = new HBox(10, visualizeSmallDatasetButton); 
        buttonRow3.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(10, buttonRow1, buttonRow2, buttonRow3); 
        buttonBox.setAlignment(Pos.CENTER);

        controlPanel.add(vectorFileRow, 0, 0, 2, 1);
        controlPanel.add(lexiconFileRow, 0, 1, 2, 1);
        controlPanel.add(similarityRow, 0, 2, 2, 1);
        controlPanel.add(buttonBox, 0, 3, 2, 1);

        controlPanelWidget = new VBox(10);
        controlPanelWidget.setPadding(new Insets(10));
        controlPanelWidget.setAlignment(Pos.CENTER);
        controlPanelWidget.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1px; -fx-background-color: #FAFAFA;");
        controlPanelWidget.setPrefWidth(500);
        controlPanelWidget.setMaxWidth(500);
        controlPanelWidget.setPrefHeight(350); 
        controlPanelWidget.setMaxHeight(350);

        Label title = new Label("Control Panel");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        controlPanelWidget.getChildren().addAll(title, controlPanel);
    }

    private void configureButtons(Stage stage) {
        vectorizeButton.setPrefWidth(150);
        filterButton.setPrefWidth(150);
        visualizeButton.setPrefWidth(150);
        exportButton.setPrefWidth(150);
        visualizeSmallDatasetButton.setPrefWidth(200); 

        vectorizeButton.setDisable(true);
        filterButton.setDisable(true);
        visualizeButton.setDisable(true);
        exportButton.setDisable(true);
        visualizeSmallDatasetButton.setDisable(true); 

        vectorFileField.textProperty().addListener((obs, oldVal, newVal) -> checkEnableVectorizeButton());
        lexiconFileField.textProperty().addListener((obs, oldVal, newVal) -> checkEnableVectorizeButton());

        vectorizeButton.setOnAction(e -> vectorize());
        filterButton.setOnAction(e -> filterWords());
        visualizeButton.setOnAction(e -> visualize(stage));
        exportButton.setOnAction(e -> exportRetrofittedVectors(stage));

        visualizeSmallDatasetButton.setOnAction(e -> {
            Map<String, double[]> smallDatasetOriginalVectors = getSmallDatasetOriginalVectors();
            Map<String, double[]> smallDatasetRetrofittedVectors = getSmallDatasetRetrofittedVectors();

            if (smallDatasetOriginalVectors.isEmpty() || smallDatasetRetrofittedVectors.isEmpty()) {
                AlertUtil.showError("Visualization Error", "Small dataset vectors are not available. Ensure that vectorization and retrofitting have been performed.");
                LoggerUtil.error("Small dataset visualization failed: Vectors are not available.");
                return;
            }
        });
    }

    private void checkEnableVectorizeButton() {
        boolean enable = !vectorFileField.getText().trim().isEmpty() && !lexiconFileField.getText().trim().isEmpty();
        vectorizeButton.setDisable(!enable);
    }

    private void configureTextField(TextField textField) {
        textField.setEditable(false);
        textField.setPrefWidth(220);
    }

    private HBox createFileRow(String buttonText, TextField textField, Stage stage, FileHandler handler) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Button button = new Button(buttonText);
        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(buttonText + " File");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                textField.setText(file.getAbsolutePath());
                try {
                    handler.handle(file);
                } catch (IOException ex) {
                    LoggerUtil.error("Failed to load file: " + ex.getMessage());
                    AlertUtil.showError("File Load Error", "Failed to load file:\n" + ex.getMessage());
                }
                LoggerUtil.log(buttonText + " loaded: " + file.getName());
            }
        });
        row.getChildren().addAll(button, textField);
        return row;
    }

    private HBox createSimilaritySlider() {
        similaritySlider.setShowTickLabels(true);
        similaritySlider.setShowTickMarks(true);
        similaritySlider.setMajorTickUnit(0.1);
        similaritySlider.setBlockIncrement(0.01);
        similaritySlider.setPrefWidth(220);

        similaritySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            similarityLabel.setText(String.format("Similarity: %.2f", newVal.doubleValue()));
            LoggerUtil.log("Similarity threshold set to: " + String.format("%.2f", newVal.doubleValue()));
        });

        HBox sliderBox = new HBox(10, similarityLabel, similaritySlider);
        sliderBox.setAlignment(Pos.CENTER);
        return sliderBox;
    }

    private void vectorize() {
        LoggerUtil.log("Starting vectorization...");
        double alpha = 1.0; 
        double beta = 1.0;  
        int numIterations = 10; // Default number of iterations

        vectorService.vectorize(
                fileService.getOriginalVectors(),
                fileService.getLexicon(),
                numIterations,
                alpha,
                beta,
                elapsedTime -> Platform.runLater(() -> {
                    LoggerUtil.log("Vectorization completed in " + elapsedTime + " seconds.");
                    filterButton.setDisable(false);
                    visualizeButton.setDisable(false);
                    exportButton.setDisable(false);
                    visualizeSmallDatasetButton.setDisable(false); 
                }),
                () -> Platform.runLater(() -> LoggerUtil.error("Vectorization failed.")),
                (currentIteration, totalIterations) -> {
                    LoggerUtil.log(String.format("Vectorization progress: %.2f%% (%d/%d)",
                            ((double) currentIteration / totalIterations) * 100, currentIteration, totalIterations));
                }
        );
    }

    private void filterWords() {
        double threshold = similaritySlider.getValue();
        LoggerUtil.log("Starting filtering with similarity threshold: " + String.format("%.2f", threshold));
        vectorService.filterWords(threshold,
                (count, avgSimilarity) -> Platform.runLater(() -> {
                    LoggerUtil.log("Filtering completed.");
                    LoggerUtil.log("Number of words meeting the threshold: " + count);
                    LoggerUtil.log(String.format("Average Similarity: %.4f", avgSimilarity));
                    logTopAlignedWords(5); // Log top 5 aligned words
                    visualizeButton.setDisable(false);
                    exportButton.setDisable(false);
                }),
                () -> Platform.runLater(() -> LoggerUtil.warning("No words met the similarity threshold."))
        );
    }

    private void logTopAlignedWords(int topN) {
        Map<String, Double> similarityMap = vectorService.getAlignedWordSimilarities();

        LoggerUtil.log("Top " + topN + " Aligned Words After Retrofitting:");
        LoggerUtil.log(String.format("%-20s%-10s", "Word", "Similarity"));
        LoggerUtil.log(String.format("%-20s%-10s", "----", "----------"));

        similarityMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .forEach(entry -> LoggerUtil.log(String.format("%-20s%-10.4f", entry.getKey(), entry.getValue())));
    }

    private void visualize(Stage stage) {
        if (!vectorService.isVectorized()) {
            AlertUtil.showError("Visualization Error", "Please perform vectorization before visualization.");
            LoggerUtil.error("Visualization failed: Vectors are not retrofitted yet.");
            return;
        }

        if (!vectorService.isFiltered()) {
            AlertUtil.showError("Visualization Error", "Please perform filtering before visualization.");
            LoggerUtil.error("Visualization failed: No words meet the similarity threshold.");
            return;
        }

        LoggerUtil.log("Launching main visualization...");
        visualizationService.visualize(
                vectorService.getOriginalVectors(),
                vectorService.getRetrofittedVectors(),
                trackedWords,
                stage
        );
    }

    private void exportRetrofittedVectors(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Retrofitted Vectors");
        fileChooser.setInitialFileName("retrofitted_vectors.txt");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                FileOperations.writeWordVectors(vectorService.getRetrofittedVectors(), file.getAbsolutePath());
                LoggerUtil.log("Retrofitted vectors exported to: " + file.getAbsolutePath());
                AlertUtil.showInfo("Export Successful", "Retrofitted vectors exported successfully to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                LoggerUtil.error("Failed to export retrofitted vectors: " + e.getMessage());
                AlertUtil.showError("Export Error", "Failed to export retrofitted vectors:\n" + e.getMessage());
            }
        }
    }

    public VBox getControlPanelWidget() {
        return controlPanelWidget;
    }

    @FunctionalInterface
    private interface FileHandler {
        void handle(File file) throws IOException;
    }

    private Map<String, double[]> getSmallDatasetOriginalVectors() {
        LoggerUtil.log("Preparing small dataset original vectors for visualization.");

        Map<String, double[]> smallDataset = new HashMap<>();

        String[][] wordPairs = {
        		{"dog", "puppy"},
        	    {"cat", "kitten"},
        	    {"bird", "cat"},
        	    {"dog", "kitten"},
        	    {"puppy", "bird"}
        };

        for (String[] pair : wordPairs) {
            String word1 = pair[0];
            String word2 = pair[1];

            if (vectorService.containsWord(word1) && vectorService.containsWord(word2)) {
                smallDataset.put(word1, vectorService.getOriginalVectors().get(word1));
                smallDataset.put(word2, vectorService.getOriginalVectors().get(word2));
                LoggerUtil.log("Added word pair: (" + word1 + ", " + word2 + ")");
            } else {
                LoggerUtil.warning("Word pair (" + word1 + ", " + word2 + ") contains words not in the vectors.");
            }
        }

        LoggerUtil.log("Small dataset original vectors preparation completed. Total words: " + smallDataset.size());
        return smallDataset;
    }

    private Map<String, double[]> getSmallDatasetRetrofittedVectors() {
        LoggerUtil.log("Preparing small dataset retrofitted vectors for visualization.");

        Map<String, double[]> smallDataset = new HashMap<>();

        String[][] wordPairs = {
        		{"dog", "puppy"},
        	    {"cat", "kitten"},
        	    {"bird", "cat"},
        	    {"dog", "kitten"},
        	    {"puppy", "bird"}
        };

        for (String[] pair : wordPairs) {
            String word1 = pair[0];
            String word2 = pair[1];

            if (vectorService.getRetrofittedVectors().containsKey(word1) && vectorService.getRetrofittedVectors().containsKey(word2)) {
                smallDataset.put(word1, vectorService.getRetrofittedVectors().get(word1));
                smallDataset.put(word2, vectorService.getRetrofittedVectors().get(word2));
                LoggerUtil.log("Added retrofitted word pair: (" + word1 + ", " + word2 + ")");
            } else {
                LoggerUtil.warning("Retrofitted word pair (" + word1 + ", " + word2 + ") contains words not in the retrofitted vectors.");
            }
        }

        LoggerUtil.log("Small dataset retrofitted vectors preparation completed. Total words: " + smallDataset.size());
        return smallDataset;
    }
}
