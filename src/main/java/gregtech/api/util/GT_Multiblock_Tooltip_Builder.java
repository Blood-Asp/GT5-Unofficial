package gregtech.api.util;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

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
 * toolTipFinisher<br>
 * <br>
 * Originally created by kekzdealer
 */
public class GT_Multiblock_Tooltip_Builder {
	private static final String TAB = "   ";
	private static final String COLON = ": ";
	
	private final List<String> iLines;
	private final List<String> sLines;
	
	private String[] iArray;
	private String[] sArray;

	//Localized tooltips
	private static final String TT_machineType = StatCollector.translateToLocal("GT5U.MBTT.MachineType");
	private static final String TT_dimensions = StatCollector.translateToLocal("GT5U.MBTT.Dimensions");
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
	private static final String TT_mod = StatCollector.translateToLocal("GT5U.MBTT.Mod");

	public GT_Multiblock_Tooltip_Builder() {
		iLines = new LinkedList<>();
		sLines = new LinkedList<>();
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
		iLines.add(TT_machineType + EnumChatFormatting.YELLOW + machine + EnumChatFormatting.RESET);
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
	 * Add a line telling you what the machine type is. Usually, this will be the name of a SB version.<br>
	 * Machine Type: machine
	 * 
	 * @param machine
	 * 		Name of the machine type
	 * 
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder addPollutionAmount(int pollution) {
		iLines.add(TT_causes + " " + EnumChatFormatting.DARK_PURPLE + pollution + " " + TT_pps + EnumChatFormatting.RESET);
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
	 * @return Instance this method was called on.
	 */
	public GT_Multiblock_Tooltip_Builder beginStructureBlock(int w, int h, int l) {
		sLines.add(TT_dimensions + w + "x" + h + "x" + l + " (WxHxL)");
		sLines.add(TT_structure);
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
		sLines.add(TAB + TT_controller + info);
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
		sLines.add(TAB + name + info);
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
		sLines.add(TAB + TT_maintenancehatch + info);
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
		sLines.add(TAB + TT_mufflerhatch + info);
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
		sLines.add(TAB + TT_energyhatch + info);
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
		sLines.add(TAB + TT_dynamohatch + info);
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
		sLines.add(TAB + TT_inputbus + info);
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
		sLines.add(TAB + TT_inputhatch + info);
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
		sLines.add(TAB + TT_outputbus + info);
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
		sLines.add(TAB + TT_outputhatch + info);
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
	 * Call at the very end.<br>
	 * Adds a final line with the mod name and information on how to display the structure guidelines.<br>
	 * Ends the building process.
	 * 
	 * @param mod
	 * 		Name of the mod that adds this multiblock machine
	 */
	public void toolTipFinisher(String mod) {
		iLines.add(TT_hold + " " + EnumChatFormatting.BOLD + "[LSHIFT]" + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + " " + TT_todisplay);
		iLines.add(TT_mod + " " + EnumChatFormatting.GREEN + mod + EnumChatFormatting.RESET);
		iArray = new String[iLines.size()];
		sArray = new String[sLines.size()];
		iLines.toArray(iArray);
		sLines.toArray(sArray);
	}
	
	public String[] getInformation() {
		return iArray;
	}
	
	public String[] getStructureInformation() {
		return sArray;
	}

}
