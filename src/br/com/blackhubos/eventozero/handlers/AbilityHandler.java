/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.blackhubos.eventozero.handlers;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import br.com.blackhubos.eventozero.ability.Ability;

/**
 *
 * @author Hugo
 */
public class AbilityHandler {

	public static final List<Ability> abilitys = new Vector<>();

	public static Optional<Ability> getAbilityByName(String name) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(name), 
				"name cannot be null or empty");
		
		return abilitys.parallelStream()
				.filter(a -> a.getName().equals(name))
				.findAny();
	}

	public static boolean hasAbilityByName(String name) {
		return getAbilityByName(name).isPresent();
	}

	public static void loadAbility(Ability ability) {
		abilitys.add(ability);
	}

}
