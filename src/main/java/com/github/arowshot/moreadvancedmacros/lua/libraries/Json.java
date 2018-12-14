package com.github.arowshot.moreadvancedmacros.lua.libraries;

import java.util.Map.Entry;

import javax.swing.JFrame;

import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.theincgi.advancedMacros.publicInterfaces.LuaPlugin;

import net.minecraft.client.Minecraft;

public class Json extends TwoArgFunction implements LuaPlugin {
	public Json() { }
	
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaValue library = tableOf();
		
		library.set("parse", new OneArgFunction() {
	        @Override
			public LuaValue call(LuaValue arg) {
	        	String json = arg.checkjstring();
	    		JsonParser parser = new JsonParser();
	    		
	    		JsonObject o = parser.parse(json).getAsJsonObject();
	    		
				return toLua(o);
			}
		});
		
		library.set("stringify", new OneArgFunction() {
	        @Override
			public LuaValue call(LuaValue arg) {
				return LuaValue.valueOf(toJson(arg).toString());
			}
		});
		
		return library;
	}
	
	public JsonElement toJson(LuaValue l) {
		JsonElement ret = JsonNull.INSTANCE;
		
		switch(l.type()) {
		case LuaValue.TBOOLEAN:
			ret = new JsonPrimitive(l.toboolean());
			break;
		case LuaValue.TINT:
			ret = new JsonPrimitive(l.toint());
			break;
		case LuaValue.TNUMBER:
			ret = new JsonPrimitive(l.todouble());
			break;
		case LuaValue.TSTRING:
			ret = new JsonPrimitive(l.tojstring());
			break;
		case LuaValue.TTABLE:
			JsonObject j = new JsonObject();
			LuaValue k = LuaValue.NONE;
			while(true) {
				Varargs n = l.next(k);
				if((k = n.arg1()).isnil())
					break;
				LuaValue v = n.arg(2);
				j.add(k.tojstring(), toJson(v));
			}
			ret = j;
			break;
		}
		
		return ret;
	}
	
	public LuaValue toLua(JsonElement o) {
		LuaValue ret = LuaValue.NIL;
		if(o.isJsonArray()) {
			ret = tableOf();
			int i = 0;
			for(JsonElement el : (JsonArray) o) {
				ret.set(i, toLua(el));
				i++;
			}
		} else if(o.isJsonObject()) {
			ret = tableOf();
			for(Entry<String, JsonElement> ent : ((JsonObject)o).entrySet()) {
				System.out.println(ent.getKey());
				ret.set(ent.getKey(), toLua(ent.getValue()));
			}
		} else if(o.isJsonPrimitive()) {
			JsonPrimitive obj = o.getAsJsonPrimitive();
			if(obj.isString()) {
				return LuaValue.valueOf(obj.getAsString());
			} else if(obj.isNumber()) {
				return LuaValue.valueOf(obj.getAsDouble());
			} else if(obj.isBoolean()) {
				return LuaValue.valueOf(obj.getAsBoolean());
			}
		}
		return ret;
	}
	
	@Override
	public String getLibraryName() {
		return "json";
	}
}
