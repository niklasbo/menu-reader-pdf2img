package de.nbsoftware;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final String CLOSING_BRACKET = "\">";
    private static final Pattern DATE_PATTERN = Pattern.compile("([0-3]?[0-9]\\.[0-1]?[0-9]\\.202[1-3])");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");  

    public static int getWeeknumOfUrl(PdfUrl pdfUrl) {
        try {
            return Integer.parseInt(pdfUrl.urlToPdf.substring(pdfUrl.urlToPdf.lastIndexOf('W') + 1, pdfUrl.urlToPdf.lastIndexOf('.')).trim());
        } catch(NumberFormatException e) {
            System.out.println(e.toString());
        }
        Matcher m = DATE_PATTERN.matcher(pdfUrl.visibleLinkText);
        List<String> results = new ArrayList<>();
        while (m.find()) {
            results.add(m.group());
        }
        for (String possibleDateString: results) {
            try {
                final Date date = DATE_FORMATTER.parse(possibleDateString);
                final Calendar c = Calendar.getInstance(Locale.GERMANY);
                c.setTime(date);
                return c.get(Calendar.WEEK_OF_YEAR);
            } catch (ParseException e) {
                System.out.println(e.toString());
            }
        }
        // guess this week
        return Calendar.getInstance(Locale.GERMANY).get(Calendar.WEEK_OF_YEAR);
    }

    public static List<PdfUrl> findPdfUrlsInHtml(String menuBaseUrl, String hrefIdentifyer, String htmlText) {
        List<PdfUrl> urlPdfs = new ArrayList<>();
        String[] foundUrls = StringUtils.substringsBetween(htmlText, "<a href=\"" + hrefIdentifyer, CLOSING_BRACKET);
        for (String a : foundUrls) {
            final String urlToPdf = menuBaseUrl + hrefIdentifyer + a;
            urlPdfs.add(new PdfUrl(urlToPdf, StringUtils.substringBetween(htmlText, urlToPdf + CLOSING_BRACKET, "<")));
        }
        return urlPdfs;
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
