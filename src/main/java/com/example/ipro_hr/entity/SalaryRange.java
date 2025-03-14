package com.example.ipro_hr.entity;

public enum SalaryRange {
    RANGE_1_5("1-5 mln"),
    RANGE_5_10("5-10 mln"),
    RANGE_10_15("10-15 mln"),
    RANGE_15_20("15-20 mln"),
    RANGE_20_PLUS("20+ mln");

    private final String label;

    SalaryRange(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Enumni stringdan olish uchun metod
    public static SalaryRange fromLabel(String label) {
        for (SalaryRange range : values()) {
            if (range.label.equals(label)) {
                return range;
            }
        }
        throw new IllegalArgumentException("Invalid salary range: " + label);
    }
}
