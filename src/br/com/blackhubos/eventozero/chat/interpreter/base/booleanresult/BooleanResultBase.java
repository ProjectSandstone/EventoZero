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
package br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import br.com.blackhubos.eventozero.chat.interpreter.base.QuestionBase;

/**
 * Classe #Hidden de extensão a BooleanResult
 *
 * Veja sobre as classes Hidden: {@link br.com.blackhubos.eventozero.chat.interpreter.base.Interpreter}
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public interface BooleanResultBase<T, ID> extends BooleanResult<T, ID> {

    /**
     * Aplica os valores a função
     *
     * @param value Valor
     * @return Resultado
     */
    @Override
    default Result apply(T value) {
        if (getFunction().isPresent()) {
            return getFunction().get().apply(value);
        }
        return Result.YES;
    }

    /**
     * Obtem a proxima questão das afirmações YES
     *
     * @return Obtem a proxima questão das afirmações YES
     */
    Optional<QuestionBase> getYesQuestion();

    /**
     * Obtem a proxima questão das afirmações NO
     *
     * @return Obtem a proxima questão das afirmações NO
     */
    Optional<QuestionBase> getNoQuestion();

    /**
     * Otem o consumidor para as ações YES
     *
     * @return Otem o consumidor para as ações YES
     */
    Optional<BiConsumer<Player, T>> getYesConsumer();

    /**
     * Obtem o consumidor para as ações NO
     *
     * @return Obtem o consumidor para as ações NO
     */
    Optional<BiConsumer<Player, T>> getNoConsumer();

    /**
     * Obtém a função
     *
     * @return Obtém a função
     */
    Optional<Function<T, Result>> getFunction();

}
