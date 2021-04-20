package com.github.stykalin.allure.issues;

import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.Label;
import io.qameta.allure.entity.Link;
import io.qameta.allure.entity.Status;
import io.qameta.allure.entity.TestResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class BasePluginTest {

    public static final String SUITE_LABEL_NAME = "suite";
    public static final String ISSUE_LINK_TYPE = "issue";
    public static final String TMS_LINK_TYPE = "tms";

    LaunchResults getLaunchResults() {
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
                .setName(tmsName)
                .setStatus(Status.PASSED)
                .setLinks(singletonList(createTmsLink(tmsName)))
                .setLabels(singletonList(createSuite(suiteName)));
    }

    private TestResult createTestResultWithIssues(String tmsName, String issueName, String suiteName, Status status) {
        return new TestResult()
                .setName(tmsName)
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