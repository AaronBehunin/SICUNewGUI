package sicu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Observable;
import java.util.Observer;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import jssc.SerialPort;
import jssc.SerialPortException;

public class App {

    private static JFrame app;

    private static JPanel RoomPanel; 
    private static JPanel AlarmPanel; 
    private static JPanel StatsPanel; 
    private static JLabel RoomLabel;
    private static JLabel AlarmLabel;
    private static JLabel StatsLabel;

    private final static int WIDTH = 3000;
    private final static int HEIGHT = 2000;

    static String[] parsed = { "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "a", "a", "a" };



    static class Read840 implements Runnable {
        Read840() {
        }

        @Override
        public void run() {

           
            try {
                SerialPort serialPort = new SerialPort("/dev/ttyUSB0");
                serialPort.openPort();// Open serial port
                serialPort.setParams(9600, 8, 1, 0);// Set params.



                String msgReceived = "";

               while(true)
            {
                boolean written = serialPort.writeString("SNDF\r");
                System.out.println("Write to serial port success: " + written);
                msgReceived="";
                    while (msgReceived.length() == 0 || msgReceived.charAt(msgReceived.length() - 1) != (char) 13) {
                        byte[] tmp = serialPort.readBytes();
                        if (tmp != null) {
                            msgReceived += new String(tmp);
                        }
                    }

                 /*   BufferedWriter writer = new BufferedWriter(
                    new FileWriter("/home/sicu/Desktop/840SeriesLogs.txt", true)  //Set true for append mode
                    );
                    writer.newLine();   //Add new line
                    writer.write(msgReceived);
                    writer.write("\n");
                    writer.write("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    writer.close();
*/
                    // System.out.println(msgReceived.length());
                    if (msgReceived.length() == 0)
                        continue;

                    System.out.println(msgReceived); // System.out.println((int)
                                                     // msgReceived.charAt(msgReceived.length()-1));

                    parsed = msgReceived.split(",");
                    System.out.println(" Parsed Array Token  length : " + parsed.length);

                    System.out.println(
                            "MandatoryType                                                         Alarm                                                           Apnea");
                    System.out.println(" 115 :  " + parsed[115] + ",Low Minute Ventilation : " + parsed[116]
                            + " ,Alarm  Priority :" + parsed[120]);
                    System.out.println("Mandatory Type : " + parsed[8]);
                    System.out.println("Mode :  " + parsed[7]);
                    System.out.println("Spontaneous Type :  " + parsed[9]);
                    System.out.println("Trigger type :  " + parsed[10]);
                    System.out.println("Respiratory Rate :  " + parsed[11]);
                    System.out.println("Tidal Volume (L):  " + parsed[12]);
                    System.out.println("Flow :  " + parsed[13]);
                    System.out.println("FI02 :  " + parsed[14]);
                    System.out.println("Wave Pattern : " + parsed[30]);
                    System.out.println("Apnea (s) : " + parsed[18]);
                    System.out.println("Inspitory Presssure : " + parsed[44]);
                    System.out.println("Presssure Support : " + parsed[29]);
                    System.out.println("PEEP : " + parsed[16]);
                    System.out.println("Flow Trigger : " + parsed[43]);
                    System.out.println("Inspitory Time (s): " + parsed[45]);
                    System.out.println("PEEP HIGH : " + parsed[60]);
                    System.out.println("PEEP LOW : " + parsed[61]);
                    System.out.println("Time HIGH : " + parsed[62]);

                }
                
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        
    }
    }

 /*   @SuppressWarnings("serial")
    static class DrawRoomLabel
    {
        
        private String RoomName;
        private Timer timer;
        DrawGUI(String roomName, String oneOneFive, String vent, String priority, String[] RawStrings) {
            RoomName = roomName;
        }
        public void run() {
            setSize(WIDTH, HEIGHT);
            setBackground(Color.BLACK);
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            while (true) {
                // Set the background of Layout
                // Setting a title label
                JLabel title = new JLabel(RoomName.toUpperCase());
                title.setForeground(Color.white); // Title Color
                title.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20)); // title font
                Border border = BorderFactory.createLineBorder(Color.white, 2);
                title.setBorder(border);
                c.fill = GridBagConstraints.BOTH;
                c.weighty = .25;
                c.weightx = .5;
                c.gridx = 0;
                c.gridy = 0;
                // this.add(title);
                this.add(title, c);
                // Sets the name
                this.setName(RoomName);
                this.setBorder(border);
                JLabel info = new JLabel("<html>115 :  " + parsed[115] + "<br>Low Minute Ventilation : " + parsed[116]
                        + "<br>Alarm  Priority :" + parsed[120] + "</html>");
                info.setForeground(Color.white);
                info.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
                info.setBorder(border);
                c.fill = GridBagConstraints.BOTH;
                c.weighty = .25;
                c.weightx = .5;
                c.gridx = 1;
                c.gridy = 0;
                this.add(info, c);
                String Raw = "<html>Mandatory Type : " + parsed[8] + "nMode :  " + parsed[7]
                        + "<br>Spontaneous Type :  " + parsed[9] + "<br>Trigger type :  " + parsed[10]
                        + "<br>Respiratory Rate :  " + parsed[11] + "<br>Tidal Volume (L):  " + parsed[12]
                        + "<br>Flow :  " + parsed[13] + "<br>FI02 :  " + parsed[14] + "<br>Wave Pattern : " + parsed[30]
                        + "<br>Apnea (s) : " + parsed[18] + "<br>Inspitory Presssure : " + parsed[44]
                        + "<br>Presssure Support : " + parsed[29] + "<br>PEEP : " + parsed[16] + "<br>Flow Trigger : "
                        + parsed[43] + "<br>Inspitory Time (s): " + parsed[45] + "<br>PEEP HIGH : " + parsed[60]
                        + "<br>PEEP LOW : " + parsed[61] + "<br>Time HIGH : " + parsed[62] + "</html>";
                JLabel raw = new JLabel();
                raw.setText(Raw);
                raw.setForeground(Color.white);
                raw.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
                raw.setBorder(border);
                c.weighty = .75;
                c.gridwidth = 2;
                c.gridx = 0;
                c.gridy = 1;
                this.add(raw, c);
                this.invalidate();
                this.validate();
                this.repaint();
                setVisible(true);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }      
    } */
    
    public static void main( String[] args ) throws SerialPortException, InterruptedException
    {


        System.out.println("starting serial port");

        Read840 read = new Read840();
        new Thread(read).start();     

        String[] Raw = {"a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a"};
        
     //   DrawGUI draw = new DrawGUI(, "OneOneFive", "Vent", "High", Raw);

        app = new JFrame();

        app.setLayout(new GridBagLayout());
        

        RoomPanel = new JPanel();
        AlarmPanel = new JPanel();
        StatsPanel = new JPanel();

       RoomPanel.setSize(WIDTH, HEIGHT);
       RoomPanel.setBackground(Color.BLACK);

       AlarmPanel.setSize(WIDTH, HEIGHT);
       AlarmPanel.setBackground(Color.BLACK);

       StatsPanel.setSize(WIDTH, HEIGHT);
       StatsPanel.setBackground(Color.BLACK);

        RoomPanel.setLayout(new BorderLayout());
        AlarmPanel.setLayout(new BorderLayout());
        StatsPanel.setLayout(new BorderLayout());

        RoomLabel = new JLabel();
        AlarmLabel = new JLabel();
        StatsLabel = new JLabel();

        RoomLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        AlarmLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        StatsLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));

        Border border = BorderFactory.createLineBorder(Color.white, 2);

        RoomLabel.setBorder(border);
        AlarmLabel.setBorder(border);
        StatsLabel.setBorder(border);


        GridBagConstraints c = new GridBagConstraints();

        
        c.fill = GridBagConstraints.BOTH;
        c.weighty = .25;
        c.weightx = .5;
        c.gridx = 0;
        c.gridy = 0;

        DrawRoom("Joe Somebody Room 4");

        app.add(RoomPanel, c);

        
        c.fill = GridBagConstraints.BOTH;
        c.weighty = .25;
        c.weightx = .5;
        c.gridx = 1;
        c.gridy = 0;

        DrawAlarms();

        app.add(AlarmPanel,c);

        c.weighty = .75;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;

        DrawStats();

        app.add(StatsPanel,c);



        app.setSize(1500,800);
        app.setLocationRelativeTo(null);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);


        while(true)
        {
            DrawAlarms();
            DrawStats();
            Thread.sleep(1000);
        }


        


        
    }   


    public static void DrawRoom(String RoomName)
    {
        RoomPanel.removeAll();
        RoomLabel.setText(RoomName);
        RoomLabel.setForeground(Color.WHITE);

        RoomPanel.add(RoomLabel, BorderLayout.CENTER);

        RoomPanel.revalidate();
        RoomPanel.repaint();
    }

    public static void DrawAlarms()
    {
        AlarmPanel.removeAll();
        AlarmLabel.setText("<html>115 :  " + parsed[115] + "<br>Low Minute Ventilation : " + parsed[116]
        + "<br>Alarm  Priority :" + parsed[120] + "</html>");
        AlarmLabel.setForeground(Color.WHITE);

        AlarmPanel.add(AlarmLabel, BorderLayout.CENTER);



        AlarmPanel.revalidate();
        AlarmPanel.repaint();
    }

    public static void DrawStats()
    {
        StatsPanel.removeAll();
        StatsLabel.setText("<html>Mandatory Type : " + parsed[8] + "nMode :  " + parsed[7]
        + "<br>Spontaneous Type :  " + parsed[9] + "<br>Trigger type :  " + parsed[10]
        + "<br>Respiratory Rate :  " + parsed[11] + "<br>Tidal Volume (L):  " + parsed[12]
        + "<br>Flow :  " + parsed[13] + "<br>FI02 :  " + parsed[14] + "<br>Wave Pattern : " + parsed[30]
        + "<br>Apnea (s) : " + parsed[18] + "<br>Inspitory Presssure : " + parsed[44]
        + "<br>Presssure Support : " + parsed[29] + "<br>PEEP : " + parsed[16] + "<br>Flow Trigger : "
        + parsed[43] + "<br>Inspitory Time (s): " + parsed[45] + "<br>PEEP HIGH : " + parsed[60]
        + "<br>PEEP LOW : " + parsed[61] + "<br>Time HIGH : " + parsed[62] + "</html>");
        StatsLabel.setForeground(Color.WHITE);

        StatsPanel.add(StatsLabel, BorderLayout.CENTER);

        StatsPanel.revalidate();
        StatsPanel.repaint();
    }


}


