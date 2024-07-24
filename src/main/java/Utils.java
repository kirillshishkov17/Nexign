import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<String> readData(String filePath) throws IOException {
        var data = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while(reader.ready()) {
            data.add(reader.readLine());
        }
        reader.close();
        return data;
    }


}
