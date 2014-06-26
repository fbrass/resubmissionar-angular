// app.js

'use strict';

var resubmissionarApp = angular.module('resubmissionarApp', [
    'ngRoute',
    'resubmissionarControllers',
    'resubmissionarServices'
]);

resubmissionarApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/customers', {
                templateUrl: 'partials/customer-list.html',
                controller: 'CustomerListCtrl'
            }).
            when('/customers/:customerId', {
                templateUrl: 'partials/customer-detail.html',
                controller: 'CustomerDetailCtrl'
            }).
            otherwise({
                redirectTo: '/customers'
            });
    }
]);
