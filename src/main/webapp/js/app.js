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
            when('/', {
                redirectTo: '/dashboard'
            }).
            when('/dashboard', {
                templateUrl: 'partials/dashboard.html',
                controller: 'DashboardCtrl'
            }).
            when('/customers', {
                templateUrl: 'partials/customer-list.html',
                controller: 'CustomerListCtrl'
            }).
            when('/customers/:customerId', {
                templateUrl: 'partials/customer-detail.html',
                controller: 'CustomerDetailCtrl'
            }).
            otherwise({
//                redirectTo: '/customers'
                redirectTo: '/dashboard'
            });
    }
]);

resubmissionarApp.directive('myDatepicker', function ($parse) {
    return function (scope, element, attrs, controller) {
        var ngModel = $parse(attrs.ngModel);
        $(function(){
            element.datepicker({
//                showOn:"both",
//                changeYear:true,
//                changeMonth:true,
                dateFormat:'yy-mm-dd',
//                maxDate: new Date(),
//                yearRange: '1920:2012',
                onSelect:function (dateText, inst) {
                    scope.$apply(function(scope){
                        // Change binded variable
                        ngModel.assign(scope, dateText);
                    });
                }
            });
        });
    }
});
