package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;

public class XRay extends Module {
    public static XRay INSTANCE;

    public XRay() {
        super("XRay", Category.RENDER);
        INSTANCE = this;
    }
}
