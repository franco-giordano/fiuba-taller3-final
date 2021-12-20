package plugin.trackmate.examples;
public class SpotEntradaSalida implements Comparable<SpotEntradaSalida> {
    private int track_id;
    private int frame_inicio;
    private int frame_fin;

    public SpotEntradaSalida(int track_id) {
        this.track_id = track_id;
        this.frame_inicio = -1;
        this.frame_fin = -1;
    }

    public SpotEntradaSalida(int track_id, int frame_inicio, int frame_fin) {
        this.track_id = track_id;
        this.frame_inicio = frame_inicio;
        this.frame_fin = frame_fin;
    }

    public int getTrackID() {
        return track_id;
    }

    public int getFrameInicio() {
        return frame_inicio;
    }

    public int getFrameFin() {
        return frame_fin;
    }

    public void updateFrames(int frame_act) {
        if(frame_inicio == -1 && frame_fin == -1) {
            frame_inicio = frame_act;
            frame_fin = frame_act;
            return;
        }
        frame_inicio = Math.min(frame_inicio, frame_act);
        frame_fin = Math.max(frame_fin, frame_act);
    }

    public boolean valido() {
        return frame_inicio != -1 && frame_fin != -1;
    }

    // Sort by frame_inicio, then by frame_fin then by track_id
    @Override
    public int compareTo(SpotEntradaSalida o) {
        if (this.frame_inicio != o.frame_inicio) {
            return this.frame_inicio - o.frame_inicio;
        }
        if(this.frame_fin != o.frame_fin) {
            return this.frame_fin - o.frame_fin;
        }            
        return this.track_id - o.track_id;
    }
}
