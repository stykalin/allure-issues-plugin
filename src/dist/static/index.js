'use strict';

allure.api.addTranslation('en', {
    tab: {
        issues: {
            name: 'Issues'
        },
        no_issues: {
            name: 'w/o Issues'
        }
    },
    widget: {
        issues: {
            name: 'Tests with Issues',
            showAll: 'show all'
        }
    }
});

allure.api.addTranslation('ru', {
    tab: {
        issues: {
            name: 'Баги'
        },
        no_issues: {
            name: 'Без багов'
        }
    },
    widget: {
        issues: {
            name: 'Баги',
            showAll: 'показать все'
        }
    }
});


allure.api.addTab('issues', {
    title: 'tab.issues.name', icon: 'fa fa-bug',
    route: 'issues(/)(:testGroup)(/)(:testResult)(/)(:testResultTab)(/)',
    onEnter: (function (testGroup, testResult, testResultTab) {
        return new allure.components.TreeLayout({
            testGroup: testGroup,
            testResult: testResult,
            testResultTab: testResultTab,
            tabName: 'tab.issues.name',
            baseUrl: 'issues',
            url: 'data/issues.json',
            csvUrl: 'data/issues.csv'
        });
    })
});

allure.api.addTab('no_issues', {
    title: 'tab.no_issues.name', icon: 'fa fa-sun-o',
    route: 'no_issues(/)(:testGroup)(/)(:testResult)(/)(:testResultTab)(/)',
    onEnter: (function (testGroup, testResult, testResultTab) {
        return new allure.components.TreeLayout({
            testGroup: testGroup,
            testResult: testResult,
            testResultTab: testResultTab,
            tabName: 'tab.no_issues.name',
            baseUrl: 'no_issues',
            url: 'data/no-issues.json',
            csvUrl: 'data/no-issues.csv'
        });
    })
});

allure.api.addWidget('widgets', 'issues', allure.components.WidgetStatusView.extend({
    rowTag: 'a',
    title: 'widget.issues.name',
    baseUrl: 'issues',
    showLinks: true
}));