package de.nbsoftware;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestWebsiteUtils {

    @Test
    public void testWeeknumOfUrl() {
        try {
            String res = WebsiteUtils.scanWebsiteToText("https://www.sodexo-tk-online.de/speiseplan.html");
            assertTrue(res.length() > 1000);
        } catch(IOException e) {
            fail(e.getLocalizedMessage());
        }
    }
}
