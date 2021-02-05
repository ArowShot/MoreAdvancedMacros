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
	    		
	    		JsonElement o = parser.parse(json);
	    		
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
	
	public JsonArray toJsonArray(LuaValue l) {
		LuaValue metatable = l.getmetatable();
		LuaValue arrayMeta;
		try {
			arrayMeta = metatable.get("__array");
		} catch(Exception e) {
			arrayMeta = LuaValue.NIL;
		}
		
		if(arrayMeta.isboolean()) {
			if(arrayMeta.checkboolean()) {
				// Force array
				JsonArray array = new JsonArray();
				
				LuaValue key = LuaValue.NIL;
				while (true) {
				    Varargs nextPair = l.next(key);
				    if ((key = nextPair.arg1()).isnil())
				        break;
				    LuaValue value = nextPair.arg(2);
				    array.add(toJson(value));
				}
				
				return array;
			} else {
				// Force object
				throw new RuntimeException("Lua table is not an array");
			}
		} else {
			// No behavior specified, auto detect array
			JsonArray array = new JsonArray();

			LuaValue key = LuaValue.NIL;
			int i = 1;
			while (true) {
			    Varargs nextPair = l.next(key);
			    if ((key = nextPair.arg1()).isnil())
			        break;
			    if(!(key.isint() && key.checkint() == i)) {
					throw new RuntimeException("Could not convert Lua table to JSON table");
			    }
			    i++;
			    LuaValue value = nextPair.arg(2);
			    array.add(toJson(value));
			}
			
			return array;
		}
	}
	
	public JsonObject toJsonObject(LuaValue l) {
		JsonObject jsonObj = new JsonObject();
		LuaValue key = LuaValue.NONE;
		while (true) {
		    Varargs nextPair = l.next(key);
		    if ((key = nextPair.arg1()).isnil())
		        break;
		    LuaValue value = nextPair.arg(2);
			jsonObj.add(key.tojstring(), toJson(value));
		}
		return jsonObj;
	}
	
	public JsonElement toJson(LuaValue l) {
		switch(l.type()) {
		case LuaValue.TBOOLEAN:
			return new JsonPrimitive(l.toboolean());
		case LuaValue.TINT:
			return new JsonPrimitive(l.toint());
		case LuaValue.TNUMBER:
			return new JsonPrimitive(l.todouble());
		case LuaValue.TSTRING:
			return new JsonPrimitive(l.tojstring());
		case LuaValue.TTABLE:
			try {
				return toJsonArray(l);
			} catch(Exception e) {
				return toJsonObject(l);
			}
		}
		
		return JsonNull.INSTANCE;
	}
	
	public LuaValue toLua(JsonElement o) {
		LuaValue ret = LuaValue.NIL;
		if(o.isJsonArray()) {
			ret = tableOf();
			int i = 1;
			for(JsonElement el : (JsonArray) o) {
				ret.set(i, toLua(el));
				i++;
			}
		} else if(o.isJsonObject()) {
			ret = tableOf();
			for(Entry<String, JsonElement> ent : ((JsonObject)o).entrySet()) {
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
