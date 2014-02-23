package net.clareburt.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Justin Clareburt
 * @since 22/02/14
 */
public class Ingredient {

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private String item;
	private int amount;
	private Unit unit;
	private Date useBy;

	public Ingredient() {
	}

	public Ingredient(String item, String amount, String unit, String useBy) {
		setItem(item);
		setAmount(amount);
		setUnit(Unit.valueOf(unit));
		setUseBy(useBy);
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setAmount(String amount) {
		this.amount = Integer.parseInt(amount);
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Date getUseBy() {
		return useBy;
	}

	public void setUseBy(String useByStr) {
		if (useByStr == null) return;
		try {
			this.useBy = new SimpleDateFormat(DATE_FORMAT).parse(useByStr);
		} catch (ParseException e) {
			// TODO: Handle parse exception
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Ingredient that = (Ingredient) o;

		if (amount != that.amount) return false;
		if (item != null ? !item.equals(that.item) : that.item != null) return false;
		if (unit != that.unit) return false;
		if (useBy != null ? !useBy.equals(that.useBy) : that.useBy != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + amount;
		result = 31 * result + (unit != null ? unit.hashCode() : 0);
		result = 31 * result + (useBy != null ? useBy.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Ingredient{" +
				"item='" + item + '\'' +
				", amount=" + amount +
				", unit=" + unit +
				", useBy=" + formatDate() +
				'}';
	}

	private String formatDate() {
		return useBy == null ? null : new SimpleDateFormat(DATE_FORMAT).format(useBy);
	}
}
