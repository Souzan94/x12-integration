/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.as2.client;
import com.helger.as2.app.*;
import com.helger.as2.app.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author SHussien
 */
public class AppProperties {
    private static AppProperties instance;
     private Properties prop ;

private static AppProperties getInstance(){
if(instance == null){
instance = new AppProperties();
}
return instance;
}

private AppProperties(){

try (FileReader reader=new FileReader("app.properties") )  {

prop = new Properties();

if (reader == null) {
System.out.println("Sorry, unable to find config.properties");
return;
}

//load a properties file from class path, inside static method
prop.load(reader);


} catch (IOException ex) {
ex.printStackTrace();
}
}

public static String getProperty(String name){
return getInstance().prop.getProperty(name);
}

}


