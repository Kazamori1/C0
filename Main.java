import java.io.*;

public class Main {
    static InputStream input = null;
    static byte[] byt = null;

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            readFile(args[0]);
            TokenAnalyser.analyseTokens(new String(byt),args[1]);
        } else {
            System.exit(1);
        }

    }

    static void readFile(String filePath) throws IOException {
        try {
            input = new FileInputStream(filePath);
            byt = new byte[input.available()];
            input.read(byt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        } finally {
            assert input != null;
            input.close();
        }
    }
}
