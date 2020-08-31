package com.cj.cn;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {
    @Test
    public void test01() {
        System.out.println("-----------test01-----------");
        System.out.println(0.05 + 0.01);
        System.out.println(1.0 - 0.42);
        System.out.println(4.015 * 100);
        System.out.println(123.3 / 100);
    }

    @Test
    public void test02() {
        System.out.println("-----------test02-----------");
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        System.out.println(b1.add(b2));
    }

    @Test
    public void test03() {
        System.out.println("-----------test03-----------");
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));
    }

    @Test
    public void test() {
        test01();
        test02();
        test03();
    }
}
