package net.brakingpoint.trackInfo;

public class TrackMarker {
    private float triggerDistance;
    private String audioClipName;
    private String description;
    private boolean triggeredThisLap;

    public TrackMarker(float dist, String clip, String desc) {
        this.triggerDistance = dist;
        this.audioClipName = clip;
        this.description = desc;
        this.triggeredThisLap = false;
    }

    // Getters and Setters...
    public boolean checkTrigger(float currentDist) {
        if (!triggeredThisLap && currentDist >= triggerDistance) {
            triggeredThisLap = true;
            return true;
        }
        return false;
    }
    
    public void reset() {
        this.triggeredThisLap = false;
    }
}
