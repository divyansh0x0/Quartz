package material.theme.enums;

import java.io.Serializable;

public enum Elevation implements Serializable {
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
    public static Elevation get(int index){
        return values()[index];
    }
}
