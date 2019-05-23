/**
 * Created by Sangyeon on 2018. 4. 18..
 */
public class DME {

    private String result;
    private String HightoLow = "-┐_";
    private String LowtoHigh = "_┌-";
    private boolean currentLevelHigh;

    public DME() {
        currentLevelHigh = false;
        result = "";
    }

    public void show(char[] signal) {
        for(int i = 0; i < signal.length; i++) {
            if(signal[i] == '0') {
                if(currentLevelHigh) {
                    result += LowtoHigh;
                    currentLevelHigh = true;
                }
                else {
                    result += HightoLow;
                    currentLevelHigh = false;
                }
            }
            else if(signal[i] == '1') {
                if(currentLevelHigh) {
                    result += HightoLow;
                    currentLevelHigh = false;
                }
                else {
                    result += LowtoHigh;
                    currentLevelHigh = true;
                }
            }
            else {
                System.out.println("Wrong Input. Signal should be formed with 0 or 1");
                return;
            }
        }
        System.out.println(result);
    }
}
