/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.blackhubos.eventozero.handlers;

import br.com.blackhubos.eventozero.ability.Ability;
import java.util.Vector;

import com.google.common.base.Optional;

/**
 *
 * @author Hugo
 */
public class AbilityHandler {

    public static final Vector<Ability> abilitys = new Vector<>();

    public static Optional<Ability> getAbilityByName(String name){
        for(Ability ability : abilitys){
            if(ability.getName().equals(name)){
                return Optional.of(ability);
            }
        }
        return Optional.absent();
    }
    
    public static boolean hasAbilityByName(String name){
        return getAbilityByName(name).isPresent();
    }
    
    public static void loadAbility(Ability ability) {
        abilitys.add(ability);
    }

}
