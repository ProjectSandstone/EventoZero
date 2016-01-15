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
package br.com.blackhubos.eventozero.chat.interpreter.state.input.impl;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.InputState;
import br.com.blackhubos.eventozero.chat.interpreter.state.input.InputStateBase;

/**
 * Implementação do InputState (Gerenciador de entrada)
 *
 * @param <T>  Tipo dos valores
 * @param <ID> ID da questão para o método de retorno da questão relacionada
 */
public class InputStateImpl<T, ID> implements InputStateBase<T, ID> {

    private final Question<T, ID> question;
    private Optional<BiConsumer<Player, String>> okConsumer = Optional.empty();
    private Optional<BiConsumer<Player, String>> errorConsumer = Optional.empty();

    /**
     * Construtor do gerenciador de entrada
     *
     * @param question Questão relacionada
     */
    public InputStateImpl(Question<T, ID> question) {
        this.question = question;
    }

    @Override
    public Optional<BiConsumer<Player, String>> getOk() {
        return this.okConsumer;
    }

    @Override
    public Optional<BiConsumer<Player, String>> getError() {
        return this.errorConsumer;
    }

    @Override
    public InputState<T, ID> ok(BiConsumer<Player, String> okConsumer) {
        this.okConsumer = Optional.of(okConsumer);
        return this;
    }

    @Override
    public InputState<T, ID> error(BiConsumer<Player, String> errorConsumer) {
        this.errorConsumer = Optional.of(errorConsumer);
        return this;
    }

    @Override
    public Question<T, ID> endDefinition() {
        return this.question;
    }
}
