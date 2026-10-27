package esei.uvigo.xcs.presentacion.presentacionxcs;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {
    private final CalculatorService service = new CalculatorService();

    @ParameterizedTest
    @CsvSource({"2,+,3,5", "7,-,4,3", "3,*,4,12", "8,/,2,4",
            "0,+,0,0", "0,*,9,0", "-2,+,5,3", "-3,*,-4,12",
            "0.1,+,0.2,0.3", "1,-,3,-2", "1,/,3,0.3333333333",
            "2,/,3,0.6666666667", "-2,/,3,-0.6666666667",
            "0.00000000005,+,0,0.0000000001"})
    void calculates(String left, String operation, String right, String expected) {
        assertEquals(0, new BigDecimal(expected).compareTo(
                service.calculate(new BigDecimal(left), operation, new BigDecimal(right))));
    }

    @Test void rejectsDivisionByZero() {
        assertEquals("Cannot divide by zero.", assertThrows(CalculationException.class,
                () -> service.calculate(BigDecimal.ONE, "/", BigDecimal.ZERO)).getMessage());
    }
    @Test void rejectsUnknownOperation() {
        assertThrows(CalculationException.class, () -> service.calculate(BigDecimal.ONE, "%", BigDecimal.ONE));
    }
    @Test void rejectsMissingOperand() {
        assertThrows(CalculationException.class, () -> service.calculate(null, "+", BigDecimal.ONE));
    }
}
