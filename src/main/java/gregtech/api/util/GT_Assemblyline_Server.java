package gregtech.api.util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregtech.api.GregTech_API;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class GT_Assemblyline_Server {
	
	
	public static LinkedHashMap<String,String> lServerNames = new LinkedHashMap<String,String>();
	private static LinkedHashMap<String,String> internal2= new LinkedHashMap<String,String>(),internal3 = new LinkedHashMap<String,String>(),internal4= new LinkedHashMap<String,String>();
	private static HashMap<String, Property> internal = new HashMap<String, Property>();
	
	public static void fillMap(FMLPreInitializationEvent aEvent) {
		
		String s = new String(aEvent.getModConfigurationDirectory().getAbsolutePath());
		s = s.substring(0, aEvent.getModConfigurationDirectory().getAbsolutePath().length()-6);
		s = s + "GregTech.lang";
		File f = new File(s);
		s = "";
		Configuration conf = new Configuration(f);
		
		ConfigCategory cat = conf.getCategory("languagefile");
		internal.putAll(cat.getValues());
		for (Map.Entry<String, Property> entry : internal.entrySet())
		{
				s=entry.getValue().getString().replaceAll("%", "");
			/*if (s.contains("material")&&!entry.getKey().contains("metaitem"))
				lServerNames.put(entry.getKey(), entry.getKey());
			else */
			if (entry.getKey().contains("metaitem")&&s.contains("material"))
				internal2.put(entry.getKey(), s);
			else if (entry.getKey().contains("blockmachines")&&s.contains("material"))
				internal3.put(entry.getKey(), s);
			else if (entry.getKey().contains("blockmetal")&&s.contains("material"))
				internal4.put(entry.getKey(), s);
			else
				lServerNames.put(entry.getKey(), s);
		}
		for (Map.Entry<String, String> entry : internal2.entrySet()) {
			if (entry.getKey().contains("name")) {
				int i = Integer.parseInt(entry.getKey().substring("gt.metaitem.01.".length(), entry.getKey().length()-".name".length()));
				i=i%1000;
				lServerNames.put(entry.getKey(), entry.getValue().replace("material",GregTech_API.sGeneratedMaterials[i].toString()));
			}
		}
		for (Map.Entry<String, String> entry : internal3.entrySet()) {
			if (entry.getKey().contains("cable"))
				lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.cable.".length(), entry.getKey().length()-".01.name".length())));	
			else if (entry.getKey().contains("gt_frame_"))
				lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.gt_frame_".length(), entry.getKey().length()-".name".length())));	
			else if(entry.getKey().contains("gt_pipe_")) {
					if(
					!entry.getKey().contains("_huge")	&&
					!entry.getKey().contains("_large")	&&
					!entry.getKey().contains("_nonuple")&&
					!entry.getKey().contains("_quadruple")&&
					!entry.getKey().contains("_small")&&
					!entry.getKey().contains("_tiny")
					)
						lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.gt_pipe_".length(), entry.getKey().length()-".name".length())));	
					else if (entry.getKey().contains("_huge")||entry.getKey().contains("_tiny"))
						lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.gt_pipe_".length(), entry.getKey().length()-"_tiny.name".length())));	
					else if (entry.getKey().contains("_large")||entry.getKey().contains("_small"))
						lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.gt_pipe_".length(), entry.getKey().length()-"_large.name".length())));
					else if (entry.getKey().contains("_nonuple"))
						lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.gt_pipe_".length(), entry.getKey().length()-"_nonuple.name".length())));
					else if (entry.getKey().contains("_quadruple"))
						lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.gt_pipe_".length(), entry.getKey().length()-"_quadruple.name".length())));
			}
			else if (entry.getKey().contains("wire"))
				lServerNames.put(entry.getKey(), entry.getValue().replace("material", entry.getKey().substring("gt.blockmachines.wire.".length(), entry.getKey().length()-".01.name".length())));	
			else
				lServerNames.put(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, String> entry : internal4.entrySet()) {
			if (entry.getValue().contains("blockores")) {
				int i = Integer.parseInt(entry.getKey().substring("gt.blockores.".length(), entry.getKey().length()-".name".length()));
				i=i%1000;
				lServerNames.put(entry.getKey(), entry.getValue().replace("material",GregTech_API.sGeneratedMaterials[i].toString()));
			}
		}
		
		internal = null;
		internal2 = null;
		internal3 = null;
		internal4 = null;
	}

}
