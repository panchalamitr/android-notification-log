package org.hcilab.projects.nlogx.firebase;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;

public class MyNotification {

    public String title = "";
    public String titleBig = "";
    public String text = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleBig() {
        return titleBig;
    }

    public void setTitleBig(String titleBig) {
        this.titleBig = titleBig;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextBig() {
        return textBig;
    }

    public void setTextBig(String textBig) {
        this.textBig = textBig;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public void setTextInfo(String textInfo) {
        this.textInfo = textInfo;
    }

    public String getTextSub() {
        return textSub;
    }

    public void setTextSub(String textSub) {
        this.textSub = textSub;
    }

    public String getTextSummary() {
        return textSummary;
    }

    public void setTextSummary(String textSummary) {
        this.textSummary = textSummary;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String textBig = "";
    public String textInfo = "";
    public String textSub = "";
    public String textSummary = "";
    public String packageName = "";


    public MyNotification(){

    }

    @Override
    public String toString() {
        return "MyNotification{" +
                "title='" + title + '\'' +
                ", titleBig='" + titleBig + '\'' +
                ", text='" + text + '\'' +
                ", textBig='" + textBig + '\'' +
                ", textInfo='" + textInfo + '\'' +
                ", textSub='" + textSub + '\'' +
                ", textSummary='" + textSummary + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    public MyNotification(String _packageName, String _title, String _titleBig, String _text, String _textBig, String _textInfo, String _textSub, String _textSummery){
        packageName = _packageName;
        title = _title;
        titleBig = _titleBig;
        text = _text;
        textBig = _textBig;
        textInfo = _textInfo;
        textSub = _textSub;
        textSummary = _textSummery;
    }

    public Map<String, String> toMapAlarmDow() {
        HashMap<String, String> result = new HashMap<>();
        result.put("packageName", packageName);
        result.put("title", title);
        result.put("titleBig", titleBig);
        result.put("text", text);
        result.put("textBig", textBig);
        result.put("textInfo", textInfo);
        result.put("textSub", textSub);
        result.put("textSummary", textSummary);
        return result;
    }
}
