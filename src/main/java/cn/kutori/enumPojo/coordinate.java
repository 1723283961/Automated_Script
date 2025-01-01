package cn.kutori.enumPojo;

import lombok.Getter;

@Getter
public enum coordinate {

    COORDINATEX("X轴坐标","x"),
    COORDINATEY("Y轴坐标","y");

    public final String name;

    public final String value;

    private coordinate(String name,String value){
        this.name = name;
        this.value = value;
    }
}
