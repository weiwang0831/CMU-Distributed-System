/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task1;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 *
 * @author weiwang_ww5
 */
public class InstallerClient {

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
            System.out.println("Enter symmetric key as a 16-digit integer:");
            Scanner input = new Scanner(System.in);
            String tmp = input.nextLine();
            byte[] key = tmp.getBytes();
            TEA tea = new TEA(key);

            //encrypt message and transfer to server side
            byte[] userInput = inputToByte();
            byte[] encryptMessage = tea.encrypt(userInput);

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.write(encryptMessage);
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
}
