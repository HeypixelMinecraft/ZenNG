package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;

public class ItemTags extends Module {
    public static ItemTags INSTANCE;

    public ItemTags() {
        super("ItemTags", Category.RENDER);
        INSTANCE = this;
    }
}
