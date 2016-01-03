package br.com.blackhubos.eventozero.updater.formater;

/**
 * Created by jonathan on 03/01/16.
 */

import com.google.common.base.Optional;

import java.util.HashSet;
import java.util.Set;

import br.com.blackhubos.eventozero.updater.formater.exception.CannotFormatTypeException;

/**
 * Formatador anonimo (ou multiplo dependendo da forma de seu uso)
 */
public class MultiTypeFormatter {

    private Set<TypeFormatter<?>> formatterSet = new HashSet<>();

    public void registerFormatter (TypeFormatter<?> formatter) {
        formatterSet.add(formatter);
    }

    public void unregisterFormatter (TypeFormatter<?> formatter) {
        formatterSet.remove(formatter);
    }

    public void unregisterFormatter (Class<? extends TypeFormatter> formatterClass) {
        for(TypeFormatter<?> typeFormatter : formatterSet){
            if(typeFormatter.getClass() == formatterClass){
                formatterSet.remove(typeFormatter);
            }
        }
    }

    public void unregisterFormatterType (Class<?> formatClass) {
        for(TypeFormatter<?> typeFormatter : formatterSet){
            if(typeFormatter.getFormatClass() == formatClass){
                formatterSet.remove(typeFormatter);
            }
        }
    }

    public boolean canFormat (Class<?> classToFormat) {
        for(TypeFormatter<?> typeFormatter : formatterSet) {
            if(typeFormatter.getFormatClass() == classToFormat) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tenta formatar baseado na classe do tipo (não precisa ser necessariamente a classe exata)
     * Para algo mais preciso use {@link #formatSpecific(Object, Class)}
     * @see #formatSpecific(Object, Class)
     * @param value Valor a formatar
     * @param target Tipo para retorno
     * @param <T> Tipo
     * @return Valor formatado
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> format (Object value, Class<? extends T> target) {
        for(TypeFormatter<?> typeFormatter : formatterSet) {
            if(typeFormatter.getFormatClass().isAssignableFrom(target)) {
                return (Optional<T>) typeFormatter.formatType(value);
            }
        }
        return Optional.absent();
    }

    /**
     * Tenta formatar com o formatador especifico para o valor
     * Para um formatador menos exato use {@link #format(Object, Class)}
     * @see #format(Object, Class)
     * @param value Valor a formatar
     * @param target Tipo para retorno
     * @param <T> Tipo
     * @return Valor formatado
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> formatSpecific (Object value, Class<? extends T> target) {
        for(TypeFormatter<?> typeFormatter : formatterSet) {
            if(typeFormatter.getFormatClass() == target) {
                return (Optional<T>) typeFormatter.formatType(value);
            }
        }
        return Optional.absent();
    }

    /**
     * Tenta formatar com todos formatadores
     * @see #format(Object, Class)
     * @see #formatSpecific(Object, Class)
     * @param value Valor a formatar
     * @param <T> Tipo
     * @return Valor formatado
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> tryFormatAll (Object value) {
        for(TypeFormatter<?> typeFormatter : formatterSet) {
            try{
                return (Optional<T>) typeFormatter.tryFormatType(value);
            }catch(CannotFormatTypeException ignored){}
        }
        return Optional.absent();
    }
}
