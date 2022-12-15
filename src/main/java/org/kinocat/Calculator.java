package org.kinocat;

public class Calculator {
    private final char[] expression;
    int brCount = 0;


    public Calculator(String expression) {
        this.expression = expression.toCharArray();
    }

    private double calc(double r, StringBuilder strNum, StringBuilder ops) {
        double number = Double.parseDouble(strNum.toString());
        switch (ops.charAt(0)) {
            case '+':
                return r + number;
            case '-':
                return r - number;
            case '*':
                return r * number;
            case '/':
                return r / number;
            default:
                return number;
        }
    }

    private Double checkBraces(int i) {
        double result = 0.0;
        final char[] exp = expression;
        int length = exp.length;
        StringBuilder strNum = new StringBuilder();
        StringBuilder ops = new StringBuilder();
        for (; i < length; i++) {
            char c = exp[i];
            switch (c) {
                case ')':
                    brCount--;
                    if (brCount < 0) return null;
                    result += calc(result, strNum, ops);
                    break;
                case '(':
                    brCount++;
                    Double r = checkBraces(i + 1);
                    if (r == null) return null;
                    result += r;
                    break;
                case ' ':
                    continue;
                case '.':
                    strNum.append(c);
                    break;
                case '+':
                case '-':
                    if (i > 0) {
                        if (exp[i - 1] == '-') return null;
                        if (ops.charAt(ops.length() - 1) == '-') {
                            ops.deleteCharAt(ops.length() - 1);
                            continue;
                        }
                    }
                case '*':
                case '/':
                    result += calc(result, strNum, ops);
                    ops.append(c);

                    break;
                default:
                    if (Character.isDigit(c)) {
                        strNum.append(c);
                    } else {
                        return null;
                    }
                    break;
            }

        }
        return result;
    }

    public double calculate() {
        Double b = checkBraces(0);


        return 0.0;
    }
}
