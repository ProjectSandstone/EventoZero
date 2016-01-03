package br.com.blackhubos.eventozero.updater.formater;

import com.google.common.base.Optional;

import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Created by jonathan on 03/01/16.
 */

/**
 * Formata Booleanos
 */
public class BooleanFormatter implements TypeFormatter<Boolean> {

    @Override
    public Boolean unsecuredFormatType(Object objectToFormat) {
        if(objectToFormat instanceof Boolean){
            return (Boolean) objectToFormat;
        }else{
            if(objectToFormat.equals("true")){
                return true;
            }else if(objectToFormat.equals("false")){
                return false;
            }
        }
        return null;
    }

    @Override
    public Boolean tryFormatType(Object objectToFormat) throws CannotFormatTypeException {
        Boolean result = unsecuredFormatType(objectToFormat);
        if(result == null){
            throw new CannotFormatTypeException(String.format("Cannot format type: 'Class[%s]'", objectToFormat.getClass().getCanonicalName()));
        }
        return result;
    }

    @Override
    public Optional<Boolean> formatType(Object objectToFormat) {
        return Optional.fromNullable(unsecuredFormatType(objectToFormat));
    }

    @Override
    public Class<? extends Boolean> getFormatClass() {
        return Boolean.class;
    }
}
