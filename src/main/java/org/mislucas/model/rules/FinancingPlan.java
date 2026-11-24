package org.mislucas.model.rules;

/**
 * Opciones de pago para financiar un gasto obligatorio.
 */
public enum FinancingPlan {
    /** Una cuota sin intereses, pagadera la semana siguiente. */
    ONE_INSTALLMENT,
    /** Dos cuotas con un 10 % de interés total. */
    TWO_INSTALLMENTS
}
