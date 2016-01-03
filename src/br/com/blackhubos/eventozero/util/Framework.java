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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.file.FilenameException;
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

public final class Framework
{

	private static final Pattern commentary = Pattern.compile("(?:^(?:(?:\\s+)?\\#(?:\\s+)?))+(.*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

	@SuppressWarnings("all")
	private static Plugin worldguard = null;

	@SuppressWarnings("all")
	private static Plugin worldedit = null;

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

	public static final class LoggerManager<T extends JavaPlugin>
	{

		protected static Integer task = 0;
		private final T plugin;
		private final java.util.Vector<Log> log = new java.util.Vector<Log>();
		private final File parent;

		@SuppressWarnings("unchecked")
		public LoggerManager(final Plugin plugin, final File parent)
		{
			this.plugin = (T) plugin;
			this.parent = parent;
		}

		public LoggerManager<T> init(final String time)
		{
			LoggerManager.task = Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
			{
				@Override
				public void run()
				{
					LoggerManager.this.write();
				}
			}, Framework.reverseOf(time), Framework.reverseOf(time)).getTaskId();

			return this;
		}

		public void cancel()
		{
			Bukkit.getScheduler().cancelTask(LoggerManager.task);
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
				if (!LoggerManager.this.parent.exists())
				{
					LoggerManager.this.parent.mkdir();
				}
				final HashMap<String, java.util.Vector<Log>> date_log = new HashMap<String, java.util.Vector<Log>>();
				for (final Log l : this.saving_log)
				{
					final String n = LoggerManager.this.getFilename(l.getDate());
					final File f = new File(LoggerManager.this.parent, n);
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
					final File f = new File(LoggerManager.this.parent, n);
					BufferedWriter writer = null;
					try
					{
						writer = new BufferedWriter(new FileWriter(f, true));
						for (final Log l : date_log.get(n))
						{
							writer.write(LoggerManager.this.format(l.getDate(), l.getMessage()));
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
		private String filename;

		public Configuration()
		{
			this.copied = false;
		}

		public Configuration(final File file)
		{
			try
			{
				this.filename = file.getName();
				this.load(file);
			}
			catch (IOException | InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		}

		public Configuration(final Plugin plugin, final File file)
		{
			this.filename = file.getName();
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

		public String getFile()
		{
			return this.filename;
		}

		@Override
		public void save(final File file) throws IOException
		{
			Validate.notNull(file, "File cannot be null");
			this.filename = file.getName();
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

	/**
	 * @info O {@link CuboidClipboard} está sendo substituido aos poucos pelo {@link com.sk89q.worldedit.extent.clipboard.Clipboard}
	 * O que faz com que as Clipboards sejam incompativeis com a CuboidClipboard, e os schematics retornam o {@link CuboidClipboard} que está
	 * deprecated!
	 */
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
			/*
				Salvamento da CuboidClipboard no Clipboard não é possivel e foi removido!
			 */
			CuboidClipboard cuboidClipboard = SchematicFormat.MCEDIT.load(saveFile);
			cuboidClipboard.place(this.editSession, getPastePosition(loc), false);
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
				return this.localSession.getClipboard().getClipboard().getOrigin();
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
