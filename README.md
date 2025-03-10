# Brokerage Platform API

This is a robust and secure Brokerage Platform API built with Java Spring Boot, designed to handle core brokerage functionalities including user authentication, asset management, order processing, and administrative tasks.


## API Specification

### Authentication

#### Register User
- **URL**: `/v1/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "dogukan",
    "password": "ing123",
    "role": "USER"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "username": "dogukan",
    "token": "jwttoken"
  }
  ```

#### Login
- **URL**: `/v1/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "dogukan",
    "password": "ing123",
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "username": "dogukan",
    "token": "jwttoken"
  }
  ```

### Orders

#### Create Order
- **URL**: `/v1/orders`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
      "customerId" : "1",
      "assetName" : "ING",
      "orderSide" : "BUY",
      "size" : 10,
      "price" : 100
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "id": "1",
    "customerId": "1",
    "assetName": "ING",
    "orderSide": "BUY",
    "size": 10,
    "price": 100,
    "status": "PENDING",
    "creatDate": "string (ISO date time)"
  }
  ```

#### Get Orders
- **URL**: `/v1/orders`
- **Method**: `GET`
- **Query Parameters**:
  - `customerId`: Required, number
  - `startDate`: Optional, ISO date format (YYYY-MM-DD)
  - `endDate`: Optional, ISO date format (YYYY-MM-DD)
- **Response**: `200 OK`
  ```json
  [
    {
        "customerId" : "1",
        "assetName" : "ING",
        "orderSide" : "BUY",
        "size" : 10,
        "price" : 100
    }
  ]
  ```

#### Cancel Order
- **URL**: `/v1/orders/{id}`
- **Method**: `DELETE`
- **Path Parameters**:
  - `id`: Required, Order ID
- **Response**: `200 OK`
  ```json
  {
    "id": "1",
    "customerId": "1",
    "assetName": "ING",
    "orderSide": "BUY",
    "size": 10,
    "price": 100,
    "status": "CANCELLED",
    "creatDate": "string (ISO date time)"
  }
  ```

### Assets

#### Get Assets by Customer ID
- **URL**: `/v1/assets/{customerId}`
- **Method**: `GET`
- **Path Parameters**:
  - `customerId`: Required, Customer ID
- **Response**: `200 OK`
  ```json
  [
    {
      "customerId": "1",
      "assetName": "ING",
      "size": 100,
      "usableSize": 50
    }
  ]
  ```

#### Add Asset
- **URL**: `/v1/assets`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "customerId": "1",
    "assetName": "ING",
    "size": 10
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "customerId": "1",
    "assetName": "ING",
    "size": 20,
    "usableSize": 15
  }
  ```

### Admin Operations

#### Match Orders
- **URL**: `/v1/admin/match-orders`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    orderIds : ["1" , "2"],
    matchAll : false
  }
  ```
- **Response**: `200 OK`
  ```json
  [
    {
      "id": "1",
      "customerId": "1",
      "assetName": "ING",
      "orderSide": "BUY",
      "size": 100,
      "price": 10,
      "status": "MATCHED",
      "creatDate": "string (ISO date time)"
    },
    {
      "id": "2",
      "customerId": "1",
      "assetName": "ING",
      "orderSide": "BUY",
      "size": 100,
      "price": 10,
      "status": "MATCHED",
      "creatDate": "string (ISO date time)"
    }
  ]
  ```
