package com.cyser.test.json;

import com.cyser.base.utils.JsonUtil;
import com.cyser.test.R;
import com.cyser.test.bean.Simple;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class Json2MapTest {

    public static void main1() {
        Simple simple = new Simple();
        simple.setAge(1);
        simple.setName("cyser");
        String json = JsonUtil.pojo2Json(R.ok(simple));
        System.out.println(json);
        R<Simple> ret = JsonUtil.json2Obj(json, new TypeReference<R<Simple>>() {});
        System.out.println(ret.getData().getName());
    }

    public static void main(String[] args) {
        main2();;
    }

    public static void main2() {
        String json = "[[[3,3],[5.5,5.5]],[],[],[[0.5,23.5]],[],[],[]]";
        List<Object> arr=JsonUtil.json2Obj(json, new TypeReference<List<Object>>() {});
        System.out.println();
    }
}
