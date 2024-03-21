package com.cyser.test.field;

import com.cyser.base.annotations.TimeFormat;
import com.cyser.base.bean.Copyable;
import com.cyser.base.enums.FastDateFormatPattern;

import java.util.List;

public class Boy implements Copyable<Boy> {

    public String id;

    public int age;

    public List<Girl> nn;

    public String name;

    public Long cells;

    @TimeFormat(value = FastDateFormatPattern.CN_DATE_FORMAT)
    public String birth;
}
