/// <reference path="../../API/Client/api.d.ts" />
/// <reference path="../../typings/tsd.d.ts" />

'use strict';

describe('PetApi', function () {

  // load the controller's module
  beforeEach(module('petstore'));

  var api: API.Client.PetApi;

  // Initialize the controller and a mock scope
  beforeEach(inject((petApi: API.Client.PetApi, _$httpBackend_: any) => {
    api = petApi;
	console.log(_$httpBackend_);
    _$httpBackend_.whenGET(/.*/).passThrough();
  }));

  it('should call sample petstore', (done) => {
    var pet = new API.Client.Pet();
    pet.name = 'TypeScriptDoggie';
    var petId: any;

    var petApi = api;
	
	this.timeout(5000);

	api.addPet(pet).then(() => {
		console.log('Created pet');
		done();
	}).catch((err:any) => {
		console.error(err);
		done(err);
	});

	
    // Test various API calls to the petstore
/*
var promise = petApi.addPet(pet)
.then((res) => {
	var newPet = <API.Client.Pet>res.data;
	petId = newPet.id;
	console.log(`Created pet with ID ${petId}`);
	newPet.status = API.Client.Pet.StatusEnum.available;
	return petApi.updatePet(newPet);
})
.then((res) => {
	console.log('Updated pet using POST body');
	return petApi.updatePetWithForm(petId, undefined, "pending");
})
.then((res) => {
	console.log('Updated pet using POST form');
	return <ng.IPromise<ng.IHttpPromiseCallbackArg<any>>>petApi.getPetById(petId);
})
.then((res) => {
	console.log('Got pet by ID: ' + JSON.stringify(res.data));
	if (res.data.status != API.Client.Pet.StatusEnum.pending) {
		throw new Error("Unexpected pet status");
	}
})
.catch((err:any) => {
	console.error(err);
	done(err);
})
.finally(() => {
	return petApi.deletePet(petId);
})
.then((res) => {
	console.log('Deleted pet');
  done();
});
*/
  });
});
