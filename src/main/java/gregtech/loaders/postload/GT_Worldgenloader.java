package gregtech.loaders.postload;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Log;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import gregtech.common.GT_Worldgen_GT_Ore_SmallPieces;
import gregtech.common.GT_Worldgen_Stone;
import gregtech.common.GT_Worldgenerator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings("ALL")
public class GT_Worldgenloader implements Runnable {

    public void run() {

        new GT_Worldgenerator();

        new GT_Worldgen_Stone("overworld.stone.blackgranite.tiny", true, GregTech_API.sBlockGranites, 0, 0, 1, 75, 5, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.small", true, GregTech_API.sBlockGranites, 0, 0, 1, 100, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.medium", true, GregTech_API.sBlockGranites, 0, 0, 1, 200, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.large", true, GregTech_API.sBlockGranites, 0, 0, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.huge", true, GregTech_API.sBlockGranites, 0, 0, 1, 400, 150, 0, 120, null, false);
        
        new GT_Worldgen_Stone("overworld.stone.redgranite.tiny", true, GregTech_API.sBlockGranites, 8, 0, 1, 75, 5, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.small", true, GregTech_API.sBlockGranites, 8, 0, 1, 100, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.medium", true, GregTech_API.sBlockGranites, 8, 0, 1, 200, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.large", true, GregTech_API.sBlockGranites, 8, 0, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.huge", true, GregTech_API.sBlockGranites, 8, 0, 1, 400, 150, 0, 120, null, false);

        new GT_Worldgen_Stone("overworld.stone.marble.tiny", true, GregTech_API.sBlockStones, 0, 0, 1, 75, 5, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.small", true, GregTech_API.sBlockStones, 0, 0, 1, 100, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.medium", true, GregTech_API.sBlockStones, 0, 0, 1, 200, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.large", true, GregTech_API.sBlockStones, 0, 0, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.huge", true, GregTech_API.sBlockStones, 0, 0, 1, 400, 150, 0, 120, null, false);
        
        new GT_Worldgen_Stone("overworld.stone.basalt.tiny", true, GregTech_API.sBlockStones, 8, 0, 1, 75, 5, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.small", true, GregTech_API.sBlockStones, 8, 0, 1, 100, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.medium", true, GregTech_API.sBlockStones, 8, 0, 1, 200, 10, 0, 180, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.large", true, GregTech_API.sBlockStones, 8, 0, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.huge", true, GregTech_API.sBlockStones, 8, 0, 1, 400, 150, 0, 120, null, false);

        new GT_Worldgen_Stone("nether.stone.blackgranite.tiny", false, GregTech_API.sBlockGranites, 0, -1, 1, 50, 45, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.small", false, GregTech_API.sBlockGranites, 0, -1, 1, 100, 60, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.medium", false, GregTech_API.sBlockGranites, 0, -1, 1, 200, 80, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.large", false, GregTech_API.sBlockGranites, 0, -1, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.huge", false, GregTech_API.sBlockGranites, 0, -1, 1, 400, 150, 0, 120, null, false);
        
        new GT_Worldgen_Stone("nether.stone.redgranite.tiny", false, GregTech_API.sBlockGranites, 8, -1, 1, 50, 45, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.small", false, GregTech_API.sBlockGranites, 8, -1, 1, 100, 60, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.medium", false, GregTech_API.sBlockGranites, 8, -1, 1, 200, 80, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.large", false, GregTech_API.sBlockGranites, 8, -1, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.huge", false, GregTech_API.sBlockGranites, 8, -1, 1, 400, 150, 0, 120, null, false);

        new GT_Worldgen_Stone("nether.stone.marble.tiny", false, GregTech_API.sBlockStones, 0, -1, 1, 50, 45, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.small", false, GregTech_API.sBlockStones, 0, -1, 1, 100, 60, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.medium", false, GregTech_API.sBlockStones, 0, -1, 1, 200, 80, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.large", false, GregTech_API.sBlockStones, 0, -1, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.huge", false, GregTech_API.sBlockStones, 0, -1, 1, 400, 150, 0, 120, null, false);
        
        new GT_Worldgen_Stone("nether.stone.basalt.tiny", false, GregTech_API.sBlockStones, 8, -1, 1, 50, 45, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.small", false, GregTech_API.sBlockStones, 8, -1, 1, 100, 60, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.medium", false, GregTech_API.sBlockStones, 8, -1, 1, 200, 80, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.large", false, GregTech_API.sBlockStones, 8, -1, 1, 300, 70, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.huge", false, GregTech_API.sBlockStones, 8, -1, 1, 400, 150, 0, 120, null, false);

        //GT Default Small Ores
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.copper", true, 60, 180, 32, true, true, true, true, true, false, Materials.Copper);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.tin", true, 80, 220, 32, true, true, true, true, true, true, Materials.Tin);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.bismuth", true, 80, 120, 8, false, true, false, true, true, false, Materials.Bismuth);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.coal", true, 120, 250, 24, true, false, false, false, false, false, Materials.Coal);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.iron", true, 40, 100, 16, true, true, true, true, true, false, Materials.Iron);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.lead", true, 40, 180, 16, false, true, true, true, true, true, Materials.Lead);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.zinc", true, 80, 210, 24, true, true, true, true, true, false, Materials.Zinc);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.gold", true, 20, 60, 8, true, false, true, true, true, true, Materials.Gold);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.silver", true, 20, 60, 20, true, true, true, true, true, true, Materials.Silver);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.nickel", true, 80, 150, 8, true, false, true, true, true, true, Materials.Nickel);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.lapis", true, 10, 50, 4, true, false, false, true, false, true, Materials.Lapis);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.diamond", true, 5, 15, 2, true, false, false, true, true, true, Materials.Diamond);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.emerald", true, 5, 35, 2, false, false, false, false, true, true, Materials.Emerald);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.ruby", true, 5, 35, 2, false, false, false, false, true, true, Materials.Ruby);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.sapphire", true, 5, 35, 2, false, false, false, false, true, true, Materials.Sapphire);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.greensapphire", true, 5, 35, 2, false, false, false, false, true, true, Materials.GreenSapphire);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.olivine", true, 5, 35, 2, false, false, false, false, true, true, Materials.Olivine);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.topaz", true, 5, 35, 2, false, false, false, false, true, true, Materials.Topaz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.tanzanite", true, 5, 35, 2, false, false, false, false, true, true, Materials.Tanzanite);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.amethyst", true, 5, 35, 2, false, false, false, false, true, true, Materials.Amethyst);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.opal", true, 5, 35, 2, false, false, false, false, true, true, Materials.Opal);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.jasper", true, 5, 35, 2, false, false, false, false, true, true, Materials.Jasper);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.bluetopaz", true, 5, 35, 2, false, false, false, false, true, true, Materials.BlueTopaz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.amber", true, 5, 35, 2, false, false, false, false, true, true, Materials.Amber);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.foolsruby", true, 5, 35, 2, false, false, false, false, true, true, Materials.FoolsRuby);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.garnetred", true, 5, 35, 2, false, false, false, false, true, true, Materials.GarnetRed);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.garnetyellow", true, 5, 35, 2, false, false, false, false, true, true, Materials.GarnetYellow);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.redstone", true, 5, 25, 8, true, true, false, true, true, true, Materials.Redstone);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.netherquartz", true, 30, 120, 64, false, true, false, false, false, false, Materials.NetherQuartz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.saltpeter", true, 10, 60, 8, false, true, false, false, false, false, Materials.Saltpeter);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.sulfur", true, 5, 60, 40, false, true, false, false, false, false, Materials.Sulfur);
        
        //TODO: GTNH Custom Small Ores 
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.titanium",true,10,180,32,false, false, false, Materials.Titanium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.tungsten",true,10,120,16,false, false, false, Materials.Tungsten);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.meteoriciron",true,50,70,8,false, false, false, Materials.MeteoricIron);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.firestone",true,5,15,2,false, false, false, Materials.Firestone);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.neutronium",true,5,15,8,false, false, false, Materials.Neutronium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.chromite",true,20,40,8,false, false, false, Materials.Chromite);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.tungstate",true,20,40,8,false, false, false, Materials.Tungstate);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.naquadah",true,5,25,8,false, false, false, Materials.Naquadah);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.quantium",true,5,25,6,false, false, false, Materials.Quantium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.mythril",true,5,25,6,false, false, false, Materials.Mytryl);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.ledox",true,40,60,4,false, false, false, Materials.Ledox);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.oriharukon",true,20,40,6,false, false, false, Materials.Oriharukon);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.draconium",true,5,15,4,false, false, false, Materials.Draconium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.awdraconium",true,5,15,2,false, false, false, Materials.DraconiumAwakened);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.desh",true,10,30,6,false, false, false, Materials.Desh);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.blackplutonium",true,25,45,6,false, false, false, Materials.BlackPlutonium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.infinitycatalyst",true,40,80,6,false, false, false, Materials.InfinityCatalyst);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.infinity",true,2,40,2,false, false, false, Materials.Infinity);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.bedrockium",true,5,25,6,false, false, false, Materials.Bedrockium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.realgar",true,15,85,32,false, true, false, Materials.Realgar);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.certusquartz",true,5,115,16,false, true, false, Materials.CertusQuartz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.jade",true,5,35,2,false, false, false, Materials.Jade);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.deepiron",true,5,40,8,false, false, false, Materials.DeepIron);

        //GT Default Veins

        new GT_Worldgen_GT_Ore_Layer("ore.mix.naquadah", true, 10, 90, 30, 4, 32, false, false, false, Materials.Naquadah, Materials.Naquadah, Materials.Naquadah, Materials.NaquadahEnriched);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.lignite", true, 80, 210, 160, 7, 32, true, false, false, Materials.Lignite, Materials.Lignite, Materials.Lignite, Materials.Coal);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.coal", true, 30, 80, 80, 5, 32, true, false, false, Materials.Coal, Materials.Coal, Materials.Coal, Materials.Lignite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.magnetite", true, 60, 180, 160, 2, 32, true, false, false, Materials.Magnetite, Materials.Magnetite, Materials.Iron, Materials.VanadiumMagnetite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.gold", true, 30, 60, 160, 2, 32, true, false, true, Materials.Magnetite, Materials.Magnetite, Materials.VanadiumMagnetite, Materials.Gold);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.iron", true, 10, 40, 120, 3, 24, true, true, false, Materials.BrownLimonite, Materials.YellowLimonite, Materials.BandedIron, Materials.Malachite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.cassiterite", true, 60, 220, 50, 4, 24, true, false, true, Materials.Tin, Materials.Tin, Materials.Cassiterite, Materials.Tin);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.tetrahedrite", true, 80, 120, 70, 3, 24, false, true, true, Materials.Tetrahedrite, Materials.Tetrahedrite, Materials.Copper, Materials.Stibnite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.netherquartz", true, 40, 80, 80, 4, 24, false, true, false, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz, Materials.Quartzite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.sulfur", true, 5, 20, 100, 4, 24, false, true, false, Materials.Sulfur, Materials.Sulfur, Materials.Pyrite, Materials.Sphalerite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.copper", true, 5, 60, 80, 3, 24, true, true, true, Materials.Chalcopyrite, Materials.Iron, Materials.Pyrite, Materials.Copper);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.bauxite", true, 10, 80, 80, 3, 24, false, false, false, Materials.Bauxite, Materials.Ilmenite, Materials.Aluminium, Materials.Ilmenite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.salts", true, 50, 70, 50, 2, 24, true, false, false, Materials.RockSalt, Materials.Salt, Materials.Lepidolite, Materials.Spodumene);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.redstone", true, 5, 40, 60, 2, 24, true, true, false, Materials.Redstone, Materials.Redstone, Materials.Ruby, Materials.Cinnabar);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.soapstone", true, 20, 50, 40, 2, 16, true, false, false, Materials.Soapstone, Materials.Talc, Materials.Glauconite, Materials.Pentlandite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.nickel", true, 10, 40, 40, 2, 16, false, false, true, Materials.Garnierite, Materials.Nickel, Materials.Cobaltite, Materials.Pentlandite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.platinum", true, 40, 50, 5, 2, 16, false, false, false, Materials.Cooperite, Materials.Palladium, Materials.Platinum, Materials.Iridium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.pitchblende", true, 30, 60, 40, 2, 16, false, false, false, Materials.Pitchblende, Materials.Pitchblende, Materials.Uraninite, Materials.Uraninite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.monazite", true, 20, 40, 30, 2, 16, false, false, false, Materials.Bastnasite, Materials.Bastnasite, Materials.Monazite, Materials.Neodymium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.molybdenum", true, 20, 50, 5, 2, 16, false, true, true, Materials.Wulfenite, Materials.Molybdenite, Materials.Molybdenum, Materials.Powellite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.tungstate", true, 20, 60, 10, 2, 16, false, false, false, Materials.Scheelite, Materials.Scheelite, Materials.Tungstate, Materials.Lithium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.sapphire", true, 10, 40, 60, 2, 16, false, false, false, Materials.Almandine, Materials.Pyrope, Materials.Sapphire, Materials.GreenSapphire);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.manganese", true, 20, 30, 20, 2, 16, true, true, false, Materials.Grossular, Materials.Spessartine, Materials.Pyrolusite, Materials.Tantalite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.quartz", true, 80, 120, 30, 2, 16, false, true, false, Materials.Quartzite, Materials.Barite, Materials.CertusQuartz, Materials.CertusQuartz);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.diamond", true, 5, 20, 40, 1, 16, true, false, false, Materials.Graphite, Materials.Graphite, Materials.Diamond, Materials.Coal);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.olivine", true, 10, 40, 60, 2, 16, false, false, false, Materials.Bentonite, Materials.Magnesite, Materials.Olivine, Materials.Glauconite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.apatite", true, 40, 60, 60, 2, 16, true, false, false, Materials.Apatite, Materials.Apatite, Materials.TricalciumPhosphate, Materials.Pyrochlore);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.galena", true, 5, 45, 40, 4, 16, false, false, false, Materials.Galena, Materials.Galena, Materials.Silver, Materials.Lead);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.lapis", true, 20, 50, 40, 4, 16, true, false, false, Materials.Lazurite, Materials.Sodalite, Materials.Lapis, Materials.Calcite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.beryllium", true, 5, 30, 30, 2, 16, false, true, true, Materials.Beryllium, Materials.Beryllium, Materials.Emerald, Materials.Thorium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.uranium", true, 20, 30, 20, 2, 16, false, false, false, Materials.Uraninite, Materials.Uraninite, Materials.Uranium, Materials.Uranium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.oilsand", true, 50, 80, 40, 5, 16, true, false, false, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands);
        
        /*
         * TODO: custom GTNH OreMixes
         * WARNING: NO DUPLICATS IN aName OR DEPRECATED MATERIALS IN HERE.
         * Materials can be used unlimited, since achievements for Ores are turned off.
         */
        
        //aName, aDefault, aMinY, aMaxY, aWeight, aDensity, aSize, aOverworld, aNether, aEnd, aPrimary, aSecondary, aBetween, aSporadic
        new GT_Worldgen_GT_Ore_Layer("ore.mix.neutronium", true, 5, 30, 10, 2, 16, false, false, false, Materials.Neutronium, Materials.Adamantium, Materials.Naquadah, Materials.Titanium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.aquaignis", true, 5, 35, 16, 2, 16, false, false, false, Materials.InfusedWater, Materials.InfusedFire, Materials.Amber, Materials.Cinnabar);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.terraaer", true, 5, 35, 16, 2, 16, false, false, false, Materials.InfusedEarth, Materials.InfusedAir, Materials.Amber, Materials.Cinnabar);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.perditioordo", true, 5, 35, 16, 2, 16, false, false, false, Materials.InfusedEntropy, Materials.InfusedOrder, Materials.Amber, Materials.Cinnabar);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.coppertin", true, 80, 200, 80, 3, 24, true, false, false, Materials.Chalcopyrite, Materials.Vermiculite, Materials.Cassiterite, Materials.Alunite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.titaniumchrome", true, 10, 70, 16, 2, 16, false, false, false, Materials.Ilmenite, Materials.Chromite, Materials.Uvarovite, Materials.Perlite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.mineralsand", true, 50, 60, 80, 3, 24, true, false, false, Materials.BasalticMineralSand, Materials.GraniticMineralSand, Materials.FullersEarth, Materials.Gypsum);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.garnettin", true, 50, 60, 80, 3, 24, true, false, false, Materials.CassiteriteSand, Materials.GarnetSand, Materials.Asbestos, Materials.Diatomite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.kaolinitezeolite", true, 50, 70, 60, 4, 16, true, false, false, Materials.Kaolinite, Materials.Zeolite, Materials.FullersEarth, Materials.GlauconiteSand);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.mica", true, 20, 40, 20, 2, 16, true, false, false, Materials.Kyanite, Materials.Mica, Materials.Cassiterite, Materials.Pollucite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.dolomite", true, 150, 200, 40, 4, 24, true, false, false, Materials.Dolomite, Materials.Wollastonite, Materials.Trona, Materials.Andradite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.platinumchrome", true, 5, 30, 10, 2, 16, false, false, false, Materials.Platinum, Materials.Chrome, Materials.Cooperite, Materials.Palladium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.iridiummytryl", true, 15, 40, 10, 2, 16, false, false, false, Materials.Nickel, Materials.Iridium, Materials.Palladium, Materials.Mithril);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.osmium", true, 5, 30, 10, 2, 16, false, false, false, Materials.Nickel, Materials.Osmium, Materials.Iridium, Materials.Nickel);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.saltpeterelectrotine", true, 5, 45, 40, 3, 16, false, true, false, Materials.Saltpeter, Materials.Diatomite, Materials.Electrotine, Materials.Alunite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.desh", true, 5, 40, 30, 2, 16, false, false, false, Materials.Desh, Materials.Desh, Materials.Scheelite, Materials.Tungstate);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.draconium", true, 20, 40, 40, 1, 16, false, false, false, Materials.Draconium, Materials.Electrotine, Materials.Jade, Materials.Vinteum);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.quantium", true, 5, 25, 30,3, 24, false, false, false, Materials.Quantium, Materials.Amethyst, Materials.Rutile, Materials.Ardite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.callistoice", true, 40, 60, 40, 2, 16, false, false, false, Materials.CallistoIce, Materials.Topaz, Materials.BlueTopaz, Materials.Alduorite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.mytryl", true, 10, 30, 40, 2, 16, false, false, false, Materials.Mytryl, Materials.Jasper, Materials.Ceruclase, Materials.Vulcanite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.ledox", true, 55, 65, 30, 2, 24, false, false, false, Materials.Ledox, Materials.Opal, Materials.Orichalcum, Materials.Rubracium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.oriharukon", true, 30, 60, 40, 2, 16, false, false, false, Materials.Oriharukon, Materials.Tanzanite, Materials.Vyroxeres, Materials.Mirabilite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.blackplutonium", true, 5, 25, 40, 2, 24, false, false, false, Materials.BlackPlutonium, Materials.GarnetRed, Materials.GarnetYellow, Materials.Borax);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.infusedgold", true, 15, 40, 30, 2, 16, false, false, false, Materials.Gold, Materials.Gold, Materials.InfusedGold, Materials.Platinum);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.niobium", true, 5, 30, 60, 2, 24, false, false, false, Materials.Niobium, Materials.Yttrium, Materials.Gallium, Materials.Gallium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.tungstenirons", true, 5, 25, 16, 2, 30, false, false, false, Materials.Tungsten, Materials.Silicon, Materials.DeepIron, Materials.ShadowIron);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.uraniumgtnh", true, 10, 30, 60, 2, 24, false, false, false, Materials.Thorium, Materials.Uranium, Materials.Plutonium241, Materials.Uranium235);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.vanadiumgold", true, 10, 50, 60, 2, 24, false, false, false, Materials.Vanadium, Materials.Magnetite, Materials.Gold, Materials.Chrome);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.netherstar", true, 20, 60, 60, 2, 24, false, false, false, Materials.GarnetSand, Materials.NetherStar, Materials.GarnetRed, Materials.GarnetYellow);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.garnet", true, 10, 30, 40, 2, 16, false, false, false, Materials.GarnetRed, Materials.GarnetYellow, Materials.Chrysotile, Materials.Realgar);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.rareearth", true, 30, 60, 40, 2, 24, false, false, false, Materials.Cadmium, Materials.Caesium, Materials.Lanthanum, Materials.Cerium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.richnuclear", true, 55, 120, 5, 2, 8, false, false, false, Materials.Uranium, Materials.Plutonium, Materials.Thorium, Materials.Thorium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.heavypentele", true, 40, 60, 60, 5, 32, false, false, false, Materials.Arsenic, Materials.Bismuth, Materials.Antimony, Materials.Antimony);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.europa", true, 55, 65, 110, 4, 24, false, false, false, Materials.Magnesite, Materials.BandedIron, Materials.Sulfur, Materials.Opal);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.europacore", true, 5, 15, 5, 2, 16, false, false, false, Materials.Chrome, Materials.Tungstate, Materials.Molybdenum, Materials.Manganese);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.secondlanthanid", true, 10, 40, 10, 3, 24, false, false, false, Materials.Samarium, Materials.Neodymium, Materials.Tartarite, Materials.Tartarite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.quartzspace", true, 40, 80, 20, 3, 16, false, false, false, Materials.Quartzite, Materials.Barite, Materials.CertusQuartz, Materials.CertusQuartz);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.rutile", true, 5, 20, 8, 4, 12, false, false, false, Materials.Rutile, Materials.Titanium, Materials.Bauxite, Materials.MeteoricIron);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.tfgalena", true, 5, 35, 40, 4, 16, false, false, false, Materials.Galena, Materials.Silver, Materials.Lead, Materials.Cryolite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.luvtantalite", true, 20, 30, 10, 4, 16, false, false, false, Materials.Pyrolusite, Materials.Apatite, Materials.Tantalite, Materials.Pyrochlore);
        
        //DO NOT DELETE V THIS V - this is needed so that gregtech generates its Ore Layer's first (the ones up there), which can then be transformed into "GT_Worldgen_GT_Ore_Layer_Space". Also Reflexion is slow.
        try {
            Class clazz = Class.forName("bloodasp.galacticgreg.WorldGenGaGT");
            Constructor constructor=clazz.getConstructor();
            Method method=clazz.getMethod("run");
            method.invoke(constructor.newInstance());
            GT_Log.out.println("Started Galactic Greg ore gen code");
            //this function calls Galactic Greg and enables its generation.
        } catch (Exception e) {
            // ClassNotFound is expected if Galactic Greg is absent, so only report if other problem
            if (!(e instanceof ClassNotFoundException)) {
                GT_Log.err.println("Unable to start Galactic Greg ore gen code");
                e.printStackTrace(GT_Log.err);
            }
        }
        //DO NOT DELETE ^ THIS ^
        
    }
}
