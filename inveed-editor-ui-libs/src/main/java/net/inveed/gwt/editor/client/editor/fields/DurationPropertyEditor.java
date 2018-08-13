package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.DoubleBox;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.base.ValueBoxBase;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.DurationPropertyModel;
import net.inveed.gwt.editor.client.types.JSTimeInterval;
import net.inveed.gwt.editor.client.types.JSTimeInterval.Format;

public class DurationPropertyEditor extends AbstractFormPropertyEditor<DurationPropertyModel, JSTimeInterval> {
	private InputGroup widget;
	
	private IntegerBox yearsTb;
	private IntegerBox monthsTb;
	private IntegerBox daysTb;
	private IntegerBox hoursTb;
	private IntegerBox minutesTb;
	private DoubleBox secondsTb;
	
	//private MaterialColumn titleCol;
	//private Label titleLabel;
	
	private InputGroupAddon createAddonBox(String text) {
		InputGroupAddon ret = new InputGroupAddon();
		ret.setText(text);
		return ret;
	}
	public DurationPropertyEditor() {
		this.widget = new InputGroup();
		this.add(this.widget);
	}
	public  void bind(JSEntity entity, DurationPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		boolean ro = this.isReadonly();
		
		if (this.getProperty().getFormat() == Format.ISO) {
			this.yearsTb = new IntegerBox();
			//this.yearsTb.setLabel("Years");
			this.yearsTb.setValue(0);
			//this.yearsTb.setReadOnly(ro);
			
			this.addListener(this.yearsTb);
			
			this.monthsTb = new IntegerBox();
			//this.monthsTb.setLabel("Months");
			//this.monthsTb.setMin("0");
			this.monthsTb.setReadOnly(ro);
			this.addListener(this.monthsTb);
			
			this.widget.add(yearsTb);
			this.widget.add(createAddonBox("Years"));
			this.widget.add(monthsTb);
			this.widget.add(createAddonBox("Months"));
		}
		
		this.daysTb = new IntegerBox();
		//this.daysTb.setLabel("Days");
		//this.daysTb.setMin("0");
		//this.daysTb.setReadOnly(ro);
		this.addListener(this.daysTb);
		
		this.hoursTb = new IntegerBox();
		//this.hoursTb.setLabel("Hours");
		//this.hoursTb.setMin("0");
		//this.hoursTb.setReadOnly(ro);
		this.addListener(this.hoursTb);
		
		this.minutesTb = new IntegerBox();
		//this.minutesTb.setLabel("Minutes");
		//this.minutesTb.setMin("0");
		//this.minutesTb.setReadOnly(ro);
		this.addListener(this.minutesTb);
		

		this.widget.add(daysTb);
		this.widget.add(createAddonBox("Days"));
		this.widget.add(hoursTb);
		this.widget.add(createAddonBox("Hrs"));
		this.widget.add(minutesTb);
		this.widget.add(createAddonBox("Min"));
		
		if (this.getProperty().getFormat() != Format.MINUTES) {
			this.secondsTb = new DoubleBox();
			//this.secondsTb.setLabel("Seconds");
			//this.secondsTb.setMin("0");
			//this.secondsTb.setReadOnly(ro);
			this.addListener(this.secondsTb);
			
			
			this.widget.add(secondsTb);
			this.widget.add(createAddonBox("Sec"));
		}
		
		
		
		this.setValue(this.getOriginalValue());
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.widget;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addListener(ValueBoxBase<?> b) {
		b.addValueChangeHandler(new ValueChangeHandler() {

			@Override
			public void onValueChange(ValueChangeEvent event) {
				onValueChanged();
			}
		});
		b.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});
	}
	
	@Override
	public void setValue(String v) {
		if (v == null) {
			return;
		}
		v = v.trim();
		if (v.length() == 0) {
			return;
		}
		try {
			Double d = Double.parseDouble(v);
			JSONNumber num = new JSONNumber(d);
			JSTimeInterval ival = JSTimeInterval.parse(num, this.getProperty().getFormat());
			if (ival != null) {
				this.setValue(ival);
				return;
			}
		} catch (Exception e) {
		}
		
		JSONString jstring = new JSONString(v);
		JSTimeInterval ival = JSTimeInterval.parse(jstring, this.getProperty().getFormat());
		if (ival != null) {
			this.setValue(ival);
		}
	}
	
	public void setValue(JSTimeInterval interval) {
		if (interval == null) {
			return;
		}
		if (this.getProperty().getFormat() == Format.ISO) {
			this.yearsTb.setValue((int)interval.getHYears());
			this.monthsTb.setValue((int) interval.getHMonths());
		}

		this.daysTb.setValue((int)interval.getHDays());
		this.hoursTb.setValue((int)interval.getHHours());
		this.minutesTb.setValue((int)interval.getHMinutes());
		if (this.getProperty().getFormat() != Format.MINUTES) {
			this.secondsTb.setValue(interval.getHSeconds());
		}
	}
	
	@Override
	public boolean validate() {
		return true; //TODO: реализовать валидацию!
	}

	@Override
	public JSTimeInterval getValue() {
		Double seconds = null;
		if (this.secondsTb != null) {
			seconds = this.secondsTb.getValue();
		}
		if (this.getProperty().getFormat() == Format.ISO) {
			Integer years = null;
			Integer months = null;
			if (this.yearsTb.getValue() != null) {
				years = this.yearsTb.getValue().intValue();
			}
			
			if (this.monthsTb.getValue() != null) {
				months = this.monthsTb.getValue().intValue();
			}
			
			return new JSTimeInterval(years, 
					months,
					this.daysTb.getValue(), 
					this.hoursTb.getValue(), 
					this.minutesTb.getValue(),
					seconds, 
					this.getProperty().getFormat());
		} else {
			return new JSTimeInterval(0, 0,
					this.daysTb.getValue(), 
					this.hoursTb.getValue(), 
					this.minutesTb.getValue(),
					seconds, 
					this.getProperty().getFormat());
		}
	}

	@Override
	public void setId(String uid) {
		this.widget.setId(uid);
	}
	@Override
	public void setEnabled(boolean value) {
		if (this.daysTb != null) this.daysTb.setEnabled(value);
		if (this.hoursTb != null) this.hoursTb.setEnabled(value);
		if (this.minutesTb != null) this.minutesTb.setEnabled(value);
		if (this.monthsTb != null) this.monthsTb.setEnabled(value);
		if (this.secondsTb != null) this.secondsTb.setEnabled(value);
		if (this.yearsTb != null) this.yearsTb.setEnabled(value);
	}
}
