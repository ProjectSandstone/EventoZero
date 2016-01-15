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
package br.com.blackhubos.eventozero.chat.interpreter.values;

import java.util.ArrayList;
import java.util.List;

public class ListTransformer {

    public static final ValueTransformer<List<String>> STRING_LIST_TRANSFORMER = new StringList();

    private static class StringList implements ValueTransformer<List<String>> {

        @Override
        public List<String> transform(String input) {
            List<String> contents = new ArrayList<>();

            StringBuilder sb = new StringBuilder();

            for (char c : input.toCharArray()) {
                int pos = sb.length()-1;

                if(c == ' ' && sb.length() == 0){
                    continue;
                }

                if (c == ','
                        && pos > -1) {
                    if(sb.charAt(pos) != '\\'){
                        contents.add(sb.toString());
                        sb = new StringBuilder();
                    }else{
                        sb.deleteCharAt(pos);
                    }
                }else{
                    sb.append(c);
                }
            }

            if(sb.length() > 0){
                contents.add(sb.toString());
            }

            return contents;
        }
    }
}
