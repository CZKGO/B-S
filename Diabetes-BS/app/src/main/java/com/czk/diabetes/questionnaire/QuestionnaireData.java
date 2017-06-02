package com.czk.diabetes.questionnaire;

import java.util.List;

/**
 * Created by 陈忠凯 on 2017/6/1.
 */

public class QuestionnaireData {
    private String question;
    private List<AnswerData> answers;
    private String type;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerData> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerData> answers) {
        this.answers = answers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public static class AnswerData {
        private String content;
        private String code;

        public void setContent(String content) {
            this.content = content;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
