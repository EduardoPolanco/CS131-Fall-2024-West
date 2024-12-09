package com.wordVectorRetrofit.util;

import java.util.*;
import java.util.function.BiConsumer;

public class VectorProcessor {

    public static Map<String, double[]> retrofit(
            Map<String, double[]> originalVectors,
            Map<String, List<String>> lexicon,
            int numIterations,
            double alpha,
            double beta,
            BiConsumer<Integer, Integer> callback) {

        Map<String, double[]> retrofittedVectors = new HashMap<>(originalVectors);
        Set<String> lexiconVocabulary = new HashSet<>(lexicon.keySet());
        lexiconVocabulary.retainAll(retrofittedVectors.keySet()); // Common words

        LoggerUtil.log("Retrofitting vocabulary size after intersection: " + lexiconVocabulary.size());

        for (int iteration = 1; iteration <= numIterations; iteration++) {
            LoggerUtil.log("Starting iteration " + iteration + " of retrofitting.");
            int updatedWords = 0;
            int totalMissingNeighbors = 0;

            for (String word : lexiconVocabulary) {
                List<String> neighbors = lexicon.get(word);
                if (neighbors == null || neighbors.isEmpty()) {
                    LoggerUtil.log("Skipping word with no neighbors: " + word);
                    continue; 
                }

                double[] originalVec = originalVectors.get(word);
                double[] newVec = new double[originalVec.length];
                double totalWeight = alpha; 

                int validNeighbors = 0;
                for (String neighbor : neighbors) {
                    if (retrofittedVectors.containsKey(neighbor)) {
                        double[] neighborVec = retrofittedVectors.get(neighbor);
                        for (int i = 0; i < newVec.length; i++) {
                            newVec[i] += beta * neighborVec[i]; 
                        }
                        totalWeight += beta; 
                        validNeighbors++;
                    }
                }

                if (validNeighbors == 0) {
                    totalMissingNeighbors++;
                    continue; 
                }

                
                for (int i = 0; i < newVec.length; i++) {
                    newVec[i] += alpha * originalVec[i];
                    newVec[i] /= totalWeight; 
                }

                retrofittedVectors.put(word, newVec);
                updatedWords++;
            }

            LoggerUtil.log("Iteration " + iteration + " completed: Words updated: " + updatedWords);
            if (totalMissingNeighbors > 0) {
                LoggerUtil.warning("Total missing neighbors during this iteration: " + totalMissingNeighbors);
            }
            callback.accept(iteration, numIterations);
        }

        LoggerUtil.log("Retrofitting process completed.");
        return retrofittedVectors;
    }

    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }

    public static double[] normalize(double[] vector) {
        double norm = 0.0;
        for (double v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);

        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / (norm + 1e-10);
        }
        return normalized;
    }

    public static int countMissingNeighbors(String word, List<String> neighbors, Map<String, double[]> vectors) {
        int missing = 0;
        for (String neighbor : neighbors) {
            if (!vectors.containsKey(neighbor)) {
                missing++;
                LoggerUtil.warning("Neighbor '" + neighbor + "' for word '" + word + "' not found in vectors.");
            }
        }
        return missing;
    }
}
