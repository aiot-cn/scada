package org.aiot.model.enums.aimodel;

public enum TaskEnum {
    detect("目标检测"),
    segment("语义分割"),
    classify("分类"),
    pose("姿势估计"),
    obb("旋转框"),
    ocr("文字识别")
    ;

    private String name;


    TaskEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
