// controllers.js

'use strict';

var resubmissionarControllers = angular.module('resubmissionarControllers', []);


resubmissionarControllers.controller('NavCtrl', ['$scope', '$route', function($scope, $route) {
    $scope.$route = $route;
}]);

resubmissionarControllers.controller('DashboardCtrl', ['$scope', 'Dashboard', function($scope, Dashboard) {
    $scope.dashboardActive = Dashboard.query();
}]);

resubmissionarControllers.controller('CustomerListCtrl', ['$scope', '$http', 'Customer',
    function($scope, $http, Customer) {
        $scope.paginationInfo = {
            searchText: undefined,
            pageSize: 25,
            page: 1,
            hasNext: true,
            hasPrev: false,
            count: 0
        };

        $scope.getPaginated = function() {
            var search;

            if ($scope.paginationInfo.searchText && $scope.paginationInfo.searchText.length > 1) {
                search = $scope.paginationInfo.searchText;
            } else {
                search = undefined;
            }

            var args;
            if (search) {
                args = { pageSize: $scope.paginationInfo.pageSize,
                    page: $scope.paginationInfo.page,
                    searchText: search
                };
            } else {
                args = { pageSize: $scope.paginationInfo.pageSize,
                    page: $scope.paginationInfo.page
                };
            }

            Customer.getPaginated(args, function(data) {
                // set data when it's avail. to avoid flickering
                $scope.customers = data.customers;
                // Update info
                $scope.paginationInfo.count = data.total;
                $scope.paginationInfo.hasPrev = $scope.paginationInfo.page > 1;
                $scope.paginationInfo.hasNext = data.customers.length >= $scope.paginationInfo.pageSize;
            });
        };

        $scope.paginatePrev = function() {
            if ($scope.paginationInfo.hasPrev) {
                $scope.paginationInfo.page -= 1;
                $scope.getPaginated();
            }
        };

        $scope.paginateNext = function() {
            if ($scope.paginationInfo.hasNext) {
                $scope.paginationInfo.page += 1;
                $scope.getPaginated();
            }
        };

        $scope.search = function() {
            var timer;
            if (timer) {
                clearTimeout(timer);
            }
            //noinspection JSUnusedAssignment
            timer = setTimeout(function() {
                $scope.getPaginated();
            }, 300);
        };

        $scope.$watch('paginationInfo.searchText', function(/*newValue, oldValue*/) {
            $scope.paginationInfo.page = 1; // reset to first page
        });

        $scope.isSeachEnabled= function() {
            return $scope.paginationInfo.searchText && $scope.paginationInfo.searchText.length > 1;
        };

        // Initial load of data
        $scope.getPaginated();
    }]);

resubmissionarControllers.controller('CustomerDetailCtrl', ['$scope', '$location', '$routeParams', '$modal', '$log', 'Customer', 'Resubmission',
    function($scope, $location, $routeParams, $modal, $log, Customer, Resubmission) {
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

        // Modal for delete customer confirmation

        $scope.openDeleteCustomerModal = function () {
            var modalInstance = $modal.open({
                templateUrl: 'myModalContent.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    customer: function () {
                        return $scope.customer;
                    }
                }
            });

            modalInstance.result.then(function () {
                Customer.delete({customerId: $routeParams.customerId}, function() {
                    $log.info('Successfully deleted customer');
                    $location.path('/customers');
                    $location.replace();
                });
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        var ModalInstanceCtrl = function ($scope, $modalInstance, customer) {
            $scope.customer = customer;

            $scope.ok = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        };

        // Create new resubmission

        $scope.createResubmission = function(resub) {
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

        // --------------------------------------- date stuff -------------------------------------

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };

        $scope.dateOptions = {
            startingDay: 1
        };

        $scope.initDate = new Date();
        $scope.minDate = $scope.initDate;
    }]);

resubmissionarControllers.controller('EditCustomerCtrl', ['$scope', '$route', '$routeParams', '$location' , '$upload', 'Customer',
    function($scope, $route, $routeParams, $location, $upload, Customer) {
        var editMode = $route.current.editMode !== 'create'; // see app.js/create-customer
        $scope.isEditMode = editMode;

        $scope.titleText = editMode ? 'Edit Customer' : 'New Customer';
        $scope.subTitleText = editMode ? '' : 'what a luck';
        $scope.legendText = $scope.titleText;
        $scope.saveCustomerText = editMode ? 'Save' : 'Create';
        $scope.cancelText = 'Cancel';

        var defaultForm = {
            companyName: '',
            description: null
        };

        if (editMode) {
            $scope.customer = Customer.get({customerId: $routeParams.customerId}, function(customer) {
                $scope.imageUrl = customer.imageUrl;
            });
        } else {
            $scope.customer = angular.copy(defaultForm);
        }

        $scope.saveCustomer = function(aCustomer) {
            aCustomer.imageUrl = undefined; // Not known on the server!
            aCustomer.resubmissions = undefined;

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
            });
        };

        $scope.cancelEdit = function() {
            $location.path('/customers/' + $routeParams.customerId);
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
                }).success(function (data /*, status, headers, config*/) { // file is uploaded successfully
                    $scope.uploads = data; // store uploads info in the scope to be used when customer is created
                    if (data != null && data.length > 0) {
                        var i = data[0];
                        $scope.customer.logoId = i.logoId;
                        $scope.imageUrl = i.imageUrl;
                    }
                });

                // Optional progress function:
                // .progress(function (evt) {
                //   console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                // }
            }
        };
    }]);

resubmissionarControllers.controller('AboutCtrl', ['$scope', function($scope) {
}]);
