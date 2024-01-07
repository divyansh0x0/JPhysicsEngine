package material.theme.enums;

import java.io.Serializable;

public enum ElevationDP implements Serializable {
    _0,
    _1,
    _2,
    _3,
    _4,
    _6,
    _8,
    _12,
    _16,
    _24;
    public static ElevationDP get(int index){
        return values()[index];
    }
}
