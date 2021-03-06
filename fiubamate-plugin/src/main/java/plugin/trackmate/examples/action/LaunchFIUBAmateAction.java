package plugin.trackmate.examples.action;

import java.awt.Frame;

import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.gui.displaysettings.DisplaySettings;
import plugin.trackmate.examples.view.FIUBAmateView;

public class LaunchFIUBAmateAction implements TrackMateAction {

	private Logger logger;

	@Override
	public void execute(final TrackMate trackmate, final SelectionModel selectionModel,
			final DisplaySettings displaySettings, final Frame parent) {
		logger.log("Starting FIUBAmate...");

		final FIUBAmateView view = new FIUBAmateView(trackmate.getModel(), selectionModel);
		view.render();
		logger.log(" Done!\n");
	}

	@Override
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}
}
