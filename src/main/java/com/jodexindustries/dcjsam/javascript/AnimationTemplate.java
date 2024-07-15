package com.jodexindustries.dcjsam.javascript;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.data.Animation;
import com.jodexindustries.donatecase.api.data.CaseData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.util.UUID;

public class AnimationTemplate implements Animation {
    private final Source script;
    private final Context ctx;

    public AnimationTemplate(Context ctx, Source script) {
        this.script = script;
        this.ctx = ctx;
    }

    @Override
    public void start(Player player, Location location, UUID uuid, CaseData caseData, CaseData.Item winItem) {
        ctx.getBindings("js").putMember("Case", Case.class);
        ctx.getBindings("js").putMember("JavaClass", Class.class);

        ctx.getBindings("js").putMember("player", player);
        ctx.getBindings("js").putMember("location", location);
        ctx.getBindings("js").putMember("uuid", uuid);
        ctx.getBindings("js").putMember("caseData", caseData);
        ctx.getBindings("js").putMember("winItem", winItem);

        ctx.eval(script);
    }

}
