package org.dk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dk.script.*;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class JsonMergerMultiple {

    private static final String DEFINITIONS_PATH = "src/main/resources/macro-definitions.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            // result 폴더 정리
            clearResultFolder();

            // 이벤트 설정 로드
            MacroConfig macroConfig = MacroConfig.getInstance();
            String sidunEvent = macroConfig.getCurrentSidunEvent();
            String dungeonEvent = macroConfig.getCurrentDungeonEvent();

            // macro-definitions.json 읽기
            JsonNode definitions = mapper.readTree(new File(DEFINITIONS_PATH));
            JsonNode macros = definitions.get("macros");
            JsonNode groups = definitions.get("groups");

            System.out.println("=== 매크로 정의 파일 로드 완료 ===");
            System.out.println("시던 이벤트: " + sidunEvent);
            System.out.println("던전 이벤트: " + dungeonEvent + "\n");

            // 매크로 생성
            processMacroDefinitions(macros, sidunEvent, dungeonEvent);

            // 그룹 생성
            processGroupDefinitions(groups);

            System.out.println("\n=== 모든 매크로 생성 완료 ===");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 매크로 정의 처리
     */
    private static void processMacroDefinitions(JsonNode macros, String sidunEvent, String dungeonEvent) throws IOException {
        Iterator<String> categoryNames = macros.fieldNames();

        while (categoryNames.hasNext()) {
            String category = categoryNames.next();
            JsonNode categoryMacros = macros.get(category);

            System.out.println("\n--- 카테고리: " + category + " ---");

            for (JsonNode macroDef : categoryMacros) {
                String name = macroDef.get("name").asText();
                String type = macroDef.get("type").asText();
                JsonNode configNode = macroDef.get("config");

                Config config = buildConfig(configNode, sidunEvent, dungeonEvent);
                String[] scripts = buildScripts(type, config, configNode);

                if (scripts != null && scripts.length > 0) {
                    makeMacroFile(name, scripts, "1");
                }
            }
        }
    }

    /**
     * Config 객체 생성
     */
    private static Config buildConfig(JsonNode configNode, String sidunEvent, String dungeonEvent) {
        Config.Builder builder = Config.builder();

        // 기본 설정
        if (configNode.has("mainCharRepeat")) {
            builder.mainCharRepeat(configNode.get("mainCharRepeat").asInt());
        }
        if (configNode.has("character")) {
            builder.character(configNode.get("character").asText());
        }
        if (configNode.has("group")) {
            builder.group(configNode.get("group").asText());
        }
        if (configNode.has("scheduleTime")) {
            builder.scheduleTime(configNode.get("scheduleTime").asText());
        }
        if (configNode.has("scheduleOnly")) {
            builder.scheduleOnly(configNode.get("scheduleOnly").asBoolean());
        }

        // 이벤트 설정 (useCurrentEvent: true인 경우 macro-config.json에서 현재 이벤트 사용)
        if (configNode.has("useCurrentEvent") && configNode.get("useCurrentEvent").asBoolean()) {
            builder.sidunEvent(sidunEvent);
            builder.eventDungeon(dungeonEvent);
        }

        // 키 설정
        if (configNode.has("returnHomeKey")) {
            builder.returnHomeKey(configNode.get("returnHomeKey").asText());
        }
        if (configNode.has("returnKey")) {
            builder.returnKey(configNode.get("returnKey").asText());
        }
        if (configNode.has("dragonKey")) {
            builder.dragonKey(configNode.get("dragonKey").asText());
        }

        // 추가 필드 (JSON 파라미터명과 일치)
        if (configNode.has("isSubCharacter")) {
            builder.isSubCharacter(configNode.get("isSubCharacter").asInt());
        }
        if (configNode.has("waitTime")) {
            builder.waitTime(configNode.get("waitTime").asText());
        }
        if (configNode.has("potionEvent")) {
            builder.potionEvent(configNode.get("potionEvent").asBoolean());
        }
        if (configNode.has("worldDungeon")) {
            builder.worldDungeon(configNode.get("worldDungeon").asText());
        }

        return builder.build();
    }

    /**
     * 스크립트 타입에 따른 스크립트 배열 생성
     */
    private static String[] buildScripts(String type, Config config, JsonNode configNode) {
        switch (type) {
            case "mainCharacter":
                return ScriptBuilder.makeMainCharacter(config);
            case "subCharacter":
                return ScriptBuilder.makeSubCharacter(config);
            case "scheduleOnly":
                return ScriptBuilder.makeScheduleOnly(config);
            case "weekendAll":
                return ScriptBuilder.makeWeekendAll(config);
            default:
                System.out.println("알 수 없는 스크립트 타입: " + type);
                return new String[0];
        }
    }

    /**
     * 그룹 정의 처리
     */
    private static void processGroupDefinitions(JsonNode groups) throws IOException {
        System.out.println("\n=== 그룹 생성 ===");

        Iterator<String> groupNames = groups.fieldNames();
        while (groupNames.hasNext()) {
            String groupName = groupNames.next();
            JsonNode groupDef = groups.get(groupName);

            List<String> macroScripts = new ArrayList<>();
            List<String> resultScripts = new ArrayList<>();

            JsonNode macroScriptsNode = groupDef.get("macroScripts");
            if (macroScriptsNode != null) {
                for (JsonNode script : macroScriptsNode) {
                    macroScripts.add(script.asText());
                }
            }

            JsonNode resultScriptsNode = groupDef.get("resultScripts");
            if (resultScriptsNode != null) {
                for (JsonNode script : resultScriptsNode) {
                    resultScripts.add(script.asText());
                }
            }

            makeGroupFile(groupName,
                macroScripts.toArray(new String[0]),
                resultScripts.toArray(new String[0]));
        }
    }

    /**
     * 한글 파일명을 Windows 호환 형식(NFC)으로 정규화
     */
    private static String normalizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        return Normalizer.normalize(fileName, Normalizer.Form.NFC);
    }

    /**
     * result 폴더의 모든 파일을 삭제
     */
    private static void clearResultFolder() {
        File resultFolder = new File("src/result");
        File groupsFolder = new File("src/result/groups");

        if (!resultFolder.exists()) {
            resultFolder.mkdirs();
            System.out.println("result 폴더 생성: " + resultFolder.getAbsolutePath());
        }
        if (!groupsFolder.exists()) {
            groupsFolder.mkdirs();
            System.out.println("groups 폴더 생성: " + groupsFolder.getAbsolutePath());
        }

        int deletedCount = 0;

        File[] files = resultFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.delete()) {
                        deletedCount++;
                        System.out.println("삭제: " + file.getName());
                    }
                }
            }
        }

        File[] groupFiles = groupsFolder.listFiles();
        if (groupFiles != null) {
            for (File file : groupFiles) {
                if (file.isFile()) {
                    if (file.delete()) {
                        deletedCount++;
                        System.out.println("삭제: groups/" + file.getName());
                    }
                }
            }
        }

        if (deletedCount > 0) {
            System.out.println("총 " + deletedCount + "개 파일 삭제 완료\n");
        } else {
            System.out.println("삭제할 파일이 없습니다.\n");
        }
    }

    private static void makeMacroFile(String resultFileName, String[] ready, String count) throws IOException {
        String[] schedule = new String[ready.length + 1];
        for (int i = 0; i < ready.length; i++) {
            schedule[i] = ready[i];
        }
        schedule[ready.length] = "script_end";

        List<String> pathList = Arrays.stream(schedule)
            .map(JsonMergerMultiple::makePath)
            .collect(Collectors.toList());

        String normalizedFileName = normalizeFileName(resultFileName);
        String outputFile = "src/result/" + normalizedFileName + ".json";

        mergeJsonFiles(pathList, outputFile, normalizedFileName, count);
    }

    public static String makePath(String name) {
        String prefix = "src/main/resources/macro/";
        String ext = ".json";
        return prefix + name + ext;
    }

    /**
     * macro 폴더와 result 폴더의 JSON 파일들을 r0, r1, r2... 형태로 묶어서 하나의 파일로 생성
     */
    public static void makeGroupFile(String groupFileName, String[] macroFileNames, String[] resultFileNames) throws IOException {
        if ((macroFileNames == null || macroFileNames.length == 0) &&
            (resultFileNames == null || resultFileNames.length == 0)) {
            throw new IllegalArgumentException("묶을 파일이 없습니다.");
        }

        ObjectNode result = mapper.createObjectNode();
        ObjectNode records = mapper.createObjectNode();

        int totalFiles = (macroFileNames != null ? macroFileNames.length : 0) +
                         (resultFileNames != null ? resultFileNames.length : 0);

        System.out.println("\n=== 그룹 파일 생성: " + groupFileName + " ===");
        System.out.println("묶을 파일 개수: " + totalFiles);

        int recordIndex = 0;

        // macro 폴더의 파일들 먼저 추가
        if (macroFileNames != null) {
            for (String fileName : macroFileNames) {
                String filePath = "src/main/resources/macro/" + fileName + ".json";
                String recordKey = "r" + recordIndex;

                System.out.println("  " + recordKey + ": [macro] " + fileName);

                try {
                    JsonNode jsonNode = mapper.readTree(new File(filePath));
                    JsonNode recordsNode = jsonNode.get("records");
                    if (recordsNode != null && recordsNode.fields().hasNext()) {
                        JsonNode firstRecord = recordsNode.fields().next().getValue();
                        records.set(recordKey, firstRecord);
                    } else {
                        records.set(recordKey, jsonNode);
                    }
                } catch (IOException e) {
                    System.out.println("    오류: " + fileName + " 파일을 읽을 수 없습니다. - " + e.getMessage());
                    throw e;
                }
                recordIndex++;
            }
        }

        // result 폴더의 파일들 추가
        if (resultFileNames != null) {
            for (String fileName : resultFileNames) {
                String filePath = "src/result/" + fileName + ".json";
                String recordKey = "r" + recordIndex;

                System.out.println("  " + recordKey + ": [result] " + fileName);

                try {
                    JsonNode jsonNode = mapper.readTree(new File(filePath));
                    JsonNode recordsNode = jsonNode.get("records");
                    if (recordsNode != null && recordsNode.fields().hasNext()) {
                        JsonNode firstRecord = recordsNode.fields().next().getValue();
                        records.set(recordKey, firstRecord);
                    } else {
                        System.out.println("    경고: " + fileName + "에 records가 없습니다.");
                    }
                } catch (IOException e) {
                    System.out.println("    오류: " + fileName + " 파일을 읽을 수 없습니다. - " + e.getMessage());
                    throw e;
                }
                recordIndex++;
            }
        }

        result.set("records", records);

        File groupsFolder = new File("src/result/groups");
        if (!groupsFolder.exists()) {
            groupsFolder.mkdirs();
        }

        String normalizedFileName = normalizeFileName(groupFileName);
        String outputFile = "src/result/groups/" + normalizedFileName + ".json";

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), result);

        System.out.println("그룹 파일 생성 완료: " + outputFile);
        System.out.println("총 " + totalFiles + "개의 records 포함 (r0 ~ r" + (recordIndex - 1) + ")\n");
    }

    /**
     * N개의 JSON 파일을 순서대로 합치는 메서드
     */
    public static void mergeJsonFiles(List<String> inputFiles, String outputFile, String resultFileName, String count) throws IOException {
        if (inputFiles == null || inputFiles.isEmpty()) {
            throw new IllegalArgumentException("입력 파일 리스트가 비어있습니다.");
        }

        JsonNode firstJson = mapper.readTree(new File(inputFiles.get(0)));

        ObjectNode result = mapper.createObjectNode();
        ObjectNode records = mapper.createObjectNode();
        ObjectNode r8 = mapper.createObjectNode();

        JsonNode firstRecords = firstJson.get("records");
        JsonNode firstRecord = firstRecords.fields().next().getValue();
        copyMetadata(firstRecord, r8, resultFileName, count);

        ArrayNode mergedEvents = mapper.createArrayNode();
        long cumulativeMs = 0;

        for (int i = 0; i < inputFiles.size(); i++) {
            String filePath = inputFiles.get(i);
            System.out.println("처리 중: " + filePath + " (" + (i + 1) + "/" + inputFiles.size() + ")");

            JsonNode currentJson = mapper.readTree(new File(filePath));
            JsonNode currentRecords = currentJson.get("records");
            JsonNode currentRecord = currentRecords.fields().next().getValue();
            ArrayNode currentEvents = (ArrayNode) currentRecord.get("events");

            if (currentEvents == null || currentEvents.size() == 0) {
                System.out.println("경고: " + filePath + " 파일에 events가 없습니다.");
                continue;
            }

            // 마지막 파일인지 확인 (script_end.json)
            boolean isLastFile = (i == inputFiles.size() - 1);
            int addedCount = 0;
            long lastValidMs = 0;

            for (JsonNode event : currentEvents) {
                // type이 18446744073709551615인 경우 마지막 파일이 아니면 스킵
                String eventType = event.has("type") ? event.get("type").asText() : "";
                if ("18446744073709551615".equals(eventType) && !isLastFile) {
                    continue; // 스크립트 종료 이벤트는 마지막에만 추가
                }

                ObjectNode newEvent = event.deepCopy();
                long originalMs = event.get("ms").asLong();
                long adjustedMs = originalMs + cumulativeMs;
                newEvent.put("ms", String.valueOf(adjustedMs));
                mergedEvents.add(newEvent);
                addedCount++;
                lastValidMs = originalMs;
            }

            if (addedCount > 0) {
                cumulativeMs += lastValidMs;
            }

            System.out.println("완료: " + addedCount + "개 이벤트 추가, 누적 ms: " + cumulativeMs);
        }

        r8.set("events", mergedEvents);
        records.set("r8", r8);
        result.set("records", records);

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), result);

        System.out.println("\n=== 합치기 완료 ===");
        System.out.println("총 " + inputFiles.size() + "개 파일 처리");
        System.out.println("총 " + mergedEvents.size() + "개 이벤트");
        System.out.println("결과 파일: " + outputFile);
    }

    /**
     * 첫 번째 파일의 메타데이터를 결과 JSON에 복사
     */
    private static void copyMetadata(JsonNode sourceR8, ObjectNode targetR8, String resultFileName, String count) {
        if (sourceR8.has("version")) {
            targetR8.put("version", sourceR8.get("version").asText());
        }

        if (sourceR8.has("repeat")) {
            ObjectNode repeat = mapper.createObjectNode();
            JsonNode sourceRepeat = sourceR8.get("repeat");

            if (sourceRepeat.has("count")) {
                repeat.put("count", count);
            }
            if (sourceRepeat.has("duration")) {
                repeat.put("duration", sourceRepeat.get("duration").asText());
            }
            if (sourceRepeat.has("sel")) {
                repeat.put("sel", sourceRepeat.get("sel").asText());
            }

            targetR8.set("repeat", repeat);
        }

        String[] fields = {"timestamp", "interval", "speed", "title"};
        for (String field : fields) {
            if (sourceR8.has(field)) {
                if ("title".equals(field)) {
                    targetR8.put(field, resultFileName);
                } else {
                    targetR8.put(field, sourceR8.get(field).asText());
                }
            }
        }
    }
}
