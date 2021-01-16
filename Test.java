import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String args[]){
        Double a=1.5;
        System.out.println(Double.doubleToRawLongBits(a));
    }

    private static boolean isDigit(char c){
        return c>='0'&&c<='9';
    }
}
