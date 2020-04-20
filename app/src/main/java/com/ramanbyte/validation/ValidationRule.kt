package com.ramanbyte.validation

data class ValidationRule(
    val propertyId: Int,
    val rule: ValidationFlags,
    val property: String,
    var message: String,
    val otherProperty: String? = null,
    val limit: Int? = null,
    var spinnerSelection: Int? = 0,
    var isEnabled: Boolean = true
)