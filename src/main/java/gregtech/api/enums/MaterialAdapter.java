package gregtech.api.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gregtech.api.objects.MaterialStack;

public class MaterialAdapter {
	public static final int DIESEL = 0, GAS = 1, THERMAL = 2, SEMIFLUID = 3, PLASMA = 4, MAGIC = 5;
	
	private int metaItemSubID;
	private TextureSet iconSet;
	private float toolSpeed = 1.0f;
	private int durability = 0;
	private int toolQuality = 0;
	private int types = 0;
	private int r = 255, g = 255, b = 255, a = 255;
	private String name;
	private String defaultLocalName;
	private int fuelType = 0;
	private int fuelPower = 0;
	private int meltingPoint = 0;
	private int blastFurnaceTemp = 0;
	private boolean blastFurnaceRequired = false;
	private boolean transparent = false;
	private int oreValue = 1;
	private int densityMultiplier = 1;
	private int densityDivider = 1;
	private Dyes color = Dyes._NULL;
	private int extraData = 0;
	private List<MaterialStack> materialList = new ArrayList<MaterialStack>();
	private List<TC_Aspects.TC_AspectStack> aspects = new ArrayList<TC_Aspects.TC_AspectStack>();

	public MaterialAdapter(int metaItemSubID, TextureSet iconSet, String defaultLocalName) {
		this.metaItemSubID = metaItemSubID;
		this.iconSet = iconSet;
		this.name = defaultLocalName.replace(" ", "");
		this.defaultLocalName = defaultLocalName;
	}

	public Materials constructMaterial() {
		return new Materials(metaItemSubID, iconSet, toolSpeed, durability, toolQuality, types, r, g, b, a, name, defaultLocalName, fuelType, fuelPower, meltingPoint, blastFurnaceTemp,
				blastFurnaceRequired, transparent, oreValue, densityMultiplier, densityDivider, color, extraData, materialList, aspects);
	}
	
	public MaterialAdapter setName(String name){
		this.name = name;
		return this;
	}
	
	public MaterialAdapter setTypes(int types){
		this.types = types;
		return this;
	}
	
	public MaterialAdapter addDustItems(){
		types = types | 1;
		return this;
	}
	
	public MaterialAdapter addMetalItems(){
		types = types | 2;
		return this;
	}
	
	public MaterialAdapter addGemItems(){
		types = types | 4;
		return this;
	}
	
	public MaterialAdapter addOreItems(){
		types = types | 8;
		return this;
	}
	
	public MaterialAdapter addCell(){
		types = types | 16;
		return this;
	}
	
	public MaterialAdapter addPlasma(){
		types = types | 32;
		return this;
	}
	
	public MaterialAdapter addToolHeadItems(){
		types = types | 64;
		return this;
	}
	
	public MaterialAdapter addGearItems(){
		types = types | 128;
		return this;
	}
	
	public MaterialAdapter addFluid(){
		types = types | 256;
		return this;
	}
	
	public MaterialAdapter addGas(){
		types = types | 512;
		return this;
	}
	
	
	public MaterialAdapter setRGBA(int r, int g, int b, int a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}
	
	public MaterialAdapter setRGB(int r, int g, int b){
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}
	
	public MaterialAdapter setTransparent(boolean transparent){
		this.transparent = transparent;
		return this;
	}
	
	public MaterialAdapter setColor(Dyes color){
		this.color = color;
		return this;
	}


	public MaterialAdapter setToolSpeed(float toolSpeed) {
		this.toolSpeed = toolSpeed;
		return this;
	}

	public MaterialAdapter setDurability(int durability) {
		this.durability = durability;
		return this;
	}

	public MaterialAdapter setToolQuality(int toolQuality) {
		this.toolQuality = toolQuality;
		return this;
	}


	public MaterialAdapter setFuelType(int fuelType) {
		this.fuelType = fuelType;
		return this;
	}

	public MaterialAdapter setFuelPower(int fuelPower) {
		this.fuelPower = fuelPower;
		return this;
	}

	public MaterialAdapter setMeltingPoint(int meltingPoint) {
		this.meltingPoint = meltingPoint;
		return this;
	}

	public MaterialAdapter setBlastFurnaceTemp(int blastFurnaceTemp) {
		this.blastFurnaceTemp = blastFurnaceTemp;
		return this;
	}

	public MaterialAdapter setBlastFurnaceRequired(boolean blastFurnaceRequired) {
		this.blastFurnaceRequired = blastFurnaceRequired;
		return this;
	}

	public MaterialAdapter setOreValue(int oreValue) {
		this.oreValue = oreValue;
		return this;
	}

	public MaterialAdapter setDensityMultiplier(int densityMultiplier) {
		this.densityMultiplier = densityMultiplier;
		return this;
	}

	public MaterialAdapter setDensityDivider(int densityDivider) {
		this.densityDivider = densityDivider;
		return this;
	}

	public MaterialAdapter setExtraData(int extraData) {
		this.extraData = extraData;
		return this;
	}

	public MaterialAdapter addElectrolyzerRecipe(){
		extraData = extraData | 1;
		return this;
	}
	
	public MaterialAdapter addCentrifugeRecipe(){
		extraData = extraData | 2;
		return this;
	}
	
	public MaterialAdapter setMaterialList(List<MaterialStack> materialList) {
		this.materialList = materialList;
		return this;
	}
	
	public MaterialAdapter setMaterialList(MaterialStack ... materials) {
		this.materialList = Arrays.asList(materials);
		return this;
	}

	public MaterialAdapter setAspects(List<TC_Aspects.TC_AspectStack> aspects) {
		this.aspects = aspects;
		return this;
	}
}
