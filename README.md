# SmartCode - Online Coding Platform    
**SmartCode** là nền tảng giáo dục và luyện lập trình all-in-one, kết hợp trải nghiệm học tập tương tác và hệ thống chấm bài thi đấu thuật toán. Hệ thống được xây dựng trên kiến trúc Microservices hiện đại, đảm bảo khả năng mở rộng và chịu tải cao.

---
## Tính năng chính 
### 1. Học tương tác
* **Lộ trình bài bản:** Các khóa học (Courses) được chia thành Chương (Modules) và Bài học (Lessons).
* **Live Playground:** Học viên đọc lý thuyết bên trái và gõ code thực hành ngay bên phải.
* **Instant Feedback:** Chạy code và nhận phản hồi tức thì mà không cần cài đặt môi trường.

### 2. Competitive programming 
* **Problem Bank:** Kho bài tập thuật toán đa dạng (Easy, Medium, Hard).
* **Online Judge:** Hệ thống chấm bài tự động với Testcase ẩn, đo thời gian (Time Limit) và bộ nhớ (Memory Limit) chính xác.
* **Contests:** Tổ chức các cuộc thi lập trình thời gian thực, bảng xếp hạng (Leaderboard) trực tiếp.

### 3. Sandboxed Execution
* Code của người dùng được thực thi trong các **Docker Container** cô lập.
* Ngăn chặn các hành vi nguy hiểm (Fork bomb, Memory leak, Network access).
* Hỗ trợ đa ngôn ngữ: **Java, C/C++, Python**.

--- 
## Kiến trúc hệ thống
Hệ thống bao gồm các Microservices giao tiếp qua REST API và RabbitMQ:

| Service | Tên cấu hình          | Port | Vai trò & Trách nhiệm |
| :--- |:----------------------| :--- | :--- |
| **Gateway** | `api-gateway`         | **8080** | Cổng giao tiếp duy nhất, định tuyến request, xác thực (Auth). |
| **Identity** | `auth-service`        | **8081** | Quản lý đăng ký/đăng nhập, JWT Token (MySQL). |
| **Core** | `user-services`       | **8085** | Quản lý hồ sơ người dùng, thống kê thành tích (MongoDB). |
| **LMS** | `course-service`      | **8084** | Quản lý Khóa học, Bài giảng, Tiến độ học tập (LMS Logic). |
| **OJ** | `content-service`     | **8083** | Quản lý Đề bài (Problems), Testcase, Cuộc thi (Contest). |
| **Engine** | `submission-services` | **8082** | Điều phối việc nộp bài, đẩy job vào hàng đợi (RabbitMQ). |
| **Worker** | `runtime-service`     | **8086** | Thực thi code trong Docker, đo lường tài nguyên. |
| **Infra** | `discovery/config`    | **8761/8888** | Service Discovery (Eureka) và Config Server. |

---
## Workflows
### Kịch bản A: Học viên làm bài học (Playground Mode)
1. **FE** gọi `POST /submissions` với `mode="PLAYGROUND"` và `lessonId`.
2. **Runtime Service** chạy code với Input mẫu của bài học.
3. Trả về kết quả Output ngay lập tức để học viên biết mình làm đúng hay sai.

### Kịch bản B: Thí sinh nộp bài thi (Contest Mode)
1. **FE** gọi `POST /submissions` với `mode="SUBMIT"` và `problemId`.
2. **Submission Service** lấy danh sách **Hidden Testcases** từ `content-service`.
3. **Runtime Service** chạy code lần lượt với từng testcase.
4. Tính toán Time/Memory tối đa.
5. Cập nhật trạng thái `ACCEPTED` hoặc `WRONG ANSWER` vào Database và Leaderboard.

---
## Setup
### 1. Yêu cầu 
* Java 21+, Docker, Maven
### 2. Khởi tạo 
Sử dụng Docker Compose để xây dựng các hạ tầng 
```bash
    docker compose up -d
    #Build MySQL (3306), MongoDB (27017), Redis(), RabbitMQ (5672)
```
