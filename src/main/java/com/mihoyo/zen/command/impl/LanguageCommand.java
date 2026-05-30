package com.mihoyo.zen.command.impl;

import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.command.Command;

public class LanguageCommand
extends Command {
    public static final class EventHandler {
        private final LanguageCommand parent;

        public EventHandler(LanguageCommand parent) {
            this.parent = parent;
        }
    }

    public LanguageCommand() {
        super("language", new String[]{"lang"});
    }

    @Override
    public void onCommand(String[] stringArray) {
        ZenClient.getInstance().getEventBus().register(new LanguageCommand.EventHandler(this));
    }

    @Override
    public String[] onTab(String[] stringArray) {
        return new String[0];
    }
}