package bg.sofia.uni.fmi.mjt.cooking.eatingtime;

public enum MealType {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack"),
    TEA_TIME("Teatime");


    private final String value;

    MealType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}

