package HuffmanAlgorithm.HuffmanDecompressFiles;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import HuffmanAlgorithm.ByteArrayWrapper;
import HuffmanAlgorithm.HuffmanNode;

public class HuffmanDecompress {
    HuffmanNode root;
    private HuffmanNode tempRoot;
    private int maxSize = 50000;
    private byte[] bytesToBeWritten;
    private byte[] lastTwoBytes = new byte[2];
    private boolean empty = true;
    private int index = 0;
    FileOutputStream outputFile;
    BufferedOutputStream writeOutputFile;
    private int lastUsedBytes=0;
    private boolean onlyRoot=false;


    public void setSettings(HuffmanNode r, FileOutputStream outputFile, int usedBytes,boolean onlyRoot) {
        this.root = r;
        this.tempRoot = r;
        this.outputFile = outputFile;
        this.writeOutputFile = new BufferedOutputStream(outputFile);
        bytesToBeWritten = new byte[this.maxSize];
        this.lastUsedBytes=usedBytes;
        this.onlyRoot=onlyRoot;
    }

    public void setRoot() {
        this.tempRoot = this.root;
    }

    public void decompressData(byte[] byteArray) throws UnsupportedEncodingException {

        if (!this.empty) {
            for (int i = 0; i < 2; i++) {
                byte b = lastTwoBytes[i];
                String temp = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                handleByte(temp);
            }
            this.empty = true;
        }
        int i = 0;
        for (i = 0; i < byteArray.length - 2; i++) {
            byte b = byteArray[i];
            String temp = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            handleByte(temp);
        }

        for (int j = 0; j < 2; j++) {
            lastTwoBytes[j] = byteArray[i];
            i++;
        }
        this.empty = false;

    }

    public void decompressLastBytes(byte[] byteArray) throws UnsupportedEncodingException {

        if (this.empty) {

            int length = byteArray.length;
            String temp;
            for (int i = 0; i < length - 2; i++) {
                byte b = byteArray[i];
                temp = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                handleByte(temp);
            }
            temp = String.format("%8s", Integer.toBinaryString(byteArray[length - 1] & 0xFF)).replace(' ', '0');
            int shift = Integer.parseInt(temp, 2);
            temp = String.format("%8s", Integer.toBinaryString(byteArray[length - 2] & 0xFF)).replace(' ', '0');
            handleLastByte(temp, shift);
        } else
            handleEndFile(byteArray);
    }

    private void handleEndFile(byte[] byteArray) throws UnsupportedEncodingException {

        int length = byteArray.length;
        String temp;
        if (length == 1) {

            byte b = lastTwoBytes[0];
            temp = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            handleByte(temp);
            temp = String.format("%8s", Integer.toBinaryString(byteArray[0] & 0xFF)).replace(' ', '0');
            int shift = Integer.parseInt(temp, 2);
            temp = String.format("%8s", Integer.toBinaryString(lastTwoBytes[1] & 0xFF)).replace(' ', '0');
            this.empty = true;
            handleLastByte(temp, shift);
        } else {

            if (!this.empty) {
                for (int i = 0; i < 2; i++) {
                    byte b = lastTwoBytes[i];
                    temp = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                    handleByte(temp);
                }
                this.empty = true;
            }
            for (int i = 0; i < length - 2; i++) {
                byte b = byteArray[i];
                temp = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                handleByte(temp);
            }
            temp = String.format("%8s", Integer.toBinaryString(byteArray[length - 1] & 0xFF)).replace(' ', '0');
            int shift = Integer.parseInt(temp, 2);
            temp = String.format("%8s", Integer.toBinaryString(byteArray[length - 2] & 0xFF)).replace(' ', '0');
            handleLastByte(temp, shift);
        }
    }

    public void handleByte(String byteString) throws UnsupportedEncodingException {

        for (int i = 0; i < byteString.length(); i++) {
            char c = byteString.charAt(i);
            if (c == '0')
                tempRoot = tempRoot.getLeftNode();
            else if (c == '1')
                tempRoot = tempRoot.getRightNode();

            if (tempRoot.isLeafNode()) {
                ByteArrayWrapper wrapper = tempRoot.getByteWrapper();
                    byte[] b = wrapper.getData();
                    storeBytes(b);
                    setRoot();
            }
        }
    }
    public void handleLastByte(String byteString, int length) {

        for (int i = 0; i < length; i++) {
            char c = byteString.charAt(i);
            if (c == '0' && !this.onlyRoot)
                tempRoot = tempRoot.getLeftNode();
            else if (c == '1' )
                tempRoot = tempRoot.getRightNode();
            if (tempRoot.isLeafNode()) {
                ByteArrayWrapper wrapper = tempRoot.getByteWrapper();
                if (i==length-1) {
                    int size = this.lastUsedBytes;
                    byte[] b = new byte[size];
                    byte[] byteWrapper = wrapper.getData();
                    int ind = 0;
                    for (int m = 0; m < size; m++) {
                        b[m] = byteWrapper[ind];
                        ind++;
                    }
                    storeBytes(b);
                } else {
                    byte[] b = wrapper.getData();
                    storeBytes(b);
                }

                setRoot();
            }
        }
    }

    public void storeBytes(byte[] b) {
        if (this.index < this.maxSize) {
            for (int i = 0; i < b.length; i++) {
                this.bytesToBeWritten[this.index] = b[i];
                this.index++;
                
                if (this.index == this.maxSize) {
                    writeBytesToFile(this.bytesToBeWritten);
                    this.bytesToBeWritten = new byte[this.maxSize];
                    this.index = 0;
                }
            }

        } else {
            writeBytesToFile(this.bytesToBeWritten);
            this.bytesToBeWritten = new byte[this.maxSize];
            this.index = 0;
            for (int i = 0; i < b.length; i++) {
                this.bytesToBeWritten[this.index] = b[i];
                this.index++;
            }

        }
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

    public int getIndex() {
        return this.index;
    }

    public byte[] getBytesToBeWritten() {
        return this.bytesToBeWritten;
    }

    public boolean getEmpty() {
        return this.empty;
    }

    public void store(){
        String temp = String.format("%8s", Integer.toBinaryString(this.lastTwoBytes[1] & 0xFF)).replace(' ', '0');
        int shift = Integer.parseInt(temp, 2);
        temp = String.format("%8s", Integer.toBinaryString(this.lastTwoBytes[0] & 0xFF)).replace(' ', '0');
        handleLastByte(temp, shift);
    }

}
