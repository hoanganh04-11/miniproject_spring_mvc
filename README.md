# HomeSmartIoT

Hệ thống nhà thông minh xây dựng bằng **Spring Boot + JSP + MySQL**, hỗ trợ quản lý phòng/cảm biến/thiết bị, phân quyền người dùng và tích hợp **MQTT** để giao tiếp thiết bị IoT.

## 1) Công nghệ sử dụng

- Java 17
- Spring Boot 3.5.12
- Spring MVC + JSP (Tomcat Jasper + JSTL)
- Spring Data JPA (Hibernate)
- Spring Security
- Spring Session JDBC
- MySQL
- MQTT client (Eclipse Paho)

## 2) Cấu trúc chính

```text
src/main/java/com/smarthome/iot
|- config/            # Security, MQTT, WebMvc, SuccessHandler
|- controller/
|  |- admin/          # Trang quản trị
|  |- client/         # Trang người dùng
|  |- api/            # REST API (MQTT, device)
|- domain/            # Entity: User, Role, Room, Sensor, Device, SensorData, Alert
|- repository/        # JPA repositories
|- service/           # Business logic

src/main/webapp/WEB-INF/view
|- admin/             # JSP admin
|- client/            # JSP client

src/main/resources
|- application.properties
```

## 3) Chức năng hiện có

### Admin (`/admin/**`, role `ADMIN`)

- Dashboard tổng quan
- CRUD User
- CRUD Room
- CRUD Sensor
- CRUD Device (đã hỗ trợ gán phòng cho device)

### Client

- Trang chủ: `/`
- Đăng ký: `/register`
- Đăng nhập: `/login`
- Danh sách phòng: `/client/room-list`
- Chi tiết phòng: `/client/room/{id}`
- Danh sách cảm biến: `/client/sensor-list`
- Chi tiết cảm biến: `/client/sensor/{id}`
- Danh sách thiết bị: `/client/device`

### Phân quyền điều khiển thiết bị

- Guest: chỉ xem thông tin.
- User/Admin: được bật/tắt thiết bị.
- API điều khiển `POST /client/device/{id}/toggle` đã được chặn quyền ở backend (chỉ `USER`/`ADMIN`).

## 4) MQTT trong dự án

### Thành phần MQTT

- `MqttConfig`: tạo client, kết nối broker, subscribe các topic.
- `MqttService`: publish command/payload.
- `MqttTopicCatalogService`: catalog topic mẫu.
- `MqttApiController`: API test MQTT.
- `DeviceService.toggleStatus(...)`: vừa cập nhật DB vừa publish command MQTT.

### API MQTT test nhanh

- `GET /api/v1/mqtt/status` -> trạng thái kết nối MQTT
- `GET /api/v1/mqtt/topics` -> danh sách topic mẫu
- `POST /api/v1/mqtt/publish` -> publish thủ công
- `POST /api/v1/mqtt/device-command` -> gửi lệnh theo `deviceId`

## 5) Cấu hình `application.properties`

File: `src/main/resources/application.properties`

### Database

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smarthome
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
```

### MQTT

```properties
mqtt.enabled=true
mqtt.broker.url=
mqtt.client.id=test
mqtt.username=
mqtt.password=
```

Lưu ý:
- `mqtt.broker.url` **bắt buộc** phải điền nếu muốn MQTT hoạt động.
- Ví dụ local broker: `mqtt.broker.url=tcp://localhost:1883`

## 6) Chuẩn bị dữ liệu ban đầu

Tạo database:

```sql
CREATE DATABASE smarthome CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Ngoài ra cần có role trong bảng `roles`:
- `ADMIN`
- `USER`

(đăng ký tài khoản mới hiện đang gán role `USER` trong `HomePageController`).

## 7) Cách chạy dự án

### Build

```bash
mvnw.cmd -DskipTests compile
```

### Run

```bash
mvnw.cmd spring-boot:run
```

### Truy cập

- Client: `http://localhost:8080/`
- Login: `http://localhost:8080/login`
- Register: `http://localhost:8080/register`
- Admin: `http://localhost:8080/admin`

## 8) Kiểm tra nhanh MQTT

1. Đảm bảo đã cấu hình `mqtt.broker.url`.
2. Chạy app và gọi:
   - `GET /api/v1/mqtt/status` (mong đợi `connected = true`)
3. Gửi lệnh test:
   - `POST /api/v1/mqtt/device-command?deviceId=1&command=ON`
4. Kiểm tra log server để thấy dòng connect/subscribe/publish.

## 9) Lưu ý kỹ thuật

- App dùng `spring-session-jdbc`, có cấu hình `spring.session.jdbc.initialize-schema=always`.
- Nếu MQTT không kết nối, app vẫn chạy bình thường (chỉ mất chức năng publish/subscribe MQTT).
- Project hiện dùng JSP, không dùng Thymeleaf.

## 10) Định hướng phát triển

- Seed dữ liệu role/user mặc định tự động khi khởi động.
- Viết test integration cho security và MQTT.
- Bổ sung dashboard realtime và lịch sử biểu đồ dữ liệu cảm biến.
- Cấu hình profile riêng cho môi trường dev/staging/prod.