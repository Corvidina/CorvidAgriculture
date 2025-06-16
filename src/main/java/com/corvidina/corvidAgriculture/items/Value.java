package com.corvidina.corvidAgriculture.items;

import org.jetbrains.annotations.NotNull;

public enum Value {
    ROTTEN,
    FAIR,
    GOOD,
    GREAT,
    EXEMPLARY,
    DIVINE;

    public int compare(@NotNull Value other){
        if(this==other){
            return 0;
        }
        if(other==ROTTEN){
            return 1;
        }
        if(this==ROTTEN){
            return -1;
        }
        if(this==GOOD&&other==FAIR){
            return 1;
        }
        if(other==GOOD&&this==FAIR)
            return -1;
        if(this==GREAT&&(other==FAIR||other==GOOD))
            return 1;
        if(other==GREAT&&(this==FAIR||this==GOOD))
            return -1;
        if(this==EXEMPLARY&&(other==FAIR||other==GOOD||other==GREAT))
            return 1;
        if(other==EXEMPLARY&&(this==FAIR||this==GOOD||this==GREAT))
            return -1;
        if(this==DIVINE)
            return 1;
        return -1;
    }
}
