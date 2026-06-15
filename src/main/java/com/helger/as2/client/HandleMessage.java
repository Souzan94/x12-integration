/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.as2.client;

import com.helger.as2.app.*;
import com.helger.as2.app.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.Charsets;
import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author souzan
 */
public class HandleMessage implements FileListener {

    @Override
   public void fileCreated(FileChangeEvent fe) throws Exception {
        if (fe.getFile().isHidden()) {
            return;
        }
        synchronized (fe) {
            String filename = "";
            String originalFileName = "";
            if (fe.getFile().getName().toString().contains("////")) {
                originalFileName = fe.getFile().getName().toString().replaceAll("file://", "");
            } else {
                originalFileName = fe.getFile().getName().toString().replaceAll("file:///", "");
            }
            filename = originalFileName;
            if (originalFileName.indexOf("#") > 0) {
                filename = originalFileName.replaceAll("#", "");
                new File(originalFileName).renameTo(new File(filename));
               // log.info("Rename file " + originalFileName + " to " + filename);
            }
            new File(filename).setLastModified(System.currentTimeMillis());
            processFile(fe);
        }
    }
    @Override
    public void fileDeleted(FileChangeEvent fce) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fileChanged(FileChangeEvent fce) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void processFile(FileChangeEvent fe) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ParseException {
        List<String> content = new ArrayList<String>();
        String filePath = "";
        WatchService watchService
                = FileSystems.getDefault().newWatchService();
        String url = AppProperties.getProperty("path");

        Path path = Paths.get(url);

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind().name().equalsIgnoreCase("ENTRY_CREATE")) {
                    System.out.println(
                            "Event kind:" + event.kind().name()
                            + ". File affected: " + event.context().toString() + ".");
                    //    content = Files.readAllLines(Paths.get(event.context().toString()));
                    filePath = url + "\\" + event.context().toString();
                    getFileContents(filePath, content);
                    String str = Arrays.toString(content.toArray());
                    String[] splitRow = str.split("(?<=~)");

                    List<String> res = Arrays.asList(splitRow);

                    String[] nm1 = res.get(5).split("\\*");
                    String[] ref = res.get(10).split("\\*");
                    String[] dtp = res.get(13).split("\\*");
                    String insuranceCompany = nm1[3];
                    String nationaId = StringUtils.chop(ref[2]);
                    String policy = "BUPA - SAUDI GROUND SERVICES COMPANY";
                    String date ="";
                    try{
                        date = StringUtils.chop(dtp[3]);
                    }catch(Exception ex){
                        
                    }
                    insertMessage(insuranceCompany, nationaId, policy, date);

                }
            }

            key.reset();
        }

    }

    public static void getFileContents(String path, List<String> contents) throws FileNotFoundException, IOException {
        contents.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            contents.add(line);

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
        try{
            sqlDate = new java.sql.Date(dateFormat.parse(insuranceDate).getTime());
        }catch(Exception ex){
            System.out.println(insuranceDate);
        }
        sts2.setString(4, insurance);
        sts2.setDate(5, sqlDate);
        sts2.setString(6, policy);

        sts2.executeUpdate();
    }
}//ENTRY_CREATE

