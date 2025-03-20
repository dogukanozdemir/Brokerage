# Brokerage Platform API

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-3.4.3-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License">
</p>

A robust, secure, and scalable brokerage platform API built with Spring Boot. This platform enables financial trading with comprehensive user authentication, asset management, order processing, and administrative functionality.

## ✨ Features

- **🔒 Secure Authentication** - JWT-based user authentication and role-based access control
- **💰 Asset Management** - Track and manage multiple asset types and balances
- **📈 Trading** - Create, view, and cancel buy/sell orders
- **🤝 Order Matching** - Administrative functionality to match compatible orders
- **🔄 Transaction Safety** - Ensures fund availability for transactions
- **🧪 Extensive Testing** - Comprehensive test coverage
- **🚀 Easy Deployment** - Docker support for quick setup

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Docker Setup](#docker-setup)
- [Usage Guide](#-usage-guide)
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [License](#-license)

## 🛠 Technology Stack

- **Backend Framework**: Spring Boot 3.4.3
- **Language**: Java 21
- **Database**: H2 (in-memory)
- **Migration**: Flyway
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Gradle
- **Code Quality**: Spotless (automated code formatting)
- **API Testing**: Postman collection provided

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Gradle (or use the included wrapper)
- Docker (optional, for containerized deployment)

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/dogukanozdemir/Brokerage.git
   cd Brokerage
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

1. Pull the Docker image
   ```bash
   docker pull dogukanozdemirr/ing-app:latest
   ```

2. Run the container
   ```bash
   docker run -it -p 8080:8080 dogukanozdemirr/ing-app:latest
   ```

## 📘 Usage Guide

1. **Registration**: Create a new account with your desired role (USER/ADMIN)
2. **Authentication**: Log in to receive your JWT token
3. **Add Assets**: Deposit funds (TRY) or other assets into your account
4. **Trading**: Place buy/sell orders for supported assets
5. **Tracking**: View your orders and asset balances
6. **Order Management**: Cancel pending orders if needed

A comprehensive Postman collection is included in the project root (`Brokarage.postman_collection.json`) to help you explore all API endpoints.

## 📚 API Documentation

### Authentication

#### Register User
- **URL**: `/v1/register`
- **Method**: `POST`
- **Description**: Create a new user account
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
- **Description**: Authenticate a user and receive a JWT token
- **Request Body**:
  ```json
  {
    "username": "dogukan",
    "password": "ing123"
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
- **Description**: Place a new buy or sell order
- **Request Body**:
  ```json
  {
    "customerId": "1",
    "assetName": "ING",
    "orderSide": "BUY",
    "size": 10,
    "price": 100
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
    "creatDate": "2023-01-01T12:00:00Z"
  }
  ```

#### Get Orders
- **URL**: `/v1/orders`
- **Method**: `GET`
- **Description**: Retrieve orders by customer with optional date filtering
- **Query Parameters**:
  - `customerId`: Required, number
  - `startDate`: Optional, ISO date format (YYYY-MM-DD)
  - `endDate`: Optional, ISO date format (YYYY-MM-DD)
- **Response**: `200 OK`
  ```json
  [
    {
      "id": "1",
      "customerId": "1",
      "assetName": "ING",
      "orderSide": "BUY",
      "size": 10,
      "price": 100,
      "status": "PENDING",
      "creatDate": "2023-01-01T12:00:00Z"
    }
  ]
  ```

#### Cancel Order
- **URL**: `/v1/orders/{id}`
- **Method**: `DELETE`
- **Description**: Cancel a pending order
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
    "creatDate": "2023-01-01T12:00:00Z"
  }
  ```

### Assets

#### Get Assets by Customer ID
- **URL**: `/v1/assets`
- **Method**: `GET`
- **Description**: Retrieve all assets for a customer
- **Query Parameters**:
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
- **Description**: Add or increase an asset in a customer's portfolio
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
- **Description**: Match compatible orders (admin only)
- **Request Body**:
  ```json
  {
    "orderIds": ["1", "2"],
    "matchAll": false
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
      "creatDate": "2023-01-01T12:00:00Z"
    },
    {
      "id": "2",
      "customerId": "2",
      "assetName": "ING",
      "orderSide": "SELL",
      "size": 100,
      "price": 10,
      "status": "MATCHED",
      "creatDate": "2023-01-01T12:00:00Z"
    }
  ]
  ```

## 🛠 Development

### Build and Test Commands
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Test: `./gradlew test`
- Format code: `./gradlew spotlessApply`
- Check formatting: `./gradlew spotlessCheck`

### Project Structure
The project follows a standard Spring Boot application structure:
- `controller`: REST API endpoints
- `service`: Business logic implementation
- `repository`: Database access interfaces
- `entity`: Database models
- `dto`: Data transfer objects
- `auth`: Authentication and security
- `config`: Application configuration
- `exception`: Custom exceptions and error handling

## 📄 License

This project is licensed under the MIT License - see [here](https://opensource.org/license/mit) for details.