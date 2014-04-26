package com.laurocesar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

public abstract class SVGToPDF {

	/**
	 * Convert a String SVG as File to a PDF file as File
	 * @param svgFromFile
	 * @param toPdfFile
	 * @throws FileNotFoundException
	 * @throws TranscoderException
	 */
	public static void svgToPDF(File svgFromFile, File toPdfFile) 
			throws FileNotFoundException, TranscoderException{
		
		InputStream isFrom = new FileInputStream(svgFromFile);
		OutputStream osTo = new FileOutputStream(toPdfFile);
		
		svgToPDF(isFrom, osTo);
	}
	
	
	/**
	 * Convert a String SVG as InputStream to a PDF file as Outputstream
	 * @param svgFromStream
	 * @param toPdf
	 * @throws FileNotFoundException
	 * @throws TranscoderException
	 */
	public static void svgToPDF(InputStream svgFromStream, OutputStream toPdf) 
			throws FileNotFoundException, TranscoderException{
		 new PDFTranscoder().transcode(new TranscoderInput(svgFromStream), new TranscoderOutput(toPdf));
	}
	
}
