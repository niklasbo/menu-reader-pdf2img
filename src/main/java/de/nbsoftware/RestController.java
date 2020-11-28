package de.nbsoftware;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Base64;

import javax.imageio.ImageIO;

import java.awt.image.*;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RestController {
    private String authToken;
    private String menuBaseUrl;
    private String connectionString;
    private String databaseName;
    private String collectionName;
    private int imageDpi;
    private int pdfPageNumber;

    @Autowired
    private Environment env;

    private void loadEnvVars() {
        authToken = env.getProperty("AUTH_TOKEN");
        menuBaseUrl = env.getProperty("MENU_BASE_URL");
        connectionString = env.getProperty("MONGODB_CONNECTION_STRING");
        databaseName = env.getProperty("MONGODB_DATABASE");
        collectionName = env.getProperty("MONGODB_COLLECTION");
        imageDpi = env.getProperty("IMAGE_DPI", Integer.class);
        pdfPageNumber = env.getProperty("PDF_PAGE_NUMBER", Integer.class);
        if (authToken == null) {
            System.out.println("authToken environment variable is not given");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pdf2image")
    public ResponseEntity<String> loadPdfToImage(
            @RequestHeader(name = "X-AUTH-TOKEN", required = false, defaultValue = "") String givenToken) {
        if (authToken == null) {
            loadEnvVars();
        }
        if (!givenToken.equals(authToken)) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }

        int weeknum = getCurrentWeeknum();
        if (isWeeknumAlreadyInMongoDB(weeknum)) {
            return new ResponseEntity<String>(HttpStatus.ALREADY_REPORTED);
        }
        try {
            File pdfFile = File.createTempFile("temp", ".pdf");
            File jpegFile = File.createTempFile("temp", ".jpeg");
            pdfFile.deleteOnExit();
            jpegFile.deleteOnExit();

            downloadPdf(weeknum, pdfFile);
            pdfToJpeg(pdfFile, jpegFile);

            String base64Image = jpegToBase64String(jpegFile);

            writeToMongoDB(weeknum, base64Image);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    private int getCurrentWeeknum() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    private boolean isWeeknumAlreadyInMongoDB(int weeknum) {
        MongoClient client = MongoClients.create(connectionString);

        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("weeknum", weeknum);
        MongoCursor<Document> cursor = collection.find(searchQuery).cursor();

        while (cursor.hasNext()) {
            return true;
        }
        return false;
    }

    private void downloadPdf(int weeknum, File pdf) throws IOException {
        String fullUrl = menuBaseUrl + weeknum + ".pdf";
        URL url = new URL(fullUrl);

        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(pdf.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void pdfToJpeg(File pdf, File jpeg) throws IOException {
        PDDocument doc = PDDocument.load(pdf);
        PDFRenderer renderer = new PDFRenderer(doc);
        BufferedImage bim = renderer.renderImageWithDPI(pdfPageNumber, imageDpi, ImageType.GRAY);
        ImageIO.write(bim, "JPEG", jpeg);
        doc.close();
    }

    private String jpegToBase64String(File jpeg) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(jpeg);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private void writeToMongoDB(int weeknum, String imageAString) {
        MongoClient client = MongoClients.create(connectionString);

        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document doc = new Document("weeknum", weeknum).append("jpegImageAsBase64String", imageAString);
        collection.insertOne(doc);
    }
}
