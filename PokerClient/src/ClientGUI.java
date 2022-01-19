import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame {

    public ClientGUI(){
        ViewManager.gui = this;
        ViewManager.views.add(new LoginView());


        setVisible(true);
        setPreferredSize(new Dimension(270, 250));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(getPreferredSize().width, getPreferredSize().height);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);


        pack();
        repaint();

        changePanel(ViewManager.views.get(0));  // first view will be login

        //changePanel(new Lobby());
        //changePanel(new GameImproved());

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                if (Handler.handler!=null){
                    Handler.handler.sendToServer("player disconnected");
                    try {
                        Handler.handler.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                dispose();

                System.exit(0);
            }
        });
    }

    public void changePanel(JPanel panel) {
        ViewManager.currentView = panel;
        if (panel instanceof Lobby l){
            Table.tableList.clear();
            l.clear();

        }
        panel.repaint();
        panel.revalidate();

        getContentPane().removeAll();
        getContentPane().invalidate();
        getContentPane().add(panel);
        getContentPane().revalidate();
        getContentPane().repaint();
    }
}
