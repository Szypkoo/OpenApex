package lol.apex.feature.module.implementation.player;

import lol.apex.feature.module.base.Category;
import lol.apex.feature.module.base.Module;
import lol.apex.feature.module.base.ModuleInfo;
import lol.apex.util.player.PlayerUtil;

@ModuleInfo(
        name = "Damage",
        description = "Automatically self-damages you.",
        category = Category.PLAYER
)
public class DamageModule extends Module {
    @Override
    public void onEnable() {
        PlayerUtil.selfDamage(3.04554231);
        if(mc.player.age % 20 == 0) toggle();
    }
}
