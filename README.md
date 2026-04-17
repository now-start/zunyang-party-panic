# zunyang-party-panic

치즈냥 생일 방송 직전의 준비방을 돌아다니며 조사, 대사, 미니게임, 스토리 시퀀스를 진행하는 libGDX 기반 2D 팬게임입니다.

현재 구조는 `adapter -> application -> domain` 흐름을 기준으로 정리하고 있으며, 허브 이동/상호작용 로직은 use case 중심으로 분리되어 있습니다.

## 실행 방법

### 요구 사항

- Java 25
- Gradle Wrapper 사용 가능 환경

### 실행

```bash
./gradlew run
```

기본 실행 모드는 `test`입니다. 현재 기본값은 [game.properties](/Users/moon/IdeaProjects/demo4/src/main/resources/game.properties#L1)에 정의되어 있습니다.

`live` 모드로 실행하려면 환경변수를 사용하면 됩니다.

```bash
APP_MODE=live ./gradlew run
```

### 테스트

```bash
./gradlew test
```

## 모드 설명

- `test`: 좌표, 진행 상황, 추천 이벤트 같은 운영용 UI를 함께 표시합니다.
- `live`: 실제 플레이 화면에 가까운 UI만 표시합니다.

모드 판별은 다음 순서로 결정됩니다.

1. 시스템 프로퍼티 `app.mode`
2. 환경변수 `APP_MODE`
3. 리소스 파일 `game.properties`

관련 클래스:

- [AppMode.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/AppMode.java)
- [GameConfig.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/GameConfig.java)
- [GameConfigLoader.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/GameConfigLoader.java)

## 게임 흐름

게임은 타이틀 화면에서 시작해 허브를 거쳐 각 활동으로 이동합니다.

1. 방송 책상
2. 장식 창고
3. 케이크 테이블
4. 포토존
5. 백스테이지 복도
6. 팬레터 우편함
7. 생일 방송 무대

활동은 두 종류로 나뉩니다.

- 점수형 활동: `BROADCAST_DESK`, `CAKE_TABLE`, `PHOTO_TIME`
- 스토리형 활동: `STORAGE_ROOM`, `BACKSTAGE`, `FAN_LETTER`, `FINALE_STAGE`

진행 잠금 해제와 엔딩 톤 계산은 [GameProgress.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/progress/GameProgress.java)가 담당합니다.

## 아키텍처 개요

현재 구조는 아래처럼 나뉩니다.

```text
org.nowstart.zunyang.partypanic
├── adapter
│   ├── in
│   │   ├── runtime
│   │   ├── renderer
│   │   └── screen
│   └── out
│       ├── map
│       └── state
├── application
│   ├── dto
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
├── config
├── domain
│   ├── activity
│   ├── event
│   ├── minigame
│   ├── model
│   ├── policy
│   ├── progress
│   └── story
```

레이어 책임은 다음과 같습니다.

- `adapter.in`: libGDX 입력, 화면, 렌더링
- `adapter.out`: 맵 로딩, 런타임 상태 저장 같은 외부 구현
- `application`: use case 계약, 결과 DTO, use case 구현
- `domain`: 규칙, 상태, 정책, 미니게임 로직, 이벤트, 진행도, 스토리 콘텐츠
- `config`: 객체 조립, 실행 모드 설정, 환경별 설정 로딩

## 패키지 및 클래스 설명

아래 설명은 현재 소스 트리를 기준으로 정리했습니다.

### `org.nowstart.zunyang.partypanic`

데스크톱 실행 진입점을 두는 루트 패키지입니다.

- [DesktopLauncher.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/DesktopLauncher.java): 데스크톱 실행 진입점입니다. libGDX `Lwjgl3Application`을 띄우고 창 크기, FPS, 타이틀을 설정합니다.

### `org.nowstart.zunyang.partypanic.adapter.in.runtime`

libGDX 런타임과 애플리케이션 화면 흐름을 연결하는 인바운드 어댑터 패키지입니다.

- [PartyPanicGame.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/runtime/PartyPanicGame.java): 전체 게임의 화면 전환 허브입니다. `GameNavigator` 구현체로서 타이틀, 허브, 미니게임, 스토리 화면 이동과 활동 완료 후 복귀를 관리합니다.
- [GameAssets.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/runtime/GameAssets.java): libGDX `AssetManager`, 공용 `SpriteBatch`, 공용 UI 폰트와 텍스처를 한 번만 로드하고 화면에 제공합니다.
- [GameAssetCatalog.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/runtime/GameAssetCatalog.java): 공용 폰트와 텍스처 경로를 한 곳에서 관리합니다.
- [GameViewportConfig.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/runtime/GameViewportConfig.java): libGDX `FitViewport`와 데스크톱 창 크기에서 함께 쓰는 기준 해상도를 정의합니다.

### `org.nowstart.zunyang.partypanic.adapter.in.screen`

libGDX `Screen` 구현체가 모여 있는 인바운드 어댑터 패키지입니다. 사용자의 키 입력을 받고 화면을 렌더링합니다.

- [AbstractGameScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/AbstractGameScreen.java): 공통 `OrthographicCamera`, `FitViewport`, `SpriteBatch` projection 적용을 담당하는 화면 베이스 클래스입니다.
- [AbstractMiniGameScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/AbstractMiniGameScreen.java): 미니게임 공통 베이스 클래스입니다. 공유 `GameAssets`를 받아 배경, 프레임, 폰트, 커맨드 바 렌더링 루프를 제공합니다.
- [TitleScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/TitleScreen.java): 타이틀 화면입니다. 진입 텍스트, 모드 안내, 시작/종료 입력을 처리합니다.
- [HubScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/HubScreen.java): 준비방 허브 화면입니다. 플레이어 이동, 조사, 대사 진행, 추천 이벤트 표시, 허브 타일 렌더링을 담당합니다.
- [PartyPanicScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/PartyPanicScreen.java): 방송 책상 정리 미니게임 화면입니다. `DeskSetupStateMachine`을 사용해 조절형 점수 게임을 렌더링합니다.
- [CakeTableScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/CakeTableScreen.java): 케이크 밸런스 미니게임 화면입니다. `CakeBalanceStateMachine`을 사용해 좌우 밸런스 조정 게임을 렌더링합니다.
- [PhotoTimeScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/PhotoTimeScreen.java): 포토존 촬영 미니게임 화면입니다. 공통 미니게임 베이스 위에서 프레임 이동과 촬영 판정을 렌더링합니다.
- [StorySequenceScreen.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/screen/StorySequenceScreen.java): 스토리 챕터 전용 화면입니다. 페이지 단위 대사 진행과 챕터 완료 후 허브 복귀를 처리합니다.

### `org.nowstart.zunyang.partypanic.adapter.in.renderer`

libGDX 드로잉을 돕는 렌더링 유틸리티 패키지입니다.

- [PixelUiRenderer.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/renderer/PixelUiRenderer.java): 패널, 테두리, 텍스트, 텍스처 맞춤 출력 같은 기본 UI 드로잉 도구입니다.
- [MiniGameChrome.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/renderer/MiniGameChrome.java): 미니게임 공통 프레임과 커맨드 바를 그리는 정적 유틸리티입니다.
- [MiniGameLayout.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/renderer/MiniGameLayout.java): 미니게임 화면 레이아웃 상수 모음입니다.
- [MiniGamePalette.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/renderer/MiniGamePalette.java): 미니게임 공통 색상 팔레트입니다.
- [DialogueWindowRenderer.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/renderer/DialogueWindowRenderer.java): 인물 초상과 대사창을 함께 그리는 전용 렌더러입니다.
- [HubMapRenderer.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/in/renderer/HubMapRenderer.java): 허브 타일, 이벤트, 플레이어, 위치/포커스 패널을 그리는 허브 전용 renderer입니다.

### `org.nowstart.zunyang.partypanic.adapter.out.map`

맵 공급자 구현 패키지입니다.

- [StaticHubMapAdapter.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/out/map/StaticHubMapAdapter.java): 하드코딩된 허브 레이아웃을 읽어 `GameMap`과 `DialogueEvent` 목록으로 변환합니다. 앵커 문자 파싱, 시작 위치 추출, 이벤트 생성이 여기서 일어납니다.

### `org.nowstart.zunyang.partypanic.adapter.out.state`

게임 상태 저장소 구현 패키지입니다.

- [InMemoryGameStateAdapter.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/adapter/out/state/InMemoryGameStateAdapter.java): 현재 세션 안에서만 유지되는 메모리 기반 `GameStatePort` 구현입니다.

### `org.nowstart.zunyang.partypanic.application.port.in`

인바운드 use case 인터페이스 패키지입니다. 화면은 구현체가 아니라 이 인터페이스에 의존합니다.

- [MovePlayerUseCase.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/port/in/MovePlayerUseCase.java): 플레이어 이동 요청 계약입니다.
- [InteractUseCase.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/port/in/InteractUseCase.java): 정면 이벤트 조사 요청 계약입니다.
- [AdvanceDialogueUseCase.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/port/in/AdvanceDialogueUseCase.java): 진행 중 대사 한 줄 넘기기 계약입니다.

### `org.nowstart.zunyang.partypanic.application.port.out`

애플리케이션 계층이 외부 구현에 기대는 지점을 정의합니다.

- [GameNavigator.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/port/out/GameNavigator.java): 화면 전환과 활동 완료를 외부 UI 어댑터에 위임하는 출력 포트입니다.
- [LoadMapPort.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/port/out/LoadMapPort.java): 허브 맵을 불러오는 포트입니다.
- [GameStatePort.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/port/out/GameStatePort.java): 현재 게임 상태를 읽고 저장하는 포트입니다.

### `org.nowstart.zunyang.partypanic.application.dto`

use case 입출력 데이터를 담는 DTO 패키지입니다.

- [MovePlayerCommand.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/dto/MovePlayerCommand.java): 이동 방향과 실제 이동 시도 여부를 담습니다.
- [MovePlayerResult.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/dto/MovePlayerResult.java): 이동 후 상태와 이동 성공 여부를 담습니다.
- [InteractResult.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/dto/InteractResult.java): 조사 결과, 대사 시작 여부, 잠금 해제 여부, 다음 활동 ID를 담습니다.
- [AdvanceDialogueResult.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/dto/AdvanceDialogueResult.java): 대사 진행 후 상태, 계속 대사 중인지 여부, 완료된 활동 ID를 담습니다.

### `org.nowstart.zunyang.partypanic.application.usecase`

허브 플레이 흐름을 실제로 수행하는 use case 구현 패키지입니다.

- [MovePlayerInteractor.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/usecase/MovePlayerInteractor.java): 플레이어 방향 갱신, 목표 좌표 계산, 이동 가능 여부 판단, 상태 저장을 수행합니다.
- [InteractInteractor.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/usecase/InteractInteractor.java): 정면 이벤트를 찾고 잠금 상태에 따라 적절한 대사를 시작합니다.
- [AdvanceDialogueInteractor.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/application/usecase/AdvanceDialogueInteractor.java): 대사 다음 줄 진행 또는 활동 완료 처리를 담당합니다.

### `org.nowstart.zunyang.partypanic.config`

애플리케이션 조립을 담당하는 구성 패키지입니다.

- [AppMode.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/AppMode.java): `test`/`live` 실행 모드를 정의합니다.
- [GameConfig.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/GameConfig.java): 현재 모드를 감싸고 화면 표시 규칙과 창 제목 suffix를 제공합니다.
- [GameConfigLoader.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/GameConfigLoader.java): 프로퍼티, 환경변수, 리소스 파일을 읽어 `GameConfig`를 만듭니다.
- [GameModule.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/config/GameModule.java): 허브에 필요한 맵, 상태 저장소, 이벤트 해석기, use case 구현체를 연결합니다.
- `GameModule.HubContext`: `HubScreen`이 필요로 하는 허브 초기 상태와 use case 묶음을 전달하는 조립 결과입니다.

### `org.nowstart.zunyang.partypanic.domain.activity`

활동 종류를 정의합니다.

- [ActivityId.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/activity/ActivityId.java): 게임 내 활동 식별자입니다. 코드값과 점수형 활동 여부를 함께 가집니다.

### `org.nowstart.zunyang.partypanic.domain.event`

허브에서 상호작용 가능한 이벤트를 표현하는 패키지입니다.

- [GameEvent.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/event/GameEvent.java): 허브 이벤트 공통 인터페이스입니다.
- [DialogueEvent.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/event/DialogueEvent.java): 제목, 잠금 대사, 상호작용 대사, 비주얼, 위치를 갖는 기본 이벤트 레코드입니다.
- [EventVisual.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/event/EventVisual.java): 이벤트의 화면 표시 타입입니다.

### `org.nowstart.zunyang.partypanic.domain.model`

허브와 대사 시스템의 핵심 상태를 담는 순수 도메인 모델 패키지입니다.

- [Direction.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/Direction.java): 상하좌우 방향과 이동 벡터를 정의합니다.
- [Position.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/Position.java): 좌표 값 객체입니다.
- [Player.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/Player.java): 현재 위치와 바라보는 방향을 갖는 플레이어 값 객체입니다.
- [DialogueLine.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/DialogueLine.java): 화자와 문장을 담는 한 줄 대사 데이터입니다.
- [Dialogue.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/Dialogue.java): 여러 줄 대사와 현재 인덱스를 관리합니다. 다음 줄 진행과 현재 줄 조회를 담당합니다.
- [GameMap.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/GameMap.java): 타일 레이아웃, 이벤트 목록, 시작 위치를 가진 허브 맵 모델입니다.
- [GameState.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/model/GameState.java): 맵, 플레이어, 현재 대사, 대사 후 진입할 활동 ID를 묶은 현재 게임 상태입니다.

### `org.nowstart.zunyang.partypanic.domain.policy`

허브 이동과 상호작용 판단에 필요한 도메인 정책 패키지입니다.

- [MovementPolicy.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/policy/MovementPolicy.java): 특정 좌표로 이동 가능한지 판단합니다.
- [EventResolver.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/policy/EventResolver.java): 정면 이벤트와 추천 이벤트를 계산합니다.

### `org.nowstart.zunyang.partypanic.domain.progress`

장기 진행 상태와 엔딩 판정을 담당합니다.

- [GameProgress.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/progress/GameProgress.java): 완료한 활동, 최고 점수, 다음 목표, 엔딩 타이틀/문장을 계산합니다.

### `org.nowstart.zunyang.partypanic.domain.story`

스토리형 활동의 챕터 데이터를 담는 패키지입니다.

- [StoryChapter.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/story/StoryChapter.java): 챕터 제목, 배경, 완료 메시지, 페이지 목록을 담는 레코드입니다.
- [StoryChapterFactory.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/story/StoryChapterFactory.java): 활동 ID와 현재 진행도를 받아 해당 스토리 챕터 데이터를 생성합니다.

### `org.nowstart.zunyang.partypanic.domain.minigame`

미니게임 규칙을 화면으로부터 분리한 상태 머신 패키지입니다.

- [DeskSetupStateMachine.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/minigame/DeskSetupStateMachine.java): 방송 책상 정리 미니게임의 선택, 수치 조정, 확인, 시간, 최종 점수를 관리합니다.
- [CakeBalanceStateMachine.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/minigame/CakeBalanceStateMachine.java): 케이크 밸런스 미니게임의 좌우 흔들림, 안정화, 시간, 점수를 관리합니다.
- [PhotoTimeStateMachine.java](/Users/moon/IdeaProjects/demo4/src/main/java/org/nowstart/zunyang/partypanic/domain/minigame/PhotoTimeStateMachine.java): 포토 프레임 이동, 촬영 판정, 누적 점수, 남은 컷 수를 관리합니다.

## 테스트 패키지 설명

### `org.nowstart.zunyang.partypanic.application.usecase`

- [HubUseCaseTest.java](/Users/moon/IdeaProjects/demo4/src/test/java/org/nowstart/zunyang/partypanic/application/usecase/HubUseCaseTest.java): 허브 이동, 이벤트 조사, 대사 진행 use case를 검증합니다.

### `org.nowstart.zunyang.partypanic.domain.progress`

- [GameProgressTest.java](/Users/moon/IdeaProjects/demo4/src/test/java/org/nowstart/zunyang/partypanic/domain/progress/GameProgressTest.java): 최고 점수 유지, 다음 목표 계산, 엔딩 판정을 검증합니다.

### `org.nowstart.zunyang.partypanic.domain.minigame`

- [CakeBalanceStateMachineTest.java](/Users/moon/IdeaProjects/demo4/src/test/java/org/nowstart/zunyang/partypanic/domain/minigame/CakeBalanceStateMachineTest.java): 케이크 밸런스 상태 머신의 시작, 조작, 종료 점수 계산을 검증합니다.

## 리소스 구조

- `src/main/resources/assets/images/backgrounds`: 배경 이미지
- `src/main/resources/assets/images/characters`: 캐릭터 이미지
- `src/main/resources/assets/images/events`: 이벤트/미니게임 관련 이미지
- `src/main/resources/assets/images/choices`: 선택지 카드 이미지
- `src/main/resources/assets/images/ui`: UI 배경 이미지
- `src/main/resources/assets/fonts`: 프로젝트 내장 폰트

현재 사용 중인 폰트:

- `NanumSquareRoundR.ttf`: 기본 본문 UI 폰트
- `GowunDodum-Regular.ttf`: 타이틀/헤드라인 폰트

## 현재 구조에서 볼 포인트

- 허브 로직은 `HubScreen -> application.usecase -> domain` 흐름으로 정리되어 있습니다.
- 화면 공통 자원은 `GameAssets`, 공통 좌표계와 카메라 적용은 `AbstractGameScreen`이 담당합니다.
- 미니게임은 화면과 상태 머신을 분리해 두었고, 공통 무대 프레임은 `AbstractMiniGameScreen`과 `MiniGameChrome`으로 정리되어 있습니다.
- 상태 저장은 아직 `InMemoryGameStateAdapter`만 사용하므로 세션을 종료하면 초기화됩니다.
- 맵 로딩은 `StaticHubMapAdapter` 기반이라 데이터 파일 분리 여지가 남아 있습니다.
