package net.inveed.gwt.editor.client.editor.fields;

import com.github.nmorel.gwtjackson.client.utils.Base64Utils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.controls.GroupedButton;
import net.inveed.gwt.editor.client.controls.GroupedTextBox;
import net.inveed.gwt.editor.client.controls.InputGroup;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.BinaryPropertyModel;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.client.utils.ByteArrayConvertor;
import net.inveed.gwt.editor.client.utils.CryptoHelper;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class BinaryPropertyEditor extends AbstractFormPropertyEditor<BinaryPropertyModel, JSString> {
	private static final String FMT_HEX = "HEX";
	private static final String FMT_BASE64 = "B64";
	
	public static final IPropertyEditorFactory<BinaryPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<BinaryPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<BinaryPropertyModel, ?> createEditor(BinaryPropertyModel property, EditorFieldDTO dto) {
				BinaryPropertyEditor ret = new BinaryPropertyEditor();
				ret.setAllowGenerate(property.isAllowGenerate());
				return ret;
			}};
	}
	
	private InputGroup widget;
	private int length = 16;
	private byte[] value;
	private boolean valid;
	private boolean allowGenerate;

	private GroupedTextBox textBox;
	private GroupedButton btnFormat;
	private GroupedButton btnGenerate;
	
	private boolean isHex;

	public BinaryPropertyEditor() {
		this.widget = new InputGroup();
		this.initWidget(this.widget);
	}
	
	private void buildControls() {
		this.textBox = new GroupedTextBox();
		this.widget.add(this.textBox);

		this.btnFormat = new GroupedButton();
		this.btnFormat.setWidth("35px");
		this.btnFormat.setTextColor(Color.GREY_DARKEN_2);
		this.btnFormat.setShadow(0);
		this.btnFormat.setText(FMT_HEX);
		this.isHex = true;
		
		this.widget.add(this.btnFormat);
		
		if (isAllowGenerate()) {
			this.btnGenerate = new GroupedButton();
			this.btnGenerate.setIconType(IconType.REFRESH);
			this.btnGenerate.setTextColor(Color.GREY_DARKEN_1);
			this.btnGenerate.setWidth("30px");
			this.btnGenerate.setShadow(0);
			this.widget.add(this.btnGenerate);
			
			this.btnGenerate.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					generate();
					onValueChanged();
				}
			});
		}
		
		this.btnFormat.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				toggleFormatBtn();
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
	}
	
	private void toggleFormatBtn() {
		this.isHex = !this.isHex;
		this.btnFormat.setText(this.isHex ? FMT_HEX : FMT_BASE64);
	}
	public  void bind(JSEntity entity, BinaryPropertyModel field, String viewName) {
		this.buildControls();
		
		super.bind(entity, field, viewName);

		this.widget.setLabel(field.getDisplayName(viewName));
		if (this.getProperty().getGenLength() != null) {
			this.length = this.getProperty().getGenLength();
		}
		
		this.setInitialValue();
	}
	
	@Override
	public void setValue(JSString str) {
		if (str == null) {
			this.textBox.setValue("");
			this.setValid(true);
			return;
		}
		String v = str.getValue();
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
	
	/*
	private void setValue(JSString originalValue) {
		if (originalValue != null) {
			this.textBox.setValue(originalValue.getValue());
		} else {
			this.textBox.setValue(null);
		}
	}*/
	
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
		if (value) {
			this.validateValue();
		}
		
	}


	@Override
	public void setGrid(String grid) {
		this.widget.setGrid(grid);
	}

	public boolean isAllowGenerate() {
		return allowGenerate;
	}

	public void setAllowGenerate(boolean v) {
		this.allowGenerate = v;
	}
}
