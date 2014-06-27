// services.js

'use strict';

var resubmissionarServices = angular.module('resubmissionarServices', ['ngResource']);

resubmissionarServices.factory('Dashboard', ['$resource',
    function($resource) {
        return $resource('resources/dashboard', {}, {
            query: {method:'GET', params:{}, isArray:true}
        });
    }]);


resubmissionarServices.factory('Customer', ['$resource',
    function($resource) {
        return $resource('resources/customers/:customerId', {}, {
            query: {method:'GET', params:{customerId:''}, isArray:true}
        });
    }]);

resubmissionarServices.factory('Resubmission', ['$resource',
    function($resource) {
        return $resource('resources/resubmissions/:resubmissionId', {}, {
            query: {method:'GET', params:{resubmissionId:''}, isArray:true}
        });
    }]);
