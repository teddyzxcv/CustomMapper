package implementation;

import ru.hse.homework4.Exported;
import ru.hse.homework4.Ignored;

@Exported

public class ReviewComment {

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    private String comment;
    @Ignored
    private String author;
    private boolean resolved;
// toString, геттеры, сеттеры опущены для краткости
}