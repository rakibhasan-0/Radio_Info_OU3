package Model;
import java.util.ArrayList;

public interface DataFetchStrategy<T> {
    ArrayList<T> fetchData();
}

