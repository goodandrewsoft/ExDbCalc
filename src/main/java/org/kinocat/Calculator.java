package org.kinocat;

public class Calculator {
    private final char[] expression;
    int brCount = 0;


    public Calculator(String expression) {
        this.expression = expression.toCharArray();
    }

    private double calc(double r, StringBuilder strNum, StringBuilder ops) {
        if (ops.length() == 2) {
            strNum.insert(0, ops.charAt(1));
        }
        char c = ops.charAt(0);
        double number = Double.parseDouble(strNum.toString());
        strNum.setLength(0);
        ops.setLength(0);
        switch (c) {
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

    char lc(StringBuilder sb) {
        return sb.charAt(sb.length() - 1);
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
                    if (strNum.length() == 0) return null;
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
                    if (ops.length() != 0 && lc(ops) == '-') {
                        continue;
                    }
                    if (strNum.length() > 0) ops.append('+');
                    break;
                case '-':
                    if (i > 0) {
                        if (exp[i - 1] == '-') return null;
                        char lc = lc(ops);
                        if (lc == '+') {
                            ops.deleteCharAt(ops.length() - 1);
                        } else if (lc == '-') {
                            ops.setCharAt(ops.length() - 1, '+');
                            continue;
                        }
                    }
                    ops.append(c);
                    break;
                case '*':
                case '/':
                    if (strNum.length() == 0 || ops.length() > 0) return null;
                    ops.append(c);
                    break;
                default:
                    if (Character.isDigit(c)) {
                        if (ops.length() > 0) result += calc(result, strNum, ops);
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
