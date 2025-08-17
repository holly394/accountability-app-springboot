

// TODO: convert all kotlin files to java file and code! (for uniformity)

package com.github.holly.accountability.validation

import org.springframework.validation.BindingResult

class BindingResultWrapper(bindingResult: BindingResult) {
    val violations: List<BindingResultError> = bindingResult.fieldErrors.map { BindingResultError(it.field, it.codes?.toList() ?: listOf()) }
}