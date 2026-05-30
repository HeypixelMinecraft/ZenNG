package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;

public class NoHurtCam
extends Module {
    public static NoHurtCam INSTANCE;
    public NoHurtCam() {
        super("NoHurtCam", Category.RENDER);
        INSTANCE = this;
    }
}