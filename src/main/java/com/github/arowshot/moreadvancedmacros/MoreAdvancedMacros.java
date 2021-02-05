package com.github.arowshot.moreadvancedmacros;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.luaj.vm2_v3_0_1.LuaFunction;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import com.github.arowshot.moreadvancedmacros.lua.libraries.Json;
import com.github.arowshot.moreadvancedmacros.lua.libraries.MinecraftSettings;
import com.github.arowshot.moreadvancedmacros.lua.libraries.Movement;
import com.github.arowshot.moreadvancedmacros.lua.libraries.WebSockets;
import com.theincgi.advancedMacros.AdvancedMacros;
import com.theincgi.advancedMacros.misc.JarLibSearcher;
import com.theincgi.advancedMacros.publicInterfaces.LuaPlugin;

@Mod(modid = MoreAdvancedMacros.MODID, version = MoreAdvancedMacros.VERSION, dependencies = "required-after:advancedmacros")
public class MoreAdvancedMacros
{
    public static final String MODID = "moreadvancedmacros";
    public static final String VERSION = "3.0.0";
    public static List<LuaPlugin> libraries = new ArrayList<LuaPlugin>();
    
    private JarLibSearcher jarLibSearcher;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
		LuaTable searchers = AdvancedMacros.globals.get("package").get("searchers").checktable();
		searchers.set(searchers.length() + 1, new VarArgFunction() {
			public Varargs invoke(Varargs args) {
				String name = args.checkjstring(1);
				for(LuaPlugin lib : libraries) {
					if(name.equals(lib.getLibraryName())) {
						LuaFunction library = (LuaFunction) lib;
						library.initupvalue1(AdvancedMacros.globals);
						return varargsOf(library, AdvancedMacros.globals);
					}
				}
				return valueOf("\n\tAdvancedMacros library '"+name+"' not found" );
			}
		});

		libraries.add(new Json());
		libraries.add(new WebSockets());
		libraries.add(new MinecraftSettings());
		libraries.add(new Movement());
    }
}
