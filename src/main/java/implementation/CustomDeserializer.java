package implementation;

import ru.hse.homework4.DateFormat;
import ru.hse.homework4.Exported;
import ru.hse.homework4.PropertyName;
import ru.hse.homework4.UnknownPropertiesPolicy;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomDeserializer {
    private static boolean retainIdentity;

    /**
     * Retain consumer.
     *
     * @param nameToValue Map field name and value.
     * @param obj         Object to check.
     * @return Object that refreshed
     */
    private static Object retainIdConsumer(Map<String, String> nameToValue, Object obj) {
        if (!retainIdentity) {
            return obj;
        }
        if (!identityStorage.containsKey(nameToValue.get("static"))) {
            identityStorage.put(nameToValue.get("static"), obj);
            return obj;
        } else {
            obj = identityStorage.get(nameToValue.get("static"));
            return obj;
        }
    }

    private static final HashMap<String, Object> identityStorage = new HashMap<>();

    /**
     * String to record.
     *
     * @param clazz            Class of record.
     * @param fieldNameToValue Field name to value.
     * @return Object.
     */
    private static Object stringToRecord(Class<?> clazz, Map<String, String> fieldNameToValue) {
        try {
            Object obj = null;
            ArrayList<Object> values = new ArrayList<>();
            RecordComponent[] rc = clazz.getRecordComponents();
            for (RecordComponent recordcomp : rc) {
                Field field = clazz.getDeclaredField(recordcomp.getAccessor().getName());
                field.trySetAccessible();
                values.add(stringToEverthing(field, fieldNameToValue, field.getType()));
            }
            obj = clazz.getDeclaredConstructors()[0].newInstance(values.toArray());
            return obj;
        } catch (NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * String to object.
     *
     * @param clazz Class of object.
     * @param input Input of string.
     * @param rid   RetainId.
     * @param <T>   Type.
     * @return Value.
     */
    public static <T> T stringToObject(Class<T> clazz, String input, boolean rid) {
        retainIdentity = rid;
        if (input == null) {
            return null;
        }
        Object obj = null;
        Map<String, String> fieldNameToValue = objectFieldToStringList(input);
        if (clazz.isEnum()) {
            List<T> enums = Arrays.stream(clazz.getEnumConstants()).toList();
            List<String> enumsInString = enums.stream().map(Object::toString).toList();
            if (enumsInString.contains(input.substring(1, input.length() - 1))) {
                int index = enumsInString.indexOf(input.substring(1, input.length() - 1));
                obj = enums.get(index);
            }
            obj = retainIdConsumer(fieldNameToValue, obj);
            return (T) obj;
        } else if (clazz.isRecord()) {
            obj = stringToRecord(clazz, fieldNameToValue);
            obj = retainIdConsumer(fieldNameToValue, obj);
            return (T) obj;
        }
        List<Field> allFields = List.of(clazz.getDeclaredFields());
        allFields = CustomSerializer.filterFields(allFields);
        UnknownPropertiesPolicy unknownPropertiesPolicy = clazz.getAnnotation(Exported.class).unknownPropertiesPolicy();
        for (String key : fieldNameToValue.keySet()) {
            Set<String> fieldNames = new HashSet<>();
            for (Field field : allFields) {
                if (field.isAnnotationPresent(PropertyName.class)) {
                    fieldNames.add(field.getAnnotation(PropertyName.class).value());
                } else {
                    fieldNames.add(field.getName());
                }
            }
            if (!fieldNames.contains(key) && !key.equals("static") && unknownPropertiesPolicy == UnknownPropertiesPolicy.FAIL) {
                throw new RuntimeException();
            }
        }
        try {
            obj = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        fillValueInField(allFields, fieldNameToValue, obj);
        obj = retainIdConsumer(fieldNameToValue, obj);
        return (T) obj;
    }

    /**
     * Fill value in one field.
     *
     * @param allFields   All field list.
     * @param nameToValue Name to value.
     * @param object      Object to fill.
     * @param <T>         Type
     */
    private static <T> void fillValueInField(List<Field> allFields, Map<String, String> nameToValue, T object) {
        for (Field field :
                allFields) {
            field.trySetAccessible();
            Class<?> valueClazz = field.getType();
            try {
                field.set(object, stringToEverthing(field, nameToValue, valueClazz));
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * String to everything.
     *
     * @param field       Field that need to find.
     * @param nameToValue Name / Value map.
     * @param valueClazz  Class of value.
     * @param <T>         Type
     * @return Casted object.
     */
    private static <T> Object stringToEverthing(Field field, Map<String, String> nameToValue, Class<T> valueClazz) {
        Object obj = null;
        try {
            String fieldName = field.getName();
            if (field.isAnnotationPresent(PropertyName.class)) {
                fieldName = field.getAnnotation(PropertyName.class).value();
            }
            String valueInString = nameToValue.get(fieldName);
            if (valueInString == null) {
                return null;
            }
            valueInString = valueInString.trim();
            if (valueInString.charAt(0) == '\"' && valueInString.charAt(valueInString.length() - 1) == '\"') {
                valueInString = valueInString.substring(1, valueInString.length() - 1);
            }
            if (valueClazz.isAnnotationPresent(Exported.class)) {
                obj = stringToObject(valueClazz, valueInString, retainIdentity);
            } else if (valueClazz == Set.class) {
                ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                obj = stringToSet(valueInString, ((Class<?>) ptype.getActualTypeArguments()[0]));
            } else if (valueClazz == List.class) {
                ParameterizedType ptype = (ParameterizedType) field.getGenericType();
                obj = stringToList(valueInString, (Class<?>) ptype.getActualTypeArguments()[0]);
            } else if (field.isAnnotationPresent(DateFormat.class)) {
                obj = stringToDateTime(valueClazz.getSimpleName(), field.getAnnotation(DateFormat.class).value(), valueInString);
            } else if (valueClazz.isPrimitive()) {
                obj = stringToPrimitive(valueInString, valueClazz);
            } else if (valueClazz == String.class) {
                obj = valueClazz.cast(valueClazz.getConstructor(String.class).newInstance(valueInString));
            }
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Deserialize DateTime format.
     *
     * @param className     Class name.
     * @param pattern       Pattern to find.
     * @param valueInString Value in string.
     * @return Object DateTime.
     */
    private static Object stringToDateTime(String className, String pattern, String valueInString) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        Object obj;
        switch (className) {
            case "LocalDate" -> obj = LocalDate.parse(valueInString, f);
            case "LocalTime" -> obj = LocalTime.parse(valueInString, f);
            case "LocalDateTime" -> obj = LocalDateTime.parse(valueInString, f);
            default -> obj = null;
        }
        return obj;
    }

    /**
     * String to list.
     *
     * @param valueInString String of value.
     * @param valueClazz    Value  Type
     * @return List of type.
     * @throws NoSuchMethodException     getConstructor
     * @throws InvocationTargetException newInstance
     * @throws InstantiationException    new Instance
     * @throws IllegalAccessException    getConstructor.
     */
    private static <T> List<T> stringToList(String valueInString, Class<T> valueClazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        for (String input : stringArrayToCollection(valueInString)) {
            if (input == null) {
                continue;
            }
            input = input.trim();
            if (input.charAt(0) == '\"' && input.charAt(input.length() - 1) == '\"') {
                input = input.substring(1, input.length() - 1);
            }
            if (valueClazz.isAnnotationPresent(Exported.class)) {
                list.add(stringToObject(valueClazz, input, retainIdentity));
            } else if (valueClazz.isAnnotationPresent(DateFormat.class)) {
                Object obj = null;
                list.add((T) stringToDateTime(valueClazz.getSimpleName(), valueClazz.getAnnotation(DateFormat.class).value(), valueInString));
            } else if (valueClazz.isPrimitive()) {
                list.add((T) stringToPrimitive(input, valueClazz));
            } else if (valueClazz == String.class) {
                list.add(valueClazz.cast(valueClazz.getConstructor(String.class).newInstance(input)));
            }
        }
        return list;
    }

    /**
     * String to set.
     *
     * @param valueInString String of value.
     * @param valueClazz    Value  Type
     * @return List of type.
     * @throws NoSuchMethodException     getConstructor
     * @throws InvocationTargetException newInstance
     * @throws InstantiationException    new Instance
     * @throws IllegalAccessException    getConstructor.
     */
    private static <T> Set<T> stringToSet(String valueInString, Class<T> valueClazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<T> set = new HashSet<>();
        for (String input : stringArrayToCollection(valueInString)) {
            if (input == null) {
                continue;
            }
            input = input.trim();
            if (input.charAt(0) == '\"' && input.charAt(input.length() - 1) == '\"') {
                input = input.substring(1, input.length() - 1);
            }
            if (valueClazz.isAnnotationPresent(Exported.class)) {
                set.add(stringToObject(valueClazz, input, retainIdentity));
            } else if (valueClazz.getSimpleName().equals("LocalDate")) {

            } else if (valueClazz.getSimpleName().equals("LocalTime")) {

            } else if (valueClazz.getSimpleName().equals("LocalDateTime")) {

            } else if (valueClazz.isPrimitive()) {
                set.add((T) stringToPrimitive(input, valueClazz));
            } else if (valueClazz == String.class) {
                set.add(valueClazz.cast(valueClazz.getConstructor(String.class).newInstance(input)));
            }
        }
        return set;
    }

    /**
     * Work with primitive and their wrapper class.
     *
     * @param value Value of primitive.
     * @param clazz Class of primitive
     * @param <T>   Type
     * @return Object.
     */
    private static <T> Object stringToPrimitive(String value, Class<T> clazz) {
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

    /**
     * String array to collection. collect from input array.
     * Split by first level comma.
     *
     * @param input Input string.
     * @return List of string.
     */
    public static List<String> stringArrayToCollection(String input) {
        List<String> output = new ArrayList<>();
        input = input.trim();
        int bracketCount = 0;
        int sbracketCount = 0;
        if (input.charAt(0) != '[' || input.charAt(input.length() - 1) != ']') {
            throw new RuntimeException();
        }
        StringBuilder currentElement = new StringBuilder();
        for (int i = 1; i < input.length() - 1; i++) {
            char cr = input.charAt(i);
            switch (cr) {
                case ('['):
                    if (bracketCount == 0) {
                        sbracketCount++;
                    }
                    currentElement.append(cr);
                    break;
                case (']'):
                    if (bracketCount == 0) {
                        sbracketCount--;
                    }
                    currentElement.append(cr);
                    break;
                case ('{'):
                    if (sbracketCount == 0) {
                        bracketCount++;
                    }
                    currentElement.append(cr);
                    break;
                case ('}'):
                    if (sbracketCount == 0) {
                        bracketCount--;
                    }
                    currentElement.append(cr);
                    break;
                case (','):
                    if (sbracketCount == 0 && bracketCount == 0) {
                        output.add(currentElement.toString());
                        currentElement = new StringBuilder();
                        break;
                    }
                    currentElement.append(cr);
                    break;
                default:
                    currentElement.append(cr);
                    break;
            }
        }
        output.add(currentElement.toString());
        return output;
    }

    /**
     * Create map that map field name and field value and splited by first level comma.
     * Can ignore brackets, qoute, etc.
     *
     * @param input Input string
     * @return Map with field name and field value.
     */
    public static Map<String, String> objectFieldToStringList(String input) {
        Map<String, String> fieldToValue = new HashMap<>();
        input = input.trim();
        int bracketCount = 0;
        int sbracketCount = 0;
        boolean qouteOpen = false;
        if (input.charAt(0) != '{' || input.charAt(input.length() - 1) != '}') {
            throw new RuntimeException();
        }
        boolean isName = true;
        StringBuilder currentWord = new StringBuilder();
        String currentName = "";
        String currentValue = "";
        for (int i = 1; i < input.length() - 1; i++) {
            char cr = input.charAt(i);
            switch (cr) {
                case (':'):
                    if (bracketCount != 0 || sbracketCount != 0 || qouteOpen) {
                        currentWord.append(cr);
                        break;
                    }
                    if (!qouteOpen) {
                        isName = false;
                    }
                    break;
                case ('{'):
                    if (!qouteOpen && sbracketCount == 0) {
                        bracketCount++;
                    }
                    currentWord.append(cr);

                    break;
                case ('}'):
                    if (!qouteOpen && sbracketCount == 0) {
                        bracketCount--;
                    }
                    currentWord.append(cr);
                    if (bracketCount == 0) {
                        currentValue = currentWord.toString();
                    }
                    break;
                case ('"'):
                    if (bracketCount != 0 || sbracketCount != 0) {
                        currentWord.append(cr);
                        break;
                    }
                    if (input.charAt(i - 1) != '\\') {
                        qouteOpen = !qouteOpen;
                        if (!qouteOpen) {
                            if (isName) {
                                currentName = currentWord.toString();
                            } else {
                                currentValue = currentWord.toString();
                            }
                            currentWord = new StringBuilder();
                        }
                    } else {
                        currentWord.append(cr);
                    }
                    break;
                case ('['):
                    if (!qouteOpen && bracketCount == 0) {
                        sbracketCount++;
                    }
                    currentWord.append(cr);
                    break;
                case (']'):
                    if (!qouteOpen && bracketCount == 0) {
                        sbracketCount--;
                    }
                    currentWord.append(cr);
                    if (sbracketCount == 0 && bracketCount == 0) {
                        currentValue = currentWord.toString();
                    }
                    break;
                case ('\\'):
                    if (qouteOpen && input.charAt(i - 1) != '\\') {
                        currentWord.append(cr);
                    }
                    break;
                case (','):
                    if (qouteOpen || bracketCount > 0 || sbracketCount > 0) {
                        currentWord.append(cr);
                    }
                    if (!qouteOpen && bracketCount == 0 && sbracketCount == 0) {
                        fieldToValue.put(currentName, currentValue);
                        currentWord = new StringBuilder();
                        isName = true;
                        qouteOpen = false;
                    }
                    break;
                default:
                    currentWord.append(cr);
                    break;
            }
        }
        fieldToValue.put(currentName, currentValue);
        return fieldToValue;
    }

}
