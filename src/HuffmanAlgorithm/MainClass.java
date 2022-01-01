package HuffmanAlgorithm;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import HuffmanAlgorithm.HuffmanCompressFiles.CompressFile;
import HuffmanAlgorithm.HuffmanDecompressFiles.DecompressFile;

import java.nio.file.FileSystems;



public class MainClass {
  private static final DecimalFormat df = new DecimalFormat("0.000");

  public static void main(String[] args) throws IOException {

    String decision = args[0];
    // to handle time
    long start, end;
    double compressTime, decompressTime;
    start = System.currentTimeMillis();
    if (decision.equals("c")) {

      String filePath = args[1];
      String inputN = args[2];

      // we got the n
      int n = Integer.parseInt(inputN);
      // we got the input file
      double compressRatio = 0;
      String fileName = buildPtah(filePath, inputN);
      CompressFile compress = new CompressFile();

      compress.beginCompressing(filePath, fileName, n);

      end = System.currentTimeMillis();
      compressTime = ((double)(end - start)) / 1000;

      try {
        compressRatio = (double) Files.size(Paths.get(fileName)) / Files.size(Paths.get(filePath));
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println("Compressing Time(s): " + compressTime);

      System.out.println("\n at  n : " + n + ", Compressing Time(s): " + compressTime
          + " , Time(mins): " + df.format((compressTime) / 60.0)
          + " , Compression Ratio: " + df.format(compressRatio));
      System.out.println(" the compressed file path : " + fileName);
    }

    else if (decision.equals("d")) {
      String filePath = args[1];
      start = System.currentTimeMillis();
      DecompressFile decompress = new DecompressFile();
      String outPutFileName = buildOutPtah(filePath);
      decompress.beginDecompressing(filePath, outPutFileName);
      end = System.currentTimeMillis();

      decompressTime = ((double)(end - start) )/ 1000;
      System.out.println("\n at n : " + decompress.getN() + " , Decompressing Time(s): "
          + decompressTime + " ,  Time(mins): " + df.format((decompressTime) / 60.0));

      System.out.println(" the extracted file path : " + outPutFileName);

    }

  }

  public static String buildPtah(String filePath, String inputN) {
    int l = filePath.length() + inputN.length() + 20;
    StringBuilder buildPath = new StringBuilder(l);

    String sep = FileSystems.getDefault().getSeparator();

    int indexOfLast = filePath.lastIndexOf(sep) + 1;
    buildPath.append(filePath.substring(0, indexOfLast));

    buildPath.append("18012078.");
    buildPath.append(inputN);
    buildPath.append(".");
    buildPath.append(filePath.substring(indexOfLast));
    buildPath.append(".hc");
    return buildPath.toString();
  }

  public static String buildOutPtah(String filePath) {
    int l = filePath.length() + "extracted".length();
    StringBuilder buildPath = new StringBuilder(l);

    String sep = FileSystems.getDefault().getSeparator();
    int indexOfLast = filePath.lastIndexOf(sep) + 1;
    buildPath.append(filePath.substring(0, indexOfLast));

    buildPath.append("extracted.");
    String editFileName = filePath.substring(indexOfLast).substring(0, filePath.substring(indexOfLast).length() - 3);
    buildPath.append(editFileName);
    return buildPath.toString();
  }

}