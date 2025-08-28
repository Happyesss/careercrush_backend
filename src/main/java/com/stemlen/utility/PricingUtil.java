package com.stemlen.utility;

public class PricingUtil {

    /**
     * Calculates final per-month price after applying plan, checkout and student discounts.
     * All percents are expected as whole numbers (e.g., 40 for 40%).
     * Stack order: base -> plan% -> checkout% -> student% (configurable later).
     */
    public static double calculateEffectivePerMonth(double basePerMonth,
                                                    double planPercent,
                                                    double checkoutPercent,
                                                    double studentPercent) {
        double afterPlan = applyPercent(basePerMonth, planPercent);
        double afterCheckout = applyPercent(afterPlan, checkoutPercent);
        double afterStudent = applyPercent(afterCheckout, studentPercent);
        return round2(afterStudent);
    }

    /** Apply a percentage discount to an amount. */
    public static double applyPercent(double amount, double percent) {
        if (percent <= 0) return amount;
        return amount * (1 - (percent / 100.0));
    }

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
