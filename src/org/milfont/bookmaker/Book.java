package org.milfont.bookmaker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

public class Book {

	public Document document = new com.itextpdf.text.Document(PageSize.A4);
	private String css;
	private String fileName;
	private PdfWriter pdfWriter;
	private XMLParser parser;
	
	public Book(String fileName, HashMap<String, String> config, Boolean macHack) throws IOException {
		this.fileName = fileName;
		try {
			pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(
					getFileName()));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.open();

		document.addAuthor( config.get("author") );
		document.addCreator( config.get("creator") );
		document.addSubject( config.get("subject") );
		document.addTitle( config.get("title") );
		document.addCreationDate();

		CssAppliers cssAppliers;
		if(macHack) {
			cssAppliers = new CssAppliersImpl(
					new XMLWorkerFontProvider() {
						public Font getFont(String fontname, String encoding,
								boolean embedded, float size, int style,
								BaseColor color) {

							String n = "Palatino.ttc";
							// if(fontname != "palatino") {
							// n = fontname;
							// }

							return super.getFont(n, BaseFont.MACROMAN,
									BaseFont.EMBEDDED, size, style, color);
						}
					});			
		} else {
			cssAppliers = new CssAppliersImpl(new XMLWorkerFontProvider());
		}

		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		CSSResolver cssResolver = XMLWorkerHelper.getInstance()
				.getDefaultCssResolver(true);
		Pipeline<?> pipeline = new CssResolverPipeline(cssResolver,
				new HtmlPipeline(htmlContext, new PdfWriterPipeline(document,
						pdfWriter)));
		XMLWorker worker = new XMLWorker(pipeline, true);
		parser = new XMLParser(worker);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public void addCover(String img) {
		try {
			byte[] imageBytes = getFileBytes( new FileInputStream(img) );
			Element cover = Image.getInstance(imageBytes, true);
			document.add(cover );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void add(String titulo, String html) {
		try {
			Chapter chapter = new Chapter(titulo, 0);
			document.add(chapter);
			if (html != null) {
				InputStream streamHTML = new ByteArrayInputStream(
						html.getBytes("UTF-8"));
				parser.parse(streamHTML);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    private static byte[] getFileBytes(InputStream file) throws IOException {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = file;
            int read = 0;
            while ((read = ios.read(buffer)) != -1)
                ous.write(buffer, 0, read);
        } finally {
            try {
                if (ous != null) ous.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        return ous.toByteArray();
    }

	public void close() {
		document.close();
	}

}