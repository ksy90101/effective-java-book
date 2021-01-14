package book.effective.chpater2.item1;

import java.lang.annotation.Target;

public class Main {
    public static void main(String[] args) {
        Book book1 = new Book();
        Book book2 = Book.getInstance();

        Table table1 = new Table(1, true);
        Table table2 = new Table(2, false);

        Table emptyTable = Table.getEmptyTable(1);
        Table notEmptyTable = Table.getNotEmptyTable(2);
    }
}
