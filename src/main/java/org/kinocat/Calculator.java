package org.kinocat;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final int SIGN = 0;
    private static final int DM = 1; // division or multiplication

    private final char[] expression;
    int index = 0;

    private Calculator(String expression) {
        this.expression = expression.toCharArray();
    }

    public static String fmtDouble(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    private static void checkOps(char[] ops) {
        if (ops[SIGN] == '?' && ops[DM] == '?') throw new RuntimeException("Operators absent");
    }

    public static double calculate(String expression) {
        return new Calculator(expression).calc(0);
    }

    public static int calcNumbers(String expression) {
        int cnt = 0;
        boolean found = false;
        int dots = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                if (c == '.') {
                    dots++;
                    if (dots > 1) {
                        found = false;
                        dots = 0;
                    }
                } else {
                    if (!found) {
                        cnt++;
                        found = true;
                    }
                }
            } else {
                found = false;
                dots = 0;
            }
        }
        return cnt;
    }

    private void addOrDm(List<Double> numbers, StringBuilder strNum, char[] ops) {
        if (strNum.length() > 0) {
            double number = Double.parseDouble(strNum.toString());
            strNum.setLength(0);
            addOrDm(numbers, number, ops);
        }
    }

    private void addOrDm(List<Double> numbers, double number, char[] ops) {
        if (ops[SIGN] == '-') number = -number;
        if (ops[DM] != '?') {
            double prevNumber = numbers.get(numbers.size() - 1);
            if (ops[DM] == '*') {
                prevNumber *= number;
            } else {
                prevNumber /= number;
            }
            numbers.set(numbers.size() - 1, prevNumber);
        } else {
            numbers.add(number);
        }
        ops[SIGN] = '?';
        ops[DM] = '?';
    }

    private Double calc(int brCount) {
        final char[] exp = expression;
        StringBuilder strNum = new StringBuilder();
        List<Double> numbers = new ArrayList<>();
        char[] ops = {'+', '?'};
        for (; ; index++) {
            if (index >= exp.length) {
                addOrDm(numbers, strNum, ops);
                break;
            }
            char c = exp[index];
            if (Character.isDigit(c) || c == '.') {
                checkOps(ops);
                strNum.append(c);
            } else {
                addOrDm(numbers, strNum, ops);
                if (c == ')') {
                    brCount--;
                    if (brCount < 0) throw new RuntimeException("Incorrect brackets");
                    break;
                } else if (c == '(') {
                    checkOps(ops);
                    index++;
                    double number = calc(1);
                    addOrDm(numbers, number, ops);
                } else if (c == '+') {
                    if (index > 0 && exp[index - 1] == '+') throw new RuntimeException("Incorrect addition");
                    if (ops[SIGN] != '-') ops[SIGN] = '+';
                } else if (c == '-') {
                    if (index > 0 && exp[index - 1] == '-') throw new RuntimeException("Incorrect subtraction");
                    ops[SIGN] = ops[SIGN] == '+' ? '-' : ops[SIGN] == '-' ? '+' : '-';
                } else if (c == '*' || c == '/') {
                    if (numbers.isEmpty() || ops[SIGN] != '?' || ops[DM] != '?') throw new RuntimeException("Incorrect division or multiplication");
                    ops[DM] = c;
                } else {
                    if (c != ' ') throw new RuntimeException("Incorrect character");
                }
            }
        }
        if (ops[SIGN] != '?' || ops[DM] != '?') throw new RuntimeException("Redundant operators or empty expression");
        if (brCount != 0) throw new RuntimeException("Incorrect brackets");

        return numbers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
