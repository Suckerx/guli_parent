package com.sucker.msmservice.utils;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();
    //我定义的验证码位数是6位
    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }
}


