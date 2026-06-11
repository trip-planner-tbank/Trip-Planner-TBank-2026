# Trip Planner API 

## General Information



**Authentication**

```text
JWT Bearer Token
```

**Content-Type**

```text
application/json
```

---
# Authentication

## Sign Up

### Endpoint

```http
POST /auth/signup
```
### Description
Sign up new user and give him access and refresh tokens.


### Request Body

```json
{
  "email": "john@gmail.com",
  "password": "fsh#fD23{-"
}
```



### Validation Rules

| Field    | Rules |
|----------|------|
| email    | required, valid format, max 255 chars, unique |
| password  | required, 8–64 chars, strong password required |

### Success Response

**201 Created**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```
### Error Responses

| Status | Description |
|---------|-------------|
| 400 | Validation error |
| 409 | User already exists |
| 500 | Internal server error |

---
## Log In

### Endpoint

```http
POST /auth/login
```

### Description

Authenticate an existing user and return access and refresh tokens.



### Request Body

```json
{
  "email": "john@gmail.com",
  "password": "fsh#fD23{-"
}
```

### Validation Rules

| Field    | Rules                                 |
| -------- | ------------------------------------- |
| email    | required                              |
| password | required                              |

### Success Response

**200 OK**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### Error Responses

| Status | Description               |
| ------ | ------------------------- |
| 400    | Validation error          |
| 401    | Invalid email or password |
| 500    | Internal server error     |

---
## Log Out

### Endpoint

```http
POST /auth/logout
```

### Description

Revoke the user's refresh token and log the user out of the system.

### Authorization

JWT required

### Request Body

No request body.

### Success Response

**200 OK**

```json
{
  "message": "Successfully logged out"
}
```

### Error Responses

| Status | Description           |
| ------ | --------------------- |
| 401    | Unauthorized          |
| 500    | Internal server error |

---
## Refresh Token

### Endpoint

```http
POST /auth/refresh
```


### Description

Generate a new access token and rotate the refresh token (invalidate the old one and issue a new one).

### Authorization

Not required

### Request Body

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```


### Validation Rules

| Field        | Rules                      |
| ------------ | -------------------------- |
| refreshToken | required, valid JWT format |


### Success Response

**200 OK**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```


### Behavior

* Old refresh token is invalidated
* New refresh token is issued and stored
* Client must replace stored refresh token


### Error Responses

| Status | Description                      |
| ------ | -------------------------------- |
| 400    | Validation error                 |
| 401    | Invalid or expired refresh token |
| 500    | Internal server error            |


---

# City

## Create City

### Endpoint

```http
POST /cities
```

### Description

Add new city.

### Authorization

JWT required (ADMIN only)

### Request Body
```json
{
  "name": "Moscow",
  "country": "Russia"
}
```

### Validation Rules

| Field    | Rules |
|----------|------|
| name | required, 1–50 chars |
| country    | required, 1-50 chars |

### Success Response

**201 Created**

```json
{
  "id": 1,
  "name": "Moscow",
  "country": "Russia",
  "createdAt": "2026-06-09T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token) |
| 403 | Forbidden (not ADMIN role) |
| 409 | City already exists |
| 500 | Internal server error |

---
## Get all cities

### Endpoint

```http
GET /cities
```

### Description

Get list of all cities.

### Request Body
No request body.


### Success Response

**200 OK**

```json
[
  {
    "id": 1,
    "name": "Moscow",
    "country": "Russia",
    "createdAt": "2026-06-09T12:00:00Z"
  },
  {
    "id": 2,
    "name": "London",
    "country": "UK",
    "createdAt": "2026-06-09T12:00:00Z"
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token) |
| 500 | Internal server error |

---

## Get city

### Endpoint

```http
GET /cities/{id}
```

### Description

Get a specific city by id.

### Request Body
No request body.


### Success Response

**200 OK**

```json
{
  "id": 2,
  "name": "London",
  "country": "UK",
  "createdAt": "2026-06-09T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token) |
| 404 | City not found |
| 500 | Internal server error |

---

## Update city

### Endpoint

```http
PUT /cities/{id}
```

### Description

Update a specific city by id.

### Authorization

JWT required (ADMIN only)

### Request Body

```json
{
  "name": "London",
  "country": "United Kingdom"
}
```

### Validation Rules

| Field    | Rules |
|----------|------|
| name | required, 1–50 chars |
| country    | required, 1-50 chars |

### Success Response

**200 OK**

```json
{
  "id": 2,
  "name": "London",
  "country": "United Kingdom",
  "createdAt": "2026-06-09T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | City not found |
| 409 | City already exists |
| 500 | Internal server error |

---

## Delete city

### Endpoint

```http
DELETE /cities/{id}
```

### Description

Delete a specific city by id.

### Authorization

JWT required (ADMIN only)

### Request Body
No request body.


### Success Response

**204 No Content**

No response body.

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token) |
| 403	| Forbidden (not ADMIN role) |
| 404	| City not found |
| 500 | Internal server error |

---

# Office

## Create Office

### Endpoint

```http
POST /offices
```

### Description

Add a new office.

### Authorization

JWT required (ADMIN only)

### Request Body

```json
{
  "cityId": 1,
  "name": "Moscow HQ",
  "address": "Red Square, 1",
  "latitude": 55.7539,
  "longitude": 37.6208
}
```

### Validation Rules

| Field     | Rules                          |
| --------- | ------------------------------ |
| cityId    | required, existing city ID     |
| name      | required, 1–100 chars          |
| address   | required, 1–255 chars          |
| latitude  | required, between -90 and 90   |
| longitude | required, between -180 and 180 |

### Success Response

**201 Created**

```json
{
  "id": 1,
  "cityId": 1,
  "name": "Moscow HQ",
  "address": "Red Square, 1",
  "latitude": 55.7539,
  "longitude": 37.6208,
  "createdAt": "2026-06-09T12:00:00Z"
}
```

### Error Responses

| Status | Description                 |
| ------ | --------------------------- |
| 400    | Validation error            |
| 401    | Unauthorized (no JWT token) |
| 403    | Forbidden (not ADMIN role)  |
| 404    | City not found              |
| 500    | Internal server error       |

---

## Get offices

### Endpoint

```http
GET /offices
```

### Description

Get a list of offices. Optionally filter by city.

### Authorization

JWT required

### Query Parameters

| Parameter | Rules |
|-----------|-------|
| cityId | optional, filter offices by city ID |

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 1,
    "cityId": 1,
    "name": "Moscow HQ",
    "address": "Red Square, 1",
    "latitude": 55.7539,
    "longitude": 37.6208,
    "createdAt": "2026-06-09T12:00:00Z"
  },
  {
    "id": 2,
    "cityId": 1,
    "name": "Moscow South",
    "address": "Leninsky Ave, 10",
    "latitude": 55.7000,
    "longitude": 37.5800,
    "createdAt": "2026-06-09T12:30:00Z"
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid query parameters |
| 401 | Unauthorized (no JWT token) |
| 404 | City not found (if cityId is provided but does not exist) |
| 500 | Internal server error |

---

## Get office

### Endpoint

```http
GET /offices/{id}
```

### Description

Get a specific office by id.

### Authorization

JWT required

### Request Body

No request body.

### Success Response

**200 OK**

```json
{
  "id": 1,
  "cityId": 1,
  "name": "Moscow HQ",
  "address": "Red Square, 1",
  "latitude": 55.7539,
  "longitude": 37.6208,
  "createdAt": "2026-06-09T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token) |
| 404 | Office not found |
| 500 | Internal server error |

---

## Update office

### Endpoint

```http
PUT /offices/{id}
```

### Description

Update a specific office.

### Authorization

JWT required (ADMIN only)

### Request Body

```json
{
  "cityId": 1,
  "name": "Moscow HQ Updated",
  "address": "Red Square, 1",
  "latitude": 55.7539,
  "longitude": 37.6208
}
```

### Validation Rules

| Field     | Rules                          |
| --------- | ------------------------------ |
| cityId    | required, existing city ID     |
| name      | required, 1–100 chars          |
| address   | required, 1–255 chars          |
| latitude  | required, between -90 and 90   |
| longitude | required, between -180 and 180 |

### Success Response

**200 OK**

```json
{
  "id": 1,
  "cityId": 1,
  "name": "Moscow HQ Updated",
  "address": "Red Square, 1",
  "latitude": 55.7539,
  "longitude": 37.6208,
  "createdAt": "2026-06-09T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Office not found |
| 404 | City not found |
| 500 | Internal server error |

---

## Delete office

### Endpoint

```http
DELETE /offices/{id}
```

### Description

Delete a specific office.

### Authorization

JWT required (ADMIN only)

### Request Body

No request body.

### Success Response

**204 No Content**

No response body.

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Office not found |
| 500 | Internal server error |

---

## Get places near office

### Endpoint

```http
GET /offices/{id}/nearby-places
```

### Description

Get all active places near the selected office, sorted by distance (ascending). Distance is calculated using the Haversine formula or OSRM.

### Authorization

JWT required

### Query Parameters

| Parameter | Rules |
|-----------|-------|
| placeTypeId | optional, filter by place type ID |
| maxDistanceKm | optional, positive number; return only places within this radius in km |

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 10,
    "cityId": 1,
    "placeTypeId": 2,
    "createdBy": 3,
    "name": "Coffee House",
    "address": "Tverskaya 15",
    "latitude": 55.755826,
    "longitude": 37.617300,
    "description": "Popular cafe near the office",
    "isActive": true,
    "avgRating": 4.5,
    "distanceKm": 0.35,
    "createdAt": "2026-06-09T12:00:00Z"
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid query parameters |
| 401 | Unauthorized (no JWT token) |
| 404 | Office not found |
| 500 | Internal server error |

---
# Place Type

## Get All Place Types

### Endpoint

```http
GET /place-types
```

### Description

Get a list of all available place types.

### Authorization

JWT required

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 1,
    "name": "Hotel",
    "code": "HOTEL"
  },
  {
    "id": 2,
    "name": "Cafe",
    "code": "CAFE"
  }
]
```

### Error Responses

| Status | Description                 |
| ------ | --------------------------- |
| 401    | Unauthorized (no JWT token) |
| 500    | Internal server error       |


---

# Place

## Create place

### Endpoint

```http
POST /places
```

### Description

Create a new place. Any authenticated user can add non-hotel places (cafes, parks, etc.).
Creating a place with `placeType = HOTEL` (corporate hotel list) requires ADMIN role.

### Authorization

JWT required

### Request Body

```json
{
  "cityId": 1,
  "placeTypeId": 2,
  "name": "Coffee House",
  "address": "Tverskaya 15",
  "latitude": 55.755826,
  "longitude": 37.617300,
  "description": "Popular cafe near the office"
}
```

### Validation Rules

| Field       | Rules |
|------------|-------|
| cityId | required, existing city ID |
| placeTypeId | required, existing place type ID; HOTEL type allowed for ADMIN only |
| name | required, 1–100 chars |
| address | required, 1–255 chars |
| latitude | required, between -90 and 90 |
| longitude | required, between -180 and 180 |
| description | optional, max 1000 chars |

### Success Response

**201 Created**

```json
{
  "id": 10,
  "cityId": 1,
  "placeTypeId": 2,
  "createdBy": 3,
  "name": "Coffee House",
  "address": "Tverskaya 15",
  "latitude": 55.755826,
  "longitude": 37.617300,
  "description": "Popular cafe near the office",
  "isActive": true,
  "avgRating": 0.0,
  "createdAt": "2026-06-09T12:00:00Z"
}
```
### Error Responses

| Status | Description |
|---------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token) |
| 403 | Forbidden (not ADMIN role) when placeType is HOTEL |
| 404 | City not found |
| 404 | Place type not found |
| 500 | Internal server error |

---


## Get places

### Endpoint

```http
GET /places
```

### Description

Get a list of available places with optional filtering by city, type, and distance from a reference point (office or place).

### Authorization

JWT required

### Query Parameters

| Parameter | Rules |
|-----------|-------|
| cityId | optional, filter by city ID |
| placeTypeId | optional, filter by place type ID |
| officeId | optional, use office coordinates as distance reference (requires maxDistanceKm) |
| referencePlaceId | optional, use place coordinates as distance reference (requires maxDistanceKm) |
| maxDistanceKm | optional, positive number; filter places within this radius in km from officeId or referencePlaceId |
| page | optional, zero-based page index (default: 0) |
| size | optional, page size (default: 20, max: 100) |

When `officeId` or `referencePlaceId` is used, results are sorted by `distanceKm` ascending and include the `distanceKm` field. Without a distance reference, `distanceKm` is omitted.

### Request Body

No request body.

### Success Response

**200 OK**

Example with distance filter (`?officeId=1&maxDistanceKm=5`):

```json
[
  {
    "id": 10,
    "cityId": 1,
    "placeTypeId": 2,
    "createdBy": 3,
    "name": "Coffee House",
    "address": "Tverskaya 15",
    "latitude": 55.755826,
    "longitude": 37.617300,
    "description": "Popular cafe near the office",
    "isActive": true,
    "avgRating": 4.5,
    "distanceKm": 0.35,
    "createdAt": "2026-06-09T12:00:00Z"
  },
  {
    "id": 11,
    "cityId": 1,
    "placeTypeId": 1,
    "createdBy": 4,
    "name": "Grand Hotel",
    "address": "Arbat 10",
    "latitude": 55.7512,
    "longitude": 37.6184,
    "description": "Luxury hotel in city center",
    "isActive": true,
    "avgRating": 4.8,
    "distanceKm": 1.2,
    "createdAt": "2026-06-09T12:10:00Z"
  }
]
```
### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid query parameters (e.g. wrong filters, invalid pagination values) |
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | City, office, or reference place not found (if corresponding filter is provided) |
| 500 | Internal server error |

---


## Get a place

### Endpoint

```http
GET /places/{id}
```

### Description

Get a specific place by id.

### Request Body

No request body.

### Success Response

**200 OK**

```json
{
  "id": 10,
  "cityId": 1,
  "placeTypeId": 2,
  "createdBy": 3,
  "name": "Coffee House",
  "address": "Tverskaya 15",
  "latitude": 55.755826,
  "longitude": 37.617300,
  "description": "Popular cafe near the office",
  "isActive": true,
  "avgRating": 4.5,
  "createdAt": "2026-06-09T12:00:00Z"
}
```
### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | Place not found |
| 500 | Internal server error |

---

## Get places near place

### Endpoint

```http
GET /places/{id}/nearby-places
```

### Description

Get all active places near the selected place (e.g. a hotel), sorted by distance (ascending). Distance is calculated using the Haversine formula or OSRM.

### Authorization

JWT required

### Query Parameters

| Parameter | Rules |
|-----------|-------|
| placeTypeId | optional, filter by place type ID |
| maxDistanceKm | optional, positive number; return only places within this radius in km |

The reference place itself is excluded from the result.

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 12,
    "cityId": 1,
    "placeTypeId": 2,
    "createdBy": 5,
    "name": "Bakery",
    "address": "Arbat 12",
    "latitude": 55.7515,
    "longitude": 37.6180,
    "description": "Cozy bakery",
    "isActive": true,
    "avgRating": 4.2,
    "distanceKm": 0.18,
    "createdAt": "2026-06-09T13:00:00Z"
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid query parameters |
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | Place not found |
| 500 | Internal server error |

---
## Update a place

### Endpoint

```http
PUT /places/{id}
```

### Description

Update a specific place by id. ADMIN only.

### Authorization

JWT required (ADMIN only)

### Request Body

```json
{
"cityId": 1,
"placeTypeId": 2,
"name": "Updated Coffee House",
"address": "Tverskaya 15",
"latitude": 55.755826,
"longitude": 37.617300,
"description": "Popular cafe near the office"
}
```
### Validation Rules

| Field       | Rules |
|------------|-------|
| cityId | required, existing city ID |
| placeTypeId | required, existing place type ID; changing to HOTEL type allowed for ADMIN only |
| name | required, 1–100 chars |
| address | required, 1–255 chars |
| latitude | required, between -90 and 90 |
| longitude | required, between -180 and 180 |
| description | optional, max 1000 chars |


### Success Response

**200 OK**

```json
{
  "id": 10,
  "cityId": 1,
  "placeTypeId": 2,
  "createdBy": 3,
  "name": "Coffee House",
  "address": "Tverskaya 15",
  "latitude": 55.755826,
  "longitude": 37.617300,
  "description": "Popular cafe near the office",
  "isActive": true,
  "avgRating": 4.5,
  "createdAt": "2026-06-09T12:00:00Z"
}
```
### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Place not found |
| 500 | Internal server error |

---

## Delete a place

### Endpoint

```http
DELETE /places/{id}
```

### Description

Delete a specific place by id. ADMIN only.

### Authorization

JWT required (ADMIN only)

### Request Body

No request body.

### Success Response

**204 No Content**

No response body.
### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid id format |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Place not found |
| 500 | Internal server error |

---

# Hotel
## Create hotel details

### Endpoint
```http
POST /places/{id}/hotel-details
```

### Description

Create hotel details for a corporate hotel place (only allowed if placeType = HOTEL). ADMIN only.

### Authorization

JWT required (ADMIN only)

### Request Body
```json
{
  "starRating": 5,
  "phone": "+123456789",
  "website": "https://hotel.com",
  "roomCount": 120
}
```

### Validation Rules

| Field | Rules |
|------|------|
| starRating | required, 1–5 |
| phone | optional |
| website | optional |
| roomCount | required, > 0 |

### Success Response

**201 Created**

```json
{
  "id": 1,
  "placeId": 10,
  "starRating": 5,
  "phone": "+123456789",
  "website": "https://hotel.com",
  "roomCount": 120
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error or place is not HOTEL |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Place not found |
| 409 | Hotel details already exist |
| 500 | Internal server error |

--- 
## Get hotel details

### Endpoint

```http
GET /places/{id}/hotel-details
```

### Authorization

JWT required
### Description

Get hotel details for a specific place (only if placeType = HOTEL).

### Request Body

No request body.

### Success Response

**200 OK**

```
{
  "id": 1,
  "placeId": 10,
  "starRating": 5,
  "phone": "+123456789",
  "website": "https://hotel.com",
  "roomCount": 120
}
```
### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | Hotel details not found |
| 500 | Internal server error |

---

## Update hotel details

### Endpoint

```http
PUT /places/{id}/hotel-details
```

### Description

Update hotel details for a corporate hotel place (only if placeType = HOTEL). ADMIN only.

### Authorization

JWT required (ADMIN only)

### Request Body

```json
{
  "starRating": 4,
  "phone": "+123456789",
  "website": "https://hotel-updated.com",
  "roomCount": 100
}
```

### Validation Rules

| Field | Rules |
|------|------|
| starRating | required, 1–5 |
| phone | optional |
| website | optional |
| roomCount | required, > 0 |

### Success Response

**200 OK**

```json
{
  "id": 1,
  "placeId": 10,
  "starRating": 4,
  "phone": "+123456789",
  "website": "https://hotel-updated.com",
  "roomCount": 100
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error or place is not HOTEL |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Hotel details not found |
| 500 | Internal server error |

---
# Booking

## Create booking

### Endpoint
```
POST /bookings
```
### Description

Create a new booking for a hotel place. A user can book a hotel for a specific date range.


### Request Body
```
{
  "placeId": 10,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05"
}
```
---

### Validation Rules

| Field     | Rules |
|-----------|------|
| placeId   | required, must exist, must be HOTEL |
| checkIn   | required, valid date |
| checkOut  | required, valid date, must be after checkIn |

---

### Success Response

**201 Created**
```
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05",
  "status": "PENDING",
  "createdAt": "2026-06-10T12:00:00Z",
  "updatedAt": "2026-06-10T12:00:00Z"
}
```
---

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error (invalid dates, checkOut before checkIn, or place is not a HOTEL) |
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | Place not found |
| 500 | Internal server error |

---

## Get bookings

### Endpoint

```http
GET /bookings
```

### Description

Get a list of bookings. Regular users receive only their own bookings. ADMIN users receive all bookings and can filter by user.

### Authorization

JWT required

### Query Parameters

| Parameter | Rules |
|-----------|-------|
| status | optional, one of: PENDING, CONFIRMED, CANCELLED |
| userId | optional, ADMIN only; filter bookings by user ID |
| page | optional, zero-based page index (default: 0) |
| size | optional, page size (default: 20, max: 100) |

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 1,
    "userId": 3,
    "placeId": 10,
    "checkIn": "2026-07-01",
    "checkOut": "2026-07-05",
    "status": "PENDING",
    "createdAt": "2026-06-10T12:00:00Z",
    "updatedAt": "2026-06-10T12:00:00Z"
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid query parameters |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (userId filter used by non-ADMIN) |
| 500 | Internal server error |

---

## Get booking

### Endpoint

```http
GET /bookings/{id}
```

### Description

Get a specific booking by id. Accessible by the booking owner or ADMIN.

### Authorization

JWT required

### Request Body

No request body.

### Success Response

**200 OK**

```json
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05",
  "status": "PENDING",
  "createdAt": "2026-06-10T12:00:00Z",
  "updatedAt": "2026-06-10T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not booking owner nor ADMIN) |
| 404 | Booking not found |
| 500 | Internal server error |

---
## Update booking

### Endpoint

PUT /bookings/{id}

### Description

Update an existing booking (dates or place change if needed).

---

### Authorization

JWT required

---

### Request Body

{
  "placeId": 10,
  "checkIn": "2026-07-02",
  "checkOut": "2026-07-06"
}

---

### Validation Rules

| Field     | Rules |
|-----------|------|
| placeId   | required, must exist, must be HOTEL |
| checkIn   | required, valid date |
| checkOut  | required, valid date, must be after checkIn |

---

### Success Response

**200 OK**
```
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "checkIn": "2026-07-02",
  "checkOut": "2026-07-06",
  "status": "PENDING",
  "createdAt": "2026-06-10T12:00:00Z",
  "updatedAt": "2026-06-10T12:00:00Z"
}
```
---

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not owner of booking) |
| 404 | Booking not found |
| 500 | Internal server error |

---

## Update booking status (Admin)

### Endpoint
```
PATCH /bookings/{id}/status
```
### Description

Allows an admin to change the status of a booking.
Used for confirming or cancelling bookings.



### Request Body
```
{
  "status": "CONFIRMED"
}
```

### Validation Rules

| Field  | Rules |
|--------|------|
| status | required, one of: PENDING, CONFIRMED, CANCELLED |

---

### Success Response

**200 OK**
```
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05",
  "status": "CONFIRMED",
  "createdAt": "2026-06-10T12:00:00Z",
  "updatedAt": "2026-06-10T12:30:00Z"
}
```
---

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error (invalid status value) |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not ADMIN) |
| 404 | Booking not found |
| 500 | Internal server error |

---
# Review

## Create review

### Endpoint
```
POST /reviews
```
### Description

Create a new review for a place. One user can create only one review per place.


### Request Body
```
{
  "placeId": 10,
  "rating": 5,
  "comment": "Amazing place, really enjoyed it!"
}
```
### Validation Rules

| Field   | Rules |
|---------|------|
| placeId | required, must exist |
| rating  | required, integer 1–5 |
| comment | optional, max 1000 chars |


### Success Response

**201 Created**
```
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "rating": 5,
  "comment": "Amazing place, really enjoyed it!",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | Place not found |
| 409 | Review already exists for this place |
| 500 | Internal server error |

---

## Get reviews

### Endpoint

```http
GET /reviews
```

### Description

Get a list of reviews. Used to display reviews on place cards and in the admin panel.

### Authorization

JWT required

### Query Parameters

| Parameter | Rules |
|-----------|-------|
| placeId | optional, filter reviews by place ID |
| userId | optional, filter reviews by user ID (ADMIN only) |
| page | optional, zero-based page index (default: 0) |
| size | optional, page size (default: 20, max: 100) |

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 1,
    "userId": 3,
    "placeId": 10,
    "rating": 5,
    "comment": "Amazing place, really enjoyed it!",
    "createdAt": "2026-06-10T12:00:00Z"
  },
  {
    "id": 2,
    "userId": 4,
    "placeId": 10,
    "rating": 4,
    "comment": "Good coffee",
    "createdAt": "2026-06-10T14:00:00Z"
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Invalid query parameters |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (userId filter used by non-ADMIN) |
| 404 | Place not found (if placeId is provided but does not exist) |
| 500 | Internal server error |

---

## Update review

### Endpoint
```
PUT /reviews/{id}
```
### Description

Update an existing review. Only the owner of the review can update it.


### Request Body
```
{
  "rating": 4,
  "comment": "Updated review text"
}
```

### Validation Rules

| Field   | Rules |
|---------|------|
| rating  | optional, integer 1–5 |
| comment | optional, max 1000 chars |

At least one of `rating` or `comment` must be provided. An empty `{}` request body is rejected with **400**.

### Success Response

**200 OK**
```
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "rating": 4,
  "comment": "Updated review text",
  "createdAt": "2026-06-10T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error (empty body, invalid rating, or comment too long) |
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not review owner) |
| 404 | Review not found |
| 500 | Internal server error |

---

## Delete review

### Endpoint

```http
DELETE /reviews/{id}
```

### Description

Delete a review. ADMIN only.

### Authorization

JWT required (ADMIN only)

### Request Body

No request body.

### Success Response

**204 No Content**

No response body.

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not ADMIN role) |
| 404 | Review not found |
| 500 | Internal server error |

---
# Wishlist

## Add to wishlist

### Endpoint
```
POST /wishlists
```
### Description

Add a place to the user's wishlist. Each user can add a place only once.

### Request Body
```
{
  "placeId": 10
}
```


### Validation Rules

| Field   | Rules |
|---------|------|
| placeId | required, must exist |


### Success Response

**201 Created**
```
{
  "id": 1,
  "userId": 3,
  "placeId": 10,
  "addedAt": "2026-06-10T12:00:00Z"
}
```

### Error Responses

| Status | Description |
|--------|-------------|
| 400 | Validation error |
| 401 | Unauthorized (no JWT token or invalid token) |
| 404 | Place not found |
| 409 | Place already in wishlist |
| 500 | Internal server error |

---

## Get wishlist

### Endpoint

```http
GET /wishlists
```

### Description

Get the current user's wishlist (list of places marked as "want to visit").

### Authorization

JWT required

### Request Body

No request body.

### Success Response

**200 OK**

```json
[
  {
    "id": 1,
    "userId": 3,
    "placeId": 10,
    "addedAt": "2026-06-10T12:00:00Z",
    "place": {
      "id": 10,
      "cityId": 1,
      "placeTypeId": 2,
      "name": "Coffee House",
      "address": "Tverskaya 15",
      "latitude": 55.755826,
      "longitude": 37.617300,
      "avgRating": 4.5,
      "isActive": true
    }
  }
]
```

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token or invalid token) |
| 500 | Internal server error |

---

## Remove from wishlist

### Endpoint

```http
DELETE /wishlists/{id}
```

### Description

Remove a place from the current user's wishlist by wishlist entry id.

### Authorization

JWT required

### Request Body

No request body.

### Success Response

**204 No Content**

No response body.

### Error Responses

| Status | Description |
|--------|-------------|
| 401 | Unauthorized (no JWT token or invalid token) |
| 403 | Forbidden (not wishlist entry owner) |
| 404 | Wishlist entry not found |
| 500 | Internal server error |


