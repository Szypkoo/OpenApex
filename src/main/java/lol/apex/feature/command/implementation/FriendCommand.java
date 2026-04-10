package lol.apex.feature.command.implementation;

import lol.apex.Apex;
import lol.apex.feature.command.base.Command;
import lol.apex.feature.command.base.CommandInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

@CommandInfo(
        name = "friend",
        description = "Helps manage your added friends or remove friends.",
        alias = "f"
)
public class FriendCommand extends Command {

    @Override
    public void execute(String[] args) {
        if(args.length < 1) {
            Apex.sendChatMessage("Usage: .friends <add/remove/list/clear>");
            return;
        } 

        String action = args[0].toLowerCase();

        switch(action) {
            case "add": {
                if(args.length < 2) {
                    Apex.sendChatMessage("Usage: .friends add <username>");
                    return;
                } 

                String name = args[1];
                var entity = getPlayer(name);

                entity.ifPresentOrElse(e -> {
                    Apex.friendManager.addIfAbsent(e.getUuid());
                    Apex.sendChatMessage(name + " is now your friend!");

                }, () -> Apex.sendChatMessage("Player not found!"));
                break;
            } 

            case "remove": {
                if(args.length < 2) {
                    Apex.sendChatMessage("Usage: .friends remove <username>");
                    return;
                } 

                String name = args[1];
                var entity = getPlayer(name);

                entity.ifPresentOrElse(e -> {
                    Apex.friendManager.remove(e.getUuid());
                    Apex.sendChatMessage(name + " was removed from friends.");

                }, () -> {
                    Apex.sendChatMessage("Friend not found");
                });
                break;                
            } 

            case "list": {
                if(Apex.friendManager.isEmpty()) {
                    Apex.sendChatMessage("You have no friends");
                    return;
                }

                Apex.sendChatMessage("Friends: ");
                for(UUID friend : Apex.friendManager) {
                    var entity = getPlayer(friend);

                    String display = entity.isEmpty() ? friend.toString() : entity.get().getName().getLiteralString();
                    Apex.sendChatMessage(" - " + display);
                }
                break;
            }  

            case "clear": {
                Apex.friendManager.clear();
                Apex.sendChatMessage("Cleared all friends");
                break;
            } 

            default: {
                Apex.sendChatMessage("Usage: .friends add <username>");
                break;
            }
        }
    }

    private Optional<PlayerEntity> getPlayer(UUID uuid) {
        return Optional.ofNullable(MinecraftClient.getInstance().world.getPlayerByUuid(uuid));
    }

    private Optional<AbstractClientPlayerEntity> getPlayer(String name) {
        System.out.println(MinecraftClient.getInstance().world.getPlayers().size());

        Optional<AbstractClientPlayerEntity> entity = MinecraftClient.getInstance().world.getPlayers()
                .stream()
                .filter(p -> p.getName().contains(Text.of(name)))
                .findFirst();

        return entity;
    }

}
