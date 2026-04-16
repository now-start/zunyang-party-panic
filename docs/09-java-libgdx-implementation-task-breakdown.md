# Java LibGDX Implementation Task Breakdown

- Version: 0.1
- Date: 2026-04-16
- Project id: `zunyang-party-panic`
- Based on: `docs/08-prototype-scope-and-mvp-build-plan.md`
- Goal of this document: `Java + libGDX + Windows 단일 클라이언트` 기준으로 실제 구현 방향과 작업 단위를 정리한다.

## 1. 이번 단계에서 확정된 기술 전제

사용자 기준으로 현재 확정된 구현 전제는 아래와 같다.

- 구현 언어는 `Java`
- 클라이언트 프레임워크는 `libGDX`
- 실행 대상은 `Windows`
- 게임은 `단일 Windows 클라이언트` 안에서 동작
- 스트리머 조작, 게임 화면, 채팅 입력 수신까지 모두 클라이언트 내부에서 처리
- 채팅 관련 구현은 CHZZK 공식 개발자 문서를 기준으로 설계
- 비주얼 톤은 로컬 `asset/` 폴더 그림체를 참고

즉, 이제부터는 웹 앱이나 Spring 서버가 아니라 `데스크톱 방송용 툴형 게임 클라이언트`로 본다.

## 2. 현재 레포 기준 현실 점검

현재 레포는 아직 `Spring Boot` 스타터 상태다.

- `build.gradle`은 Spring Boot 플러그인 기반
- 모듈 구조도 libGDX 구조가 아님
- 현재 소스는 데스크톱 게임 클라이언트 구조와 맞지 않음

따라서 구현 시작 전에 먼저 해야 할 일은 기능 추가가 아니라 `프로젝트 뼈대 전환`이다.

이 작업은 사실상 `Task 0`이다.

## 3. 권장 프로젝트 구조

`libGDX` 공식 문서 기준으로 데스크톱 앱은 `core` 게임 코드와 `desktop launcher`를 분리하는 구조가 가장 자연스럽다. 데스크톱 엔트리포인트는 `Lwjgl3ApplicationConfiguration`과 `Lwjgl3Application`을 사용하는 `DesktopLauncher` 또는 `Lwjgl3Launcher` 형태가 일반적이다.

권장 구조는 아래와 같다.

```text
zunyang-party-panic/
  core/
    src/main/java/
      game/
        DemoGame.java
        GameStateMachine.java
        screens/
        ui/
        domain/
        content/
        chat/
        assets/
    src/main/resources/
  lwjgl3/
    src/main/java/
      launcher/
        Lwjgl3Launcher.java
  asset/
  docs/
```

### 역할 분리

- `core`: 게임 로직, 상태 머신, 화면, UI, 채팅 이벤트 처리
- `lwjgl3`: Windows 데스크톱 실행기
- `asset`: 원본 그림과 리소스 참고본
- `docs`: 지금까지 작성한 기획 문서

## 4. libGDX 기준 구현 방향

`libGDX` 기준으로 이번 프로젝트에서 핵심이 될 영역은 아래 세 가지다.

### 1. 데스크톱 실행기

Windows 전용 실행기는 `LWJGL3` 백엔드를 쓰는 것이 기본이다.

- 창 크기 설정
- vsync
- 초기 해상도
- 창 아이콘
- 시작 화면 진입

### 2. 메인 게임 루프와 상태 머신

이미 `07-ui-state-flow.md`에서 상태 전이는 정리되어 있으므로, 이걸 `libGDX` 애플리케이션 상태 머신으로 옮기면 된다.

- IDLE
- COUNTDOWN
- CHOICE
- TROUBLE
- FINALE
- RESULT

### 3. UI/HUD 구성

`libGDX`에서 HUD와 버튼 패널은 `scene2d`와 `scene2d.ui`를 쓰는 쪽이 가장 안정적이다.

이 프로젝트에서 `scene2d`를 써야 하는 이유는 아래와 같다.

- 상단 HUD
- 하단 명령어 바
- 우측 스트리머 버튼 패널
- 상태별 버튼 활성/비활성
- 레이아웃 관리

즉, 중앙 파티 무대는 자유 렌더링, HUD와 운영 패널은 `scene2d.ui`가 적절하다.

## 5. CHZZK 채팅 수신 구조

CHZZK 공식 문서 기준으로 채팅 수신은 대략 아래 순서로 설계된다.

1. 인증
2. 세션 생성
3. 소켓 연결
4. 세션 키 획득
5. 채팅 이벤트 구독
6. `CHAT` 이벤트 수신

### 공식 문서 기준 핵심 포인트

- Authorization 문서:
  - 인증 코드 요청 및 발급
  - Access Token 발급
  - Access Token 갱신
  - Access Token 삭제
- Session 문서:
  - 세션 생성(클라이언트)
  - 세션 생성(유저)
  - 소켓 연결 URL 발급
  - 세션 연결 완료 후 `sessionKey` 수신
  - `POST /open/v1/sessions/events/subscribe/chat` 로 채팅 이벤트 구독
  - 채팅 발생 시 `CHAT` 이벤트 메시지 수신

### 채팅 이벤트에서 필요한 데이터

공식 문서 기준 `CHAT` 이벤트 메시지에는 아래 정보가 포함된다.

- `channelId`
- `senderChannelId`
- `profile.nickname`
- `profile.userRoleCode`
- `content`
- `emojis`
- `messageTime`

게임 입장에서는 이 중 아래가 우선순위가 높다.

- `senderChannelId`
- `profile.nickname`
- `content`
- `messageTime`

즉, 게임 입력 변환용으로는 `누가`, `무슨 명령어를`, `언제 보냈는지`가 핵심이다.

## 6. 클라이언트 단독 처리와 보안 리스크

여기가 가장 중요한 기술 판단 지점이다.

CHZZK 공식 문서 기준으로:

- Access Token 발급 시 `clientSecret`이 요청 바디에 들어간다
- Client 인증 API는 `Client-Id`, `Client-Secret` 헤더를 사용한다

즉, `모든 걸 클라이언트에서 처리`하려면 결국 민감 정보가 클라이언트 환경에 존재하게 된다.

### 이 요구가 현실적으로 가능한 경우

- 앱이 `스트리머 본인 PC 전용 운영 툴`일 때
- 앱을 외부에 배포하지 않을 때
- `clientSecret`을 코드에 하드코딩하지 않고 로컬 설정/보안 저장소에서 주입할 때

### 위험한 경우

- 퍼블릭 배포형 팬게임 클라이언트로 배포할 때
- 빌드 산출물 안에 `clientSecret`이 들어갈 때
- 소스 없이도 리버스 엔지니어링이 가능한 형태로 민감 값이 포함될 때

### 현재 기준 권장 해석

지금 요구사항을 안전하게 만족시키려면 이 앱은 `일반 배포 앱`이 아니라 `스트리머 방송용 개인 운영 툴`로 보는 것이 맞다.

즉, 문서 기준 운영 전제는 아래 문장으로 고정하는 것이 적절하다.

`이 앱은 스트리머 본인의 Windows 방송 PC에서 실행되는 비공개 운영 툴로 간주한다.`

이 전제가 아니면 나중에 반드시 보안 구조를 다시 바꿔야 한다.

## 7. CHZZK 연동 구현 방향

실제 구현은 아래처럼 나누는 편이 좋다.

### A. 인증 계층

- 로그인/인가 코드 받기
- Access Token 저장
- Refresh Token 저장
- 만료 시 갱신

### B. 세션 계층

- 세션 생성 요청
- 소켓 URL 획득
- 소켓 연결
- `connected` 시스템 메시지에서 `sessionKey` 확보

### C. 구독 계층

- 채팅 이벤트 구독
- 권한 취소 또는 구독 해제 처리

### D. 게임 입력 변환 계층

- `content`를 게임 명령어로 파싱
- `senderChannelId` 기준 중복 입력 제어
- 라운드별 허용 명령어 필터링
- fallback 입력 집계

즉, CHZZK 연동 코드는 `수신`, `구독`, `파싱`, `게임 반영`을 한 군데 섞지 말고 분리해야 한다.

## 8. Java/libGDX 기준 권장 패키지 분리

권장 패키지는 아래처럼 나누는 편이 좋다.

```text
game/
  launcher/
  app/
  state/
  screen/
  ui/
  content/
  model/
  service/
  chat/
  config/
```

### 세부 역할

- `launcher`: LWJGL3 시작점
- `app`: 메인 Game/App 진입 객체
- `state`: 상태 머신
- `screen`: 각 화면 또는 주요 페이즈 렌더링
- `ui`: Scene2D HUD / 버튼 패널
- `content`: 선택지, 사고 이벤트, 결과 멘트 테이블
- `model`: 상태값, 점수, 채팅 이벤트 모델
- `service`: 라운드 집계, 결과 계산, fallback 처리
- `chat`: CHZZK 인증, 세션, 소켓, 메시지 파싱
- `config`: 로컬 설정, 토큰 파일, 앱 설정

## 9. 자산 그림체 기준

로컬 `asset/` 폴더를 기준으로 보면 현재 그림체 방향은 꽤 선명하다.

확인한 자산 기준 특징은 아래와 같다.

- SD 또는 치비 비율
- 둥글고 작은 실루엣
- 부드러운 파스텔 계열 색감
- 연필/크레용 같은 종이 질감
- 얇고 부드러운 갈색 계열 선화
- 볼터치와 소형 소품이 강조되는 귀여운 분위기
- 고양이, 머그컵, 책상, 작은 장식 같은 일상 소품 친화적 무드

즉, 비주얼 기준은 아래처럼 잡는 것이 맞다.

- UI도 너무 날카롭거나 메탈릭하게 가지 않기
- 굵은 대비보다 부드러운 명암과 밝은 배경
- 버튼은 방송용이라 읽히기 쉬워야 하지만, 스타일은 귀엽고 둥근 편
- 파티 오브젝트는 스티커처럼 읽히는 실루엣이 좋음

### 참고 자산

- `asset/치즈냥 sd.png`
- `asset/그려주기_치즈냥.png`
- `asset/12월.png`

## 10. 실제 구현 태스크 분해

이제부터의 구현 태스크는 아래 순서가 가장 적절하다.

### Task 0. 프로젝트 전환

- Spring Boot 의존 제거
- libGDX 프로젝트 구조 생성
- `core + lwjgl3` 구조 정리

완료 기준:

- 빈 libGDX 창이 Windows에서 실행됨

### Task 1. 기본 앱 골격

- `Lwjgl3Launcher`
- 메인 `Game` 또는 `ApplicationAdapter`
- 기본 `Screen` 전환 구조
- 상단 HUD / 하단 커맨드 바 / 우측 패널 빈 레이아웃

완료 기준:

- 와이어프레임 수준의 빈 화면이 뜸

### Task 2. 상태 머신 구현

- `IDLE -> COUNTDOWN -> CHOICE -> TROUBLE -> FINALE -> RESULT`
- 자동 전이
- 수동 전이

완료 기준:

- 내용 없이도 한 판이 순환

### Task 3. Scene2D HUD 및 스트리머 패널

- 상단 상태바
- 우측 버튼 패널
- 버튼 활성/비활성 규칙
- 로그/상태 패널

완료 기준:

- 스트리머 버튼이 상태에 맞게 작동

### Task 4. 로컬 채팅 시뮬레이터

- 가짜 유저 목록
- 입력 큐
- 명령어 주입
- 무응답/동률 재현

완료 기준:

- 실제 네트워크 없이 채팅 입력 흐름 테스트 가능

### Task 5. 게임 콘텐츠 적용

- 선택지 2세트
- 사고 이벤트 3개
- 결과 등급과 결과 멘트

완료 기준:

- 게임처럼 보이는 최소 루프 완성

### Task 6. CHZZK 인증/세션 연동

- 로컬 설정에서 client 정보 읽기
- 인증 토큰 발급 및 갱신
- 세션 생성
- 소켓 연결

완료 기준:

- 세션 생성 후 연결 완료 메시지 수신

### Task 7. CHZZK 채팅 이벤트 구독

- `sessionKey` 획득
- 채팅 구독 요청
- `CHAT` 이벤트 메시지 수신

완료 기준:

- 실채팅 메시지가 클라이언트 로그에 찍힘

### Task 8. 채팅 입력 -> 게임 반영

- `1`, `2`, `3`
- `!축하`, `!응원`
- sender 기준 중복 제어
- 라운드별 허용 명령 필터링

완료 기준:

- 실제 채팅으로 게임이 반응

### Task 9. 자산 스타일 반영

- 임시 도형에서 자산 기반 스타일로 교체
- 배경, 캐릭터, 소품 톤 맞추기
- HUD 색감 조정

완료 기준:

- 최소한의 방송용 완성도 확보

## 11. 즉시 착수 순서

지금 바로 시작한다면 아래 순서가 맞다.

1. `Task 0`
2. `Task 1`
3. `Task 2`
4. `Task 4`
5. `Task 5`
6. `Task 3`
7. `Task 6`
8. `Task 7`
9. `Task 8`
10. `Task 9`

이 순서가 좋은 이유는 다음과 같다.

- 먼저 libGDX로 창이 떠야 한다
- 그다음 흐름이 돌아야 한다
- 그다음 입력을 넣어야 한다
- 마지막에 실채팅과 자산을 붙여야 한다

## 12. 구현 전 체크포인트

실제 착수 전 아래를 다시 확인하는 것이 좋다.

### 기술 체크

- Java 버전
- libGDX Gradle 구조
- Windows 배포 방식
- 로컬 설정 파일 위치

### 운영 체크

- 이 앱이 정말 `비공개 운영 툴`인지
- CHZZK 앱 등록 정보가 준비됐는지
- redirect URI 전략을 어떻게 할지

### 아트 체크

- asset 원본을 실제 게임 리소스로 바로 쓸지
- 참고용 톤 가이드로만 쓸지

## 13. 이번 단계 결론

현재 기준 구현 방향은 아래 문장으로 정리할 수 있다.

`이 프로젝트는 Java + libGDX 기반의 Windows 전용 비공개 방송 운영 툴로 구현하며, core 게임 로직과 desktop launcher를 분리하고, CHZZK 세션/채팅 수신은 클라이언트 내부 모듈로 붙이되 민감 인증 정보는 코드에 하드코딩하지 않는다.`

## 14. Source Note

- CHZZK Developers 메인: https://developers.chzzk.naver.com/
- CHZZK Authorization: https://chzzk.gitbook.io/chzzk/chzzk-api/authorization
- CHZZK Session: https://chzzk.gitbook.io/chzzk/chzzk-api/session
- CHZZK 참고사항: https://chzzk.gitbook.io/chzzk/chzzk-api/tips
- libGDX application framework / launcher / UI docs: https://libgdx.com/wiki/app/the-application-framework
- libGDX starter classes and configuration: https://libgdx.com/wiki/app/starter-classes-and-configuration
- libGDX application/modules/UI examples: https://libgdx.com/wiki/app/modules-overview
- 로컬 참고 자산:
  - `asset/치즈냥 sd.png`
  - `asset/그려주기_치즈냥.png`
  - `asset/12월.png`
