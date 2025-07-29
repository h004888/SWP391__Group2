# 🌟 OLearning - Online Learning Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019-blue.svg)](https://www.microsoft.com/en-us/sql-server)
[![Maven](https://img.shields.io/badge/Maven-3.9.5+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive backend service for online course management platform built with Spring Boot, featuring multi-role authentication, payment processing, AI-powered chatbot, and complete course lifecycle management. Built with modern web technologies and Vietnamese payment gateways, it offers a robust solution for educational institutions and instructors.

## 👥 Team Members

| # | Name | Student ID |
|---|------|------------|
| 1 | Nguyễn Duy Thắng | HE186677 |
| 2 | Pham Xuân Hùng | HE186353 |
| 3 | Nguyễn Quang Tiền | HE186694 |
| 4 | Phạm Đình Trường | HE186668 |
| 5 | Vũ Đức Nghĩa | HE182095 |

## ✨ Key Features

### 🔐 Authentication & Security
- **Multi-role Authentication** - Support for Admin, Instructor, and User roles
- **Google OAuth2 Integration** - Social login with Google accounts
- **Session Management** - Secure session handling with timeout controls
- **Role-based Authorization** - Granular access control for different user types
- **Password Reset** - Email-based password recovery system

### 👥 User Management
- **User Registration** - Email verification and profile management
- **Profile Management** - Avatar upload, personal information, and skill tracking
- **Instructor Requests** - Admin approval system for instructor applications
- **User Status Control** - Account blocking/unblocking and warning system
- **Coin System** - Internal virtual currency for course purchases

### 📚 Course Management
- **Complete Course Lifecycle** - Create, edit, publish, and manage courses
- **Chapter & Lesson Structure** - Hierarchical content organization
- **Video Content Support** - Cloudinary integration for video storage
- **Course Reviews & Ratings** - Student feedback and rating system
- **Progress Tracking** - Lesson completion and learning analytics
- **Course Maintenance** - Monthly fee system for instructors

### 💳 Payment Processing
- **VNPay Integration** - Vietnamese payment gateway for domestic cards
- **SePay QR Banking** - QR code payment system for bank transfers
- **Internal Coin System** - Virtual currency for course purchases
- **Invoice Generation** - PDF invoice creation and email delivery
- **Voucher System** - Discount codes with expiry management
- **Payment History** - Complete transaction records with detailed tracking

### 🤖 AI-Powered Chatbot
- **Google AI Integration** - Powered by Gemini 1.5 Flash
- **Real-time Chat** - Interactive AI assistant for course support
- **Context Awareness** - Remembers conversation history
- **Course Search** - AI-powered course discovery
- **User Authentication** - Secure chat access for logged-in users

### 📊 Analytics & Reporting
- **Dashboard Analytics** - Revenue, enrollment, and course performance
- **Export Capabilities** - Excel/PDF report generation
- **Instructor Analytics** - Course-specific performance metrics
- **Student Progress Tracking** - Learning analytics and completion rates

### 🔔 Notification System
- **Real-time Notifications** - User activity and system notifications
- **Email Notifications** - Automated email alerts
- **Course Announcements** - Instructor-to-student communication
- **Payment Confirmations** - Transaction status notifications

### 🛡️ Content Moderation
- **Report System** - User-generated content reporting
- **Content Hiding** - Automatic content moderation
- **Warning System** - Progressive user discipline
- **Admin Response System** - Admin feedback to user reports

## 🛠 Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.4.6 |
| **Security** | Spring Security + OAuth2 | 6.x |
| **Database** | Microsoft SQL Server | 2019+ |
| **Template Engine** | Thymeleaf | 3.x |
| **Build Tool** | Maven | 3.9.5+ |
| **Cloud Storage** | Cloudinary | 2.0.0 |
| **Email Service** | Gmail SMTP | - |
| **Payment Gateway** | VNPay + SePay | - |
| **AI Service** | Google AI (Gemini) | 1.5 Flash |
| **PDF Generation** | iText7 + Flying Saucer | 7.x |
| **Excel Export** | Apache POI | 5.x |
| **Mapping** | MapStruct | 1.5.5 |
| **Validation** | Bean Validation | 3.x |

## 📋 Prerequisites

Before running this application, make sure you have:

- ☕ **Java 21** or higher
- 🛠️ **Maven 3.9.5** or higher
- 🗄️ **Microsoft SQL Server 2019** or higher
- 🔑 **Google OAuth Credentials** (for social login)
- 💳 **VNPay Account** (for payment processing)
- ☁️ **Cloudinary Account** (for file storage)
- 🤖 **Google AI API Key** (for chatbot functionality)

## 🚀 Quick Start

### Local Development

```bash
# 1. Clone the repository
git clone <repository-url>
cd OLearning

# 2. Set up SQL Server
# Create database: OLearning_New
# Run the SQL script: src/database/olearning.sql

# 3. Configure application.properties
# Update database connection, API keys, and other settings

# 4. Build and run
mvn clean install
mvn spring-boot:run
```

## ⚙️ Configuration

### Environment Variables

Create a `.env` file in the project root:

```properties
# Database Configuration
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=OLearning_New
DB_USERNAME=sa
DB_PASSWORD=123

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_oauth_client_id
GOOGLE_CLIENT_SECRET=your_google_oauth_client_secret

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Google AI Configuration
GOOGLE_AI_API_KEY=your_google_ai_api_key

# Payment Configuration
VNPAY_TMN_CODE=your_vnpay_tmn_code
VNPAY_HASH_SECRET=your_vnpay_hash_secret
SEPAY_ACCOUNT_NUMBER=your_sepay_account
SEPAY_BANK_CODE=your_bank_code

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

### Application Configuration

```properties
# Server Configuration
server.port=8080
server.servlet.session.timeout=30m

# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=OLearning_New
spring.datasource.username=sa
spring.datasource.password=123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# File Upload
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Thymeleaf Configuration
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.cache=false
```

## 📁 Project Structure

```
OLearning/
├── src/
│   ├── main/
│   │   ├── java/com/OLearning/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # MVC Controllers
│   │   │   │   ├── adminDashBoard/
│   │   │   │   ├── api/
│   │   │   │   ├── chat/
│   │   │   │   ├── homePage/
│   │   │   │   ├── instructorDashBoard/
│   │   │   │   ├── login/
│   │   │   │   └── userDashboard/
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── mapper/          # Object Mappers
│   │   │   ├── repository/      # Data Access Layer
│   │   │   ├── security/        # Security Components
│   │   │   ├── service/         # Business Logic
│   │   │   └── util/            # Utility Classes
│   │   └── resources/
│   │       ├── static/          # Static Resources
│   │       ├── templates/       # Thymeleaf Templates
│   │       └── application.properties
│   └── test/                    # Test Classes
├── database/                    # SQL Scripts
├── pom.xml                     # Maven Configuration
└── README.md                   # This File
```

## 🔧 Key Components

### Authentication System
- **CustomUserDetailsService** - Custom user authentication
- **CustomOAuth2UserService** - Google OAuth2 integration
- **SecurityConfig** - Spring Security configuration
- **Session Management** - Multi-session support

### Payment Processing
- **VNPayService** - VNPay payment gateway integration
- **VietQRService** - SePay QR code generation
- **OrdersService** - Order management and processing
- **CoinTransaction** - Internal coin system

### AI Chatbot
- **ChatService** - AI chat functionality
- **GoogleAIConfig** - Google AI API configuration
- **ChatController** - Chat API endpoints
- **ChatMessage** - Chat history management

### Course Management
- **CourseService** - Course CRUD operations
- **ChapterService** - Chapter management
- **LessonService** - Lesson content management
- **EnrollmentService** - Student enrollment

## 🚀 Deployment

### Production Deployment

1. **Database Setup**
   ```sql
   -- Run the database script
   USE master;
   CREATE DATABASE OLearning_New;
   USE OLearning_New;
   -- Execute src/database/olearning.sql
   ```

2. **Environment Configuration**
   ```bash
   # Set production environment variables
   export SPRING_PROFILES_ACTIVE=prod
   export DB_URL=jdbc:sqlserver://your-db-server:1433;databaseName=OLearning_New
   export GOOGLE_CLIENT_ID=your_production_client_id
   export GOOGLE_AI_API_KEY=your_production_ai_key
   ```

3. **Build and Deploy**
   ```bash
   mvn clean package -DskipTests
   java -jar target/OLearning-0.0.1-SNAPSHOT.jar
   ```


## 🔒 Security Features

- **CSRF Protection** - Cross-site request forgery prevention
- **Session Security** - Secure session management
- **Input Validation** - Comprehensive input sanitization
- **SQL Injection Prevention** - Parameterized queries
- **XSS Protection** - Cross-site scripting prevention
- **File Upload Security** - Secure file handling

## 📊 Monitoring & Logging

- **Application Logs** - Comprehensive logging system
- **Error Tracking** - Global exception handling
- **Performance Monitoring** - Request/response tracking
- **Database Monitoring** - Query performance tracking


