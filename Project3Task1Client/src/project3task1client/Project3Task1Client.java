/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task1client;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import ww5.cmu.edu.Block;
import ww5.cmu.edu.NoSuchAlgorithmException_Exception;

/**
 *
 * @author weiwang_ww5
 */
public class Project3Task1Client {

    static BigInteger e = new BigInteger("65537");
    static BigInteger n = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
    static BigInteger d = new BigInteger("339177647280468990599683753475404338964037287357290649639740920420195763493261892674937712727426153831055473238029100340967145378283022484846784794546119352371446685199413453480215164979267671668216248690393620864946715883011485526549108913");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException_Exception, NoSuchAlgorithmException {
        // TODO code application logic here
        java.util.Scanner input = new java.util.Scanner(System.in);
        int userInput = 100;
        //BlockChain blockchain = new BlockChain();
        while (userInput != 4) {
            //print user options
            System.out.println("1. Add a transaction to the blockchain. ");
            System.out.println("2. Verify the blockchain. ");
            System.out.println("3. View the blockchain. ");
            System.out.println("4. Exit ");
            userInput = input.nextInt();
            switch (userInput) {
                //get blockchain size and return current hash amount
                case 1:
                    //get the variable from user
                    System.out.println("Enter difficulty > 0");
                    int diff = input.nextInt();
                    input.nextLine();
                    System.out.println("Enter transaction ");
                    //get the user input transaction data
                    String data = input.nextLine();
                    //Compute a SHA-256 digest of these bytes
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    // Get the bytes from the string to be signed. 
                    byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
                    byte[] extraHash = new byte[hash.length + 1];
                    // Copy these bytes into a byte array that is one byte longer than needed.          
                    //The resulting byte array has its extra byte set to zero. 
                    System.arraycopy(hash, 0, extraHash, 1, hash.length);
                    extraHash[0] = 0;
                    // Create a BigInteger from the byte array.
                    BigInteger cryptInteger = new BigInteger(extraHash);
                    // Encrypt the BigInteger with RSA d and n. 
                    BigInteger crypt = cryptInteger.modPow(d, n);
                    String encrypt = crypt.toString();
                    String transaction = data + "#" + encrypt;
                    //create Block object
                    long start = System.currentTimeMillis();
                    if (addBlock(transaction, diff) != null) {
                        long stop = System.currentTimeMillis();
                        System.out.println("Total execution time to add this block was " + (stop - start) + " milliseconds ");
                    } else {
                        System.out.println("This block action is denied, signature not approved");
                    }
                    break;
                case 2:
                    //verify blockchain status
                    long start1 = System.currentTimeMillis();
                    System.out.println("Verifying entire chain ");
                    System.out.println("Chain verification:" + verify());
                    long end1 = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (end1 - start1)
                            + " milliseconds");

                    break;
                case 3:
                    //view the block chain
                    System.out.println("View the Blockchain: ");
                    System.out.println(view());
                    break;
                case 4:
                    break;
            }
        }
    }

    private static Block addBlock(java.lang.String data, int difficulty) throws NoSuchAlgorithmException_Exception {
        ww5.cmu.edu.BlockchainServer_Service service = new ww5.cmu.edu.BlockchainServer_Service();
        ww5.cmu.edu.BlockchainServer port = service.getBlockchainServerPort();
        return port.addBlock(data, difficulty);
    }

    private static Boolean verify() throws NoSuchAlgorithmException_Exception {
        ww5.cmu.edu.BlockchainServer_Service service = new ww5.cmu.edu.BlockchainServer_Service();
        ww5.cmu.edu.BlockchainServer port = service.getBlockchainServerPort();
        return port.verify();
    }

    private static String view() {
        ww5.cmu.edu.BlockchainServer_Service service = new ww5.cmu.edu.BlockchainServer_Service();
        ww5.cmu.edu.BlockchainServer port = service.getBlockchainServerPort();
        return port.view();
    }

}
