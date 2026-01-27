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

        scripts.addAll(Arrays.asList(scriptDailyCheck()));
        scripts.addAll(Arrays.asList(scriptBuyAll()));

        for (int i = 1; i <= config.getMainCharRepeat(); i++) {

            scripts.addAll(Arrays.asList(scriptEventDungeon()));
            scripts.addAll(Arrays.asList(scriptEventSidun()));
            scripts.addAll(Arrays.asList(scriptSidunPadun()));

            if (config.hasScheduleTime()) {
                scripts.addAll(Arrays.asList(scriptScheduleHunting()));
            }
            scripts.addAll(Arrays.asList(scriptCharChange(i)));
        }

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
            scripts.addAll(Arrays.asList(scriptScheduleHuntingItemChange()));
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
                if (!config.checkFirstGroup()) {
                    scripts.addAll(Arrays.asList(scriptGroupDelay()));
                }

                // 출석체크, 일괄구매
                scripts.addAll(Arrays.asList(scriptDailyCheck()));
                scripts.addAll(Arrays.asList(scriptBuyAll()));
            }

            // 이벤트시던, 시던파던
            scripts.addAll(Arrays.asList(scriptEventDungeon()));
            scripts.addAll(Arrays.asList(scriptEventSidun()));
            scripts.addAll(Arrays.asList(scriptSidunPadun()));

            if (config.hasScheduleTime()) {
                scripts.addAll(Arrays.asList(scriptScheduleHunting()));
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
     * 주말 전체 월드 던전 스크립트 생성 (테베 -> 아틀란 -> 티칼)
     * Config 필드 사용: isSubCharacter, waitTime, isEvent
     */
    public static String[] buildWeekendAll(Config config) {
        int isSubCharacter = config.getIsSubCharacter();
        String waitTime = config.getWaitTime();
        boolean potionEvent = config.isPotionEvent();

        String[] tebe = buildWeekendHuntInternal("tebe", isSubCharacter, waitTime, potionEvent);
        String[] atlan = buildWeekendHuntInternal("atlan", isSubCharacter, waitTime, potionEvent);
        String[] tikal = buildWeekendHuntInternal("tical", isSubCharacter, waitTime, potionEvent);

        ArrayList<String> scripts = new ArrayList<>();
        scripts.addAll(Arrays.asList(tebe));
        scripts.addAll(Arrays.asList(atlan));
        scripts.addAll(Arrays.asList(tikal));

        return scripts.toArray(new String[0]);
    }

    /**
     * 개별 월드 던전 스크립트 생성 (테베, 티칼, 아틀란)
     * Config 필드 사용: worldDungeon, isSubCharacter, waitTime, potionEvent
     */
    public static String[] buildWeekendHunt(Config config) {
        String worldDungeon = config.getWorldDungeon();
        int isSubCharacter = config.getIsSubCharacter();
        String waitTime = config.getWaitTime();
        boolean potionEvent = config.isPotionEvent();

        return buildWeekendHuntInternal(worldDungeon, isSubCharacter, waitTime, potionEvent);
    }

    /**
     * 개별 월드 던전 스크립트 생성 (내부용)
     */
    private static String[] buildWeekendHuntInternal(String worldDungeon, int isSubCharacter, String waitTime, boolean potionEvent) {
        String returnHome = "button_8"; // 일반귀환
        String potion = potionEvent ? "world_buy_potion_half_event" : "world_buy_potion_half";

        if (isSubCharacter == 0) {
            // 주캐릭터용 스크립트
            return new String[]{
                "wait_sec_10",
                "world_move_" + worldDungeon,
                "wait_sec_5",
                "world_hunting_" + worldDungeon,
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
                "world_move_" + worldDungeon,
                "wait_sec_5",
                potion,
                "wait_sec_5",
                "world_hunting_" + worldDungeon,
                "wait_sec_5",
                "power_save_on",
                waitTime,

                "power_save_off",
                returnHome,
                "wait_sec_10",
                potion,
                "wait_sec_5",
                "world_hunting_" + worldDungeon,
                "wait_sec_5",
                "power_save_on",
                waitTime,

                "power_save_off",
                returnHome,
                "wait_sec_10",
                potion,
                "wait_sec_5",
                "world_hunting_" + worldDungeon,
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
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 출석체크 스크립트
     */
    public String[] scriptDailyCheck() {
        return new String[]{
            "daily_check",
            "wait_sec_2"
        };
    }

    /**
     * 일괄구매 스크립트
     */
    public String[] scriptBuyAll() {
        return new String[]{
            "buy_all",
            "wait_sec_2"
        };
    }

    /**
     * 이벤트 던전 스크립트
     */
    public String[] scriptEventDungeon() {
        if (config.hasEventDungeon()) {
            return new String[]{
                config.getEventDungeon(),
                config.getReturnHomeKey(),
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
        if (config.hasSidunEvent() && !"event".equals(config.getSidunEvent())) {
            return new String[]{
                config.getSidunEvent(),
                config.getReturnHomeKey(),
                config.getReturnHomeKey(),
                "wait_sec_5"
            };
        }
        return new String[0];
    }

    /**
     * 시던파던 스크립트
     * 시던 이벤트가 있으면 이벤트 버전(sidun_turnevent, sidun_mimicevent) 사용
     */
    public String[] scriptSidunPadun() {
        String sidunTurn = config.hasSidunEvent() ? "sidun_turnevent" : "sidun_turn";
        String sidunMimic = config.hasSidunEvent() ? "sidun_mimicevent" : "sidun_mimic";

        return new String[]{
            sidunTurn,
            config.getReturnHomeKey(),
            config.getReturnHomeKey(),
            "wait_sec_5",

            sidunMimic,
            config.getReturnHomeKey(),
            config.getReturnHomeKey(),
            "wait_sec_5",

            "party_death",
            config.getReturnHomeKey(),
            config.getReturnHomeKey(),
            "wait_sec_10",

            "party_orim",
            config.getReturnHomeKey(),
            config.getReturnHomeKey(),
            "wait_sec_10",

            "make_favorite",
            "donate",

            "wait_sec_2",
            "get_quest",
            "get_mail",
            "wait_sec_2",
            config.getDragonKey(),
            "wait_sec_2",
            "get_item_green",
            "wait_sec_2",
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
            "get_item_white",
            "wait_sec_5",
            "run_schedule",
            "wait_sec_30",
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
                "item_change_move_kenmal",
                "wait_sec_10",

                "get_item_white",
                "wait_sec_2",
                "item_change_find",
                "wait_sec_2",
                "item_change_choose_1",
                "wait_sec_2",

                "run_schedule",
                "wait_sec_30",
                "power_save_on",
                config.getScheduleTime(),
                "power_save_off",
                config.getReturnHomeKey(),

                "wait_sec_10",
                "item_change_move_kenmal",
                "wait_sec_10",
                "item_change_choose_3",
                "wait_sec_2",
                "item_change_save",
                "wait_sec_2"

        };
    }

    /**
     * 그룹 대기 스크립트
     */
    public String[] scriptGroupDelay() {
        return new String[]{
            "run_schedule",
            "wait_sec_30",
            "power_save_on",
            config.getGroup(),
            "power_save_off",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 캐릭터 변경 스크립트
     */
    public String[] scriptCharChange(int repeatIndex) {
        int charNum = getNextCharacterNumber(repeatIndex);
        return new String[]{
            "get_quest",
            "get_mail",
            "wait_sec_2",
            "changeChar_" + charNum + "_integration",
        };
    }

    // 유효한 캐릭터 번호 (6, 7, 8번 파일 없음)
    private static final int[] VALID_CHAR_NUMBERS = {1, 2, 3, 4, 5, 9, 10, 11, 12, 13};

    /**
     * 반복 인덱스에 따른 다음 캐릭터 번호 계산
     */
    private int getNextCharacterNumber(int repeatIndex) {
        String character = config.getCharacter();
        if (character != null) {
            String numStr = character.replace("번", "");
            try {
                // "번" 제거 및 숫자 변환
                int baseNum = Integer.parseInt(character.replace("번", ""));

                // 순환 로직: (baseNum + repeatIndex - 1) % 3 + 1
                return (baseNum + repeatIndex - 1) % 3 + 1;
            } catch (NumberFormatException e) {
                return repeatIndex;
            }
        }

        // mainCharRepeat 기반일 경우 (mainCharacter용)
        if(repeatIndex == config.getMainCharRepeat())
            return 1;
        else
            return config.getMainCharRepeat();
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
            config.getReturnKey(),
            "wait_sec_10",
            "dungeon_giran",
            "power_save_on",
            "wait_hour_2.5",
            "power_save_off",
            "get_quest",
            "get_mail",
            "power_save_on",
            "wait_hour_2.5",
            "power_save_off",
            config.getReturnHomeKey(),
            "wait_sec_10"
        };
    }

    /**
     * 말하는 섬 스크립트
     */
    public String[] scriptStoryIsland(String dungeonType) {
        return new String[]{
            "get_item_green",
            "wait_sec_2",
            dungeonType,
            "power_save_on",
            "wait_hour_1",
            "power_save_off",
            config.getReturnHomeKey(),
        };
    }

    /**
     * 마무리 스크립트
     */
    public String[] scriptFinish() {
        return new String[]{
            "run_schedule",
            "wait_sec_30",
            "power_save_on"
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
    public static String[] makeWeekendAll(Config config) {
        return buildWeekendAll(config);
    }

    /**
     * 주말 개별 던전 스크립트 생성 (정적 메서드)
     */
    public static String[] makeWeekendHunt(Config config) {
        return buildWeekendHunt(config);
    }
}
