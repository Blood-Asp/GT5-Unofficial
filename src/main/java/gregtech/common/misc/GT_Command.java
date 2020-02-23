package gregtech.common.misc;

import gregtech.api.enums.GT_Values;
import gregtech.api.objects.GT_ChunkManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class GT_Command extends CommandBase {

    @Override
    public String getCommandName() {
        return "gt";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: gt <subcommand>. Valid subcommands are: toggle, chunks.";
    }
    private void printHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("Usage: gt <toggle|chunks>"));
        sender.addChatMessage(new ChatComponentText("\"toggle D1\" - toggles general.Debug (D1)"));
        sender.addChatMessage(new ChatComponentText("\"toggle D2\" - toggles general.Debug2 (D2)"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugCleanroom\" - toggles cleanroom debug log"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugDriller\" - toggles oil drill debug log"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugWorldGen\" - toggles generic worldgen debug"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugOrevein\" - toggles worldgen ore vein debug"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugSmallOres\" - toggles worldgen small vein debug"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugStones\" - toggles worldgen stones debug"));
        sender.addChatMessage(new ChatComponentText("\"toggle debugChunkloaders\" - toggles chunkloaders debug"));
        sender.addChatMessage(new ChatComponentText("\"chunks\" - print a list of the force loaded chunks"));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] ss) {
        List<String> l = new ArrayList<>();
        if (ss.length == 0 || ss.length == 1 && (ss[0].trim().isEmpty() || "toggle".startsWith(ss[0]) || "chunks".startsWith(ss[0]))) {
            if ("toggle".startsWith(ss[0]))
                l.add("toggle");
            else if ("chunks".startsWith(ss[0]))
                l.add("chunks");
            else {
                l.add("toggle");
                l.add("chunks");
            }
        } else if (ss[0].equals("toggle")) {
            if (ss.length == 1 || ss[1].trim().isEmpty()) {
                l.add("D1");
                l.add("D2");
                l.add("debugCleanroom");
                l.add("debugDriller");
                l.add("debugWorldGen");
                l.add("debugOrevein");
                l.add("debugSmallOres");
                l.add("debugStones");
                l.add("debugChunkloaders");
            } else if (ss[1].startsWith("D")) {
                l.add("D1");
                l.add("D2");
            } else if (ss[1].startsWith("d")) {
                l.add("debugCleanroom");
                l.add("debugDriller");
                l.add("debugWorldGen");
                l.add("debugOrevein");
                l.add("debugSmallOres");
                l.add("debugStones");
                l.add("debugChunkloaders");
            }
        }
        return l;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {
        if (strings.length < 1 || (!strings[0].equals("toggle") && !strings[0].equals("chunks"))) {
            printHelp(sender);
            return;
        }
        switch (strings[0]) {
            case "toggle":
                if (strings.length < 2) {
                    printHelp(sender);
                    return;
                }
                try {
                    Field field = GT_Values.class.getDeclaredField(strings[1]);
                    if (field.getType() != boolean.class) {
                        sender.addChatMessage(new ChatComponentText("Wrong variable: " + strings[1]));
                        return;
                    }
                    boolean b = !field.getBoolean(null);
                    field.setBoolean(null, b);
                    sender.addChatMessage(new ChatComponentText(strings[1] + " = " + (b ? "true" : "false")));
                } catch (Exception e) {
                    sender.addChatMessage(new ChatComponentText("No such variable: " + strings[0]));
                }
                break;
            case "chunks":
                GT_ChunkManager.printTickets();
                sender.addChatMessage(new ChatComponentText("Forced chunks logged to GregTech.log"));
                break;
        }
    }
}
