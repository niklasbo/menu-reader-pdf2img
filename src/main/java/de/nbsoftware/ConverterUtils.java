package de.nbsoftware;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Utils for converting
 */
public class ConverterUtils {
    public static int getWeeknumOfUrl(String urlToPdf) {
        return Integer.parseInt(urlToPdf.substring(urlToPdf.lastIndexOf('W') + 1, urlToPdf.lastIndexOf('.')).trim());
    }

    public static List<String> findPdfUrlsInHtml(String menuBaseUrl, String hrefIdentifyer, String htmlText){
        List<String> urlsToPdfs = new ArrayList<>();
        String[] foundUrls = StringUtils.substringsBetween(htmlText, "<a href=\"" + hrefIdentifyer, "\">");
        for (String a : foundUrls) {
            urlsToPdfs.add(menuBaseUrl + hrefIdentifyer + a);
        }
        return urlsToPdfs;
    }

    public static void convertPdfToJpeg(File pdfFileToConvert, int pdfPageNumber, float imageDpi, File jpegFile)
            throws IOException {
        PDDocument doc = PDDocument.load(pdfFileToConvert);
        PDFRenderer renderer = new PDFRenderer(doc);
        BufferedImage bim = renderer.renderImageWithDPI(pdfPageNumber, imageDpi, ImageType.GRAY);
        ImageIO.write(bim, "JPEG", jpegFile);
        doc.close();
    }

    public static String jpegToBase64String(File jpeg) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(jpeg);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
