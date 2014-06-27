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

resubmissionarControllers.controller('CustomerDetailCtrl', ['$scope', '$routeParams', 'Customer', 'Resubmission',
    function($scope, $routeParams, Customer, Resubmission) {
        $scope.customer = Customer.get({customerId: $routeParams.customerId}, function(customer){
            // empty, but we could fiddle with the result data here :-)
            // $scope.mainImageUrl = customer.images[0];
        });

        $scope.createResubmission = function(resub) {
            alert('createResubmission called with: ' + resub.note + ', ' + resub.due);
            resub.customerId = $scope.customer.id;
            Resubmission.save(resub);
        };
    }]);
