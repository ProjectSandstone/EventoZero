/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright � 2016 BlackHub OS and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package br.com.blackhubos.eventozero.factory;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import br.com.blackhubos.eventozero.handlers.AbilityHandler;

/*
 * TODO: Adicionar método para os "cuboIds" (example.yml#94) 
 * 
 * TODO: Adicionar método para adicionar um horario do evento, não achei um nome adequado pra
 * isso ainda.
 * 
 * TODO: Adicionar métodos para configurar as mensagens.
 */

/**
 * @author Leonardosc
 */
public interface EventBuilder {

	/**
	 * Define o nome do evento
	 * 
	 * @param name
	 */
	EventBuilder name(String name);

	/**
	 * Define se será ativado o sistema de partys no evento
	 * 
	 * @param enabled
	 */
	EventBuilder partyEnabled(boolean enabled);

	/**
	 * Define a quantidade máxima de jogadores permitido no evento.
	 * 
	 * @param maxPlayers
	 */
	EventBuilder maxPlayers(int maxPlayers);

	/**
	 * Define a quantidade mínima de jogadores permitido no evento.
	 * 
	 * @param minPlayers
	 */
	EventBuilder minPlayers(int minPlayers);

	/**
	 * Define o limite de jogadores permitido em uma party.
	 * 
	 * @param partySize Numero de jogadores permitido em uma party.
	 */
	EventBuilder partySize(int partySize);

	/**
	 * Define o nome de exibição do evento, que será exibido nas mensagens
	 * etc...
	 * 
	 * @param name Nome de exibição.
	 */
	EventBuilder displayName(String name);

	/**
	 * Define a descrição do evento.
	 * 
	 * @param desc Descrição do evento
	 */
	EventBuilder description(String desc);

	/**
	 * Define o tempo em segundos para finalizar o evento, caso não haja
	 * ganhadores.
	 * 
	 * @param seconds Tempo em segundos.
	 */
	EventBuilder timeToStop(int seconds);

	/**
	 * Define a quantidade de pontos que cada colocado irá ganhar.
	 * 
	 * <p>
	 * Exemplo:
	 * <p>
	 * 
	 * <pre>
	 * {@code
	 * 	Map<Integer, Integer> points = Maps.newHashMap();
	 * 	points.put(1, 10); // primeiro colocado irá ganhar 10 pontos
	 * 	points.put(2, 7); // segundo irá ganhar 7 pontos
	 * 	points.put(3, 3); // terceiro irá ganhar 3 pontos
	 * 	// e assim por diante
	 * }
	 * </pre>
	 * 
	 * @param points
	 *            Mapa com a quantidade de pontos que cada colocado irá ganhar
	 */
	EventBuilder points(Map<Integer, Integer> points);

	/**
	 * Define a quantidade de dinheiro que cada colocado irá ganhar.
	 * 
	 * <p>
	 * Exemplo:
	 * <p>
	 * 
	 * <pre>
	 * {@code
	 * Map<Integer, Integer> money = Maps.newHashMap();
	 * money.put(1, 100); // primeiro colocado irá ganhar 100 "dinheiros"
	 * money.put(2, 50); // segundo irá ganhar 50 "dinheiros"
	 * money.put(3, 20); // terceiro irá ganhar 20 "dinheiros"
	 * // e assim por diante}
	 * </pre>
	 * 
	 * @param money
	 *            Mapa com a quantidade de dinheiro que cada colocado irá ganhar
	 */
	EventBuilder money(Map<Integer, Integer> money);

	/**
	 * Define a quantidade de colocações, exemplo 1 ganhador, 2 ganhador e 3
	 * ganhador etc...
	 * 
	 * @param placements
	 *            Quantidade de colocações
	 */
	EventBuilder placements(int placements);
	
	/**
	 * Define as habilidades disponiveis no evento.
	 * 
	 * @param abilitys
	 *            Lista com o nome das habilidades disponiveis no evento.
	 * 
	 * @see AbilityHandler#getAbilityByName(String)
	 */
	EventBuilder abilitys(Set<String> abilitys );
	
	/**
	 * Define os shops disponiveis no evento.
	 * 
	 * @param shops
	 *            Lista com o nome das habilidades disponiveis no evento.
	 */
	EventBuilder shops(Set<String> shops);
	
	/**
	 * Define a localização do lobby do evento.
	 * 
	 * @param locations Lista de localização do lobby
	 */
	EventBuilder lobbyLocations(Set<Location> locations);
	
	/**
	 * Define a localização do spawn do evento.
	 * 
	 * @param locations Lista de localização do spawn.
	 */
	EventBuilder spawnLocations(Set<Location> locations);
	
	//TODO: documentar
	/**
	 * @param saveInventory
	 * @return
	 */
	EventBuilder safeInventory(boolean saveInventory);
	
	/**
	 * @return O Evento construido
	 */
	Event build();
}
