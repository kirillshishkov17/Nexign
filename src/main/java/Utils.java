import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<String> readData(String fileName) throws IOException {
        var data = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            data.add(line);
            line = reader.readLine();
        }
        reader.close();
        return data;
    }


}
