package gregtech.api.util;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import gregtech.api.enums.Materials;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This makes it easier to build multi tooltips, with a standardized format. <br>
 * Info section order should be:<br>
 * addMachineType<br>
 * addInfo, for what it does, special notes, etc.<br>
 * addSeparator, if you need it<br>
 * addPollutionAmount<br>
 * <br>
 * Structure order should be:<br>
 * beginStructureBlock<br>
 * addController<br>
 * addCasingInfo<br>
 * addOtherStructurePart, for secondary structure block info (pipes, coils, etc)<br>
 * addEnergyHatch/addDynamoHatch<br>
 * addMaintenanceHatch<br>
 * addMufflerHatch<br>
 * addInputBus/addInputHatch/addOutputBus/addOutputHatch, in that order<br>
 * Use addStructureInfo for any comments on nonstandard structure info wherever needed
 * <br>
 * toolTipFinisher goes at the very end<br>
 * <br>
 * Originally created by kekzdealer
 */
public class GT_Multiblock_Tooltip_Builder {
	private static final String TAB = "   ";
	private static final String COLON = ": ";
	private static final String SEPARATOR = ", ";

	private final List<String> iLines;
	private final List<String> sLines;
	private final List<String> hLines;
	private final SetMultimap<Integer, String> hBlocks;

	private String[] iArray;
	private String[] sArray;
	private String[] hArray;

	//Localized tooltips
	private static final String TT_machineType = StatCollector.translateToLocal("GT5U.MBTT.MachineType");
	private static final String TT_dimensions = StatCollector.translateToLocal("GT5U.MBTT.Dimensions");
	private static final String TT_hollow = StatCollector.translateToLocal("GT5U.MBTT.Hollow");
	private static final String TT_structure = StatCollector.translateToLocal("GT5U.MBTT.Structure");
	private static final String TT_controller = StatCollector.translateToLocal("GT5U.MBTT.Controller");
	private static final String TT_minimum = StatCollector.translateToLocal("GT5U.MBTT.Minimum");
	private static final String TT_maintenancehatch = StatCollector.translateToLocal("GT5U.MBTT.MaintenanceHatch");
	private static final String TT_energyhatch = StatCollector.translateToLocal("GT5U.MBTT.EnergyHatch");
	private static final String TT_dynamohatch = StatCollector.translateToLocal("GT5U.MBTT.DynamoHatch");
	private static final String TT_mufflerhatch = StatCollector.translateToLocal("GT5U.MBTT.MufflerHatch");
	private static final String TT_inputbus = StatCollector.translateToLocal("GT5U.MBTT.InputBus");
	private static final String TT_inputhatch = StatCollector.translateToLocal("GT5U.MBTT.InputHatch");
	private static final String TT_outputbus = StatCollector.translateToLocal("GT5U.MBTT.OutputBus");
	private static final String TT_outputhatch = StatCollector.translateToLocal("GT5U.MBTT.OutputHatch");
	private static final String TT_causes = StatCollector.translateToLocal("GT5U.MBTT.Causes");
	private static final String TT_pps = StatCollector.translateToLocal("GT5U.MBTT.PPS");
	private static final String TT_hold = StatCollector.translateToLocal("GT5U.MBTT.Hold");
	private static final String TT_todisplay = StatCollector.translateToLocal("GT5U.MBTT.Display");
	private static final String TT_structurehint = StatCollector.translateToLocal("GT5U.MBTT.StructureHint");
	private static final String TT_mod = StatCollector.translateToLocal("GT5U.MBTT.Mod");
	private static final String TT_air = StatCollector.translateToLocal("GT5U.MBTT.Air");
	private static final String[] TT_dots = IntStream.range(0, 16).mapToObj(i -> StatCollector.translateToLocal("structurelib.blockhint." + i + ".name")).toArray(String[]::new);

	public GT_Multiblock_Tooltip_Builder() {
		iLines = new LinkedList<>();
		sLines = new LinkedList<>();
		hLines = new LinkedList<>();
		hBlocks = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
		hBlocks.put(StructureLibAPI.HINT_BLOCK_META_AIR, TT_air);
	}
	
	/**
	 * Add a line telling you what the machine type is. Usually, this will be the name of a SB version.<br>
	 * Machine Type: machine
	 * 
	 * @param machine
	 * 		Name of the machine type
	 * 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addMachineType(String machine) {
		iLines.add(TT_machineType + COLON + EnumChatFormatting.YELLOW + machine + EnumChatFormatting.RESET);
		return this;
	}
	
	/**
	 * Add a basic line of information about this structure
	 * 
	 * @param info
	 * 		The line to be added.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addInfo(String info) {
		iLines.add(info);
		return this;
	}
	
	/**
	 * Add a separator line like this:<br>
	 * -----------------------------------------
	 * 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addSeparator() {
		iLines.add("-----------------------------------------");
		return this;
	}
	
	/**
	 * Add a line telling how much this machine pollutes.
	 * 
	 * @param pollution
	 * 		Amount of pollution per second when active
	 * 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addPollutionAmount(int pollution) {
		iLines.add(TT_causes + COLON + EnumChatFormatting.DARK_PURPLE + pollution + " " + EnumChatFormatting.GRAY + TT_pps);
		return this;
	}

	/**
	 * Begin adding structural information by adding a line about the structure's dimensions
	 * and then inserting a "Structure:" line.
	 * 
	 * @param w
	 * 		Structure width.
	 * @param h
	 * 		Structure height.
	 * @param l
	 * 		Structure depth/length.
	 * @param hollow
	 * 		T/F, adds a (hollow) comment if true
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder beginStructureBlock(int w, int h, int l, boolean hollow) {
		if (hollow) {
			sLines.add(TT_dimensions + COLON + w + "x" + h + "x" + l + " (WxHxL) " + TT_hollow);
		}
		else {
			sLines.add(TT_dimensions + COLON + w + "x" + h + "x" + l + " (WxHxL)");
		}
			sLines.add(TT_structure + COLON);
			return this;
	}
	
	/**
	 * Begin adding structural information by adding a line about the structure's dimensions<br>
	 * and then inserting a "Structure:" line. Variable version displays min and max
	 *  
	 * @param wmin
	 * 		Structure min width.
	 * @param wmax
	 * 		Structure max width.
	 * @param hmin
	 * 		Structure min height.
	 * @param hmax
	 * 		Structure max height.
	 * @param lmin
	 * 		Structure min depth/length.
	 * @param lmax
	 * 		Structure max depth/length.
	 * @param hollow
	 * 		T/F, adds a (hollow) comment if true
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder beginVariableStructureBlock(int wmin, int wmax, int hmin, int hmax, int lmin, int lmax, boolean hollow) {
		if (hollow) {
			sLines.add(TT_dimensions + COLON + wmin + "-" + wmax + "x" + hmin + "-" + hmax + "x" + lmin + "-" + lmax + " (WxHxL) " + TT_hollow);
		}
		else {
			sLines.add(TT_dimensions + COLON + wmin + "-" + wmax + "x" + hmin + "-" + hmax + "x" + lmin + "-" + lmax + " (WxHxL)");
		}
			sLines.add(TT_structure + COLON);
			return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Controller: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addController(String info) {
		sLines.add(TAB + TT_controller + COLON + info);
		return this;
	}
		
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)minCountx casingName (minimum)
	 * @param casingName
	 * 		Name of the Casing.
	 * @param minCount
	 * 		Minimum needed for valid structure check.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addCasingInfo(String casingName, int minCount) {
		sLines.add(TAB + minCount +"x " + casingName + " " + TT_minimum);
		return this;
	}
	
	/**
	 * Use this method to add a structural part that isn't covered by the other methods.<br>
	 * (indent)name: info
	 * @param name
	 * 		Name of the hatch or other component.
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addOtherStructurePart(String name, String info) {
		sLines.add(TAB + name + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Maintenance Hatch: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addMaintenanceHatch(String info) {
		sLines.add(TAB + TT_maintenancehatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Muffler Hatch: info
	 * @param info
	 * 		Location where the hatch goes 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addMufflerHatch(String info) {
		sLines.add(TAB + TT_mufflerhatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Energy Hatch: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addEnergyHatch(String info) {
		sLines.add(TAB + TT_energyhatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Dynamo Hatch: info
	 * @param info
	 * 		Positional information.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addDynamoHatch(String info) {
		sLines.add(TAB + TT_dynamohatch + COLON + info);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Input Bus: info
	 * @param info
	 * 		Location where the bus goes 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addInputBus(String info) {
		sLines.add(TAB + TT_inputbus + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Input Hatch: info
	 * @param info
	 * 		Location where the hatch goes 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addInputHatch(String info) {
		sLines.add(TAB + TT_inputhatch + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Output Bus: info
	 * @param info
	 * 		Location where the bus goes 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addOutputBus(String info) {
		sLines.add(TAB + TT_outputbus + COLON + info);
		return this;
	}
	
	/**
	 * Add a line of information about the structure:<br>
	 * 	(indent)Output Hatch: info
	 * @param info
	 * 		Location where the bus goes 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addOutputHatch(String info) {
		sLines.add(TAB + TT_outputhatch + COLON + info);
		return this;
	}

	/**
	 * Use this method to add a structural part that isn't covered by the other methods.<br>
	 * (indent)name: info
	 * @param name
	 * 		Name of the hatch or other component.
	 * @param info
	 * 		Positional information.
	 * @param dots
	 * 		The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addOtherStructurePart(String name, String info, int... dots) {
		sLines.add(TAB + name + COLON + info);
		for (int dot : dots) hBlocks.put(dot, name);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Maintenance Hatch: info
	 *
	 * @param info Positional information.
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addMaintenanceHatch(String info, int... dots) {
		sLines.add(TAB + TT_maintenancehatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_maintenancehatch);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Muffler Hatch: info
	 *
	 * @param info Location where the hatch goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addMufflerHatch(String info, int... dots) {
		sLines.add(TAB + TT_mufflerhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_mufflerhatch);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Energy Hatch: info
	 *
	 * @param info Positional information.
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addEnergyHatch(String info, int... dots) {
		sLines.add(TAB + TT_energyhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_energyhatch);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Dynamo Hatch: info
	 *
	 * @param info Positional information.
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addDynamoHatch(String info, int... dots) {
		sLines.add(TAB + TT_dynamohatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_dynamohatch);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Input Bus: info
	 *
	 * @param info Location where the bus goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addInputBus(String info, int... dots) {
		sLines.add(TAB + TT_inputbus + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_inputbus);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Input Hatch: info
	 *
	 * @param info Location where the hatch goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addInputHatch(String info, int... dots) {
		sLines.add(TAB + TT_inputhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_inputhatch);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Output Bus: info
	 *
	 * @param info Location where the bus goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addOutputBus(String info, int... dots) {
		sLines.add(TAB + TT_outputbus + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_outputbus);
		return this;
	}

	/**
	 * Add a line of information about the structure:<br>
	 * (indent)Output Hatch: info
	 *
	 * @param info Location where the bus goes
	 * @param dots The valid locations for this part when asked to display hints
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addOutputHatch(String info, int... dots) {
		sLines.add(TAB + TT_outputhatch + COLON + info);
		for (int dot : dots) hBlocks.put(dot, TT_outputhatch);
		return this;
	}

	/**
	 * Use this method to add non-standard structural info.<br>
	 * (indent)info
	 * @param info
	 * 		The line to be added.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addStructureInfo(String info) {
		sLines.add(TAB + info);
		return this;
	}

	/**
	 * Use this method to add non-standard structural hint. This info will appear before the standard structural hint.
	 * @param info
	 * 		The line to be added. This should be an entry into minecraft's localization system.
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addStructureHint(String info) {
		hLines.add(StatCollector.translateToLocal(info));
		return this;
	}

	/**
	 * Use this method to add an entry to standard structural hint without creating a corresponding line in structure information
	 * @param name
	 * 		The name of block This should be an entry into minecraft's localization system.
	 * @param dots
	 * 		Possible locations of this block
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addStructureHint(String name, int... dots) {
		for (int dot : dots) hBlocks.put(dot, StatCollector.translateToLocal(name));
		return this;
	}
	
	/**
	 * Call at the very end.<br>
	 * Adds a final line with the mod name and information on how to display the structure guidelines.<br>
	 * Ends the building process.
	 * 
	 * @param mod
	 * 		Name of the mod that adds this multiblock machine
	 */
	public void toolTipFinisher(String mod) {
		iLines.add(TT_hold + " " + EnumChatFormatting.BOLD + "[LSHIFT]" + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + " " + TT_todisplay);
		iLines.add(TT_mod + COLON + EnumChatFormatting.GREEN + mod + EnumChatFormatting.GRAY);
		hLines.add(TT_structurehint);
		iArray = iLines.toArray(new String[0]);
		sArray = sLines.toArray(new String[0]);
		// e.getKey() - 1 because 1 dot is meta 0.
		hArray = Stream.concat(hLines.stream(), hBlocks.asMap().entrySet().stream().map(e -> TT_dots[e.getKey() - 1] + COLON + String.join(SEPARATOR, e.getValue()))).toArray(String[]::new);
	}
	
	public String[] getInformation() {
		return iArray;
	}
	
	public String[] getStructureInformation() {
		return sArray;
	}

	public String[] getStructureHint() {
		return hArray;
	}
}
