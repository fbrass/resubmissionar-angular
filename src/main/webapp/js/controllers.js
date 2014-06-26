// controllers.js

'use strict';

var resubmissionarControllers = angular.module('resubmissionarControllers', []);

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

resubmissionarControllers.controller('CustomerDetailCtrl', ['$scope', '$routeParams', 'Customer',
    function($scope, $routeParams, Customer) {
        $scope.customer = Customer.get({customerId: $routeParams.customerId}, function(customer){
            // empty, but we could fiddle with the result data here :-)
            // $scope.mainImageUrl = customer.images[0];
        });

        $scope.setImage = function(imageUrl) {
            alert('setImage called with: ' + imageUrl);
        };

        $scope.hello = function(name) {
            alert('hello called with: ' + name);
        };
    }]);
