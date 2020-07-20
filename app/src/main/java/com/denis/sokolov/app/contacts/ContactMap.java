package com.denis.sokolov.app.contacts;

import java.util.HashMap;

public class ContactMap extends HashMap<String, String> {

    static final String NAME = "name";
    static final String PHONE = "phone";

    public ContactMap(String name, String phone) {
        super();
        super.put(NAME, name);
        super.put(PHONE, phone);
    }
}