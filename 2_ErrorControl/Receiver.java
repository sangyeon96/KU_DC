import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Sangyeon on 2018. 5. 11..
 */
public class Receiver {
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public void StopAndWait(int port, int frameSize, int ACKLostNum) {
        int sequenceNum = 1;
        int ACKNum;
        String sendMSG, receiveMSG;

        try {
            socket = new Socket("localhost", port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while(sequenceNum <= frameSize) {
                //receive frame
                receiveMSG = in.readUTF();
                System.out.println("Received Frame " + receiveMSG);

                if(sequenceNum == ACKLostNum) { //send ACK Lost
                    receiveMSG = in.readUTF();
                    //System.out.println("Received Frame " + receiveMSG + " Again.");
                }

                //send ACK
                ACKNum = Integer.parseInt(receiveMSG);
                sendMSG = String.valueOf(ACKNum);
                out.writeUTF(sendMSG);

                sequenceNum++;
            }
            System.out.println("\nComplete receiving the data.");
            socket.close();
            in.close();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void GoBackN(int port, int frameSize, int frameLostNum, int ACKLostNum) {
        int sequenceNum = 1;
        int ACKNum = 0; //initialize
        int windowSize = 8;
        int windowCount_fL = 0; //initialize
        int windowCount_AL = 0; //initialize
        int discardNum_fL;
        int discardNum_AL;
        boolean firstArrived_fL = true;
        boolean firstArrived_AL = true;
        String sendMSG, receiveMSG;

        try {
            socket = new Socket("localhost", port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            //set discardNum related to frameLostNum
            if(frameSize - frameLostNum < windowSize - 1)
                discardNum_fL = frameSize - frameLostNum + 1;
            else
                discardNum_fL = windowSize;

            //set discardNum related to ACKLostNum
            if(frameSize - ACKLostNum < windowSize - 1)
                discardNum_AL = frameSize - ACKLostNum + 1;
            else
                discardNum_AL = windowSize;

            while(sequenceNum <= frameSize) {
                //System.out.println("sequence Num : " + sequenceNum);
                if(sequenceNum == frameLostNum && firstArrived_fL) {
                    System.out.println("Not Received Frame " + frameLostNum);
                    sendMSG = String.valueOf(ACKNum);
                    out.writeUTF(sendMSG);
                    firstArrived_fL = false;
                } else if(sequenceNum >= frameLostNum && windowCount_fL < discardNum_fL - 1) {
                    receiveMSG = in.readUTF();
                    System.out.println("Discard Frame " + receiveMSG);
                    windowCount_fL++;
                } else if(sequenceNum == ACKLostNum && firstArrived_AL) {
                    out.writeUTF(String.valueOf(ACKNum));
                    firstArrived_AL = false;
                } else if(sequenceNum >= ACKLostNum && windowCount_AL < discardNum_AL) {
                    receiveMSG = in.readUTF();
                    System.out.println("Discard Frame " + receiveMSG);
                    windowCount_AL++;
                } else {
                    //receive frame
                    receiveMSG = in.readUTF();
                    System.out.println("Received Frame " + receiveMSG);

                    //send ACK, Sender will receive later
                    ACKNum = Integer.parseInt(receiveMSG);
                    sendMSG = String.valueOf(ACKNum);
                    out.writeUTF(sendMSG);
                    sequenceNum++;
                }
            }
            System.out.println("\nComplete receiving the data.");
            socket.close();
            in.close();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Receiver receive = new Receiver();

        System.out.println("1. Stop & Wait, 2. Go Back N");
        System.out.print("Input : ");
        int choice = scan.nextInt();
        if(choice == 1) {
            receive.StopAndWait(7356, 30, 19);
        } else if(choice == 2) {
            receive.GoBackN(7357, 30, 11, 23);
        } else {
            System.out.println("Wrong Input. Input should be 1 or 2.");
        }
    }
}
