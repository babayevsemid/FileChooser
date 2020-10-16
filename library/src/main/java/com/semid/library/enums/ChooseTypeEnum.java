package com.semid.library.enums;

public enum ChooseTypeEnum {
    TAKE_VIDEO(3232), CHOOSE_VIDEO(3233), TAKE_PHOTO(3234), CHOOSE_PHOTO(3235), CHOOSE_FILE(3236);

    int id;

    ChooseTypeEnum(int id) {
        this.id = id;
    }

    public static ChooseTypeEnum byId(int id) {
        ChooseTypeEnum[] chooseTypes = values();
        for (ChooseTypeEnum type : chooseTypes)
            if (type.id == id)
                return type;
        return chooseTypes[0];
    }
}
