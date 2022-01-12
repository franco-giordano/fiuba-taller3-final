package plugin.trackmate.examples.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CreditsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public CreditsPanel() {
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        final JLabel lblFirstFrame = new JLabel("Credits");
        final GridBagConstraints gbc_lblFirstFrame = new GridBagConstraints();
        gbc_lblFirstFrame.anchor = GridBagConstraints.WEST;
        gbc_lblFirstFrame.insets = new Insets(0, 0, 5, 5);
        gbc_lblFirstFrame.gridx = 0;
        gbc_lblFirstFrame.gridy = 0;
        add(lblFirstFrame, gbc_lblFirstFrame);

        String s = "Developed by Julian Ferres and Franco Giordano, during 'Taller de Programacion III, FIUBA, 2C2021, Argentina'. In partnership with 'Medios Porosos' Lab";
        String html = "<html><body style='width: 200px'>";

        final JLabel lblLastFrame = new JLabel(html + s);
        final GridBagConstraints gbc_lblLastFrame = new GridBagConstraints();
        gbc_lblLastFrame.anchor = GridBagConstraints.WEST;
        gbc_lblLastFrame.insets = new Insets(0, 0, 5, 5);
        gbc_lblLastFrame.gridx = 0;
        gbc_lblLastFrame.gridy = 1;
        add(lblLastFrame, gbc_lblLastFrame);
    }
}
