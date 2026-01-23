package org.dk.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 스크립트 생성 통합 클래스
 * mainCharacter, subCharacter, scheduleOnly, weekend_all, weekend_hunt 기능을 통합
 */
public class ScriptBuilder {

    private final Config config;

    public ScriptBuilder(Config config) {
        this.config = config;
    }

    // ================================
    // 스크립트 타입별 빌드 메서드
    // ================================

    /**
     * 메인 캐릭터 스크립트 생성
     */
    public String[] buildMainCharacter() {
        ArrayList<String> scripts = new ArrayList<>();
        scripts.addAll(Arrays.asList(scriptInit()));

        if (!config.isScheduleOnly()) {
            scripts.addAll(Arrays.asList(scriptDailyCheck()));
            scripts.addAll(Arrays.asList(scriptBuyAll()));
        }

        for (int i = 1; i <= config.getMainCharRepeat(); i++) {
            if (!config.isScheduleOnly()) {
                scripts.addAll(Arrays.asList(scriptEventDungeon()));
                scripts.addAll(Arrays.asList(scriptEventSidun()));
                scripts.addAll(Arrays.asList(scriptSidunPadun()));

                if (config.hasScheduleTime()) {
                    scripts.addAll(Arrays.asList(scriptScheduleHunting()));
                }
            } else {
                scripts.addAll(Arrays.asList(scriptScheduleHuntingItemChange()));
            }
            scripts.addAll(Arrays.asList(scriptCharChange(i)));
        }

        scripts.addAll(Arrays.asList(scriptFinish()));
        return scripts.toArray(new String[0]);
    }

    /**
     * 보조 캐릭터 스크립트 생성
     */
    public String[] buildSubCharacter() {
        ArrayList<String> scripts = new ArrayList<>();
        scripts.addAll(Arrays.asList(scriptInit()));

        for (int i = 1; i <= config.getRepeat(); i++) {
            // 첫번째 캐릭터인 경우: 스케쥴 먼저 -> 출석체크, 일괄구매 -> 이벤트시던, 시던파던 -> 기란던전
            if (config.checkFirstAllstart() && i == 1) {
                // 스케쥴 먼저 실행
                if (config.hasScheduleTime()) {
                    scripts.addAll(Arrays.asList(scriptGroupScheduleHunting()));
                } else if (!config.checkFirstGroup()) {
                    scripts.addAll(Arrays.asList(scriptGroupDelay()));
                }

                // 출석체크, 일괄구매
                scripts.addAll(Arrays.asList(scriptDailyCheck()));
                scripts.addAll(Arrays.asList(scriptBuyAll()));

                // 이벤트시던, 시던파던
                scripts.addAll(Arrays.asList(scriptEventDungeon()));
                scripts.addAll(Arrays.asList(scriptEventSidun()));
                scripts.addAll(Arrays.asList(scriptSidunPadun()));
            }
            // 첫번째 캐릭터가 아닌 경우: 이벤트시던, 시던파던 -> 스케쥴
            else {
                // 이벤트시던, 시던파던
                scripts.addAll(Arrays.asList(scriptEventDungeon()));
                scripts.addAll(Arrays.asList(scriptEventSidun()));
                scripts.addAll(Arrays.asList(scriptSidunPadun()));

                if (config.hasScheduleTime()) {
                    scripts.addAll(Arrays.asList(scriptScheduleHunting()));
                }
            }

            // 기란던전
            scripts.addAll(Arrays.asList(scriptGiran()));

            // 캐릭터 전환
            scripts.addAll(Arrays.asList(scriptCharChange(i)));
        }

        scripts.addAll(Arrays.asList(scriptStoryIsland("prevstory_gisa")));
        scripts.addAll(Arrays.asList(scriptFinish()));

        return scripts.toArray(new String[0]);
    }

    /**
     * 스케쥴 전용 스크립트 생성
     */
    public String[] buildScheduleOnly() {
        ArrayList<String> scripts = new ArrayList<>();
        scripts.addAll(Arrays.asList(scriptInit()));

        for (int i = 1; i <= config.getMainCharRepeat(); i++) {
            scripts.addAll(Arrays.asList(scriptScheduleHunting()));
            scripts.addAll(Arrays.asList(scriptCharChange(i)));
        }

        scripts.addAll(Arrays.asList(scriptFinish()));
        return scripts.toArray(new String[0]);
    }

    /**
     * 주말 전체 월드 던전 스크립트 생성 (테베 -> 아틀란 -> 티칼)
     * @param isSubCharacter 0: 주캐릭터, 1: 보조캐릭터
     * @param waitTime 대기 시간 스크립트명 (예: "wait_hour_2")
     * @param isEvent 이벤트 여부
     */
    public static String[] buildWeekendAll(int isSubCharacter, String waitTime, boolean isEvent) {
        String[] tebe = buildWeekendHunt("tebe", isSubCharacter, waitTime, isEvent);
        String[] atlan = buildWeekendHunt("atlan", isSubCharacter, waitTime, isEvent);
        String[] tikal = buildWeekendHunt("tical", isSubCharacter, waitTime, isEvent);

        ArrayList<String> scripts = new ArrayList<>();
        scripts.addAll(Arrays.asList(tebe));
        scripts.addAll(Arrays.asList(atlan));
        scripts.addAll(Arrays.asList(tikal));

        return scripts.toArray(new String[0]);
    }

    /**
     * 개별 월드 던전 스크립트 생성 (테베, 티칼, 아틀란)
     * @param dungeonName 던전명 (tebe, atlan, tical)
     * @param isSubCharacter 0: 주캐릭터, 1: 보조캐릭터
     * @param waitTime 대기 시간 스크립트명
     * @param isEvent 이벤트 여부
     */
    public static String[] buildWeekendHunt(String dungeonName, int isSubCharacter, String waitTime, boolean isEvent) {
        String returnHome = "button_8"; // 일반귀환
        String potion = isEvent ? "world_buy_potion_half_event" : "world_buy_potion_event";

        if (isSubCharacter == 0) {
            // 주캐릭터용 스크립트
            return new String[]{
                "wait_sec_10",
                "world_move_" + dungeonName,
                "wait_sec_5",
                "world_hunting_" + dungeonName,
                "wait_sec_5",
                "power_save_on",
                waitTime,
                "power_save_off",
                returnHome
            };
        } else {
            // 보조캐릭터용 스크립트 (물약 구매 포함, 3회 반복)
            return new String[]{
                "wait_sec_10",
                "world_move_" + dungeonName,
                "wait_sec_5",
                potion,
                "wait_sec_5",
                "world_hunting_" + dungeonName,
                "wait_sec_5",
                "power_save_on",
                waitTime,
                "power_save_off",
                returnHome,
                "wait_sec_10",
                potion,
                "wait_sec_5",
                "world_hunting_" + dungeonName,
                "wait_sec_5",
                "power_save_on",
                waitTime,
                "power_save_off",
                returnHome,
                "wait_sec_10",
                potion,
                "wait_sec_5",
                "world_hunting_" + dungeonName,
                "wait_sec_5",
                "power_save_on",
                waitTime,
                "power_save_off",
                returnHome
            };
        }
    }

    // ================================
    // 개별 스크립트 생성 메서드
    // ================================

    /**
     * 초기화 스크립트
     */
    public String[] scriptInit() {
        return new String[]{
            "ESC",
            "wait_sec_2"
        };
    }

    /**
     * 출석체크 스크립트
     */
    public String[] scriptDailyCheck() {
        return new String[]{
            "daily_check",
            "wait_sec_5"
        };
    }

    /**
     * 일괄구매 스크립트
     */
    public String[] scriptBuyAll() {
        return new String[]{
            "buy_all",
            "wait_sec_5"
        };
    }

    /**
     * 이벤트 던전 스크립트
     */
    public String[] scriptEventDungeon() {
        if (config.hasSpecialDungeon()) {
            return new String[]{
                config.getSpecialDungeon(),
                "wait_sec_10",
                config.getReturnHomeKey(),
                "wait_sec_10"
            };
        }
        return new String[0];
    }

    /**
     * 이벤트 시던 스크립트
     */
    public String[] scriptEventSidun() {
        if (config.hasEvent()) {
            return new String[]{
                config.getEventRaw(),
                "wait_sec_10",
                config.getReturnHomeKey(),
                "wait_sec_10"
            };
        }
        return new String[0];
    }

    /**
     * 시던파던 스크립트
     */
    public String[] scriptSidunPadun() {
        return new String[]{
            "sidun_turn",
            "wait_sec_10",
            config.getReturnHomeKey(),
            "wait_sec_10",
            "sidun_mimic",
            "wait_sec_10",
            config.getReturnHomeKey(),
            "wait_sec_10",
            "party_death",
            "wait_sec_10",
            config.getReturnHomeKey(),
            "wait_sec_10",
            "party_orim",
            "wait_sec_10",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 스케줄 사냥 스크립트
     */
    public String[] scriptScheduleHunting() {
        if (!config.hasScheduleTime()) {
            return new String[0];
        }
        return new String[]{
            "run_schedule",
            "wait_sec_5",
            "power_save_on",
            config.getScheduleTime(),
            "power_save_off",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 스케줄 사냥 + 아이템 변경 스크립트 (스케줄 전용)
     */
    public String[] scriptScheduleHuntingItemChange() {
        if (!config.hasScheduleTime()) {
            return new String[0];
        }
        return new String[]{
            "item_change_choose_1",
            "wait_sec_5",
            "run_schedule",
            "wait_sec_5",
            "power_save_on",
            config.getScheduleTime(),
            "power_save_off",
            config.getReturnHomeKey(),
            "wait_sec_10",
            "item_change_choose_3",
            "wait_sec_5"
        };
    }

    /**
     * 그룹 스케줄 사냥 스크립트
     */
    public String[] scriptGroupScheduleHunting() {
        if (!config.hasScheduleTime()) {
            return new String[0];
        }
        return new String[]{
            "run_schedule",
            "wait_sec_5",
            "power_save_on",
            config.getScheduleTime(),
            "power_save_off",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 그룹 대기 스크립트
     */
    public String[] scriptGroupDelay() {
        return new String[]{
            "power_save_on",
            config.getGroup(),
            "power_save_off"
        };
    }

    /**
     * 캐릭터 변경 스크립트
     */
    public String[] scriptCharChange(int repeatIndex) {
        int charNum = getNextCharacterNumber(repeatIndex);
        return new String[]{
            "choose_menu",
            "button_restart",
            "changeChar_" + charNum + "_integration",
            "wait_sec_30",
            config.getReturnHomeKey(),
            "wait_sec_5"
        };
    }

    // 유효한 캐릭터 번호 (6, 7, 8번 파일 없음)
    private static final int[] VALID_CHAR_NUMBERS = {1, 2, 3, 4, 5, 9, 10, 11, 12, 13};

    /**
     * 반복 인덱스에 따른 다음 캐릭터 번호 계산
     */
    private int getNextCharacterNumber(int repeatIndex) {
        // character 설정에 따라 다음 캐릭터 번호 결정 (subCharacter용)
        String character = config.getCharacter();
        if (character != null) {
            // "1번" -> 1, "2번" -> 2 등
            String numStr = character.replace("번", "");
            try {
                int baseNum = Integer.parseInt(numStr);
                int targetNum = baseNum + repeatIndex;
                // 6, 7, 8번 스킵
                if (targetNum >= 6 && targetNum <= 8) {
                    targetNum += 3; // 6->9, 7->10, 8->11
                }
                return targetNum;
            } catch (NumberFormatException e) {
                return repeatIndex;
            }
        }

        // mainCharRepeat 기반일 경우 (mainCharacter용)
        // 13부터 역순으로: 13, 12, 11, 10, 9, 5, 4, 3, 2, 1 (6,7,8 스킵)
        int mainRepeat = config.getMainCharRepeat();
        int[] sequence = buildCharSequence(mainRepeat);
        if (repeatIndex > 0 && repeatIndex <= sequence.length) {
            return sequence[repeatIndex - 1];
        }
        return 1;
    }

    /**
     * mainCharRepeat에 따른 캐릭터 변경 순서 생성
     * 예: 13 -> [12, 11, 10, 9, 5, 4, 3, 2, 1] (13번에서 시작하므로 13은 제외)
     */
    private int[] buildCharSequence(int startChar) {
        java.util.List<Integer> sequence = new java.util.ArrayList<>();
        for (int i = startChar - 1; i >= 1; i--) {
            // 6, 7, 8번 스킵
            if (i >= 6 && i <= 8) continue;
            sequence.add(i);
        }
        return sequence.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 기란 던전 스크립트
     */
    public String[] scriptGiran() {
        return new String[]{
            "dungeon_giran",
            "wait_sec_10",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 말하는 섬 스크립트
     */
    public String[] scriptStoryIsland(String dungeonType) {
        return new String[]{
            dungeonType,
            "wait_sec_10",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 마무리 스크립트
     */
    public String[] scriptFinish() {
        return new String[]{
            "choose_menu",
            "button_restart",
            "changeChar_1_integration",
            "wait_sec_30",
            config.getReturnHomeKey(),
            "wait_sec_5"
        };
    }

    // ================================
    // 정적 팩토리 메서드 (편의성)
    // ================================

    /**
     * 메인 캐릭터 스크립트 생성 (정적 메서드)
     */
    public static String[] makeMainCharacter(Config config) {
        return new ScriptBuilder(config).buildMainCharacter();
    }

    /**
     * 보조 캐릭터 스크립트 생성 (정적 메서드)
     */
    public static String[] makeSubCharacter(Config config) {
        return new ScriptBuilder(config).buildSubCharacter();
    }

    /**
     * 스케쥴 전용 스크립트 생성 (정적 메서드)
     */
    public static String[] makeScheduleOnly(Config config) {
        return new ScriptBuilder(config).buildScheduleOnly();
    }

    /**
     * 주말 전체 월드 스크립트 생성 (정적 메서드)
     */
    public static String[] makeWeekendAll(int isSubCharacter, String waitTime, boolean isEvent) {
        return buildWeekendAll(isSubCharacter, waitTime, isEvent);
    }

    /**
     * 주말 개별 던전 스크립트 생성 (정적 메서드)
     */
    public static String[] makeWeekendHunt(String dungeonName, int isSubCharacter, String waitTime, boolean isEvent) {
        return buildWeekendHunt(dungeonName, isSubCharacter, waitTime, isEvent);
    }
}
