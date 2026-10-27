package esei.uvigo.xcs.presentacion.presentacionxcs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorBeanTest {
    private CalculatorBean calculator() throws ReflectiveOperationException {
        CalculatorBean bean = new CalculatorBean();
        // Unit tests use the real arithmetic service; container injection is checked on deployment.
        var field = CalculatorBean.class.getDeclaredField("service");
        field.setAccessible(true);
        field.set(bean, new CalculatorService());
        return bean;
    }

    @Test void entersNegativeDecimalAndReusesResult() throws ReflectiveOperationException {
        var bean = calculator();
        bean.negate();
        bean.digit("2");
        bean.decimal();
        bean.digit("5");
        bean.selectOperation("*");
        bean.digit("4");
        bean.equals();
        assertEquals("-10", bean.getDisplay());
        bean.selectOperation("+");
        bean.digit("3");
        bean.equals();
        assertEquals("-7", bean.getDisplay());
        assertEquals("", bean.getError());
    }

    @Test void recoversFromZeroDivisionAndClearResetsPendingOperation() throws ReflectiveOperationException {
        var bean = calculator();
        bean.digit("8");
        bean.selectOperation("/");
        bean.digit("0");
        bean.equals();
        assertEquals("Cannot divide by zero.", bean.getError());
        bean.digit("2");
        bean.equals();
        assertEquals("4", bean.getDisplay());
        bean.selectOperation("+");
        bean.clear();
        assertEquals("0", bean.getDisplay());
        assertEquals("", bean.getExpression());
        assertEquals("", bean.getError());
        bean.equals();
        assertFalse(bean.getError().isEmpty());
    }

    @Test void rejectsIncompleteCalculationAndKeepsInstancesIndependent() throws ReflectiveOperationException {
        var first = calculator();
        var second = calculator();
        first.digit("9");
        first.selectOperation("+");
        first.equals();
        assertFalse(first.getError().isEmpty());
        assertEquals("0", second.getDisplay());
        assertEquals("", second.getError());
        first.digit("1");
        first.selectOperation("*");
        assertEquals("Press = before selecting another operation.", first.getError());
        first.equals();
        assertEquals("10", first.getDisplay());
    }
}
