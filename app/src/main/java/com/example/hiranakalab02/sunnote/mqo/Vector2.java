package com.example.hiranakalab02.sunnote.mqo;

/**
 * Created by HiranakaLab02 on 2015/08/28.
 */
public class Vector2 {
    public float x = 0.0f;
    public float y = 0.0f;

    @Override
    public boolean equals(Object o) {
        Vector2 v = (Vector2) o;
        return v.x == x && v.y == y;
    }
}