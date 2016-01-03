package br.com.blackhubos.eventozero.updater.parser;

import com.google.common.base.Optional;

import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;

/**
 * Created by jonathan on 03/01/16.
 */

/**
 *
 * @param <T> Tipo para fazer "parse"
 * @param <E> Tipo de retorno
 */
public interface Parser<T, E> {

    Optional<E> parseObject(T object, MultiTypeFormatter typeFormatter);

}
