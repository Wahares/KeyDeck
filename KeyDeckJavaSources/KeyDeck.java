import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

//{BUTTON}name{MODE}{KEYS}{MOUSE}{WHEELDELTA}

public class KeyDeck {
    public static void main(String[] args) {

        if (args.length < 1)
            throw new RuntimeException("Not enough arguments");

        Robot rb;
        try {
            rb = new Robot();
        } catch (AWTException e) {
            System.err.println("Problem occured while trying to setup Robot");
            throw new RuntimeException(e);
        }

        String portName="";
        ArrayList<Button> buttons = new ArrayList<>();
        try {
            Scanner fileScan = new Scanner(new File(args[0]));

            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine();

                if(line.startsWith("PORT="))
                portName = line.trim().substring("PORT=".length());
                
                if (!line.startsWith("{BUTTON}"))
                    continue;
                buttons.add(new Button(line, rb));
            }
            if (buttons.size() != 6)
                System.err.println(buttons.size() + "configurated buttons - should be 6");
        } catch (FileNotFoundException e) {
            System.err.println("Could not find config file!");
            throw new RuntimeException(e);
        }
        System.out.println("Succesfully loaded raw button data");

        if(portName.length()==0){
            System.err.println("Could not find defined port name");
            return;
        }
            System.out.println("Found defined port name");


        SerialPort port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);


        if (port.openPort())
            System.out.println(portName + ": port opened successfully");
        else {
            System.out.println("Failed to open port");
            return;
        }

        try (InputStream inputStream = port.getInputStream()) {
            while (true) {
                try {
                    int data = inputStream.read();
                    if (data == -1)
                        continue;
                    for (int i = 0; i < buttons.size(); i++)
                        buttons.get(i).ping((data & (1 << i)) != 0);
                    //if(data!= 0 )
                    //    System.out.println(data);
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("Some problem occured!");
            e.printStackTrace();
        }
    }
}

class Button {
    public ArrayList<Integer> keyInstructions;
    public ArrayList<Integer> mouseInstructions;
    public int wheelDelta;
    public ButtonMode mode;
    public Robot rb;

    public Button(String data, Robot rb) {
        keyInstructions = new ArrayList<>();
        mouseInstructions = new ArrayList<>();
        wheelDelta = 0;
        this.rb = rb;

        mode = Enum.valueOf(ButtonMode.class, data.split("\\{MODE}")[1].split("\\{KEYS}")[0]);

        String[] tmp = data.split("\\{KEYS}")[1].split("\\{MOUSE}");
        if (tmp.length > 0 && tmp[0].length() > 0) {
            String[] keys = data.split("\\{KEYS}")[1].split("\\{MOUSE}")[0].split("\\+");
            for (String key : keys)
                keyInstructions.add(getKeyID(key));
        }
        tmp = data.split("\\{MOUSE}")[1].split("\\{WHEELDELTA}");
        if (tmp.length > 0 && tmp[0].length() > 0) {
            String[] mouse = data.split("\\{MOUSE}")[1].split("\\{WHEELDELTA}")[0].split("\\+");
            for (String ms : mouse)
                mouseInstructions.add(getMouseButtonID(ms));
        }
        if (data.split("\\{WHEELDELTA}").length > 1)
            wheelDelta = Integer.parseInt(data.split("\\{WHEELDELTA}")[1]);

    }

    private boolean wasPressed = false;

    private boolean isPressingSwitched = false;

    public void ping(boolean isPressed) {

        switch (mode) {
            case Click -> {
                if (wasPressed && isPressed)
                    break;
                if (isPressed) {
                    pressInstruction();
                    releaseInstruction();
                    if (wheelDelta != 0)
                        rb.mouseWheel(wheelDelta);
                }
            }
            case Press -> {
                if (isPressed) {
                    if (!wasPressed)
                        pressInstruction();
                    if (wheelDelta != 0)
                        rb.mouseWheel(wheelDelta);
                } else if (wasPressed) {
                    releaseInstruction();
                }
            }
            case Switch -> {
                boolean currentSwitch = isPressingSwitched;
                if (isPressed && !wasPressed)
                    isPressingSwitched = !isPressingSwitched;
                if (isPressingSwitched && wheelDelta != 0)
                    rb.mouseWheel(wheelDelta);
                if (currentSwitch != isPressingSwitched) {
                    if (isPressingSwitched)
                        pressInstruction();
                    else
                        releaseInstruction();
                }
            }
        }
        wasPressed = isPressed;
    }

    private void pressInstruction() {
        for (int key : keyInstructions)
            rb.keyPress(key);
        for (int ms : mouseInstructions)
            rb.mousePress(ms);
    }

    private void releaseInstruction() {
        for (int i = 0; i < mouseInstructions.size(); i++)
            rb.mouseRelease(mouseInstructions.get(mouseInstructions.size() - 1 - i));

        for (int i = 0; i < keyInstructions.size(); i++)
            rb.keyRelease(keyInstructions.get(keyInstructions.size() - 1 - i));

    }

    private int getKeyID(String keyName) {
        return switch (keyName) {
            case "a" -> KeyEvent.VK_A;
            case "b" -> KeyEvent.VK_B;
            case "c" -> KeyEvent.VK_C;
            case "d" -> KeyEvent.VK_D;
            case "e" -> KeyEvent.VK_E;
            case "f" -> KeyEvent.VK_F;
            case "g" -> KeyEvent.VK_G;
            case "h" -> KeyEvent.VK_H;
            case "i" -> KeyEvent.VK_I;
            case "j" -> KeyEvent.VK_J;
            case "k" -> KeyEvent.VK_K;
            case "l" -> KeyEvent.VK_L;
            case "m" -> KeyEvent.VK_M;
            case "n" -> KeyEvent.VK_N;
            case "o" -> KeyEvent.VK_O;
            case "p" -> KeyEvent.VK_P;
            case "q" -> KeyEvent.VK_Q;
            case "r" -> KeyEvent.VK_R;
            case "s" -> KeyEvent.VK_S;
            case "t" -> KeyEvent.VK_T;
            case "u" -> KeyEvent.VK_U;
            case "v" -> KeyEvent.VK_V;
            case "w" -> KeyEvent.VK_W;
            case "x" -> KeyEvent.VK_X;
            case "y" -> KeyEvent.VK_Y;
            case "z" -> KeyEvent.VK_Z;
            case "`" -> KeyEvent.VK_BACK_QUOTE;
            case "0" -> KeyEvent.VK_0;
            case "1" -> KeyEvent.VK_1;
            case "2" -> KeyEvent.VK_2;
            case "3" -> KeyEvent.VK_3;
            case "4" -> KeyEvent.VK_4;
            case "5" -> KeyEvent.VK_5;
            case "6" -> KeyEvent.VK_6;
            case "7" -> KeyEvent.VK_7;
            case "8" -> KeyEvent.VK_8;
            case "9" -> KeyEvent.VK_9;
            case "-" -> KeyEvent.VK_MINUS;
            case "=" -> KeyEvent.VK_EQUALS;
            case "!" -> KeyEvent.VK_EXCLAMATION_MARK;
            case "@" -> KeyEvent.VK_AT;
            case "#" -> KeyEvent.VK_NUMBER_SIGN;
            case "$" -> KeyEvent.VK_DOLLAR;
            case "^" -> KeyEvent.VK_CIRCUMFLEX;
            case "&" -> KeyEvent.VK_AMPERSAND;
            case "*" -> KeyEvent.VK_ASTERISK;
            case "(" -> KeyEvent.VK_LEFT_PARENTHESIS;
            case ")" -> KeyEvent.VK_RIGHT_PARENTHESIS;
            case "_" -> KeyEvent.VK_UNDERSCORE;
            case "+" -> KeyEvent.VK_PLUS;
            case "tab" -> KeyEvent.VK_TAB;
            case "enter" -> KeyEvent.VK_ENTER;
            case "[" -> KeyEvent.VK_OPEN_BRACKET;
            case "]" -> KeyEvent.VK_CLOSE_BRACKET;
            case "\\" -> KeyEvent.VK_BACK_SLASH;
            case ";" -> KeyEvent.VK_SEMICOLON;
            case ":" -> KeyEvent.VK_COLON;
            case "'" -> KeyEvent.VK_QUOTE;
            case "\"" -> KeyEvent.VK_QUOTEDBL;
            case "," -> KeyEvent.VK_COMMA;
            case "." -> KeyEvent.VK_PERIOD;
            case "/" -> KeyEvent.VK_SLASH;
            case " " -> KeyEvent.VK_SPACE;
            case "shift" -> KeyEvent.VK_SHIFT;
            case "ctrl" -> KeyEvent.VK_CONTROL;
            default -> throw new IllegalArgumentException("Cannot type character " + keyName);
        };
    }

    private int getMouseButtonID(String name) {
        return switch (name) {
            case "Left" -> MouseEvent.BUTTON1_DOWN_MASK;
            case "Right" -> MouseEvent.BUTTON2_DOWN_MASK;
            case "Middle" -> MouseEvent.BUTTON3_DOWN_MASK;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}

enum ButtonMode {
    Click, Press, Switch
}
