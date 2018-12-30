package com.github.arowshot.moreadvancedmacros.lua.libraries;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import com.github.arowshot.moreadvancedmacros.lua.libraries.WebSockets.newWs;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.theincgi.advancedMacros.AdvancedMacros;
import com.theincgi.advancedMacros.misc.Utils;
import com.theincgi.advancedMacros.publicInterfaces.LuaPlugin;

import net.minecraft.client.Minecraft;

public class MinecraftSettings extends TwoArgFunction implements LuaPlugin {
	public MinecraftSettings() { }
	
	public LuaValue call(LuaValue modname, LuaValue env) {
		
		LuaValue library = tableOf();
		library.set("setgamma", new OneArgFunction() {
	        @Override
			public LuaValue call(LuaValue arg1) {
				double gamma = arg1.checkdouble();
		        Minecraft.getMinecraft().gameSettings.gammaSetting = (float) gamma;
				return LuaValue.NONE;
			}
		});
		library.set("getgamma", new ZeroArgFunction() {
	        @Override
			public LuaValue call() {
				return LuaValue.valueOf(Minecraft.getMinecraft().gameSettings.gammaSetting);
			}
		});
		
		library.set("getfps", new ZeroArgFunction() {
	        @Override
			public LuaValue call() {
				return LuaValue.valueOf(Minecraft.getDebugFPS());
			}
		});
		return library;
	}
	static class newWs extends OneArgFunction {
		public LuaValue call(LuaValue uri) {
			return new WebSocket(uri.tojstring());
		}
	}

	@Override
	public String getLibraryName() {
		return "settings";
	}
}