package net.inveed.gwt.editor.client.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;

public class JSEntityList implements IJSObject, INativeObject<List<JSEntity>> {
	private static final Logger LOG = Logger.getLogger(JSEntityList.class.getName());
	
	public static final String TYPE = "ENTITY_LIST";
	private List<JSEntity> list;
	
	public static final JSEntityList parse(JSONValue v, EntityModel basicType, EntityManager em) {
		if (v.isNull() != null) {
			LOG.info("Got null JSON value");
			return null;
		}
		if (v.isArray() == null) {
			LOG.info("Got non-array JSON value: " + v.toString());
			return null;
		}
		List<JSEntity> list = new ArrayList<>();

		for (int i = 0; i < v.isArray().size(); i++) {
			JSONValue av = v.isArray().get(i);
			
			if (av.isObject() != null) {
				list.add(em.get(basicType, av.isObject()));
			} else if (av.isString() != null ) {
				JSString id = new JSString(av.isString().stringValue());
				list.add(em.get(basicType, id));
			} else if (av.isNumber() != null) {
				JSLong id = new JSLong((long)av.isNumber().doubleValue());
				list.add(em.get(basicType, id));
			} else if (av.isNull() != null) {
				//TODO: INFO: null
			} else {
				//TODO: WARN: boolean
			}
		}
		return new JSEntityList(list);

	}
	public JSEntityList(List<JSEntity> list) {
		this.list = list;
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<JSEntity> getValue() {
		return Collections.unmodifiableList(this.list);
	}
	
	public void add(JSEntity e) {
		this.list.add(e);
	}
	
	public boolean remove(JSEntity e) {
		return this.list.remove(e);
	}
	@Override
	public int compareTo(IJSObject o) {
		return 0;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public JSONValue getJSONValue() {
		JSONArray ret = new JSONArray();
		for (int i = 0; i < this.list.size(); i++) {
			JSEntity e = this.list.get(i);
			ret.set(i, e.getJSONValue());
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return "...";
	}

}
