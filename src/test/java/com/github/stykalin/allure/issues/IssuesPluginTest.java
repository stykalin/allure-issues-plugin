package com.github.stykalin.allure.issues;

import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.Label;
import io.qameta.allure.entity.Link;
import io.qameta.allure.entity.Status;
import io.qameta.allure.entity.TestResult;
import io.qameta.allure.tree.TestResultTreeGroup;
import io.qameta.allure.tree.Tree;
import io.qameta.allure.tree.TreeWidgetData;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class IssuesPluginTest {
    public static final String SUITE_LABEL_NAME = "suite";
    public static final String ISSUE_LINK_TYPE = "issue";
    public static final String TMS_LINK_TYPE = "tms";

    @Test
    public void issuesAggregationTest() {
        LaunchResults results = getLaunchResults();

        Tree<TestResult> issuesData = IssuesPlugin.getData(singletonList(results));

        assertThat(issuesData.getChildren())
                .extracting("name")
                .containsExactlyInAnyOrder("S2", "S3");

        assertThat(((TestResultTreeGroup) issuesData.getChildren().get(0)))
                .satisfies(group -> assertThat(group.getChildren()).hasSize(2));
        assertThat(((TestResultTreeGroup) issuesData.getChildren().get(1)))
                .satisfies(group -> assertThat(group.getChildren()).hasSize(1));
    }

    @Test
    public void issuesAggregationWidgetTest() {
        LaunchResults results = getLaunchResults();

        TreeWidgetData issuesData = new IssuesPlugin.WidgetAggregator().getData(singletonList(results));

        assertThat(issuesData.getItems())
                .extracting("name")
                .containsExactlyInAnyOrder("S2", "S3");
        assertThat(issuesData.getTotal()).isEqualTo(3);
    }

    private LaunchResults getLaunchResults() {
        final Set<TestResult> testResults = new HashSet<>();
        testResults.add(createPassedTestResult("A-1", "S1"));
        testResults.add(createPassedTestResult("A-2", "S1"));
        testResults.add(createPassedTestResult("A-3", "S2"));
        testResults.add(createTestResultWithIssues("A-4", "I-2", "S2", Status.FAILED));
        testResults.add(createTestResultWithIssues("A-5", "I-3", "S2", Status.PASSED));
        testResults.add(createPassedTestResult("A-6", "S3"));
        testResults.add(createTestResultWithIssues("A-7", "I-4", "S3", Status.FAILED));

        return new SimpleLaunchResults(testResults, Collections.emptyMap(), Collections.emptyMap());
    }

    private TestResult createPassedTestResult(String tmsName, String suiteName) {
        return new TestResult()
                .setStatus(Status.PASSED)
                .setLinks(singletonList(createTmsLink(tmsName)))
                .setLabels(singletonList(createSuite(suiteName)));
    }

    private TestResult createTestResultWithIssues(String tmsName, String issueName, String suiteName, Status status) {
        return new TestResult()
                .setStatus(status)
                .setLinks(asList(createTmsLink(tmsName), createIssueLink(issueName)))
                .setLabels(singletonList(createSuite(suiteName)));
    }

    private Link createTmsLink(String name) {
        return new Link().setName(name).setType(TMS_LINK_TYPE);
    }

    private Link createIssueLink(String name) {
        return new Link().setName(name).setType(ISSUE_LINK_TYPE);
    }

    private Label createSuite(String suiteName) {
        return new Label().setName(SUITE_LABEL_NAME).setValue(suiteName);
    }
}
