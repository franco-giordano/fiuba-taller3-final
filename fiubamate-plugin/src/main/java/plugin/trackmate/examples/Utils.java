package plugin.trackmate.examples;

import fiji.plugin.trackmate.Spot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Utils {
    public static List<Spot> sortTrackSpots(Set<Spot> spots) {
        /* 
            Pareceria absurdo convertir entre 20 tipos
            distintos solo para ordenarlo, pero bueno,
            por eso odiamos java no?
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
