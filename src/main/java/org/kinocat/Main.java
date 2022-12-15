package org.kinocat;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final int MENU = 0;
    private static final int SHOW = 1;
    private static final int ADD = 2;

    Scanner mScanner = new Scanner(System.in);
    private int mState = MENU;

    public double calculate(String expression) {
        expression = expression.replaceAll("\\s+","");
        System.out.println(expression);
        return 0;
    }

    public static void main(String[] args) {
//        new Main().go();
        double calculate = new Calculator("2 + 3 * 4").calculate();
        System.out.println(calculate);
        int b = (2 + 2) * (2);
    }

    private String prln(String text) {
        System.out.print(text);
        return mScanner.nextLine();
    }

    public void go() {
        ExpressionsDAO db;
        try {
            db = new ExpressionsDAO();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                switch (mState) {
                    case MENU:
                        String action = prln("Write action (show, add, exit): ");
                        if ("show".equals(action)) {
                            mState = SHOW;
                        } else if ("add".equals(action)) {
                            mState = ADD;
                        } else if ("exit".equals(action)) {
                            return;
                        } else {
                            throw new RuntimeException();
                        }
                        break;
                    case SHOW:
                        System.out.println("* - show all");
                        System.out.println("> - more then");
                        System.out.println("< - less then");
                        System.out.println("= - equal to");
                        System.out.println("back - to main menu");
                        String option = prln("Enter option: ");
                        if (!"back".equals(option)) {
                            char c = option.charAt(0);
                            double result = 0;
                            switch (c) {
                                case '*':
                                    break;
                                case '>':
                                case '=':
                                case '<':
                                    result = Double.parseDouble(prln("Write the desired result, in digit: "));
                                    break;
                                default:
                                    throw new RuntimeException();
                            }

                            List<String> list = db.index(c, result);
                            System.out.println("----------- EXPRESSIONS AND RESULTS -------------");
                            for (String ex : list) {
                                System.out.println(ex);
                            }
                            System.out.println("-------------------------------------------------");
                        } else mState = MENU;
                        break;
                    case ADD:
                        String expression = prln(("Write expression or back for return to main menu: "));
                        if (!"back".equals(expression)) {
                            double result = System.currentTimeMillis();
                            db.addExpressionAndResult(expression, result);
                            System.out.println("The expression was added to the database successfully.");
                        } else mState = MENU;
                        break;
                    default:
                        break;
                }
                System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                System.out.println("Incorrect input!");
            }
        }
    }
}
