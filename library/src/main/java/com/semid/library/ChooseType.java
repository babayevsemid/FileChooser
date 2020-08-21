package com.semid.library;

public enum ChooseType {
    TAKE_VIDEO(3232), CHOOSE_VIDEO(3233), TAKE_PHOTO(3234), CHOOSE_PHOTO(3235), CHOOSE_FILE(3236);

    int id;

    ChooseType(int id) {
        this.id = id;
    }

    public static ChooseType byId(int id) {
        ChooseType[] chooseTypes = values();
        for (ChooseType type : chooseTypes)
            if (type.id == id)
                return type;
        return chooseTypes[0];
    }
}
