package br.com.blackhubos.eventozero;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class ConfigFiles {

private ConfigFiles() { }
	
	static ConfigFiles instance = new ConfigFiles();
	
	public static ConfigFiles getInstance() {
		return instance;
	}
	
	Plugin p;
	
	FileConfiguration config;
	File cfile;
	
	FileConfiguration mensagem;
	File mfile;
	
	FileConfiguration rank;
	File rfile;
	
	
	public void setup(Plugin p) {
		cfile = new File(p.getDataFolder(), "config.yml");
		config = p.getConfig();
		
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		
		//Config das mensagens
		
		mfile = new File(p.getDataFolder(), "mensagens.yml");
		
		if (!mfile.exists()) {
			try {
				mfile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getLogger().severe("§4Impossivel de criar a mfile.yml!");
			}
		}
		
		mensagem = YamlConfiguration.loadConfiguration(mfile);
		
		//Config do ranking
		
		rfile = new File(p.getDataFolder(), "ranking.yml");
		
		if (!rfile.exists()) {
			try {
				rfile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getLogger().severe("�4Impossivel de criar a ranking.yml!");
			}
		}
		
		rank = YamlConfiguration.loadConfiguration(rfile);
		
	}
	
	public FileConfiguration getMensagem() {
		return mensagem;
	}
	
	public FileConfiguration getRank() {
		return rank;
	}
	
	public void saveMensagem() {
		try {
			mensagem.save(mfile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe("§4Impossivel de salvar a mensagens.yml!");
		}
	}
	
	public void saveRank() {
		try {
			rank.save(rfile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe("�4Impossivel de salvar a ranking.yml!");
		}

	}
	
	public void reloadMensagem() {
		mensagem = YamlConfiguration.loadConfiguration(mfile);
	}
	
	public void reloadRank() {
		rank = YamlConfiguration.loadConfiguration(rfile);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		try {
			config.save(cfile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe("�4Impossivel de salvar a config.yml!");
		}
	}
	
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}
	
	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}
	
}
