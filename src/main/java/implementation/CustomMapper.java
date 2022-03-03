package implementation;

import ru.hse.homework4.Exported;
import ru.hse.homework4.Ignored;
import ru.hse.homework4.Mapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomMapper implements Mapper {
    /**
     * Читает сохранённый экземпляр класса {@code clazz} из строки {@code input} * и возвращает восстановленный экземпляр класса {@code clazz}.
     * <p>
     * Пример вызова:
     *
     * <pre>
     * String input = """
     * {"comment":"Хорошая работа","resolved":false}""";
     * ReviewComment reviewComment =
     * mapper.readFromString(ReviewComment.class, input);
     * System.out.println(reviewComment);
     * </pre>
     *
     * @param clazz класс, сохранённый экземпляр которого находится в {@code input} * @param input строковое представление сохранённого экземпляра класса {@code
     *              clazz}
     * @param input
     * @return восстановленный экземпляр {@code clazz}
     */
    @Override
    public <T> T readFromString(Class<T> clazz, String input) {
        return CustomDeserializer.stringToObject(clazz, input);
    }

    /**
     * Читает объект класса {@code clazz} из {@code InputStream}'а * и возвращает восстановленный экземпляр класса {@code clazz}. * <p>
     * Данный метод закрывает {@code inputStream}.
     * <p>
     * Пример вызова:
     *
     * <pre>
     *
     *
     *
     *
     * ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
     *         System.out.println(reviewComment);
     * </pre>
     *
     * @param clazz       класс, сохранённый экземпляр которого находится в {@code inputStream}
     * @param inputStream поток ввода, содержащий строку в {@link StandardCharsets#UTF_8} кодировке
     *                    String input = """
     *                    {"comment":"Хорошая работа","resolved":false}""";
     *                    ReviewComment reviewComment = mapper.read(ReviewComment.class,
     *                    new
     *                    5
     * @return восстановленный экземпляр класса {@code clazz} * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public <T> T read(Class<T> clazz, InputStream inputStream) throws IOException {
        return null;
    }

    /**
     * Читает сохранённое представление экземпляра класса {@code clazz} из {@code
     * File}'а
     * и возвращает восстановленный экземпляр класса {@code clazz}.
     * <p>
     * Пример вызова:
     *
     * <pre>
     * ReviewComment reviewComment = mapper.read(ReviewComment.class, new
     * File("/tmp/review"));
     * System.out.println(reviewComment);
     * </pre>
     *
     * @param clazz класс, сохранённый экземпляр которого находится в файле
     * @param file  файл, содержимое которого - строковое представление экземпляра
     *              {@code clazz}
     *              в {@link StandardCharsets#UTF_8} кодировке * @param <T> возвращаемый тип метода
     * @return восстановленный экземпляр {@code clazz}
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public <T> T read(Class<T> clazz, File file) throws IOException {
        return null;
    }

    /**
     * Сохраняет {@code object} в строку * <p>
     * Пример вызова:
     *
     * <pre>
     *         ReviewComment reviewComment = new ReviewComment();
     * reviewComment.setComment("Хорошая работа");
     *         reviewComment.setResolved(false);
     *
     *         String string = mapper.writeToString(reviewComment);
     * System.out.println(string);
     * </pre>
     *
     * @param object объект для сохранения
     * @return строковое представление объекта в выбранном формате
     */
    @Override
    public String writeToString(Object object) {
        return CustomSerializer.objectToString(object);
    }

    /**
     * Сохраняет {@code object} в {@link OutputStream}. * <p>
     * <p>
     * То есть после вызова этого метода в {@link OutputStream} должны оказаться байты, соответствующие строковому
     * представлению {@code object}'а в кодировке {@link StandardCharsets#UTF_8}
     * <p>
     * Данный метод закрывает {@code outputStream} * <p>
     * Пример вызова:
     *
     * <pre>
     *         ReviewComment reviewComment = new ReviewComment();
     * reviewComment.setComment("Хорошая работа");
     *         reviewComment.setResolved(false);
     *
     * mapper.write(reviewComment, new FileOutputStream("/tmp/review")); * </pre>
     *
     * @param object       объект для сохранения
     * @param outputStream
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public void write(Object object, OutputStream outputStream) throws IOException {

    }

    /**
     * Сохраняет {@code object} в {@link File}.
     * <p>
     * То есть после вызова этого метода в {@link File} должны оказаться байты,
     * соответствующие строковому
     * представлению {@code object}'а в кодировке {@link
     * StandardCharsets#UTF_8}
     * <p>
     * Данный метод закрывает {@code outputStream} * <p>
     * Пример вызова:
     *
     * <pre>
     *         ReviewComment reviewComment = new ReviewComment();
     * reviewComment.setComment("Хорошая работа");
     *         reviewComment.setResolved(false);
     *
     * mapper.write(reviewComment, new File("/tmp/review")); * </pre>
     *
     * @param object объект для сохранения
     * @param file
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public void write(Object object, File file) throws IOException {

    }
}
