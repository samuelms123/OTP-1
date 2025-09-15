package application.view;

import application.model.entity.Post;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PostView {
    private final StringProperty author = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();

    public PostView(Post post, String authorName) {
        this.author.set(authorName);   // resolved from userId
        this.content.set(post.getContent());
    }

    public StringProperty authorProperty() { return author; }
    public StringProperty contentProperty() { return content; }
}
