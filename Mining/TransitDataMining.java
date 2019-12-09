import org.apache.commons.csv.CSVFormat;
import smile.association.*;
import smile.classification.DecisionTree;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.type.StructType;
import smile.io.Arff;
import smile.io.CSV;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransitDataMining {

  public static void main(String[] args) {
    String txtFilePath = "C:/Path/To/data.txt";
    int minSupport = 2;

    //test(minSupport);
    //runFPGrowthFromFile(txtFilePath, minSupport);
    //decisionTreeCSV();
    //decisionTreeArff();
  }

  public static void test(int minSupport){
    int[][] itemsets =
      {
        {0,1},
        {0,2},
        {1}
      };

    FPTree tree = FPTree.of(minSupport, itemsets);
    List<ItemSet> results = FPGrowth.apply(tree).collect(Collectors.toList());

    for(int i = 0; i < results.size(); i++) {
      String frequentSet = results.get(i).toString();
      System.out.println(frequentSet);
    }
  }

  public static void runFPGrowthFromFile(String path, int minSupport){

    FPTree tree = FPTree.of(minSupport, () -> ReadFromFile.read(path));
    List<ItemSet> results = FPGrowth.apply(tree).collect(Collectors.toList());

    for(int i = 0; i < results.size(); i++) {
      String frequentSet = results.get(i).toString();
      System.out.println(frequentSet);
    }
  }

  //https://github.com/haifengl/smile/blob/master/shell/src/universal/data/regression/diabetes.csv
  public static void decisionTreeCSV(){
    try {

      CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
      CSV csv = new CSV(format);
      csv.schema((StructType)null);

      DataFrame data = csv.read(java.nio.file.Paths.get("C:/Path/To/diabetes.csv"));
      Formula formula = Formula.lhs("y");// predict the "y" column

      DecisionTree model = DecisionTree.fit(formula, data);
      System.out.println(model.dot());// put the output of this into http://viz-js.com/

      DataFrame dataPredict = csv.read(java.nio.file.Paths.get("C:/Path/To/diabetesPredict.csv"));

      System.out.println("\nPredictions:");
      System.out.println(Arrays.toString(model.predict(dataPredict)));

      /*
      int[] prediction = model.predict(dataPredict);
      for(int i = 0; i < prediction.length; i++) {
        System.out.println(prediction[i]);
      }
      */
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  //https://github.com/haifengl/smile/blob/8a794175832549ba7057de95bb2bbce032eabce1/shell/src/universal/data/weka/weather.arff
  public static void decisionTreeArff(){
    try{
      Formula formula = Formula.lhs("play");// predict the "play" column
      Arff arff = new Arff(java.nio.file.Paths.get("C:/Path/To/weather.arff"));

      DataFrame data = arff.read();

      DecisionTree model = DecisionTree.fit(formula, data);
      System.out.println(model.dot());// put the output of this into http://viz-js.com/

      Arff arffPredict = new Arff(java.nio.file.Paths.get("C:/Path/To/weatherPredict.arff"));
      DataFrame dataPredict = arffPredict.read();

      System.out.println("\nPredictions:");
      System.out.println(Arrays.toString(model.predict(dataPredict)));

      /*
      int[] prediction = model.predict(dataPredict);
      for(int i = 0; i < prediction.length; i++) {
        System.out.println(prediction[i]);
      }
      */
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
            String[] s = line.split(",");

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
}
