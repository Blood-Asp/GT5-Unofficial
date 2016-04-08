package gregtech.api.util;

import java.util.ArrayList;

public class EX_MaterialUtils {
	
	private static boolean freeMaterialDump = false;
	
	public static void checkFreeIDs(){
		if (freeMaterialDump){
			checkFreeIDsMethod();
		}
	}
	
	private static void checkFreeIDsMethod(){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for (String s: Mats) {
			String r = s;
			r = r.substring(r.indexOf("(") + 1);
			r = r.substring(0, r.indexOf(","));
			arr.add(Integer.parseInt(r));
			GT_Log.err.println(r);
		}
		
		GT_Log.err.println("=================================================================");
		
		for(int elem : arraySortUp(convert(arr))){
			GT_Log.err.println(elem);
			}
		
		GT_Log.err.println("=================================================================");
		
		findFreeIDS(arraySortUp(convert(arr)));
		
		//System.exit(1);
	}
	
	private static int[ ] arraySortUp( int[ ] intArray )
	{
	        int toSwap, indexOfSmallest = 0;
	        int i, j, smallest;

	        for( i = 0; i < intArray.length; i ++ )
	        {               

	            smallest = 1000;

	            for( j = i; j < intArray.length; j ++ )
	            {
	                if( intArray[ j ] < smallest /*&& intArray[j] >= 0 */)
	                {
	                    smallest = intArray[ j ];
	                    indexOfSmallest = j;
	                }                   
	            }

	            toSwap = intArray[ i ];
	            intArray[ i ] = smallest;
	            intArray[ indexOfSmallest ] = toSwap;
	        }

	        return intArray;
	}   
	
	private static int[] convert(ArrayList<Integer> IntegerList) {

	    int s = IntegerList.size();
	    int[] intArray = new int[s];
	    for (int i = 0; i < s; i++) {
	        intArray[i] = IntegerList.get(i).intValue();
	    }
	    return intArray;
	}
	
	private static void findFreeIDS(int[] x){
		ArrayList<Integer> arp = new ArrayList<Integer>();
		int a[] = x;
		int j = a[0];
		for (int i=0;i<a.length;i++)
		{
		    if (j==a[i])
		    {
		        j++;
		        continue;
		    }
		    else
		    {
		        arp.add(j);
		        i--;
		    j++;
		    }
		}
		System.out.println("missing numbers are ");
		for(int r : arp)
		{
			GT_Log.err.println(" " + r);
		}
	}

	public static final String Mats[] = {

		"Aluminium(19, TextureSet.SET_DULL, 10.0F, 128, 2, 1 | 2 | 8 | 32 | 64 | 128, 128, 200, 240,3, 1700, true, false, 3, 1, 1, Dyes.dyeLightBlue, Element.Al, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VOLATUS, 1)))",
		"Americium(103, TextureSet.SET_METALLIC, 1.0F, 0, 3, 1 | 2 | 8 | 32, 200, 200,, 0, false, false, 3, 1, 1, Dyes.dyeLightGray, Element.Am, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Antimony(58, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 2 | 8 | 32, 220, 220, 240, falseyeLightGray, Element.Sb, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.AQUA, 1)))",
		"Argon(24, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 0, 255, 0, 2401, 1, Dyes.dyeGreen, Element.Ar, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 2)))",
		"Arsenic(39, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 2 | 8 | 16 | 32, 255, 255, 0, 0, false, false, 3, 1, 1, Dyes.dyeOrange, Element.As, Arrays.asList(new TC_AspectStack(TC_Aspects.VENENUM, 3)))",
		"Barium(63, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8 | 32, 255, 255, 25alse, fal, Element.Ba, Arrays.asList(new TC_AspectStack(TC_Aspects.VINCULUM, 3)))",
		"Beryllium(8, TextureSet.SET_METALLIC, 14.0F, 64, 2, 1 | 2 | 8 | 32 | 64, 100, 180, 100,1560, 0, false, false, 6, 1, 1, Dyes.dyeGreen, Element.Be, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.LUCRUM, 1)))",
		"Bismuth(90, TextureSet.SET_METALLIC, 6.0F, 64, 1, 1 | 2 | 8 | 32 | 64 | 128, 100, 160, 16, 544, 0, false, false, 2, 1, 1, Dyes.dyeCyan, Element.Bi, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"Boron(9, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8 | 32, 250, 250, 250, 1, 1, 1, Dyes.dyeWhite, Element.B, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Caesium(62, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255301, 0, false, false, 4, 1, 1, Dyes._NULL, Element.Cs, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Calcium(26, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 32, 255, , 1115, 0, false, false, 4, 1, 1, Dyes.dyePink, Element.Ca, Arrays.asList(new TC_AspectStack(TC_Aspects.SANO, 1), new TC_AspectStack(TC_Aspects.TUTAMEN, 1)))",
		"Carbon(10, TextureSet.SET_DULL, 1.0F, 64, 2, 1 | 2 | 32 | 64 | 128, 20, 2, 0, 3800, 0, false, false, 2, 1, 1, Dyes.dyeBlack, Element.C, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"Cadmium(55, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 8 | 32, 50, 50, 60, 0, 0, false, false, 3, 1, 1, Dyes.dyeGray, Element.Cd, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 1), new TC_AspectStack(TC_Aspects.VENENUM, 1)))",
		"Cerium(65, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 0, 1068, 1068, tr 4, 1, 1, Dyes._NULL, Element.Ce, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Chlorine(23, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 255, 0, false, false, 2, 1, 1, Dyes.dyeCyan, Element.Cl, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2), new TC_AspectStack(TC_Aspects.PANNUS, 1)))",
		"Chrome(30, TextureSet.SET_SHINY, 11.0F, 256, 3, 1 | 2 | 8 | 32 | 64 | 128, 255, 23, 0, 0, 2180, 1700, true, false, 5, 1, 1, Dyes.dyePink, Element.Cr, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1)))",
		"Cobalt(33, TextureSet.SET_METALLIC, 8.0F, 512, 3, 1 | 2 | 8 | 32 | 64, 80, 80,alse, 3, 1, 1, Dyes.dyeBlue, Element.Co, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"Copper(35, TextureSet.SET_SHINY, 1.0F, 0, 1, 1 | 2 | 8 | 32 | 128, 255, 1 0, 1357, 0, false, false, 3, 1, 1, Dyes.dyeOrange, Element.Cu, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.PERMUTATIO, 1)))",
		"Deuterium(2, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 255, 255, 14, 0, false, true, 10, 1, 1, Dyes.dyeYellow, Element.D, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 3)))",
		"Dysprosium(73, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 255 0, 0, 1680, 1680, true, false, 4, 1, 1, Dyes._NULL, Element.Dy, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3)))",
		"Empty(0, TextureSet.SET_NONE, 1.0F, 0, 2, 256/*Only for Prefixes which need it*/, 25 0, 0, -1, 0, false, true, 1, 1, 1, Dyes._NULL, Element._NULL, Arrays.asList(new TC_AspectStack(TC_Aspects.VACUOS, 2)))",
		"Erbium(75, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 255, 255, 002, true, false, 4, 1, 1, Dyes._NULL, Element.Er, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Europium(70, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 25 0, 1099, 1099, true, false, 4, 1, 1, Dyes._NULL, Element.Eu, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Fluorine(14, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 255, 255, 255, , 0, false, true, 2, 1, 1, Dyes.dyeGreen, Element.F, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 2)))",
		"Gadolinium(71, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255585, 1585, true, false, 4, 1, 1, Dyes._NULL, Element.Gd, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Gallium(37, TextureSet.SET_SHINY, 1.0F, 64, 2, 1 | 2 | 8 | 32, 220, 220, 20, 0, 302, 0, false, false, 5, 1, 1, Dyes.dyeLightGray, Element.Ga, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ELECTRUM, 1)))",
		"Gold(86, TextureSet.SET_SHINY, 12.0F, 64, 2, 1 | 2 | 8 | 32 | 64 | 128, 255, 255 0, 0, 1337, 0, false, false, 4, 1, 1, Dyes.dyeYellow, Element.Au, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.LUCRUM, 2)))",
		"Holmium(74, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255,  0, 0, 1734, 1734, true, false, 4, 1, 1, Dyes._NULL, Element.Ho, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Hydrogen(1, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 0, 0, 255, 2 1, 15, 14, 0, false, true, 2, 1, 1, Dyes.dyeBlue, Element.H, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 1)))",
		"Helium(4, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 255, 255, 0, 240,  0, 0, 1, 0, false, true, 5, 1, 1, Dyes.dyeYellow, Element.He, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 2)))",
		"Helium_3(5, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 255, 255, 0, 240 0, 0, 1, 0, false, true, 10, 1, 1, Dyes.dyeYellow, Element.He_3, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 3)))",
		"Indium(56, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 64, 0, 128,, 0, false, false, 4, 1, 1, Dyes.dyeGray, Element.In, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Iridium(84, TextureSet.SET_DULL, 6.0F, 5120, 4, 1 | 2 | 8 | 32 | 64 | 128, 240, 2719, 0, false, false, 10, 1, 1, Dyes.dyeWhite, Element.Ir, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1)))",
		"Iron(32, TextureSet.SET_METALLIC, 6.0F, 256, 2, 1 | 2 | 8 | 32 | 64 | 1281811, 0, false, false, 3, 1, 1, Dyes.dyeLightGray, Element.Fe, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3)))",
		"Lanthanum(64, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 255, 23, 1193, true, false, 4, 1, 1, Dyes._NULL, Element.La, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Lead(89, TextureSet.SET_DULL, 8.0F, 64, 1, 1 | 2 | 8 | 32 | 64 | 128, 140, 10, false, false, 3, 1, 1, Dyes.dyePurple, Element.Pb, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ORDO, 1)))",
		"Lithium(6, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 2 | 8 | 32, 225, 220, 255, , false, 4, 1, 1, Dyes.dyeLightBlue, Element.Li, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 2)))",
		"Lutetium(78, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 255, 255, true, false, 4, 1, 1, Dyes._NULL, Element.Lu, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		//"Magic(-128, TextureSet.SET_SHINY, 8.0F, 5120, 5, 1 | 2 | 4 | 8 | 16 | 32 | 64 | 1, 5000, 0, false, false, 7, 1, 1, Dyes.dyePurple, Element.Ma, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 4)))",
		"Magnesium(18, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 200, 20e, false, 3, 1, 1, Dyes.dyePink, Element.Mg, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.SANO, 1)))",
		"Manganese(31, TextureSet.SET_DULL, 7.0F, 512, 2, 1 | 2 | 8 | 32 | 64, 250, 250, 2, false, false, 3, 1, 1, Dyes.dyeWhite, Element.Mn, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3)))",
		"Mercury(87, TextureSet.SET_SHINY, 1.0F, 0, 0, 16 | 32, 255, 220, 220 3, 1, 1, Dyes.dyeLightGray, Element.Hg, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.AQUA, 1), new TC_AspectStack(TC_Aspects.VENENUM, 1)))",
		"Molybdenum(48, TextureSet.SET_SHINY, 7.0F, 512, 2, 1 | 2 | 8 | 32 | 64, 180, 18896, 0, false, false, 1, 1, 1, Dyes.dyeBlue, Element.Mo, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"Neodymium(67, TextureSet.SET_METALLIC, 7.0F, 512, 2, 1 | 2 | 8 | 32 | 64 | 128, 10 0, 0, 1297, 1297, true, false, 4, 1, 1, Dyes._NULL, Element.Nd, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 2)))",
		"Neutronium(129, TextureSet.SET_DULL, 6.0F, 81920, 6, 1 | 2 | 8 | 32 | 64 | 128, 250, 0, 10000, 0, false, false, 20, 1, 1, Dyes.dyeWhite, Element.Nt, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 4), new TC_AspectStack(TC_Aspects.VITREUS, 3), new TC_AspectStack(TC_Aspects.ALIENIS, 2)))",
		"Nickel(34, TextureSet.SET_METALLIC, 6.0F, 64, 2, 1 | 2 | 8 | 32 | 64 | 128, 200,728, 0, false, false, 4, 1, 1, Dyes.dyeLightBlue, Element.Ni, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"Niobium(47, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 190, 180750, true, false, 5, 1, 1, Dyes._NULL, Element.Nb, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ELECTRUM, 1)))",
		"Nitrogen(12, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 0, 150, 200, true, 2, 1, 1, Dyes.dyeCyan, Element.N, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 2)))",
		"Osmium(83, TextureSet.SET_METALLIC, 16.0F, 1280, 4, 1 | 2 | 8 | 32 | 64 | 128, 50, 50, 0, 3306, 0, false, false, 10, 1, 1, Dyes.dyeBlue, Element.Os, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1), new TC_AspectStack(TC_Aspects.NEBRISUM, 1)))",
		"Oxygen(13, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 0, 100, 200,, 54, 0, false, true, 1, 1, 1, Dyes.dyeWhite, Element.O, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 1)))",
		"Palladium(52, TextureSet.SET_SHINY, 8.0F, 512, 2, 1 | 2 | 8 | 32 | 64 | 128, 128,0, 0, 1828, 1828, false, false, 4, 1, 1, Dyes.dyeGray, Element.Pd, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3)))",
		"Phosphor(21, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8 | 32, 255, 2, false, 2, 1, 1, Dyes.dyeYellow, Element.P, Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 2), new TC_AspectStack(TC_Aspects.POTENTIA, 1)))",
		"Platinum(85, TextureSet.SET_SHINY, 12.0F, 64, 2, 1 | 2 | 8 | 32 | 64 | 128, 255, 25 0, 2041, 0, false, false, 6, 1, 1, Dyes.dyeOrange, Element.Pt, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.NEBRISUM, 1)))",
		"Plutonium(100, TextureSet.SET_METALLIC, 6.0F, 512, 3, 1 | 2 | 8 | 32 | 64, 240, 50, 5 0, 0,alse, 6, 1, 1, Dyes.dyeLime, Element.Pu, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 2)))",
		"Plutonium241(101, TextureSet.SET_SHINY, 6.0F, 512, 3, 1 | 2 | 8 | 32 | 64, 250, , 0, 912, 0, false, false, 6, 1, 1, Dyes.dyeLime, Element.Pu_241, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 3)))",
		"Potassium(25, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 2 | 32, 250, 250, 250, , 0, 336, 0, false, false, 2, 1, 1, Dyes.dyeWhite, Element.K, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 1)))",
		"Praseodymium(66, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 328, true, false, 4, 1, 1, Dyes._NULL, Element.Pr, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Promethium(68, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255,  0, 1315, 1315, true, false, 4, 1, 1, Dyes._NULL, Element.Pm, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Radon(93, TextureSet.SET_FLUID, 1.0F, 0, 2, 16 | 32, 255, 0, 2e, true, 5, 1, 1, Dyes.dyePurple, Element.Rn, Arrays.asList(new TC_AspectStack(TC_Aspects.AER, 1), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Rubidium(43, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 240, 3 0, 312, 0, false, false, 4, 1, 1, Dyes.dyeRed, Element.Rb, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 1)))",
		"Samarium(69, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 20, 1345, 1345, true, false, 4, 1, 1, Dyes._NULL, Element.Sm, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Scandium(27, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255 1814, 1814, true, false, 2, 1, 1, Dyes.dyeYellow, Element.Sc, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Silicon(20, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 60, 60, 687, true, false, 1, 1, 1, Dyes.dyeBlack, Element.Si, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.TENEBRAE, 1)))",
		"Silver(54, TextureSet.SET_SHINY, 10.0F, 64, 2, 1 | 2 | 8 | 32 | 64 | 128, 0, 0, 1234, 0, false, false, 3, 1, 1, Dyes.dyeLightGray, Element.Ag, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.LUCRUM, 1)))",
		"Sodium(17, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 32, 0, 0,lse, false, 1, 1, 1, Dyes.dyeBlue, Element.Na, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.LUX, 1)))",
		"Strontium(44, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8 | 320, 0, 1050, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, Element.Sr, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.STRONTIO, 1)))",
		"Sulfur(22, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8 | 32, 200, 200, , false, false, 2, 1, 1, Dyes.dyeYellow, Element.S, Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"Tantalum(80, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 255, 0, 0, 3290, 0, false, false, 4, 1, 1, Dyes._NULL, Element.Ta, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VINCULUM, 1)))",
		"Tellurium(59, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 255, 0, 0, 722, 0, false, false, 4, 1, 1, Dyes.dyeGray, Element.Te, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Terbium(72, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255,29, 1629, true, false, 4, 1, 1, Dyes._NULL, Element.Tb, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Thorium(96, TextureSet.SET_SHINY, 6.0F, 512, 2, 1 | 2 | 8 | 32 | 64, 0, 30,, 2115, 0, false, false, 4, 1, 1, Dyes.dyeBlack, Element.Th, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Thulium(76, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 0, 1818, 1818, true, false, 4, 1, 1, Dyes._NULL, Element.Tm, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Tin(57, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 2 | 8 | 32 | 128, 220, 505, 505, false, false, 3, 1, 1, Dyes.dyeWhite, Element.Sn, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 1)))",
		"Titanium(28, TextureSet.SET_METALLIC, 8.0F, 2560, 3, 1 | 2 | 8 | 32 | 64 | 128, 2, 0, 1941, 1500, true, false, 5, 1, 1, Dyes.dyePurple, Element.Ti, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.TUTAMEN, 1)))",
		"Tritium(3, TextureSet.SET_METALLIC, 1.0F, 0, 2, 16 | 32, 2550, 14, 0, false, true, 10, 1, 1, Dyes.dyeRed, Element.T, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 4)))",
		"Tungsten(81, TextureSet.SET_METALLIC, 8.0F, 5120, 3, 1 | 2 | 8 | 32 | 64 | 0, 0, 3695, 2500, true, false, 4, 1, 1, Dyes.dyeBlack, Element.W, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3), new TC_AspectStack(TC_Aspects.TUTAMEN, 1)))",
		"Uranium(98, TextureSet.SET_METALLIC, 6.0F, 512, 3, 1 | 2 | 8 | 32 | 64, 50, 240,  0, 0, 1405, 0, false, false, 4, 1, 1, Dyes.dyeGreen, Element.U, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Uranium235(97, TextureSet.SET_SHINY, 6.0F, 512, 3, 1 | 2 | 8 | 32 | 64, 70, 25 0, 0, 1405, 0, false, false, 4, 1, 1, Dyes.dyeGreen, Element.U_235, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 2)))",
		"Vanadium(29, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 50, 50183, 2183, true, false, 2, 1, 1, Dyes.dyeBlack, Element.V, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Ytterbium(77, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 255, 2, 0, 1097, 1097, true, false, 4, 1, 1, Dyes._NULL, Element.Yb, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Yttrium(45, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 8 | 32, 220, 2500, 1799, 1799, true, false, 4, 1, 1, Dyes._NULL, Element.Y, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.RADIO, 1)))",
		"Zinc(36, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 2 | 8 | 32, 250, 240, 240, 092, 0, false, false, 2, 1, 1, Dyes.dyeWhite, Element.Zn, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.SANO, 1)))",
		"Endium(770, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 2 | 8, 165, 220, 250, 0,  Endium , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeYellow),)))",
		"DarkSteel(364, TextureSet.SET_DULL, 8.0F, 512, 3, 1 | 2 | 8 | 64, 80, 70, 80, 0,  Dark Steel , 0, 0, 1811, 0, false, false, 5, 1, 1, Dyes.dyePurple),)))",
		"Staballoy(319, TextureSet.SET_ROUGH, 10.0F, 5120, 4, 1 | 2 | 8 | 16 | 32 | 64 | 128, 48, 55, 46, 0,  Staballoy , 0, 0, 1500, 2800, true, false, 1, 1, 1, Dyes.dyeGreen, 2, Arrays.asList(new MaterialStack(Titanium, 1), new MaterialStack(Uranium, 8)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 8), new TC_AspectStack(TC_Aspects.STRONTIO, 3)))",
		"Bedrockium(780, TextureSet.SET_ROUGH, 8.0F, 8196, 3, 1 | 2 | 16 | 32 | 64 | 128, 39, 39, 39, 0,  Bedrockium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, Arrays.asList(new TC_AspectStack(TC_Aspects.VACUOS, 8), new TC_AspectStack(TC_Aspects.TUTAMEN, 3)))",
		"BloodSteel(781, TextureSet.SET_MAGNETIC, 12.0F, 1024, 3, 1 | 2 | 8 | 16 | 32 | 64 | 128, 142, 28, 0, 0,  Blood Steel , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeRed, Arrays.asList(new TC_AspectStack(TC_Aspects.VICTUS, 8), new TC_AspectStack(TC_Aspects.IGNIS, 3)))",
		"VoidMetal(782, TextureSet.SET_DULL, 6.0F, 2560, 2, 1 | 2 | 16 | 32 | 64 | 128, 5, 8, 12, 0,  Void Metal , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeBlack)", 
		"ConductiveIron(783, TextureSet.SET_FINE, 1.0F, 0, 2, 1 | 2, 164, 109, 100, 0,  Conductive Iron , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeRed)",   
		"ElectricalSteel(784, TextureSet.SET_MAGNETIC, 1.0F, 0, 2, 1 | 2 | 64 | 128, 194, 194, 194, 0,  Electrical Steel , 0, 0, 1811, 1000, true, false, 3, 1, 1, Dyes.dyeLightGray)",  
		"EnergeticAlloy(785, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 2 | 64 | 128, 173, 76, 0, 0,  Energetic Alloy , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeOrange)",   
		"VibrantAlloy(786, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 2 | 64 | 128, 158, 206, 0, 0,  Vibrant Alloy , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeLime)",   
		"PulsatingIron(787, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2 | 64 | 128, 50, 91, 21, 0,  Pulsating Iron , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGreen)",
		"Alduorite(485, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 8 | 16, 159, 180, 180, 0,  Alduorite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Amber(514, TextureSet.SET_RUBY, 4.0F, 128, 2, 1 | 4 | 8 | 64, 255, 128, 0, 127,  Amber , 5, 3, -1, 0, false, true, 1, 1, 1, Dyes.dyeOrange, Arrays.asList(new TC_AspectStack(TC_Aspects.VINCULUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 1)))",
		"Angmallen(958, TextureSet.SET_METALLIC, 10.0F, 128, 2, 1 | 2 | 8 | 16 | 64, 215, 225, 138, 0,  Angmallen , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Atlarus(965, TextureSet.SET_METALLIC, 6.0F, 64, 2, 1 | 2 | 8 | 64, 255, 255, 255, 0,  Atlarus , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Blizz(851, TextureSet.SET_SHINY, 1.0F, 0, 2, 1, 220, 233, 255, 0,  Blizz , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes._NULL)",
		"Blueschist(852, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 255, 255, 255, 0,  Blueschist , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeLightBlue)",   
		"Bluestone(813, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 255, 255, 255, 0,  Bluestone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlue)",   
		"Carmot(962, TextureSet.SET_METALLIC, 16.0F, 128, 1, 1 | 2 | 8 | 64, 217, 205, 140, 0,  Carmot , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Celenegil(964, TextureSet.SET_METALLIC, 10.0F, 4096, 2, 1 | 2 | 8 | 16 | 64, 148, 204, 72, 0,  Celenegil , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"CertusQuartz(516, TextureSet.SET_QUARTZ, 5.0F, 32, 1, 1 | 4 | 8 | 64, 210, 210, 230, 0,  Certus Quartz , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeLightGray, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 1), new TC_AspectStack(TC_Aspects.VITREUS, 1)))",
		"Ceruclase(952, TextureSet.SET_METALLIC, 6.0F, 1280, 2, 1 | 2 | 8, 140, 189, 208, 0,  Ceruclase , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"CobaltHexahydrate(853, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 16, 80, 80, 250, 0,  Cobalt Hexahydrate , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlue)",   
		"ConstructionFoam(854, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 16, 128, 128, 128, 0,  Construction Foam , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray)",   
		"Chert(857, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 255, 255, 255, 0,  Chert , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes._NULL)",
		"CrudeOil(858, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 10, 10, 10, 0,  Crude Oil , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack)",	
		"Dacite(859, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 255, 255, 255, 0,  Dacite , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeLightGray)",    
		"DarkIron(342, TextureSet.SET_DULL, 7.0F, 384, 3, 1 | 2 | 8 | 64, 55, 40, 60, 0,  Dark Iron , 0, 0, -1, 0, false, false, 5, 1, 1, Dyes.dyePurple)",   
		"Desh(884, TextureSet.SET_DULL, 1.0F, 1280, 3, 1 | 2 | 8 | 64 | 128, 40, 40, 40, 0,  Desh , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack)",   
		"Dilithium(515, TextureSet.SET_DIAMOND, 1.0F, 0, 1, 1 | 4 | 8 | 16, 255, 250, 250, 127,  Dilithium , 0, 0, -1, 0, false, true, 1, 1, 1, Dyes.dyeWhite)",   
		"Duranium(328, TextureSet.SET_METALLIC, 8.0F, 1280, 4, 1 | 2 | 64, 255, 255, 255, 0,  Duranium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray)",   
		"Eclogite(860, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 255, 255, 255, 0,  Eclogite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"ElectrumFlux(320, TextureSet.SET_SHINY, 16.0F, 512, 3, 1 | 2 | 64, 255, 255, 120, 0,  Fluxed Electrum , 0, 0, 3000, 3000, true, false, 1, 1, 1, Dyes.dyeYellow)",  
		"Emery(861, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 255, 255, 255, 0,  Emery , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Enderium(321, TextureSet.SET_DULL, 8.0F, 256, 3, 1 | 2 | 64, 89, 145, 135, 0,  Enderium , 0, 0, 3000, 3000, true, false, 1, 1, 1, Dyes.dyeGreen, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ALIENIS, 1)))",
		"Epidote(862, TextureSet.SET_DULL, 1.0F, 0, 2, 1, 255, 255, 255, 0,  Epidote , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes._NULL)",
		"Eximite(959, TextureSet.SET_METALLIC, 5.0F, 2560, 3, 1 | 2 | 8 | 64, 124, 90, 150, 0,  Eximite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"FierySteel(346, TextureSet.SET_FIERY, 8.0F, 256, 3, 1 | 2 | 16 | 64 | 128, 64, 0, 0, 0,  Fiery Steel , 5, 2048, 1811, 1000, true, false, 1, 1, 1, Dyes.dyeRed, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 3), new TC_AspectStack(TC_Aspects.IGNIS, 3), new TC_AspectStack(TC_Aspects.CORPUS, 3)))",
		"Firestone(347, TextureSet.SET_QUARTZ, 6.0F, 1280, 3, 1 | 4 | 8 | 64, 200, 20, 0, 0,  Firestone , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeRed)", 
		"FoolsRuby(512, TextureSet.SET_RUBY, 1.0F, 0, 2, 1 | 4 | 8, 255, 100, 100, 127,  Ruby , 0, 0, -1, 0, false, true, 3, 1, 1, Dyes.dyeRed, Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 2), new TC_AspectStack(TC_Aspects.VITREUS, 2)))",
		"Force(521, TextureSet.SET_DIAMOND, 10.0F, 128, 3, 1 | 2 | 4 | 8 | 64 | 128, 255, 255, 0, 0,  Force , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeYellow, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 5)))",
		"Forcicium(518, TextureSet.SET_DIAMOND, 1.0F, 0, 1, 1 | 4 | 8 | 16, 50, 50, 70, 0,  Forcicium , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGreen, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2)))",
		"Forcillium(519, TextureSet.SET_DIAMOND, 1.0F, 0, 1, 1 | 4 | 8 | 16, 50, 50, 70, 0,  Forcillium , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGreen, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2)))",
		"Gabbro(863, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Gabbro , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes._NULL)",
		"Glowstone(811, TextureSet.SET_SHINY, 1.0F, 0, 1, 1 | 16, 255, 255, 0, 0,  Glowstone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, Arrays.asList(new TC_AspectStack(TC_Aspects.LUX, 2), new TC_AspectStack(TC_Aspects.SENSUS, 1)))",
		"Gneiss(864, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Gneiss , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes._NULL)",
		"Graphite(865, TextureSet.SET_DULL, 5.0F, 32, 2, 1 | 8 | 16 | 64, 128, 128, 128, 0,  Graphite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGray, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"Graphene(819, TextureSet.SET_DULL, 6.0F, 32, 1, 1 | 8 | 64, 128, 128, 128, 0,  Graphene , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGray, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.ELECTRUM, 1)))",
		"Greenschist(866, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Green Schist , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGreen)",  
		"Greenstone(867, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Greenstone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGreen)", 
		"Greywacke(868, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Greywacke , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray)", 
		"Haderoth(963, TextureSet.SET_METALLIC, 10.0F, 3200, 3, 1 | 2 | 8 | 16 | 64, 119, 52, 30, 0,  Haderoth , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Hepatizon(957, TextureSet.SET_METALLIC, 12.0F, 128, 2, 1 | 2 | 8 | 16 | 64, 117, 94, 117, 0,  Hepatizon , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"HSLA(322, TextureSet.SET_METALLIC, 6.0F, 500, 2, 1 | 2 | 64 | 128, 128, 128, 128, 0,  HSLA Steel , 0, 0, 1811, 1000, true, false, 3, 1, 1, Dyes._NULL, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.ORDO, 1)))",
		"Ignatius(950, TextureSet.SET_METALLIC, 12.0F, 512, 2, 1 | 2 | 16, 255, 169, 83, 0,  Ignatius , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Infuscolium(490, TextureSet.SET_METALLIC, 6.0F, 64, 2, 1 | 2 | 8 | 16 | 64, 146, 33, 86, 0,  Infuscolium , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes._NULL)",
		"InfusedGold(323, TextureSet.SET_SHINY, 12.0F, 64, 3, 1 | 2 | 8 | 64 | 128, 255, 200, 60, 0,  Infused Gold , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeYellow)",    "InfusedAir(540, TextureSet.SET_SHARDS, 8.0F, 64, 3, 1 | 4 | 8 | 64 | 128, 255, 255, 0, 0,  Aer , 5, 160, -1, 0, false, true, 3, 1, 1, Dyes.dyeYellow, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.AER, 2)))",
		"InfusedFire(541, TextureSet.SET_SHARDS, 8.0F, 64, 3, 1 | 4 | 8 | 64 | 128, 255, 0, 0, 0,  Ignis , 5, 320, -1, 0, false, true, 3, 1, 1, Dyes.dyeRed, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.IGNIS, 2)))",
		"InfusedEarth(542, TextureSet.SET_SHARDS, 8.0F, 256, 3, 1 | 4 | 8 | 64 | 128, 0, 255, 0, 0,  Terra , 5, 160, -1, 0, false, true, 3, 1, 1, Dyes.dyeGreen, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.TERRA, 2)))",
		"InfusedWater(543, TextureSet.SET_SHARDS, 8.0F, 64, 3, 1 | 4 | 8 | 64 | 128, 0, 0, 255, 0,  Aqua , 5, 160, -1, 0, false, true, 3, 1, 1, Dyes.dyeBlue, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.AQUA, 2)))",
		"InfusedEntropy(544, TextureSet.SET_SHARDS, 32.0F, 64, 4, 1 | 4 | 8 | 64 | 128, 62, 62, 62, 0,  Perditio , 5, 320, -1, 0, false, true, 3, 1, 1, Dyes.dyeBlack, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.PERDITIO, 2)))",
		"InfusedOrder(545, TextureSet.SET_SHARDS, 8.0F, 64, 3, 1 | 4 | 8 | 64 | 128, 252, 252, 252, 0,  Ordo , 5, 240, -1, 0, false, true, 3, 1, 1, Dyes.dyeWhite, Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 1), new TC_AspectStack(TC_Aspects.ORDO, 2)))",
		"Inolashite(954, TextureSet.SET_NONE, 8.0F, 2304, 3, 1 | 2 | 8 | 16 | 64, 148, 216, 187, 0,  Inolashite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Jade(537, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 8, 0, 100, 0, 0,  Jade , 0, 0, -1, 0, false, false, 5, 1, 1, Dyes.dyeGreen, Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Jasper(511, TextureSet.SET_EMERALD, 1.0F, 0, 2, 1 | 4 | 8, 200, 80, 80, 100,  Jasper , 0, 0, -1, 0, false, true, 3, 1, 1, Dyes.dyeRed, Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 4), new TC_AspectStack(TC_Aspects.VITREUS, 2)))",
		"Kalendrite(953, TextureSet.SET_METALLIC, 5.0F, 2560, 3, 1 | 2 | 16, 170, 91, 189, 0,  Kalendrite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Komatiite(869, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Komatiite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow)",    "Lava(700, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 255, 64, 0, 0,  Lava , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange)",    "Lemurite(486, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 16, 219, 219, 219, 0,  Lemurite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"MeteoricIron(340, TextureSet.SET_METALLIC, 6.0F, 384, 2, 1 | 2 | 8 | 64, 100, 50, 80, 0,  Meteoric Iron , 0, 0, 1811, 0, false, false, 1, 1, 1, Dyes.dyeGray, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"MeteoricSteel(341, TextureSet.SET_METALLIC, 6.0F, 768, 2, 1 | 2 | 64, 50, 25, 40, 0,  Meteoric Steel , 0, 0, 1811, 1000, true, false, 1, 1, 1, Dyes.dyeGray, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1), new TC_AspectStack(TC_Aspects.ORDO, 1)))",
		"Meutoite(487, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8 | 16, 95, 82, 105, 0,  Meutoite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Migmatite(872, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Migmatite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Naquadah(324, TextureSet.SET_METALLIC, 6.0F, 1280, 4, 1 | 2 | 8 | 16 | 64, 50, 50, 50, 0,  Naquadah , 0, 0, 3000, 3000, true, false, 10, 1, 1, Dyes.dyeBlack, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3), new TC_AspectStack(TC_Aspects.RADIO, 1), new TC_AspectStack(TC_Aspects.NEBRISUM, 1)))",
		"NaquadahAlloy(325, TextureSet.SET_METALLIC, 8.0F, 5120, 5, 1 | 2 | 64 | 128, 40, 40, 40, 0,  Naquadah Alloy , 0, 0, 3000, 3000, true, false, 10, 1, 1, Dyes.dyeBlack, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 4), new TC_AspectStack(TC_Aspects.NEBRISUM, 1)))",
		"NaquadahEnriched(326, TextureSet.SET_METALLIC, 6.0F, 1280, 4, 1 | 2 | 8 | 16 | 64, 50, 50, 50, 0,  Enriched Naquadah , 0, 0, 3000, 3000, true, false, 15, 1, 1, Dyes.dyeBlack, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 3), new TC_AspectStack(TC_Aspects.RADIO, 2), new TC_AspectStack(TC_Aspects.NEBRISUM, 2)))",
		"Naquadria(327, TextureSet.SET_SHINY, 1.0F, 512, 4, 1 | 2 | 8 | 64, 30, 30, 30, 0,  Naquadria , 0, 0, 3000, 3000, true, false, 20, 1, 1, Dyes.dyeBlack, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 4), new TC_AspectStack(TC_Aspects.RADIO, 3), new TC_AspectStack(TC_Aspects.NEBRISUM, 3)))",
		"NetherBrick(814, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 100, 0, 0, 0,  Nether Brick , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeRed, Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"NetherQuartz(522, TextureSet.SET_QUARTZ, 1.0F, 32, 1, 1 | 4 | 8 | 64, 230, 210, 210, 0,  Nether Quartz , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeWhite, Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 1), new TC_AspectStack(TC_Aspects.VITREUS, 1)))",
		"NetherStar(506, TextureSet.SET_NETHERSTAR, 1.0F, 5120, 4, 1 | 4 | 64, 255, 255, 255, 0,  Nether Star , 5, 50000, -1, 0, false, false, 15, 1, 1, Dyes.dyeWhite)",    "Nikolite(812, TextureSet.SET_SHINY, 1.0F, 0, 1, 1 | 8, 60, 180, 200, 0,  Nikolite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeCyan, Arrays.asList(new TC_AspectStack(TC_Aspects.ELECTRUM, 2)))",
		"Oilsands(878, TextureSet.SET_NONE, 1.0F, 0, 1, 1 | 8, 10, 10, 10, 0,  Oilsands , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Orichalcum(966, TextureSet.SET_METALLIC, 4.5F, 3456, 3, 1 | 2 | 8 | 64, 84, 122, 56, 0,  Orichalcum , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Oureclase(961, TextureSet.SET_METALLIC, 6.0F, 1920, 3, 1 | 2 | 8 | 64, 183, 98, 21, 0,  Oureclase , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Prometheum(960, TextureSet.SET_METALLIC, 8.0F, 512, 1, 1 | 2 | 8 | 64, 90, 129, 86, 0,  Prometheum , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Quartzite(523, TextureSet.SET_QUARTZ, 1.0F, 0, 1, 1 | 4 | 8, 210, 230, 210, 0,  Quartzite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeWhite)",    
		//"RefinedGlowstone(-326, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 2, 255, 255, 0, 0,  Refined Glowstone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow)",    
		//"RefinedObsidian(-327, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 2, 80, 50, 100, 0,  Refined Obsidian , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePurple)",   
		"Rhyolite(875, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Rhyolite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Rubracium(488, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8 | 16, 151, 45, 45, 0,  Rubracium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Sanguinite(955, TextureSet.SET_METALLIC, 3.0F, 4480, 4, 1 | 2 | 8, 185, 0, 0, 0,  Sanguinite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Siltstone(876, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Siltstone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Tartarite(956, TextureSet.SET_METALLIC, 20.0F, 7680, 5, 1 | 2 | 8 | 16, 255, 118, 60, 0,  Tartarite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Tritanium(329, TextureSet.SET_METALLIC, 6.0F, 2560, 4, 1 | 2 | 64, 255, 255, 255, 0,  Tritanium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ORDO, 2)))",
		"UUAmplifier(721, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 96, 0, 128, 0,  UU-Amplifier , 0, 0, -1, 0, false, false, 10, 1, 1, Dyes.dyePink)",   
		"UUMatter(703, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 128, 0, 196, 0,  UU-Matter , 0, 0, -1, 0, false, false, 10, 1, 1, Dyes.dyePink)",	
		"Vulcanite(489, TextureSet.SET_METALLIC, 6.0F, 64, 2, 1 | 2 | 8 | 16 | 64, 255, 132, 72, 0,  Vulcanite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"Vyroxeres(951, TextureSet.SET_METALLIC, 9.0F, 768, 3, 1 | 2 | 8 | 64, 85, 224, 1, 0,  Vyroxeres , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL)",
		"BioFuel(705, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 255, 128, 0, 0,  Biofuel , 0, 6, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange)",   
		"Biomass(704, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 0, 255, 0, 0,  Biomass , 3, 8, -1, 0, false, false, 1, 1, 1, Dyes.dyeGreen)",    
		"Cheese(894, TextureSet.SET_FINE, 1.0F, 0, 0, 1 | 8, 255, 255, 0, 0,  Cheese , 0, 0, 320, 0, false, false, 1, 1, 1, Dyes.dyeYellow)",   
		"Chili(895, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 200, 0, 0, 0,  Chili , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeRed)",   
		"Chocolate(886, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 190, 95, 0, 0,  Chocolate , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown)",   
		"CoalFuel(710, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 50, 50, 70, 0,  Coalfuel , 0, 16, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack)",   
		"Cocoa(887, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 190, 95, 0, 0,  Cocoa , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown)",   
		"Coffee(888, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 150, 75, 0, 0,  Coffee , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown)",   
		"Creosote(712, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 128, 64, 0, 0,  Creosote , 3, 8, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown)",   
		"Ethanol(706, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 255, 128, 0, 0,  Ethanol , 0, 128, -1, 0, false, false, 1, 1, 1, Dyes.dyePurple)",  
		"FishOil(711, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 255, 196, 0, 0,  Fish Oil , 3, 2, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, Arrays.asList(new TC_AspectStack(TC_Aspects.CORPUS, 2)))",
		"Fuel(708, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 255, 255, 0, 0,  Diesel , 0, 128, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow)",  
		"Glue(726, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 200, 196, 0, 0,  Glue , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, Arrays.asList(new TC_AspectStack(TC_Aspects.LIMUS, 2)))",
		"Gunpowder(800, TextureSet.SET_DULL, 1.0F, 0, 0, 1, 128, 128, 128, 0,  Gunpowder , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 3), new TC_AspectStack(TC_Aspects.IGNIS, 4)))",
		"FryingOilHot(727, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 200, 196, 0, 0,  Hot Frying Oil , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 1), new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"Honey(725, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 210, 200, 0, 0,  Honey , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow)",   
		"Lubricant(724, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 255, 196, 0, 0,  Lubricant , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1)))",
		"McGuffium239(999, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 200, 50, 150, 0,  Mc Guffium 239 , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePink, Arrays.asList(new TC_AspectStack(TC_Aspects.ALIENIS, 8), new TC_AspectStack(TC_Aspects.PERMUTATIO, 8), new TC_AspectStack(TC_Aspects.SPIRITUS, 8), new TC_AspectStack(TC_Aspects.AURAM, 8), new TC_AspectStack(TC_Aspects.VITIUM, 8), new TC_AspectStack(TC_Aspects.RADIO, 8), new TC_AspectStack(TC_Aspects.MAGNETO, 8), new TC_AspectStack(TC_Aspects.ELECTRUM, 8), new TC_AspectStack(TC_Aspects.NEBRISUM, 8), new TC_AspectStack(TC_Aspects.STRONTIO, 8)))",
		"MeatRaw(892, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 255, 100, 100, 0,  Raw Meat , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePink)",  
		"MeatCooked(893, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 150, 60, 20, 0,  Cooked Meat , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePink)",    
		"Milk(885, TextureSet.SET_FINE, 1.0F, 0, 0, 1 | 16, 254, 254, 254, 0,  Milk , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, Arrays.asList(new TC_AspectStack(TC_Aspects.SANO, 2)))",
		"Oil(707, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 10, 10, 10, 0,  Oil , 3, 16, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack)", 
		"Paper(879, TextureSet.SET_PAPER, 1.0F, 0, 0, 1, 250, 250, 250, 0,  Paper , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, Arrays.asList(new TC_AspectStack(TC_Aspects.COGNITO, 1)))",
		"RareEarth(891, TextureSet.SET_FINE, 1.0F, 0, 0, 1, 128, 128, 100, 0,  Rare Earth , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 1), new TC_AspectStack(TC_Aspects.LUCRUM, 1)))",
		"SeedOil(713, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 196, 255, 0, 0,  Seed Oil , 3, 2, -1, 0, false, false, 1, 1, 1, Dyes.dyeLime, Arrays.asList(new TC_AspectStack(TC_Aspects.GRANUM, 2)))",
		"SeedOilHemp(722, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 196, 255, 0, 0,  Hemp Seed Oil , 3, 2, -1, 0, false, false, 1, 1, 1, Dyes.dyeLime, Arrays.asList(new TC_AspectStack(TC_Aspects.GRANUM, 2)))",
		"SeedOilLin(723, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 196, 255, 0, 0,  Lin Seed Oil , 3, 2, -1, 0, false, false, 1, 1, 1, Dyes.dyeLime, Arrays.asList(new TC_AspectStack(TC_Aspects.GRANUM, 2)))",
		"Stone(299, TextureSet.SET_ROUGH, 4.0F, 32, 1, 1 | 64 | 128, 205, 205, 205, 0,  Stone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, Arrays.asList(new TC_AspectStack(TC_Aspects.TERRA, 1)))",
		"Wheat(881, TextureSet.SET_POWDER, 1.0F, 0, 0, 1, 255, 255, 196, 0,  Wheat , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, Arrays.asList(new TC_AspectStack(TC_Aspects.MESSIS, 2)))",
		"Osmiridium(317, TextureSet.SET_METALLIC, 8.0F, 3000, 4, 1 | 2 | 64 | 128, 100, 100, 255, 0,  Osmiridium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightBlue)",  " Sunnarium(318, TextureSet.SET_SHINY, 1.0F, 0, 1, 1 | 2, 255, 255, 0, 0,  Sunnarium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow)",   
		"Endstone(808, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 255, 255, 255, 0,  Endstone , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeYellow)",  " Netherrack(807, TextureSet.SET_DULL, 1.0F, 0, 0, 1, 200, 0, 0, 0,  Netherrack , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeRed)",   
		"Almandine(820, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1 | 8, 255, 0, 0, 0,  Almandine , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeRed, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Iron, 3), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12)))",
		"Andradite(821, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1 | 8, 150, 120, 0, 0,  Andradite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeYellow, 1, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Iron, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12)))",
		"AnnealedCopper(345, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 2 | 128, 255, 120, 20, 0,  Annealed Copper , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Copper, 1)))",
		"Asbestos(946, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 230, 230, 230, 0,  Asbestos , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 2), new MaterialStack(Hydrogen, 4), new MaterialStack(Oxygen, 9)))", // Mg3Si2O5(OH)4
		"Ash(815, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 150, 150, 150, 0,  Ashes , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, 2, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.PERDITIO, 1)))",
		"BandedIron(917, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 145, 90, 90, 0,  Banded Iron , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown, 1, Arrays.asList(new MaterialStack(Iron, 2), new MaterialStack(Oxygen, 3)))",
		"BatteryAlloy(315, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 2, 156, 124, 160, 0,  Battery Alloy , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePurple, 2, Arrays.asList(new MaterialStack(Lead, 4), new MaterialStack(Antimony, 1)))",
		"Bauxite(822, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 200, 100, 0, 0,  Bauxite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeBrown, 1, Arrays.asList(new MaterialStack(Titanium, 1), new MaterialStack(Aluminium, 16), new MaterialStack(Hydrogen, 10), new MaterialStack(Oxygen, 12)))",
		"BlueTopaz(513, TextureSet.SET_GEM_HORIZONTAL, 7.0F, 256, 3, 1 | 4 | 8 | 64, 0, 0, 255, 127,  Blue Topaz , 0, 0, -1, 0, false, true, 3, 1, 1, Dyes.dyeBlue, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 1), new MaterialStack(Fluorine, 2), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 6)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4)))",
		"Bone(806, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 250, 250, 250, 0,  Bone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 0, Arrays.asList(new MaterialStack(Calcium, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.MORTUUS, 2), new TC_AspectStack(TC_Aspects.CORPUS, 1)))",
		"Brass(301, TextureSet.SET_METALLIC, 7.0F, 96, 1, 1 | 2 | 64 | 128, 255, 180, 0, 0,  Brass , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Zinc, 1), new MaterialStack(Copper, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"Bronze(300, TextureSet.SET_METALLIC, 6.0F, 192, 2, 1 | 2 | 64 | 128, 255, 128, 0, 0,  Bronze , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Copper, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"BrownLimonite(930, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8, 200, 100, 0, 0,  Brown Limonite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown, 2, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Hydrogen, 1), new MaterialStack(Oxygen, 2)))", // FeO(OH)
		"Calcite(823, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 250, 230, 220, 0,  Calcite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Oxygen, 3)))",
		"Cassiterite(824, TextureSet.SET_METALLIC, 1.0F, 0, 1, 8, 220, 220, 220, 0,  Cassiterite , 0, 0, -1, 0, false, false, 4, 3, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Oxygen, 2)))",
		"CassiteriteSand(937, TextureSet.SET_SAND, 1.0F, 0, 1, 8, 220, 220, 220, 0,  Cassiterite Sand , 0, 0, -1, 0, false, false, 4, 3, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Oxygen, 2)))",
		"Celestine(913, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 200, 205, 240, 0,  Celestine , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, 1, Arrays.asList(new MaterialStack(Strontium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4)))",
		"Chalcopyrite(855, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 160, 120, 40, 0,  Chalcopyrite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, 1, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Iron, 1), new MaterialStack(Sulfur, 2)))",
		"Chalk(856, TextureSet.SET_FINE, 1.0F, 0, 2, 1, 250, 250, 250, 0,  Chalk , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Oxygen, 3)))",
		"Charcoal(536, TextureSet.SET_FINE, 1.0F, 0, 1, 1 | 4, 100, 70, 70, 0,  Charcoal , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2), new TC_AspectStack(TC_Aspects.IGNIS, 2)))",
		"Chromite(825, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8, 35, 20, 15, 0,  Chromite , 0, 0, 1700, 1700, true, false, 6, 1, 1, Dyes.dyePink, 1, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Chrome, 2), new MaterialStack(Oxygen, 4)))",
		"ChromiumDioxide(361, TextureSet.SET_DULL, 11.0F, 256, 3, 1 | 2, 230, 200, 200, 0,  Chromium Dioxide , 0, 0, 650, 650, false, false, 5, 3, 1, Dyes.dyePink, 1, Arrays.asList(new MaterialStack(Chrome, 1), new MaterialStack(Oxygen, 2)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MACHINA, 1)))",
		"Cinnabar(826, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1 | 8, 150, 0, 0, 0,  Cinnabar , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeBrown, 2, Arrays.asList(new MaterialStack(Mercury, 1), new MaterialStack(Sulfur, 1)))",
		"Clay(805, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1, 200, 200, 220, 0,  Clay , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeLightBlue, 1, Arrays.asList(new MaterialStack(Sodium, 2), new MaterialStack(Lithium, 1), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 2)))",
		"Coal(535, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1 | 4 | 8, 70, 70, 70, 0,  Coal , 0, 0, -1, 0, false, false, 2, 2, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.POTENTIA, 2), new TC_AspectStack(TC_Aspects.IGNIS, 2)))",
		"Cobaltite(827, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8, 80, 80, 250, 0,  Cobaltite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeBlue, 1, Arrays.asList(new MaterialStack(Cobalt, 1), new MaterialStack(Arsenic, 1), new MaterialStack(Sulfur, 1)))",
		"Cooperite(828, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8, 255, 255, 200, 0,  Sheldonite , 0, 0, -1, 0, false, false, 5, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Platinum, 3), new MaterialStack(Nickel, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Palladium, 1)))",
		"Cupronickel(310, TextureSet.SET_METALLIC, 6.0F, 64, 1, 1 | 2 | 64, 227, 150, 128, 0,  Cupronickel , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Nickel, 1)))",
		"DarkAsh(816, TextureSet.SET_DULL, 1.0F, 0, 1, 1, 50, 50, 50, 0,  Dark Ashes , 0, 0, -1, 0, false, false, 1, 2, 1, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.IGNIS, 1), new TC_AspectStack(TC_Aspects.PERDITIO, 1)))",
		"DeepIron(829, TextureSet.SET_METALLIC, 6.0F, 384, 2, 1 | 2 | 8 | 64, 150, 140, 140, 0,  Deep Iron , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyePink, 2, Arrays.asList(new MaterialStack(Iron, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"Diamond(500, TextureSet.SET_DIAMOND, 8.0F, 1280, 3, 1 | 4 | 8 | 64 | 128, 200, 255, 255, 127,  Diamond , 0, 0, -1, 0, false, true, 5, 64, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3), new TC_AspectStack(TC_Aspects.LUCRUM, 4)))",
		"Electrum(303, TextureSet.SET_SHINY, 12.0F, 64, 2, 1 | 2 | 8 | 64 | 128, 255, 255, 100, 0,  Electrum , 0, 0, -1, 0, false, false, 4, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Silver, 1), new MaterialStack(Gold, 1)))",
		"Emerald(501, TextureSet.SET_EMERALD, 7.0F, 256, 2, 1 | 4 | 8 | 64, 80, 255, 80, 127,  Emerald , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyeGreen, 1, Arrays.asList(new MaterialStack(Beryllium, 3), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 6), new MaterialStack(Oxygen, 18)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3), new TC_AspectStack(TC_Aspects.LUCRUM, 5)))",
		"Galena(830, TextureSet.SET_DULL, 1.0F, 0, 3, 1 | 8, 100, 60, 100, 0,  Galena , 0, 0, -1, 0, false, false, 4, 1, 1, Dyes.dyePurple, 1, Arrays.asList(new MaterialStack(Lead, 3), new MaterialStack(Silver, 3), new MaterialStack(Sulfur, 2)))",
		"Garnierite(906, TextureSet.SET_METALLIC, 1.0F, 0, 3, 1 | 8, 50, 200, 70, 0,  Garnierite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightBlue, 1, Arrays.asList(new MaterialStack(Nickel, 1), new MaterialStack(Oxygen, 1)))",
		"Glyceryl(714, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 0, 150, 150, 0,  Glyceryl Trinitrate , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeCyan, 1, Arrays.asList(new MaterialStack(Carbon, 3), new MaterialStack(Hydrogen, 5), new MaterialStack(Nitrogen, 3), new MaterialStack(Oxygen, 9)))",
		"GreenSapphire(504, TextureSet.SET_GEM_HORIZONTAL, 7.0F, 256, 2, 1 | 4 | 8 | 64, 100, 200, 130, 127,  Green Sapphire , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyeCyan, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Grossular(831, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1 | 8, 200, 100, 0, 0,  Grossular , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12)))",
		"HolyWater(729, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 0, 0, 255, 0,  Holy Water , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlue, 0, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2), new TC_AspectStack(TC_Aspects.AURAM, 1)))",
		"Ice(702, TextureSet.SET_SHINY, 1.0F, 0, 0, 1 | 16, 200, 200, 255, 0,  Ice , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlue, 0, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.GELUM, 2)))",
		"Ilmenite(918, TextureSet.SET_METALLIC, 1.0F, 0, 3, 1 | 8, 70, 55, 50, 0,  Ilmenite , 0, 0, -1, 0, false, false, 1, 2, 1, Dyes.dyePurple, 1, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Titanium, 1), new MaterialStack(Oxygen, 3)))",
		"Invar(302, TextureSet.SET_METALLIC, 6.0F, 256, 2, 1 | 2 | 64 | 128, 180, 180, 120, 0,  Invar , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown, 2, Arrays.asList(new MaterialStack(Iron, 2), new MaterialStack(Nickel, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.GELUM, 1)))",
		"Kanthal(312, TextureSet.SET_METALLIC, 6.0F, 64, 2, 1 | 2 | 64, 194, 210, 223, 0,  Kanthal , 0, 0, 1800, 1800, true, false, 1, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Aluminium, 1), new MaterialStack(Chrome, 1)))",
		"Lazurite(524, TextureSet.SET_LAPIS, 1.0F, 0, 1, 1 | 4 | 8, 100, 120, 255, 0,  Lazurite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeCyan, 1, Arrays.asList(new MaterialStack(Aluminium, 6), new MaterialStack(Silicon, 6), new MaterialStack(Calcium, 8), new MaterialStack(Sodium, 8)))",
		"Magnalium(313, TextureSet.SET_DULL, 6.0F, 256, 2, 1 | 2 | 64 | 128, 200, 190, 255, 0,  Magnalium , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightBlue, 2, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Aluminium, 2)))",
		"Magnesite(908, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 250, 250, 180, 0,  Magnesite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePink, 1, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Oxygen, 3)))",
		"Magnetite(870, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 30, 30, 30, 0,  Magnetite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Iron, 3), new MaterialStack(Oxygen, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"Methane(715, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 255, 255, 255, 0,  Methane , 1, 45, -1, 0, false, false, 3, 1, 1, Dyes.dyeMagenta, 1, Arrays.asList(new MaterialStack(Carbon, 1), new MaterialStack(Hydrogen, 4)))",
		"Molybdenite(942, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 25, 25, 25, 0,  Molybdenite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlue, 1, Arrays.asList(new MaterialStack(Molybdenum, 1), new MaterialStack(Sulfur, 2)))", // MoS2 (also source of Re)
		"Nichrome(311, TextureSet.SET_METALLIC, 6.0F, 64, 2, 1 | 2 | 64, 205, 206, 246, 0,  Nichrome , 0, 0, 2700, 2700, true, false, 1, 1, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(Nickel, 4), new MaterialStack(Chrome, 1)))",
		"NiobiumNitride(359, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 2, 29, 41, 29, 0,  Niobium Nitride , 0, 0, 2573, 2573, true, false, 1, 1, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Niobium, 1), new MaterialStack(Nitrogen, 1)))", // Anti-Reflective Material
		"NiobiumTitanium(360, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 2, 29, 29, 41, 0,  Niobium-Titanium , 0, 0, 2800, 2800, true, false, 1, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Niobium, 1), new MaterialStack(Titanium, 1)))",
		"NitroCarbon(716, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 0, 75, 100, 0,  Nitro-Carbon , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeCyan, 1, Arrays.asList(new MaterialStack(Nitrogen, 1), new MaterialStack(Carbon, 1)))",
		"NitrogenDioxide(717, TextureSet.SET_FLUID, 1.0F, 0, 1, 16, 100, 175, 255, 0,  Nitrogen Dioxide , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeCyan, 1, Arrays.asList(new MaterialStack(Nitrogen, 1), new MaterialStack(Oxygen, 2)))",
		"Obsidian(804, TextureSet.SET_DULL, 1.0F, 0, 3, 1, 80, 50, 100, 0,  Obsidian , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Iron, 1), new MaterialStack(Silicon, 2), new MaterialStack(Oxygen, 8)))",
		"Phosphate(833, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8 | 16, 255, 255, 0, 0,  Phosphate , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeYellow, 1, Arrays.asList(new MaterialStack(Phosphor, 1), new MaterialStack(Oxygen, 4)))",
		"PigIron(307, TextureSet.SET_METALLIC, 6.0F, 384, 2, 1 | 2 | 8 | 64, 200, 180, 180, 0,  Pig Iron , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyePink, 2, Arrays.asList(new MaterialStack(Iron, 1)))",
		"Plastic(874, TextureSet.SET_DULL, 3.0F, 32, 1, 1 | 2 | 64 | 128, 200, 200, 200, 0,  Plastic , 0, 0, 400, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 0, Arrays.asList(new MaterialStack(Carbon, 1), new MaterialStack(Hydrogen, 2)), Arrays.asList(new TC_AspectStack(TC_Aspects.MOTUS, 2)))",
		"Powellite(883, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 255, 255, 0, 0,  Powellite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Molybdenum, 1), new MaterialStack(Oxygen, 4)))",
		"Pumice(926, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 230, 185, 185, 0,  Pumice , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, 2, Arrays.asList(new MaterialStack(Stone, 1)))",
		"Pyrite(834, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1 | 8, 150, 120, 40, 0,  Pyrite , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Sulfur, 2)))",
		"Pyrolusite(943, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 150, 150, 170, 0,  Pyrolusite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, 1, Arrays.asList(new MaterialStack(Manganese, 1), new MaterialStack(Oxygen, 2)))",
		"Pyrope(835, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 120, 50, 100, 0,  Pyrope , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyePurple, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12)))",
		"RockSalt(944, TextureSet.SET_FINE, 1.0F, 0, 1, 1 | 8, 240, 200, 200, 0,  Rock Salt , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Chlorine, 1)))",
		"Rubber(880, TextureSet.SET_SHINY, 1.5F, 16, 0, 1 | 2 | 64 | 128, 0, 0, 0, 0,  Rubber , 0, 0, 400, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 0, Arrays.asList(new MaterialStack(Carbon, 5), new MaterialStack(Hydrogen, 8)), Arrays.asList(new TC_AspectStack(TC_Aspects.MOTUS, 2)))",
		"Ruby(502, TextureSet.SET_RUBY, 7.0F, 256, 2, 1 | 4 | 8 | 64, 255, 100, 100, 127,  Ruby , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyeRed, 1, Arrays.asList(new MaterialStack(Chrome, 1), new MaterialStack(Aluminium, 2), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4)))",
		"Salt(817, TextureSet.SET_FINE, 1.0F, 0, 1, 1 | 8, 250, 250, 250, 0,  Salt , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Chlorine, 1)))",
		"Saltpeter(836, TextureSet.SET_FINE, 1.0F, 0, 1, 1 | 8, 230, 230, 230, 0,  Saltpeter , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Nitrogen, 1), new MaterialStack(Oxygen, 3)))",
		"Sapphire(503, TextureSet.SET_GEM_VERTICAL, 7.0F, 256, 2, 1 | 4 | 8 | 64, 100, 100, 200, 127,  Sapphire , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyeBlue, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Scheelite(910, TextureSet.SET_DULL, 1.0F, 0, 3, 1 | 8, 200, 140, 20, 0,  Scheelite , 0, 0, 2500, 2500, false, false, 4, 1, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Tungsten, 1), new MaterialStack(Calcium, 2), new MaterialStack(Oxygen, 4)))",
		"SiliconDioxide(837, TextureSet.SET_QUARTZ, 1.0F, 0, 1, 1 | 16, 200, 200, 200, 0,  Silicon Dioxide , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLightGray, 1, Arrays.asList(new MaterialStack(Silicon, 1), new MaterialStack(Oxygen, 2)))",
		"Snow(728, TextureSet.SET_FINE, 1.0F, 0, 0, 1 | 16, 250, 250, 250, 0,  Snow , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 0, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.GELUM, 1)))",
		"Sodalite(525, TextureSet.SET_LAPIS, 1.0F, 0, 1, 1 | 4 | 8, 20, 20, 255, 0,  Sodalite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeBlue, 1, Arrays.asList(new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Sodium, 4), new MaterialStack(Chlorine, 1)))",
		"SodiumPersulfate(718, TextureSet.SET_FLUID, 1.0F, 0, 2, 16, 255, 255, 255, 0,  Sodium Persulfate , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4)))",
		"SodiumSulfide(719, TextureSet.SET_FLUID, 1.0F, 0, 2, 16, 255, 255, 255, 0,  Sodium Sulfide , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Sulfur, 1)))",
		"SolderingAlloy(314, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 2, 220, 220, 230, 0,  Soldering Alloy , 0, 0, 400, 400, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Tin, 9), new MaterialStack(Antimony, 1)))",
		"Spessartine(838, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 255, 100, 100, 0,  Spessartine , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeRed, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Manganese, 3), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12)))",
		"Sphalerite(839, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 255, 255, 255, 0,  Sphalerite , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeYellow, 1, Arrays.asList(new MaterialStack(Zinc, 1), new MaterialStack(Sulfur, 1)))",
		"StainlessSteel(306, TextureSet.SET_SHINY, 7.0F, 480, 2, 1 | 2 | 64 | 128, 200, 200, 220, 0,  Stainless Steel , 0, 0, -1, 1700, true, false, 1, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Iron, 6), new MaterialStack(Chrome, 1), new MaterialStack(Manganese, 1), new MaterialStack(Nickel, 1)))",
		"Steel(305, TextureSet.SET_METALLIC, 6.0F, 512, 2, 1 | 2 | 64 | 128, 128, 128, 128, 0,  Steel , 0, 0, 1811, 1000, true, false, 4, 51, 50, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Iron, 50), new MaterialStack(Carbon, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.ORDO, 1)))",
		"Stibnite(945, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 70, 70, 70, 0,  Stibnite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Antimony, 2), new MaterialStack(Sulfur, 3)))",
		"SulfuricAcid(720, TextureSet.SET_FLUID, 1.0F, 0, 2, 16, 255, 128, 0, 0,  Sulfuric Acid , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4)))",
		"Tanzanite(508, TextureSet.SET_GEM_VERTICAL, 7.0F, 256, 2, 1 | 4 | 8 | 64, 64, 0, 200, 127,  Tanzanite , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyePurple, 1, Arrays.asList(new MaterialStack(Calcium, 2), new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Hydrogen, 1), new MaterialStack(Oxygen, 13)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Tetrahedrite(840, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 200, 32, 0, 0,  Tetrahedrite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(Copper, 3), new MaterialStack(Antimony, 1), new MaterialStack(Sulfur, 3), new MaterialStack(Iron, 1)))", //Cu3SbS3 + x(Fe,Zn)6Sb2S9
		"TinAlloy(363, TextureSet.SET_METALLIC, 6.5F, 96, 2, 1 | 2 | 64 | 128, 200, 200, 200, 0,  Tin Alloy , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Tin, 1), new MaterialStack(Iron, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"Topaz(507, TextureSet.SET_GEM_HORIZONTAL, 7.0F, 256, 3, 1 | 4 | 8 | 64, 255, 128, 0, 127,  Topaz , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyeOrange, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 1), new MaterialStack(Fluorine, 2), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 6)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4)))",
		"Tungstate(841, TextureSet.SET_DULL, 1.0F, 0, 3, 1 | 8, 55, 50, 35, 0,  Tungstate , 0, 0, 2500, 2500, true, false, 4, 1, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Tungsten, 1), new MaterialStack(Lithium, 2), new MaterialStack(Oxygen, 4)))",
		"Ultimet(344, TextureSet.SET_SHINY, 6.0F, 512, 3, 1 | 2 | 64 | 128, 180, 180, 230, 0,  Ultimet , 0, 0, 2700, 2700, true, false, 1, 1, 1, Dyes.dyeLightBlue, 1, Arrays.asList(new MaterialStack(Cobalt, 5), new MaterialStack(Chrome, 2), new MaterialStack(Nickel, 1), new MaterialStack(Molybdenum, 1)))", // 54% Cobalt, 26% Chromium, 9% Nickel, 5% Molybdenum, 3% Iron, 2% Tungsten, 0.8% Manganese, 0.3% Silicon, 0.08% Nitrogen and 0.06% Carbon
		"Uraninite(922, TextureSet.SET_METALLIC, 1.0F, 0, 3, 1 | 8, 35, 35, 35, 0,  Uraninite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeLime, 2, Arrays.asList(new MaterialStack(Uranium, 1), new MaterialStack(Oxygen, 2)))",
		"Uvarovite(842, TextureSet.SET_DIAMOND, 1.0F, 0, 2, 1 | 8, 180, 255, 180, 0,  Uvarovite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGreen, 1, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Chrome, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 12)))",
		"VanadiumGallium(357, TextureSet.SET_SHINY, 1.0F, 0, 2, 1 | 2, 128, 128, 140, 0,  Vanadium-Gallium , 0, 0, 3000, 3000, true, false, 1, 1, 1, Dyes.dyeGray, 2, Arrays.asList(new MaterialStack(Vanadium, 3), new MaterialStack(Gallium, 1)))",
		"Water(701, TextureSet.SET_FLUID, 1.0F, 0, 0, 16, 0, 0, 255, 0,  Water , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlue, 0, Arrays.asList(new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.AQUA, 2)))",
		"Wood(809, TextureSet.SET_WOOD, 2.0F, 16, 0, 1 | 2 | 64 | 128, 100, 50, 0, 0,  Wood , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBrown, 0, Arrays.asList(new MaterialStack(Carbon, 1), new MaterialStack(Oxygen, 1), new MaterialStack(Hydrogen, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.ARBOR, 2)))",
		"WroughtIron(304, TextureSet.SET_METALLIC, 6.0F, 384, 2, 1 | 2 | 64 | 128, 200, 180, 180, 0,  Wrought Iron , 0, 0, 1811, 0, false, false, 3, 1, 1, Dyes.dyeLightGray, 2, Arrays.asList(new MaterialStack(Iron, 1)))",
		"Wulfenite(882, TextureSet.SET_DULL, 1.0F, 0, 3, 1 | 8, 255, 128, 0, 0,  Wulfenite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Lead, 1), new MaterialStack(Molybdenum, 1), new MaterialStack(Oxygen, 4)))",
		"YellowLimonite(931, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 200, 200, 0, 0,  Yellow Limonite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Hydrogen, 1), new MaterialStack(Oxygen, 2)))", // FeO(OH) + a bit Ni and Co
		"YttriumBariumCuprate(358, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 2, 80, 64, 70, 0,  Yttrium Barium Cuprate , 0, 0, 1200, 1200, true, false, 1, 1, 1, Dyes.dyeGray, 0, Arrays.asList(new MaterialStack(Yttrium, 1), new MaterialStack(Barium, 2), new MaterialStack(Copper, 3), new MaterialStack(Oxygen, 7)))",
		"WoodSealed(889, TextureSet.SET_WOOD, 3.0F, 24, 0, 1 | 2 ",
		"LiveRoot(832, TextureSet.SET_WOOD, 1.0F, 0, 1, 1, 220, 200, 0, 0,  Liveroot , 5, 16, -1, 0, false, false, 2, 4, 3, Dyes.dyeBrown, 2, Arrays.asList(new MaterialStack(Wood, 3), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.ARBOR, 2), new TC_AspectStack(TC_Aspects.VICTUS, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1)))",
		"IronWood(338, TextureSet.SET_WOOD, 6.0F, 384, 2, 1 | 2 | 64 | 128, 150, 140, 110, 0,  Ironwood , 5, 8, -1, 0, false, false, 2, 19, 18, Dyes.dyeBrown, 2, Arrays.asList(new MaterialStack(Iron, 9), new MaterialStack(LiveRoot, 9), new MaterialStack(Gold, 1)))",
		"Glass(890, TextureSet.SET_GLASS, 1.0F, 4, 0, 1 | 4, 250, 250, 250, 220,  Glass , 0, 0, 1500, 0, false, true, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(SiliconDioxide, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2)))",
		"Perlite(925, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 30, 20, 30, 0,  Perlite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Obsidian, 2), new MaterialStack(Water, 1)))",
		"Borax(941, TextureSet.SET_FINE, 1.0F, 0, 1, 1 | 8, 250, 250, 250, 0,  Borax , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Sodium, 2), new MaterialStack(Boron, 4), new MaterialStack(Water, 10), new MaterialStack(Oxygen, 7)))",
		"Lignite(538, TextureSet.SET_LIGNITE, 1.0F, 0, 0, 1 | 4 | 8, 100, 70, 70, 0,  Lignite Coal , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 1, Arrays.asList(new MaterialStack(Carbon, 2), new MaterialStack(Water, 4), new MaterialStack(DarkAsh, 1)))",
		"Olivine(505, TextureSet.SET_RUBY, 7.0F, 256, 2, 1 | 4 | 8 | 64, 150, 255, 150, 127,  Olivine , 0, 0, -1, 0, false, true, 5, 1, 1, Dyes.dyeLime, 1, Arrays.asList(new MaterialStack(Magnesium, 2), new MaterialStack(Iron, 1), new MaterialStack(SiliconDioxide, 2)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 4), new TC_AspectStack(TC_Aspects.VITREUS, 2)))",
		"Opal(510, TextureSet.SET_OPAL, 7.0F, 256, 2, 1 | 4 | 8 | 64, 0, 0, 255, 0,  Opal , 0, 0, -1, 0, false, true, 3, 1, 1, Dyes.dyeBlue, 1, Arrays.asList(new MaterialStack(SiliconDioxide, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 5), new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Amethyst(509, TextureSet.SET_FLINT, 7.0F, 256, 3, 1 | 4 | 8 | 64, 210, 50, 210, 127,  Amethyst , 0, 0, -1, 0, false, true, 3, 1, 1, Dyes.dyePink, 1, Arrays.asList(new MaterialStack(SiliconDioxide, 4), new MaterialStack(Iron, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 6), new TC_AspectStack(TC_Aspects.VITREUS, 4)))",
		"Redstone(810, TextureSet.SET_ROUGH, 1.0F, 0, 2, 1 | 8, 200, 0, 0, 0,  Redstone , 0, 0, 500, 0, false, false, 3, 1, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(Silicon, 1), new MaterialStack(Pyrite, 5), new MaterialStack(Ruby, 1), new MaterialStack(Mercury, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 1), new TC_AspectStack(TC_Aspects.POTENTIA, 2)))",
		"Lapis(526, TextureSet.SET_LAPIS, 1.0F, 0, 1, 1 | 4 | 8, 70, 70, 220, 0,  Lapis , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeBlue, 2, Arrays.asList(new MaterialStack(Lazurite, 12), new MaterialStack(Sodalite, 2), new MaterialStack(Pyrite, 1), new MaterialStack(Calcite, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.SENSUS, 1)))",
		"Blaze(801, TextureSet.SET_POWDER, 2.0F, 16, 1, 1 | 64, 255, 200, 0, 0,  Blaze , 0, 0, 6400, 0, false, false, 2, 3, 2, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(DarkAsh, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 2), new TC_AspectStack(TC_Aspects.IGNIS, 4)))",
		"EnderPearl(532, TextureSet.SET_SHINY, 1.0F, 16, 1, 1 | 4, 108, 220, 200, 0,  Enderpearl , 0, 0, -1, 0, false, false, 1, 16, 10, Dyes.dyeGreen, 1, Arrays.asList(new MaterialStack(Beryllium, 1), new MaterialStack(Potassium, 4), new MaterialStack(Nitrogen, 5), new MaterialStack(Magic, 6)), Arrays.asList(new TC_AspectStack(TC_Aspects.ALIENIS, 4), new TC_AspectStack(TC_Aspects.ITER, 4), new TC_AspectStack(TC_Aspects.PRAECANTIO, 2)))",
		"EnderEye(533, TextureSet.SET_SHINY, 1.0F, 16, 1, 1 | 4, 160, 250, 230, 0,  Endereye , 5, 10, -1, 0, false, false, 1, 2, 1, Dyes.dyeGreen, 2, Arrays.asList(new MaterialStack(EnderPearl, 1), new MaterialStack(Blaze, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.SENSUS, 4), new TC_AspectStack(TC_Aspects.ALIENIS, 4), new TC_AspectStack(TC_Aspects.ITER, 4), new TC_AspectStack(TC_Aspects.PRAECANTIO, 3), new TC_AspectStack(TC_Aspects.IGNIS, 2)))",
		"Flint(802, TextureSet.SET_FLINT, 2.5F, 64, 1, 1 | 64, 0, 32, 64, 0,  Flint , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, 2, Arrays.asList(new MaterialStack(SiliconDioxide, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.TERRA, 1), new TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1)))",
		"Diatomite(948, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 225, 225, 225, 0,  Diatomite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, 2, Arrays.asList(new MaterialStack(Flint, 8), new MaterialStack(BandedIron, 1), new MaterialStack(Sapphire, 1)))",
		"VolcanicAsh(940, TextureSet.SET_FLINT, 1.0F, 0, 0, 1, 60, 50, 50, 0,  Volcanic Ashes , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Flint, 6), new MaterialStack(Iron, 1), new MaterialStack(Magnesium, 1)))",
		"Niter(531, TextureSet.SET_FLINT, 1.0F, 0, 1, 1 | 4 | 8, 255, 200, 200, 0,  Niter , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePink, 2, Arrays.asList(new MaterialStack(Saltpeter, 1)))",
		"Pyrotheum(843, TextureSet.SET_FIERY, 1.0F, 0, 1, 1, 255, 128, 0, 0,  Pyrotheum , 2, 62, -1, 0, false, false, 2, 3, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Coal, 1), new MaterialStack(Redstone, 1), new MaterialStack(Blaze, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.PRAECANTIO, 2), new TC_AspectStack(TC_Aspects.IGNIS, 1)))",
		"HydratedCoal(818, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1, 70, 70, 100, 0,  Hydrated Coal , 0, 0, -1, 0, false, false, 1, 9, 8, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Coal, 8), new MaterialStack(Water, 1)))",
		"Apatite(530, TextureSet.SET_DIAMOND, 1.0F, 0, 1, 1 | 4 | 8, 200, 200, 255, 0,  Apatite , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeCyan, 1, Arrays.asList(new MaterialStack(Calcium, 5), new MaterialStack(Phosphate, 3), new MaterialStack(Chlorine, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.MESSIS, 2)))",
		"ShadowIron(336, TextureSet.SET_METALLIC, 6.0F, 384, 2, 1 | 2 | 8 | 64, 120, 120, 120, 0,  Shadowiron , 0, 0, -1, 0, false, false, 3, 4, 3, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Iron, 3), new MaterialStack(Magic, 1)))",
		"ShadowSteel(337, TextureSet.SET_METALLIC, 6.0F, 768, 2, 1 | 2 | 64, 90, 90, 90, 0,  Shadowsteel , 0, 0, -1, 1700, true, false, 4, 4, 3, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Steel, 3), new MaterialStack(Magic, 1)))",
		"Steeleaf(339, TextureSet.SET_LEAF, 8.0F, 768, 3, 1 | 2 | 64 | 128, 50, 127, 50, 0,  Steeleaf , 5, 24, -1, 0, false, false, 4, 1, 1, Dyes.dyeGreen, 2, Arrays.asList(new MaterialStack(Steel, 1), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.HERBA, 2), new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1)))",
		"Knightmetal(362, TextureSet.SET_METALLIC, 8.0F, 1024, 3, 1 | 2 | 64 | 128, 210, 240, 200, 0,  Knightmetal , 5, 24, -1, 0, false, false, 4, 1, 1, Dyes.dyeLime, 2, Arrays.asList(new MaterialStack(Steel, 2), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.LUCRUM, 1), new TC_AspectStack(TC_Aspects.METALLUM, 2)))",
		"SterlingSilver(350, TextureSet.SET_SHINY, 13.0F, 128, 2, 1 | 2 | 64 | 128, 250, 220, 225, 0,  Sterling Silver , 0, 0, -1, 1700, true, false, 4, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Silver, 4)))",
		"RoseGold(351, TextureSet.SET_SHINY, 14.0F, 128, 2, 1 | 2 | 64 | 128, 255, 230, 30, 0,  Rose Gold , 0, 0, -1, 1600, true, false, 4, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Copper, 1), new MaterialStack(Gold, 4)))",
		"BlackBronze(352, TextureSet.SET_DULL, 12.0F, 256, 2, 1 | 2 | 64 | 128, 100, 50, 125, 0,  Black Bronze , 0, 0, -1, 2000, true, false, 4, 1, 1, Dyes.dyePurple, 2, Arrays.asList(new MaterialStack(Gold, 1), new MaterialStack(Silver, 1), new MaterialStack(Copper, 3)))",
		"BismuthBronze(353, TextureSet.SET_DULL, 8.0F, 256, 2, 1 | 2 | 64 | 128, 100, 125, 125, 0,  Bismuth Bronze , 0, 0, -1, 1100, true, false, 4, 1, 1, Dyes.dyeCyan, 2, Arrays.asList(new MaterialStack(Bismuth, 1), new MaterialStack(Zinc, 1), new MaterialStack(Copper, 3)))",
		"BlackSteel(334, TextureSet.SET_METALLIC, 6.5F, 768, 2, 1 | 2 | 64, 100, 100, 100, 0,  Black Steel , 0, 0, -1, 1200, true, false, 4, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Nickel, 1), new MaterialStack(BlackBronze, 1), new MaterialStack(Steel, 3)))",
		"RedSteel(348, TextureSet.SET_METALLIC, 7.0F, 896, 2, 1 | 2 | 64, 140, 100, 100, 0,  Red Steel , 0, 0, -1, 1300, true, false, 4, 1, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(SterlingSilver, 1), new MaterialStack(BismuthBronze, 1), new MaterialStack(Steel, 2), new MaterialStack(BlackSteel, 4)))",
		"BlueSteel(349, TextureSet.SET_METALLIC, 7.5F, 1024, 2, 1 | 2 | 64, 100, 100, 140, 0,  Blue Steel , 0, 0, -1, 1400, true, false, 4, 1, 1, Dyes.dyeBlue, 2, Arrays.asList(new MaterialStack(RoseGold, 1), new MaterialStack(Brass, 1), new MaterialStack(Steel, 2), new MaterialStack(BlackSteel, 4)))",
		"DamascusSteel(335, TextureSet.SET_METALLIC, 8.0F, 1280, 2, 1 | 2 | 64, 110, 110, 110, 0,  Damascus Steel , 0, 0, 2000, 1500, true, false, 4, 1, 1, Dyes.dyeGray, 2, Arrays.asList(new MaterialStack(Steel, 1)))",
		"TungstenSteel(316, TextureSet.SET_METALLIC, 10.0F, 5120, 4, 1 | 2 | 64 | 128, 100, 100, 160, 0,  Tungstensteel , 0, 0, -1, 3000, true, false, 4, 1, 1, Dyes.dyeBlue, 2, Arrays.asList(new MaterialStack(Steel, 1), new MaterialStack(Tungsten, 1)))",
		"NitroFuel(709, TextureSet.SET_FLUID, 1.0F, 0, 2, 16, 200, 255, 0, 0,  Nitro-Diesel , 0, 384, -1, 0, false, false, 1, 1, 1, Dyes.dyeLime, 0, Arrays.asList(new MaterialStack(Glyceryl, 1), new MaterialStack(Fuel, 4)))",
		"AstralSilver(333, TextureSet.SET_SHINY, 10.0F, 64, 2, 1 | 2 | 8 | 64, 230, 230, 255, 0,  Astral Silver , 0, 0, -1, 0, false, false, 4, 3, 2, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Silver, 2), new MaterialStack(Magic, 1)))",
		"Midasium(332, TextureSet.SET_SHINY, 12.0F, 64, 2, 1 | 2 | 8 | 64, 255, 200, 40, 0,  Midasium , 0, 0, -1, 0, false, false, 4, 3, 2, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Gold, 2), new MaterialStack(Magic, 1)))",
		"Mithril(331, TextureSet.SET_SHINY, 14.0F, 64, 3, 1 | 2 | 8 | 64, 255, 255, 210, 0,  Mithril , 0, 0, -1, 0, false, false, 4, 3, 2, Dyes.dyeLightBlue, 2, Arrays.asList(new MaterialStack(Platinum, 2), new MaterialStack(Magic, 1)))",
		"BlueAlloy(309, TextureSet.SET_DULL, 1.0F, 0, 0, 1 | 2, 100, 180, 255, 0,  Blue Alloy , 0, 0, -1, 0, false, false, 3, 5, 1, Dyes.dyeLightBlue, 2, Arrays.asList(new MaterialStack(Silver, 1), new MaterialStack(Nikolite, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.ELECTRUM, 3)))",
		"RedAlloy(308, TextureSet.SET_DULL, 1.0F, 0, 0, 1 | 2, 200, 0, 0, 0,  Red Alloy , 0, 0, -1, 0, false, false, 3, 5, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(Metal, 1), new MaterialStack(Redstone, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.MACHINA, 3)))",
		"CobaltBrass(343, TextureSet.SET_METALLIC, 8.0F, 256, 2, 1 | 2 | 64 | 128, 180, 180, 160, 0,  Cobalt Brass , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(Brass, 7), new MaterialStack(Aluminium, 1), new MaterialStack(Cobalt, 1)))",
		"Phosphorus(534, TextureSet.SET_FLINT, 1.0F, 0, 2, 1 | 4 | 8 | 16, 255, 255, 0, 0,  Phosphorus , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Calcium, 3), new MaterialStack(Phosphate, 2)))",
		"Basalt(844, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1, 30, 20, 20, 0,  Basalt , 0, 0, -1, 0, false, false, 2, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Olivine, 1), new MaterialStack(Calcite, 3), new MaterialStack(Flint, 8), new MaterialStack(DarkAsh, 4)), Arrays.asList(new TC_AspectStack(TC_Aspects.TENEBRAE, 1)))",
		"GarnetRed(527, TextureSet.SET_RUBY, 7.0F, 128, 2, 1 | 4 | 8 | 64, 200, 80, 80, 127,  Red Garnet , 0, 0, -1, 0, false, true, 4, 1, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(Pyrope, 3), new MaterialStack(Almandine, 5), new MaterialStack(Spessartine, 8)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"GarnetYellow(528, TextureSet.SET_RUBY, 7.0F, 128, 2, 1 | 4 | 8 | 64, 200, 200, 80, 127,  Yellow Garnet , 0, 0, -1, 0, false, true, 4, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Andradite, 5), new MaterialStack(Grossular, 8), new MaterialStack(Uvarovite, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 3)))",
		"Marble(845, TextureSet.SET_FINE, 1.0F, 0, 1, 1, 200, 200, 200, 0,  Marble , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Calcite, 7)), Arrays.asList(new TC_AspectStack(TC_Aspects.PERFODIO, 1)))",
		"Sugar(803, TextureSet.SET_FINE, 1.0F, 0, 1, 1, 250, 250, 250, 0,  Sugar , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 1, Arrays.asList(new MaterialStack(Carbon, 2), new MaterialStack(Water, 5), new MaterialStack(Oxygen, 25)), Arrays.asList(new TC_AspectStack(TC_Aspects.HERBA, 1), new TC_AspectStack(TC_Aspects.AQUA, 1), new TC_AspectStack(TC_Aspects.AER, 1)))",
		"Thaumium(330, TextureSet.SET_METALLIC, 12.0F, 256, 3, 1 | 2 | 64 | 128, 150, 100, 200, 0,  Thaumium , 0, 0, -1, 0, false, false, 5, 2, 1, Dyes.dyePurple, 0, Arrays.asList(new MaterialStack(Iron, 1), new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1)))",
		"Vinteum(529, TextureSet.SET_EMERALD, 10.0F, 128, 3, 1 | 4 | 8 | 64, 100, 200, 255, 0,  Vinteum , 5, 32, -1, 0, false, false, 4, 1, 1, Dyes.dyeLightBlue, 2, Arrays.asList(new MaterialStack(Magic, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.VITREUS, 2), new TC_AspectStack(TC_Aspects.PRAECANTIO, 1)))",
		"Redrock(846, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1, 255, 80, 50, 0,  Redrock , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeRed, 2, Arrays.asList(new MaterialStack(Calcite, 2), new MaterialStack(Flint, 1), new MaterialStack(Clay, 1)))",
		"PotassiumFeldspar(847, TextureSet.SET_FINE, 1.0F, 0, 1, 1, 120, 40, 40, 0,  Potassium Feldspar , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyePink, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Aluminium, 1), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 8)))",
		"Biotite(848, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1, 20, 30, 20, 0,  Biotite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Magnesium, 3), new MaterialStack(Aluminium, 3), new MaterialStack(Fluorine, 2), new MaterialStack(Silicon, 3), new MaterialStack(Oxygen, 10)))",
		"GraniteBlack(849, TextureSet.SET_ROUGH, 4.0F, 64, 3, 1 | 64 | 128, 10, 10, 10, 0,  Black Granite , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(SiliconDioxide, 4), new MaterialStack(Biotite, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.TUTAMEN, 1)))",
		"GraniteRed(850, TextureSet.SET_ROUGH, 4.0F, 64, 3, 1 | 64 | 128, 255, 0, 128, 0,  Red Granite , 0, 0, -1, 0, false, false, 0, 1, 1, Dyes.dyeMagenta, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(PotassiumFeldspar, 1), new MaterialStack(Oxygen, 3)), Arrays.asList(new TC_AspectStack(TC_Aspects.TUTAMEN, 1)))",
		"Chrysotile(912, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 110, 140, 110, 0,  Chrysotile , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Asbestos, 1)))",
		"Realgar(555, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 140, 100, 100, 0,  Realgar , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(Arsenic, 4), new MaterialStack(Sulfur, 4)))",
		"VanadiumMagnetite(923, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 35, 35, 60, 0,  Vanadium Magnetite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Magnetite, 1), new MaterialStack(Vanadium, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))", // Mixture of Fe3O4 and V2O5
		"BasalticMineralSand(935, TextureSet.SET_SAND, 1.0F, 0, 1, 1 | 8, 40, 50, 40, 0,  Basaltic Mineral Sand , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Magnetite, 1), new MaterialStack(Basalt, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"GraniticMineralSand(936, TextureSet.SET_SAND, 1.0F, 0, 1, 1 | 8, 40, 60, 60, 0,  Granitic Mineral Sand , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeBlack, 2, Arrays.asList(new MaterialStack(Magnetite, 1), new MaterialStack(GraniteBlack, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"GarnetSand(938, TextureSet.SET_SAND, 1.0F, 0, 1, 1 | 8, 200, 100, 0, 0,  Garnet Sand , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeOrange, 2, Arrays.asList(new MaterialStack(GarnetRed, 1), new MaterialStack(GarnetYellow, 1)))",
		"QuartzSand(939, TextureSet.SET_SAND, 1.0F, 0, 1, 1 | 8, 200, 200, 200, 0,  Quartz Sand , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes.dyeWhite, 2, Arrays.asList(new MaterialStack(CertusQuartz, 1), new MaterialStack(Quartzite, 1)))",
		"Bastnasite(905, TextureSet.SET_FINE, 1.0F, 0, 2, 1 | 8, 200, 110, 45, 0,  Bastnasite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Cerium, 1), new MaterialStack(Carbon, 1), new MaterialStack(Fluorine, 1), new MaterialStack(Oxygen, 3)))", // (Ce, La, Y)CO3F
		"Pentlandite(909, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 165, 150, 5, 0,  Pentlandite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Nickel, 9), new MaterialStack(Sulfur, 8)))", // (Fe,Ni)9S8
		"Spodumene(920, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 190, 170, 170, 0,  Spodumene , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Lithium, 1), new MaterialStack(Aluminium, 1), new MaterialStack(Silicon, 2), new MaterialStack(Oxygen, 6)))", // LiAl(SiO3)2
		"Pollucite(919, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 240, 210, 210, 0,  Pollucite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Caesium, 2), new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 4), new MaterialStack(Water, 2), new MaterialStack(Oxygen, 12)))", // (Cs,Na)2Al2Si4O12 2H2O (also a source of Rb)
		"Tantalite(921, TextureSet.SET_METALLIC, 1.0F, 0, 3, 1 | 8, 145, 80, 40, 0,  Tantalite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Manganese, 1), new MaterialStack(Tantalum, 2), new MaterialStack(Oxygen, 6)))", // (Fe, Mn)Ta2O6 (also source of Nb)
		"Lepidolite(907, TextureSet.SET_FINE, 1.0F, 0, 2, 1 | 8, 240, 50, 140, 0,  Lepidolite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Lithium, 3), new MaterialStack(Aluminium, 4), new MaterialStack(Fluorine, 2), new MaterialStack(Oxygen, 10)))", // K(Li,Al,Rb)3(Al,Si)4O10(F,OH)2
		"Glauconite(933, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 130, 180, 60, 0,  Glauconite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Magnesium, 2), new MaterialStack(Aluminium, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12)))", // (K,Na)(Fe3+,Al,Mg)2(Si,Al)4O10(OH)2
		"GlauconiteSand(949, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 130, 180, 60, 0,  Glauconite Sand , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Magnesium, 2), new MaterialStack(Aluminium, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12)))", // (K,Na)(Fe3+,Al,Mg)2(Si,Al)4O10(OH)2
		"Vermiculite(932, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 200, 180, 15, 0,  Vermiculite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Iron, 3), new MaterialStack(Aluminium, 4), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Water, 4), new MaterialStack(Oxygen, 12)))", // (Mg+2, Fe+2, Fe+3)3 [(AlSi)4O10] (OH)2 4H2O)
		"Bentonite(927, TextureSet.SET_ROUGH, 1.0F, 0, 2, 1 | 8, 245, 215, 210, 0,  Bentonite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Magnesium, 6), new MaterialStack(Silicon, 12), new MaterialStack(Hydrogen, 6), new MaterialStack(Water, 5), new MaterialStack(Oxygen, 36)))", // (Na,Ca)0.33(Al,Mg)2(Si4O10)(OH)2 nH2O
		"FullersEarth(928, TextureSet.SET_FINE, 1.0F, 0, 2, 1 | 8, 160, 160, 120, 0,  Fullers Earth , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Magnesium, 1), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 1), new MaterialStack(Water, 4), new MaterialStack(Oxygen, 11)))", // (Mg,Al)2Si4O10(OH) 4(H2O)
		"Pitchblende(873, TextureSet.SET_DULL, 1.0F, 0, 3, 1 | 8, 200, 210, 0, 0,  Pitchblende , 0, 0, -1, 0, false, false, 5, 1, 1, Dyes.dyeYellow, 2, Arrays.asList(new MaterialStack(Uraninite, 3), new MaterialStack(Thorium, 1), new MaterialStack(Lead, 1)))",
		"Monazite(520, TextureSet.SET_DIAMOND, 1.0F, 0, 1, 1 | 4 | 8, 50, 70, 50, 0,  Monazite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGreen, 1, Arrays.asList(new MaterialStack(RareEarth, 1), new MaterialStack(Phosphate, 1)))", // Wikipedia: (Ce, La, Nd, Th, Sm, Gd)PO4 Monazite also smelt-extract to Helium, it is brown like the rare earth Item Monazite sand deposits are inevitably of the monazite-(Ce) composition. Typically, the lanthanides in such monazites contain about 45�48% cerium, about 24% lanthanum, about 17% neodymium, about 5% praseodymium, and minor quantities of samarium, gadolinium, and yttrium. Europium concentrations tend to be low, about 0.05% Thorium content of monazite is variable and sometimes can be up to 20�30%
		"Malachite(871, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 5, 95, 5, 0,  Malachite , 0, 0, -1, 0, false, false, 3, 1, 1, Dyes.dyeGreen, 1, Arrays.asList(new MaterialStack(Copper, 2), new MaterialStack(Carbon, 1), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 5)))", // Cu2CO3(OH)2
		"Mirabilite(900, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 240, 250, 210, 0,  Mirabilite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Sodium, 2), new MaterialStack(Sulfur, 1), new MaterialStack(Water, 10), new MaterialStack(Oxygen, 4)))", // Na2SO4 10H2O
		"Mica(901, TextureSet.SET_FINE, 1.0F, 0, 1, 1 | 8, 195, 195, 205, 0,  Mica , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 3), new MaterialStack(Fluorine, 2), new MaterialStack(Oxygen, 10)))", // KAl2(AlSi3O10)(F,OH)2
		"Trona(903, TextureSet.SET_METALLIC, 1.0F, 0, 1, 1 | 8, 135, 135, 95, 0,  Trona , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Sodium, 3), new MaterialStack(Carbon, 2), new MaterialStack(Hydrogen, 1), new MaterialStack(Water, 2), new MaterialStack(Oxygen, 6)))", // Na3(CO3)(HCO3) 2H2O
		"Barite(904, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 230, 235, 255, 0,  Barite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Barium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Oxygen, 4)))",
		"Gypsum(934, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 230, 230, 250, 0,  Gypsum , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Sulfur, 1), new MaterialStack(Water, 2), new MaterialStack(Oxygen, 4)))", // CaSO4 2H2O
		"Alunite(911, TextureSet.SET_METALLIC, 1.0F, 0, 2, 1 | 8, 225, 180, 65, 0,  Alunite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Potassium, 1), new MaterialStack(Aluminium, 3), new MaterialStack(Silicon, 2), new MaterialStack(Hydrogen, 6), new MaterialStack(Oxygen, 14)))", // KAl3(SO4)2(OH)6
		"Dolomite(914, TextureSet.SET_FLINT, 1.0F, 0, 1, 1 | 8, 225, 205, 205, 0,  Dolomite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Magnesium, 1), new MaterialStack(Carbon, 2), new MaterialStack(Oxygen, 6)))", // CaMg(CO3)2
		"Wollastonite(915, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 240, 240, 240, 0,  Wollastonite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Calcium, 1), new MaterialStack(Silicon, 1), new MaterialStack(Oxygen, 3)))", // CaSiO3
		"Zeolite(916, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 240, 230, 230, 0,  Zeolite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Sodium, 1), new MaterialStack(Calcium, 4), new MaterialStack(Silicon, 27), new MaterialStack(Aluminium, 9), new MaterialStack(Water, 28), new MaterialStack(Oxygen, 72)))", // NaCa4(Si27Al9)O72 28(H2O)
		"Kyanite(924, TextureSet.SET_FLINT, 1.0F, 0, 2, 1 | 8, 110, 110, 250, 0,  Kyanite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 1), new MaterialStack(Oxygen, 5)))", // Al2SiO5
		"Kaolinite(929, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 245, 235, 235, 0,  Kaolinite , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Aluminium, 2), new MaterialStack(Silicon, 2), new MaterialStack(Hydrogen, 4), new MaterialStack(Oxygen, 9)))", // Al2Si2O5(OH)4
		"Talc(902, TextureSet.SET_DULL, 1.0F, 0, 2, 1 | 8, 90, 180, 90, 0,  Talc , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12)))", // H2Mg3(SiO3)4
		"Soapstone(877, TextureSet.SET_DULL, 1.0F, 0, 1, 1 | 8, 95, 145, 95, 0,  Soapstone , 0, 0, -1, 0, false, false, 1, 1, 1, Dyes._NULL, 1, Arrays.asList(new MaterialStack(Magnesium, 3), new MaterialStack(Silicon, 4), new MaterialStack(Hydrogen, 2), new MaterialStack(Oxygen, 12)))", // H2Mg3(SiO3)4
		"Concrete(947, TextureSet.SET_ROUGH, 1.0F, 0, 1, 1, 100, 100, 100, 0,  Concrete , 0, 0, 300, 0, false, false, 0, 1, 1, Dyes.dyeGray, 0, Arrays.asList(new MaterialStack(Stone, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.TERRA, 1)))",
		"IronMagnetic(354, TextureSet.SET_MAGNETIC, 6.0F, 256, 2, 1 | 2 | 64 | 128, 200, 200, 200, 0,  Magnetic Iron , 0, 0, -1, 0, false, false, 4, 51, 50, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Iron, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 2), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"SteelMagnetic(355, TextureSet.SET_MAGNETIC, 6.0F, 512, 2, 1 | 2 | 64 | 128, 128, 128, 128, 0,  Magnetic Steel , 0, 0, 1000, 1000, true, false, 4, 51, 50, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Steel, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.ORDO, 1), new TC_AspectStack(TC_Aspects.MAGNETO, 1)))",
		"NeodymiumMagnetic(356, TextureSet.SET_MAGNETIC, 7.0F, 512, 2, 1 | 2 | 64 | 128, 100, 100, 100, 0,  Magnetic Neodymium , 0, 0, 1297, 1297, true, false, 4, 51, 50, Dyes.dyeGray, 1, Arrays.asList(new MaterialStack(Neodymium, 1)), Arrays.asList(new TC_AspectStack(TC_Aspects.METALLUM, 1), new TC_AspectStack(TC_Aspects.MAGNETO, 3)))",






	};
}
