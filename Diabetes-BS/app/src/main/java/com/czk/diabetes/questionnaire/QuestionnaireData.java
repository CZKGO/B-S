package com.czk.diabetes.questionnaire;

import java.util.List;

/**
 * Created by 陈忠凯 on 2017/6/1.
 */

public class QuestionnaireData {
    public final static int TYPE_SINGLE = 0;
    public final static int TYPE_MULTI = 1;
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
        private boolean select = false;

        public void setContent(String content) {
            this.content = content;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getContent() {
            return content;
        }

        public String getCode() {
            return code;
        }

        public boolean isSelect() {
            return select;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }
    }
}
