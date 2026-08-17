package com.reqlint.core.model.testreport;

import com.reqlint.core.model.testreport.items.TestRun;
import com.reqlint.core.input.surefire.SurefireReportsLoader;
import com.reqlint.core.model.testreport.warnings.TestRunWarning;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains the surefire test report, in a more accessible form.
 * @see SurefireReportsLoader
 */
public class TestReport {
    private final List<TestRun> reportItems = new ArrayList<>();
    private final List<TestRunWarning> warnings = new ArrayList<>();

    /**
     * @param reportItem A new item to add to the report.
     */
    public void addReportItem(TestRun reportItem) {
        reportItems.add(reportItem);
    }

    /**
     * @param warning A warning found while processing the reprt.
     */
    public void addReportWarning(TestRunWarning warning) {
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
    public List<TestRun> reportsOfTestCase(String testCaseIdentifier) {
        String testCaseIdentifierWithSafeChars = testCaseIdentifier.replace("-", "[-_]");
        String sPattern = String.format("(^|[@_\\s])(%s)($|[@_\\s])", testCaseIdentifierWithSafeChars);
        Pattern pattern = Pattern.compile(sPattern);

        List<TestRun> matchingReports = new ArrayList<>();
        for (TestRun reportItem : reportItems) {
            String name = reportItem.title();
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                matchingReports.add(reportItem);
            }
        }
        return matchingReports;
    }

    public List<TestRunWarning> warnings() {
        return warnings.stream().sorted().toList();
    }

    public List<TestRun> reports() {
        return reportItems;
    }

    public int numberOfReports() {
        return reportItems.size();
    }
}
