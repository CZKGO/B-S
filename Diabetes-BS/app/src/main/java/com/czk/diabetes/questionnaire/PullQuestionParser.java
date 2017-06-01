package com.czk.diabetes.questionnaire;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/6/1.
 */

public class PullQuestionParser extends PullObjectParser<QuestionnaireData> {

    @Override
    public List<QuestionnaireData> parse(InputStream is) throws Exception {
        List<QuestionnaireData> questionnaireDatas = null;
        List<QuestionnaireData.AnswerData> answerDatas = null;
        QuestionnaireData questionnaireData = null;

//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlPullParser parser = factory.newPullParser();

        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("questions")) {
                        questionnaireDatas = new ArrayList<QuestionnaireData>();
                    } else if (parser.getName().equals("question")) {
                        eventType = parser.next();
                        questionnaireData = new QuestionnaireData();
                        answerDatas = new ArrayList<>();
                        questionnaireData.setQuestion(parser.getText());
                    } else if (parser.getName().equals("answer")) {
                        eventType = parser.next();
                        questionnaireData.setName(parser.getText());
                    } else if (parser.getName().equals("price")) {
                        eventType = parser.next();
                        questionnaireData.setPrice(Float.parseFloat(parser.getText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("question")) {
                        questionnaireDatas.add(questionnaireData);
                        questionnaireData = null;
                    } else if (parser.getName().equals("answer")) {
                        questionnaireDatas.add(questionnaireData);
                        questionnaireData = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return questionnaireDatas;
    }

    @Override
    public String serialize(List<QuestionnaireData> questionnaireDatas) throws Exception {
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlSerializer serializer = factory.newSerializer();

        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);   //设置输出方向为writer
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "questionnaireDatas");
        for (QuestionnaireData questionnaireData : questionnaireDatas) {
            serializer.startTag("", "questionnaireData");
            serializer.attribute("", "id", questionnaireData.getId() + "");

            serializer.startTag("", "name");
            serializer.text(questionnaireData.getName());
            serializer.endTag("", "name");

            serializer.startTag("", "price");
            serializer.text(questionnaireData.getPrice() + "");
            serializer.endTag("", "price");

            serializer.endTag("", "questionnaireData");
        }
        serializer.endTag("", "questionnaireDatas");
        serializer.endDocument();

        return writer.toString();
    }
}