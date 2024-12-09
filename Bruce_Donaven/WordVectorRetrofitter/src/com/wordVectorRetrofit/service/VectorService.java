package com.wordVectorRetrofit.service;

import com.wordVectorRetrofit.util.VectorProcessor;
import com.wordVectorRetrofit.util.LoggerUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VectorService {

    private Map<String, double[]> originalVectors = new HashMap<>();
    private Map<String, double[]> retrofittedVectors = new HashMap<>();
    private List<String> filteredWords = new ArrayList<>();

    private boolean isVectorized = false;
    private boolean isFiltered = false;

    private Map<String, List<String>> lexicon = new HashMap<>();
    private Map<String, Double> alignedWordSimilarities = new HashMap<>();
    private Map<String, Double> preRetrofitSimilarities = new HashMap<>();

    public void setOriginalVectors(Map<String, double[]> vectors) {
        this.originalVectors = vectors;
        LoggerUtil.log("Original vectors set. Total words: " + vectors.size());
        if (!vectors.isEmpty()) {
            String sampleWord = vectors.keySet().iterator().next();
            LoggerUtil.log("Sample word: " + sampleWord + ", Vector: " + Arrays.toString(vectors.get(sampleWord)));
        }
    }

    public void setLexicon(Map<String, List<String>> lexicon) {
        this.lexicon = lexicon;
        LoggerUtil.log("Lexicon set. Total entries: " + lexicon.size());
        if (!lexicon.isEmpty()) {
            String sampleWord = lexicon.keySet().iterator().next();
            LoggerUtil.log("Sample lexicon entry: " + sampleWord + " -> " + lexicon.get(sampleWord));
        }
    }

    private void calculatePreRetrofitSimilarities() {
        LoggerUtil.log("Calculating pre-retrofitting similarities...");

        for (String word : originalVectors.keySet()) {
            List<String> neighbors = lexicon.get(word);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    if (originalVectors.containsKey(neighbor)) {
                        double similarity = cosineSimilarity(originalVectors.get(word), originalVectors.get(neighbor));
                        preRetrofitSimilarities.put(word + "->" + neighbor, similarity);
                        LoggerUtil.log(String.format("Pre-retrofit similarity for %s and %s: %.4f",
                                word, neighbor, similarity));
                    }
                }
            }
        }
    }

    private void comparePrePostRetrofitSimilarities() {
        LoggerUtil.log("Comparing pre- and post-retrofitting similarities...");
        for (String pair : preRetrofitSimilarities.keySet()) {
            String[] words = pair.split("->");
            if (words.length == 2 && retrofittedVectors.containsKey(words[0]) && retrofittedVectors.containsKey(words[1])) {
                double preSimilarity = preRetrofitSimilarities.get(pair);
                double postSimilarity = cosineSimilarity(retrofittedVectors.get(words[0]), retrofittedVectors.get(words[1]));
                LoggerUtil.log(String.format("Similarity for %s and %s: Pre: %.4f, Post: %.4f, Difference: %.4f",
                        words[0], words[1], preSimilarity, postSimilarity, postSimilarity - preSimilarity));
            }
        }
    }

    /**
     * Performs vectorization using retrofitting.
     *
     * @param vectors        Original word vectors.
     * @param lexicon        Lexicon mapping words to their neighbors.
     * @param numIterations  Number of iterations for retrofitting.
     * @param alpha          Weight for original vector.
     * @param beta           Weight for neighbor influence.
     * @param onComplete     Callback for successful completion with elapsed time.
     * @param onError        Callback for handling errors.
     * @param onProgress     Callback for progress updates.
     */
    public void vectorize(Map<String, double[]> vectors, Map<String, List<String>> lexicon, int numIterations,
                          double alpha, double beta, Consumer<Long> onComplete, Runnable onError,
                          BiConsumer<Integer, Integer> onProgress) {
        setOriginalVectors(vectors);
        setLexicon(lexicon);

        calculatePreRetrofitSimilarities(); // Step 1: Pre-retrofit similarities

        new Thread(() -> {
            try {
                LoggerUtil.log("Starting vectorization with " + lexicon.size() + " lexicon entries.");
                long startTime = System.currentTimeMillis();

                retrofittedVectors = VectorProcessor.retrofit(originalVectors, lexicon, numIterations, alpha, beta, onProgress);

                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                isVectorized = true;

                LoggerUtil.log("Vectorization process completed after " + elapsedTime + " seconds.");
                comparePrePostRetrofitSimilarities(); // Step 2: Compare similarities
                onComplete.accept(elapsedTime);
            } catch (Exception e) {
                LoggerUtil.error("Error during vectorization: " + e.getMessage());
                onError.run();
            }
        }).start();
    }

    /**
     * Filters words based on a similarity threshold.
     *
     * @param threshold  Similarity threshold.
     * @param onComplete Callback with count and average similarity.
     * @param onError    Callback for handling errors.
     */
    public void filterWords(double threshold, BiConsumer<Integer, Double> onComplete, Runnable onError) {
        new Thread(() -> {
            LoggerUtil.log("Starting filtering with threshold: " + threshold);
            List<String> filtered = new ArrayList<>();
            double totalSimilarity = 0.0;
            int count = 0;

            alignedWordSimilarities.clear();

            for (String word : originalVectors.keySet()) {
                if (retrofittedVectors.containsKey(word)) {
                    double similarity = cosineSimilarity(originalVectors.get(word), retrofittedVectors.get(word));
                    if (similarity >= threshold) {
                        filtered.add(word);
                        totalSimilarity += similarity;
                        count++;
                        alignedWordSimilarities.put(word, similarity);
                        LoggerUtil.log(String.format("Word '%s' meets the threshold with similarity %.4f.", word, similarity));
                    }
                }
            }

            if (count > 0) {
                double avgSimilarity = totalSimilarity / count;
                filteredWords = filtered;
                isFiltered = true;

                LoggerUtil.log(String.format("Filtering completed: %d words passed the threshold. Average similarity: %.4f", count, avgSimilarity));
                onComplete.accept(count, avgSimilarity);
            } else {
                isFiltered = false;
                LoggerUtil.warning("Filtering completed: No words met the similarity threshold of " + threshold);
                onError.run();
            }
        }).start();
    }

    public double cosineSimilarity(double[] vec1, double[] vec2) {
        return VectorProcessor.cosineSimilarity(vec1, vec2);
    }

    public Map<String, double[]> getOriginalVectors() {
        return originalVectors;
    }

    public Map<String, double[]> getRetrofittedVectors() {
        return retrofittedVectors;
    }

    public List<String> getFilteredWords() {
        return filteredWords;
    }

    public boolean isVectorized() {
        return isVectorized;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public Map<String, Double> getAlignedWordSimilarities() {
        return alignedWordSimilarities;
    }

    public boolean containsWord(String word) {
        return originalVectors.containsKey(word) || retrofittedVectors.containsKey(word);
    }

}
