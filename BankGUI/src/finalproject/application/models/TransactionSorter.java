package finalproject.application.models;

import java.util.Comparator;

public class TransactionSorter implements Comparator<Transaction> {
    @Override
    public int compare(Transaction o1, Transaction o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
}
