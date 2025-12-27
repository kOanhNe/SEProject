# SoftwareEngineeringProject
## Ecommerce Website of Shoe Store
### 
This project is a Shoe Store E-Commerce Website developed as part of a Software Engineering course.
The system allows customers to browse footwear products, view detailed information, manage shopping carts, and place orders through a clean and intuitive web interface.

The project focuses on applying software engineering principles, including layered architecture, MVC pattern, and clean separation of concerns between backend, frontend, and data access layers.

## Project Objectives
1. Build a functional e-commerce system for selling shoes online
2. Apply Spring Boot & MVC architecture in a real project
3. Design a relational database with proper entity relationships
4. Create a lightweight and fast frontend using HTML + CSS only
5. Prepare a scalable foundation for future commercial features

## Core Feature
1. Home page with featured products

2. Product listing & product detail pages

3. Product browsing and viewing

4. Shopping cart management

5. Order placement

6. User authentication (login / register)

7. Database-driven product & order management

## Technology Stack
# Backend
Programing Language: Java
Spring Framework Ecosystem:
- Spring Boot
- Spring MVC
- Spring Data JPA
# Frontend
- HTML5 (Markup Language)
- CSS3 (Style Sheet Language)
- JavaScript (Client-side Scripting Language)
- Thymeleaf (Server-side Template Engine)
# Database
- PostgreSQL (hosted on Supabase)
## System Architecture
- The system follows a layered architecture:
- Controller Layer – Handles HTTP requests & responses
- Service Layer – Business logic
- Repository Layer – Database access using JPA
- View Layer – HTML templates rendered by Spring MVC
## Project Struture
```text
src/main/java/com/ecommerce/shoestore
 ├── shoes
 │   ├── Shoes.java
 │   ├── ShoesController.java
 │   ├── ShoesService.java
 │   ├── ShoesRepository.java
 │   └── dto/
 ├── order
 │   ├── Order.java
 │   ├── OrderService.java
 │   ├── OrderController.java
 │   └── OrderRepository.java
 |___ ....................
 src/main/resources/
 ├── templates/                    
 │   ├── index.html                
 │   ├── shoes-list.html         
 │   ├── shoes-detail.html       
 │   ├── cart.html                 
 │   ├── order.html               
 │   └── login.html                
 │
 └── static/                       
     ├── css/
     │   ├── style.css             
     │   ├── product.css           
     │
     └── img/                   

```
---
# Homepage

# Product List

# Product Detail

# Cart Page

# Order Page



## How to run the project 
```bash
mvn spring-boot:run
```
---
## Include Credits (Team Members)
| No. | Member Name | Role |
|---|------------------------------|-------------------|
| 1 | **Chung Thị Ái Nguyên** | Project Manager |
| 2 | **Lê Huỳnh Kiều Oanh** | Member |
| 3 | **Lê Ngô Thanh Hoa** | Member |
| 4 | **Nguyễn Thị Việt Hoa** | Member |
| 5 | **Nguyễn Thị Hồng Nhung** | Member |
| 6 | **Nguyễn Thị Ngọc Thùy** | Member |
| 7 | **Nguyễn Lê Việt Quốc** | Member |
| 8 | **Phạm Trần Quốc Bảo** | Member |
| 9 | **Nguyễn Lê Hoàng Kiệt** | Member |
| 10 | **Vương Đức Huy** | Member |
## 
---

## License
This project is developed for educational purposes as part of a university course.
