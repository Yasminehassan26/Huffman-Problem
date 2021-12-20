package HuffmanAlgorithm;

import java.util.Arrays;

public final class ByteArrayWrapper {
    private final byte[] data;
    private boolean lastByte = false;
    private int bytesNum = 0;

    public boolean isLastByte() {
        return lastByte;
    }

    public void setLastByte(boolean lastByte) {
        this.lastByte = lastByte;
    }

    public int getBytesNum() {
        return bytesNum;
    }

    public void setBytesNum(int bytesNum) {
        this.bytesNum = bytesNum;
    }

    public byte[] getData() {
        return data;
    }

    public ByteArrayWrapper(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteArrayWrapper)) {
            return false;
        }
        return Arrays.equals(data, ((ByteArrayWrapper) other).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}