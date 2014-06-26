// services.js

'use strict';

var resubmissionarServices = angular.module('resubmissionarServices', ['ngResource']);

resubmissionarServices.factory('Customer', ['$resource',
    function($resource){
        return $resource('resources/customers/:customerId', {}, {
            query: {method:'GET', params:{customerId:''}, isArray:true}
        });
    }]);
