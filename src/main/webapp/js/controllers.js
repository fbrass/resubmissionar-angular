// controllers.js

'use strict';

var resubmissionarControllers = angular.module('resubmissionarControllers', []);

resubmissionarControllers.controller('DashboardCtrl', ['$scope', 'Dashboard', function($scope, Dashboard) {
        $scope.dashboard = Dashboard.query();
    }]);

resubmissionarControllers.controller('CustomerListCtrl', ['$scope', 'Customer', function($scope, Customer) {
        $scope.customers = Customer.query();
        $scope.orderProp = 'age'; // TODO unused
        $scope.paginatePrev = function() {
            alert('paginatePrev called');
        };
        $scope.paginateNext = function() {
            alert('paginateNext called');
        };
        $scope.createCustomer = function() {
            alert('createCustomer called');
        };
    }]);

resubmissionarControllers.controller('CustomerCtrl', ['$scope', 'Customer',
    function($scope, Customer) {
        var defaultForm = {
            companyName:''
        };

        $scope.customer = angular.copy(defaultForm);

        $scope.createCustomer = function(customer) {
            Customer.save(customer, function() {
                $scope.customerform.$setPristine(true);
                $scope.customer = angular.copy(defaultForm);
            })
        };
    }
]);

resubmissionarControllers.controller('CustomerDetailCtrl', ['$scope', '$routeParams', 'Customer', 'Resubmission',
    function($scope, $routeParams, Customer, Resubmission) {
        function dateToYMD(date) {
            var d = date.getDate();
            var m = date.getMonth() + 1;
            var y = date.getFullYear();
            return '' + y + '-' + (m<=9 ? '0' + m : m) + '-' + (d <= 9 ? '0' + d : d);
        }

        var defaultForm = {
            note:'',
            due:dateToYMD(new Date())
        };

        $scope.resub = angular.copy(defaultForm);

        $scope.customer = Customer.get({customerId: $routeParams.customerId}, function(customer) {
            // empty, but we could fiddle with the result data here :-)
            // $scope.mainImageUrl = customer.images[0];
        });

        function reloadCustomer() {
            Customer.get({customerId: $routeParams.customerId}, function(customer) {
                $scope.customer = customer;
            });
        }

        $scope.createResubmission = function(resub) {
            //alert('createResubmission called with: ' + resub.note + ', ' + resub.due);
            resub.customerId = $scope.customer.id;
            Resubmission.save(resub, function() {
                reloadCustomer();
                $scope.resubform.$setPristine(true);
                $scope.resub = angular.copy(defaultForm);
            });
        };

        $scope.markDone = function(resubmission) {
            resubmission.active = false
            Resubmission.save(resubmission, function() {
                reloadCustomer();
            });
        };
    }]);
