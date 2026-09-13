package com.example.purchase_system.util;

import java.util.Set;

public class IdGenerator {
    
    public static int getNextId(Set<Integer> ids) {
        int maxId = 0;
        for (int id : ids) {
            if (id > maxId) maxId = id;
        }
        return maxId + 1;
    }
}
