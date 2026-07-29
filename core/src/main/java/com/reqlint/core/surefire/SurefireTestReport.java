package com.reqlint.core.surefire;

import com.reqlint.core.surefire.warnings.TestReportWarning;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains the surefire test report, in a more accessible form.
 * @see TestReportsLoader
 */
public class SurefireTestReport {
    private final List<SurefireTestReportItem> reportItems = new ArrayList<>();
    private final List<TestReportWarning> warnings = new ArrayList<>();

    /**
     * @param reportItem A new item to add to the report.
     */
    public void addReportItem(SurefireTestReportItem reportItem) {
        reportItems.add(reportItem);
    }

    /**
     * @param warning A warning found while processing the reprt.
     */
    public void addReportWarning(TestReportWarning warning) {
        warnings.add(warning);
    }

    /**
     * Finds all reports related to the specified test case identifier.
     * It should ideally answer with exactly one report, but:
     * <ul>
     *     <li>Zero results - A test case may not be automatized, so it has no corresponding test report.</li>
     *     <li>More than one result - By accident, label from one test scenario is duplicated to another.
     *     This should be treated as a problem.
     *     </li>
     * </ul>
     * @param testCaseIdentifier The test case identifier.
     * @return A collection of reports.
     */
    public List<SurefireTestReportItem> reportsOfTestCase(String testCaseIdentifier) {
        String sPattern = String.format("(^|[@_\\s])(%s)($|[@_\\s])", testCaseIdentifier);
        Pattern pattern = Pattern.compile(sPattern);

        List<SurefireTestReportItem> matchingReports = new ArrayList<>();
        for (SurefireTestReportItem reportItem : reportItems) {
            String name = reportItem.name();
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                matchingReports.add(reportItem);
            }
        }
        return matchingReports;
    }

    public List<TestReportWarning> warnings() {
        return warnings.stream().sorted().toList();
    }

    public List<SurefireTestReportItem> reports() {
        return reportItems;
    }
}
