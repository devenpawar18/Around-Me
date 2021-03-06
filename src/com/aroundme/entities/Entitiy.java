package com.aroundme.entities;

import org.json.JSONObject;

/**
 * Interface for all entities
 * 
 * @author DEVEN
 * 
 */
interface Entitiy {
	JSONObject serializeJSON() throws Exception;

	void deserializeJSON(JSONObject jsonObject) throws Exception;
}
