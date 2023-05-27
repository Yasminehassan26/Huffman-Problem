package HuffmanAlgorithm.HuffmanDecompressFiles;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class DecompressFile {

    DictionnaryDecompress dictionnaryDecompress = new DictionnaryDecompress();
    HuffmanDecompress decompress = new HuffmanDecompress();
    FileOutputStream outputFile;

    public int getN(){
        return dictionnaryDecompress.getN();
    }

    public void beginDecompressing(String fileName,String outFilename) throws IOException {
        System.out.println("started decompression");

        FileInputStream compressedFile = new FileInputStream(fileName);
        this.outputFile = new FileOutputStream(outFilename);
        handleReadFile(compressedFile);

    }

    public void handleReadFile(FileInputStream inputFile) throws IOException {

        try (BufferedInputStream readInputFile = new BufferedInputStream(inputFile)) {
            int bytesRead = 0;
             //read used nodes
             StringBuilder buildUsedNodes = new StringBuilder(1000);
             byte[] tempByteArray = new byte[1];
            
             // to reaad size of dictionnary
             while (((bytesRead = readInputFile.read(tempByteArray)) != -1)) {
                 String readByte = new String(tempByteArray, Charset.forName("ISO-8859-1"));
                 buildUsedNodes.append(readByte);
                 tempByteArray = new byte[1];
                 if (readByte.equals(","))
                     break;
             }

             String tempUsed = buildUsedNodes.toString();
             int usedBytes=Integer.parseInt(tempUsed.substring(0, tempUsed.length() - 1));

            // read header first
            StringBuilder buildTempHeader = new StringBuilder();
            tempByteArray = new byte[1];
            
            // to reaad size of dictionnary
            while (((bytesRead = readInputFile.read(tempByteArray)) != -1)) {
                String readByte = new String(tempByteArray, Charset.forName("ISO-8859-1"));
                buildTempHeader.append(readByte);
                tempByteArray = new byte[1];
                if (readByte.equals(","))
                    break;
            }
     
            String tempString = buildTempHeader.toString();
           long size = Long.parseLong(tempString.substring(0, tempString.length() - 1));
           StringBuilder buildHeader = new StringBuilder((int)size);

              byte[] newtempByteArray = new byte[(int)size];
              readInputFile.read(newtempByteArray);
              String readByte = new String(newtempByteArray, Charset.forName("ISO-8859-1"));
              buildHeader.append(readByte);
          

            dictionnaryDecompress.constructDictionnary(buildHeader);
            decompress.setSettings(dictionnaryDecompress.getRoot(),this.outputFile,usedBytes,dictionnaryDecompress.getOnlyRoot());

            System.out.println("finished reading and constructing the dictionnary");

            int sizeB = 50000;
            byte[] byteArray = new byte[sizeB];
            while ((bytesRead = readInputFile.read(byteArray)) != -1) {
                if (bytesRead < sizeB) {
                    byte[] endBytes = new byte[bytesRead];
                    for (int i = 0; i < bytesRead; i++) {
                        endBytes[i] = byteArray[i];
                    }
                    decompress.decompressLastBytes(endBytes);
                } else {
                    decompress.decompressData(byteArray);
                }
                byteArray = new byte[sizeB];
            }

            if (!decompress.getEmpty()) {
                decompress.store();
            }
            if (decompress.getIndex() != 0) {
                decompress.writeFinalBytesToFile(decompress.getBytesToBeWritten(), decompress.getIndex());
            }
            readInputFile.close();
            decompress.writeOutputFile.close();
            System.out.println("DONE.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



}