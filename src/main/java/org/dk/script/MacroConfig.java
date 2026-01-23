package org.dk.script;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 매크로 설정 파일 관리 클래스
 * src/main/resources/macro-config.json 파일에서 설정을 읽어옴
 */
public class MacroConfig {

    private static final String CONFIG_PATH = "src/main/resources/macro-config.json";
    private static MacroConfig instance;

    private String currentSidunEvent;
    private String currentDungeonEvent;
    private List<String> availableSidunEvents;
    private List<String> availableDungeonEvents;

    private MacroConfig() {
        loadConfig();
    }

    /**
     * 싱글톤 인스턴스 반환
     */
    public static MacroConfig getInstance() {
        if (instance == null) {
            instance = new MacroConfig();
        }
        return instance;
    }

    /**
     * 설정 파일 다시 로드 (이벤트 변경 시 사용)
     */
    public void reload() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(CONFIG_PATH));

            // 현재 이벤트 설정
            JsonNode currentEvent = root.get("currentEvent");
            this.currentSidunEvent = currentEvent.get("sidun").asText();
            this.currentDungeonEvent = currentEvent.get("dungeon").asText();

            // 사용 가능한 이벤트 목록
            this.availableSidunEvents = new ArrayList<>();
            JsonNode sidunEvents = root.get("availableEvents").get("sidun");
            for (JsonNode event : sidunEvents) {
                availableSidunEvents.add(event.asText());
            }

            this.availableDungeonEvents = new ArrayList<>();
            JsonNode dungeonEvents = root.get("availableEvents").get("dungeon");
            for (JsonNode event : dungeonEvents) {
                availableDungeonEvents.add(event.asText());
            }

            System.out.println("[MacroConfig] 설정 로드 완료");
            System.out.println("  - 현재 시던 이벤트: " + currentSidunEvent);
            System.out.println("  - 현재 던전 이벤트: " + currentDungeonEvent);

        } catch (IOException e) {
            System.err.println("[MacroConfig] 설정 파일 로드 실패: " + e.getMessage());
            // 기본값 설정
            this.currentSidunEvent = "noevent";
            this.currentDungeonEvent = "nospecialdugeon";
            this.availableSidunEvents = new ArrayList<>();
            this.availableDungeonEvents = new ArrayList<>();
        }
    }

    // Getters
    public String getCurrentSidunEvent() {
        return currentSidunEvent;
    }

    public String getCurrentDungeonEvent() {
        return currentDungeonEvent;
    }

    public List<String> getAvailableSidunEvents() {
        return availableSidunEvents;
    }

    public List<String> getAvailableDungeonEvents() {
        return availableDungeonEvents;
    }

    /**
     * 이벤트가 활성화되어 있는지 확인
     */
    public boolean hasActiveEvent() {
        return !"noevent".equals(currentSidunEvent) || !"nospecialdugeon".equals(currentDungeonEvent);
    }
}
