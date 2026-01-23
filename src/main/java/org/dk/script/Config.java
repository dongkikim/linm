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
	private String event;
	private String returnHomeKey;
	private String returnKey;
	private String dragonKey;
	private boolean scheduleOnly;
	private int mainCharRepeat;
	private String specialDungeon;

	// private 생성자 (Builder를 통해서만 생성)
	private Config() {
		// 기본값 설정
		this.returnHomeKey = "button_9";
		this.returnKey = "button_8";
		this.dragonKey = "button_10";
		this.scheduleOnly = false;
		this.mainCharRepeat = 0;
		this.event = "noevent";
		this.specialDungeon = "nospecialdugeon";
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

		public Builder event(String event) {
			config.event = event;
			return this;
		}

		public Builder specialDungeon(String specialDungeon) {
			config.specialDungeon = specialDungeon;
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

	public Config(int mainCharRepeat, String scheduleTime, String event, String specialDungeon) {
		this();
		this.mainCharRepeat = mainCharRepeat;
		this.scheduleTime = scheduleTime;
		this.event = event;
		this.specialDungeon = specialDungeon;
	}

	public Config(String character, String group, String scheduleTime, String event) {
		this();
		this.character = character;
		this.group = group;
		this.scheduleTime = scheduleTime;
		this.event = event;
	}

	public Config(String character, String group, String scheduleTime, String event, String specialDungeon) {
		this();
		this.character = character;
		this.group = group;
		this.scheduleTime = scheduleTime;
		this.event = event;
		this.specialDungeon = specialDungeon;
	}

	public Config(String character, String group, String scheduleTime, String event,
			String returnHomeKey, String returnKey, String dragonKey) {
		this();
		this.character = character;
		this.group = group;
		this.scheduleTime = scheduleTime;
		this.event = event;
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

	public String getEvent() {
		if ("noevent".equals(event)) {
			return "";
		} else {
			return "event";
		}
	}

	public String getEventRaw() {
		return event;
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

	public String getSpecialDungeon() {
		return specialDungeon;
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

	public boolean hasEvent() {
		return event != null && !"noevent".equals(event) && !"okevnet".equals(event) && !"".equals(event);
	}

	public boolean hasSpecialDungeon() {
		return specialDungeon != null && !"nospecialdugeon".equals(specialDungeon)
			&& !"okevnet".equals(specialDungeon) && !"".equals(specialDungeon);
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

	public void setEvent(String event) {
		this.event = event;
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
