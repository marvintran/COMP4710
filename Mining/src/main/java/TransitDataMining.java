import org.apache.commons.csv.CSVFormat;
import smile.association.FPGrowth;
import smile.association.FPTree;
import smile.association.ItemSet;
import smile.classification.DecisionTree;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.formula.Formula;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.io.CSV;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransitDataMining {

  public static void main(String[] args) {
    String trainingDataEncoded = "C:/Path/To/trainingData.txt";
    String predictDataEncoded = "C:/Path/To/predictData.txt";

    int minSupport = 25;
    int[] predictions = {};

    runFPGrowthFromFile(trainingDataEncoded, minSupport);
    predictions = decisionTree(predictDataEncoded);
    calculateCorrectness(predictDataEncoded, predictions);

    System.out.println("Finished Successfully");
  }

  private static void runFPGrowthFromFile(String path, int minSupport){

    System.out.println("Building FPTree from training data\n");
    FPTree tree = FPTree.of(minSupport, () -> ReadFromFile.read(path));

    System.out.println("Mining frequent itemsets using FPGrowth\n");
    List<ItemSet> results = FPGrowth.apply(tree).collect(Collectors.toList());

    try{
      BufferedWriter bw = new BufferedWriter(new FileWriter("frequentPatterns.csv"));

      for(int i = 0; i < results.size(); i++)
      {
        String frequentSet = results.get(i).toString();
        String[] tokens = frequentSet.split(" ");

        int[] orderedFrequentSet = new int[5];

        if(tokens.length == 6)
        {
          orderedFrequentSet[0] = Integer.parseInt(tokens[0]);
          orderedFrequentSet[1] = Integer.parseInt(tokens[1]);
          orderedFrequentSet[2] = Integer.parseInt(tokens[2]);
          orderedFrequentSet[3] = Integer.parseInt(tokens[3]);
          orderedFrequentSet[4] = Integer.parseInt(tokens[4]);

          Arrays.sort(orderedFrequentSet,0,5);

          String toWrite = orderedFrequentSet[4]+" "+
            orderedFrequentSet[3]+" "+
            orderedFrequentSet[2]+" "+
            orderedFrequentSet[1]+" "+
            orderedFrequentSet[0];
          bw.write(toWrite);
          bw.newLine();
          bw.flush();
        }
      }

      bw.close();

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  //https://github.com/haifengl/smile/blob/master/shell/src/universal/data/regression/diabetes.csv
  private static int[] decisionTreeCSV(String path){

    System.out.println("Creating Decision Tree\n");

    int[] predictions = {};

    try{
      CSVFormat format = CSVFormat.DEFAULT.withDelimiter(' ');//.withFirstRecordAsHeader();
      CSV csv = new CSV(format);

      StructType schema = DataTypes.struct(
        new StructField("stopNumber", DataTypes.IntegerType),
        new StructField("routNumber", DataTypes.IntegerType),
        new StructField("timeOfDay", DataTypes.IntegerType),
        new StructField("dayOfWeek", DataTypes.IntegerType),
        new StructField("delayValue", DataTypes.IntegerType));

      csv.schema(schema);

      DataFrame data = csv.read(java.nio.file.Paths.get("frequentPatterns.csv"));
      Formula formula = Formula.lhs("delayValue");
      DecisionTree model = DecisionTree.fit(formula, data);
      System.out.println(model.dot());// put the output of this into http://viz-js.com/

      System.out.println("Predicting delayValue\n");
      //DataFrame dataToPredict = csv.read(java.nio.file.Paths.get(path));
      DataFrame dataToPredict = DataFrame.of(ReadFromFileTupleStream.read(path, schema));
      predictions = model.predict(dataToPredict);

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    return predictions;
  }

  private static void calculateCorrectness(String path, int[] predictions){

    System.out.println("Calculating Correctness\n");
    try{
      BufferedReader br = new BufferedReader(new FileReader(path));

      String strCurrentLine;
      int correctValue = 0;
      int predictedValue = 1;

      int index = 0;// also the total rows in the array
      int correctCount = 0;

      while ((strCurrentLine = br.readLine()) != null) {
        correctValue = Character.getNumericValue(strCurrentLine.charAt(strCurrentLine.length()-1));
        predictedValue = predictions[index];

        if(correctValue == predictedValue)
          correctCount++;

        index++;
      }

      System.out.println("Total Values: " + index);
      System.out.println("Predictions Correct: " + correctCount);
      br.close();

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  //used by runFPGrowthFromFile
  //https://github.com/haifengl/smile/blob/2e1c56edb010ea8c6a7eb3f6d9486ae2cf16da87/core/src/test/java/smile/association/ItemSetTestData.java
  public interface ReadFromFile {

    static Stream<int[]> read(String path) {
      try {
        return java.nio.file.Files.lines(java.nio.file.Paths.get(path))
          .map(String::trim)
          .filter(line -> !line.isEmpty())
          .map(line -> {
            String[] s = line.split(" ");

            int[] basket = new int[s.length];
            for (int i = 0; i < s.length; i++) {
              basket[i] = Integer.parseInt(s[i]);
            }
            return basket;
          });
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      return Stream.empty();
    }
  }

  public interface ReadFromFileTupleStream {

    static Stream<Tuple> read(String path, StructType schema) {
      try {
        return java.nio.file.Files.lines(java.nio.file.Paths.get(path))
          .map(String::trim)
          .filter(line -> !line.isEmpty())
          .map(line -> {
            String[] s = line.split(" ");

            List<Function<String, Object>> parser = schema.parser();

            Object[] row = new Object[s.length];
            for (int i = 0; i < s.length; ++i) {
              row[i] = ((Function) parser.get(i)).apply(s[i]);
            }
            return Tuple.of(row, schema);
          });
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      return Stream.empty();
    }
  }
}
