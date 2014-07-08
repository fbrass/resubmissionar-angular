// app.js

'use strict';

var resubmissionarApp = angular.module('resubmissionarApp', [
    'ngRoute',
    'resubmissionarControllers',
    'resubmissionarServices',
    'angularFileUpload',
    'ui.bootstrap'
]);

resubmissionarApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/', {
                redirectTo: '/dashboard'
            }).
            when('/dashboard', {
                templateUrl: 'partials/dashboard.html',
                controller: 'DashboardCtrl',
                activeTab: 'dashboard'
            }).
            when('/customers', {
                templateUrl: 'partials/customer-list.html',
                controller: 'CustomerListCtrl',
                activeTab: 'customers'
            }).
            when('/customers/:customerId', {
                templateUrl: 'partials/customer-detail.html',
                controller: 'CustomerDetailCtrl',
                activeTab: 'customers'
            }).
            when('/create-customer', {
                templateUrl: 'partials/edit-customer.html',
                controller: 'EditCustomerCtrl',
                editMode: 'create',
                activeTab: 'customers'
            }).
            when('/edit-customer/:customerId', {
                templateUrl: 'partials/edit-customer.html',
                controller: 'EditCustomerCtrl',
                editMode: 'edit',
                activeTab: 'customers'
            }).
            when('/about', {
                templateUrl: 'partials/about.html',
                controller: 'AboutCtrl',
                activeTab: 'about'
            }).
            otherwise({
                redirectTo: '/dashboard'
            });
    }
]);
