# 아이템2. 생성자에 매개 변수가 많다면 빌더를 고려하라

## 서론

- 정적 팩터리, 생성자에는 공통점이 있다. 바로 선택적 매개변수가 많을 때는 적절하게 대응하지 못한다는 점이다.
- 따라서 선택적 매개변수가 많을 때에는 3가지 방식으로 많이 사용한다.
    1. 점층적 생성자 패턴
    2. 자바빈즈 패턴
    3. 빌더 패턴

## 점층적 생성자 패턴(Telescoping Constructor Pattern)

- 필수 매개변수만 받은 생성자, 필수 매개변수와 선택적 매개변수 1개만 받는 생성자, 필수 매개변수와 선택 매개변수 2개만 받는 생성자 ..... 이렇게 필요할 때마다 각 생성자를 오버로딩하는 방식으로 하는 것이라고 생각하면 됩니다.

```java
public class NutritionFacts {
    private final int servingSize; // 필수
    private final int servings; // 필수
    private final int calories; // 선택
    private final int fat; // 선택
    private final int sodium; // 선택
    private final int carbohydrate; // 선택

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}
```

- 위의 코드처럼 작성하는 것이 `점층적 생성자 패턴` 인데, 가장 큰 단점은 사용자가 설정하길 원치 않는 매개변수까지 포함하기 쉬워 매개변수에도 모두 값을 넣어둬야 한다는 것이다.
- 아울러 지금 같은 경우에 선택 필드가 4개만 있지만 선택 필드가 늘어날 수록 계속 추가될 수 있다는 단점이 있다.
- 즉, 매개변수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.
- 또한 매개변수의 순서를 바꿔 입력해도 컴파일러는 인식하지 못하고 런타임에 버그가 발생하게 되지만 디버깅 하기가 어렵다는 단점도 존재한다.

## 자바빈즈 패턴(JavaBeans Pattern)

- 간단하게 setter 메서드를 이용해 필요한 값만 넣는 방식이다.

```java
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
```

- 필수값만 생성자로 받고 나머지 값들은 setter를 이용해서 받는 방식으로 아래와 같이 사용할 수 있다.

```java
public class Main {
    public static void main(String[] args) {
        NutritionFacts2 nutritionFacts2 = new NutritionFacts2(240, 8);
        nutritionFacts2.setCalories(100);
        nutritionFacts2.setSodium(35);
        nutritionFacts2.setCarbohydrate(27);
    }
}
```

- 그러나 가장 큰 단점이 존재한다. 바로 불변성을 보장할 수 없다는 거와 객체 하나를 만들기 위해 여러 메서드들을 호출해야 한다는 것이다.
- 즉, 완전히 생성되기 전까지 일관성이 무너진 상태가 되며, 스레드 안정성을 보장할 수 없다는 의미이다.
- 이러한 단점을 완화하고자 추가적인 작업이 가능하긴 하다. 바로 얼리고(freezing) 방식이다. 그러나 다루기 어렵기도 하고 freeze 메서드를 확실히 호출했는지 컴파일러가 보증할 방법이 없어서 런타임 오류에 취약하다.

```java
public class NutritionFacts3 {
    private final int servingSize; // 필수
    private final int servings; // 필수
    private int calories; // 선택
    private int fat; // 선택
    private int sodium; // 선택
    private int carbohydrate; // 선택
    private boolean isFreezing;

    public NutritionFacts3(int servingSize, int servings) {
        this.servingSize = servingSize;
        this.servings = servings;
        isFreezing = false;
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

    public void freeze() {
        this.isFreezing = true;
    }

    private void isFreezing() {
        if (isFreezing) {
            throw new RuntimeException();
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        NutritionFacts3 nutritionFacts3 = new NutritionFacts3(240, 8);
        nutritionFacts3.setCalories(100);
        nutritionFacts3.setSodium(35);
        nutritionFacts3.setCarbohydrate(27);
        nutritionFacts3.freeze();
    }
}
```

## 빌더 패턴(Bulider Pattern)

- 필수 매개변수만으로 생성자를 호출해 빌더 객체를 얻고 다음 빌더 객체가 제공하는 setter 메서드로 원하는 선택 매개변수를 얻고 마지막으로 build() 메서드를 호출해 필요한 객체를 얻는 방식이다.
- 빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는 게 보통이다.

```java
public class NutritionFacts4 {
    private final int servingSize; // 필수
    private final int servings; // 필수
    private final int calories; // 선택
    private final int fat; // 선택
    private final int sodium; // 선택
    private final int carbohydrate; // 선택

    public NutritionFacts4(Builder builder) {
        this.servingSize = builder.servingSize;
        this.servings = builder.servings;
        this.calories = builder.calories;
        this.fat = builder.fat;
        this.sodium = builder.sodium;
        this.carbohydrate = builder.carbohydrate;
    }

    public static class Builder {
        private final int servingSize; // 필수
        private final int servings; // 필수

        private int calories; // 선택
        private int fat; // 선택
        private int sodium; // 선택
        private int carbohydrate; // 선택

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int calories) {
            this.calories = calories;
            return this;
        }

        public Builder fat(int fat) {
            this.fat = fat;
            return this;
        }

        public Builder sodium(int sodium) {
            this.sodium = sodium;
            return this;
        }

        public Builder carbohydrate(int carbohydrate) {
            this.carbohydrate = carbohydrate;
            return this;
        }

        public NutritionFacts4 build() {
            return new NutritionFacts4(this);
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        NutritionFacts4 nutritionFacts4 = new NutritionFacts4.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}
```

- 위와 같이 빌더패턴을 구현할 수 있으며 가장 큰 장점은 연쇄적으로 매개변수를 호출할 수 있고 불변을 유지할 수 있다는 점이다.
- 사실 빌더 패턴은 파이썬, 스칼라에 있는 명명된 선택적 매개변수(named optional parameters)를 흉내낸거다.
- 아울러 빌더내에서는 유효성 검사 코드도 할 수 있다. 빌더의 생성자와 메서드에서 잘못된 매개변수를 일찍 발견하기 위해 불변식을 검사하고 빌더로부터 매개변수를 복사한 후 해당 객체 필드들도 검사해야 한다.(이부분은 아이템50에서 다시 나오게 될것이다.)

> 불변 : 어떠한 변경도 허용하지 않는 객체
가변 : 변경을 허용한다.

불변식 : 프로그램이 실행되는 동안, 혹은 정해진 기간 동안 반드시 만족해야 하는 조건

### 계층적으로 설계된 클래스와 함께 쓰기 좋다

```java
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class Pizza {
    final Set<Topping> toppings;

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }

    public enum Topping {HAM, MUSHROOM, ONION, PEPPER, SAUSAGE}

    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addToppings(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));

            return self();
        }

        abstract Pizza build();

        protected abstract T self();
    }
}
```

> Pizza.Builder Class는 재귀적 타입 한정을 이용하는 제네릭 타입이다.

self()는 시뮬레이트한 셀프 타입 관용구라고 한다.

```java
import java.util.Objects;

public class NyPizza extends Pizza {
    private final Size size;

    public NyPizza(Builder builder) {
        super(builder);
        this.size = builder.size;
    }

    public enum Size {SMALL, MEDIUM, LARGE}

    public static class Builder extends Pizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }

        @Override
        Pizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
```

- 위의 코드와 같이 하위 클래스의 메서드가 상위 클래스의 메서드가 정의한 반환 타입이 아닌, 그 하위 타입을 반환하는 기능을 공변 반환 타이핑이라고 한다.
- 이 기능을 이용하면 클라이언트가 형변환에 신경쓰지 않고도 빌더를 사용 가능하다.
- 또한 빌더를 이용하면 가변 인수 매개변수를 여러 개 사용할 수 있다.

## 빌더 패턴의 단점

- 비럳 생성 비용이 크지는 않지만, 성능의 민감한 상황에서는 문제가 생길 수 있다.
- 또한 점층적 생성자 패턴보다 코드가 장황해 이점을 보려면 4개 이상의 매개변수가 있어여 한다.
    - 그러나, API는 시간이 지날 수록 매개변수가 늘어나는 경항이 있어 처음부터 빌더패턴을 고려하는 것도 좋을 방식이다.

## 결론

- 선택적 매개변수가 많다면 아래의 3가지 방식을 고려한다.
    1. 점층적 생성자 패턴
    2. 자바빈즈 패턴
    3. 빌더 패턴
- 점층정 생성자 패턴이나 자바빈즈 패턴은 단점이 있어 매개 변수가 많은 경우에는 빌더 패턴을 사용하는게 더 좋다.

## 출처

[이펙티브 자바](https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=171196410)
