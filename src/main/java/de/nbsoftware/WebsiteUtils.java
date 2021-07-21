package de.nbsoftware;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.io.IOUtils;

/**
 * Utils for interacting with websites
 */
public class WebsiteUtils {
    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public static String scanWebsiteToText(String urlToRead) throws IOException {
        URL url = new URL(urlToRead);

        InputStream in = url.openStream();
        String body = IOUtils.toString(in, StandardCharsets.UTF_8);

        return body;
    }

    public static void downloadPdf(String urlToPdf, File pdf) throws IOException {
        URL url = new URL(urlToPdf.replaceAll(" ", "%20"));
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(pdf.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
