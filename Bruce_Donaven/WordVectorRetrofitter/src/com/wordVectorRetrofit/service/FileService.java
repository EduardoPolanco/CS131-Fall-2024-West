package com.wordVectorRetrofit.service;

import com.wordVectorRetrofit.util.FileOperations;
import com.wordVectorRetrofit.util.LoggerUtil;
import com.wordVectorRetrofit.util.AlertUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileService {

    private Map<String, double[]> originalVectors = new HashMap<>();
    private Map<String, List<String>> lexicon = new HashMap<>();

    public void loadWordVectors(File file) {
        LoggerUtil.info("Initiating load of Word Vectors from file: " + file.getAbsolutePath());

        if (file == null || !file.exists()) {
            LoggerUtil.warning("File does not exist or was not provided: " + (file == null ? "null" : file.getAbsolutePath()));
            AlertUtil.showError("Error", "File does not exist or was not provided.");
            return;
        }

        try {
            LoggerUtil.info("Reading word vectors from file: " + file.getAbsolutePath());
            originalVectors = FileOperations.readWordVectors(file.getAbsolutePath());
            LoggerUtil.info("Word Vectors loaded successfully. Total vectors: " + originalVectors.size());
            AlertUtil.showInfo("Success", "Word Vectors loaded successfully.\nTotal vectors: " + originalVectors.size());
        } catch (IOException e) {
            LoggerUtil.severe("Failed to load Word Vectors from file: " + file.getAbsolutePath());
            LoggerUtil.severe("Error details: " + e.getMessage());
            AlertUtil.showError("Error", "Failed to load Word Vectors: " + e.getMessage());
        }
    }

    public void loadLexicon(File file) {
        LoggerUtil.info("Initiating load of Lexicon from file: " + file.getAbsolutePath());

        if (file == null || !file.exists()) {
            LoggerUtil.warning("File does not exist or was not provided: " + (file == null ? "null" : file.getAbsolutePath()));
            AlertUtil.showError("Error", "File does not exist or was not provided.");
            return;
        }

        try {
            LoggerUtil.info("Reading lexicon from file: " + file.getAbsolutePath());
            lexicon = FileOperations.readLexicon(file.getAbsolutePath());
            LoggerUtil.info("Lexicon loaded successfully. Total entries: " + lexicon.size());
            AlertUtil.showInfo("Success", "Lexicon loaded successfully.\nTotal entries: " + lexicon.size());
        } catch (IOException e) {
            LoggerUtil.severe("Failed to load Lexicon from file: " + file.getAbsolutePath());
            LoggerUtil.severe("Error details: " + e.getMessage());
            AlertUtil.showError("Error", "Failed to load Lexicon: " + e.getMessage());
        }
    }

    public Map<String, double[]> getOriginalVectors() {
        LoggerUtil.info("Fetching Original Word Vectors. Total vectors available: " + originalVectors.size());
        return originalVectors;
    }

    public Map<String, List<String>> getLexicon() {
        LoggerUtil.info("Fetching Lexicon. Total entries available: " + lexicon.size());
        return lexicon;
    }
}
