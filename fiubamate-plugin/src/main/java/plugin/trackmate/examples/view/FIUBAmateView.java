package plugin.trackmate.examples.view;

import static fiji.plugin.trackmate.gui.Icons.TRACKMATE_ICON;
import static fiji.plugin.trackmate.gui.Icons.LOG_ICON;
import static fiji.plugin.trackmate.gui.Icons.ADD_ICON;
import static fiji.plugin.trackmate.gui.Icons.CSV_ICON;

import static fiji.plugin.trackmate.gui.Fonts.BIG_FONT;
import static fiji.plugin.trackmate.gui.Fonts.FONT;
import static fiji.plugin.trackmate.gui.Fonts.SMALL_FONT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.opencsv.CSVWriter;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.SpotCollection;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.util.FileChooser;
import fiji.plugin.trackmate.util.FileChooser.DialogType;
import fiji.plugin.trackmate.util.FileChooser.SelectionMode;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import fiji.plugin.trackmate.visualization.table.TrackTableView;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.RoiListener;
import plugin.trackmate.examples.Utils;
import ij.gui.Roi;

import java.text.NumberFormat;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FIUBAmateView extends JFrame
		implements TrackMateModelView, RoiListener {

	private static final long serialVersionUID = 1L;

	private static final Color BORDER_COLOR = new java.awt.Color(192, 192, 192);

	private static final String KEY = "FIUBAMATE";

	public static String selectedFile = TrackTableView.selectedFile;

	private final Model model;

	private final JButton btnAgregarArea;
	private final JButton btnExportarCSV;

	private List<Roi> addedAreas = new ArrayList<Roi>();

	private int firstFrame;

	private int lastFrame;

	private JLabel lblAmountAreasAdded;

	public FIUBAmateView(final Model model, final SelectionModel selectionModel) {
		super("FIUBAmate");
		IJ.log("Inicio!");
		IJ.log("Cantidad de Tracks distintos en el modelo: " + model.getTrackModel().trackIDs(false).size());
		setIconImage(TRACKMATE_ICON.getImage());
		this.model = model;
		firstFrame = 0;
		lastFrame = IJ.getImage().getNFrames() - 1;

		/*
		 * GUI.
		 */

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(350, 521));
		this.setSize(350, 400);

		final GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[] { 1.0, 1.0 };
		layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		setLayout(layout);

		/*
		 * Title
		 */

		final JLabel lblDisplayOptions = new JLabel();
		lblDisplayOptions.setText("FIUBAmate");
		lblDisplayOptions.setFont(BIG_FONT);
		lblDisplayOptions.setHorizontalAlignment(SwingConstants.LEFT);
		final GridBagConstraints gbcLabelDisplayOptions = new GridBagConstraints();
		gbcLabelDisplayOptions.gridwidth = 1;
		gbcLabelDisplayOptions.fill = GridBagConstraints.BOTH;
		gbcLabelDisplayOptions.insets = new Insets(5, 5, 5, 5);
		gbcLabelDisplayOptions.gridx = 0;
		gbcLabelDisplayOptions.gridy = 0;
		add(lblDisplayOptions, gbcLabelDisplayOptions);

		final JButton btnEditSettings = new JButton("Ver creditos", LOG_ICON);
		btnEditSettings.addActionListener(e -> {
			final VerCreditosPanel panel = new VerCreditosPanel();
			JOptionPane.showMessageDialog(
					null,
					panel,
					"FIUBAmate - Creditos",
					JOptionPane.INFORMATION_MESSAGE,
					TRACKMATE_ICON);
		});

		final GridBagConstraints gbcBtnEditSettings = new GridBagConstraints();
		gbcBtnEditSettings.fill = GridBagConstraints.NONE;
		gbcBtnEditSettings.insets = new Insets(5, 5, 5, 5);
		gbcBtnEditSettings.anchor = GridBagConstraints.EAST;
		gbcBtnEditSettings.gridx = 1;
		gbcBtnEditSettings.gridy = 0;
		add(btnEditSettings, gbcBtnEditSettings);

		final JLabel lblContarCuerpos = new JLabel("Obtener estadisticas en Area");
		lblContarCuerpos.setFont(FONT);
		final GridBagConstraints gbcLblContarCuerpos = new GridBagConstraints();
		gbcLblContarCuerpos.anchor = GridBagConstraints.NORTH;
		gbcLblContarCuerpos.fill = GridBagConstraints.HORIZONTAL;
		gbcLblContarCuerpos.insets = new Insets(0, 5, 0, 5);
		gbcLblContarCuerpos.gridx = 0;
		gbcLblContarCuerpos.gridy = 1;
		add(lblContarCuerpos, gbcLblContarCuerpos);

		/*
		 * Spot options panel.
		 */

		final JPanel panelSpotOptions = new JPanel();
		panelSpotOptions.setBorder(new LineBorder(BORDER_COLOR, 1, true));
		final GridBagConstraints gbcPanelSpotOptions = new GridBagConstraints();
		gbcPanelSpotOptions.gridwidth = 2;
		gbcPanelSpotOptions.insets = new Insets(0, 5, 5, 5);
		gbcPanelSpotOptions.fill = GridBagConstraints.BOTH;
		gbcPanelSpotOptions.gridx = 0;
		gbcPanelSpotOptions.gridy = 2;
		add(panelSpotOptions, gbcPanelSpotOptions);
		final GridBagLayout gblPanelSpotOptions = new GridBagLayout();
		gblPanelSpotOptions.columnWeights = new double[] { 0.0, 1.0 };
		gblPanelSpotOptions.rowWeights = new double[] { 0.0, 0.0 };
		panelSpotOptions.setLayout(gblPanelSpotOptions);

		btnAgregarArea = new JButton("Guardar Area", ADD_ICON);
		btnAgregarArea.addActionListener(e -> onAgregarArea());
		btnAgregarArea.setEnabled(false);

		final GridBagConstraints gbcbtnAgregarArea = new GridBagConstraints();
		gbcbtnAgregarArea.anchor = GridBagConstraints.CENTER;
		gbcbtnAgregarArea.insets = new Insets(5, 5, 2, 5);
		gbcbtnAgregarArea.gridx = 0;
		gbcbtnAgregarArea.gridy = 0;
		panelSpotOptions.add(btnAgregarArea, gbcbtnAgregarArea);

		btnExportarCSV = new JButton("Exportar a CSV", CSV_ICON);
		btnExportarCSV.addActionListener(e -> onExportarCSV());
		btnExportarCSV.setEnabled(false);

		final GridBagConstraints gbcbtnExportarCSV = new GridBagConstraints();
		gbcbtnExportarCSV.anchor = GridBagConstraints.CENTER;
		gbcbtnExportarCSV.insets = new Insets(2, 5, 2, 5);
		gbcbtnExportarCSV.gridx = 0;
		gbcbtnExportarCSV.gridy = 1;
		panelSpotOptions.add(btnExportarCSV, gbcbtnExportarCSV);

		lblAmountAreasAdded = new JLabel("Cantidad de areas agregadas: " + addedAreas.size());
		lblAmountAreasAdded.setFont(SMALL_FONT);
		final GridBagConstraints gbclblAmountAreasAdded = new GridBagConstraints();
		gbclblAmountAreasAdded.anchor = GridBagConstraints.CENTER;
		gbclblAmountAreasAdded.insets = new Insets(2, 5, 5, 5);
		gbclblAmountAreasAdded.gridx = 0;
		gbclblAmountAreasAdded.gridy = 2;
		panelSpotOptions.add(lblAmountAreasAdded, gbclblAmountAreasAdded);

		/*
		 * Panel de Configurar frames
		 */

		final JLabel lblConfigFrames = new JLabel("Configurar frames de interes");
		lblConfigFrames.setFont(FONT);
		final GridBagConstraints gbclblConfigFrames = new GridBagConstraints();
		gbclblConfigFrames.anchor = GridBagConstraints.NORTH;
		gbclblConfigFrames.fill = GridBagConstraints.HORIZONTAL;
		gbclblConfigFrames.insets = new Insets(0, 5, 0, 5);
		gbclblConfigFrames.gridx = 0;
		gbclblConfigFrames.gridy = 3;
		add(lblConfigFrames, gbclblConfigFrames);

		/*
		 * Spot options panel.
		 */

		final JPanel panelFrameConfig = new JPanel();
		panelFrameConfig.setBorder(new LineBorder(BORDER_COLOR, 1, true));
		final GridBagConstraints gbcpanelFrameConfig = new GridBagConstraints();
		gbcpanelFrameConfig.gridwidth = 2;
		gbcpanelFrameConfig.insets = new Insets(0, 5, 5, 5);
		gbcpanelFrameConfig.fill = GridBagConstraints.BOTH;
		gbcpanelFrameConfig.gridx = 0;
		gbcpanelFrameConfig.gridy = 4;
		add(panelFrameConfig, gbcpanelFrameConfig);
		final GridBagLayout gblpanelFrameConfig = new GridBagLayout();
		gblpanelFrameConfig.columnWeights = new double[] { 0.0, 0.0 };
		gblpanelFrameConfig.rowWeights = new double[] { 0.0, 0.0 };
		panelFrameConfig.setLayout(gblpanelFrameConfig);

		final JLabel lblFirstFrame = new JLabel("Primer frame:");
		final GridBagConstraints gbc_lblFirstFrame = new GridBagConstraints();
		gbc_lblFirstFrame.anchor = GridBagConstraints.CENTER;
		gbc_lblFirstFrame.insets = new Insets(5, 5, 5, 5);
		gbc_lblFirstFrame.gridx = 0;
		gbc_lblFirstFrame.gridy = 0;
		panelFrameConfig.add(lblFirstFrame, gbc_lblFirstFrame);

		final JFormattedTextField tftFirst = new JFormattedTextField(NumberFormat.getIntegerInstance());
		tftFirst.setValue(Integer.valueOf(firstFrame));
		tftFirst.setColumns(5);
		final GridBagConstraints gbc_tftFirst = new GridBagConstraints();
		gbc_tftFirst.anchor = GridBagConstraints.WEST;
		gbc_tftFirst.insets = new Insets(5, 0, 5, 5);
		// gbc_tftFirst.fill = GridBagConstraints.HORIZONTAL;
		gbc_tftFirst.gridx = 1;
		gbc_tftFirst.gridy = 0;
		panelFrameConfig.add(tftFirst, gbc_tftFirst);

		final JLabel lbllastFrame = new JLabel("Ultimo frame:");
		final GridBagConstraints gbc_lbllastFrame = new GridBagConstraints();
		gbc_lbllastFrame.anchor = GridBagConstraints.CENTER;
		gbc_lbllastFrame.insets = new Insets(0, 5, 5, 5);
		gbc_lbllastFrame.gridx = 0;
		gbc_lbllastFrame.gridy = 1;
		panelFrameConfig.add(lbllastFrame, gbc_lbllastFrame);

		final JFormattedTextField tftLast = new JFormattedTextField(NumberFormat.getIntegerInstance());
		tftLast.setValue(Integer.valueOf(lastFrame));
		tftLast.setColumns(5);
		final GridBagConstraints gbc_tftLast = new GridBagConstraints();
		gbc_tftLast.anchor = GridBagConstraints.WEST;
		gbc_tftLast.insets = new Insets(0, 0, 5, 5);
		// gbc_tftLast.fill = GridBagConstraints.HORIZONTAL;
		gbc_tftLast.gridx = 1;
		gbc_tftLast.gridy = 1;
		panelFrameConfig.add(tftLast, gbc_tftLast);


        // String s = "Nota: FIUBAmate ignorara los cuerpos que comiencen antes del Primer Frame, o terminen luego del Ultimo Frame.";
        // String html = "<html><body style='width: 250px; text-align: center'>";
		// final JLabel lblnotaSobreFrames = new JLabel(html+s);
		// lblnotaSobreFrames.setFont(SMALL_FONT);
		// final GridBagConstraints gbclblnotaSobreFrames = new GridBagConstraints();
		// gbclblnotaSobreFrames.anchor = GridBagConstraints.CENTER;
		// gbclblnotaSobreFrames.insets = new Insets(0, 5, 5, 5);
		// gbclblnotaSobreFrames.gridx = 0;
		// gbclblnotaSobreFrames.gridy = 2;
		// panelFrameConfig.add(lblnotaSobreFrames, gbclblnotaSobreFrames);
		final FocusListener fl = new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						((JFormattedTextField) e.getSource()).selectAll();
					}
				});
			}
		};
		tftFirst.addFocusListener(fl);
		tftLast.addFocusListener(fl);

		tftFirst.addPropertyChangeListener("value", (e) -> this.firstFrame = ((Number) tftFirst.getValue()).intValue());
		tftLast.addPropertyChangeListener("value", (e) -> this.lastFrame = ((Number) tftLast.getValue()).intValue());

		Roi.addRoiListener(this);
	}

	public void exportToCsv(List<String[]> stats) {
		/*
		 * Recibo la lista de spots que cruzaron cada ROI y la exporto como csv.
		 */
		final File file = FileChooser.chooseFile(
				this,
				selectedFile,
				new FileNameExtensionFilter("CSV files", "csv"),
				"Export table to CSV",
				DialogType.SAVE,
				SelectionMode.FILES_ONLY);
		if (null == file)
			return;

		selectedFile = file.getAbsolutePath();

		// Se escribe el archivo elegido
		try (CSVWriter writer = new CSVWriter(new FileWriter(selectedFile),
				CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER,
				CSVWriter.DEFAULT_LINE_END)) {
			String[] header = { "ROI ID", "cantidad", "proporcion" };
			writer.writeNext(header);
			for (String[] stat : stats) {
				writer.writeNext(stat);
			}
		} catch (final IOException e) {
			model.getLogger().error("Problem exporting to file "
					+ selectedFile + "\n" + e.getMessage());
		}
	}

	@Override
	public void render() {
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void refresh() {
		repaint();
	}

	@Override
	public void centerViewOn(final Spot spot) {
		// spotTable.scrollToObject( spot );
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void clear() {
	}

	public void roiModified(ImagePlus img, int id) {
		switch (id) {
			case CREATED:
				btnAgregarArea.setEnabled(true);
				break;
			case DELETED:
				btnAgregarArea.setEnabled(false);
				break;
		}
		// IJ.log("ROI modified: " + (img != null ? img.getTitle() : "") + ", " + type);
	}

	private void onAgregarArea() {
		IJ.log("Se agrego un ROI");
		ImagePlus img = IJ.getImage();
		Roi roi = img.getRoi();
		if (roi == null) {
			IJ.log("ROI nulo :(\n");
			return;
		}

		IJ.log(roi.getBounds().toString());

		addedAreas.add(roi);
		lblAmountAreasAdded.setText("Cantidad de areas agregadas: " + addedAreas.size());
		btnExportarCSV.setEnabled(true);
	}

	private void onExportarCSV() {
		/*
		 * Se itera frame a frame, y se itera por cada spot en ese frame
		 * Si el centro de dicho spot esta en el ROI, se agrega el punto a una
		 * estructura de set
		 * y se calcula el porcentaje de spots que pasaron por el ROI
		 */
		List<String[]> stats = new ArrayList<String[]>();

		for (int i = 0; i < addedAreas.size(); i++) {
			stats.add(spotsInROI(addedAreas.get(i), i));
		}

		IJ.log("Exportando CSV");
		exportToCsv(stats);
	}

	private String[] spotsInROI(Roi roi, int roi_index) {
		/*
		 * Se itera frame a frame, y se itera por cada spot en ese frame
		 * Si el centro de dicho spot esta en el ROI, se agrega el punto a una
		 * estructura de set
		 * y se calcula el porcentaje de spots que pasaron por el ROI
		 */

		SpotCollection spotCollection = model.getSpots();
		Set<Integer> tracksInRoi = new HashSet<Integer>();

		IJ.log("Contando los spots en el ROI");

		// get trackIds from model
		TrackModel trackModel = model.getTrackModel();
		Set<Integer> trackIds = trackModel.trackIDs(false);
		int cantidadTracksValidos = 0; // es menor o igual que trackids.size()

		if (spotCollection == null) {
			IJ.log("No hay spots\n");
			return new String[] { String.valueOf(roi_index), "0", "0.0000" };
		}

		for (Integer trackId : trackIds) {
			// get spots from trackId
			List<Spot> sortedTrackSpots = Utils.sortTrackSpots(trackModel.trackSpots(trackId));

			IJ.log(trackId + " track id ============================");

			if (isIncompleteTrack(sortedTrackSpots)) {
				IJ.log("Track comienza o termina fuera de los limites. Ignorando...");
				continue;
			}

			cantidadTracksValidos++;

			for (Spot spot : sortedTrackSpots) {
				// get POSITION_X and POSITION_Y from the spot
				double x = spot.getFeature(Spot.POSITION_X);
				double y = spot.getFeature(Spot.POSITION_Y);
				// IJ.log("posicion T: " + String.valueOf(spot.getFeature(Spot.POSITION_T)));

				// Check if the point (x, y) is inside the ROI
				if (roi.containsPoint(x, y)) {
					tracksInRoi.add(trackId);
					break;
				}
			}
		}
		IJ.log("Tracks in ROI " + tracksInRoi.toString());
		IJ.log("Se encontraron " + cantidadTracksValidos + " tracks validos ('completos')");
		float proportion = (float) tracksInRoi.size() / cantidadTracksValidos;

		return new String[] {
				String.valueOf(roi_index),
				String.valueOf(tracksInRoi.size()),
				String.format("%.4f", proportion),
		};
	}

	private boolean isIncompleteTrack(List<Spot> sortedTrackSpots) {
		double firstSpotFrame = sortedTrackSpots.get(0).getFeature(Spot.POSITION_T);
		double lastSpotFrame = sortedTrackSpots.get(sortedTrackSpots.size() - 1).getFeature(Spot.POSITION_T);
		// IJ.log("frames: " + firstSpotFrame + ", " + this.firstFrame + ", " + this.lastFrame + ", " + lastSpotFrame);
		return firstSpotFrame < this.firstFrame || this.lastFrame < lastSpotFrame;
	}
}
