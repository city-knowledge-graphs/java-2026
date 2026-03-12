package lab6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CEA_Evaluator {

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

        Map<String, String> gtCellEnt = new HashMap<>();

        // Read ground truth
        try (BufferedReader br = new BufferedReader(new FileReader(gtFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);

                String tabId = parts[0];
                String rowId = parts[1];
                String colId = parts[2];
                String entity = parts[3];

                String cell = tabId + " " + colId + " " + rowId;
                gtCellEnt.put(cell, entity);
                //System.out.println(cell + "\n\t" + entity);
                
            }
        }

        Set<String> correctCells = new HashSet<>();
        Set<String> annotatedCells = new HashSet<>();

        // Read system submission
        try (BufferedReader br = new BufferedReader(new FileReader(systemFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);

                String tabId = parts[0];
                String rowId = parts[1];
                String colId = parts[2];
                String entity = parts[3].replace("\"", ""); //removes trailing quotes too

                String cell = tabId + " " + colId + " " + rowId;
                //System.out.println(cell + "\n\t" + entity);
                
                
                
                
                if (gtCellEnt.containsKey(cell)) {

                    if (annotatedCells.contains(cell)) {
                        throw new Exception("Duplicate cells in the submission file");
                    } else {
                        annotatedCells.add(cell);
                    }

                    String annotation = entity;
                    String gtEntities = gtCellEnt.get(cell).toLowerCase().replace("\"", ""); //removes trailing quotes too

                    List<String> gtSplit = Arrays.asList(gtEntities.split("\\s+"));
                    
                    //System.out.println(gtSplit);
                    //System.out.println(annotation.toLowerCase());
                    

                    if (gtSplit.contains(annotation.toLowerCase())) {
                        correctCells.add(cell);
                    }
                }
            }
        }
        
        //System.out.println(annotatedCells.size());
        //System.out.println(correctCells.size());
        //System.out.println(gtCellEnt.size());
        

        double precision = annotatedCells.size() > 0 ?
                (double) correctCells.size() / annotatedCells.size() : 0.0;

        double recall = (double) correctCells.size() / gtCellEnt.size();

        double f1 = (precision + recall) > 0 ?
                (2 * precision * recall) / (precision + recall) : 0.0;

        double mainScore = f1;
        double secondaryScore = precision;

        System.out.printf("F1-score: %.3f, Precision: %.3f, Recall: %.3f%n", f1, precision, recall);

        return new Result(mainScore, secondaryScore);
    }

    public static void main(String[] args) throws Exception {

    	String base_path = "files/lab6/sem-tab-data/";
    	
        String gtFile = base_path + "gt/CWI64CIY_cea_gt.csv";
        String systemFile = base_path + "system_example/CWI64CIY_cea_system.csv";

        CEA_Evaluator evaluator = new CEA_Evaluator();

        
        Result result = evaluator.evaluate(systemFile, gtFile);

        System.out.println(result);
    }
}