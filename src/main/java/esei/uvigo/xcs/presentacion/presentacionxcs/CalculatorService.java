package esei.uvigo.xcs.presentacion.presentacionxcs;

import jakarta.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Stateless
public class CalculatorService {
    // All operations use at most 10 decimal places; ties round away from zero.
    public BigDecimal calculate(BigDecimal left, String operation, BigDecimal right) {
        if (left == null || right == null || operation == null) {
            throw new CalculationException("Enter two numbers and select an operation.");
        }
        BigDecimal result = switch (operation) {
            case "+" -> left.add(right); // new BigDecimal(4) o BigDecimal(5) como ejemplo de tests que fallan y luego se pone: left.add(right) para que funcione bien
            case "-" -> left.subtract(right);
            case "*" -> left.multiply(right);
            case "/" -> {
                if (right.signum() == 0) throw new CalculationException("Cannot divide by zero.");
                yield left.divide(right, 10, RoundingMode.HALF_UP);
            }
            default -> throw new CalculationException("Select a valid operation.");
        };
        return result.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}

