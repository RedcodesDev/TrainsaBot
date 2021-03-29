package de.visionvenue.trainsabot.user;

public enum Job {

	FARMER("👨‍🌾 Landwirt"), BAKER("🍞 Bäcker"), LUMBERJACK("🪓 Holzfäller"), MINER("⛏ Minenarbeiter"), FISHER("🎣 Fischer"), NONE("❌ Kein Job");

	private String value;
	
	Job(String value) {
		this.value = value;
	}
	
	public String getName() {
		return this.value;
	}

}
