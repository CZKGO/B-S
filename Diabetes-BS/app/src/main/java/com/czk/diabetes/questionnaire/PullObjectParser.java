package com.czk.diabetes.questionnaire;

import java.io.InputStream;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/6/1.
 */

public class PullObjectParser<E> implements ObjectParser<E> {

    @Override
    public List<E> parse(InputStream is) throws Exception {
        return null;
    }

    @Override
    public String serialize(List<E> objects) throws Exception {
        return null;
    }
}
