/*
 *  Copyright 2004 by BerryWorks
 *  All rights reserved.
 *
 */
package com.helger.as2.test;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import com.berryworks.edireader.EDIReader;

import java.io.*;

/**
 *  Converts EDI input to XML output using the
 *  default XSLT transformer.
 *
 *  <br><br>
 *  Assuming your CLASSPATH contains EDIReader.jar, you may run
 *  this program with the command line
 *  <br><br><code>
 *    java com.berryworks.edireader.demo.EDItoXML [input-file] [-o output-file]
 * </code><br><br>
 * If an input-file is not specified, System.in is used;
 * if an output-file is not specified, System.out is used.
 *
 */
public class EDItoXML {
	InputSource inputSource;
	OutputStream generatedOutput;
	ContentHandler handler;
	//EDIReader parser;
	String inputFileName = null;
	String outputFileName = null;


	/**
	 *Constructor for the EDItoXML object
	 *
	 * @param  input   file containing EDI-structured data
	 * @param  output  XML file
	 */
	public EDItoXML(String input, String output) {
		inputFileName = input;
		outputFileName = output;

		// Establish output file
		if (outputFileName != null) {
			try {
				generatedOutput = new BufferedOutputStream(
						new FileOutputStream(outputFileName));
				System.out.println("Output file " + outputFileName + " opened");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			generatedOutput = System.out;
		}

		// Establish inputSource, a SAX InputSource
		if (inputFileName != null) {
			try {
				inputSource = new InputSource(
						new FileReader(inputFileName));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			inputSource = new InputSource(
					new InputStreamReader(System.in));
		}
	}


	/**
	 *  Main processing method for the EDItoXML object
	 */
	public boolean run() {
            boolean check=true;
		try {
			// Establish an XMLReader which is actually an EDIReader.
			System.setProperty("javax.xml.parsers.SAXParserFactory","com.berryworks.edireader.EDIParserFactory");
			SAXParserFactory sFactory = SAXParserFactory.newInstance();
			SAXParser sParser = sFactory.newSAXParser();
			XMLReader ediReader = sParser.getXMLReader();

			// Establish the SAXSource
			SAXSource source = new SAXSource(ediReader, inputSource);

			// Establish an XSL Transformer to generate the XML output.
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			
			// The StreamResult to capture the generated XML output.
			StreamResult result = new StreamResult(generatedOutput);
			
			// Call the XSL Transformer with no stylesheet to generate
			// XML output from the parsed input.
			transformer.transform(source, result);
			System.out.print("\nTransformation complete\n");
		} catch (SAXException e) {
			System.err.println("\nUnable to create EDIReader: " + e);
                        check =false;
		} catch (ParserConfigurationException e) {
			System.err.println("\nUnable to create EDIReader: " + e);
                        check =false;
		} catch (TransformerConfigurationException e) {
			System.err.println("\nUnable to create Transformer: " + e);
                          check = false;
		} catch (TransformerException e) {
			System.err.println("\nFailure to transform: " + e);
			e.printStackTrace(System.err);
                      check =false;
		
               }
                return check;
	}
        
        


	/**
	 *  Main for EDItoXML.
	 *
	 * @param  args  command line arguments
	 */
	public static void main(String args[]) {
            System.out.print("علي");
		String inputFileName = "output.txt";
		String outputFileName = "test.xml";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-o")) {
				// -o filename
				if ((++i) >= args.length) {
					badArgs();
				}
				outputFileName = args[i];
			} else {
				inputFileName = args[i];
			}
		}

		EDItoXML theObject = new EDItoXML(inputFileName, outputFileName);
		theObject.run();
	}


	/**
	 *  Print summary of command line arguments expected.
	 */
	private static void badArgs() {
		System.err.println(
				"Usage: EDItoXML [-o outputfile] [inputfile]");
		System.exit(1);
	}

}

