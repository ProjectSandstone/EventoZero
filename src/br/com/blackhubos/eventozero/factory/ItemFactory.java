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
package br.com.blackhubos.eventozero.factory;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import br.com.blackhubos.eventozero.util.Framework;

public final class ItemFactory
{

	private static final Pattern pattern_name = Pattern.compile("(?:nome|name|nm|nm)\\s*(?:\\{)\\s*'+([^']+)'+\\s*(?:\\})", Pattern.CASE_INSENSITIVE);
	private static final Pattern pattern_item = Pattern.compile("(?:item|id|data|itemstack|stack|is|slot)\\s*(?:\\{)\\s*([0-9]+)\\s*(?::\\s*([0-9]+))*\\s*(?:\\})", Pattern.CASE_INSENSITIVE);
	private static final Pattern pattern_encantos_outside = Pattern.compile("(?:encantos|enchants|enchantments|encants|enc|ench|powers|power)\\s*(?:\\{)\\s*([a-zA-Z0-9\\s\\,_-]+)\\s*(?:\\})", Pattern.CASE_INSENSITIVE);
	private static final Pattern pattern_encantos_inside = Pattern.compile("([a-zA-Z_-]+)\\s*(?:,|-)\\s*([0-9]+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern pattern_desc_outside = Pattern.compile("(?:desc|description|descricao|descrição|descr|d|lore|info|subinfo|subname)\\s*(?:\\{)\\s*([^\\)]+)\\}", Pattern.CASE_INSENSITIVE);
	private static final Pattern pattern_desc_inside = Pattern.compile("'+\\s*([^']+)\\s*'+", Pattern.CASE_INSENSITIVE);
	private static final Pattern pattern_amount = Pattern.compile("(?:quantia|amount|size|total|amnt|qnt)\\s*(?:\\{)\\s*([0-9]+)\\}", Pattern.CASE_INSENSITIVE);
	private ItemStack item;

	public static void main(final String[] args)
	{
		final Matcher m = ItemFactory.pattern_desc_outside.matcher("desc('oi', 'tchau')");
		System.out.println(m.find());
		final String t = m.group(1);
		final Matcher r = ItemFactory.pattern_desc_inside.matcher(t);
		r.find();
		System.out.println(r.group(1));
	}

	public ItemFactory(final String script, final HashMap<String, String> replaces)
	{
		final Matcher item = ItemFactory.pattern_item.matcher(script);
		if (!item.find())
		{
			return;
		}

		final int id = Integer.parseInt(item.group(1));
		byte data = 0;

		if (item.group(2) != null)
		{
			data = (byte) Integer.parseInt(item.group(2));
		}

		final ItemStack is = new ItemStack(id);
		final MaterialData isdata = is.getData();
		final ItemMeta meta = is.getItemMeta();
		isdata.setData(data);
		is.setData(isdata);

		final Matcher name = ItemFactory.pattern_name.matcher(script);
		if (name.find())
		{
			final String isname = name.group(1);
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.replaces(isname, replaces)));
		}

		final Matcher desc = ItemFactory.pattern_desc_outside.matcher(script);
		if (desc.find())
		{
			final Matcher desc2 = ItemFactory.pattern_desc_inside.matcher(desc.group(1));
			final Vector<String> lore = new Vector<String>();
			while (desc2.find())
			{
				String text = desc2.group(1);
				text = this.replaces(text, replaces);
				text = ChatColor.translateAlternateColorCodes('&', text);
				lore.add(text);
			}

			meta.setLore(lore);
		}

		final Matcher ench = ItemFactory.pattern_encantos_outside.matcher(script);
		if (ench.find())
		{
			final Matcher enchs = ItemFactory.pattern_encantos_inside.matcher(ench.group(1));
			while (enchs.find())
			{
				final String encanto = enchs.group(1);
				final int level = Integer.parseInt(enchs.group(2));
				final Enchantment e = Framework.checkEnchantment(encanto);
				if (e != null)
				{
					is.addUnsafeEnchantment(e, level);
				}
			}
		}

		final Matcher qnt = ItemFactory.pattern_amount.matcher(script);
		if (qnt.find())
		{
			final int quantia = Integer.parseInt(qnt.group(1));
			if (quantia > 0)
			{
				is.setAmount(quantia);
			}
		}

		is.setItemMeta(meta);
		this.item = is;
	}

	@Nullable
	public ItemStack getPreparedItem()
	{
		return this.item;
	}

	private String replaces(String old, final HashMap<String, String> replaces)
	{
		if (replaces != null)
		{
			for (final Entry<String, String> kv : replaces.entrySet())
			{
				old = old.replace("{" + kv.getKey() + "}", kv.getValue());
			}
		}

		return old;
	}

}
