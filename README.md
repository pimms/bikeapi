# BikeAPI

_(For the time being)_ a dumb wrapper around the public API provided by
[Oslo Bysykkel](https://developer.oslobysykkel.no).


## Initializing
##### API Identifier
The first thing you need to do to host this application is to create a
user account at [Oslo Bysykkel](https://developer.oslobysykkel.no) and
generate an API key. The key should be placed as a VM-variable
under the name `OBS_API_SECRET`.

The app should crash spectacularly if you've failed this step, so you'll know
when you've got it.

Start the application from `no.jstien.bikeapi.Main#main`, and you're good to
go.

##### OpenTSDB
If you want to run with OpenTSDB, you need to specifiy the URL on which it can
be reached in the VM-variable `TSDB_URL`. It should be on the form
`http://host:port`. Keep in mind that BikeAPI will only ever use the HTTP-API.

If you want BikeAPI to never **send** data to OpenTSDB, define the environment
or VM variable `TSDB_NO_WRITE`. A dummy-object that discards all datums will
be used in place of an actual `TSDBWriter`-instance.

If you want BikeAPI to never **read** TSDB data, define the environment or VM
variable `TSDB_NO_READ`. It will always return empty data sets.

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


### /stations/history
Retrieve the history of one or more stations.

##### Parameters:
> **from**
> The start of the queried range, on ISO-8601 UTC format

> **to**
> The end of the queried range, on ISO-8601 UTC format

> **id**
> The station(s) to query. If you want data from more than one station,
> repeat the parameter *(e.g., `...&id=123&id=124`)*.

> **dsm** *(OPTIONAL)*
> The number of minutes to downsample the results by. Leave undefined for raw data.

##### Example URL

`host.com/stations/history?from=2018-05-20T13:00:00.000Z&to=2018-05-20T23:59:59.000Z&id=272&id=188&dsm=10`

##### Return format
```
[
   {
      "stationId" : 272,
      "freeBikes" : {
         "dataPoints" : [
            {
               "ts" : 1526821200,
               "val" : 3
            },
            {
               "ts" : 1526821800,
               "val" : 2.05
            }
         ]
      },
      "freeLocks" : {
         "dataPoints" : [
            {
               "ts" : 1526821200,
               "val" : 7
            },
            {
               "ts" : 1526821800,
               "val" : 7.55
            }
         ]
      }
   }
]
```

##### Gotchas
 - Do note that **ALL** returned timestamps are in UTC.
 - You cannot query for longer than 30 hours in a single query
 - You cannot query for more than 5 stations in a single query

## Roadmap
The long term goal of this API is to provide a back-end that will answer a suitable
front-end the following questions:

 - When are the bikes typically all gone in the morning?
 - Where do I need to park my bike when I get downtown?

To properly answer these questions, the API should probably be fitted with a
TSDB of some sort.
