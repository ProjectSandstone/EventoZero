/**
 *
 * EventoZero - Advanced event factory and executor for Bukkit and Spigot.
 * Copyright © 2016 BlackHub OS and contributors.
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
package br.com.blackhubos.eventozero.ranking;

import java.io.Serializable;

import br.com.blackhubos.eventozero.storage.Storage;

public enum Ranking implements Serializable
{

	VITÓRIAS("vitorias", 1), DERROTAS("derrotas", 2), MORTES("mortes", 3);

	private int id;
	private String column;

	private Ranking(final String column, final int id)
	{
		this.id = id;
		this.column = column;
	}

	public String getTable()
	{
		return Storage.Module.RANKING.getTable();
	}

	public String getColuna()
	{
		return this.column;
	}

	public static String byId(final int id)
	{
		switch (id)
		{
			case 1:
			{
				return VITÓRIAS.getColuna();
			}

			case 2:
			{
				return DERROTAS.getColuna();
			}

			case 3:
			{
				return MORTES.getColuna();
			}

			default:
			{
				return VITÓRIAS.getColuna();
			}
		}
	}

	public int getId()
	{
		return this.id;
	}

}
