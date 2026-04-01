 SalesSavvy Backend
 Overview

SalesSavvy Backend is a scalable and modular e-commerce application built using Java Spring Boot. 
It provides secure REST APIs to handle user management, product catalog, cart operations, order processing, and payment integration.

🛠️ Tech Stack
Java
Spring Boot
Spring Security (JWT Authentication)
REST APIs
MySQL
Hibernate / JPA
Payment Gateway Integration (Razorpay)

⚙️ Features
🔐 Authentication & Authorization
JWT-based authentication
Role-based access control (Admin & Customer)

👤 User Management
User registration & login
Profile management

🛍️ Product Service
Add, update, delete products (Admin)
View product catalog (Users)

🛒 Cart Service
Add/remove items from cart
Update cart quantity

📦 Order Management
Place orders
Track order status
Order history
💳 Payment Integration
Razorpay 
Secure payment processing


🧩 Architecture

The backend follows a modular architecture with independent services:

Authentication Service
User Service
Product Service
Cart Service
Order Service
Payment Service

🔒 Security
Password hashing using BCrypt
JWT token-based session handling
Input validation & data protection

🗄️ Database
Relational database design using MySQL
Optimized schema for:
Users
Products
Orders
Cart
Payments

API Endpoints
| Method | Endpoint             | Description      |
| ------ | -------------------- | ---------------- |
| POST   | `/api/users/register` | Register user    |
| POST   | `/api/auth/login`    | Login user       |
| GET    | `/api/products`      | Get all products |
| POST   | `/api/cart/add`      | Add to cart      |
| POST   | `/api/order/create`  | Place order      |
| POST   | `/api/payment`       | Process payment  |
