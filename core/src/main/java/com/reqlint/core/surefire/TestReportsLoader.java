package com.reqlint.core.surefire;

import com.reqlint.core.surefire.warnings.TestReportWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestReportsLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReportsLoader.class);

    private final List<SurefireTestReport> reports = new ArrayList<>();
    private final List<TestReportWarning> warnings = new ArrayList<>();

    private final DocumentBuilder documentBuilder;

    public List<SurefireTestReport> reports() {
        return reports;
    }

    public List<SurefireTestReport> reportsOfTestCase(String testCaseIdentifier) {
        String sPattern = String.format("(^|[@_\\s])(%s)($|[@_\\s])", testCaseIdentifier);
        Pattern pattern = Pattern.compile(sPattern);

        List<SurefireTestReport> matchingReports = new ArrayList<>();
        for (SurefireTestReport report : reports) {
            String name = report.name();
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                matchingReports.add(report);
            }
        }
        return matchingReports;
    }

    /**
     * Default class constructor.
     */
    public TestReportsLoader() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the provided content to the list of test reports.
     * @param inputStream A stream initialized on an XML file produced by surefire, with one or more test cases.
     * @throws IOException Hopefully not.
     */
    public void loadReport(InputStream inputStream) throws IOException {
        loadReport(LocalDateTime.now(), inputStream);
    }

    /**
     * Reads the provided content to the list of test reports.
     * @param xmlFile A XML file produced by surefire, with one or more test cases.
     * @throws IOException Hopefully not.
     */
    public void loadReport(File xmlFile) throws IOException {
        LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(xmlFile.lastModified()),
                TimeZone.getDefault().toZoneId());
        FileInputStream fis = new FileInputStream(xmlFile);
        loadReport(timestamp, fis);
    }

    /**
     * Reads the provided content to the list of test reports.
     * @param timestamp The timestamp when the test was ran.
     * @param inputStream A stream initialized on an XML file produced by surefire, with one or more test cases.
     * @throws IOException Hopefully not.
     */
    public void loadReport(LocalDateTime timestamp, InputStream inputStream) throws IOException {
        Document doc;
        try {
            doc = documentBuilder.parse(inputStream);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        doc.getDocumentElement().normalize();

        NodeList testCaseNodes = doc.getElementsByTagName("testcase");
        for (int i = 0; i < testCaseNodes.getLength(); i++) {
            Element testCaseElement = (Element) testCaseNodes.item(i);
            String testCaseName = testCaseElement.getAttribute("name");
            String testOutput = "";
            NodeList systemOutNodes = testCaseElement.getElementsByTagName("system-out");
            if (systemOutNodes.getLength() > 0) {
                testOutput = systemOutNodes.item(0).getTextContent().trim();
            }
            String testFailure = "";
            NodeList failureNodes = testCaseElement.getElementsByTagName("failure");
            if (failureNodes.getLength() > 0) {
                testFailure = systemOutNodes.item(0).getTextContent().trim();
            }

            LOGGER.info("Test report of: {}", testCaseName);
            reports.add(new SurefireTestReport(
                    timestamp,
                    testCaseName,
                    testOutput,
                    testFailure));
        }
    }
}
