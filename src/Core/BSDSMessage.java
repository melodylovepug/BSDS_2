/**
 * Created by Ian Gortan on 9/20/2016.
 */
package Core;

//contains data for message delivery to subscribers

public class BSDSMessage {
    private String title;
    private String message;
    private String source;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

