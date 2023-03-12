package com.cyser.test.field;

import com.cyser.base.Copyable;
import com.cyser.base.annotations.TimeFormat;
import com.cyser.base.utils.TimestampUtil;

import java.util.List;

public class Boy implements Copyable<Boy> {

    public String id;

    public int age;

    public List<Girl> nn;

    public String name;

    public Long cells;

    @TimeFormat(value = TimestampUtil.FastDateFormatPattern.CN_DATE_FORMAT)
    public String birth;
}
