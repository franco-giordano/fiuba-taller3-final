package plugin.trackmate.examples.view;

import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.ModelChangeEvent;
import fiji.plugin.trackmate.ModelChangeListener;
import fiji.plugin.trackmate.SelectionChangeEvent;
import fiji.plugin.trackmate.SelectionChangeListener;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import ij.IJ;
import ij.gui.RoiListener;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.Point;


public class EventLoggerView implements TrackMateModelView, ModelChangeListener, SelectionChangeListener
{

	private final Model model;

	private final SelectionModel selectionModel;

	public EventLoggerView( final Model model, final SelectionModel selectionModel )
	{
		/*
		 * The constructor: We just have been instantiated, but we do nothing
		 * yet. We wait to be #render()ed.
		 */
		this.model = model;
		this.selectionModel = selectionModel;

		IJ.log("Bienvenido a FIUBAmate");
		ImagePlus imp = IJ.getImage();
		IJ.log(imp.getInfoProperty());
		IJ.log(imp.getTitle());

		Roi roi = imp.getRoi();

		if (roi == null) {
			IJ.log("ROI nulo :(\n");
			return;
		}
		IJ.log("DEBUG INFO: " + roi.getDebugInfo() + "\n");
		IJ.log("PROPS: " + roi.getProperties() + "\n");
		
		for (Point p : roi) {
			// process p
			IJ.log(p.toString());
		}
	}

	@Override
	public void render()
	{
		/*
		 * When this view is "rendered", it just registers itself as a listener
		 * for model and selection changes (if it is not already registered).
		 * That's all.
		 */

		if ( !model.getModelChangeListener().contains( this ) )
		{
			model.addModelChangeListener( this );
		}

		if ( !selectionModel.getSelectionChangeListener().contains( this ) )
		{
			selectionModel.addSelectionChangeListener( this );
		}
	}

	@Override
	public void refresh()
	{
		// Ignored.
	}

	@Override
	public void clear()
	{
		// Clear the log window using a log-specific command.
		IJ.log( "\\Clear" );
	}

	@Override
	public void centerViewOn( final Spot spot )
	{
		// Ignored.
	}

	@Override
	public Model getModel()
	{
		// Ignored.
		return null;
	}

	@Override
	public String getKey()
	{
		// This MUST be the same key that for the facory of this view.
		return "EVENT_LOGGER";
	}

	/*
	 * MODELCHANGELISTENER
	 */

	@Override
	public void modelChanged( final ModelChangeEvent event )
	{
		/*
		 * Instead of crafting a message for each event type, we can reuse the
		 * ModelChangeEvent#toString() method:
		 */

		IJ.log( event.toString() );
	}

	/*
	 * SELECTIONCHANGELISTENER
	 */

	@Override
	public void selectionChanged( final SelectionChangeEvent event )
	{
		/*
		 * Here we build a string representation of the selection change event,
		 * manually.
		 */

		final StringBuilder str = new StringBuilder();
		str.append( "Selection change event from " + event.getSource() + ":\n" );

		final Map< Spot, Boolean > spotChanged = event.getSpots();
		// Careful: can be null if there is no spot in the change.
		if ( null != spotChanged )
		{
			for ( final Spot spot : spotChanged.keySet() )
			{
				if ( spotChanged.get( spot ).booleanValue() )
				{
					str.append( "\tSpot " + spot.getName() + " was added to selection.\n" );
				}
				else
				{
					str.append( "\tSpot " + spot.getName() + " was removed from selection.\n" );
				}
			}
		}

		final Map< DefaultWeightedEdge, Boolean > edgeChanged = event.getEdges();
		if ( null != edgeChanged )
		{
			for ( final DefaultWeightedEdge edge : edgeChanged.keySet() )
			{
				if ( edgeChanged.get( edge ).booleanValue() )
				{
					str.append( "\tEdge " + edge + " was added to selection.\n" );
				}
				else
				{
					str.append( "\tEdge " + edge + " was removed from selection.\n" );
				}
			}
		}
		str.append( "There are currently " );
		if ( selectionModel.getSpotSelection().size() == 0 )
		{
			str.append( "no spots and " );
		}
		else if ( selectionModel.getSpotSelection().size() == 1 )
		{
			str.append( "one spot and " );
		}
		else
		{
			str.append( selectionModel.getSpotSelection().size() + " spots and " );
		}

		if ( selectionModel.getEdgeSelection().size() == 0 )
		{
			str.append( "no edges " );
		}
		else if ( selectionModel.getEdgeSelection().size() == 1 )
		{
			str.append( "one edge " );
		}
		else
		{
			str.append( selectionModel.getEdgeSelection().size() + " edges " );
		}
		str.append( "in the selection.\n" );

		IJ.log( str.toString() );
	}

	// @Override
	// public void roiModified​(ImagePlus imp, int id) {
	// 	IJ.log("Cambio el ROI! \n");
	// }
}
