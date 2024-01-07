package material.animation;

public interface ColorAnimationModel {
    float getProgress();
    float getDurationInMs();
    void forceCompleteAnimation();
    void prepareForNewAnimation();
    void incrementAnimationTime(float delta);
}
