package HuffmanAlgorithm;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private long frequnecy;
    private HuffmanNode leftNode;
    private HuffmanNode rightNode;
    private ByteArrayWrapper byteWrapper;
    // private StringBuilder byteEncodingValue = new StringBuilder();

    // public void setByteEncodingValue(String byteEncodingValue) {
    //     this.byteEncodingValue.append(byteEncodingValue);
    // }
    // public String getByteEncodingValue() {
    //     return byteEncodingValue.toString();
    // }

    public HuffmanNode(ByteArrayWrapper wrapper, long frequency) {
        this.byteWrapper = wrapper;
        this.frequnecy = frequency;
    }

    public HuffmanNode(ByteArrayWrapper wrapper) {
        this.byteWrapper = wrapper;
    }

    // public HuffmanNode(String parentPath) {
    //     this.setByteEncodingValue(parentPath);
    // }

    public HuffmanNode() {

    }

    public ByteArrayWrapper getByteWrapper() {
        return byteWrapper;
    }

    public void setByteWrapper(ByteArrayWrapper byteWrapper) {
        this.byteWrapper = byteWrapper;
    }

    // setters and getters
    public long getFrequnecy() {
        return frequnecy;
    }

 

    public HuffmanNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(HuffmanNode leftNode) {
        this.leftNode = leftNode;
    }

    public HuffmanNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(HuffmanNode rightNode) {
        this.rightNode = rightNode;
    }

 

    public boolean isLeafNode() {
        if (this.leftNode == null && this.rightNode == null)
            return true;
        return false;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        long diff = this.frequnecy - node.getFrequnecy();
        if (diff > 0)
            return 1;
        else if (diff < 0)
            return -1;
        return 0;
    }
}
