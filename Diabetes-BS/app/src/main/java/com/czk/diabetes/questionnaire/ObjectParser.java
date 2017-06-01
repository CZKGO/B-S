package com.czk.diabetes.questionnaire;

import java.io.InputStream;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/6/1.
 */

public interface ObjectParser<E> {
    /**
     * 解析输入流 得到Object对象集合
     * @param is
     * @return
     * @throws Exception
     */
    public List<E> parse(InputStream is) throws Exception;

    /**
     * 序列化Object对象集合 得到XML形式的字符串
     * @param objects
     * @return
     * @throws Exception
     */
    public String serialize(List<E> objects) throws Exception;
}
