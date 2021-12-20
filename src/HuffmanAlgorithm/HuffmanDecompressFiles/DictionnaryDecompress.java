package HuffmanAlgorithm.HuffmanDecompressFiles;

import java.nio.charset.Charset;
import HuffmanAlgorithm.ByteArrayWrapper;
import HuffmanAlgorithm.HuffmanNode;

public class DictionnaryDecompress {

    private HuffmanNode root;
    private int n;
    private boolean onlyRoot=false;
    private int globalIndex=0;

    public void setOnlyRoot() {
         this.onlyRoot=true;
    }
    public boolean getOnlyRoot() {
      return  this.onlyRoot;
   }
    public HuffmanNode getRoot() {
        return this.root;
    }

    public void setSettings(int n) {
        this.n = n;
    }

    public int getN() {
        return this.n;
    }

    public void constructDictionnary(StringBuilder header) {
        int k = 0;
        for (k = 0; k < header.length(); k++) {
            char c = header.charAt(k);
            if (c == ',')
                break;
        }
      
        setSettings(Integer.parseInt(header.substring(0, k)));
        k++;
         this.globalIndex=k;

        root = new HuffmanNode();

        if (header.charAt(this.globalIndex) == '1') {
            this.globalIndex++;
            byte[] byteArray = createByte(header);
            root.setByteWrapper(new ByteArrayWrapper(byteArray));
            this.setOnlyRoot();

        } 
        else {
            System.out.println("constructing the tree....");
            constructTree(root, header);
        }

    }


    public byte[] createByte(StringBuilder header) {
        int size = header.length();
        int i = 0;
        StringBuilder bytes = new StringBuilder(this.n);
        while (i < size && i < this.n) {
            bytes.append(header.charAt(this.globalIndex));
            this.globalIndex++;
            i++;
        }
        byte[] b=new byte[i];
        b=bytes.toString().getBytes(Charset.forName("ISO-8859-1"));
        return b;
    }

    public static byte[] stringToBytes(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

    public void constructTree(HuffmanNode node, StringBuilder header) {
        if (header.length() == 0)
            return;
        if (header.charAt(this.globalIndex) != '1') {

            HuffmanNode leftNode = new HuffmanNode();
            node.setLeftNode(leftNode);
            this.globalIndex++;

            constructTree(node.getLeftNode(), header);
            HuffmanNode rightNode = new HuffmanNode();
            node.setRightNode(rightNode);
            constructTree(node.getRightNode(), header);
        }
        else if (header.charAt(this.globalIndex) == '1') {
            this.globalIndex++;
            byte[] byteArray = createByte(header);
            ByteArrayWrapper wrapper = new ByteArrayWrapper(byteArray);
            node.setByteWrapper(wrapper);
            return;
        }
    }



}
