package de.nbsoftware;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class TestConverterUtils {

    @Test
    public void testWeeknumOfUrl() {
        assertEquals(13, ConverterUtils.getWeeknumOfUrl(new PdfUrl("someUrlKW   13.pdf", "")));
        assertEquals(13, ConverterUtils.getWeeknumOfUrl(new PdfUrl("someUrl KW13  .pdf", "")));
        assertEquals(48, ConverterUtils.getWeeknumOfUrl(new PdfUrl("someUrlKW   48   .pdf", "")));
        assertEquals(11, ConverterUtils.getWeeknumOfUrl(new PdfUrl("someUrlKW   11  1 .pdf", "Speiseplan 15.03.2021 - 19.03.2021")));
        assertEquals(11, ConverterUtils.getWeeknumOfUrl(new PdfUrl("someUrl KW  2 11 .pdf", "Speiseplan 15.03.2021 - 19.03.2021")));
        assertEquals(11, ConverterUtils.getWeeknumOfUrl(new PdfUrl("someUrl KW  3 11  1 .pdf", "Speiseplan 15.03.2021 - 19.03.2021")));
    }

    @Test
    public void testFindPdfUrlsInHtml() {
        String htmlText = "";
        try {
            htmlText = WebsiteUtils.scanWebsiteToText("https://www.sodexo-tk-online.de/speiseplan.html");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Precondition not fulfilled.");
        }
        List<PdfUrl> res = ConverterUtils.findPdfUrlsInHtml("https://www.sodexo-tk-online.de/", "assets/context/sodexo-tk-online/Speiseplan/", htmlText);
        assertEquals(3, res.size());
        res.forEach((s) -> {
            System.out.println(s);
            assertTrue(s.urlToPdf.startsWith("https://"));
            assertTrue(s.urlToPdf.endsWith(".pdf"));
        });
    }
}
