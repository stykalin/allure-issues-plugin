package com.github.stykalin.allure.issues;

import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.Label;
import io.qameta.allure.entity.Link;
import io.qameta.allure.entity.Status;
import io.qameta.allure.entity.TestResult;
import io.qameta.allure.tree.TestResultTreeGroup;
import io.qameta.allure.tree.Tree;
import io.qameta.allure.tree.TreeNode;
import io.qameta.allure.tree.TreeWidgetData;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
                .containsExactlyInAnyOrder("S2");

        assertThat(issuesData.getChildren())
                .allSatisfy(treeGroup -> {
                            assertThat(treeGroup.getName()).isEqualTo("S2");
                            List<TreeNode> children = ((TestResultTreeGroup) treeGroup).getChildren();
                            assertThat(children).hasSize(1);
                            assertThat(children.get(0).getName()).isEqualTo("T4");
                        }
                );
    }

    @Test
    public void issuesAggregationWidgetTest() {
        LaunchResults results = getLaunchResults();

        TreeWidgetData issuesData = new IssuesPlugin.WidgetAggregator().getData(singletonList(results));

        assertThat(issuesData.getItems())
                .extracting("name")
                .containsExactly("S2");
    }

    private LaunchResults getLaunchResults() {
        final Set<TestResult> testResults = new HashSet<>();
        testResults.add(new TestResult()
                .setName("T1")
                .setStatus(Status.PASSED)
                .setLinks(singletonList(createTmsLink("A-1")))
                .setLabels(singletonList(createSuite("S1"))));
        testResults.add(new TestResult()
                .setName("T2")
                .setStatus(Status.PASSED)
                .setLinks(singletonList(createTmsLink("A-2")))
                .setLabels(singletonList(createSuite("S1"))));
        testResults.add(new TestResult()
                .setName("T3")
                .setStatus(Status.PASSED)
                .setLinks(singletonList(createTmsLink("A-3")))
                .setLabels(singletonList(createSuite("S2"))));
        testResults.add(new TestResult()
                .setName("T4")
                .setStatus(Status.FAILED)
                .setLinks(asList(createTmsLink("A-4"), createIssueLink("I-2")))
                .setLabels(singletonList(createSuite("S2"))));

        return new SimpleLaunchResults(testResults, Collections.emptyMap(), Collections.emptyMap());
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
