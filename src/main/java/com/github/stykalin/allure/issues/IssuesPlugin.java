package com.github.stykalin.allure.issues;

import io.qameta.allure.CommonCsvExportAggregator;
import io.qameta.allure.CommonJsonAggregator;
import io.qameta.allure.CompositeAggregator;
import io.qameta.allure.Constants;
import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.csv.CsvExportSuite;
import io.qameta.allure.entity.TestResult;
import io.qameta.allure.tree.TestResultTree;
import io.qameta.allure.tree.TestResultTreeGroup;
import io.qameta.allure.tree.Tree;
import io.qameta.allure.tree.TreeWidgetData;
import io.qameta.allure.tree.TreeWidgetItem;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.qameta.allure.entity.LabelName.PARENT_SUITE;
import static io.qameta.allure.entity.LabelName.SUB_SUITE;
import static io.qameta.allure.entity.LabelName.SUITE;
import static io.qameta.allure.entity.Statistic.comparator;
import static io.qameta.allure.entity.TestResult.comparingByTimeAsc;
import static io.qameta.allure.tree.TreeUtils.calculateStatisticByLeafs;
import static io.qameta.allure.tree.TreeUtils.groupByLabels;

public class IssuesPlugin extends CompositeAggregator {

    private static final String ISSUE_LINK_TYPE = "issue";
    private static final String ISSUES = "issues";

    /**
     * Name of the json file.
     */
    protected static final String JSON_FILE_NAME = "issues.json";

    /**
     * Name of the csv file.
     */
    protected static final String CSV_FILE_NAME = "issues.csv";

    public IssuesPlugin() {
        super(Arrays.asList(
                new JsonAggregator(), new CsvExportAggregator(), new WidgetAggregator()
        ));
    }

    @SuppressWarnings("PMD.DefaultPackage")
    static /* default */ Tree<TestResult> getData(final List<LaunchResults> launchResults) {

        // @formatter:off
        final Tree<TestResult> issuesTree = new TestResultTree(
                ISSUES,
                testResult -> groupByLabels(testResult, PARENT_SUITE, SUITE, SUB_SUITE)
        );
        // @formatter:on

        launchResults.stream()
                .map(LaunchResults::getResults)
                .flatMap(Collection::stream)
                .filter(tr -> tr.getLinks().stream().anyMatch(link -> link.getType().equals(ISSUE_LINK_TYPE)))
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
            return IssuesPlugin.getData(launches);
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

    /**
     * Generates widget data.
     */
    protected static class WidgetAggregator extends CommonJsonAggregator {

        WidgetAggregator() {
            super(Constants.WIDGETS_DIR, JSON_FILE_NAME);
        }

        @Override
        public TreeWidgetData getData(final List<LaunchResults> launches) {
            final Tree<TestResult> data = IssuesPlugin.getData(launches);
            final List<TreeWidgetItem> items = data.getChildren().stream()
                    .filter(TestResultTreeGroup.class::isInstance)
                    .map(TestResultTreeGroup.class::cast)
                    .map(WidgetAggregator::toWidgetItem)
                    .sorted(Comparator.comparing(TreeWidgetItem::getStatistic, comparator()).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
            int totalLeafSize = (int) data.getChildren().stream().map(group -> ((TestResultTreeGroup) group).getChildren()).mapToLong(Collection::size).sum();
            return new TreeWidgetData().setItems(items).setTotal(totalLeafSize);
        }

        private static TreeWidgetItem toWidgetItem(final TestResultTreeGroup group) {
            return new TreeWidgetItem()
                    .setUid(group.getUid())
                    .setName(group.getName())
                    .setStatistic(calculateStatisticByLeafs(group));
        }
    }
}
