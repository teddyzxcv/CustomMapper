package implementation;

import ru.hse.homework4.Exported;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDeserializer {

    public static <T> T stringToObject(Class<T> clazz, String input) {
        if (input == null) {
            return null;
        }
        List<Field> allFields = List.of(clazz.getDeclaredFields());
        allFields = CustomSerializer.filterFields(allFields);
        Map<String, String> fieldNameToValue = objectFieldToStringList(input, allFields);
        T obj = null;
        try {
            obj = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        fillValueInField(allFields, fieldNameToValue, obj);
        return obj;
    }

    private static <T> void fillValueInField(List<Field> allFields, Map<String, String> nameToValue, T object) {
        for (Field field :
                allFields) {
            field.setAccessible(true);
            Class<?> valueClazz = field.getType();
            try {
                String valueInString = nameToValue.get(field.getName());
                if (valueInString == null) {
                    continue;
                }
                valueInString = valueInString.trim();
                if (valueInString.charAt(0) == '\"' && valueInString.charAt(valueInString.length() - 1) == '\"') {
                    valueInString = valueInString.substring(1, valueInString.length() - 1);
                }
                if (valueClazz.isPrimitive()) {
                    field.set(object, stringToFieldValue(valueInString, valueClazz));
                } else if (valueClazz.isAnnotationPresent(Exported.class)) {
                    field.set(object, stringToObject(valueClazz, valueInString));
                } else if (valueClazz.isEnum()) {
                } else if (valueClazz.getSimpleName().equals("List")) {

                } else if (valueClazz.getSimpleName().equals("Set")) {

                } else {
                    field.set(object, valueClazz.cast(valueClazz.getConstructor(String.class).newInstance(valueInString)));
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }

    private static <T> Object stringToFieldValue(String value, Class<T> clazz) {
        switch (clazz.getSimpleName()) {
            case ("int"):
                return Integer.parseInt(value);
            case ("boolean"):
                return Boolean.parseBoolean(value);
            case ("byte"):
                return Byte.parseByte(value);
            case ("short"):
                return Short.parseShort(value);
            case ("long"):
                return Long.parseLong(value);
            case ("float"):
                return Float.parseFloat(value);
            case ("double"):
                return Double.parseDouble(value);
            default:
                throw new RuntimeException();
        }
    }

    private static Map<String, String> objectFieldToStringList(String input, List<Field> allFields) {
        Map<String, String> fieldToValue = new HashMap<>();
        input = input.trim();
        for (Field field : allFields) {
            String regex = String.format("\\\"%s\\\"[:]+((?=\\[)\\[[^]]*\\]|(?=\\{)\\{[^\\}]*\\}|\\\"[^\\\"]*\\\")", field.getName());
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                fieldToValue.put(field.getName(), matcher.group(1));
            }
        }
        return fieldToValue;
    }
}
