import com.fazecast.jSerialComm.SerialPort;
public class PortScanner {
    public static void main(String[] args) {
        for (SerialPort port : SerialPort.getCommPorts())
            System.out.println(port.getDescriptivePortName());
    }
}
