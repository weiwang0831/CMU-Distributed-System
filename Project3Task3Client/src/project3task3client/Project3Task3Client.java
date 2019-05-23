/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3task3client;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author DELL
 */
// A simple class to wrap a result.
class Result {

    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

public class Project3Task3Client {

    static BigInteger e = new BigInteger("65537");
    static BigInteger n = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
    static BigInteger d = new BigInteger("339177647280468990599683753475404338964037287357290649639740920420195763493261892674937712727426153831055473238029100340967145378283022484846784794546119352371446685199413453480215164979267671668216248690393620864946715883011485526549108913");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
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
                    doPost(transaction, diff);
                    //String result = parseXML(XMLString);
                    long stop = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was " + (stop - start) + " milliseconds ");
                    break;
                case 2:
                    //verify blockchain status
                    long start1 = System.currentTimeMillis();
                    System.out.println("Verifying entire chain ");
                    System.out.println("Chain verification:" + read("verify"));
                    long end1 = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (end1 - start1)
                            + " milliseconds");
                    break;
                case 3:
                    //view the block chain
                    System.out.println("View the Blockchain: ");
                    System.out.println(read("view"));
                    break;
                case 4:
                    break;
            }
        }
    }

    // read a value associated with a name from the server
    // return either the value read or an error message
    public static String read(String name) {
        Result r = new Result();
        int status = 0;
        if ((status = doGet(name, r)) != 200) {
            return "Error from server " + status;
        }
        return r.getValue();
    }

    // assign a string value to a string name (One character names for demo)
    public static boolean assign(String name, int value) {
        // We always want to be able to assign so we may need to PUT or POST.
        // Try to PUT, if that fails then try to POST
        doPost(name, value);
        return true;
    }

    public static int doGet(String name, Result r) {

        // Make an HTTP GET passing the name on the URL line
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;

        try {

            // pass the name on the URL line
            URL url = new URL("http://localhost:8080/Project3Task3Server/BlockChainService" + "//" + name);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");
            
            

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                response += output;

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return value from server 
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }

    public static int doPost(String transaction, int diff) {

        int status = 0;
        String output;

        try {
            // Make call to a particular URL
            URL url = new URL("http://localhost:8080/Project3Task3Server/BlockChainService/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to POST and send transaction diff pair
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            String value = transaction + "##" + diff;
            out.write(value);
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();

            //close the connection
            conn.disconnect();
        } // handle exceptions // handle exceptions // handle exceptions // handle exceptions
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return HTTP status
        return status;
    }

}
