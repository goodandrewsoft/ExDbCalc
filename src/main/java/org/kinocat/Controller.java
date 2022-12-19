package org.kinocat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Controller {

    /**
     * If 'true', then the following parameters will be used when connecting to the database:
     * URL = "jdbc:mysql://localhost:3306";
     * USERNAME = "root";
     * PASSWORD = "";
     * If 'false' - parameters from user inputs will be used.
     */
    private static final boolean DEFAULT_CONNECTION_PARAMS = false;

    private static final int MENU_MODE = 0;
    private static final int ADD_MODE = 1;
    private static final int TEST_MODE = 2;

    private static final String HELP = "help";
    private static final String ADD = "add";
    private static final String TEST = "test";
    private static final String SHOW = "show";
    private static final String EDIT = "edit";
    private static final String DEL = "del";
    private static final String EXIT = "exit";

    private static final String HELP_MESSAGE = HELP + "\tDisplay this help.\n" +
            TEST + "\tTest expression.\n" +
            ADD + "\tAdd expression to database.\n" +
            SHOW + "\tShow expressions.\n" +
            EDIT + "\tEdit expressions.\n" +
            DEL + "\tDelete expression from database\n" +
            EXIT + "\tExit calculator.\n\n" +
            "For command help, type 'help command'";

    Map<String, String> mHelpMap = new HashMap<>() {
        {
            put(HELP, "To make a more specific request, please type 'help <item>'");
            put(TEST, "Write command, like 'test 2+2*2' and press enter for test expression, or 'test' for enter to test mode");
            put(ADD, "Write command, like 'add 2+2*2' and press enter for add to database, or 'add' for enter to add mode");
            put(SHOW, "Example:\n" +
                    "'show' - show all\n" +
                    "'show result 30' - show all expressions that result in 30.\n" +
                    "'show result >30' - show all expressions with result greater than 30.\n" +
                    "'show result <30' - show all expressions with result less than 30.\n" +
                    "'show 30' - show all expression with id 30 and so on (with < or >)."
            );
            put(EDIT, "Write 'edit id expression' for edit expression with specific id");
            put(DEL, "Write 'del id' for delete expression with specific id or 'del *' for truncate table.");
            put(EXIT, "Used for exit calculator.");
        }
    };

    Scanner mScanner = new Scanner(System.in);
    int mMode = MENU_MODE;
    String mPrompt = "calc> ";

    public static void main(String[] args) {
        new Controller().go();
    }

    private static String parseArgument(String action, int index) {
        action = action.trim();
        if (action.length() == index) {
            return null; // without argument
        } else {
            if (action.charAt(index) != ' ') throw new RuntimeException();
            return action.substring(index).trim();
        }
    }

    private String prln(String text) {
        System.out.print(text);
        return mScanner.nextLine();
    }

    private String prompt() {
        System.out.print(mPrompt);
        return mScanner.nextLine();
    }

    private void showResults(List<String> list) {
        System.out.println("----------- EXPRESSIONS AND RESULTS -------------");
        for (String ex : list) {
            System.out.println(ex);
        }
        System.out.println("-------------------------------------------------");
    }

    private void addExpression(ExpressionsDAO db, String expression, boolean test) throws SQLException {
        try {
            double result = Calculator.calculate(expression);
            System.out.println(expression + " = " + Calculator.fmtDouble(result));
            if (!test) {
                db.addExpressionAndResult(expression, result);
                System.out.println("The expression was added to the database successfully.");
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("\tERROR: " + e.getMessage());
        }
    }

    private void editExpression(ExpressionsDAO db, int id, String expression) throws SQLException {
        try {
            double result = Calculator.calculate(expression);
            System.out.println(expression + " = " + Calculator.fmtDouble(result));
            db.update(id, expression, result);
            System.out.println("The expression was updated in the database successfully.");
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("\tERROR: " + e.getMessage());
        }
    }

    private void showHelpMessage(String arg) {
        if (arg == null) {
            System.out.println(HELP_MESSAGE);
        } else {
            String helpMessage = mHelpMap.get(arg);
            if (helpMessage == null) throw new RuntimeException();
            System.out.println(helpMessage);
        }
    }

    public void go() {
        ExpressionsDAO db;
        try {
            if (DEFAULT_CONNECTION_PARAMS) {
                db = new ExpressionsDAO();
            } else {
                String url = prln("Write jdbc mysql address, or press enter for use default:\n> ");
                if (url.trim().isEmpty()) {
                    url = ExpressionsDAO.URL;
                } else {
                    if (!url.startsWith("jdbc:mysql://")) url = "jdbc:mysql://" + url;
                }
                System.out.println("used url: " + url);
                String username = prln("Write username, or press enter for use default.\n> ");
                if (username.trim().isEmpty()) username = ExpressionsDAO.USERNAME;
                System.out.println("used username: " + username);
                String password = prln("Write password, or press enter for use default:\n> ");
                if (password.trim().isEmpty()) password = ExpressionsDAO.PASSWORD;
                System.out.println("used password: " + password);
                db = new ExpressionsDAO(url, username, password);
            }
            System.out.println("Connected to database successfully\n");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Calculator 1.0\nType 'help' for help.");
        while (true) {
            try {
                switch (mMode) {
                    case MENU_MODE:
                        String action = prompt();
                        if (action.startsWith(HELP)) {
                            String arg = parseArgument(action, HELP.length());
                            showHelpMessage(arg);
                        } else if (action.startsWith(ADD)) {
                            String arg = parseArgument(action, ADD.length());
                            if (arg == null) {
                                mMode = ADD_MODE;
                            } else {
                                addExpression(db, arg, false);
                            }
                        } else if (action.startsWith(TEST)) {
                            String arg = parseArgument(action, TEST.length());
                            if (arg == null) {
                                mMode = TEST_MODE;
                            } else {
                                addExpression(db, arg, true);
                            }
                        } else if (action.startsWith(SHOW)) {
                            String arg = parseArgument(action, SHOW.length());
                            if (arg == null) {
                                showResults(db.index(null, '*', 0));
                            } else {
                                String[] args = arg.split(" ", 2);
                                String key;
                                if (args.length == 2 && args[0].equals("result")) {
                                    arg = args[1];
                                    key = "result";
                                } else {
                                    key = "id";
                                }

                                char c;
                                double r;
                                if (arg.charAt(0) == '>' || arg.charAt(0) == '<') {
                                    c = arg.charAt(0);
                                    r = Double.parseDouble(arg.substring(1));
                                } else {
                                    c = '=';
                                    r = Double.parseDouble(arg);
                                }
                                showResults(db.index(key, c, r));
                            }
                        } else if (action.startsWith(EDIT)) {
                            String arg = parseArgument(action, EDIT.length());
                            if (arg == null) {
                                showHelpMessage(EDIT);
                            } else {
                                String[] args = arg.split(" ", 2);
                                if (args.length != 2) throw new RuntimeException();
                                int id = Integer.parseInt(args[0]);
                                editExpression(db, id, args[1]);
                            }
                        } else if (action.startsWith(DEL)) {
                            String arg = parseArgument(action, DEL.length());
                            if (arg == null) {
                                showHelpMessage(DEL);
                            } else {
                                if (arg.equals("*")) {
                                    db.truncate();
                                    System.out.println("All expressions was deleted.");
                                } else {
                                    int id = Integer.parseInt(arg);
                                    db.delete(id);
                                    System.out.println("Expression with id " + id + " was deleted successfully.");
                                }
                            }
                        } else if (action.equals(EXIT)) {
                            return;
                        } else {
                            throw new RuntimeException();
                        }

                        break;
                    case ADD_MODE:
                    case TEST_MODE:
                        System.out.println("Write expression (or nothing for exit) and press enter:");
                        String expression = prln(mMode == TEST_MODE ? "test> " : "add> ");
                        if (expression.isEmpty()) {
                            mMode = MENU_MODE;
                        } else {
                            addExpression(db, expression, mMode == TEST_MODE);
                        }

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
