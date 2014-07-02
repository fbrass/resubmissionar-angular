// controllers.js

'use strict';

var resubmissionarControllers = angular.module('resubmissionarControllers', []);

resubmissionarControllers.controller('DashboardCtrl', ['$scope', 'Dashboard', function($scope, Dashboard) {
        $scope.dashboard = Dashboard.query();
    }]);

resubmissionarControllers.controller('CustomerListCtrl', ['$scope', '$http', 'Customer',
    function($scope, $http, Customer) {
        $scope.paginationInfo = {
            pageSize: 17,
            page: 1,
            hasNext: true,
            hasPrev: false,
            count: 0
        };

        $scope.getPaginatedData = function() {
            $scope.customers = Customer.getPaginated(
                {pageSize:$scope.paginationInfo.pageSize,
                page:$scope.paginationInfo.page},
                function(data) {
                    // Update info
                    $scope.paginationInfo.count = data.total;
                    $scope.paginationInfo.hasPrev = $scope.paginationInfo.page > 1;
                    $scope.paginationInfo.hasNext =
                        $scope.customers.customers.length >= $scope.paginationInfo.pageSize;
                }
            );
        };

        $scope.paginatePrev = function() {
            if ($scope.paginationInfo.hasPrev) {
                $scope.paginationInfo.page -= 1;
                $scope.getPaginatedData();
            }
        };

        $scope.paginateNext = function() {
            if ($scope.paginationInfo.hasNext) {
                $scope.paginationInfo.page += 1;
                $scope.getPaginatedData();
            }
        };

        // Initial load of data
        $scope.getPaginatedData();
    }]);

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
            resubmission.active = false;
            Resubmission.save(resubmission, function() {
                reloadCustomer();
            });
        };
    }]);

resubmissionarControllers.controller('CreateCustomerCtrl', ['$scope', '$location' , '$upload', 'Customer',
    function($scope, $location, $upload, Customer) {
        var defaultForm = {
            companyName:''
        };

        $scope.customer = angular.copy(defaultForm);

        $scope.createCustomer = function(aCustomer) {
            Customer.save(aCustomer, function(data) {
                $scope.customerform.$setPristine(true);
                $scope.customer = angular.copy(defaultForm);

                // show customer details and back button should not go back to create-customer
                if (data.customerId) {
                    $location.path('/customers/' + data.customerId);
                } else {
                    $location.path('/customers');
                }
                $location.replace();
            })
        };

        $scope.removeImage = function() {
            $scope.imageUrl = undefined;
            $scope.customer.logoId = undefined;
        };

        $scope.onFileSelect = function($files) {
            // $files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                $scope.upload = $upload.upload({
                    url: 'file',
                    method: 'POST', // POST or PUT
                    file: file      // or list of files: $files for html5 only
                }).progress(function (evt) {
                    console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                }).success(function (data, status, headers, config) { // file is uploaded successfully
                    console.log(data);
                    $scope.uploads = data; // store uploads info in the scope to be used when customer is created
                    if (data != null && data.length > 0) {
                        var i = data[0];
                        $scope.customer.logoId = i.logoId;
                        $scope.imageUrl = i.imageUrl;
                    }
                });
            }
        };
    }]);
