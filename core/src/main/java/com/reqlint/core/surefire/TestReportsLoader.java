package com.reqlint.core.surefire;

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

public class TestReportsLoader {
    private final List<SurefireTestReport> reports = new ArrayList<>();
    private final DocumentBuilder documentBuilder;

    public List<SurefireTestReport> reports() {
        return reports;
    }

    public SurefireTestReport getReport(String testCaseIdentifier) {
        return null;
    }

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

    public void loadReport(InputStream inputStream) throws IOException {
        loadReport(LocalDateTime.now(), inputStream);
    }

    public void loadReport(File xmlFile) throws IOException {
        LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(xmlFile.lastModified()),
                TimeZone.getDefault().toZoneId());
        FileInputStream fis = new FileInputStream(xmlFile);
        loadReport(timestamp, fis);
    }

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
                // getTextContent() automatically unpacks CDATA blocks seamlessly
                testOutput = systemOutNodes.item(0).getTextContent().trim();
            }
            String testFailure = "";
            NodeList failureNodes = testCaseElement.getElementsByTagName("failure");
            if (failureNodes.getLength() > 0) {
                // getTextContent() automatically unpacks CDATA blocks seamlessly
                testFailure = systemOutNodes.item(0).getTextContent().trim();
            }

            reports.add(new SurefireTestReport(
                    timestamp,
                    testCaseName,
                    testOutput,
                    testFailure));
        }
    }
}
