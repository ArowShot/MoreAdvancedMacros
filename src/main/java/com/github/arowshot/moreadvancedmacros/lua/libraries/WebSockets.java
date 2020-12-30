package com.github.arowshot.moreadvancedmacros.lua.libraries;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.luaj.vm2_v3_0_1.LuaFunction;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.LuaValue;
import org.luaj.vm2_v3_0_1.lib.OneArgFunction;
import org.luaj.vm2_v3_0_1.lib.TwoArgFunction;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;
import org.luaj.vm2_v3_0_1.lib.ZeroArgFunction;

import com.theincgi.advancedMacros.publicInterfaces.LuaPlugin;
import com.theincgi.advancedMacros.AdvancedMacros;

public class WebSockets extends TwoArgFunction implements LuaPlugin {
	public WebSockets() { }
	
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaValue library = tableOf();
		library.set("new", new newWs());
		return library;
	}
	
	static class newWs extends OneArgFunction {
		public LuaValue call(LuaValue uri) {
			return new WebSocket(uri.tojstring());
		}
	}

	@Override
	public String getLibraryName() {
		return "websockets";
	}
}

class WebSocket extends LuaTable {
	WebSocketClient client;
	
	public WebSocket(String uri) {
		set("uri", LuaValue.valueOf(uri));
		
		set("connect", new ZeroArgFunction() {
			public LuaValue call() {
				client.connect();
				return LuaValue.NONE;
			}
		});

		set("close", new ZeroArgFunction() {
			public LuaValue call() {
				client.close();
				return LuaValue.NONE;
			}
		});
		
		set("isOpen", new ZeroArgFunction() {
			public LuaValue call() {
				return LuaValue.valueOf(client.isOpen());
			}
		});
		
		set("reconnect", new ZeroArgFunction() {
			public LuaValue call() {
				client.reconnect();
				return LuaValue.NONE;
			}
		});
		
		set("send", new OneArgFunction() {
			public LuaValue call(LuaValue message) {
				client.send(message.optjstring(""));
				return LuaValue.NONE;
			}
		});
		
		try {
			client = new WebSocketClient(new URI(uri)) {

				@Override
				public void onOpen(ServerHandshake handshakedata) {
					LuaValue onOpenFunction = get("onOpen");
					if(onOpenFunction.isfunction()) {
						org.luaj.vm2_v3_0_1.LuaThread luaThread = new org.luaj.vm2_v3_0_1.LuaThread(AdvancedMacros.globals, onOpenFunction);
						AdvancedMacros.globals.setCurrentLuaThread(luaThread);
						onOpenFunction.invoke();
					}
				}

				@Override
				public void onMessage(String message) {
					LuaValue onMessageFunction = get("onMessage");
					if(onMessageFunction.isfunction()) {
						org.luaj.vm2_v3_0_1.LuaThread luaThread = new org.luaj.vm2_v3_0_1.LuaThread(AdvancedMacros.globals, onMessageFunction);
						AdvancedMacros.globals.setCurrentLuaThread(luaThread);
						onMessageFunction.invoke(LuaValue.valueOf(message));
					}
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					LuaValue onCloseFunction = get("onClose");
					if(onCloseFunction.isfunction()) {
						org.luaj.vm2_v3_0_1.LuaThread luaThread = new org.luaj.vm2_v3_0_1.LuaThread(AdvancedMacros.globals, onCloseFunction);
						AdvancedMacros.globals.setCurrentLuaThread(luaThread);
						onCloseFunction.invoke(LuaValue.varargsOf(LuaValue.valueOf(code), LuaValue.valueOf(reason), LuaValue.valueOf(remote)));
					}
				}

				@Override
				public void onError(Exception ex) {
					LuaValue onErrorFunction = get("onError");
					if(onErrorFunction.isfunction()) {
						org.luaj.vm2_v3_0_1.LuaThread luaThread = new org.luaj.vm2_v3_0_1.LuaThread(AdvancedMacros.globals, onErrorFunction);
						AdvancedMacros.globals.setCurrentLuaThread(luaThread);
						onErrorFunction.invoke(LuaValue.valueOf(ex.getMessage()));
					}
				}
				
			};
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}