import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataPrinter {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
        System.out.println(new String(bytes, Charset.defaultCharset()));
    }
}