package sicu;

import java.awt.event.ActionEvent;
import org.apache.*;
import org.apache.commons.codec.binary.Hex;

import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Observable;
import java.util.Observer;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.*;

public class App {

    private static JFrame app;

    private static JPanel RoomPanel; 
    private static JPanel AlarmPanel; 
    private static JPanel StatsPanel840; 
    private static JPanel StatsPanelMP70; 
    private static JLabel RoomLabel;
    private static JLabel FiveLabel;
    private static JLabel AlarmLabel;
    private static JLabel VentLabel;
    private static JLabel StatsLabel;
    private static JLabel PulseLabel;
    private static JLabel HRLabel;
    private static JLabel ARTLabel;
    private static JLabel RRLabel;
    private static JLabel SPO2Label;
    private static JLabel NBPLabel;

    private final static int WIDTH = 3000;
    private final static int HEIGHT = 2000;

    private static Boolean SPO2RaiseAlarm = false;
    private static Boolean NPBRaiseAlarm = false;
    private static Boolean RRRaiseAlarm = false;
    private static Boolean ARTRaiseAlarm = false;
    private static Boolean hrRaiseAlarm = false;
    private static Boolean pulseRaiseAlarm = false;

    static SerialPort serialPort = new SerialPort("COM3");

    static  String[] parsed = { "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
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
            "a", "a", "a", "a", "a", "a", "a", "a", "a" };     //For UI Debugging purposes( parse needs to be something or 
                                                               //UI won't render

    static class Shutdown implements Runnable                  //Releases Monitor assosiation
    {                                                          //Monitor will keep sending data without this
        Shutdown(){}                                           //ALWAYS EXIT CODE NATURALLY
                                                               

        @Override
        public void run()
        {
            try
            {
                serialPort.writeBytes(SerialHelper.ReleaseRequest);    //Send Release Request

                String msgReceived = "";
                while (msgReceived.length() == 0 || msgReceived.charAt(msgReceived.length() - 1) != (char) 13)
                {
                    byte[] tmp = serialPort.readBytes();        //Wait for responce before closing app
                    if (tmp != null)
                    {
                        System.out.println("Exiting");
                        break;
                    }                  
                }
 
                serialPort.closePort();
            }
            catch (Exception e) 
            {
                System.out.println(e.toString());
            }
        }
    }

    static class ReadH7L implements Runnable                     //Mode for a Philips MP70 Monitor
    {
        ReadH7L(){}

        @Override
        public void run()
        {
            try
            {
                if (serialPort.isOpened())
                {
                    serialPort.closePort();
                }
                        
                serialPort.openPort();// Open serial port
                serialPort.setParams(115200, 8, 1, 0);// Set port params.
        
                String msgReceived = "";
                boolean written = serialPort.writeBytes(SerialHelper.AssociationRequest);     //Send Associantion Request to Monitor

                System.out.println("Write to serial port success: " + written);
                msgReceived="";
                    
                while (msgReceived.length() == 0 || msgReceived.charAt(msgReceived.length() - 1) != (char) 13) 
                {
                    byte[] tmp = serialPort.readBytes();                //Recieve reponce from Monitor
                    if (tmp != null)
                    {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : tmp) 
                        {
                            sb.append(String.format("%02X ", b));
                        }
                        msgReceived += sb.toString();
                    }
                               
                    if (msgReceived.length() == 0)
                        continue;

                    if (msgReceived.contains("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"))      //Packet End String
                    {
                        //Validate that Monitor is now associated
                        System.out.println(SerialTranslator(msgReceived.substring(15, 17), msgReceived.substring(21, 62)));
                                    
                        msgReceived=msgReceived.split("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ")[1];   //save remainder
                        msgReceived = msgReceived.substring(18);
                        break;
                    }
                }

                //Monitor will now send Confirmation request, An appropriate responce must be sent back in order to recieve data
                while (msgReceived.length() == 0 || msgReceived.charAt(msgReceived.length() - 1) != (char) 13) 
                {
                    byte[] tmp = serialPort.readBytes();
                    if (tmp != null) 
                    {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : tmp) 
                        {
                            sb.append(String.format("%02X ", b));
                        }
                        
                        msgReceived += sb.toString();
                    }
                                
                    if (msgReceived.length() == 0)
                        continue;
                    
                    if (msgReceived.contains("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"))
                    {
                        //Parse sent message
                        String mdsRequest= (SerialTranslator(msgReceived.substring(24, 35), msgReceived.substring(36, 47)));
                        System.out.println(mdsRequest);
                        
                        if (mdsRequest.equals("MDS Event Requested"))           //Send back Confirmation responce
                        {
                            serialPort.writeBytes(SerialHelper.MDSResult);
                            System.out.println("MDS Event Result Sent\n");
                        }
                        break;
                    }
                } 
                            
                serialPort.writeBytes(SerialHelper.DataRequest);        //Send Request for Monitor data
                System.out.println("Data Requested");
                       
                msgReceived = "";
                while (msgReceived.length() == 0 || msgReceived.charAt(msgReceived.length() - 1) != (char) 13) 
                {
                    byte[] tmp = serialPort.readBytes();                //Recieve Montor Data
                    if (tmp != null) 
                    {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : tmp) 
                        {
                            sb.append(String.format("%02X ", b));
                        }
                        msgReceived += sb.toString();
                    }

                    

                    if (msgReceived.length() == 0)
                       continue;                                        //Make sure data Packet is complete
                    else if (msgReceived.contains("E1 00 00 02 00 02 ")&&msgReceived.contains("FF FF FF FF FF FF FF FF")) 
                    {                                       
                        if (msgReceived.contains("50 00 75 00 6C 00 73 00 65 00 20 00 00 09 11 00 02 00 06"))         //Parse Pulse Data
                        {
                            int pulseIndex = msgReceived.indexOf("50 00 75 00 6C 00 73 00 65 00 20 00 00 09 11 00 02 00 06") + 96;

                            String pulseValue = msgReceived.substring(pulseIndex, pulseIndex+2);
                            System.out.println("Pulse: "+ Integer.parseInt(pulseValue, 16));

                            int pulseAlarm = msgReceived.indexOf("50 00 75 00 6C 00 73 00 65 00 20 00 00 09 11 00 02 00 06")+78;

                            String pulseAlarmValue = msgReceived.substring(pulseAlarm, pulseAlarm+2);

                            if (pulseAlarmValue.equals("02"))
                                pulseRaiseAlarm = true;
                            else
                                pulseRaiseAlarm = false;
                            if (pulseRaiseAlarm)
                                System.out.println("!!!ALARM!!!");
                            parsed[8]=String.valueOf(Integer.parseInt(pulseValue, 16));
                        }

                        if (msgReceived.contains("48 00 52"))           //Parse Heart Rate Data
                        {
                            int hrIndex = msgReceived.indexOf("48 00 52")+ 96;
                            String hrValue = msgReceived.substring(hrIndex, hrIndex+2);

                            System.out.println("HR: "+ Integer.parseInt(hrValue, 16));

                            int hrAlarm = msgReceived.indexOf("48 00 52")+78;

                            String hrAlarmValue = msgReceived.substring(hrAlarm, hrAlarm+2);

                            if (hrAlarmValue.equals("02"))
                                hrRaiseAlarm = true;
                            else
                                hrRaiseAlarm = false;
                            if (hrRaiseAlarm)
                                System.out.println("!!!ALARM!!!");
                            parsed[9]=String.valueOf(Integer.parseInt(hrValue, 16));
                        }

                        if (msgReceived.contains("41 00 52 00 54"))           //Parse ART Data
                        {
                            int artHighIndex = msgReceived.indexOf("41 00 52 00 54")+ 138;
                            String artHighValue = msgReceived.substring(artHighIndex, artHighIndex+2);
                            parsed[10]=String.valueOf(Integer.parseInt(artHighValue, 16));

                            int artLowIndex = msgReceived.indexOf("41 00 52 00 54")+ 168;
                            String artLowValue = msgReceived.substring(artLowIndex, artLowIndex+2);

                            int artMeanIndex = msgReceived.indexOf("41 00 52 00 54")+ 108;
                            String artMeanValue = msgReceived.substring(artMeanIndex, artMeanIndex+2);

                            System.out.println("ART: "+ Integer.parseInt(artHighValue, 16)+"/"+Integer.parseInt(artLowValue, 16)+" ("+Integer.parseInt(artMeanValue, 16)+")");
                            
                            int ARTAlarm = msgReceived.indexOf("41 00 52 00 54")+120;

                            String ARTAlarmValue = msgReceived.substring(ARTAlarm, ARTAlarm+2);

                            if (ARTAlarmValue.equals("02"))
                                ARTRaiseAlarm = true;
                            else
                                ARTRaiseAlarm = false;
                            if (ARTRaiseAlarm)
                                System.out.println("!!!ALARM!!!");

                            parsed[11]=String.valueOf(Integer.parseInt(artLowValue, 16));

                            parsed[18]= String.valueOf(Integer.parseInt(artMeanValue, 16));
                        }

                        if (msgReceived.contains("52 00 52"))           //Parse RR Data
                        {
                            int RRIndex = msgReceived.indexOf("52 00 52")+ 96;
                            String RRValue = msgReceived.substring(RRIndex, RRIndex+2);

                            System.out.println(("RR: "+ Integer.parseInt(RRValue, 16)));

                            int RRAlarm = msgReceived.indexOf("52 00 52")+78;

                            String RRAlarmValue = msgReceived.substring(RRAlarm, RRAlarm+2);

                            if (RRAlarmValue.equals("02"))
                                RRRaiseAlarm = true;
                            else
                                RRRaiseAlarm = false;
                            if (RRRaiseAlarm)
                                System.out.println("!!!ALARM!!!");

                            parsed[12]=String.valueOf(Integer.parseInt(RRValue, 16));
                        }

                        if (msgReceived.contains("53 00 70 00 4F 20 82 00 20 00 20 00 00 09 17 00 02 03"))           //Parse SPO2 Data
                        {
                            int SPO2Index = msgReceived.indexOf("53 00 70 00 4F 20 82 00 20 00 20 00 00 09 17 00 02 03")+129;
                            String SPO2Value = msgReceived.substring(SPO2Index, SPO2Index+5).replaceAll("\\s+","");

                            float percent = Integer.parseInt(SPO2Value, 16);
                            percent= percent/10;
                            System.out.println(("SPO2: "+ percent+" %"));

                            int SPO2Alarm = msgReceived.indexOf("53 00 70 00 4F 20 82 00 20 00 20 00 00 09 17 00 02 03")+114;

                            String SPO2AlarmValue = msgReceived.substring(SPO2Alarm, SPO2Alarm+2);


                            if (SPO2AlarmValue.equals("02"))
                                SPO2RaiseAlarm = true;
                            else
                                SPO2RaiseAlarm = false;
                            if (SPO2RaiseAlarm)
                                System.out.println("!!!ALARM!!!");
                            parsed[13]=String.valueOf(percent);
                        }

                        if (msgReceived.contains("4E 00 42 00 50"))     //parse NBP Data
                        {
                            int NBPIndexA = msgReceived.indexOf("4E 00 42 00 50")+168;
                            String NBPValueA = msgReceived.substring(NBPIndexA, NBPIndexA+2);

                            if (NBPValueA.equals("00"))
                            {
                                NBPIndexA = msgReceived.indexOf("4E 00 42 00 50")+171;
                                NBPValueA = msgReceived.substring(NBPIndexA, NBPIndexA+2);
                            }

                            int NBPIndexB = msgReceived.indexOf("4E 00 42 00 50")+198;
                            String NBPValueB = msgReceived.substring(NBPIndexB, NBPIndexB+2);

                            if (NBPValueB.equals("00"))
                            {
                                NBPIndexB = msgReceived.indexOf("4E 00 42 00 50")+201;
                                NBPValueB = msgReceived.substring(NBPIndexB, NBPIndexB+2);
                            }

                            int NBPIndexC = msgReceived.indexOf("4E 00 42 00 50")+228;
                            String NBPValueC = msgReceived.substring(NBPIndexC, NBPIndexC+2);

                            if (NBPValueC.equals("00"))
                            {
                                NBPIndexC = msgReceived.indexOf("4E 00 42 00 50")+231;
                                NBPValueC = msgReceived.substring(NBPIndexC, NBPIndexC+2);
                            }
                            
                            

                            System.out.println(("NBP: "+ Integer.parseInt(NBPValueA, 16)+"/"+Integer.parseInt(NBPValueB, 16)+" ("+Integer.parseInt(NBPValueC, 16)+")"));
                            
                            int NPBAlarm = msgReceived.indexOf("4E 00 42 00 50")+150;

                            String NPBAlarmValue = msgReceived.substring(NPBAlarm, NPBAlarm+2);


                            if (NPBAlarmValue.equals("02"))
                                NPBRaiseAlarm = true;
                            else
                                NPBRaiseAlarm = false;
                            if (NPBRaiseAlarm)
                                System.out.println("!!!ALARM!!!");
                            
                            parsed[15]=String.valueOf(Integer.parseInt(NBPValueA, 16));
                            parsed[16]=String.valueOf(Integer.parseInt(NBPValueB, 16)); 
                            parsed[17]=String.valueOf(Integer.parseInt(NBPValueC,16));

                        }
                        msgReceived=""; 
                        serialPort.writeBytes(SerialHelper.DataRequest); 

                    }
                }
                msgReceived=""; 
               
                   

            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
        }
    }

    static class Read840 implements Runnable {
        Read840() {
        }

        @Override
        public void run() {

            try {
                SerialPort serialPort = new SerialPort("#####");

                serialPort.openPort();// Open serial port
                serialPort.setParams(00000, 0, 0, 0);// Set params.

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

    static Thread ShutdownThread = new Thread(new Shutdown());

    
    public static void main( String[] args ) throws SerialPortException, InterruptedException
    {


        System.out.println("starting serial port");

        Read840 read840 = new Read840();
        ReadH7L readH7L = new ReadH7L();


        new Thread(read840).start();    
        new Thread(readH7L).start();  

        String[] Raw = {"a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a"};
        
     //   DrawGUI draw = new DrawGUI(, "OneOneFive", "Vent", "High", Raw);

        app = new JFrame();

        app.setLayout(new GridBagLayout());
        
        Runtime.getRuntime().addShutdownHook(ShutdownThread);
        RoomPanel = new JPanel();
        AlarmPanel = new JPanel();
        StatsPanel840 = new JPanel();
        StatsPanelMP70 = new JPanel();

       RoomPanel.setSize(WIDTH, HEIGHT);
       RoomPanel.setBackground(Color.BLACK);

       AlarmPanel.setSize(WIDTH, HEIGHT);
       AlarmPanel.setBackground(Color.BLACK);

       StatsPanel840.setSize(WIDTH, HEIGHT);
       StatsPanel840.setBackground(Color.BLACK);
       StatsPanelMP70.setSize(WIDTH, HEIGHT);
       StatsPanelMP70.setBackground(Color.BLACK);

        RoomPanel.setLayout(new BorderLayout());
        AlarmPanel.setLayout(new BoxLayout(AlarmPanel, BoxLayout.Y_AXIS));
        StatsPanel840.setLayout(new BoxLayout(StatsPanel840, BoxLayout.Y_AXIS));
        StatsPanelMP70.setLayout(new BoxLayout(StatsPanelMP70, BoxLayout.Y_AXIS));

        RoomLabel = new JLabel();
        FiveLabel = new JLabel();
        AlarmLabel = new JLabel();
        VentLabel = new JLabel();
        StatsLabel = new JLabel();
        PulseLabel = new JLabel();
        HRLabel = new JLabel();
        ARTLabel = new JLabel();
        RRLabel = new JLabel();
        SPO2Label = new JLabel();
        NBPLabel = new JLabel();

        RoomLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        FiveLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        AlarmLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        VentLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        StatsLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        PulseLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        HRLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        ARTLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));   
        RRLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        SPO2Label.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));
        NBPLabel.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));

        Border borderwhite = BorderFactory.createLineBorder(Color.white, 2);
        Border borderblack = BorderFactory.createLineBorder(Color.black, 2);

        RoomLabel.setBorder(borderwhite);
        FiveLabel.setBorder(borderblack);
        AlarmLabel.setBorder(borderblack);
        VentLabel.setBorder(borderblack);
        StatsLabel.setBorder(borderwhite);
        PulseLabel.setBorder(borderblack);
        HRLabel.setBorder(borderblack);
        ARTLabel.setBorder(borderblack);
        RRLabel.setBorder(borderblack);
        SPO2Label.setBorder(borderblack);
        NBPLabel.setBorder(borderblack);
        


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
        c.gridx = 0;
        c.gridy = 1;

        Draw840();

        app.add(StatsPanel840,c);
        c.weighty = .75;
        c.gridx = 1;
        c.gridy = 1;

        DrawMP70();

        app.add(StatsPanelMP70,c);



        app.setSize(1500,800);
        app.setLocationRelativeTo(null);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);


        while(true)
        {
            DrawAlarms();
            Draw840();
            DrawMP70();

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
        FiveLabel.setText("<html><pre>115 :                       " + parsed[115]+ "</pre></html>");
        VentLabel.setText("<html><pre>Low Minute Ventilation :    " + parsed[116]+ "</pre></html>");
        AlarmLabel.setText("<html><pre>Alarm  Priority :           " + parsed[120]
                                + "</pre></html>");
                                
        if (hrRaiseAlarm||pulseRaiseAlarm||ARTRaiseAlarm||RRRaiseAlarm||SPO2RaiseAlarm||NPBRaiseAlarm)
            AlarmLabel.setForeground(Color.RED);
        else
            AlarmLabel.setForeground(Color.WHITE);

        FiveLabel.setForeground(Color.WHITE);
        VentLabel.setForeground(Color.WHITE);

        AlarmPanel.add(Box.createGlue());
        
        AlarmPanel.add(FiveLabel, BorderLayout.CENTER);
        AlarmPanel.add(VentLabel, BorderLayout.CENTER);
        
        AlarmPanel.add(AlarmLabel, BorderLayout.CENTER);
        AlarmPanel.add(Box.createGlue());

        AlarmPanel.revalidate();
        AlarmPanel.repaint();
    }

    public static void Draw840()
    {
        
        StatsPanel840.removeAll();
        StatsLabel.setText("<html><pre>Mandatory Type :         " + parsed[8] 
                                + "<br>nMode :                  " + parsed[7]
                                + "<br>Spontaneous Type :       " + parsed[9] 
                                + "<br>Trigger type :           " + parsed[10]
                                + "<br>Respiratory Rate :       " + parsed[11] 
                                + "<br>Tidal Volume (L):        " + parsed[12]
                                + "<br>Flow :                   " + parsed[13] 
                                + "<br>FI02 :                   " + parsed[14] 
                                + "<br>Wave Pattern :           " + parsed[30]
                                + "<br>Apnea (s) :              " + parsed[18] 
                                + "<br>Inspitory Presssure :    " + parsed[44]
                                + "<br>Presssure Support :      " + parsed[29]
                                + "<br>PEEP :                   " + parsed[16] 
                                + "<br>Flow Trigger :           " + parsed[43] 
                                + "<br>Inspitory Time (s):      " + parsed[45] 
                                + "<br>PEEP HIGH :              " + parsed[60]
                                + "<br>PEEP LOW :               " + parsed[61]
                                + "<br>Time HIGH :              " + parsed[62] 
                                + "</pre></html>");
        StatsLabel.setForeground(Color.WHITE);

        StatsPanel840.add(StatsLabel, BorderLayout.CENTER);

        StatsPanel840.revalidate();
        StatsPanel840.repaint();
    }

    public static void DrawMP70()
    {
        
        StatsPanelMP70.removeAll();
        PulseLabel.setText("<html><pre>Pulse :         " + parsed[8]+"</pre></html>"); 
        HRLabel.setText("<html><pre>Heart Rate :       " + parsed[9]+"</pre></html>");  
        ARTLabel.setText("<html><pre>Arterial Blood Pressure :       " + parsed[10]+"/"+parsed[11] + " ("+parsed[18]+")</pre></html>");
        RRLabel.setText("<html><pre>Respitory Rate :       " + parsed[12]+"</pre></html>");   
        SPO2Label.setText("<html><pre>SpO2 Percent :       " + parsed[13] + "%</pre></html>");
        NBPLabel.setText("<html><pre>NBP :        "+parsed[15]+"/"+parsed[16]+" ("+parsed[17]+")</pre></html>");

        if (pulseRaiseAlarm)
            PulseLabel.setForeground(Color.RED);
        else 
            PulseLabel.setForeground(Color.WHITE);

        if (hrRaiseAlarm)
            HRLabel.setForeground(Color.RED);
        else 
            HRLabel.setForeground(Color.WHITE);
        
        if (ARTRaiseAlarm)
            ARTLabel.setForeground(Color.RED);
        else 
            ARTLabel.setForeground(Color.WHITE);
        
        if (RRRaiseAlarm)
            RRLabel.setForeground(Color.RED);
        else 
            RRLabel.setForeground(Color.WHITE);
        
        if (SPO2RaiseAlarm)
            SPO2Label.setForeground(Color.RED);
        else 
            SPO2Label.setForeground(Color.WHITE);
        
        if (NPBRaiseAlarm)
            NBPLabel.setForeground(Color.RED);
        else 
            NBPLabel.setForeground(Color.WHITE);

            StatsPanelMP70.add(Box.createGlue());
            StatsPanelMP70.add(PulseLabel, BorderLayout.CENTER);
            StatsPanelMP70.add(HRLabel, BorderLayout.CENTER);
            StatsPanelMP70.add(ARTLabel, BorderLayout.CENTER);
            StatsPanelMP70.add(RRLabel, BorderLayout.CENTER);
            StatsPanelMP70.add(SPO2Label, BorderLayout.CENTER);
            StatsPanelMP70.add(NBPLabel, BorderLayout.CENTER);
            StatsPanelMP70.add(Box.createGlue());

            StatsPanelMP70.revalidate();
            StatsPanelMP70.repaint();
    }

    

    public static String SerialTranslator(String header, String session)
    {
        String responce = "";

        if (header.equals("0E"))
        {
            responce+="Association Responce\n";

            responce += "Session: "+ session;    
        }
        else if(header.equals("E1 00 00 02"))
        {
            if (session.equals("00 01 01 12"))
            {
                responce+="MDS Event Requested";
            }
        }

        return responce;
    }
}


