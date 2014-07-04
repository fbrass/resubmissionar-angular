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
        return $resource('resources/customers/:customerId/:pageSize/:page/:searchText',
            {customerId:null, pageSize:null, page:null, searchText:null},
            {
                'getPaginated': { method: 'GET', isArray: false } // !important
            });
    }]);

resubmissionarServices.factory('Resubmission', ['$resource',
    function($resource) {
        return $resource('resources/resubmissions/:resubmissionId', {}, {
            query: {method:'GET', params:{resubmissionId:''}, isArray:true}
        });
    }]);
