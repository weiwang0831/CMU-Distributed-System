/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task0;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author weiwang_ww5
 */
public class Block extends Object {

    private int index = 0;
    private Timestamp timestamp = null;
    private String data = "";
    private String previousHash = "";
    private BigInteger nonce = BigInteger.ZERO;
    private int difficulty = 0;

    /**
     *
     * @param index
     * @param timestamp
     * @param data
     * @param difficulty
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    /**
     *
     * @return @throws NoSuchAlgorithmException This method computes a hash of
     * the concatenation of the index, timestamp, data, previousHash, nonce, and
     * difficulty.
     */
    public String calculateHash() throws NoSuchAlgorithmException {
        String concatenate = "" + getIndex() + getTimestamp() + getData() + getPreviousHash() + getNonce() + getDifficulty();
        String result = hash(concatenate);
        return result;
    }

    /**
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String hash(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String hexBinary=DatatypeConverter.printHexBinary(hash);
        return hexBinary;
    }

    /**
     *
     * @return @throws NoSuchAlgorithmException The proof of work methods finds
     * a good hash.
     */
    public String proofOfWork() throws NoSuchAlgorithmException {
        int proofOfWork = 0;
        String hash = "";
        //while number of 0 is not equal to difficulty
        while (proofOfWork != difficulty) {
            this.nonce = this.nonce.add(new BigInteger("1"));
            hash = calculateHash();
            int count = 0;
            for (int i = 0; i < hash.length(); i++) {
                if (hash.charAt(i) == '0') {
                    count++;
                } else {
                    break;
                }
            }
            proofOfWork = count;
        }
        return hash;
    }

    /**
     *
     * @return link the string as JSON string
     */
    @Override
    public String toString() {
        String subJSONstring = "";
        subJSONstring = "{"
                + "\"index\": " + getIndex() + ","
                + "\"time stamp\": \"" + getTimestamp() + "\","
                + "\"Tx\": \"" + getData() + "\","
                + "\"PrevHash\": \"" + getPreviousHash() + "\","
                + "\"nonce\": " + getNonce() + ","
                + "\"difficulty\": " + getDifficulty() + "}";
        return subJSONstring;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

}
