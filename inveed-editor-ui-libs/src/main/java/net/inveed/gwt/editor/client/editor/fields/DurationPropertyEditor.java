package net.inveed.gwt.editor.client.editor.fields;


import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.controls.InputGroup;
import net.inveed.gwt.editor.client.controls.InputGroupText;
import net.inveed.gwt.editor.client.controls.SimpleDoubleBox;
import net.inveed.gwt.editor.client.controls.SimpleIntegerBox;
import net.inveed.gwt.editor.client.controls.SimpleNumberBox;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.DurationPropertyModel;
import net.inveed.gwt.editor.client.types.JSTimeInterval;
import net.inveed.gwt.editor.commons.DurationPrecision;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class DurationPropertyEditor extends AbstractFormPropertyEditor<DurationPropertyModel, JSTimeInterval> {
	public static final IPropertyEditorFactory<DurationPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<DurationPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<DurationPropertyModel, ?> createEditor(DurationPropertyModel property, EditorFieldDTO dto) {
				return new DurationPropertyEditor();
			}};
	}

	private InputGroup group;
	
	private SimpleIntegerBox yearsTb;
	private SimpleIntegerBox monthsTb;
	private SimpleIntegerBox daysTb;
	private SimpleIntegerBox hoursTb;
	private SimpleIntegerBox minutesTb;
	private SimpleDoubleBox  secondsTb;
	
	public DurationPropertyEditor() {
		this.group = new InputGroup();
		this.initWidget(this.group);
		
	}
	
	private void addText(String txt) {
		this.group.add(new InputGroupText(txt));
		
	}
	private void buildRow() {
		if (this.getProperty().getMaxItem() == DurationPrecision.YEAR) {
			this.yearsTb = new SimpleIntegerBox();
			this.yearsTb.setValue(0);
			this.yearsTb.setMin("0");
			
			this.addListener(this.yearsTb);
			this.group.add(this.yearsTb);
			
			this.addText("Years");
		}
		if (getProperty().getMaxItem().getLevel() >= DurationPrecision.MONTH.getLevel() 
				&& getProperty().getPrecision().getLevel() <= DurationPrecision.MONTH.getLevel()) {
			this.monthsTb = new SimpleIntegerBox();
			this.monthsTb.setValue(0);
			this.monthsTb.setMin("0");
			
			this.addListener(this.monthsTb);
			this.group.add(this.monthsTb);
			
			this.addText("Months");
		}
		
		if (getProperty().getMaxItem().getLevel() >= DurationPrecision.DAY.getLevel() 
				&& getProperty().getPrecision().getLevel() <= DurationPrecision.DAY.getLevel()) {
			this.daysTb = new SimpleIntegerBox();
			this.daysTb.setValue(0);
			this.daysTb.setMin("0");
			
			this.addListener(this.daysTb);
			this.group.add(this.daysTb);
			
			this.addText("Days");
		}
		
		if (getProperty().getMaxItem().getLevel() >= DurationPrecision.HOUR.getLevel() 
				&& getProperty().getPrecision().getLevel() <= DurationPrecision.HOUR.getLevel()) {
			this.hoursTb = new SimpleIntegerBox();
			this.hoursTb.setValue(0);
			this.hoursTb.setMin("0");
			
			this.addListener(this.hoursTb);
			this.group.add(this.hoursTb);
			
			this.addText("Hours");
		}
		if (getProperty().getMaxItem().getLevel() >= DurationPrecision.MINUTE.getLevel() 
				&& getProperty().getPrecision().getLevel() <= DurationPrecision.MINUTE.getLevel()) {
		
			this.minutesTb = new SimpleIntegerBox();
			this.minutesTb.setValue(0);
			this.minutesTb.setMin("0");
			
			this.addListener(this.minutesTb);
			this.group.add(this.minutesTb);
			
			this.addText("Min");
		}
		if (getProperty().getMaxItem().getLevel() >= DurationPrecision.SECOND.getLevel() 
				&& getProperty().getPrecision().getLevel() <= DurationPrecision.SECOND.getLevel()) {
			this.secondsTb = new SimpleDoubleBox();
			this.secondsTb.setValue(0D);
			this.secondsTb.setMin("0");
			
			this.addListener(this.secondsTb);
			this.group.add(this.secondsTb);
			
			this.addText("Sec");
		}		
	}

	public  void bind(JSEntity entity, DurationPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		this.buildRow();
		this.group.setLabel(field.getDisplayName(viewName));
		
		this.setInitialValue();
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addListener(SimpleNumberBox<?> yearsTb2) {
		yearsTb2.addValueChangeHandler(new ValueChangeHandler() {

			@Override
			public void onValueChange(ValueChangeEvent event) {
				onValueChanged();
			}
		});
		yearsTb2.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});
	}
	

	public int getYears() {
		return this.yearsTb == null ? 0 : (this.yearsTb.getValue() == null ? 0 : this.yearsTb.getValue());
	}
	
	public int getMonths() {
		return this.monthsTb == null ? 0 : (this.monthsTb.getValue() == null ? 0 : this.monthsTb.getValue());
	}
	
	public int getDays() {
		return this.daysTb == null ? 0 : (this.daysTb.getValue() == null ? 0 : this.daysTb.getValue());
	}
	
	public int getHours() {
		return this.hoursTb == null ? 0 : (this.hoursTb.getValue() == null ? 0 : this.hoursTb.getValue());
	}
	
	public int getMinutes() {
		return this.minutesTb == null ? 0 : (this.minutesTb.getValue() == null ? 0 : this.minutesTb.getValue());
	}
	
	public double getSeconds() {
		return this.secondsTb == null ? 0 : (this.secondsTb.getValue() == null ? 0 : this.secondsTb.getValue());
	}
	
	@Override
	public void setValue(JSTimeInterval interval) {
		if (interval == null) {
			return;
		}
		if (this.yearsTb != null && this.monthsTb != null) {
			this.yearsTb.setValue((int)interval.getHYears());
			this.monthsTb.setValue((int) interval.getHMonths());
		} else if (this.yearsTb != null) {
			this.yearsTb.setValue((int)interval.getHYears());
		} else if (this.monthsTb != null) {
			this.monthsTb.setValue((int) interval.getMonths());
		}
		
		double seconds = interval.getSeconds();
		if (this.daysTb != null) {
			this.daysTb.setValue((int) interval.getHDays());
			seconds -= (double) (interval.getHDays() * 24L * 3600L);
		}
		if (this.hoursTb != null) {
			long hours = (long) (seconds / 3600D);
			seconds -= (double) (hours * 3600L);
			this.hoursTb.setValue((int)hours);
		}
		if (minutesTb != null) {
			long minutes = (long) (seconds / 60D);
			seconds -= (double) (minutes * 60L);
			this.minutesTb.setValue((int)minutes);
		}
		if (this.secondsTb != null) {
			this.secondsTb.setValue(seconds);
		}
	}
	
	@Override
	public boolean validate() {
		return true; //TODO: реализовать валидацию!
	}

	@Override
	public JSTimeInterval getValue() {
		return new JSTimeInterval(this.getYears(), 
					this.getMonths(),
					this.getDays(),
					this.getHours(),
					this.getMinutes(),
					this.getSeconds(),
					this.getProperty().getFormat());
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
	@Override
	public void setGrid(String grid) {
		this.group.setGrid(grid);
	}
}
