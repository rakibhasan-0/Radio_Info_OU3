package Model;
import java.util.ArrayList;

/**
 * It is strategy designe patterns component, there will be different algorithm to fetch data from the API.
 * @param <T> the return type of the data.
 */
public interface DataFetchStrategy<T> {
    ArrayList<T> fetchData();
}

