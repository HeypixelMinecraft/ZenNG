package com.mihoyo.zen.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.KeyEvent;
import com.mihoyo.zen.exception.ModuleNotFoundException;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.modules.impl.combat.AntiBots;
import com.mihoyo.zen.modules.impl.combat.AntiFireball;
import com.mihoyo.zen.modules.impl.combat.AntiKB;
import com.mihoyo.zen.modules.impl.combat.AutoOffHand;
import com.mihoyo.zen.modules.impl.combat.AutoSoup;
import com.mihoyo.zen.modules.impl.combat.AutoThrow;
import com.mihoyo.zen.modules.impl.combat.Backtrack;
import com.mihoyo.zen.modules.impl.combat.Critical;
import com.mihoyo.zen.modules.impl.combat.CrystalAura;
import com.mihoyo.zen.modules.impl.combat.KillAura;
import com.mihoyo.zen.modules.impl.exploit.Disabler;
import com.mihoyo.zen.modules.impl.exploit.FastPlace;
import com.mihoyo.zen.modules.impl.misc.AimAssist;
import com.mihoyo.zen.modules.impl.misc.AutoClicker;
import com.mihoyo.zen.modules.impl.misc.AutoRod;
import com.mihoyo.zen.modules.impl.misc.SafeWalk;
import com.mihoyo.zen.modules.impl.movement.CollisionSpeed;
import com.mihoyo.zen.modules.impl.movement.NoSlow;
import com.mihoyo.zen.modules.impl.movement.FastWeb;
import com.mihoyo.zen.modules.impl.movement.FireballBlink;
import com.mihoyo.zen.modules.impl.movement.Fly;
import com.mihoyo.zen.modules.impl.movement.GuiMove;
import com.mihoyo.zen.modules.impl.movement.HighJump;
import com.mihoyo.zen.modules.impl.movement.NoDelay;
import com.mihoyo.zen.modules.impl.movement.NoPush;
import com.mihoyo.zen.modules.impl.movement.Scaffold;
import com.mihoyo.zen.modules.impl.movement.Sprint;
import com.mihoyo.zen.modules.impl.movement.TargetStrafe;
import com.mihoyo.zen.modules.impl.player.AntiTNT;
import com.mihoyo.zen.modules.impl.player.AntiVoid;
import com.mihoyo.zen.modules.impl.player.AntiWeb;
import com.mihoyo.zen.modules.impl.player.AutoMLG;
import com.mihoyo.zen.modules.impl.player.AutoWebPlace;
import com.mihoyo.zen.modules.impl.player.ChestStealer;
import com.mihoyo.zen.modules.impl.player.GhostHand;
import com.mihoyo.zen.modules.impl.player.Helper;
import com.mihoyo.zen.modules.impl.player.InventoryManager;
import com.mihoyo.zen.modules.impl.player.MidPearl;
import com.mihoyo.zen.modules.impl.player.NoFall;
import com.mihoyo.zen.modules.impl.player.Stuck;
import com.mihoyo.zen.modules.impl.render.AspectRatio;
import com.mihoyo.zen.modules.impl.render.ChestESP;
import com.mihoyo.zen.modules.impl.render.ClickGuiModule;
import com.mihoyo.zen.modules.impl.render.Compass;
import com.mihoyo.zen.modules.impl.render.DamageGlow;
import com.mihoyo.zen.modules.impl.render.ESP;
import com.mihoyo.zen.modules.impl.render.FullBright;
import com.mihoyo.zen.modules.impl.render.Interface;
import com.mihoyo.zen.modules.impl.render.ItemTags;
import com.mihoyo.zen.modules.impl.render.NameProtect;
import com.mihoyo.zen.modules.impl.render.NameTags;
import com.mihoyo.zen.modules.impl.render.NoHurtCam;
import com.mihoyo.zen.modules.impl.render.OldHitting;
import com.mihoyo.zen.modules.impl.render.Projectiles;
import com.mihoyo.zen.modules.impl.render.Watermark;
import com.mihoyo.zen.modules.impl.render.XRay;
import com.mihoyo.zen.modules.impl.world.AntiStaff;
import com.mihoyo.zen.modules.impl.world.AutoPlay;
import com.mihoyo.zen.modules.impl.world.AutoTools;
import com.mihoyo.zen.modules.impl.world.Debugger;
import com.mihoyo.zen.modules.impl.world.Teams;
import com.mihoyo.zen.modules.impl.world.WebUI;
import com.mihoyo.zen.event.EventTarget;

public class ModuleManager extends ClientBase {
    private final Map<String, Module> moduleMap = new ConcurrentHashMap<>();

    public ModuleManager() {
        ZenClient.getInstance().getEventBus().register(this);
    }

    public void initModules() {
        this.register(new AntiBots());
        this.register(new AntiFireball());
        this.register(new AntiKB());
        this.register(new AutoOffHand());
        this.register(new AutoSoup());
        this.register(new AutoThrow());
        this.register(new Backtrack());
        this.register(new Critical());
        this.register(new CrystalAura());
        this.register(new KillAura());

        this.register(new Disabler());
        this.register(new FastPlace());

        this.register(new AimAssist());
        this.register(new AutoClicker());
        this.register(new AutoRod());
        this.register(new SafeWalk());

        this.register(new CollisionSpeed());
        this.register(new NoSlow());
        this.register(new FastWeb());
        this.register(new FireballBlink());
        this.register(new Fly());
        this.register(new GuiMove());
        this.register(new HighJump());
        this.register(new NoDelay());
        this.register(new NoPush());
        this.register(new Scaffold());
        this.register(new Sprint());
        this.register(new TargetStrafe());

        this.register(new AntiTNT());
        this.register(new AntiVoid());
        this.register(new AntiWeb());
        this.register(new AutoMLG());
        this.register(new AutoWebPlace());
        this.register(new ChestStealer());
        this.register(new GhostHand());
        this.register(new Helper());
        this.register(new InventoryManager());
        this.register(new MidPearl());
        this.register(new NoFall());
        this.register(new Stuck());

        this.register(new AspectRatio());
        this.register(new ChestESP());
        this.register(new ClickGuiModule());
        this.register(new Compass());
        this.register(new DamageGlow());
        this.register(new ESP());
        this.register(new FullBright());
        this.register(new Interface());
        this.register(new ItemTags());
        this.register(new NameProtect());
        this.register(new NameTags());
        this.register(new NoHurtCam());
        this.register(new OldHitting());
        this.register(new Projectiles());
        this.register(new Watermark());
        this.register(new XRay());

        this.register(new AntiStaff());
        this.register(new AutoPlay());
        this.register(new AutoTools());
        this.register(new Debugger());
        this.register(new Teams());
        this.register(new WebUI());
    }

    public void register(Module module) {
        this.moduleMap.put(module.getClass().getSimpleName(), module);
        module.registerSettings();
    }

    public Module getModule(String string) {
        Module module = null;
        for (Module module2 : this.moduleMap.values()) {
            if (!StringUtils.replace(module2.getName(), " ", "").equalsIgnoreCase(string)) continue;
            module = module2;
        }
        if (module == null) {
            throw new ModuleNotFoundException();
        }
        return module;
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        Module module = clazz.cast(this.moduleMap.get(clazz.getSimpleName()));
        if (module == null) {
            throw new ModuleNotFoundException();
        }
        return (T) module;
    }

    public List<Module> getModules() {
        return this.moduleMap.values().stream().toList();
    }

    public List<Module> getModulesByCategory(Category category) {
        return this.moduleMap.values().stream()
                .filter(module -> module.getCategory().equals(category))
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .collect(Collectors.toList());
    }

    @EventTarget
    public void onKey(KeyEvent event) {
        if (mc.screen == null) {
            for (Module module : this.moduleMap.values()) {
                if (module.getKey() != 0 && module.getKey() == event.getKeyCode() && event.isPressed()) {
                    module.toggle();
                }
            }
        }
    }
}
