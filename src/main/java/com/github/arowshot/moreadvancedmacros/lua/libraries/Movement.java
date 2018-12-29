package com.github.arowshot.moreadvancedmacros.lua.libraries;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.ThreeArgFunction;
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

public class Movement extends TwoArgFunction implements LuaPlugin {
	public Movement() { }
	
	public LuaValue call(LuaValue modname, LuaValue env) {
		
		LuaValue library = tableOf();
		library.set("teleport", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
				double x = arg1.checkdouble();
				double y = arg2.checkdouble();
				double z = arg3.checkdouble();
				Minecraft.getMinecraft().player.setPositionAndUpdate(x, y, z);
				return LuaValue.NONE;
			}
		});
		
		return library;
	}
	
	@Override
	public String getLibraryName() {
		return "movement";
	}
}