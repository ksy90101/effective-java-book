package book.effective.chpater2.item2;

import java.util.Map;
import java.util.WeakHashMap;

public class Main {
    public static void main(String[] args) {
        NutritionFacts2 nutritionFacts2 = new NutritionFacts2(240, 8);
        nutritionFacts2.setCalories(100);
        nutritionFacts2.setSodium(35);
        nutritionFacts2.setCarbohydrate(27);

        NutritionFacts3 nutritionFacts3 = new NutritionFacts3(240, 8);
        nutritionFacts3.setCalories(100);
        nutritionFacts3.setSodium(35);
        nutritionFacts3.setCarbohydrate(27);
        nutritionFacts3.freeze();

        NutritionFacts4 nutritionFacts4 = new NutritionFacts4.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();

        Map<Integer, String> map = new WeakHashMap<>();

        Integer key1 = 127;
        Integer key2 = 2;

        map.put(key1, "hi 1");
        map.put(key2, "hi 2");

        key1 = null;

        System.gc();

        map.entrySet().stream().forEach(el -> System.out.println(el));

        Map<Integer, String> map2 = new WeakHashMap<>();

        Integer key3 = 1000;
        Integer key4 = 2000;

        map2.put(key3, "test a");
        map2.put(key4, "test b");

        key3 = null;

        System.gc();  //강제 Garbage Collection

        map2.entrySet().stream().forEach(el -> System.out.println(el));
    }
}
