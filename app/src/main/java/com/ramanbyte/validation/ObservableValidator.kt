package com.ramanbyte.validation

import android.util.Patterns
import androidx.collection.ArrayMap
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_NAME_PATTERN
import com.ramanbyte.utilities.KEY_PASSWORD_PATTERN
import java.lang.reflect.Field
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class ObservableValidator<T : Observable>(
    private var bindingResource: Class<*>
) {

    val TAG: String = "ObservableValidator"

    //Store the validation rules at a ValidationRule List, by fieldId
    private val rules = ArrayMap<Int, ArrayList<ValidationRule>>()

    //Store the field error string, or null if validation pass
    private val validations = ArrayMap<Int, MutableLiveData<String>>()

    private var spinnerTemp = 0

    //Get the LiveData<String> for the property
    fun getValidation(property: String): MutableLiveData<String>? {
        val field: Field = bindingResource.getDeclaredField(property)
        field.isAccessible = true
        val genBr: Int = field.getInt(null)

        /*return ObservableField<String>().apply {
            set(validations[genBr]?.value)
        }*/
        return validations[genBr]
    }

    private lateinit var observedObject: T

    //Add the callback to the observedObject
/* init {
     oervedObject.addOnPropertyChangedCallback(object :
         Observable.OnPropertyChangedCallback() {
         override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
             logRules(propertyId)
             validateField(propertyId)
         }
     })
 }*/

    constructor(observedObject: T, bindingResource: Class<*>) : this(bindingResource) {
        this.bindingResource = bindingResource
        this.observedObject = observedObject

        this.observedObject.addOnPropertyChangedCallback(object :
            OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                logRules(propertyId)
                validateField(propertyId)
            }
        })
    }

    fun observeDataModel(observedObject: T) {

        this.observedObject = observedObject

        /*this.observedObject.addOnPropertyChangedCallback(object :
            OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                logRules(propertyId)
                validateField(propertyId)
            }
        })*/

        this.observedObject.addOnPropertyChangedCallback(object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                logRules(propertyId)
                validateField(propertyId)
            }
        })
    }

    //Validate all fields
    fun validateAll(): Boolean {

        //Run validation for all fields
        rules.entries.forEach {
            it.value.forEach {
                if (it.spinnerSelection != -1)
                    validateField(it.propertyId)
                else {
                    AppLog.infoLog("validateAll ${it.propertyId} before${it.spinnerSelection}")
                    it.spinnerSelection = it.spinnerSelection!! + 1
                    validateField(it.propertyId)
                }
            }
        }
        //Default response is valid=true
        var response = true

        //If there is any string message set, some validation must have failed, so response is invalid=false
        validations.entries.forEach {
            if (!it.value?.value.isNullOrEmpty()) response = false
            AppLog.infoLog("validateAll entries ${it.value.value}")
        }

        //the response
        return response
    }

    fun validateAll(vararg exception: ValidationFlags): Boolean {
        //FIELD_MATCH
        //Run validation for all fields
        rules.entries.forEach {
            it.value.forEach {

                validateField(it.propertyId, *exception)
            }
        }

        //Default response is valid=true
        var response = true

        //If there is any string message set, some validation must have failed, so response is invalid=false
        validations.entries.forEach {
            if (!it.value?.value.isNullOrEmpty()) response = false
        }

        //the response
        return response
    }

    fun clearAllErrors() {
        validations.entries.forEach {
            it.value?.value = null
        }
    }

    //Run all validations for this field, stop if we fail some rule
    fun validateField(propertyId: Int) {
        rules[propertyId]?.forEach {
            logProperty(it.property)
            if (!validateProperty(it)) return //return if validation hit a invalid state
        }
    }

    fun validateField(propertyId: Int, vararg exception: ValidationFlags) {
        rules[propertyId]?.forEach {
            logProperty(it.property)

            if (!exception.contains(it.rule)) {
                if (!validateProperty(it)) return //return if validation hit a invalid state
            }
        }
    }

    //Run the proper validation function for the rule
    private fun validateProperty(validationRule: ValidationRule): Boolean {

        if (!validationRule.isEnabled) {
            validations[validationRule.propertyId].also {
                it?.value = null
            }

            return true
        }

        when (validationRule.rule) {
            ValidationFlags.FIELD_REQUIRED -> {
                return validateRequired(validationRule)
            }
            ValidationFlags.FIELD_EMAIL -> {
                return validateEmail(validationRule)
            }
            ValidationFlags.FIELD_MATCH -> {
                return validateMatch(validationRule)
            }
            ValidationFlags.FIELD_NOT_MATCH -> {
                return validateNotMatch(validationRule)
            }
            ValidationFlags.FIELD_MAX -> {
                return validateMax(validationRule)
            }
            ValidationFlags.FIELD_MIN -> {
                return validateMin(validationRule)
            }
            ValidationFlags.FIELD_PASSWORD -> {
                return validatePassword(validationRule)
            }
            ValidationFlags.FIELD_CONTAINS_SPACE -> {
                return validateSpace(validationRule)
            }
            ValidationFlags.FIELD_MATCH_IGNORE_CASE -> {
                return validateMatchIgnoreCase(validationRule)
            }
            ValidationFlags.FIELD_SPINNER_SELECTION -> {
                return validateSpinnerSelection(validationRule)
            }
            ValidationFlags.FIELD_DECIMAL -> {
                return validateDecimal(validationRule)
            }
            ValidationFlags.FIELD_URL_LINK -> {
                return validateUrlLink(validationRule)
            }
        }
    }

    //Add a validation rule
    fun addRule(
        field: String,
        rule: ValidationFlags,
        message: String,
        otherField: String? = null,
        limit: Int? = null,
        spinnerSelectPosition: Int? = 0
    ) {

        this.spinnerTemp = spinnerSelectPosition!!

        //Get the field BR Int
        val bindingField: Field = bindingResource.getDeclaredField(field)
        bindingField.isAccessible = true
        val propertyId: Int = bindingField.getInt(null)

        /*Log.d(TAG, "Generic bindingResource: $propertyId")
        Log.d(
            TAG,
            "addRule(field: $field, rule:$rule, message: $message), keyOther field: $otherField, limit: $limit"
        )*/

        //Initialize the LiveData<String> for this field
        validations[propertyId] = MutableLiveData()

        //If there's no rule yet, initialize the rule array
        if (rules[propertyId] == null) rules[propertyId] = ArrayList()

        rules[propertyId]?.add(
            ValidationRule(
                propertyId,
                rule,
                field,
                message,
                otherField,
                limit,
                spinnerSelectPosition!!
            )
        )

        //Add this rule to the array
        /*val validationRuleIndex = rules[propertyId]?.indexOfFirst { it.rule == rule }

        if (validationRuleIndex != -1) {

            val validationRule = rules[propertyId]?.get(validationRuleIndex!!)

            validationRule?.spinnerSelection = spinnerSelectPosition
            rules[propertyId]?.set(validationRuleIndex!!, validationRule!!)
        } else {
            rules[propertyId]?.add(
                ValidationRule(
                    propertyId,
                    rule,
                    field,
                    message,
                    otherField,
                    limit,
                    spinnerSelectPosition!!
                )
            )
        }*/

        //Order the rules so we run FIELD_REQUIRED first, and others after
        Collections.sort(rules[propertyId], ValidationRulesComparator())
    }

    fun removeRule(isEnable: Boolean, vararg fields: String) {

        fields.forEach { field ->
            updateValidation(isEnable, ValidationFlags.FIELD_REQUIRED, field)
        }
    }

    fun updateValidation(isEnable: Boolean, validationFlag: ValidationFlags, field: String) {

        val bindingField: Field = bindingResource.getDeclaredField(field)
        bindingField.isAccessible = true
        val propertyId: Int = bindingField.getInt(null)

        val index = rules[propertyId]?.indexOfFirst { it.rule == validationFlag }

        if (index != null) {
            rules[propertyId]?.get(index)?.apply {
                isEnabled = isEnable
            }
        }
    }

    //Yep logszz
    fun logRules(propertyId: Int) {
        rules[propertyId]?.forEach {
            logProperty(it.property)
        }
    }

    //Yep... logggin
    private fun logProperty(property: String) {
        try {
            var value: String = observedObject::class.java.getMethod("get" + property.capitalize())
                .invoke(observedObject)?.toString() ?: ""
            //Log.d(TAG, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Get the actual value of the property using the field getter: get+FieldName
    private fun getProperty(property: String): Any? {

        return observedObject::class.java.getMethod("get" + property.capitalize())
            .invoke(observedObject)
    }

    //Validate rule FIELD_MAX
    private fun validateMax(validationRule: ValidationRule): Boolean {
        var value = getProperty(validationRule.property)

        if (validationRule.limit != null) {
            if (value is Int) {
                if (value > validationRule.limit) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            } else if (value is String) {
                if (value.length > validationRule.limit) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }

        return true

    }

    //Validate rule FIELD_MIN
    private fun validateMin(validationRule: ValidationRule): Boolean {
        var value = getProperty(validationRule.property)

        if (validationRule.limit != null) {
            if (value is Int) {
                if (value < validationRule.limit) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            } else if (value is String) {
                if (value.length > 0 && value.length < validationRule.limit) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }

        return true

    }

    //Validate rule FIELD_REQUIRED
    private fun validateRequired(validationRule: ValidationRule): Boolean {
        var value = getProperty(validationRule.property)

        if (value == null) {
            validations[validationRule.propertyId].also {
                it?.value = validationRule.message
            }
            return false
        }
        if (value is Boolean) {
            if (!value) {
                validations[validationRule.propertyId].also {
                    it?.value = validationRule.message
                }
                return false
            }
        } else if (value is String) {
            if (value.isNullOrEmpty()) {
                validations[validationRule.propertyId].also {
                    it?.value = validationRule.message
                }
                return false
            }
        } else if (value is Int) {
            if (value.toString().isEmpty()) {
                validations[validationRule.propertyId].also {
                    it?.value = validationRule.message
                }
                return false
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }

        return true

    }

    //Validate rule FIELD_MATCH
    private fun validateMatch(validationRule: ValidationRule): Boolean {
        var value1 = getProperty(validationRule.property)
        if (validationRule.otherProperty != null) {
            var value2 = getProperty(validationRule.otherProperty)

            if (value1 is Int && value2 is Int) {
                if (value1 != value2) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            } else if (value1 is String && value2 is String) {
                if (value1 != value2) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }

        } else {
            validations[validationRule.propertyId].also {
                it?.value = validationRule.message
            }
            return false
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true

    }


    //Validate rule FIELD_MATCH_IGNORE_CASE
    private fun validateMatchIgnoreCase(validationRule: ValidationRule): Boolean {
        var value1 = getProperty(validationRule.property)
        if (validationRule.otherProperty != null) {
            var value2 = getProperty(validationRule.otherProperty)

            if (value1 is Int && value2 is Int) {
                if (value1 != value2) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            } else if (value1 is String && value2 is String) {
                if (!value1.equals(value2, true)) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }

        } else {
            validations[validationRule.propertyId].also {
                it?.value = validationRule.message
            }
            return false
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true

    }


    //Validate rule FIELD_NOT_MATCH
    private fun validateNotMatch(validationRule: ValidationRule): Boolean {
        var value1 = getProperty(validationRule.property)
        if (validationRule.otherProperty != null) {
            var value2 = getProperty(validationRule.otherProperty)

            if (value1 is Int && value2 is Int) {
                if (value1 == value2) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            } else if (value1 is String && value2 is String) {
                if (value1 == value2) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }

        } else {
            validations[validationRule.propertyId].also {
                it?.value = validationRule.message
            }
            return false
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true

    }

    //Validate rule FIELD_EMAIL
    private fun validateEmail(validationRule: ValidationRule): Boolean {
        var value = getProperty(validationRule.property)

        if (value is String && value.toString().isNotEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                validations[validationRule.propertyId].also {
                    it?.value = validationRule.message
                }
                return false
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true

    }

    //Validate rule FIELD_PASSWORD
    private fun validatePassword(validationRule: ValidationRule): Boolean {

        val value = getProperty(validationRule.property)

        if (value is String) {

            if (value.isNotEmpty()) {
                if (!validatePasswordPattern(value)) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true
    }

    //Validate rule FIELD_CONTAINS_SPACE
    private fun validateSpace(validationRule: ValidationRule): Boolean {

        val value = getProperty(validationRule.property)

        if (value is String) {
            if (value.isNotEmpty() && value.contains(" ")) {
                validations[validationRule.propertyId].also {
                    it?.value = validationRule.message
                }
                return false
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true
    }

    private fun validateSpinnerSelection(validationRule: ValidationRule): Boolean {

        AppLog.warningLog("Spinner Property ------ ${validationRule.property}")
        AppLog.warningLog("Spinner spinnerTemp ------ ${spinnerTemp}")

        val value = getProperty(validationRule.property)

        if (value is Int) {
            if (value == 0) {
                if (validationRule.spinnerSelection!! != -1) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                } else {
                    //validationRule.spinnerSelection = 1
                }
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true
    }

    private fun validateDecimal(validationRule: ValidationRule): Boolean {

        val value = getProperty(validationRule.property)

        if (value is String) {
            if (value.isNotEmpty()) {

                if (value.startsWith(".") || value.endsWith(".")) {
                    validations[validationRule.propertyId].also {
                        it?.value = validationRule.message
                    }
                    return false
                }
            }
        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true
    }

    private fun validateUrlLink(validationRule: ValidationRule): Boolean {

        val value = getProperty(validationRule.property)

        if (value is String) {

            val p: Pattern = Patterns.WEB_URL
            val m: Matcher = p.matcher(value.toString().toLowerCase())

            if (!m.matches()) {
                validations[validationRule.propertyId].also {
                    it?.value = validationRule.message
                }
                return false
            }

        }

        validations[validationRule.propertyId].also {
            it?.value = null
        }
        return true
    }

    private fun validatePasswordPattern(newPassword: String): Boolean {
        val pattern = Pattern.compile(KEY_PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(newPassword)
        return matcher.matches()
    }

    //----------------------- for profile details person name validataion ----------
    private fun validationNamePattern(name: String): Boolean {
        val pattern = Pattern.compile(KEY_NAME_PATTERN)
        val matcher: Matcher = pattern.matcher(name)
        return matcher.matches()
    }


    internal inner class ValidationRulesComparator : Comparator<ValidationRule> {
        override fun compare(left: ValidationRule, right: ValidationRule): Int {
            return left.rule.compareTo(right.rule)
        }
    }
}