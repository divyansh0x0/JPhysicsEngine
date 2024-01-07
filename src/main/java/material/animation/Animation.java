package material.animation;

import java.util.ArrayList;

public class Animation {
    public interface AnimationCompletedListener{
        void onComplete();
    }
    public interface ComponentPropertyChangedListener {
        void propertyChanged();
    }
    private final ArrayList<AnimationCompletedListener> _animationCompletedListeners = new ArrayList<>();
    private final ArrayList<ComponentPropertyChangedListener> _propertyChangedListeners = new ArrayList<>();

    public void onCompleted(AnimationCompletedListener l) {
        _animationCompletedListeners.add(l);
    }
    protected void animationCompletedCallback(){
        for(AnimationCompletedListener l : _animationCompletedListeners)
            l.onComplete();
    }

    public void onPropertyChanged(ComponentPropertyChangedListener l){
        _propertyChangedListeners.add(l);
    }
    protected void propertyChangedCallback(){
        for(ComponentPropertyChangedListener l : _propertyChangedListeners)
            l.propertyChanged();
    }
}
