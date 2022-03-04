package implementation;

import ru.hse.homework4.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CustomSerializer {

    private static boolean retainIdentity;

    private static HashMap<Integer, Set<Integer>> identityObjectStorage = new HashMap<>();

    private static Set<Integer> identityStorage = new HashSet<>();

    /**
     * Retain Id consumer, detect is hashcode present in the json.
     *
     * @param obj Object to hash.
     * @param <T> Type of object.
     * @return
     */
    public static <T> String retainIdConsumer(T obj) {
        String hashcode = null;
        if (retainIdentity) {
            hashcode = "\"static\"" + ":\"" + ((obj == null) ? null : obj.hashCode()) + "\"";
        }
        return hashcode;
    }

    /**
     * Get all field of object and serialize them to string.
     *
     * @param object object to convert.
     * @return Serialized json.
     * @throws IllegalAccessException
     */
    public static String getObjectFieldsToString(Object object) throws IllegalAccessException {
        identityStorage.add(object.hashCode());
        Class<?> clazz = object.getClass();
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fields = filterFields(fields);
        List<String> fields_in_list = new ArrayList<>();
        String retainString = "";
        NullHandling nullHandling = object.getClass().getAnnotation(Exported.class).nullHandling();
        for (Field field : fields) {
            if (!field.canAccess(object)) {
                field.trySetAccessible();
            }
            Object fieldObject = field.get(object);
            detectCycle(fieldObject, object);
            // Null
            if (fieldObject == null) {
                if (nullHandling == NullHandling.INCLUDE) {
                    fields_in_list.add("\"" + field.getName() + "\":\"null\"");
                }
                continue;
            }
            addFieldObject(fieldObject, fields_in_list, field);
        }
        // Retain Id consume.
        retainString = retainIdConsumer(object);
        if (retainString != null) {
            fields_in_list.add(retainString);
        }
        return String.join(",", fields_in_list);
    }

    /**
     * Filter all field by given statements.
     *
     * @param fieldList list of field to filter.
     * @return filtered list.
     */
    public static List<Field> filterFields(List<Field> fieldList) {
        return fieldList.stream().filter(field -> !field.isAnnotationPresent(Ignored.class) && !field.isSynthetic()).collect(Collectors.toList());
    }

    /**
     * Set to string.
     *
     * @param set Set to convert.
     * @param <T> Type of set.
     * @return Serialized json.
     */
    private static <T> String setToString(Set<T> set) {
        List<String> fields_in_list = new ArrayList<>();
        for (T fieldObject : set) {
            addElementsToCountable(fieldObject, fields_in_list);
        }
        return String.join(",", fields_in_list);
    }

    /**
     * List to string
     *
     * @param list L ist to convert
     * @param <T>  Type of list
     * @return Serialized json.
     */
    private static <T> String listToString(List<T> list) {
        List<String> fields_in_list = new ArrayList<>();
        for (T fieldObject : list) {
            addElementsToCountable(fieldObject, fields_in_list);
        }
        return String.join(",", fields_in_list);
    }

    /**
     * Add elements to all field that coutable.
     *
     * @param fieldObject    Field (Collection)
     * @param fields_in_list List of fields in string to build json.
     * @param <T>            Generic type of list.
     */
    private static <T> void addElementsToCountable(Object fieldObject, List<String> fields_in_list) {
        if (fieldObject.getClass().isAnnotationPresent(Exported.class)) {
            fields_in_list.add(objectToString(fieldObject, retainIdentity));
        } else {
            Class<? extends Object> fieldClazz = fieldObject.getClass();
            if (Arrays.stream(fieldClazz.getInterfaces()).toList().contains(Set.class)) {
                fields_in_list.add("[" + setToString((Set<T>) fieldObject) + "]");
            } else if (Arrays.stream(fieldClazz.getInterfaces()).toList().contains(List.class)) {
                fields_in_list.add("[" + listToString((List<T>) fieldObject) + "]");
            } else if (fieldClazz.isAnnotationPresent(DateFormat.class)) {
                dateTimeToStringCountable(fieldObject, fields_in_list);
            } else if (fieldClazz.isPrimitive()) {
                fields_in_list.add("\"" + fieldObject + "\"");
            } else {
                fields_in_list.add("\"" + fieldObject + "\"");
            }
        }
    }

    /**
     * Method to comsume datetime array.
     *
     * @param fieldObject    Object to consume.
     * @param fields_in_list List of field in string to convert.
     */
    private static void dateTimeToStringCountable(Object fieldObject, List<String> fields_in_list) {
        Class<? extends Object> fieldClazz = fieldObject.getClass();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(fieldClazz.getAnnotation(DateFormat.class).value());
        switch (fieldClazz.getSimpleName()) {
            case "LocalDateTime" -> fields_in_list.add(((LocalDateTime) fieldObject).format(dateTimeFormatter));
            case "LocalDate" -> fields_in_list.add(((LocalDate) fieldObject).format(dateTimeFormatter));
            case "LocalTime" -> fields_in_list.add(((LocalTime) fieldObject).format(dateTimeFormatter));
        }
    }

    /**
     * Add one object in string to field string list
     *
     * @param fieldObject    Object to get fields
     * @param fields_in_list All their fields.
     * @param field          Object field.
     * @param <T>            Type of object.
     */
    private static <T> void addFieldObject(Object fieldObject, List<String> fields_in_list, Field field) {
        String fieldName;
        if (field.isAnnotationPresent(PropertyName.class)) {
            fieldName = field.getAnnotation(PropertyName.class).value();
        } else {
            fieldName = field.getName();
        }
        if (fieldObject.getClass().isAnnotationPresent(Exported.class)) {
            fields_in_list.add("\"" + fieldName + "\":" + objectToString(fieldObject, retainIdentity));
        } else {
            Class<? extends Object> fieldClazz = fieldObject.getClass();
            if (Arrays.stream(fieldClazz.getInterfaces()).toList().contains(Set.class)) {
                fields_in_list.add("\"" + fieldName + "\":[" + setToString((Set<T>) fieldObject) + "]");
            } else if (Arrays.stream(fieldClazz.getInterfaces()).toList().contains(List.class)) {
                fields_in_list.add("\"" + fieldName + "\":[" + listToString((List<T>) fieldObject) + "]");
            } else if (field.isAnnotationPresent(DateFormat.class)) {
                dateTimeToString(fieldObject, fields_in_list, fieldName, field);
            } else if (fieldClazz.isPrimitive()) {
                fields_in_list.add("\"" + fieldName + "\":\"" + fieldObject + "\"");
            } else {
                fields_in_list.add("\"" + fieldName + "\":\"" + fieldObject + "\"");
            }
        }
    }

    /**
     * Datetime to string.
     *
     * @param fieldObject    Field object.
     * @param fields_in_list List of field in string.
     * @param fieldName      Field name (add to maintain @PropertyName)
     * @param field          Field to consume.
     */
    private static void dateTimeToString(Object fieldObject, List<String> fields_in_list, String fieldName, Field field) {
        Class<? extends Object> fieldClazz = fieldObject.getClass();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(field.getAnnotation(DateFormat.class).value());
        switch (fieldClazz.getSimpleName()) {
            case "LocalDateTime" -> fields_in_list.add("\"" + fieldName + "\":\"" + ((LocalDateTime) fieldObject).format(dateTimeFormatter) + "\"");
            case "LocalDate" -> fields_in_list.add("\"" + fieldName + "\":\"" + ((LocalDate) fieldObject).format(dateTimeFormatter) + "\"");
            case "LocalTime" -> fields_in_list.add("\"" + fieldName + "\":\"" + ((LocalTime) fieldObject).format(dateTimeFormatter) + "\"");
        }
    }

    /**
     * Any object with exported to string.
     *
     * @param object
     * @param rid
     * @return
     */
    public static String objectToString(Object object, boolean rid) {
        retainIdentity = rid;
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Exported.class)) {
            throw new RuntimeException("No @Exported support!");
        }
        String output = "{";
        try {
            if (clazz.isEnum()) {
                output += object.toString();
            } else {
                output += getObjectFieldsToString(object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        output += "}";
        identityStorage = new HashSet<>();
        identityObjectStorage = new HashMap<>();
        return output;
    }

    /**
     * Create graph and children connection to detect cycle.
     * @param fieldObject Child
     * @param object Parent
     */
    private static void detectCycle(Object fieldObject, Object object) {
        if (fieldObject != null && !identityStorage.contains(fieldObject.hashCode())) {
            identityStorage.add(fieldObject.hashCode());
            Set<Integer> set = new HashSet<>();
            set.add(fieldObject.hashCode());
            identityObjectStorage.put(object.hashCode(), set);
        } else if (fieldObject != null) {
            Set<Integer> set = identityObjectStorage.get(object.hashCode());
            if (set == null) {
                set = new HashSet<>();
                set.add(fieldObject.hashCode());
                identityObjectStorage.put(object.hashCode(), set);
            }
            if (!set.contains(fieldObject.hashCode())) {
                set.add(fieldObject.hashCode());
            } else {
                Graph graph = new Graph(identityStorage.size());
                for (Integer key : identityObjectStorage.keySet()) {
                    List<Integer> ids = identityStorage.stream().toList();
                    Set<Integer> dis = identityObjectStorage.get(key);
                    for (Integer value : dis) {
                        graph.addEdge(ids.indexOf(key), ids.indexOf(value));
                    }
                }
                if (graph.isCyclic()) {
                    throw new RuntimeException("Cycle detected!! Plz, dont play with cycles))");
                }
            }
        }
    }
}
