package de.nbsoftware;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Utils for interacting with websites
 */
public class WebsiteUtils {

    public static String scanWebsiteToText(String urlToRead) throws IOException {
        String htmlText;
        URL url = new URL(urlToRead);
        try (Scanner sc = new Scanner(url.openStream(), StandardCharsets.UTF_8.toString())) {
            sc.useDelimiter("\\A");
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext()) {
                String s = sc.next();
                sb.append(s);
            }
            htmlText = sb.toString();
        }
        return htmlText;
    }

    public static void downloadPdf(String urlToPdf, File pdf) throws IOException {
        URL url = new URL(urlToPdf);
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(pdf.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
