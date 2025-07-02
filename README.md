# Pipebank Order System

## 프로젝트 개요
Pipebank 회사의 수주/발주 관리 시스템 백엔드 프로젝트입니다.

## 기술 스택
- **Spring Boot**: 3.2.0
- **Java**: 17
- **Gradle**: 8.5
- **기타**: Lombok, Jackson

## 실행 방법

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd be
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

또는 IDE에서 `PipebankOrderSystemApplication.java`의 main 메소드를 실행

### 3. 동작 확인
- 애플리케이션 실행 후 브라우저에서 `http://localhost:8080/api/health` 접속
- 응답이 정상적으로 나오면 실행 성공

## API 엔드포인트
- `GET /api/health` - 애플리케이션 상태 체크

## 향후 개발 예정
- MySQL 데이터베이스 연동
- Redis 캐시 연동
- JWT 기반 보안 시스템
- 수주/발주 관리 기능 "# be" 
