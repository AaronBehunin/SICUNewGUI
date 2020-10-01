package sicu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.awt.*;  
import javax.swing.*;  
import javax.swing.border.Border;

import jssc.SerialPort;
import jssc.SerialPortException;

 
public class App 
{
    static String[] parsed = {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", 
    "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", 
    "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}; 

    static class Read840 implements Runnable    
    {
        Read840(){}

        @Override
        public void run()
        {
    
            try
            {
                SerialPort serialPort = new SerialPort("/dev/ttyUSB0");
                serialPort.openPort();//Open serial port                    
                serialPort.setParams(9600, 8, 1, 0);//Set params.
                boolean written = serialPort.writeString("SNDF\r");
                System.out.println("Write to serial port success: " + written);
                String msgReceived = "";

                while (true)
                {
                    while(msgReceived.length() == 0 || msgReceived.charAt(msgReceived.length()-1) != (char)13 ) 
                    {           
                        byte[] tmp = serialPort.readBytes();
                        if(tmp != null) 
                        {
                            msgReceived += new String(tmp);
                        }
                    }
                    
                    // System.out.println(msgReceived.length());
                    if (msgReceived.length() == 0) continue;
            
                    System.out.println(msgReceived);   //System.out.println((int) msgReceived.charAt(msgReceived.length()-1));
                    
                    parsed = msgReceived.split(",");
                    System.out.println(" Parsed Array Token  length : " + parsed.length);




                    System.out.println("MandatoryType                                                         Alarm                                                           Apnea");
                    System.out.println(  " 115 :  "+parsed[115]+",Low Minute Ventilation : "+ parsed[116] + " ,Alarm  Priority :" +parsed[120]) ;
                    System.out.println("Mandatory Type : "  +parsed[8]);
                    System.out.println("Mode :  "  +parsed[7]);
                    System.out.println("Spontaneous Type :  "  +parsed[9]);
                    System.out.println("Trigger type :  "  +parsed[10]);
                    System.out.println("Respiratory Rate :  "  +parsed[11]);
                    System.out.println("Tidal Volume (L):  "  +parsed[12]);
                    System.out.println("Flow :  "  +parsed[13]);
                    System.out.println("FI02 :  "  +parsed[14]);
                    System.out.println("Wave Pattern : "  +parsed[30]);
                    System.out.println("Apnea (s) : "  +parsed[18]);
                    System.out.println("Inspitory Presssure : "  +parsed[44]);
                    System.out.println("Presssure Support : "  +parsed[29]);
                    System.out.println("PEEP : " +parsed[16]);
                    System.out.println("Flow Trigger : "  +parsed[43]);
                    System.out.println("Inspitory Time (s): " +parsed[45]);
                    System.out.println("PEEP HIGH : "+ parsed[60]);
                    System.out.println("PEEP LOW : "+ parsed[61]);
                    System.out.println("Time HIGH : "+ parsed[62]);


                    

             

                    Thread.sleep(1000);
                }                       
            }   
            catch(Exception e)
            {}           
        }
    }
    @SuppressWarnings("serial")
    static class DrawGUI extends JPanel implements Runnable
    {
        private final static int WIDTH = 3000;
        private final static int HEIGHT = 2000;
        public final static int BORDER = 5;
        public final static int FONT_SIZE = 20;

        private String RoomName;

        private Timer timer;

        DrawGUI(String roomName, String oneOneFive,String vent,String priority, String[] RawStrings )
        {
            RoomName = roomName;
        }
        public void run()
        {

        
            setSize(WIDTH, HEIGHT);
            setBackground(Color.BLACK);

            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            while(true)
            {
            //Set the background of Layout
        // Setting a title label
        JLabel title = new JLabel(RoomName.toUpperCase());    
        title.setForeground(Color.white);                     //Title Color
        title.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20));   //title font

        Border border = BorderFactory.createLineBorder(Color.white, 2);     
        title.setBorder(border);

        c.fill = GridBagConstraints.BOTH;
        c.weighty=.25;
        c.weightx=.5;
        c.gridx = 0;
        c.gridy = 0;
        
        //this.add(title);
        this.add(title, c);

        // Sets the name
        this.setName(RoomName);

        this.setBorder(border);

        JLabel info = new JLabel("<html>115 :  "+parsed[115]+"<br>Low Minute Ventilation : "+ parsed[116] + "<br>Alarm  Priority :" +parsed[120]+"</html>");
        info.setForeground(Color.white);  
        info.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20)); 
        info.setBorder(border);

        c.fill = GridBagConstraints.BOTH;
        c.weighty=.25;
        c.weightx=.5;
        c.gridx=1;
        c.gridy=0;

        this.add(info, c);

        String Raw = "<html>Mandatory Type : "  +parsed[8] + "nMode :  "  +parsed[7]+
        "<br>Spontaneous Type :  "  +parsed[9]+
        "<br>Trigger type :  "  +parsed[10]+
        "<br>Respiratory Rate :  "  +parsed[11]+
        "<br>Tidal Volume (L):  "  +parsed[12]+
        "<br>Flow :  "  +parsed[13]+
        "<br>FI02 :  "  +parsed[14]+
        "<br>Wave Pattern : "  +parsed[30]+
        "<br>Apnea (s) : "  +parsed[18]+
        "<br>Inspitory Presssure : "  +parsed[44]+
        "<br>Presssure Support : "  +parsed[29]+
        "<br>PEEP : " +parsed[16]+
        "<br>Flow Trigger : "  +parsed[43]+
        "<br>Inspitory Time (s): " +parsed[45]+
        "<br>PEEP HIGH : "+ parsed[60]+
        "<br>PEEP LOW : "+ parsed[61]+
        "<br>Time HIGH : "+ parsed[62]+"</html>";

        JLabel raw = new JLabel(Raw);
        raw.setForeground(Color.white);  
        raw.setFont(new Font(Font.SANS_SERIF, Font.LAYOUT_LEFT_TO_RIGHT, 20)); 
        raw.setBorder(border);

        c.weighty=.75;
        c.gridwidth =2;
        c.gridx=0;
        c.gridy=1;

        this.add(raw,c);

        this.invalidate();
        this.validate();
        this.repaint();
        setVisible(true);
            }

        }      
    }
    
    public static void main( String[] args ) throws SerialPortException
    {


        System.out.println("starting serial port");

        Read840 read = new Read840();
        new Thread(read).start();     

        String[] Raw = {"a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a"};
        
        DrawGUI draw = new DrawGUI("Joe Somebody Room 4", "OneOneFive", "Vent", "High", Raw);

        JFrame app = new JFrame("Silent ICU");

        app.add(draw, BorderLayout.CENTER);
        app.setSize(1500,800);
        app.setLocationRelativeTo(null);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);

        new Thread(draw).start();

        


        
    }   


}
