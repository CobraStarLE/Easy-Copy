package com.cyser.test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.Map;

public class SplitterTest {

    public static void main(String[] args) {
        String rawStr = "12345678好好学习  study ,hard 123 2323 ，<b> </b>!  sf!!！桂\t林S\nSDF\rSSD  、DF   alter(test)<sef8989。，奔啊 lalsdl";
//单个分隔符（可单字或多字）分隔
        List<String> list1 = Splitter.on("。").trimResults().omitEmptyStrings().splitToList(rawStr);
//正则分隔
        List<String> list2 = Splitter.onPattern("\\s").trimResults().omitEmptyStrings().splitToList(rawStr);
//多个分隔符分隔
        List<String> list3 = Splitter.on(CharMatcher.anyOf(",，")).trimResults().omitEmptyStrings().splitToList(rawStr);
//fixedLength固定长度切分，limit限制最大分隔量
        List<String> list4 = Splitter.fixedLength(8).trimResults().omitEmptyStrings().limit(6).splitToList(rawStr);
//字符串转为map,on切分字符串，withKeyValueSeparator分隔key和value
        Map<String, String> map1 = Splitter.on(",").withKeyValueSeparator("=").split("one=1,two=2,three=3,four=4");
        System.out.println();
    }
}
