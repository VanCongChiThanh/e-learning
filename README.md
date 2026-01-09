# E-Learning Platform

Nền tảng học trực tuyến toàn diện được xây dựng bằng Spring Boot với kiến trúc microservices, cung cấp các tính năng quản lý khóa học, thanh toán, thông báo và nhiều hơn nữa.

## Mục lục

- [Tổng quan](#tổng-quan)
- [Kiến trúc](#kiến-trúc)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt](#cài-đặt)
- [Cấu hình](#cấu-hình)
- [Chạy ứng dụng](#chạy-ứng-dụng)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Docker](#docker)
- [Tính năng](#tính-năng)

## Tổng quan

Hệ thống E-Learning là một nền tảng học trực tuyến hiện đại, cho phép:
- Quản lý khóa học và nội dung học tập
- Đăng ký và theo dõi tiến độ học tập
- Thanh toán trực tuyến qua PayOS
- Thông báo real-time qua WebSocket và Firebase
- Xác thực OAuth2 (Google, Facebook)
- Quản lý file và video
- Hệ thống đánh giá và bình luận
- Chứng chỉ hoàn thành khóa học

## Kiến trúc

Dự án sử dụng kiến trúc **Modular Monolith** với các module độc lập:

```
e-learning/
├── web/                      # Module chính - REST API Gateway
├── common-service/           # Shared utilities, models, configurations
├── security-service/         # Authentication & Authorization
├── user-service/            # User management
├── course-service/          # Course management
├── enrollment-service/      # Enrollment & progress tracking
├── commerce-service/        # Payment & orders
├── notification-service/    # Push notifications
├── email-service/          # Email services
└── file-service/           # File & video handling
```

## Công nghệ sử dụng

### Backend Framework
- **Spring Boot 2.7.5**
- **Java 17**
- **Maven** - Build tool

### Database
- **PostgreSQL** - Primary database
- **Redis** - Caching layer
- **Hibernate JPA** - ORM

### Security
- **Spring Security**
- **OAuth2** (Google, Facebook)
- **JWT Token** - Authentication

### Messaging & Queue
- **RabbitMQ** - Message broker
- **WebSocket** - Real-time communication

### Storage & Media
- **AWS S3** - File storage
- **Video processing**

### Payment Integration
- **PayOS** - Payment gateway

### Notification
- **Firebase Cloud Messaging** - Push notifications
- **Spring Mail** - Email service

### Background Jobs
- **JobRunr** - Background job processing

### API Documentation
- **Swagger/Springfox 3.0.0** - API documentation

### Other Libraries
- **MapStruct** - Object mapping
- **Lombok** - Reduce boilerplate code
- **QueryDSL** - Type-safe queries
- **Spring Filter** - Advanced filtering
- **Apache POI** - Excel processing
- **QRGen** - QR code generation
- **Flying Saucer** - PDF generation
- **Jsoup** - HTML parsing

### Testing
- **JUnit** - Unit testing
- **Maven Surefire** - Unit test runner
- **Maven Failsafe** - Integration test runner
- **JaCoCo** - Code coverage (60% minimum)

## Cấu trúc dự án

### Modules chính

#### 1. **web** - API Gateway
Module chính chứa tất cả REST API endpoints:
- `/api/auth/*` - Authentication endpoints
- `/api/courses/*` - Course management
- `/api/users/*` - User management
- `/api/enrollments/*` - Enrollment management
- `/api/payments/*` - Payment processing
- `/api/notifications/*` - Notification management
- `/api/media/*` - Media handling

#### 2. **security-service**
- JWT token generation & validation
- OAuth2 login (Google, Facebook)
- User authentication & authorization
- Custom UserDetailsService

#### 3. **user-service**
- User profile management
- Instructor profiles
- User bank accounts
- Career planning
- Instructor applications
- Account deletion

#### 4. **course-service**
- Course CRUD operations
- Sections & lectures
- Resources & materials
- Tags & categories
- Reviews & ratings
- Comments
- Notes
- Events

#### 5. **enrollment-service**
- Course enrollment
- Progress tracking
- Quizzes & questions
- Assignments
- Code exercises
- Submissions
- Certificates (PDF generation)

#### 6. **commerce-service**
- Shopping cart
- Order management
- Payment processing (PayOS integration)
- Revenue tracking

#### 7. **notification-service**
- Push notifications via Firebase
- Device token management
- Real-time notifications

#### 8. **email-service**
- Email sending via SMTP
- Template-based emails (Thymeleaf)

#### 9. **file-service**
- File upload/download
- Video handling
- AWS S3 integration

#### 10. **common-service**
- Shared DTOs, entities, models
- Common configurations
- Utilities & helpers

## Yêu cầu hệ thống

- **Java**: 17 hoặc cao hơn
- **Maven**: 3.6+
- **PostgreSQL**: 12+
- **Redis**: 6+
- **RabbitMQ**: 3.8+
- **AWS Account** (cho S3)
- **Firebase Account** (cho push notifications)
- **PayOS Account** (cho payment)

## Cài đặt

### 1. Clone repository

```bash
git clone https://github.com/VanCongChiThanh/e-learning.git
cd e-learning
```

### 2. Cài đặt PostgreSQL

```bash
# Tạo database
createdb elearning_db
```

### 3. Cài đặt Redis

```bash
# Ubuntu/Debian
sudo apt-get install redis-server
sudo systemctl start redis

# macOS
brew install redis
brew services start redis

# Windows
# Download từ https://github.com/microsoftarchive/redis/releases
```

### 4. Cài đặt RabbitMQ

```bash
# Ubuntu/Debian
sudo apt-get install rabbitmq-server
sudo systemctl start rabbitmq-server

# macOS
brew install rabbitmq
brew services start rabbitmq

# Windows
# Download từ https://www.rabbitmq.com/install-windows.html
```

## Cấu hình

### 1. Tạo file cấu hình

Tạo file `web/src/main/resources/application-dev.yml`:

```yaml
server:
  port: 8105

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/elearning_db
    username: your_username
    password: your_password
    hikari:
      max-lifetime: 60000

  rabbitmq:
    host: localhost
    username: guest
    password: guest

  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

origin-patterns: "*"
dev-patterns: "http://localhost:3000"
web-url: "http://localhost:3000"

e-learning-mail:
  domain: yourdomain.com

app:
  oauth2:
    providers:
      google:
        clientId: your_google_client_id
        clientSecret: your_google_client_secret
        redirectUri: http://localhost:3000/oauth2/callback/google
      facebook:
        clientId: your_facebook_client_id
        clientSecret: your_facebook_client_secret
        redirectUri: http://localhost:8105/oauth2/callback/facebook

aws:
  accessKey: your_aws_access_key
  secretKey: your_aws_secret_key
  region: us-east-1
  s3:
    bucket: your-bucket-name

payos:
  client-id: your_payos_client_id
  api-key: your_payos_api_key
  checksum-key: your_payos_checksum_key
  partner-code: your_partner_code
  sandbox: true
  default-expiration-minutes: 15
  return-url: http://localhost:8105/api/payments/return/payos
  cancel-url: http://localhost:8105/api/payments/cancel/payos
  webhook-url: http://localhost:8105/api/payments/webhook/payos

firebase:
  service-account-path: /path/to/firebase-service-account.json
```

### 2. Biến môi trường

Tạo file `.env` ở thư mục gốc:

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/elearning_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Tokens
TOKEN_SECRET=your_token_secret_min_32_characters
REFRESH_TOKEN_SECRET=your_refresh_token_secret_min_32_characters

# Email
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_app_password

# OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
FACEBOOK_CLIENT_ID=your_facebook_client_id
FACEBOOK_CLIENT_SECRET=your_facebook_client_secret

# AWS
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key

# PayOS
PAYOS_CLIENT_ID=your_payos_client_id
PAYOS_API_KEY=your_payos_api_key
PAYOS_CHECKSUM_KEY=your_payos_checksum_key
PAYOS_PARTNER_CODE=your_partner_code
```

## Chạy ứng dụng

### Development Mode

```bash
# Build toàn bộ project
mvn clean install -DskipTests

# Chạy ứng dụng
cd web
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Hoặc chạy từ IDE (IntelliJ IDEA, Eclipse):
1. Import project as Maven project
2. Chạy class `com.pbl.elearning.web.WebApplication`
3. Chọn profile: `dev`

### Production Mode

```bash
# Build
mvn clean package -DskipTests

# Chạy
java -jar web/target/web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Truy cập ứng dụng

- **API Base URL**: `http://localhost:8105/api`
- **Swagger UI**: `http://localhost:8105/swagger-ui/`
- **JobRunr Dashboard**: `http://localhost:8105/dashboard` (nếu enabled)

## API Documentation

API documentation được tạo tự động bằng Swagger/Springfox.

Truy cập: `http://localhost:8105/swagger-ui/`

### Các endpoint chính:

#### Authentication
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/refresh` - Refresh token
- `GET /api/auth/oauth2/google` - OAuth2 Google login
- `GET /api/auth/oauth2/facebook` - OAuth2 Facebook login

#### Courses
- `GET /api/courses` - Danh sách khóa học
- `POST /api/courses` - Tạo khóa học mới
- `GET /api/courses/{id}` - Chi tiết khóa học
- `PUT /api/courses/{id}` - Cập nhật khóa học
- `DELETE /api/courses/{id}` - Xóa khóa học

#### Enrollments
- `POST /api/enrollments` - Đăng ký khóa học
- `GET /api/enrollments` - Danh sách đăng ký
- `GET /api/enrollments/progress` - Tiến độ học tập

#### Payments
- `POST /api/payments/create` - Tạo thanh toán
- `GET /api/payments/return/payos` - PayOS return URL
- `POST /api/payments/webhook/payos` - PayOS webhook

## Testing

### Chạy Unit Tests

```bash
mvn test
```

### Chạy Integration Tests

```bash
mvn verify
```

### Kiểm tra Code Coverage

```bash
mvn clean verify

# Report được tạo tại: target/site/jacoco/index.html
```

Coverage tối thiểu yêu cầu: **60%**

## Docker

### Build Docker Image

```bash
docker build -t e-learning-api .
```

### Run với Docker Compose

Tạo file `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: elearning_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

  app:
    build: .
    ports:
      - "8105:8105"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/elearning_db
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      TOKEN_SECRET: your_token_secret_min_32_characters
      REFRESH_TOKEN_SECRET: your_refresh_token_secret_min_32_characters
    depends_on:
      - postgres
      - redis
      - rabbitmq

volumes:
  postgres_data:
```

Chạy:

```bash
docker-compose up -d
```

## Tính năng

### 1. Quản lý người dùng
- Đăng ký/Đăng nhập (Email, Google, Facebook)
- Quản lý profile
- Quản lý tài khoản ngân hàng
- Đăng ký làm giảng viên
- Career planning

### 2. Quản lý khóa học
- CRUD khóa học
- Sections và Lectures
- Tài nguyên học tập (PDF, Video, Documents)
- Tags và Categories
- Đánh giá và Review
- Bình luận
- Ghi chú cá nhân

### 3. Học tập
- Đăng ký khóa học
- Theo dõi tiến độ
- Quiz và Bài tập
- Code exercises
- Nộp bài
- Chứng chỉ hoàn thành (PDF)

### 4. Thương mại
- Giỏ hàng
- Thanh toán trực tuyến (PayOS)
- Quản lý đơn hàng
- Theo dõi doanh thu

### 5. Thông báo
- Push notification (Firebase)
- Email notification
- Real-time notification (WebSocket)

### 6. File & Media
- Upload file lên AWS S3
- Xử lý video
- Quản lý tài nguyên

## Đóng góp

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

### Coding Standards

- Sử dụng Java 17 features
- Follow Spring Boot best practices
- Viết unit tests cho business logic
- Code coverage tối thiểu 60%
- Sử dụng Lombok để giảm boilerplate
- Follow RESTful API conventions

## License

Dự án này thuộc về nhóm phát triển PBL E-Learnaing.

## Liên hệ

- Repository: [https://github.com/VanCongChiThanh/e-learning](https://github.com/VanCongChiThanh/e-learning)
- Issues: [https://github.com/VanCongChiThanh/e-learning/issues](https://github.com/VanCongChiThanh/e-learning/issues)

## Tài liệu bổ sung

- [PayOS Integration Guide](commerce-service/PAYOS_INTEGRATION_GUIDE.md)

---

**Phát triển bởi**: Nhóm PBL E-Learning
**Version**: 1.0-SNAPSHOT
**Spring Boot**: 2.7.5
**Java**: 17
