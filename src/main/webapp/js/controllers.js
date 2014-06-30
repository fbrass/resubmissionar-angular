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

resubmissionarControllers.controller('CustomerCtrl', ['$scope', 'Customer',
    function($scope, Customer) {
        var defaultForm = {
            companyName:''
        };

        $scope.customer = angular.copy(defaultForm);

        $scope.createCustomer = function(aCustomer) {
            Customer.save(aCustomer, function() {
                debugger;
                if (null != $scope.uploadIds) {
                    customer.logoId = $scope.uploadIds[0];
                }
                $scope.customerform.$setPristine(true);
                $scope.customer = angular.copy(defaultForm);
            })
        };
    }]);

resubmissionarControllers.controller('MyCtrl', ['$scope', '$upload',
    function($scope, $upload) {
        $scope.onFileSelect = function($files) {
            //$files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                $scope.upload = $upload.upload({
                    url: 'image', //upload.php script, node.js route, or servlet url
                    // method: 'POST' or 'PUT',
                    method: 'POST',
                    // headers: {'header-key': 'header-value'},
                    // withCredentials: true,
                    data: {myObj: $scope.myModelObj},
                    file: file // or list of files: $files for html5 only
                    /* set the file formData name ('Content-Desposition'). Default is 'file' */
                    //fileFormDataName: myFile, //or a list of names for multiple files (html5).
                    /* customize how data is added to formData. See #40#issuecomment-28612000 for sample code */
                    //formDataAppender: function(formData, key, val){}
                }).progress(function (evt) {
                    console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                }).success(function (data, status, headers, config) {
                    // file is uploaded successfully
                    console.log(data);
                    debugger;
                    $scope.uploadIds = data;
                });
                //.error(...)
                //.then(success, error, progress);
                //.xhr(function(xhr){xhr.upload.addEventListener(...)})// access and attach any event listener to XMLHttpRequest.
            }
        };
    }]);


