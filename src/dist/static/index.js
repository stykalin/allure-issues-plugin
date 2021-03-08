'use strict';

allure.api.addTranslation('en', {
    tab: {
        issues: {
            name: 'Issues'
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

allure.api.addWidget('widgets', 'issues', allure.components.WidgetStatusView.extend({
    rowTag: 'a',
    title: 'widget.issues.name',
    baseUrl: 'issues',
    showLinks: true
}));