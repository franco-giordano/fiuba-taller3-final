package plugin.trackmate.examples.view;

import static fiji.plugin.trackmate.gui.Icons.TRACKMATE_ICON;
import static fiji.plugin.trackmate.gui.Icons.ADD_ICON;
import static fiji.plugin.trackmate.gui.Icons.CSV_ICON;
import static fiji.plugin.trackmate.gui.Icons.LOG_ICON;
import static fiji.plugin.trackmate.gui.Icons.PLOT_ICON;
import static fiji.plugin.trackmate.gui.Icons.REMOVE_ICON;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

import ij.IJ;
import ij.ImagePlus;
import ij.gui.RoiListener;
import plugin.trackmate.examples.Utils;
import plugin.trackmate.examples.SpotIO;
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

	public static String csvSelectedFile = System.getProperty("user.home") + File.separator + "body_proportion.csv";
	public static String csvTemporalDistSelectedFile = System.getProperty("user.home") + File.separator
			+ "body_temporal_dist.csv";

	private final Model model;

	private final JButton btnAddRegion;
	private final JButton btnRemoveRegion;
	private final JButton btnExportCSV;
	private final JButton btnExportTemporalDistCSV;

	private List<Roi> addedRegions = new ArrayList<Roi>();

	private int firstFrame;

	private int lastFrame;

	private JLabel lblAmountRegionsAdded;

	public FIUBAmateView(final Model model, final SelectionModel selectionModel) {
		super("FIUBAmate");
		// IJ.log("Inicio!");
		// IJ.log("Cantidad de Tracks distintos en el modelo: " +
		// model.getTrackModel().trackIDs(false).size());
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
		 * ===========================================
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

		final JButton btnCredits = new JButton("Credits", LOG_ICON);
		btnCredits.addActionListener(e -> {
			final CreditsPanel panel = new CreditsPanel();
			JOptionPane.showMessageDialog(
					null,
					panel,
					"FIUBAmate - Credits",
					JOptionPane.INFORMATION_MESSAGE,
					TRACKMATE_ICON);
		});

		final GridBagConstraints gbcbtnCredits = new GridBagConstraints();
		gbcbtnCredits.fill = GridBagConstraints.NONE;
		gbcbtnCredits.insets = new Insets(5, 5, 5, 5);
		gbcbtnCredits.anchor = GridBagConstraints.EAST;
		gbcbtnCredits.gridx = 1;
		gbcbtnCredits.gridy = 0;
		add(btnCredits, gbcbtnCredits);

		/*
		 * ===========================================
		 * Panel to add region
		 */

		final JLabel lblCountBodies = new JLabel("Modify Regions of Interest");
		lblCountBodies.setFont(FONT);
		final GridBagConstraints gbcLblCountBodies = new GridBagConstraints();
		gbcLblCountBodies.anchor = GridBagConstraints.NORTH;
		gbcLblCountBodies.fill = GridBagConstraints.HORIZONTAL;
		gbcLblCountBodies.insets = new Insets(5, 5, 0, 5);
		gbcLblCountBodies.gridx = 0;
		gbcLblCountBodies.gridy = 1;
		add(lblCountBodies, gbcLblCountBodies);

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
		gblPanelSpotOptions.columnWeights = new double[] { 0.0, 0.0 };
		gblPanelSpotOptions.rowWeights = new double[] { 0.0, 0.0 };
		panelSpotOptions.setLayout(gblPanelSpotOptions);

		btnAddRegion = new JButton("Save Area", ADD_ICON);
		btnAddRegion.addActionListener(e -> onAddRegion());
		btnAddRegion.setEnabled(false);
		btnAddRegion.setToolTipText("Include selected area.");

		final GridBagConstraints gbcbtnAddArea = new GridBagConstraints();
		gbcbtnAddArea.anchor = GridBagConstraints.EAST;
		gbcbtnAddArea.insets = new Insets(5, 5, 2, 2);
		gbcbtnAddArea.gridx = 0;
		gbcbtnAddArea.gridy = 0;
		panelSpotOptions.add(btnAddRegion, gbcbtnAddArea);

		btnRemoveRegion = new JButton("Remove last region", REMOVE_ICON);
		btnRemoveRegion.addActionListener(e -> onRemoveRegion());
		btnRemoveRegion.setEnabled(false);
		btnRemoveRegion.setToolTipText("Quitar la ultima area recientemente guardada.");

		final GridBagConstraints gbcbtnRemoverArea = new GridBagConstraints();
		gbcbtnRemoverArea.anchor = GridBagConstraints.WEST;
		gbcbtnRemoverArea.insets = new Insets(5, 2, 2, 5);
		gbcbtnRemoverArea.gridx = 1;
		gbcbtnRemoverArea.gridy = 0;
		panelSpotOptions.add(btnRemoveRegion, gbcbtnRemoverArea);

		lblAmountRegionsAdded = new JLabel("# Added Regions: " + addedRegions.size());
		lblAmountRegionsAdded.setFont(SMALL_FONT);
		final GridBagConstraints gbclblAmountAreasAdded = new GridBagConstraints();
		gbclblAmountAreasAdded.anchor = GridBagConstraints.CENTER;
		gbclblAmountAreasAdded.insets = new Insets(0, 5, 5, 5);
		gbclblAmountAreasAdded.gridx = 0;
		gbclblAmountAreasAdded.gridy = 1;
		gbclblAmountAreasAdded.gridwidth = 2;
		panelSpotOptions.add(lblAmountRegionsAdded, gbclblAmountAreasAdded);

		/*
		 * ===========================================
		 * Set up frames
		 */

		final JLabel lblConfigFrames = new JLabel("Set up frames of interest");
		lblConfigFrames.setFont(FONT);
		final GridBagConstraints gbclblConfigFrames = new GridBagConstraints();
		gbclblConfigFrames.anchor = GridBagConstraints.NORTH;
		gbclblConfigFrames.fill = GridBagConstraints.HORIZONTAL;
		gbclblConfigFrames.insets = new Insets(5, 5, 0, 5);
		gbclblConfigFrames.gridx = 0;
		gbclblConfigFrames.gridy = 3;
		add(lblConfigFrames, gbclblConfigFrames);

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

		final JLabel lblFirstFrame = new JLabel("First frame:");
		final GridBagConstraints gbc_lblFirstFrame = new GridBagConstraints();
		gbc_lblFirstFrame.anchor = GridBagConstraints.EAST;
		gbc_lblFirstFrame.insets = new Insets(5, 5, 5, 5);
		gbc_lblFirstFrame.gridx = 0;
		gbc_lblFirstFrame.gridy = 0;
		panelFrameConfig.add(lblFirstFrame, gbc_lblFirstFrame);

		final JFormattedTextField tftFirst = new JFormattedTextField(NumberFormat.getIntegerInstance());
		tftFirst.setValue(Integer.valueOf(firstFrame));
		tftFirst.setColumns(15);
		final GridBagConstraints gbc_tftFirst = new GridBagConstraints();
		gbc_tftFirst.anchor = GridBagConstraints.WEST;
		gbc_tftFirst.insets = new Insets(5, 0, 5, 5);
		// gbc_tftFirst.fill = GridBagConstraints.HORIZONTAL;
		gbc_tftFirst.gridx = 1;
		gbc_tftFirst.gridy = 0;
		panelFrameConfig.add(tftFirst, gbc_tftFirst);

		final JLabel lbllastFrame = new JLabel("Last frame:");
		final GridBagConstraints gbc_lbllastFrame = new GridBagConstraints();
		gbc_lbllastFrame.anchor = GridBagConstraints.EAST;
		gbc_lbllastFrame.insets = new Insets(0, 5, 5, 5);
		gbc_lbllastFrame.gridx = 0;
		gbc_lbllastFrame.gridy = 1;
		panelFrameConfig.add(lbllastFrame, gbc_lbllastFrame);

		final JFormattedTextField tftLast = new JFormattedTextField(NumberFormat.getIntegerInstance());
		tftLast.setValue(Integer.valueOf(lastFrame));
		tftLast.setColumns(15);
		final GridBagConstraints gbc_tftLast = new GridBagConstraints();
		gbc_tftLast.anchor = GridBagConstraints.WEST;
		gbc_tftLast.insets = new Insets(0, 0, 5, 5);
		// gbc_tftLast.fill = GridBagConstraints.HORIZONTAL;
		gbc_tftLast.gridx = 1;
		gbc_tftLast.gridy = 1;
		panelFrameConfig.add(tftLast, gbc_tftLast);

		String s = "Note: FIUBAmate will ignore bodies that starts before 'first frame' or after 'last frame'.";
		String html = "<html><body style='width: 165px; text-align: center'>";
		final JLabel lblnotaSobreFrames = new JLabel(html + s);
		lblnotaSobreFrames.setFont(SMALL_FONT);
		final GridBagConstraints gbclblnotaSobreFrames = new GridBagConstraints();
		gbclblnotaSobreFrames.anchor = GridBagConstraints.CENTER;
		gbclblnotaSobreFrames.insets = new Insets(0, 5, 5, 5);
		gbclblnotaSobreFrames.gridx = 0;
		gbclblnotaSobreFrames.gridy = 2;
		gbclblnotaSobreFrames.gridwidth = 2;
		panelFrameConfig.add(lblnotaSobreFrames, gbclblnotaSobreFrames);

		/*
		 * ===========================================
		 * Export Regions Panel
		 */

		final JLabel lblExportStats = new JLabel("Export Regions stats");
		lblExportStats.setFont(FONT);
		final GridBagConstraints gbclblExportStats = new GridBagConstraints();
		gbclblExportStats.anchor = GridBagConstraints.NORTH;
		gbclblExportStats.fill = GridBagConstraints.HORIZONTAL;
		gbclblExportStats.insets = new Insets(5, 5, 0, 5);
		gbclblExportStats.gridx = 0;
		gbclblExportStats.gridy = 5;
		add(lblExportStats, gbclblExportStats);

		final JPanel panelExportStats = new JPanel();
		panelExportStats.setBorder(new LineBorder(BORDER_COLOR, 1, true));
		final GridBagConstraints gbcpanelExportStats = new GridBagConstraints();
		gbcpanelExportStats.gridwidth = 2;
		gbcpanelExportStats.insets = new Insets(0, 5, 5, 5);
		gbcpanelExportStats.fill = GridBagConstraints.BOTH;
		gbcpanelExportStats.gridx = 0;
		gbcpanelExportStats.gridy = 6;
		add(panelExportStats, gbcpanelExportStats);
		final GridBagLayout gblpanelExportStats = new GridBagLayout();
		gblpanelExportStats.columnWeights = new double[] { 0.0, 0.0 };
		gblpanelExportStats.rowWeights = new double[] { 0.0, 0.0 };
		panelExportStats.setLayout(gblpanelExportStats);

		btnExportCSV = new JButton("Export to CSV", CSV_ICON);
		btnExportCSV.addActionListener(e -> onExportCSV());
		btnExportCSV.setEnabled(false);
		btnExportCSV.setToolTipText("Generates CSV with columns: ROI ID,amount,proportion");

		final GridBagConstraints gbcbtnExportCSV = new GridBagConstraints();
		gbcbtnExportCSV.anchor = GridBagConstraints.EAST;
		gbcbtnExportCSV.insets = new Insets(5, 5, 5, 2);
		gbcbtnExportCSV.gridx = 0;
		gbcbtnExportCSV.gridy = 0;
		panelExportStats.add(btnExportCSV, gbcbtnExportCSV);

		btnExportTemporalDistCSV = new JButton("Export temporal dist to CSV", PLOT_ICON);
		btnExportTemporalDistCSV.addActionListener(e -> onExportTemporalDistCSV());
		btnExportTemporalDistCSV.setEnabled(false);
		btnExportTemporalDistCSV
				.setToolTipText("Generates CSV with columns: ROI ID,Frame In,Frame Out,Spot ID");

		final GridBagConstraints gbcbtnExportTemporalDistCSV = new GridBagConstraints();
		gbcbtnExportTemporalDistCSV.anchor = GridBagConstraints.WEST;
		gbcbtnExportTemporalDistCSV.insets = new Insets(5, 2, 5, 5);
		gbcbtnExportTemporalDistCSV.gridx = 1;
		gbcbtnExportTemporalDistCSV.gridy = 0;
		panelExportStats.add(btnExportTemporalDistCSV, gbcbtnExportTemporalDistCSV);

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

	public void exportToCsv(List<String[]> stats, String[] header, String selectedFile) {
		/*
		 * Receives spots that entered each ROI and exports them as CSV.
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

		// Write chosen file
		try (CSVWriter writer = new CSVWriter(new FileWriter(selectedFile),
				CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER,
				CSVWriter.DEFAULT_LINE_END)) {
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
				btnAddRegion.setEnabled(true);
				break;
			case DELETED:
				btnAddRegion.setEnabled(false);
				break;
		}
	}

	private void updateActionButtons() {
		int n_added_areas = addedRegions.size();

		lblAmountRegionsAdded.setText("# Areas added: " + n_added_areas);
		btnExportCSV.setEnabled(n_added_areas > 0);
		btnExportTemporalDistCSV.setEnabled(n_added_areas > 0);
		btnRemoveRegion.setEnabled(n_added_areas > 0);
	}

	private void onAddRegion() {
		// IJ.log("Se agrego un ROI");
		ImagePlus img = IJ.getImage();
		Roi roi = img.getRoi();
		if (roi == null) {
			// IJ.log("ROI nulo :(\n");
			return;
		}

		// IJ.log(roi.getBounds().toString());

		addedRegions.add(roi);

		updateActionButtons();
	}

	private void onRemoveRegion() {
		// IJ.log("Se removio el ultimo ROI");
		addedRegions.remove(addedRegions.size() - 1);
		updateActionButtons();
	}

	private void onExportCSV() {
		/*
		 * Iterate frame to frame, and the for each spot in that frame
		 * If spot's centre is in the ROI, it adds the spot to a set data structure
		 * and after the iteration we can calculate proportion of spots that entered the
		 * ROI
		 */
		List<String[]> stats = new ArrayList<String[]>();

		for (int i = 0; i < addedRegions.size(); i++) {
			stats.add(spotsInROI(addedRegions.get(i), i));
		}

		// IJ.log("Exportando CSV");
		String[] header = { "ROI ID", "amount", "proportion" };
		exportToCsv(stats, header, csvSelectedFile);
	}

	private void onExportTemporalDistCSV() {
		/*
		 * For each ROI, we iterate all the frames, and for each frame we iterate all
		 * spots
		 * If spot's center is in the ROI, we update in and out for that ROI and spot in
		 * particular
		 */
		List<String[]> stats = new ArrayList<String[]>();

		for (int i = 0; i < addedRegions.size(); i++) {
			ROI_IO(addedRegions.get(i), i, stats);
		}

		// IJ.log("Exportando CSV temporal");
		String[] header = { "ROI ID", "Frame In", "Frame Out", "Spot ID" };
		exportToCsv(stats, header, csvTemporalDistSelectedFile);
	}

	private String[] spotsInROI(Roi roi, int roi_index) {
		/*
		 * For each frame, and for each spot in that frame:
		 * If spot's center is in the ROI, we add the spot to a set structure
		 * and in the end calculate the spot proportion that entered that ROI
		 */

		SpotCollection spotCollection = model.getSpots();
		Set<Integer> tracksInRoi = new HashSet<Integer>();

		// IJ.log("Contando los spots en el ROI");

		// get trackIds from model
		TrackModel trackModel = model.getTrackModel();
		Set<Integer> trackIds = trackModel.trackIDs(false);
		int validTracks = 0; // es less that or equal than trackids.size()

		if (spotCollection == null) {
			// IJ.log("No spots\n");
			return new String[] { String.valueOf(roi_index), "0", "0.0000" };
		}

		for (Integer trackId : trackIds) {
			// get spots from trackId
			List<Spot> sortedTrackSpots = Utils.sortTrackSpots(trackModel.trackSpots(trackId));

			// IJ.log(trackId + " track id ============================");

			if (isIncompleteTrack(sortedTrackSpots)) {
				// IJ.log("Track comienza o termina fuera de los limites. Ignorando...");
				continue;
			}

			validTracks++;

			for (Spot spot : sortedTrackSpots) {
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
		// IJ.log("Tracks in ROI " + tracksInRoi.toString());
		// IJ.log("Se encontraron " + cantidadTracksValidos + " tracks validos
		// ('completos')");
		float proportion = (float) tracksInRoi.size() / validTracks;

		return new String[] {
				String.valueOf(roi_index),
				String.valueOf(tracksInRoi.size()),
				String.format(Locale.ENGLISH, "%.4f", proportion),
		};
	}

	private boolean isIncompleteTrack(List<Spot> sortedTrackSpots) {
		double firstSpotFrame = sortedTrackSpots.get(0).getFeature(Spot.POSITION_T);
		double lastSpotFrame = sortedTrackSpots.get(sortedTrackSpots.size() - 1).getFeature(Spot.POSITION_T);
		// IJ.log("frames: " + firstSpotFrame + ", " + this.firstFrame + ", " +
		// this.lastFrame + ", " + lastSpotFrame);
		return firstSpotFrame < this.firstFrame || this.lastFrame < lastSpotFrame;
	}

	private void ROI_IO(Roi roi, int roi_index, List<String[]> stats) {
		/*
		 * Iterate frame to frame, and the for each spot in that frame
		 * If spot's centre is in the ROI, it adds the spot to a set data structure
		 * and after the iteration we can calculate proportion of spots that entered the
		 * ROI
		 */

		SpotCollection spotCollection = model.getSpots();

		// IJ.log("Min y Max para cada spot en un ROI");

		// get trackIds from model
		TrackModel trackModel = model.getTrackModel();
		Set<Integer> trackIds = trackModel.trackIDs(false);
		int validTracks = 0; // is less or equal than trackids.size()

		if (spotCollection == null) {
			// IJ.log("No spots\n");
			stats.add(new String[] { String.valueOf(roi_index), "NaN", "NaN" });
			return;
		}

		ArrayList<SpotIO> spotsInRoi = new ArrayList<SpotIO>();

		for (Integer trackId : trackIds) {
			// get spots from trackId
			List<Spot> sortedTrackSpots = Utils.sortTrackSpots(trackModel.trackSpots(trackId));

			// IJ.log(trackId + "tiempos: track id ============================");

			if (isIncompleteTrack(sortedTrackSpots)) {
				// IJ.log("Track comienza o termina fuera de los limites. Ignorando...");
				continue;
			}

			SpotIO spotIO = new SpotIO(trackId);

			validTracks++;

			for (Spot spot : sortedTrackSpots) {
				// get POSITION_X and POSITION_Y from the spot
				double x = spot.getFeature(Spot.POSITION_X);
				double y = spot.getFeature(Spot.POSITION_Y);
				int frame_act = spot.getFeature(Spot.FRAME).intValue();

				// Check if the point (x, y) is inside the ROI
				if (roi.containsPoint(x, y)) {
					spotIO.updateFrames(frame_act);
				}
			}
			spotsInRoi.add(spotIO);

		}
		// IJ.log("Tracks in ROI " + spotsInRoi.toString());
		// IJ.log("Se encontraron " + cantidadTracksValidos + " tracks validos
		// ('completos')");

		Collections.sort(spotsInRoi);

		for (SpotIO spot : spotsInRoi) {
			if (!spot.valid())
				continue;
			stats.add(new String[] {
					String.valueOf(roi_index),
					String.valueOf(spot.getTrackID()),
					String.valueOf(spot.getFrameIn()),
					String.valueOf(spot.getFrameOut())
			});
		}
	}

}
