package com.github.stykalin.allure.issues;

import io.qameta.allure.CommonCsvExportAggregator;
import io.qameta.allure.CommonJsonAggregator;
import io.qameta.allure.CompositeAggregator;
import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.csv.CsvExportSuite;
import io.qameta.allure.entity.TestResult;
import io.qameta.allure.tree.TestResultTree;
import io.qameta.allure.tree.Tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.qameta.allure.entity.LabelName.PARENT_SUITE;
import static io.qameta.allure.entity.LabelName.SUB_SUITE;
import static io.qameta.allure.entity.LabelName.SUITE;
import static io.qameta.allure.entity.TestResult.comparingByTimeAsc;
import static io.qameta.allure.tree.TreeUtils.groupByLabels;

public class NoIssuesPlugin extends CompositeAggregator {

    private static final String ISSUE_LINK_TYPE = "issue";
    private static final String NO_ISSUES = "no_issues";

    /**
     * Name of the json file.
     */
    protected static final String JSON_FILE_NAME = "no-issues.json";

    /**
     * Name of the csv file.
     */
    protected static final String CSV_FILE_NAME = "no-issues.csv";

    public NoIssuesPlugin() {
        super(Arrays.asList(
                new JsonAggregator(), new CsvExportAggregator()
        ));
    }

    @SuppressWarnings("PMD.DefaultPackage")
    static /* default */ Tree<TestResult> getData(final List<LaunchResults> launchResults) {

        // @formatter:off
        final Tree<TestResult> issuesTree = new TestResultTree(
                NO_ISSUES,
                testResult -> groupByLabels(testResult, PARENT_SUITE, SUITE, SUB_SUITE)
        );
        // @formatter:on

        launchResults.stream()
                .map(LaunchResults::getResults)
                .flatMap(Collection::stream)
                .filter(tr -> tr.getLinks().stream().noneMatch(link -> link.getType().equals(ISSUE_LINK_TYPE)))
                .sorted(comparingByTimeAsc())
                .forEach(issuesTree::add);
        return issuesTree;
    }

    /**
     * Generates tree data.
     */
    private static class JsonAggregator extends CommonJsonAggregator {

        JsonAggregator() {
            super(JSON_FILE_NAME);
        }

        @Override
        protected Tree<TestResult> getData(final List<LaunchResults> launches) {
            return NoIssuesPlugin.getData(launches);
        }
    }

    /**
     * Generates export data.
     */
    private static class CsvExportAggregator extends CommonCsvExportAggregator<CsvExportSuite> {

        CsvExportAggregator() {
            super(CSV_FILE_NAME, CsvExportSuite.class);
        }

        @Override
        protected List<CsvExportSuite> getData(final List<LaunchResults> launchesResults) {
            return launchesResults.stream()
                    .flatMap(launch -> launch.getResults().stream())
                    .map(CsvExportSuite::new).collect(Collectors.toList());
        }
    }
}