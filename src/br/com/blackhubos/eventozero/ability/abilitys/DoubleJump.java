package br.com.blackhubos.eventozero.ability.abilitys;

import org.bukkit.entity.Player;

import br.com.blackhubos.eventozero.ability.Ability;

public class DoubleJump extends Ability{

	public DoubleJump(long cooldown) {
		super(cooldown);
		
	}

	@Override
	public boolean tryUse(Player player) {
		if(canUse()){
			foceUse(player);
			return true;
		}
		return false;
	}

	@Override
	public void foceUse(Player player) {
		player.setVelocity(player.getLocation().getDirection().multiply(0.5).setY(1));	
	}

}
