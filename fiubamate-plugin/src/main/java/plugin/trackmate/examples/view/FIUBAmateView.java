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
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.opencsv.CSVWriter;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Spot;
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

	// private final TablePanel< Spot > spotTable;

	// private final AtomicBoolean ignoreSelectionChange = new AtomicBoolean(false);

	// private final SelectionModel selectionModel;

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
		// this.selectionModel = selectionModel;

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

		/*
		 * Display spot checkbox.
		 */

		// final JCheckBox chkboxDisplaySpots = new JCheckBox();
		// chkboxDisplaySpots.setText("Display spots");
		// chkboxDisplaySpots.setFont(FONT);
		// final GridBagConstraints gbcCheckBoxDisplaySpots = new GridBagConstraints();
		// gbcCheckBoxDisplaySpots.anchor = GridBagConstraints.NORTH;
		// gbcCheckBoxDisplaySpots.fill = GridBagConstraints.HORIZONTAL;
		// gbcCheckBoxDisplaySpots.insets = new Insets(0, 5, 0, 5);
		// gbcCheckBoxDisplaySpots.gridx = 0;
		// gbcCheckBoxDisplaySpots.gridy = 1;
		// add(chkboxDisplaySpots, gbcCheckBoxDisplaySpots);

		final JLabel lblContarCuerpos = new JLabel("Contabilizar cuerpos en Area");
		lblContarCuerpos.setFont(FONT);
		final GridBagConstraints gbcLblContarCuerpos = new GridBagConstraints();
		gbcLblContarCuerpos.anchor = GridBagConstraints.NORTH;
		gbcLblContarCuerpos.fill = GridBagConstraints.HORIZONTAL;
		gbcLblContarCuerpos.insets = new Insets(0, 5, 0, 5);
		gbcLblContarCuerpos.gridx = 0;
		gbcLblContarCuerpos.gridy = 1;
		add(lblContarCuerpos, gbcLblContarCuerpos);

		// final JCheckBox chkboxDisplaySpotsAsRois = new JCheckBox();
		// chkboxDisplaySpotsAsRois.setText("as ROIs");
		// final GridBagConstraints gbcChkboxDisplaySpotsAsRois = new
		// GridBagConstraints();
		// gbcChkboxDisplaySpotsAsRois.insets = new Insets(0, 0, 0, 5);
		// gbcChkboxDisplaySpotsAsRois.anchor = GridBagConstraints.EAST;
		// gbcChkboxDisplaySpotsAsRois.gridx = 1;
		// gbcChkboxDisplaySpotsAsRois.gridy = 1;
		// add(chkboxDisplaySpotsAsRois, gbcChkboxDisplaySpotsAsRois);
		// chkboxDisplaySpotsAsRois.setFont(FONT);
		// chkboxDisplaySpotsAsRois
		// .addActionListener(e ->
		// ds.setSpotDisplayedAsRoi(chkboxDisplaySpotsAsRois.isSelected()));
		// chkboxDisplaySpotsAsRois.setSelected(ds.isSpotDisplayedAsRoi());

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

		// final JLabel lblSpotRadius = new JLabel("Spot display radius ratio:");
		// lblSpotRadius.setFont(SMALL_FONT);
		// final GridBagConstraints gbcLblSpotRadius = new GridBagConstraints();
		// gbcLblSpotRadius.anchor = GridBagConstraints.EAST;
		// gbcLblSpotRadius.insets = new Insets(5, 5, 0, 5);
		// gbcLblSpotRadius.gridx = 0;
		// gbcLblSpotRadius.gridy = 0;
		// panelSpotOptions.add(lblSpotRadius, gbcLblSpotRadius);

		btnAgregarArea = new JButton("Agregar Area", ADD_ICON);
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

		// final JFormattedTextField ftfSpotRadius = new JFormattedTextField();
		// GuiUtils.selectAllOnFocus(ftfSpotRadius);
		// ftfSpotRadius.setHorizontalAlignment(SwingConstants.CENTER);
		// ftfSpotRadius.setFont(SMALL_FONT);
		// ftfSpotRadius.setMinimumSize(new Dimension(80, 20));
		// ftfSpotRadius.setColumns(5);
		// final GridBagConstraints gbcFtfSpotRadius = new GridBagConstraints();
		// gbcFtfSpotRadius.insets = new Insets(5, 0, 0, 0);
		// gbcFtfSpotRadius.anchor = GridBagConstraints.WEST;
		// gbcFtfSpotRadius.gridx = 1;
		// gbcFtfSpotRadius.gridy = 0;
		// panelSpotOptions.add(ftfSpotRadius, gbcFtfSpotRadius);

		lblAmountAreasAdded = new JLabel("Cantidad de areas agregadas: " + addedAreas.size());
		lblAmountAreasAdded.setFont(SMALL_FONT);
		final GridBagConstraints gbclblAmountAreasAdded = new GridBagConstraints();
		gbclblAmountAreasAdded.anchor = GridBagConstraints.CENTER;
		gbclblAmountAreasAdded.insets = new Insets(2, 5, 5, 5);
		gbclblAmountAreasAdded.gridx = 0;
		gbclblAmountAreasAdded.gridy = 2;
		panelSpotOptions.add(lblAmountAreasAdded, gbclblAmountAreasAdded);

		// final JCheckBox chkboxSpotNames = new JCheckBox();
		// final GridBagConstraints gbcChkboxSpotNames = new GridBagConstraints();
		// gbcChkboxSpotNames.anchor = GridBagConstraints.WEST;
		// gbcChkboxSpotNames.gridx = 1;
		// gbcChkboxSpotNames.gridy = 1;
		// panelSpotOptions.add(chkboxSpotNames, gbcChkboxSpotNames);

		// final JPanel selectorForSpots = featureSelector.createSelectorForSpots();
		// final GridBagConstraints gbcCmbboxSpotColor = new GridBagConstraints();
		// gbcCmbboxSpotColor.insets = new Insets( 0, 5, 5, 5 );
		// gbcCmbboxSpotColor.fill = GridBagConstraints.BOTH;
		// gbcCmbboxSpotColor.gridwidth = 2;
		// gbcCmbboxSpotColor.gridx = 0;
		// gbcCmbboxSpotColor.gridy = 3;
		// panelSpotOptions.add( selectorForSpots, gbcCmbboxSpotColor );

		// final UpdateListener refresher = () -> refresh();
		// ds.listeners().add(refresher);
		// selectionModel.addSelectionChangeListener(this);
		// model.addModelChangeListener(this);
		// addWindowListener(new WindowAdapter() {
		// 	@Override
		// 	public void windowClosing(final java.awt.event.WindowEvent e) {
		// 		// selectionModel.removeSelectionChangeListener(FIUBAmateView.this);
		// 		// model.removeModelChangeListener(FIUBAmateView.this);
		// 		ds.listeners().remove(refresher);
		// 	};
		// });
		Roi.addRoiListener(this);
	}

	public void exportToCsv() {
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
		exportToCsv(selectedFile);
	}

	public void exportToCsv(final String csvFile) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile),
				CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER,
				CSVWriter.DEFAULT_LINE_END)) {
			String[] header = {"trackID", "cantidad"};
			writer.writeNext(header);
			writer.writeNext(new String[] { "Primer csv :)" });
		} catch (final IOException e) {
			model.getLogger().error("Problem exporting to file "
					+ csvFile + "\n" + e.getMessage());
		}
	}

	// public static final TablePanel< Spot > createSpotTable( final Model model,
	// final DisplaySettings ds )
	// {
	// final List< String > features = new ArrayList<>(
	// model.getFeatureModel().getSpotFeatures() );
	// final Map< String, String > featureNames =
	// model.getFeatureModel().getSpotFeatureNames();
	// final Map< String, String > featureShortNames =
	// model.getFeatureModel().getSpotFeatureShortNames();
	// final Map< String, String > featureUnits = new HashMap<>();
	// for ( final String feature : features )
	// {
	// final Dimension dimension =
	// model.getFeatureModel().getSpotFeatureDimensions().get( feature );
	// final String units = TMUtils.getUnitsFor( dimension, model.getSpaceUnits(),
	// model.getTimeUnits() );
	// featureUnits.put( feature, units );
	// }
	// final Map< String, Boolean > isInts =
	// model.getFeatureModel().getSpotFeatureIsInt();
	// final Map< String, String > infoTexts = new HashMap<>();
	// final Function< Spot, String > labelGenerator = spot -> spot.getName();
	// final BiConsumer< Spot, String > labelSetter = ( spot, label ) ->
	// spot.setName( label );

	// /*
	// * Feature provider. We add a fake one to show the spot ID.
	// */
	// final String SPOT_ID = "ID";
	// features.add( 0, SPOT_ID );
	// featureNames.put( SPOT_ID, "Spot ID" );
	// featureShortNames.put( SPOT_ID, "Spot ID" );
	// featureUnits.put( SPOT_ID, "" );
	// isInts.put( SPOT_ID, Boolean.TRUE );
	// infoTexts.put( SPOT_ID, "The id of the spot." );

	// /*
	// * Feature provider. We add a fake one to show the spot *track* ID.
	// */
	// final String TRACK_ID = "TRACK_ID";
	// features.add( 1, TRACK_ID );
	// featureNames.put( TRACK_ID, "Track ID" );
	// featureShortNames.put( TRACK_ID, "Track ID" );
	// featureUnits.put( TRACK_ID, "" );
	// isInts.put( TRACK_ID, Boolean.TRUE );
	// infoTexts.put( TRACK_ID, "The id of the track this spot belongs to." );

	// final BiFunction< Spot, String, Double > featureFun = ( spot, feature ) -> {
	// if ( feature.equals( TRACK_ID ) )
	// {
	// final Integer trackID = model.getTrackModel().trackIDOf( spot );
	// return trackID == null ? null : trackID.doubleValue();
	// }
	// else if ( feature.equals( SPOT_ID ) )
	// return ( double ) spot.ID();

	// return spot.getFeature( feature );
	// };

	// final Supplier< FeatureColorGenerator< Spot > > coloring =
	// () -> FeatureUtils.createSpotColorGenerator( model, ds );

	// final BiConsumer< Spot, Color > colorSetter =
	// ( spot, color ) -> spot.putFeature( ManualSpotColorAnalyzerFactory.FEATURE,
	// Double.valueOf( color.getRGB() ) );

	// final TablePanel< Spot > table =
	// new TablePanel<>(
	// model.getSpots().iterable( true ),
	// features,
	// featureFun,
	// featureNames,
	// featureShortNames,
	// featureUnits,
	// isInts,
	// infoTexts,
	// coloring,
	// labelGenerator,
	// labelSetter,
	// ManualSpotColorAnalyzerFactory.FEATURE,
	// colorSetter );
	// return table;
	// }

	@Override
	public void render() {
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void refresh() {
		repaint();
	}

	// @Override
	// public void modelChanged(final ModelChangeEvent event) {
	// 	if (event.getEventID() == ModelChangeEvent.FEATURES_COMPUTED) {
	// 		refresh();
	// 		return;
	// 	}

	// 	final List<Spot> spots = new ArrayList<>();
	// 	for (final Spot spot : model.getSpots().iterable(true))
	// 		spots.add(spot);
	// 	// spotTable.setObjects( spots );

	// 	refresh();
	// }

	/*
	 * Forward selection model changes to the tables.
	 */
	// @Override
	// public void selectionChanged(final SelectionChangeEvent event) {
	// 	if (ignoreSelectionChange.get())
	// 		return;
	// 	ignoreSelectionChange.set(true);

	// 	// Vertices table.
	// 	final Set<Spot> selectedVertices = selectionModel.getSpotSelection();
	// 	// final JTable vt = spotTable.getTable();
	// 	// vt.getSelectionModel().clearSelection();
	// 	// for ( final Spot spot : selectedVertices )
	// 	// {
	// 	// final int row = spotTable.getViewRowForObject( spot );
	// 	// vt.getSelectionModel().addSelectionInterval( row, row );
	// 	// }

	// 	// Center on selection if we added one spot exactly
	// 	final Map<Spot, Boolean> spotsAdded = event.getSpots();
	// 	if (spotsAdded != null && spotsAdded.size() == 1) {
	// 		final boolean added = spotsAdded.values().iterator().next();
	// 		if (added) {
	// 			final Spot spot = spotsAdded.keySet().iterator().next();
	// 			centerViewOn(spot);
	// 		}
	// 	}

	// 	refresh();
	// 	ignoreSelectionChange.set(false);
	// }

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
		IJ.log("apretaron agregar area");
		ImagePlus img = IJ.getImage();
		Roi roi = img.getRoi();
		if (roi == null) {
			IJ.log("ROI nulo :(\n");
			return;
		}

		// ambos printean null:
		// IJ.log("DEBUG INFO: " + roi.getDebugInfo() + "\n");
		// IJ.log("PROPS: " + roi.getProperties() + "\n");

		// for (Point p : roi) {
		// // process p
		// IJ.log(p.toString());
		// }
		IJ.log(roi.getBounds().toString());

		addedAreas.add(roi);
		lblAmountAreasAdded.setText("Cantidad de areas agregadas: " + addedAreas.size());

		if (addedAreas.size() > 0) {
			btnExportarCSV.setEnabled(true);
		}
	}

	private void onExportarCSV() {
		IJ.log("Exportando CSV");
		exportToCsv();
	}

	/**
	 * Forward spot table selection to selection model.
	 */
	// private final class SpotTableSelectionListener implements
	// ListSelectionListener
	// {

	// @Override
	// public void valueChanged( final ListSelectionEvent event )
	// {
	// if ( event.getValueIsAdjusting() || ignoreSelectionChange.get() )
	// return;

	// ignoreSelectionChange.set( true );

	// final int[] selectedRows = spotTable.getTable().getSelectedRows();
	// final List< Spot > toSelect = new ArrayList<>( selectedRows.length );
	// for ( final int row : selectedRows )
	// toSelect.add( spotTable.getObjectForViewRow( row ) );

	// selectionModel.clearSelection();
	// selectionModel.addSpotToSelection( toSelect );
	// refresh();

	// ignoreSelectionChange.set( false );
	// }
	// }
}
