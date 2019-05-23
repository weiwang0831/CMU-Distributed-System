/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task2;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author weiwang_ww5
 */
public class InstallerClientAndRSA {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        // arguments supply message and hostname
        Socket s = null;
        try {
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);
            
            //set public key
            BigInteger e=new BigInteger("65537");
            BigInteger n=new BigInteger("71474935626032657300820349884523278906246807737349632910476134944633196267381");
            //generate random key for TEA
            Random rnd = new Random();             
            BigInteger teaKey = new BigInteger(16*8,rnd);
            byte[] key=teaKey.toByteArray();
            //encrypt TEA key
            BigInteger crypt=teaKey.modPow(e, n);
            byte[] cryptKey=crypt.toByteArray();
            //encrypt message and transfer to server side
            TEA tea = new TEA(key);
            byte[] userInput = inputToByte();
            byte[] encryptMessage = tea.encrypt(userInput);

            //transmit data to server side
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.write(cryptKey);
            out.flush();
            out.write(encryptMessage);
            out.flush();
            // read a line of data from the stream
            byte[] receive=new byte[100];
            int data = in.read(receive);
            String success=new String(receive);
            System.out.println("Received: " + success);
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }

    /**
     *
     * @return
     * User input information following instructions
     * store information into JSON String, return successful message
     */
    public static byte[] inputToByte() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter your ID: ");
        String id = input.nextLine();
        System.out.println("Enter your password: ");
        String password = input.nextLine();
        System.out.println("Enter sensor ID: ");
        String sensorID = input.nextLine();
        System.out.println("Enter new sensor location: ");
        String sensorLocation = input.nextLine();
        //store sensor location by split , into latitude, longitude, altitude
        String[] splited = sensorLocation.split(",");

        String JSONString = "{Credentials:"
                + "{\"ID\":" + id + ",\"passwd\":" + password + "},"
                + "\"Sensor ID\":" + sensorID + ","
                + "\"Latitude\":" + splited[0] + ",\"Longitude\":" + splited[1] + "}";
        byte[] result = JSONString.getBytes();
        return result;
    }
    
    /**
     *
     * @param count
     * @return
     * Generate random bytes with count bit of byte array
     */
    public static byte[] generateRandomBytes(int count) {
        Random random = new Random();
        byte[] values = new byte[count];
        random.nextBytes(values);
        return values;
    }
}
