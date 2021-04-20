package com.github.stykalin.allure.issues;

import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.TestResult;
import io.qameta.allure.tree.TestResultTreeGroup;
import io.qameta.allure.tree.Tree;
import io.qameta.allure.tree.TreeWidgetData;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class IssuesPluginTest extends BasePluginTest {

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
}
