import smile.association.FPGrowth;
import smile.association.FPTree;
import smile.association.ItemSet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransitDataMining {

  public static void main(String[] args) {
    String txtFilePath = "C:/Path/To/file.txt";
    int minSupport = 2;

    test(minSupport);
    //runFPGrowthFromFile(txtFilePath, minSupport);
    //decisionTree(minSupport);
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

  public static void decisionTree(int minSupport){


  }

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
