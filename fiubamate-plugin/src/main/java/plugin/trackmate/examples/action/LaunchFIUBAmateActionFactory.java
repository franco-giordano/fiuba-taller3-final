package plugin.trackmate.examples.action;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import static fiji.plugin.trackmate.gui.Icons.CALCULATOR_ICON;

import fiji.plugin.trackmate.TrackMatePlugIn;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import ij.ImageJ;
import ij.ImagePlus;

@Plugin(type = TrackMateActionFactory.class)
public class LaunchFIUBAmateActionFactory implements TrackMateActionFactory {

	private static final String INFO_TEXT = "<html>Esta accion lanza FIUBAmate, un sistema integrado a ImageJ pensado para obtener estadisticas sobre distintos canales y cuerpos en una experiencia dada.</html>";

	private static final String KEY = "LAUNCH_FIUBAMATE";

	private static final String NAME = "Lanzar FIUBAmate";

	@Override
	public String getInfoText() {
		return INFO_TEXT;
	}

	@Override
	public ImageIcon getIcon() {
		return CALCULATOR_ICON;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TrackMateAction create() {
		return new LaunchFIUBAmateAction();
	}

	public static void main(final String[] args) {
		ImageJ.main(args);
		new ImagePlus("samples/FakeTracks.tif").show();
		new TrackMatePlugIn().run("");
	}
}
