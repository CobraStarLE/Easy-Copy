package com.cyser.test.json;

import com.cyser.base.utils.JsonUtil;
import com.cyser.test.R;
import com.cyser.test.bean.Simple;
import com.fasterxml.jackson.core.type.TypeReference;

public class Json2MapTest {

    public static void main(String[] args) {
        Simple simple = new Simple();
        simple.setAge(1);
        simple.setName("cyser");
        String json = JsonUtil.pojo2Json(R.ok(simple));
        System.out.println(json);
        R<Simple> ret = JsonUtil.json2Obj(json, new TypeReference<R<Simple>>() {});
        System.out.println(ret.getData().getName());
    }
}
