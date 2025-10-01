package com.mixo.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;


@Service
public class HtmlToPdfService {

	public byte[] convertHtmlPageToPdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Step 1: Convert HTML to PDF using ITextRenderer
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent, null);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();

            // Step 2: Add Header and Footer using PdfReader & PdfStamper
            return addHeaderFooter(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }

    private byte[] addHeaderFooter(byte[] pdfBytes) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfReader reader = new PdfReader(pdfBytes);
            PdfStamper stamper = new PdfStamper(reader, outputStream);
            int totalPages = reader.getNumberOfPages();
            PdfContentByte canvas;

            for (int i = 1; i <= totalPages; i++) {
                canvas = stamper.getOverContent(i);

                // Add Header (Logo on Left & Right)
                Image leftLogo = Image.getInstance(new URL("https://customer.sbiunipay.sbi/CustomerPortal/resources/images/logo.png"));
                leftLogo.setAbsolutePosition(30, 800); // Adjust Y position for header
                leftLogo.scaleToFit(50, 50);
                canvas.addImage(leftLogo);

                Image rightLogo = Image.getInstance(new URL("https://customer.sbiunipay.sbi/CustomerPortal/resources/images/logo.png"));
                rightLogo.setAbsolutePosition(500, 800);
                rightLogo.scaleToFit(50, 50);
                canvas.addImage(rightLogo);

                // Add Footer (Page Number)
                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, 
                    new Phrase("Page " + i + " of " + totalPages, new Font(Font.HELVETICA, 10, Font.ITALIC)), 
                    300, 30, 0); // Adjust position for footer
            }

            stamper.close();
            reader.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error adding header/footer to PDF", e);
        }
    }

	public void convertHtmlPageToPdf(String htmlPageContent, OutputStream outputStream)
			throws IOException, DocumentException {
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlPageContent);
		renderer.layout();
		renderer.createPDF(outputStream);
	}

	public void convertHtmlPageToPdf(String htmlPageContent, OutputStream outputStream, String baseUrl)
			throws IOException, DocumentException {
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlPageContent, baseUrl);
		renderer.layout();
		renderer.createPDF(outputStream);
	}
	
	

}