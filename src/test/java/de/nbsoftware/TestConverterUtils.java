package de.nbsoftware;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class TestConverterUtils {

    @Test
    public void testWeeknumOfUrl() {
        assertEquals(13, ConverterUtils.getWeeknumOfUrl("someUrlKW   13.pdf"));
        assertEquals(13, ConverterUtils.getWeeknumOfUrl("someUrl KW13  .pdf"));
        assertEquals(48, ConverterUtils.getWeeknumOfUrl("someUrlKW   48   .pdf"));
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
        List<String> res = ConverterUtils.findPdfUrlsInHtml("https://www.sodexo-tk-online.de/", "assets/context/sodexo-tk-online/Speiseplan/", htmlText);
        assertEquals(2, res.size());
        res.forEach((s) -> {
            System.out.println(s);
            assertTrue(s.startsWith("https://"));
            assertTrue(s.endsWith(".pdf"));
        });
    }
}
