# BikeAPI

_(For the time being)_ a dumb wrapper around the public API provided by
[Oslo Bysykkel](https://developer.oslobysykkel.no).


## Initializing

The first thing you need to do to host this application is to create a
user account at [Oslo Bysykkel](https://developer.oslobysykkel.no) and
generate an API key. Place the key inside
`src/main/resources/api-identifier.properties` on the following format:

```
api.identifier=<blablablabla>
```

The app should crash spectacularly if you've failed this step, so you'll know
when you've got it.

Start the application from `no.jstien.bikeapi.Main#main`, and you're good to
go.


## Methods

### /stations

Returns all stations with the current bike/lock status.

##### Example URL
`host.com/stations`

##### Return format
```
[
   {
      "title" : "Badebakken"
      "subtitle" : "ved Maridalsveien",
      "id" : 428,
      "numberOfLocks" : 18,
      "freeBikes" : 0,
      "freeLocks" : 18,
      "coordinate" : {
         "latitude" : 59.945575,
         "longitude" : 10.760417
      },
   },
   {
      "title" : "Landstads gate",
      "subtitle" : "ved Uelands gate",
      "id" : 432,
      "numberOfLocks" : 27
      "freeLocks" : 23,
      "freeBikes" : 4,
      "coordinate" : {
         "latitude" : 59.929013,
         "longitude" : 10.749677
      },
   },
   { ... }, { ... }
]
```


### /stations/closest

Returns the closest station to a given geographical location, regardless of
current bike status.

##### Example URL
`host.com/stations/closest?lat=69.420&lon=10.69420`

##### Return format
Just as the other one, but only one.
```
{
   "title" : "Lodalen busstopp",
   "subtitle" : "langs Dyvekes vei",
   "id" : 188,
   "numberOfLocks" : 12
   "freeLocks" : 8,
   "freeBikes" : 3,
   "coordinate" : {
      "latitude" : 59.903467,
      "longitude" : 10.777866
   },
}
```

### /stations/closestWithBikes

Behaves identically to `/stations/closest`, with the exception that it returns
the closest station that has *at least* 1 available bike.

##### Example URL
`host.com/stations/closestWithBikes?lat=69.420&lon=10.69420`

## Roadmap

The long term goal of this API is to provide a back-end that will answer a suitable
front-end the following questions:

 - When are the bikes typically all gone in the morning?
 - Where do I need to park my bike when I get downtown?

To properly answer these questions, the API should probably be fitted with a
TSDB of some sort.
