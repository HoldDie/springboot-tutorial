package com.holddie.springboot.elasticsearch.tutorial.search.services.field;

import java.util.Date;

public class RZDateField extends RZField {
	
	public Date getValue() {
		return (Date)super.getValue();
	}

}
