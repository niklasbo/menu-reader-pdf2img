package de.nbsoftware;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RestController {
    private boolean initSuccessful = false;
    private String menuBaseUrl;
    private String menuOverviewUrlEnding;
    private String hrefIdentifier;
    private String connectionString;
    private String databaseName;
    private String collectionName;
    private int imageDpi;
    private int pdfPageNumber;

    @Autowired
    private Environment env;

    @RequestMapping(method = RequestMethod.GET, value = "/pdf2image")
    public ResponseEntity<String> loadPdfToImage() {
        if (!initSuccessful) {
            loadEnvVars();
        }
        try {
            String htmlText = WebsiteUtils.scanWebsiteToText(menuBaseUrl + menuOverviewUrlEnding);
            List<String> urlsToPdfs = ConverterUtils.findPdfUrlsInHtml(menuBaseUrl, hrefIdentifier, htmlText);
            boolean onlyKnownPdfs = true;
            for (String urlToPdf : urlsToPdfs) {
                int weeknum = ConverterUtils.getWeeknumOfUrl(urlToPdf);

                if (MongoUtils.isWeeknumAlreadyInMongoDB(connectionString, databaseName, collectionName, weeknum)) {
                    continue;
                }
                onlyKnownPdfs = false;

                File pdfFile = File.createTempFile("temp", ".pdf");
                File jpegFile = File.createTempFile("temp", ".jpeg");
                pdfFile.deleteOnExit();
                jpegFile.deleteOnExit();

                WebsiteUtils.downloadPdf(urlToPdf, pdfFile);
                ConverterUtils.convertPdfToJpeg(pdfFile, pdfPageNumber, imageDpi, jpegFile);

                String base64Image = ConverterUtils.jpegToBase64String(jpegFile);

                MongoUtils.writeToMongoDB(connectionString, databaseName, collectionName, weeknum, base64Image);
            }

            if (onlyKnownPdfs) {
                return new ResponseEntity<String>(HttpStatus.ALREADY_REPORTED);
            }
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void loadEnvVars() {
        menuBaseUrl = env.getProperty("MENU_BASE_URL");
        menuOverviewUrlEnding = env.getProperty("MENU_OVERVIEW_URL_ENDING");
        hrefIdentifier = env.getProperty("MENU_OVERVIEW_HREF_IDENTIFIER");
        connectionString = env.getProperty("MONGODB_CONNECTION_STRING");
        databaseName = env.getProperty("MONGODB_DATABASE");
        collectionName = env.getProperty("MONGODB_COLLECTION");
        imageDpi = env.getProperty("IMAGE_DPI", Integer.class);
        pdfPageNumber = env.getProperty("PDF_PAGE_NUMBER", Integer.class);
        initSuccessful = true;
    }
}
