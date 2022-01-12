package plugin.trackmate.examples;

public class SpotIO implements Comparable<SpotIO> {
    private int track_id;
    private int frame_in;
    private int frame_out;

    public SpotIO(int track_id) {
        this.track_id = track_id;
        this.frame_in = -1;
        this.frame_out = -1;
    }

    public SpotIO(int track_id, int frame_in, int frame_out) {
        this.track_id = track_id;
        this.frame_in = frame_in;
        this.frame_out = frame_out;
    }

    public int getTrackID() {
        return track_id;
    }

    public int getFrameIn() {
        return frame_in;
    }

    public int getFrameOut() {
        return frame_out;
    }

    public void updateFrames(int frame_act) {
        if (frame_in == -1 && frame_out == -1) {
            frame_in = frame_act;
            frame_out = frame_act;
            return;
        }
        frame_in = Math.min(frame_in, frame_act);
        frame_out = Math.max(frame_out, frame_act);
    }

    public boolean valid() {
        return frame_in != -1 && frame_out != -1;
    }

    // Sort by frame_in, then by frame_out then by track_id
    @Override
    public int compareTo(SpotIO o) {
        if (this.frame_in != o.frame_in) {
            return this.frame_in - o.frame_in;
        }
        if (this.frame_out != o.frame_out) {
            return this.frame_out - o.frame_out;
        }
        return this.track_id - o.track_id;
    }
}
