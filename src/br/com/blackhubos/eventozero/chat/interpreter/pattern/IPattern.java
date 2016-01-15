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
package br.com.blackhubos.eventozero.chat.interpreter.pattern;

import com.google.common.base.Objects;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import br.com.blackhubos.eventozero.chat.interpreter.base.Question;
import br.com.blackhubos.eventozero.chat.interpreter.base.booleanresult.BooleanResult;
import br.com.blackhubos.eventozero.chat.interpreter.values.ValueTransformer;

/**
 * Classe de avaliação de valores
 *
 * @param <T> Tipo do valor
 */
public class IPattern<T> {

    private final Predicate<String> check;
    private final ValueTransformer<T> transformer;
    private final Optional<Function<T, BooleanResult.Result>> booleanResult;

    /**
     * Cria um novo IPattern
     *
     * @param pattern       Pattern regex para avaliar a resposta
     * @param transformer   Tradutor de valores
     * @param booleanResult Avaliador responsável por informar qual será as definições que serão
     *                      consideradas pelo {@link Question}, é subscrito pelo {@link
     *                      Question#booleanResult(Function)} caso informado
     */
    public IPattern(Pattern pattern, ValueTransformer<T> transformer, Function<T, BooleanResult.Result> booleanResult) {
        this(value -> pattern.matcher(value).matches(), transformer, Optional.of(booleanResult));
    }

    /**
     * Cria um novo IPattern
     *
     * Sempre será considerado as definições yes {@link BooleanResult#yes()}
     *
     * @param pattern     Pattern regex para avaliar a resposta
     * @param transformer Tradutor de valores
     */
    public IPattern(Pattern pattern, ValueTransformer<T> transformer) {
        this(value -> pattern.matcher(value).matches(), transformer, Optional.empty());
    }

    /**
     * Cria um novo IPattern
     *
     * Sempre será considerado as definições yes {@link BooleanResult#yes()}
     *
     * @param check       Predicato que irá avaliar as respostas
     * @param transformer Tradutor de valores
     */
    public IPattern(Predicate<String> check, ValueTransformer<T> transformer) {
        this(check, transformer, Optional.empty());
    }

    /**
     * Cria um novo IPattern
     *
     * @param check         Predicato que irá avaliar as respostas
     * @param transformer   Tradutor de valores
     * @param booleanResult Avaliador responsável por informar qual será as definições que serão
     *                      consideradas pelo {@link Question}, é subscrito pelo {@link
     *                      Question#booleanResult(Function)} caso informado
     */
    public IPattern(Predicate<String> check, ValueTransformer<T> transformer, Function<T, BooleanResult.Result> booleanResult) {
        this(check, transformer, Optional.ofNullable(booleanResult));
    }

    /**
     * Cria um novo IPattern
     *
     * @param check         Predicato que irá avaliar as respostas
     * @param transformer   Tradutor de valores
     * @param booleanResult Avaliador responsável por informar qual será as definições que serão
     *                      consideradas pelo {@link Question}, é subscrito pelo {@link
     *                      Question#booleanResult(Function)} caso informado
     */
    public IPattern(Predicate<String> check, ValueTransformer<T> transformer, Optional<Function<T, BooleanResult.Result>> booleanResult) {
        this.check = check;
        this.transformer = transformer;
        this.booleanResult = booleanResult;
    }

    /**
     * Obtém quais definições serão consideradas pelo {@link Question}
     *
     * @param value Valor
     * @return Definições a serem consideradas pelo {@link Question}
     */
    public BooleanResult.Result booleanResult(T value) {
        if (this.booleanResult.isPresent()) {
            return booleanResult.get().apply(value);
        }
        return BooleanResult.Result.YES;
    }

    /**
     * Verifica se o predicado/pattern aceita o texto informado
     *
     * @param match Texto
     * @return True caso aceite, false caso contrário
     */
    public boolean check(String match) {
        return check.test(match);
    }

    /**
     * Obtém o tradutor de valores
     *
     * @return Tradutor de valores
     */
    public ValueTransformer<T> getTransformer() {
        return transformer;
    }

    public String toText(Object value) {
        return Objects.toStringHelper(this)
                .add("valueTransformer", Objects.toStringHelper(this.transformer).add("type", this.transformer.toType(value)))
                .toString();
    }
}
