package ir.khu.gasht.models;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String title;
    private int isChecked;
    private long alertDate;
    private String type;

    public boolean hasAlert() {
        return alertDate > 0;
    }

    public boolean isAlertInPast() {
        return alertDate < System.currentTimeMillis();
    }

    public long getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(long alertDate) {
        this.alertDate = alertDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
