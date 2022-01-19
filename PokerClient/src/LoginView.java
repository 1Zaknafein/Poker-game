import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {


    JLabel nameLabel = new JLabel("Enter name");
    JTextField name_input= new JTextField("", 10);
    JButton connect = new JButton("Connect");
    JButton changeIP = new JButton("IP");
    String address = "localhost";

    public LoginView(){
        setMinimumSize(new Dimension(30,30));
        setPreferredSize(new Dimension(30,30));
        setLayout(null);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(50, 10, 120, 20);
        name_input.setBounds(50, 40, 150, 40);
        name_input.setFont(new Font("Arial", Font.PLAIN, 20));
        connect.setBounds(70, 100, 120, 50);
        connect.setBackground(Color.orange);
        setBackground(Color.darkGray);
        connect.setFocusable(false);

        changeIP.setBounds(130, 160, 60,30);
        changeIP.setFocusable(false);
        changeIP.setBackground(Color.orange);

        add(changeIP);
        add(nameLabel);
        add(name_input);
        add(connect);
        ViewManager.gui.repaint();
        ViewManager.gui.revalidate();


        changeIP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = JOptionPane.showInputDialog("Set server IP", address);
                if (ip!=null && !ip.strip().isEmpty() && ip.length()>10){
                    address = ip;
                }
            }
        });

        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name = name_input.getText();

                if(name.isEmpty()){
                    JOptionPane.showMessageDialog(ViewManager.currentView, "name cannot be empty!");
                    name_input.setText("");
                }
                else if(name.length()<3){
                    JOptionPane.showMessageDialog(ViewManager.currentView, "name too short!");
                    name_input.setText("");
                }
                else if(name.length()>15){
                    JOptionPane.showMessageDialog(ViewManager.currentView, "kinda too long, don't you think?");
                    name_input.setText("");
                }

                else if(!isString(name)){
                    JOptionPane.showMessageDialog(ViewManager.currentView, "use only letters (no spaces either)");
                    name_input.setText("");
                }
                else {

                    //new Handler(8888,"34.142.37.154", player);
                    new Handler(8888,address, new Player(name));


                }
            }
        });




    }

    private boolean isString(String text) {
        return text.matches("[a-zA-Z]+");
    }

}
