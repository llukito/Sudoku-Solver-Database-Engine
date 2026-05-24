import view.MetropolisFrame;
import javax.swing.SwingUtilities;

public class MetropolisMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MetropolisFrame frame = new MetropolisFrame();
            frame.setVisible(true);
        });
    }
}