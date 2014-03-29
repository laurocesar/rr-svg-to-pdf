package com.laurocesar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.svg.PDFTranscoder;

/**
 * Extracts SVG information from Railroad Diagram Generator (http://www.bottlecaps.de/rr/ui) 
 * and convert them to PDF
 * @author laurocesar@laurocesar.com
 *
 */
public class RRSVG2PDF {

	private final static Pattern TITLE_SVG_PATTERN = Pattern.compile(
			"<xhtml:a name=\"([\\w\\W]*?)\">[\\w\\W]*?:</xhtml:a></xhtml:p>(<svg[\\w\\W]*?</svg>)"
			);
	
	private static final String FILENAME_PREFIX = "ebnf_";
	private static final String PDF_SUFIX = "pdf";

	public static void main(String args[]) throws FileNotFoundException, TranscoderException, IOException{
		if (args.length!=1){
			System.out.println("Railroad SVG to PDF");
			System.out.println("-------------------");
			System.out.println("1) Create an EBNF specification in http://www.bottlecaps.de/rr/ui");
			System.out.println("2) Save the result as SVG file (.xhtml file)");
			System.out.println("3) Run: ");
			System.out.println("   RRSVG2PDF filename.xhtml");
			System.out.println("4) Done! ");
			System.out.println("   This will produce as many .PDF files as SVG entries of the .xhtml file");
			System.out.println("   A .tex file will also be create (to be used in your LaTeX document)");
			System.out.println("-------------------");
		} else {
			File f = new File(args[0]);
			
			if (!f.exists()){
				System.out.println("File "+f.getAbsolutePath()+" does not exist.");
			} else {
				RRSVG2PDF esvg = new RRSVG2PDF();
				esvg.extractToCurrentDir(f);
			}
		}
		
	}
	
	/**
	 * Extract files to current directory
	 * @param xhtmlFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TranscoderException
	 */
	public void extractToCurrentDir(File xhtmlFile) throws FileNotFoundException, IOException, TranscoderException{

		String path = xhtmlFile.getParent();
		
		System.out.println("Deleting PDF files in the directory");
		cleanPdfFiles(new File(path));
		
		System.out.println("Looking for SVG and titles in XHTML file");
		Map<String, String> mapSVG = extractTitleAndSVG(FileUtils.readFileToString(xhtmlFile));
		System.out.println("Found: "+mapSVG.size());

		StringBuilder sbTeXFile = new StringBuilder();
		
		int n = 1;
		for (String s : mapSVG.keySet()){
			System.out.println("Processing "+n + " ("+s+")");
			
			//SVG to PDF
			svgToPDF(IOUtils.toInputStream(mapSVG.get(s)), new FileOutputStream(new File(path+"/"+FILENAME_PREFIX+s+"."+PDF_SUFIX)));
			
			//put the title in a String that will become a .tex file
			//this file contains the SVG titles in the same order they appear in the xhtml file
			sbTeXFile.append("\\includegraphicebnftext{"+s+"} \n");
			
			n++;
		}
		
		File texFile = new File(path+"/svg2pdf.tex");
		System.out.println("Saving TeX file: "+texFile.getAbsolutePath());
		FileUtils.writeStringToFile(texFile, sbTeXFile.toString());
		
		System.out.println("Done!");
	}
	
	/**
	 * Delete all files from directory that have been generated by this
	 * @param directory
	 */
	private void cleanPdfFiles(File directory){
		for (String s : directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				String arg1Lower = arg1.toLowerCase();
				return arg1Lower.startsWith(FILENAME_PREFIX) && arg1Lower.endsWith(PDF_SUFIX);
			}
		})){
			new File(directory+"/"+s).delete();
		}
	}
	
	/**
	 * Extract SVG and titles of the images 
	 * @param content
	 * @return
	 */
	private Map<String,String> extractTitleAndSVG(String content){

		Map<String, String> ret = new LinkedHashMap<String, String>();
		
		Matcher m = TITLE_SVG_PATTERN.matcher(content);
		while (m.find()){
			String title = m.group(1);
			String svg = m.group(2);
			
			ret.put(title, svg);
		}
		
		return ret;
	}

	/**
	 * Convert an String SVG InputStream to a PDF file
	 * @param svgFromStream
	 * @param toPdf
	 * @throws FileNotFoundException
	 * @throws TranscoderException
	 */
	private void svgToPDF(InputStream svgFromStream, OutputStream toPdf) 
			throws FileNotFoundException, TranscoderException{
		 new PDFTranscoder().transcode(new TranscoderInput(svgFromStream), new TranscoderOutput(toPdf));
	}
	
}
