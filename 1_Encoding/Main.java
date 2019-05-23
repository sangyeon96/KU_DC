import java.util.Scanner;

/**
 * Created by Sangyeon on 2018. 4. 18..
 */
public class Main {
    public static void main(String[] args) {
        String signal;
        Scanner scan = new Scanner(System.in);

        ME me = new ME();
        DME dme = new DME();

        System.out.print("Input the signal : ");
        signal = scan.next();

        System.out.println("\nManchester Encoding Result");
        me.show(signal.toCharArray());

        System.out.println("\nDifferential Manchester Encoding Result");
        dme.show(signal.toCharArray());
    }

}
