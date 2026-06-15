/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.as2.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author amassri
 */
public class X12Client {

    private static X12Client instance = new X12Client();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(X12Client.class);

    public static X12Client getInstance() {
        return instance;
    }

    public String SendMessage(String x12Message, int id,int insuranceID) throws SQLException {
   
        String result = "";
        Connection SQlCnn = null;
        String url = AppProperties.getProperty("URL");
        LOGGER.info("----------- Try to open a socket with the remote server -------------");

        String hostname = AppProperties.getProperty("HOST_NAME");
        String port = AppProperties.getProperty("PORT");
        
        
        try (Socket socket = new Socket(hostname, Integer.parseInt(port))) {
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(x12Message);
            
            LOGGER.info("----------- Message sent -------------\n{}" + x12Message);

//            DataOutputStream dataOutputStream = new DataOutputStream(output);
//            dataOutputStream.writeUTF(x12Message);
//            dataOutputStream.flush();
            //  System.out.println("MESSASGE SENT NOW ");
            // send the message
            
            SQlCnn = DriverManager.getConnection(url);
            PreparedStatement updatests = SQlCnn.prepareStatement(Utils.getInstance().MessageSent);
            PreparedStatement saveMSG = SQlCnn.prepareStatement(Utils.getInstance().saveMessage);

            updatests.setInt(1, insuranceID);
            updatests.executeUpdate();
            LOGGER.info("----------- update the patient -------------");

            saveMSG.setString(1, x12Message);
            Date utilDate = new Date();
            java.sql.Date msgDate = new java.sql.Date(utilDate.getTime());

            saveMSG.setDate(2, msgDate);
            saveMSG.setInt(3, 1);
            saveMSG.executeUpdate();
            
            LOGGER.info("----------- message saved to DB -------------");
            LOGGER.info("----------- try to close the socket -------------");

       //  InputStream input = socket.getInputStream();
//          System.out.println("Start Reading Response");
//          BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            String line;
//         while ((line = reader.readLine()) != null) {
//               System.out.println(line);
//             result +=line;
//             if(result.contains("IEA"))
//                            break;
//         }
//          System.out.println("End Reading Response");
          //  socket.close();
        } catch (UnknownHostException ex) {
            LOGGER.error("Server not found-------------" + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
            LOGGER.error("I/O error:------------------- " + ex.getMessage());

        }
        return result;
    }

    public String getMessage(InputStream input) {
        DataInputStream in = new DataInputStream(input);
        String message = "";
        try {
            char dataType = in.readChar();
            System.out.println("Reading Message Type = " + dataType);

            int length = in.readInt();
            System.out.println("Reading Message Length = " + length);
            // read the message from the socket
            System.out.println("Decode Message");
            StringBuilder dataString = new StringBuilder();
            //if(dataType == 's') {
            byte[] messageByte = new byte[length];
            boolean end = false;

            int totalBytesRead = 0;
            while (!end) {
                int currentBytesRead = in.read(messageByte);
                totalBytesRead = currentBytesRead + totalBytesRead;
                if (totalBytesRead <= length) {
                    dataString
                            .append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
                } else {
                    dataString
                            .append(new String(messageByte, 0, length - totalBytesRead + currentBytesRead,
                                    StandardCharsets.UTF_8));
                }
                if (dataString.length() >= length) {
                    end = true;
                }
            }
            message = dataString.toString();
            //}
        } catch (IOException ex) {
            Logger.getLogger(X12Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }
}
