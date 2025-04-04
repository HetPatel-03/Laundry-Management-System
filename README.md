# LaundaryManagementSystem

# Laundry Management System

A comprehensive Spring Boot application for managing laundry services with multi-role access, scheduling, and reporting capabilities. The system allows tracking of laundry orders, customer information, staff assignments, pickups, deliveries, and generates analytics reports.

## Features

### Admin Features
- Manage users (customers and staff)
- Manage service categories with pricing 
- Assign laundry orders to staff
- Generate revenue, order analytics, and staff productivity reports
- Dashboard with key metrics

### Customer Features
- View order history and current status
- Schedule laundry pickups and deliveries
- Submit feedback/reviews for completed orders
- Manage personal profile

### Staff Features
- View and manage assigned tasks
- Update order status through the workflow
- Record pickups and deliveries
- View delivery schedules

## Technologies Used

- Java 11
- Spring Boot 2.7.14
- Spring Data JPA
- MySQL Database
- Maven
- Docker & Docker Compose
- Swagger/OpenAPI

## Getting Started

### Prerequisites

- Docker and Docker Compose installed on your system
- Git (optional, for cloning the repository)

### Running with Docker Compose

1. Clone the repository (or download the source code)
   ```bash
   git clone https://github.com/yourusername/laundry-management-system.git
   cd laundry-management-system
   ```

2. Start the application and MySQL database with Docker Compose
   ```bash
   docker-compose up -d
   ```

3. The application will be available at http://localhost:8080
   - API documentation: http://localhost:8080/swagger-ui.html
   - Default admin credentials: admin@laundry.com / password123
   - Default staff credentials: staff1@laundry.com / password123

### Running Locally (without Docker)

1. Make sure you have Java 11 and Maven installed
2. Configure MySQL database (update application.properties if needed)
3. Build the application
   ```bash
   mvn clean install
   ```
4. Run the application
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### User Management

| Method | URL                            | Description                        |
|--------|--------------------------------|------------------------------------|
| GET    | /api/users                     | Get all users                      |
| GET    | /api/users/{id}                | Get user by ID                     |
| POST   | /api/users                     | Create a new user                  |
| PUT    | /api/users/{id}                | Update a user                      |
| DELETE | /api/users/{id}                | Delete a user                      |
| GET    | /api/users/role/{role}         | Get users by role                  |
| PATCH  | /api/users/{id}/activate       | Activate a user                    |
| PATCH  | /api/users/{id}/deactivate     | Deactivate a user                  |

### Service Category Management

| Method | URL                              | Description                        |
|--------|---------------------------------|------------------------------------|
| GET    | /api/services                   | Get all service categories         |
| GET    | /api/services/active            | Get active service categories      |
| GET    | /api/services/{id}              | Get service category by ID         |
| POST   | /api/services                   | Create a new service category      |
| PUT    | /api/services/{id}              | Update a service category          |
| DELETE | /api/services/{id}              | Delete a service category          |
| PATCH  | /api/services/{id}/activate     | Activate a service category        |
| PATCH  | /api/services/{id}/deactivate   | Deactivate a service category      |

### Laundry Order Management

| Method | URL                                   | Description                                |
|--------|---------------------------------------|--------------------------------------------|
| GET    | /api/laundry                          | Get all laundry orders                     |
| GET    | /api/laundry/{id}                     | Get laundry order by ID                    |
| POST   | /api/laundry                          | Create a new laundry order                 |
| PUT    | /api/laundry/{id}                     | Update a laundry order                     |
| DELETE | /api/laundry/{id}                     | Delete a laundry order                     |
| GET    | /api/laundry/customer/{customerId}    | Get orders by customer                     |
| GET    | /api/laundry/staff/{staffId}          | Get orders by assigned staff               |
| GET    | /api/laundry/status/{status}          | Get orders by status                       |
| GET    | /api/laundry/service/{serviceId}      | Get orders by service category             |
| PATCH  | /api/laundry/{id}/status              | Update order status                        |
| PATCH  | /api/laundry/{id}/assign-staff/{staffId} | Assign staff to an order                |
| PATCH  | /api/laundry/{id}/schedule-pickup     | Schedule a pickup                          |
| PATCH  | /api/laundry/{id}/schedule-delivery   | Schedule a delivery                        |
| PATCH  | /api/laundry/{id}/record-pickup       | Record a pickup                            |
| PATCH  | /api/laundry/{id}/record-delivery     | Record a delivery                          |
| PATCH  | /api/laundry/{id}/review              | Submit review for an order                 |
| PATCH  | /api/laundry/{id}/payment             | Record payment for an order                |

### Delivery Schedule Management

| Method | URL                                   | Description                                |
|--------|---------------------------------------|--------------------------------------------|
| GET    | /api/schedules                        | Get all delivery schedules                 |
| GET    | /api/schedules/{id}                   | Get schedule by ID                         |
| POST   | /api/schedules                        | Create a new schedule                      |
| PUT    | /api/schedules/{id}                   | Update a schedule                          |
| DELETE | /api/schedules/{id}                   | Delete a schedule                          |
| GET    | /api/schedules/staff/{staffId}        | Get schedules by staff                     |
| GET    | /api/schedules/laundry/{laundryId}    | Get schedules by laundry order             |
| GET    | /api/schedules/type/{type}            | Get schedules by type (pickup/delivery)    |
| GET    | /api/schedules/date-range             | Get schedules within a date range          |
| PATCH  | /api/schedules/{id}/status            | Update schedule status                     |
| PATCH  | /api/schedules/{id}/complete          | Mark schedule as completed                 |
| PATCH  | /api/schedules/{id}/cancel            | Cancel a schedule                          |
| PATCH  | /api/schedules/{id}/reschedule        | Reschedule a delivery                      |

### Report & Analytics Endpoints

| Method | URL                                   | Description                                |
|--------|---------------------------------------|--------------------------------------------|
| GET    | /api/laundry/dashboard                | Get dashboard statistics                   |
| GET    | /api/laundry/reports/staff-productivity | Get staff productivity report            |
| GET    | /api/laundry/reports/revenue          | Get revenue report                         |
| GET    | /api/laundry/reports/customer/{id}    | Get customer order report                  |
| GET    | /api/laundry/reports/service-category | Get service category report                |

## Sample Request Bodies

### Create User
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "1234567890",
  "password": "password123",
  "role": "CUSTOMER",
  "address": "123 Main St, City"
}
```

### Create Service Category
```json
{
  "name": "Premium Cleaning",
  "description": "Premium cleaning service for delicate fabrics",
  "basePrice": 15.00,
  "pricePerKg": 5.00,
  "estimatedTimeInHours": 48
}
```

### Create Laundry Order
```json
{
  "customerId": 1,
  "serviceCategoryId": 2,
  "numberOfItems": 5,
  "weight": 3.5,
  "specialInstructions": "Please use mild detergent",
  "pickupAddress": "123 Main St, City",
  "deliveryAddress": "123 Main St, City"
}
```

### Create Delivery Schedule
```json
{
  "staffId": 2,
  "laundryId": 1,
  "scheduledTime": "2025-04-05T14:00:00",
  "type": "PICKUP",
  "address": "123 Main St, City",
  "contactName": "John Doe",
  "contactPhone": "1234567890",
  "notes": "Call before arrival"
}
```
>>>>>>> ed56870 (uploaded inital files)
# -Laundry-Management-System
