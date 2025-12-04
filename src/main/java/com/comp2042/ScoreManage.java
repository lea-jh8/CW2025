package com.comp2042;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManage {
    private static final String FILE_NAME = "highscores.txt";
    private static final int MAX_SCORES = 5;

    public static void saveScore(int score) {
        List<Integer> scores = loadScores();
        scores.add(score);

        scores.sort(Collections.reverseOrder());

        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }

        writeToFile(scores);
    }

    // Get highest score
    public static int getTopScore() {
        List<Integer> scores = loadScores();
        if (scores.isEmpty()) return 0;
        return scores.get(0);
    }

    public static List<Integer> getTopScores() {
        return loadScores();
    }

    private static List<Integer> loadScores() {
        List<Integer> scores = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(Integer.parseInt(line));
            }
        } catch (IOException | NumberFormatException exception) {
            System.err.println("Error loading scores: " + exception.getMessage());
        }
        return scores;
    }

    private static void writeToFile(List<Integer> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))){
            for (int s : scores) {
                writer.write(String.valueOf(s));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }
}
