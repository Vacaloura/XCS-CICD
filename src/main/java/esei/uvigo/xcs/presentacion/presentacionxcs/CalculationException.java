package esei.uvigo.xcs.presentacion.presentacionxcs;

import jakarta.ejb.ApplicationException;

// Expected validation errors must cross the EJB proxy without becoming system errors.
@ApplicationException(rollback = false)
public class CalculationException extends IllegalArgumentException {
    public CalculationException(String message) { super(message); }
}
