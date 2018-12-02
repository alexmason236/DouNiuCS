package com.zk.entity;

import java.util.ArrayList;
import java.util.List;

public class PlayGround {
	int pgId;
	List<Paticipator> players=new ArrayList<>();
	public int getPgId() {
		return pgId;
	}
	public void setPgId(int pgId) {
		this.pgId = pgId;
	}
	public List<Paticipator> getPlayers() {
		return players;
	}
	public void setPlayers(List<Paticipator> players) {
		this.players = players;
	}
}
