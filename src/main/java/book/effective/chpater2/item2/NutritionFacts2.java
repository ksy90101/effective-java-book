package book.effective.chpater2.item2;

public class NutritionFacts2 {
    private final int servingSize; // 필수
    private final int servings; // 필수
    private int calories; // 선택
    private int fat; // 선택
    private int sodium; // 선택
    private int carbohydrate; // 선택

    public NutritionFacts2(int servingSize, int servings) {
        this.servingSize = servingSize;
        this.servings = servings;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }
}
