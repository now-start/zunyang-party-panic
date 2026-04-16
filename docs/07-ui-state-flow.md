# UI State Flow

- Version: 0.1
- Date: 2026-04-16
- Based on: `docs/03-birthday-game-loop-design.md`, `docs/04-streamer-intervention-design.md`, `docs/05-content-table-and-ui-action-mapping.md`, `docs/06-game-screen-wireframe.md`
- Goal of this document: 화면 상태 전이, 버튼 활성화 조건, 자동 진행 규칙, fallback 동작을 정의한다.

## 1. 이번 단계에서 정할 것

이번 문서에서는 아래를 정리한다.

- 어떤 상태가 존재하는지
- 상태가 어떤 조건에서 다음 상태로 넘어가는지
- 스트리머 버튼이 언제 활성화되는지
- 저채팅량 또는 무응답 시 어떤 fallback이 작동하는지

## 2. 상태 설계 원칙

이 게임의 상태 설계는 아래 원칙을 따라야 한다.

### 원칙 1. 한 화면에는 한 가지 행동만 요구해야 한다

시청자와 스트리머 모두 지금 무엇을 해야 하는지 바로 알아야 한다.

- 선택 라운드면 선택만
- 사고 대응이면 대응만
- 피날레면 축하만

한 상태에서 여러 행동을 동시에 요구하면 저채팅량 환경에서 더 흐려진다.

### 원칙 2. 자동 전이가 기본이고, 스트리머 개입은 제한적이다

게임이 스트리머 클릭 없이는 멈춘다면 운영 피로가 커진다.  
기본은 자동 전이, 중요한 장면만 스트리머가 끼어드는 구조가 맞다.

### 원칙 3. 무응답이어도 상태는 계속 흘러가야 한다

입력이 없다고 정지되면 방송이 죽는다.  
무응답은 실패나 반성공으로 처리하고 계속 전이해야 한다.

### 원칙 4. 스트리머 액션은 상태 조건이 맞을 때만 켜진다

모든 버튼이 항상 켜져 있으면 오작동과 진행 혼선이 난다.

## 3. 상태 그룹 정의

전체 상태는 크게 아래 네 그룹으로 나눈다.

### A. 시스템 상태

- 앱 실행
- 준비 완료
- 대기
- 라운드 종료 후 리셋

### B. 방송 진행 상태

- 카운트다운
- 선택 라운드
- 사고 대응
- 피날레
- 결과 공개

### C. 스트리머 개입 상태

- 오늘의 픽 지정 가능
- 리롤 사용 가능
- 스킬 사용 가능
- 피날레 트리거 사용 가능
- 결과 코멘트 선택 가능

### D. fallback 상태

- 무응답
- 동률
- 입력 부족
- 강제 스트리머 판정

## 4. 전체 상태 흐름

권장 전체 흐름은 아래와 같다.

```text
BOOT
  -> READY_CHECK
  -> IDLE
  -> COUNTDOWN
  -> CHOICE_1_ACTIVE
  -> CHOICE_1_RESOLVE
  -> CHOICE_2_ACTIVE
  -> CHOICE_2_RESOLVE
  -> TROUBLE_1_ACTIVE
  -> TROUBLE_1_RESOLVE
  -> TROUBLE_2_ACTIVE
  -> TROUBLE_2_RESOLVE
  -> TROUBLE_3_ACTIVE
  -> TROUBLE_3_RESOLVE
  -> FINALE_ACTIVE
  -> FINALE_TRIGGER_READY
  -> FINALE_RESOLVE
  -> RESULT
  -> RESET_TO_IDLE
```

이 흐름이 기본이며, 중간에 아래 분기가 들어간다.

- `CHOICE_*_ACTIVE -> STREAMER_DECISION_FALLBACK`
- `TROUBLE_*_ACTIVE -> PARTIAL_SUCCESS_FALLBACK`
- `FINALE_ACTIVE -> SMALL_FINALE_FALLBACK`

## 5. 상태 상세 정의

아래 표는 실제 운영에 필요한 핵심 상태를 정리한 것이다.

| 상태 코드 | 상태 이름 | 화면 목적 | 진입 조건 | 종료 조건 |
|---|---|---|---|---|
| S0 | BOOT | 앱 실행, 리소스 로드 | 프로그램 시작 | 초기 점검 완료 |
| S1 | READY_CHECK | 방송 전 점검 | BOOT 완료 | 스트리머가 준비 확인 |
| S2 | IDLE | 다음 판 대기 | READY_CHECK 또는 RESULT 종료 | 스트리머가 시작 버튼 클릭 |
| S3 | COUNTDOWN | 판 시작 카운트다운 | IDLE에서 시작 | 5초 종료 |
| S4 | CHOICE_1_ACTIVE | 첫 선택 입력 | COUNTDOWN 종료 | 타이머 종료 또는 조기 마감 |
| S5 | CHOICE_1_RESOLVE | 첫 선택 결과 반영 | CHOICE_1_ACTIVE 종료 | 짧은 결과 연출 종료 |
| S6 | CHOICE_2_ACTIVE | 두 번째 선택 입력 | CHOICE_1_RESOLVE 종료 | 타이머 종료 또는 조기 마감 |
| S7 | CHOICE_2_RESOLVE | 두 번째 선택 결과 반영 | CHOICE_2_ACTIVE 종료 | 짧은 결과 연출 종료 |
| S8 | TROUBLE_1_ACTIVE | 첫 사고 대응 | CHOICE_2_RESOLVE 종료 | 5~6초 종료 |
| S9 | TROUBLE_1_RESOLVE | 첫 사고 판정 반영 | TROUBLE_1_ACTIVE 종료 | 결과 연출 종료 |
| S10 | TROUBLE_2_ACTIVE | 두 번째 사고 대응 | TROUBLE_1_RESOLVE 종료 | 5~6초 종료 |
| S11 | TROUBLE_2_RESOLVE | 두 번째 사고 판정 반영 | TROUBLE_2_ACTIVE 종료 | 결과 연출 종료 |
| S12 | TROUBLE_3_ACTIVE | 세 번째 사고 대응 | TROUBLE_2_RESOLVE 종료 | 5~6초 종료 |
| S13 | TROUBLE_3_RESOLVE | 세 번째 사고 판정 반영 | TROUBLE_3_ACTIVE 종료 | 결과 연출 종료 |
| S14 | FINALE_ACTIVE | 축하 입력 누적 | TROUBLE_3_RESOLVE 종료 | 타이머 종료 또는 피날레 준비 도달 |
| S15 | FINALE_TRIGGER_READY | 스트리머 피날레 발동 대기 | 축하 게이지 임계치 도달 | 스트리머 발동 또는 타이머 종료 |
| S16 | FINALE_RESOLVE | 최종 축하 연출 | 피날레 발동 또는 fallback | 연출 종료 |
| S17 | RESULT | 결과 카드 노출 | FINALE_RESOLVE 종료 | 결과 표시 시간 종료 |
| S18 | RESET_TO_IDLE | 다음 판 준비 | RESULT 종료 | 2~3초 후 IDLE |

## 6. 자동 전이 규칙

기본 전이 규칙은 아래와 같다.

### COUNTDOWN

- `5초` 후 자동으로 `CHOICE_1_ACTIVE`

### CHOICE_1_ACTIVE / CHOICE_2_ACTIVE

- 기본 타이머 종료 시 자동으로 Resolve 상태로 전이
- 단, 아래 조건이면 `조기 마감` 가능
  - 유효 투표자 수 5명 이상
  - 1위 선택지가 60% 이상
  - 남은 시간 6초 이하

이 조기 마감은 자동으로 넣지 말고 `스트리머 수동 확인 가능 옵션`으로 두는 편이 안전하다.

### TROUBLE_*_ACTIVE

- 5초 ~ 6초 뒤 자동 Resolve
- 스트리머 스킬 사용 여부와 관계없이 무조건 다음 상태로 넘어감

### FINALE_ACTIVE

- 타이머 종료 시 자동 종료
- 임계치 도달 시 `FINALE_TRIGGER_READY`로 진입

### RESULT

- 10초 ~ 15초 노출 후 자동으로 `RESET_TO_IDLE`

## 7. 스트리머 수동 전이 규칙

자동 전이와 별개로 스트리머가 직접 상태를 움직일 수 있는 지점이 있다.

### 허용되는 수동 전이

- `IDLE -> COUNTDOWN`
  - 시작 버튼
- `CHOICE_*_ACTIVE -> CHOICE_*_RESOLVE`
  - 조기 마감 버튼 또는 스트리머 확정
- `FINALE_TRIGGER_READY -> FINALE_RESOLVE`
  - 피날레 발동 버튼
- `RESULT -> RESET_TO_IDLE`
  - 즉시 다음 판 준비 버튼

### 허용하지 않는 수동 전이

- 중간 상태 건너뛰기
- 사고 대응 구간 전체 스킵
- 결과 화면 없이 다음 판 시작

이건 방송 흐름을 지키기 위한 제한이다.

## 8. 버튼 활성화 매트릭스

버튼은 상태에 따라 활성/비활성이 명확해야 한다.

| 버튼 | IDLE | COUNTDOWN | CHOICE_1 | CHOICE_2 | TROUBLE | FINALE_ACTIVE | FINALE_READY | RESULT |
|---|---|---|---|---|---|---|---|---|
| Start | ON | OFF | OFF | OFF | OFF | OFF | OFF | OFF |
| Today's Pick | OFF | OFF | ON | ON | OFF | OFF | OFF | OFF |
| Reroll | OFF | OFF | ON | ON | OFF | OFF | OFF | OFF |
| Birthday Wish | OFF | OFF | OFF | OFF | ON | OFF | OFF | OFF |
| Finale Trigger | OFF | OFF | OFF | OFF | OFF | OFF | ON | OFF |
| Result Comment | OFF | OFF | OFF | OFF | OFF | OFF | OFF | ON |

## 9. 버튼별 세부 활성 조건

### Start

- 활성 상태: `IDLE`
- 비활성 조건:
  - 라운드 진행 중
  - 결과 화면 노출 중

### Today's Pick

- 활성 상태: `CHOICE_1_ACTIVE`, `CHOICE_2_ACTIVE`
- 사용 제한:
  - 각 선택 라운드당 1회
- 자동 비활성:
  - 확정 직전
  - Resolve 상태 진입 후

### Reroll

- 활성 상태: `CHOICE_1_ACTIVE`, `CHOICE_2_ACTIVE`
- 사용 제한:
  - 판당 1회
  - 해당 라운드 시작 후 `첫 6초` 이내만 권장
- 자동 비활성:
  - 이미 사용함
  - 동률 판정 중
  - 남은 시간 부족

### Birthday Wish

- 활성 상태: `TROUBLE_1_ACTIVE`, `TROUBLE_2_ACTIVE`, `TROUBLE_3_ACTIVE`
- 사용 제한:
  - 판당 1회
- 자동 비활성:
  - 이미 사용함
  - Resolve 상태
  - 사건 시작 직후 1초 이내 잠금이 필요하면 그 규칙 추가 가능

### Finale Trigger

- 활성 상태: `FINALE_TRIGGER_READY`
- 활성 조건:
  - 축하 게이지 임계치 도달
- 자동 비활성:
  - 아직 임계치 미도달
  - 이미 발동함
  - 피날레 종료

### Result Comment

- 활성 상태: `RESULT`
- 사용 목적:
  - 베스트 순간 선택
  - 오늘의 문제아 선택
  - 결과 카드 문구 확정

## 10. fallback 상태 흐름

저채팅량을 고려하면 이 문단이 매우 중요하다.

### 선택 라운드 fallback

#### F1. 무응답

- 조건: 유효 투표 0개
- 동작:
  - 상태는 멈추지 않음
  - `STREAMER_DECISION_FALLBACK` 처리
  - 스트리머가 현재 보이는 선택지 중 하나 확정

#### F2. 입력 부족

- 조건: 유효 투표 1~2개
- 동작:
  - 표시 문구: `소수 정예 투표 반영`
  - 스트리머가 확정하거나, 최고 득표안 자동 확정

#### F3. 동률

- 조건: 1위 동률
- 동작:
  - 표시 문구: `스트리머 판정`
  - 스트리머가 3초 안에 확정
  - 미입력 시 `오늘의 픽` 우선, 없으면 좌측부터 우선

### 사고 대응 fallback

#### F4. 유효 입력 없음

- 조건: 해당 사고 구간에 유효 입력 0개
- 동작:
  - 완전 실패 대신 `반실패`
  - 안정도 소폭 감소
  - 시각적 연출은 너무 처참하지 않게

#### F5. 유효 입력 1~2개

- 조건: 입력은 있으나 판정치 부족
- 동작:
  - `간신히 막아냈어요` 또는 `아슬아슬했어요`
  - 스트리머 스킬 사용 유도 강조

### 피날레 fallback

#### F6. 참여 부족

- 조건: 피날레 종료 시 참여자 3명 이하
- 동작:
  - 실패 엔딩이 아니라 `소박한 축하 엔딩`
  - `FINALE_RESOLVE`는 정상 진입
  - 컷신만 규모 축소

#### F7. 임계치 미도달

- 조건: `FINALE_TRIGGER_READY` 진입 실패
- 동작:
  - 스트리머 버튼은 잠금 유지
  - 자동으로 소형 피날레 연출 재생

## 11. HUD 상태 전이

상단 HUD와 하단 명령어 바도 상태에 따라 바뀌어야 한다.

### 상단 HUD

항상 유지:

- 라운드명
- 타이머
- 준비도
- 안정도
- 축하열기

상태별 변화:

- 선택 라운드: 선택 주제 강조
- 사고 대응: 위험 배너 강조
- 피날레: 게이지 강조
- 결과 화면: 등급 강조

### 하단 명령어 바

상태별 문구 예시:

- COUNTDOWN: `곧 시작합니다`
- CHOICE: `채팅 입력 1 / 2 / 3`
- TROUBLE: `지금 막아야 해요: 1 / 2 / 3`
- FINALE: `!축하 / !응원`
- RESULT: `다음 판 준비 중`

## 12. 조작 패널 상태 전이

우측 패널은 고정 배치지만 내부 콘텐츠가 상태별로 바뀐다.

### IDLE

- Start 버튼 강조
- 이전 판 요약 표시 가능

### CHOICE

- Today's Pick
- Reroll
- 유효 투표 수
- 동률 여부

### TROUBLE

- Birthday Wish
- 현재 사건 번호
- 이번 사건 유효 입력 수
- fallback 경고

### FINALE

- 게이지 수치
- unique / total 집계
- Finale Trigger 잠금 상태

### RESULT

- 결과 코멘트 버튼
- 다음 판 버튼

## 13. 상태별 애니메이션 강도

상태 전이가 눈에 보여야 방송이 살아난다.

### 약한 전이

- Choice Active -> Choice Resolve
- Trouble Resolve -> 다음 Trouble Active

### 중간 전이

- Countdown -> Choice 1
- Choice 2 Resolve -> Trouble 1

### 강한 전이

- Finale Trigger Ready -> Finale Resolve
- Finale Resolve -> Result

즉, 모든 상태 전이를 크게 만들 필요는 없고, 감정 피크 구간만 강하게 주면 된다.

## 14. 예외 상태

운영상 아래 예외 상태를 두는 편이 좋다.

### PAUSED

- 사용 목적: 스트리머가 잠깐 정지해야 할 때
- 진입:
  - 관리자 패널에서만 가능
- 동작:
  - 방송 화면에는 `잠시 정리 중` 정도로 표시
  - 일반 시청자용 명령어는 잠금

### FORCE_ADVANCE

- 사용 목적: 버그 또는 운영상 꼬임 정리
- 노출:
  - 일반 스트리머 버튼과 분리
  - 작은 관리자 메뉴 내부

이 둘은 방송 컨텐츠 버튼이 아니라 운영 안정성 장치다.

## 15. 권장 구현 우선순위

MVP에서 먼저 구현할 상태 흐름은 아래와 같다.

1. IDLE
2. COUNTDOWN
3. CHOICE_1_ACTIVE / RESOLVE
4. CHOICE_2_ACTIVE / RESOLVE
5. TROUBLE_1~3_ACTIVE / RESOLVE
6. FINALE_ACTIVE
7. FINALE_TRIGGER_READY
8. RESULT

fallback은 최소 아래부터 넣는 것이 맞다.

1. 선택 라운드 무응답 fallback
2. 동률 fallback
3. 피날레 참여 부족 fallback

## 16. 이번 단계 결론

현재 기준 UI 상태 흐름은 아래 문장으로 정리할 수 있다.

`이 게임의 UI는 자동 전이를 기본으로 하되, 선택 확정, 위기 대응 스킬, 피날레 발동, 결과 코멘트 같은 핵심 지점에서만 스트리머의 제한적 수동 개입을 허용하는 상태 머신 구조로 설계한다.`

## 17. 다음 문서에서 다룰 내용

다음 단계에서는 아래를 정리하면 된다.

1. 실제 구현 우선순위
2. 프로토타입 범위
3. 기술 검증 대상
4. 로컬 시뮬레이터 요구사항

즉, 다음 문서는 `08-prototype-scope-and-mvp-build-plan.md` 성격이 맞다.
