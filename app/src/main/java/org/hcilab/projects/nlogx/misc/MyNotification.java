package org.hcilab.projects.nlogx.misc;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;

public class MyNotification {

    String title = "";
    String titleBig = "";
    String text = "";
    String textBig = "";
    String textInfo = "";
    String textSub = "";
    String textSummary = "";
    String packageName = "";

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
