package implementation;

import ru.hse.homework4.Mapper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class CustomMapper implements Mapper {
    private final boolean retainIdentity;

    public CustomMapper() {
        retainIdentity = false;
    }

    public CustomMapper(boolean rid) {
        retainIdentity = rid;
    }

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
     * @param input Input string.
     * @return восстановленный экземпляр {@code clazz}
     */
    @Override
    public <T> T readFromString(Class<T> clazz, String input) {
        return CustomDeserializer.stringToObject(clazz, input, retainIdentity);
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
        StringBuilder inputBuilder = new StringBuilder();
        for (int charactor; (charactor = inputStream.read()) != -1; ) {
            inputBuilder.append((char) charactor);
        }
        return readFromString(clazz, inputBuilder.toString());
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
        InputStream stream = new FileInputStream(file);
        return read(clazz, stream);
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
        return CustomSerializer.objectToString(object, retainIdentity);
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
     * @param outputStream Output file stream
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public void write(Object object, OutputStream outputStream) throws IOException {
        String serialized = writeToString(object);
        outputStream.write(serialized.getBytes(Charset.forName("UTF-8")));
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
     * @param file   Write to file.
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    public void write(Object object, File file) throws IOException {
        OutputStream stream = new FileOutputStream(file);
        write(object, stream);
    }
}
