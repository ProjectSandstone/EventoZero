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

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import br.com.blackhubos.eventozero.chat.interpreter.values.ValueTransformer;

public class IPattern<T> {

    private final Predicate<String> check;
    private final ValueTransformer<T> transformer;
    private final Optional<Predicate<T>> yesOrNo;

    public IPattern(Pattern pattern, ValueTransformer<T> transformer, Predicate<T> yesOrNo) {
        this(value -> pattern.matcher(value).matches(), transformer, Optional.of(yesOrNo));
    }

    public IPattern(Pattern pattern, ValueTransformer<T> transformer) {
        this(value -> pattern.matcher(value).matches(), transformer, Optional.empty());
    }

    public IPattern(Predicate<String> check, ValueTransformer<T> transformer) {
        this(check, transformer, Optional.empty());
    }

    public IPattern(Predicate<String> check, ValueTransformer<T> transformer, Predicate<T> yesOrNo) {
        this(check, transformer, Optional.ofNullable(yesOrNo));
    }

    public IPattern(Predicate<String> check, ValueTransformer<T> transformer, Optional<Predicate<T>> yesOrNo) {
        this.check = check;
        this.transformer = transformer;
        this.yesOrNo = yesOrNo;
    }

    public boolean yesOrNo(T value) {
        if (this.yesOrNo.isPresent()) {
            return yesOrNo.get().test(value);
        }

        return true;
    }

    public boolean check(String match) {
        return check.test(match);
    }

    public ValueTransformer<T> getTransformer() {
        return transformer;
    }

}
