package net.inveed.gwt.editor.client.editor.fields;


import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupButton;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.github.nmorel.gwtjackson.client.utils.Base64Utils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.SecretFieldPropertyModel;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.client.utils.ByteArrayConvertor;
import net.inveed.gwt.editor.client.utils.CryptoHelper;

public class SecretKeyPropertyEditor extends AbstractFormPropertyEditor<SecretFieldPropertyModel, JSString> {
	
	private static final String FMT_HEX = "HEX";
	private static final String FMT_BASE64 = "Base64";
	private InputGroup widget;
	private int length = 16;
	private byte[] value;
	private boolean valid;

	private Button btnFormat;
	private TextBox textBox;
	private Button btnGenerate;
	
	private boolean isHex;

	public SecretKeyPropertyEditor(int length, boolean autoGen) {
		this.length = length;
		this.widget = new InputGroup();

		InputGroupButton igFormat = new InputGroupButton();
		this.btnFormat = new Button();
		this.btnFormat.setDataToggle(Toggle.DROPDOWN);
		this.btnFormat.setWidth("6em");
		DropDownMenu ddmFormat = new DropDownMenu();
		
		AnchorListItem aliBase64 = new AnchorListItem(FMT_BASE64);
		
		
		AnchorListItem aliHex = new AnchorListItem(FMT_HEX);
		
		ddmFormat.add(aliBase64);
		ddmFormat.add(aliHex);
		
		igFormat.add(this.btnFormat);
		igFormat.add(ddmFormat);
		
		this.btnFormat.setText(FMT_BASE64);
		this.isHex = false;
		
		this.textBox = new TextBox();
		this.textBox.addStyleName("monospace");
		
		this.widget.add(igFormat);
		this.widget.add(this.textBox);
		
		
		if (autoGen) {
			InputGroupButton gbtn = new InputGroupButton();
			this.btnGenerate = new Button();
			this.btnGenerate.setIcon(IconType.REFRESH);
			gbtn.add(this.btnGenerate);
			this.widget.add(gbtn);
			
			this.btnGenerate.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					generate();
					onValueChanged();
				}
			});
		}
		
		aliBase64.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				btnFormat.setText(FMT_BASE64);
				isHex = false;
				updateView();
			}
		});

		aliHex.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				btnFormat.setText(FMT_HEX);
				isHex = true;
				updateView();
			}
		});

		this.textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				validateValue();
				onValueChanged();
			}
		});
		
		this.textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				validateValue();
				onValueChanged();
			}
		});
		
		
		
		
		this.add(this.widget);
	}
	
	@Override
	public void setId(String uid) {
		this.widget.setId(uid);
	}
	
	public  void bind(JSEntity entity, SecretFieldPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		if (this.getOriginalValue() != null) {
			this.setValue(this.getOriginalValue());
		}
	}
	
	
	@Override
	protected Widget getChildWidget() {
		return this.widget;
	}

	@Override
	public void setValue(String v) {
		if (v == null) {
			this.textBox.setValue("");
			this.setValid(true);
			return;
		}
		v = v.trim();
		if (v.length() == 0) {
			this.textBox.setValue("");
			this.setValid(true);
			return;
		}
		try {
			byte[] bv = Base64Utils.fromBase64(v);
			if (bv != null) {
				this.value = bv;
				this.setValid(true);
				return;
			}
		} catch (Throwable e) {
		}
		
		try {
			byte[] bv = ByteArrayConvertor.hexStringToByteArray(v);
			if (bv != null) {
				this.value = bv;
				this.setValid(true);
				return;
			}
		} catch (Throwable e) {
		}
		
		this.textBox.setValue(v);
		this.setValid(false);
	}
	
	
	
	public byte[] getByteValue() {
		return this.value;
	}
	public String getBase64Value() {
		if (this.value == null)
			return null;
		return Base64Utils.toBase64(this.value);
	}
	
	public String getHexValue() {
		if (this.value == null)
			return null;
		return ByteArrayConvertor.toHexString(this.value);
	}
	
	private void setValid(boolean v){
		if (!v) {
			this.btnFormat.setEnabled(false);
		} else {
			this.btnFormat.setEnabled(this.textBox.isEnabled());
		}
		this.valid = v;
	}
	
	private void validateValue() {
		String tv = this.textBox.getValue();
		if (tv == null) {
			this.value = null;
			this.setValid(false);
			return;
		}
		tv = tv.trim();
		if (tv.length() == 0) {
			this.value = null;
			this.setValid(true);
			return;
		}
		if (!this.isHex) {
			try {
				byte[] bv = Base64Utils.fromBase64(tv);
				if (bv == null) {
					this.setValid(false);
					return;
				}
				if (bv.length != this.length) {
					this.setValid(false);
					return;
				}
				this.value = bv;
				this.setValid(true);
				return;
			} catch (Throwable e) {
				this.setValid(false);
				return;
			}
		} else {
			try {
				byte[] bv = ByteArrayConvertor.hexStringToByteArray(tv);
				if (bv == null) {
					this.setValid(false);
					return;
				}
				if (bv.length != this.length) {
					this.setValid(false);
					return;
				}
				this.value = bv;
				this.setValid(true);
				return;
			} catch (Throwable e) {
				this.setValid(false);
				return;
			}
		}
	}
	
	private void setValue(JSString originalValue) {
		if (originalValue != null) {
			this.textBox.setValue(originalValue.getValue());
		} else {
			this.textBox.setValue(null);
		}
	}
	
	private void updateView() {
		if (this.value == null) {
			this.textBox.setValue("");
			return;
		}
		
		if (!this.isHex) {
			this.textBox.setValue(Base64Utils.toBase64(this.value));
		} else {
			this.textBox.setValue(ByteArrayConvertor.toHexString(this.value));
		}
	}
	
	private void generate() {
		this.value = CryptoHelper.generateRandomSeed(this.length);
		updateView();
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && (this.textBox.getValue() == null || this.textBox.getValue().trim().length() == 0)) {
			return false;
		}
		validateValue();
		return this.valid;
	}

	@Override
	public JSString getValue() {
		String v = this.getBase64Value();
		if (v == null) {
			return null;
		}
		return new JSString(v);
	}

	@Override
	public void setEnabled(boolean value) {
		if (this.btnGenerate != null) {
			this.btnGenerate.setEnabled(value);
		}
		
		this.textBox.setEnabled(value);
		this.validateValue();
	}	
}
