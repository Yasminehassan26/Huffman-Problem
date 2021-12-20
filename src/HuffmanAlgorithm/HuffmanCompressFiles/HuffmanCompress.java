package HuffmanAlgorithm.HuffmanCompressFiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import HuffmanAlgorithm.ByteArrayWrapper;
import HuffmanAlgorithm.HuffmanNode;

public class HuffmanCompress {
    HashMap<ByteArrayWrapper, String> codeWordMap = new HashMap<>();

    private StringBuilder treeHeader;
    private StringBuilder byteBuilder;

    private FileInputStream inputFile;
    private FileOutputStream outputFile;
    public BufferedOutputStream writeOutputFile;
    private BufferedInputStream readInputFile;
    private int n;
    private int maxSize = 50000;
    private byte[] bytesToBeWritten;
    private int index = 0;
    public HuffmanNode root;
    private int usedNodesCount = 0;

    public void setSettings(String inputFilePath, FileOutputStream outputFile, int n, int usedNodes) {
        try {
            this.inputFile = new FileInputStream(inputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bytesToBeWritten = new byte[this.maxSize];
        this.outputFile = outputFile;
        this.writeOutputFile = new BufferedOutputStream(outputFile);
        this.readInputFile = new BufferedInputStream(this.inputFile);
        this.n = n;
        treeHeader = new StringBuilder(this.maxSize);
        byteBuilder = new StringBuilder(9);
        this.usedNodesCount = usedNodes;
    }

    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // handle the flow of actions
    public void compressFile(HashMap<ByteArrayWrapper, Long> frequencies) {
        root = constructTree(frequencies);

        if (root.isLeafNode()) {
            codeWordMap.put(root.getByteWrapper(), "0");
            treeHeader.append("1");
            byteToString(root);
        } else {
            encodeNodes(root, new StringBuilder());
        }
        System.out.println("finished creating codewords");

        compressFunction();

    }

    public void compressFunction() {
        compressDictionary();

        System.out.println("finished writing the header of file");
        try {
            compressInputFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("finished compressing the body of file");

        try {
            this.writeOutputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] stringToBytes(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

    public void compressDictionary() {
        // i construct the header of the dictionnary
        int estimateLength = treeHeader.length();
        StringBuilder header = new StringBuilder(estimateLength);
        header.append(String.valueOf(n));
        header.append(",");
        header.append(treeHeader.toString());
        String temp = header.toString();
        int lengthH = temp.length()+100;

        StringBuilder finalHeader = new StringBuilder(lengthH);
        finalHeader.append(String.valueOf(this.usedNodesCount));
        finalHeader.append(",");
        finalHeader.append(String.valueOf(temp.length()));
        finalHeader.append(",");
        finalHeader.append(temp);
        byte[] bytes = finalHeader.toString().getBytes(Charset.forName("ISO-8859-1"));
        writeBytesToFile(bytes);
    }

    public void compressInputFile() throws IOException {
        int fixedSize = 50000;
        int divisibleByn = ((int) (fixedSize) / n) * n;
        int leftBytes = readInputFile.available();

        while (leftBytes > 0) {
            if (leftBytes <= fixedSize) {
                readWholeFile(inputFile, leftBytes);
            } else if (leftBytes > fixedSize) {
                readWholeFile(inputFile, divisibleByn);
            }
            leftBytes = readInputFile.available();
        }

        readInputFile.close();

        if (byteBuilder.length() > 0) {
            byte b = (byte) Integer.parseInt(byteBuilder.toString(), 2);
            b = (byte) (b << (8 - byteBuilder.length()));
            storesBytes(b);
            storesBytes((byte) byteBuilder.length());
        }

        else {
            storesBytes((byte) 8);
        }
        if (this.index != 0) {
            writeFinalBytesToFile(this.bytesToBeWritten, this.index);
        }

    }

    public void readWholeFile(FileInputStream inputFile, int size) throws IOException {
        byte[] byteArray = new byte[size];
        readInputFile.read(byteArray);
        buildWholeCodeWords(byteArray);
    }

    public void buildWholeCodeWords(byte[] bytes) {
        int size = (bytes.length / this.n) * this.n;
        int i = 0;
        while (i < size) {
            byte[] toStore = new byte[this.n];
            for (int j = 0; j < this.n; j++) {
                toStore[j] = bytes[i];
                i++;
            }
            ByteArrayWrapper wrapper = new ByteArrayWrapper(toStore);
            findBytesCodeWord(wrapper);
        }
        int leftLength = bytes.length - i;
        byte[] toStore = new byte[this.n];
        int ind = 0;
        for (; i < bytes.length; i++) {
            toStore[ind] = bytes[i];
            ind++;
        }
        if (leftLength != 0) {

            ByteArrayWrapper wrapper = new ByteArrayWrapper(toStore);
            findBytesCodeWord(wrapper);

        }

    }

    public void findBytesCodeWord(ByteArrayWrapper wrapper) {
        String codeWord = codeWordMap.get(wrapper);
        int diff = 8 - byteBuilder.length();
        if (codeWord.length() <= diff) {
            byteBuilder.append(codeWord);
            if (byteBuilder.length() == 8) {
                byte b = (byte) Integer.parseInt(byteBuilder.toString(), 2);
                // write to file
                storesBytes(b);
                byteBuilder = new StringBuilder(9);
            }
        } else {
            byteBuilder.append(codeWord.substring(0, diff));
            byte b = (byte) Integer.parseInt(byteBuilder.toString(), 2);
            // write to file
            storesBytes(b);
            byteBuilder = new StringBuilder(9);

            if (codeWord.length() - diff < 8) {
                byteBuilder.append(codeWord.substring(diff));
            } else {
                // write to file
                handleString(codeWord.substring(diff));
            }
        }
    }

    public void storesBytes(byte b) {
        if (this.index < this.maxSize) {
            this.bytesToBeWritten[this.index] = b;
            this.index++;
        } else {
            writeBytesToFile(this.bytesToBeWritten);
            this.bytesToBeWritten = new byte[this.maxSize];
            this.index = 0;
            this.bytesToBeWritten[this.index] = b;
            this.index++;
        }
    }

    public void handleString(String codeWord) {
        int size = codeWord.length() / 8;
        int i = 0;

        for (i = 0; i < size; i++) {
            byteBuilder.append(codeWord.substring(i * 8, (i * 8) + 8));
            byte b = (byte) Integer.parseInt(byteBuilder.toString(), 2);
            storesBytes(b);
            byteBuilder = new StringBuilder(9);

        }
        byteBuilder.append(codeWord.substring(i * 8));

    }

    public void writeBytesToFile(byte[] b) {
        try {
            this.writeOutputFile.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFinalBytesToFile(byte[] b, int end) {
        try {
            this.writeOutputFile.write(b, 0, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HuffmanNode constructTree(HashMap<ByteArrayWrapper, Long> frequencies) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();

        for (Map.Entry<ByteArrayWrapper, Long> set : frequencies.entrySet()) {
            HuffmanNode node = new HuffmanNode(set.getKey(), set.getValue());
            queue.add(node);

        }

        while (queue.size() > 1) {
            HuffmanNode temp1 = queue.poll();
            HuffmanNode temp2 = queue.poll();
            long frequency = temp1.getFrequnecy() + temp2.getFrequnecy();
            HuffmanNode newNode = new HuffmanNode(null, frequency);
            newNode.setLeftNode(temp1);
            newNode.setRightNode(temp2);
            queue.add(newNode);
        }
        return queue.poll();

    }

    public void encodeNodes(HuffmanNode node, StringBuilder codeword) {
        if (!node.isLeafNode()) {
            treeHeader.append("0");
            codeword.append("0");
            encodeNodes(node.getLeftNode(), codeword);

            codeword.setCharAt(codeword.length()-1, '1');
            encodeNodes(node.getRightNode(), codeword);
            codeword.deleteCharAt(codeword.length()-1);

        } else {
            this.codeWordMap.put(node.getByteWrapper(), codeword.toString());
            treeHeader.append("1");
            byteToString(node);
            return;
        }
    }

    public void byteToString(HuffmanNode node) {
        treeHeader.append(new String(node.getByteWrapper().getData(), Charset.forName("ISO-8859-1")));

    }

    public HashMap<ByteArrayWrapper, String> getHashMap() {
        return this.codeWordMap;
    }

}
