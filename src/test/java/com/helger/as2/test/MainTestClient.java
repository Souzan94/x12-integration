//
//package com.helger.as2.test;
//
//import java.io.File;
//import java.util.Enumeration;
//
//import javax.annotation.Nonnull;
//import javax.mail.Header;
//import javax.mail.internet.MimeBodyPart;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.helger.as2lib.cert.CertificateFactory;
//import com.helger.as2lib.client.AS2Client;
//import com.helger.as2lib.client.AS2ClientRequest;
//import com.helger.as2lib.client.AS2ClientSettings;
//import com.helger.as2lib.crypto.ECryptoAlgorithmCrypt;
//import com.helger.as2lib.crypto.ECryptoAlgorithmSign;
//import com.helger.as2lib.disposition.DispositionOptions;
//import com.helger.as2lib.message.AS2Message;
//import com.helger.as2lib.message.IMessage;
//import com.helger.as2lib.message.IMessageMDN;
//import com.helger.as2lib.params.InvalidParameterException;
//import com.helger.as2lib.partner.CPartnershipIDs;
//import com.helger.as2lib.partner.Partnership;
//import com.helger.as2lib.partner.SelfFillingPartnershipFactory;
//import com.helger.as2lib.processor.sender.AS2SenderModule;
//import com.helger.as2lib.processor.sender.IProcessorSenderModule;
//import com.helger.as2lib.session.AS2Session;
//import com.helger.commons.collection.attr.StringMap;
//import com.helger.commons.io.resource.ClassPathResource;
//import com.helger.commons.system.SystemHelper;
//import com.helger.security.keystore.EKeyStoreType;
////import com.berryworks.edireader.EDIReader;
////import com.berryworks.edireader.benchmark.EDITestData;
//
////import com.berryworks.edireader.formatter.FormatterParser;
////import com.berryworks.edireader.json.fromedi.EdiToJson;
////import com.berryworks.edireader.tokenizer.EDITokenizer;
//import com.helger.as2.app.session.AS2ServerXMLSession;
//import com.helger.as2.cmd.XMLCommandRegistry;
//import com.helger.as2lib.client.AS2ClientResponse;
//import com.helger.as2lib.exception.OpenAS2Exception;
//import io.xlate.edi.schema.EDISchemaException;
//import io.xlate.edi.schema.Schema;
//
//import io.xlate.edi.stream.EDIInputFactory;
//import io.xlate.edi.stream.EDIStreamEvent;
//import io.xlate.edi.stream.EDIStreamException;
//import io.xlate.edi.stream.EDIStreamReader;
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.Writer;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.cert.X509Certificate;
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import javax.swing.text.Document;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLStreamReader;
////import jdk.internal.org.xml.sax.helpers.DefaultHandler;
////import jdk.nashorn.internal.parser.JSONParser;
//import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;
////import org.json.JSONArray;
////import org.json.JSONObject;
//import org.w3c.dom.NodeList;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.SAXNotRecognizedException;
//import org.xml.sax.SAXNotSupportedException;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.XMLFilterImpl;
//import org.xml.sax.helpers.XMLReaderFactory;
//
///**
// * <pre>
// * &lt;partnerships&gt;
// *  &lt;partner name="OpenAS2A" as2_id="OpenAS2A" x509_alias="OpenAS2A" email="OpenAS2 A email"/&gt;
// *  &lt;partner name="OpenAS2B" as2_id="OpenAS2B" x509_alias="OpenAS2B" email="OpenAS2 B email"/&gt;
// *  &lt;partnership name="OpenAS2A-OpenAS2B"&gt;
// *     &lt;sender name="OpenAS2A"/&gt;
// *     &lt;receiver name="OpenAS2B"/&gt;
// *     &lt;attribute name="protocol" value="as2"/&gt;
// *     &lt;attribute name="subject" value="From OpenAS2A to OpenAS2B"/&gt;
// *     &lt;attribute name="as2_url" value="http://localhost:10080"/&gt;
// *     &lt;attribute name="as2_mdn_to" value="http://localhost:10080"/&gt;
// *     &lt;attribute name="as2_mdn_options" value="signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1" /&gt;
// *     &lt;attribute name="encrypt" value="3des"/&gt;
// *     &lt;attribute name="sign" value="md5"/&gt;
// *   &lt;/partnership&gt;
// *   &lt;partnership name="OpenAS2B-OpenAS2A"&gt;
// *     &lt;sender name="OpenAS2B"/&gt;
// *     &lt;receiver name="OpenAS2A"/&gt;
// *     &lt;attribute name="protocol" value="as2"/&gt;
// *     &lt;attribute name="subject" value="From OpenAS2B to OpenAS2A"/&gt;
// *     &lt;attribute name="as2_url" value="http://localhost:10080"/&gt;
// *     &lt;attribute name="as2_mdn_to" value="http://localhost:10080"/&gt;
// *     &lt;attribute name="as2_mdn_options" value="signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1" /&gt;
// *     &lt;attribute name="encrypt" value="3des"/&gt;
// *     &lt;attribute name="sign" value="sha1"/&gt;
// *   &lt;/partnership&gt;
// * &lt;/partnerships&gt;
// * </pre>
// *
// 
// */////sender 
//public class MainTestClient {
//    // Message msg = new AS2Message();
//    // getSession().getProcessor().handle(SenderModule.DO_SEND, msg, null);
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(MainTestClient.class);
//
//    public static void main(final String[] args) throws FileNotFoundException, EDIStreamException, OpenAS2Exception, SAXException, IOException, XMLStreamException, ClassNotFoundException, SQLException, ParserConfigurationException {
//        final boolean DO_ENCRYPT = true;
//        final boolean DO_SIGN = true;
//        Connection SQlCnn = null;
//        String url = AppProperties.getProperty("URL");
//        SQlCnn = DriverManager.getConnection(url);
//        int patientID=0;
//        PreparedStatement sts1 = SQlCnn.prepareStatement(Utils.getInstance().results);
//        final AS2ClientSettings aSettings = new AS2ClientSettings();
//
//        aSettings.setKeyStore(EKeyStoreType.PKCS12, ClassPathResource.getAsFile("config/certs.p12"), "test");//password
//        aSettings.setSenderData("ROBIN", "email@example.org", "OpenAS2A_alias");
//
//        aSettings.setReceiverData("JHAH", "OpenAS2B_alias", "http://localhost:10080");
//        aSettings.setPartnershipName("ROBIN-JHAH");
//        //aSettings.setEncryptAndSign(DO_ENCRYPT ? ECryptoAlgorithmCrypt.CRYPT_3DES : null,
//       //  DO_SIGN ? ECryptoAlgorithmSign.DIGEST_SHA_1 : null);
//        final AS2ClientRequest aRequest = new AS2ClientRequest("Test message");
//        try{
//        sts1.executeQuery();
//        ResultSet requestList = sts1.getResultSet();
//        while (requestList.next()) {
//             patientID=requestList.getInt(1);
//        
//        String msg = inisializeMeassge(patientID);
//        
//        aRequest.setData(msg, Charset.defaultCharset());
//        //aRequest.setData(new File("X217-referral-request-for-review.edi"), SystemHelper.getSystemCharset());
//       new AS2Client().sendSynchronous(aSettings, aRequest);
//
////        File out = new File("output.txt");
////        FileWriter writer = new FileWriter(out);
////
////        writer.write(msg);
////        writer.flush();
////        writer.close();
////        String input = "output.txt";
////        String output = "test.xml";
////        String json_file = "edi_json";
////        String data;
////        EDItoXML edix = new EDItoXML(input, output);
////        if (edix.run() == true) {
////            new AS2Client().sendSynchronous(aSettings, aRequest);
////        } else {
////            System.out.println("//////X12 FILE NOT VALID");
////        }
//
//        //  Schema schema=reader.getControlSchema();
//        //  Reader reader1 = new FileReader("X217-admission-response-to-request-for-review.edi");
//        //      EdiToJson edi = new EdiToJson(); 
//        // String data2 =edi.asJson(reader1);
//        // System.out.println(edi.asJson(reader1));
//        //   EDIParser(data2);
//    }}
//    catch(Exception e){
//     e.printStackTrace();
//    }
//    
//  
//    }
//
//    public static void EDIxParser() throws ParserConfigurationException, SAXException, IOException {
//
//        File file = new File("test.xml");
//
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        org.w3c.dom.Document xmlDoc = builder.parse(file);
//        xmlDoc.getElementsByTagName("Row");
//        NodeList n1 = xmlDoc.getElementsByTagName("sender");
//        for (int i = 0; i < n1.getLength(); i++) {
//            String st1 = n1.item(i).getFirstChild().getNodeValue();
//
//            System.out.println(st1);
//
//        }
//    }
//
//    public static String inisializeMeassge(int patientId) throws SQLException, IOException, FileNotFoundException, EDIStreamException {
//        Calendar cal = Calendar.getInstance();
//        DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
//
//        cal.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
//
//        String date = sdf1.format(cal.getTime());
//        String time = sdf.format(cal.getTime());
//        Connection SQlCnn = null;
//        String in = null;        //trasmitterIdentification number
//        String url = AppProperties.getProperty("URL");
//        String provider = "";
//        String memberId = "";//member identification number
//        String firstName = "";
//        String lastName = "";
//        String birth = "";
//        String policy="";
//        String Payer="";
//        String insuranceDate="";
//        
//        int segCount = 0;
//        SQlCnn = DriverManager.getConnection(url);
//        PreparedStatement sts1 = SQlCnn.prepareStatement(Utils.getInstance().patientInfo);
//        PreparedStatement sts2=SQlCnn.prepareStatement(Utils.getInstance().insuranceInfo);
//        sts1.setInt(1, patientId);
//        sts1.executeQuery();
//
//        ResultSet requestList = sts1.getResultSet();
//        while (requestList.next()) {
//            firstName = requestList.getString(1);
//            lastName = requestList.getString(2);
//          birth = dateFormat.format(requestList.getDate(3));
//            memberId = requestList.getString(4);
//        }
//        sts2.setInt(1, patientId);
//        sts2.executeQuery();
//        ResultSet requestList2 = sts1.getResultSet();
//
//        while (requestList2.next()) {
//        policy= requestList2.getString(2);
//        Payer=requestList2.getString(3);
//        insuranceDate= dateFormat.format(requestList2.getDate(1));
//        
//        }
//
//        
////
////        String msg = "ISA*00*          *00*          *ZZ*123456789012345*ZZ*123456789012346*080503*1705*>*00501*000010216*0*T*:~\n" +
////"GS*HB*1234567890*1234567890*20080503*1705*20213*X*005010X279~\n" +
////"ST*271*4321*005010X279~\n" +
////"BHT*0022*11*10001234*" + date + "*" + time + "~\n" +
////"HL*1**20*1~\n" +
////"NM1*PR*2*" + Payer + "*****PI*842610001~\n" +
////"HL*2*1*21*1~\n" +
////"NM1*1P*2*JHAH*****SV*2000035~\n" +
////"HL*3*2*22*0~\n" +
////"NM1*IL*1*" + firstName + "*" + lastName + "****MI*" + memberId + "~\n" +
////"DMG*D8*" + birth + "*M~\n" +
////"DTP*" + policy + "*D8*" + insuranceDate + "~\n" +
////"SE*" + segCount + "*4321~\n" +
////"GE*1*20213~\n" +
////"IEA*1*000010216~";
////        File out = new File("temp.txt");
////        FileWriter writer = new FileWriter(out);
////
////        writer.write(msg);
////        writer.flush();
////        writer.close();
////        InputStream stream = new FileInputStream("temp.txt");
////
////        //InputSource inputSource = new InputSource(stream);
////        EDIInputFactory factory = EDIInputFactory.newFactory();
////
////        EDIStreamReader reader = factory.createEDIStreamReader(stream);
////        EDIStreamEvent event;
////        int i = 0;
////        while (reader.hasNext()) {
////            event = reader.next();
////
////            if (event == EDIStreamEvent.START_SEGMENT) {
////                System.out.println("Segment: " + reader.getText());
////                i++;
////                System.out.println(reader.next());
////
////            }
////        }
////        segCount = i - 4;
//      String  msg = "ISA*00*          *00*          *ZZ*123456789012345*ZZ*123456789012346*080503*1705*>*00501*000010216*0*T*:~\n" +
//"GS*HB*1234567890*1234567890*20080503*1705*20213*X*005010X279~\n" +
//"ST*271*4321*005010X279~\n" +
//"BHT*0022*11*10001234*" + date + "*" + time + "~\n" +
//"HL*1**20*1~\n" +
//"NM1*PR*2*ROBIN*****PI*842610001~\n" +
//"HL*2*1*21*1~\n" +
//"NM1*1P*2*JHAH*****SV*2000035~\n" +
//"HL*3*2*22*0~\n" +
//"NM1*IL*1*" + firstName + "*" + lastName + "****MI*" + memberId + "~\n" +
//"DMG*D8*" + birth + "*M~\n" +
//"DTP*346*D8*20060101~\n" +
//"SE*12*4321~\n" +
//"GE*1*20213~\n" +
//"IEA*1*000010216~";
//
//        return msg;
//    }
//
////  src/test/resources/dummy.txt
//    /**
//     * @param args Main args
//     * @throws Exception in case of error
//     */
//    public static void main2(final String[] args) throws Exception {
//        // Received-content-MIC
//        // original-message-id
//
//        final String pidSenderEmail = "http://localhost:10080";
//        final String pidAs2 = "OpenAS2A";
//        final String pidSenderAs2 = "OpenAS2B";
//        final String receiverKey = "OpenAS2A";// "gwtestfm2i_trusted"; //
//        final String senderKey = "OpenAS2B";
//        final String paAs2Url = "http://localhost:10080";
//        // "http://localhost:8080/as2/HttpReceiver";
//
//        final AS2SenderModule aTestSender = new AS2SenderModule();
//
//        final Partnership aPartnership = new Partnership("OpenAS2B-OpenAS2A");
//        aPartnership.setSenderAS2ID(pidSenderAs2);
//        aPartnership.setSenderX509Alias(senderKey);
//        aPartnership.setSenderEmail(pidSenderEmail);
//
//        aPartnership.setReceiverAS2ID(pidAs2);
//        aPartnership.setReceiverX509Alias(receiverKey);
//
//        aPartnership.setAttribute(CPartnershipIDs.PA_AS2_URL, paAs2Url);
//        if (false) {
//            aPartnership.setAttribute(CPartnershipIDs.PA_AS2_MDN_TO, "http://localhost:10080");//10080
//        }
//        aPartnership.setAttribute(CPartnershipIDs.PA_AS2_MDN_OPTIONS,
//                new DispositionOptions().setProtocolImportance(DispositionOptions.IMPORTANCE_OPTIONAL)
//                        .setProtocol(DispositionOptions.PROTOCOL_PKCS7_SIGNATURE)
//                        .setMICAlgImportance(DispositionOptions.IMPORTANCE_OPTIONAL)
//                        .setMICAlg(ECryptoAlgorithmSign.DIGEST_SHA_1)
//                        .getAsString());
//
//        aPartnership.setAttribute(CPartnershipIDs.PA_ENCRYPT, ECryptoAlgorithmCrypt.CRYPT_3DES.getID());
//        aPartnership.setAttribute(CPartnershipIDs.PA_SIGN, ECryptoAlgorithmSign.DIGEST_SHA_1.getID());
//        aPartnership.setAttribute(CPartnershipIDs.PA_PROTOCOL, AS2Message.PROTOCOL_AS2);
//
//        aPartnership.setAttribute(CPartnershipIDs.PA_AS2_RECEIPT_DELIVERY_OPTION, null);
//
//        LOGGER.info("ALIAS: " + aPartnership.getSenderX509Alias());
//
//        final IMessage aMsg = new AS2Message();
//        aMsg.setContentType("application/xml");
//        aMsg.setSubject("some subject");
//
//        aMsg.attrs().putIn(CPartnershipIDs.PA_AS2_URL, paAs2Url);
//
//        aMsg.attrs().putIn(CPartnershipIDs.PID_AS2, pidAs2);
//        aMsg.attrs().putIn(CPartnershipIDs.PID_EMAIL, "http://localhost:10080");
//
//        MimeBodyPart aBodyPart;
//        // part = new MimeBodyPart(new FileInputStream("/tmp/tst"));
//        aBodyPart = new MimeBodyPart();
//
//        aBodyPart.setText("some text from mme part");
//        // part.setFileName("/");
//        aMsg.setData(aBodyPart);
//
//        aMsg.setPartnership(aPartnership);
//        aMsg.setMessageID(aMsg.generateMessageID());
//        LOGGER.info("msg id: " + aMsg.getMessageID());
//
//        final AS2Session aSession = new AS2Session();
//        final CertificateFactory aCertFactory = new CertificateFactory();
//        final String filename = "C:\\Users\\souzan\\Documents\\NetBeansProjects\\as2-server-master\\target\\classes\\config\\certs.p12";
//        //  password="test" interval="300"
//
//        // String filename =
//        // "/Users/oleo/samples/parfum.spb.ru/as2/openas2/config/certs.p12";
//        //final String filename = "/Users/oleo/samples/parfum.spb.ru/as2/mendelson/certificates.p12";
//        // String filename =
//        // "/Users/oleo/samples/parfum.spb.ru/as2/test/test.p12";
//        final String password = "test";
//        // gwtestfm2i
//        // /Users/oleo/Downloads/portecle-1.5.zip
//
//        // /Users/oleo/samples/parfum.spb.ru/as2/test/test.p12
//        final StringMap aCertFactorySettings = new StringMap();
//        aCertFactorySettings.putIn(CertificateFactory.ATTR_TYPE, EKeyStoreType.PKCS12.getID());
//        aCertFactorySettings.putIn(CertificateFactory.ATTR_FILENAME, filename);
//        aCertFactorySettings.putIn(CertificateFactory.ATTR_PASSWORD, password);
//
//        aCertFactory.initDynamicComponent(aSession, aCertFactorySettings);
//
//        // logger.info(cf.getCertificate(msg.getMDN(), Partnership.PTYPE_SENDER));
//        // logger.info(cf.getCertificates());
//        aSession.setCertificateFactory(aCertFactory);
//
//        final SelfFillingPartnershipFactory aPartnershipFactory = new SelfFillingPartnershipFactory();
//        aSession.setPartnershipFactory(aPartnershipFactory);
//        aTestSender.initDynamicComponent(aSession, null);
//
//        LOGGER.info("is requesting  MDN?: " + aMsg.isRequestingMDN());
//        LOGGER.info("is async MDN?: " + aMsg.isRequestingAsynchMDN());
//        LOGGER.info("is rule to receive MDN active?: " + aMsg.partnership().getAS2ReceiptDeliveryOption());
//
//        aTestSender.handle(IProcessorSenderModule.DO_SEND, aMsg, null);
//        LOGGER.info("MDN is " + aMsg.getMDN().toString());
//
//        LOGGER.info("message sent" + aMsg.getLoggingText());
//
//        final IMessageMDN reply = aMsg.getMDN();
//
//        if (false) {
//            LOGGER.info("MDN headers:\n" + reply.headers().toString());
//        }
//
//        final Enumeration<Header> list2 = reply.getData().getAllHeaders();
//        final StringBuilder aSB2 = new StringBuilder("Mime headers:\n");
//        while (list2.hasMoreElements()) {
//
//            final Header aHeader = list2.nextElement();
//            aSB2.append(aHeader.getName()).append(" = ").append(aHeader.getValue()).append('\n');
//        }
//
//        // logger.info(sb2);
//        // logger.info(reply.getData().getRawInputStream().toString());
//    }
//
//    protected static void checkRequired(@Nonnull final IMessage aMsg) throws InvalidParameterException {
//        final Partnership aPartnership = aMsg.partnership();
//
//        try {
//            InvalidParameterException.checkValue(aMsg, "ContentType", aMsg.getContentType());
//            InvalidParameterException.checkValue(aMsg,
//                    "Attribute: " + CPartnershipIDs.PA_AS2_URL,
//                    aPartnership.getAS2URL());
//            InvalidParameterException.checkValue(aMsg,
//                    "Receiver: " + CPartnershipIDs.PID_AS2,
//                    aPartnership.getReceiverAS2ID());
//            InvalidParameterException.checkValue(aMsg, "Sender: " + CPartnershipIDs.PID_AS2, aPartnership.getSenderAS2ID());
//            InvalidParameterException.checkValue(aMsg, "Subject", aMsg.getSubject());
//            InvalidParameterException.checkValue(aMsg,
//                    "Sender: " + CPartnershipIDs.PID_EMAIL,
//                    aPartnership.getSenderEmail());
//            InvalidParameterException.checkValue(aMsg, "Message Data", aMsg.getData());
//        } catch (final InvalidParameterException ex) {
//            ex.setSourceMsg(aMsg);
//            throw ex;
//        }
//    }
//}
