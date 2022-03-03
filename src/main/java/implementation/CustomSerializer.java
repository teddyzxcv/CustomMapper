package implementation;

import ru.hse.homework4.Exported;
import ru.hse.homework4.Ignored;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomSerializer {

    public static <T> String getObjectFieldsToString(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fields = filterFields(fields);
        List<String> fields_in_list = new ArrayList<>();
        for (Field field : fields) {
            if (!field.canAccess(object)) {
                field.setAccessible(true);
            }
            Object fieldObject = field.get(object);
            // Null
            if (fieldObject == null) {
                fields_in_list.add("\"" + field.getName() + "\":\"null\"");
                continue;
            }
            addFieldObject(fieldObject, fields_in_list, field);
        }
        return String.join(",", fields_in_list);
    }

    public static List<Field> filterFields(List<Field> fieldList) {
        return fieldList.stream().filter(field -> !field.isAnnotationPresent(Ignored.class) && !field.isSynthetic()).collect(Collectors.toList());
    }

    private static <T> String setToString(Set<T> set, Field field) {
        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
        List<String> fields_in_list = new ArrayList<>();
        for (T fieldObject : set) {
            if (fieldObject.getClass().isAnnotationPresent(Exported.class)) {
                fields_in_list.add(objectToString(fieldObject));
            } else {
                Class<? extends Object> fieldClazz = fieldObject.getClass();
                if (fieldClazz.isEnum()) {
                    fields_in_list.add("\"" + fieldObject + "\"");
                } else if (fieldClazz.getSimpleName().equals("LocalDate")) {

                } else if (fieldClazz.getSimpleName().equals("LocalTime")) {

                } else if (fieldClazz.getSimpleName().equals("LocalDateTime")) {

                } else if (fieldClazz.isPrimitive()) {
                    fields_in_list.add("\"" + fieldObject + "\"");
                } else {
                    fields_in_list.add("\"" + fieldObject + "\"");
                }
            }
        }
        return String.join(",", fields_in_list);
    }

    private static <T> String listToString(List<T> list, Field field) {
        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
        List<String> fields_in_list = new ArrayList<>();
        for (T fieldObject : list) {
            if (fieldObject.getClass().isAnnotationPresent(Exported.class)) {
                fields_in_list.add(objectToString(fieldObject));
            } else {
                Class<? extends Object> fieldClazz = fieldObject.getClass();
                if (fieldClazz.isEnum()) {
                    fields_in_list.add("\"" + fieldObject + "\"");
                } else if (fieldClazz.getSimpleName().equals("LocalDate")) {

                } else if (fieldClazz.getSimpleName().equals("LocalTime")) {

                } else if (fieldClazz.getSimpleName().equals("LocalDateTime")) {

                } else if (fieldClazz.isPrimitive()) {
                    fields_in_list.add("\"" + fieldObject + "\"");
                } else {
                    fields_in_list.add("\"" + fieldObject + "\"");
                }
            }
        }
        return String.join(",", fields_in_list);
    }

    private static <T> void addFieldObject(Object fieldObject, List<String> fields_in_list, Field field) {
        if (fieldObject.getClass().isAnnotationPresent(Exported.class)) {
            fields_in_list.add("\"" + field.getName() + "\":" + objectToString(fieldObject));
        } else {
            Class<? extends Object> fieldClazz = fieldObject.getClass();
            if (Arrays.stream(fieldClazz.getInterfaces()).toList().contains(Set.class)) {
                fields_in_list.add("\"" + field.getName() + "\":[" + setToString((Set<T>) fieldObject, field) + "]");
            } else if (Arrays.stream(fieldClazz.getInterfaces()).toList().contains(List.class)) {
                fields_in_list.add("\"" + field.getName() + "\":[" + listToString((List<T>) fieldObject, field) + "]");
            } else if (fieldClazz.isEnum()) {
                fields_in_list.add("\"" + field.getName() + "\":\"" + fieldObject + "\"");
            } else if (fieldClazz.getSimpleName().equals("LocalDate")) {

            } else if (fieldClazz.getSimpleName().equals("LocalTime")) {

            } else if (fieldClazz.getSimpleName().equals("LocalDateTime")) {

            } else if (fieldClazz.isPrimitive()) {
                fields_in_list.add("\"" + field.getName() + "\":\"" + fieldObject + "\"");
            } else {
                fields_in_list.add("\"" + field.getName() + "\":\"" + fieldObject + "\"");
            }
        }
    }


    public static String objectToString(Object object) {
        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Exported.class)) {
            throw new RuntimeException();
        }
        String output = "{";
        try {
            output += getObjectFieldsToString(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        output += "}";
        return output;
    }
}
