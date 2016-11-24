/**
 * Created by Ian Gortan on 9/20/2016.
 */
package Core;
// Contains content for message publication

public class BSDSContent {
    final static int len = 140;
    private String title;
    private String message;
    private int timeToLIve;


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

    public int getTimeToLIve() {
        return timeToLIve;
    }

    public void setTimeToLIve(int timeToLIve) {
        this.timeToLIve = timeToLIve;
    }
}
