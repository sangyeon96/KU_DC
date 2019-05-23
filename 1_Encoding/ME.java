/**
 * Created by Sangyeon on 2018. 4. 18..
 */
public class ME {

    private String result;
    private String HightoLow = "-┐_"; //0
    private String LowtoHigh = "_┌-"; //1

    public ME() {
        result = "";
    }

    public void show(char[] signal) {
        for(int i = 0; i < signal.length; i++) {
            if(signal[i] == '0') {
                result += HightoLow;
            }
            else if(signal[i] == '1') {
                result += LowtoHigh;
            }
            else {
                System.out.println("Wrong Input. Signal should be formed with 0 or 1");
                return;
            }
        }
        System.out.println(result);
    }
}
