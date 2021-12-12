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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
import ij.gui.Roi;

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

	private JLabel lblAmountAreasAdded;

	public FIUBAmateView(final Model model, final SelectionModel selectionModel) {
		super("FIUBAmate");
		IJ.log("Inicio!");
		IJ.log("Cantidad de Tracks distintos en el modelo: " + model.getTrackModel().trackIDs(false).size());
		setIconImage(TRACKMATE_ICON.getImage());
		this.model = model;

		/*
		 * GUI.
		 */

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(300, 521));
		this.setSize(300, 400);

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

		/*
		 * Settings editor.
		 */

		final JButton btnEditSettings = new JButton("Ver creditos", LOG_ICON);
		// btnEditSettings.addActionListener( e -> editor.setVisible(
		// !editor.isVisible() ) );

		final GridBagConstraints gbcBtnEditSettings = new GridBagConstraints();
		gbcBtnEditSettings.fill = GridBagConstraints.NONE;
		gbcBtnEditSettings.insets = new Insets(5, 5, 5, 5);
		gbcBtnEditSettings.anchor = GridBagConstraints.EAST;
		gbcBtnEditSettings.gridx = 1;
		gbcBtnEditSettings.gridy = 0;
		add(btnEditSettings, gbcBtnEditSettings);

		final JLabel lblContarCuerpos = new JLabel("Contabilizar cuerpos en Area");
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

		if (addedAreas.size() > 0) {
			btnExportarCSV.setEnabled(true);
		}
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

		// int nFrames = IJ.getImage().getNFrames();
		SpotCollection spotCollection = model.getSpots();
		Set<Integer> tracksInRoi = new HashSet<Integer>();

		IJ.log("Contando los spots en el ROI");

		// get trackIds from model
		TrackModel trackModel = model.getTrackModel();
		Set<Integer> trackIds = trackModel.trackIDs(false);

		if (spotCollection == null) {
			IJ.log("No hay spots\n");
			return new String[] { String.valueOf(roi_index), "0", "0.0000" };
		}

		for (Integer trackId : trackIds) {
			// get spots from trackId
			for (Spot spot : trackModel.trackSpots(trackId)) {
				// get POSITION_X and POSITION_Y from the spot
				double x = spot.getFeature(Spot.POSITION_X);
				double y = spot.getFeature(Spot.POSITION_Y);

				// Check if the point (x, y) is inside the ROI
				if (roi.containsPoint(x, y)) {
					tracksInRoi.add(trackId);
					break;
				}
			}
		}
		IJ.log("Tracks in ROI " + tracksInRoi.toString());
		float proportion = (float) tracksInRoi.size() / trackIds.size();

		return new String[] {
				String.valueOf(roi_index),
				String.valueOf(tracksInRoi.size()),
				String.format("%.4f", proportion),
		};
	}
}
