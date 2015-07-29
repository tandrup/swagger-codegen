/// <reference path="API/Client/api.d.ts" />
/// <reference path="typings/tsd.d.ts" />

angular.module('petstore', [])
        .factory('petApi', ['$http', ($http: ng.IHttpService) => {
            return new API.Client.PetApi($http, '/api/dco/rest');
        }]);
