# KU_DC
Konkuk University Data Communication Class Sources.

## 과제 1: Encoding

우선 코드는 Manchester Encoding을 구현한 **ME.java**, Differential Manchester Encoding을 구현한 **DME.java**와 그 두 클래스를 객체로 선언하여 그래프를 출력하는 **Main.java**로 이루어져 있다.

### ME.java

우선 Manchester Encoding은 간단하게 0은 High to Low(-┐_)로 나타내고, 1은 Low to High(_┌-)로 나타낸다. 그러므로 입력 받은 signal문자열에서 for문으로 문자를 하나씩 받아 0이냐 1이냐에 따라 High to Low 또는 Low to High로 나타낸다.

### DME.java

Differential Manchester Encoding의 경우, 이전 신호의 영향을 받는다. 현재신호가 0인 경우 이전 신호에서 변화 없음, 1인 경우 이전 신호에서 변화를 준다(변화라 함은 High to Low -> Low to High 또는 Low to High -> High to Low). 그래서 ME.java에서 추가로 boolean형 변수 currentLevelHigh를 추가하였다. currentLevelHigh값이 true면 이전 신호가 Low to High였다는 것이고, false면 이전 신호가 High to Low였다는 것이다. currentLevelHigh값에 따라 현재 신호가 0이면 이전 신호 그대로 출력하고, 1이면 이전 신호의 반대로 출력한다.

### Main.java

Main. java는 아래와 같이 0과 1로 이루어진 임의의 값을 입력 받고, ME객체와 DME객체를 생성하여 입력 받은 값의 Manchester Encoding, Differential Manchester Encoding의 결과를 그래프를 출력한다.

출력 결과는 다음과 같다.(임의의 값으로 16비트를 넣어주었다)

![1_output](/Users/Sangyeon/GitHub/KU_DC/img/1_output.png)

## 과제 2: Error Control

소스 파일은 **Sender.java**와 **Receiver.java**로 이루어져 있다. 각 파일은 StopAndWait함수, GoBackN함수, main함수로 이루어져 있다.

우선 Sender.java와 Receiver.java의 main 함수에서 Stop&Wait방식(1번)으로 실행할 지, Go Back N방식(2번)으로 실행할 지 입력한다. 입력한 값이 1번과 2번이 아니면 Wrong Input임을 알려주고, Sender.java와 Receiver.java에 같은 번호를 입력하지 않을 시, 포트번호가 달라 Connection refused된다.

### Stop & Wait 방식(1번을 입력한 경우)

StopAndWait함수는 매개변수로 포트번호인 port, 보내려는 프레임 개수인 frameSize, 프레임 전송이 실패될 번호인 frameLostNum, ACK 전송이 실패될 번호인 ACKLostNum을 가지고 있다. main함수에서 해당 값을 입력하여 StopAndWait함수로 넘겨준다.

 **Sender.java**는 while문으로 sequenceNum 순서대로 1부터 frameSize까지 frame을 전송한다. 그리고 sequenceNum이 frameLostNum이나 ACKLostNum랑 같은 경우, boolean형인 transmissionFail을 true로 바꾼다. frameLostNum인 경우는 아예 out.writeUTF를 하지 않으며, ACKLostNum인 경우, out.writeUTF를 하긴 하지만, Receiver가 in.readUTF를 하고, 다시 ACK를 전송하지 않도록 구현하였다. 그리고 transmissionFail이 true인 경우, ACK를 받지 못했으므로 ACK를 받지 못한 frame을 다시 보내고 그 다음 frame을 계속 전송한다. 여기서 Thread.sleep을 통해 Sender와 Receiver가 데이터를 주고받는 것에 시간 간격이 조금 있게 구현하였다.

 **Receiver.java**는 Sender가 보낸 프레임의 sequenceNum을 받고, 그 sequenceNum을 ACKNum으로 변경해 다시 전송한다. Receiver는 앞에서 언급했다시피 in.readUTF를 하기 위해 ACKLostNum에 대한 정보는 가지고 있지만, frameLostNum에 대한 정보는 가지고 있지 않다. Receiver입장에서 보면 Sender에서 sequenceNum == frameLostNum인 경우 frame이 아예 오지 않았으므로 ACK전송을 하지 않을테고, 그러면 Sender입장에선 ACK가 오지 않았으므로 transmissionFail가 true가 되어 재전송을 하는 것이다.

### Go Back N 방식(2번을 입력한 경우)

GoBackN함수는 StopAndWait함수와 마찬가지로 매개변수로 포트번호인 port, 보내려는 프레임 개수인 frameSize, 프레임 전송이 실패될 번호인 frameLostNum, ACK 전송이 실패될 번호인 ACKLostNum을 가지고 있다.

 **Sender.java**는 while문으로 sequenceNum 순서대로 1부터 frameSize까지 frame을 전송하는데, 여기서는 Receiver가 보낸 ACK를 windowSize만큼 전송 하고 나서 받는다.(여기서 windowSize는 8로 설정하였다.) 그리고 sequenceNum이 frameLostNum랑 같은 경우, out.writeUTF를 하지 않는다. Receiver는 sequenceNum이 frameLostNum랑 같은 경우와 sequenceNum이 ACKLostNum랑 같은 경우, 그 이전ACKNum을 out.writeUTF한다. Sender는 sequenceNum – ACKNum이 windowSize-1과 같지 않은 걸 오류로 보고, 그 이전ACKNum에 1을 더한 값의 프레임부터 재전송한다. 그리고 재전송할 때에는 또 windowSize만큼 보내고나서야 ACK를 받는다. 재전송할 시, sequenceNum와 ACKNum를 같게 하는데, 그러면 다시 또 frameLostNum와 같아질 경우, 이미 오류를 검출하여 재전송한 프레임을 또 오류로 볼 수 있기 때문에, boolean형인 firstArrived를 선언하여 재전송하고나서는 firstArrived=false로 변경한다.

 **Receiver.java**는 sequenceNum과 frameLostNum이 같거나 sequenceNum과 ACKLostNum이랑 같은 경우(오류가 검출될 경우), 그 이전 ACK를 보내고, discardNum으로 버릴 프레임 개수를 계산하여 순서에 맞는 프레임번호가 올 때까지 그 전에 온 프레임들을 버린다.

### Stop&Wait 출력 결과

출력 결과는 frameSize = 30인 경우와 50인 경우로 실행해보았다.

1) frameSize = 30, frameLostNum = 13, ACKLostNum = 19

아래는 Frame Lost가 13에서 일어나고, ACK Lost가 19에서 일어날 시의 Sender.java 출력 결과 일부 스크린샷이다.

![stop&wait_framesize30_sender1](/Users/Sangyeon/GitHub/KU_DC/img/stop&wait_frame size30_sender(1).png)

![stop&wait_framesize30_sender2](/Users/Sangyeon/GitHub/KU_DC/img/stop&wait_frame size30_sender(2).png)

Receiver.java 출력 결과를 통해 1부터 30까지 제대로 받았음을 확인할 수 있다.

![stop&wait_framesize30_receiver](/Users/Sangyeon/GitHub/KU_DC/img/stop&wait_frame size 30_receiver.png)



2) frameSize = 50, frameLostNum = 25, ACKLostNum = 44

Sender.java 출력 결과 일부

![stop&wait_framesize50_sender1](/Users/Sangyeon/GitHub/KU_DC/img/stop&wait_frame size 50_sender(1).png)

![stop&wait_framesize50_sender2](/Users/Sangyeon/GitHub/KU_DC/img/stop&wait_frame size 50_sender(2).png)

Receiver.java 출력 결과

![stop&wait_framesize50_receiver](/Users/Sangyeon/GitHub/KU_DC/img/stop&wait_frame size 50_receiver.png)

 

### Go Back N 출력 결과

1) frameSize = 30, frameLostNum = 11, ACKLostNum = 23

Sender.java 출력 결과 

![gobackN_framesize30_sender1](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 30_sender(1).png)

![gobackN_framesize30_sender2](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 30_sender(2).png)

Receiver.java 출력 결과

![gobackN_framesize30_receiver](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 30_receiver.png)

 

2) frameSize = 50, frameLostNum = 22, ACKLostNum = 37

Sender.java 출력 결과 일부

![gobackN_framesize50_sender1](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 50_sender(1).png)

![gobackN_framesize50_sender2](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 50_sender(2).png)

Receiver.java 출력 결과 일부

![gobackN_framesize50_receiver1](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 50_receiver(1).png)

![gobackN_framesize50_receiver2](/Users/Sangyeon/GitHub/KU_DC/img/gobackN_frame size 50_receiver(2).png)

-----

by 컴퓨터공학과 201511269 송상연

