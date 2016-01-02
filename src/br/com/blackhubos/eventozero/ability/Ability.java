package br.com.blackhubos.eventozero.ability;

import org.bukkit.entity.Player;

public abstract class Ability {
	
	private long cooldown;
	private long lastTime;
	
	public Ability(long cooldown){
		this.cooldown = cooldown;
		this.lastTime = 0;
	}
	
	public long getRemaingTime(){
		return (lastTime - (cooldown * 1000));
	}
	
	public long getRemaingTimePostive(){
		return (getRemaingTime() >= 0? getRemaingTime() : 0);
	}
	
	public boolean canUse(){
		return (getRemaingTimePostive() == 0);
	}
	
	public abstract boolean tryUse(Player player);
	
	public abstract void foceUse(Player player);
	
}
