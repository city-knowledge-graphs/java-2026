/**
 * Created with the support of chatgp suing the python version as input.
 */
package lab6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class CTA_Evaluator {

    public static class Result {
        public double score;
        public double scoreSecondary;

        public Result(double score, double scoreSecondary) {
            this.score = score;
            this.scoreSecondary = scoreSecondary;
        }

        @Override
        public String toString() {
            return "{score=" + score + ", score_secondary=" + scoreSecondary + "}";
        }
    }

    public Result evaluate(String systemFile, String gtFile) throws Exception {

        Set<String> cols = new HashSet<>();
        Map<String, String> colType = new HashMap<>();

        // Load ground truth
        try (BufferedReader br = new BufferedReader(new FileReader(gtFile))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",", -1);

                String tabId = parts[0];
                String colId = parts[1];
                String type = parts[2];

                String col = tabId + " " + colId;

                cols.add(col);
                colType.put(col, type);
            }
        }

        Set<String> annotatedCols = new HashSet<>();
        int TP = 0;
        int validAnnotations = 0;

        // Load system submission
        try (BufferedReader br = new BufferedReader(new FileReader(systemFile))) {
            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",", -1);

                String tabId = parts[0];
                String colId = parts[1];
                String annotation = parts[2];

                String col = tabId + " " + colId;
                //System.out.println(col);
                //System.out.println("\t" + annotation);

                if (annotatedCols.contains(col)) {
                    throw new Exception("Duplicate columns in the submission file");
                } else {
                    annotatedCols.add(col);
                }

                if (cols.contains(col)) {
                    validAnnotations++;

                    String gt = colType.get(col);

                    if (gt.equalsIgnoreCase(annotation)) {
                        TP++;
                    }
                }
            }
        }

        double precision = validAnnotations > 0 ?
                (double) TP / validAnnotations : 0.0;

        double recall = (double) TP / cols.size();

        double f1 = (precision + recall) > 0 ?
                (2 * precision * recall) / (precision + recall) : 0.0;

        double mainScore = f1;
        double secondaryScore = precision;

        System.out.printf(
                "F1-score: %.3f, Precision: %.3f, Recall: %.3f%n",
                f1, precision, recall
        );

        return new Result(mainScore, secondaryScore);
    }

    public static void main(String[] args) throws Exception {

    	String base_path = "files/lab6/sem-tab-data/";
    	
        String gtFile = base_path + "gt/CWI64CIY_cta_gt.csv";
        String systemFile = base_path + "system_example/CWI64CIY_cta_system.csv";

        CTA_Evaluator evaluator = new CTA_Evaluator();

        Result result = evaluator.evaluate(systemFile, gtFile);

        System.out.println(result);
    }
}