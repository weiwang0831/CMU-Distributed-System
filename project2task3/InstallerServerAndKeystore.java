/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project2task3;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author weiwang_ww5
 */
public class InstallerServerAndKeystore {

    static int count = 0;
    //Map store info include <String installerID,[String salt, String hashsaltPasswd]>
    static Map<String, String[]> installerInfo = new HashMap<>();
    //Map store sensor information include <sensorID, [latitude,longitude,installerName]>
    static Map<Integer, String[]> sensorInfo = new HashMap<>();
    static BigInteger d;
    static BigInteger n;

    /**
     *
     * @param args
     * @throws NoSuchAlgorithmException Set key for server Add installer
     * information into installMap While socket is linked, build connection and
     * input data
     */
    public static void main(String args[]) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);

            //get key from jsk file and certificate file
            String keyFileName = "GunshotSensorKeys.jsk";
            String passwd = "sesame";
            String alias = "GunshotSensorCert";
            char[] passWord = passwd.toCharArray();
            FileInputStream fis = new FileInputStream(keyFileName);
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(fis, passWord);
            fis.close();
            Certificate cert = keyStore.getCertificate(alias);
            RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
            Key key = keyStore.getKey(alias, passWord);
            RSAPrivateKey privateKey = (RSAPrivateKey) key;

            //get private key and modulus
            d = privateKey.getPrivateExponent();
            n = privateKey.getModulus();

            addMapInfo();
            System.out.println("Activities of the sensor installers: ");
            while (true) {
                //User input key, turn into bytes and generate tea object
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
                count++;
            }

        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());

        }
    }

    /**
     * Add map information into installer map, which can be used for later
     * verification
     */
    public static void addMapInfo() {
        //Installer Barack
        String[] u1 = new String[]{"3E2FAD14A4489904A7F4201297A750D20F0FC9A3A4E61D60BAE8B7CB0F59E53D",
            "B0EC56268806D092DDB5FB72FBD8871A9D2F82D5F4EACC5CE8EEA5E970437D03",
            "Chief Sensor Installer"};
        installerInfo.put("Barack", u1);
        //Installer Hillary
        String[] u2 = new String[]{"9B7DF180427B06AEAA58055347A2D7AFB8C23C3AE07FBDF6C6457F638DD15922",
            "667A0D59A637ACB27442050223D5B7AF0330F828D456AF4C7A834DC637153DD5",
            "Associate Sensor Installer"};
        installerInfo.put("Hillary", u2);
        //Installer Donald
        String[] u3 = new String[]{"039E1FBC0563341A1DC0C47132571E4922F7E7977B2B882E8AD4097175EDBDDB",
            "BEB963D14DA6CEFDA6DBAC0C3FA0C44119A566197859168E7B8DC6E8A3B1C5CB",
            "Associate Sensor Installer"};
        installerInfo.put("Donald", u3);
    }
}

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    String userID;
    String title;
    String response;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    //Trim a byte array into a new byte array with no empty value
    public byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    public void run() {
        try {
            //Create new object for TEA class, construct with key
            byte[] tmpKey = new byte[2000];
            byte[] tmpMessage = new byte[2000];

            //readin the cryptkey
            in.read(tmpKey);
            in.read(tmpMessage);

            //trim input data with no empty value
            byte[] inputKey = trim(tmpKey);
            byte[] inputData = trim(tmpMessage);

            //decryptKey
            BigInteger c = new BigInteger(inputKey);
            BigInteger clear = c.modPow(InstallerServerAndKeystore.d, InstallerServerAndKeystore.n);
            byte[] decryptKey = clear.toByteArray();

            //decrypt message
            TEA tea = new TEA(decryptKey);
            byte[] decryptMessage = tea.decrypt(inputData);
            String decryptString = new String(decryptMessage);

            //If the decypted string is not all belongs to ASCII, close the sockets
            if (checkASCII(decryptString) == false) {
                System.out.println("Got visit: " + InstallerServerAndKeystore.count + " Illegal symmetric key used. This may be an attack.");
                clientSocket.close();

            } else {
                //turn String back to JSON, get element from it
                JSONObject jsonObject = new JSONObject(decryptString);
                JSONObject credentials = jsonObject.getJSONObject("Credentials");
                userID = credentials.getString("ID");
                String passwd = credentials.getString("passwd");
                int sensorID = jsonObject.getInt("Sensor ID");

                //check if username equals corresponding password
                if (InstallerServerAndKeystore.installerInfo.get(userID) == null) {
                    response = "Illegal ID or Password";
                    System.out.println("Visit: " + InstallerServerAndKeystore.count + " from " + userID + ", " + "\nIllegal ID or Password");
                    out.write(response.getBytes());
                    //check if the password enter, after hash with salt value, is equal to what stored in PasswordHash Class
                } else if (!(PasswordHash.hash(InstallerServerAndKeystore.installerInfo.get(userID)[0] + passwd)).equals(InstallerServerAndKeystore.installerInfo.get(userID)[1])) {
                    response = "Illegal ID or Password";
                    title = InstallerServerAndKeystore.installerInfo.get(userID)[2];
                    System.out.println("Visit: " + InstallerServerAndKeystore.count + " from " + userID + ", " + title + "\nIllegal ID or Password");
                    out.write(response.getBytes());
                } else if (!"Barack".equals(userID) && InstallerServerAndKeystore.sensorInfo.get(sensorID) != null) {
                    response = userID + "wants to move sensor " + sensorID + " but the system not authorized!";
                    System.out.println(userID + "wants to move sensor " + sensorID + " but the system not authorized!");
                    out.write(response.getBytes());
                } else {
                    //if the userID and password are all correct
                    response = "Thank you. The sensor’s location was securely transmitted to GunshotSensing  Inc. ";
                    title = InstallerServerAndKeystore.installerInfo.get(userID)[2];
                    out.write(response.getBytes());

                    //generate sensor location info into Map
                    String latitude = String.valueOf(jsonObject.getDouble("Latitude"));
                    String longitude = String.valueOf(jsonObject.getDouble("Longitude"));
                    String altitude = "0.00000";
                    String[] location = new String[]{latitude, longitude, altitude, userID};

                    //check if user already in database
                    if (InstallerServerAndKeystore.sensorInfo.get(sensorID) == null) {
                        System.out.println("Visit: " + InstallerServerAndKeystore.count + " -> "
                                + userID + " install sensor " + sensorID + " at "
                                + latitude + ", " + longitude + ", " + altitude);
                    } else {
                        System.out.println("Visit: " + InstallerServerAndKeystore.count
                                + userID + " move sensor " + sensorID + "to"
                                + latitude + ", " + longitude + ", " + altitude);
                    }

                    InstallerServerAndKeystore.sensorInfo.put(sensorID, location);

                    //generate KML file
                    String deskTopLocation = System.getProperty("user.home") + "/Desktop/Sensors.kml";
                    String KMLString = getKML(InstallerServerAndKeystore.sensorInfo);
                    FileWriter fw = new FileWriter(deskTopLocation);
                    try (BufferedWriter writeFileBuffer = new BufferedWriter(fw)) {
                        writeFileBuffer.write(KMLString);
                        writeFileBuffer.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (JSONException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/
            }
        }

    }

    //Check if all character in string belongs to ASCII, if yes, return true, otherwise, return false
    public boolean checkASCII(String decryptString) {
        CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
        return asciiEncoder.canEncode(decryptString);
    }

    //Cocatenent information in sensorInfo Map
    public static String getKML(Map<Integer, String[]> sensorInfo) {
        //set the beginning of KML document
        String begin = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n"
                + "<kml xmlns=\"http://earth.google.com/kml/2.2\"> \n"
                + "<Document>  \n"
                + " <Style id=\"style1\">  \n"
                + " <IconStyle>   \n"
                + "  <Icon>    \n"
                + "   <href>https://lh3.googleusercontent.com/MSOuW3ZjC7uflJAMst-cykSOEOwI_cVz96s2rtWTN4-Vu1NOBw80pTqrTe06R_AMfxS2=w170</href>\n"
                + "  </Icon>   \n"
                + " </IconStyle>    \n"
                + "</Style> \n";

        String middle = "";
        //Loop Map, get every sensor point in the dataset
        //Attached points information into the doc
        for (Map.Entry<Integer, String[]> entry : sensorInfo.entrySet()) {
            String element = "<Placemark> \n <name>Microphone " + entry.getKey() + "</name>\n"
                    + "<description>" + entry.getValue()[3] + "</description>\n"
                    + "<styleUrl>#style1</styleUrl> \n"
                    + "<Point>\n"
                    + " <coordinates>" + entry.getValue()[0] + "," + entry.getValue()[1] + ",0.000000</coordinates> \n"
                    + "</Point> \n"
                    + "</Placemark> \n";
            middle = middle + element;
        }

        //The end of the document
        String end = "</Document> \n</kml> ";

        //return appended String
        return begin + middle + end;
    }
}
