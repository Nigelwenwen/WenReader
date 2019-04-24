package com.nigel.wenreader.widget.page;

import java.util.List;

/**
 * Created by nigel on 18-9-11.
 */

public class TxtPage {
    int position;
    String title;
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "TxtPage{" +
                "position=" + position +
                ", title='" + title + '\'' +
                ", titleLines=" + titleLines +
                ", lines=" + lines +
                '}';
    }
}
