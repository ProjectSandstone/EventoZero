package br.com.blackhubos.eventozero.updater.assets.uploader;

import com.google.common.base.Optional;

import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.formater.MultiTypeFormatter;
import br.com.blackhubos.eventozero.updater.parser.Parser;

/**
 * Created by jonathan on 03/01/16.
 */
public class Uploader implements Parser<JSONObject, Uploader> {

    enum AssetUploaderInput {
        UNKNOWN, LOGIN, ID, ADMIN;

        public static AssetUploaderInput parseObject (Object objectToParse) {
            if(objectToParse instanceof String){
                String stringToParse = (String) objectToParse;
                try{
                    return AssetUploaderInput.valueOf(stringToParse.toUpperCase());
                }catch(IllegalArgumentException ignored){}
            }

            return AssetUploaderInput.UNKNOWN;
        }

    }

    private final String name;
    private final boolean admin;
    private final long id;

    public Uploader() {
        this(null, false, Long.MIN_VALUE);
    }

    private Uploader(String name, boolean admin, long id) {
        this.name = name;
        this.admin = admin;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public static Optional<Uploader> parseJsonObject(JSONObject parse, MultiTypeFormatter formatter){
        long id = Long.MIN_VALUE;
        String name = null;
        boolean admin = false;

        for(Map.Entry entries : (Set<Map.Entry>) parse.entrySet()) {

            Object key = entries.getKey();
            Object value = entries.getValue();
            String valueString = String.valueOf(value);
            switch (AssetUploaderInput.parseObject(key)) {
                case ADMIN: {
                    if(formatter.canFormat(Boolean.class)){
                        Optional<Boolean> result = formatter.format(value, Boolean.class);
                        if(result.isPresent())
                            admin = result.get();
                    }
                    break;
                }
                case ID: {
                    id = Long.parseLong(valueString);
                    break;
                }
                case LOGIN: {
                    name = valueString;
                    break;
                }

                default: {
                    break;
                }
            }
        }
        if(id == Long.MIN_VALUE || name == null) {
            return Optional.absent();
        }
        return Optional.of(new Uploader(name, admin, id));
    }

    @Override
    public Optional<Uploader> parseObject(JSONObject object, MultiTypeFormatter formatter) {
        return parseJsonObject(object, formatter);
    }


}