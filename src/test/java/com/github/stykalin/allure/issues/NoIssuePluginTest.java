package com.github.stykalin.allure.issues;

import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.TestResult;
import io.qameta.allure.tree.TestResultTreeGroup;
import io.qameta.allure.tree.Tree;
import io.qameta.allure.tree.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class NoIssuePluginTest extends BasePluginTest {

    @Test
    public void noIssuesAggregationTest() {
        LaunchResults results = getLaunchResults();

        Tree<TestResult> issuesData = NoIssuesPlugin.getData(singletonList(results));

        assertThat(issuesData.getChildren())
                .extracting("name")
                .containsExactlyInAnyOrder("S1", "S2", "S3");

        List<String> testResultNames = issuesData.getChildren().stream()
                .flatMap(x -> ((TestResultTreeGroup) x).getChildren().stream())
                .map(TreeNode::getName)
                .collect(Collectors.toList());

        assertThat(testResultNames).containsOnly("A-1", "A-2", "A-3", "A-6");
    }
}