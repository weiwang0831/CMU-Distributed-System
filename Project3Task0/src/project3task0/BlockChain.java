/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task0;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author weiwang_ww5 This BlockChain has exactly two instance members - an
 * ArrayList to hold Blocks and a chain hash to hold a SHA256 hash of the most
 * recently added Block.
 */
public class BlockChain {

    private static List<Block> list;
    private static String chainHash;

    /**
     *
     * @throws NoSuchAlgorithmException initiate the first block in block chain
     */
    public BlockChain() throws NoSuchAlgorithmException {
        list = new ArrayList<Block>();
        Block firstBlock = new Block(0, getTime(), "Genesis", 2);
        list.add(firstBlock);
        chainHash = firstBlock.proofOfWork();
    }

    /**
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     * @difference between difficulty 4 and 5: when the difficulty is 4, the
     * system will create a hash that is not start with 4 zero, while when add
     * this value to chainHash, execute proofOfWork， in this way the hash value
     * will become start with 4 zero. Instead, when the difficulty is 5, after
     * proofOfWork, the hash will start with 5 zero
     * @timeDiff4: I add three transactions into Block. The time for adding them
     * are: 356, 40, 25 millSecond,Average 140 millSeconds. after that conduct
     * verify, it verifies in 0 millSeconds
     * @timeDiff5: I add the exact same three transactions, the corresponding
     * time are: 5728, 11507, 10378,Average is 5619 MillSeconds and the verfies
     * time is 0 millSeconds
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        java.util.Scanner input = new java.util.Scanner(System.in);
        int userInput = 100;
        BlockChain blockchain = new BlockChain();

        while (userInput != 6) {
            //print user options
            System.out.println("0. View basic blockchain status. ");
            System.out.println("1. Add a transaction to the blockchain. ");
            System.out.println("2. Verify the blockchain. ");
            System.out.println("3. View the blockchain. ");
            System.out.println("4. Corrupt the chain. ");
            System.out.println("5. Hide the corruption by repairing the chain. ");
            System.out.println("6. Exit ");
            userInput = input.nextInt();
            switch (userInput) {
                //get blockchain size and return current hash amount
                case 0:
                    System.out.println("Current size of chain: " + blockchain.getChainSize());
                    System.out.println("Current hashes per second by this machine: " + blockchain.hashesPerSecond());
                    break;
                case 1:
                    //get the variable from user
                    System.out.println("Enter difficulty > 0");
                    int diff = input.nextInt();
                    input.nextLine();
                    System.out.println("Enter transaction ");
                    String data = input.nextLine();
                    //create Block object
                    //calculate execute time
                    long start = System.currentTimeMillis();
                    Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(), data, diff);
                    blockchain.addBlock(newBlock);
                    long stop = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was " + (stop - start) + " milliseconds ");
                    break;
                case 2:
                    //verify blockchain status
                    long start1 = System.currentTimeMillis();
                    System.out.println("Verifying entire chain ");
                    System.out.println("Chain verification:" + blockchain.isChainValid());
                    long end1 = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (end1 - start1)
                            + " milliseconds");
                    break;
                case 3:
                    //view the block chain
                    System.out.println("View the Blockchain: ");
                    System.out.println(blockchain.toString());
                    break;
                case 4:
                    //change data for a certain blockchain
                    System.out.println("Corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt ");
                    int id = input.nextInt();
                    input.nextLine();
                    System.out.println("Enter new data for block " + id);
                    String data1 = input.nextLine();
                    blockchain.list.get(id).setData(data1);
                    System.out.println("Block " + id + " now holds " + data1);
                    break;
                case 5:
                    //repair the blockchain that is re-constructed because of step 4
                    System.out.println("Repairing the entire chain ");
                    blockchain.repairChain();
                    break;
                case 6:
                    break;
            }
        }
    }

    /**
     *
     * @param newBlock
     * @throws NoSuchAlgorithmException
     */
    public void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        //get the previous hash of the latest block
        String linktoPrev = chainHash;
        newBlock.setPreviousHash(linktoPrev);
        chainHash = newBlock.proofOfWork();
        //add the block to the list
        list.add(newBlock);

    }

    /**
     *
     * @return chain size
     */
    public int getChainSize() {
        return list.size();
    }

    /**
     *
     * @return the last block of the chain
     */
    public Block getLastBlock() {
        return list.get(list.size() - 1);
    }

    /**
     *
     * @return get the current system time
     */
    public Timestamp getTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp;
    }

    /**
     *
     * @return @throws NoSuchAlgorithmException
     * @hashes per second of the computer holding this chain. It uses a simple
     * string - "00000000" to hash.
     */
    public int hashesPerSecond() throws NoSuchAlgorithmException {
        String second = "00000000";
        long start = System.currentTimeMillis();
        long stop = start;
        int count = 0;
        while (stop - start < 1000) {
            count++;
            hash(second);
            stop = System.currentTimeMillis();
        }
        return count;
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
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= 31; i++) {
            byte b = hash[i];
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     *
     * @return @throws NoSuchAlgorithmException If the chain only contains one
     * block, the genesis block at position 0, this routine computes the hash of
     * the block and checks that the hash has the requisite number of leftmost
     * 0's (proof of work) as specified in the difficulty field.
     */
    public boolean isChainValid() throws NoSuchAlgorithmException {

        //Scenario 1: chain only one block
        if (list.size() == 1) {

            //check1: if the block has has the required number of 0 on the left as difficulty
            Block curBlock = list.get(0);
            int diff = curBlock.getDifficulty();
            String hash = curBlock.calculateHash();
            int proofOfWork = getNumberZero(hash);
            if (proofOfWork != diff) {
                System.out.println("Block hash proofOfWork " + proofOfWork + " does not contains equal number of 0 as difficulty: " + diff);
                return false;
            }
            //check2： if the chain hash equals to this computer hash
            if (!hash.equals(chainHash)) {
                System.out.println("Chain hash " + chainHash + " is not equal to the computed hash " + hash);
                return false;
            }
        } //Scenario 2: chain has more than 1 block
        else {
            for (int i = 1; i < list.size(); i++) {
                Block previousBlock = list.get(i - 1);
                Block curBlock = list.get(i);

                //check1: if the block has has the required number of 0 on the left as difficulty
                int diff = curBlock.getDifficulty();
                String hash = curBlock.calculateHash();
                int proofOfWork = getNumberZero(hash);
                if (proofOfWork < diff) {
                    System.out.println("Block hash " + proofOfWork + " does not contains equal number of 0 as difficulty: " + diff);
                    return false;
                }

                //check2: if the chain hash equals to this computer hash
                if (!previousBlock.calculateHash().equals(curBlock.getPreviousHash())) {
                    System.out.println("Chain block " + i
                            + "previous block hash: " + previousBlock.calculateHash() + " is not equal to"
                            + "current block pointer hash: " + curBlock.getPreviousHash());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param hash
     * @return get number of 0 in the block
     */
    public int getNumberZero(String hash) {
        int proofOfWork = 0;
        for (int j = 0; j < hash.length(); j++) {
            char c = hash.toCharArray()[j];
            if (c == '0') {
                proofOfWork++;
            } else {
                break;
            }
        }
        return proofOfWork;
    }

    /**
     *
     * @throws NoSuchAlgorithmException This routine repairs the chain. It
     * checks the hashes of each block and ensures that any illegal hashes are
     * recomputed. After this routine is run, the chain will be valid. The
     * routine does not modify any difficulty values. It computes new proof of
     * work based on the difficulty specified in the Block.
     */
    public void repairChain() throws NoSuchAlgorithmException {
        //when blockchain size is only 1
        if (list.size() == 1) {
            Block curBlock = list.get(0);
            int diff = curBlock.getDifficulty();
            String hash = curBlock.calculateHash();
            int proofOfWork = getNumberZero(hash);
            System.out.println(proofOfWork);
            if (proofOfWork != diff || !hash.equals(chainHash)) {
                hash = curBlock.proofOfWork();
            }
        } //when blockchain has more than 1 blocks
        else {
            for (int i = 1; i < list.size(); i++) {
                Block previousBlock = list.get(i - 1);
                Block curBlock = list.get(i);
                //check1: if the block has has the required number of 0 on the left as difficulty
                int diff = curBlock.getDifficulty();
                String hash = curBlock.calculateHash();
                int proofOfWork = getNumberZero(hash);
                if (proofOfWork < diff) {
                    hash = curBlock.proofOfWork();
                }
                if (!previousBlock.calculateHash().equals(curBlock.getPreviousHash())) {
                    curBlock.setPreviousHash(previousBlock.calculateHash());
                }
            }
        }
    }

    /**
     *
     * @return link the string into JSON string
     */
    @Override
    public String toString() {
        String tmpString = "";
        String JSONstring = "";
        //link every block JSON string together 
        for (int i = 0; i < list.size(); i++) {
            Block curBlock = list.get(i);
            if (i == 0) {
                tmpString = tmpString + curBlock.toString();
            } else {
                tmpString = tmpString + ",\n" + curBlock.toString();
            }

        }
        //add this part to concatenant with last hash
        tmpString = "[" + tmpString + "],";
        JSONstring = "{\"ds_chain:\"" + tmpString
                + "\n\"chainHash\":" + chainHash + "}";
        return JSONstring;
    }

    public static List getList() {
        return list;
    }

    public static void setList(List list) {
        BlockChain.list = list;
    }

    public static String getChainHash() {
        return chainHash;
    }

    public static void setChainHash(String chainHash) {
        BlockChain.chainHash = chainHash;
    }

}
