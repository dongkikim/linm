# LinM Macro Generator

리니지M 매크로 스크립트(.json)를 조합하고 병합하여 실행 가능한 전체 매크로 파일을 생성하는 도구입니다.
작은 단위의 동작(매크로 조각)들을 정의하고, 이를 설정에 따라 조합하여 복잡한 시나리오의 매크로를 자동으로 생성합니다.

## 📂 프로젝트 구조

- **`src/main/resources/macro/`**: 개별 동작을 수행하는 최소 단위의 매크로 파일들이 위치합니다. (예: `button_1.json`, `dungeon_giran.json`)
- **`src/main/resources/macro-definitions.json`**: 매크로 생성 규칙과 그룹을 정의하는 핵심 설정 파일입니다.
- **`src/main/java/org/dk/JsonMergerMultiple.java`**: 프로그램을 실행하는 메인 클래스입니다.
- **`src/result/`**: 생성된 최종 매크로 파일들이 저장되는 폴더입니다. 실행 시마다 초기화됩니다.

## 🚀 실행 방법

이 프로젝트는 Gradle 기반의 Java 프로젝트입니다.

1. **프로젝트 열기**: IntelliJ IDEA 등의 IDE에서 프로젝트를 엽니다.
2. **실행**: `src/main/java/org/dk/JsonMergerMultiple.java` 파일을 열고 `main` 메소드를 실행합니다.
3. **결과 확인**: 실행이 완료되면 콘솔에 로그가 출력되고, `src/result/` 폴더에 새로운 JSON 파일들이 생성됩니다.

## ✨ 매크로 추가 및 수정 가이드

### 1. 새로운 동작(매크로 조각) 추가
특정 버튼 클릭이나 단순한 동작을 추가하려면 `src/main/resources/macro/` 폴더에 JSON 파일을 생성하세요.
- 기존 파일(예: `button_1.json`)을 복사하여 형식을 유지하는 것이 좋습니다.
- `events` 배열 안에 터치 좌표(`x`, `y`), 지연 시간(`ms`), 타입(`type`) 등을 정의합니다.

### 2. 매크로 조합 규칙 추가 (`macro-definitions.json`)
새로운 전체 매크로를 만들려면 `src/main/resources/macro-definitions.json` 파일을 수정해야 합니다.

#### A. Macros 설정 (`macros`)
단일 캐릭터나 특정 목적을 위한 스크립트를 생성합니다.

```json
{
  "name": "생성될_파일_이름",
  "type": "로직_타입",
  "config": {
    "mainCharRepeat": 10,       // 반복 횟수
    "scheduleTime": "01h",      // 스케줄 시간 (00h, 30m 등)
    "useCurrentEvent": true,    // 현재 이벤트 적용 여부
    "scheduleOnly": false       // 스케줄만 실행 여부
  }
}
```

**지원되는 로직 타입 (`type`):**
- `mainCharacter`: 메인 캐릭터용 스크립트 조합
- `subCharacter`: 서브 캐릭터용 스크립트 조합
- `scheduleOnly`: 스케줄 전용
- `weekendAll`: 주말 전체 스크립트
- `weekendHunt`: 주말 사냥 스크립트

#### B. Groups 설정 (`groups`)
여러 개의 매크로 파일(원본 조각 또는 생성된 파일)을 하나로 묶습니다. `r0`, `r1`... 순서로 병합됩니다.

```json
"group_name": {
  "macroScripts": ["원본_파일1", "원본_파일2"],      // resources/macro/ 폴더 내 파일
  "resultScripts": ["생성된_파일1", "생성된_파일2"]   // result/ 폴더 내 생성된 파일
}
```

### 3. 이벤트 및 던전 설정 변경
`JsonMergerMultiple.java` 또는 `MacroConfig` 클래스에서 현재 진행 중인 이벤트나 던전 설정을 변경할 수 있습니다.
설정 파일의 `config` 항목에서 `"event": "이벤트명"`을 지정하거나 `"useCurrentEvent": true`를 사용하여 적용합니다.

## 🛠 주의사항
- **파일명**: 한글 파일명은 자동으로 정규화(NFC) 처리되지만, 가급적 영문 사용을 권장합니다.
- **결과 폴더**: 프로그램을 실행할 때마다 `src/result/` 폴더의 내용이 모두 삭제되고 새로 생성되므로, 필요한 파일은 미리 백업하세요.
