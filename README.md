# Billioyo (빌려요)
---

## 📌 프로젝트 개요

중고 물품 및 생활용품을 사용자 간에 안전하고 편리하게 대여할 수 있도록 설계된 위치 기반 웹 플랫폼입니다. 사용자 위치를 기준으로 주변 물품을 조회하고, 채팅을 통해 직접 소통하며 거래를 진행할 수 있도록 구성하였습니다.

---

## ✨ 주요 기능

- **회원가입/로그인 및 보안 인증**
  - 이메일 인증, 비밀번호 재설정, JWT 기반 로그인/토큰 재발급
  - Spring Security 기반 권한 분리(일반 사용자/관리자)
- **대여 게시글 등록 및 탐색**
  - 대여 물품 게시글 작성/수정/삭제 및 카테고리 기반 조회
  - 이미지 업로드(S3 연동)와 게시글 상세 조회
  - Spring AI(Vertex AI Gemini) 기반 이미지 분석을 통해 게시글 제목 및 설명 자동 생성 기능 제공
- **위치 기반 대여글 검색**
  - 위도/경도 + 거리 조건으로 주변 대여글 조회
  - 사용자의 위치 정보를 기준으로 근거리 게시글 탐색
- **대여 신청/주문/결제 프로세스**
  - 주문 생성 및 상태 관리
  - Iamport 연동 결제 처리 및 결제 이력 관리
- **실시간 채팅(WebSocket)**
  - 대여자/임차자 간 채팅방 생성
  - 메시지 송수신 및 채팅 내역 조회
- **커뮤니티 기능**
  - 게시글/댓글 작성, 조회, 삭제
  - 좋아요(상호작용) 및 검색/필터링 지원
  - 위치 기반 커뮤니티 게시글 탐색 지원
- **신고 및 관리자 기능**
  - 게시글/사용자 신고 접수
  - 관리자 페이지에서 신고 목록 확인 및 처리
- **마이페이지/활동 내역 관리**
  - 내 주문/내 게시글/활동 내역 조회
  - 사용자 정보 관리 및 서비스 이용 이력 확인
- **정산 기능**
  - 거래 기반 정산 생성/조회
  - 정산 항목(아이템) 단위 관리
- **알림 기능**
  - Firebase FCM 기반 푸시 알림 전송
  - 이벤트 기반 사용자 알림 처리

---

## 🛠 Tech Stack

### 🖥 Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge)
![Spring WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white)
![Spring Mail](https://img.shields.io/badge/Spring_Mail-6DB33F?style=for-the-badge)

### 🗄 Database & Cache
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### 🔐 Authentication
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

### ☁ Cloud & External
![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/Amazon_RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase_Admin-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Iamport](https://img.shields.io/badge/Iamport_Payment-FF6B6B?style=for-the-badge)
![Vertex AI](https://img.shields.io/badge/Vertex_AI_Gemini-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)

### 📊 Monitoring
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)

### 🧪 Testing
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Spring Security Test](https://img.shields.io/badge/Spring_Security_Test-6DB33F?style=for-the-badge)
![Mockito](https://img.shields.io/badge/Mockito-FF6F00?style=for-the-badge)
![k6](https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white)

---

## 4) 디렉터리 구조

```text
billioyo-backend/
├── src/main/java/com/itemrental/billioyo
│   ├── domain/        # 사용자/대여/주문/결제/채팅/커뮤니티/정산 등
│   └── global/        # 보안, 공통 응답, 설정, 예외 처리, 인프라 연동
├── src/main/resources
│   ├── application.yaml
│   └── templates/
├── src/test/java
├── build.gradle
└── docker-compose.yml
```

---
