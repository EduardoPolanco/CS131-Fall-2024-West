package com.wordVectorRetrofit.util;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class FileOperations {
    private static final Logger LOGGER = Logger.getLogger(FileOperations.class.getName());

    public static String normWord(String word) {
        if (word.matches("\\d+.*")) {
            LOGGER.info("Normalizing word '" + word + "' to '---num---'");
            return "---num---";
        } else if (word.replaceAll("\\W+", "").isEmpty()) {
            LOGGER.info("Normalizing word '" + word + "' to '---punc---'");
            return "---punc---";
        } else {
            String normalized = word.toLowerCase();
            LOGGER.info("Normalizing word '" + word + "' to '" + normalized + "'");
            return normalized;
        }
    }

    public static Map<String, double[]> readWordVectors(String filename) throws IOException {
        LOGGER.info("Starting to read word vectors from: " + filename);
        Map<String, double[]> wordVectors = new HashMap<>();
        try (BufferedReader reader = filename.endsWith(".gz")
                ? new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))))
                : new BufferedReader(new FileReader(filename))) {

            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim().toLowerCase();
                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    LOGGER.warning("Skipping invalid line " + lineCount + ": " + line);
                    continue; // Skip invalid lines
                }
                String word = parts[0];
                double[] vector = new double[parts.length - 1];
                double sumSquares = 0;

                for (int i = 1; i < parts.length; i++) {
                    try {
                        vector[i - 1] = Double.parseDouble(parts[i]);
                        sumSquares += vector[i - 1] * vector[i - 1];
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Invalid number format at line " + lineCount + ": " + parts[i]);
                        vector[i - 1] = 0.0; // Assign zero or handle as needed
                    }
                }

                double norm = Math.sqrt(sumSquares + 1e-6);
                for (int i = 0; i < vector.length; i++) {
                    vector[i] /= norm;
                }

                wordVectors.put(word, vector);
                if (lineCount % 100000 == 0) {
                    LOGGER.info("Processed " + lineCount + " lines.");
                }
            }

            LOGGER.info("Completed reading word vectors. Total words loaded: " + wordVectors.size());
        }
        return wordVectors;
    }

    /**
     * Writes word vectors to a file.
     *
     * @param wordVectors    Map of words to their vector representations.
     * @param outputFileName Path to the output file.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeWordVectors(Map<String, double[]> wordVectors, String outputFileName) throws IOException {
        LOGGER.info("Starting to write word vectors to: " + outputFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            int count = 0;
            for (Map.Entry<String, double[]> entry : wordVectors.entrySet()) {
                String word = entry.getKey();
                double[] vector = entry.getValue();
                StringBuilder sb = new StringBuilder(word);
                for (double val : vector) {
                    sb.append(String.format(" %.4f", val));
                }
                writer.write(sb.toString());
                writer.newLine();
                count++;
                if (count % 100000 == 0) {
                    LOGGER.info("Written " + count + " word vectors.");
                }
            }
            LOGGER.info("Completed writing word vectors. Total words written: " + count);
        }
    }

    public static Map<String, List<String>> readLexicon(String filename) throws IOException {
        LOGGER.info("Starting to read lexicon from: " + filename);
        Map<String, List<String>> lexicon = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] words = line.trim().toLowerCase().split("\\s+");
                if (words.length < 2) {
                    LOGGER.warning("Skipping invalid lexicon line " + lineCount + ": " + line);
                    continue; // Skip invalid lines
                }
                String key = normWord(words[0]);
                List<String> values = new ArrayList<>();
                for (int i = 1; i < words.length; i++) {
                    values.add(normWord(words[i]));
                }
                lexicon.put(key, values);
                if (lineCount % 10000 == 0) {
                    LOGGER.info("Processed " + lineCount + " lexicon lines.");
                }
            }
            LOGGER.info("Completed reading lexicon. Total entries loaded: " + lexicon.size());
        }
        return lexicon;
    }
}
