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
package br.com.blackhubos.eventozero.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.apache.commons.lang.Validate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * This is a Bukkit Utility Class, enjoy =D.
 *
 * @author <a href="https://github.com/ReedFlake/">ReedFlake (reedflake@gmail.com)</a>
 *
 */
public final class Framework
{

	private static final Pattern commentary = Pattern.compile("(?:^(?:(?:\\s+)?\\#(?:\\s+)?))+(.*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

	@SuppressWarnings("all")
	private static Plugin worldguard = null;

	@SuppressWarnings("all")
	private static Plugin worldedit = null;

	public static void main(final String[] args)
	{
		for (final String line : new String[] { "# commentary", "Non commentary", "  #special1", "  s# gekki" })
		{
			final Matcher m = Framework.commentary.matcher(line);
			final boolean matches = m.matches();
			System.out.println((matches ? matches + " :" : matches + "  :") + "\"" + line.trim() + "\" | " + m.groupCount());
		}
	}

	public Framework()
	{
		Framework.worldguard = Framework.getPlugin("WorldGuard");
		Framework.worldedit = Framework.getPlugin("WorldEdit");
	}

	public static boolean tryBoolean(final String string)
	{
		final String f = string.toLowerCase().replaceAll("\\s", "");
		return f.equals("true") || f.equals("false") || f.equals("t") || f.equals("f") || f.equals("y") || f.equals("n") || f.equals("1") || f.equals("2");
	}

	public static boolean getBoolean(final String string)
	{
		final Pattern p = Pattern.compile("(t.*|y.*|1)", Pattern.CASE_INSENSITIVE);
		if (p.matcher(string.replaceAll("\\s", "")).matches())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static String fromLocation(final Location pos)
	{
		return String.format("World [%s] X [%s] Y [%s] Z [%s] Yaw [%s] Pitch [%s]", pos.getWorld().getName(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), pos.getYaw(), pos.getPitch());
	}

	public static Location toLocation(final String serial)
	{
		final Pattern pattern = Pattern.compile("^World\\s*\\[([a-zA-Z0-9_-]+)\\]\\s*X\\s*\\[([0-9]+)\\]\\s*Y\\s*\\[([0-9]+)\\]\\s*Z\\s*\\[([0-9]+)\\](\\s*Yaw\\s*\\[([0-9\\.]+)\\]\\s*Pitch\\s*\\[([0-9\\.]+)\\])?");
		final Matcher m = pattern.matcher(serial);
		if (m.matches())
		{
			if ((m.groupCount() >= 5) && (m.group(5) != null) && (m.group(6) != null) && (m.group(7) != null))
			{
				return new Location(Framework.getWorld(m.group(1)), Framework.getInt(m.group(2)), Framework.getInt(m.group(3)), Framework.getInt(m.group(4)), Framework.getFloat(m.group(6)), Framework.getFloat(m.group(7)));
			}
			else
			{
				return new Location(Framework.getWorld(m.group(1)), Framework.getInt(m.group(2)), Framework.getInt(m.group(3)), Framework.getInt(m.group(4)));
			}
		}
		else
		{
			return null;
		}
	}

	public static java.util.Vector<String> broadcast(@Nonnull final File file, @Nullable final HashMap<String, Object> replacements)
	{
		if ((file == null) || !file.exists())
		{
			return new java.util.Vector<String>();
		}

		try
		{
			final java.util.Vector<String> array = Framework.parseLines(file.toPath(), Charset.forName("UTF-8"));
			return Framework.broadcast(array, replacements);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return new java.util.Vector<String>();
		}
	}

	public static java.util.Vector<String> broadcast(final java.util.Vector<String> messages, @Nullable HashMap<String, Object> replacements)
	{
		if (replacements == null)
		{
			replacements = new HashMap<String, Object>();
		}

		if ((messages == null) || messages.isEmpty())
		{
			return new java.util.Vector<String>();
		}

		final java.util.Vector<String> array = new java.util.Vector<String>();

		for (String s : messages)
		{
			for (final Entry<String, Object> r : replacements.entrySet())
			{
				s = s.replaceAll(r.getKey(), String.valueOf(r.getValue()));
			}

			array.add(ChatColor.translateAlternateColorCodes('&', s));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
		}

		return array;
	}

	public static boolean tryInt(final String value, final int min, final int max)
	{
		try
		{
			final int i = Integer.parseInt(value);
			if (((min != -1) && (i < min)) || ((max != -1) && (i > max)))
			{
				return false;
			}

			return true;
		}
		catch (final NumberFormatException io)
		{
			return false;
		}
	}

	public static String reverseOf(final long delayed)
	{
		final StringBuilder literal = new StringBuilder();
		long segundos = delayed / 20L;
		long minutos = 0L;
		long horas = 0L;
		while ((segundos / 60) > 0)
		{
			minutos++;
			segundos -= 60;
		}

		while ((minutos / 60) > 0)
		{
			minutos -= 60;
			horas++;
		}

		literal.append((horas > 9 ? horas + "h" : horas != 0 ? "0" + horas + "h" : ""));
		literal.append((minutos > 9 ? minutos + "m" : minutos != 0 ? "0" + minutos + "m" : ""));
		literal.append((segundos > 9 ? segundos + "s" : segundos != 0 ? "0" + segundos + "s" : ""));
		return literal.toString().trim();
	}

	public static long reverseOf(final String tempo)
	{
		if (tempo == null)
		{
			return 0L;
		}

		final Pattern verifier = Pattern.compile("(([0-9]+)(h|m|s))", Pattern.CASE_INSENSITIVE);
		final Matcher m = verifier.matcher(tempo.toLowerCase());
		long delay = 0L;
		while (m.find())
		{
			final int numero = Framework.getInt(m.group(2));
			final char c = m.group(3).charAt(0);
			if (c == 's')
			{
				delay += numero * 20L;
			}
			else if (c == 'm')
			{
				delay += (numero * 60) * 20L;
			}
			else if (c == 'h')
			{
				delay += ((numero * 60) * 20L) * 60;
			}
		}

		return delay;
	}

	@Nullable
	public static Enchantment checkEnchantment(String key)
	{
		key = key.replaceAll("(\\s|\\-|\\_)", "").toLowerCase();
		switch (key)
		{
			case "blastprotection":
			{
				return Enchantment.PROTECTION_EXPLOSIONS;
			}
			case "fireprotection":
			{
				return Enchantment.PROTECTION_FIRE;
			}
			case "aquaaffinity":
			{
				return Enchantment.OXYGEN;
			}
			case "protection":
			{
				return Enchantment.PROTECTION_ENVIRONMENTAL;
			}
			case "sharpness":
			{
				return Enchantment.DAMAGE_ALL;
			}
			case "thorns":
			{
				return Enchantment.THORNS;
			}
			case "fortune":
			{
				return Enchantment.LOOT_BONUS_BLOCKS;
			}
			case "fireaspect":
			{
				return Enchantment.FIRE_ASPECT;
			}
			case "flame":
			{
				return Enchantment.ARROW_FIRE;
			}
			case "power":
			{
				return Enchantment.ARROW_DAMAGE;
			}
			case "punch":
			{
				return Enchantment.ARROW_KNOCKBACK;
			}
			case "smite":
			{
				return Enchantment.LOOT_BONUS_MOBS;
			}
			case "infinity":
			{
				return Enchantment.ARROW_INFINITE;
			}
			case "projectileprotection":
			{
				return Enchantment.PROTECTION_PROJECTILE;
			}
			case "looting":
			{
				return Enchantment.DAMAGE_UNDEAD;
			}
			case "baneofarthropods":
			{
				return Enchantment.DAMAGE_ARTHROPODS;
			}
			case "respiration":
			{
				return Enchantment.WATER_WORKER;
			}
			case "featherfalling":
			{
				return Enchantment.PROTECTION_FALL;
			}
			case "efficiency":
			{
				return Enchantment.DIG_SPEED;
			}
			case "unbreaking":
			{
				return Enchantment.DURABILITY;
			}
			case "silktouch":
			{
				return Enchantment.SILK_TOUCH;
			}
			case "knockback":
			{
				return Enchantment.KNOCKBACK;
			}
		}

		return null;
	}

	public static boolean setSign(final Location pos, final int tipo, final String... args)
	{
		return Framework.setSign(pos.getBlock(), tipo, args);
	}

	public static boolean setSign(final Block block, final int tipo, final String... args)
	{
		if (args.length == 4)
		{
			block.setType((tipo == 1) || (tipo == 0) ? Material.WALL_SIGN : Material.SIGN_POST);
			final Sign s = (Sign) block.getState();
			s.setLine(0, args[0]);
			s.setLine(1, args[1]);
			s.setLine(2, args[2]);
			s.setLine(3, args[3]);
			s.update(true);
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean isSign(final Location pos)
	{
		return Framework.isSign(pos.getBlock());
	}

	public static boolean isSign(final Block block)
	{
		return (block.getType() == Material.WALL_SIGN) || (block.getType() == Material.SIGN_POST);
	}

	public static void setBlocks(final Location point, final Location anotherpoint, final int id, final byte data)
	{
		Objects.requireNonNull(point, "Primary point can't be null.");
		Objects.requireNonNull(anotherpoint, "Secundary point can't be null");
		if (point.getWorld().getName().equalsIgnoreCase(anotherpoint.getWorld().getName()))
		{
			final Location min = Framework.getMinimumPoint(point, anotherpoint);
			final Location max = Framework.getMaximumPoint(point, anotherpoint);
			for (int x = min.getBlockX(); x <= max.getBlockX(); x++)
			{
				for (int y = min.getBlockY(); y <= max.getBlockY(); y++)
				{
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++)
					{
						final Block b = point.getWorld().getBlockAt(x, y, z);
						b.setTypeId(id);
						b.setData(data);
						b.getState().update(true);
					}
				}
			}
		}
	}

	public static float checkSameSize(final String expected, final String obtained)
	{
		if (expected.length() != obtained.length())
		{
			throw new ArrayIndexOutOfBoundsException("Failed to use checkSameSize() param; strings have no igual length.");
		}

		final int iLen = expected.length();
		int iDiffs = 0;

		for (int i = 0; i < iLen; i++)
		{
			if (expected.charAt(i) != obtained.charAt(i))
			{
				iDiffs++;
			}
		}

		// 1 = igual, 0 = nada haver
		return 1f - ((float) iDiffs / iLen);
	}

	public static float equals(String expected, String obtained, final boolean normalize, final boolean lower, final boolean trim)
	{
		if (normalize)
		{
			expected = Framework.normalize(expected);
			obtained = Framework.normalize(obtained);
		}

		if (lower)
		{
			expected = expected.toLowerCase();
			obtained = obtained.toLowerCase();
		}

		if (trim)
		{
			expected = expected.trim();
			obtained = obtained.trim();
		}

		if (expected.length() != obtained.length())
		{
			final int iDiff = Math.abs(expected.length() - obtained.length());
			final int iLen = Math.max(expected.length(), obtained.length());
			String sBigger, sSmaller, sAux;

			if (iLen == expected.length())
			{
				sBigger = expected;
				sSmaller = obtained;
			}
			else
			{
				sBigger = obtained;
				sSmaller = expected;
			}

			float fSim, fMaxSimilarity = Float.MIN_VALUE;
			for (int i = 0; i <= sSmaller.length(); i++)
			{
				sAux = sSmaller.substring(0, i) + sBigger.substring(i, i + iDiff) + sSmaller.substring(i);
				fSim = Framework.checkSameSize(sBigger, sAux);
				if (fSim > fMaxSimilarity)
				{
					fMaxSimilarity = fSim;
				}
			}
			return fMaxSimilarity - ((1f * iDiff) / iLen);
		}
		else
		{
			return Framework.checkSameSize(expected, obtained);
		}
	}

	public static String normalize(String arg)
	{
		arg = Normalizer.normalize(arg, Normalizer.Form.NFD);
		arg = arg.replaceAll("[^\\p{ASCII}]", "");
		return arg;
	}

	public static float getFloatReduced(final float value)
	{
		String s = String.valueOf(value);
		if (s.length() > 4)
		{
			s = s.substring(0, 3);
		}

		return Float.valueOf(s);
	}

	public static boolean aroundPlayer(final Player player, final int height, final String size, final int id, final byte data)
	{
		final Pattern sized = Pattern.compile("^([0-9]+)\\s*x\\s*([0-9]+)$");
		final Matcher m = sized.matcher(size);
		if (m.matches())
		{
			final Location pos = player.getLocation();
			final int largura = Framework.getInt(m.group(1));
			final int comprimento = Framework.getInt(m.group(2));
			for (int x = pos.getBlockX() - largura; x <= (pos.getBlockX() + comprimento); x++)
			{
				for (int y = pos.getBlockY(); y <= (pos.getBlockY() + height); y++)
				{
					for (int z = pos.getBlockZ() - largura; z <= (pos.getBlockZ() + comprimento); z++)
					{
						final Block b = player.getWorld().getBlockAt(x, y, z);
						b.setTypeId(id);
						b.setData(data);
					}
				}
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	public static float getFloatReduced(final String value)
	{
		return Framework.getFloatReduced(Float.parseFloat(value));
	}

	public static float getFloat(final String floatt)
	{
		return Float.parseFloat(floatt);
	}

	public static int getInt(final String number)
	{
		return Integer.parseInt(number);
	}

	public static World getWorld(final String name)
	{
		return Bukkit.getWorld(name);
	}

	public Economy getEconomy()
	{
		return Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
	}

	public Permission getPermissions()
	{
		return Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
	}

	public Chat getChat()
	{
		return Bukkit.getServicesManager().getRegistration(Chat.class).getProvider();
	}

	public static boolean can(final StateFlag flag, final Location around)
	{
		final WorldGuardPlugin plugin = (WorldGuardPlugin) Framework.worldguard;
		return plugin.getRegionManager(around.getWorld()).getApplicableRegions(around).allows(flag);
	}

	public static boolean isInsideRegion(final Location around)
	{
		return Framework.getRegion(around) != null;
	}

	@Nullable
	public static ProtectedRegion addRegion(final String name, final Location center, final int larg, final int comp, final int priority)
	{
		Preconditions.checkArgument(center.getWorld().getName().equalsIgnoreCase(center.getWorld().getName()), "Worlds not same!");
		Location prim = new Location(center.getWorld(), center.getBlockX() + larg, center.getBlockY(), center.getBlockZ());
		Location sec = new Location(center.getWorld(), center.getBlockX() - comp, center.getBlockY(), center.getBlockZ() - comp);
		prim = Framework.getMinimumPoint(prim, sec);
		sec = Framework.getMaximumPoint(prim, sec);
		final WorldGuardPlugin plugin = (WorldGuardPlugin) Framework.worldguard;
		final RegionManager rm = plugin.getRegionManager(center.getWorld());
		if (rm.hasRegion(name))
		{
			return null;
		}
		else
		{
			final ProtectedCuboidRegion cuboid = new ProtectedCuboidRegion(name, Framework.getWorldEditVector(prim), Framework.getWorldEditVector(sec));
			cuboid.setPriority(priority);
			rm.addRegion(cuboid);
			return cuboid;
		}
	}

	public static com.sk89q.worldedit.BlockVector getWorldEditVector(final Location location)
	{
		return new com.sk89q.worldedit.BlockVector(location.getX(), location.getY(), location.getZ());
	}

	public static java.util.Vector<ProtectedRegion> getInsideRegions(final Location around)
	{
		final WorldGuardPlugin plugin = (WorldGuardPlugin) Framework.worldguard;
		final java.util.Vector<ProtectedRegion> array = new java.util.Vector<ProtectedRegion>();
		if (Framework.isInsideRegion(around))
		{
			final ApplicableRegionSet set = plugin.getRegionManager(around.getWorld()).getApplicableRegions(around);
			for (final ProtectedRegion region : set)
			{
				array.add(region);
			}
		}

		return array;
	}

	public static ProtectedRegion getRegion(final Location around)
	{
		final WorldGuardPlugin plugin = (WorldGuardPlugin) Framework.worldguard;
		final Iterator<ProtectedRegion> i = plugin.getRegionManager(around.getWorld()).getApplicableRegions(around).iterator();
		return i.hasNext() ? i.next() : null;
	}

	public static ProtectedRegion getPrioritizedRegion(final Location around)
	{
		final WorldGuardPlugin plugin = (WorldGuardPlugin) Framework.worldguard;
		final ApplicableRegionSet set = plugin.getRegionManager(around.getWorld()).getApplicableRegions(around);
		ProtectedRegion prime = null;
		for (final ProtectedRegion region : set)
		{
			if ((prime != null) && (region.getPriority() > prime.getPriority()))
			{
				prime = region;
			}
			else
			{
				prime = region;
			}
		}

		return prime;
	}

	public static Terrain getTerrainManager(final Player player)
	{
		return new Terrain(player);
	}

	public static Location getCenter(final Location pos1, final Location pos2, final int y)
	{
		final Location min = Framework.getMinimumPoint(pos1, pos2);
		final Location max = Framework.getMaximumPoint(pos1, pos2);
		final int centerx = (max.getBlockX() + min.getBlockX()) / 2;
		final int centerz = (max.getBlockZ() + min.getBlockZ()) / 2;
		return new Location(pos1.getWorld(), centerx, y, centerz);
	}

	public static Location getMinimumPoint(final Location p1, final Location p2)
	{
		return new Location(p1.getWorld(), Math.min(p1.getBlockX(), p2.getBlockX()), Math.min(p1.getBlockY(), p2.getBlockY()), Math.min(p1.getBlockZ(), p2.getBlockZ()));
	}

	public static Location getMaximumPoint(final Location p1, final Location p2)
	{
		return new Location(p1.getWorld(), Math.max(p1.getBlockX(), p2.getBlockX()), Math.max(p1.getBlockY(), p2.getBlockY()), Math.max(p1.getBlockZ(), p2.getBlockZ()));
	}

	@SuppressWarnings("unchecked")
	public static <T extends JavaPlugin> T getPlugin(final String name)
	{
		return (T) Bukkit.getPluginManager().getPlugin(name);
	}

	public static void disablePlugin(final String name)
	{
		Framework.disablePlugin(Framework.getPlugin(name));
	}

	public static void disablePlugin(final Plugin plugin)
	{
		Bukkit.getPluginManager().disablePlugin(plugin);
	}

	public static ItemStack createItem(final String name, final int id, final byte subType, final int quantity, final String... lore)
	{
		return Framework.createItem(name, id, subType, quantity, Arrays.asList(lore));
	}

	public static ItemStack createItem(final String name, final int id, final byte subType, final int quantity, Collection<String> lores)
	{
		if (lores == null)
		{
			lores = new java.util.Vector<String>();
		}

		final java.util.Vector<String> lore = new java.util.Vector<String>(lores);
		final ItemStack is = new ItemStack(id, quantity);
		is.getData().setData(subType);
		is.getItemMeta().setLore(lore);
		is.getItemMeta().setDisplayName(name);
		return is;
	}

	public static void printTable(final String[][] content)
	{

		final int maxLength = Framework.getMaximumLength(content);
		final StringBuilder indexes = new StringBuilder("|Index|");
		final int max = Framework.getMaximumElement(content) + 1;

		for (int x = 0; x < max; ++x)
		{
			indexes.append(String.format("%" + maxLength + "d|", x));
		}

		System.out.println(indexes.toString());

		for (int x = 0; x < content.length; ++x)
		{

			final String[] dimension1 = content[x];
			final StringBuffer sb = new StringBuffer(String.format("|%5d|", x));
			for (int y = 0; y < dimension1.length; ++y)
			{
				final String value = dimension1[y];
				sb.append(String.format("%" + maxLength + "s|", value));
			}
			System.out.println(sb.toString());

		}

	}

	public static int getMaximumElement(final String[][] content)
	{
		int maxElement = 0;

		for (int x = 0; x < content.length; ++x)
		{
			final String[] dimension1 = content[x];
			for (int y = 0; y < dimension1.length; ++y)
			{
				if (y > maxElement)
				{
					maxElement = y;
				}
			}
		}
		return maxElement;

	}

	public static int getMaximumLength(final String[][] content)
	{
		int maxLength = 0;

		for (int x = 0; x < content.length; ++x)
		{
			final String[] dimension1 = content[x];
			for (int y = 0; y < dimension1.length; ++y)
			{
				final String value = dimension1[y];
				if (value.length() > maxLength)
				{
					maxLength = value.length();
				}
			}
		}
		return maxLength;
	}

	public static Handler<Integer, ItemInfo> getBlockPercentageBySequence(final String item)
	{
		final Handler<Integer, Integer> handler = Framework.getBlockPercentageType(item);
		final int t = handler.getPrimary();
		if (t != 0)
		{
			ItemInfo result = null;
			final String[] rs = item.split("\\s*%\\s*");
			final String[] sub = rs[1].split("\\s*:\\s*");
			final int percent = Integer.parseInt(rs[0]);
			if (t == 1)
			{
				result = Items.itemById(Integer.parseInt(sub[0]));
			}
			else
			{
				result = Items.getItemByName(sub[0]);
			}

			if (sub.length == 2)
			{
				result.setSubTypeId(Short.parseShort(sub[1]));
			}

			return new Handler<Integer, ItemInfo>(percent, result);
		}
		else
		{
			return new Handler<Integer, ItemInfo>(null, null);
		}
	}

	public static boolean checkBlockPercentageSequence(String item)
	{
		item = Framework.fixSpaces(item);
		final Pattern prim = Pattern.compile("\\s*(([0-9]{1,3})\\s*\\%\\s*([0-9]+|[a-zA-Z_-]+)\\s*(:\\s*([0-9])+)?)\\s*");
		final Matcher m = prim.matcher(item);
		return m.matches();
	}

	public static Handler<Integer, Integer> getBlockPercentageType(String item)
	{
		item = Framework.fixSpaces(item);
		if (Framework.checkBlockPercentageSequence(item))
		{
			final Pattern prim = Pattern.compile("\\s*(([0-9]{1,3})\\s*\\%\\s*([0-9]+|[a-zA-Z_-]+)\\s*(:\\s*([0-9])+)?)\\s*");
			final Matcher matcher = prim.matcher(item);
			if (matcher.find())
			{
				final int module = matcher.group(3).matches("[0-9]+") ? 1 : 2;
				final int subModule = ((matcher.group(5) != null) && matcher.group(5).matches("[0-9]+")) ? 1 : 0;
				return new Handler<Integer, Integer>(module, subModule);
			}
			else
			{
				return new Handler<Integer, Integer>(0, 0);
			}
		}
		else
		{
			return new Handler<Integer, Integer>(0, 0);
		}
	}

	public static String fixSpaces(String literal)
	{
		literal = literal.trim();
		final Pattern pattern = Pattern.compile("\\s{2}");
		final Matcher matcher = pattern.matcher(literal);
		while (matcher.find())
		{
			literal = literal.replaceAll(pattern.pattern(), " ");
		}

		return literal;
	}

	public static void printGroups(final Pattern pattern, final String literal)
	{
		final Matcher matcher = pattern.matcher(literal);
		if (matcher.find())
		{
			for (int i = 1; i <= matcher.groupCount(); i++)
			{
				System.out.println("Group " + i + ":  \"" + (matcher.group(i) != null ? matcher.group(i) : "(não encontrado)") + "\"");
			}
		}
	}

	public static java.util.Vector<String> parseLines(final Path path, final Charset cs) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(path, cs))
		{
			final java.util.Vector<String> result = new java.util.Vector<String>();
			for (; true;)
			{
				final String line = reader.readLine();
				if (line == null)
				{
					break;
				}
				if (!Framework.isCommentary(line))
				{
					result.add(line);
				}
			}
			return result;
		}
	}

	public static final class BlockFill
	{
		private final int percent;
		private final Block block;

		public BlockFill(final int percent, final Block block)
		{
			this.percent = percent;
			this.block = block;
		}

		public Block getBlock()
		{
			return this.block;
		}

		public int getPercent()
		{
			return this.percent;
		}

		public static boolean validate(final Collection<BlockFill> blockFills)
		{
			int currentPercent = 0;
			final Iterator<BlockFill> iterator = blockFills.iterator();
			while (iterator.hasNext())
			{
				final BlockFill fill = iterator.next();
				currentPercent += fill.percent;
				if (currentPercent > 100)
				{
					return false;
				}
			}
			return currentPercent == 100;
		}
	}

	public static void setBlocks(final Location min, final Location max, final Collection<BlockFill> blocks0)
	{
		if (!BlockFill.validate(blocks0))
		{
			throw new RuntimeException("Provided set exceeded 100%!");
		}

		final java.util.Vector<BlockFill> blocks = new java.util.Vector<BlockFill>(blocks0);
		final java.util.Vector<Block> blockList = Framework.getBlocks(min, max);
		final java.util.Vector<Location> locationsExcluded = new java.util.Vector<>();
		final int size = blockList.size();
		final Iterator<BlockFill> fills = blocks.iterator();
		while (fills.hasNext())
		{
			final BlockFill block = fills.next();
			final int quantity = (block.getPercent() * size) / 100;
			final java.util.Vector<Location> locations = new java.util.Vector<Location>();

			for (int x = 0; x < quantity; ++x)
			{
				final Location selected = Framework.getBlocks(min, max, locationsExcluded);
				locations.add(selected);
				locationsExcluded.add(selected);
			}

			for (final Location loc : locations)
			{
				loc.getBlock().setType(block.getBlock().getType());
				loc.getBlock().setBiome(block.getBlock().getBiome());
			}

			if (!fills.hasNext() && (locationsExcluded.size() < blockList.size()))
			{
				for (final Block currentBlock : blockList)
				{
					if (!locationsExcluded.contains(currentBlock.getLocation()))
					{
						currentBlock.setType(block.getBlock().getType());
						currentBlock.setBiome(block.getBlock().getBiome());
					}
				}
			}
		}

	}

	public static java.util.Vector<Block> getBlocks(final World world, final ProtectedRegion region)
	{
		final Location p = Framework.toLocation(world, region.getMinimumPoint());
		final Location f = Framework.toLocation(world, region.getMaximumPoint());
		return Framework.getBlocks(p, f);
	}

	public static Location getBlocks(final Location min, final Location max, final java.util.Vector<Location> excluded)
	{
		final int locX = Math.min(min.getBlockX(), max.getBlockX());
		final int locY = Math.min(min.getBlockY(), max.getBlockY());
		final int locZ = Math.min(min.getBlockZ(), max.getBlockZ());
		final int locMaxX = Math.max(min.getBlockX(), max.getBlockX()) - locX;
		final int locMaxY = Math.max(min.getBlockY(), max.getBlockY()) - locY;
		final int locMaxZ = Math.max(min.getBlockZ(), max.getBlockZ()) - locZ;
		final World world = min.getWorld();
		final Random rand = new Random();
		Location loc = null;
		while (excluded.contains((loc = new Location(world, rand.nextInt(locMaxX + 1) + locX, rand.nextInt(locMaxY + 1) + locY, rand.nextInt(locMaxZ + 1) + locZ))))
		{
			assert ((loc.getBlockX() <= Math.max(min.getBlockX(), min.getBlockX())) && (loc.getBlockY() <= Math.max(min.getBlockY(), min.getBlockY())) && (loc.getBlockZ() <= Math.max(min.getBlockZ(), min.getBlockZ())));
		}
		return loc;
	}

	public static Location toLocation(final World world, final Vector vector)
	{
		return new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	public static Location toLocation(final World world, final com.sk89q.worldedit.Vector vector)
	{
		return new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	public static java.util.Vector<Block> getBlocks(Location prim, Location another, final String... except)
	{
		prim = Framework.getMinimumPoint(prim, another);
		another = Framework.getMaximumPoint(prim, another);
		final World world = prim.getWorld();
		final java.util.Vector<Block> blockList = new java.util.Vector<Block>();
		for (int currentX = prim.getBlockX(); currentX <= another.getBlockX(); ++currentX)
		{
			for (int currentY = prim.getBlockY(); currentY <= another.getBlockY(); ++currentY)
			{
				for (int currentZ = prim.getBlockZ(); currentZ <= another.getBlockZ(); ++currentZ)
				{
					final Location loc = new Location(world, currentX, currentY, currentZ);
					final Block b = loc.getBlock();
					if (!Framework.isBlockExcept(b.getTypeId(), b.getData(), except))
					{
						blockList.add(loc.getBlock());
					}
				}
			}
		}

		return blockList;
	}

	public static boolean isBlockExcept(final int id, final byte data, final String[] items)
	{
		for (final String item : items)
		{
			if (item.matches(id + "\\s*:\\s*" + data))
			{
				return true;
			}
		}

		return false;
	}

	public static java.util.Vector<String> fromBlockList(final Collection<Block> blocks)
	{
		final java.util.Vector<Block> blocks0 = new java.util.Vector<Block>(blocks);
		final java.util.Vector<String> rs = new java.util.Vector<String>();
		for (final Block b : blocks0)
		{
			final Location p = b.getLocation();
			rs.add(p.getWorld().getName() + ";" + p.getBlockX() + ";" + p.getBlockY() + ";" + p.getBlockZ() + ";" + b.getTypeId() + ":" + b.getData());
		}

		return rs;
	}

	public static DecimalFormat getFormatterD()
	{
		return Framework.formatter;
	}

	@Nullable
	public static String getCommentary(final String literal)
	{
		return Framework.commentary.matcher(literal).group(1);
	}

	public static boolean isCommentary(final String literal)
	{
		return Framework.commentary.matcher(literal).matches();
	}

	// TODO: ANOTHER CLASSES | LINE SEPARATOR

	public static final class LogManager<T extends JavaPlugin>
	{

		protected static Integer task = 0;
		private final T plugin;
		private final java.util.Vector<Log> log = new java.util.Vector<Log>();
		private final File parent;

		@SuppressWarnings("unchecked")
		public LogManager(final Plugin plugin, final File parent)
		{
			this.plugin = (T) plugin;
			this.parent = parent;
		}

		public void init(final String time)
		{
			LogManager.task = Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
			{
				@Override
				public void run()
				{
					LogManager.this.write();
				}
			}, Framework.reverseOf(time), Framework.reverseOf(time)).getTaskId();
		}

		public void cancel()
		{
			Bukkit.getScheduler().cancelTask(LogManager.task);
		}

		public void restart(final String newTime)
		{
			this.cancel();
			this.init(newTime);
		}

		public void addLog(final Log l)
		{
			if (!this.log.contains(l))
			{
				this.log.add(l);
			}
		}

		public void addLog(final Date d, final String msg)
		{
			this.log.add(new Log(d, msg));
		}

		public void addLog(final String msg)
		{
			this.log.add(new Log(new Date(), msg));
		}

		public void removeLogFromCache(final Log l)
		{
			if (this.log.contains(l))
			{
				this.log.remove(l);
			}
		}

		public java.util.Vector<Log> getCache()
		{
			final java.util.Vector<Log> log2 = new java.util.Vector<Log>();
			log2.addAll(this.log);
			return log2;
		}

		public void write()
		{
			final java.util.Vector<Log> saving_log = this.getCache();
			if (saving_log.size() == 0)
			{
				return;
			}
			this.log.clear();
			new LogWriter(saving_log).start();
		}

		private final class LogWriter extends Thread
		{
			private java.util.Vector<Log> saving_log = null;

			public LogWriter(final java.util.Vector<Log> l)
			{
				this.saving_log = l;
			}

			@Override
			public void run()
			{
				if (!LogManager.this.parent.exists())
				{
					LogManager.this.parent.mkdir();
				}
				final HashMap<String, java.util.Vector<Log>> date_log = new HashMap<String, java.util.Vector<Log>>();
				for (final Log l : this.saving_log)
				{
					final String n = LogManager.this.getFilename(l.getDate());
					final File f = new File(LogManager.this.parent, n);
					if (!f.exists())
					{
						try
						{
							f.createNewFile();
						}
						catch (final Exception e)
						{
						}
					}
					if (date_log.containsKey(n))
					{
						date_log.get(n).add(l);
					}
					else
					{
						final java.util.Vector<Log> ll = new java.util.Vector<Log>();
						ll.add(l);
						date_log.put(n, ll);
					}
				}
				for (final String n : date_log.keySet())
				{
					final File f = new File(LogManager.this.parent, n);
					BufferedWriter writer = null;
					try
					{
						writer = new BufferedWriter(new FileWriter(f, true));
						for (final Log l : date_log.get(n))
						{
							writer.write(LogManager.this.format(l.getDate(), l.getMessage()));
							writer.newLine();
						}
					}
					catch (final Exception e)
					{
					}
					finally
					{
						try
						{
							writer.close();
						}
						catch (final Exception e)
						{
						}
					}
				}
			}
		}

		public String getFilename(final Date d)
		{
			final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			return df.format(d) + ".txt";
		}

		public File getFile(final Date d)
		{
			return new File(this.parent, this.getFilename(d));
		}

		public String format(final Date d, final String msg)
		{
			final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			return "[" + df.format(d) + "] " + msg;
		}
	}

	public static final class Log implements Serializable, Comparator<Log>
	{

		private static final long serialVersionUID = -2950841356281211210L;
		private Date date = null;
		private String msg = "";

		public Log(final Date d, final String msg)
		{
			this.date = d;
			this.msg = msg;
		}

		public String getMessage()
		{
			return this.msg;
		}

		public void setMessage(final String msg)
		{
			this.msg = msg;
		}

		public Date getDate()
		{
			return this.date;
		}

		public void setDate(final Date d)
		{
			this.date = d;
		}

		@Override
		public int compare(final Log log, final Log anotherlog)
		{
			final Calendar c = Calendar.getInstance();
			c.setTime(anotherlog.getDate());
			int same = Calendar.getInstance().compareTo(c);
			if (Framework.equals(log.getMessage(), anotherlog.getMessage(), true, true, true) >= 0.6F)
			{
				same++;
			}

			return same;
		}

	}

	public static final class Configuration extends YamlConfiguration
	{

		private boolean copied;

		public Configuration()
		{
			this.copied = false;
		}

		public Configuration(final Plugin plugin, final File file)
		{
			if (!file.exists())
			{
				plugin.saveResource(file.getName(), false);
				this.copied = true;
			}

			try
			{
				this.load(file);
			}
			catch (IOException | InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		}

		public boolean copied()
		{
			return this.copied;
		}

		@Override
		public void load(final InputStream stream) throws IOException, InvalidConfigurationException
		{
			Validate.notNull(stream, "Stream cannot be null");

			final InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
			final StringBuilder builder = new StringBuilder();
			final BufferedReader input = new BufferedReader(reader);

			try
			{
				String line;

				while ((line = input.readLine()) != null)
				{
					builder.append(line);
					builder.append('\n');
				}
			}
			finally
			{
				input.close();
			}

			this.loadFromString(builder.toString());
		}

		@Override
		public void save(final File file) throws IOException
		{
			Validate.notNull(file, "File cannot be null");
			com.google.common.io.Files.createParentDirs(file);
			final String data = this.saveToString();
			final FileOutputStream stream = new FileOutputStream(file);
			final OutputStreamWriter writer = new OutputStreamWriter(stream, Charset.forName("UTF-8"));

			try
			{
				writer.write(data);
			}
			finally
			{
				writer.close();
			}
		}
	}

	public static class Terrain
	{

		private static final String EXTENSION = "schematic";
		private final WorldEdit we;
		private final LocalSession localSession;
		private final EditSession editSession;
		private final LocalPlayer localPlayer;

		public Terrain(final Player player)
		{
			final WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
			this.we = wep.getWorldEdit();
			this.localPlayer = wep.wrapPlayer(player);
			this.localSession = this.we.getSession(this.localPlayer);
			this.editSession = this.localSession.createEditSession(this.localPlayer);
		}

		public Terrain(final WorldEditPlugin wep, final World world)
		{
			this.we = wep.getWorldEdit();
			this.localPlayer = null;
			this.localSession = new LocalSession(this.we.getConfiguration());
			this.editSession = new EditSession(new BukkitWorld(world), this.we.getConfiguration().maxChangeLimit);
		}

		public void save(File saveFile, final Location l1, final Location l2) throws FilenameException, DataException, IOException
		{
			final com.sk89q.worldedit.Vector min = this.getMin(l1, l2);
			final com.sk89q.worldedit.Vector max = this.getMax(l1, l2);
			saveFile = this.we.getSafeSaveFile(this.localPlayer, saveFile.getParentFile(), saveFile.getName(), Terrain.EXTENSION, new String[] { Terrain.EXTENSION });
			this.editSession.enableQueue();
			final CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new com.sk89q.worldedit.Vector(1, 1, 1)), min);
			clipboard.copy(this.editSession);
			SchematicFormat.MCEDIT.save(clipboard, saveFile);
			this.editSession.flushQueue();
		}

		public void load(File saveFile, final Location loc) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException
		{
			saveFile = this.we.getSafeSaveFile(this.localPlayer, saveFile.getParentFile(), saveFile.getName(), Terrain.EXTENSION, new String[] { Terrain.EXTENSION });

			this.editSession.enableQueue();
			this.localSession.setClipboard(SchematicFormat.MCEDIT.load(saveFile));
			this.localSession.getClipboard().place(this.editSession, this.getPastePosition(loc), false);
			this.editSession.flushQueue();
			this.we.flushBlockBag(this.localPlayer, this.editSession);
		}

		public void load(final File saveFile) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException
		{
			this.load(saveFile, null);
		}

		private com.sk89q.worldedit.Vector getPastePosition(final Location loc) throws EmptyClipboardException
		{
			if (loc == null)
			{
				return this.localSession.getClipboard().getOrigin();
			}
			else
			{
				return new com.sk89q.worldedit.Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			}
		}

		private com.sk89q.worldedit.Vector getMin(final Location l1, final Location l2)
		{
			return new com.sk89q.worldedit.Vector(Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()), Math.min(l1.getBlockZ(), l2.getBlockZ()));
		}

		private com.sk89q.worldedit.Vector getMax(final Location l1, final Location l2)
		{
			return new com.sk89q.worldedit.Vector(Math.max(l1.getBlockX(), l2.getBlockX()), Math.max(l1.getBlockY(), l2.getBlockY()), Math.max(l1.getBlockZ(), l2.getBlockZ()));
		}
	}

	public static final class Items
	{

		private static final java.util.Vector<ItemInfo> items = new java.util.Vector<ItemInfo>(new CopyOnWriteArrayList<ItemInfo>());

		public static java.util.Vector<ItemInfo> getItemList()
		{
			return new java.util.Vector<ItemInfo>(Collections.unmodifiableList(Items.items));
		}

		static
		{
			Items.items.add(new ItemInfo("Air", new String[][] { { "air" } }, Material.AIR));
			Items.items.add(new ItemInfo("Stone", new String[][] { { "ston" }, { "smoo", "sto" } }, Material.STONE));
			Items.items.add(new ItemInfo("Grass", new String[][] { { "gras" } }, Material.GRASS));
			Items.items.add(new ItemInfo("Dirt", new String[][] { { "dirt" } }, Material.DIRT));
			Items.items.add(new ItemInfo("Cobblestone", new String[][] { { "cobb", "sto" }, { "cobb" } }, Material.COBBLESTONE));
			Items.items.add(new ItemInfo("Oak Plank", new String[][] { { "wood" }, { "oak", "plank" }, { "oak", "wood" } }, Material.WOOD));
			Items.items.add(new ItemInfo("Spruce Plank", new String[][] { { "spru", "plank" }, { "spruc", "wood" } }, Material.WOOD, (short) 1));
			Items.items.add(new ItemInfo("Birch Plank", new String[][] { { "birch", "plank" }, { "birch", "wood" } }, Material.WOOD, (short) 2));
			Items.items.add(new ItemInfo("Jungle Plank", new String[][] { { "jung", "plank" }, { "jung", "wood" } }, Material.WOOD, (short) 3));
			Items.items.add(new ItemInfo("Oak Sapling", new String[][] { { "sapl" }, { "sapl", "oak" } }, Material.SAPLING));
			Items.items.add(new ItemInfo("Spruce Sapling", new String[][] { { "sapl", "spruc" } }, Material.SAPLING, (short) 1));
			Items.items.add(new ItemInfo("Birch Sapling", new String[][] { { "sapl", "birch" } }, Material.SAPLING, (short) 2));
			Items.items.add(new ItemInfo("Jungle Sapling", new String[][] { { "sapl", "jungle" } }, Material.SAPLING, (short) 3));
			Items.items.add(new ItemInfo("Bedrock", new String[][] { { "rock" } }, Material.BEDROCK));
			Items.items.add(new ItemInfo("Water", new String[][] { { "water" } }, Material.WATER));
			Items.items.add(new ItemInfo("Lava", new String[][] { { "lava" } }, Material.LAVA));
			Items.items.add(new ItemInfo("Sand", new String[][] { { "sand" } }, Material.SAND));
			Items.items.add(new ItemInfo("Gold Ore", new String[][] { { "ore", "gold" } }, Material.GOLD_ORE));
			Items.items.add(new ItemInfo("Iron Ore", new String[][] { { "ore", "iron" } }, Material.IRON_ORE));
			Items.items.add(new ItemInfo("Coal Ore", new String[][] { { "ore", "coal" } }, Material.COAL_ORE));
			Items.items.add(new ItemInfo("Gravel", new String[][] { { "grav" } }, Material.GRAVEL));
			Items.items.add(new ItemInfo("Oak Log", new String[][] { { "oak" }, { "log" }, { "oak", "log" } }, Material.LOG));
			Items.items.add(new ItemInfo("Spruce Log", new String[][] { { "spruc" }, { "spruc", "log" } }, Material.LOG, (short) 1));
			Items.items.add(new ItemInfo("Birch Log", new String[][] { { "birch" }, { "birch", "log" } }, Material.LOG, (short) 2));
			Items.items.add(new ItemInfo("Jungle Log", new String[][] { { "jung", "log" } }, Material.LOG, (short) 3));
			Items.items.add(new ItemInfo("Leaves Block", new String[][] { { "blo", "leaf" }, { "blo", "leaves" } }, Material.LEAVES));
			Items.items.add(new ItemInfo("Spruce Leaves Block", new String[][] { { "blo", "lea", "spruc" } }, Material.LEAVES, (short) 1));
			Items.items.add(new ItemInfo("Birch Leaves Block", new String[][] { { "blo", "lea", "birch" } }, Material.LEAVES, (short) 2));
			Items.items.add(new ItemInfo("Jungle Leaves Block", new String[][] { { "blo", "lea", "jung" } }, Material.LEAVES, (short) 3));
			Items.items.add(new ItemInfo("Leaves", new String[][] { { "leaf" }, { "leaves" } }, Material.LEAVES, (short) 4));
			Items.items.add(new ItemInfo("Spruce Leaves", new String[][] { { "lea", "spruce" } }, Material.LEAVES, (short) 5));
			Items.items.add(new ItemInfo("Birch Leaves", new String[][] { { "lea", "birch" } }, Material.LEAVES, (short) 6));
			Items.items.add(new ItemInfo("Jungle Leaves", new String[][] { { "lea", "jung" } }, Material.LEAVES, (short) 7));
			Items.items.add(new ItemInfo("Sponge", new String[][] { { "sponge" } }, Material.SPONGE));
			Items.items.add(new ItemInfo("Glass", new String[][] { { "glas" }, { "sili" } }, Material.GLASS));
			Items.items.add(new ItemInfo("Lapis Lazuli Ore", new String[][] { { "lap", "laz", "ore" }, { "lazul", "ore" }, { "ore", "lapiz" } }, Material.LAPIS_ORE));
			Items.items.add(new ItemInfo("Lapis Lazuli Block", new String[][] { { "lap", "laz", "bloc" }, { "lazu", "bloc" }, { "blo", "lapi" } }, Material.LAPIS_BLOCK));
			Items.items.add(new ItemInfo("Dispenser", new String[][] { { "dispen" } }, Material.DISPENSER));
			Items.items.add(new ItemInfo("Sandstone", new String[][] { { "sand", "st" } }, Material.SANDSTONE));
			Items.items.add(new ItemInfo("Chiseled Sandstone", new String[][] { { "chis", "sand", "sto" } }, Material.SANDSTONE, (short) 1));
			Items.items.add(new ItemInfo("Smooth Sandstone", new String[][] { { "smoo", "sand", "sto" } }, Material.SANDSTONE, (short) 2));
			Items.items.add(new ItemInfo("Note Block", new String[][] { { "note" } }, Material.NOTE_BLOCK));
			Items.items.add(new ItemInfo("Bed Block", new String[][] { { "block", "bed" } }, Material.BED_BLOCK));
			Items.items.add(new ItemInfo("Powered Rail", new String[][] { { "rail", "pow" }, { "trac", "pow" }, { "boost" } }, Material.POWERED_RAIL));
			Items.items.add(new ItemInfo("Detector Rail", new String[][] { { "rail", "det" }, { "trac", "det" }, { "detec" } }, Material.DETECTOR_RAIL));
			Items.items.add(new ItemInfo("Sticky Piston", new String[][] { { "stic", "pis" } }, Material.PISTON_STICKY_BASE));
			Items.items.add(new ItemInfo("Web", new String[][] { { "web" }, { "cobw" } }, Material.WEB));
			Items.items.add(new ItemInfo("Dead Shrub", new String[][] { { "dead", "shru" }, { "dese", "shru" }, { "shrub" } }, Material.LONG_GRASS, (short) 0));
			Items.items.add(new ItemInfo("Tall Grass", new String[][] { { "tall", "gras" }, { "long", "gras" } }, Material.LONG_GRASS, (short) 1));
			Items.items.add(new ItemInfo("Fern", new String[][] { { "fern" } }, Material.LONG_GRASS, (short) 2));
			Items.items.add(new ItemInfo("Piston", new String[][] { { "pisto" } }, Material.PISTON_BASE));
			Items.items.add(new ItemInfo("White Wool", new String[][] { { "wool", "whit" }, { "wool" } }, Material.WOOL));
			Items.items.add(new ItemInfo("Orange Wool", new String[][] { { "wool", "ora" } }, Material.WOOL, (short) 1));
			Items.items.add(new ItemInfo("Magenta Wool", new String[][] { { "wool", "mag" } }, Material.WOOL, (short) 2));
			Items.items.add(new ItemInfo("Light Blue Wool", new String[][] { { "wool", "lig", "blue" } }, Material.WOOL, (short) 3));
			Items.items.add(new ItemInfo("Yellow Wool", new String[][] { { "wool", "yell" } }, Material.WOOL, (short) 4));
			Items.items.add(new ItemInfo("Light Green Wool", new String[][] { { "wool", "lig", "gree" }, { "wool", "gree" } }, Material.WOOL, (short) 5));
			Items.items.add(new ItemInfo("Pink Wool", new String[][] { { "wool", "pink" } }, Material.WOOL, (short) 6));
			Items.items.add(new ItemInfo("Gray Wool", new String[][] { { "wool", "gray" }, { "wool", "grey" } }, Material.WOOL, (short) 7));
			Items.items.add(new ItemInfo("Light Gray Wool", new String[][] { { "lig", "wool", "gra" }, { "lig", "wool", "gre" } }, Material.WOOL, (short) 8));
			Items.items.add(new ItemInfo("Cyan Wool", new String[][] { { "wool", "cya" } }, Material.WOOL, (short) 9));
			Items.items.add(new ItemInfo("Purple Wool", new String[][] { { "wool", "pur" } }, Material.WOOL, (short) 10));
			Items.items.add(new ItemInfo("Blue Wool", new String[][] { { "wool", "blue" } }, Material.WOOL, (short) 11));
			Items.items.add(new ItemInfo("Brown Wool", new String[][] { { "wool", "brow" } }, Material.WOOL, (short) 12));
			Items.items.add(new ItemInfo("Dark Green Wool", new String[][] { { "wool", "dar", "gree" }, { "wool", "gree" } }, Material.WOOL, (short) 13));
			Items.items.add(new ItemInfo("Red Wool", new String[][] { { "wool", "red" } }, Material.WOOL, (short) 14));
			Items.items.add(new ItemInfo("Black Wool", new String[][] { { "wool", "bla" } }, Material.WOOL, (short) 15));
			Items.items.add(new ItemInfo("Dandelion", new String[][] { { "flow", "yell" }, { "dande" } }, Material.YELLOW_FLOWER));
			Items.items.add(new ItemInfo("Brown Mushroom", new String[][] { { "mush", "bro" } }, Material.BROWN_MUSHROOM));
			Items.items.add(new ItemInfo("Red Mushroom", new String[][] { { "mush", "red" } }, Material.RED_MUSHROOM));
			Items.items.add(new ItemInfo("Gold Block", new String[][] { { "gold", "bl" } }, Material.GOLD_BLOCK));
			Items.items.add(new ItemInfo("Iron Block", new String[][] { { "iron", "bl" } }, Material.IRON_BLOCK));
			Items.items.add(new ItemInfo("Stone Slab", new String[][] { { "slab", "sto" }, { "slab" }, { "step", "ston" } }, Material.STEP));
			Items.items.add(new ItemInfo("Sandstone Slab", new String[][] { { "slab", "sand", "sto" }, { "step", "sand", "sto" } }, Material.STEP, (short) 1));
			Items.items.add(new ItemInfo("Wooden Slab", new String[][] { { "slab", "woo" }, { "step", "woo" } }, Material.STEP, (short) 2));
			Items.items.add(new ItemInfo("Cobblestone Slab", new String[][] { { "slab", "cob", "sto" }, { "slab", "cob" } }, Material.STEP, (short) 3));
			Items.items.add(new ItemInfo("Brick Slab", new String[][] { { "slab", "bri" } }, Material.STEP, (short) 4));
			Items.items.add(new ItemInfo("Stone Brick Slab", new String[][] { { "slab", "sto", "bri" } }, Material.STEP, (short) 5));
			Items.items.add(new ItemInfo("Brick", new String[][] { { "bric" } }, Material.BRICK));
			Items.items.add(new ItemInfo("TNT", new String[][] { { "tnt" }, { "boom" } }, Material.TNT));
			Items.items.add(new ItemInfo("Bookshelf", new String[][] { { "bookshe" }, { "book", "she" } }, Material.BOOKSHELF));
			Items.items.add(new ItemInfo("Moss Stone", new String[][] { { "moss", "sto" }, { "moss" } }, Material.MOSSY_COBBLESTONE));
			Items.items.add(new ItemInfo("Obsidian", new String[][] { { "obsi" } }, Material.OBSIDIAN));
			Items.items.add(new ItemInfo("Torch", new String[][] { { "torc" } }, Material.TORCH));
			Items.items.add(new ItemInfo("Fire", new String[][] { { "fire" } }, Material.FIRE));
			Items.items.add(new ItemInfo("Monster Spawner", new String[][] { { "spawn" } }, Material.MOB_SPAWNER));
			Items.items.add(new ItemInfo("Oak Wood Stairs", new String[][] { { "stair", "wood" }, { "oak", "stair" } }, Material.WOOD_STAIRS));
			Items.items.add(new ItemInfo("Jungle Wood Stairs", new String[][] { { "jungle", "stair" }, { "jung", "stair", "woo" } }, Material.JUNGLE_WOOD_STAIRS));
			Items.items.add(new ItemInfo("Spruce Wood Stairs", new String[][] { { "spruce", "stai" }, { "spru", "stair", "woo" } }, Material.SPRUCE_WOOD_STAIRS));
			Items.items.add(new ItemInfo("Birch Wood Stairs", new String[][] { { "birch", "stair" }, { "birc", "stai", "woo" } }, Material.BIRCH_WOOD_STAIRS));
			Items.items.add(new ItemInfo("Chest", new String[][] { { "chest" } }, Material.CHEST));
			Items.items.add(new ItemInfo("Diamond Ore", new String[][] { { "ore", "diam" } }, Material.DIAMOND_ORE));
			Items.items.add(new ItemInfo("Diamond Block", new String[][] { { "diam", "bl" } }, Material.DIAMOND_BLOCK));
			Items.items.add(new ItemInfo("Crafting Table", new String[][] { { "benc" }, { "squa" }, { "craft" } }, Material.WORKBENCH));
			Items.items.add(new ItemInfo("Farmland", new String[][] { { "soil" }, { "farm" } }, Material.SOIL));
			Items.items.add(new ItemInfo("Furnace", new String[][] { { "furna" }, { "cooke" } }, Material.FURNACE));
			Items.items.add(new ItemInfo("Ladder", new String[][] { { "ladd" } }, Material.LADDER));
			Items.items.add(new ItemInfo("Rails", new String[][] { { "rail" }, { "trac" } }, Material.RAILS));
			Items.items.add(new ItemInfo("Cobblestone Stairs", new String[][] { { "stair", "cob", "sto" }, { "stair", "cob" } }, Material.COBBLESTONE_STAIRS));
			Items.items.add(new ItemInfo("Lever", new String[][] { { "lever" }, { "switc" } }, Material.LEVER));
			Items.items.add(new ItemInfo("Stone Pressure Plate", new String[][] { { "pres", "plat", "ston" } }, Material.STONE_PLATE));
			Items.items.add(new ItemInfo("Wooden Pressure Plate", new String[][] { { "pres", "plat", "wood" } }, Material.WOOD_PLATE));
			Items.items.add(new ItemInfo("Redstone Ore", new String[][] { { "redst", "ore" } }, Material.REDSTONE_ORE));
			Items.items.add(new ItemInfo("Redstone Torch", new String[][] { { "torc", "red" }, { "torc", "rs" } }, Material.REDSTONE_TORCH_ON));
			Items.items.add(new ItemInfo("Stone Button", new String[][] { { "stone", "button" }, { "button" } }, Material.STONE_BUTTON));
			Items.items.add(new ItemInfo("Snow", new String[][] { { "tile", "snow" }, { "snow", "slab" }, { "snow" } }, Material.SNOW));
			Items.items.add(new ItemInfo("Ice", new String[][] { { "ice" } }, Material.ICE));
			Items.items.add(new ItemInfo("Snow Block", new String[][] { { "blo", "snow" } }, Material.SNOW_BLOCK));
			Items.items.add(new ItemInfo("Cactus", new String[][] { { "cact" } }, Material.CACTUS));
			Items.items.add(new ItemInfo("Clay Block", new String[][] { { "clay", "blo" } }, Material.CLAY));
			Items.items.add(new ItemInfo("Jukebox", new String[][] { { "jukeb" } }, Material.JUKEBOX));
			Items.items.add(new ItemInfo("Oak Fence", new String[][] { { "oak", "fence" }, { "fence" } }, Material.FENCE));
			Items.items.add(new ItemInfo("Pumpkin", new String[][] { { "pump" } }, Material.PUMPKIN));
			Items.items.add(new ItemInfo("Netherrack", new String[][] { { "netherr" }, { "netherst" }, { "hellst" } }, Material.NETHERRACK));
			Items.items.add(new ItemInfo("Soul Sand", new String[][] { { "soul", "sand" }, { "soul" }, { "slowsa" }, { "nether", "mud" }, { "slow", "sand" }, { "quick", "sand" }, { "mud" } }, Material.SOUL_SAND));
			Items.items.add(new ItemInfo("Glowstone", new String[][] { { "glow", "stone" }, { "light", "stone" } }, Material.GLOWSTONE));
			Items.items.add(new ItemInfo("Portal", new String[][] { { "port" } }, Material.PORTAL));
			Items.items.add(new ItemInfo("Jack-O-Lantern", new String[][] { { "jack" }, { "lante" } }, Material.JACK_O_LANTERN));
			Items.items.add(new ItemInfo("Wooden Trapdoor", new String[][] { { "trap", "doo" }, { "woo", "hatc" }, { "woo", "trap", "door" } }, Material.TRAP_DOOR));
			Items.items.add(new ItemInfo("Stone Monster Egg", new String[][] { { "mons", "egg" }, { "sto", "mons", "egg" }, { "hid", "silver" } }, Material.MONSTER_EGGS));
			Items.items.add(new ItemInfo("Stone Brick Monster Egg", new String[][] { { "sto", "bri", "mons", "egg" }, { "hid", "silver", "sto", "bri" } }, Material.MONSTER_EGGS, (short) 2));
			Items.items.add(new ItemInfo("Mossy Stone Brick Monster Egg", new String[][] { { "moss", "sto", "bri", "mons", "egg" }, { "hid", "silver", "mos", "sto", "bri" } }, Material.MONSTER_EGGS, (short) 3));
			Items.items.add(new ItemInfo("Huge Brown Mushroom", new String[][] { { "bro", "huge", "mush" } }, Material.HUGE_MUSHROOM_1));
			Items.items.add(new ItemInfo("Huge Red Mushroom", new String[][] { { "red", "huge", "mush" } }, Material.HUGE_MUSHROOM_2));
			Items.items.add(new ItemInfo("Stone Brick", new String[][] { { "sto", "bric" }, { "smoo", "bric" } }, Material.SMOOTH_BRICK, (short) 0));
			Items.items.add(new ItemInfo("Iron Fence", new String[][] { { "bars", "iron" }, { "fence", "iron" } }, Material.IRON_FENCE));
			Items.items.add(new ItemInfo("Glass Pane", new String[][] { { "thin", "gla" }, { "pane" }, { "gla", "pane" } }, Material.THIN_GLASS));
			Items.items.add(new ItemInfo("Melon Block", new String[][] { { "melon" } }, Material.MELON_BLOCK));
			Items.items.add(new ItemInfo("Mossy Stone Brick", new String[][] { { "moss", "sto", "bri" }, { "moss", "smoo", "bri" }, { "moss", "smoo" }, { "moss", "sto" } }, Material.SMOOTH_BRICK, (short) 1));
			Items.items.add(new ItemInfo("Cracked Stone Brick", new String[][] { { "cra", "sto", "bri" }, { "cra", "sto" }, { "cra", "smoo", "bri" }, { "cra", "smoo" } }, Material.SMOOTH_BRICK, (short) 2));
			Items.items.add(new ItemInfo("Chiseled Stone Brick", new String[][] { { "chis", "sto", "bri" }, { "chis", "sto" }, { "chis", "smoo", "bri" } }, Material.SMOOTH_BRICK, (short) 3));
			Items.items.add(new ItemInfo("Brick Stairs", new String[][] { { "stair", "bri" } }, Material.BRICK_STAIRS));
			Items.items.add(new ItemInfo("Fence Gate", new String[][] { { "gate", "fen" }, { "gate" } }, Material.FENCE_GATE));
			Items.items.add(new ItemInfo("Vines", new String[][] { { "vine" }, { "ivy" } }, Material.VINE));
			Items.items.add(new ItemInfo("Stone Brick Stairs", new String[][] { { "stair", "sto", "bri" }, { "stair", "sto" }, { "stair", "smoo", "bri" }, { "stair", "smoo" } }, Material.SMOOTH_STAIRS));
			Items.items.add(new ItemInfo("Iron Shovel", new String[][] { { "shov", "ir" }, { "spad", "ir" } }, Material.IRON_SPADE));
			Items.items.add(new ItemInfo("Iron Pickaxe", new String[][] { { "pick", "ir" } }, Material.IRON_PICKAXE));
			Items.items.add(new ItemInfo("Iron Axe", new String[][] { { "axe", "ir" } }, Material.IRON_AXE));
			Items.items.add(new ItemInfo("Flint and Steel", new String[][] { { "steel" }, { "lighter" }, { "flin", "ste" } }, Material.FLINT_AND_STEEL));
			Items.items.add(new ItemInfo("Apple", new String[][] { { "appl" } }, Material.APPLE));
			Items.items.add(new ItemInfo("Bow", new String[][] { { "bow" } }, Material.BOW));
			Items.items.add(new ItemInfo("Arrow", new String[][] { { "arro" } }, Material.ARROW));
			Items.items.add(new ItemInfo("Coal", new String[][] { { "coal" } }, Material.COAL));
			Items.items.add(new ItemInfo("Charcoal", new String[][] { { "char", "coal" }, { "char" } }, Material.COAL, (short) 1));
			Items.items.add(new ItemInfo("Diamond", new String[][] { { "diamo" } }, Material.DIAMOND));
			Items.items.add(new ItemInfo("Iron Ingot", new String[][] { { "ingo", "ir" }, { "iron" } }, Material.IRON_INGOT));
			Items.items.add(new ItemInfo("Gold Ingot", new String[][] { { "ingo", "go" }, { "gold" } }, Material.GOLD_INGOT));
			Items.items.add(new ItemInfo("Iron Sword", new String[][] { { "swor", "ir" } }, Material.IRON_SWORD));
			Items.items.add(new ItemInfo("Wooden Sword", new String[][] { { "swor", "woo" } }, Material.WOOD_SWORD));
			Items.items.add(new ItemInfo("Wooden Shovel", new String[][] { { "shov", "wo" }, { "spad", "wo" } }, Material.WOOD_SPADE));
			Items.items.add(new ItemInfo("Wooden Pickaxe", new String[][] { { "pick", "woo" } }, Material.WOOD_PICKAXE));
			Items.items.add(new ItemInfo("Wooden Axe", new String[][] { { "axe", "woo" } }, Material.WOOD_AXE));
			Items.items.add(new ItemInfo("Stone Sword", new String[][] { { "swor", "sto" } }, Material.STONE_SWORD));
			Items.items.add(new ItemInfo("Stone Shovel", new String[][] { { "shov", "sto" }, { "spad", "sto" } }, Material.STONE_SPADE));
			Items.items.add(new ItemInfo("Stone Pickaxe", new String[][] { { "pick", "sto" } }, Material.STONE_PICKAXE));
			Items.items.add(new ItemInfo("Stone Axe", new String[][] { { "axe", "sto" } }, Material.STONE_AXE));
			Items.items.add(new ItemInfo("Diamond Sword", new String[][] { { "swor", "dia" } }, Material.DIAMOND_SWORD));
			Items.items.add(new ItemInfo("Diamond Shovel", new String[][] { { "shov", "dia" }, { "spad", "dia" } }, Material.DIAMOND_SPADE));
			Items.items.add(new ItemInfo("Diamond Pickaxe", new String[][] { { "pick", "dia" } }, Material.DIAMOND_PICKAXE));
			Items.items.add(new ItemInfo("Diamond Axe", new String[][] { { "axe", "dia" } }, Material.DIAMOND_AXE));
			Items.items.add(new ItemInfo("Stick", new String[][] { { "stic" } }, Material.STICK));
			Items.items.add(new ItemInfo("Bowl", new String[][] { { "bo", "wl" } }, Material.BOWL));
			Items.items.add(new ItemInfo("Mushroom Soup", new String[][] { { "soup" } }, Material.MUSHROOM_SOUP));
			Items.items.add(new ItemInfo("Gold Sword", new String[][] { { "swor", "gol" } }, Material.GOLD_SWORD));
			Items.items.add(new ItemInfo("Gold Shovel", new String[][] { { "shov", "gol" }, { "spad", "gol" } }, Material.GOLD_SPADE));
			Items.items.add(new ItemInfo("Gold Pickaxe", new String[][] { { "pick", "gol" } }, Material.GOLD_PICKAXE));
			Items.items.add(new ItemInfo("Gold Axe", new String[][] { { "axe", "gol" } }, Material.GOLD_AXE));
			Items.items.add(new ItemInfo("String", new String[][] { { "stri" } }, Material.STRING));
			Items.items.add(new ItemInfo("Feather", new String[][] { { "feat" } }, Material.FEATHER));
			Items.items.add(new ItemInfo("Gunpowder", new String[][] { { "gun" }, { "sulph" } }, Material.SULPHUR));
			Items.items.add(new ItemInfo("Wooden Hoe", new String[][] { { "hoe", "wo" } }, Material.WOOD_HOE));
			Items.items.add(new ItemInfo("Stone Hoe", new String[][] { { "hoe", "sto" } }, Material.STONE_HOE));
			Items.items.add(new ItemInfo("Iron Hoe", new String[][] { { "hoe", "iro" } }, Material.IRON_HOE));
			Items.items.add(new ItemInfo("Diamond Hoe", new String[][] { { "hoe", "dia" } }, Material.DIAMOND_HOE));
			Items.items.add(new ItemInfo("Gold Hoe", new String[][] { { "hoe", "go" } }, Material.GOLD_HOE));
			Items.items.add(new ItemInfo("Seeds", new String[][] { { "seed" } }, Material.SEEDS));
			Items.items.add(new ItemInfo("Wheat", new String[][] { { "whea" } }, Material.WHEAT));
			Items.items.add(new ItemInfo("Bread", new String[][] { { "brea" } }, Material.BREAD));
			Items.items.add(new ItemInfo("Leather Cap", new String[][] { { "cap", "lea" }, { "hat", "lea" }, { "helm", "lea" } }, Material.LEATHER_HELMET));
			Items.items.add(new ItemInfo("Leather Tunic", new String[][] { { "tun", "lea" }, { "ches", "lea" } }, Material.LEATHER_CHESTPLATE));
			Items.items.add(new ItemInfo("Leather Pants", new String[][] { { "pan", "lea" }, { "trou", "lea" }, { "leg", "lea" } }, Material.LEATHER_LEGGINGS));
			Items.items.add(new ItemInfo("Leather Boots", new String[][] { { "boo", "lea" } }, Material.LEATHER_BOOTS));
			Items.items.add(new ItemInfo("Chainmail Helmet", new String[][] { { "cap", "cha" }, { "hat", "cha" }, { "helm", "cha" } }, Material.CHAINMAIL_HELMET));
			Items.items.add(new ItemInfo("Chainmail Chestplate", new String[][] { { "tun", "cha" }, { "ches", "cha" } }, Material.CHAINMAIL_CHESTPLATE));
			Items.items.add(new ItemInfo("Chainmail Leggings", new String[][] { { "pan", "cha" }, { "trou", "cha" }, { "leg", "cha" } }, Material.CHAINMAIL_LEGGINGS));
			Items.items.add(new ItemInfo("Chainmail Boots", new String[][] { { "boo", "cha" } }, Material.CHAINMAIL_BOOTS));
			Items.items.add(new ItemInfo("Iron Helmet", new String[][] { { "cap", "ir" }, { "hat", "ir" }, { "helm", "ir" } }, Material.IRON_HELMET));
			Items.items.add(new ItemInfo("Iron Chestplate", new String[][] { { "tun", "ir" }, { "ches", "ir" } }, Material.IRON_CHESTPLATE));
			Items.items.add(new ItemInfo("Iron Leggings", new String[][] { { "pan", "ir" }, { "trou", "ir" }, { "leg", "ir" } }, Material.IRON_LEGGINGS));
			Items.items.add(new ItemInfo("Iron Boots", new String[][] { { "boo", "ir" } }, Material.IRON_BOOTS));
			Items.items.add(new ItemInfo("Diamond Helmet", new String[][] { { "cap", "dia" }, { "hat", "dia" }, { "helm", "dia" } }, Material.DIAMOND_HELMET));
			Items.items.add(new ItemInfo("Diamond Chestplate", new String[][] { { "tun", "dia" }, { "ches", "dia" } }, Material.DIAMOND_CHESTPLATE));
			Items.items.add(new ItemInfo("Diamond Leggings", new String[][] { { "pan", "dia" }, { "trou", "dia" }, { "leg", "dia" } }, Material.DIAMOND_LEGGINGS));
			Items.items.add(new ItemInfo("Diamond Boots", new String[][] { { "boo", "dia" } }, Material.DIAMOND_BOOTS));
			Items.items.add(new ItemInfo("Gold Helmet", new String[][] { { "cap", "go" }, { "hat", "go" }, { "helm", "go" } }, Material.GOLD_HELMET));
			Items.items.add(new ItemInfo("Gold Chestplate", new String[][] { { "tun", "go" }, { "ches", "go" } }, Material.GOLD_CHESTPLATE));
			Items.items.add(new ItemInfo("Gold Leggings", new String[][] { { "pan", "go" }, { "trou", "go" }, { "leg", "go" } }, Material.GOLD_LEGGINGS));
			Items.items.add(new ItemInfo("Gold Boots", new String[][] { { "boo", "go" } }, Material.GOLD_BOOTS));
			Items.items.add(new ItemInfo("Flint", new String[][] { { "flin" } }, Material.FLINT));
			Items.items.add(new ItemInfo("Raw Porkchop", new String[][] { { "pork" }, { "ham" } }, Material.PORK));
			Items.items.add(new ItemInfo("Cooked Porkchop", new String[][] { { "pork", "cook" }, { "baco" } }, Material.GRILLED_PORK));
			Items.items.add(new ItemInfo("Paintings", new String[][] { { "paint" } }, Material.PAINTING));
			Items.items.add(new ItemInfo("Golden Apple", new String[][] { { "appl", "go" } }, Material.GOLDEN_APPLE));
			Items.items.add(new ItemInfo("Enchanted Golden Apple", new String[][] { { "appl", "go", "ench" } }, Material.GOLDEN_APPLE, (short) 1));
			Items.items.add(new ItemInfo("Sign", new String[][] { { "sign" } }, Material.SIGN));
			Items.items.add(new ItemInfo("Wooden Door", new String[][] { { "door", "wood" }, { "door" } }, Material.WOOD_DOOR));
			Items.items.add(new ItemInfo("Bucket", new String[][] { { "buck" }, { "bukk" } }, Material.BUCKET));
			Items.items.add(new ItemInfo("Water Bucket", new String[][] { { "water", "buck" } }, Material.WATER_BUCKET));
			Items.items.add(new ItemInfo("Lava Bucket", new String[][] { { "lava", "buck" } }, Material.LAVA_BUCKET));
			Items.items.add(new ItemInfo("Minecart", new String[][] { { "cart" } }, Material.MINECART));
			Items.items.add(new ItemInfo("Saddle", new String[][] { { "sad" }, { "pig" } }, Material.SADDLE));
			Items.items.add(new ItemInfo("Iron Door", new String[][] { { "door", "iron" } }, Material.IRON_DOOR));
			Items.items.add(new ItemInfo("Redstone Dust", new String[][] { { "red", "ston", "dust" }, { "dust", "rs" }, { "dust", "red" }, { "reds" } }, Material.REDSTONE));
			Items.items.add(new ItemInfo("Snowball", new String[][] { { "snow", "ball" } }, Material.SNOW_BALL));
			Items.items.add(new ItemInfo("Boat", new String[][] { { "boat" } }, Material.BOAT));
			Items.items.add(new ItemInfo("Leather", new String[][] { { "lea" }, { "hide" } }, Material.LEATHER));
			Items.items.add(new ItemInfo("Milk Bucket", new String[][] { { "buck", "mil" }, { "milk" } }, Material.MILK_BUCKET));
			Items.items.add(new ItemInfo("Clay Brick", new String[][] { { "bric", "cl" }, { "sin", "bric" } }, Material.CLAY_BRICK));
			Items.items.add(new ItemInfo("Clay", new String[][] { { "clay" } }, Material.CLAY_BALL));
			Items.items.add(new ItemInfo("Sugar Cane", new String[][] { { "reed" }, { "cane" } }, Material.SUGAR_CANE));
			Items.items.add(new ItemInfo("Paper", new String[][] { { "pape" } }, Material.PAPER));
			Items.items.add(new ItemInfo("Book", new String[][] { { "book" } }, Material.BOOK));
			Items.items.add(new ItemInfo("Slimeball", new String[][] { { "slime" } }, Material.SLIME_BALL));
			Items.items.add(new ItemInfo("Storage Minecart", new String[][] { { "cart", "sto" }, { "cart", "che" }, { "cargo" } }, Material.STORAGE_MINECART));
			Items.items.add(new ItemInfo("Powered Minecart", new String[][] { { "cart", "pow" }, { "engine" } }, Material.POWERED_MINECART));
			Items.items.add(new ItemInfo("Egg", new String[][] { { "egg" } }, Material.EGG));
			Items.items.add(new ItemInfo("Compass", new String[][] { { "comp" } }, Material.COMPASS));
			Items.items.add(new ItemInfo("Fishing Rod", new String[][] { { "rod" }, { "rod", "fish" }, { "pole", "fish" } }, Material.FISHING_ROD));
			Items.items.add(new ItemInfo("Clock", new String[][] { { "cloc" }, { "watc" } }, Material.WATCH));
			Items.items.add(new ItemInfo("Glowstone Dust", new String[][] { { "glow", "sto", "dus" }, { "glow", "dus" }, { "ligh", "dust" } }, Material.GLOWSTONE_DUST));
			Items.items.add(new ItemInfo("Raw Fish", new String[][] { { "fish" }, { "fish", "raw" } }, Material.RAW_FISH));
			Items.items.add(new ItemInfo("Cooked Fish", new String[][] { { "fish", "coo" }, { "kipper" } }, Material.COOKED_FISH));
			Items.items.add(new ItemInfo("Ink Sac", new String[][] { { "ink" }, { "dye", "bla" } }, Material.INK_SACK));
			Items.items.add(new ItemInfo("Red Dye", new String[][] { { "dye", "red" }, { "pain", "red" }, { "pet", "ros" }, { "pet", "red" } }, Material.INK_SACK, (short) 1));
			Items.items.add(new ItemInfo("Cactus Green", new String[][] { { "cact", "gree" }, { "dye", "gree" }, { "pain", "gree" } }, Material.INK_SACK, (short) 2));
			Items.items.add(new ItemInfo("Cocoa Beans", new String[][] { { "bean" }, { "choco" }, { "cocoa" }, { "dye", "bro" }, { "pain", "bro" } }, Material.INK_SACK, (short) 3));
			Items.items.add(new ItemInfo("Lapis Lazuli", new String[][] { { "lapi", "lazu" }, { "dye", "lapi" }, { "dye", "blu" }, { "pain", "blu" } }, Material.INK_SACK, (short) 4));
			Items.items.add(new ItemInfo("Purple Dye", new String[][] { { "dye", "pur" }, { "pain", "pur" } }, Material.INK_SACK, (short) 5));
			Items.items.add(new ItemInfo("Cyan Dye", new String[][] { { "dye", "cya" }, { "pain", "cya" } }, Material.INK_SACK, (short) 6));
			Items.items.add(new ItemInfo("Light Gray Dye", new String[][] { { "dye", "lig", "gra" }, { "dye", "lig", "grey" }, { "pain", "lig", "grey" }, { "pain", "lig", "grey" } }, Material.INK_SACK, (short) 7));
			Items.items.add(new ItemInfo("Gray Dye", new String[][] { { "dye", "gra" }, { "dye", "grey" }, { "pain", "grey" }, { "pain", "grey" } }, Material.INK_SACK, (short) 8));
			Items.items.add(new ItemInfo("Pink Dye", new String[][] { { "dye", "pin" }, { "pain", "pin" } }, Material.INK_SACK, (short) 9));
			Items.items.add(new ItemInfo("Lime Dye", new String[][] { { "dye", "lim" }, { "pain", "lim" }, { "dye", "lig", "gree" }, { "pain", "lig", "gree" } }, Material.INK_SACK, (short) 10));
			Items.items.add(new ItemInfo("Dandelion Yellow", new String[][] { { "dye", "yel" }, { "yel", "dan" }, { "pet", "dan" }, { "pet", "yel" } }, Material.INK_SACK, (short) 11));
			Items.items.add(new ItemInfo("Light Blue Dye", new String[][] { { "dye", "lig", "blu" }, { "pain", "lig", "blu" } }, Material.INK_SACK, (short) 12));
			Items.items.add(new ItemInfo("Magenta Dye", new String[][] { { "dye", "mag" }, { "pain", "mag" } }, Material.INK_SACK, (short) 13));
			Items.items.add(new ItemInfo("Orange Dye", new String[][] { { "dye", "ora" }, { "pain", "ora" } }, Material.INK_SACK, (short) 14));
			Items.items.add(new ItemInfo("Bone Meal", new String[][] { { "bonem" }, { "bone", "me" }, { "dye", "whi" }, { "pain", "whi" } }, Material.INK_SACK, (short) 15));
			Items.items.add(new ItemInfo("Bone", new String[][] { { "bone" }, { "femur" } }, Material.BONE));
			Items.items.add(new ItemInfo("Sugar", new String[][] { { "suga" } }, Material.SUGAR));
			Items.items.add(new ItemInfo("Cake", new String[][] { { "cake" } }, Material.CAKE));
			Items.items.add(new ItemInfo("Melon Slice", new String[][] { { "sli", "melo" } }, Material.MELON));
			Items.items.add(new ItemInfo("Pumpkin Seed", new String[][] { { "seed", "pump" } }, Material.PUMPKIN_SEEDS));
			Items.items.add(new ItemInfo("Melon Seed", new String[][] { { "seed", "melo" } }, Material.MELON_SEEDS));
			Items.items.add(new ItemInfo("Raw Beef", new String[][] { { "beef", "raw" } }, Material.RAW_BEEF));
			Items.items.add(new ItemInfo("Steak", new String[][] { { "steak" }, { "beef", "coo" } }, Material.COOKED_BEEF));
			Items.items.add(new ItemInfo("Raw Chicken", new String[][] { { "chi", "raw" } }, Material.RAW_CHICKEN));
			Items.items.add(new ItemInfo("Cooked Chicken", new String[][] { { "chi", "coo" } }, Material.COOKED_CHICKEN));
			Items.items.add(new ItemInfo("Rotten Flesh", new String[][] { { "flesh" }, { "rott" } }, Material.ROTTEN_FLESH));
			Items.items.add(new ItemInfo("Bed", new String[][] { { "bed" } }, Material.BED));
			Items.items.add(new ItemInfo("Redstone Repeater", new String[][] { { "repe", "reds" }, { "diod" }, { "repeat" } }, Material.DIODE));
			Items.items.add(new ItemInfo("Cookie", new String[][] { { "cooki" } }, Material.COOKIE));
			Items.items.add(new ItemInfo("Map", new String[][] { { "map" } }, Material.MAP));
			Items.items.add(new ItemInfo("Empty Map", new String[][] { { "empt", "ma" } }, Material.EMPTY_MAP));
			Items.items.add(new ItemInfo("Shears", new String[][] { { "shea" } }, Material.SHEARS));
			Items.items.add(new ItemInfo("Ender Pearl", new String[][] { { "end", "pear" }, { "pearl" } }, Material.ENDER_PEARL));
			Items.items.add(new ItemInfo("Mycelium", new String[][] { { "myc" } }, Material.MYCEL));
			Items.items.add(new ItemInfo("Lily Pad", new String[][] { { "lil", "pad" }, { "lil", "wat" } }, Material.WATER_LILY));
			Items.items.add(new ItemInfo("Cauldron Block", new String[][] { { "bloc", "cauld" } }, Material.CAULDRON));
			Items.items.add(new ItemInfo("Cauldron", new String[][] { { "cauld" } }, Material.CAULDRON_ITEM));
			Items.items.add(new ItemInfo("Enchantment Table", new String[][] { { "ench", "tab" } }, Material.ENCHANTMENT_TABLE));
			Items.items.add(new ItemInfo("Brewing Stand Block", new String[][] { { "bloc", "brew", "stan" }, { "alch", "bloc" } }, Material.BREWING_STAND));
			Items.items.add(new ItemInfo("Brewing Stand", new String[][] { { "brew", "stan" }, { "alch", "stand" }, { "alch", "tab" } }, Material.BREWING_STAND_ITEM));
			Items.items.add(new ItemInfo("Nether Brick", new String[][] { { "neth", "bric" } }, Material.NETHER_BRICK));
			Items.items.add(new ItemInfo("Nether Brick Stairs", new String[][] { { "neth", "stair" }, { "neth", "stai", "bric" } }, Material.NETHER_BRICK_STAIRS));
			Items.items.add(new ItemInfo("Nether Brick Fence", new String[][] { { "neth", "fence" }, { "neth", "fence", "bric" } }, Material.NETHER_FENCE));
			Items.items.add(new ItemInfo("Netherwarts", new String[][] { { "wart" }, { "neth", "war" } }, Material.NETHER_WARTS));
			Items.items.add(new ItemInfo("Netherstalk", new String[][] { { "neth", "stalk" } }, Material.NETHER_STALK));
			Items.items.add(new ItemInfo("End Portal", new String[][] { { "end", "port" } }, Material.ENDER_PORTAL));
			Items.items.add(new ItemInfo("End Portal Frame", new String[][] { { "fram", "end", "port" } }, Material.ENDER_PORTAL_FRAME));
			Items.items.add(new ItemInfo("End Stone", new String[][] { { "end", "ston" } }, Material.ENDER_STONE));
			Items.items.add(new ItemInfo("Dragon Egg", new String[][] { { "drag", "egg" } }, Material.DRAGON_EGG));
			Items.items.add(new ItemInfo("Blaze Rod", new String[][] { { "rod", "blaz" } }, Material.BLAZE_ROD));
			Items.items.add(new ItemInfo("Ghast Tear", new String[][] { { "ghas", "tear" } }, Material.GHAST_TEAR));
			Items.items.add(new ItemInfo("Gold Nugget", new String[][] { { "nugg", "gold" } }, Material.GOLD_NUGGET));
			Items.items.add(new ItemInfo("Glass Bottle", new String[][] { { "bottl" }, { "glas", "bott" }, { "empt", "bott" } }, Material.GLASS_BOTTLE));
			Items.items.add(new ItemInfo("Potion", new String[][] { { "potio" } }, Material.POTION));
			Items.items.add(new ItemInfo("Water Bottle", new String[][] { { "wat", "bot" } }, Material.POTION, (short) 0));
			Items.items.add(new ItemInfo("Awkward Potion", new String[][] { { "poti", "awk" } }, Material.POTION, (short) 16));
			Items.items.add(new ItemInfo("Thick Potion", new String[][] { { "poti", "thic" } }, Material.POTION, (short) 32));
			Items.items.add(new ItemInfo("Mundane Potion (Extended)", new String[][] { { "poti", "mund", "ext" } }, Material.POTION, (short) 64));
			Items.items.add(new ItemInfo("Mundane Potion", new String[][] { { "poti", "mund" } }, Material.POTION, (short) 8192));
			Items.items.add(new ItemInfo("Potion of Regeneration", new String[][] { { "poti", "rege" } }, Material.POTION, (short) 8193));
			Items.items.add(new ItemInfo("Potion of Regeneration (Extended)", new String[][] { { "poti", "rege", "ext" } }, Material.POTION, (short) 8257));
			Items.items.add(new ItemInfo("Potion of Regeneration II", new String[][] { { "poti", "rege", "2" }, { "poti", "rege", "ii" } }, Material.POTION, (short) 8225));
			Items.items.add(new ItemInfo("Potion of Swiftness", new String[][] { { "poti", "swif" }, { "poti", "speed" } }, Material.POTION, (short) 8194));
			Items.items.add(new ItemInfo("Potion of Swiftness (Extended)", new String[][] { { "poti", "swif", "ext" }, { "poti", "speed", "ext" } }, Material.POTION, (short) 8258));
			Items.items.add(new ItemInfo("Potion of Swiftness II", new String[][] { { "poti", "swif", "2" }, { "poti", "swif", "ii" }, { "poti", "speed", "2" }, { "poti", "speed", "ii" } }, Material.POTION, (short) 8226));
			Items.items.add(new ItemInfo("Potion of Fire Resistance", new String[][] { { "poti", "fire" } }, Material.POTION, (short) 8195));
			Items.items.add(new ItemInfo("Potion of Fire Resistance (Extended)", new String[][] { { "poti", "fire", "ext" } }, Material.POTION, (short) 8259));
			Items.items.add(new ItemInfo("Potion of Fire Resistance (Reverted)", new String[][] { { "poti", "fire", "rev" } }, Material.POTION, (short) 8227));
			Items.items.add(new ItemInfo("Potion of Healing", new String[][] { { "poti", "heal" } }, Material.POTION, (short) 8197));
			Items.items.add(new ItemInfo("Potion of Healing (Reverted)", new String[][] { { "poti", "heal", "rev" } }, Material.POTION, (short) 8261));
			Items.items.add(new ItemInfo("Potion of Healing II", new String[][] { { "poti", "heal", "2" }, { "poti", "heal", "ii" } }, Material.POTION, (short) 8229));
			Items.items.add(new ItemInfo("Potion of Strength", new String[][] { { "poti", "str" } }, Material.POTION, (short) 8201));
			Items.items.add(new ItemInfo("Potion of Strength (Extended)", new String[][] { { "poti", "str", "ext" } }, Material.POTION, (short) 8265));
			Items.items.add(new ItemInfo("Potion of Strength II", new String[][] { { "poti", "str", "2" }, { "poti", "str", "ii" } }, Material.POTION, (short) 8233));
			Items.items.add(new ItemInfo("Potion of Poison", new String[][] { { "poti", "pois" } }, Material.POTION, (short) 8196));
			Items.items.add(new ItemInfo("Potion of Poison (Extended)", new String[][] { { "poti", "pois", "ext" } }, Material.POTION, (short) 8260));
			Items.items.add(new ItemInfo("Potion of Poison II", new String[][] { { "poti", "pois", "2" }, { "poti", "pois", "ii" } }, Material.POTION, (short) 8228));
			Items.items.add(new ItemInfo("Potion of Weakness", new String[][] { { "poti", "weak" } }, Material.POTION, (short) 8200));
			Items.items.add(new ItemInfo("Potion of Weakness (Extended)", new String[][] { { "poti", "weak", "ext" } }, Material.POTION, (short) 8264));
			Items.items.add(new ItemInfo("Potion of Weakness (Reverted)", new String[][] { { "poti", "weak", "rev" } }, Material.POTION, (short) 8232));
			Items.items.add(new ItemInfo("Potion of Slowness", new String[][] { { "poti", "slow" } }, Material.POTION, (short) 8202));
			Items.items.add(new ItemInfo("Potion of Slowness (Extended)", new String[][] { { "poti", "slow", "ext" } }, Material.POTION, (short) 8266));
			Items.items.add(new ItemInfo("Potion of Slowness (Reverted)", new String[][] { { "poti", "slow", "rev" } }, Material.POTION, (short) 8234));
			Items.items.add(new ItemInfo("Potion of Harming", new String[][] { { "poti", "harm" } }, Material.POTION, (short) 8204));
			Items.items.add(new ItemInfo("Potion of Harming (Reverted)", new String[][] { { "poti", "harm", "rev" } }, Material.POTION, (short) 8268));
			Items.items.add(new ItemInfo("Potion of Harming II", new String[][] { { "poti", "harm", "2" }, { "poti", "harm", "ii" } }, Material.POTION, (short) 8236));
			Items.items.add(new ItemInfo("Splash Mundane Potion", new String[][] { { "poti", "mund", "spl" } }, Material.POTION, (short) 16384));
			Items.items.add(new ItemInfo("Splash Potion of Regeneration", new String[][] { { "poti", "rege", "spl" } }, Material.POTION, (short) 16385));
			Items.items.add(new ItemInfo("Splash Potion of Regeneration (Extended)", new String[][] { { "poti", "rege", "spl", "ext" } }, Material.POTION, (short) 16449));
			Items.items.add(new ItemInfo("Splash Potion of Regeneration II", new String[][] { { "poti", "rege", "spl", "2" }, { "poti", "rege", "spl", "ii" } }, Material.POTION, (short) 16417));
			Items.items.add(new ItemInfo("Splash Potion of Swiftness", new String[][] { { "poti", "swif", "spl" }, { "poti", "speed", "spl" } }, Material.POTION, (short) 16386));
			Items.items.add(new ItemInfo("Splash Potion of Swiftness (Extended)", new String[][] { { "poti", "swif", "spl", "ext" }, { "poti", "speed", "spl", "ext" } }, Material.POTION, (short) 16450));
			Items.items.add(new ItemInfo("Splash Potion of Swiftness II", new String[][] { { "poti", "swif", "spl", "2" }, { "poti", "swif", "spl", "ii" }, { "poti", "speed", "spl", "2" }, { "poti", "speed", "spl", "ii" } }, Material.POTION, (short) 16418));
			Items.items.add(new ItemInfo("Splash Potion of Fire Resistance", new String[][] { { "poti", "fire", "spl" } }, Material.POTION, (short) 16387));
			Items.items.add(new ItemInfo("Splash Potion of Fire Resistance (Extended)", new String[][] { { "poti", "fire", "spl", "ext" } }, Material.POTION, (short) 16451));
			Items.items.add(new ItemInfo("Splash Potion of Fire Resistance (Reverted)", new String[][] { { "poti", "fire", "spl", "rev" } }, Material.POTION, (short) 16419));
			Items.items.add(new ItemInfo("Splash Potion of Healing", new String[][] { { "poti", "heal", "spl" } }, Material.POTION, (short) 16389));
			Items.items.add(new ItemInfo("Splash Potion of Healing (Reverted)", new String[][] { { "poti", "heal", "spl", "rev" } }, Material.POTION, (short) 16453));
			Items.items.add(new ItemInfo("Splash Potion of Healing II", new String[][] { { "poti", "heal", "spl", "2" }, { "poti", "heal", "spl", "ii" } }, Material.POTION, (short) 16421));
			Items.items.add(new ItemInfo("Splash Potion of Strength", new String[][] { { "poti", "str", "spl" } }, Material.POTION, (short) 16393));
			Items.items.add(new ItemInfo("Splash Potion of Strength (Extended)", new String[][] { { "poti", "str", "spl", "ext" } }, Material.POTION, (short) 16457));
			Items.items.add(new ItemInfo("Splash Potion of Strength II", new String[][] { { "poti", "str", "spl", "2" }, { "poti", "str", "spl", "ii" } }, Material.POTION, (short) 16425));
			Items.items.add(new ItemInfo("Splash Potion of Poison", new String[][] { { "poti", "pois", "spl" } }, Material.POTION, (short) 16388));
			Items.items.add(new ItemInfo("Splash Potion of Poison (Extended)", new String[][] { { "poti", "pois", "spl", "ext" } }, Material.POTION, (short) 16452));
			Items.items.add(new ItemInfo("Splash Potion of Poison II", new String[][] { { "poti", "pois", "spl", "2" }, { "poti", "pois", "spl", "ii" } }, Material.POTION, (short) 16420));
			Items.items.add(new ItemInfo("Splash Potion of Weakness", new String[][] { { "poti", "weak", "spl" } }, Material.POTION, (short) 16392));
			Items.items.add(new ItemInfo("Splash Potion of Weakness (Extended)", new String[][] { { "poti", "weak", "spl", "ext" } }, Material.POTION, (short) 16456));
			Items.items.add(new ItemInfo("Splash Potion of Weakness (Reverted)", new String[][] { { "poti", "weak", "spl", "rev" } }, Material.POTION, (short) 16424));
			Items.items.add(new ItemInfo("Splash Potion of Slowness", new String[][] { { "poti", "slow", "spl" } }, Material.POTION, (short) 16394));
			Items.items.add(new ItemInfo("Splash Potion of Slowness (Extended)", new String[][] { { "poti", "slow", "spl", "ext" } }, Material.POTION, (short) 16458));
			Items.items.add(new ItemInfo("Splash Potion of Slowness (Reverted)", new String[][] { { "poti", "slow", "spl", "rev" } }, Material.POTION, (short) 16426));
			Items.items.add(new ItemInfo("Splash Potion of Harming", new String[][] { { "poti", "harm", "spl" } }, Material.POTION, (short) 16396));
			Items.items.add(new ItemInfo("Splash Potion of Harming (Reverted)", new String[][] { { "poti", "harm", "spl", "rev" } }, Material.POTION, (short) 16460));
			Items.items.add(new ItemInfo("Splash Potion of Harming II", new String[][] { { "poti", "harm", "spl", "2" }, { "poti", "harm", "spl", "ii" } }, Material.POTION, (short) 16428));
			Items.items.add(new ItemInfo("Spider Eye", new String[][] { { "spid", "eye" } }, Material.SPIDER_EYE));
			Items.items.add(new ItemInfo("Fermented Spider Eye", new String[][] { { "ferm", "spid", "eye" } }, Material.FERMENTED_SPIDER_EYE));
			Items.items.add(new ItemInfo("Blaze Powder", new String[][] { { "powd", "blaz" } }, Material.BLAZE_POWDER));
			Items.items.add(new ItemInfo("Magma Cream", new String[][] { { "crea", "magm" } }, Material.MAGMA_CREAM));
			Items.items.add(new ItemInfo("Eye of Ender", new String[][] { { "end", "ey" } }, Material.EYE_OF_ENDER));
			Items.items.add(new ItemInfo("Glistering Melon", new String[][] { { "melo", "glis" } }, Material.SPECKLED_MELON));
			Items.items.add(new ItemInfo("Spawn Egg", new String[][] { { "spaw", "egg" } }, Material.MONSTER_EGG));
			Items.items.add(new ItemInfo("Creeper Spawn Egg", new String[][] { { "creep", "egg" } }, Material.MONSTER_EGG, (short) 50));
			Items.items.add(new ItemInfo("Skeleton Spawn Egg", new String[][] { { "skele", "egg" } }, Material.MONSTER_EGG, (short) 51));
			Items.items.add(new ItemInfo("Spider Spawn Egg", new String[][] { { "spider", "egg" } }, Material.MONSTER_EGG, (short) 52));
			Items.items.add(new ItemInfo("Zombie Spawn Egg", new String[][] { { "zombie", "egg" } }, Material.MONSTER_EGG, (short) 54));
			Items.items.add(new ItemInfo("Slime Spawn Egg", new String[][] { { "slime", "egg" } }, Material.MONSTER_EGG, (short) 55));
			Items.items.add(new ItemInfo("Ghast Spawn Egg", new String[][] { { "ghast", "egg" } }, Material.MONSTER_EGG, (short) 56));
			Items.items.add(new ItemInfo("Zombie Pigman Spawn Egg", new String[][] { { "zomb", "pig", "egg" } }, Material.MONSTER_EGG, (short) 57));
			Items.items.add(new ItemInfo("Enderman Spawn Egg", new String[][] { { "end", "man", "egg" } }, Material.MONSTER_EGG, (short) 58));
			Items.items.add(new ItemInfo("Cave Spider Spawn Egg", new String[][] { { "cav", "spid", "egg" } }, Material.MONSTER_EGG, (short) 59));
			Items.items.add(new ItemInfo("Silverfish Spawn Egg", new String[][] { { "silv", "fish", "egg" } }, Material.MONSTER_EGG, (short) 60));
			Items.items.add(new ItemInfo("Blaze Spawn Egg", new String[][] { { "blaze", "egg" } }, Material.MONSTER_EGG, (short) 61));
			Items.items.add(new ItemInfo("Magma Cube Spawn Egg", new String[][] { { "mag", "cub", "egg" }, { "neth", "slim", "egg" } }, Material.MONSTER_EGG, (short) 62));
			Items.items.add(new ItemInfo("Pig Spawn Egg", new String[][] { { "pig", "spa", "egg" }, { "pig", "egg" } }, Material.MONSTER_EGG, (short) 90));
			Items.items.add(new ItemInfo("Sheep Spawn Egg", new String[][] { { "sheep", "egg" } }, Material.MONSTER_EGG, (short) 91));
			Items.items.add(new ItemInfo("Cow Spawn Egg", new String[][] { { "cow", "spa", "egg" }, { "cow", "egg" } }, Material.MONSTER_EGG, (short) 92));
			Items.items.add(new ItemInfo("Chicken Spawn Egg", new String[][] { { "chick", "egg" } }, Material.MONSTER_EGG, (short) 93));
			Items.items.add(new ItemInfo("Squid Spawn Egg", new String[][] { { "squi", "spa", "egg" }, { "squi", "egg" } }, Material.MONSTER_EGG, (short) 94));
			Items.items.add(new ItemInfo("Wolf Spawn Egg", new String[][] { { "wolf", "spa", "egg" }, { "wolf", "egg" } }, Material.MONSTER_EGG, (short) 95));
			Items.items.add(new ItemInfo("Mooshroom Spawn Egg", new String[][] { { "moo", "room", "egg" }, { "mush", "cow", "egg" } }, Material.MONSTER_EGG, (short) 96));
			Items.items.add(new ItemInfo("Ocelot Spawn Egg", new String[][] { { "ocelo", "egg" }, { "ozelo", "egg" } }, Material.MONSTER_EGG, (short) 98));
			Items.items.add(new ItemInfo("Villager Spawn Egg", new String[][] { { "villa", "egg" } }, Material.MONSTER_EGG, (short) 120));
			Items.items.add(new ItemInfo("Bottle 'o Enchanting", new String[][] { { "bot", "ench" }, { "bot", "xp" } }, Material.EXP_BOTTLE));
			Items.items.add(new ItemInfo("Fire Charge", new String[][] { { "fir", "char" } }, Material.FIREBALL));
			Items.items.add(new ItemInfo("13 Disc", new String[][] { { "dis", "gol" }, { "rec", "gol" }, { "13", "disc" }, { "13", "reco" } }, Material.GOLD_RECORD));
			Items.items.add(new ItemInfo("cat Disc", new String[][] { { "dis", "gre" }, { "rec", "gre" }, { "cat", "disc" }, { "cat", "reco" } }, Material.GREEN_RECORD));
			Items.items.add(new ItemInfo("blocks Disc", new String[][] { { "block", "disc" }, { "block", "reco" }, { "3", "disc" }, { "3", "reco" } }, Material.RECORD_3));
			Items.items.add(new ItemInfo("chirp Disc", new String[][] { { "chirp", "disc" }, { "chirp", "reco" }, { "4", "disc" }, { "4", "reco" } }, Material.RECORD_4));
			Items.items.add(new ItemInfo("far Disc", new String[][] { { "far", "disc" }, { "far", "reco" }, { "5", "disc" }, { "5", "reco" } }, Material.RECORD_5));
			Items.items.add(new ItemInfo("mall Disc", new String[][] { { "mall", "disc" }, { "mall", "reco" }, { "6", "disc" }, { "6", "reco" } }, Material.RECORD_6));
			Items.items.add(new ItemInfo("mellohi Disc", new String[][] { { "mello", "disc" }, { "mello", "reco" }, { "7", "disc" }, { "7", "reco" } }, Material.RECORD_7));
			Items.items.add(new ItemInfo("stahl Disc", new String[][] { { "stahl", "disc" }, { "stahl", "reco" }, { "8", "disc" }, { "8", "reco" } }, Material.RECORD_8));
			Items.items.add(new ItemInfo("strad Disc", new String[][] { { "strad", "disc" }, { "strad", "reco" }, { "9", "disc" }, { "9", "reco" } }, Material.RECORD_9));
			Items.items.add(new ItemInfo("ward Disc", new String[][] { { "ward", "disc" }, { "ward", "reco" }, { "10", "disc" }, { "10", "reco" } }, Material.RECORD_10));
			Items.items.add(new ItemInfo("11 Disc", new String[][] { { "11", "disc" }, { "11", "reco" } }, Material.RECORD_11));
			Items.items.add(new ItemInfo("wait Disc", new String[][] { { "12", "disc" }, { "wait", "disc" }, { "12", "reco" }, { "wait", "reco" } }, Material.RECORD_12));
			Items.items.add(new ItemInfo("Redstone Lamp", new String[][] { { "lamp" }, { "lamp", "redst" } }, Material.REDSTONE_LAMP_OFF));
			Items.items.add(new ItemInfo("Redstone Torch Off", new String[][] { { "off", "red", "sto", "tor" } }, Material.REDSTONE_TORCH_OFF));
			Items.items.add(new ItemInfo("Emerald Ore", new String[][] { { "emer", "ore" } }, Material.EMERALD_ORE));
			Items.items.add(new ItemInfo("Emerald", new String[][] { { "emer" } }, Material.EMERALD));
			Items.items.add(new ItemInfo("Emerald Block", new String[][] { { "emer", "blo" } }, Material.EMERALD_BLOCK));
			Items.items.add(new ItemInfo("Ender Chest", new String[][] { { "end", "ches" } }, Material.ENDER_CHEST));
			Items.items.add(new ItemInfo("Tripwire Hook", new String[][] { { "hoo", "trip" } }, Material.TRIPWIRE_HOOK));
			Items.items.add(new ItemInfo("Tripwire", new String[][] { { "trip" } }, Material.TRIPWIRE));
			Items.items.add(new ItemInfo("Sandstone Stair", new String[][] { { "stair", "sand", "sto" }, { "stair", "sand" } }, Material.SANDSTONE_STAIRS));
			Items.items.add(new ItemInfo("Oak Slab", new String[][] { { "slab", "oak" }, { "step", "oak" } }, Material.WOOD_STEP));
			Items.items.add(new ItemInfo("Spruce Slab", new String[][] { { "slab", "spru" }, { "step", "spru" } }, Material.WOOD_STEP, (short) 1));
			Items.items.add(new ItemInfo("Birch Slab", new String[][] { { "slab", "birc" }, { "step", "birc" } }, Material.WOOD_STEP, (short) 2));
			Items.items.add(new ItemInfo("Jungle Wood Slab", new String[][] { { "jung", "wood", "sla" }, { "slab", "jung" }, { "step", "jung" } }, Material.WOOD_STEP, (short) 3));
			Items.items.add(new ItemInfo("Book and Quill", new String[][] { { "qui", "book" } }, Material.BOOK_AND_QUILL));
			Items.items.add(new ItemInfo("Written Book", new String[][] { { "wri", "book" } }, Material.WRITTEN_BOOK));
			Items.items.add(new ItemInfo("Cocoa Pod", new String[][] { { "coco" }, { "coc", "pod" } }, Material.COCOA));
			Items.items.add(new ItemInfo("Command Block", new String[][] { { "comm" } }, Material.COMMAND));
			Items.items.add(new ItemInfo("Beacon Block", new String[][] { { "beac" } }, Material.BEACON));
			Items.items.add(new ItemInfo("Anvil", new String[][] { { "anv" } }, Material.ANVIL));
			Items.items.add(new ItemInfo("Slightly Damaged Anvil", new String[][] { { "dam", "anv" }, { "sli", "anv" } }, Material.ANVIL, (short) 1));
			Items.items.add(new ItemInfo("Very Damaged Anvil", new String[][] { { "ver", "dam", "anv" }, { "ver", "anv" } }, Material.ANVIL, (short) 2));
			Items.items.add(new ItemInfo("Flower Pot Block", new String[][] { { "blo", "flow", "pot" } }, Material.FLOWER_POT));
			Items.items.add(new ItemInfo("Flower Pot", new String[][] { { "flow", "pot" } }, Material.FLOWER_POT_ITEM));
			Items.items.add(new ItemInfo("Cobblestone Wall", new String[][] { { "cobble", "wall" } }, Material.COBBLE_WALL));
			Items.items.add(new ItemInfo("Mossy Cobblestone Wall", new String[][] { { "mos", "cob", "wall" } }, Material.COBBLE_WALL, (short) 1));
			Items.items.add(new ItemInfo("Item Frame", new String[][] { { "fram" } }, Material.ITEM_FRAME));
			Items.items.add(new ItemInfo("Skeleton Skull", new String[][] { { "skel", "skul" }, { "skel", "hea" } }, Material.SKULL_ITEM));
			Items.items.add(new ItemInfo("Wither Skeleton Skull", new String[][] { { "wither", "skul" }, { "with", "hea" } }, Material.SKULL_ITEM, (short) 1));
			Items.items.add(new ItemInfo("Zombie Head", new String[][] { { "zomb", "hea" }, { "zomb", "skul" } }, Material.SKULL_ITEM, (short) 2));
			Items.items.add(new ItemInfo("Human Head", new String[][] { { "huma", "skul" }, { "huma", "hea" } }, Material.SKULL_ITEM, (short) 3));
			Items.items.add(new ItemInfo("Creeper Head", new String[][] { { "cree", "skul" }, { "cree", "hea" } }, Material.SKULL_ITEM, (short) 4));
			Items.items.add(new ItemInfo("Carrot", new String[][] { { "carro" } }, Material.CARROT_ITEM));
			Items.items.add(new ItemInfo("Golden Carrot", new String[][] { { "carr", "gol" } }, Material.GOLDEN_CARROT));
			Items.items.add(new ItemInfo("Carrot Block", new String[][] { { "blo", "carr" } }, Material.CARROT));
			Items.items.add(new ItemInfo("Carrot on a Stick", new String[][] { { "sti", "carr" } }, Material.CARROT_STICK));
			Items.items.add(new ItemInfo("Potato", new String[][] { { "pota" } }, Material.POTATO_ITEM));
			Items.items.add(new ItemInfo("Potato Block", new String[][] { { "blo", "pota" } }, Material.POTATO));
			Items.items.add(new ItemInfo("Baked Potato", new String[][] { { "pota", "bak" } }, Material.BAKED_POTATO));
			Items.items.add(new ItemInfo("Poisonous Potato", new String[][] { { "pota", "poi" } }, Material.POISONOUS_POTATO));
			Items.items.add(new ItemInfo("Wood Button", new String[][] { { "woo", "butto" } }, Material.WOOD_BUTTON));
			Items.items.add(new ItemInfo("Pumpkin Pie", new String[][] { { "pie" }, { "pumpk", "pie" } }, Material.PUMPKIN_PIE));
			Items.items.add(new ItemInfo("Potion of Invisibility", new String[][] { { "poti", "invi" } }, Material.POTION, (short) 8206));
			Items.items.add(new ItemInfo("Potion of Invisibility (Extended)", new String[][] { { "poti", "invi", "ext" } }, Material.POTION, (short) 8270));
			Items.items.add(new ItemInfo("Potion of Night Vision", new String[][] { { "poti", "nigh", "visi" }, { "poti", "visio" } }, Material.POTION, (short) 8198));
			Items.items.add(new ItemInfo("Potion of Night Vision (Extended)", new String[][] { { "poti", "nigh", "visi", "ext" }, { "poti", "visio", "ext" } }, Material.POTION, (short) 8262));
			Items.items.add(new ItemInfo("Enchanted Book", new String[][] { { "ench", "boo" } }, Material.ENCHANTED_BOOK));
			Items.items.add(new ItemInfo("Nether Star", new String[][] { { "star", "neth" } }, Material.NETHER_STAR));
			Items.items.add(new ItemInfo("Firework Star", new String[][] { { "fire", "star" } }, Material.FIREWORK_CHARGE));
			Items.items.add(new ItemInfo("Firework Rocket", new String[][] { { "rocket" }, { "firework" } }, Material.FIREWORK));
			Items.items.add(new ItemInfo("White Firework Star", new String[][] { { "whi", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 1));
			Items.items.add(new ItemInfo("Orange Firework Star", new String[][] { { "ora", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 2));
			Items.items.add(new ItemInfo("Magenta Firework Star", new String[][] { { "mag", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 3));
			Items.items.add(new ItemInfo("Light Blue Firework Star", new String[][] { { "blu", "lig", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 4));
			Items.items.add(new ItemInfo("Yellow Firework Star", new String[][] { { "yell", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 5));
			Items.items.add(new ItemInfo("Lime Firework Star", new String[][] { { "lim", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 6));
			Items.items.add(new ItemInfo("Pink Firework Star", new String[][] { { "pin", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 7));
			Items.items.add(new ItemInfo("Gray Firework Star", new String[][] { { "gra", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 8));
			Items.items.add(new ItemInfo("Light Gray Firework Star", new String[][] { { "lig", "gra", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 9));
			Items.items.add(new ItemInfo("Cyan Firework Star", new String[][] { { "cya", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 10));
			Items.items.add(new ItemInfo("Purple Firework Star", new String[][] { { "pur", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 11));
			Items.items.add(new ItemInfo("Blue Firework Star", new String[][] { { "blue", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 12));
			Items.items.add(new ItemInfo("Brown Firework Star", new String[][] { { "bro", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 13));
			Items.items.add(new ItemInfo("Green Firework Star", new String[][] { { "gre", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 14));
			Items.items.add(new ItemInfo("Red Firework Star", new String[][] { { "red", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 15));
			Items.items.add(new ItemInfo("Black Firework Star", new String[][] { { "bla", "fire", "star" } }, Material.FIREWORK_CHARGE, (short) 16));
			Items.items.add(new ItemInfo("Dead Bush", new String[][] { { "dea", "bush" } }, Material.DEAD_BUSH));
			Items.items.add(new ItemInfo("Nether Brick Slab", new String[][] { { "sla", "net", "bri" }, { "step", "net", "bri" } }, Material.STEP, (short) 6));
			Items.items.add(new ItemInfo("Activator Rail", new String[][] { { "rail", "acti" }, { "trac", "acti" }, { "activ" } }, Material.ACTIVATOR_RAIL));
			Items.items.add(new ItemInfo("Block of Redstone", new String[][] { { "block", "red" }, { "block", "rs" } }, Material.REDSTONE_BLOCK));
			Items.items.add(new ItemInfo("Daylight Sensor", new String[][] { { "day", "sen" }, { "ligh", "sen" } }, Material.DAYLIGHT_DETECTOR));
			Items.items.add(new ItemInfo("Dropper", new String[][] { { "drop" } }, Material.DROPPER));
			Items.items.add(new ItemInfo("Hopper", new String[][] { { "hop", "item" }, { "hop" } }, Material.HOPPER));
			Items.items.add(new ItemInfo("Explosive Minecart", new String[][] { { "cart", "tnt" }, { "cart", "exp" } }, Material.EXPLOSIVE_MINECART));
			Items.items.add(new ItemInfo("Hopper Minecart", new String[][] { { "cart", "hop" }, { "hop" } }, Material.HOPPER_MINECART));
			Items.items.add(new ItemInfo("Redstone Comparator", new String[][] { { "rs", "compara" }, { "red", "comparat" }, { "comparat" } }, Material.REDSTONE_COMPARATOR));
			Items.items.add(new ItemInfo("Trapped Chest", new String[][] { { "tra", "ches" } }, Material.TRAPPED_CHEST));
			Items.items.add(new ItemInfo("Nether Brick Item", new String[][] { { "neth", "bric", "it" } }, Material.NETHER_BRICK_ITEM));
			Items.items.add(new ItemInfo("Nether Quartz", new String[][] { { "neth", "qua" }, { "qua" } }, Material.QUARTZ));
			Items.items.add(new ItemInfo("Nether Quartz Ore", new String[][] { { "neth", "qua", "ore" }, { "qua", "ore" } }, Material.QUARTZ_ORE));
			Items.items.add(new ItemInfo("Quartz Block", new String[][] { { "qua", "blo" } }, Material.QUARTZ_BLOCK));
			Items.items.add(new ItemInfo("Quartz Slab", new String[][] { { "qua", "slab" }, { "qua", "step" } }, Material.STEP, (short) 7));
			Items.items.add(new ItemInfo("Quartz Double Slab", new String[][] { { "qua", "dou", "sla" }, { "qua", "dou", "step" } }, Material.DOUBLE_STEP, (short) 7));
			Items.items.add(new ItemInfo("Quartz Stairs", new String[][] { { "qua", "stair" } }, Material.QUARTZ_STAIRS));
			Items.items.add(new ItemInfo("Chiseled Quartz", new String[][] { { "qua", "chis" } }, Material.QUARTZ_BLOCK, (short) 1));
			Items.items.add(new ItemInfo("Quartz Pillar", new String[][] { { "qua", "pil" } }, Material.QUARTZ_BLOCK, (short) 2));
			Items.items.add(new ItemInfo("Weighted Gold Plate", new String[][] { { "wei", "plat", "gol" }, { "pres", "plat", "gol" } }, Material.GOLD_PLATE));
			Items.items.add(new ItemInfo("Weighted Iron Plate", new String[][] { { "wei", "plat", "iro" }, { "pres", "plat", "iro" } }, Material.IRON_PLATE));
		}

		public static ItemInfo itemById(final int typeId)
		{
			return Items.getItemByMaterial(Material.getMaterial(typeId), (short) 0);
		}

		public static ItemInfo itemById(final int typeId, final short subType)
		{
			return Items.getItemByMaterial(Material.getMaterial(typeId), subType);
		}

		public static ItemInfo getItemByStack(final ItemStack itemStack)
		{
			if (itemStack == null)
			{
				return null;
			}

			for (final ItemInfo item : Items.items)
			{
				if (itemStack.getType().equals(item.getType()) && item.isDurable())
				{
					return item;
				}
				else if (itemStack.getType().equals(item.getType()) && (item.getSubTypeId() == itemStack.getDurability()))
				{
					return item;
				}
			}

			return null;
		}

		public static ItemInfo itemByItem(final ItemInfo item)
		{
			for (final ItemInfo i : Items.items)
			{
				if (item.equals(i))
				{
					return i;
				}
			}
			return null;
		}

		public static ItemInfo getItemByMaterial(final Material type)
		{
			return Items.getItemByMaterial(type, (short) 0);
		}

		public static ItemInfo getItemByMaterial(final Material type, final short subType)
		{
			for (final ItemInfo item : Items.items)
			{
				if ((item.getType() == type) && (item.getSubTypeId() == subType))
				{
					return item;
				}
			}
			return null;
		}

		public static ItemInfo getItemInfo(final String string)
		{
			Pattern pattern = Pattern.compile("(?i)^(\\d+)$");
			Matcher matcher = pattern.matcher(string);
			if (matcher.find())
			{
				final int id = Integer.parseInt(matcher.group(1));
				return Items.itemById(id);
			}

			matcher.reset();
			pattern = Pattern.compile("(?i)^(\\d+):(\\d+)$");
			matcher = pattern.matcher(string);
			if (matcher.find())
			{
				final int id = Integer.parseInt(matcher.group(1));
				final short type = Short.parseShort(matcher.group(2));
				return Items.itemById(id, type);
			}

			matcher.reset();
			pattern = Pattern.compile("(?i)^(.*)$");
			matcher = pattern.matcher(string);
			if (matcher.find())
			{
				final String name = matcher.group(1);
				return Items.getItemBySearch(name);
			}

			return null;
		}

		public static ItemInfo getItemByName(final String name)
		{
			final java.util.Vector<String> array = new java.util.Vector<String>();
			array.add(name);
			return Items.getItemByName(name);
		}

		public static ItemInfo getItemByName(final java.util.Vector<String> search)
		{
			final String searchString = Items.join(search, " ");
			return Items.getItemBySearch(searchString);
		}

		public static ItemInfo[] getItemByNames(final java.util.Vector<String> search, final boolean multi)
		{
			final String searchString = Items.join(search, " ");
			return Items.getItemInfos(searchString, multi);
		}

		public static ItemInfo[] getItemInfos(final String searchString, final boolean multi)
		{
			if (multi == false)
			{
				return new ItemInfo[] { Items.getItemBySearch(searchString) };
			}

			final ItemInfo[] itemList = new ItemInfo[] {};

			if (searchString.matches("\\d+:\\d+"))
			{
				final String[] params = searchString.split(":");
				final int typeId = Integer.parseInt(params[0]);
				final short subTypeId = Short.parseShort(params[1]);
				for (final ItemInfo item : Items.items)
				{
					if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId))
					{
						itemList[0] = item;
						break;
					}
				}
			}
			else if (searchString.matches("\\d+"))
			{
				final int typeId = Integer.parseInt(searchString);
				int i = 0;
				for (final ItemInfo item : Items.items)
				{
					if (item.getId() == typeId)
					{
						itemList[i] = item;
						i++;
					}
				}
			}
			else
			{
				int i = 0;
				for (final ItemInfo item : Items.items)
				{
					for (final String[] attributes : item.search)
					{
						boolean match = false;
						for (final String attribute : attributes)
						{
							if (searchString.toLowerCase().contains(attribute))
							{
								match = true;
								break;
							}
						}
						if (match)
						{
							itemList[i] = item;
							i++;
						}
					}
				}
			}

			return itemList;
		}

		public static ItemInfo getItemBySearch(final String searchString)
		{
			ItemInfo matchedItem = null;
			int matchedItemStrength = 0;
			int matchedValue = 0;

			if (searchString.matches("\\d+:\\d+"))
			{
				final String[] params = searchString.split(":");
				final int typeId = Integer.parseInt(params[0]);
				final short subTypeId = Short.parseShort(params[1]);
				for (final ItemInfo item : Items.items)
				{
					if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId))
					{
						matchedItem = item;
						break;
					}
				}
			}
			else if (searchString.matches("\\d+"))
			{
				final int typeId = Integer.parseInt(searchString);
				final short subTypeId = 0;
				for (final ItemInfo item : Items.items)
				{
					if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId))
					{
						matchedItem = item;
						break;
					}
				}
			}
			else if (searchString.matches("\\w+:\\d+"))
			{
				final String[] params = searchString.split(":");
				final short subTypeId = Short.parseShort(params[1]);
				final ItemInfo namedItem = Items.getItemBySearch(params[0]);

				if (namedItem != null)
				{
					final int typeId = namedItem.getId();
					for (final ItemInfo item : Items.items)
					{
						if ((item.getId() == typeId) && (item.getSubTypeId() == subTypeId))
						{
							matchedItem = item;
							break;
						}
					}
				}
			}
			else
			{
				for (final ItemInfo item : Items.items)
				{
					for (final String[] attributes : item.search)
					{
						int val = 0;
						boolean match = false;
						for (final String attribute : attributes)
						{
							if (searchString.toLowerCase().contains(attribute))
							{
								val += attribute.length();
								match = true;
							}
							else
							{
								match = false;
								break;
							}
						}

						// THIS was a match
						if (match)
						{
							if ((matchedItem == null) || (val > matchedValue) || (attributes.length > matchedItemStrength))
							{
								matchedItem = item;
								matchedValue = val;
								matchedItemStrength = attributes.length;
							}
						}
					}
				}
			}

			return matchedItem;
		}

		private static String join(final java.util.Vector<String> list, final String glue)
		{
			String joined = null;
			for (final String element : list)
			{
				if (joined == null)
				{
					joined = element;
				}
				else
				{
					joined += glue + element;
				}
			}

			if (joined == null)
			{
				return "";
			}
			else
			{
				return joined;
			}
		}
	}

	public static final class ItemInfo
	{

		public final Material material;
		public short subTypeId;
		public final String name;
		public final String[][] search;

		public ItemInfo(final String name, final String[][] search, final Material material)
		{
			this.material = material;
			this.name = name;
			this.subTypeId = 0;
			this.search = search.clone();
		}

		public ItemInfo(final String name, final String[][] search, final Material material, final short subTypeId)
		{
			this.name = name;
			this.material = material;
			this.subTypeId = subTypeId;
			this.search = search.clone();
		}

		public Material getType()
		{
			return this.material;
		}

		public short getSubTypeId()
		{
			return this.subTypeId;
		}

		public void setSubTypeId(final short id)
		{
			this.subTypeId = id;
		}

		public int getStackSize()
		{
			return this.material.getMaxStackSize();
		}

		public int getId()
		{
			return this.material.getId();
		}

		public boolean isEdible()
		{
			return this.material.isEdible();
		}

		public boolean isBlock()
		{
			return this.material.isBlock();
		}

		public String getName()
		{
			return this.name;
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = (17 * hash) + this.getId();
			hash = (17 * hash) + this.subTypeId;
			return hash;
		}

		public boolean isDurable()
		{
			return (this.material.getMaxDurability() > 0);
		}

		public ItemStack toStack()
		{
			return new ItemStack(this.material, 1, this.subTypeId);
		}

		@Override
		public String toString()
		{
			return String.format("%s[%d:%d]", this.name, this.material.getId(), this.subTypeId);
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (obj == null)
			{
				return false;
			}
			else if (this == obj)
			{
				return true;
			}
			else if (!(obj instanceof ItemInfo))
			{
				return false;
			}
			else
			{
				return (((ItemInfo) obj).material == this.material) && (((ItemInfo) obj).subTypeId == this.subTypeId);
			}
		}
	}

	public static class Handler<F, S>
	{

		private final F f;
		private final S s;

		public Handler(final F firstValue, final S secondValue)
		{
			this.f = firstValue;
			this.s = secondValue;
		}

		public F getPrimary()
		{
			return this.f;
		}

		public S getSecundary()
		{
			return this.s;
		}

		@Override
		public String toString()
		{
			return String.valueOf(this.f) + "  :  " + String.valueOf(this.s);
		}
	}

	public static final class Cuboid implements Cloneable, ConfigurationSerializable, Iterable<Block>
	{

		protected String worldName;
		protected final Vector minimumPoint, maximumPoint;

		public Cuboid(final Cuboid cuboid)
		{
			this(cuboid.worldName, cuboid.minimumPoint.getX(), cuboid.minimumPoint.getY(), cuboid.minimumPoint.getZ(), cuboid.maximumPoint.getX(), cuboid.maximumPoint.getY(), cuboid.maximumPoint.getZ());
		}

		public Cuboid(final Location loc)
		{
			this(loc, loc);
		}

		public Cuboid(final Location loc1, final Location loc2)
		{
			if ((loc1 != null) && (loc2 != null))
			{
				if ((loc1.getWorld() != null) && (loc2.getWorld() != null))
				{
					if (!loc1.getWorld().getUID().equals(loc2.getWorld().getUID()))
					{
						throw new IllegalStateException("The 2 locations of the cuboid must be in the same world!");
					}
				}
				else
				{
					throw new NullPointerException("One/both of the worlds is/are null!");
				}
				this.worldName = loc1.getWorld().getName();

				final double xPos1 = Math.min(loc1.getX(), loc2.getX());
				final double yPos1 = Math.min(loc1.getY(), loc2.getY());
				final double zPos1 = Math.min(loc1.getZ(), loc2.getZ());
				final double xPos2 = Math.max(loc1.getX(), loc2.getX());
				final double yPos2 = Math.max(loc1.getY(), loc2.getY());
				final double zPos2 = Math.max(loc1.getZ(), loc2.getZ());
				this.minimumPoint = new Vector(xPos1, yPos1, zPos1);
				this.maximumPoint = new Vector(xPos2, yPos2, zPos2);
			}
			else
			{
				throw new NullPointerException("One/both of the locations is/are null!");
			}
		}

		public Cuboid(final String worldName, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2)
		{
			if ((worldName == null) || (Bukkit.getServer().getWorld(worldName) == null))
			{
				throw new NullPointerException("One/both of the worlds is/are null!");
			}
			this.worldName = worldName;

			final double xPos1 = Math.min(x1, x2);
			final double xPos2 = Math.max(x1, x2);
			final double yPos1 = Math.min(y1, y2);
			final double yPos2 = Math.max(y1, y2);
			final double zPos1 = Math.min(z1, z2);
			final double zPos2 = Math.max(z1, z2);
			this.minimumPoint = new Vector(xPos1, yPos1, zPos1);
			this.maximumPoint = new Vector(xPos2, yPos2, zPos2);
		}

		public boolean containsLocation(final Location location)
		{
			return (location != null) && location.toVector().isInAABB(this.minimumPoint, this.maximumPoint);
		}

		public boolean containsVector(final Vector vector)
		{
			return (vector != null) && Framework.toLocation(this.getWorld(), vector).toVector().isInAABB(this.minimumPoint, this.maximumPoint);
		}

		public java.util.Vector<Block> getBlocks()
		{
			final java.util.Vector<Block> blockList = new java.util.Vector<Block>();
			final World world = this.getWorld();
			if (world != null)
			{
				for (int x = this.minimumPoint.getBlockX(); x <= this.maximumPoint.getBlockX(); x++)
				{
					for (int y = this.minimumPoint.getBlockY(); (y <= this.maximumPoint.getBlockY()) && (y <= world.getMaxHeight()); y++)
					{
						for (int z = this.minimumPoint.getBlockZ(); z <= this.maximumPoint.getBlockZ(); z++)
						{
							blockList.add(world.getBlockAt(x, y, z));
						}
					}
				}
			}
			return blockList;
		}

		public Location getLowerLocation()
		{
			return Framework.toLocation(this.getWorld(), this.minimumPoint);
		}

		public double getLowerX()
		{
			return this.minimumPoint.getX();
		}

		public double getLowerY()
		{
			return this.minimumPoint.getY();
		}

		public double getLowerZ()
		{
			return this.minimumPoint.getZ();
		}

		public Location getUpperLocation()
		{
			return Framework.toLocation(this.getWorld(), this.maximumPoint);
		}

		public double getUpperX()
		{
			return this.maximumPoint.getX();
		}

		public double getUpperY()
		{
			return this.maximumPoint.getY();
		}

		public double getUpperZ()
		{
			return this.maximumPoint.getZ();
		}

		public double getVolume()
		{
			return ((this.getUpperX() - this.getLowerX()) + 1) * ((this.getUpperY() - this.getLowerY()) + 1) * ((this.getUpperZ() - this.getLowerZ()) + 1);
		}

		public World getWorld()
		{
			final World world = Bukkit.getServer().getWorld(this.worldName);
			if (world == null)
			{
				throw new NullPointerException("World '" + this.worldName + "' is not loaded.");
			}
			return world;
		}

		public void setWorld(final World world)
		{
			if (world != null)
			{
				this.worldName = world.getName();
			}
			else
			{
				throw new NullPointerException("The world cannot be null.");
			}
		}

		@Override
		public Cuboid clone()
		{
			return new Cuboid(this);
		}

		@Override
		public ListIterator<Block> iterator()
		{
			return this.getBlocks().listIterator();
		}

		@Override
		public Map<String, Object> serialize()
		{
			final Map<String, Object> serializedCuboid = new HashMap<>();
			serializedCuboid.put("worldName", this.worldName);
			serializedCuboid.put("x1", this.minimumPoint.getX());
			serializedCuboid.put("x2", this.maximumPoint.getX());
			serializedCuboid.put("y1", this.minimumPoint.getY());
			serializedCuboid.put("y2", this.maximumPoint.getY());
			serializedCuboid.put("z1", this.minimumPoint.getZ());
			serializedCuboid.put("z2", this.maximumPoint.getZ());
			return serializedCuboid;
		}

		public static Cuboid deserialize(final Map<String, Object> serializedCuboid)
		{
			try
			{
				final String worldName = (String) serializedCuboid.get("worldName");

				final double xPos1 = (Double) serializedCuboid.get("x1");
				final double xPos2 = (Double) serializedCuboid.get("x2");
				final double yPos1 = (Double) serializedCuboid.get("y1");
				final double yPos2 = (Double) serializedCuboid.get("y2");
				final double zPos1 = (Double) serializedCuboid.get("z1");
				final double zPos2 = (Double) serializedCuboid.get("z2");

				return new Cuboid(worldName, xPos1, yPos1, zPos1, xPos2, yPos2, zPos2);
			}
			catch (final Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
		}

	}

	public static final class ItemFactory
	{
		private final String itemscript;

		public ItemFactory(final String itemscript)
		{
			this.itemscript = itemscript;
		}

		public ItemStack getItem()
		{
			return this.getItem(null);
		}

		public ItemStack getItem(final HashMap<String, Object> replacements0)
		{
			ItemStack is = null;
			HashMap<String, Object> replacements = new HashMap<String, Object>();
			if (replacements0 != null)
			{
				replacements = replacements0;
			}

			String nome = "?";
			String ident = "";
			final java.util.Vector<String> lore = new java.util.Vector<String>();
			final HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			int material;
			byte data = -1;
			final int flags = Pattern.CASE_INSENSITIVE;

			final Matcher mident = Pattern.compile("((display)\\s*:\\s*\"\\s*([^\"]+)\\s*\")").matcher(ItemFactory.this.itemscript);
			if (mident.find())
			{
				ident = mident.group(3).trim();
				for (final Entry<String, Object> e : replacements.entrySet())
				{
					ident = ident.replace(e.getKey(), String.valueOf(e.getValue()));
				} ;
				ident = ChatColor.translateAlternateColorCodes('&', ident);
			}
			else
			{
				return null;
			}

			final Matcher mii = Pattern.compile("((item|i)\\s*:\\s*\"\\s*([0-9]+)\\s*\")").matcher(ItemFactory.this.itemscript);
			if (mii.find())
			{
				material = Integer.parseInt(mii.group(3).split("\\s*:\\s*")[0]);
				if (mii.group(3).split(":").length >= 2)
				{
					data = Byte.parseByte(mii.group(3).split("\\s*:\\s*")[1]);
				}
			}
			else
			{
				System.out.println("Failed to parse itemstack with Quiz/Factory: Item ID not found.");
				return null;
			}

			final Matcher min = Pattern.compile("((nome|name|nm)\\s*:\\s*\"(.+)\")", flags).matcher(ItemFactory.this.itemscript);
			if (min.find())
			{
				nome = ChatColor.translateAlternateColorCodes('&', min.group(3).trim());
			}

			final Matcher mil = Pattern.compile("((d\\+|desc\\+|description\\+)\\s*:\\s*\"([^\"]+)\")", flags).matcher(ItemFactory.this.itemscript);
			while (mil.find())
			{
				String ll = mil.group(3).trim();
				for (final Entry<String, Object> e : replacements.entrySet())
				{
					ll = ll.replaceAll("(?i)" + Pattern.quote(e.getKey()), String.valueOf(e.getValue()));
				}
				lore.add(ChatColor.translateAlternateColorCodes('&', ll));
			}

			final Matcher milam = Pattern.compile("((d|desc|description)\\s*\\[\\s*([^\\[\\]]+)\\s*\\])", flags).matcher(ItemFactory.this.itemscript);
			while (milam.find())
			{
				final Matcher dv = Pattern.compile("\"([^\"]+)\"").matcher(milam.group(3).trim());
				while (dv.find())
				{
					String ll = dv.group(1).trim();
					for (final Entry<String, Object> e : replacements.entrySet())
					{
						ll = ll.replaceAll("(?i)" + Pattern.quote(e.getKey()), String.valueOf(e.getValue()));
					}
					lore.add(ChatColor.translateAlternateColorCodes('&', ll));
				}
			}

			final Matcher mie = Pattern.compile("((e\\+|ench\\+|enchantment\\+)\\s*:\\s*\"(.+)\")", flags).matcher(ItemFactory.this.itemscript);
			while (mie.find())
			{
				final Matcher mk = Pattern.compile("((e|ench|enchantment)\\s*=\\s*\\'([a-zA-Z_-]+)\\')").matcher(mie.group(3).trim());
				if (mk.find())
				{
					final String k = mk.group(3).trim();
					if (Framework.checkEnchantment(k) != null)
					{
						final Matcher ml = Pattern.compile("((l|lev|level)\\s*=\\s*\\'([0-9]+)\\')").matcher(mie.group(3).trim());
						if (ml.find())
						{
							enchants.put(Framework.checkEnchantment(k), Integer.parseInt(ml.group(3)));
						}
						else
						{
							enchants.put(Framework.checkEnchantment(k), 1);
						}
					}
				}
			}

			is = new ItemStack(Material.getMaterial(material));

			if (data != -1)
			{
				final MaterialData mdata = is.getData();
				mdata.setData(data);
				is.setData(mdata);
			}

			final ItemMeta meta = is.getItemMeta();
			meta.setLore(lore);

			if (!nome.matches("\\?"))
			{
				meta.setDisplayName(nome);
			}

			is.setItemMeta(meta);

			for (final Entry<Enchantment, Integer> enchant : enchants.entrySet())
			{
				is.addUnsafeEnchantment(enchant.getKey(), enchant.getValue());
			}

			return is;
		}
	}

}
