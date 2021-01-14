package book.effective.chpater2.item1;

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
