package com.laurocesar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.batik.transcoder.TranscoderException;

/**
 * Command line interface that transforms a SVG file into a PDF one.
 * @author laurocesar
 *
 */
public class SVGFile2PDFFile {

	public static void main(String args[]) throws FileNotFoundException, TranscoderException, IOException{
		if (args.length!=2){
			System.out.println("SVG File to PDF File");
			System.out.println("-------------------");
			System.out.println("Usage: java com.laurocesar.SVGFile2PDFFile FROM_SVG_FILE TO_PDF_FILE");
			System.out.println("-------------------");
		} else {
			File from = new File(args[0]);
			File toPdfFile = new File(args[1]);
			
			if (!from.exists()){
				System.out.println("File "+from.getAbsolutePath()+" does not exist.");
			} else {
				System.out.println("Processing "+from.getAbsoluteFile());
				System.out.println(" producing "+toPdfFile.getAbsoluteFile());
				SVGToPDF.svgToPDF(from, toPdfFile);
				System.out.println("Done!");
			}
		}
		
	}
}
