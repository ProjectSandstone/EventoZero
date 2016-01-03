package br.com.blackhubos.eventozero.ranking;

import br.com.blackhubos.eventozero.ConfigFiles;

public class Ranks {
	
	 ConfigFiles config = ConfigFiles.getInstance();
	 
	 public void teste() {
		 config.getMensagem().getString("Teste");
		 config.getRank().getString("Teste");
	 }

}
