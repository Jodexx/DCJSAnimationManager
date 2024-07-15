package com.jodexindustries.dcjsam.javascript;

import com.jodexindustries.dcjsam.util.Loader;
import com.jodexindustries.donatecase.api.AnimationManager;
import org.bukkit.configuration.ConfigurationSection;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSAnimationManager {
    private final Loader loader;
    private final File scriptsFolder;
    private final AnimationManager animationManager;
    private final List<String> animations = new ArrayList<>();
    private final Context context;

    public JSAnimationManager(Loader loader) {
        this.loader = loader;
        this.scriptsFolder = new File(loader.getMain().getDataFolder(), "animations");
        if(!scriptsFolder.exists()) {
            if(!scriptsFolder.mkdir()) {
                loader.getMain().getLogger().severe(
                        "An error occurred while creating the folder " + scriptsFolder.getAbsolutePath()
                );
            }
        }

        this.animationManager = loader.getMain().getCaseAPI().getAnimationManager();
         context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .build();
    }


    public void registerAnimations() {

        int count = 0;
        ConfigurationSection section = loader.getConfig().getAnimations().getConfigurationSection("animations");

        if(section != null) {
            for (String animation : section.getKeys(false)) {
                ConfigurationSection animationSection = section.getConfigurationSection(animation);
                if(animationSection == null) continue;

                String id = animationSection.getString("id");
                String fileName = animationSection.getString("file");
                if(id == null || fileName == null) continue;

                if(animationManager.isRegistered(id)) {
                    loader.getMain().getLogger().warning(
                            "Animation " + id + " already registered in DonateCase!");
                    continue;
                }

                File file = new File(scriptsFolder, fileName);
                if(!file.exists()) {
                    loader.getMain().getLogger().warning("File " + fileName + " does not exist!");
                    continue;
                }
                Source script;
                try {
                    script = Source.newBuilder("js", file).build();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                AnimationTemplate animationTemplate = new AnimationTemplate(context, script);

                animationManager.registerAnimation(id, animationTemplate);
                animations.add(id);
                count++;
            }
        }

        loader.getMain().getLogger().info("Registered " + count + " animations!");
    }

    public void unregisterAnimations() {
        for(String id : animations) {
            if(animationManager.isRegistered(id)) {
                animationManager.unregisterAnimation(id);
            }
        }
        loader.getMain().getLogger().info("Animations unregistered!");
    }

}
