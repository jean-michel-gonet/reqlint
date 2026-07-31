package com.reqlint.core.surefire;

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
import java.util.TimeZone;

public class TestReportsLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReportsLoader.class);

    private final SurefireTestReport surefireTestReport;
    private final DocumentBuilder documentBuilder;

    /**
     * Default class constructor.
     */
    public TestReportsLoader() {
        this(new  SurefireTestReport());
    }

    /**
     * Use this class constructor if you want to continue building up the test report.
     * @param surefireTestReport The test report.
     */
    public TestReportsLoader(SurefireTestReport surefireTestReport) {
        this.surefireTestReport = surefireTestReport;
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
            String name = testCaseElement.getAttribute("name");
            String className = testCaseElement.getAttribute("classname");
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

            LOGGER.info("Test report of: {} {}", className, name);
            surefireTestReport.addReportItem(new SurefireTestReportItem(
                    timestamp,
                    className + " " + name,
                    testOutput,
                    testFailure));
        }
    }

    /**
     * @return The test report.
     */
    public SurefireTestReport getTestReport() {
        return surefireTestReport;
    }
}
