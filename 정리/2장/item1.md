# 아이템1. 생성자 대신 정적 팩터리 메서드를 고려하라

## 클래스의 인스턴스를 얻는 방법

- public 및 protected 생성자
- 정적 팩터리 메서드(static factory method)

### public 및 protected 생성자

```java
public class book {
	public book {
	}
}
```

```java

public class Main {
    public static void main(String[] args) {
        Book book = new Book();
    }
}
```

### 정적 팩터리 메서드

```java
public class Book {
    public static Book getInstance() {
        return new Book();
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Book book = Book.getInstance();
    }
}
```

> 디자인 패턴의 팩터리 메서드와는 다르다.

## 정적 팩토리 메서드의 장점

### 이름을 가질 수 있다.

- 생성자의 매개변수와 생성자 자체로는 반환될 객체의 특성을 제대로 설명하지 못한다.
- 아래 코드를 살펴보자.

```java
public class Table {
    private int number;
    private Boolean isEmpty;

    public Table(int number, Boolean isEmpty) {
        this.number = number;
        this.isEmpty = isEmpty;
    }

    public static Table getEmptyTable(int number) {
        return new Table(number, true);
    }

    public static Table getNotEmptyTable(int number) {
        return new Table(number, false);
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
				Table table1 = new Table(1, true);
        Table table2 = new Table(2, false); 

        Table emptyTable = Table.getEmptyTable(1);
        Table notEmptyTable = Table.getNotEmptyTable(2);
    }
}
```

- 비어 있는 테이블과 비어있지 않는 테이블을 만든다고 했을때, 위와 같이 생성자를 사용하는 것 보다는 팩토리 메서드를 사용하는것이 더 가독성이 높을수 있다.
- 지금은 생성자 시그니처가 한개이지만, 생성자 시그니처가 여러개라고 했을 때는 각 생성자가 어떤 특징을 가진 인스턴스를 반환하는지 알기 어렵다. 그럴때도 정적 팩토리 메서드를 이용해 메서드 명을 의미있게 명시해준다면 쉽게 알수 있다.

### 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.

- 불변 클래스는 인스턴스를 미리 만들어 놓거나, 새로 생성한 인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.

```java
public final class Boolean implements java.io.Serializable, Comparable<Boolean>{
    public static final Boolean TRUE = new Boolean(true);

    public static final Boolean FALSE = new Boolean(false);
		
		public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }
}
```

- 위의 코드처럼 캐싱 후에 값을 재활용할 수 있다.
- 이러한 방식으로 같은 객체가 자주 요청되는 상황이라면 성능을 상당히 올려줄 수 있다.
- 이것은 플라이웨이트 패턴과 유사하다.

[[구조 패턴] 플라이웨이트 패턴(Flyweight Pattern) 이해 및 예제](https://readystory.tistory.com/137)

- 이러한 특징때문에 정적 팩터리 방식의 클래스를 인스턴스 통제 클래스라고 하는데 인스턴스 통제란 언제 어느 인스턴스를 살아 있게 할지를 철저히 통제할수 있다는 의미입니다.
    - 예를들어 싱글턴 방식이나 인스턴스화 불가, 불변 값 클래스의 동치성 보장 등이 있다.

### 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

- 가장 대표적인 예는 `java.util.Collections` 이다.

```java
public class Collections {
    // Suppresses default constructor, ensuring non-instantiability.
    private Collections() {
    }
		
		public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c) {
        return new UnmodifiableCollection<>(c);
    }
}
```

- 위와 같은 코드를 통해 구현체를 숨기고 API를 작게 만들수 있다.
- Java7에서는 인터페이스에 정적 메서드를 선언할 수 없었다. Java8부터는 인터페이스가 정적 메서드를 가질 수 있게 되었지만, public 정적 멤버만 허용했다 그러나 Java9부터는 private 정적 메서드까지 허락하지만 정적 필드와 정적 멤버 클래스는 여전히 public이여야 하는 단점이 있다.

### 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

```java
public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E>
    implements Cloneable, java.io.Serializable {
		
	EnumSet(Class<E>elementType, Enum<?>[] universe) {
        this.elementType = elementType;
        this.universe    = universe;
    }

	public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            return new RegularEnumSet<>(elementType, universe);
        else
            return new JumboEnumSet<>(elementType, universe);
    }
}

```

- 위의 코드처럼 EnumSet은 원소의 수에 따라 다른 구현체를 반환하는데 이와 같이 매개변수에 따라 다른 구현체가 나오게 된다. 이러한 구현의 가장 큰 장점은 구현체의 존재를 알 필요가 없다는 것이다.

### 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

- 서비스 제공자 프레임워크를 만드는 근간이 되는데 서비스 제공자 프레임워크란 서비스의 구현체를 클라이언트에 제공하는 것을 프레임워크가 통제하여 클라이언트를 구현체로부터 분리하는 것이다.
- 가장 대표적인것이 JDBC가 있다.
- 서비스 제공자 프레임워크의 핵심 컴포넌트
    1. 서비스 인터페이스 : 구현체의 동작을 정의
    2. 제공자 등록 API : 제공자가 구현체를 등록할 때 사용
    3. 서비스 접근 API : 클라이언트가 서비스의 인스턴스를 얻을 때 사용
    4. 서비스 제공자 인터페이스 : 서비스 인터페이스의 인스턴스를 생성하는 팩터리 객체로 이게 없다면 각 구현체를 인스턴스로 만들때 인터페이스를 사용해야 한다.
- 간단하게 말하지면 클래스의 정보만 알수 있다면 해당 객체를 읽을 수 있다는 의미입니다.
- JDBC에는 connection이 서비스 인터페이스를 DriverManager.registerDriver가 제공자 등록 API 역할을, DriverManager.getConnection이 서비스 접근 API 역할을, Driver가 서비스 제공자 인터페이스 역할을 수행하게 된다.

```java
Class.forName("oracle.jdbc.driver.OracleDriver"); 
Connection conn = null; 
conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORA92", "test", "test"); 
Statement..
```

- Class.forName()을 통해 자기 자신을 초기화 하고 DirverManger에 Dirver를 등록하게 되고 Connection을 통해 구현체의 동작을 할수사용할 수 있다고 생각하면 좋을거 같습니다.

## 단점

### 상속을 하려면 public이나 protected 생성자가 필요해 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.

- 상속보다 컴포지션을 사용, 불변 타입으로 만들려면 이 제약을 지켜야 한다는 점에서는 오히려 장점일 수 있습니다.

### 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

- API 문서를 작성해놓으면 되지만, 어렵기 때문에 개발자들 끼리 명명규칙을 약속해놓았다.

[제목 없음](https://www.notion.so/b1e7091f8616417090a4d378100b87f5)

## 결론

- 정적 팩토리 메서드는 장단점이 명확하기 때문에 장단점을 잘 파악하고 사용해야 한다.
- 한 클래스에 시그니처가 같은 생성자가 여러 개 필요할 것 같다면 생성자를 정적 팩터리 메서드로 바꾸고 각 차이를 잘 드러내는 이름을 지어주는게 더 좋다.
- 개발자 들끼리 약속해놓은 명명방식이 있다. 명명방식을 잘 지킨다면 단점 중 하나를 해결할 수 있다.

### 정적 팩토리 메서드의 장점

1. 이름을 가질 수 있다.
2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
3. 반환 타입의 하위 타입 객체를 반환할 수 있다.
4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

### 정적 팩토리 메서드의 단점

1. 상속을 하려면 public이나 protected 생성자가 필요해 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

## 출처

[이펙티브 자바](https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=171196410)
