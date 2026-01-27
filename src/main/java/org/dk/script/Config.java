package org.dk.script;

import java.util.Map;

/**
 * 매크로 설정 클래스 - Builder 패턴 적용
 */
public class Config
{
	// 스케줄 시간 매핑
	private static final Map<String, String> SCHEDULE_TIME_MAP = Map.of(
		"03m", "wait_min_3",
		"10m", "wait_min_10",
		"20m", "wait_min_20",
		"30m", "wait_min_30",
		"01h", "wait_hour_1",
		"02h", "wait_hour_2"
	);

	// 그룹 매핑
	private static final Map<String, String> GROUP_MAP = Map.of(
		"A", "wait_group_A",
		"B", "wait_group_B"
	);

	// 캐릭터별 반복 횟수
	private static final Map<String, Integer> CHARACTER_REPEAT_MAP = Map.of(
		"1번", 3,
		"2번", 2,
		"3번", 1
	);

	// 버튼 키 매핑
	private static final Map<String, String> BUTTON_KEY_MAP = Map.of(
		"8번", "button_8",
		"9번", "button_9",
		"10번", "button_10"
	);

	// 필드
	private String character;
	private String group;
	private String scheduleTime;
	private String sidunEvent;        // 시던 이벤트 스크립트명 (예: sidun_event_christmas)
	private String returnHomeKey;
	private String returnKey;
	private String dragonKey;
	private boolean scheduleOnly;
	private int mainCharRepeat;
	private String eventDungeon;      // 이벤트 던전 스크립트명 (예: event_dugeon_icequeen)

	// 추가 필드 (JSON 파라미터명과 일치)
	private int isSubCharacter;
	private String waitTime;
	private boolean potionEvent;      // 물약 이벤트 여부 (weekend용)
	private String worldDungeon;      // 월드 던전명 (weekend용: tebe, atlan, tical)

	// private 생성자 (Builder를 통해서만 생성)
	private Config() {
		// 기본값 설정
		this.returnHomeKey = "button_9";
		this.returnKey = "button_8";
		this.dragonKey = "button_10";
		this.scheduleOnly = false;
		this.mainCharRepeat = 0;
		this.sidunEvent = "";
		this.eventDungeon = "";

		// 추가 필드 기본값
		this.isSubCharacter = 0;
		this.waitTime = "";
		this.potionEvent = false;
		this.worldDungeon = "";
	}

	// Builder 생성
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder 클래스
	 */
	public static class Builder {
		private Config config;

		public Builder() {
			config = new Config();
		}

		public Builder character(String character) {
			config.character = character;
			return this;
		}

		public Builder group(String group) {
			config.group = group;
			return this;
		}

		public Builder scheduleTime(String scheduleTime) {
			config.scheduleTime = scheduleTime;
			return this;
		}

		public Builder sidunEvent(String sidunEvent) {
			if(sidunEvent == null || "".equals(sidunEvent)) {
				config.sidunEvent = "event";
			} else {
				config.sidunEvent = sidunEvent;
				}
			return this;
		}

		public Builder eventDungeon(String eventDungeon) {
			config.eventDungeon = eventDungeon;
			return this;
		}

		public Builder returnHomeKey(String returnHomeKey) {
			config.returnHomeKey = returnHomeKey;
			return this;
		}

		public Builder returnKey(String returnKey) {
			config.returnKey = returnKey;
			return this;
		}

		public Builder dragonKey(String dragonKey) {
			config.dragonKey = dragonKey;
			return this;
		}

		public Builder scheduleOnly(boolean scheduleOnly) {
			config.scheduleOnly = scheduleOnly;
			return this;
		}

		public Builder mainCharRepeat(int mainCharRepeat) {
			config.mainCharRepeat = mainCharRepeat;
			return this;
		}

		public Builder isSubCharacter(int isSubCharacter) {
			config.isSubCharacter = isSubCharacter;
			return this;
		}

		public Builder waitTime(String waitTime) {
			config.waitTime = waitTime;
			return this;
		}

		public Builder potionEvent(boolean potionEvent) {
			config.potionEvent = potionEvent;
			return this;
		}

		public Builder worldDungeon(String worldDungeon) {
			config.worldDungeon = worldDungeon;
			return this;
		}

		public Config build() {
			return config;
		}
	}

	// === 기존 생성자 (하위 호환성 유지) ===

	public Config(int mainCharRepeat, String scheduleTime, boolean scheduleOnly) {
		this();
		this.mainCharRepeat = mainCharRepeat;
		this.scheduleTime = scheduleTime;
		this.scheduleOnly = scheduleOnly;
	}

	public Config(int mainCharRepeat, String scheduleTime, boolean scheduleOnly,
			String returnHomeKey, String returnKey, String dragonKey) {
		this();
		this.mainCharRepeat = mainCharRepeat;
		this.scheduleTime = scheduleTime;
		this.scheduleOnly = scheduleOnly;
		this.returnHomeKey = returnHomeKey;
		this.returnKey = returnKey;
		this.dragonKey = dragonKey;
	}

	public Config(int mainCharRepeat, String scheduleTime, String sidunEvent, String eventDungeon) {
		this();
		this.mainCharRepeat = mainCharRepeat;
		this.scheduleTime = scheduleTime;
		this.sidunEvent = sidunEvent;
		this.eventDungeon = eventDungeon;
	}

	public Config(String character, String group, String scheduleTime, String sidunEvent) {
		this();
		this.character = character;
		this.group = group;
		this.scheduleTime = scheduleTime;
		this.sidunEvent = sidunEvent;
	}

	public Config(String character, String group, String scheduleTime, String sidunEvent, String eventDungeon) {
		this();
		this.character = character;
		this.group = group;
		this.scheduleTime = scheduleTime;
		this.sidunEvent = sidunEvent;
		this.eventDungeon = eventDungeon;
	}

	public Config(String character, String group, String scheduleTime, String sidunEvent,
			String returnHomeKey, String returnKey, String dragonKey) {
		this();
		this.character = character;
		this.group = group;
		this.scheduleTime = scheduleTime;
		this.sidunEvent = sidunEvent;
		this.returnHomeKey = returnHomeKey;
		this.returnKey = returnKey;
		this.dragonKey = dragonKey;
	}

	// === Getter 메서드 (매핑 적용) ===

	public String getGroup() {
		return GROUP_MAP.getOrDefault(group, "wait_group_A");
	}

	public String getGroupRaw() {
		return group;
	}

	public String getSidunEvent() {
		return sidunEvent;
	}

	public String getScheduleTime() {
		return SCHEDULE_TIME_MAP.getOrDefault(scheduleTime, "");
	}

	public String getScheduleTimeRaw() {
		return scheduleTime;
	}

	public String getReturnHomeKey() {
		return BUTTON_KEY_MAP.getOrDefault(returnHomeKey, "button_9");
	}

	public String getReturnKey() {
		return BUTTON_KEY_MAP.getOrDefault(returnKey, "button_8");
	}

	public String getDragonKey() {
		return BUTTON_KEY_MAP.getOrDefault(dragonKey, "button_10");
	}

	public int getRepeat() {
		return CHARACTER_REPEAT_MAP.getOrDefault(character, 3);
	}

	public String getCharacter() {
		return character;
	}

	public boolean isScheduleOnly() {
		return scheduleOnly;
	}

	public int getMainCharRepeat() {
		return mainCharRepeat;
	}

	public String getEventDungeon() {
		return eventDungeon;
	}

	public int getIsSubCharacter() {
		return isSubCharacter;
	}

	public String getWaitTime() {
		return waitTime;
	}

	public boolean isPotionEvent() {
		return potionEvent;
	}

	public String getWorldDungeon() {
		return worldDungeon;
	}

	// === 체크 메서드 ===

	public boolean checkFirstAllstart() {
		return "1번".equals(character);
	}

	public boolean checkFirstGroup() {
		return "A".equals(group);
	}

	public boolean hasScheduleTime() {
		return !"".equals(getScheduleTime());
	}

	public boolean hasSidunEvent() {
		return sidunEvent != null && !sidunEvent.isEmpty();
	}

	public boolean hasEventDungeon() {
		return eventDungeon != null && !eventDungeon.isEmpty();
	}

	// === Setter 메서드 ===

	public void setCharacter(String character) {
		this.character = character;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public void setSidunEvent(String sidunEvent) {
		this.sidunEvent = sidunEvent;
	}

	public void setReturnHomeKey(String returnHomeKey) {
		this.returnHomeKey = returnHomeKey;
	}

	public void setReturnKey(String returnKey) {
		this.returnKey = returnKey;
	}

	public void setDragonKey(String dragonKey) {
		this.dragonKey = dragonKey;
	}
}
