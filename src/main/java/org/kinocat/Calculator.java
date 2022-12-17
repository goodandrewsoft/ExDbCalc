package org.kinocat;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final int ADD = 0x0001;
    private static final int SUB = 0x0002;
    private static final int MULTI = 0x0004;
    private static final int DIV = 0x0008;

    private final char[] expression;

    int index = 0;


    public Calculator(String expression) {
        this.expression = expression.toCharArray();
    }

    private boolean addOrDm(List<Double> numbers, StringBuilder strNum, char sign, char dm) {
        if (strNum.length() > 0) {
            double number = Double.parseDouble(strNum.toString());
            strNum.setLength(0);
            addOrDm(numbers, number, sign, dm);
            return true;
        } else return false;
    }

    private void addOrDm(List<Double> numbers, double number, char sign, char dm) {
        if (sign == '-') number = -number;
        if (dm != '?') {
            double prevNumber = numbers.get(numbers.size() - 1);
            if (dm == '*') {
                prevNumber *= number;
            } else {
                prevNumber /= number;
            }
            numbers.set(numbers.size() - 1, prevNumber);
        } else {
            numbers.add(number);
        }
    }

    private Double calc(int brCount) {
        final char[] exp = expression;
        int length = exp.length;
        StringBuilder strNum = new StringBuilder();
        List<Double> numbers = new ArrayList<>();
        char sign = '+';
        char dm = '?'; // division or multiplication
        for (; ; index++) {
            if (index >= length) {
                if (addOrDm(numbers, strNum, sign, dm)) {
                    sign = '?';
                    dm = '?';
                }
                break;
            }
            char c = exp[index];
            if (Character.isDigit(c) || c == '.') {
                if (sign == '?' && dm == '?') throw new RuntimeException("Operators absent2");
                /*if (sign == '-') {
                    strNum.append(sign);
                }
                sign = '+';*/
                strNum.append(c);
            } else {
                if (addOrDm(numbers, strNum, sign, dm)) {
                    sign = '?';
                    dm = '?';
                }
                if (c == ')') {
                    brCount--;
                    if (brCount < 0) throw new RuntimeException("Incorrect braces");
                    break;
                } else if (c == '(') {
                    if (sign == '?' && dm == '?') throw new RuntimeException("Operators absent");
                    //brCount++;
                    index++;
                    double number = calc(1);
                    addOrDm(numbers, number, sign, dm);
                    dm = '?';
                    sign = '?';
                } else if (c == ' ') {

                } else if (c == '+') {
                    if (index > 0 && exp[index - 1] == '+') throw new RuntimeException("Incorrect addition");
                    if (sign != '-') sign = '+';
                } else if (c == '-') {
                    if (index > 0 && exp[index - 1] == '-') throw new RuntimeException("Incorrect subtraction");
                    sign = sign == '+' ? '-' : sign == '-' ? '+' : '-';
                } else if (c == '*' || c == '/') {
                    if (numbers.isEmpty() || sign != '?' || dm != '?') throw new RuntimeException("Incorrect division or multiplication");
                    dm = c;
                } else {
                    throw new RuntimeException("Incorrect character");
                }
            }
        }
        if (sign != '?' || dm != '?') throw new RuntimeException("Redundant operators");
        if (brCount != 0) throw new RuntimeException("Incorrect braces");

        return numbers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public Double calculate() {
        Double b = calc(0);


        return b;
    }
}
