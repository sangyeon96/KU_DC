import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Sangyeon on 2018. 5. 11..
 */
public class Sender {
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public void StopAndWait(int port, int frameSize, int frameLostNum, int ACKLostNum) {
        int sequenceNum = 1;
        int ACKNum;
        boolean transmissionFail = false;
        String sendMSG, receiveMSG;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for Receiver to respond...");
            socket = serverSocket.accept();
            System.out.println("Receiver Connected.");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Stop And Wait ARQ(Frame Lost Number : " + frameLostNum + ", ACK Lost Number : " + ACKLostNum);
            while(sequenceNum <= frameSize) {
                //send frame
                sendMSG = String.valueOf(sequenceNum);
                if(sequenceNum == frameLostNum) {
                    System.out.println("Sent Frame " + sequenceNum + ", but Frame Lost");
                    transmissionFail = true;
                } else if(sequenceNum == ACKLostNum) {
                    out.writeUTF(sendMSG);
                    System.out.println("Sent Frame " + sequenceNum);
                    transmissionFail = true;
                } else {
                    out.writeUTF(sendMSG);
                    System.out.println("Sent Frame " + sequenceNum);
                }

                Thread.sleep(300); //transmission takes 0.3s
                if(transmissionFail) { //send frame again
                    out.writeUTF(sendMSG);
                    System.out.println("ACK Not Received.\nSent Frame " + sequenceNum + " Again.");
                    transmissionFail = false;
                    Thread.sleep(300);
                }

                //receive ACK
                receiveMSG = in.readUTF();
                ACKNum = Integer.parseInt(receiveMSG);
                System.out.println("Received ACK " + ACKNum);
                Thread.sleep(100); //it takes 0.1s to send next frame

                sequenceNum++;
            }
            System.out.println("\nComplete sending the data.");
            socket.close();
            serverSocket.close();
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
        int windowCount = 1;
        boolean firstArrived = true; //already resend is false
        String sendMSG, receiveMSG;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for Receiver to respond...");
            socket = serverSocket.accept();
            System.out.println("Receiver Connected.");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Go Back N ARQ(Window Size : " + windowSize + ", Frame Lost Number : " + frameLostNum + ", ACK Lost Number : " + ACKLostNum);
            while(sequenceNum <= frameSize || ACKNum < frameSize) {
                //send frame
                if(sequenceNum <= frameSize) {
                    sendMSG = String.valueOf(sequenceNum);
                    if(sequenceNum == frameLostNum && firstArrived) {
                        System.out.println("Sent Frame " + sequenceNum + ", but Frame Lost");
                        firstArrived = false;
                    } else {
                        out.writeUTF(sendMSG);
                        System.out.println("Sent Frame " + sequenceNum);
                    }
                    Thread.sleep(300); //transmission takes 0.3s
                }
                //receive ACK
                if(windowCount < windowSize) {
                    windowCount++;
                } else {
                    receiveMSG = in.readUTF();
                    ACKNum = Integer.parseInt(receiveMSG);
                    System.out.println("Received ACK " + ACKNum);

                    if(sequenceNum - ACKNum != windowSize - 1) { //retransmission
                        System.out.println("Frame " + (ACKNum + 1) + " Not Received.\nStart sending from Frame " + (ACKNum + 1) + " Again.");
                        sequenceNum = ACKNum;
                        windowCount = 1;
                    }
                }
                Thread.sleep(100); //it takes 0.1s to send next frame
                sequenceNum++;
            }
            System.out.println("\nComplete sending the data.");
            socket.close();
            serverSocket.close();
            in.close();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
            Scanner scan = new Scanner(System.in);
            Sender send = new Sender();

            System.out.println("1. Stop & Wait, 2. Go Back N");
            System.out.print("Input : ");
            int choice = scan.nextInt();
            if(choice == 1) {
                send.StopAndWait(7356, 30, 13, 19);
            } else if(choice == 2) {
                send.GoBackN(7357, 30, 11, 23);
            } else {
                System.out.println("Wrong Input. Input should be 1 or 2.");
            }
    }
}
