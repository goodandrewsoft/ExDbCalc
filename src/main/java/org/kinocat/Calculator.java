package org.kinocat;

public class Calculator {
    private String expression;

    public Calculator(String expression) {
        this.expression = expression;
    }

    private boolean checkBraces() {
        int lb = expression.indexOf('(');



        return true;
    }

    public double calculate(){
        checkBraces();


        return 0.0;
    }
}
