/**
 *
 * EventoZero - .
 * Copyright © 2016 BlackHub OS.
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

/**
 *
 * @author <a href="https://github.com/ReedFlake/">ReedFlake (blackthog@gmail.com)</a>
 *
 */
public enum RankingAction
{

	INSERT(1), REMOVE(2);

	private int id;

	RankingAction(final int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public static RankingAction byId(final int id)
	{
		switch (id)
		{
			case 1:
			{
				return INSERT;
			}

			case 2:
			{
				return REMOVE;
			}

			default:
			{
				return INSERT;
			}
		}
	}

}
