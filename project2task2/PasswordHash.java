/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author weiwang_ww5
 */
public class PasswordHash {

    static Scanner input = new Scanner(System.in);
    static Map<String, String[]> installer = new HashMap<>();

    /**
     *
     * @param args
     * @throws NoSuchAlgorithmException
     * Print instructions for user to input
     * Authenticate input information
     */
    public static void main(String args[]) throws NoSuchAlgorithmException {
        print();
        authenticateUser();
    }

    /**
     *
     * @throws NoSuchAlgorithmException
     * Store information user input
     * hash salt, and hash (salt+password) put this information into the map
     */
    public static void print() throws NoSuchAlgorithmException {
        
        //user enter information for one installer
        System.out.println("#Enter user ID");
        String userID = input.next();
        System.out.println();
        System.out.println("#Enter Password");
        String passwd = input.next();
        System.out.println();

        //System generate a random number for salt
        System.out.println("#Gnerating a random number for salt using SecureRandom");
        byte[] salt = generateRandomBytes(20);
        String salt1=hash(new String(salt));
        String hashSaltPasswd = hash(salt1 + passwd);

        //Concatenent results in the string to print
        String result = "#User ID = " + userID
                + "\n#Salt = " + salt1
                + "\n#Hash of salt+password= " + hashSaltPasswd;
        System.out.println(result);

        String[] hash = new String[]{salt1, hashSaltPasswd};
        installer.put(userID, hash);
    }

    /**
     *
     * @param count
     * @return
     * Generate random bytes with count bit of byte array
     */
    public static byte[] generateRandomBytes(int count) {
        SecureRandom random = new SecureRandom();
        byte[] values = new byte[count];
        random.nextBytes(values);
        return values;
    }

    /**
     *
     * @throws NoSuchAlgorithmException
     * Check user and password authentication, if correct, return true, if not correct, return false
     */
    public static void authenticateUser() throws NoSuchAlgorithmException {
        //User input username
        System.out.println("#Enter user ID for authentication testing: ");
        String userID = input.next();
        System.out.println();
        //user input password
        System.out.println("#Enter password for authetication testing: ");
        String passwd = input.next();
        System.out.println();

        //compare password store in the map, and the password user enter, both after hash
        if (!(PasswordHash.hash(installer.get(userID)[0] + passwd)).equals(installer.get(userID)[1])) {
            System.out.println("#Not able to validate this user id, password pair.");
        } else {
            System.out.println("#Validated user id and password pair.");
        }
    }

    /**
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * Hash String, return String
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
}
