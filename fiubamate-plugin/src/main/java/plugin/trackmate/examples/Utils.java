package plugin.trackmate.examples;

import fiji.plugin.trackmate.Spot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Utils {
    public static List<Spot> sortTrackSpots(Set<Spot> spots) {
        /*
         * We only need minT and maxT, sort the spots in not entirely needed.
         */
        Spot[] array = spots.toArray(new Spot[0]);
        List<Spot> list = Arrays.asList(array);
        Collections.sort(
                list,
                (o1, o2) -> {
                    return (int) (((Spot) o1).getFeature(Spot.POSITION_T) - ((Spot) o2).getFeature(Spot.POSITION_T));
                });
        return list;
    }
}
