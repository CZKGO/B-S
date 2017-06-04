package com.czk.diabetes.questionnaire;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
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
        QuestionnaireData.AnswerData answerData = null;

//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlPullParser parser = factory.newPullParser();

        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("questions")) {
                        questionnaireDatas = new ArrayList<QuestionnaireData>();
                    } else if (parser.getName().equals("question")) {
                        questionnaireData = new QuestionnaireData();
                        questionnaireData.setQuestion(parser.getAttributeValue(null,"content"));
                        questionnaireData.setType(parser.getAttributeValue(null,"type"));
                        answerDatas = new ArrayList<>();
                        eventType = parser.next();
                    }  else if (parser.getName().equals("answer")) {
                        answerData = new QuestionnaireData.AnswerData();
                        answerData.setCode(parser.getAttributeValue(null,"code"));
                        eventType = parser.next();
                        answerData.setContent(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("question")) {
                        questionnaireData.setAnswers(answerDatas);
                        questionnaireDatas.add(questionnaireData);
                        questionnaireData = null;
                    } else if (parser.getName().equals("answer")) {
                        answerDatas.add(answerData);
                        answerData = null;
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


        return null;
    }
}