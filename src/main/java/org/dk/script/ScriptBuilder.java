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

        int startNum = getStartNum();
        int maxNum = config.getMainCharRepeat();
        int numIterations = maxNum - startNum + 1;

        if (getStartNum() == 1) {
            scripts.addAll(Arrays.asList(scriptDailyCheck()));
            scripts.addAll(Arrays.asList(scriptBuyAll()));
        }

        for (int i = 1; i <= numIterations; i++) {

            scripts.addAll(Arrays.asList(scriptEventSidun()));
            //통합버전제외
            //scripts.addAll(Arrays.asList(scriptSidunPadun()));

            scripts.addAll(Arrays.asList(scriptSidun()));
            scripts.addAll(Arrays.asList(scriptPadun()));
            scripts.addAll(Arrays.asList(scriptEventDungeon()));
            scripts.addAll(Arrays.asList(scriptMakeFavorite()));
            scripts.addAll(Arrays.asList(scriptDonate()));
            scripts.addAll(Arrays.asList(scriptDragonKey()));
            scripts.addAll(Arrays.asList(scriptPotionStorage()));

            if (config.hasScheduleTime()) {
                scripts.addAll(Arrays.asList(scriptScheduleHunting()));
            }

            // 캐릭터 변경
            int nextChar = getNextMainCharacterNumber(i);
            scripts.addAll(Arrays.asList(scriptCharChange(nextChar)));
        }

        scripts.addAll(Arrays.asList(scriptFinish()));
        return scripts.toArray(new String[0]);
    }

    /**
    * 메인 캐릭터 순차 스크립트 생성
    */
    public String[] buildMainCharacterSeq() {
        ArrayList<String> scripts = new ArrayList<>();

        scripts.addAll(Arrays.asList(scriptInit()));

        scripts.addAll(Arrays.asList(scriptDailyCheck()));
        scripts.addAll(Arrays.asList(scriptBuyAll()));

        int startNum = getStartNum();
        int maxNum = config.getMainCharRepeat();
        int numIterations = maxNum - startNum + 1;

        for (int i = 1; i <= numIterations; i++) {

            scripts.addAll(Arrays.asList(scriptEventDungeon()));
            scripts.addAll(Arrays.asList(scriptEventSidun()));
            scripts.addAll(Arrays.asList(scriptSidunPadun()));

            if (config.hasScheduleTime()) {
                scripts.addAll(Arrays.asList(scriptScheduleHunting()));
            }

            int nextChar = getNextMainCharacterNumber(i);
            scripts.addAll(Arrays.asList(scriptCharChange(nextChar)));
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

        int startNum = getStartNum();
        int maxNum = config.getMainCharRepeat();
        int numIterations = maxNum - startNum + 1;

        for (int i = 1; i <= numIterations; i++) {
            scripts.addAll(Arrays.asList(scriptItemFind()));
            scripts.addAll(Arrays.asList(scriptScheduleHunting()));
            //scripts.addAll(Arrays.asList(scriptScheduleHuntingItemChange()));
            scripts.addAll(Arrays.asList(scriptItemSave()));

            int nextChar = getNextMainCharacterNumber(i);
            scripts.addAll(Arrays.asList(scriptCharChange(nextChar)));
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

        int startNum = getStartNum();
        // 1->3회(1,2,3), 2->2회(2,3), 3->1회(3)
        int numIterations = 4 - startNum; 

        for (int i = 1; i <= numIterations; i++) {
            // 캐릭터 전환을 처음으로 이동
            int nextChar = getNextSubCharacterNumber(i);
            //if (getStartNum() != 1 || i != 1) {
                scripts.addAll(Arrays.asList(scriptCharChange(nextChar)));
            //}

            // 첫 번째 반복에서 그룹 대기 수행
            if (!config.checkFirstGroup()) {
                scripts.addAll(Arrays.asList(scriptGroupDelay()));
            }

            if(getStartNum() == 1 && i ==1 ) {
                scripts.addAll(Arrays.asList(scriptDailyCheck()));
                scripts.addAll(Arrays.asList(scriptBuyAll()));
            }


            // 이벤트시던, 시던파던
            scripts.addAll(Arrays.asList(scriptEventSidun()));

            // 통합버전제외
            //scripts.addAll(Arrays.asList(scriptSidunPadun()));
            scripts.addAll(Arrays.asList(scriptSidun()));
            scripts.addAll(Arrays.asList(scriptPadun()));
            scripts.addAll(Arrays.asList(scriptEventDungeon()));
            scripts.addAll(Arrays.asList(scriptMakeFavorite()));
            scripts.addAll(Arrays.asList(scriptDonate()));
            scripts.addAll(Arrays.asList(scriptDragonKey()));
            scripts.addAll(Arrays.asList(scriptPotionStorage()));

            if (config.hasScheduleTime()) {
                scripts.addAll(Arrays.asList(scriptScheduleHunting()));
            }
            scripts.addAll(Arrays.asList(scriptItemGreen()));
            // 기란던전
            scripts.addAll(Arrays.asList(scriptGiran()));
        }

        // 모든 반복 종료 후 1번 캐릭터로 전환
        scripts.addAll(Arrays.asList(scriptCharChange(1)));

        scripts.addAll(Arrays.asList(scriptStoryIsland("prevstory_gisa")));
        scripts.addAll(Arrays.asList(scriptFinish()));

        return scripts.toArray(new String[0]);
    }

    /**
     * 보조 캐릭터 스크립트 생성
     */
    public String[] buildOman() {
        ArrayList<String> scripts = new ArrayList<>();
        scripts.addAll(Arrays.asList(scriptInit()));

        int startNum = getStartNum();
        scripts.addAll(Arrays.asList(scriptCharChange(startNum)));


        scripts.addAll(Arrays.asList(new String[]{
                "oman"
        }));

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
     * 주말 악몽
     */
    public String[] buildWeekendNightMare(Config config) {
        ArrayList<String> scripts = new ArrayList<>();

        scripts.addAll(Arrays.asList(scriptInit()));

        int startNum = getStartNum();
        int maxNum = config.getMainCharRepeat();
        int numIterations = maxNum - startNum + 1;

        for (int i = 1; i <= numIterations; i++) {

            scripts.addAll(Arrays.asList(scriptItemFind()));
            if(getStartNum() == 1 && i ==1 ) {
                scripts.addAll(Arrays.asList(scriptWeekendNightMare(true)));
            }
            else {
                scripts.addAll(Arrays.asList(scriptWeekendNightMare(false)));
            }
            scripts.addAll(Arrays.asList(scriptItemSave()));

            // 캐릭터 변경
            int nextChar = getNextMainCharacterNumber(i);
            scripts.addAll(Arrays.asList(scriptCharChange(nextChar)));
        }

        scripts.addAll(Arrays.asList(scriptFinish()));
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

    /**
     * find
     */
    public String[] scriptItemFind()
    {
        return new String[] {
            "item_change_move_kenmal",
            "wait_sec_10",
            "item_change_find",
            "wait_sec_2",
            "item_change_choose_1",
            "wait_sec_2"
        };
    }

    /**
     * save
     */
    public String[] scriptItemSave()
    {
        return new String[] {
            "item_change_move_kenmal",
            "wait_sec_10",
            "item_change_choose_3",
            "wait_sec_2",
            "item_change_save",
            "wait_sec_10"
        };
    }
    /**
     * 악몽
     */
    public String[] scriptWeekendNightMare(boolean main) {

        if(main) {
            return new String[]{

                    "world_2_move",
                    "wait_sec_30",
                    "world_2_potion",
                    "wait_sec_10",
                    "world_wind",
                    "wait_sec_5",

                    "power_save_on",
                    "wait_hour_4",
                    "power_save_off",

                    config.getReturnHomeKey(),
                    "wait_sec_30",
            };
        }
        else {
            return new String[]{

                    "world_2_move",
                    "wait_sec_30",
                    "world_2_potion",
                    "wait_sec_10",
                    "world_wind",
                    "wait_sec_5",

                    "power_save_on",
                    "wait_hour_2",
                    "power_save_off",

                    config.getReturnKey(),
                    "wait_sec_10",
                    "world_2_potion",
                    "wait_sec_10",
                    "world_wind",
                    "wait_sec_5",

                    "power_save_on",
                    "wait_hour_2",
                    "power_save_off",

                    config.getReturnHomeKey(),
                    "wait_sec_30",
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
     * 시던 스크립트
     * 시던 이벤트가 있으면 이벤트 버전(sidun_turnevent, sidun_mimicevent) 사용
     */
    public String[] scriptSidun() {
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
                "wait_sec_5"
        };
    }

    /**
     * 파던 스크립트
     */
    public String[] scriptPadun() {
        return new String[]{
                "party_death",
                config.getReturnHomeKey(),
                config.getReturnHomeKey(),
                "wait_sec_10",

                "party_orim",
                config.getReturnHomeKey(),
                config.getReturnHomeKey(),
                "wait_sec_2",
                config.getReturnHomeKey(),
                "wait_sec_10"
        };
    }

    /**
     * 제작 스크립트
     */
    public String[] scriptMakeFavorite() {
        return new String[]{
                "make_favorite",
                "wait_sec_2"
        };
    }

    /**
     * 기부 스크립트
     */
    public String[] scriptDonate() {
        return new String[]{
                "donate",
                "wait_sec_2"
        };
    }

    /**
     * 용옥 스크립트
     */
    public String[] scriptDragonKey() {
        return new String[]{
                config.getDragonKey(),
                "wait_sec_2"
        };
    }
    /**
     * 고급획득변경 스크립트
     */
    public String[] scriptPotionStorage() {
        return new String[]{
                "item_change_move_kenmal",
                "wait_sec_10",
                "potion_storage",
                "wait_sec_5"
        };
    }
    /**
     * 고급획득변경 스크립트
     */
    public String[] scriptItemGreen() {
        return new String[]{
                "get_item_green",
                "wait_sec_2"
        };
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
            config.getDragonKey(),
            "wait_sec_2",
            "get_item_green",
            "wait_sec_2"
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
    public String[] scriptCharChange(int charNum) {
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
     * 메인 캐릭터용 다음 번호 계산
     */
    private int getNextMainCharacterNumber(int repeatIndex) {
        int startNum = getStartNum();
        int maxNum = config.getMainCharRepeat();

        // 현재 플레이 중인 캐릭터 번호
        int currentNum = startNum + repeatIndex - 1;

        // 현재 플레이한 캐릭터가 설정된 최대 번호(maxNum)라면 1번으로 복귀
        if (currentNum >= maxNum) {
            return 1;
        }

        // 그 외에는 다음 번호로 변경
        return currentNum + 1;
    }

    /**
     * 시작 캐릭터 번호 추출 (기본값 1)
     */
    private int getStartNum() {
        String character = config.getCharacter();
        if (character != null) {
            try {
                return Integer.parseInt(character.replace("번", ""));
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }

    /**
     * 보조 캐릭터용 다음 번호 계산 (3개 캐릭터 순환)
     */
    private int getNextSubCharacterNumber(int repeatIndex) {
        int startNum = getStartNum();
        
        // (startNum + repeatIndex - 2) % 3 + 1
        // 예: start=1, i=1 -> (1+1-2)%3 + 1 = 1
        // 예: start=1, i=4 -> (1+4-2)%3 + 1 = 1
        // 예: start=2, i=1 -> (2+1-2)%3 + 1 = 2
        return (startNum + repeatIndex - 2) % 3 + 1;
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
            config.getReturnHomeKey(),
            "wait_sec_10",
            config.getReturnKey(),
            "wait_sec_10",
            "dungeon_giran",
            "power_save_on",
            "wait_hour_5",
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
     * 주말 악몽 월드 스크립트 생성 (정적 메서드)
     */
    public static String[] makeWeekendNightMare(Config config) {
        return new ScriptBuilder(config).buildWeekendNightMare(config);
    }

    /**
     * 주말 전체 월드 스크립트 생성 (정적 메서드)
     */
    public static String[] makeOman(Config config) {
        return new ScriptBuilder(config).buildOman();
    }

    /**
     * 주말 개별 던전 스크립트 생성 (정적 메서드)
     */
    public static String[] makeWeekendHunt(Config config) {
        return buildWeekendHunt(config);
    }
}
