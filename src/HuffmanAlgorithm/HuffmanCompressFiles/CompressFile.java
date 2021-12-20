package HuffmanAlgorithm.HuffmanCompressFiles;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileOutputStream;
import HuffmanAlgorithm.ByteArrayWrapper;
import HuffmanAlgorithm.HuffmanNode;

public class CompressFile {
    private static HashMap<ByteArrayWrapper, Long> frequencies = new HashMap<>();

    HuffmanCompress huffmanCompress = new HuffmanCompress();
    private int usedBytes = 0;
    private int sizeWanted = 0;
    private BufferedInputStream readInputFile;

    public void beginCompressing(String inputFileName,String outfileName, int n) throws IOException {
        System.out.println("begin compressing");
        this.sizeWanted = n;
        this.usedBytes = n;
        FileInputStream inputFile = new FileInputStream(inputFileName);

        this.readInputFile = new BufferedInputStream(inputFile);


        FileOutputStream outputFile = new FileOutputStream(outfileName);

        handleReadFile(inputFile, n);

        System.out.println("finished reading file to be compressed ");

        huffmanCompress.setSettings(inputFileName, outputFile, n, this.usedBytes);
        huffmanCompress.compressFile(frequencies);

    }

    public HuffmanNode getRoot() {
        return huffmanCompress.root;
    }

    public void handleReadFile(FileInputStream inputFile, int n) throws IOException {
        int fixedSize = 50000;
        int divisibleByn = ((int) (fixedSize) / n) * n;
        int leftBytes = readInputFile.available();

        while (leftBytes > 0) {

            if (leftBytes <= fixedSize) {
                readWholeFile(leftBytes);
            } else if (leftBytes > fixedSize) {
                readWholeFile(divisibleByn);

            }
            leftBytes = readInputFile.available();
        }

        this.readInputFile.close();
        inputFile.close();

    }

    public void readWholeFile(int size) throws IOException {
        byte[] byteArray = new byte[size];
        readInputFile.read(byteArray);
        buildWholeFrequencies(byteArray);
    }

    public void buildWholeFrequencies(byte[] bytes) {
        int size = (bytes.length / this.sizeWanted) * this.sizeWanted;
        int i = 0;
        while (i < size) {
            byte[] toStore = new byte[this.sizeWanted];
            for (int j = 0; j < this.sizeWanted; j++) {
                toStore[j] = bytes[i];
                i++;
            }
            addToMap(toStore);
        }
        int leftLength = bytes.length - size;
        byte[] toStore = new byte[this.sizeWanted];
        int ind = 0;
        for (; i < bytes.length; i++) {
            toStore[ind] = bytes[i];
            ind++;
        }
        if (leftLength != 0) {
            addToMapEnd(toStore, leftLength);
        }

    }

    public void addToMap(byte[] array) {
        ByteArrayWrapper wrapper = new ByteArrayWrapper(array);
        frequencies.put(wrapper, frequencies.getOrDefault(wrapper, 0L) + 1);
    }

    public void addToMapEnd(byte[] array, int used) {
        ByteArrayWrapper wrapper = new ByteArrayWrapper(array);
        wrapper.setBytesNum(used);
        wrapper.setLastByte(true);
        this.usedBytes = used;
        frequencies.put(wrapper, frequencies.getOrDefault(wrapper, 0L) + 1);
    }

}
