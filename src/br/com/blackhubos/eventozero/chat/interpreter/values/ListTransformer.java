package br.com.blackhubos.eventozero.chat.interpreter.values;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 12/01/16.
 */
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
