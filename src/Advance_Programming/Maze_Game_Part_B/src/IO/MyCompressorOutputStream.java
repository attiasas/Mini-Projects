package Advance_Programming.Maze_Game_Part_B.src.IO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static IO.BinaryOperations.*;

/**
 * This class decorate an OutputStream to compress the information - using LZ78 algorithm with dynamic bitwise compression for the index
 * Created by Assaf Attias
 */
public class MyCompressorOutputStream extends OutputStream
{
    private OutputStream out;
    private Trie trie;

    /**
     * Constructor
     * @param outputStream - streamer to decorate
     */
    public MyCompressorOutputStream(OutputStream outputStream) {
        out = outputStream;
        trie = new Trie();
    }

    @Override
    public void write(int b) throws IOException
    {
        // get deflated pair
        Pair deflated = trie.deflate((byte) (b & 0xFF));

        if (deflated != null)
        {
            // write index bitwise compressed pair
            ArrayList<Byte> indexBytes = convertIndexToBytes(deflated.index);

            for (Byte indexByte : indexBytes)
            {
                out.write((indexByte & 0xFF));
            }

            out.write((deflated.value & 0xFF));
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException
    {
        // validate
        if(bytes == null) return;

        for (byte b : bytes) {
            write(b);
        }

        flush();
    }

    @Override
    public void flush() throws IOException
    {
        if (trie.crawler != trie.root)
        {
            // flushed and crawler is not root (has excess known prefix)
            out.write(0); // sign that flushed and known prefix
            ArrayList<Byte> indexBytes = convertIndexToBytes(trie.crawler.index);
            for (Byte indexByte : indexBytes)
            {
                out.write((indexByte & 0xFF));
            }
            trie.crawler = trie.root;
        }

        out.flush();
    }

    /**
     * Convert index to bitwise compressed bytes representing the integer - MSB of the byte telling that the next byte is connected to the current
     * @param startIndex - int index to convert to bytes
     * @return - bitwise representation of the integer
     */
    private ArrayList<Byte> convertIndexToBytes(int startIndex)
    {
        int remainIndex = startIndex;
        ArrayList<Byte> converted = new ArrayList<>();

        // convert to binary bitwise representation
        ArrayList<Byte> byteIndex = toBytes(startIndex);
        while (byteIndex.size() > 7)
        {
            // split representation to byte size (8 bits = 8 binary bytes)
            ArrayList<Byte> bytesToSend = new ArrayList<>();

            for(int i = 0; i < 7; i++) bytesToSend.add(byteIndex.remove(0));
            bytesToSend.add((byte)1); // add delimiter for MSB (8th bit)

            converted.add(transformToBitwise(bytesToSend)); // covert splited representation to an byte value
        }

        // covert the leftover binary bitwise (less than 7 bits remains)
        ArrayList<Byte> bytesToSend = new ArrayList<>();
        while (!byteIndex.isEmpty()) bytesToSend.add(byteIndex.remove(0));
        converted.add(transformToBitwise(bytesToSend));

        return converted;
    }

    /**
     * Represents a pair in LZ compression
     */
    private class Pair
    {
        public int index;
        public byte value;

        public Pair(int index, byte value)
        {
            this.index = index;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return "[" + index + "," + value + "]";
        } // for debug
    }

    /**
     * Represents a Trie tree that deflates bytes and returning a pair for LZ compression
     */
    private class Trie
    {
        // trie node class
        private class TrieNode
        {
            public HashMap<Byte,TrieNode> children;
            public int index;

            TrieNode(int index)
            {
                this.index = index;
                children = new HashMap<>();
            }
        }

        public TrieNode root;
        public TrieNode crawler;
        private int nodeIndexCount;

        /**
         * Constructor
         */
        public Trie()
        {
            root = new TrieNode(1); // index 0 - delimiter
            crawler = root;
            nodeIndexCount = 2;
        }

        /**
         * Deflate a byte and store the information, if its an unknown new prefix a pair is returned, else null.
         * @param b - byte to deflate
         * @return - a pair if unknown, else null is returned
         */
        public Pair deflate(byte b)
        {
            Pair result = null;

            if(crawler.children.containsKey(b))
            {
                // known
                crawler = crawler.children.get(b);
            }
            else
            {
                // unknown
                result = new Pair(crawler.index,b);
                crawler.children.put(b,new TrieNode(nodeIndexCount));
                nodeIndexCount++;
                crawler = root;
            }

            return result;
        }
    }
}
