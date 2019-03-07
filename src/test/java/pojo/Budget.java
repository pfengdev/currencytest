package test.java.pojo;

import java.math.BigDecimal;

public class Budget {
	private String totalCost;
	
	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getTotalCostValue() {
		return new BigDecimal(totalCost.replace("$", ""));
	}
}