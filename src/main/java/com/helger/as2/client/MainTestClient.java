package com.helger.as2.client;

import java.io.File;
import java.util.Enumeration;

import javax.annotation.Nonnull;
import javax.mail.Header;
import javax.mail.internet.MimeBodyPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.as2lib.cert.CertificateFactory;
import com.helger.as2lib.client.AS2Client;
import com.helger.as2lib.client.AS2ClientRequest;
import com.helger.as2lib.client.AS2ClientSettings;
import com.helger.as2lib.crypto.ECryptoAlgorithmCrypt;
import com.helger.as2lib.crypto.ECryptoAlgorithmSign;
import com.helger.as2lib.disposition.DispositionOptions;
import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.message.AS2Message;
import com.helger.as2lib.message.IMessage;
import com.helger.as2lib.message.IMessageMDN;
import com.helger.as2lib.params.InvalidParameterException;
import com.helger.as2lib.partner.CPartnershipIDs;
import com.helger.as2lib.partner.Partnership;
import com.helger.as2lib.partner.SelfFillingPartnershipFactory;
import com.helger.as2lib.processor.sender.AS2SenderModule;
import com.helger.as2lib.processor.sender.IProcessorSenderModule;
import com.helger.as2lib.session.AS2Session;
import com.helger.commons.collection.attr.StringMap;
import com.helger.security.keystore.EKeyStoreType;
import io.xlate.edi.stream.EDIStreamException;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.codec.Charsets;
import org.quartz.SchedulerException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <pre>
 * &lt;partnerships&gt;
 *  &lt;partner name="OpenAS2A" as2_id="OpenAS2A" x509_alias="OpenAS2A" email="OpenAS2 A email"/&gt;
 *  &lt;partner name="OpenAS2B" as2_id="OpenAS2B" x509_alias="OpenAS2B" email="OpenAS2 B email"/&gt;
 *  &lt;partnership name="OpenAS2A-OpenAS2B"&gt;
 *     &lt;sender name="OpenAS2A"/&gt;
 *     &lt;receiver name="OpenAS2B"/&gt;
 *     &lt;attribute name="protocol" value="as2"/&gt;
 *     &lt;attribute name="subject" value="From OpenAS2A to OpenAS2B"/&gt;
 *     &lt;attribute name="as2_url" value="http://localhost:10080"/&gt;
 *     &lt;attribute name="as2_mdn_to" value="http://localhost:10080"/&gt;
 *     &lt;attribute name="as2_mdn_options" value="signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1" /&gt;
 *     &lt;attribute name="encrypt" value="3des"/&gt;
 *     &lt;attribute name="sign" value="md5"/&gt;
 *   &lt;/partnership&gt;
 *   &lt;partnership name="OpenAS2B-OpenAS2A"&gt;
 *     &lt;sender name="OpenAS2B"/&gt;
 *     &lt;receiver name="OpenAS2A"/&gt;
 *     &lt;attribute name="protocol" value="as2"/&gt;
 *     &lt;attribute name="subject" value="From OpenAS2B to OpenAS2A"/&gt;
 *     &lt;attribute name="as2_url" value="http://localhost:10080"/&gt;
 *     &lt;attribute name="as2_mdn_to" value="http://localhost:10080"/&gt;
 *     &lt;attribute name="as2_mdn_options" value="signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1" /&gt;
 *     &lt;attribute name="encrypt" value="3des"/&gt;
 *     &lt;attribute name="sign" value="sha1"/&gt;
 *   &lt;/partnership&gt;
 * &lt;/partnerships&gt;
 * </pre>
 *
 *
 */////sender 
public class MainTestClient {

    private static Socket socket;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainTestClient.class);
    private static final CountDownLatch latch = new CountDownLatch(1); // Initialize the latch with a count of 1
    private static final Object lock = new Object(); // Object for synchronization

    private final Hashtable<Long, PatientRequest> threadpin;
    private static int threadCount = 0; // Counter for active threads

    public MainTestClient() {

        threadpin = new Hashtable<Long, PatientRequest>();

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        boolean serverAvailable = true;
        String hostname = AppProperties.getProperty("HOST_NAME");
        String port = AppProperties.getProperty("PORT");
        MainTestClient client = new MainTestClient();

        try {
            socket = new Socket(hostname, Integer.parseInt(port));

        } catch (Exception ex) {
            LOGGER.error("Socket connection error: " + ex.getMessage());
            serverAvailable = false; // Set server availability flag to false

        }

        while (true) {
            synchronized (lock) {

                try {
                    if (!serverAvailable) {
                        LOGGER.info("Trying to open new connection ");

                        socket = new Socket(hostname, Integer.parseInt(port));
                        serverAvailable = true;

                    }
                    client.sendMessage();

                } catch (IOException e) {

                    // Connection error occurred, handle it
                    LOGGER.error("Connection error: " + e.getMessage());
                    serverAvailable = false;
                } // Other IO exceptions, handle as needed
                catch (Exception e) {
                    LOGGER.error("Exception : " + e.getMessage());

                }
                while (threadCount > 0) {
                    lock.wait();
                }

            }
        }
    }

    public void sendMessage() throws FileNotFoundException, EDIStreamException, OpenAS2Exception, SAXException, IOException, XMLStreamException, ClassNotFoundException, SQLException, ParserConfigurationException {

        Connection SQlCnn = null;
        String url = AppProperties.getProperty("URL");
        String reciever = AppProperties.getProperty("ip");

        SQlCnn = DriverManager.getConnection(url);

        int patientID = 0;
        int insuranceId = 0;
        PreparedStatement sts1 = SQlCnn.prepareStatement(Utils.getInstance().results);
        PreparedStatement sts2 = SQlCnn.prepareStatement(Utils.getInstance().updateRecord);

        ArrayList<PatientRequest> temp_requests = new ArrayList<PatientRequest>();

        try {
            sts1.executeQuery();
            ResultSet requestList = sts1.getResultSet();
            while (requestList.next()) {

                patientID = requestList.getInt(1);
                insuranceId = requestList.getInt(2);
//                sts2.setInt(1, insuranceId);
//                sts2.executeUpdate();
                PatientRequest request = new PatientRequest();
                request.setPatientID(patientID);
                request.setInsuranceID(insuranceId);
                temp_requests.add(request);

            }
        } catch (Exception e) {
            LOGGER.error("Eception Catched:-------------" + e.getMessage());
        }
        if (temp_requests.size() > 0) {

            for (int index = 0; index < temp_requests.size(); index++) {

                PatientRequest tempRequest = new PatientRequest();

                tempRequest.setInsuranceID(temp_requests.get(index).getInsuranceID());
                tempRequest.setPatientID(temp_requests.get(index).getPatientID());
                LOGGER.info("------insurance ID------ " + temp_requests.get(index).getInsuranceID() + "picked");
                ClaimJob claimJob = new ClaimJob("UnderProcess");
                threadpin.put(claimJob.getId(), tempRequest);
                synchronized (lock) {
                    threadCount++; // Increment the thread count
                }
                claimJob.start();

            }
        }

    }

    class ClaimJob extends Thread {

        private final Logger LOGGER = LoggerFactory.getLogger(MainTestClient.class);

        String claimType;

        int patientId;
        int inuranceId;

        public ClaimJob(String claimType) {
            this.claimType = claimType;

        }

        @Override
        public void run() {

            try {
                this.inuranceId = threadpin.get(this.getId()).getInsuranceID();
                this.patientId = threadpin.get(this.getId()).getPatientID();

                inisializeMeassge(this.patientId, this.inuranceId);
            } catch (Exception ex) {
                LOGGER.error("Ërror Catched:---------------------" + ex.getMessage());
            } finally {
                synchronized (lock) {
                    threadCount--; // Decrement the thread count
                    lock.notifyAll(); // Notify the main thread that a thread has finished processing
                }
            }

        }
    }

    public static void EDIxParser() throws ParserConfigurationException, SAXException, IOException {

        File file = new File("test.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document xmlDoc = builder.parse(file);
        xmlDoc.getElementsByTagName("Row");
        NodeList n1 = xmlDoc.getElementsByTagName("sender");
        for (int i = 0; i < n1.getLength(); i++) {
            String st1 = n1.item(i).getFirstChild().getNodeValue();

            System.out.println(st1);

        }
    }

    public static void inisializeMeassge(int patientId, int insuranceId) throws SQLException, IOException, FileNotFoundException, EDIStreamException {
        LOGGER.info("-----------inisialize Message for -------------" + insuranceId);

//        Locale.setDefault(new Locale("ar", "SA"));
//        Calendar cal = Calendar.getInstance();
//        //DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//
//        cal.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");

        String date = "";
        String time ="";
        Connection SQlCnn = null;
        String url = AppProperties.getProperty("URL");
        String firstName = "";
        String lastName = "";
        String birth = "";
        String policy = "";
        String mrn = "";
        String Payer = "";
        String payercode = "";
        String insuranceDate = "";
        String expiryDate = "";
        String nationalId = "";
        String gender = "";
        String provider = "";
        int segCount = 0;
        try {

            SQlCnn = DriverManager.getConnection(url);

            PreparedStatement sts1 = SQlCnn.prepareStatement(Utils.getInstance().patientInfo);
            PreparedStatement sts2 = SQlCnn.prepareStatement(Utils.getInstance().insuranceInfo);
            PreparedStatement sts3 = SQlCnn.prepareStatement(Utils.getInstance().mrnInfo);
            PreparedStatement sts4=SQlCnn.prepareStatement(Utils.getInstance().currentDateTime);
         
            sts1.setInt(1, patientId);
            sts1.executeQuery();

            ResultSet requestList = sts1.getResultSet();
            while (requestList.next()) {

                //   Charset charset = Charset.forName("UTF-8");
                // byte[] ptext = requestList.getString(1).getBytes(StandardCharsets.ISO_8859_1); 
                firstName = new String(requestList.getString(1).getBytes(), "UTF-8");

                lastName = requestList.getString(2);
                birth = requestList.getString(3);
                nationalId = requestList.getString(4);
                if (requestList.getInt(5) == 1) {
                    gender = "M";
                } else {
                    gender = "F";
                }
            }

            sts2.setInt(1, insuranceId);
            sts2.executeQuery();
            ResultSet requestList2 = sts2.getResultSet();

            while (requestList2.next()) {
                policy = requestList2.getString(3);
                Payer = requestList2.getString(4);
                 insuranceDate = requestList2.getString(1);
                expiryDate = requestList2.getString(2);
                payercode = requestList2.getString(5);
//                if (date1 != null) {
//                    insuranceDate =date1.toString();
//                } else {
//                    insuranceDate = dateFormat.format(new java.util.Date());
//                }
//                if (date2 != null) {
//                    expiryDate = date2.toString();
//                } else {
//                    expiryDate = dateFormat.format(new java.util.Date());
//
//                }

            }
            sts3.setInt(1, patientId);
            sts3.executeQuery();
            ResultSet requestList3 = sts3.getResultSet();
            while (requestList3.next()) {
                mrn = requestList3.getString(1);

            }
            sts4.executeQuery();
           ResultSet requestList4 = sts4.getResultSet();
           while (requestList4.next()) {
                date= requestList4.getString(1);
                time = requestList4.getString(2);

            }


        } catch (NullPointerException e) {
            LOGGER.error("Eception Catched:-------------" + e.getMessage());

        }
        String msg = "ISA*00*          *00*          *ZZ*123456789012345*ZZ*123456789012346*080503*1705*>*00501*000010216*0*T*:~\n"
                + "GS*HB*1234567890*1234567890*20080503*1705*20213*X*005010X279~\n"
                + "ST*271*4321*005010X279~\n"
                + "BHT*0022*11*10001234*" + date + "*" + time + "~\n"
                + "HL*1**20*1~\n"
                + "NM1*PR*2*" + Payer + "*****PI*" + payercode + "~\n"
                + "HL*2*1*21*1~\n"
                + "NM1*1P*2*JHAH*****SV*2000035~\n"
                + "HL*3*2*22*0~\n"
                + "NM1*IL*1*" + firstName + "*" + lastName + "****MI*" + mrn + "~\n"
                + "REF*SY*" + nationalId + "~\n"
                + "DMG*D8*" + birth + "*" + gender + "~\n"
                + "DTP*346*D8*" + insuranceDate + "~\n"
                + "DTP*347*D8*" + expiryDate + "~\n"
                + "EB*1~\n"
                + "SE*13*4321~\n"
                + "GE*1*20213~\n"
                + "IEA*1*000010216~";

        SendMessage(msg, patientId, insuranceId);

    }

    public static void SendMessage(String x12Message, int id, int insuranceID) throws SQLException {

        Connection SQlCnn = null;
        String url = AppProperties.getProperty("URL");

        try {
            // Perform a heartbeat check to ensure the connection is still alive

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
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date msgDate = new java.sql.Date(utilDate.getTime());

            saveMSG.setDate(2, msgDate);
            saveMSG.setInt(3, 1);
            saveMSG.executeUpdate();

            LOGGER.info("----------- message saved to DB -------------");
            LOGGER.info("----------- try to close the socket -------------");

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
            LOGGER.error("I/O error:------------------- " + ex.getMessage());

        }
    }

    private static boolean isServerAvailable(Socket socket) {
        LOGGER.info("------------------------" + socket.isConnected());
        LOGGER.info("------------------------" + socket.isClosed());

        try {
            socket.sendUrgentData(0xFF);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected static void checkRequired(@Nonnull final IMessage aMsg) throws InvalidParameterException {
        final Partnership aPartnership = aMsg.partnership();

        try {
            InvalidParameterException.checkValue(aMsg, "ContentType", aMsg.getContentType());
            InvalidParameterException.checkValue(aMsg,
                    "Attribute: " + CPartnershipIDs.PA_AS2_URL,
                    aPartnership.getAS2URL());
            InvalidParameterException.checkValue(aMsg,
                    "Receiver: " + CPartnershipIDs.PID_AS2,
                    aPartnership.getReceiverAS2ID());
            InvalidParameterException.checkValue(aMsg, "Sender: " + CPartnershipIDs.PID_AS2, aPartnership.getSenderAS2ID());
            InvalidParameterException.checkValue(aMsg, "Subject", aMsg.getSubject());
            InvalidParameterException.checkValue(aMsg,
                    "Sender: " + CPartnershipIDs.PID_EMAIL,
                    aPartnership.getSenderEmail());
            InvalidParameterException.checkValue(aMsg, "Message Data", aMsg.getData());
        } catch (final InvalidParameterException ex) {
            ex.setSourceMsg(aMsg);
            throw ex;
        }
    }
}
