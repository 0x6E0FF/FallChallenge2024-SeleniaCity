package com.codingame.game;

import com.codingame.gameengine.runner.SoloGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;
import java.io.*;
import java.util.*;

public class Benchmark {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("required args: <bot_command> <tests_dir> <ref_scores>");
            return;
        }

        String botCmd = args[0];
        String testsDir = args[1];
        String refFile = args[2];

        int[] ref1 = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        int[] ref2 = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        try {
            List<int[]> data = readFile(refFile);
            ref1 = data.get(0);
            ref2 = data.get(1);
        } catch (IOException e) {
        }

        int[] scores1 = new int[13];
        int[] scores2 = new int[13];
        scores1[0] = 0;
        scores2[0] = 0;

        SoloGameRunner gameRunner = new SoloGameRunner();

        int total1 = 0;
        int total2 = 0;
        int total1Ref = 0;
        int total2Ref = 0;
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("|         |              IDE               |        ARENA                   |");
        System.out.println("|         |   ref    |   cur    |   diff   |   ref    |   cur    |   diff   |");
        System.out.println("-----------------------------------------------------------------------------");
        for (int testCase = 1; testCase <= 12; testCase++) {

            gameRunner = new SoloGameRunner();
            gameRunner.setAgent(botCmd);
            gameRunner.setTestCase(new File(testsDir + "/test" + testCase + ".json"));
            GameResult result = gameRunner.simulate();
            int points1 = getScore(result.metadata);
            int points1Ref = ref1[testCase];
            total1 += points1; 
            total1Ref += points1Ref;
            scores1[testCase] = points1;
            
            String col1;
            if (points1 < points1Ref){
                col1 = "\u001B[31m";
            } else {
                col1 = "\u001B[32m";
            }

            gameRunner = new SoloGameRunner();
            gameRunner.setAgent(botCmd);
            gameRunner.setTestCase(new File(testsDir + "/test" + (testCase+12) + ".json"));
            result = gameRunner.simulate();
            int points2 = getScore(result.metadata);
            int points2Ref = ref2[testCase];
            total2 += points2; 
            total2Ref += points2Ref;
            scores2[testCase] = points2;

            String col2;
            if (points2 < points2Ref){
                col2 = "\u001B[31m";
            } else {
                col2 = "\u001B[32m";
            }


            System.out.printf("| test %2d | %8d | %s%8d\u001B[0m | %8d | %8d | %s%8d\u001B[0m | %8d |\n", testCase, 
                    points1Ref, col1, points1, points1 - points1Ref, 
                    points2Ref, col2, points2, points2 - points2Ref);
        }
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("| total   | %8d | %s%8d\u001B[0m | %8d | %8d | %s%8d\u001B[0m | %8d |\n", 
                    total1Ref, (total1 < total1Ref ? "\u001B[31m" : "\u001B[32m"), total1, total1 - total1Ref, 
                    total2Ref, (total2 < total2Ref ? "\u001B[31m" : "\u001B[32m"), total2, total2 - total2Ref);
        System.out.println("-----------------------------------------------------------------------------");

        try  {
            writeFile("scores.txt", scores1, scores2);
        } catch (IOException e) {
            System.out.println("Error processing the file: " + e.getMessage());
        }
    }

    private static int getScore(String metadata) {
        String points = metadata.replace("{", "").replace("}", "").replace("\"", "").split(":")[1];
        return (int)Double.parseDouble(points);
    }

    private static List<int[]> readFile(String filename) throws IOException {
        List<int[]> arrays = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            int[] array = Arrays.stream(parts)
                                .map(String::trim)
                                .mapToInt(Integer::parseInt)
                                .toArray();
            arrays.add(array);
        }
        reader.close();
        return arrays;
    }
        private static void writeFile(String filename, int[] ref1, int[] ref2) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        
        // Write ref1 as a comma-separated string
        writer.write(arrayToString(ref1));
        writer.newLine(); // Add a new line
        
        // Write ref2 as a comma-separated string
        writer.write(arrayToString(ref2));
        writer.newLine(); // Add a new line
        
        writer.close();
    }

    private static String arrayToString(int[] array) {
        return Arrays.stream(array)
                     .mapToObj(Integer::toString)
                     .reduce((a, b) -> a + "," + b)
                     .orElse("");
    }
}
