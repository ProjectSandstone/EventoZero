package br.com.blackhubos.eventozero.party;

import java.util.Vector;

import org.bukkit.entity.Player;

public class Party {
	
	private final Vector<Player> joineds;
	private final Vector<Player> inviteds;
	private final int max;
	
	public Party(int max){
		this.joineds = new Vector<>();
		this.inviteds = new Vector<>();
		this.max = max;
	}
	
	public Player getOwner(){
		return this.joineds.get(0);
	}
	
	public Vector<Player> getJoineds(){
		return this.joineds;
	}
	
	public Vector<Player> getInviteds(){
		return this.inviteds;
	}
	
	public Party playerJoin(Player player){
		this.joineds.add(player);
		return this;
	}
	
	public Party playerQuit(Player player){
		this.joineds.remove(player);
		return this;
	}
	
	public Party playerJoinInvited(Player player){
		this.inviteds.addElement(player);
		return this;
	}
	
	public Party playerQuitInvited(Player player){
		this.inviteds.remove(player);
		return this;
	}

}
