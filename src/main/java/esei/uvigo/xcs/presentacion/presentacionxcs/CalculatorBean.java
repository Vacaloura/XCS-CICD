package esei.uvigo.xcs.presentacion.presentacionxcs;

import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;

@Named("calculator")
@ViewScoped
public class CalculatorBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int MAX_DIGITS = 16;
    @EJB
    private CalculatorService service;
    private String display = "0";
    private String expression = "";
    private String error = "";
    private BigDecimal left;
    private String operation;
    private boolean newInput = true;
    private boolean rightEntered;

    public void digit(String digit) {
        if (digit == null || !digit.matches("[0-9]")) {
            error = "Enter a digit from 0 to 9.";
            return;
        }
        error = "";
        if (newInput) {
            display = "0";
            newInput = false;
            if (operation == null) expression = "";
        }
        if (display.replace("-", "").replace(".", "").length() >= MAX_DIGITS) {
            error = "Use at most 16 digits per number.";
            return;
        }
        display = switch (display) {
            case "0" -> digit;
            case "-0" -> "-" + digit;
            default -> display + digit;
        };
        rightEntered = true;
    }

    public void decimal() {
        error = "";
        if (newInput) {
            display = "0";
            newInput = false;
            if (operation == null) expression = "";
        }
        if (!display.contains(".")) display += ".";
        rightEntered = true;
    }

    public void negate() {
        error = "";
        if (newInput && operation != null) {
            display = "0";
            newInput = false;
        }
        display = display.startsWith("-") ? display.substring(1) : "-" + display;
        newInput = false;
        rightEntered = true;
        if (operation == null) expression = "";
    }

    public void selectOperation(String selected) {
        if (selected == null || !java.util.Set.of("+", "-", "*", "/").contains(selected)) {
            error = "Select a valid operation.";
            return;
        }
        if (operation != null && rightEntered) {
            error = "Press = before selecting another operation.";
            return;
        }
        error = "";
        left = new BigDecimal(display);
        operation = selected;
        expression = left.toPlainString() + " " + symbol(selected);
        newInput = true;
        rightEntered = false;
    }

    public void equals() {
        if (operation == null || !rightEntered) {
            error = "Enter two numbers and select an operation.";
            return;
        }
        try {
            BigDecimal right = new BigDecimal(display);
            BigDecimal result = service.calculate(left, operation, right);
            expression = left.toPlainString() + " " + symbol(operation) + " " + right.toPlainString() + " =";
            display = result.toPlainString();
            operation = null;
            left = null;
            newInput = true;
            rightEntered = false;
            error = "";
        } catch (IllegalArgumentException ex) {
            error = ex.getMessage();
            // The next digit replaces the invalid right operand.
            newInput = true;
            rightEntered = false;
        }
    }

    public void clear() {
        display = "0";
        expression = "";
        error = "";
        left = null;
        operation = null;
        newInput = true;
        rightEntered = false;
    }

    private String symbol(String value) {
        return switch (value) { case "*" -> "×"; case "/" -> "÷"; default -> value; };
    }
    public String getDisplay() { return display; }
    public String getExpression() { return expression; }
    public String getError() { return error; }
}
