package br.com.blackhubos.eventozero.kit;

import java.util.Vector;

import org.bukkit.plugin.Plugin;

public class KitHandler {
	
	private final Vector<Kit> kits;
	
	public KitHandler(){
		this.kits = new Vector<>();
	}
	
	public Kit getKitByName(String name){
		for(Kit kit : getKits()){
			if(kit.getName().equals(name)){
				return kit;
			}
		}
		return null;
	}
	
	public Vector<Kit> getKits(){
		return this.kits;
	}
	
	public void loadKits(Plugin plugin){
		
	}
	
}
