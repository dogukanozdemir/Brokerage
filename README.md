# Brokerage Platform API

## Table of Contents
- [Overview](#overview)
- [Technology](#technology)
- [Usage](#usage)
- [API Specification](#api-specification)
- [Build and Run](#build-and-run)
- [License](#license)

## Overview

This is a robust and secure Brokerage Platform API built with Java Spring Boot, designed to handle core brokerage functionalities including user authentication, asset management, order processing, and administrative tasks.

The API is implemented using Java Spring Boot, and utilizes a H2 database for storing data. The API supports the following features:

- Registering New Customer
- Logining in Customer
- List all orders of the customer
- Placing a new order
- Cancelling an order
- Get All Assets of customer
- Add an Asset
- Match orders (ADMIN)

## Technology

This project is built with **Spring 3.4.3** and **Java 21**, using **H2** as the database and **Flyway** for database migrations.

Additionally, the project enforces **Spotless** as a code formatter — meaning any unformatted code will cause the build to fail.
To maintain code quality and consistency, this formatting and build verification can also be integrated as a GitHub Action, ensuring that every pull request (PR) is automatically checked and cannot be merged unless the project builds successfully.

## Usage

When you first launch the application, you'll need to register as a new customer and select your preferred role.
Once registered, you can deposit funds — either TRY or other assets — into your account using the Add Asset endpoint. This will allow you to start buying and selling stocks.

You can explore and test all available endpoints using the provided Postman collection, which is available [here]

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
- **URL**: `/v1/assets?customerId=1`
- **Method**: `GET`
- **Request Parameters**:
  - `customerId`: Required, Customer ID
- **Response**: `200 OK`
  ```json
  [
    {
      "customerId": "1",
      "assetName": "ING",
      "size": 100,
      "usableSize": 50
    },
    {
    "customerId": "1",
    "assetName": "TRY",
    "size": 1000,
    "usableSize": 500
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
      "orderIds" : ["1", "2"],
      "matchAll" : false
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
## Build and Run

### Local Setup

#### Build and Run Locally
1. Clone the repository
   ```bash
   git clone https://github.com/dogukanozdemir/Brokerage.git
   cd Brokarage
   ```

2. Build the project
   ```bash
   ./gradlew clean build
   ```

3. Run the application
   ```bash
   ./gradlew bootRun
   ```

The API will be available at `http://localhost:8080`

### Docker Setup

#### Run with Docker

1. Pull the Docker image
   ```bash
   docker pull dogukanozdemirr/ing-app:latest
   ```

2. Run the container
   ```bash
   docker run -it -p 8080:8080 dogukanozdemirr/ing-app:latest
   ```

## License

This project is licensed under the MIT License - see (here)[https://opensource.org/license/mit] for details:

