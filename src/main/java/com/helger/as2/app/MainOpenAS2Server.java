package com.helger.as2.app;

import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.as2.app.session.AS2ServerXMLSession;
import com.helger.as2.client.AppProperties;
import com.helger.as2.client.Utils;
import com.helger.as2.cmd.CommandManager;
import com.helger.as2.cmd.ICommandRegistry;
import com.helger.as2.cmdprocessor.AbstractCommandProcessor;
import com.helger.as2lib.CAS2Info;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
;
import java.util.Date;
import java.util.logging.Level;
import org.codehaus.plexus.util.StringUtils;

/**
 * original author unknown in this release added ability to have multiple
 * command processors
 *
 *
 */
//reciever 


public class MainOpenAS2Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainOpenAS2Server.class);
    private static final char END_OF_BLOCK = '\u003c';
    private static final char START_OF_BLOCK = '\u000b';

    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 10080;

    public static void main(final String[] args) {
        //   String path = "E:\\Settings\\x12_jhah\\server\\config.xml";

        new MainOpenAS2Server().start();
        //ArrayHelper.getFirst (args)
    }

    public void start() {
        try {
            //create the socket server object
            server = new ServerSocket(Integer.parseInt(AppProperties.getProperty("PORT")));
            //keep listens indefinitely until receives 'exit' call or program terminates
            while (true) {
                System.out.println("Waiting for the client request");
                //creating socket and waiting for client connection
                Socket socket = server.accept();
                System.out.println("from Address" + socket.getRemoteSocketAddress());
                //read from socket to ObjectInputStream object
                try {
                    System.out.println("Reading Message");
                    String result = "";
                    //String message = getMessage(socket.getInputStream());
                    //DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                     StringBuilder resultBuilder = new StringBuilder();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                          resultBuilder.append(line).append("\n");}

                            result = resultBuilder.toString();
 
                     
                    String message = result;
                    System.out.println("Message === Received: " + message);
                    // handleMessage(message);
                    //create ObjectOutputStream object
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    //write object to Socket
                    String InterchangeSenderID = "123456789012346";
                    String InterchangeReceiverID = "123456789012345";
                    Date date = new Date();
                    SimpleDateFormat dateFormmater = new SimpleDateFormat("yyMMdd");
                    SimpleDateFormat timeFormmater = new SimpleDateFormat("HHmm");
                    String response = "ISA*00*          *00*          *ZZ*" + InterchangeSenderID + "*ZZ*" + InterchangeReceiverID
                            + "*" + dateFormmater.format(date) + "*" + timeFormmater.format(date) + "*>*00501*000000026*0*T*:~\n"
                            + "IEA*0*000000026~";
                    System.out.println("Message Received: " + response);
                    PrintWriter writer = new PrintWriter(oos, true);
                    writer.println(response);

//                    DataOutputStream dataOutputStream = new DataOutputStream(oos);
//                    dataOutputStream.writeUTF(response);
//                    dataOutputStream.flush(); // send the message
                    //terminate the server if client sends exit request
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    java.util.logging.Logger.getLogger(MainOpenAS2Server.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
            }
            System.out.println("Shutting down Socket server!!");
            //close the ServerSocket object
            server.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainOpenAS2Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getMessage(InputStream anInputStream) throws IOException {

        boolean end_of_message = false;
        StringBuffer parsedMessage = new StringBuffer();

        int characterReceived = 0;

        try {
            characterReceived = anInputStream.read();
        } catch (SocketException e) {
            System.out
                    .println("Unable to read from socket stream. "
                            + "Connection may have been closed: " + e.getMessage());
            return null;
        }

        while (!end_of_message) {
            characterReceived = anInputStream.read();

//            if (characterReceived == END_OF_TRANSMISSION) {
//                throw new RuntimeException(
//                        "Message terminated without end of message character");
//            }
            parsedMessage.append((char) characterReceived);
        }

        return parsedMessage.toString();
    }

    private void handleMessage(String message) {
        try {
            String[] splitRow = message.split("(?<=~)");

            List<String> res = Arrays.asList(splitRow);

            String[] nm1 = res.get(5).split("\\*");
            String[] ref = res.get(10).split("\\*");
            String[] dtp = res.get(13).split("\\*");
            String insuranceCompany = nm1[3];
            String nationaId = StringUtils.chop(ref[2]);
            String policy = "BUPA - SAUDI GROUND SERVICES COMPANY";
            String date = "";
            try {
                date = StringUtils.chop(dtp[3]);
            } catch (Exception ex) {

            }
            insertMessage(insuranceCompany, nationaId, policy, date);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MainOpenAS2Server.class.getName()).log(Level.SEVERE, null, ex);

        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(MainOpenAS2Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insertMessage(String insurance, String nationalId, String policy, String insuranceDate) throws SQLException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");

        Connection SQlCnn = null;
        String url = AppProperties.getProperty("URL");
        SQlCnn = DriverManager.getConnection(url);
        PreparedStatement sts1 = SQlCnn.prepareStatement(Utils.getInstance().patientinsured);
        String national_id;
        sts1.setString(1, nationalId);
        sts1.executeQuery();
        String memberId = "";
        int patientId = 0;
        ResultSet requestList = sts1.getResultSet();
        while (requestList.next()) {
            memberId = requestList.getString(1);
            patientId = requestList.getInt(2);

        }
        PreparedStatement sts2 = SQlCnn.prepareStatement(Utils.getInstance().addNewInsurance);
        sts2.setInt(1, patientId);
        sts2.setString(2, memberId);
        sts2.setString(3, "Principal");
        java.sql.Date sqlDate = null;
        try {
            sqlDate = new java.sql.Date(dateFormat.parse(insuranceDate).getTime());
        } catch (Exception ex) {
            System.out.println(insuranceDate);
        }
        sts2.setString(4, insurance);
        sts2.setDate(5, sqlDate);
        sts2.setString(6, policy);

        sts2.executeUpdate();
    }

    public void start1(@Nullable final String sConfigFilePath) {
        AS2ServerXMLSession aXMLSession = null;
        try {
            LOGGER.info(CAS2Info.NAME_VERSION + " - starting Server...");

            // create the OpenAS2 Session object
            // this is used by all other objects to access global configs and 
            // functionality
            LOGGER.info("Loading configuration...");
            if (StringHelper.hasText(sConfigFilePath)) {
                // Load config file
                aXMLSession = new AS2ServerXMLSession(sConfigFilePath);
            } else {
                LOGGER.info("Usage:");
                LOGGER.info("java " + getClass().getName() + " <configuration file>");
                throw new Exception("Missing configuration file name on the commandline. You may specify src/main/resources/config/config.xml");
            }

            // start the active processor modules
            LOGGER.info("Starting Active Modules...");
            aXMLSession.getMessageProcessor().startActiveModules();

            final ICommandRegistry aCommandRegistry = aXMLSession.getCommandRegistry();
            final CommandManager aCommandMgr = aXMLSession.getCommandManager();
            final List<AbstractCommandProcessor> aCommandProcessors = aCommandMgr.getProcessors();
            for (final AbstractCommandProcessor cmd : aCommandProcessors) {
                LOGGER.info("Loading Command Processor " + cmd.getClass().getName() + "");
                cmd.init();

                cmd.addCommands(aCommandRegistry);
                new Thread(cmd, ClassHelper.getClassLocalName(cmd)).start();
            }

            // enter the command processing loop
            LOGGER.info("OpenAS2 Started");
            // Start waiting for termination

            breakOut:
            while (true) {
                for (final AbstractCommandProcessor cmd : aCommandProcessors) {
                    if (cmd.isTerminated()) {
                        break breakOut;
                    }

                }
                // Wait outside loop in case no command processor is present
                Thread.sleep(100);

            }

            LOGGER.info("- OpenAS2 Stopped -");
        } catch (final Throwable t) {
            t.printStackTrace();
        } finally {
            if (aXMLSession != null) {
                try {
                    aXMLSession.getMessageProcessor().stopActiveModules();
                } catch (final OpenAS2Exception same) {
                    same.terminate();
                }

            }

            LOGGER.info("OpenAS2 has    down");
        }
    }

}
